rootApp.controller('signupCtrl', function ($scope, $rootScope, $state, signupService,appNotifyService,$sessionStorage) {
  
    $rootScope.bodyClass = 'body-standalone';
    
    $scope.submitSignUp = function (isValid) {
    	if (!isValid) {
            appNotifyService.error('Please enter valid inputs', 'Invalid inputs');
               console.log('Please enter username and password', 'Invalid inputs');
                return false;
            }

        // Validating login form
    	
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
                
            } 
           
        }, function (error) {
        	 if(error.reason =="organization_not_unique") {
                 //Login failed. Showing error notification
                 appNotifyService.error('Oops!!..Organization is already exist.');
             }
        	 else if(error.reason =="email_not_unique") {
                 //Login failed. Showing error notification
                 appNotifyService.error('Email is already exist.Please enter new email ');
             }
        	 else{
            //Login failed. Showing error notification
            appNotifyService.error(error.msg, 'Registration  Failed.');
        }
    });
    };
});
