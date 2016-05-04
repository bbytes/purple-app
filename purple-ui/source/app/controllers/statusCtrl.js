/**
 * Status Controller
 */
rootApp.controller('statusCtrl', function($scope, $rootScope, $state,
		$sessionStorage, statusService, projectService, appNotifyService,
		$window, $location,settingsService,$filter) {
			
	$rootScope.bodyClass = 'body-standalone1';
	$scope.isSubmit = true;
	$scope.submitStatus = function() {

		var status = new Object();
		status.projectId = $scope.project;
		status.hours = $scope.hours;
		status.workingOn = $scope.workingOn;
		status.workedOn = $scope.workedOn;
		status.blockers = $scope.blockers;
		if(!status.projectId){
			appNotifyService.error('Please select a valid project');
            return false;
		}
		else if(!status.hours){
			appNotifyService.error('Please fill in the hours for the selected project.');
            return false;
		}

		statusService.submitStatus(status).then(function(response) {
			if (response.success = true) {

				$scope.clearStatus();
				$scope.usersstatusLoad();
			}

		}, function(error) {
			if(error.response == "hours_exceeded"){
				appNotifyService.error('You have already logged in 24hrs for the day!');
			}
			else{
				appNotifyService.error('Error while submitting status');
			}	
		});
	};

	$scope.usersstatusLoad = function() {

		settingsService.getConfigSetting().then(function(response){
         if (response.success = true) 
             $rootScope.statusEnable = response.data.statusEnable;
       }, function(error){
       });

		statusService.getAllStatus().then(function(response) {
			if (response.success) {

				$scope.artists = [];
				$rootScope.dateArr = [];

				var dateResult,statusEnable,i;
				statusEnable = $rootScope.statusEnable;
					
				for(i=0;i<=statusEnable;i++){
					dateResult = $filter('date')(new Date().setDate(new Date().getDate()-i));	
					$scope.dateArr.push(dateResult);
				}
				angular.forEach(response.data.gridData, function(value, key) {
					
					$scope.artists.push(value);
					$scope.allstatus = value.statusList;
				});
			}
		});
	}
	$scope.showEditIcon = function(date){
		var result = false;
		angular.forEach($rootScope.dateArr,function(value)
		{
			if(date.valueOf() == value.valueOf())
				result = true;
		});
		return result;
	};
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
				appNotifyService.success('Status has been successfully deleted.');
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
		
		if(!newstatus.projectId){
			appNotifyService.error('Please select a valid project');
            return false;
		}
		else if(!newstatus.hours){
				appNotifyService.error('Please fill in the hours for the selected project.');
				return false;
		}
		statusService.updateStatus(newstatus, id).then(function(response) {
			if (response.success = true) {
		
				$scope.clearStatus();
				$scope.usersstatusLoad();
				$scope.isSubmit = true;
				$scope.isUpdate = false;
			} 

		}, function(error) {
			if(error.response == "hours_exceeded"){
				appNotifyService.error('You have already logged in 24hrs for the day!');
			}
			else{
				appNotifyService.error('Error while updating status');
			}
		});		
	};

	// Clearing the text area for status page
	$scope.clearStatus = function(){

			$scope.project = '';
			$scope.hours = '';
			$scope.workingOn = '';
			$scope.workedOn = '';
			$scope.blockers = '';
	}
});