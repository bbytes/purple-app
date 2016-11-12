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
				projectService.getAllUsersOfProject($scope.project.projectId)
						.then(function(response) {
							$scope.projectUsers = response.data;
						});
			};
			$scope.createTaskItem = function(taskItem) {
				tasksService.createTaskItem($scope.taskList, taskItem).then(
						function(response) {
							$scope.taskItemsLists.push(response.data);
						});
				$uibModalInstance.close($scope.selection);
			};

			$scope.cancel = function() {
				$uibModalInstance.dismiss('cancel');
			};
			$scope.popup1 = {
				opened : false
			};
			$scope.openDp = function() {
				$scope.popup1.opened = true;
			};
		});