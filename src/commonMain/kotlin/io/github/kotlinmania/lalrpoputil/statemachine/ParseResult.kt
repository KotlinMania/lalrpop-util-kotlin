// SPDX-License-Identifier: Apache-2.0 OR MIT
// port-lint: source src/state_machine.rs
package io.github.kotlinmania.lalrpoputil.statemachine

import io.github.kotlinmania.lalrpoputil.ParseError

/**
 * Result of a parse: either a fully reduced success value, or a parse error.
 *
 * The upstream type alias is `pub type ParseResult<D> = Result<Success<D>,
 * ParseError<D>>`, where `Success<D>` is the start-symbol output and
 * `ParseError<D>` is the runtime error type. Kotlin's built-in `Result<T>`
 * constrains its error to `Throwable`, so this sealed class is the
 * Kotlin-shaped equivalent that carries an arbitrary `ParseError<L, T, E>`
 * without forcing the consumer to subclass `Throwable`.
 *
 * - `S` — the success value type (the reduced start symbol).
 * - `L` — location type (the parser uses it to track spans for errors).
 * - `T` — token type.
 * - `E` — user error type carried inside [ParseError.User].
 */
sealed class ParseResult<S, L, T, E> {
    /** A fully reduced parse with [value] produced from the start symbol. */
    data class Success<S, L, T, E>(val value: S) : ParseResult<S, L, T, E>()

    /** The parse failed with [error] before reducing the start symbol. */
    data class Failure<S, L, T, E>(val error: ParseError<L, T, E>) : ParseResult<S, L, T, E>()
}
