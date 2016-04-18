/**
 *  Reset Controller to reset password
 */
rootApp.controller('resetPasswordCtrl', function ($scope, $rootScope, $state, $window,$sessionStorage,settingsService,appNotifyService) {

    $window.sessionStorage.token = $state.params.token;
      
         $rootScope.authToken = $state.params.token;
         
         var tokenstored = $rootScope.authToken;

    $scope.updatePassword = function (user) {
    	   if (user.newPassword != user.confirmPassword) {
    	        appNotifyService.error('Password is mismatch');
    	            return false;
    	        }
      
    	settingsService.updatePassword($scope.user).then(function (response) {
        	 if (response.success = true) {
        		 $scope.user ='';
        		 appNotifyService.success('Your password has been reset'); 
                   $state.go('login');  		 
            } else {

                appNotifyService.error(response.data, 'Enter valid passwords.');
            }
        }, function (error) {
            appNotifyService.error(error.msg, 'Enter valid passwords.');
        });
    };
});
