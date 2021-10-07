// $scope - хранит всё, до чего можно достучаться в html-е;
// $http - через неё можно делать запросы на бэкенд
angular.module('app', []).controller('indexController', function ($scope, $http) {

    const contextPath = 'http://localhost:8082';
    console.log(contextPath); // лог в консоль браузера (F12 -> Console)

    // Получить все счета клиента
    $scope.getAccounts = function (clientId) {
        const url = contextPath + '/api/v1/client/' + clientId + '/account';
        $http.get(url)
                .then(function (resp) {
                    $scope.Accounts = resp.data;
                });
    };

    // Получить данные для заполнения страницы
    $scope.fillPage = function (clientId) {
        $scope.getAccounts(clientId);
    };

    // Внести деньги на счёт
    $scope.addBalance = function (cardNumber, add) {
        const url = contextPath + '/api/v1/client/' + 1 + '/account/card?number=' + cardNumber + '&add=' + add;
        console.log(url);
        $http.put(url)
                .then(function (resp) {
                    $scope.fillPage(1);
                });
    };

    // Удалить карту
    $scope.deleteCard = function (cardId) {
        const url = contextPath + '/api/v1/client/' + 1 + '/card/' + cardId;
        console.log(url);
        $http.delete(url)
                .then(function (resp) {
                    $scope.fillPage(1);
                });
    };

    // Добавить карту по счёту
    $scope.saveCard = function () {
        const account = $scope.Accounts.find(entry => entry.number === $scope.accountNumberSelected);
        const url = contextPath + '/api/v1/client/' + 1 + '/account/' + account.id + '/card';
        console.log(url);
        $http.post(url)
                .then(function (resp) {
                    $scope.fillPage(1);
                });
    };

    $scope.fillPage(1);

});
