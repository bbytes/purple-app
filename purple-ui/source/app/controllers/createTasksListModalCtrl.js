/*
 * All User Modal Controller
 */
angular.module('rootApp').controller('createTasksListModalCtrl',
		function($scope,params, $uibModalInstance, $uibModal,tasksService) {

			$scope.selection = [];
			$scope.projects = params.projects;
			$scope.taskLists =params.taskLists;
			$scope.selectedProject=params.project;
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
			$scope.createTaskList = function(taskList) {
				tasksService.createTaskList(taskList).then(function(response) {
					if(response.data.project.projectId==$scope.selectedProject.projectId)
						$scope.taskLists.push(response.data);
				});
				$uibModalInstance.close($scope.selection);
			};

			$scope.cancel = function() {
				$uibModalInstance.dismiss('cancel');
			};
		});