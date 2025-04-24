# Дипломная работа "Облачное хранилище"

Сервис, который предоставляет REST API для загрузки файлов и вывода уже загруженных файлов пользователя.

## Технологии
- JDK17
- Spring Boot 3.4.4
- Maven
- Docker
- Docker-compose

## Требования
- Docker
- Docker-compose
- Maven

## Использование
1. Собрать .jar
```bash
mvn clean package
```
2. Запуск контейнера
```bash
docker-compose up -d --build
```
#### Поднимается на порту: 9090
3. Для тестового подключения используются тестовые данные: \
login = "test_user"\
password = "12345"
4. Эндпоинты:
- /login POST - аутентификация
- /file POST - загрузка файла в БД
- /file GET - скачивание фала из БД
- /file PUT - изменение файла
- /file DELETE - удаление файла
- /list GET - получение списка файлов
- /logout POST 
5. Команды curl для теста приложения:
- Аутентификация
```bash
curl -X POST http://localhost:9090/login -H "Content-type: application/json" -d '{"login": "test_user", "password": "12345"}'
```
Возвращает токен {"auth-token":"CREATED_TOKEN"} \
- Записываем значение в переменную 
```bash
$TOKEN=CREATED_TOKEN
```
- Далее используем токен для взаимодействия с API, например, для получения списка файлов:
```bash
curl -X GET "http://localhost:9090/list" -H "auth-token: $TOKEN"
```
Возвращает список файлов в формате JSON.
#### Важно
- При первом запуске автоматически создаются таблицы и тестовый пользователь
- Данные БД сохраняются между перезапусками (том pg_data)
6. Остановка
```bash
docker-compose down
```