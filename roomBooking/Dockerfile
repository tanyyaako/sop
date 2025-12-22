# Используем легковесный образ JRE 17 на базе Alpine Linux
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Создаем пользователя 'javauser', чтобы не запускать приложение от root (Security Best Practice)
RUN addgroup -S javauser && adduser -S javauser -G javauser

# Аргумент для поиска JAR файла.
# Используем wildcard, так как .original файлы исключены через .dockerignore
# Так как мы сделали mvn clean package, в папке target будет лежать нужный нам fat-jar.
ARG JAR_FILE=target/*.jar

# Копируем JAR внутрь образа под именем app.jar
COPY --chown=javauser:javauser ${JAR_FILE} app.jar

# Переключаемся на пользователя
USER javauser

# Документируем порт (этот порт должен совпадать с server.port в properties)
EXPOSE 8081

# Запускаем приложение
# Использование exec формы ["..."] позволяет корректно передавать сигналы остановки (SIGTERM)
ENTRYPOINT ["java", "-jar", "app.jar"]