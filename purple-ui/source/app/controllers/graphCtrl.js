  
    rootApp.controller('graphCtrl', function ($scope, $rootScope, $state, $mdSidenav, dropdownListService, projectService,appNotifyService,$window,$location,statusService, commentService, editableOptions, $mdSidenav) {
		
    	$rootScope.bodyClass = 'body-standalone1';
	    $rootScope.navClass = 'nav-control';
	    $rootScope.navstatusClass = 'nav navbar-nav';
		 
		 //grpg
		$scope.labels = ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul","Sept","Oct","Nov","Dec"];
        $scope.series = ['Series A', 'Series B'];
        $scope.data = [
        [65, 59, 80, 81, 56, 55, 40],
        [28, 48, 40, 19, 86, 27, 90]
        ];
        $scope.onClick = function (points, evt) {
        console.log(points, evt);
        };
		
		 /**
      * Load all projects of logged in user
      */
     $scope.loadUserProjects = function(){
     	projectService.getUserproject().then(function (response) {
     			var	projectIds = [];
             if (response.success) {
                 $scope.userprojects   =  response.data.gridData;
                 angular.forEach(response.data.gridData, function(value, key) {
                	 projectIds.push(value.projectId);
                     });
             }
     		 projectService.getprojectsUsers(projectIds).then(function (response) {
     	            if (response.success) {
     	            	$scope.projectUsers = response.data.gridData;
     	            }
     	            });
         });
     }
	 
	 	//nav active
     $scope.setClickedRow = function(index){  //function that sets the value of selectedRow to current index
     $scope.selectedRow = index;
	 $scope.selectedUser = null;
  }
  
  $scope.setClickedUser = function(index){  //function that sets the value of selectedRow to current index
     $scope.selectedUser = index;
	 $scope.selectedRow = null;
  };
	 
    });