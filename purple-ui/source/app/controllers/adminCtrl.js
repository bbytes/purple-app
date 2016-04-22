/**
 * Admin Controller
 */
rootApp.controller('adminCtrl', function ($scope, $rootScope, $state, adminService,appNotifyService,$window) {
	
	 $rootScope.bodyClass = 'body-standalone1';
    $scope.invite = function (isValid) {
        if (!isValid) {
        	appNotifyService.error('Please enter email and username');
            return false;
        }
        
    	if($scope.admin == null){
    		appNotifyService.error('Please enter email or username ', 'Not entered inputs');
    	}
        else{
        adminService.inviteUser($scope.admin).then(function (response) {
       
        	 if (response.success) {
        		 appNotifyService.success('Activation link has been sent to added  email.');
        		 $scope.loadUsers();
                 $scope.admin = '';
                $scope.isSubmitted = true;
        		 
            } else {
                appNotifyService.error(response.data, 'Invite unsuccesfull.');
            }

        }, function (error) {
        	 if(error.reason =="user_not_found") {
                 appNotifyService.error('Username or Email already exist');
             }
        	 else{
              appNotifyService.error(error.msg, 'Error while adding users..');
        	 }
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
    			appNotifyService.success( 'User has been deleted.');
    	}
    		$scope.allusers.splice($index, 1);
    });
    }
});
