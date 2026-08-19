#!/usr/bin/env bash
set -euo pipefail
# shellcheck source=common.sh
source "$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)/common.sh"

require_docker
assert_prod_env

target="${1:-}"
case "${target}" in
  "")
    compose logs -f --tail=200
    ;;
  backend)
    compose logs -f --tail=200 backend
    ;;
  frontend)
    compose logs -f --tail=200 frontend
    ;;
  db|postgres|postgresql)
    compose logs -f --tail=200 db
    ;;
  *)
    echo "Usage: $0 [backend|frontend|db]" >&2
    exit 1
    ;;
esac
