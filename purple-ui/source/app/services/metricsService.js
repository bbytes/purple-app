/*
 * Metrics Service
 */

angular.module('rootApp').service('metricsService', function ($rootScope, $http, $q) {

    this.getAllStatusAnalytics = function (updateData, time) {

        var deferred = $q.defer();
        var timePeriod = time;

        $http({
            method: 'POST',
            url: $rootScope.baseUrl + 'api/v1/status/analytics',
            data: updateData,
            params: {"timePeriod": timePeriod},
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