rootApp.controller('signupCtrl', function ($scope, $rootScope, $state, signupService) {
console.log("message");
    $scope.submitSignUp = function (isValid) {
   console.log("message");
        // Validating login form
        if (!isValid) {
           console.log('Please enter username and password', 'Invalid inputs');
            return false;
        }


       signupService.submitSignUp().then(function(response){


        // Calling Signup service
        

        }), function (error) {
            //Login failed. Showing error notification
            //appNotifyService.error(error.msg, 'Login Failed.');
        }
   }
});
