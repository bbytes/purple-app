/*
 *  Comment snippet Controller
 *  @author - Akshay
 */
angular.module('rootApp').controller('commentSnippetCtrl', function ($rootScope, $scope, $state, $q, $http, appNotifyService) {

    $rootScope.feedbackClass = 'feedback-log';
    $rootScope.authToken = $state.params.pk;
    $rootScope.commentId = $state.params.cid;

    var deferred = $q.defer();

    $http({
        method: 'GET',
        url: $rootScope.baseUrl + 'api/v1/comment/' + $rootScope.commentId,
        headers: {
            'Content-Type': 'application/json'
        }
    }).success(function (response, status, headers, config) {

        if (response.success) {
            $scope.comment = response.data;
            $scope.status = response.data.status;
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