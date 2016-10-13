/*
 * Login Service
 */

angular.module('rootApp').service('loginService', function ($rootScope, $http, $q) {

    return {
        login: function (userName, Passwd) {

            var deferred = $q.defer();

            $http({
                method: 'GET',
                url: $rootScope.baseUrl + 'auth/login?username=' + userName + '&password=' + Passwd,
                headers: {
                    'Content-Type': 'application/json'
                }
            }).success(function (response, status, headers, config) {

                this.userName = userName;

                var result = {};

                result.data = response.data;
                result.headers = headers();
                result.success = response.success;

                deferred.resolve(result);
            }).error(function (response) {
                deferred.reject(response);
            });

            return deferred.promise;
        },
        logout: function () {
            var result = {};
            var deferred = $q.defer();

            $http({
                method: 'GET',
                url: $rootScope.baseUrl + 'auth/logout',
                cache: false
            }).success(function (response, status, headers) {

                result.headers = headers();
                result.success = response.success;

                deferred.resolve(result);

            }).error(function () {
                deferred.reject({'success': false, 'msg': 'Something went wrong. Please try again later.'});
            });

            return deferred.promise;
        },
        resetChangePassword: function (input) {

            var deferred = $q.defer();

            $rootScope.url = appConfig.patternIs + appConfig.api.resetPassword,
                    $http({
                        method: 'GET',
                        url: appConfig.baseurl + appConfig.rootIs + $rootScope.url,
                        params: input,
                        headers: {
                            'Content-Type': 'application/json'
                        }
                    }).success(function (response) {
                if (response) {
                    deferred.resolve(response);
                }

            }).error(function () {
                deferred.reject({'success': false, 'msg': 'Something went wrong. Please try again later.'});
            });

            return deferred.promise;
        }
    };
});