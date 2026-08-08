FROM eclipse-temurin:22-jre-jammy

WORKDIR /app

COPY build/libs/*.jar app.jar

EXPOSE 2025

ENTRYPOINT ["java", "-jar", "app.jar"]