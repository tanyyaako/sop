# Используем легковесный образ JRE 17 на базе Alpine Linux
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Создаем пользователя 'javauser', чтобы не запускать приложение от root (Security Best Practice)
RUN addgroup -S javauser && adduser -S javauser -G javauser

# Копируем JAR файл (должен быть собран локально перед сборкой Docker образа)
# Используем явное имя файла для надежности
COPY --chown=javauser:javauser target/roomBooking-rest-0.0.1-SNAPSHOT.jar app.jar

# Переключаемся на пользователя
USER javauser

# Документируем порт (этот порт должен совпадать с server.port в properties)
EXPOSE 8081

# Запускаем приложение
# Использование exec формы ["..."] позволяет корректно передавать сигналы остановки (SIGTERM)
ENTRYPOINT ["java", "-jar", "app.jar"]