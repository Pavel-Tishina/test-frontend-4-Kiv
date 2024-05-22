#############
# backend   #
#############
# FROM maven:3.8.4-openjdk-17 AS build
#
# WORKDIR /app
# COPY ./pom.xml /app
# COPY ./src /app/src
#
# RUN mvn clean package -Dmaven.test.skip=true
# FROM openjdk:17-jdk
#
# WORKDIR /app
# COPY --from=build /app/target/*.jar app.jar
#
# EXPOSE 8080
# CMD ["java", "-jar", "app.jar"]

FROM eclipse-temurin:17-jdk-focal

LABEL maintainer="test4sandbox"
ADD target/test4sandbox-0.0.1-SNAPSHOT.jar test4sandbox.jar

ENTRYPOINT ["java", "-jar", "test4sandbox.jar"]