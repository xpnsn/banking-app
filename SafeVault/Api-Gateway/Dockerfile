FROM openjdk:18.0-slim
COPY target/*.jar api-gateway.jar
EXPOSE 5000
ENTRYPOINT ["java", "-jar", "/api-gateway.jar"]