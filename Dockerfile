# Maven Build
FROM maven:3-eclipse-temurin-21-alpine AS builder
COPY pom.xml /app/
COPY src /app/src
RUN --mount=type=cache,target=/root/.m2 mvn -f /app/pom.xml clean package -DskipTests

# Run jar file with appropriate veriables
FROM eclipse-temurin:21
EXPOSE 8080
COPY target/*.jar journey-api.jar
ENV SPRING_PROFILES_ACTIVE=prod
ENTRYPOINT ["java","-jar","/journey-api.jar"]
