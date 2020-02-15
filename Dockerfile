FROM openjdk:8-jdk-alpine
COPY jarfile/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
