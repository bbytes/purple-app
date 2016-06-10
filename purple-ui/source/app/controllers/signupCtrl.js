/*
* Sign Up Controller
*/
rootApp.controller('signupCtrl', function ($scope, $rootScope, $state, signupService,appNotifyService,$sessionStorage) {
  
    $rootScope.bodyClass = 'body-standalone';
      $rootScope.feedbackClass = 'feedback-log';
    $scope.submitSignUp = function (isValid) {
    	if (!isValid) {
            appNotifyService.error('Please enter valid inputs');
            return false;
            }

        // Validating login form
        signupService.submitSignUp($scope.user).then(function (response) {
         if (response.success == true) {
                
                appNotifyService.success('An email has been sent to your registered email-id for activation. Please check!');
                $state.go('login');
            } 
           
        }, function (error) {
        	 if(error.reason =="organization_not_unique") {
                 //Login failed. Showing error notification
                 appNotifyService.error('Looks like somebody has already signed-up from your Organization!');
             }
        	 else if(error.reason =="email_not_unique") {
                 //Login failed. Showing error notification
                 appNotifyService.error('Looks like this email is already registered with us.');
             }
        	 else{
            //Login failed. Showing error notification
            appNotifyService.error(error.msg, 'Oops!! Registration has Failed. Please try after sometime!');
        }
    });
    };
});
