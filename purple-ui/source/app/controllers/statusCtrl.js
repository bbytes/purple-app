/**
 * 
 */
rootApp.controller('statusCtrl', function ($scope, $rootScope, $state,$sessionStorage,statusService, projectService,appNotifyService,$window,$location) {
	 $rootScope.bodyClass = 'body-standalone1';
	 
	 $scope.showeditpage = false;
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
        		// $scope.artists.push(value,1);
          		
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

            	        $scope.allstatus   =  value.statusList;
            	    });
              
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
    
    
    

   
    
    $scope.deleteStatus =function(id,$index){
     	
		  statusService.deleteStatus(id).then(function (response) {
	    		if (response.success =true) {
	    		appNotifyService.success( 'Status has been deleted.');
	    	}
	    $scope.allstatus.splice($index, 1);
	    });
	    }
    /*Update */
    
 $scope.showUpdatePage = function(id) {
		
		statusService.getStatusWithId(id).then(function (response) {
			if (response.success =true) {
				
				$scope.statusdata=response.data.gridData;
				$scope.newstatus = [];
        	    angular.forEach(response.data.gridData, function(value, key) {
        	        $scope.newstatus.push(value);

        	        $scope.allstatus   =  value.statusList;
        	        $scope.showeditpage = true;
        	    });
				
				
			}
		
				});

	}
 
 $scope.updateStatus = function (id,newsatus) {
 	
     /* if (!isValid) {
      	appNotifyService.error('Please enter email and username', 'Invalid inputs');
          return false;
      }*/
      // Validating  form
  	
      
      // Calling login service
     statusService.updateStatus(newsatus,id).then(function (response) {
      	 if (response.success = true) {
      		// $scope.loadUsers();
      		 $scope.status = '';
      		 $window.location.reload();
      		// $scope.artists.push(value,1);
        		
          } else {
              //Login failed. Showing error notification
              appNotifyService.error(response.data, 'Invite unsuccesfull.');
          }

      }, function (error) {
          //Login failed. Showing error notification
          appNotifyService.error(error.msg, 'Invite unsuccesfull.');
      });
      
  };
  
  
  $scope.isActive = function (viewLocation) {
     var active = (viewLocation === $location.path());
     return active;
};

    
});
