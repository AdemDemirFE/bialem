#!/usr/bin/env bash
set -euo pipefail
cat <<'EOF'
Bialem schema is applied by Liquibase when bialem-backend starts (prod context).

Do NOT import supabase/migrations into this PostgreSQL database.
Those files belong to the retired Supabase stack and would conflict
with JHipster Liquibase changelogs.

There is no automatic data import on each deploy.
If you need a one-off copy from another environment, use:

  ./deploy/scripts/backup-db.sh
  ./deploy/scripts/restore-db.sh path/to/backup.sql.gz
EOF
