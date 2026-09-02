#!/usr/bin/env bash
# Bialem VPS package - stop (volumes korunur)
set -euo pipefail
cd "$(dirname "${BASH_SOURCE[0]}")/.."
docker compose --env-file .env.prod -f "$(pwd)/docker-compose.yml" stop