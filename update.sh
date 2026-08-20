#!/usr/bin/env bash
# Run: bash update.sh
set -euo pipefail
exec "$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)/deploy/scripts/update.sh" "$@"
