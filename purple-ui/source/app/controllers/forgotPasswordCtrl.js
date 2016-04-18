/*
* Forgot password controller 
*/
rootApp.controller('forgotPasswordCtrl', function ($scope, $rootScope, $state, forgotPasswordService,appNotifyService) {

    $rootScope.bodyClass = 'body-standalone';
    
    $scope.submitForgotPasswordForm = function (isValid) {
        if (!isValid) {     
            appNotifyService.error('Please enter valid inputs', 'Invalid inputs');
                return false;
            }
        
        forgotPasswordService.submitForgotPassword($scope.user).then(function (response) {
         if (response.success == true) {
                
             appNotifyService.success('Your reset password form has been sent to your email.');
                $state.go('login');   
            } 
           
        }, function (error) {
            if(error.reason =="account_inactive") {       
                appNotifyService.error('Please first activate you account');
             }
             else{
                appNotifyService.error('This email id is not registered with purple App');
             }
        });
    };  
});
