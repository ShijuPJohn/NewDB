COPY . *.jar app.jar
FROM openjdk:8-jdk-alpine
ENTRYPOINT ["java","-jar","/app.jar"]
