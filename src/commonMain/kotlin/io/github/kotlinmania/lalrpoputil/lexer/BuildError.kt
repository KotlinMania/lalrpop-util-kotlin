// SPDX-License-Identifier: Apache-2.0 OR MIT
// port-lint: source src/lexer.rs
package io.github.kotlinmania.lalrpoputil.lexer

/** Error returned when the built-in lexer cannot compile one of its regexes. */
class BuildError(
    val pattern: String,
    cause: Throwable,
) : IllegalArgumentException("could not compile lexer regex `$pattern`: ${cause.message}", cause)
