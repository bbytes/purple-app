/*
 *  Reset Controller to reset password
 */
angular.module('rootApp').controller('resetPasswordCtrl', function ($scope, $rootScope, $state, $window, settingsService, appNotifyService) {

    $window.sessionStorage.token = $state.params.token;

    $rootScope.authToken = $state.params.token;

    $scope.updatePassword = function (user) {
        if (user.newPassword !== user.confirmPassword) {
            appNotifyService.error('Password is mismatch');
            return false;
        }

        settingsService.updatePassword($scope.user).then(function (response) {
            if (response.success) {
                $scope.user = '';
                appNotifyService.success('Your password has been reset');
                $state.go('login');
            }
        }, function (error) {
            appNotifyService.error('Enter valid passwords.');
        });
    };
});
