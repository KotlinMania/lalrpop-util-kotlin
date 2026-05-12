// SPDX-License-Identifier: Apache-2.0 OR MIT
// port-lint: source src/lib.rs
package io.github.kotlinmania.lalrpoputil

import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Mirrors the upstream `lib.rs` `mod tests::test()` formatting check for
 * `UnrecognizedToken`: the Display output must list the unexpected token
 * span and the comma-and-or expected list in the documented form.
 */
class ParseErrorTest {
    @Test
    fun test() {
        val err: ParseError<Int, String, String> = ParseError.UnrecognizedToken(
            token = Triple(1, "t0", 2),
            expected = listOf("t1", "t2", "t3"),
        )
        assertEquals(
            "Unrecognized token `t0` found at 1:2\n" +
                "Expected one of t1, t2 or t3",
            err.toString(),
        )
    }

    @Test
    fun invalidTokenDisplay() {
        val err: ParseError<Int, String, String> = ParseError.InvalidToken(location = 7)
        assertEquals("Invalid token at 7", err.toString())
    }

    @Test
    fun unrecognizedEofWithExpectedList() {
        val err: ParseError<Int, String, String> = ParseError.UnrecognizedEof(
            location = 42,
            expected = listOf("a", "b"),
        )
        assertEquals(
            "Unrecognized EOF found at 42\n" +
                "Expected one of a or b",
            err.toString(),
        )
    }

    @Test
    fun unrecognizedEofWithEmptyExpected() {
        val err: ParseError<Int, String, String> = ParseError.UnrecognizedEof(
            location = 0,
            expected = emptyList(),
        )
        assertEquals("Unrecognized EOF found at 0", err.toString())
    }

    @Test
    fun extraTokenDisplay() {
        val err: ParseError<Int, String, String> = ParseError.ExtraToken(
            token = Triple(3, "x", 4),
        )
        assertEquals("Extra token x found at 3:4", err.toString())
    }

    @Test
    fun userErrorDelegatesToString() {
        val err: ParseError<Int, String, String> = ParseError.User(error = "boom")
        assertEquals("boom", err.toString())
    }

    @Test
    fun fromCompanionWrapsUserError() {
        val err: ParseError<Int, String, String> = ParseError.from("oops")
        assertEquals(ParseError.User(error = "oops"), err)
    }

    @Test
    fun mapLocationTransformsAllPositions() {
        val err: ParseError<Int, String, String> = ParseError.UnrecognizedToken(
            token = Triple(1, "tok", 5),
            expected = listOf("a"),
        )
        val mapped: ParseError<String, String, String> = err.mapLocation { "L$it" }
        val expected: ParseError<String, String, String> = ParseError.UnrecognizedToken(
            token = Triple("L1", "tok", "L5"),
            expected = listOf("a"),
        )
        assertEquals(expected, mapped)
    }

    @Test
    fun mapTokenTransformsToken() {
        val err: ParseError<Int, String, String> = ParseError.ExtraToken(
            token = Triple(1, "tok", 2),
        )
        val mapped: ParseError<Int, Int, String> = err.mapToken { it.length }
        val expected: ParseError<Int, Int, String> = ParseError.ExtraToken(
            token = Triple(1, 3, 2),
        )
        assertEquals(expected, mapped)
    }

    @Test
    fun mapErrorTransformsUserError() {
        val err: ParseError<Int, String, String> = ParseError.User(error = "msg")
        val mapped: ParseError<Int, String, Int> = err.mapError { it.length }
        assertEquals(ParseError.User(error = 3), mapped)
    }

    @Test
    fun descriptionIsParseError() {
        val err: ParseError<Int, String, String> = ParseError.InvalidToken(location = 0)
        assertEquals("parse error", err.description)
    }
}
