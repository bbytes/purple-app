/**
 * Status Controller
 */
rootApp.controller('statusCtrl', function($scope, $rootScope, $state,
		$sessionStorage, statusService, projectService, appNotifyService,
		$window, $location) {
			
		$rootScope.bodyClass = 'body-standalone1';
		$scope.showeditpage = false;
	$scope.submitStatus = function() {

		// Calling login service
		statusService.submitStatus($scope.status).then(function(response) {
			if (response.success = true) {
				// $scope.loadUsers();
				$scope.status = '';
				$scope.usersstatusLoad();
				// $scope.artists.push(value,1);

			} else {
				//Login failed. Showing error notification
				appNotifyService.error(response.data, 'Invite unsuccesfull.');
			}

		}, function(error) {
			//Login failed. Showing error notification
			appNotifyService.error(error.msg, 'Invite unsuccesfull.');
		});
	};

	$scope.usersstatusLoad = function() {
		statusService.getAllStatus().then(function(response) {
			if (response.success) {

				$scope.artists = [];
				angular.forEach(response.data.gridData, function(value, key) {
					$scope.artists.push(value);

					$scope.allstatus = value.statusList;
				});
			}
		});
	}
	
	$scope.loadProjects = function() {
		projectService.getAllprojects().then(function(response) {
			if (response.success) {
				
				$scope.allprojects = response.data.gridData;
			}
		});
	}

	$scope.deleteStatus = function(id, $index) {
		statusService.deleteStatus(id).then(function(response) {
			if (response.success = true) {
				appNotifyService.success('Status has been deleted.');
			}
			$scope.allstatus.splice($index, 1);
			$scope.usersstatusLoad();
		});
	}
	/*Update */

	$scope.showUpdatePage = function(id) {

		statusService.getStatusWithId(id).then(function(response) {
			if (response.success = true) {

				$scope.statusdata = response.data.gridData;
				$scope.selectables = [1,1.5,2,2.5,3,3.5,4,4.5,5,5.5,6,6.5,7,7.5,8,8.5,9,9.5,10,10.5,11,11.5,12];
				
				angular.forEach(response.data.gridData, function(value, key) {

					$scope.allstatus = value.statusList;
					$scope.project = $scope.allstatus[0].projectName;
					$scope.hours = $scope.allstatus[0].hours;
					$scope.workingOn = $scope.allstatus[0].workingOn;
					$scope.workedOn = $scope.allstatus[0].workedOn;
					$rootScope.statusId = $scope.allstatus[0].statusId;
					$scope.blockers = $scope.allstatus[0].blockers;
					$scope.showeditpage = true;
					$scope.loadProjects();	
				});
			}
		});
	}
	//ends

	$scope.updateStatus = function() {
		
		var id = $rootScope.statusId;
		var newstatus =new Object();
		newstatus.projectName = $scope.project;
		newstatus.hours = $scope.hours;
		newstatus.workingOn = $scope.workingOn;
		newstatus.workedOn = $scope.workedOn;
		newstatus.blockers = $scope.blockers;
		
		// Calling login service
		statusService.updateStatus(newstatus, id).then(function(response) {
			if (response.success = true) {
		
				$scope.project = '';
				$scope.hours = '';
				$scope.workingOn = '';
				$scope.workedOn = '';
				$scope.blockers = '';
				$scope.usersstatusLoad();
			} else {
				//Login failed. Showing error notification
				appNotifyService.error(response.data, 'Invite unsuccesfull.');
			}

		}, function(error) {
			//Login failed. Showing error notification
			appNotifyService.error(error.msg, 'Invite unsuccesfull.');
		});		
	};
});
