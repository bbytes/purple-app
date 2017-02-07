/*
 * Index Controller
 */
angular.module('rootApp').controller('indexCtrl', function ($scope, $rootScope, $state, $window, $localStorage) {
    window.scrollTo(0, 0);
    $rootScope.bodyClass = 'body-standalone1';
    $rootScope.feedbackClass = 'feedback-log';

    if ($localStorage.userInfo) {
        var userInfo = $localStorage.userInfo;
        $rootScope.authToken = userInfo.accessToken;
        $state.go("status");
    }

    $scope.toTheTop = function () {
        $document.scrollTop(0, 5000);
    }
    var section2 = angular.element(document.getElementById('section-2'));
    $scope.toSection2 = function () {
        $document.scrollTo(section2, 40, 1000);
    }

    $scope.navClass = 'big';
    angular.element($window).bind(
            "scroll", function () {
                if (window.pageYOffset > 0) {
                    $scope.navClass = 'small';
                } else {
                    $scope.navClass = 'big';
                }
                $scope.$apply();
            });

});