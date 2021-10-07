// $scope - хранит всё, до чего можно достучаться в html-е;
// $http - через неё можно делать запросы на бэкенд
angular.module('app', []).controller('indexController', function ($scope, $http) {

    const contextPath = 'http://localhost:8082';
    console.log(contextPath); // лог в консоль браузера (F12 - Console)

    $scope.getAccounts = function (clientId) {
        const url = contextPath + '/api/v1/client/' + clientId + '/account';
        console.log(url);
        $http.get(url)
                .then(function (resp) {
                    $scope.Accounts = resp.data;
                    console.log($scope.Accounts);
                });
    };

    // $scope.getCards = function (clientId) {
    //     const url = contextPath + '/api/v1/client/' + clientId + '/card';
    //     console.log(url);
    //     $http.get(url)
    //             .then(function (resp) {
    //                 $scope.Cards = resp.data;
    //                 console.log($scope.Cards);
    //             });
    // };

    $scope.fillPage = function (clientId) {
        console.log("Получить данные для заполнения страницы")
        $scope.getAccounts(clientId);
        // $scope.getCards(clientId);
    };

    $scope.fillPage(1);

});
