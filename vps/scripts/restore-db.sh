#!/usr/bin/env bash
# Bialem VPS package - DB restore
# Usage: bash scripts/restore-db.sh backups/bialem-YYYYMMDD-HHMMSS.sql.gz
set -euo pipefail
cd "$(dirname "${BASH_SOURCE[0]}")/.."
backup="${1:-}"
[[ -f "${backup}" ]] || { echo "Kullanim: bash scripts/restore-db.sh backups/bialem-<tarih>.sql.gz"; exit 1; }

echo "DİKKAT: ${POSTGRES_DB:-bialem} veritabani yeniden yazilacak. 5 sn bekleniyor (iptal: Ctrl+C)..."
sleep 5

docker exec -i bialem-db psql -U "${POSTGRES_USER:-bialem}" -d "${POSTGRES_DB:-bialem}" -v ON_ERROR_STOP=1 \
  < <(gunzip -c "${backup}")

echo "Geri yukleme tamam. backend yeniden baslatiliyor..."
docker restart bialem-backend 2>/dev/null || true
echo "Tamam."