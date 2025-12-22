# Этап сборки (чтобы не требовать установленного Java/Maven на хосте)
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
# Копируем локальные библиотеки (контракты), если они есть
COPY lib /app/lib
# Копируем описание проекта
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

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
# Копируем JAR из этапа сборки
COPY --from=build /app/target/*.jar app.jar
# Указываем порт, на котором работает приложение
EXPOSE 8083
ENTRYPOINT ["java", "-jar", "/app/app.jar"]