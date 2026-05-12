# High Priority Ports - Action Plan

## Files by Impact

Priority = deps * 1,000,000 + SymDeficit * 10,000 + SrcSymbols * 100 + (1 - function similarity) * 10

Dependency fanout is ranked first so the ladder favors ports that clear downstream compilation failures fastest.

This list is complete and includes function/type detail for every matched file. Function similarity is the required body/parameter comparison; file-level shape does not rescue a port.

| Rank | Source | Target | Function similarity | Deps | Functions | Missing functions | Types | Missing types | SymDeficit | SrcSymbols | Priority |
|------|--------|--------|------------|------|-----------|-------------------|-------|---------------|-----------|------------|----------|
| 1 | `state_machine` | `statemachine.Parser [ZERO] [PROVENANCE-FALLBACK]` | 0.00 | 0 | 10/10 matched (target 27) | _none_ | 7/15 matched (target 19) | `Location`, `Token`, `Error`, `Symbol`, `ParseError`, `TokenTriple`, `SymbolTriple`, `ErrorRecovery` | 8 | 25 | 82510.0 |
| 2 | `lexer` | `lexer.Token [ZERO] [PROVENANCE-FALLBACK]` | 0.00 | 0 | 3/4 matched (target 15) | `fmt` | 3/4 matched (target 7) | `Item` | 2 | 8 | 20810.0 |
| 3 | `lib` | `lalrpoputil.ParseError [STUB] [PROVENANCE-FALLBACK]` | 0.00 | 0 | 8/9 matched (target 19) | `description` | 2/2 matched (target 8) | _none_ | 1 | 11 | 11110.0 |

## Cheat Detection / Scoring Failures

- `state_machine` -> `statemachine.Parser [ZERO] [PROVENANCE-FALLBACK]`: function-by-function score forced to 0. ParserAction.kt: snake_case identifier `integral_indices` in Kotlin comments; ParserAction.kt: Rust `pub` item in Kotlin comments
- `lexer` -> `lexer.Token [ZERO] [PROVENANCE-FALLBACK]`: function-by-function score forced to 0. MatcherBuilder.kt: Rust `match` expression in Kotlin code
- `lib` -> `lalrpoputil.ParseError [STUB] [PROVENANCE-FALLBACK]`: function-by-function score forced to 0. target contains TODO/stub/placeholder markers in function bodies

## Critical Issues (Function Similarity < 0.60 with Dependencies)

No critical issues with dependencies.

## Missing Files (by Dependents)

No missing files detected.

