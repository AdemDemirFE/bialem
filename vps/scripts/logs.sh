#!/usr/bin/env bash
# Bialem VPS package - logs [service]
# Service: backend | frontend | admin | db  (default: all)
set -euo pipefail
cd "$(dirname "${BASH_SOURCE[0]}")/.."
service="${1:-}"
if [[ -n "${service}" ]]; then
  docker compose --env-file .env.prod -f "$(pwd)/docker-compose.yml" logs -f --tail=100 "${service}"
else
  docker compose --env-file .env.prod -f "$(pwd)/docker-compose.yml" logs -f --tail=100
fi