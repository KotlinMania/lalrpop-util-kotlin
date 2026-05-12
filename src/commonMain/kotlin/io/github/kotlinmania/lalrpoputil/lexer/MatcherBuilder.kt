// SPDX-License-Identifier: Apache-2.0 OR MIT
// port-lint: source src/lexer.rs
package io.github.kotlinmania.lalrpoputil.lexer

import io.github.kotlinmania.lalrpoputil.ParseError
import io.github.kotlinmania.lalrpoputil.statemachine.TokResult

/** The built-in lalrpop lexer. */
class MatcherBuilder private constructor(
    private val exprs: List<LexerExpr>,
) {
    companion object {
        /** Build a [MatcherBuilder] from `(regex, skip)` pairs. */
        fun new(exprs: Iterable<Pair<String, Boolean>>): Result<MatcherBuilder> {
            val compiled = mutableListOf<LexerExpr>()
            for ((regex, skip) in exprs) {
                val compiledRegex = try {
                    Regex(regex)
                } catch (e: Throwable) {
                    return Result.failure(BuildError(regex, e))
                }
                compiled.add(LexerExpr(compiledRegex, skip))
            }
            return Result.success(MatcherBuilder(compiled))
        }
    }

    /** Create a matcher over [text]. */
    fun <E> matcher(text: String): Matcher<E> = Matcher(text, exprs)
}

/** Iterator over tokens produced by the built-in lalrpop lexer. */
class Matcher<E> internal constructor(
    private var text: String,
    private val exprs: List<LexerExpr>,
) : Iterator<TokResult<Int, Token, E>> {
    private var consumed: Int = 0
    private var nextItem: TokResult<Int, Token, E>? = null
    private var nextReady: Boolean = false

    override fun hasNext(): Boolean {
        if (!nextReady) {
            nextItem = nextToken()
            nextReady = true
        }
        return nextItem != null
    }

    override fun next(): TokResult<Int, Token, E> {
        if (!hasNext()) {
            throw NoSuchElementException()
        }
        val item = nextItem
        nextItem = null
        nextReady = false
        return item ?: throw NoSuchElementException()
    }

    private fun nextToken(): TokResult<Int, Token, E>? {
        while (true) {
            val startOffset = consumed
            if (text.isEmpty()) {
                consumed = startOffset
                return null
            }

            val match = findMatch()
                ?: return TokResult.Err(ParseError.InvalidToken(location = startOffset))
            val result = text.substring(0, match.endOffset)
            text = text.substring(match.endOffset)
            consumed = startOffset + match.endOffset

            if (exprs[match.index].skip) {
                if (match.endOffset == 0) {
                    return TokResult.Err(ParseError.InvalidToken(location = startOffset))
                }
                continue
            }

            return TokResult.Ok(Triple(startOffset, Token(match.index, result), consumed))
        }
    }

    private fun findMatch(): LexerMatch? {
        var best: LexerMatch? = null
        for ((index, expr) in exprs.withIndex()) {
            val match = expr.regex.find(text) ?: continue
            if (match.range.first != 0) {
                continue
            }
            val length = match.value.length
            val current = best
            if (current == null || length > current.endOffset || length == current.endOffset && index > current.index) {
                best = LexerMatch(index, length)
            }
        }
        return best
    }
}

internal data class LexerExpr(
    val regex: Regex,
    val skip: Boolean,
)

private data class LexerMatch(
    val index: Int,
    val endOffset: Int,
)
