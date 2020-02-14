FROM openjdk:8-jdk-alpine
COPY home/runner/work/NewDB/NewDB/build/libs/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
