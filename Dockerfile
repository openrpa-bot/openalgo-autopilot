# Multi-stage build for Spring Boot application
FROM gradle:8.5-jdk21 AS build
WORKDIR /app

# Copy Gradle files
COPY build.gradle settings.gradle gradle.properties ./
COPY gradle ./gradle

# Copy all module source files
COPY api-module ./api-module
COPY db-layer ./db-layer
COPY socket-listener-module ./socket-listener-module
COPY ui-module ./ui-module
COPY src ./src

# Build the application
RUN gradle clean build -x test --no-daemon

# Runtime stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Create non-root user
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copy the built JAR from build stage
COPY --from=build /app/build/libs/*.jar app.jar

# Expose port (adjust if your app uses a different port)
EXPOSE 8092

# Set timezone
ENV TZ=Asia/Kolkata
RUN apk add --no-cache tzdata

# Run the application
ENTRYPOINT ["java", "-Duser.timezone=Asia/Kolkata", "-jar", "app.jar"]
