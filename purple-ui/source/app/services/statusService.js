/**
 * Status Service
 */

rootApp.service('statusService', function ($rootScope, $http, $q, $window, cfpLoadingBar) {

    this.submitStatus = function (status) {

        var deferred = $q.defer();

        $http({
            method: 'POST',
            url: $rootScope.baseUrl + 'api/v1/status/add',
            data: status,
            headers: {
                'Content-Type': 'application/json'
            }

        }).success(function (response, status, headers, config) {

            deferred.resolve(response);
        }).error(function (response) {
            deferred.reject(response);
        });

        return deferred.promise;

    };

    this.getAllusers = function () {

        var deferred = $q.defer();

        $http({
            method: 'GET',
            url: $rootScope.baseUrl + 'api/v1/admin/user',
            headers: {
                'Content-Type': 'application/json'
            }
        }).success(function (response, status, headers, config) {
            deferred.resolve(response);
        }).error(function () {
            deferred.reject({
                'success': false,
                'msg': 'Oops! Something went wrong. Please try again later.'
            });
        });

        return deferred.promise;
    };

    this.deleteUser = function (email) {

        var deferred = $q.defer();

        $http({
            method: 'DELETE',
            url: $rootScope.baseUrl + 'api/v1/admin/user/delete/' + email,
            // data : admin,
            headers: {
                'Content-Type': 'application/json',
            }

        }).success(function (response, status, headers, config) {

            deferred.resolve(response);
        }).error(function () {
            // Something went wrong.
            deferred.reject({
                'success': false,
                'msg': 'Oops! Something went wrong. Please try again later.'
            });
        });

        return deferred.promise;

    };

    this.getAllStatus = function (time) {

        var deferred = $q.defer();
        var timePeriod = time;
        $http({
            method: 'GET',
            url: $rootScope.baseUrl + 'api/v1/status',
            params: {"timePeriod": timePeriod},
            headers: {
                'Content-Type': 'application/json'
            }
        }).success(function (response, status, headers, config) {
            deferred.resolve(response);
        }).error(function () {
            deferred.reject({
                'success': false,
                'msg': 'Oops! Something went wrong. Please try again later.'
            });
        });

        return deferred.promise;
    };

    this.deleteStatus = function (id) {

        var deferred = $q.defer();

        $http({
            method: 'DELETE',
            url: $rootScope.baseUrl + 'api/v1/status/' + id,
            // data : admin,
            headers: {
                'Content-Type': 'application/json',
            }

        }).success(function (response, status, headers, config) {

            deferred.resolve(response);
        }).error(function () {
            // Something went wrong.
            deferred.reject({
                'success': false,
                'msg': 'Oops! Something went wrong. Please try again later.'
            });
        });

        return deferred.promise;

    };

    this.getStatusWithId = function (id) {

        var deferred = $q.defer();

        $http({
            method: 'GET',
            url: $rootScope.baseUrl + 'api/v1/status/' + id,
            // data : admin,
            headers: {
                'Content-Type': 'application/json',
            }

        }).success(function (response, status, headers, config) {

            deferred.resolve(response);
        }).error(function () {
            // Something went wrong.
            deferred.reject({
                'success': false,
                'msg': 'Oops! Something went wrong. Please try again later.'
            });
        });

        return deferred.promise;

    };

    this.updateStatus = function (data, id) {

        var deferred = $q.defer();

        $http({
            method: 'PUT',
            url: $rootScope.baseUrl + 'api/v1/status/update/' + id,
            data: data,
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

    this.getAllTimelineStatus = function (updateData, time) {

        var deferred = $q.defer();
        var timePeriod = time;

        $http({
            method: 'POST',
            url: $rootScope.baseUrl + 'api/v1/status/project/user',
            data: updateData,
            params: {"timePeriod": timePeriod},
            headers: {
                'Content-Type': 'application/json'
            }
        }).success(function (response, status, headers, config) {
            deferred.resolve(response);
        }).error(function () {
            deferred.reject({
                'success': false,
                'msg': 'Oops! Something went wrong. Please try again later.'
            });
        });

        return deferred.promise;
    };

    // method is used to download all status by timeperiod
    this.csvDownloadAllStatus = function (time) {

        var deferred = $q.defer();
        var timePeriod = time;
        $http({
            method: 'GET',
            url: $rootScope.baseUrl + 'api/v1/status/csv',
            params: {
                "timePeriod": timePeriod
            },
            headers: {
                'Content-Type': 'text/csv'
            }
        }).success(function (response, status, headers, config) {
            var result = {};
            result.fileName = headers('purple-file-name') || 'statusData.csv';
            result.data = response;
            deferred.resolve(result);
        }).error(function (response) {
            deferred.reject(response);
        });

        return deferred.promise;
    };

    // method is used to download all status by user
    this.csvDownloadAllStatusByUser = function (userId) {

        var deferred = $q.defer();
        $http({
            method: 'GET',
            url: $rootScope.baseUrl + 'api/v1/status/csv',
            params: {
                "userId": userId
            },
            headers: {
                'Content-Type': 'text/csv'
            }
        }).success(function (response, status, headers, config) {
            var result = {};
            result.fileName = headers('purple-file-name') || 'statusData.csv';
            result.data = response;
            deferred.resolve(result);
        }).error(function (response) {
            deferred.reject(response);
        });

        return deferred.promise;
    };

    this.csvDownloadByProjectAndUser = function (data) {

        var deferred = $q.defer();
        projectAndUserObj = data.value;
        var timePeriod = data.timePeriod;

        $http({
            method: 'POST',
            url: $rootScope.baseUrl + 'api/v1/status/project/user/csv',
            params: {"timePeriod": timePeriod},
            data: projectAndUserObj
        }).success(function (response, status, headers, config) {
            var result = {};
            result.fileName = headers('purple-file-name') || 'statusData.csv';
            result.data = response;
            deferred.resolve(result);
        }).error(function (response) {
            deferred.reject(response);
        });

        return deferred.promise;
    };


});