#!/usr/bin/env bash
# Bialem VPS package - restart [service]  (default: all)
set -euo pipefail
cd "$(dirname "${BASH_SOURCE[0]}")/.."
service="${1:-}"
if [[ -n "${service}" ]]; then
  docker compose --env-file .env.prod -f "$(pwd)/docker-compose.yml" restart "${service}"
else
  docker compose --env-file .env.prod -f "$(pwd)/docker-compose.yml" restart
fi