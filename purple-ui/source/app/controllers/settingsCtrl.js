/**
 * Setting controller
 */
rootApp.controller('settingsCtrl', function ($scope, $rootScope, $state, settingsService,appNotifyService) {

    // Reset password for admin
    $scope.updatePassword = function (user,confirmPassword) {
    	
    var oldPassword=user.oldPassword;
    var newPassword=user.newPassword;
    var confirmpassword=$scope.confirmPassword;
      if( confirmpassword == newPassword && oldPassword != null){
      
    	settingsService.updatePassword($scope.user).then(function (response) {
        	 if (response.success = true) {
        		appNotifyService.success('Your password has been changed.');
        		$scope.clearPasswordText(user,confirmPassword);
                $scope.isFormSubmitted = true;
            } 
        }, function (error) {
            if(error.reason == 'password_mistach')
            appNotifyService.error('Current password is incorrect');
        });   
    }
    else if(newPassword != confirmpassword){
    	appNotifyService.error('Password mismatch.');	
    }
    else{
    	appNotifyService.error('Please enter current password');
    }
    };
    
	$("[name='my-checkbox']").bootstrapSwitch();
	
	//calendar
    $scope.uiConfig = {
      calendar:{
        //height: 350,
        editable: false,
        header:{
          left: 'prev',
          center: 'title',
          right: 'next'
        },
        dayClick: $scope.alertEventOnClick,
        eventDrop: $scope.alertOnDrop,
        eventResize: $scope.alertOnResize
      }
    };

      $scope.clearPasswordText = function(user,confirmPassword){
    	
		user.oldPassword = '';
		$scope.confirmPassword = '';
		user.newPassword = '';
    }      
});
