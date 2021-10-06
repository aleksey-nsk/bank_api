// $scope - хранит всё, до чего можно достучаться в html-е;
// $http - через неё можно делать запросы на наш бэкенд
angular.module('app', []).controller('indexController', function ($scope, $http) {

    const contextPath = 'http://localhost:8082';

    $scope.getCards = function (clientId) {
        const url = contextPath + '/api/v1/client/' + clientId + '/card';
        $http.get(url)
                .then(function (resp) {
                    $scope.Cards = resp.data;
                });
    };

    $scope.fillPage = function (clientId) {
        const url = contextPath + '/api/v1/client/' + clientId + '/account';
        console.log(url);

        $http.get(url)
                .then(function (resp) {
                    console.log(resp); // лог в консоль браузера (F12 - Console)
                    $scope.Accounts = resp.data;
                    $scope.getCards(clientId);
                });
    };

    $scope.fillPage(1);

});
