/*
 *  Reply snippet Controller
 *  @author - Akshay
 */
angular.module('rootApp').controller('replySnippetCtrl', function ($rootScope, $scope, $state, $q, $http, appNotifyService) {

    $rootScope.feedbackClass = 'feedback-log';
    $rootScope.authToken = $state.params.pk;
    $rootScope.commentId = $state.params.cid;
    $rootScope.replyId = $state.params.rid;

    var deferred = $q.defer();

    $http({
        method: 'GET',
        url: $rootScope.baseUrl + 'api/v1/comment/' + $rootScope.commentId + '/reply/' + $rootScope.replyId,
        headers: {
            'Content-Type': 'application/json'
        }
    }).success(function (response, status, headers, config) {

        if (response.success) {
          $scope.comment = response.data.comment;
           $scope.reply = response.data.reply;
            $scope.status = response.data.comment.status;
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