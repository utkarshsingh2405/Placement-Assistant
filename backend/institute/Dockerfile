# Build stage
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copy root pom and module poms for Maven reactor validation
COPY pom.xml .
COPY shared-lib/pom.xml shared-lib/
COPY portal/pom.xml portal/
COPY institute/pom.xml institute/
COPY placement/pom.xml placement/

# Copy sources
COPY shared-lib/ shared-lib/
COPY shared-web/ shared-web/
COPY institute/ institute/

# Package application
RUN mvn clean package -DskipTests -pl institute -am

# Runtime stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

RUN apk add --no-cache curl

# Create data and uploads directories
RUN mkdir -p /app/data /app/database /app/uploads/resumes

# Copy pre-seeded SQLite databases and executable JAR
COPY database/ /app/database/
COPY --from=build /app/institute/target/*.jar /app/app.jar

ENV SERVER_PORT=8081
ENV CONTEXT_PATH=/institute
ENV SPRING_PROFILES_ACTIVE=sqlite
ENV DB_PATH=/app/database/institute.db
ENV UPLOAD_DIR=/app/uploads/resumes

EXPOSE 8081 10000 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
