# Build stage
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copy root pom and module poms first for caching
COPY pom.xml .
COPY shared-lib/pom.xml shared-lib/
COPY placement/pom.xml placement/

# Copy sources
COPY shared-lib/ shared-lib/
COPY shared-web/ shared-web/
COPY placement/ placement/

# Package application
RUN mvn clean package -DskipTests -pl placement -am

# Runtime stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Create data and uploads directories
RUN mkdir -p /app/data /app/data/placement/uploads

COPY --from=build /app/placement/target/*.jar /app/app.jar

ENV SERVER_PORT=8082
ENV CONTEXT_PATH=/placement
ENV DB_PATH=/app/data/placement.db
ENV UPLOAD_DIR=/app/data/placement/uploads

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
