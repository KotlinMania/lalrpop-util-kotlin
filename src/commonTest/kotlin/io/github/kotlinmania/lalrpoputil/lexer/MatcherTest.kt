// SPDX-License-Identifier: Apache-2.0 OR MIT
// port-lint: source src/lexer.rs
package io.github.kotlinmania.lalrpoputil.lexer

import io.github.kotlinmania.lalrpoputil.ParseError
import io.github.kotlinmania.lalrpoputil.statemachine.TokResult
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class MatcherTest {
    @Test
    fun tokenDisplayUsesText() {
        assertEquals("abc", Token(1, "abc").toString())
    }

    @Test
    fun matcherYieldsTokensWithSpans() {
        val builder = MatcherBuilder.new(
            listOf(
                "[ \\t\\n]+".toPattern(skip = true),
                "[0-9]+".toPattern(skip = false),
                "[a-z]+".toPattern(skip = false),
            )
        ).getOrThrow()

        val items = builder.matcher<String>("abc 123").asSequence().toList()

        assertEquals(
            listOf(
                TokResult.Ok(Triple(0, Token(2, "abc"), 3)),
                TokResult.Ok(Triple(4, Token(1, "123"), 7)),
            ),
            items,
        )
    }

    @Test
    fun matcherChoosesLongestMatchThenHighestPatternIndex() {
        val builder = MatcherBuilder.new(
            listOf(
                "a".toPattern(skip = false),
                "ab".toPattern(skip = false),
                "ab".toPattern(skip = false),
            )
        ).getOrThrow()

        val item = builder.matcher<String>("ab").next()

        assertEquals(TokResult.Ok(Triple(0, Token(2, "ab"), 2)), item)
    }

    @Test
    fun matcherReturnsInvalidTokenWhenNoPatternMatches() {
        val builder = MatcherBuilder.new(listOf("[a-z]+".toPattern(skip = false))).getOrThrow()

        val item = builder.matcher<String>("1").next()

        assertEquals(TokResult.Err(ParseError.InvalidToken(location = 0)), item)
    }

    @Test
    fun skippedEmptyMatchReturnsInvalidToken() {
        val builder = MatcherBuilder.new(listOf("".toPattern(skip = true))).getOrThrow()

        val item = builder.matcher<String>("abc").next()

        assertEquals(TokResult.Err(ParseError.InvalidToken(location = 0)), item)
    }

    @Test
    fun builderReportsRegexCompileError() {
        val result = MatcherBuilder.new(listOf("[".toPattern(skip = false)))

        assertTrue(result.isFailure)
        assertIs<BuildError>(result.exceptionOrNull())
    }

    private fun String.toPattern(skip: Boolean): Pair<String, Boolean> = this to skip
}
