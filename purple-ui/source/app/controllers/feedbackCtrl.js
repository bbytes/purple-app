/**
 * Feedback Controller
 */
rootApp.controller('feebackCtrl', function ($scope, $rootScope, $q, $http, appNotifyService, $fancyModal) {

    $scope.sendFeedback = function () {

        if (!$scope.category) {
            appNotifyService.error('Please select a category');
            return false;
        }
        var feebackData = new Object();
        feebackData.category = $scope.category;
        feebackData.suggestions = $scope.commentText;

        var deferred = $q.defer();

        $http({
            method: 'POST',
            url: $rootScope.baseUrl + 'api/v1/feedback',
            data: feebackData,
            headers: {
                'Content-Type': 'application/json',
            }
        }).success(function (response, status, headers, config) {
            deferred.resolve(response);
            appNotifyService.success('Thank you for your feedback...!');
        }).error(function (response) {
            deferred.reject(response);
        });
        return deferred.promise;

    };

});