/*
 *  Activate Account Controller
 */
angular.module('rootApp').controller('activateAccountCtrl', function ($rootScope, $state, $q, $http, $window, $sessionStorage, appNotifyService) {

    $window.sessionStorage.token = $state.params.token;
    $rootScope.authToken = $state.params.token;

    var deferred = $q.defer();

    $http({
        method: 'GET',
        url: $rootScope.baseUrl + 'api/v1/admin/activateAccount',
        headers: {
            'Content-Type': 'application/json'
        }
    }).success(function (response) {

        $rootScope.userRole = response.data.userRole.id;
        $rootScope.userName = response.data.userName;

        var userInfo = {
            name: $rootScope.userName,
            userRoles: $rootScope.userRole,
            accessToken: $rootScope.authToken
        };

        $sessionStorage.userInfo = userInfo;
        deferred.resolve(response);
        if (response.data.accountInitialise === true && ($sessionStorage.userInfo.userRoles === "NORMAL" || $sessionStorage.userInfo.userRoles === "MANAGER"))
        {
            appNotifyService.success('Your account has been activated successfully. Redirecting to settings.');
            $state.go("settings");
        } else {
            appNotifyService.success('Your account has been activated successfully. Why dont you invite users?');
            $state.go("user-manager");
        }
    }).error(function (response) {
        deferred.reject(response);
        appNotifyService.error('The link is expired');
    });

    return deferred.promise;
});