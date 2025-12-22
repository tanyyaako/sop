# Hotel Booking System

Микросервисная система бронирования номеров отелей.

## Структура проекта

- **roomBooking** - основной сервис бронирования (порт 8081)
- **pricing-service** - сервис динамического ценообразования через gRPC (порт 9090)
- **notification-service** - сервис уведомлений через WebSocket (порт 8083)
- **audit-booking-service** - сервис аудита бронирований (порт 8082)
- **hotelBooking-api** - общий API модуль
- **events-roomBooking-contract** - контракты событий

## Требования

- Docker и Docker Compose
- Java 17 (для локальной сборки, если нужно)
- Git (для работы с GitHub)

## Быстрый запуск

### 1. Распакуйте архив

```bash
unzip bookings.zip
cd bookings
```

### 2. Соберите зависимости (если нужно)

Если JAR файлы в `roomBooking/target/` отсутствуют, соберите их:

```bash
# Windows
cd hotelBooking-api
.\mvnw.cmd clean install -DskipTests
cd ..\events-roomBooking-contract
.\mvnw.cmd clean install -DskipTests
cd ..\roomBooking
.\mvnw.cmd clean package -DskipTests
cd ..
```

### 3. Запустите все сервисы

```bash
docker-compose up --build -d
```

### 4. Проверьте статус

```bash
docker-compose ps
```

### 5. Настройте Jenkins

1. Откройте http://localhost:8085
2. Получите initial admin password:
   ```bash
   docker-compose exec jenkins cat /var/jenkins_home/secrets/initialAdminPassword
   ```
3. Следуйте инструкциям в `EXAM_INSTRUCTIONS.md`

## Доступные сервисы

- **roomBooking API**: http://localhost:8081
- **Jenkins**: http://localhost:8085
- **RabbitMQ Management**: http://localhost:15672 (guest/guest)
- **Prometheus**: http://localhost:9090
- **Grafana**: http://localhost:3000 (admin/admin)
- **Zipkin**: http://localhost:9411
- **Notification Service WebSocket**: ws://localhost:8083/ws/notifications
- **Notification Service UI**: http://localhost:8083

## Остановка

```bash
docker-compose down
```

## Проблемы и решения

### roomBooking не запускается

Убедитесь, что JAR файл собран:
```bash
cd roomBooking
.\mvnw.cmd clean package -DskipTests
cd ..
docker-compose build roomBooking
docker-compose up -d roomBooking
```

### Ошибка подключения к pricing-service

Проверьте, что pricing-service запущен:
```bash
docker-compose logs pricing-service
```

### Проблемы с портами

Убедитесь, что порты 8081, 8082, 8083, 9090, 9091, 3000, 5672, 15672, 9411 свободны.

## Структура для архивации

Включите в архив:
- Все папки с сервисами (roomBooking, pricing-service, и т.д.)
- docker-compose.yml
- prometheus.yml
- README.md (этот файл)
- Все файлы pom.xml, Dockerfile, src/
- Папки lib/ с JAR зависимостями
- Maven wrapper (mvnw, mvnw.cmd, .mvn/)

Исключите из архива:
- Папки target/ (кроме roomBooking/target/*.jar если нужно)
- Папки .idea/, .vscode/
- Папки out/

## Примечания

- roomBooking требует предварительной сборки JAR файла перед запуском Docker
- Все сервисы используют Java 17
- RabbitMQ должен запуститься первым (healthcheck настроен)

