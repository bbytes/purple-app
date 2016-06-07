/*
* Forgot password controller 
*/
rootApp.controller('forgotPasswordCtrl', function ($scope, $rootScope, $state, forgotPasswordService,appNotifyService) {

    $rootScope.bodyClass = 'body-standalone';
    
    $scope.submitForgotPasswordForm = function (isValid) {
        if (!isValid) {     
            appNotifyService.error('Please enter a valid email-id.');
                return false;
            }
        
        forgotPasswordService.submitForgotPassword($scope.user).then(function (response) {
         if (response.success == true) {
                
             appNotifyService.success('Reset password email has been sent. Please follow instructions to reset password.');
                $state.go('login');   
            } 
        }, function (error) {

            if(error.reason =="account_inactive") {       
                appNotifyService.error('Please activaite your account before trying to reset password.');
             }
             else{
                appNotifyService.error('This email id is not registered with us. Please sign-up!');
             }
        });
    };  
});
