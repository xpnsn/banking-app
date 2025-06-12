FROM openjdk:17-jdk-alpine
COPY target/*.jar transactions.jar
ENTRYPOINT ["java","-jar","transactions.jar"]