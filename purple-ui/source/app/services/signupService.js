/*
 * Sign up Service
 * @author Akshay
 */
angular.module('rootApp').service('signupService', function ($rootScope, $http, $q) {
    return {
        submitSignUp: function (user) {

            var deferred = $q.defer();

            $http({
                method: 'POST',
                url: $rootScope.baseUrl + 'auth/signup',
                data: user,
                headers: {
                    'Content-Type': 'application/json'
                }
            }).success(function (response, status, headers, config) {
                deferred.resolve(response);
            }).error(function (response) {
                deferred.reject(response);
            });
            return deferred.promise;
        },
        
        enterpriseModeCheck: function () {

            var deferred = $q.defer();

            $http({
                method: 'GET',
                url: $rootScope.baseUrl + 'auth/enterprise/mode',
                headers: {
                    'Content-Type': 'application/json'
                }
            }).success(function (response, status, headers, config) {
                deferred.resolve(response);
            }).error(function (response) {
                deferred.reject(response);
            });

            return deferred.promise;
        }
    };
});