/**
 *  Login Controller
 */
rootApp.controller('loginCtrl', function ($scope, $rootScope, $state, loginService,appNotifyService,$sessionStorage,$window) {

    $rootScope.bodyClass = 'body-standalone';
    
    $scope.submitLoginForm = function (isValid) {

        // Validating login form
        if (!isValid) {
        appNotifyService.error('Please enter username and password', 'Invalid inputs');
            return false;
        }

        // Calling login service
        loginService.login($scope.username, $scope.password).then(function (response) {
        	 if (response.headers["x-auth-token"] && response.data.accountInitialise == true) {
        	$window.sessionStorage.token = response.headers["x-auth-token"];
               $rootScope.loggedStatus = true;
               $rootScope.loggedInUser = $scope.username;
               $rootScope.userRole = response.data.userRole.id;
               $rootScope.userName = response.data.userName;
                $rootScope.authToken = response.headers["x-auth-token"];
               // $rootScope.permissions = response.data.permissions;

               var userInfo = {
                    authToken: response.headers["x-auth-token"],
                    email: $rootScope.loggedInUser,
                    name: $rootScope.userName,
                    userRoles:  $rootScope.userRole,
                };
              
            $sessionStorage.userInfo =  userInfo;
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
            console.log(error);
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
