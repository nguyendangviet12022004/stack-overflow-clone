ARG PORT=8080

FROM maven:3.9.11-eclipse-temurin-21-alpine AS build

WORKDIR /build

COPY pom.xml .

RUN mvn dependency:go-offline

COPY src ./src

RUN mvn package

FROM eclipse-temurin:21.0.9_10-jre-alpine-3.22

WORKDIR /app

COPY --from=build /build/target/*.jar ./app.jar
RUN apk  update; apk  add curl


EXPOSE ${PORT}

ENTRYPOINT ["java", "-jar", "app.jar"]

