#!/usr/bin/env bash
# Bialem VPS package - update (yeni paket klasore kopyalandiktan sonra)
# Yeni backend/bialem-backend.jar, frontend/dist veya admin/ dosyalarini alir,
# DB volume'u dokunulmadan her seyi yeniden uretir.
set -euo pipefail
cd "$(dirname "${BASH_SOURCE[0]}")/.."
ROOT="$(pwd)"
ENV_FILE="${ROOT}/.env.prod"
COMPOSE=(docker compose --env-file "${ENV_FILE}" -f "${ROOT}/docker-compose.yml")

[[ -f "${ENV_FILE}" ]] || { echo "HATA: .env.prod yok"; exit 1; }
[[ -f "${ROOT}/backend/bialem-backend.jar" ]] || { echo "HATA: backend jar ilk kez geliyorsa once: bash scripts/deploy.sh"; exit 1; }

echo "Yeni paket yayinlaniyor (DB volume korunur)..."
"${COMPOSE[@]}" up -d --force-recreate --no-deps backend frontend admin
echo "Saglik kontrolu..."
sleep 20
code=$(curl -s -o /dev/null -w "%{http_code}" --max-time 5 "http://127.0.0.1:8080/management/health" 2>/dev/null || echo "000")
echo "Backend health: HTTP ${code}"
if [[ "${code}" != "200" ]]; then
  echo "Backend 200 degil. Loglar:"
  docker compose --env-file "${ENV_FILE}" -f "${ROOT}/docker-compose.yml" logs --tail=60 backend
  exit 1
fi
echo "Guncelleme tamam."