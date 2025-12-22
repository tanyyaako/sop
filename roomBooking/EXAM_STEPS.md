# 📝 Пошаговая инструкция для экзамена

## 🔧 ПЕРЕД ЭКЗАМЕНОМ (дома)

### 1️⃣ Соберите проекты
```powershell
cd hotelBooking-api
.\mvnw.cmd clean install -DskipTests
cd ..\events-roomBooking-contract
.\mvnw.cmd clean install -DskipTests
cd ..\roomBooking
.\mvnw.cmd clean package -DskipTests
cd ..
```

### 2️⃣ Обновите lib/
```powershell
copy hotelBooking-api\target\hotelBooking-api-0.0.1-SNAPSHOT.jar roomBooking\lib\hotelBooking-api.jar
copy events-roomBooking-contract\target\events-roomBooking-contract-1.0-SNAPSHOT.jar roomBooking\lib\events-roomBooking-contract.jar
copy events-roomBooking-contract\target\events-roomBooking-contract-1.0-SNAPSHOT.jar notification-service\lib\events-roomBooking-contract.jar
copy events-roomBooking-contract\target\events-roomBooking-contract-1.0-SNAPSHOT.jar audit-booking-service\lib\events-roomBooking-contract.jar
```

### 3️⃣ Проверьте файлы
- ✅ `roomBooking/target/roomBooking-rest-0.0.1-SNAPSHOT.jar` должен существовать!

### 4️⃣ Протестируйте
```powershell
docker-compose up --build -d
docker-compose ps  # Все должны быть "Up"
docker-compose down
```

### 5️⃣ Создайте архив

**Вариант А: Минимальный (рекомендуется)**
- **Исключите:**
  - `target/` (кроме `roomBooking/target/roomBooking-rest-0.0.1-SNAPSHOT.jar`) - скомпилированные файлы, можно пересобрать
  - `.idea/`, `.vscode/` - настройки IDE, персональные файлы
  - `out/` - альтернативная папка для скомпилированных файлов
  - `jenkins-data/` - Docker volume для Jenkins, создается автоматически
- **Включите:** все папки сервисов, `docker-compose.yml`, `Jenkinsfile`, `lib/`, `roomBooking/target/roomBooking-rest-0.0.1-SNAPSHOT.jar`
- **Размер:** ~5-15 МБ

**Вариант Б: Всё включить (проще)**
- Просто заархивируйте всю папку проекта целиком
- **Плюсы:** Не нужно ничего исключать, ничего не забудете
- **Минусы:** Архив будет больше (~50-100 МБ), но это не критично
- **Важно:** Убедитесь, что `roomBooking/target/roomBooking-rest-0.0.1-SNAPSHOT.jar` включен!

---

## 🎓 НА ЭКЗАМЕНЕ

### ШАГ 1: Запуск проекта (5 минут)

```bash
unzip bookings.zip
cd bookings
docker-compose up --build -d
docker-compose ps  # Проверьте, что все "Up"
```

**Если roomBooking не запустился:**
```bash
cd roomBooking
.\mvnw.cmd clean package -DskipTests
cd ..
docker-compose build roomBooking
docker-compose up -d roomBooking
```

---

### ШАГ 2: Настройка Grafana (3 минуты)

1. Откройте: http://localhost:3000
2. Логин: `admin`, Пароль: `admin`
3. **Configuration** → **Data Sources** → **Add data source**
4. Выберите **Prometheus**
5. URL: `http://prometheus:9090`
6. **Save & Test**

---

### ШАГ 3: Настройка Jenkins (10 минут)

#### 3.1. Первый запуск
1. Откройте: http://localhost:8085
2. Получите пароль:
   ```bash
   docker-compose exec jenkins cat /var/jenkins_home/secrets/initialAdminPassword
   ```
3. Введите пароль → **Install suggested plugins**
4. Создайте админа: `admin` / `admin`

#### 3.2. Настройте инструменты
1. **Manage Jenkins** → **Tools**
2. **JDK**: Name=`JDK 17`, JAVA_HOME=`/usr/lib/jvm/java-17-openjdk-amd64`
3. **Maven**: Name=`Maven`, MAVEN_HOME=`/usr/share/maven`

#### 3.3. Создайте Pipeline
1. **New Item** → `bookings-pipeline` → **Pipeline** → **OK**
2. **Pipeline** → **Pipeline script from SCM**
3. SCM: **Git** → Repository URL: `<ваш-github-url>`
4. Script Path: `Jenkinsfile` → **Save**

---

### ШАГ 4: GitHub и CI/CD (10 минут)

#### 4.1. Создайте репозиторий на GitHub

#### 4.2. Запушьте код
```bash
git init
git add .
git commit -m "Initial commit"
git branch -M main
git remote add origin <ваш-github-url>
git push -u origin main
```

#### 4.3. Внесите изменение
- Откройте любой файл (например, `README.md`)
- Добавьте комментарий или измените текст
- Сохраните

#### 4.4. Запушьте изменение
```bash
git add .
git commit -m "Test change for CI/CD"
git push origin main
```

#### 4.5. Запустите сборку в Jenkins
1. Jenkins → **bookings-pipeline** → **Build Now**
2. Нажмите на номер сборки → **Console Output**
3. Дождитесь завершения (должно быть "SUCCESS")

---

## ✅ ПРОВЕРКА

Все должно быть доступно:
- ✅ roomBooking: http://localhost:8081
- ✅ Jenkins: http://localhost:8085
- ✅ Grafana: http://localhost:3000
- ✅ Prometheus: http://localhost:9090
- ✅ RabbitMQ: http://localhost:15672

---

## 🚨 БЫСТРАЯ ПОМОЩЬ

**Проблема:** roomBooking не запускается
```bash
docker-compose logs roomBooking
cd roomBooking && .\mvnw.cmd clean package -DskipTests && cd ..
docker-compose build roomBooking && docker-compose up -d roomBooking
```

**Проблема:** Jenkins не запускается
```bash
docker-compose logs jenkins
docker-compose restart jenkins
```

**Проблема:** Pipeline не собирает
- Проверьте настройки JDK и Maven в Jenkins
- Проверьте логи в Console Output

**Полная пересборка:**
```bash
docker-compose down
docker-compose build --no-cache
docker-compose up -d
```

---

## 📋 ЧЕКЛИСТ

### Перед экзаменом:
- [ ] Все собрано локально
- [ ] `roomBooking/target/roomBooking-rest-0.0.1-SNAPSHOT.jar` существует
- [ ] Локальный тест прошел
- [ ] Архив создан

### На экзамене:
- [ ] Проект распакован и запущен
- [ ] Grafana настроена
- [ ] Jenkins настроен
- [ ] Pipeline создан
- [ ] Код в GitHub
- [ ] Сборка в Jenkins успешна

