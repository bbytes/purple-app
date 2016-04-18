/**
 * User seeting controller
 */

rootApp.controller('userSettingsCtrl', function ($scope, $rootScope, $state, settingsService,appNotifyService) {

    // Reset password for user
    $scope.updatePassword = function (user,confirmPassword) {
    	
        var oldPassword=user.oldPassword;
        var newPassword=user.newPassword;
        var confirmpassword=$scope.confirmPassword;
      if( confirmpassword == newPassword && oldPassword != null){
      
        settingsService.updatePassword($scope.user).then(function (response) {
             if (response.success = true) {
                 appNotifyService.success('Your password has been changed.');
                 $scope.clearPasswordText(user,confirmPassword);
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
    
    $scope.updateTime= function (isValid) {
 	   if (!isValid) {
 	        appNotifyService.error('Please enter valid passwords', 'Invalid inputs');
 	           console.log('Please enter username and password', 'Invalid inputs');
 	            return false;
 	        }
   
 	settingsService.updateTimezone($scope.time).then(function (response) {
     	 if (response.success = true) {
     		 $scope.time ='';
     		 appNotifyService.success('Timezone has been updated.');
     		
     		 
         } else {
             //Login failed. Showing error notification
             appNotifyService.error(response.data, 'Enter valid passwords.');
         }

     }, function (error) {
         //Login failed. Showing error notification
         appNotifyService.error(error.msg, 'Enter valid passwords.');
     });

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
