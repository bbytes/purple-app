rootApp.controller('loginCtrl', function ($scope, $rootScope, $state, loginService, appNotifyService) {

    $scope.submitLoginForm = function (isValid) {

        // Validating login form
        if (!isValid) {
            appNotifyService.error('Please enter username and password', 'Invalid inputs');
            return false;
        }

        // Calling login service
        loginService.login($scope.usrName, $scope.usrPass).then(function (response) {

        }, function (error) {
            //Login failed. Showing error notification
            appNotifyService.error(error.msg, 'Login Failed.');
        });
    };
});
