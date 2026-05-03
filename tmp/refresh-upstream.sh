#!/usr/bin/env bash
# Refresh tmp/lalrpop-util/ from lalrpop/lalrpop HEAD.
# Records the upstream commit in tmp/UPSTREAM_COMMIT.txt.
#
# Usage:
#   ./refresh-upstream.sh           # pull current HEAD
#   UPSTREAM_REF=v0.23.1 ./refresh-upstream.sh
#                                   # pull a specific tag or commit

set -euo pipefail

cd "$(dirname "$0")"

UPSTREAM_REF="${UPSTREAM_REF:-HEAD}"
WORKDIR="lalrpop"

if [[ -d lalrpop-util ]]; then
    echo "Removing existing tmp/lalrpop-util/"
    rm -rf lalrpop-util
fi
if [[ -d "$WORKDIR" ]]; then
    echo "Removing leftover tmp/$WORKDIR/ from a prior fetch"
    rm -rf "$WORKDIR"
fi

echo "Cloning lalrpop/lalrpop (shallow, ref=$UPSTREAM_REF)..."
if [[ "$UPSTREAM_REF" == "HEAD" ]]; then
    git clone --depth=1 https://github.com/lalrpop/lalrpop.git "$WORKDIR"
else
    # For a specific tag/commit, fetch deep enough to hit it. lalrpop's tag
    # history is small; --depth=50 covers any 0.23.x patch.
    git clone --depth=50 https://github.com/lalrpop/lalrpop.git "$WORKDIR"
    git -C "$WORKDIR" checkout "$UPSTREAM_REF"
fi

echo "Extracting lalrpop-util/..."
mv "$WORKDIR/lalrpop-util" ./lalrpop-util

echo "Recording upstream commit metadata..."
git -C "$WORKDIR" rev-parse HEAD > UPSTREAM_COMMIT.txt
git -C "$WORKDIR" log -1 \
    --format="%H %ad %s%n%nCloned from: https://github.com/lalrpop/lalrpop%nSubdirectory: lalrpop-util/" \
    --date=iso >> UPSTREAM_COMMIT.txt

rm -rf "$WORKDIR"

echo "Done. Upstream snapshot at $(head -1 UPSTREAM_COMMIT.txt)."
