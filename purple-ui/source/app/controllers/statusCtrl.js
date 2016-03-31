/**
 * 
 */
rootApp.controller('statusCtrl', function ($scope, $rootScope, $state,$sessionStorage,statusService, projectService,appNotifyService,$window,$location) {
	 $rootScope.bodyClass = 'body-standalone1';
	 
    $scope.submitStatus = function () {
    	
       /* if (!isValid) {
        	appNotifyService.error('Please enter email and username', 'Invalid inputs');
            return false;
        }*/
        // Validating  form
    	
        
        // Calling login service
       statusService.submitStatus($scope.status).then(function (response) {
        	 if (response.success = true) {
        		// $scope.loadUsers();
        		 $scope.status = '';
        		 $scope.usersstatusLoad();
          		
            } else {
                //Login failed. Showing error notification
                appNotifyService.error(response.data, 'Invite unsuccesfull.');
            }

        }, function (error) {
            //Login failed. Showing error notification
            appNotifyService.error(error.msg, 'Invite unsuccesfull.');
        });
        
    };
    
    $scope.usersstatusLoad = function(){
   	 statusService.getAllStatus().then(function (response) {
            if (response.success) {
            	 $scope.artists = [];
            	    angular.forEach(response.data.gridData, function(value, key) {
            	        $scope.artists.push(value);
            	    });
                $scope.allstatus   =  response.data.gridData.date;
            }
        });
    }
    
   
    
    $scope.initStatus = function() {
        $scope.usersstatusLoad();
    };
    
    $scope.initProjects = function() {
        $scope.loadUsers();
    };
    $scope.loadUsers = function(){
    	projectService.getAllprojects().then(function (response) {
            if (response.success) {
            	if (response) {
					$scope.userscount = response.data.length;
				}
            	//$scope.joinedCount = response.data.joined_count ;
            	//$scope.pendingCount = response.data.pending_count;
                $scope.allprojects   =  response.data;
            }
        });
    }
    
    
    

   
    
    $scope.deleteUser =function(email){
    	adminService.deleteUser(email).then(function (response) {
    		if (response.success) {
    			//$route.reload();
    			$window.location.reload();
    	}
    });
    }
    
});
