/*
 * Logout Controller
 */
rootApp.controller('logoutCtrl', function ($scope, $rootScope, $state, logoutService, $location, $localStorage, $window) {

    $scope.logout = function () {
        delete $window.sessionStorage.token;
        delete $rootScope.statusDateFromLink;
        delete $rootScope.authToken;
        $localStorage.$reset();
        $state.go("login");
        location.reload();
    };
});
