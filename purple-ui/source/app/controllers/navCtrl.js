
angular.module('rootApp').controller('navCtrl',function ($scope,$rootScope, $location,toaster,appNotifyService, $state,cfpLoadingBar,billingInfoService, $fancyModal) {

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
        
    $scope.addBillingInfo = function (isValid,customer) {
            // Validating login form
        if (!isValid) {
            toaster.pop({type: 'error', body: 'Please valid inputs.', toasterId: 1});
            return false;
        }
        $scope.variables= {
            "contactNo":customer.contactNo,
                    "email":$scope.loggedInUser,
                    "name":$scope.userName,
                    "website":customer.website,
                    "billingAddress":customer.billingAddress
        };
        
        billingInfoService.addBillingDetails($scope.variables).then(function (response) {
            if (response.success) {
                appNotifyService.error('Your details are saved');
                $scope.billngDetails = response.data;
                customer.website=[];
                customer.billingAddress=[];
                customer.contactNo=[];
            }
        });
    };
    
    
    $scope.getPricingPlans = function () {
        billingInfoService.getPricingPlans().then(function (response) {
            if (response.success) {
                $scope.pricingPlans = response.data;
                
            }
        });
        
        /*To get Current Plan */
      
        billingInfoService.getOnlyCurrentPlan().then(function (response) {
            if (response.success) {
                $scope.currentPlan = response.data;
                
            }
        });
    };
    $scope.getInvoiceDetails = function () {
        billingInfoService.getInvoiceDetails().then(function (response) {
            if (response.success) {
                $scope.pricingPlans = response.data;
                
            }
        });
    };

    });