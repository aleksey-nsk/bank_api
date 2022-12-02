# Выполнить команду "mvn clean package" и далее созданный jar-файл скопировать
# из папки target в папку docker/prod/services/backend
mvn clean package
cp --force target/bank_api-0.0.1-SNAPSHOT.jar docker/prod/services/backend
