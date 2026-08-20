# syntax=docker/dockerfile:1
# Runtime only (~180MB). JAR is built on the host via deploy/scripts/build-backend-jar.sh
# using a temporary Maven container (not kept in the final image).
# eclipse-temurin = OpenJDK 17 JRE (Eclipse Adoptium), not Eclipse IDE.
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

RUN apk add --no-cache curl \
    && addgroup -S -g 1001 bialem \
    && adduser -S -u 1001 -G bialem -h /app -s /sbin/nologin bialem

COPY target/bialem-0.0.1-SNAPSHOT.jar /app/app.jar
RUN chown bialem:bialem /app/app.jar

USER bialem
EXPOSE 8080

ENV SPRING_PROFILES_ACTIVE=prod \
    JAVA_OPTS="-Xms256m -Xmx512m" \
    SPRING_DOCKER_COMPOSE_ENABLED=false

HEALTHCHECK --interval=15s --timeout=5s --start-period=90s --retries=12 \
    CMD curl -fsS http://127.0.0.1:8080/management/health || exit 1

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /app/app.jar"]
