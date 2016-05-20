/**
 * Graph Service
 */

rootApp.service('graphService', function($rootScope, $http, $q, $window) {

	this.getAllStatusAnalytics = function(updateData, time) {

		var deferred = $q.defer();
		var timePeriod = time;

		$http({
			method : 'POST',
			url : $rootScope.baseUrl + 'api/v1/statusAnalytics',
			data : updateData,
			params : {"timePeriod" : timePeriod},
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