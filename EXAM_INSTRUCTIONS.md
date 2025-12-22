# Инструкции для экзамена

## Этап 1: Подготовка и запуск

### 1. Распакуйте проект
```bash
unzip bookings.zip
cd bookings
```

### 2. Поднимите все контейнеры
```bash
docker-compose up -d
```

### 3. Дождитесь запуска всех сервисов
```bash
# Проверьте статус
docker-compose ps

# Дождитесь, пока все сервисы будут в статусе "Up" (может занять 1-2 минуты)
```

## Этап 2: Настройка Grafana

### 1. Откройте Grafana
- URL: http://localhost:3000
- Логин: `admin`
- Пароль: `admin`

### 2. Добавьте Prometheus как источник данных
1. Перейдите в **Configuration** → **Data Sources**
2. Нажмите **Add data source**
3. Выберите **Prometheus**
4. URL: `http://prometheus:9090`
5. Нажмите **Save & Test**

### 3. Создайте дашборд (опционально)
1. Перейдите в **Dashboards** → **New Dashboard**
2. Добавьте панели с метриками:
   - HTTP запросы: `http_server_requests_seconds_count`
   - JVM память: `jvm_memory_used_bytes`
   - Процессор: `process_cpu_usage`

## Этап 3: Настройка Jenkins

### 1. Откройте Jenkins
- URL: http://localhost:8085
- Дождитесь загрузки (может занять 1-2 минуты при первом запуске)

### 2. Получите initial admin password
```bash
docker-compose exec jenkins cat /var/jenkins_home/secrets/initialAdminPassword
```

### 3. Первоначальная настройка Jenkins
1. Введите initial admin password
2. Выберите **Install suggested plugins**
3. Создайте администратора:
   - Username: `admin`
   - Password: `admin` (или свой)
4. Нажмите **Save and Finish**

### 4. Настройте Jenkins для работы с проектом

#### Установите необходимые плагины:
1. Перейдите в **Manage Jenkins** → **Plugins**
2. Установите:
   - **Pipeline**
   - **Git**
   - **Docker Pipeline**
   - **Maven Integration**

#### Настройте инструменты:
1. Перейдите в **Manage Jenkins** → **Tools**
2. Настройте **JDK**:
   - Name: `JDK-17`
   - JAVA_HOME: `/usr/lib/jvm/java-17-openjdk-amd64` (или путь к Java 17 в контейнере)
3. Настройте **Maven**:
   - Name: `Maven-3.9`
   - MAVEN_HOME: `/usr/share/maven` (или путь к Maven в контейнере)

#### Создайте Pipeline Job:
1. Нажмите **New Item**
2. Введите имя: `bookings-pipeline`
3. Выберите **Pipeline**
4. Нажмите **OK**
5. В разделе **Pipeline**:
   - Definition: **Pipeline script from SCM**
   - SCM: **Git**
   - Repository URL: ваш GitHub репозиторий
   - Credentials: добавьте, если репозиторий приватный
   - Script Path: `Jenkinsfile`
6. Нажмите **Save**

## Этап 4: Работа с GitHub

### 1. Создайте репозиторий на GitHub
- Создайте новый репозиторий
- Скопируйте URL репозитория

### 2. Инициализируйте Git в проекте
```bash
cd bookings
git init
git add .
git commit -m "Initial commit"
git branch -M main
git remote add origin <ваш-github-url>
git push -u origin main
```

### 3. Внесите изменения
- Измените любой файл (например, добавьте комментарий)
- Или измените версию в pom.xml

### 4. Запушьте изменения
```bash
git add .
git commit -m "Test change for CI/CD"
git push origin main
```

## Этап 5: Запуск сборки в Jenkins

### 1. Обновите конфигурацию Pipeline (если нужно)
- Перейдите в Jenkins → **bookings-pipeline** → **Configure**
- Убедитесь, что URL репозитория правильный
- Нажмите **Save**

### 2. Запустите сборку
- Нажмите **Build Now**
- Или нажмите на проект и выберите **Build Now**

### 3. Отслеживайте прогресс
- Нажмите на номер сборки (#1, #2, и т.д.)
- Выберите **Console Output** для просмотра логов

### 4. Проверьте результат
- После успешной сборки проверьте:
  ```bash
  docker-compose ps
  ```
- Все сервисы должны быть запущены

## Полезные команды

### Проверка статуса
```bash
# Статус контейнеров
docker-compose ps

# Логи сервиса
docker-compose logs -f roomBooking

# Логи Jenkins
docker-compose logs -f jenkins
```

### Перезапуск
```bash
# Перезапуск всех сервисов
docker-compose restart

# Перезапуск конкретного сервиса
docker-compose restart roomBooking
```

### Остановка
```bash
# Остановка всех сервисов
docker-compose down

# Остановка с удалением volumes
docker-compose down -v
```

## Возможные проблемы

### Jenkins не запускается
```bash
# Проверьте логи
docker-compose logs jenkins

# Проверьте права на docker.sock
ls -la /var/run/docker.sock
```

### Pipeline не может собрать проект
- Убедитесь, что Maven и JDK настроены в Jenkins
- Проверьте, что все зависимости собраны
- Проверьте логи в Console Output

### Сервисы не запускаются после сборки
```bash
# Проверьте логи
docker-compose logs roomBooking

# Пересоберите образы
docker-compose build --no-cache roomBooking
docker-compose up -d roomBooking
```

## Чеклист для экзамена

- [ ] Проект распакован
- [ ] Все контейнеры запущены (`docker-compose ps`)
- [ ] Grafana настроена и подключена к Prometheus
- [ ] Jenkins доступен и настроен
- [ ] Pipeline создан в Jenkins
- [ ] Проект залит в GitHub
- [ ] Внесены изменения в код
- [ ] Изменения запушены в GitHub
- [ ] Сборка запущена в Jenkins
- [ ] Сборка завершилась успешно
- [ ] Все сервисы работают после сборки

