# High Priority Ports - Action Plan

## Files by Impact

Priority = deps * 1,000,000 + SymDeficit * 10,000 + SrcSymbols * 100 + (1 - function similarity) * 10

Dependency fanout is ranked first so the ladder favors ports that clear downstream compilation failures fastest.

This list is complete and includes function/type detail for every matched file. Function similarity is the required body/parameter comparison; file-level shape does not rescue a port.

| Rank | Source | Target | Function similarity | Deps | Functions | Missing functions | Types | Missing types | SymDeficit | SrcSymbols | Priority |
|------|--------|--------|------------|------|-----------|-------------------|-------|---------------|-----------|------------|----------|
| 1 | `state_machine` | `statemachine.Parser [ZERO]` | 0.00 | 0 | 10/10 matched (target 27) | _none_ | 7/15 matched (target 19) | `Location`, `Token`, `Error`, `Symbol`, `ParseError`, `TokenTriple`, `SymbolTriple`, `ErrorRecovery` | 8 | 25 | 82510.0 |
| 2 | `lib` | `lalrpoputil.ParseError [STUB]` | 0.00 | 0 | 6/9 matched (target 22) | `fmt`, `description`, `test` | 2/2 matched (target 8) | _none_ | 3 | 11 | 31110.0 |

## Cheat Detection / Scoring Failures

- `state_machine` -> `statemachine.Parser [ZERO]`: function-by-function score forced to 0. ParserAction.kt: snake_case identifier `integral_indices` in Kotlin comments; ParserAction.kt: Rust `pub` item in Kotlin comments
- `lib` -> `lalrpoputil.ParseError [STUB]`: function-by-function score forced to 0. target contains TODO/stub/placeholder markers in function bodies

## Critical Issues (Function Similarity < 0.60 with Dependencies)

No critical issues with dependencies.

## Missing Files (by Dependents)

| Rank | Source file | Expected target | Deps | Functions | Classes/types | Symbols | Source path | Expected path |
|------|-------------|-----------------|------|-----------|---------------|---------|-------------|---------------|
| 1 | `build` | `Build` | 0 | 1 | 0 | 1 | `build.rs` | `Build.kt` |
| 2 | `lexer` | `Lexer` | 0 | 4 | 4 | 8 | `src/lexer.rs` | `Lexer.kt` |

