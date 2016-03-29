/**
 * 
 */
/**
 * 
 */

rootApp.service('statusService', function($rootScope, $http, $q, $window) {

	this.submitStatus = function(status) {

		var deferred = $q.defer();

		$http({
			method : 'POST',
			url : $rootScope.baseUrl + 'api/v1/status/add',
			data : status,
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

	this.getAllusers = function() {

		var deferred = $q.defer();

		$http({
			method : 'GET',
			url : $rootScope.baseUrl + 'api/v1/admin/user',
			headers : {
				'Content-Type' : 'application/json'
			}
		}).success(function(response, status, headers, config) {
			deferred.resolve(response);
		}).error(function() {
			deferred.reject({
				'success' : false,
				'msg' : 'Oops! Something went wrong. Please try again later.'
			});
		});

		return deferred.promise;
	};
	
	this.deleteUser = function(email) {

		var deferred = $q.defer();

		$http({
			method :'DELETE',
			url : $rootScope.baseUrl + '/api/v1/admin/user/delete/' +email,
			//data : admin,
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
	
	this.getAllStatus = function() {

		var deferred = $q.defer();

		$http({
			method : 'GET',
			url : $rootScope.baseUrl + 'api/v1/status',
			headers : {
				'Content-Type' : 'application/json'
			}
		}).success(function(response, status, headers, config) {
			deferred.resolve(response);
		}).error(function() {
			deferred.reject({
				'success' : false,
				'msg' : 'Oops! Something went wrong. Please try again later.'
			});
		});

		return deferred.promise;
	};
	
	
	this.deleteStatus = function(id) {

		var deferred = $q.defer();

		$http({
			method :'DELETE',
			url : $rootScope.baseUrl + '/api/v1/status/' +id,
			//data : admin,
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