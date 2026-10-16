// SPDX-License-Identifier: Apache-2.0 OR MIT
// port-lint: source state_machine.rs
package io.github.kotlinmania.lalrpoputil.statemachine

/**
 * An action produced by a parse table: shift, reduce, or error.
 */
interface ParserAction<StateIndex, ReduceIndex> {
    /** If this action is a shift, returns the target state; otherwise `null`. */
    fun asShift(): StateIndex?

    /** If this action is a reduce, returns the reduction index; otherwise `null`. */
    fun asReduce(): ReduceIndex?

    /** True iff this action is a shift. */
    fun isShift(): Boolean

    /** True iff this action is a reduce. */
    fun isReduce(): Boolean

    /** True iff this action is an error sentinel. */
    fun isError(): Boolean
}

// In generated rules, integers represent index values (using the smallest fitting width).
// Positive values shift, negative values reduce, zero represents an error sentinel.

/** [ParserAction] backed by a `Byte` (corresponds to upstream `i8`). */
data class ByteAction(
    val value: Byte,
) : ParserAction<Byte, Byte> {
    override fun asShift(): Byte? = if (value > 0) (value - 1).toByte() else null

    override fun asReduce(): Byte? = if (value < 0) (-(value + 1)).toByte() else null

    override fun isShift(): Boolean = value > 0

    override fun isReduce(): Boolean = value < 0

    override fun isError(): Boolean = value.toInt() == 0
}

/** [ParserAction] backed by a `Short` (corresponds to upstream `i16`). */
data class ShortAction(
    val value: Short,
) : ParserAction<Short, Short> {
    override fun asShift(): Short? = if (value > 0) (value - 1).toShort() else null

    override fun asReduce(): Short? = if (value < 0) (-(value + 1)).toShort() else null

    override fun isShift(): Boolean = value > 0

    override fun isReduce(): Boolean = value < 0

    override fun isError(): Boolean = value.toInt() == 0
}

/** [ParserAction] backed by an `Int` (corresponds to upstream `i32`). */
data class IntAction(
    val value: Int,
) : ParserAction<Int, Int> {
    override fun asShift(): Int? = if (value > 0) value - 1 else null

    override fun asReduce(): Int? = if (value < 0) -(value + 1) else null

    override fun isShift(): Boolean = value > 0

    override fun isReduce(): Boolean = value < 0

    override fun isError(): Boolean = value == 0
}
