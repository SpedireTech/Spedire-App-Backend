FROM maven:3.8.5-openjdk-17 AS build
FROM openjdk:17.0.1-jdk-slim

# Install Maven
RUN apt-get update && \
    apt-get install -y maven

COPY . .
RUN mvn clean package -DskipTests

FROM openjdk:17.0.1-jdk-slim
COPY --from=build /target/Spedire-0.0.1-SNAPSHOT.jar Spedire.jar

EXPOSE  8081
ENTRYPOINT ["java", "-jar","Spedire.jar"]




