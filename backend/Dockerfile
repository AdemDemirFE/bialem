# ============================================================================
# BIALEM BACKEND — PRODUCTION DOCKERFILE
# ============================================================================
# JAR is built externally (deploy.sh or CI). Docker only packages the runtime.
# Final image: ~180MB Alpine JRE
#
# Build:  cd backend && docker build -t bialem:latest .
# Run:    docker run -p 8080:8080 --env-file config.env bialem:latest
# ============================================================================

FROM eclipse-temurin:17-jre-alpine

# Security: non-root user
RUN addgroup -S bialem -g 1001 && \
    adduser -S -u 1001 -G bialem -h /app -s /sbin/nologin bialem

WORKDIR /app

# Install curl for healthcheck
RUN apk add --no-cache curl

# Copy pre-built JAR (built by deploy.sh or CI pipeline)
COPY target/bialem-0.0.1-SNAPSHOT.jar /app/app.jar

# Fix ownership
RUN chown bialem:bialem /app/app.jar

USER bialem

EXPOSE 8080

# Default environment (overridden by docker-compose or env file)
ENV SPRING_PROFILES_ACTIVE=prod \
    JAVA_OPTS="-Xms256m -Xmx512m" \
    SPRING_DOCKER_COMPOSE_ENABLED=false

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=120s --retries=5 \
    CMD curl -fsS http://127.0.0.1:8080/management/health || exit 1

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /app/app.jar"]
