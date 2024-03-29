
angular.module('rootApp').controller('navCtrl',function ($scope,$rootScope, $location,toaster,appNotifyService, $state,cfpLoadingBar,billingInfoService, $fancyModal) {

        $scope.isActive = function (viewLocation) {
            var active = (viewLocation === $location.path());
            return active;

        };

        $(document).ready(function () {
            $('.dropdown-toggle').dropdown();
        });


        $scope.open = function () {

            var modalInstance = $modal.open({
                templateUrl: 'app/partials/feedback.html',
                controller: 'feedbackCtrl',
                resolve: {
                    items: function () {
                        return $scope.items;
                    }
                }
            });

            modalInstance.result.then(function (selectedItem) {
                $scope.selected = selectedItem;
            }, function () {
            });
        };
    });