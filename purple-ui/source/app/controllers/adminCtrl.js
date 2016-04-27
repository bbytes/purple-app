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
            $scope.loadUsers();
    });
    }

    // Method for bulk upload 
     $scope.bulkupload = function (element) {
        var file = element.files[0];
         var fd = new FormData();
            fd.append('file', file);
              adminService.bulkupload(fd).then(function (response) {
                var len = response.data.length;
         if (response.success) {

                if(len > 0){
                 appNotifyService.success('Activation link has been sent to '+len+' users.');
                }
                else{
                    appNotifyService.success('All users are already exist');
                }
                 $scope.loadUsers();        
            } 

        }, function (error) {
             if(error.reason =="add_user_failed") {
                 appNotifyService.error('Some of users are already exist');
             }
             else{
              appNotifyService.error('Invalid file format');
             }
        });
     }
});
