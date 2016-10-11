/*
 * Forgot password controller 
 */
angular.module('rootApp').controller('forgotPasswordCtrl', function ($scope, $rootScope, $state, forgotPasswordService, appNotifyService) {

    $rootScope.bodyClass = 'body-standalone';
    $rootScope.feedbackClass = 'feedback-log';
    $scope.submitForgotPasswordForm = function (isValid) {
        if (!isValid) {
            appNotifyService.error('Please enter a valid email-id.');
            return false;
        }

        forgotPasswordService.submitForgotPassword($scope.user).then(function (response) {
            if (response.success) {
                appNotifyService.success('Reset password email has been sent. Please follow instructions to reset password.');
                $state.go('login');
            }
        }, function (error) {
            appNotifyService.error('This email id is not registered with us. Please sign-up!');
        });
    };
});
