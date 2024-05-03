FROM maven:3.8.5-openjdk-17 AS build
COPY . .
RUN mvn install -DskipTests

FROM openjdk:17.0.1-jdk-slim
COPY --from=build /target/Spedire-0.0.1-SNAPSHOT.jar Spedire.jar

EXPOSE  8080
ENTRYPOINT ["java", "-jar","Spedire.jar"]






