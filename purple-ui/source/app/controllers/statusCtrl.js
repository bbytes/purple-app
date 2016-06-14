/**
 * Status Controller
 */
rootApp.controller('statusCtrl', function($scope, $rootScope, $state,
		$sessionStorage, dropdownListService, statusService, projectService, appNotifyService,
		$window, $location, settingsService, $filter, cfpLoadingBar) {
			
	$rootScope.bodyClass = 'body-standalone1';
	$rootScope.navClass = 'nav-control';
	$rootScope.navstatusClass = 'nav navbar-nav';
	
	$rootScope.statusClass = 'status-current';
	$rootScope.dashboardClass = 'dashboard-nav';
	$rootScope.settingClass = 'setting-nav';
	  $rootScope.feedbackClass = 'feedback-log feedback-show';
	$scope.isSubmit = true;
	$scope.submitStatus = function() {

		var status = new Object();
		status.projectId = $scope.project;
		status.hours = $scope.hours;
		status.workingOn = $scope.workingOn;
		status.workedOn = $scope.workedOn;
		status.blockers = $scope.blockers;
		status.dateTime = $rootScope.statusDate;
		if(!status.projectId){
			appNotifyService.error('Please select a valid project');
            return false;
		}
		else if(!status.hours){
			appNotifyService.error('Please fill in the hours for the selected project.');
            return false;
		}
		else if(!status.workedOn && !status.workingOn){
			appNotifyService.error('WorkedOn or WorkingOn field can not be empty');
            return false;
		}

		statusService.submitStatus(status).then(function(response) {
			if (response.success = true) {

				$scope.clearStatus();
				$scope.usersstatusLoad();
			}

		}, function(error) {
			if(error.reason == "hours_exceeded"){
				appNotifyService.error('You have exceeded 24 hours in a day!');
			}
			else if (error.reason == "pass_duedate_status_edit"){
				appNotifyService.error('You are not allow to enter status pass due date');
			}
			else if (error.reason == "future_date_status_edit"){
				appNotifyService.error('Can not allow to enter status for future date');
			}
			else{
				appNotifyService.error('Error while submitting status');
			}	
		});
	};

	$scope.usersstatusLoad = function() {

	var	time = "Weekly";
	
	settingsService.getConfigSetting().then(function(response){
         if (response.success = true) 
             $rootScope.statusEnable = response.data.statusEnable;
       }, function(error){
       });

	dropdownListService.getTimePeriod().then(function(response){
            $scope.timePeriod = response.data;
           	$scope.mytime = response.data[2].value;
          
              }, function(error){
        });
 	 	
		statusService.getAllStatus(time).then(function(response) {
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

	$scope.timePeriodChange = function(timePeriod) {

		statusService.getAllStatus(timePeriod).then(function(response) {
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
				$scope.allprojects = response.data.gridData;
			}
		});
		dropdownListService.getHours().then(function(response){
			$scope.selectables = response.data;
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
				
				angular.forEach(response.data.gridData, function(value, key) {

					$scope.allstatus = value.statusList;
					$scope.project = $scope.allstatus[0].projectId;
					$scope.hours = $scope.allstatus[0].hours.toString();
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
			if(error.reason == "hours_exceeded"){
				appNotifyService.error('You have exceeded 24 hours in a day!');
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
	//Reset status page
	$scope.reset = function(){
	
		$scope.isSubmit = true;
   		$scope.isUpdate = false;
	}

	// avoid spacing while copy paste in text angular
	$scope.stripFormat = function ($html) {
  return $filter('htmlToPlaintext')($html);
};

});