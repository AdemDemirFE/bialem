#!/usr/bin/env bash
# Bialem VPS package - deploy (self-contained, no git/repo needed)
# Usage: bash scripts/deploy.sh
set -euo pipefail

cd "$(dirname "${BASH_SOURCE[0]}")/.."
ROOT="$(pwd)"
ENV_FILE="${ROOT}/.env.prod"
COMPOSE=(docker compose --env-file "${ENV_FILE}" -f "${ROOT}/docker-compose.yml")

command -v docker >/dev/null 2>&1 || { echo "HATA: docker kurulu degil"; exit 1; }
[[ -f "${ENV_FILE}" ]] || { echo "HATA: ${ENV_FILE} yok. once: cp .env.prod.example .env.prod && nano .env.prod"; exit 1; }
[[ -f "${ROOT}/backend/bialem-backend.jar" ]] || { echo "HATA: backend/bialem-backend.jar yok"; exit 1; }
[[ -d "${ROOT}/frontend/dist" ]] || { echo "HATA: frontend/dist yok"; exit 1; }
[[ -f "${ROOT}/admin/admin/server.js" ]] || { echo "HATA: admin standalone (admin/server.js) yok"; exit 1; }

grep -q "CHANGE_ME" "${ENV_FILE}" && { echo "HATA: .env.prod icinde CHANGE_ME kaldi."; exit 1; }

echo "Bialem VPS paketi kuruluyor (${ROOT})"
"${COMPOSE[@]}" up -d

echo "Saglik bekleniyor..."
FAILED=0
for i in $(seq 1 30); do
  code=$(curl -s -o /dev/null -w "%{http_code}" --max-time 5 "http://127.0.0.1:8080/management/health" 2>/dev/null || echo "000")
  if [[ "${code}" == "200" ]]; then
    echo "Backend saglikli (HTTP 200) - ${i} denemede"
    FAILED=0
    break
  fi
  FAILED=$i
  sleep 5
done

if [[ "${FAILED}" != "0" ]]; then
  echo "UYARI: backend /management/health 200 olmadi. Servis yeni kondu; loglari inceleyin:"
  "${COMPOSE[@]}" logs --tail=50 backend
fi

echo
"${COMPOSE[@]}" ps
echo
echo "Frontend : http://127.0.0.1:${FRONTEND_PORT:-4174}"
echo "Backend  : http://127.0.0.1:${BACKEND_PORT:-8080}/management/health"
echo "Admin    : http://127.0.0.1:${ADMIN_PORT:-3000}"
echo "Postgres : bialem-db:5432 (host portu yok)"