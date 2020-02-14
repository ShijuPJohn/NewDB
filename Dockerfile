FROM openjdk:8-jdk-alpine
COPY /var/lib/docker/tmp/docker-builder064442952/demo-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
