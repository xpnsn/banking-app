FROM openjdk:17-jdk-alpine
COPY target/*.jar security.jar
ENTRYPOINT ["java","-jar","security.jar"]