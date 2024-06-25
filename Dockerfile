FROM openjdk:17
WORKDIR /app
COPY target/Medicine-Inventory-Management-System-0.0.1-SNAPSHOT.jar /app/app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]