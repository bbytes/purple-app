/*
 * Project controller
 */
angular.module('rootApp').controller('projectCtrl', function ($scope, $rootScope, projectService, appNotifyService, $uibModal) {

    $scope.isActive = function (route) {
        return route === $location.path();
    };

    $rootScope.navClass = 'nav navbar-nav';
    $rootScope.navstatusClass = 'right-nav-ct';
    $rootScope.bodyClass = 'body-standalone1';
    $scope.showpage = false;
    // variable to store information of all project list
    $scope.allprojects;
    //when page is loading, showing sort by username
    $scope.sortKey = 'projectName';
    
    // Method is used to create project
    $scope.createProject = function (project) {

        if (!project) {
            appNotifyService.error('Please enter a valid Project name');
            return false;
        }
        $scope.userEmailsList = [];
        angular.forEach($scope.newList, function (user) {
            $scope.userEmailsList.push(user.email);
        });
        $scope.project.users = $scope.userEmailsList;

        projectService.createProject($scope.project).then(function (response) {
            if (response.success) {
                $scope.project = response.data;
                $scope.allprojects.unshift($scope.project);
                $scope.project = '';
                $scope.newList = '';

            } else {
                appNotifyService.error(response.data);
            }
        }, function (error) {
            appNotifyService.error('Error while creating project.');
        });
    };

    // Method is used to load all projects
    $scope.loadAllProjects = function () {
        projectService.getAllprojects().then(function (response) {
            if (response.success) {
                if (response) {
                    $scope.userscount = response.data.length;
                }
                $scope.usersCount = response.data.gridData.usersCount;
                $scope.projectCount = response.data.project_count;
                $scope.allprojects = response.data.gridData;
            }
        });
    };

    /* Method to get Loggedin user projects for status*/
    $scope.loadUsersProjects = function () {
        projectService.getUserproject().then(function (response) {
            if (response.success) {

                $scope.userprojects = response.data.gridData;
            }
        });
    };

    $scope.initUserProjects = function () {
        $scope.loadUsersProjects();
    };

    // Get all 'joined' status users
    $scope.getAllUseresInModal = function () {

        projectService.getAllUsersToAdd().then(function (response) {
            if (response.success) {
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
                animation: true,
                templateUrl: 'app/partials/allusers-modal.html',
                controller: 'allusersModalCtrl',
                backdrop: 'static',
                size: 'md',
                resolve: {
                    options: function () {
                        return {
                            "title": 'Add Users',
                            "data": $scope.allusers
                        };
                    }
                }
            });

            uibModalInstance.result.then(function (selection) {
                $scope.newList = [];
                angular.forEach($scope.allusers, function (user) {
                    if (selection.indexOf(user.id) > -1) {
                        $scope.newList.push(user);
                    }
                });
            });
        }
    };

    // Calling method to open the assign project modal for assigning project to user.
    $scope.openAssignProjectModal = function (projectId, index) {

        projectService.getUsersToAssignProject(projectId).then(function (response) {
            if (response.success) {
                $scope.allusers = response.data.gridData;
                showModal(projectId, index);
            }
        });

        function showModal(projectId, index) {
            var uibModalInstance = $uibModal.open({
                animation: true,
                templateUrl: 'app/partials/assignProject-modal.html',
                controller: 'assignProjectModalCtrl',
                backdrop: 'static',
                size: 'md',
                resolve: {
                    modalData: function () {
                        return {
                            "title": 'Assign Manager/Admin to Project',
                            "userData": $scope.allusers,
                            "projectId": projectId
                        };
                    }
                }
            });

            uibModalInstance.result.then(function (project) {
                $scope.allprojects.splice(index, 1);
                $scope.allprojects.unshift(project);
                appNotifyService.success(project.projectOwner + ' is currently owner of Project - ' + project.projectName);
            });
        }
    };

    //to delete user from modal
    $scope.deleteUserFromList = function (id, $index) {
        $scope.newList.splice($index, 1);
    };

    //To delete user from ui
    $scope.deleteOrgUserFromList = function (id, $index) {
        $scope.orgUserList.splice($index, 1);
    };

    // view the all members of project
    $scope.viewMembers = function (userList) {
        $scope.membersList = userList;
        $("#myModal").modal('show');

    };

    // Edit project
    $scope.showEditPage = function (id) {
        projectService.getProjectWithId(id).then(function (response) {
            if (response.success) {

                $scope.data = response.data;
                $scope.orgUserList = response.data.userList;
                $scope.showpage = true;
            }
        });
    };

    // Get all 'JOINED' users who are not part of project
    $scope.getAllUseresInUpdateModal = function (projid) {

        projectService.getMoreUsersToAdd(projid).then(function (response) {
            if (response.success) {
                $scope.allmoreusers = response.data.gridData;
                showModal();
            }
        });
        function showModal() {

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
                            "data": $scope.allmoreusers
                        };
                    }
                }
            });

            uibModalInstance.result.then(function (selection) {
                angular.forEach($scope.allmoreusers, function (user) {
                    if (selection.indexOf(user.id) > -1) {
                        $scope.orgUserList.push(user);
                    }
                });
            });
        }
    };

    // Update the project
    $scope.updateProject = function (project) {

        if (!project.projectName) {
            appNotifyService.error('Project name can not be empty');
            return false;
        }
        $scope.updateuserEmailsList = [];
        angular.forEach($scope.orgUserList, function (user) {
            $scope.updateuserEmailsList.push(user.email);
        });

        $scope.updateData = {
            projectName: $scope.data.projectName,
            timePreference: $scope.data.timePreference,
            users: $scope.updateuserEmailsList
        };
        var id = $scope.data.projectId;
        projectService.updateProject($scope.updateData, id).then(
                function (response) {
                    if (response.success) {
                        $scope.data = '';
                        $scope.orgUserList = '';
                        $scope.loadAllProjects();
                        $scope.showpage = false;
                    }

                }, function (error) {
            appNotifyService.error("Error while updating project.");
        });
    };

    // Delete project
    $scope.deleteProject = function (id, index) {
        projectService.deleteProject(id).then(function (response) {
            if (response.success) {
                appNotifyService.success('Project has been successfully deleted.');
            }
            $scope.allprojects.splice(index, 1);
        });
    };

    $scope.sort = function (keyname) {
        // below code is just hide and show sort icon based on key
        if (keyname !== 'projectName')
            $scope.isSortKeyIcon = true;
        else
            $scope.isSortKeyIcon = false;
        $scope.sortKey = keyname;   //set the sortKey to the param passed
        $scope.reverse = !$scope.reverse; //if true make it false and vice versa
    };

    $scope.show = true;
    $scope.closeAlert = function () {
        $scope.show = false;
    };

    $scope.alert = {
        type: 'alert-info'
    };

    {
        $scope.time = new Date(1970, 0, 1, 10, 30, 40);
        $scope.selectedTimeAsNumber = 10 * 36e5 + 30 * 6e4 + 40 * 1e3;
        $scope.selectedTimeAsString = '10:00';
        $scope.sharedDate = new Date(new Date().setMinutes(0, 0));
    }


    $scope.scrollToTop = function ($var) {
        // 'html, body' denotes the html element, to go to any other custom element, use '#elementID'
        $('html, body').animate({
            scrollTop: 0
        }, 'fast'); // 'fast' is for fast animation
    };
});
