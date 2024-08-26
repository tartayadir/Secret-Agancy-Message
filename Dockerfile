## Stage 1: Build the application
#FROM ghcr.io/graalvm/graalvm-ce:latest AS build
#
## Install native-image tool
#RUN gu install native-image
#
#WORKDIR /app
#
## Copy Gradle wrapper and source files
#COPY gradlew .
#COPY gradle gradle
#COPY build.gradle .
#COPY src src
#
## Build the application using Gradle
#RUN ./gradlew bootBuildImage --imageName=secret-agency-message-service
#
## Stage 2: Run the application
#FROM ubuntu:20.04
#
#WORKDIR /app
#
## Copy the native binary from the build stage
#COPY --from=build /app/build/libs/secret-agency-message-service .
#
## Expose the application port
#EXPOSE 8080
#
## Run the native binary
#CMD ["./secret-agency-message-service"]
FROM openjdk:17
WORKDIR /my-project
CMD ["./gradlew", "clean", "bootJar"]
COPY build/libs/*.jar app.jar

#EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]