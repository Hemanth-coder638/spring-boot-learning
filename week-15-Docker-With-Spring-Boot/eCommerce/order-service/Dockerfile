FROM maven:3.9.4-eclipse-temurin-21-alpine

RUN apk add --no-cache curl

WORKDIR /app

COPY .mvn/ .mvn
COPY mvnw pom.xml ./

RUN chmod +x mvnw
RUN ./mvnw dependency:go-offline

COPY src ./src

EXPOSE 9021

CMD ["./mvnw", "spring-boot:run"]