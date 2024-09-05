# Maven Build
FROM maven:3-eclipse-temurin-21-alpine AS build
COPY . .
RUN mvn clean package -Pprod -DskipTests


# Run jar file with appropriate veriables
FROM eclipse-temurin:21-jre-alpine
EXPOSE 8080
COPY --from=build target/*.jar journey-api.jar
ENV SPRING_PROFILES_ACTIVE=prod
ENTRYPOINT ["java","-jar","/journey-api.jar"]
