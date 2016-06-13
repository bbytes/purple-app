/**
 *  Login Controller
 */
rootApp.controller('loginCtrl', function ($scope, $rootScope, $state, loginService,appNotifyService,$sessionStorage,$window) {
    
    $rootScope.bodyClass = 'body-standalone';
      $rootScope.feedbackClass = 'feedback-log';
	
    $scope.submitLoginForm = function (isValid) {

        // Validating login form
        if (!isValid) {
        appNotifyService.error('Please enter username and password');
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
                $rootScope.timePreference = response.data.timePreference;
                $rootScope.switchState = response.data.emailNotificationState;
                $rootScope.timeZone = response.data.timeZone;
                $rootScope.current_date = new Date(); 

               var userInfo = {
                    authToken: response.headers["x-auth-token"],
                    email: $rootScope.loggedInUser,
                    name: $rootScope.userName,
                    userRoles:  $rootScope.userRole,
                    timePreference :  $rootScope.timePreference,
                    emailNotificationState : $rootScope.switchState,  
                    timeZone : $rootScope.timeZone,
                    displayDate : $rootScope.current_date,
                };
              
            $sessionStorage.userInfo =  userInfo;
                $rootScope.showWelcomeMessage = true;
                
                $state.go('status');
            } else {
            	  // Erase the token if the user fails to log in
            	 delete $window.sessionStorage.token;
                //Login failed. Showing error notification
                appNotifyService.error('Please activate your account before login. Check your email for activation link.');
            }

        }, function (error) {
            //Login failed. Showing error notification
            appNotifyService.error('Invalid Username or Password');
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
