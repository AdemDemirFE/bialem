#!/usr/bin/env bash
set -euo pipefail
# shellcheck source=common.sh
source "$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)/common.sh"

require_docker

jar="${ROOT}/backend/target/bialem-0.0.1-SNAPSHOT.jar"
m2_volume="bialem-m2-cache"

echo "Building backend JAR with temporary Maven container (final image is JRE-only)..."

docker run --rm \
  -v "${ROOT}/backend:/src" \
  -v "${m2_volume}:/root/.m2" \
  -w /src \
  maven:3.9-eclipse-temurin-17-alpine \
  mvn -ntp -Pprod -DskipTests package

[[ -f "${jar}" ]] || die "JAR not found: ${jar}"
echo "JAR ready: ${jar}"
