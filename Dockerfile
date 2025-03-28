FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/PersonalMoneyTracker-1.3-SNAPSHOT-jar-with-dependencies.jar app.jar

COPY src/main/resources/application.yml /app/config/application.yml

ENTRYPOINT ["java", "-jar", "app.jar"]