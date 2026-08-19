# syntax=docker/dockerfile:1
FROM maven:3.9.9-eclipse-temurin-17-jammy AS build
WORKDIR /src

COPY pom.xml sonar-project.properties checkstyle.xml ./
COPY src src
RUN mvn -ntp -Pprod -DskipTests package \
    && cp target/bialem-0.0.1-SNAPSHOT.jar /tmp/app.jar

FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

RUN apt-get update \
    && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/* \
    && groupadd --system --gid 1001 bialem \
    && useradd --system --uid 1001 --gid bialem --home-dir /app --shell /usr/sbin/nologin bialem

COPY --from=build /tmp/app.jar /app/app.jar
RUN chown bialem:bialem /app/app.jar

USER bialem
EXPOSE 8080

ENV SPRING_PROFILES_ACTIVE=prod \
    JAVA_OPTS="-Xms256m -Xmx512m" \
    SPRING_DOCKER_COMPOSE_ENABLED=false

HEALTHCHECK --interval=15s --timeout=5s --start-period=90s --retries=12 \
    CMD curl -fsS http://127.0.0.1:8080/management/health || exit 1

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /app/app.jar"]
