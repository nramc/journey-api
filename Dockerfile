# Use a JDK image for the build stage; we run the Maven Wrapper to ensure consistent Maven/version
FROM eclipse-temurin:21-jdk AS build

WORKDIR /workspace

# Copy everything (including mvnw and .mvn)
COPY . /workspace

# Ensure the wrapper is executable
RUN chmod +x ./mvnw

# Use the Maven Wrapper so the project uses the exact Maven version it expects
# -B = batch mode, adjust profiles to match your intended build
RUN ./mvnw -B package -P release -pl '!journey-api-tests' -DskipTests

# Final runtime image: use a matching JRE for Java 21
FROM eclipse-temurin:21-jre

# Install any needed packages (alpine vs debian depends on chosen base; using debian-based jre above)
RUN apt-get update \
    && apt-get install \
        --yes \
        --no-install-recommends \
        ca-certificates \
        bind9-dnsutils \
        busybox \
    && rm -rf /var/lib/apt/lists/*

EXPOSE 8080

# Copy artifact from build stage (adjust path if different)
COPY --from=build /workspace/journey-api-web/target/*.jar /journey-api.jar

ENV SPRING_PROFILES_ACTIVE=prod

ENTRYPOINT ["java", "-jar", "/journey-api.jar"]