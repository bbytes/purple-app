/**
 * Dropdown List Service
 */
rootApp.service('dropdownListService', function($rootScope, $http, $q, $window) {

// Method is used to get hours list
	this.getHours = function() {

		var deferred = $q.defer();

		$http({
			method : 'GET',
			url : $rootScope.baseUrl + 'api/v1/dropdownList/hours',
			headers : {
				'Content-Type' : 'application/json',
			}

		}).success(function(response, status, headers, config) {

			console.log(response);
			deferred.resolve(response);
		}).error(function(response) {
				 deferred.reject(response);
		});
		return deferred.promise;

	};
// Method is used to get user's role
	this.getRole = function() {

		var deferred = $q.defer();

		$http({
			method : 'GET',
			url : $rootScope.baseUrl + 'api/v1/dropdownList/roles',
			headers : {
				'Content-Type' : 'application/json',
			}

		}).success(function(response, status, headers, config) {

			console.log(response);
			deferred.resolve(response);
		}).error(function(response) {
				 deferred.reject(response);
		});
		return deferred.promise;

	};

// Method is used to get status enable days
	this.getStatusEnable = function() {

		var deferred = $q.defer();

		$http({
			method : 'GET',
			url : $rootScope.baseUrl + 'api/v1/dropdownList/statusEnable',
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