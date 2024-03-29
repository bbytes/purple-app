/*
 * Sign Up Controller
 */
angular.module('rootApp').controller('signupCtrl', function ($scope, $rootScope, $state, signupService, appNotifyService, $localStorage) {

    $rootScope.bodyClass = 'body-standalone';
    $rootScope.feedbackClass = 'feedback-log';

    $scope.enterpriseModeCheck = function () {

        signupService.enterpriseModeCheck().then(function (response) {
            $scope.isEnterPriseMode = response.data;
        });
    };

    $scope.submitSignUp = function (isValid) {
        if (!isValid) {
            appNotifyService.error('Please enter valid inputs');
            return false;
        }

        // Validating login form
        signupService.submitSignUp($scope.user).then(function (response) {
            if (response.success) {
                appNotifyService.success('An email has been sent to your registered email-id for activation. Please check!');
                $state.go('login');
            }

        }, function (error) {
            appNotifyService.error(error.msg, 'Oops!! Registration has Failed. Please try after sometime!');
        });
    };
});
