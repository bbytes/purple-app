angular.module('rootApp').service('billingInfoService', function ($rootScope, $http, $q) {
    
    

    this.addBillingDetails = function (customer) {

        var deferred = $q.defer();

        $http({
            method: 'POST',
            url: $rootScope.baseUrl + 'api/v1/billing/billingInfo',
            data:customer,
            headers: {
                'Content-Type': 'application/json'
            }
        }).success(function (response, status, headers, config) {
            deferred.resolve(response);
        }).error(function () {
            deferred.reject({'success': false, 'msg': 'Oops! Something went wrong. Please try again later.'});
        });

        return deferred.promise;
    };
    
    this.getPricingPlans = function () {

        var deferred = $q.defer();

        $http({
            method: 'GET',
            url: $rootScope.baseUrl + 'api/v1/billing/pricingPlans',
            headers: {
                'Content-Type': 'application/json'
            }
        }).success(function (response, status, headers, config) {
            deferred.resolve(response);
        }).error(function () {
            deferred.reject({'success': false, 'msg': 'Oops! Something went wrong. Please try again later.'});
        });

        return deferred.promise;
    };
    //getting current due
    this.getCurrentDue = function () {

        var deferred = $q.defer();

        $http({
            method: 'GET',
            url: $rootScope.baseUrl + 'api/v1/billing/currentDue',
            headers: {
                'Content-Type': 'application/json'
            }
        }).success(function (response, status, headers, config) {
            deferred.resolve(response);
        }).error(function () {
            deferred.reject({'success': false, 'msg': 'Oops! Something went wrong. Please try again later.'});
        });

        return deferred.promise;
    };
   //getting Billing History
    this.getBillHistory = function () {

        var deferred = $q.defer();

        $http({
            method: 'GET',
            url: $rootScope.baseUrl + 'api/v1/billing/invoiceDetails',
            headers: {
                'Content-Type': 'application/json'
            }
        }).success(function (response, status, headers, config) {
            deferred.resolve(response);
        }).error(function () {
            deferred.reject({'success': false, 'msg': 'Oops! Something went wrong. Please try again later.'});
        });

        return deferred.promise;
    };
   
   
    this.getOnlyCurrentPlan = function () {

        var deferred = $q.defer();

        $http({
            method: 'GET',
            url: $rootScope.baseUrl + 'api/v1/billing/currentPlan',
            headers: {
                'Content-Type': 'application/json'
            }
        }).success(function (response, status, headers, config) {
            deferred.resolve(response);
        }).error(function () {
            deferred.reject({'success': false, 'msg': 'Oops! Something went wrong. Please try again later.'});
        });

        return deferred.promise;
    };
   
   
});