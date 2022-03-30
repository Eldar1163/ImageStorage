# ImageStorage
Это Spring Boot приложение, реализующее Rest API сервиса хранения изображений для списка задач
### Оно позволяет:
- Выгружать, скачивать, удалять изображения

### Приложение использует следующий стек технологий:
- Spring Boot (Web, JDBC Template)
- Flyway
- Lombok
- СУБД PostgreSQL
- Docker

### О приложении
**ImageStorage** позволяет хранить по одному изображению на каждую задачу списка задач. Предполагается взаимодействие этого приложения с **[simple_todo](https://github.com/Eldar1163/simple_todo)**, но пока это взаимодействие не реализовано и **ImageStorage** развивается изолированно.
### Запуск приложения
Перед первым запуском необходимо создать Docker-контейнер , содержащий СУБД и изменить директорию для хранения изображений в **application.properties** приложения (ImageStorage/src/main/resources/application.properties) параметр **image.path**. Для создания контейнера с СУБД, в корне проекта находится файл **docker-compose.yaml**. Когда контейнер работает, можно запускать само приложение. После запуска, оно становится доступным по адресу: http://localhost:8080
### API
**/api/image**

- `GET`: Скачать изображение
- `POST`: Выгрузить изображение
- `DELETE`: Удалить изображение

## Примеры использования API
В примерах запросы отправляются через **cURL**.
Для выпонения любого запроса обязательно требуется указать идентификатор задачи **<task_id>**, предстваляющий собой положительное целое число. Для загрузки изображения требуется указать путь до него **<ПУТЬ_ДО_ВАШЕГО_ИЗОБРАЖЕНИЯ>**.
### Выгрузка изображения
Для выгрузки изображения его необходимо передать в данных формы в ключе **image**
```
curl --location --request POST 'localhost:8080/api/image?taskid=<task_id>' \
--form 'image=@"<ПУТЬ_ДО_ВАШЕГО_ИЗОБРАЖЕНИЯ>"'
```
### Загрузка изображения
Вы можете загрузить изображение, используя следующий запрос
```
curl --location --request GET 'localhost:8080/api/image?taskid=<task_id>
```

### Удаление изображения
Вы можете удалить изображение, использую следующий запрос
```
curl --location --request DELETE 'localhost:8080/api/image?taskid=<task_id>'
```
