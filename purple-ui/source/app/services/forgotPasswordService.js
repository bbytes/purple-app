rootApp.service('forgotPasswordService', function ($rootScope, $http, $q) {
    return {
        submitForgotPassword: function (user) {

            var email = user.username;
            var deferred = $q.defer();

            $http({
                method: 'GET',
                url: $rootScope.baseUrl + 'auth/forgotPassword',
                params: {email: email},
                headers: {
                    'Content-Type': 'application/json',
                }

            }).success(function (response, status, headers, config) {

                deferred.resolve(response);
            }).error(function (response) {

                deferred.reject(response);
            });

            return deferred.promise;

        },
    };
});