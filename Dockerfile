FROM alpine:latest

RUN apk add --no-cache openjdk17-jre

WORKDIR /diploma_app

COPY target/Diploma_BACK-1.0-SNAPSHOT.jar app.jar

EXPOSE 9090

ENTRYPOINT ["java", "-jar", "app.jar"]
