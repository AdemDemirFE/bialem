#!/usr/bin/env bash
# Run: bash deploy.sh
set -euo pipefail
exec "$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)/deploy/scripts/deploy.sh" "$@"
