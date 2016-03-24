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
        		 $scope.clearProjectText(user,confirmPassword);
        		 
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


      $scope.clearProjectText = function(user,confirmPassword){
    	
	   user.oldPassword = '';
	   user.newPassword = '';
    	project.users.length = 0;
    }
});
