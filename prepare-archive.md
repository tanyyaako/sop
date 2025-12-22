# Инструкция по подготовке архива для экзамена

## Шаг 1: Убедитесь, что все проекты собраны

```powershell
# Соберите все зависимости
cd hotelBooking-api
.\mvnw.cmd clean install -DskipTests
cd ..\events-roomBooking-contract
.\mvnw.cmd clean install -DskipTests

# Соберите roomBooking (важно!)
cd ..\roomBooking
.\mvnw.cmd clean package -DskipTests
cd ..
```

## Шаг 2: Проверьте наличие JAR файлов

Убедитесь, что существуют:
- `roomBooking/target/roomBooking-rest-0.0.1-SNAPSHOT.jar` ✅
- `hotelBooking-api/target/hotelBooking-api-0.0.1-SNAPSHOT.jar` ✅
- `events-roomBooking-contract/target/events-roomBooking-contract-1.0-SNAPSHOT.jar` ✅

## Шаг 3: Обновите JAR файлы в lib/ (если нужно)

```powershell
copy hotelBooking-api\target\hotelBooking-api-0.0.1-SNAPSHOT.jar roomBooking\lib\hotelBooking-api.jar
copy events-roomBooking-contract\target\events-roomBooking-contract-1.0-SNAPSHOT.jar roomBooking\lib\events-roomBooking-contract.jar
copy events-roomBooking-contract\target\events-roomBooking-contract-1.0-SNAPSHOT.jar notification-service\lib\events-roomBooking-contract.jar
copy events-roomBooking-contract\target\events-roomBooking-contract-1.0-SNAPSHOT.jar audit-booking-service\lib\events-roomBooking-contract.jar
```

## Шаг 4: Создайте архив

### Вариант 1: Вручную (Windows)

1. Выделите все папки и файлы в корне проекта
2. Исключите:
   - `target/` (кроме `roomBooking/target/roomBooking-rest-0.0.1-SNAPSHOT.jar`)
   - `.idea/`
   - `.vscode/`
   - `out/`
   - `jenkins-data/` (если есть)
3. Создайте ZIP архив

### Вариант 2: PowerShell скрипт

```powershell
# Создайте архив, исключая ненужные папки
Compress-Archive -Path `
  docker-compose.yml, `
  prometheus.yml, `
  Jenkinsfile, `
  README.md, `
  CHECKLIST.md, `
  EXAM_INSTRUCTIONS.md, `
  prepare-archive.md, `
  roomBooking, `
  pricing-service, `
  notification-service, `
  audit-booking-service, `
  hotelBooking-api, `
  events-roomBooking-contract `
  -DestinationPath bookings-exam.zip -Force
```

## Шаг 5: Проверьте размер архива

Архив должен быть примерно 5-15 МБ (без target папок). Если больше - проверьте, что исключили target/.

## Что должно быть в архиве:

✅ Все папки сервисов (roomBooking, pricing-service, и т.д.)
✅ docker-compose.yml
✅ prometheus.yml
✅ Jenkinsfile
✅ README.md, CHECKLIST.md, EXAM_INSTRUCTIONS.md, prepare-archive.md
✅ Все pom.xml, Dockerfile, src/
✅ Папки lib/ с JAR файлами
✅ Maven wrapper (mvnw, mvnw.cmd, .mvn/)
✅ roomBooking/target/roomBooking-rest-0.0.1-SNAPSHOT.jar (важно!)

❌ Папки target/ (кроме roomBooking/target/*.jar)
❌ Папки .idea/, .vscode/
❌ Папки out/
❌ jenkins-data/ (volume для Jenkins)

## На экзамене:

1. Распакуйте архив
2. Откройте терминал в папке проекта
3. Запустите: `docker-compose up --build -d`
4. Подождите, пока все контейнеры запустятся
5. Проверьте: `docker-compose ps`

Если roomBooking не запустится, возможно нужно пересобрать JAR:
```bash
cd roomBooking
.\mvnw.cmd clean package -DskipTests
cd ..
docker-compose build roomBooking
docker-compose up -d roomBooking
```

