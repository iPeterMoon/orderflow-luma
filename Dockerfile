FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /app
COPY pom.xml .
COPY orders-api/pom.xml orders-api/pom.xml
COPY notifications-lambda/pom.xml notifications-lambda/pom.xml
COPY orders-api/src orders-api/src
COPY notifications-lambda/src notifications-lambda/src
RUN mvn -B -pl orders-api -am clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /app/orders-api/target/orders-api.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
