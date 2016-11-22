/*
 * All User Modal Controller
 */
angular.module('rootApp').controller(
		'createTasksItemModalCtrl',
		function($scope,$rootScope, $uibModalInstance, $uibModal, params, tasksService,
				dropdownListService, projectService,appNotifyService) {
			$scope.taskList = params.taskList;
			$scope.taskItemsLists = params.taskItems;
			$scope.project = params.project;
			$scope.taskItem=params.taskItem;
			$scope.title=params.title;
			$scope.mindate = new Date();			

			if($scope.taskItem!=null){
	                angular.forEach($scope.taskItem.users, function (user) {
	                    	$scope.taskItem.userIds.push(user.userId);
	                });
	        }

			$scope.dateOptions = {
				minDate : new Date()
			};

			$scope.toggleSelection = function toggleSelection(id) {
				var idx = $scope.selection.indexOf(id);

				// is currently selected
				if (idx > -1) {
					$scope.selection.splice(idx, 1);
				}
				// is newly selected
				else {
					$scope.selection.push(id);
				}
			};
			$scope.getHours = function() {
				dropdownListService.getEstimateHours().then(function(response) {
					$scope.estimateHours = response.data;
				});
			};
			$scope.getAllUsersOfProject = function() {
				projectService.getAllUsersOfProject($scope.taskList.projectId)
						.then(function(response) {
							$scope.projectUsers = response.data;
						});
			};
			$scope.createTaskItem = function(taskItem) {
				if(validateTaskItem(taskItem)){
					tasksService.createTaskItem($scope.taskList, taskItem).then(
						function(response) {
							if ($scope.taskItemsLists == null)
								$scope.taskItemsLists = response.data;
							else{
								if($scope.taskItem==null){
									$scope.taskItemsLists.push(response.data);
									$scope.taskList.taskItems.push(response.data);
									$scope.taskList.estimatedHours=$scope.taskList.estimatedHours+response.data.estimatedHours;
								}else{
										$rootScope.$broadcast('TASK_ITEM_EDITED', response.data);
								}
							}
						});
					$uibModalInstance.close($scope.selection);
				}
			};
			$scope.createMoreTaskItem = function(taskItem) {
				if(validateTaskItem(taskItem)){
					tasksService.createTaskItem($scope.taskList, taskItem).then(
						function(response) {
							if ($scope.taskItemsLists == null)
								$scope.taskItemsLists = response.data;
							else
								$scope.taskItemsLists.push(response.data);
							$scope.taskList.taskItems.push(response.data);
							$scope.taskList.estimatedHours=$scope.taskList.estimatedHours+response.data.estimatedHours;
						});
					$scope.taskItem = new Object();
				}
			};
			
			function validateTaskItem(taskItem){
				if(taskItem==null){
					appNotifyService.error("Please enter valid values");
					return false;
				}
				if(taskItem.name==null || taskItem.name==""){
					appNotifyService.error("Please enter a name");
					return false;
				}
				if(taskItem.desc==null || taskItem.desc==""){
					appNotifyService.error("Please enter a description");
					return false;
				}
				if(taskItem.estimatedHours==null || taskItem.estimatedHours==""){
					appNotifyService.error("Please enter estimated hours");
					return false;
				}
				if(taskItem.dueDate==null || taskItem.dueDate==""){
					appNotifyService.error("Please enter dueDate");
					return false;
				}
				if(taskItem.userIds==null || taskItem.userIds.length==0){
					appNotifyService.error("Please select users");	
					return false;
				}	
				return true;
					
			}
	
			$scope.cancel = function() {
				$uibModalInstance.dismiss('cancel');
			};
			$scope.popup = {
				opened : false
			};
			$scope.openDp = function() {
				$scope.popup.opened = true;
			};
		});