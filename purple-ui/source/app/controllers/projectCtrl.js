/**
 * Project controller on project page
 */
rootApp.controller('projectCtrl', function($scope, $rootScope, $state,
		projectService, appNotifyService, adminService, $uibModal,
		statusService, $window) {

	$scope.isActive = function(route) {
		return route === $location.path();
	}

	$rootScope.bodyClass = 'body-standalone1';
	$scope.showpage = false;
	
	// Method is used to create project
	$scope.createProject = function(project) {

		$scope.userEmailsList = [];
		angular.forEach($scope.newList, function(user) {
			$scope.userEmailsList.push(user.email);
		});
		$scope.project.users = $scope.userEmailsList;

		projectService.createProject($scope.project).then(
				function(response) {
					if (response.success) {
						$scope.project = '';
						$scope.newList = '';
						$scope.loadAllProjects();

					} else {
						appNotifyService.error(response.data,
								'error while creating project.');
					}
				}, function(error) {
					appNotifyService.error(error.msg, 'Invite unsuccesfull.');
				});
	};

	// Method is used to load all projects
	$scope.loadAllProjects = function() {
		projectService.getAllprojects().then(function(response) {
			if (response.success) {
				if (response) {
					$scope.userscount = response.data.length;
				}
				$scope.usersCount = response.data.gridData.usersCount;
				$scope.projectCount = response.data.project_count;
				//            	 /repeatSelect: null,
				$scope.allprojects = response.data.gridData;
			}
		});
	}

	/* Method to get Loggedin user projects for status*/
	$scope.loadUsersProjects = function() {
		projectService.getUserproject().then(function(response) {
			if (response.success) {

				$scope.userprojects = response.data.gridData;
			}
		});
	}
	$scope.initUserProjects = function() {
		$scope.loadUsersProjects();
	};
	
	// Get all 'joined' status users
	$scope.getAllUseresInModal = function() {

		projectService.getAllUsersToAdd().then(function(response) {
			if (response.success = true) {
				if (response) {
					$scope.userscount = response.data.length;
				}
				$scope.joinedCount = response.data.joined_count;
				$scope.pendingCount = response.data.pending_count;
				$scope.allusers = response.data.gridData;
				showModal();
			}
		});

		function showModal() {
			var uibModalInstance = $uibModal.open({
				animation : true,
				templateUrl : 'app/partials/allusers-modal.html',
				controller : 'allusersModalCtrl',
				backdrop : 'static',
				size : 'md',
				resolve : {
					options : function() {
						return {
							"title" : 'Add Users',
							"data" : $scope.allusers
						};
					}
				}
			});

			uibModalInstance.result.then(function(selection) {
				$scope.newList = [];
				angular.forEach($scope.allusers, function(user) {
					if (selection.indexOf(user.id) > -1) {
						$scope.newList.push(user);
					}
				});
			});
		}
	}

	//to delete user from modal
	$scope.deleteUserFromList = function(id, $index) {
		$scope.newList.splice($index, 1);
	}
	
	//To delete user from ui
	$scope.deleteOrgUserFromList = function(id, $index) {
		$scope.orgUserList.splice($index, 1);
	}

	// view the all members of project
	$scope.viewMembers = function(userList) {
		$scope.membersList = userList;
		$("#myModal").modal('show');

	}

	// Edit project
	$scope.showEditPage = function(id) {
		projectService.getProjectWithId(id).then(function(response) {
			if (response.success = true) {

				$scope.data = response.data;
				$scope.orgUserList = response.data.userList;
				$scope.showpage = true;
			}
		});
	}

	// Get all 'JOINED' users who are not part of project
	$scope.getAllUseresInUpdateModal = function(projid) {

		projectService.getMoreUsersToAdd(projid).then(function(response) {
			if (response.success = true) {
				$scope.allmoreusers = response.data.gridData;
				console.log($scope.allmoreusers);
				showModal();
			}
		});
		function showModal() {

			var uibModalInstance = $uibModal.open({
				animation : true,
				templateUrl : 'app/partials/allusers-modal.html',
				controller : 'allusersModalCtrl',
				backdrop : 'static',
				size : 'md',
				resolve : {
					options : function() {
						return {
							"title" : 'Add Users',
							"data" : $scope.allmoreusers
						};
					}
				}
			});

			uibModalInstance.result.then(function(selection) {
				angular.forEach($scope.allmoreusers, function(user) {
					if (selection.indexOf(user.id) > -1) {
						$scope.orgUserList.push(user);
					}
				});
			});
		}
	}

	// Update the project
	$scope.updateProject = function() {

		$scope.updateuserEmailsList = [];
		angular.forEach($scope.orgUserList, function(user) {
			$scope.updateuserEmailsList.push(user.email);
		});

		$scope.updateData = {
			projectName : $scope.data.projectName,
			timePreference : $scope.data.timePreference,
			users : $scope.updateuserEmailsList
		}
		var id = $scope.data.projectId;
		projectService.updateProject($scope.updateData, id).then(
				function(response) {
					if (response.success) {
						$scope.data = '';
						$scope.orgUserList = '';
						$scope.loadAllProjects();
						$scope.showpage = false;

					} else {
						appNotifyService.error(response.data,
								'Invite unsuccesfull.');
					}

				}, function(error) {
					appNotifyService.error(error.msg, 'Invite unsuccesfull.');
				});
	};
	
	// Delete project
	$scope.deleteProject = function(id, $index) {
		projectService.deleteProject(id).then(function(response) {
			if (response.success = true) {
				appNotifyService.success('Project has been deleted.');
			}
			$scope.allprojects.splice($index, 1);
		});
	}

	$scope.show = true;
	$scope.closeAlert = function() {
		$scope.show = false;
	};

	$scope.alert = {
		type : 'alert-info'
	};

	{
		$scope.time = new Date(1970, 0, 1, 10, 30, 40);
		$scope.selectedTimeAsNumber = 10 * 36e5 + 30 * 6e4 + 40 * 1e3;
		$scope.selectedTimeAsString = '10:00';
		$scope.sharedDate = new Date(new Date().setMinutes(0, 0));
	}

});
