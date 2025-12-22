# Этап сборки (чтобы не требовать установленного Java/Maven на хосте)
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
# Копируем локальные библиотеки (контракты), если они есть
COPY lib /app/lib
# Копируем описание проекта
COPY pom.xml .
COPY src ./src

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
# ARG позволяет передать имя файла при сборке
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
# Указываем порт, на котором работает приложение
EXPOSE 8083
ENTRYPOINT ["java", "-jar", "/app/app.jar"]