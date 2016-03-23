rootApp.controller('signupCtrl', function ($scope, $rootScope, $state, signupService,appNotifyService,$sessionStorage) {
  
    $rootScope.bodyClass = 'body-standalone';
    
    $scope.submitSignUp = function (isValid) {

        // Validating login form
        if (!isValid) {
             console.log("message");
           growl.error('Please enter valid inputs');
            return false;
        }

        

        signupService.submitSignUp($scope.user).then(function (response) {
         if (response.success == true) {
                
        	/*   $rootScope.authToken = response.data;
        	   var userInfo = {
                       authToken: response.data,
                       id: $rootScope.loggedIn,
                       //name: $rootScope.userName,
                      // userRoles: response.data.userRoles,
                      // permissions: response.data.permissions,
                      // viewMode:$rootScope.viewMode
                   };
                   
                   
                 $sessionStorage.userInfo = userInfo;*/
        	 appNotifyService.success('Activation link has been sent your registered mail.');
                $state.go('login');
                
            } else {
                //Login failed. Showing error notification
                appNotifyService.error(response.data, 'Registration Failed something wrong.');
            }

        }, function (error) {
            //Login failed. Showing error notification
            appNotifyService.error(error.msg, 'Login Failed.');
        });
    };
});
