/**
 * Dropdown List Service
 */
rootApp.service('dropdownListService', function ($rootScope, $http, $q, $window) {

// Method is used to get hours list
    this.getHours = function () {

        var deferred = $q.defer();

        $http({
            method: 'GET',
            url: $rootScope.baseUrl + 'api/v1/dropdownList/hours',
            headers: {
                'Content-Type': 'application/json',
            }

        }).success(function (response, status, headers, config) {

            deferred.resolve(response);
        }).error(function (response) {
            deferred.reject(response);
        });
        return deferred.promise;

    };
    this.getEstimateHours = function () {

        var deferred = $q.defer();

        $http({
            method: 'GET',
            url: $rootScope.baseUrl + 'api/v1/dropdownList/getEstimateHours',
            headers: {
                'Content-Type': 'application/json',
            }

        }).success(function (response, status, headers, config) {

            deferred.resolve(response);
        }).error(function (response) {
            deferred.reject(response);
        });
        return deferred.promise;

    };
    
// Method is used to get user's role
    this.getRole = function () {

        var deferred = $q.defer();

        $http({
            method: 'GET',
            url: $rootScope.baseUrl + 'api/v1/dropdownList/roles',
            headers: {
                'Content-Type': 'application/json',
            }

        }).success(function (response, status, headers, config) {

            deferred.resolve(response);
        }).error(function (response) {
            deferred.reject(response);
        });
        return deferred.promise;

    };

// Method is used to get status enable days
    this.getStatusEnable = function () {

        var deferred = $q.defer();

        $http({
            method: 'GET',
            url: $rootScope.baseUrl + 'api/v1/dropdownList/statusEnable',
            headers: {
                'Content-Type': 'application/json',
            }

        }).success(function (response, status, headers, config) {
            deferred.resolve(response);
        }).error(function (response) {
            deferred.reject(response);
        });
        return deferred.promise;

    };

    // Method is used to get timeperiod
    this.getTimePeriod = function () {

        var deferred = $q.defer();

        $http({
            method: 'GET',
            url: $rootScope.baseUrl + 'api/v1/dropdownList/timeperiod',
            headers: {
                'Content-Type': 'application/json',
            }

        }).success(function (response, status, headers, config) {
            deferred.resolve(response);
        }).error(function (response) {
            deferred.reject(response);
        });
        return deferred.promise;

    };

    this.getProjectAndUser = function () {

        var deferred = $q.defer();

        $http({
            method: 'GET',
            url: $rootScope.baseUrl + 'api/v1/dropdownList/projectuser',
            headers: {
                'Content-Type': 'application/json',
            }

        }).success(function (response, status, headers, config) {
            deferred.resolve(response);
        }).error(function (response) {
            deferred.reject(response);
        });
        return deferred.promise;

    };

    // Method is used to get statusCount and statusHours field
    this.getStatusCountAndHours = function () {

        var deferred = $q.defer();

        $http({
            method: 'GET',
            url: $rootScope.baseUrl + 'api/v1/dropdownList/status/countandhour',
            headers: {
                'Content-Type': 'application/json',
            }

        }).success(function (response, status, headers, config) {
            deferred.resolve(response);
        }).error(function (response) {
            deferred.reject(response);
        });
        return deferred.promise;

    };

});