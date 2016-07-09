/**
 * Admin Service
 */
rootApp.service('adminService', function ($rootScope, $http, $q, $window) {

    this.inviteUser = function (admin) {

        var deferred = $q.defer();

        $http({
            method: 'POST',
            url: $rootScope.baseUrl + 'api/v1/admin/user/add',
            data: admin,
            headers: {
                'Content-Type': 'application/json',
            }

        }).success(function (response, status, headers, config) {

            deferred.resolve(response);
        }).error(function (response) {
            // Something went wrong.
            deferred.reject(response);
        });

        return deferred.promise;

    };

    this.reInviteUser = function (name, email) {

        var deferred = $q.defer();

        $http({
            method: 'GET',
            url: $rootScope.baseUrl + 'api/v1/admin/user/reinvite',
            params: {"name": name, "email": email},
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

    this.updateUserRole = function (userId, role) {

        var deferred = $q.defer();

        $http({
            method: 'POST',
            url: $rootScope.baseUrl + 'api/v1/admin/user/role',
            params: {"userId": userId, "role": role},
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

    this.deleteUser = function (email) {

        var deferred = $q.defer();

        $http({
            method: 'DELETE',
            url: $rootScope.baseUrl + 'api/v1/admin/user/delete/' + email,
            //data : admin,
            headers: {
                'Content-Type': 'application/json'
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

    this.bulkupload = function (file) {

        var deferred = $q.defer();
        $http.post($rootScope.baseUrl + 'api/v1/admin/user/bulkupload', file, {
            transformRequest: angular.identity,
            headers: {'Content-Type': undefined}

        }).success(function (response, status, headers, config) {

            deferred.resolve(response);
        }).error(function (response) {
            deferred.reject(response);
        });

        return deferred.promise;
    };

});