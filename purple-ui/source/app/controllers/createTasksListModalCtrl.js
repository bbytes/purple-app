/*
 * All User Modal Controller
 */
angular.module('rootApp').controller('createTasksListModalCtrl',
		function($scope,$rootScope,params, $uibModalInstance, $uibModal,tasksService,appNotifyService) {

			$scope.selection = [];
			$scope.projects = params.projects;
			$scope.taskLists =params.taskLists;
			$scope.taskList =params.taskList;
			$scope.selectedProject=params.project;
			$scope.title=params.title;
			
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
				console.log("createTaskList");
				if(validateTaskList(taskList)){
					tasksService.createTaskList(taskList).then(function(response) {
						if($scope.selectedProject=="All"||response.data.projectId==$scope.selectedProject.projectId){
							if($scope.taskLists==null)
								$scope.taskLists=response.data;
							else
								$scope.taskLists.push(response.data);
							if($scope.taskLists.length==1)
								$rootScope.$broadcast('FIRST_TL_ADDED', response.data);
						}
					});
					$uibModalInstance.close($scope.selection);
				}
			};

			$scope.cancel = function() {
				$uibModalInstance.dismiss('cancel');
			};
			function validateTaskList(taskList){
				if(taskList==null){
					appNotifyService.error("Please enter valid values");
					return false;
				}
				if(taskList.name==null || taskList.name==""){
					appNotifyService.error("Please enter a name");
					return false;
				}
				if(taskList.projectId==null || taskList.projectId==""){
					appNotifyService.error("Please select a project");
					return false;
				}
				return true;
					
			}
		});