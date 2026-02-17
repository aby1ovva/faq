# 🚀 Быстрый запуск проекта FAQ

## Требования
- Java 17+
- Maven 3.6+
- PostgreSQL 14+

---

## Запуск за 5 шагов

### 1. Создать базу данных

```bash
# Войти в PostgreSQL
psql -U postgres

# В psql выполнить:
CREATE DATABASE faq_db;
\c faq_db;
\i /путь/к/проекту/faq/src/main/resources/DB.sql
\q
```

---

### 2. Настроить пароль БД

Откройте `faq/src/main/resources/application.properties`:

```properties
spring.datasource.password=ВАШ_ПАРОЛЬ  # ← Измените на свой пароль
```

---

### 3. Запустить Backend

```bash
cd faq
mvn clean install
mvn spring-boot:run
```

**Проверка:** Откройте http://localhost:8080/api/faqs - должен показать JSON

---

### 4. Запустить Frontend

**Вариант А (рекомендуется):**
- Откройте VS Code
- Установите расширение **Live Server**
- Правой кнопкой на `faqFrontend/index.html` → **Open with Live Server**

**Вариант Б (простой):**
- Двойной клик на `faqFrontend/index.html`

---

### 5. Готово! ✅

Frontend откроется в браузере автоматически.

---

## Возможные проблемы

### Backend не запускается
```bash
# Проверьте, что PostgreSQL запущен
# macOS: brew services start postgresql@14
# Linux: sudo systemctl start postgresql
```

### CORS error
Проверьте, что в `FAQController.java` есть:
```java
@CrossOrigin(origins = "*")
```

### Port 8080 занят
Измените в `application.properties`:
```properties
server.port=8081
```

И в `faqFrontend/app.js`:
```javascript
const API_URL = 'http://localhost:8081/api/faqs';
```

---

## API Endpoints

```bash
GET    /api/faqs              # Получить все
GET    /api/faqs/{id}         # Получить один
POST   /api/faqs              # Создать
PUT    /api/faqs/{id}         # Обновить
DELETE /api/faqs/{id}         # Удалить
GET    /api/faqs?q=текст      # Поиск
```

---

## Тестирование

```bash
# Получить все FAQ
curl http://localhost:8080/api/faqs

# Создать новый FAQ
curl -X POST http://localhost:8080/api/faqs \
  -H "Content-Type: application/json" \
  -d '{"question":"Тест","answer":"Ответ","category":"general"}'
```

---

**Готово!** 🎉
