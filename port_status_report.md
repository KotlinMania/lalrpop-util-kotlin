# Code Port - Progress Report

**Generated:** 2026-10-16
**Source:** tmp/lalrpop-util/src
**Target:** src/commonMain/kotlin/io/github/kotlinmania/lalrpoputil

## Executive Summary

| Metric | Count | Percentage |
|--------|-------|------------|
| Function parity | 23/23 matched (target 56) | 100.0% |
| Class/type parity | 17/21 matched (target 38) | 81.0% |
| Combined symbol parity | 40/44 matched (target 94) | 90.9% |
| Average function body similarity | 0.16 | inline-code cosine |
| Average documentation similarity | 0.62 | doc text cosine |
| Missing source functions | 0 | 0% parity until ported |
| Missing source classes/types | 0 | 0% parity until ported |
| Missing source symbol files | 0 | 0 symbols |
| Cheat/scoring failures | 0 | forced to 0% |
| Total source files | 3 | 100% |
| Target units (paired) | 18 | - |
| Target files (total) | 18 | - |
| Porting progress | 3 | 100.0% (matched) |
| Missing files | 0 | 0.0% |

## Port Quality Analysis

**Average Function Similarity:** 0.16

Similarity in this report is the required function-by-function body/parameter score. Class/type parity and symbol deficits are reported beside it; whole-file shape is diagnostic only.

**Work Distribution:**
- Critical (<0.60): 3 files (100.0% of matched)
- Needs review (0.60-0.84): 0 files (0.0% of matched)
- Excellent (>=0.85): 0 files (0.0% of matched)

## Worst Function Scores First

Every matched file is listed from lowest function body/parameter similarity upward. Missing symbol names are not capped.

| Rank | Source | Target | Function similarity | Functions | Missing functions | Types | Missing types | Tests | Symbol deficit | Priority |
|------|--------|--------|---------------------|-----------|-------------------|-------|---------------|-------|----------------|----------|
| 1 | `lexer` | `lexer.Token` | 0.02 | 4/4 matched (target 9) | _none_ | 4/4 matched (target 7) | _none_ | - | 0 | 809.8 |
| 2 | `lib` | `lalrpoputil.ParseError` | 0.22 | 9/9 matched (target 20) | _none_ | 2/2 matched (target 8) | _none_ | 1/1 | 0 | 1107.8 |
| 3 | `state_machine` | `statemachine.Parser` | 0.24 | 10/10 matched (target 27) | _none_ | 11/15 matched (target 23) | `Location`, `Token`, `Error`, `Symbol` | - | 4 | 42507.6 |

## Cheat Detection / Scoring Failures

_None detected._

### Critical Ports (Similarity < 0.60, Worst First)

These files need significant work:

- `lexer` -> `lexer.Token` (0.02)
- `lib` -> `lalrpoputil.ParseError` (0.22)
- `state_machine` -> `statemachine.Parser` (0.24)

## Incorrect Ports (Missing Types)

These files are matched (often via `// port-lint`) but appear to be missing one or more type declarations
present in the Rust source file.

| Source | Target | Missing types | Examples |
|--------|--------|---------------|----------|
| `state_machine` | `statemachine.Parser` | 4/15 | `Location`, `Token`, `Error`, `Symbol` |

## High Priority Missing Files

No missing files detected.

## Documentation Gaps

Documentation correspondence is incomplete; it is reported separately from implementation scoring.

**Documentation line amount:** 117 / 416 lines (28%)

Documentation gaps (>20%), complete list:

- `state_machine` - 80% gap (214 → 43 lines)
- `lib` - 61% gap (188 → 73 lines)
- `lexer` - 93% gap (14 → 1 lines)

