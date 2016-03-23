/**
 * 
 */
rootApp.controller('projectCtrl', function ($scope, $rootScope, $state, projectService,appNotifyService,adminService, $uibModal,statusService,$window) {

    $scope.createProject = function () {
    	
       
        // Validating  form
    	
    	
    	$scope.userEmailsList = [];
    	angular.forEach($scope.newList, function(user){
    		$scope.userEmailsList.push(user.email);
    	});
    	$scope.project.users = $scope.userEmailsList;
        // Calling login service
    	
        	projectService.createProject($scope.project).then(function (response) {
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
                        "title": 'All Users',
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
    
    $scope.initUser = function(){
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
 
    $scope.viewMembers = function(users){
		console.log(users);
		$scope.membersList=users;
		$("#myModal").modal('show');
	
	}
    
    
    
    $scope.submitStatus = function (status) {
    	var workedOn=status.workedOn;
        var workingOn=status.workingOn;
        var time =status.time;
        var projectId =status.projectId;
    	
        /* if (!isValid) {
         	appNotifyService.error('Please enter email and username', 'Invalid inputs');
             return false;
         }*/
         // Validating  form
     	
         
         // Calling login service
    	//All functions related to status have to write in statusCtrl time being written here
        if(workedOn !=null && workingOn !=null && time !=null && projectId !=null ){
        statusService.submitStatus($scope.status).then(function (response) {
         	 if (response.success = true) {
         		$scope.usersstatusLoad();
             } else {
                 //Login failed. Showing error notification
                 appNotifyService.error(response.data, 'Invite unsuccesfull.');
             }

         }, function (error) {
             //Login failed. Showing error notification
             appNotifyService.error(error.msg, 'Invite unsuccesfull.');
         });
        }
        else{
        	appNotifyService.error( 'Enter Required fields.');
        }
         
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
     $scope.deleteProject =function(id){
    	 projectService.deleteProject(id).then(function (response) {
     		if (response.success) {
     			//$route.reload();
     			$window.location.reload();
     	}
     });
     }
    
});
