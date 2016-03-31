/**
 * 
 */

rootApp.controller('userSettingsCtrl', function ($scope, $rootScope, $state, settingsService,appNotifyService) {

    $scope.updatePassword = function (isValid) {
    	   if (!isValid) {
    	        appNotifyService.error('Please enter valid passwords', 'Invalid inputs');
    	           console.log('Please enter username and password', 'Invalid inputs');
    	            return false;
    	        }

  
      
        // Calling login service
    	settingsService.updatePassword($scope.user).then(function (response) {
        	 if (response.success = true) {
        		 $scope.user ='';
        		 appNotifyService.success('yes', 'Your password has been changed.');
        		
        		 
            } else {
                //Login failed. Showing error notification
                appNotifyService.error(response.data, 'Enter valid passwords.');
            }

        }, function (error) {
            //Login failed. Showing error notification
            appNotifyService.error(error.msg, 'Enter valid passwords.');
        });

    };
    
    $scope.updateTime= function (isValid) {
 	   if (!isValid) {
 	        appNotifyService.error('Please enter valid passwords', 'Invalid inputs');
 	           console.log('Please enter username and password', 'Invalid inputs');
 	            return false;
 	        }


   
     // Calling login service
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


   
});
