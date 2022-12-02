// $scope - хранит всё, до чего можно достучаться в html-е;
// $http - через неё можно делать запросы на бэкенд
angular.module('app', []).controller('indexController', function ($scope, $http) {

    const apiPath = 'http://localhost:8082';
    console.log(apiPath); // лог в консоль браузера (F12 -> Console)

    // Получить клиента по идентификатору
    $scope.getClient = function (clientId) {
        const url = apiPath + '/api/v1/client/' + clientId;
        $http.get(url)
                .then(function (resp) {
                    $scope.Client = resp.data;
                });
    };

    // Получить все счета клиента
    $scope.getAccounts = function (clientId) {
        const url = apiPath + '/api/v1/client/' + clientId + '/account';
        $http.get(url)
                .then(function (resp) {
                    $scope.Accounts = resp.data;
                });
    };

    // Получить данные для заполнения страницы
    $scope.fillPage = function (clientId) {
        $scope.getClient(clientId);
        $scope.getAccounts(clientId);
    };

    // Внести деньги на счёт
    $scope.addBalance = function (clientId, cardNumber, add) {
        const url = apiPath + '/api/v1/client/' + clientId + '/account/card?cardNumber=' + cardNumber + '&add=' + add;
        console.log(url);
        $http.put(url)
                .then(function (resp) {
                    $scope.fillPage(clientId);
                });
    };

    // Удалить карту
    $scope.deleteCard = function (clientId, cardId) {
        const url = apiPath + '/api/v1/client/' + clientId + '/card/' + cardId;
        console.log(url);
        $http.delete(url)
                .then(function (resp) {
                    $scope.fillPage(clientId);
                });
    };

    // Добавить карту по счёту
    $scope.saveCard = function (clientId) {
        const account = $scope.Accounts.find(entry => entry.number === $scope.accountNumberSelected);
        const url = apiPath + '/api/v1/client/' + clientId + '/account/' + account.id + '/card';
        console.log(url);
        $http.post(url)
                .then(function (resp) {
                    $scope.fillPage(clientId);
                });
    };

    const CLIENT_ID = 1;
    $scope.fillPage(CLIENT_ID);

});
