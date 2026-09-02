#!/usr/bin/env bash
# Bialem VPS package - status
set -euo pipefail
cd "$(dirname "${BASH_SOURCE[0]}")/.."
docker compose --env-file .env.prod -f "$(pwd)/docker-compose.yml" ps
echo
echo "Duracak diski gormek icin: df -h /"
echo "Log icin: bash scripts/logs.sh [backend|frontend|admin|db]"