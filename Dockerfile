FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/PersonalMoneyTracker-1.3-SNAPSHOT-jar-with-dependencies.jar app.jar

COPY src/main/resources/application.properties /app/config/application.properties

ENTRYPOINT ["java", "-jar", "app.jar"]