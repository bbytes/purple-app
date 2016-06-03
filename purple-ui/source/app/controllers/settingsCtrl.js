/**
 * User setting controller
 */
rootApp.controller('settingsCtrl', function ($scope, $rootScope, $state, settingsService,appNotifyService, cfpLoadingBar) {
    
	$rootScope.bodyClass = 'body-standalone1';
	$rootScope.navClass = 'nav-control';
	$rootScope.navstatusClass = 'nav navbar-nav';
	
	$rootScope.statusClass = 'status-nav';
	$rootScope.dashboardClass = 'dashboard-nav';
	$rootScope.settingClass = 'setting-current';
    // Reset password for user
    $scope.updatePassword = function (user,confirmPassword) {
    	
        var oldPassword=user.oldPassword;
        var newPassword=user.newPassword;
        var confirmpassword=$scope.confirmPassword;
      if( confirmpassword == newPassword && oldPassword != null){
      
        settingsService.updatePassword($scope.user).then(function (response) {
             if (response.success = true) {
                 appNotifyService.success('Your password has been successfully changed.');
                 $scope.clearPasswordText(user,confirmPassword);
                 $scope.isFormSubmitted = true;
            } 
        }, function (error) {
            if(error.reason == 'password_mistach')
            appNotifyService.error('Current password is incorrect');
        });   
    }
    else if(newPassword != confirmpassword){
        appNotifyService.error('Password mismatch.');   
    }
    else{
        appNotifyService.error('Please enter your current password');
    }

    };
    
    $scope.updateSetting= function () {
    
        var settingObj = new Object();
        settingObj.timeZone = $scope.timeZone;
        settingObj.timePreference = $scope.timePreference;
        settingObj.emailNotificationState = $scope.switchState;

 	settingsService.updateSetting(settingObj).then(function (response) {
     	 if (response.success = true) {

            $rootScope.timePreference = response.data.timePreference;
            $rootScope.switchState = response.data.emailNotificationState;
            $rootScope.timeZone = response.data.timeZone;
     		 appNotifyService.success('Preference has been successfully updated.');   		 
         } 

     }, function (error) {
         appNotifyService.error('Please set a valid setting');
     });

 };
 
  $scope.loadSetting= function () {

	   $scope.timePreference =  $rootScope.timePreference;
       $scope.switchState =  $rootScope.switchState;
       $scope.timeZone = $rootScope.timeZone;
 };
    
	$("[name='my-checkbox']").bootstrapSwitch();
	
	//calendar
    $scope.uiConfig = {
      calendar:{
        //height: 350,
        editable: false,
        header:{
          left: 'prev',
          center: 'title',
          right: 'next'
        },
        dayClick: $scope.alertEventOnClick,
        eventDrop: $scope.alertOnDrop,
        eventResize: $scope.alertOnResize
      }
    };
     $scope.clearPasswordText = function(user,confirmPassword){
        
        user.oldPassword = '';
        $scope.confirmPassword = '';
        user.newPassword = '';
    } 

	
	 $scope.switchState= function () {

	   $scope.switchStatus =  $rootScope.switchStatus;
 };
	
});
