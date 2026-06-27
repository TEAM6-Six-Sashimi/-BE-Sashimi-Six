FROM eclipse-temurin:17-jdk-alpine AS builder

WORKDIR /workspace

COPY gradlew settings.gradle build.gradle ./
COPY gradle ./gradle
RUN sed -i 's/\r$//' ./gradlew && chmod +x ./gradlew

COPY src ./src
RUN ./gradlew clean bootJar --no-daemon -x test

FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

RUN addgroup -S spring \
    && adduser -S spring -G spring \
    && mkdir -p /app/logs /app/uploads \
    && chown -R spring:spring /app

COPY --from=builder /workspace/build/libs/lms-0.0.1-SNAPSHOT.jar /app/app.jar

ENV SPRING_PROFILES_ACTIVE=prod
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75.0 -Duser.timezone=Asia/Seoul"

EXPOSE 8080

USER spring

HEALTHCHECK --interval=30s --timeout=5s --start-period=60s --retries=3 \
    CMD wget -qO- http://localhost:8080/actuator/health | grep -q '"status":"UP"' || exit 1

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
