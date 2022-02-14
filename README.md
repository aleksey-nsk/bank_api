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

6. Обработка исключений в Spring: создаём **пользовательские исключения**:  `ClientNotFoundException`, 
`AccountNotFoundException`, `CardNotFoundException`, `ClientDuplicateException`:  
![](https://github.com/aleksey-nsk/bank_api/blob/master/screenshots/12_exception.png)  

Для обработки исключений используем **эдвайсы**. Начиная со Spring 3.2 можно глобально и централизованно 
обрабатывать исключения с помощью класса с аннотацией **@ControllerAdvice**. При обработке исключений
@ControllerAdvice-класс имеет наибольший приоритет. Внутри данного класса создаются 
методы с аннотацией **@ExceptionHandler**, с помощью которой определяется 
обрабатываемое исключение (либо список обрабатываемых исключений).  
 
Можно в рамках эдвайса сделать сразу несколько методов с аннотациями @ExceptionHandler для обработки 
разных исключений. Все эти методы возвращают **ResponseEntity<>** с нашим собственным типом **Response**.
Таким образом возвращаем клиенту как **код ответа**, так и **тело ответа**.

7. Для автоматизации внесения обновлений в структуру БД используем **Liquibase**. Для внесения изменения 
в БД требуется добавить новый **набор изменений (changeSet)**.

8. Рекомендация: _"в БД на все foreign key и все поля поиска нужно добавить индексы для ускорения чтения"_.
Добавляем changeSet, создающий индексы.  

Затем выводим список индексов запросом:  
![](https://github.com/aleksey-nsk/bank_api/blob/master/screenshots/13_select_indexes_query.png)  

В результате видим свои индексы:  
![](https://github.com/aleksey-nsk/bank_api/blob/master/screenshots/14_select_indexes_result.png)  

Чтобы увидеть использование Postgres-ом индексов (при помощи **оператора EXPLAIN**), нужно добавить 
в таблицы много записей.
Например, если сделать в таблице **clients** 300 записей, **accounts** - 500 записей, **cards** - 500 записей,
то можно увидеть что индексы действительно используются:  
![](https://github.com/aleksey-nsk/bank_api/blob/master/screenshots/15_use_explain.png)  
![](https://github.com/aleksey-nsk/bank_api/blob/master/screenshots/16_use_explain.png)  

9. **Блокировки** - это механизм, позволяющий организовать параллельную работу с одними и теми же данными в БД.
Когда более чем одна **транзакция** пытается получить доступ к одним и тем же данным в одно и то же время,
в дело вступают блокировки, которые гарантируют, что только одна из этих транзакций изменит данные.

Существует два основных подхода к **блокированию транзакций**: **оптимистичный** и **пессимистичный**.
Оптимистичное блокирование выполнено на уровне Hibernate, а не базы данных. Для поддержки таких блокировок 
в класс вводится специально поле версии (поле с аннотацией **@Version**), которое анализирует Hibernate 
при сохранении изменений.

Во все entity-классы добавил поле version. Создал liquibase-миграцию, добавляющую столбец version в таблицы.

**В итоге не работает! Столбик version имеет значение 0, и не увеличивается!**
(https://stackoverflow.com/questions/71093826/optimistic-locking-in-hibernate-does-not-change-the-value-of-the-version-column)
**Не смог разобраться, в чём ошибка!**

# TODO list

~~1) В entity нет поля version. Нужно учитывать, что все клиентские приложения работают в многопоточной среде, 
и одна и та же сущность может меняться из разных сред. Нужно озаботиться блокировками, 
а через version Hibernate может это делать из коробки.~~

~~2) В БД на все foreign key и все поля поиска нужно добавить индексы для ускорения чтения~~.
