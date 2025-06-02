# Maven Build
FROM maven:3-eclipse-temurin-21-alpine AS build
COPY . .
RUN mvn clean package -P prod -DskipTests -P release -pl '!journey-api-tests'


# Run jar file with appropriate veriables
FROM eclipse-temurin:21.0.7_6-jre-alpine
EXPOSE 8080
COPY --from=build journey-api-web/target/*.jar journey-api.jar
ENV SPRING_PROFILES_ACTIVE=prod
ENTRYPOINT ["java","-jar","/journey-api.jar"]
