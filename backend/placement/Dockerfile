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
COPY placement/ placement/

# Package application
RUN mvn clean package -DskipTests -pl placement -am

# Runtime stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

RUN apk add --no-cache curl

# Create data and uploads directories
RUN mkdir -p /app/data /app/data/placement/uploads

COPY --from=build /app/placement/target/*.jar /app/app.jar

ENV PORT=8082
ENV SERVER_PORT=8082
ENV CONTEXT_PATH=/placement
ENV SPRING_PROFILES_ACTIVE=dev
ENV UPLOAD_DIR=/app/data/placement/uploads

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
