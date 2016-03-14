rootApp.controller('signupCtrl', function ($scope, $rootScope, $state, signupService,appNotifyService) {
  appNotifyService.error('this is test');
console.log("message");
    $scope.submitSignUp = function (isValid) {
   console.log("message");
        // Validating login form
        if (!isValid) {
             console.log("message");
           growl.error('Please enter valid inputs');
            return false;
        }

        

        signupService.submitSignUp($scope.user).then(function (response) {
         if (response.success == true) {
                
                
                $state.go('home');
                
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
