/**
 * 
 */

rootApp.service('settingsService', function($rootScope, $http, $q, $window) {

	this.updatePassword = function(newpassword) {

		var deferred = $q.defer();

		$http({
			method : 'POST',
			url : $rootScope.baseUrl + 'api/v1/user/setting/password',
			data:newpassword,
			headers : {
				'Content-Type' : 'application/json',

			}

		}).success(function(response, status, headers, config) {

			deferred.resolve(response);
		}).error(function(response) {
				 deferred.reject(response);
		});

		return deferred.promise;

	};
	
	
	this.updateTimezone = function(time) {
       console.log(time);
		
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

	this.saveConfigSetting = function(admin) {

		var deferred = $q.defer();

		$http({
			method : 'POST',
			url : $rootScope.baseUrl + 'api/v1/admin/configSetting/update',
			data : admin,
			headers : {
				'Content-Type' : 'application/json',
			}

		}).success(function(response, status, headers, config) {
				deferred.resolve(response);
		}).error(function(response) {
				deferred.reject(response);
		});

		return deferred.promise;
	};

	this.getConfigSetting = function(admin) {

		var deferred = $q.defer();

		$http({
			method : 'GET',
			url : $rootScope.baseUrl + 'api/v1/admin/configSetting',
			headers : {
				'Content-Type' : 'application/json',
			}

		}).success(function(response, status, headers, config) {
				deferred.resolve(response);
		}).error(function(response) {
				deferred.reject(response);
		});

		return deferred.promise;
	};

});