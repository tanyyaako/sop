# Этап сборки
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
# Копируем локальные библиотеки (контракты), если они есть
COPY lib /app/lib
COPY pom.xml .
COPY src ./src

# Устанавливаем локальные зависимости в Maven репозиторий
RUN mvn install:install-file \
    -Dfile=/app/lib/events-roomBooking-contract.jar \
    -DgroupId=org.example \
    -DartifactId=events-roomBooking-contract \
    -Dversion=1.0-SNAPSHOT \
    -Dpackaging=jar || true

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

# Порт сервиса
EXPOSE 8082

ENTRYPOINT ["java", "-jar", "app.jar"]