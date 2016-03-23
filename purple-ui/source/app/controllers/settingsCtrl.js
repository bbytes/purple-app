/**
 * 
 */
/**
 * 
 */
rootApp.controller('settingsCtrl', function ($scope, $rootScope, $state, settingsService,appNotifyService) {

    $scope.updatePassword = function (user,confirmPassword) {
    	
    var oldPassword=user.oldPassword;
      var newPassword=user.newPassword;
      var confirmpassword=$scope.confirmPassword;
      if( confirmpassword == newPassword && oldPassword != null){
      
        // Calling login service
    	settingsService.updatePassword($scope.user).then(function (response) {
        	 if (response.success = true) {
        		 appNotifyService.success('yes', 'Your password has been changed.');
            } else {
                //Login failed. Showing error notification
                appNotifyService.error(response.data, 'Enter valid passwords.');
            }

        }, function (error) {
            //Login failed. Showing error notification
            appNotifyService.error(error.msg, 'Enter valid passwords.');
        });
        
    }
    
    else if(newPassword != confirmpassword){
    	appNotifyService.error('Password mismatch.');
    	
    }
    else{
    	appNotifyService.error('Oops!...Something wrong');
    }
    };
    
    
});
