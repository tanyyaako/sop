FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

RUN addgroup -S javauser && adduser -S javauser -G javauser

COPY --from=build /app/target/*.jar app.jar

RUN chown javauser:javauser /app/app.jar
USER javauser

EXPOSE 9090

ENTRYPOINT ["java", "-jar", "app.jar"]