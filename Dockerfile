# ==============================================================================
# Placement Assistant Unified Platform - Railway & Docker Container
# Combines Portal Gateway + Institute Pillar + Placement Pillar in One Container
# ==============================================================================

# Stage 1: Build all modules
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copy reactor poms for dependency resolution
COPY pom.xml .
COPY backend/pom.xml backend/
COPY backend/shared-lib/pom.xml backend/shared-lib/
COPY backend/portal/pom.xml backend/portal/
COPY backend/institute/pom.xml backend/institute/
COPY backend/placement/pom.xml backend/placement/

# Copy sources
COPY backend/shared-lib/ backend/shared-lib/
COPY backend/shared-web/ backend/shared-web/
COPY backend/portal/ backend/portal/
COPY backend/institute/ backend/institute/
COPY backend/placement/ backend/placement/

# Build and package all executable JARs
RUN mvn clean package -Dmaven.test.skip=true

# Stage 2: Unified Runtime image
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

RUN apt-get update && apt-get install -y --no-install-recommends curl procps && rm -rf /var/lib/apt/lists/*

# Create data and upload storage directories
RUN mkdir -p /app/database \
    /app/data/portal/uploads \
    /app/data/institute/uploads/resumes \
    /app/data/placement/uploads/jd \
    /app/data/placement/uploads/results \
    /app/data/placement/uploads/offers \
    /app/data/placement/uploads/logos \
    /app/uploads/resumes

# Copy pre-seeded SQLite databases
COPY database/ /app/database/

# Copy executable JARs from build stage
COPY --from=build /app/backend/portal/target/portal-1.0.0-SNAPSHOT.jar /app/portal.jar
COPY --from=build /app/backend/institute/target/institute-1.0.0-SNAPSHOT.jar /app/institute.jar
COPY --from=build /app/backend/placement/target/placement-1.0.0-SNAPSHOT.jar /app/placement.jar

# Environment defaults for cloud container
ENV PORT=8080
ENV SERVER_PORT=8080
ENV SPRING_PROFILES_ACTIVE=sqlite
ENV DB_PATH=/app/database/portal.db
ENV DB_INSTITUTE_PATH=/app/database/institute.db
ENV DB_PLACEMENT_PATH=/app/database/placement.db
ENV PILLAR_INSTITUTE_JAR=/app/institute.jar
ENV PILLAR_PLACEMENT_JAR=/app/placement.jar
ENV INSTITUTE_URL=http://localhost:8081
ENV PLACEMENT_URL=http://localhost:8082
ENV INSTITUTE_API_KEY=inst-sec-key-7f9a12b4e8c345d6
ENV JWT_SECRET=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970
ENV MAIL_MODE=log

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java -Dserver.port=${PORT:-8080} -Dspring.profiles.active=sqlite -DDB_PATH=/app/database/portal.db -DDB_INSTITUTE_PATH=/app/database/institute.db -DDB_PLACEMENT_PATH=/app/database/placement.db -Dpillar.institute.jar=/app/institute.jar -Dpillar.placement.jar=/app/placement.jar -jar /app/portal.jar"]

