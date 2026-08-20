#!/usr/bin/env bash
set -euo pipefail
# shellcheck source=common.sh
source "$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)/common.sh"

require_docker

echo "=== Disk usage ==="
df -h / /var/lib/docker 2>/dev/null || df -h /
echo
docker system df 2>/dev/null || true
echo

echo "Removing dangling Docker images (safe, not tied to running containers)..."
docker image prune -f

echo
echo "Removing unused bialem-* images (not used by a running container)..."
while read -r id tag; do
  [[ -n "${id}" ]] || continue
  if docker ps -a --filter "ancestor=${id}" --format '{{.ID}}' | grep -q .; then
    echo "  keep (in use): ${tag}"
  else
    echo "  remove: ${tag}"
    docker rmi "${id}" 2>/dev/null || true
  fi
done < <(docker images --format '{{.ID}} {{.Repository}}:{{.Tag}}' | grep '^[^ ]* bialem-' || true)

echo
echo "Removing BuildKit cache older than 48h (frees space from failed builds)..."
docker builder prune -f --filter 'until=48h' 2>/dev/null || docker builder prune -f

echo
echo "=== After cleanup ==="
df -h / /var/lib/docker 2>/dev/null || df -h /
docker system df 2>/dev/null || true
echo
echo "Done. Other projects' containers/volumes were not stopped or deleted."
echo "DB volume bialem-postgres-data was not touched."
