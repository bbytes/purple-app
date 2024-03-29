/*
 *  Login Controller
 */
angular.module('rootApp').controller('loginCtrl', function ($scope, $rootScope, $state, loginService, appNotifyService, $localStorage, $window, $filter) {

    $rootScope.bodyClass = 'body-standalone';
    $rootScope.feedbackClass = 'feedback-log';

    $scope.submitLoginForm = function (isValid) {

        // Validating login form
        if (!isValid) {
            appNotifyService.error('Please enter username and password');
            return false;
        }
        // Calling login service
        loginService.login($scope.username, $scope.password).then(function (response) {
            if (response.headers["x-auth-token"] && response.data.accountInitialise === true && response.data.disableState === false && response.data.markDeleteState === false) {
                $window.sessionStorage.token = response.headers["x-auth-token"];
                $rootScope.loggedStatus = true;
                $rootScope.loggedInUser = $scope.username;
                $rootScope.userRole = response.data.userRole.id;
                $rootScope.userName = response.data.userName;
                $rootScope.authToken = response.headers["x-auth-token"];
                $rootScope.timePreference = response.data.timePreference;
                $rootScope.switchState = response.data.emailNotificationState;
                $rootScope.timeZone = response.data.timeZone;
                $rootScope.viewType = response.data.viewType;

                var userInfo = {
                    accessToken: response.headers["x-auth-token"],
                    email: $rootScope.loggedInUser,
                    name: $rootScope.userName,
                    userRoles: $rootScope.userRole,
                    timePreference: $rootScope.timePreference,
                    emailNotificationState: $rootScope.switchState,
                    timeZone: $rootScope.timeZone,
                    viewType: $rootScope.viewType
                };

                $localStorage.userInfo = userInfo;
                $rootScope.showWelcomeMessage = true;

                $state.go('status');
            } else if (response.data.accountInitialise === false) {
                // Erase the token if the user fails to log in
                delete $window.sessionStorage.token;
                //Login failed. Showing error notification
                appNotifyService.error('Please activate your account before login. Check your email for activation link.');
            } else if (response.data.disableState === true) {
                // Erase the token if the user fails to log in
                delete $window.sessionStorage.token;
                //Login failed. Showing error notification
                appNotifyService.error('Currenlty you have been disabled, Please contact Admin to enable');
            } else if (response.data.markDeleteState === true) {
                // Erase the token if the user fails to log in
                delete $window.sessionStorage.token;
                //Login failed. Showing error notification
                appNotifyService.error('Your User Name has been marked for delete. Please contact Admin to revoke the same.');
            }

        }, function (error) {
            appNotifyService.error('Invalid Username or Password');
        });
    };

    $scope.logout = function () {
        delete $window.sessionStorage.token;

        $localStorage.remove('userInfo');
        loginService.logout().then(function (response) {

            $location.path("login");
        });
    };
});
