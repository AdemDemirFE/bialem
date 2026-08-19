#!/usr/bin/env bash
set -euo pipefail
# shellcheck source=common.sh
source "$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)/common.sh"

require_docker
assert_prod_env

backup_dir="${ROOT}/backups"
mkdir -p "${backup_dir}"
stamp="$(date +%Y%m%d-%H%M%S)"
out="${backup_dir}/bialem-${stamp}.sql.gz"

docker exec bialem-db pg_dump -U "${POSTGRES_USER}" -d "${POSTGRES_DB}" --no-password | gzip > "${out}"
echo "Backup written: ${out}"
