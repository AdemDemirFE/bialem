#!/usr/bin/env bash
# Bialem VPS package - health check
set -euo pipefail
cd "$(dirname "${BASH_SOURCE[0]}")/.."

check() {
  local url="$1"; local name="$2"
  local code
  code=$(curl -s -o /dev/null -w "%{http_code}" --max-time 8 "${url}" 2>/dev/null || echo "000")
  if [[ "${code}" == "200" ]]; then
    echo "[OK]   ${name} -> HTTP ${code} (${url})"
  else
    echo "[HATA] ${name} -> HTTP ${code} (${url})"
  fi
}

check "http://127.0.0.1:${FRONTEND_PORT:-4174}"                        "Frontend"
check "http://127.0.0.1:${BACKEND_PORT:-8080}/management/health"       "Backend health"
check "http://127.0.0.1:${ADMIN_PORT:-3000}"                            "Admin"