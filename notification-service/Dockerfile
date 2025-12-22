# Этап сборки (чтобы не требовать установленного Java/Maven на хосте)
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
# Копируем описание проекта
COPY pom.xml .
# Копируем JAR контрактов (если есть) и устанавливаем в Maven репозиторий
COPY lib/events-roomBooking-contract.jar /tmp/events-roomBooking-contract.jar
RUN if [ -f /tmp/events-roomBooking-contract.jar ]; then \
        mvn install:install-file \
            -Dfile=/tmp/events-roomBooking-contract.jar \
            -DgroupId=org.example \
            -DartifactId=events-roomBooking-contract \
            -Dversion=1.0-SNAPSHOT \
            -Dpackaging=jar || true; \
    fi
# Копируем исходный код
COPY src ./src
# Собираем проект (Maven найдет контракты в локальном репозитории)
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
# Копируем JAR из этапа сборки
COPY --from=build /app/target/*.jar app.jar
# Указываем порт, на котором работает приложение
EXPOSE 8083
ENTRYPOINT ["java", "-jar", "/app/app.jar"]