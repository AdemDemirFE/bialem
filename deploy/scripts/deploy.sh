#!/usr/bin/env bash
set -euo pipefail
# shellcheck source=common.sh
source "$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)/common.sh"

require_docker
assert_prod_env
assert_bialem_ports

echo "Deploying Bialem (project=${PROJECT_NAME}) from ${ROOT}"
compose up -d --build

echo "Waiting for health..."
"${SCRIPT_DIR}/health-check.sh"

echo
compose ps
echo
echo "Bialem is up."
echo "Frontend (host): http://127.0.0.1:${FRONTEND_PORT}"
echo "Backend  (host): http://127.0.0.1:${BACKEND_PORT}/management/health"
echo "Postgres: internal bialem-db:5432 (no host port)"
