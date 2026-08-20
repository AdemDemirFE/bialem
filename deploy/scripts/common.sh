#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT="$(cd "${SCRIPT_DIR}/../.." && pwd)"
COMPOSE_FILE="${ROOT}/deploy/docker-compose.prod.yml"
ENV_FILE="${ROOT}/.env.prod"
PROJECT_NAME="bialem"
FRONTEND_PORT="4174"
BACKEND_PORT="8184"

compose() {
  docker compose -p "${PROJECT_NAME}" --env-file "${ENV_FILE}" -f "${COMPOSE_FILE}" "$@"
}

die() {
  echo "ERROR: $*" >&2
  exit 1
}

require_cmd() {
  command -v "$1" >/dev/null 2>&1 || die "'$1' is not installed"
}

load_prod_env() {
  [[ -f "${ENV_FILE}" ]] || die ".env.prod not found. Copy .env.prod.example to .env.prod and fill secrets."
  set -a
  # shellcheck disable=SC1090
  source "${ENV_FILE}"
  set +a
}

assert_prod_env() {
  load_prod_env
  [[ "${BIALEM_ENV:-}" == "prod" ]] || die "BIALEM_ENV must be prod in .env.prod (do not use .env.dev)."
  [[ "${SPRING_PROFILES_ACTIVE:-}" == "prod" ]] || die "SPRING_PROFILES_ACTIVE must be prod."

  if grep -E '15432|localhost:8080|localhost:5173' "${ENV_FILE}" >/dev/null 2>&1; then
    die ".env.prod contains local/dev hosts or port 15432. Production DB is bialem-db:5432 only."
  fi

  if grep -Ei 'SUPABASE|EXPO_PUBLIC_SUPABASE|EXPO_PUBLIC_' "${ENV_FILE}" >/dev/null 2>&1; then
    die ".env.prod must not contain Expo or Supabase variables. Production is Spring Boot + JWT + PostgreSQL."
  fi

  case "${SPRING_DATASOURCE_URL:-}" in
    *bialem-db:5432*) ;;
    *) die "SPRING_DATASOURCE_URL must use jdbc:postgresql://bialem-db:5432/..." ;;
  esac

  [[ "${POSTGRES_PASSWORD:-}" != "CHANGE_ME" && -n "${POSTGRES_PASSWORD:-}" ]] || die "Set POSTGRES_PASSWORD in .env.prod"
  [[ "${JWT_SECRET:-}" != "CHANGE_ME" && -n "${JWT_SECRET:-}" ]] || die "Set JWT_SECRET in .env.prod (openssl rand -base64 64)"
  [[ "${SPRING_DATASOURCE_PASSWORD:-}" != "CHANGE_ME" && -n "${SPRING_DATASOURCE_PASSWORD:-}" ]] || die "Set SPRING_DATASOURCE_PASSWORD in .env.prod"
}

require_docker() {
  require_cmd docker
  require_cmd curl
  docker info >/dev/null 2>&1 || die "Docker daemon is not running"
  docker compose version >/dev/null 2>&1 || die "Docker Compose plugin is missing"
}

port_owner_container() {
  local port="$1"
  docker ps --format '{{.Names}} {{.Ports}}' | awk -v p=":${port}->" '
    $0 ~ p { print $1; exit }
  '
}

assert_port_free_or_bialem() {
  local port="$1"
  local expected="$2"
  local owner
  owner="$(port_owner_container "${port}" || true)"
  if [[ -n "${owner}" ]]; then
    [[ "${owner}" == "${expected}" ]] || die "Port ${port} is used by container '${owner}', expected '${expected}'"
    return 0
  fi
  if command -v ss >/dev/null 2>&1; then
    if ss -ltn | awk '{print $4}' | grep -E "[:.]${port}$" >/dev/null 2>&1; then
      die "Port ${port} is already in use by a host process (not a Bialem container)"
    fi
  fi
}

assert_bialem_ports() {
  assert_port_free_or_bialem "${FRONTEND_PORT}" "bialem-frontend"
  assert_port_free_or_bialem "${BACKEND_PORT}" "bialem-backend"
}

warn_disk_space() {
  local avail_kb
  avail_kb="$(df -Pk / | awk 'NR==2 {print $4}')"
  if [[ "${avail_kb}" -lt 2097152 ]]; then
    echo "WARNING: less than 2GB free on /. Run: bash deploy/scripts/free-disk.sh" >&2
  fi
}
