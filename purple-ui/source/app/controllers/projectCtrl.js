/**
 * 
 */
rootApp.controller('projectCtrl', function ($scope, $rootScope, $state, projectService,appNotifyService,adminService, $uibModal,statusService,$window) {
	 $rootScope.bodyClass = 'body-standalone1';
	 $scope.showpage = false;
    $scope.createProject = function (project) {
    	
       
        // Validating  form
    	
    	
    	$scope.userEmailsList = [];
    	angular.forEach($scope.newList, function(user){
    		$scope.userEmailsList.push(user.email);
    	});
    	$scope.project.users = $scope.userEmailsList;
        // Calling login service
    	
        	projectService.createProject($scope.project).then(function (response) {
        	 if (response.success) {
        		 $scope.project='';
        		 $scope.newList='';
        		 $scope.loadUsers();
        		
            } else {
                //Login failed. Showing error notification
                appNotifyService.error(response.data, 'error while creating project.');
            }

        }, function (error) {
            //Login failed. Showing error notificationvar
            appNotifyService.error(error.msg, 'Invite unsuccesfull.');
        });
        
    };
    
    
    
    $scope.loadUsers = function(){
    	projectService.getAllprojects().then(function (response) {
            if (response.success) {
            	if (response) {
					$scope.userscount = response.data.length;
				}
            	$scope.usersCount = response.data.gridData.usersCount;
            	$scope.projectCount = response.data.project_count;
//            	 /repeatSelect: null,
                $scope.allprojects   =  response.data.gridData;
            }
        });
    }
   
    $scope.initProjects = function() {
        $scope.loadUsers();
    };
    
    /* Method to get Loggedin user projects for status*/
    
    $scope.loadUsersProjects = function(){
    	projectService.getUserproject().then(function (response) {
            if (response.success) {
           
                $scope.userprojects   =  response.data.gridData;
            }
        });
    }
    $scope.initUserProjects = function() {
        $scope.loadUsersProjects();
    };
    
    
    $scope.getAllUseresInModal = function(){
    	
    	var uibModalInstance = $uibModal.open({
            animation: true,
            templateUrl: 'app/partials/allusers-modal.html',
            controller: 'allusersModalCtrl',
            backdrop: 'static',
            size: 'md',
            resolve: {
                options: function () {
                    return {
                        "title": 'Add Users',
                        	"data":$scope.allusers
                    };
                }
            }
        });
    	
    uibModalInstance.result.then(function (selection) {
    	$scope.newList = [];
    	angular.forEach($scope.allusers, function(user){
    		if(selection.indexOf(user.id) > -1)
    		{
    			$scope.newList.push(user);
    		}
    	});
    	console.log($scope.newList);
    });
    }
    
    
    
    
    /*to delete user from modal*/
    $scope.deleteUserFromList =function(id,$index){
     	
	    $scope.newList.splice($index, 1);
	    
	    }
  /*To delete user from ui*/  
    $scope.deleteOrgUserFromList =function(id,$index){
     	
	    $scope.orgUserList.splice($index, 1);
	    
	    }
    $scope.initUser = function(){
    	projectService.getAllUsersToAdd().then(function (response) {
            if (response.success =true) {
            	if (response) {
					$scope.userscount = response.data.length;
				}
            	$scope.joinedCount = response.data.joined_count ;
            	$scope.pendingCount = response.data.pending_count;
                $scope.allusers   =  response.data.gridData;
              
            }
        });
    }
 
    $scope.viewMembers = function(userList){
		console.log(userList);
		$scope.membersList=userList;
		$("#myModal").modal('show');
	
	}
    
    $scope.editProject = function(id){
		console.log(id);
		
		projectService.getProjectWithId(id).then(function (response) {
			if (response.success =true) {
				$scope.singleProjectData=response.data.gridData;
				$("#myModalEdit").modal('show');
			
			}
		
				});
				
	}
   
    /*update */
    $scope.showEditPage = function(id) {
		
		projectService.getProjectWithId(id).then(function (response) {
			if (response.success =true) {
				
				$scope.data=response.data;
				$scope.orgUserList=response.data.userList;
				$scope.showpage = true;
				
			}
		
				});

	}
    
   
    
  $scope.getAllUseresInUpdateModal = function(projid){
	  
	
	    	projectService.getMoreUsersToAdd(projid).then(function (response) {
	            if (response.success =true) {
	                $scope.allmoreusers   =  response.data.gridData;
	              
	            }
	        });
	   
    	
    	var uibModalInstance = $uibModal.open({
            animation: true,
            templateUrl: 'app/partials/usersupdate-modal.html',
            controller: 'updateModalCtrl',
            backdrop: 'static',
            size: 'md',
            resolve: {
                options: function () {
                    return {
                        "title": 'Add Users',
                        	"data":$scope.allmoreusers
                    };
                }
            }
        });
    	
    uibModalInstance.result.then(function (selection) {
    	$scope.newupdateList = [];
    	angular.forEach($scope.allmoreusers, function(user){
    		if(selection.indexOf(user.email) > -1)
    		{
    			$scope.orgUserList.push(user);
    		}
    	});
    	console.log($scope.orgUserList);
    });
    }
  
  $scope.updateProject = function () {
  	
      
      // Validating  form
  	
  
  $scope.updateuserEmailsList = [];
  	angular.forEach($scope.orgUserList, function(user){
  		$scope.updateuserEmailsList.push(user.email);
  	});
  //	$scope.project.users = scope.up$dateuserEmailsList;
  	
      // Calling login service
  	
  	//var res = data.split();
	  $scope.updateData = {
			  projectName : $scope.data.projectName,
			  timePreference : $scope.data.timePreference,
			  users : $scope.updateuserEmailsList
	  }
	  var id=$scope.data.projectId;
      	projectService.updateProject($scope.updateData,id).then(function (response) {
      	 if (response.success) {
      		 $scope.updateData='';
      		 $scope.loadUsers();
      		
          } else {
              //Login failed. Showing error notification
              appNotifyService.error(response.data, 'Invite unsuccesfull.');
          }

      }, function (error) {
          //Login failed. Showing error notificationvar
          appNotifyService.error(error.msg, 'Invite unsuccesfull.');
      });
      
  };
    $scope.submitStatus = function () {
    	
    	
        /* if (!isValid) {
         	appNotifyService.error('Please enter email and username', 'Invalid inputs');
             return false;
         }*/
         // Validating  form
     	
         
         // Calling login service
    	//All functions related to status have to write in statusCtrl time being written here
       
        statusService.submitStatus($scope.status).then(function (response) {
         	 if (response.success =true) {
         		 
         		$scope.initStatus();
         		
         		
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
            
                 $scope.allstatus   =  response.data.gridData;
             }
         });
     }
     
   
     
     $scope.initStatus = function() {
         $scope.usersstatusLoad();
     };
     
     $scope.deleteStatus =function(id,$index){
     	
		  statusService.deleteStatus(id).then(function (response) {
	    		if (response.success =true) {
	    		appNotifyService.success( 'Status has been deleted.');
	    	}
	    $scope.allstatus.splice($index, 1);
	    });
	    }
     
     /*method to edit status*/
     $scope.editStatus = function(id){
 		projectService.getStatuWithId(id).then(function (response) {
 			if (response.success =true) {
 				$scope.singleStatusData=response.data.gridData;
 				$("#myModalEdit").modal('show');
 			
 			}
 		
 				});
 				
 	}
     $scope.deleteProject =function(id,$index){
    	 projectService.deleteProject(id).then(function (response) {
     		if (response.success =true) {
     			appNotifyService.success( 'Project has been deleted.');
     	}
     		$scope.allprojects.splice($index, 1);
     });
     }
	 
	 $scope.show = true;
     $scope.closeAlert = function() {
     $scope.show = false;
   };

    $scope.alert = {type: 'alert-info'};
    
	 {
  $scope.time = new Date(1970, 0, 1, 10, 30, 40);
  $scope.selectedTimeAsNumber = 10 * 36e5 + 30 * 6e4 + 40 * 1e3;
  $scope.selectedTimeAsString = '10:00';
  $scope.sharedDate = new Date(new Date().setMinutes(0, 0));
}



 
  
  
  
});


