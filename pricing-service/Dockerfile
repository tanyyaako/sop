# Этап сборки
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src

# Собираем проект
RUN mvn clean package -DskipTests

# Этап запуска
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

RUN addgroup -S javauser && adduser -S javauser -G javauser

# Копируем JAR из этапа сборки
COPY --from=build /app/target/*.jar app.jar

RUN chown javauser:javauser /app/app.jar
USER javauser

# Порт gRPC
EXPOSE 9090

ENTRYPOINT ["java", "-jar", "app.jar"]