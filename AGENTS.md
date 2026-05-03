# lalrpop-util-kotlin — agent guide

This repo is the Kotlin Multiplatform port of the upstream Rust
[`lalrpop-util`](https://crates.io/crates/lalrpop-util) crate. Upstream
source mirrors into `tmp/lalrpop-util-<rev>/` via
`tmp/refresh-upstream.sh` and is the **read-only** translation oracle.
Never edit `tmp/`.

## Scope

This artifact is the **runtime island** — `ParseError`, `ErrorRecovery`,
`state_machine::ParserDefinition` + parser driver, the optional
built-in `lexer::Matcher`, and the small Kotlin-only adapter classes
(`ParseTables`, `Production`, `TableDriven*`) that the Kotlin-emit
codegen back-end of `lalrpop-kotlin` builds on top of.

The runtime is the leaf of every consumer's dependency graph. Anything
that pulls in `kotlinx-*`, anything generator-side (parser of `.lalrpop`,
LR builder, codegen), or anything that grows the artifact size for a
benefit any single consumer can replicate themselves does **not**
belong here.

## Maven coordinates

`io.github.kotlinmania:lalrpop-util-kotlin:<version>`

Package root: `io.github.kotlinmania.lalrpoputil`. Subpackages mirror
upstream Rust modules:

- `io.github.kotlinmania.lalrpoputil` ← `lalrpop_util` crate root
  (`ParseError`, `ErrorRecovery`)
- `io.github.kotlinmania.lalrpoputil.statemachine` ← `lalrpop_util::state_machine`
- `io.github.kotlinmania.lalrpoputil.lexer` ← `lalrpop_util::lexer`
- `io.github.kotlinmania.lalrpoputil.tabledriven` ← Kotlin-only auxiliary
  types (no upstream module; Rust embeds the same data inline as
  `const __ACTION: &[i16]` arrays in generated source)

## Port-lint headers

Every Kotlin file derived from upstream MUST start with:

```kotlin
// port-lint: source <path-relative-to-tmp/lalrpop-util/>
package io.github.kotlinmania.lalrpoputil.<module>
```

Example:

```kotlin
// port-lint: source src/state_machine.rs
package io.github.kotlinmania.lalrpoputil.statemachine
```

Path is relative to `tmp/lalrpop-util-<rev>/`. This is how `ast_distance`
tracks provenance.

For Kotlin-only auxiliary files use:

```kotlin
// port-lint: ignore — Kotlin-only adapter (no upstream counterpart;
//                    Rust embeds equivalent data inline in generated source)
package io.github.kotlinmania.lalrpoputil.tabledriven
```

## Translation discipline

These are line-by-line **transliterations**. Read the Rust file end to
end, then port. Don't reorder, summarize, or "improve."

- **Doc comments translate word-for-word.** Rust syntax inside KDoc
  (`Vec<T>`, `Option<&str>`, `Self::foo()`, lifetimes, `cfg(test)`,
  `#[derive(...)]`) gets rewritten to its Kotlin equivalent (`List<T>`,
  `String?`, `foo()`, KDoc links). Translate the code-in-comment; never
  delete the comment to silence a rule.
- **No no-op shells.** Rust constructs the GC subsumes (`Box<T>`,
  `Cell<T>`, `RefCell<T>`, `Arc<T>`, `Rc<T>`, `Pin`, `mem::forget`,
  `drop_in_place`, `MaybeUninit<T>`, `dyn Trait`) get **deleted** in
  the port. Inline the wrapped value or use the closest Kotlin idiom.
  Empty shells inflate symbol counts without porting any behavior.
- **Tests live in `commonTest`.** Inline `#[cfg(test)] mod tests` blocks
  port to `commonTest` mirroring the same package path.

## Trait default methods with `where` clauses → method-level Kotlin generic bounds

Rust traits routinely declare a default method whose body only
typechecks when the type parameter satisfies a stricter bound:

```rust
pub trait RangeBounds<T> {
    fn start_bound(&self) -> Bound<&T>;
    fn end_bound(&self) -> Bound<&T>;

    fn is_empty(&self) -> bool
    where T: PartialOrd,
    { /* default body uses < */ }
}
```

The trait stays unconstrained; the *method* picks up the bound via its
own `where` clause. Kotlin has no per-method `where` on an interface
member. Three obvious mappings fail:

1. **Tighten the interface to `<T : Comparable<T>>`.** Breaks every
   caller that holds the unbounded interface type.
2. **Make the method abstract on the interface.** Forces every concrete
   impl to invent a body and pile on `override` boilerplate, even when
   the Rust counterpart inherits the default unchanged.
3. **Runtime cast helper** — `if (left is Comparable<*> ...) ... else throw IllegalStateException(...)`.
   Compile-time bounds become runtime crashes; the cheat detector flags
   this and zeros the file's score.

### The faithful pattern

Translate the default to a Kotlin **extension function whose own type
parameter carries the bound**:

```kotlin
interface RangeBounds<T> {
    fun startBound(): Bound<T>
    fun endBound(): Bound<T>
}

fun <T : Comparable<T>> RangeBounds<T>.isEmpty(): Boolean { /* default body */ }
```

Concrete impls that want to specialise the default supply a same-named
**member function**. Kotlin resolves `range.isEmpty()` to the member
when the static receiver type is the concrete class and to the
extension when it is the interface — exactly mirroring Rust's
"default method, per-impl override". No `override` keyword on the
member; there is nothing on the interface to override.

When the bound lives on a *class* parameter (e.g. `impl<K: Ord> Map<K, V>`),
Kotlin has no method-level analog — class type parameters bind for the
whole class. Use the `Comparator<in K>` field pattern with a
`compareKeys(a, b)` dispatch helper that prefers the supplied
comparator and falls back to a `Comparable<K>`-based path.

## Code discipline

- **No `@Suppress`.** Warnings are errors. Fix the cause.
- **No stubs.** No `TODO()`, no `error("not implemented")`, no empty
  class bodies on types that have fields and methods.
- **No JVM imports.** No `kotlin.jvm.*`, no `java.*`, no `javax.*`.
- **No third-party dependencies.** This is the runtime that every
  generated parser ships against. Every dependency added here cascades
  to every downstream consumer of every generated parser. Raise on
  Slack before adding any.

## Blast radius

- No repo-wide scripting (`find -exec`, blanket `sed`/`perl`, regex over
  many files).
- Changes are task-scoped, not pattern-scoped. Every touched file is
  named up front.
- Small multi-file changes are allowed when mechanically coupled —
  primary file plus its `commonTest` and any required call-site
  rewires.
- No drive-by refactors, renames, or formatting churn.
- More than ~5 files in a single change? Stop and ask.

## Verification

The build gate is **`./gradlew test`**.

```bash
./gradlew macosArm64Test
./gradlew linuxX64Test
./gradlew jsNodeTest
./gradlew wasmJsNodeTest
```

`./gradlew jvmTest` is **not** valid — there is no JVM target.

## Approved dependencies

**None at present.** Pure Kotlin stdlib only.

## Dependents

Downstream Kotlin consumers (must stay on a published version of this
artifact, never composite/include-build):

- `lalrpop-kotlin` — the parser generator. Its bootstrap parser, build
  driver, and Kotlin-emit codegen all import runtime types.
- `starlark-syntax-kotlin` — consumes the generated `StarlarkParser.kt`
  + this runtime when the Kotlin-emit backend lands.
- `starlark-lsp-kotlin` (future) — same shape.

## Subagent policy

Do not delegate `.kt` writes to subagents. Subagents cheat on
translation: hollow out KDoc, drop semantically load-bearing
constructs, produce confident summaries that mask damage. Search and
read-only reports via subagents are fine. Edits happen in the main
loop.

## Commit style

No AI branding, no Co-Authored-By lines, no emoji. Clear, descriptive
messages focused on what changed and why. One file → one commit; squash
later via `git rebase -i` for logical units.

## Maintainer

Sydney Renee &lt;sydney@solace.ofharmony.ai&gt;,
The Solace Project (KotlinMania org).

Discussion: [Solace Project Discord](https://discord.gg/rJqVeSmx4).
