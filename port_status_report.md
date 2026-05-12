# Code Port - Progress Report

**Generated:** 2026-05-07
**Source:** tmp/lalrpop-util/src
**Target:** src/commonMain/kotlin/io/github/kotlinmania/lalrpoputil

## Executive Summary

| Metric | Count | Percentage |
|--------|-------|------------|
| Function parity | 21/23 matched (target 61) | 91.3% |
| Class/type parity | 12/21 matched (target 34) | 57.1% |
| Combined symbol parity | 33/44 matched (target 95) | 75.0% |
| Average function body similarity | 0.00 | inline-code cosine |
| Average documentation similarity | 0.55 | doc text cosine |
| Missing source functions | 0 | 0% parity until ported |
| Missing source classes/types | 0 | 0% parity until ported |
| Missing source symbol files | 0 | 0 symbols |
| Cheat/scoring failures | 3 | forced to 0% |
| Total source files | 3 | 100% |
| Target units (paired) | 18 | - |
| Target files (total) | 18 | - |
| Porting progress | 3 | 100.0% (matched) |
| Missing files | 0 | 0.0% |

## Port Quality Analysis

**Average Function Similarity:** 0.00

Similarity in this report is the required function-by-function body/parameter score. Class/type parity and symbol deficits are reported beside it; whole-file shape is diagnostic only.

**Work Distribution:**
- Critical (<0.60): 3 files (100.0% of matched)
- Needs review (0.60-0.84): 0 files (0.0% of matched)

## Worst Function Scores First

Every matched file is listed from lowest function body/parameter similarity upward. Missing symbol names are not capped.

| Rank | Source | Target | Function similarity | Functions | Missing functions | Types | Missing types | Tests | Symbol deficit | Priority |
|------|--------|--------|---------------------|-----------|-------------------|-------|---------------|-------|----------------|----------|
| 1 | `state_machine` | `statemachine.Parser [ZERO] [PROVENANCE-FALLBACK]` | 0.00 | 10/10 matched (target 27) | _none_ | 7/15 matched (target 19) | `Location`, `Token`, `Error`, `Symbol`, `ParseError`, `TokenTriple`, `SymbolTriple`, `ErrorRecovery` | - | 8 | 82510.0 |
| 2 | `lexer` | `lexer.Token [ZERO] [PROVENANCE-FALLBACK]` | 0.00 | 3/4 matched (target 15) | `fmt` | 3/4 matched (target 7) | `Item` | - | 2 | 20810.0 |
| 3 | `lib` | `lalrpoputil.ParseError [STUB] [PROVENANCE-FALLBACK]` | 0.00 | 8/9 matched (target 19) | `description` | 2/2 matched (target 8) | _none_ | 1/1 | 1 | 11110.0 |

## Cheat Detection / Scoring Failures

- `state_machine` -> `statemachine.Parser [ZERO] [PROVENANCE-FALLBACK]`: function-by-function score forced to 0. ParserAction.kt: snake_case identifier `integral_indices` in Kotlin comments; ParserAction.kt: Rust `pub` item in Kotlin comments
- `lexer` -> `lexer.Token [ZERO] [PROVENANCE-FALLBACK]`: function-by-function score forced to 0. MatcherBuilder.kt: Rust `match` expression in Kotlin code
- `lib` -> `lalrpoputil.ParseError [STUB] [PROVENANCE-FALLBACK]`: function-by-function score forced to 0. target contains TODO/stub/placeholder markers in function bodies

### Critical Ports (Similarity < 0.60, Worst First)

These files need significant work:

- `state_machine` -> `statemachine.Parser [ZERO] [PROVENANCE-FALLBACK]` (0.00)
- `lexer` -> `lexer.Token [ZERO] [PROVENANCE-FALLBACK]` (0.00)
- `lib` -> `lalrpoputil.ParseError [STUB] [PROVENANCE-FALLBACK]` (0.00)

## Incorrect Ports (Missing Types)

These files are matched (often via `// port-lint`) but appear to be missing one or more type declarations
present in the Rust source file.

| Source | Target | Missing types | Examples |
|--------|--------|---------------|----------|
| `state_machine` | `statemachine.Parser [ZERO] [PROVENANCE-FALLBACK]` | 8/15 | `Location`, `Token`, `Error`, `Symbol`, `ParseError`, `TokenTriple`, `SymbolTriple`, `ErrorRecovery` |
| `lexer` | `lexer.Token [ZERO] [PROVENANCE-FALLBACK]` | 1/4 | `Item` |

## High Priority Missing Files

No missing files detected.

## Documentation Gaps

There is missing documentation that is hurting overall scoring.

**Documentation coverage:** 117 / 416 lines (28%)

Documentation gaps (>20%), complete list:

- `state_machine` - 80% gap (214 → 43 lines)
- `lib` - 61% gap (188 → 73 lines)
- `lexer` - 93% gap (14 → 1 lines)

