/*
 *  Status snippet Controller
 *  @author - Akshay
 */
angular.module('rootApp').controller('statusSnippetCtrl', function ($rootScope, $scope, $state, $q, $http, appNotifyService) {

    $rootScope.feedbackClass = 'feedback-log';
    $rootScope.authToken = $state.params.pk;
    $rootScope.statusId = $state.params.sid;

    var deferred = $q.defer();

    $http({
        method: 'GET',
        url: $rootScope.baseUrl + 'api/v1/status/' + $rootScope.statusId,
        headers: {
            'Content-Type': 'application/json'
        }
    }).success(function (response, status, headers, config) {

        if (response.success) {
            $scope.statusDate = response.data.gridData[0].date;
            $scope.status = response.data.gridData[0].statusList[0];
        } else {
            $state.go("login");
        }

        deferred.resolve(response);
    }).error(function (response) {
        deferred.reject(response);
        appNotifyService.error('The link is expired');
        $state.go("login");
    });
    return deferred.promise;
});