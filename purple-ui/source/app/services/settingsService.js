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
	
	
	this.updateSetting = function(settingObj) {
       console.log(settingObj);
		var timeZone = "IST";
		var timePreference = settingObj.timePreference;
		var deferred = $q.defer();

		$http({
			method : 'POST',
			url : $rootScope.baseUrl + 'api/v1/setting',
			params : {"timeZone" : timeZone, "timePreference" : timePreference},
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