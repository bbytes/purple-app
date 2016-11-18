/*
 * All User Modal Controller
 */
angular.module('rootApp').controller(
		'createTasksItemModalCtrl',
		function($scope, $uibModalInstance, $uibModal, params, tasksService,
				dropdownListService, projectService) {
			$scope.taskList = params.taskList;
			$scope.taskItemsLists = params.taskItems;
			$scope.project = params.project;
			$scope.mindate = new Date();

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
				tasksService.createTaskItem($scope.taskList, taskItem).then(
						function(response) {
							if ($scope.taskItemsLists == null)
								$scope.taskItemsLists = response.data;
							else
								$scope.taskItemsLists.push(response.data);

							$scope.taskList.taskItems.push(response.data);
							$scope.taskList.estimatedHours=$scope.taskList.estimatedHours+response.data.estimatedHours;
						});
				$uibModalInstance.close($scope.selection);
			};
			$scope.createMoreTaskItem = function(taskItem) {
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
			};
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