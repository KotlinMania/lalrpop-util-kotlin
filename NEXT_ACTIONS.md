# Immediate Actions - High-Value Files

Based on AST analysis, here are the concrete next steps.

## Summary

- **Files Present:** 3/3 (100.0%)
- **Function parity:** 21/23 matched (target 61) — 91.3%
- **Class/type parity:** 12/21 matched (target 34) — 57.1%
- **Combined symbol parity:** 33/44 matched (target 95) — 75.0%
- **Average inline-code cosine:** 0.00 (function body across 2 matched files)
- **Average documentation cosine:** 0.55 (doc text across 2 matched files)
- **Cheat-zeroed Files:** 3
- **Critical Issues:** 3 files with <0.60 function similarity

## Priority 1: Fix Incomplete High-Dependency Files

No incomplete high-dependency files detected.

## Priority 2: Port Missing High-Value Files

Critical missing files (>10 dependencies):

No missing high-value files detected.

## Detailed Work Items

Every matched file is listed below with function and type symbol parity.

### 1. state_machine

- **Target:** `statemachine.Parser [ZERO] [PROVENANCE-FALLBACK]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 82510.0
- **Functions:** 10/10 matched (target 27)
- **Missing functions:** _none_
- **Types:** 7/15 matched (target 19)
- **Missing types:** `Location`, `Token`, `Error`, `Symbol`, `ParseError`, `TokenTriple`, `SymbolTriple`, `ErrorRecovery`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `src/state_machine.rs` vs expected `state_machine.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `src/state_machine.rs` vs expected `state_machine.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `src/state_machine.rs` vs expected `state_machine.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `src/state_machine.rs` vs expected `state_machine.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `src/state_machine.rs` vs expected `state_machine.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `src/state_machine.rs` vs expected `state_machine.rs`
- **Proposed provenance header:** `// port-lint: source state_machine.rs` (current: `// port-lint: source src/state_machine.rs`)
- **Proposed provenance header:** `// port-lint: source state_machine.rs` (current: `// port-lint: source src/state_machine.rs`)
- **Proposed provenance header:** `// port-lint: source state_machine.rs` (current: `// port-lint: source src/state_machine.rs`)
- **Proposed provenance header:** `// port-lint: source state_machine.rs` (current: `// port-lint: source src/state_machine.rs`)
- **Proposed provenance header:** `// port-lint: source state_machine.rs` (current: `// port-lint: source src/state_machine.rs`)
- **Proposed provenance header:** `// port-lint: source state_machine.rs` (current: `// port-lint: source src/state_machine.rs`)
- **Lint issues:** 6

### 2. lexer

- **Target:** `lexer.Token [ZERO] [PROVENANCE-FALLBACK]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 20810.0
- **Functions:** 3/4 matched (target 15)
- **Missing functions:** `fmt`
- **Types:** 3/4 matched (target 7)
- **Missing types:** `Item`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `src/lexer.rs` vs expected `lexer.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `src/lexer.rs` vs expected `lexer.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `src/lexer.rs` vs expected `lexer.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `src/lexer.rs` vs expected `lexer.rs`
- **Proposed provenance header:** `// port-lint: source lexer.rs` (current: `// port-lint: source src/lexer.rs`)
- **Proposed provenance header:** `// port-lint: source lexer.rs` (current: `// port-lint: source src/lexer.rs`)
- **Proposed provenance header:** `// port-lint: source lexer.rs` (current: `// port-lint: source src/lexer.rs`)
- **Proposed provenance header:** `// port-lint: source lexer.rs` (current: `// port-lint: source src/lexer.rs`)
- **Lint issues:** 4

### 3. lib

- **Target:** `lalrpoputil.ParseError [STUB] [PROVENANCE-FALLBACK]`
- **Similarity:** 0.00
- **Dependents:** 0
- **Priority Score:** 11110.0
- **Functions:** 8/9 matched (target 19)
- **Missing functions:** `description`
- **Types:** 2/2 matched (target 8)
- **Missing types:** _none_
- **Tests:** 1/1 matched
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `src/lib.rs` vs expected `lib.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `src/lib.rs` vs expected `lib.rs`
- **Provenance warning:** port-lint provenance header matched only after fallback normalization: `src/lib.rs` vs expected `lib.rs`
- **Proposed provenance header:** `// port-lint: source lib.rs` (current: `// port-lint: source src/lib.rs`)
- **Proposed provenance header:** `// port-lint: source lib.rs` (current: `// port-lint: source src/lib.rs`)
- **Proposed provenance header:** `// port-lint: source lib.rs` (current: `// port-lint: source src/lib.rs`)
- **Lint issues:** 3

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
./ast_distance --init-tasks ../../tmp/lalrpop-util/src rust ../../src/commonMain/kotlin/io/github/kotlinmania/lalrpoputil kotlin tasks.json ../../AGENTS.md

# Get next high-priority task
./ast_distance --assign tasks.json <agent-id>
```
