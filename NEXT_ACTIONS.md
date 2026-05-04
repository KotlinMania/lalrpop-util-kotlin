# Immediate Actions - High-Value Files

Based on AST analysis, here are the concrete next steps.

## Summary

- **Files Present:** 2/4 (50.0%)
- **Function parity:** 16/24 matched (target 49) — 66.7%
- **Class/type parity:** 9/21 matched (target 27) — 42.9%
- **Combined symbol parity:** 25/45 matched (target 76) — 55.6%
- **Average inline-code cosine:** 0.00 (function body across 1 matched files)
- **Average documentation cosine:** 0.61 (doc text across 1 matched files)
- **Cheat-zeroed Files:** 2
- **Critical Issues:** 2 files with <0.60 function similarity

## Priority 1: Fix Incomplete High-Dependency Files

No incomplete high-dependency files detected.

## Priority 2: Port Missing High-Value Files

Critical missing files (>10 dependencies):

No missing high-value files detected.

## Detailed Work Items

Every matched file is listed below with function and type symbol parity.

### 1. state_machine

- **Target:** `statemachine.Parser [ZERO]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 82510.0
- **Functions:** 10/10 matched (target 27)
- **Missing functions:** _none_
- **Types:** 7/15 matched (target 19)
- **Missing types:** `Location`, `Token`, `Error`, `Symbol`, `ParseError`, `TokenTriple`, `SymbolTriple`, `ErrorRecovery`

### 2. lib

- **Target:** `lalrpoputil.ParseError [STUB]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 31110.0
- **Functions:** 6/9 matched (target 22)
- **Missing functions:** `fmt`, `description`, `test`
- **Types:** 2/2 matched (target 8)
- **Missing types:** _none_
- **Tests:** 0/1 matched

## Success Criteria

For each file to be considered "complete":
- **Similarity ≥ 0.85** (Excellent threshold)
- All public APIs ported
- All tests ported
- Documentation ported
- port-lint header present

## Next Commands

```bash
# Initialize task queue for systematic porting
cd tools/ast_distance
./ast_distance --init-tasks ../../tmp/lalrpop-util rust ../../src/commonMain/kotlin/io/github/kotlinmania/lalrpoputil kotlin tasks.json ../../AGENTS.md

# Get next high-priority task
./ast_distance --assign tasks.json <agent-id>
```
