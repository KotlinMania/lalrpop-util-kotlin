// SPDX-License-Identifier: Apache-2.0 OR MIT
// port-lint: source src/state_machine.rs
package io.github.kotlinmania.lalrpoputil.statemachine

/**
 * Information produced by [ParserDefinition.simulateReduce]: either a real
 * reduction (how many states to pop, which nonterminal to push) or the special
 * "accept" outcome that signals the parse is finished.
 *
 * The upstream `enum SimulatedReduce<D>` is parameterised by the entire
 * parser definition and reads the nonterminal index off it via an associated
 * type. Here the nonterminal-index type is the only piece needed at the use
 * site, so the Kotlin port carries it directly as a single generic parameter.
 *
 * - `NonterminalIndex` — the type a generated parser uses to identify
 *   nonterminals (typically `Int`, `Short`, or `Byte`).
 */
sealed class SimulatedReduce<out NonterminalIndex> {
    /**
     * The reduction would pop [statesToPop] states off the parse stack and
     * push a [nonterminalProduced] symbol in their place.
     */
    class Reduce<out NonterminalIndex>(
        val statesToPop: Int,
        val nonterminalProduced: NonterminalIndex,
    ) : SimulatedReduce<NonterminalIndex>()

    /**
     * The reduction is the start-rule reduction; the parse is done as soon
     * as it executes.
     */
    object Accept : SimulatedReduce<Nothing>()
}
