/**
 * 
 */
rootApp.service('logoutService', function ($rootScope, $http, $q) {
	
	return {

logout: function () {
            var result = {};
            var deferred = $q.defer();

            $http({
                method: 'GET',
                url:  $rootScope.baseUrl + 'auth/logout',
                cache: false
            }).success(function (response, status, headers) {

                result.headers = headers();
                result.success = response.success;

                deferred.resolve(result);

            }).error(function () {
                deferred.reject({'success': false, 'msg': 'Something went wrong. Please try again later.'});
            });

            return deferred.promise;
        }
	};
        
});