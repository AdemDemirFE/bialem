#!/usr/bin/env bash
set -euo pipefail
# shellcheck source=common.sh
source "$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)/common.sh"

require_docker
assert_prod_env

frontend_ok=0
backend_ok=0
db_ok=0

if curl -fsS -o /dev/null --max-time 10 "http://127.0.0.1:${FRONTEND_PORT}/"; then
  frontend_ok=1
fi

if curl -fsS -o /dev/null --max-time 10 "http://127.0.0.1:${BACKEND_PORT}/management/health"; then
  backend_ok=1
fi

if docker exec bialem-db pg_isready -U "${POSTGRES_USER}" -d "${POSTGRES_DB}" >/dev/null 2>&1; then
  db_ok=1
fi

echo "Frontend: $([[ ${frontend_ok} -eq 1 ]] && echo OK || echo FAIL)"
echo "Backend: $([[ ${backend_ok} -eq 1 ]] && echo OK || echo FAIL)"
echo "Database: $([[ ${db_ok} -eq 1 ]] && echo OK || echo FAIL)"

[[ ${frontend_ok} -eq 1 && ${backend_ok} -eq 1 && ${db_ok} -eq 1 ]]
