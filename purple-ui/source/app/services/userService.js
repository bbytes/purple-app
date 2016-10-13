/*
 * User Service
 */
angular.module('rootApp').service('userService', function ($rootScope, $http, $q) {

    this.inviteUser = function (admin) {

        var deferred = $q.defer();

        $http({
            method: 'POST',
            url: $rootScope.baseUrl + 'api/v1/user/add',
            data: admin,
            headers: {
                'Content-Type': 'application/json'
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
            url: $rootScope.baseUrl + 'api/v1/user/reinvite',
            params: {"name": name, "email": email},
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
            url: $rootScope.baseUrl + 'api/v1/user',
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

    // This method is used to update user role
    this.updateUserRole = function (userId, role) {

        var deferred = $q.defer();

        $http({
            method: 'PUT',
            url: $rootScope.baseUrl + 'api/v1/user/role',
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
            url: $rootScope.baseUrl + 'api/v1/user/delete/' + email,
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
        $http.post($rootScope.baseUrl + 'api/v1/user/bulkupload', file, {
            transformRequest: angular.identity,
            headers: {'Content-Type': undefined}

        }).success(function (response, status, headers, config) {

            deferred.resolve(response);
        }).error(function (response) {
            deferred.reject(response);
        });

        return deferred.promise;
    };

    // this method is used to enable/disable user
    this.disableUser = function (userId, disableState) {

        var deferred = $q.defer();

        $http({
            method: 'PUT',
            url: $rootScope.baseUrl + 'api/v1/user/disable/' + userId,
            params: {"disableState": disableState},
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

    // this method is used to set user as mark for delete (user and their statuses,comments
    // will be deleted after 30 Days, it been taking care by server side)
    this.markForDelete = function (userId, markDeleteState, days) {

        var deferred = $q.defer();

        $http({
            method: 'DELETE',
            url: $rootScope.baseUrl + 'api/v1/user/markdelete/' + userId,
            params: {"markdeleteState": markDeleteState, "days": days},
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
});