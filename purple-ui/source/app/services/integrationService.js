/* 
 * Integration Service
 * @author - Akshay
 */
angular.module('rootApp').service('integrationService', function ($rootScope, $http, $q) {

    this.connectToJira = function (data) {

        var deferred = $q.defer();

        $http({
            method: 'POST',
            url: $rootScope.baseUrl + 'api/v1/integration/jira/addAuthentication',
            data: data,
            headers: {
                'Content-Type': 'application/json'
            }

        }).success(function (response, status, headers, config) {
            deferred.resolve(response);
        }).error(function (response) {
            deferred.reject(response);
        });

        return deferred.promise;
    };

    this.getJiraConnection = function () {

        var deferred = $q.defer();

        $http({
            method: 'GET',
            url: $rootScope.baseUrl + 'api/v1/integration/jira/getAuthentication',
            headers: {
                'Content-Type': 'application/json'
            }

        }).success(function (response, status, headers, config) {
            deferred.resolve(response);
        }).error(function (response) {
            deferred.reject(response);
        });

        return deferred.promise;
    };

    this.getJiraProject = function () {

        var deferred = $q.defer();

        $http({
            method: 'GET',
            url: $rootScope.baseUrl + 'api/v1/integration/jira/syncProjects',
            headers: {
                'Content-Type': 'application/json'
            }

        }).success(function (response, status, headers, config) {
            deferred.resolve(response);
        }).error(function (response) {
            deferred.reject(response);
        });

        return deferred.promise;
    };
    
    this.getJiraProjectAndUser = function () {

        var deferred = $q.defer();

        $http({
            method: 'GET',
            url: $rootScope.baseUrl + 'api/v1/integration/jira/syncUsers',
            headers: {
                'Content-Type': 'application/json'
            }

        }).success(function (response, status, headers, config) {
            deferred.resolve(response);
        }).error(function (response) {
            deferred.reject(response);
        });

        return deferred.promise;
    };

    // method is used to get the slack channerls
    this.getSlackChannels = function () {

        var deferred = $q.defer();

        $http({
            method: 'GET',
            url: $rootScope.baseUrl + 'api/v1/integration/slack/channels',
            headers: {
                'Content-Type': 'application/json'
            }

        }).success(function (response, status, headers, config) {
            deferred.resolve(response);
        }).error(function (response) {
            deferred.reject(response);
        });

        return deferred.promise;
    };

    // method is used to setting slack channel for user
    this.setSlackChannel = function (channelId) {

        var deferred = $q.defer();

        $http({
            method: 'POST',
            url: $rootScope.baseUrl + 'api/v1/integration/slack/channel/' + channelId,
            headers: {
                'Content-Type': 'application/json'
            }

        }).success(function (response, status, headers, config) {
            deferred.resolve(response);
        }).error(function (response) {
            deferred.reject(response);
        });

        return deferred.promise;
    };

});