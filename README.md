# Info

1. Проектная задача **Bank API**. Требуется реализовать вэб-сервис, реализующий логику работы клиентов
с банковскими счетами. API Физического лица (клиента) должно позволять выполнять следующие действия по счетам:  
![](https://github.com/aleksey-nsk/bank_api/blob/master/screenshots/01_task.png)  
Подробное условие задачи в файле **files/task.pdf**.

2. Использована БД **Postgres** в контейнере **Docker**. Настройки контейнера 
указываем в файле **docker-compose.yaml**:  
![](https://github.com/aleksey-nsk/bank_api/blob/master/screenshots/02_docker_compose.png)  

Настройки подключения к БД прописываем в файле **src/main/resources/application.yaml**:  
![](https://github.com/aleksey-nsk/bank_api/blob/master/screenshots/03_application.png)  

3. Для тестирования используем **in-memory базу данных H2**. Настройки **тестового профиля** 
прописываем в файле **src/test/resources/application-test.yaml**:  
![](https://github.com/aleksey-nsk/bank_api/blob/master/screenshots/04_application_test.png)  

Далее над всеми тестовыми классами пишем аннотацию **@ActiveProfiles("test")** для
активации тестового профиля.  

Тесты (**unit** и **интеграционные**) создаём в директории **src/test**:  
![](https://github.com/aleksey-nsk/bank_api/blob/master/screenshots/05_test_structure.png)    

4. Документацию к API генерируем с помощью **Swagger**. Для этого подключаем зависимости в pom-файле:  
![](https://github.com/aleksey-nsk/bank_api/blob/master/screenshots/06_swagger_dependency.png)  

Далее создаём конфигурационный файл **src/main/java/com/example/bank_api/config/SpringFoxConfig.java**:  
![](https://github.com/aleksey-nsk/bank_api/blob/master/screenshots/07_spring_fox_config.png)  

Для документирования используем аннотацию **@Api** над классами контроллеров, 
и аннотацию **@ApiOperation** над методами:  
![](https://github.com/aleksey-nsk/bank_api/blob/master/screenshots/08_swagger_annotation.png)  

Для просмотра API-документации открыть адрес **http://localhost:8082/swagger-ui/index.html**:
![](https://github.com/aleksey-nsk/bank_api/blob/master/screenshots/09_api_documentation.png)  

5. Фронтэнд






 



6. **http://localhost:8082/** - открыть приложение (фронтэнд начал делать на **AngularJS**).
7. Изначально проверка API осуществлялась с помощью **Postman**. Затем были 
написаны **интеграционные тесты** и **юнит-тесты**.
8. Для тестирования используется тестовый **профиль** со своей базой данных.

# TODO list
- Доделать оставшийся функционал по заданию.
- Исправить замечания после код-ревью. Замечания сохранил в файле **task/Замечания.rtf**.
- Фронт: счета сортировать по дате. Карты сортировать по дате.

# Замечания
Есть пара коментов:  

~~1) Зависимости лучше внедрять через конструктор в final поля. Так в будущем будет проще тестировать 
классы unit тестами, т.к все нужные зависимости можно будет передать через конструктор, 
а в случае с внедрение в поле, так не получится. Так же так нельзя будет использовать класс, 
не передав в него нужные зависимости.~~

~~2) Ошибки в ответах лучше не в контроллере формировать. Лучше кинуть исключение из сервиса, 
а на сетевом слое добавить @ExceptionHandler, в том числе на все неизвестные 
ошибки (на все RuntimeException), чтобы всегда отправлять клиенту один и тот же формат ответа.~~

3) В entity нет поля version. Нужно учитывать, что все клиентские приложения работают в многопоточной среде, 
и одна и та же сущность может меняться из разных сред. Нужно озаботиться блокировками, 
а через version Hibernate может это делать из коробки.

4) В БД на все foreign key и все поля поиска нужно добавить индексы для ускорения чтения.
