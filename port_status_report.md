# Code Port - Progress Report

**Generated:** 2026-05-02
**Source:** tmp/lalrpop-util
**Target:** src/commonMain/kotlin/io/github/kotlinmania/lalrpoputil

## Executive Summary

| Metric | Count | Percentage |
|--------|-------|------------|
| Function parity | 16/24 matched (target 49) | 66.7% |
| Class/type parity | 9/21 matched (target 27) | 42.9% |
| Combined symbol parity | 25/45 matched (target 76) | 55.6% |
| Average function body similarity | 0.00 | inline-code cosine |
| Average documentation similarity | 0.61 | doc text cosine |
| Missing source functions | 5 | 0% parity until ported |
| Missing source classes/types | 4 | 0% parity until ported |
| Missing source symbol files | 2 | 9 symbols |
| Cheat/scoring failures | 2 | forced to 0% |
| Total source files | 4 | 100% |
| Target units (paired) | 14 | - |
| Target files (total) | 14 | - |
| Porting progress | 2 | 50.0% (matched) |
| Missing files | 2 | 50.0% |

## Port Quality Analysis

**Average Function Similarity:** 0.00

Similarity in this report is the required function-by-function body/parameter score. Class/type parity and symbol deficits are reported beside it; whole-file shape is diagnostic only.

**Work Distribution:**
- Critical (<0.60): 2 files (100.0% of matched)
- Needs review (0.60-0.84): 0 files (0.0% of matched)

## Worst Function Scores First

Every matched file is listed from lowest function body/parameter similarity upward. Missing symbol names are not capped.

| Rank | Source | Target | Function similarity | Functions | Missing functions | Types | Missing types | Tests | Symbol deficit | Priority |
|------|--------|--------|---------------------|-----------|-------------------|-------|---------------|-------|----------------|----------|
| 1 | `state_machine` | `statemachine.Parser [ZERO]` | 0.00 | 10/10 matched (target 27) | _none_ | 7/15 matched (target 19) | `Location`, `Token`, `Error`, `Symbol`, `ParseError`, `TokenTriple`, `SymbolTriple`, `ErrorRecovery` | - | 8 | 82510.0 |
| 2 | `lib` | `lalrpoputil.ParseError [STUB]` | 0.00 | 6/9 matched (target 22) | `fmt`, `description`, `test` | 2/2 matched (target 8) | _none_ | 0/1 | 3 | 31110.0 |

## Cheat Detection / Scoring Failures

- `state_machine` -> `statemachine.Parser [ZERO]`: function-by-function score forced to 0. ParserAction.kt: snake_case identifier `integral_indices` in Kotlin comments; ParserAction.kt: Rust `pub` item in Kotlin comments
- `lib` -> `lalrpoputil.ParseError [STUB]`: function-by-function score forced to 0. target contains TODO/stub/placeholder markers in function bodies

### Critical Ports (Similarity < 0.60, Worst First)

These files need significant work:

- `state_machine` -> `statemachine.Parser [ZERO]` (0.00)
- `lib` -> `lalrpoputil.ParseError [STUB]` (0.00)

## Incorrect Ports (Missing Types)

These files are matched (often via `// port-lint`) but appear to be missing one or more type declarations
present in the Rust source file.

| Source | Target | Missing types | Examples |
|--------|--------|---------------|----------|
| `state_machine` | `statemachine.Parser [ZERO]` | 8/15 | `Location`, `Token`, `Error`, `Symbol`, `ParseError`, `TokenTriple`, `SymbolTriple`, `ErrorRecovery` |

## High Priority Missing Files

| Rank | Source file | Expected target | Deps | Functions | Classes/types | Symbols | Source path | Expected path |
|------|-------------|-----------------|------|-----------|---------------|---------|-------------|---------------|
| 1 | `lexer` | `Lexer` | 0 | 4 | 4 | 8 | `src/lexer.rs` | `Lexer.kt` |
| 2 | `build` | `Build` | 0 | 1 | 0 | 1 | `build.rs` | `Build.kt` |

## Documentation Gaps

There is missing documentation that is hurting overall scoring.

**Documentation coverage:** 116 / 402 lines (29%)

Documentation gaps (>20%), complete list:

- `state_machine` - 80% gap (214 → 43 lines)
- `lib` - 61% gap (188 → 73 lines)

