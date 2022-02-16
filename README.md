# Info

1. Проектная задача **Bank API**. Требуется реализовать вэб-сервис, реализующий логику работы клиентов
с банковскими счетами. API Физического лица (клиента) должно позволять выполнять следующие действия по счетам:  
![](https://github.com/aleksey-nsk/bank_api/blob/master/screenshots/01_task.png)  
Подробное условие задачи в файле files/**task.pdf**.

2. Использована БД **Postgres** в контейнере **Docker**. Настройки контейнера 
указываем в файле **docker-compose.yaml**:  
![](https://github.com/aleksey-nsk/bank_api/blob/master/screenshots/02_docker_compose.png)  

Настройки подключения к БД прописываем в файле src/main/resources/**application-dev.yaml**:  
![](https://github.com/aleksey-nsk/bank_api/blob/master/screenshots/03_application-dev.png)  

3. Для тестирования используем **in-memory базу данных H2**. Настройки **тестового профиля** 
прописываем в файле src/test/resources/**application-test.yaml**:  
![](https://github.com/aleksey-nsk/bank_api/blob/master/screenshots/04_application_test.png)  

Далее над всеми тестовыми классами пишем аннотацию **@ActiveProfiles("test")** для
активации тестового профиля.  

Тесты (**unit** и **интеграционные**) создаём в директории **src/test**:  
![](https://github.com/aleksey-nsk/bank_api/blob/master/screenshots/05_test_structure.png)    

4. Документацию к API генерируем с помощью **Swagger**. Для этого подключаем зависимости в pom-файле:  
![](https://github.com/aleksey-nsk/bank_api/blob/master/screenshots/06_swagger_dependency.png)  

Далее создаём конфигурационный файл src/main/java/com/example/bank_api/config/**SpringFoxConfig.java**:  
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
Добавляем миграцию, создающую индексы (src/main/resources/db/changelog/**04_create_indexes.xml**).  

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

# Как поднять приложение в Docker-контейнере

1. Данное приложение состоит из 3 компонентов, которые зависят друг от друга, но могут быть 
запущены изолированно на разных машинах. Поэтому надо поднять 3 контейнера:

       Бекэнд (Spring Boot REST API на встроенном Tomcat-сервере)
       Базу данных PostgreSQL
       AngularJS-фронтэнд на сервере Nginx

2. Сначала надо создать три профиля (**dev**, **test**, **prod**) и 
файл **application.yaml**, в котором будем задавать активный профиль:  
![](https://github.com/aleksey-nsk/bank_api/blob/master/screenshots/17_18_three_profiles_and_active.png)  

Профили dev и test к этому моменту уже были. Поэтому создал профиль prod, в котором порт и 
настройки подключения к БД отличаются от настроек dev-профиля:  
![](https://github.com/aleksey-nsk/bank_api/blob/master/screenshots/19_prod_profile.png)  

3. Далее создать конфигурационный файл src/main/java/com/example/bank_api/config/**CorsConfig.java**
для настройки **CORS**:  
![](https://github.com/aleksey-nsk/bank_api/blob/master/screenshots/20_cors_config.png)  

4. В корне проекта создать папку **prod** с такой структурой:  
![](https://github.com/aleksey-nsk/bank_api/blob/master/screenshots/21_prod_structure.png)  

5. Файлы для фронтэнда **index.html** и **index.js** скопировать в 
директорию **prod/services/frontend/html**, и при этом поменять `contextPath`
в js-файле:  
![](https://github.com/aleksey-nsk/bank_api/blob/master/screenshots/22_index_js_context_path.png)  

6. Выполнить команду `mvn clean package` и далее созданный jar-файл скопировать из папки **target**
в папку **prod/services/backend**.

7. Файл prod/**docker-compose.yaml** имеет вид:

       version: '3.0'
       
       services:
       
           # База данных PostgreSQL
           database:
               build:
                   context: services/database
                   dockerfile: Dockerfile
               container_name: 'bank_prod_db'
               ports:
                   - 15433:5432 # для доступа с хост-машины
               environment:
                   - 'POSTGRES_DB=bank_prod'
                   - 'POSTGRES_USER=admin_prod'
                   - 'POSTGRES_PASSWORD=987'
               volumes:
                   - db-data:/var/lib/postgresql/data
       
           # Бекэнд (Spring Boot REST API на встроенном Tomcat-сервере)
           backend:
               build:
                   context: services/backend
                   dockerfile: Dockerfile
               command: java -jar ./back.jar
               environment:
                   - 'PORT=8083'
                   - 'SPRING_DATASOURCE_URL=jdbc:postgresql://database:5432/bank_prod'
                   - 'JS_URL=http://localhost:8080'
               image: 'image_back'
               container_name: 'bank_prod_back'
               ports:
                   - 8083:8083
               depends_on:
                   - database
       
           # Фронтэнд на JavaScript на Nginx-сервере
           frontend:
               build:
                   context: services/frontend
                   dockerfile: Dockerfile
               image: 'image_front'
               container_name: 'bank_prod_front'
               ports:
                   - 8080:80
       
       # Создать volume с именем db-data
       volumes:
           db-data:

Для каждого сервиса указан свой **Dockerfile (файл для сборки образа)**.

8. **Сервис database** (база данных PostgreSQL).  

Создаётся volume с именем db-data отдельной командой в конце файла:

    # Создать volume с именем db-data  
    volumes:
        db-data:
    
Содержимое Dockerfile для сервиса database:  
![](https://github.com/aleksey-nsk/bank_api/blob/master/screenshots/23_dockerfile_for_db.png)  
т.е. просто берётся готовый образ **postgres:13**.

Порты можно было и не выставлять, ведь к базе будем подключаться не мы с хост-машины,
а Spring Boot из соседнего контейнера. А проброс портов нужен именно для доступа с хост-машины:

    ports:
        - 15433:5432 # для доступа с хост-машины

9. **Сервис backend** (Spring Boot REST API на встроенном Tomcat-сервере).  

Директива ports объявляет проброс портов:

    ports:
        - 8083:8083

т.е. порту 8083 контейнера соответствует порт 8083 хост-машины. На указанном порту будет запущен http-сервер. 

Указывается зависимость:

    depends_on:
        - database
    
т.е. данный сервис зависит от сервиса database. Это означает, что сначала запускается сервис database,
а потом сервис backend.

Задаётся имя образа:

    image: 'image_back'
          
В **environment** перечисляются **переменные среды**, к которым Spring Boot приложение имеет доступ.
Мы их прописываем в файле **application-prod.yml**.

**Доступ из одного контейнера к другому происходит по имени сервиса**. То есть к базе данных 
мы обращаемся не по localhost, а по database:

    - 'SPRING_DATASOURCE_URL=jdbc:postgresql://database:5432/bank_prod'

и это же значение мы указывали в файле **application-prod.yml**.

Содержимое Dockerfile для сервиса backend:  
![](https://github.com/aleksey-nsk/bank_api/blob/master/screenshots/24_dockerfile_for_back.png)  

тут мы собираем образ на основе готового образа **java:8-jre**. Затем задаём рабочую папку
и копируем в неё наше приложение **bank_api-0.0.1-SNAPSHOT.jar** под именем **back.jar**.
А потом запускаем приложение с помощью команды `java -jar ./back.jar`

10. **Сервис frontend** (фронтэнд на JavaScript на Nginx-сервере).  

Тут всё аналогично, только заданы другие порты и другое имя образа.

Докер-файл у данного сервиса такой:  
![](https://github.com/aleksey-nsk/bank_api/blob/master/screenshots/25_dockerfile_for_front.png)  
т.е. мы собираем образ на основе готового образа **nginx**. Затем копируем в папку 
сервера Nginx **/usr/share/nginx/html** файлы из папки **html**, которая лежит рядом с докер-файлом
и содержит html и js-файлы.

11. Есть машина, на которой установлены **Docker** и утилита **docker-compose**.
Копируем на эту машину всю папку **prod** и открываем её в терминале. Далее выполняем
команду `docker-compose up --build` и видим, что скачиваются нужные образы и создаются контейнеры.

Далее в терминале видим, что Spring Boot приложение запущено:  
![](https://github.com/aleksey-nsk/bank_api/blob/master/screenshots/26_app_start_in_container.png)  

В итоге в терминале можно увидеть все образы, контейнеры и тома:  
![](https://github.com/aleksey-nsk/bank_api/blob/master/screenshots/27_image_container_volume.png)  

Можно подключиться к БД с хост-машины:  
![](https://github.com/aleksey-nsk/bank_api/blob/master/screenshots/28_connect_to_db_in_prod.png)  

Далее проверим, что наше приложение доступно в браузере по адресу `http://localhost:8080/`, а по 
адресу `http://localhost:8083/swagger-ui/index.html` открывается **API-документация**.

Чтобы остановить и удалить контейнеры, нужно выполнить команду `docker-compose down`. В результате видим:  
![](https://github.com/aleksey-nsk/bank_api/blob/master/screenshots/29_delete_containers.png)    

Если нужно удалить и **том (volume) с данными**, выполнить команду `docker-compose down --volume`:   
![](https://github.com/aleksey-nsk/bank_api/blob/master/screenshots/30_delete_volume.png)  
при этом если были созданы новые карты в приложении, то после этой команды 
при следующем запуске приложения их не будет.

# Использованные источники

- [Spring Profiles](https://sysout.ru/spring-profiles/)
- [Использование профилей Spring](https://doc.cuba-platform.com/manual-latest-ru/spring_profiles.html)
- [Настройка CORS в Spring Security](https://sysout.ru/nastrojka-cors-v-spring-security/)
- [Spring на практике: 2 способа настроить CORS в Spring Boot](https://www.youtube.com/watch?v=XVfss_ttn_c)
- [Разработка под Docker. Локальное окружение](https://habr.com/ru/post/459972/)
- [Spring Boot + PostgreSQL + JS в Doсker](https://sysout.ru/spring-boot-postgresql-js-v-dosker/)
