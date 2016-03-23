/**
 * 
 */
rootApp.controller('statusCtrl', function ($scope, $rootScope, $state, projectService,appNotifyService,$window) {

    $scope.submitStatus = function () {
    	
       /* if (!isValid) {
        	appNotifyService.error('Please enter email and username', 'Invalid inputs');
            return false;
        }*/
        // Validating  form
    	
        
        // Calling login service
       statusService.submitStatus($scope.status).then(function (response) {
        	 if (response.success) {
        		// $scope.loadUsers();
            } else {
                //Login failed. Showing error notification
                appNotifyService.error(response.data, 'Invite unsuccesfull.');
            }

        }, function (error) {
            //Login failed. Showing error notification
            appNotifyService.error(error.msg, 'Invite unsuccesfull.');
        });
        
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
