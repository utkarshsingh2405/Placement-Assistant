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
COPY portal/ portal/

# Package application
RUN mvn clean package -DskipTests -pl portal -am

# Runtime stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Create data directories
RUN mkdir -p /app/data /app/data/portal/uploads

COPY --from=build /app/portal/target/*.jar /app/app.jar

ENV SERVER_PORT=8080
ENV DB_PATH=/app/data/portal.db

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
