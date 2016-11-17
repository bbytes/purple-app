/*
 * Billing Info controller
 */
angular.module('rootApp').controller('billingInfoCtrl', function ($scope, $rootScope, $location, $state, toaster, billingInfoService, appNotifyService, $uibModal) {

    $scope.isActive = function (route) {
        return route === $location.path();
    };
    $scope.email = $scope.loggedInUser;
    $rootScope.navClass = 'nav navbar-nav';
    $rootScope.navstatusClass = 'right-nav-ct';
    $rootScope.bodyClass = 'body-standalone1';
    $scope.showpage = false;
    // variable to store information of all project list
    $scope.allprojects;
    //when page is loading, showing sort by username
    $scope.sortKey = 'projectName';
    
//  Method to save billing information
    $scope.addBillingInfo = function (isValid, customer) {
        // Validating login form
        if (!isValid) {
            toaster.pop({type: 'error', body: 'Please valid inputs.', toasterId: 1});
            return false;
        }
        $scope.variables = {
            "contactNo": customer.contactNo,
            "email": $scope.loggedInUser,
            "name": $scope.userName,
            "website": customer.website,
            "billingAddress": customer.billingAddress
        };

        billingInfoService.addBillingDetails($scope.variables).then(function (response) {
            if (response.success) {
                appNotifyService.error('Your details are saved');
                $scope.billngDetails = response.data;
                customer.website = [];
                customer.billingAddress = [];
                customer.contactNo = [];
            }
        });
    };

//  Method to get all plans
    $scope.getPricingPlans = function () {
        billingInfoService.getPricingPlans().then(function (response) {
            if (response.success) {
                $scope.pricingPlans = response.data;

            }
        });

        /*To get Current Plan */

        billingInfoService.getOnlyCurrentPlan().then(function (response) {
            if (response.success) {
                $scope.currentPlan = response.data;

            }
        });
    };

//  Method to get all invoice details
    $scope.getInvoiceDetails = function () {
        billingInfoService.getInvoiceDetails().then(function (response) {
            if (response.success) {
                $scope.pricingPlans = response.data;

            }
        });
    };

});
