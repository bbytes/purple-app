/**
 * 
 */
/**
 * 
 */

rootApp.service('projectService', function($rootScope, $http, $q, $window) {

	this.createProject = function(project) {

		var deferred = $q.defer();

		$http({
			method : 'POST',
			url : $rootScope.baseUrl + '/api/v1/admin/project/create',
			data : project,
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

	this.getAllprojects = function() {

		var deferred = $q.defer();

		$http({
			method : 'GET',
			url : $rootScope.baseUrl + 'api/v1/admin/project',
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
	
	this.deleteProject = function(id) {

		var deferred = $q.defer();

		$http({
			method :'DELETE',
			url : $rootScope.baseUrl + 'api/v1/admin/project/delete/' +id,
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
	
	this.getProjectWithId = function(id) {

		var deferred = $q.defer();

		$http({
			method :'POST',
			url : $rootScope.baseUrl + 'api/v1/admin/project/' +id,
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