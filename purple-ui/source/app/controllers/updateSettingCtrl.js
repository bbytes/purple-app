/*
 *  Update Status Controller
 */
angular.module('rootApp').controller('updateSettingCtrl', function ($rootScope, $state, $q, $http, $window, $sessionStorage, appNotifyService) {

    $window.sessionStorage.token = $state.params.token;

    $rootScope.authToken = $state.params.token;

    var deferred = $q.defer();

    $http({
        method: 'GET',
        url: $rootScope.baseUrl + 'api/v1/currentUser',
        headers: {
            'Content-Type': 'application/json'
        }
    }).success(function (response, status, headers, config) {

        $rootScope.userName = response.data.userName;
        $rootScope.userRole = response.data.userRole.id;
        $rootScope.timePreference = response.data.timePreference;
        $rootScope.switchState = response.data.emailNotificationState;
        $rootScope.timeZone = response.data.timeZone;

        var userInfo = {
            accessToken: $rootScope.authToken,
            email: $rootScope.loggedInUser,
            name: $rootScope.userName,
            userRoles: $rootScope.userRole,
            timePreference: $rootScope.timePreference,
            timeZone: $rootScope.timeZone,
            emailNotificationState: $rootScope.switchState
        };

        $sessionStorage.userInfo = userInfo;
        $state.go("settings");
        deferred.resolve(response);
    }).error(function (response) {
        deferred.reject(response);
        appNotifyService.error('The link is expired');
    });

    return deferred.promise;
});