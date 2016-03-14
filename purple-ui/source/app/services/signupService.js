rootApp.service('signupService', function ($rootScope, $http, $q) {
	 return {
        submitSignUp: function (user) {

            var deferred = $q.defer();

            $http({
                method: 'POST',
                url:$rootScope.baseUrl+'auth/signup',
                data: user,
                headers: {
                    'Content-Type': 'application/json'
                }
            }).success(function (response, status, headers, config) {
                deferred.resolve(response);
            }).error(function () {
                // Something went wrong.
                deferred.reject(response);
            });

            return deferred.promise;

        },
	};
});