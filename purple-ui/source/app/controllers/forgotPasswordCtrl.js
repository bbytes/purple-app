/*
* Forgot password controller 
*/
rootApp.controller('forgotPasswordCtrl', function ($scope, $rootScope, $state, forgotPasswordService,appNotifyService) {

    $rootScope.bodyClass = 'body-standalone';
    
    $scope.submitForgotPasswordForm = function (isValid) {
        if (!isValid) {
            
            appNotifyService.error('Please enter valid inputs', 'Invalid inputs');
               console.log('Please enter username and password', 'Invalid inputs');
                return false;
            }
        
        forgotPasswordService.submitForgotPassword($scope.user).then(function (response) {
         if (response.success == true) {
                
             appNotifyService.success('Your reset password form has been sent to your email.');
                $state.go('login');   
            } 
           
        }, function (error) {
            console.log(error);
             if(error.reason =="user_not_found") {
                 //Login failed. Showing error notification
                 appNotifyService.error('You are not registered with purple application');
             }
              else if(error.reason =="account_inactive") {
                 //Login failed. Showing error notification
                 appNotifyService.error('Please first activate you account');
             }
             else{
            //Login failed. Showing error notification
            appNotifyService.error(error.msg, 'Forgot password Failed.');
        }
    });
    };  
});
