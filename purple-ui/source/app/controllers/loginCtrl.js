rootApp.controller('loginCtrl', function ($scope, $rootScope, $state, loginService,appNotifyService,$sessionStorage,$window) {

    $rootScope.bodyClass = 'body-standalone';
    
    $scope.submitLoginForm = function (isValid) {
   console.log("message");
        // Validating login form
        if (!isValid) {
           console.log('Please enter username and password', 'Invalid inputs');
            return false;
        }

        // Calling login service
        loginService.login($scope.username, $scope.password).then(function (response) {
        	 if (response.headers["x-auth-token"]) {
        	$window.sessionStorage.token = response.headers["x-auth-token"];
               $rootScope.loggedStatus = true;
                $rootScope.loggedIn = $scope.username;
              // $rootScope.userName = response.data.name;
                $rootScope.authToken = response.headers["x-auth-token"];
               // $rootScope.permissions = response.data.permissions;

                var userInfo = {
                    authToken: response.headers["x-auth-token"],
                    id: $rootScope.loggedIn,
                    //name: $rootScope.userName,
                   // userRoles: response.data.userRoles,
                   // permissions: response.data.permissions,
                   // viewMode:$rootScope.viewMode
                };
                
              $sessionStorage.userInfo = userInfo;

                // Login successful, set user locale and Redirect to home page
             /*   if(response.data.locale){
                    appLocaleService.setLocale(response.data.locale);
                }
*/
                $rootScope.showWelcomeMessage = true;
                
                $state.go('user-mgr');
                
            } else {
            	  // Erase the token if the user fails to log in
            	 delete $window.sessionStorage.token;
                //Login failed. Showing error notification
                appNotifyService.error(response.data, 'Login Failed.');
            }

        }, function (error) {
            //Login failed. Showing error notification
            appNotifyService.error(error.msg, 'Login Failed.');
        });
    };
    
    $scope.logout = function() {
    	loginService.logout().then(function (response) {
			
				$location.path("login");
			
		});
	};
});
