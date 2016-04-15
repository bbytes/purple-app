/**
 *  Reset Controller to reset password
 */
rootApp.controller('resetPasswordCtrl', function ($scope, $rootScope, $state, $window,$sessionStorage,settingsService,appNotifyService) {

    $window.sessionStorage.token = $state.params.token;
      
         $rootScope.authToken = $state.params.token;
         
         var tokenstored = $rootScope.authToken;

    $scope.updatePassword = function (isValid) {
    	   if (!isValid) {
    	        appNotifyService.error('Please enter valid passwords', 'Invalid inputs');
    	           console.log('Please enter username and password', 'Invalid inputs');
    	            return false;
    	        }
      
    	settingsService.updatePassword($scope.user).then(function (response) {
        	 if (response.success = true) {
                console.log(response);
        		 $scope.user ='';
        		 appNotifyService.success('yes', 'Your password has been changed.');  		 
            } else {

                appNotifyService.error(response.data, 'Enter valid passwords.');
            }

        }, function (error) {

            appNotifyService.error(error.msg, 'Enter valid passwords.');
        });
    };
});
