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

    // $scope.saveProduct = function () {
    //     $scope.NewProduct.category = $scope.NewCategory;
    //     console.log($scope.NewProduct);
    //     $http.post(contextPath + '/product', $scope.NewProduct)
    //             .then(function (resp) {
    //                 $scope.fillTable();
    //             });
    // };
    $scope.saveCard = function () {
        console.log("method saveCard()")
        console.log($scope.NewAccount);
        // $scope.NewCard.account = $scope.NewAccount;

        let searchNumber = $scope.NewAccount;
        console.log(searchNumber);
        //const found = x.find(entry => entry.id == 1002);
        let account = $scope.Accounts.find(entry => entry.number == 55555666667777788888);
        // console.log(account);
        console.log(account.id);

        console.log($scope.NewCard);
        let objectA = {};
        console.log(objectA);
        const url = contextPath + '/api/v1/client/' + 1 + '/account/' + account.id + '/card';
        console.log(url);
        $http.post(url, objectA)
                .then(function (resp) {
                    console.log(resp);
                    $scope.fillPage(1);
                });
    };

    $scope.deleteCard = function (cardId) {
        console.log("METHOD deleteCard()");
        console.log(cardId);
        const url = contextPath + '/api/v1/client/' + 1 + '/card/' + cardId;
        $http.delete(url)
                .then(function (resp) {
                    console.log(resp);
                    $scope.fillPage(1);
                });
    };

    // $scope.changePrice = function (product, new_price) {
    //     product.price = new_price;
    //     console.log(product)
    //     $http.put(contextPath + '/product/' + product.id, product)
    //             .then(function (resp) {
    //                 console.log(resp);
    //                 $scope.fillTable()
    //             });
    // };

    $scope.fillPage(1);

});
