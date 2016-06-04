/**
 * Setting controller
 */

rootApp.controller('adminSettingsCtrl', function ($scope, $rootScope, $state, dropdownListService,settingsService,appNotifyService) {

    $rootScope.navClass = 'nav navbar-nav';
	$rootScope.navstatusClass = 'right-nav-ct';

    $scope.loadSetting = function (){

        dropdownListService.getStatusEnable().then(function(response){
            $scope.days = response.data;
        });

        settingsService.getConfigSetting().then(function(response){
         if (response.success = true) 
            $rootScope.statusEnable = response.data.statusEnable;
            $rootScope.weekendnotify = response.data.weekendNotification;
            $rootScope.capturehours = response.data.captureHours;
       }, function(error){
       });
    };
    // config setting method to save admin setting information
    $scope.configSetting = function(){
        var admin = new Object();
        admin.captureHours = $scope.capturehours;
        admin.weekendNotification = $scope.weekendnotify;
        admin.statusEnable = $scope.statusEnable;
        if(!admin.statusEnable){
            appNotifyService.error('Please select valid input');   
            return false;
        }

       settingsService.saveConfigSetting(admin).then(function(response){

         if (response.success = true) 
                 appNotifyService.success('Your Setting has been successfully saved.');
             $scope.statusEnable = response.data.statusEnable;
             $scope.weekendnotify = response.data.weekendNotification;
             $scope.capturehours = response.data.captureHours;
       }, function(error){
            appNotifyService.error("Error while saving setting.");
       });
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

});
