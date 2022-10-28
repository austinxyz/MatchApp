FROM openjdk:17-jdk-slim-buster
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
COPY src ./src
COPY input ./input
ENTRYPOINT ["java","-jar","/app.jar"]