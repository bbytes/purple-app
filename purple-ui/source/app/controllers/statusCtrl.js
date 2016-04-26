/**
 * Status Controller
 */
rootApp.controller('statusCtrl', function($scope, $rootScope, $state,
		$sessionStorage, statusService, projectService, appNotifyService,
		$window, $location) {
			
	$rootScope.bodyClass = 'body-standalone1';
	$scope.isSubmit = true;
	$scope.submitStatus = function() {

		var status = new Object();
		status.projectId = $scope.project;
		status.hours = $scope.hours;
		status.workingOn = $scope.workingOn;
		status.workedOn = $scope.workedOn;
		status.blockers = $scope.blockers;
		statusService.submitStatus(status).then(function(response) {
			if (response.success = true) {

				$scope.clearStatus();
				$scope.usersstatusLoad();
			}

		}, function(error) {
			appNotifyService.error(error.msg);
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
		projectService.getUserproject().then(function(response) {
			if (response.success) {
				$scope.selectables = [1,1.5,2,2.5,3,3.5,4,4.5,5,5.5,6,6.5,7,7.5,8,8.5,9,9.5,10,10.5,11,11.5,12];
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
					$scope.project = $scope.allstatus[0].projectId;
					$scope.hours = $scope.allstatus[0].hours;
					$scope.workingOn = $scope.allstatus[0].workingOn;
					$scope.workedOn = $scope.allstatus[0].workedOn;
					$rootScope.statusId = $scope.allstatus[0].statusId;
					$scope.blockers = $scope.allstatus[0].blockers;
					$scope.isUpdate = true;
					$scope.isSubmit = false;
					$scope.loadProjects();	
				});
			}
		});
	}
	//ends

	$scope.updateStatus = function() {
		
		var id = $rootScope.statusId;
		var newstatus =new Object();
		newstatus.projectId = $scope.project;
		newstatus.hours = $scope.hours;
		newstatus.workingOn = $scope.workingOn;
		newstatus.workedOn = $scope.workedOn;
		newstatus.blockers = $scope.blockers;
		$scope.isSubmit = true;
		$scope.isUpdate = false;

		statusService.updateStatus(newstatus, id).then(function(response) {
			if (response.success = true) {
		
				$scope.clearStatus();
				$scope.usersstatusLoad();
			} 

		}, function(error) {
			appNotifyService.error(error.msg);
		});		
	};

	$scope.clearStatus = function(){

			$scope.project = '';
			$scope.hours = '';
			$scope.workingOn = '';
			$scope.workedOn = '';
			$scope.blockers = '';
	}
});
