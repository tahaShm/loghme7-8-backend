# For Java 8, try this
#FROM openjdk:8-jdk-alpine

# For Java 11, try this
FROM maven:3.6.3-jdk-11-slim AS build

COPY src /usr/loghme7-back/src
COPY pom.xml /usr/loghme7-back

# package our application code
RUN mvn -f /usr/loghme7-back/pom.xml clean package

# For Java 11, try this
FROM adoptopenjdk/openjdk11:alpine-jre

# Refer to Maven build -> finalName

COPY --from=build /usr/loghme7-back/target/loghme7-back-0.0.1-SNAPSHOT.jar app.jar

# java -jar /opt/app/app.jar
CMD ["java","-jar","app.jar"]