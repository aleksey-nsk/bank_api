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

5. Фронтенд реализован в виде **SPA**: имеется единственный HTML-файл с вёрсткой, и JS-скрипт для динамической
подгрузки данных с бэкенда. Использован **AngularJS**.  

Структура фронтенда:    
![](https://github.com/aleksey-nsk/bank_api/blob/master/screenshots/10_frontend_structure.png)  

Запущенное приложение:  
![](https://github.com/aleksey-nsk/bank_api/blob/master/screenshots/11_front.png)  

6. Обработка исключений в Spring  
![](https://github.com/aleksey-nsk/bank_api/blob/master/screenshots/12_exception.png)  

Для обработки исключений используем **эдвайсы**. Начиная со Spring 3.2 можно глобально и централизованно 
обрабатывать исключения с помощью класса с аннотацией **@ControllerAdvice**. При обработке исключений
@ControllerAdvice-класс имеет наибольший приоритет. Внутри данного класса создаются 
методы с аннотацией **@ExceptionHandler**, с помощью которой определяется 
обрабатываемое исключение (либо список обрабатываемых исключений).  

Создаём **пользовательские исключения**:  `ClientNotFoundException`, `AccountNotFoundException`,
`CardNotFoundException`, `ClientDuplicateException`.  
 
Можно в рамках эдвайса сделать сразу несколько методов с аннотациями @ExceptionHandler для обработки 
разных исключений. Все эти методы возвращают **ResponseEntity<>** с нашим собственным типом **Response**.
Таким образом возвращаем клиенту как **код ответа**, так и **тело ответа**.
 
# TODO list

1) В entity нет поля version. Нужно учитывать, что все клиентские приложения работают в многопоточной среде, 
и одна и та же сущность может меняться из разных сред. Нужно озаботиться блокировками, 
а через version Hibernate может это делать из коробки.

2) В БД на все foreign key и все поля поиска нужно добавить индексы для ускорения чтения.
