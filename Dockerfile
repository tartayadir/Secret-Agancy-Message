## Use GraalVM as the base image
#FROM ghcr.io/graalvm/graalvm-ce:latest as graalvm
#
## Set the working directory inside the Docker image
#WORKDIR /app
#
## Copy the gradle files and the application code to the working directory
#COPY build.gradle settings.gradle gradlew /app/
#COPY gradle /app/gradle
#COPY src /app/src
#
## Install GraalVM native image component
#RUN gu install native-image
#
## Run Gradle build to resolve dependencies and build the application
#RUN ./gradlew build -x test
#
## Create the native image
#RUN ./gradlew nativeCompile
#
## Use a smaller base image for the final application
#FROM amazonlinux:2
#
## Set the working directory inside the final Docker image
#WORKDIR /app
#
## Copy the native image from the build stage
#COPY --from=graalvm /app/build/native/nativeCompile/* /app/
#
## Expose the application's port
#EXPOSE 8080
#
## Run the native executable
#CMD ["./secret-agency-message-service"]

#TODO fix issue described below
#GraalVM support works properly except issue with finding executable native image

FROM openjdk:17
WORKDIR /my-project
CMD ["./gradlew", "clean", "bootJar"]
COPY build/libs/*.jar app.jar

#EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]