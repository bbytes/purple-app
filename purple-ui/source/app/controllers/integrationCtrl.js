/**
 * Admin Controller
 */
rootApp.controller('integrationCtrl', function ($scope, $rootScope, $state, $window, appNotifyService, integrationService) {
	
	$scope.isActive = function(route) {
		return route === $location.path();
	}

	$rootScope.navClass = 'nav navbar-nav';
	$rootScope.navstatusClass = 'right-nav-ct';
	$rootScope.bodyClass = 'body-standalone1';
    
    $scope.connectToJira = function(){

    	 var dataObj = new Object();
        dataObj.userName = $scope.username;
        dataObj.password = $scope.password;
        dataObj.jiraBaseUrl = $scope.jiraurl;

        integrationService.connectToJira(dataObj).then(function (response) {
                     if (response.success) {
                        appNotifyService.success('You have been connected to JIRA');  
                         $scope.isConnected = true; 
                        $scope.isOffline = false;   
                 }
             }, function(error) {
             			$scope.isConnected = false; 
                        $scope.isOffline = true;
			if (error.reason == "bad_gateway"){
				appNotifyService.error('You have entered invalid JIRA URL');
			}
			else if (error.reason == "authentication_failure"){
				appNotifyService.error('User is not authorized');
			}
			else{
				appNotifyService.error('Error while connecting to JIRA');
			}	
		});

    };

     $scope.getJiraConnection = function(){

        integrationService.getJiraConnection().then(function (response) {
                     if (response.success) { 
                        $scope.isConnected = true; 
                        $scope.isOffline = false;      
                 }
             }, function(error) {
             			$scope.isConnected = false; 
                        $scope.isOffline = true; 
				
		});

    };

     $scope.syncJiraProject = function(){

        integrationService.getJiraProject().then(function (response) {
                     if (response.success) {
                        appNotifyService.success('Connected to JIRA');  
                        
                 }
             }, function(error) {
             			 
			if (error.reason == "bad_gateway"){
				appNotifyService.error('You have entered invalid JIRA URL');
			}
			else if (error.reason == "authentication_failure"){
				appNotifyService.error('User is not authorized');
			}
			else{
				appNotifyService.error('Error getting JIRA Connection');
			}	
		});

    };


});
