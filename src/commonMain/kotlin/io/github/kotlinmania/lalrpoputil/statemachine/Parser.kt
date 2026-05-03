// SPDX-License-Identifier: Apache-2.0 OR MIT
// port-lint: source src/state_machine.rs
package io.github.kotlinmania.lalrpoputil.statemachine

import io.github.kotlinmania.lalrpoputil.ErrorRecovery
import io.github.kotlinmania.lalrpoputil.ParseError

private const val DEBUG_ENABLED: Boolean = false

private inline fun debug(msg: () -> String) {
    // Mirrors the upstream `debug!` macro gated on `DEBUG_ENABLED`.
    if (DEBUG_ENABLED) {
        println(msg())
    }
}

/**
 * The LR(1) driver loop used by generated parsers.
 *
 * The upstream `Parser<D, I>` carries the iterator as a second type
 * parameter; here the driver accepts any [Iterator] over [TokResult] values,
 * which mirrors the upstream
 * `Iterator<Item = Result<TokenTriple<D>, ParseError<D>>>`.
 */
class Parser<
    Location,
    Error,
    Token,
    TokenIndex,
    Symbol,
    Success,
    StateIndex,
    Action : ParserAction<StateIndex, ReduceIndex>,
    ReduceIndex,
    NonterminalIndex,
> private constructor(
    private val definition: ParserDefinition<
        Location, Error, Token, TokenIndex, Symbol, Success, StateIndex,
        Action, ReduceIndex, NonterminalIndex,
    >,
    private val tokens: Iterator<TokResult<Location, Token, Error>>,
) {
    private val states: MutableList<StateIndex> = mutableListOf(definition.startState())
    private val symbols: MutableList<Triple<Location, Symbol, Location>> = mutableListOf()
    private var lastLocation: Location = definition.startLocation()

    companion object {
        /**
         * Run a generated [definition] against [tokens] and return the
         * resulting [ParseResult]. The static entry point most callers use.
         */
        fun <
            Location, Error, Token, TokenIndex, Symbol, Success, StateIndex,
            Action : ParserAction<StateIndex, ReduceIndex>, ReduceIndex, NonterminalIndex,
        > drive(
            definition: ParserDefinition<
                Location, Error, Token, TokenIndex, Symbol, Success, StateIndex,
                Action, ReduceIndex, NonterminalIndex,
            >,
            tokens: Iterator<TokResult<Location, Token, Error>>,
        ): ParseResult<Success, Location, Token, Error> {
            return Parser(definition, tokens).parse()
        }
    }

    private fun topState(): StateIndex = states.last()

    private fun parse(): ParseResult<Success, Location, Token, Error> {
        // Outer loop: each time we continue around this loop, we shift a new
        // token from the input. We break from the loop when the end of the
        // input is reached (we return early if an error occurs).
        shift@ while (true) {
            var pair = when (val nt0 = nextToken()) {
                is NextToken.FoundToken -> nt0.lookahead to nt0.tokenIndex
                is NextToken.Eof -> return parseEof()
                is NextToken.Done -> return nt0.result
            }

            debug { "+ SHIFT: ${pair.first}" }
            debug { "\\ token_index: ${pair.second}" }

            inner@ while (true) {
                val lookahead = pair.first
                val tokenIndex = pair.second
                val topState = topState()
                val action = definition.action(topState, tokenIndex)
                debug { "\\ action: $action" }

                val targetState = action.asShift()
                if (targetState != null) {
                    debug { "\\ shift to: $targetState" }

                    // Shift and transition to state `action - 1`.
                    val symbol = definition.tokenToSymbol(tokenIndex, lookahead.second)
                    states.add(targetState)
                    symbols.add(Triple(lookahead.first, symbol, lookahead.third))
                    continue@shift
                }

                val reduceIndex = action.asReduce()
                if (reduceIndex != null) {
                    debug { "\\ reduce to: $reduceIndex" }

                    val r = reduce(reduceIndex, lookahead.first)
                    if (r != null) {
                        return when (r) {
                            // We reached eof, but still have lookahead.
                            is ParseResult.Success -> ParseResult.Failure(ParseError.ExtraToken(lookahead))
                            is ParseResult.Failure -> r
                        }
                    }
                } else {
                    debug { "\\ error -- initiating error recovery!" }

                    when (val nt = errorRecovery(lookahead, tokenIndex)) {
                        is NextToken.FoundToken -> {
                            pair = nt.lookahead to nt.tokenIndex
                            continue@inner
                        }
                        is NextToken.Eof -> return parseEof()
                        is NextToken.Done -> return nt.result
                    }
                }
            }
        }
    }

    /** Invoked when we have no more tokens to consume. */
    private fun parseEof(): ParseResult<Success, Location, Token, Error> {
        while (true) {
            val topState = topState()
            val action = definition.eofAction(topState)
            val reduceIndex = action.asReduce()
            if (reduceIndex != null) {
                val result = definition.reduce(reduceIndex, null, states, symbols)
                if (result != null) {
                    return result
                }
            } else {
                when (val nt = errorRecovery(null, null)) {
                    is NextToken.FoundToken -> error("cannot find token at EOF")
                    is NextToken.Done -> return nt.result
                    is NextToken.Eof -> continue
                }
            }
        }
    }

    private fun errorRecovery(
        initialLookahead: Triple<Location, Token, Location>?,
        initialTokenIndex: TokenIndex?,
    ): NextToken<Location, Token, TokenIndex, Success, Error> {
        var optLookahead = initialLookahead
        var optTokenIndex = initialTokenIndex

        debug { "\\+ errorRecovery(optLookahead=$optLookahead, optTokenIndex=$optTokenIndex)" }

        if (!definition.usesErrorRecovery()) {
            debug { "\\ error -- no error recovery!" }

            return NextToken.Done(
                ParseResult.Failure(unrecognizedTokenError(optLookahead, states))
            )
        }

        val error = unrecognizedTokenError(optLookahead, states)

        val droppedTokens: MutableList<Triple<Location, Token, Location>> = mutableListOf()

        // We are going to insert ERROR into the lookahead. So, first, perform
        // all reductions from current state triggered by having ERROR in the
        // lookahead.
        while (true) {
            val state = topState()
            val action = definition.errorAction(state)
            val reduceIndex = action.asReduce()
            if (reduceIndex != null) {
                debug { "\\\\ reducing: $reduceIndex" }

                val result = reduce(reduceIndex, optLookahead?.first)
                if (result != null) {
                    debug { "\\\\ reduced to a result" }

                    return NextToken.Done(result)
                }
            } else {
                break
            }
        }

        // Now try to find the recovery state.
        val statesLen = states.size
        val top: Int
        findState@ while (true) {
            // Go backwards through the states...
            debug { "\\\\+ errorRecovery: findState loop, ${states.size} states = $states" }

            var foundTop: Int? = null
            for (candidate in (statesLen - 1) downTo 0) {
                val state = states[candidate]
                debug { "\\\\\\ top = $candidate, state = $state" }

                // ...fetch action for error token...
                val action = definition.errorAction(state)
                debug { "\\\\\\ action = $action" }
                val errorState = action.asShift()
                if (errorState != null) {
                    // If action is a shift that takes us into `errorState`,
                    // and `errorState` can accept this lookahead, we are done.
                    if (accepts(errorState, states.subList(0, candidate + 1), optTokenIndex)) {
                        debug { "\\\\\\ accepted!" }
                        foundTop = candidate
                        break
                    }
                } else {
                    // ...else, if action is error or reduce, go to next state.
                    continue
                }
            }
            if (foundTop != null) {
                top = foundTop
                break@findState
            }

            // Otherwise, if we could not find a state that would — after
            // shifting the error token — accept the lookahead, then drop the
            // lookahead and advance to next token in the input.
            val currentLookahead = optLookahead
            if (currentLookahead == null) {
                // If the lookahead is EOF, we cannot drop any more tokens,
                // abort error recovery and just report the original error
                // (it might be nice if we would propagate back the dropped
                // tokens, though).
                debug { "\\\\\\ no more lookahead, report error" }
                return NextToken.Done(ParseResult.Failure(error))
            }

            // Else, drop the current token and shift to the next. If there is
            // a next token, we will `continue` to the start of the
            // `findState` loop.
            debug { "\\\\\\ dropping lookahead token" }

            droppedTokens.add(currentLookahead)
            optLookahead = null
            when (val nt = nextToken()) {
                is NextToken.FoundToken -> {
                    optLookahead = nt.lookahead
                    optTokenIndex = nt.tokenIndex
                }
                is NextToken.Eof -> {
                    debug { "\\\\\\ reached EOF" }
                    optLookahead = null
                    optTokenIndex = null
                }
                is NextToken.Done -> {
                    debug { "\\\\\\ no more tokens" }
                    return NextToken.Done(nt.result)
                }
            }
        }

        // If we get here, we are ready to push the error recovery state.
        //
        // We have to compute the span for the error recovery token. We do
        // this first, before we pop any symbols off the stack. There are
        // several possibilities, in order of preference.
        //
        // For the **start** of the message, we prefer to use the start of
        // any popped states. This represents parts of the input we had
        // consumed but had to roll back and ignore.
        //
        // Example:
        //
        //       a + (b + /)
        //              ^ start point is here, since this `+` will be popped off
        //
        // If there are no popped states, but there *are* dropped tokens, we
        // can use the start of those.
        //
        // Example:
        //
        //       a + (b + c e)
        //                  ^ start point would be here
        //
        // Finally, if there are no popped states *nor* dropped tokens, we
        // can use the end of the top-most state.
        val start: Location = if (top < symbols.size) {
            symbols[top].first
        } else if (droppedTokens.isNotEmpty()) {
            droppedTokens.first().first
        } else if (top > 0) {
            symbols[top - 1].third
        } else {
            definition.startLocation()
        }

        // For the end span, here are the possibilities:
        //
        // We prefer to use the end of the last dropped token.
        //
        // Examples:
        //
        //       a + (b + /)
        //              ---
        //       a + (b c)
        //              -
        //
        // But, if there are no dropped tokens, we will use the end of the
        // popped states, if any:
        //
        //       a + /
        //         -
        //
        // If there are neither dropped tokens *or* popped states, then the
        // user is simulating insertion of an operator. In this case, we
        // prefer the start of the lookahead, but fallback to the start if we
        // are at EOF.
        //
        // Examples:
        //
        //       a + (b c)
        //             -
        val end: Location = if (droppedTokens.isNotEmpty()) {
            droppedTokens.last().third
        } else if (statesLen - 1 > top) {
            symbols.last().third
        } else {
            val la = optLookahead
            la?.first ?: start
        }

        truncate(states, top + 1)
        truncate(symbols, top)

        val recoverState = states[top]
        val errorAction = definition.errorAction(recoverState)
        val errorState = errorAction.asShift()
            ?: error("expected error action to be a shift, was $errorAction")
        states.add(errorState)
        val recovery = definition.errorRecoverySymbol(
            ErrorRecovery(error = error, droppedTokens = droppedTokens)
        )
        symbols.add(Triple(start, recovery, end))

        val lf = optLookahead
        val ti = optTokenIndex
        return when {
            lf != null && ti != null -> NextToken.FoundToken(lf, ti)
            lf == null && ti == null -> NextToken.Eof()
            else -> error("lookahead and token_index mismatched: $lf, $ti")
        }
    }

    /**
     * The `accepts` function has the job of figuring out whether the given
     * error state would "accept" the given lookahead. We basically trace
     * through the LR automaton looking for one of two outcomes:
     *
     * - the lookahead is eventually shifted
     * - we reduce to the end state successfully (in the case of EOF).
     *
     * If we used the pure LR(1) algorithm, we would not need this function,
     * because we would be guaranteed to error immediately (and not after
     * some number of reductions). But with an LALR (or Lane Table) generated
     * automaton, it is possible to reduce some number of times before
     * encountering an error. Failing to take this into account can lead
     * error recovery into an infinite loop (see the `errorRecoveryLalrLoop`
     * test) or produce crappy results (see `errorRecoveryLockIn`).
     */
    private fun accepts(
        errorState: StateIndex,
        states: List<StateIndex>,
        optTokenIndex: TokenIndex?,
    ): Boolean {
        debug {
            "\\\\\\+ accepts(errorState=$errorState, states=$states, optTokenIndex=$optTokenIndex)"
        }

        val scratch: MutableList<StateIndex> = states.toMutableList()
        scratch.add(errorState)
        while (true) {
            var statesLen = scratch.size
            val top = scratch[statesLen - 1]
            val action = if (optTokenIndex == null) {
                definition.eofAction(top)
            } else {
                definition.action(top, optTokenIndex)
            }

            // If we encounter an error action, we do **not** accept.
            if (action.isError()) {
                debug { "\\\\\\\\ accepts: error" }
                return false
            }

            // If we encounter a reduce action, we need to simulate its effect
            // on the state stack.
            val reduceAction = action.asReduce()
            if (reduceAction != null) {
                when (val sim = definition.simulateReduce(reduceAction)) {
                    is SimulatedReduce.Reduce -> {
                        statesLen -= sim.statesToPop
                        truncate(scratch, statesLen)
                        val newTop = scratch[statesLen - 1]
                        val nextState = definition.goto(newTop, sim.nonterminalProduced)
                        scratch.add(nextState)
                    }
                    is SimulatedReduce.Accept -> {
                        debug { "\\\\\\\\ accepts: reduce accepts!" }
                        return true
                    }
                }
            } else {
                // If we encounter a shift action, we DO accept.
                debug { "\\\\\\\\ accepts: shift accepts!" }
                check(action.isShift())
                return true
            }
        }
    }

    private fun reduce(
        action: ReduceIndex,
        lookaheadStart: Location?,
    ): ParseResult<Success, Location, Token, Error>? {
        return definition.reduce(action, lookaheadStart, states, symbols)
    }

    private fun unrecognizedTokenError(
        token: Triple<Location, Token, Location>?,
        states: List<StateIndex>,
    ): ParseError<Location, Token, Error> {
        return if (token != null) {
            ParseError.UnrecognizedToken(
                token = token,
                expected = definition.expectedTokensFromStates(states),
            )
        } else {
            ParseError.UnrecognizedEof(
                location = lastLocation,
                expected = definition.expectedTokensFromStates(states),
            )
        }
    }

    /**
     * Consume the next token from the input and classify it into a token
     * index. Classification can fail with an error. If there are no more
     * tokens, signal EOF.
     */
    private fun nextToken(): NextToken<Location, Token, TokenIndex, Success, Error> {
        if (!tokens.hasNext()) {
            return NextToken.Eof()
        }
        val token: Triple<Location, Token, Location> = when (val tr = tokens.next()) {
            is TokResult.Ok -> tr.value
            is TokResult.Err -> return NextToken.Done(ParseResult.Failure(tr.error))
        }

        lastLocation = token.third

        val tokenIndex = definition.tokenToIndex(token.second)
            ?: return NextToken.Done(
                ParseResult.Failure(unrecognizedTokenError(token, states))
            )

        return NextToken.FoundToken(token, tokenIndex)
    }
}

/**
 * Internal trichotomy returned by the next-token / error-recovery helpers:
 * either a usable token was produced, or the input was exhausted, or the
 * driver should terminate with the given [ParseResult].
 */
private sealed class NextToken<Location, Token, TokenIndex, Success, Error> {
    class FoundToken<Location, Token, TokenIndex, Success, Error>(
        val lookahead: Triple<Location, Token, Location>,
        val tokenIndex: TokenIndex,
    ) : NextToken<Location, Token, TokenIndex, Success, Error>()

    class Eof<Location, Token, TokenIndex, Success, Error> :
        NextToken<Location, Token, TokenIndex, Success, Error>()

    class Done<Location, Token, TokenIndex, Success, Error>(
        val result: ParseResult<Success, Location, Token, Error>,
    ) : NextToken<Location, Token, TokenIndex, Success, Error>()
}

/**
 * Mirrors the upstream `Vec::truncate` by clearing the tail of [list] beyond
 * [newLen] in place.
 */
private fun <T> truncate(list: MutableList<T>, newLen: Int) {
    if (list.size > newLen) {
        list.subList(newLen, list.size).clear()
    }
}
