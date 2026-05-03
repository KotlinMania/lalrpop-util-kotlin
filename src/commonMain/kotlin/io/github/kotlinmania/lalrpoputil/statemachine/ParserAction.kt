// SPDX-License-Identifier: Apache-2.0 OR MIT
// port-lint: source src/state_machine.rs
package io.github.kotlinmania.lalrpoputil.statemachine

/**
 * An action produced by a parse table: shift, reduce, or error.
 *
 * The upstream trait is `pub trait ParserAction<D: ParserDefinition>` and is
 * parameterised by the entire parser definition so it can refer to
 * `D::StateIndex` and `D::ReduceIndex`. Here the action only ever needs the
 * two index types directly, so the Kotlin interface carries them as its own
 * type parameters and the parser definition wires them up at use sites.
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

// ---------------------------------------------------------------------------
// integralIndices(i8 / i16 / i32) — port of the upstream `integral_indices!`
// macro that implements `ParserAction` for each of the three integer widths
// generated parsers select between based on state count.
//
// LALRPOP-generated parsers pack action codes into the smallest integer that
// can hold every state and reduction index: positive values shift to
// `value - 1`, negative values reduce by `-(value + 1)`, zero is an error
// sentinel. The same scheme is mirrored here per width.
// ---------------------------------------------------------------------------

/** [ParserAction] backed by a `Byte` (corresponds to upstream `i8`). */
data class ByteAction(val value: Byte) : ParserAction<Byte, Byte> {
    override fun asShift(): Byte? = if (value > 0) (value - 1).toByte() else null
    override fun asReduce(): Byte? = if (value < 0) (-(value + 1)).toByte() else null
    override fun isShift(): Boolean = value > 0
    override fun isReduce(): Boolean = value < 0
    override fun isError(): Boolean = value.toInt() == 0
}

/** [ParserAction] backed by a `Short` (corresponds to upstream `i16`). */
data class ShortAction(val value: Short) : ParserAction<Short, Short> {
    override fun asShift(): Short? = if (value > 0) (value - 1).toShort() else null
    override fun asReduce(): Short? = if (value < 0) (-(value + 1)).toShort() else null
    override fun isShift(): Boolean = value > 0
    override fun isReduce(): Boolean = value < 0
    override fun isError(): Boolean = value.toInt() == 0
}

/** [ParserAction] backed by an `Int` (corresponds to upstream `i32`). */
data class IntAction(val value: Int) : ParserAction<Int, Int> {
    override fun asShift(): Int? = if (value > 0) value - 1 else null
    override fun asReduce(): Int? = if (value < 0) -(value + 1) else null
    override fun isShift(): Boolean = value > 0
    override fun isReduce(): Boolean = value < 0
    override fun isError(): Boolean = value == 0
}
