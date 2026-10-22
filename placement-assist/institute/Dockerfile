# Build stage
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copy root pom and module poms first for caching
COPY pom.xml .
COPY shared-lib/pom.xml shared-lib/
COPY institute/pom.xml institute/

# Copy sources
COPY shared-lib/ shared-lib/
COPY shared-web/ shared-web/
COPY institute/ institute/

# Package application
RUN mvn clean package -DskipTests -pl institute -am

# Runtime stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Create data and uploads directories
RUN mkdir -p /app/data /app/uploads/resumes

COPY --from=build /app/institute/target/*.jar /app/app.jar

ENV SERVER_PORT=8081
ENV DB_PATH=/app/data/institute.db
ENV UPLOAD_DIR=/app/uploads/resumes

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
