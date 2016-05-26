  
    rootApp.controller('metricsCtrl', function ($scope, $rootScope, $state, $mdSidenav, dropdownListService, projectService,appNotifyService,$window,$location,metricsService, commentService, editableOptions, $mdSidenav) {
		
    	$rootScope.bodyClass = 'body-standalone1';
	    $rootScope.navClass = 'nav-control';
	    $rootScope.navstatusClass = 'nav navbar-nav';
		 
		$rootScope.statusClass = 'status-nav';
	    $rootScope.dashboardClass = 'dashboard-current';
	    $rootScope.settingClass = 'setting-nav';
		 
		 //grpg
		/*$scope.labels = ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul","Sept","Oct","Nov","Dec"];
        $scope.series = ['Series A', 'Series B'];
        $scope.data = [
        [65, 59, 80, 81, 56, 55, 40],
        [28, 48, 40, 19, 86, 27, 90]
        ];*/
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
                 labels = response.data.DateList.reverse();
                  $scope.labels = labels;
                 angular.forEach(response.data.gridData, function(value, key) {
                    
                    $scope.series.push(value.projectName);
                    var temp = [];
                    angular.forEach(value.projectUserCountStatsDTOList, function(value, key) {

                                    temp.push(value.hours);
                     });
                    $scope.data.push(temp);
                     });
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