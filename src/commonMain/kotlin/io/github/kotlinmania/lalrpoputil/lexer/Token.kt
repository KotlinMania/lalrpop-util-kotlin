// SPDX-License-Identifier: Apache-2.0 OR MIT
// port-lint: source src/lexer.rs
package io.github.kotlinmania.lalrpoputil.lexer

/** A token matched by the built-in lalrpop lexer. */
data class Token(
    val index: Int,
    val text: String,
) : Comparable<Token> {
    override fun compareTo(other: Token): Int {
        val indexCmp = index.compareTo(other.index)
        return if (indexCmp != 0) indexCmp else text.compareTo(other.text)
    }

    override fun toString(): String = text
}
