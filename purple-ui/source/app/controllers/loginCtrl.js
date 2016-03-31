rootApp.controller('loginCtrl', function ($scope, $rootScope, $state, loginService,appNotifyService,$sessionStorage,$window) {

    $rootScope.bodyClass = 'body-standalone';
    
    $scope.submitLoginForm = function (isValid) {
   console.log("message");
        // Validating login form
        if (!isValid) {
        appNotifyService.error('Please enter username and password', 'Invalid inputs');
           console.log('Please enter username and password', 'Invalid inputs');
            return false;
        }

        // Calling login service
        loginService.login($scope.username, $scope.password).then(function (response) {
        	 if (response.headers["x-auth-token"] && response.data.accountInitialise == true) {
        	$window.sessionStorage.token = response.headers["x-auth-token"];
               $rootScope.loggedStatus = true;
               $rootScope.loggedInUser = $scope.username;
               $rootScope.userRole = response.data.userRole.id;
              
                $rootScope.authToken = response.headers["x-auth-token"];
               // $rootScope.permissions = response.data.permissions;

               var userInfo = {
                    authToken: response.headers["x-auth-token"],
                    email: $rootScope.loggedInUser,
                    //name: $rootScope.userName,
                    userRoles:  $rootScope.userRole,
                   // permissions: response.data.permissions,
                   // viewMode:$rootScope.viewMode
                };
              
            $sessionStorage.userInfo =  userInfo;

                // Login successful, set user locale and Redirect to home page
             /*   if(response.data.locale){
                    appLocaleService.setLocale(response.data.locale);
                }
*/
            
           
                $rootScope.showWelcomeMessage = true;
                
                $state.go('status');
                
                
            } else {
            	  // Erase the token if the user fails to log in
            	 delete $window.sessionStorage.token;
            	 //delete  $sessionStorage.userInfo;
                //Login failed. Showing error notification
                appNotifyService.error('Please activate your account to login.');
            }

        }, function (error) {
            //Login failed. Showing error notification
            appNotifyService.error(error.msg, 'Login Failed.');
        });
    };
    
    $scope.logout = function() {
    	 delete $window.sessionStorage.token;
    
    	$sessionStorage.remove('userInfo');
    	loginService.logout().then(function (response) {
			
				$location.path("login");
			
		});
	};
});
