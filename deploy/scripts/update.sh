#!/usr/bin/env bash
set -euo pipefail
# shellcheck source=common.sh
source "$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)/common.sh"

require_docker
assert_prod_env
assert_bialem_ports

cd "${ROOT}"
if [[ -d .git ]]; then
  if [[ -n "$(git status --porcelain)" ]]; then
    echo "Local changes detected. Deployment stopped." >&2
    echo "Changed files:" >&2
    git status --porcelain >&2
    echo >&2
    echo "Inspect:  git diff" >&2
    echo "If changes are only from chmod on VPS, reset all tracked files:" >&2
    echo "  git checkout -- ." >&2
    echo "  git pull --ff-only && ./update.sh" >&2
    echo "(.env.prod is gitignored and will not be deleted.)" >&2
    exit 1
  fi
  echo "Branch: $(git rev-parse --abbrev-ref HEAD)"
  git pull --ff-only
else
  echo "Not a git checkout; skipping git pull."
fi

echo "Building and recreating Bialem services (volumes kept)..."
compose up -d --build --remove-orphans

echo "Waiting for health..."
"${SCRIPT_DIR}/health-check.sh"

echo
compose ps
echo "Update complete. PostgreSQL volume bialem-postgres-data was not removed."
