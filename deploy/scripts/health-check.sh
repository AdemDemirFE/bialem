#!/usr/bin/env bash
set -euo pipefail
# shellcheck source=common.sh
source "$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)/common.sh"

require_docker
assert_prod_env

attempts="${HEALTH_ATTEMPTS:-40}"
sleep_s="${HEALTH_SLEEP:-5}"

echo "Checking http://127.0.0.1:${FRONTEND_PORT}/"
echo "Checking http://127.0.0.1:${BACKEND_PORT}/management/health"

ok=0
for i in $(seq 1 "${attempts}"); do
  fe=0
  be=0
  db=0
  curl -fsS -o /dev/null --max-time 5 "http://127.0.0.1:${FRONTEND_PORT}/" && fe=1 || true
  curl -fsS -o /dev/null --max-time 5 "http://127.0.0.1:${BACKEND_PORT}/management/health" && be=1 || true
  docker exec bialem-db pg_isready -U "${POSTGRES_USER}" -d "${POSTGRES_DB}" >/dev/null 2>&1 && db=1 || true
  if [[ ${fe} -eq 1 && ${be} -eq 1 && ${db} -eq 1 ]]; then
    ok=1
    break
  fi
  echo "Attempt ${i}/${attempts}: frontend=$([[ ${fe} -eq 1 ]] && echo OK || echo wait) backend=$([[ ${be} -eq 1 ]] && echo OK || echo wait) db=$([[ ${db} -eq 1 ]] && echo OK || echo wait)"
  sleep "${sleep_s}"
done

if [[ ${ok} -ne 1 ]]; then
  echo "Health check failed." >&2
  compose ps || true
  exit 1
fi

echo "Frontend: OK"
echo "Backend: OK"
echo "Database: OK"
