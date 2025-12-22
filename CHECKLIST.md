# Чеклист перед экзаменом

## ✅ Проверка перед созданием архива

### 1. Все проекты собраны
- [ ] `hotelBooking-api` собран: `.\mvnw.cmd clean install -DskipTests`
- [ ] `events-roomBooking-contract` собран: `.\mvnw.cmd clean install -DskipTests`
- [ ] `roomBooking` собран: `.\mvnw.cmd clean package -DskipTests`
- [ ] Проверено наличие `roomBooking/target/roomBooking-rest-0.0.1-SNAPSHOT.jar`

### 2. JAR файлы в lib/ актуальны
- [ ] `roomBooking/lib/hotelBooking-api.jar` обновлен
- [ ] `roomBooking/lib/events-roomBooking-contract.jar` обновлен
- [ ] `notification-service/lib/events-roomBooking-contract.jar` обновлен
- [ ] `audit-booking-service/lib/events-roomBooking-contract.jar` обновлен

### 3. Все необходимые файлы на месте
- [ ] `docker-compose.yml` существует
- [ ] `prometheus.yml` существует
- [ ] Все `Dockerfile` на месте
- [ ] Все `pom.xml` на месте
- [ ] Все `src/` папки на месте
- [ ] Maven wrapper (mvnw, mvnw.cmd) на месте

### 4. Тестовая проверка (опционально, но рекомендуется)
- [ ] `docker-compose up --build -d` работает локально
- [ ] Все сервисы запускаются без ошибок
- [ ] `docker-compose ps` показывает все сервисы как "Up"

## 📦 Что включить в архив

### Обязательно:
- ✅ Все папки сервисов (roomBooking, pricing-service, notification-service, audit-booking-service, hotelBooking-api, events-roomBooking-contract)
- ✅ `docker-compose.yml`
- ✅ `prometheus.yml`
- ✅ `Jenkinsfile`
- ✅ `README.md`, `CHECKLIST.md`, `EXAM_INSTRUCTIONS.md`, `prepare-archive.md`
- ✅ Все `pom.xml`, `Dockerfile`, `src/`
- ✅ Папки `lib/` с JAR файлами
- ✅ Maven wrapper (`mvnw`, `mvnw.cmd`, `.mvn/`)
- ✅ **`roomBooking/target/roomBooking-rest-0.0.1-SNAPSHOT.jar`** (критически важно!)

### Исключить:
- ❌ Папки `target/` (кроме `roomBooking/target/roomBooking-rest-0.0.1-SNAPSHOT.jar`)
- ❌ Папки `.idea/`, `.vscode/`
- ❌ Папки `out/`
- ❌ Папки `.mvn/` (но можно оставить, если архив не слишком большой)

## 🚀 На экзамене

### Быстрый запуск:
```bash
# 1. Распакуйте архив
unzip bookings.zip
cd bookings

# 2. Запустите все сервисы
docker-compose up --build -d

# 3. Проверьте статус
docker-compose ps

# 4. Настройте Jenkins (см. EXAM_INSTRUCTIONS.md)
# 5. Настройте Grafana (см. EXAM_INSTRUCTIONS.md)
# 6. Проверьте логи (если что-то не работает)
docker-compose logs roomBooking
```

### Если roomBooking не запускается:
```bash
cd roomBooking
.\mvnw.cmd clean package -DskipTests
cd ..
docker-compose build roomBooking
docker-compose up -d roomBooking
```

### Полезные команды:
```bash
# Просмотр логов
docker-compose logs -f roomBooking

# Перезапуск сервиса
docker-compose restart roomBooking

# Остановка всех сервисов
docker-compose down

# Полная пересборка
docker-compose down
docker-compose build --no-cache
docker-compose up -d
```

## 🔍 Проверка работы

После запуска проверьте:
- [ ] roomBooking доступен: http://localhost:8081
- [ ] Jenkins доступен: http://localhost:8085
- [ ] RabbitMQ доступен: http://localhost:15672
- [ ] Prometheus доступен: http://localhost:9090
- [ ] Grafana доступен: http://localhost:3000
- [ ] Все контейнеры в статусе "Up": `docker-compose ps`

## ⚠️ Важные замечания

1. **roomBooking требует предварительной сборки JAR** - убедитесь, что `roomBooking/target/roomBooking-rest-0.0.1-SNAPSHOT.jar` включен в архив
2. **Порты должны быть свободны** - проверьте, что порты 8081, 8082, 8083, 9090, 9091, 3000, 5672, 15672, 9411 не заняты
3. **Docker должен быть запущен** - перед запуском убедитесь, что Docker Desktop работает
4. **Первый запуск может занять время** - Docker будет скачивать образы и собирать контейнеры

## 📝 Резервный план

Если что-то не работает на экзамене:
1. Проверьте логи: `docker-compose logs [service-name]`
2. Пересоберите проблемный сервис: `docker-compose build --no-cache [service-name]`
3. Если roomBooking не работает - пересоберите JAR локально (если есть Maven)
4. Проверьте, что все зависимости в `lib/` на месте

