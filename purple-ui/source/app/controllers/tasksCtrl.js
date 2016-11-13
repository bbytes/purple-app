angular.module('rootApp').controller(
		'tasksCtrl',
		function($scope, $location, tasksService, projectService, $uibModal) {

			$scope.taskList;

			$scope.isActive = function(viewLocation) {
				var active = (viewLocation === $location.path());
				return active;

			};

			$(document).ready(function() {
				$('.dropdown-toggle').dropdown();
			});
			/*
			 * Load all states
			 */
			$scope.loadStates = function(taskState) {
				tasksService.getAllTasksStates().then(function(response) {
					if (response.success) {
						$scope.taskStates = response.data;
						$scope.selectedState = $scope.taskStates[0];
						$scope.loadTasks($scope.selectedState);
					}
				});

			};
			/*
			 * Load tasks for state
			 */
			$scope.loadTasks = function(selectedState) {
				$scope.selectedState = selectedState;
				tasksService.getAllTasksForState($scope.selectedState).then(
						function(response) {
							if (response.success) {
								$scope.taskLists = response.data;
								$scope.taskList = $scope.taskLists[0];
								if ($scope.taskList != null)
									$scope.loadTaskItems($scope.taskList);
								else
									$scope.loadTaskItems.length = 0;
							}
						});

			};

			/* Load tasks for project */

			$scope.loadProjectStateTasks = function(selectedProject) {
				$scope.selectedProject = selectedProject;
				tasksService.getAllTasksForProjectAndState(
						$scope.selectedProject, $scope.selectedState).then(
						function(response) {
							if (response.success) {
								$scope.taskLists = response.data;
								$scope.taskList = $scope.taskLists[0];
								if ($scope.taskList != null)
									$scope.loadTaskItems($scope.taskList);
								else
									$scope.loadTaskItems.length = 0;
							}
						});
			};
			/* Load tasks for project */

			$scope.loadStateProjectTasks = function(selectedState) {
				$scope.selectedState = selectedState;
				tasksService.getAllTasksForProjectAndState(
						$scope.selectedProject, $scope.selectedState).then(
						function(response) {
							if (response.success) {
								$scope.taskLists = response.data;
								$scope.taskList = $scope.taskLists[0];
								$scope.loadTaskItems($scope.taskList);
							}
						});
			};

			/*
			 * Load all projects of logged in user
			 */
			$scope.loadUserProjects = function() {
				projectService.getUserproject().then(function(response) {
					if (response.success) {
						$scope.userprojects = response.data.gridData;
						$scope.selectedProject = $scope.userprojects[0];
					}
				});
			};
			/* Create new tasklist */
			$scope.showTaskListModal = function() {
				showTaskListModal();
			}
			function showTaskListModal() {
				var uibModalInstance = $uibModal.open({
					animation : true,
					templateUrl : 'app/partials/addtaskslist-modal.html',
					controller : 'createTasksListModalCtrl',
					backdrop : 'static',
					size : 'md',
					resolve : {
						params : function() {
							return {
								"projects" : $scope.userprojects,
								"taskLists" : $scope.taskLists
							};
						}
					}
				});
			}
			$scope.deleteTaskList = function(taskList) {
				tasksService.deleteTaskList(taskList).then(function(response) {
					if (response.success) {
						var index = $scope.taskLists.indexOf(taskList);
						$scope.taskLists.splice(index, 1);
					}
				});
			};
			$scope.deleteTaskItem = function(taskItem) {
				tasksService.deleteTaskItem(taskItem).then(function(response) {
					if (response.success) {
						var index = $scope.taskItemsLists.indexOf(taskItem);
						$scope.taskItemsLists.splice(index, 1);
					}
				});
			};
			$scope.setClickedState = function(index) { // function that sets
				// the value of
				// selectedState to
				// current index
				$scope.selectedState = $scope.taskStates[index];
			};
			$scope.setSelectedProject = function(index) { // function that
				// sets
				// the value of
				// selectedProject to
				// current index
				$scope.selectedProject = $scope.userprojects[index];
			};

			/* Load taskItems for selected tasklist */
			$scope.loadTaskItems = function(taskList) {
				$scope.taskList = taskList;
				tasksService.getTaskItems(taskList).then(function(response) {
					$scope.taskItemsLists = response.data;
				});
			}

			$scope.showTaskItemModal = function() {
				showTaskItemModal();
			}
			function showTaskItemModal() {
				var uibModalInstance = $uibModal.open({
					animation : true,
					templateUrl : 'app/partials/addtaskitem-modal.html',
					controller : 'createTasksItemModalCtrl',
					backdrop : 'static',
					size : 'md',
					resolve : {
						params : function() {
							return {
								"taskList" : $scope.taskList,
								"taskItems" : $scope.taskItemsLists,
								"project" : $scope.selectedProject
							};
						}
					}
				});
			}
		});