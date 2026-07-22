# syntax=docker/dockerfile:1

FROM maven:3.9-eclipse-temurin-21 AS builder

ARG MODULE
WORKDIR /workspace

COPY pom.xml .
COPY infra/api-gateway/pom.xml infra/api-gateway/pom.xml
COPY infra/config-server/pom.xml infra/config-server/pom.xml
COPY infra/eureka-server/pom.xml infra/eureka-server/pom.xml
COPY services/catalog-service/pom.xml services/catalog-service/pom.xml
COPY services/member-service/pom.xml services/member-service/pom.xml
COPY services/loan-service/pom.xml services/loan-service/pom.xml
COPY services/fine-service/pom.xml services/fine-service/pom.xml
COPY services/notification-service/pom.xml services/notification-service/pom.xml
COPY shared/common-web/pom.xml shared/common-web/pom.xml

RUN --mount=type=cache,target=/root/.m2 \
    test -n "${MODULE}" \
    && mvn --batch-mode --no-transfer-progress \
       -pl "${MODULE}" -am dependency:go-offline -DskipTests

COPY . .

RUN --mount=type=cache,target=/root/.m2 \
    test -n "${MODULE}" \
    && mvn --batch-mode --no-transfer-progress \
        -pl "${MODULE}" -am package -DskipTests \
    && ls -l "${MODULE}/target"/*.jar \
    && find "${MODULE}/target" \
        -maxdepth 1 \
        -type f \
        -name "*.jar" \
        ! -name "*.original" \
        -printf "%s %p\n" \
        | sort -rn \
        | head -n1 \
        | cut -d' ' -f2- \
        | xargs -I{} cp {} /application.jar \
    && test -f /application.jar \
    && jar xf /application.jar META-INF/MANIFEST.MF \
    && grep -q "^Main-Class:" META-INF/MANIFEST.MF

FROM eclipse-temurin:21-jre

WORKDIR /app

RUN mkdir -p /app/uploads/catalog \
    && chown -R 10001:10001 /app

COPY --from=builder --chown=10001:10001 /application.jar /app/app.jar

USER 10001:10001

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
