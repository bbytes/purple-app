/**
 * 
 */

rootApp.service('commentService', function($rootScope, $http, $q, $window) {

	this.postComment = function(commentBody) {

		var deferred = $q.defer();

		$http({
			method : 'POST',
			url : $rootScope.baseUrl + 'api/v1/comment/add',
			data : commentBody,
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
	
	//get comment
	
	this.getComment = function(statusId) {

		var deferred = $q.defer();

		$http({
			method : 'GET',
			url : $rootScope.baseUrl + 'api/v1/comments?statusId=' + statusId,
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
	this.postReply = function(replyPost, statusId) {

		var deferred = $q.defer();

		$http({
			method : 'POST',
			url : $rootScope.baseUrl + 'api/v1/comment/' +statusId +'/reply',
			data : replyPost,
			headers : {
				'Content-Type' : 'application/json',

			}

		}).success(function(response, replyPost, headers, config) {

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
	
	//get comment to reply
	
	this.getReplies = function() {

		var deferred = $q.defer();

		$http({
			method : 'GET',
			url : $rootScope.baseUrl + 'api/v1/comment/' + statusId + '/reply/all',
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
	
	//maping
	this.getProjectMap = function(projectId) {

		var deferred = $q.defer();

		$http({
			method : 'POST',
			url : $rootScope.baseUrl + 'api/v1/projects/users/all/map',
			data : [projectId],
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