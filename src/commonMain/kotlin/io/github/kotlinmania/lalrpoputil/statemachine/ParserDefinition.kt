// SPDX-License-Identifier: Apache-2.0 OR MIT
// port-lint: source src/state_machine.rs
package io.github.kotlinmania.lalrpoputil.statemachine

import io.github.kotlinmania.lalrpoputil.ErrorRecovery

/**
 * The core trait implemented by every LALRPOP-generated parser.
 *
 * Each of the upstream associated types (`Location`, `Error`, `Token`,
 * `TokenIndex`, `Symbol`, `Success`, `StateIndex`, `Action`, `ReduceIndex`,
 * `NonterminalIndex`) is promoted to a distinct generic type parameter,
 * because Kotlin interfaces have no associated-type facility. A generated
 * parser fixes all ten parameters at its declaration site and consumers
 * normally interact with the parser through its concrete type, never through
 * the abstract interface.
 */
interface ParserDefinition<
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
> {
    /** Returns a location representing the "start of the input". */
    fun startLocation(): Location

    /** Returns the initial state. */
    fun startState(): StateIndex

    /**
     * Converts the user tokens into an internal index; this index is then
     * used to index into actions and the like. When using an internal
     * tokenizer, these indices are directly produced. When using an
     * **external** tokenizer, however, this function matches against the
     * patterns given by the user: it is fallible therefore as these patterns
     * may not be exhaustive. If a token value is found that does not match
     * any of the patterns the user supplied, then this function returns
     * `null`, which is translated into a parse error by LALRPOP
     * ("unrecognized token").
     */
    fun tokenToIndex(token: Token): TokenIndex?

    /**
     * Given the top-most state and the pending terminal, returns an action.
     * This can be either SHIFT(state), REDUCE(action), or ERROR.
     */
    fun action(state: StateIndex, tokenIndex: TokenIndex): Action

    /**
     * Returns the action to take if an error occurs in the given state. This
     * function is the same as the ordinary [action], except that it applies
     * not to the user terminals but to the "special terminal" `!`.
     */
    fun errorAction(state: StateIndex): Action

    /**
     * Action to take if EOF occurs in the given state. This function is the
     * same as the ordinary [action], except that it applies not to the user
     * terminals but to the "special terminal" `$`.
     */
    fun eofAction(state: StateIndex): Action

    /**
     * If we reduce to a nonterminal in the given state, what state do we go
     * to? This is infallible due to the nature of LR(1) grammars.
     */
    fun goto(state: StateIndex, nt: NonterminalIndex): StateIndex

    /** "Upcast" a terminal into a symbol so we can push it onto the parser stack. */
    fun tokenToSymbol(tokenIndex: TokenIndex, token: Token): Symbol

    /** Returns the expected tokens in a given state. Used for error reporting. */
    fun expectedTokens(state: StateIndex): List<String>

    /**
     * Returns the expected tokens in a given state. This is used in the same
     * way as [expectedTokens] but allows more precise reporting of accepted
     * tokens in some cases.
     */
    fun expectedTokensFromStates(states: List<StateIndex>): List<String> =
        // Default to using the preexisting `expectedTokens` method.
        expectedTokens(states.last())

    /** True if this grammar supports error recovery. */
    fun usesErrorRecovery(): Boolean

    /**
     * Given error information, creates an error recovery symbol that we push
     * onto the stack (and supply to user actions).
     */
    fun errorRecoverySymbol(recovery: ErrorRecovery<Location, Token, Error>): Symbol

    /**
     * Execute a reduction in the given state: that is, execute user code. The
     * start location indicates the "starting point" of the current lookahead
     * that is triggering the reduction (it is `null` for EOF).
     *
     * The [states] and [symbols] lists represent the internal state machine
     * vectors; they are given to [reduce] so that it can pop off states that
     * no longer apply (and consume their symbols). At the end, it should also
     * push the new state and symbol produced.
     *
     * Returns a non-`null` [ParseResult] if we reduced the start state and
     * hence parsing is complete, or if we encountered an irrecoverable error.
     *
     * FIXME. It would be nice to not have so much logic live in reduce. It
     * should just be given an iterator of popped symbols and return the newly
     * produced symbol (or error). We can use [simulateReduce] and our own
     * information to drive the rest, right? This would also allow us — I
     * think — to extend error recovery to cover user-produced errors.
     */
    fun reduce(
        reduceIndex: ReduceIndex,
        startLocation: Location?,
        states: MutableList<StateIndex>,
        symbols: MutableList<Triple<Location, Symbol, Location>>,
    ): ParseResult<Success, Location, Token, Error>?

    /**
     * Returns information about how many states will be popped during a
     * reduction, and what nonterminal would be produced as a result.
     */
    fun simulateReduce(action: ReduceIndex): SimulatedReduce<NonterminalIndex>
}
