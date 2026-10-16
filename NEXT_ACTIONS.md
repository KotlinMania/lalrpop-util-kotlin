# Immediate Actions - High-Value Files

Based on AST analysis, here are the concrete next steps.

## Summary

- **Files Present:** 3/3 (100.0%)
- **Function parity:** 23/23 matched (target 56) — 100.0%
- **Class/type parity:** 17/21 matched (target 38) — 81.0%
- **Combined symbol parity:** 40/44 matched (target 94) — 90.9%
- **Average inline-code cosine:** 0.16 (function body across 3 matched files)
- **Average documentation cosine:** 0.62 (doc text across 3 matched files)
- **Cheat-zeroed Files:** 0
- **Critical Issues:** 3 files with <0.60 function similarity
- **Needs Review:** 0 files with 0.60-0.84 function similarity
- **Excellent:** 0 files with >=0.85 function similarity

## Priority 1: Fix Incomplete High-Dependency Files

No incomplete high-dependency files detected.

## Priority 2: Port Missing High-Value Files

Critical missing files (>10 dependencies):

No missing high-value files detected.

## Detailed Work Items

Every matched file is listed below with function and type symbol parity.

### 1. state_machine

- **Target:** `statemachine.Parser`
- **Similarity:** 0.24
- **Dependents:** 0
- **Priority Score:** 42507.6
- **Functions:** 10/10 matched (target 27)
- **Missing functions:** _none_
- **Types:** 11/15 matched (target 23)
- **Missing types:** `Location`, `Token`, `Error`, `Symbol`

### 2. lib

- **Target:** `lalrpoputil.ParseError`
- **Similarity:** 0.22
- **Dependents:** 0
- **Priority Score:** 1107.8
- **Functions:** 9/9 matched (target 20)
- **Missing functions:** _none_
- **Types:** 2/2 matched (target 8)
- **Missing types:** _none_
- **Tests:** 1/1 matched

### 3. lexer

- **Target:** `lexer.Token`
- **Similarity:** 0.02
- **Dependents:** 0
- **Priority Score:** 809.8
- **Functions:** 4/4 matched (target 9)
- **Missing functions:** _none_
- **Types:** 4/4 matched (target 7)
- **Missing types:** _none_

## Success Criteria

For each file to be considered "complete":
- **Similarity ≥ 0.85** (Excellent threshold)
- All public APIs ported
- All tests ported
- Documentation ported
- port-lint header present

