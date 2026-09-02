#!/usr/bin/env bash
# Bialem VPS package - DB backup
# Output: backups/bialem-YYYYMMDD-HHMMSS.sql.gz
set -euo pipefail
cd "$(dirname "${BASH_SOURCE[0]}")/.."
mkdir -p backups
stamp=$(date +%Y%m%d-%H%M%S)
out="backups/bialem-${stamp}.sql.gz"
docker exec bialem-db pg_dump -U "${POSTGRES_USER:-bialem}" -d "${POSTGRES_DB:-bialem}" | gzip > "${out}"
echo "Yedek: ${out}"
echo "Geri yukleme: bash scripts/restore-db.sh ${out}"