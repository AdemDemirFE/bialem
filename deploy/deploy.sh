#!/usr/bin/env bash
# ============================================================================
# BIALEM BACKEND — DEPLOYMENT SCRIPT
# ============================================================================
# Usage:
#   ./deploy.sh build          # Build Docker image
#   ./deploy.sh up             # Start all services
#   ./deploy.sh down           # Stop all services
#   ./deploy.sh restart        # Restart all services
#   ./deploy.sh status         # Check service status
#   ./deploy.sh logs           # Tail logs
#   ./deploy.sh migrate        # Run Liquibase migrations only
#   ./deploy.sh shell          # Shell into backend container
#   ./deploy.sh db-shell       # Shell into PostgreSQL
#   ./deploy.sh clean          # Remove containers and volumes
#   ./deploy.sh jar            # Build JAR only (no Docker)
# ============================================================================

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
BACKEND_DIR="$PROJECT_ROOT/backend"
DEPLOY_DIR="$SCRIPT_DIR"
CONFIG_ENV="$DEPLOY_DIR/config.env"
COMPOSE_FILE="$DEPLOY_DIR/docker-compose.prod.yml"
IMAGE_NAME="bialem"
IMAGE_TAG="${APP_VERSION:-latest}"

# ─── Colors ─────────────────────────────────────────────────────────────
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

info()  { echo -e "${BLUE}ℹ ${NC}$*"; }
ok()    { echo -e "${GREEN}✓ ${NC}$*"; }
warn()  { echo -e "${YELLOW}⚠ ${NC}$*"; }
err()   { echo -e "${RED}✗ ${NC}$*" >&2; }

# ─── Load config ────────────────────────────────────────────────────────
load_config() {
  if [[ -f "$CONFIG_ENV" ]]; then
    # shellcheck disable=SC1090
    set -a
    source "$CONFIG_ENV"
    set +a
    info "Loaded config from $CONFIG_ENV"
  else
    warn "No config.env found at $CONFIG_ENV"
    warn "Copy config.env.example to config.env and fill in your values"
  fi
}

# ─── Build JAR ─────────────────────────────────────────────────────────
build_jar() {
  info "Building JAR..."
  cd "$BACKEND_DIR"
  ./mvnw -Pprod package -DskipTests -q
  ok "JAR built: $(ls -la target/*.jar | awk '{print $9}')"
}

# ─── Build Docker image ────────────────────────────────────────────────
build_image() {
  info "Building Docker image: ${IMAGE_NAME}:${IMAGE_TAG}"
  build_jar
  cd "$BACKEND_DIR"
  docker build \
    --tag "${IMAGE_NAME}:${IMAGE_TAG}" \
    --tag "${IMAGE_NAME}:latest" \
    --build-arg JAR_FILE=target/bialem-0.0.1-SNAPSHOT.jar \
    .
  ok "Docker image built: ${IMAGE_NAME}:${IMAGE_TAG}"
  docker images "${IMAGE_NAME}" | head -5
}

# ─── Start services ────────────────────────────────────────────────────
up() {
  load_config
  info "Starting services..."
  docker compose -f "$COMPOSE_FILE" --env-file "$CONFIG_ENV" up -d
  ok "Services started"
  status
}

# ─── Stop services ─────────────────────────────────────────────────────
down() {
  load_config
  info "Stopping services..."
  docker compose -f "$COMPOSE_FILE" --env-file "$CONFIG_ENV" down
  ok "Services stopped"
}

# ─── Restart ───────────────────────────────────────────────────────────
restart() {
  load_config
  info "Restarting services..."
  docker compose -f "$COMPOSE_FILE" --env-file "$CONFIG_ENV" restart
  ok "Services restarted"
}

# ─── Status ────────────────────────────────────────────────────────────
status() {
  load_config
  info "Service status:"
  docker compose -f "$COMPOSE_FILE" --env-file "$CONFIG_ENV" ps
  echo ""
  info "Health check:"
  local port="${APP_PORT:-8080}"
  if curl -sf "http://127.0.0.1:${port}/management/health" >/dev/null 2>&1; then
    ok "Backend is healthy on port ${port}"
  else
    warn "Backend not responding on port ${port} (may still be starting)"
  fi
}

# ─── Logs ──────────────────────────────────────────────────────────────
logs() {
  load_config
  docker compose -f "$COMPOSE_FILE" --env-file "$CONFIG_ENV" logs -f --tail=100
}

# ─── Run migrations only ──────────────────────────────────────────────
migrate() {
  load_config
  info "Running Liquibase migrations..."
  docker compose -f "$COMPOSE_FILE" --env-file "$CONFIG_ENV" exec app \
    java -jar /app/app.jar --spring.main.web-application-type=none \
    --spring.profiles.active="${SPRING_PROFILES_ACTIVE:-prod}" \
    --spring.main.allow-bean-definition-overriding=true
  ok "Migrations complete"
}

# ─── Shell into backend ───────────────────────────────────────────────
shell() {
  load_config
  docker compose -f "$COMPOSE_FILE" --env-file "$CONFIG_ENV" exec app sh
}

# ─── Shell into PostgreSQL ─────────────────────────────────────────────
db_shell() {
  load_config
  docker compose -f "$COMPOSE_FILE" --env-file "$CONFIG_ENV" exec postgresql \
    psql -U "${POSTGRES_USER:-bialem}" -d "${POSTGRES_DB:-bialem}"
}

# ─── Clean up ──────────────────────────────────────────────────────────
clean() {
  load_config
  warn "This will remove containers, networks, and volumes!"
  read -rp "Are you sure? (y/N) " confirm
  if [[ "$confirm" =~ ^[Yy]$ ]]; then
    docker compose -f "$COMPOSE_FILE" --env-file "$CONFIG_ENV" down -v --remove-orphans
    ok "Cleaned up"
  else
    info "Cancelled"
  fi
}

# ─── Build JAR only ───────────────────────────────────────────────────
jar() {
  build_jar
}

# ─── Main ──────────────────────────────────────────────────────────────
main() {
  case "${1:-help}" in
    build)    build_image ;;
    up)       up ;;
    down)     down ;;
    restart)  restart ;;
    status)   status ;;
    logs)     logs ;;
    migrate)  migrate ;;
    shell)    shell ;;
    db-shell) db_shell ;;
    clean)    clean ;;
    jar)      jar ;;
    help|*)
      echo "Bialem Backend Deployment"
      echo ""
      echo "Usage: $0 <command>"
      echo ""
      echo "Commands:"
      echo "  build      Build Docker image (includes JAR)"
      echo "  jar        Build JAR only (no Docker)"
      echo "  up         Start all services (detached)"
      echo "  down       Stop all services"
      echo "  restart    Restart all services"
      echo "  status     Show service status and health"
      echo "  logs       Tail service logs"
      echo "  migrate    Run Liquibase migrations"
      echo "  shell      Shell into backend container"
      echo "  db-shell   Shell into PostgreSQL"
      echo "  clean      Remove containers, networks, volumes"
      echo "  help       Show this help"
      echo ""
      echo "Configuration:"
      echo "  1. cp deploy/config.env.example deploy/config.env"
      echo "  2. Edit deploy/config.env with your values"
      echo "  3. ./deploy/deploy.sh build"
      echo "  4. ./deploy/deploy.sh up"
      ;;
  esac
}

main "$@"
