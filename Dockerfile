FROM eclipse-temurin:21
EXPOSE 8080
COPY target/*.jar journey-api.jar
ENV SPRING_PROFILES_ACTIVE=prod
ENTRYPOINT ["java","-jar","/journey-api.jar"]
