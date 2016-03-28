/**
 * 
 */
rootApp.controller('adminCtrl', function ($scope, $rootScope, $state, adminService,appNotifyService,$window) {
	
	 $rootScope.bodyClass = 'body-standalone1';
    $scope.invite = function (isValid) {
    	
        if (!isValid) {
        	appNotifyService.error('Please enter email and username', 'Invalid inputs');
            return false;
        }
        // Validating  form
    	if($scope.admin == null){
    		appNotifyService.error('Please enter email or username ', 'Not entered inputs');
    	}
    
        else{
        // Calling login service
        adminService.inviteUser($scope.admin).then(function (response) {
        	 if (response.success) {
        		 $scope.loadUsers();
            } else {
                //Login failed. Showing error notification
                appNotifyService.error(response.data, 'Invite unsuccesfull.');
            }

        }, function (error) {
            //Login failed. Showing error notification
            appNotifyService.error(error.msg, 'Invite unsuccesfull.');
        });
        }
    };
    
    $scope.loadUsers = function(){
    	adminService.getAllusers().then(function (response) {
            if (response.success) {
            	if (response) {
					$scope.userscount = response.data.length;
				}
            	$scope.joinedCount = response.data.joined_count ;
            	$scope.pendingCount = response.data.pending_count;
                $scope.allusers   =  response.data.gridData;
            }
        });
    }
    
    $scope.initUser = function() {
        $scope.loadUsers();
    };
    
    $scope.deleteUser =function(email, $index){
    	adminService.deleteUser(email).then(function (response) {
    		if (response.success) {
    			//$route.reload();
    			//$window.location.reload();
    			appNotifyService.success( 'User has been deleted.');
    			
    	}
    		$scope.allusers.splice($index, 1);
    });
    }
    
});
