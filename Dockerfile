FROM openjdk:8-jdk-alpine
COPY home/runner/work/NewDB/NewDB/build/libs/demo-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
