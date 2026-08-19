#!/usr/bin/env bash
set -euo pipefail
# shellcheck source=common.sh
source "$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)/common.sh"

require_docker
assert_prod_env

file="${1:-}"
[[ -n "${file}" ]] || die "Usage: $0 backup.sql.gz"
[[ -f "${file}" ]] || die "Backup file not found: ${file}"

echo "This will REPLACE the production database in container bialem-db."
echo "File: ${file}"
printf "Type RESTORE to continue: "
read -r confirm
[[ "${confirm}" == "RESTORE" ]] || die "Cancelled."

if [[ "${file}" == *.gz ]]; then
  gzip -dc "${file}" | docker exec -i bialem-db psql -U "${POSTGRES_USER}" -d "${POSTGRES_DB}" --no-password
else
  docker exec -i bialem-db psql -U "${POSTGRES_USER}" -d "${POSTGRES_DB}" --no-password < "${file}"
fi

echo "Restore finished."
