/**
 * Admin Controller
 */
rootApp.controller('adminCtrl', function ($scope, $rootScope, $state, adminService,dropdownListService,appNotifyService,$window) {
	
	 $rootScope.navClass = 'nav navbar-nav';
	 $rootScope.navstatusClass = 'right-nav-ct';
	 $rootScope.bodyClass = 'body-standalone1';
    $scope.invite = function (isValid) {
        if (!isValid) {
        	appNotifyService.error('Please enter a valid email and username.');
            return false;
        }
        
    	if($scope.admin == null){
    		appNotifyService.error('Please enter a valid email and username.');
    	}
        else{
        adminService.inviteUser($scope.admin).then(function (response) {
       
        	 if (response.success) {
        		 appNotifyService.success('Activation link has been sent to your registered email.');
        		 $scope.loadUsers();
                 $scope.admin = '';
                $scope.isSubmitted = true;
        		 
            } else {
                appNotifyService.error('Invite unsuccesfull. Please check back again!');
            }

        }, function (error) {
        	 if(error.reason =="user_not_found") {
                 appNotifyService.error('Username or Email already exist');
             }
        	 else{
              appNotifyService.error('Error while adding users. Please check back again!');
        	 }
        });
        }
    };
    
    $scope.loadUsers = function(){
 
        dropdownListService.getRole().then(function(response){
                 $scope.userRoles = response.data;
            });
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
    			appNotifyService.success( 'User has been sucessfully deleted.');
    	}
    		$scope.allusers.splice($index, 1);
            $scope.loadUsers();
    });
    }
    
    // Method is used to assigning user role

    $scope.roleChange = function(userRole,user){

        var userId = user.id;
       adminService.updateUserRole(userId,userRole).then(function (response) {
            if (response.success) {
                appNotifyService.success( 'Users role has been sucessfully changed.');
        }
    },
        function (error) {
              appNotifyService.error('Error while assigning users role. Please check back again!');
             
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
                    appNotifyService.success('All users are already exist. Please check!');
                }
                 $scope.loadUsers();        
            } 

        }, function (error) {
             if(error.reason =="add_user_failed") {
                 appNotifyService.error('Some of users already exist.');
             }
             else{
              appNotifyService.error('Invalid file format');
             }
        });
     }

});
