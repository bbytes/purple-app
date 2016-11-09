

angular.module('rootApp').controller('navCtrl', ['$scope','$rootScope', '$location','$state', function ($scope,$rootScope, $location, $state,cfpLoadingBar, $fancyModal) {

        $scope.isActive = function (viewLocation) {
            var active = (viewLocation === $location.path());
            return active;

        };
        
   
         $scope.setAdminTab = function (section) {
        if (section == 'billinginfo') {
            $rootScope.adminTab = 'billinginfo';
            $state.go('billinginfo');
        } else if (section == 'invoicedetails') {
        
            $rootScope.adminTab = 'invoicedetails';
          
        }else{
             $rootScope.adminTab = 'productplans';
        }
    };
              $scope.activeAdminTab = function (section) {
        return (section === $rootScope.adminTab) ? true : false;
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

    }]);