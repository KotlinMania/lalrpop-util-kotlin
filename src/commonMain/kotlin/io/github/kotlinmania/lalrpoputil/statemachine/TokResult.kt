// SPDX-License-Identifier: Apache-2.0 OR MIT
// port-lint: source src/state_machine.rs
package io.github.kotlinmania.lalrpoputil.statemachine

import io.github.kotlinmania.lalrpoputil.ParseError

/**
 * Element type for the token iterator a generated parser consumes.
 *
 * The upstream type alias is `Result<TokenTriple<D>, ParseError<D>>`, where
 * `TokenTriple<D> = (Location<D>, Token<D>, Location<D>)` represents a
 * `(start, token, end)` triple. Kotlin's built-in `Result<T>` constrains its
 * error to `Throwable`, so this sealed class is the Kotlin-shaped equivalent
 * that carries an arbitrary `ParseError<L, T, E>`.
 *
 * Tokenizers feeding [Parser.drive] yield an `Iterator<TokResult<L, T, E>>`:
 * each successful read produces an [Ok] holding the `(start, token, end)`
 * triple; tokenizer failures produce an [Err] holding the parse error to
 * surface to the parse driver, which forwards it back as a [ParseResult.Failure].
 */
sealed class TokResult<L, T, E> {
    /** The tokenizer produced a `(start, token, end)` triple. */
    data class Ok<L, T, E>(val value: Triple<L, T, L>) : TokResult<L, T, E>()

    /** The tokenizer surfaced a parse error before producing a usable token. */
    data class Err<L, T, E>(val error: ParseError<L, T, E>) : TokResult<L, T, E>()
}
