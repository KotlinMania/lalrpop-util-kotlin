# tmp/ — upstream Rust mirror

This directory holds the read-only upstream `lalrpop-util` source the
Kotlin port translates from. It is provisioned by `refresh-upstream.sh`,
not committed to git (see the project root `.gitignore`).

## Quick start

```bash
./refresh-upstream.sh                  # pull lalrpop-util at HEAD
UPSTREAM_REF=v0.23.1 ./refresh-upstream.sh   # pin to a release tag
```

After it runs:

- `tmp/lalrpop-util/` contains the upstream Rust source tree (5 files).
- `tmp/UPSTREAM_COMMIT.txt` records the exact commit the snapshot came
  from. Commit this file alongside any port-affecting change so future
  contributors can reproduce the oracle.

## Discipline

- **Never edit `tmp/lalrpop-util/`.** It is the translation oracle. If
  upstream looks wrong, the bug is in the port or in your understanding
  of Rust, not in `tmp/`.
- **Re-run `refresh-upstream.sh` only intentionally.** Bumping the
  upstream snapshot is a coordinated task — port-affected files need to
  be re-translated and `UPSTREAM_COMMIT.txt` updated in the same change.
- **`UPSTREAM_COMMIT.txt` is the only file in this directory that
  belongs in git.** Everything else is regenerated.
