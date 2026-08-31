FROM eclipse-temurin:21-jre

WORKDIR /app

COPY target/Banking-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 1212

ENTRYPOINT ["java", "-jar", "app.jar"]