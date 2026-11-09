# ==============================================================================
# Placement Assistant Unified Platform - Single-JVM Container
# Single runnable Spring Boot JAR running Portal, Institute, and Placement
# Optimized for 512 MB RAM environments (Render Free Tier, Railway, Fly.io)
# ==============================================================================

# Stage 1: Build unified portal JAR
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copy reactor poms for efficient layer caching
COPY pom.xml .
COPY backend/pom.xml backend/
COPY backend/shared-lib/pom.xml backend/shared-lib/
COPY backend/portal/pom.xml backend/portal/
COPY backend/portal/institute/pom.xml backend/portal/institute/
COPY backend/portal/placement/pom.xml backend/portal/placement/

# Copy sources
COPY backend/shared-lib/ backend/shared-lib/
COPY backend/shared-web/ backend/shared-web/
COPY backend/portal/ backend/portal/

# Build portal and its submodule dependencies
WORKDIR /app/backend
RUN mvn clean package -pl portal -am -DskipTests

# Stage 2: Minimal Runtime image
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

RUN apt-get update && apt-get install -y --no-install-recommends curl procps && rm -rf /var/lib/apt/lists/*

# Create data and upload storage directories (including /data for persistent disk mount)
RUN mkdir -p /data \
    /app/database \
    /app/uploads/resumes

# Copy pre-seeded SQLite database
COPY backend/database/ /app/database/

# Copy single executable JAR from build stage
COPY --from=build /app/backend/portal/target/portal.jar /app/portal.jar

# Environment defaults for cloud container
ENV PORT=10000
ENV SPRING_PROFILES_ACTIVE=sqlite
ENV SQLITE_DB_PATH=/data/app.db
ENV DB_PATH=/data/app.db
ENV MAIL_MODE=log

EXPOSE 10000

ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75", "-XX:+UseSerialGC", "-Xss512k", "-jar", "/app/portal.jar"]
