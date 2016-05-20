/**
 * User setting controller
 */
rootApp.controller('settingsCtrl', function ($scope, $rootScope, $state, settingsService,appNotifyService) {
    
	$rootScope.bodyClass = 'body-standalone1';
	$rootScope.navClass = 'nav-control';
	$rootScope.navstatusClass = 'nav navbar-nav';
    // Reset password for user
    $scope.updatePassword = function (user,confirmPassword) {
    	
        var oldPassword=user.oldPassword;
        var newPassword=user.newPassword;
        var confirmpassword=$scope.confirmPassword;
      if( confirmpassword == newPassword && oldPassword != null){
      
        settingsService.updatePassword($scope.user).then(function (response) {
             if (response.success = true) {
                 appNotifyService.success('Your password has been successfully changed.');
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
        appNotifyService.error('Please enter your current password');
    }

    };
    
    $scope.updateTime= function (isValid) {
 	   if (!isValid) {
 	        appNotifyService.error('Please select a valid Timezone');
 	            return false;
 	        }
   
 	settingsService.updateTimezone($scope.time).then(function (response) {
     	 if (response.success = true) {
     		 $scope.time ='';
     		 appNotifyService.success('Timezone has been successfully updated.');   		 
         } 

     }, function (error) {
         appNotifyService.error('Please select a valid Timezone');
     });

 };
 
  $scope.setTime= function () {
 	   
	   $scope.sharedDate = "1970-01-01T14:30:00.000Z";
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
