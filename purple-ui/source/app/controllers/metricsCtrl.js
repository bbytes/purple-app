  
    rootApp.controller('metricsCtrl', function ($scope, $rootScope, $state, $mdSidenav, dropdownListService, projectService,appNotifyService,$window,$location,metricsService, commentService, editableOptions, $mdSidenav) {
		
    	$rootScope.bodyClass = 'body-standalone1';
	    $rootScope.navClass = 'nav-control';
	    $rootScope.navstatusClass = 'nav navbar-nav';
		 
		$rootScope.statusClass = 'status-nav';
	    $rootScope.dashboardClass = 'dashboard-current';
	    $rootScope.settingClass = 'setting-nav';
		  
        $scope.onClick = function (points, evt) {
        console.log(points, evt);
        };
		
        $scope.loadTimePeriod = function(){

            dropdownListService.getTimePeriod().then(function(response){
            $scope.timePeriod = response.data;
            $scope.mytime = response.data[2].value;
          
              }, function(error){
        });
         
        }

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

      $scope.loadAllStatusGraph = function(){

        time = "Weekly";
         $scope.updateData = {
              projectList :[],
              userList : []
      }
        metricsService.getAllStatusAnalytics($scope.updateData,time).then(function (response) {
            
            $scope.labels = [];
            $scope.series = [];
            $scope.data = [];

             if (response.success) {
        
                      $scope.labels = response.data.labels;
                      $scope.series = response.data.series;
                      $scope.data = response.data.data;
             }
             
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