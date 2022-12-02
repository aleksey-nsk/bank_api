# Info

1. Проектная задача **Bank API**. Требуется реализовать вэб-сервис, реализующий логику работы клиентов
с банковскими счетами. API Физического лица (клиента) должно позволять выполнять следующие действия по счетам:  
![](https://github.com/aleksey-nsk/bank_api/blob/master/screenshots/01_task.png)  
Подробное условие задачи в файле files/**task.pdf**.

2. Использована БД **Postgres** в контейнере **Docker**. Настройки контейнера указываем  
в файле docker/dev/**docker-compose.yaml**:  
![](https://github.com/aleksey-nsk/bank_api/blob/master/screenshots/02_docker_compose.png)  

Настройки подключения к БД прописываем в файле src/main/resources/**application-dev.yaml**:  
![](https://github.com/aleksey-nsk/bank_api/blob/master/screenshots/03_application-dev.png)  

3. Для тестирования используем **in-memory базу данных H2**. Настройки **тестового профиля** 
прописываем в файле src/test/resources/**application-test.yaml**:  
![](https://github.com/aleksey-nsk/bank_api/blob/master/screenshots/04_application_test.png)  

Далее над всеми тестовыми классами пишем аннотацию **@ActiveProfiles("test")** для
активации тестового профиля.  

Тесты (**unit** и **интеграционные**) создаём в директории **src/test/java**:  
![](https://github.com/aleksey-nsk/bank_api/blob/master/screenshots/05_test_structure.png)    

4. Документацию к API генерируем с помощью **Swagger**. Для этого подключаем зависимости  
в pom-файле:  
![](https://github.com/aleksey-nsk/bank_api/blob/master/screenshots/06_swagger_dependency.png)  

Далее создаём конфигурационный файл  
src/main/java/com/example/bank_api/config/**SpringFoxConfig.java**:  
![](https://github.com/aleksey-nsk/bank_api/blob/master/screenshots/07_spring_fox_config.png)  

Для документирования используем аннотацию **@Api** над классами контроллеров, 
и аннотацию **@ApiOperation** над методами:  
![](https://github.com/aleksey-nsk/bank_api/blob/master/screenshots/08_swagger_annotation.png)  

Для просмотра **API-документации** открыть адрес **http://localhost:8082/swagger-ui/index.html**:
![](https://github.com/aleksey-nsk/bank_api/blob/master/screenshots/09_api_documentation.png)  

5. Фронтенд реализован в виде **SPA**: имеется единственный HTML-файл с вёрсткой, и JS-скрипт для динамической
подгрузки данных с бэкенда. Использован **AngularJS**.  

Структура фронтенда:    
![](https://github.com/aleksey-nsk/bank_api/blob/master/screenshots/10_frontend_structure.png)  

Запущенное приложение:  
![](https://github.com/aleksey-nsk/bank_api/blob/master/screenshots/11_front.png)  

6. Обработка исключений в Spring: создаём **пользовательские исключения**:  `ClientNotFoundException`, 
`AccountNotFoundException`, `CardNotFoundException`, `ClientDuplicateException`:  
![](https://github.com/aleksey-nsk/bank_api/blob/master/screenshots/12_1_exception.png)  

Для обработки исключений используем **эдвайсы**. Начиная со Spring 3.2 можно глобально и централизованно 
обрабатывать исключения с помощью класса с аннотацией **@ControllerAdvice**. При обработке исключений
@ControllerAdvice-класс имеет наибольший приоритет. Внутри данного класса создаются 
методы с аннотацией **@ExceptionHandler**, с помощью которой определяется 
обрабатываемое исключение (либо список обрабатываемых исключений).  
 
Можно в рамках эдвайса сделать сразу несколько методов с аннотациями @ExceptionHandler для обработки 
разных исключений. Все эти методы возвращают **ResponseEntity<>** с нашим собственным типом **Response**.
Таким образом возвращаем клиенту как **код ответа**, так и **тело ответа**.

7. Для автоматизации внесения обновлений в структуру БД используем **Liquibase**. Для внесения изменения 
в БД требуется добавить новый **набор изменений (changeSet)**. Структура файлов для Liquibase:  
![](https://github.com/aleksey-nsk/bank_api/blob/master/screenshots/12_2_liquibase.png)  

8. **Индекс** — объект базы данных, создаваемый с целью повышения производительности поиска данных.
Рекомендация: _"в БД на все foreign key и все поля поиска нужно добавить индексы для ускорения чтения"_.
Добавляем **миграцию**, создающую индексы (src/main/resources/db/changelog/**04_create_indexes.xml**).  

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

# Как поднять приложение в Docker-контейнерах

1. Данное приложение состоит из 3 компонентов, которые зависят друг от друга, но могут быть 
запущены изолированно на разных машинах. Поэтому надо поднять 3 контейнера:

       Бекэнд (Spring Boot REST API на встроенном Tomcat-сервере)
       Базу данных PostgreSQL
       AngularJS-фронтэнд на сервере Nginx

2. Сначала надо создать три профиля (**dev**, **test**, **prod**) и 
файл **application.yaml**, в котором будем задавать активный профиль. Указать профиль prod:  
![](https://github.com/aleksey-nsk/bank_api/blob/master/screenshots/17_18_three_profiles_and_active.png)  

Профили dev и test к этому моменту уже были. Поэтому создал профиль prod, в котором порт и 
настройки подключения к БД отличаются от настроек dev-профиля:  
![](https://github.com/aleksey-nsk/bank_api/blob/master/screenshots/19_prod_profile.png)  

3. Далее создать конфигурационный файл src/main/java/com/example/bank_api/config/**CorsConfig.java**
для настройки **CORS**:  
![](https://github.com/aleksey-nsk/bank_api/blob/master/screenshots/20_cors_config.png)  

4. Создать папку **prod** с такой структурой:  
![](https://github.com/aleksey-nsk/bank_api/blob/master/screenshots/21_prod_structure.png)  

5. Файлы для фронтэнда скопировать в директорию  
**docker/prod/services/frontend/html**, и при этом поменять `apiPath` в js-файле:  
![](https://github.com/aleksey-nsk/bank_api/blob/master/screenshots/22_1_index_js_context_path.png)  

6. Необходимо выполнить команду `mvn clean package` и далее созданный jar-файл скопировать из папки **target**
в папку **docker/prod/services/backend**. Чтобы не делать это каждый раз вручную, создал скрипт **build_and_copy.sh**
в корне проекта:   
![](https://github.com/aleksey-nsk/bank_api/blob/master/screenshots/22_2_shell.png)     

7. Файл docker/prod/**docker-compose.yaml** имеет вид:  
![](https://github.com/aleksey-nsk/bank_api/blob/master/screenshots/22_3_docker_compose.png)  
![](https://github.com/aleksey-nsk/bank_api/blob/master/screenshots/22_4_docker_compose.png)  

Для каждого сервиса указан свой **Dockerfile (файл для сборки образа)**.

8. **Сервис database** (база данных PostgreSQL).  

Создаётся том (volume) с именем **pgdata**.  
В файле **docker-compose.yaml**, **volumes** может появляться в двух разных местах:

    services:
        database:
            # ...
            volumes:  # вложенный ключ. Настраивает тома для определённой службы
                # ...
    
    volumes:  # ключ верхнего уровня. Объявляет тома, на которые можно ссылаться из нескольких сервисов
        # ...    
    
Содержимое Dockerfile для сервиса database:  
![](https://github.com/aleksey-nsk/bank_api/blob/master/screenshots/23_dockerfile_for_db.png)  
т.е. просто берётся готовый образ **postgres:13**.

9. **Сервис backend** (Spring Boot REST API на встроенном Tomcat-сервере).  

Указывается зависимость:

    depends_on:
        - database  # контроль порядка запуска контейнеров
    
т.е. данный сервис зависит от сервиса database. Это означает, что сначала запускается сервис database,
а потом сервис backend.

Содержимое Dockerfile для сервиса backend:  
![](https://github.com/aleksey-nsk/bank_api/blob/master/screenshots/24_dockerfile_for_back.png)  

тут мы собираем образ на основе готового образа **openjdk:8-jre**. Затем задаём рабочую папку
и копируем в неё наше приложение **bank_api-0.0.1-SNAPSHOT.jar** под именем **back.jar**.
А потом запускаем приложение с помощью команды `java -jar ./back.jar`

10. **Сервис frontend** (фронтэнд на JavaScript на Nginx-сервере).  

Тут всё аналогично.

Докер-файл у данного сервиса такой:  
![](https://github.com/aleksey-nsk/bank_api/blob/master/screenshots/25_dockerfile_for_front.png)  
т.е. мы собираем образ на основе готового образа **nginx**. Затем копируем в папку 
сервера Nginx **/usr/share/nginx/html** файлы из папки **html**, которая лежит рядом
с докер-файлом и содержит файлы фронтенда.  

11. Есть машина, на которой установлены **Docker** и утилита **docker-compose**.
Копируем на эту машину всю папку **prod** и открываем её в терминале. Далее выполняем
команду `docker-compose up --build` и видим, что происходит развёртывание программы.
Сначала билдятся сами образы, потом от них запускаются контейнеры.
- `docker-compose up` - команда на запуск файла docker-compose.yaml
- `--build` - при каждом запуске билдить образы заново, и далее запускать от них контейнеры
- `docker-compose -f test.yaml up --build -d` - полная команда выглядит так

В итоге в терминале видим, что Spring Boot приложение запущено:  
![](https://github.com/aleksey-nsk/bank_api/blob/master/screenshots/26_app_start_in_container.png)  

Можно посмотреть все образы, контейнеры и тома:  
![](https://github.com/aleksey-nsk/bank_api/blob/master/screenshots/27_image_container_volume.png)  

Можно подключиться к БД с хост-машины:  
![](https://github.com/aleksey-nsk/bank_api/blob/master/screenshots/28_connect_to_db_in_prod.png)  

Далее проверим, что наше приложение доступно в браузере по адресу **http://localhost/**  
а по адресу **http://localhost:8083/swagger-ui/index.html** открывается **API-документация**.

Чтобы **остановить и удалить контейнеры**, нужно выполнить команду  
`docker-compose down`  
В результате видим:  
![](https://github.com/aleksey-nsk/bank_api/blob/master/screenshots/29_delete_containers.png)    

Если нужно **удалить контейнеры и том (volume) с данными**, выполнить команду  
`docker-compose down --volume`     
![](https://github.com/aleksey-nsk/bank_api/blob/master/screenshots/30_delete_volume.png)  
**при этом если были созданы новые карты в приложении, то после этой команды 
при следующем запуске приложения их не будет**.  

Если нужно **удалить контейнеры, тома и образы**, выполнить команду  
`docker-compose down --rmi all --volume`  
![](https://github.com/aleksey-nsk/bank_api/blob/master/screenshots/31_delete_all.png)  

12. **Использованные источники**:
- [Spring Profiles](https://sysout.ru/spring-profiles/)
- [Использование профилей Spring](https://doc.cuba-platform.com/manual-latest-ru/spring_profiles.html)
- [Настройка CORS в Spring Security](https://sysout.ru/nastrojka-cors-v-spring-security/)
- [Spring на практике: 2 способа настроить CORS в Spring Boot](https://www.youtube.com/watch?v=XVfss_ttn_c)
- [Разработка под Docker. Локальное окружение](https://habr.com/ru/post/459972/)
- [Spring Boot + PostgreSQL + JS в Doсker](https://sysout.ru/spring-boot-postgresql-js-v-dosker/)

