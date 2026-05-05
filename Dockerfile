FROM eclipse-temurin:21-jdk-jammy AS builder

WORKDIR /app

ENV GRADLE_OPTS="-Dorg.gradle.internal.http.socketTimeout=120000 -Dorg.gradle.internal.http.connectionTimeout=120000"

COPY build.gradle settings.gradle gradlew ./
COPY gradle ./gradle
COPY src ./src

RUN chmod +x gradlew

RUN ./gradlew bootJar --no-daemon

FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar
COPY --from=builder /app/src/main/resources/static/ /static/

RUN mkdir -p /data/photos

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]