FROM maven:3.8.5-openjdk-21 AS build
COPY . .
RUN mvn clean package -DskipTests

FROM openjdk:21.0.1-jdk-slim
COPY --from=build /target/Spedire-0.0.1-SNAPSHOT.jar Spedire.jar

EXPOSE  8081
ENTRYPOINT ["java", "-jar","Spedire.jar"]




