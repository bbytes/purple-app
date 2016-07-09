rootApp.service('integrationService', function ($rootScope, $http, $q) {

    this.connectToJira = function (data) {

        var deferred = $q.defer();

        $http({
            method: 'POST',
            url: $rootScope.baseUrl + 'api/v1/integration/jira/addAuthentication',
            data: data,
            headers: {
                'Content-Type': 'application/json',
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
                'Content-Type': 'application/json',
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
            url: $rootScope.baseUrl + 'api/v1/integration/jira/getprojects',
            headers: {
                'Content-Type': 'application/json',
            }

        }).success(function (response, status, headers, config) {
            deferred.resolve(response);
        }).error(function (response) {
            deferred.reject(response);
        });

        return deferred.promise;
    };

});