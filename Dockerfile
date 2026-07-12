# ==================================================================================
# Build stage: Compiles the Java application using Maven. It leverages Docker's
# caching by copying the pom.xml first and downloading dependencies offline to
# speed up builds. The .mvn directory and mvnw wrapper script are also copied to
# ensure consistent Maven usage. After setting the executable permission on the
# Maven wrapper and downloading the dependencies, we run the build to create the
# clean JAR file. Test strategy: `mvn package` runs unit tests, but does NOT run
# integration tests (IT). This is intentional: Testcontainers-based ITs need a
# Docker daemon, which is not available inside `docker build`. ITs are executed
# via `./mvnw verify` locally or in CI before the image is built. `-DskipITs` is
# added to communicate this intent explicitly to anyone reading the Dockerfile,
# even though ITs are already skipped by phase ordering. The flag -B is used for
# batch mode to avoid interactive prompts during the build.
# ==================================================================================

FROM eclipse-temurin:21-jdk-alpine AS build

WORKDIR /app

COPY pom.xml mvnw ./

COPY .mvn ./.mvn

RUN chmod +x mvnw

RUN ./mvnw dependency:go-offline -B

COPY src ./src

RUN ./mvnw clean package -DskipITs -B

# ==================================================================================
# Runtime stage: Uses a minimal JRE image to run the application. A system group +
# user are created, without a password for security reasons. The compiled JAR from
# the build stage is copied over with appropriate ownership. The heap is capped at
# 75% of the container memory limit. The JVM timezone is explicitly set to UTC to
# match the database. The application runs and listens on port 8080.
# ==================================================================================

FROM eclipse-temurin:21-jre-alpine AS runtime

RUN addgroup -S fiapgroup && adduser -S fiapuser -G fiapgroup

USER fiapuser

WORKDIR /app

COPY --chown=fiapuser:fiapgroup --from=build /app/target/querocomidahub*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-Duser.timezone=UTC", "-jar", "app.jar"]
