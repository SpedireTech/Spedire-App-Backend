#FROM maven:3.8.5-openjdk-17 AS build
#COPY . .
#RUN mvn clean package -DskipTests
#
#FROM openjdk:17.0.1-jdk-slim
#COPY --from=build /target/Spedire-0.0.1-SNAPSHOT.jar Spedire.jar
#
#EXPOSE  8081
#ENTRYPOINT ["java", "-jar","Spedire.jar"]
#
#
#
#


FROM maven:3.8.5-openjdk-17 AS build

# Set the working directory in the container
WORKDIR /app

# Copy the Maven project files into the container
COPY . .

# Build the Maven project
RUN mvn clean package -DskipTests

FROM openjdk:17.0.1-jdk-slim

# Copy the built JAR file from the build stage to the current stage
COPY --from=build /app/target/Spedire-0.0.1-SNAPSHOT.jar Spedire.jar

# Expose the port on which the application will run
EXPOSE 8080

# Define the command to run the application
ENTRYPOINT ["java", "-jar", "Spedire.jar"]
