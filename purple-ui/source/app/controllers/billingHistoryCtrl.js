/*
 * Billing History controller
 */
angular.module('rootApp').controller('billingHistoryCtrl', function ($scope, $rootScope, $location, $state, toaster, billingInfoService, appNotifyService, $uibModal) {
    
    $scope.email = $scope.loggedInUser;
    $rootScope.navClass = 'nav navbar-nav';
    $rootScope.navstatusClass = 'right-nav-ct';
    $rootScope.bodyClass = 'body-standalone1';
    $scope.showpage = false;


    /*load Due amount and billing history */
    $scope.init = function () {
        $scope.plutusLogin = false;//if plutus is not login $scope.plutusLogin is false
        billingInfoService.getCurrentDue().then(function (response) {
            if (response.success) {
                $scope.plutusLogin = true;
                $scope.currentDueDetails = response.data;

            }
        });

        billingInfoService.getBillHistory().then(function (response) {
            if (response.success) {
                $scope.billHistory = response.data;
            }
        });
    }
});
