rootApp.controller('loginCtrl', function ($scope, $rootScope, $state, loginService,appNotifyService) {

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
                
                $rootScope.loggedStatus = true;
                $rootScope.loggedIn = $scope.username;
                $rootScope.userName = response.data.name;
                $rootScope.authToken = response.headers["x-auth-token"];
                $rootScope.permissions = response.data.permissions;

                var userInfo = {
                    accessToken: response.headers["x-auth-token"],
                    id: $rootScope.loggedIn,
                    name: $rootScope.userName,
                    userRoles: response.data.userRoles,
                    permissions: response.data.permissions,
                    viewMode:$rootScope.viewMode
                };
                
                $sessionStorage.userInfo = userInfo;

                // Login successful, set user locale and Redirect to home page
                if(response.data.locale){
                    appLocaleService.setLocale(response.data.locale);
                }

                $rootScope.showWelcomeMessage = true;
                
                $state.go('home');
                
            } else {
                //Login failed. Showing error notification
                appNotifyService.error(response.data, 'Login Failed.');
            }

        }, function (error) {
            //Login failed. Showing error notification
            appNotifyService.error(error.msg, 'Login Failed.');
        });
    };
});
