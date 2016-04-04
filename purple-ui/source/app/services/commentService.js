/**
 * 
 */

rootApp.service('commentService', function($rootScope, $http, $q, $window) {

	this.postComment = function(status) {

		var deferred = $q.defer();

		$http({
			method : 'POST',
			url : $rootScope.baseUrl + '/api/v1/comment/add',
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
	
	//get reply
	
	this.getComment = function() {

		var deferred = $q.defer();

		$http({
			method : 'GET',
			url : $rootScope.baseUrl + '/api/v1/comments?statusId=57021aa67dba891b747bab9a',
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
	
	
	//reaply
	this.postReply = function(status) {

		var deferred = $q.defer();

		$http({
			method : 'POST',
			url : $rootScope.baseUrl + 'api/v1/comment/57021aec7dba891b747bab9b/reply',
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
	
	//get reply to reply
	
	this.getReply = function() {

		var deferred = $q.defer();

		$http({
			method : 'GET',
			url : $rootScope.baseUrl + '/api/v1/comment/57021aec7dba891b747bab9b/reply/all',
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
	
	
	});