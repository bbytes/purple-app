/**
 * 
 */

rootApp.service('settingsService', function($rootScope, $http, $q, $window) {

	this.updatePassword = function(password,newpassword) {

		var deferred = $q.defer();

		$http({
			method : 'POST',
			url : $rootScope.baseUrl + 'api/v1/user/setting/password',
			data : password,newpassword,
			headers : {
				'Content-Type' : 'application/json',

			}

		}).success(function(response, status, headers, config) {

			deferred.resolve(response);
		}).error(function() {
			// Something went wrong.
			deferred.reject({
				'success' : false,
				'msg' : 'Oops! Something went wrong. Please try again later.'
			});
		});

		return deferred.promise;

	};
	
	
	this.updateTimezone = function(time) {

		var deferred = $q.defer();

		$http({
			method : 'POST',
			url : $rootScope.baseUrl + 'api/v1/admin/setting/timezone',
			params:time,
			headers : {
				'Content-Type' : 'application/json',

			}

		}).success(function(response, status, headers, config) {

			deferred.resolve(response);
		}).error(function() {
			// Something went wrong.
			deferred.reject({
				'success' : false,
				'msg' : 'Oops! Something went wrong. Please try again later.'
			});
		});

		return deferred.promise;

	};


});