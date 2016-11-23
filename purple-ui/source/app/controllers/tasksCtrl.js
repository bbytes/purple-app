angular.module('rootApp').controller('tasksCtrl', function ($scope, $rootScope, $location, tasksService, projectService, appNotifyService, $uibModal) {

    $rootScope.bodyClass = 'body-standalone1';
    $rootScope.navClass = 'nav-control';
    $rootScope.navstatusClass = 'nav navbar-nav';

    $rootScope.statusClass = 'status-current';
    $rootScope.dashboardClass = 'dashboard-nav';
    $rootScope.settingClass = 'setting-nav';
    $rootScope.feedbackClass = 'feedback-log feedback-show';

    $scope.taskList;
    $scope.taskItemsLists=new Array();
    $scope.taskLists=new Array();

    $scope.isActive = function (viewLocation) {
        var active = (viewLocation === $location.path());
        return active;

    };

    $(document).ready(function () {
        $('.dropdown-toggle').dropdown();
        $scope.$on('TASK_ITEM_EDITED', function (event, data) {
            console.log("TASK_ITEM_EDITED "+ data); 
            getTaskListforId($scope.taskList.taskListId);
            loadTaskItemsForList($scope.taskList); 
          });
    });

    $scope.initTasks = function () {
        console.log("0-initTasks");
        $scope.loadStates();
        $scope.loadUserProjects();
        $scope.selectedProject = "All";
        $scope.selectedPjtIndex = -1;
        $scope.loadStateProjectTasks('All',-1);
    };
    /*
	 * Load all states
	 */
    $scope.loadStates = function () {
        tasksService.getAllTasksStates().then(function (response) {
            if (response.success) {
                $scope.taskStates = response.data;
            }
        });

    };
    /*
	 * Initial loading of tasks and task items
	 */
    $scope.loadAllTasksForState = function () {
        $scope.selectedProject = "All";
        $scope.selectedPjtIndex = -1;
        console.log("3-loadAllTasksForState selectedProject:" + $scope.selectedProject + " selectedState:" + $scope.selectedState);
        getAllTasksForState();
    };

    /* Load tasks and task items on click of project */

			$scope.loadProjectStateTasks = function(selectedProject,index) {
				$scope.selectedPjtIndex=index;
				$scope.selectedProject = selectedProject;
				console.log("loadProjectStateTasks selectedProject:"+$scope.selectedProject+" selectedState:"+$scope.selectedState);
				getAllTasksForStateAndProject();
				
			};
			/* Load tasks and task items on click of state */

    $scope.loadStateProjectTasks = function (selectedState, index) {
        $scope.selectedSateIndex = index;
        $scope.selectedState = selectedState;
        console.log("loadStateProjectTasks selectedProject:" + $scope.selectedProject + " selectedState:" + $scope.selectedState);
        getAllTasksForStateAndProject();
        
    };
    function getAllTasksForState() {
        tasksService.getAllTasksForState($scope.selectedState).then(
                function (response) {
                    if (response.success) {
                        $scope.taskLists = response.data;
                        if ($scope.taskLists != null
                                && $scope.taskLists.length > 0) {
                            $scope.taskList = $scope.taskLists[0];
                            $scope.loadTaskItems($scope.taskList, 0);
                        } else {
                            if($scope.taskItemsLists!=null)
                            	$scope.taskItemsLists.length = 0;
                            
                            // appNotifyService.success("No tasks to show for
							// selected state");
                        }
                    }
                });
    }
    function getAllTasksForStateAndProject() {
        console.log("getAllTasksForStateAndProject selectedProject:" + $scope.selectedProject + " selectedState:" + $scope.selectedState);
        tasksService.getAllTasksForProjectAndState(
                $scope.selectedProject, $scope.selectedState).then(
                function (response) {
                    if (response.success) {
                        $scope.taskLists = response.data;
                        if ($scope.taskLists != null
                                && $scope.taskLists.length > 0) {
                            $scope.taskList = $scope.taskLists[0];
                            $scope.loadTaskItems($scope.taskList, 0);
                        } else {
                        	if($scope.taskItemsLists!=null)
                            	$scope.taskItemsLists.length = 0;
                            // appNotifyService.success("No tasks to show for
							// selected project and state");
                        }
                    }
                });
    }

    /*
	 * Load all projects of logged in user
	 */
    $scope.loadUserProjects = function () {
        projectService.getUserproject().then(function (response) {
            if (response.success) {
                $scope.userprojects = response.data.gridData;
                console.log("2-loadUserProjects selectedProject:" + $scope.selectedProject + " selectedState:" + $scope.selectedState);
            }
        });
    };
    /* Create new tasklist */
    $scope.showTaskListModal = function () {
        showTaskListModal();
    }
    function showTaskListModal() {
        var uibModalInstance = $uibModal.open({
            animation: true,
            templateUrl: 'app/partials/addtaskslist-modal.html',
            controller: 'createTasksListModalCtrl',
            backdrop: 'static',
            size: 'sm',
            resolve: {
                params: function () {
                    return {
                        "projects": $scope.userprojects,
                        "taskLists": $scope.taskLists,
                        "project": $scope.selectedProject,
                        "title":"CREATE NEW TASK LIST"
                    };
                }
            }
        });
    }
    $scope.editTaskList=function(taskList){
    	 var uibModalInstance = $uibModal.open({
             animation: true,
             templateUrl: 'app/partials/addtaskslist-modal.html',
             controller: 'createTasksListModalCtrl',
             backdrop: 'static',
             size: 'md',
             resolve: {
                 params: function () {
                     return {
                         "projects": $scope.userprojects,
                         "taskList": taskList,
                         "project": $scope.selectedProject,
                         "title":"EDIT TASK LIST"
                     };
                 }
             }
         });
    }
    $scope.deleteTaskList = function (taskList) {
        tasksService.deleteTaskList(taskList).then(function (response) {
            if (response.success) {
                var index = $scope.taskLists.indexOf(taskList);
                $scope.taskLists.splice(index, 1);
                if($scope.taskList.taskListId==taskList.taskListId)
                	$scope.taskItemsLists.length=0;
            }
        });
    };
    $scope.deleteTaskItem = function (taskItem) {
        tasksService.deleteTaskItem(taskItem).then(function (response) {
            if (response.success) {
                var index = $scope.taskItemsLists.indexOf(taskItem);
                $scope.taskItemsLists.splice(index, 1);
				$scope.taskList.taskItems.splice(index, 1);
				$scope.taskList.estimatedHours=$scope.taskList.estimatedHours-taskItem.estimatedHours;
            }
        });
    };
    $scope.editTaskItem=function(taskItem){    		
            var uibModalInstance = $uibModal.open({
                animation: true,
                templateUrl: 'app/partials/addtaskitem-modal.html',
                controller: 'createTasksItemModalCtrl',
                backdrop: 'static',
                size: 'md',
                resolve: {
                    params: function () {
                        return {
                        	"title":"EDIT TASK",
                            "taskList": $scope.taskList,
                            "taskItems": $scope.taskItemsLists,
                            "project": $scope.selectedProject,
                            "taskItem":taskItem
                        };
                    }
                }
            });
    };
    $scope.setClickedState = function (index) {
        $scope.selectedState = $scope.taskStates[index];
    };
    $scope.setSelectedProject = function (index) {
        $scope.selectedProject = $scope.userprojects[index];
    };

    /* Load taskItems for selected tasklist */
    $scope.loadTaskItems = function (taskList, index) {
        $scope.taskList = taskList;
        $scope.selectedTLIndx = index;
        loadTaskItemsForList(taskList);
    }
    function loadTaskItemsForList(taskList){
    	tasksService.getTaskItems(taskList,$scope.selectedState).then(function (response) {
            if (response.data != null && response.data.length > 0) {
                $scope.taskItemsLists = response.data;
            } else {
                if($scope.taskItemsLists!=null)
                	$scope.taskItemsLists.length = 0;
            }
        });
    }
    function getTaskListforId(taskListId){
    	tasksService.getTaskListforId(taskListId).then(function(response){
    		if(response.success){
    			$scope.taskList.estimatedHours=response.data.estimatedHours;
    		}
    	});
    }
    $scope.showTaskItemModal = function () {
        showTaskItemModal();
    }
    function showTaskItemModal() { 
        var uibModalInstance = $uibModal.open({
            animation: true,
            templateUrl: 'app/partials/addtaskitem-modal.html',
            controller: 'createTasksItemModalCtrl',
            backdrop: 'static',
            size: 'md',
            resolve: {
                params: function () {
                    return {
                    		"title":"CREATE NEW TASK",
                            "taskList": $scope.taskList,
                            "taskItems": $scope.taskItemsLists,
                            "project": $scope.selectedProject
                    };
                }
            }
        });
    };
    $scope.markCompleted = function (taskItem) {
        tasksService.markCompleted(taskItem).then(function (response) {
        	 if (response.success) 
        		 taskItem.state=response.data.state;
        		 appNotifyService.success(taskItem.name+" marked completed");
        });
    };
    
         $scope.getUsersToBeAddedToTask = function(taskItem) {
        	$scope.selectedTaskItem=taskItem;
			tasksService.getUsersToBeAddedToTask(taskItem.taskItemId)
					.then(function(response) {
						  if (response.success) {
							  $scope.projectUsersToAdd=response.data;
							  var taskItemUsers=taskItem.users;	
							  showUserModal();
						  }
					});
        function showUserModal() {
            var uibModalInstance = $uibModal.open({
                animation: true,
                templateUrl: 'app/partials/allusers-modal.html',
                controller: 'allusersModalCtrl',
                backdrop: 'static',
                size: 'md',
                resolve: {
                    options: function () {
                        return {
                            "title": 'Add Users',
                            "data": $scope.projectUsersToAdd
                        };
                    }
                }
            });

            uibModalInstance.result.then(function (selection) {
                var usersToBeAdded = [];
                angular.forEach($scope.projectUsersToAdd, function (user) {
                    if (selection.indexOf(user.id) > -1) {
                        usersToBeAdded.push(user.id);
                    }
                });
                tasksService.addUsersToItem($scope.selectedTaskItem,usersToBeAdded).then(function(response){
                	if(response.success){
                		$scope.selectedTaskItem.users=response.data.users;
                	}
                });
            });
        }
    };
    $scope.deleteUserFromProject=function(userid,taskItem){
    	tasksService.removeUsersFromItem(userid,taskItem).then(function(response){
        	if(response.success){
        		taskItem.users=response.data.users;
        		}
    		});
    	}
    
});
    	
    	