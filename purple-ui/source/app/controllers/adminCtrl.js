/**
 * 
 */
rootApp.controller('adminCtrl', function ($scope, $rootScope, $state, adminService,appNotifyService) {

    $scope.invite = function (isValid) {
   console.log("message");
        // Validating login form
        if (!isValid) {
           console.log('Please enter username and password', 'Invalid inputs');
            return false;
        }

        // Calling login service
        adminService.inviteUser($scope.admin).then(function (response) {
        	 if (response.success) {
                
            
                
                $state.go('user-mgr');
                
            } else {
                //Login failed. Showing error notification
                appNotifyService.error(response.data, 'Invite unsuccesfull.');
            }

        }, function (error) {
            //Login failed. Showing error notification
            appNotifyService.error(error.msg, 'Invite unsuccesfull.');
        });
    };
});
