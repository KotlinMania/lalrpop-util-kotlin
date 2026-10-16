# High Priority Ports - Action Plan

## Files by Impact

Priority = deps * 1,000,000 + SymDeficit * 10,000 + SrcSymbols * 100 + (1 - function similarity) * 10

Dependency fanout is ranked first so the ladder favors ports that clear downstream compilation failures fastest.

This list is complete and includes function/type detail for every matched file. Function similarity is the required body/parameter comparison; file-level shape does not rescue a port.

| Rank | Source | Target | Function similarity | Deps | Functions | Missing functions | Types | Missing types | SymDeficit | SrcSymbols | Priority |
|------|--------|--------|------------|------|-----------|-------------------|-------|---------------|-----------|------------|----------|
| 1 | `state_machine` | `statemachine.Parser` | 0.24 | 0 | 10/10 matched (target 27) | _none_ | 11/15 matched (target 23) | `Location`, `Token`, `Error`, `Symbol` | 4 | 25 | 42507.6 |
| 2 | `lib` | `lalrpoputil.ParseError` | 0.22 | 0 | 9/9 matched (target 20) | _none_ | 2/2 matched (target 8) | _none_ | 0 | 11 | 1107.8 |
| 3 | `lexer` | `lexer.Token` | 0.02 | 0 | 4/4 matched (target 9) | _none_ | 4/4 matched (target 7) | _none_ | 0 | 8 | 809.8 |

## Cheat Detection / Scoring Failures

_None detected._

## Critical Issues (Function Similarity < 0.60 with Dependencies)

No critical issues with dependencies.

## Missing Files (by Dependents)

No missing files detected.

