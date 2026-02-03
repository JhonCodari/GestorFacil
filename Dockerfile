FROM eclipse-temurin:25-jdk-alpine

WORKDIR /app

COPY target/*.jar app.jar

EXPOSE 8080
EXPOSE 5005

ENTRYPOINT ["java", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005", "-jar", "app.jar"]

# ENTRYPOINT ["java", "-jar", "app.jar"]