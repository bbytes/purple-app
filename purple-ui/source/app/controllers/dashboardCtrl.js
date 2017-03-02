/*
 * Dashboard controller
 */
angular.module('rootApp').controller('dashboardCtrl', function ($scope, $rootScope, $mdSidenav, dropdownListService, $localStorage, userService, projectService, appNotifyService, $location, statusService, commentService, editableOptions, $mdSidenav, $mdMedia, cfpLoadingBar) {
    $scope.commentDesc = '';
    $scope.isActive = function (route) {
        return route === $location.path();
    };
    $rootScope.bodyClass = 'body-standalone1';
    $rootScope.navClass = 'nav-control';
    $rootScope.navstatusClass = 'nav navbar-nav';

    $rootScope.statusClass = 'status-nav';
    $rootScope.dashboardClass = 'dashboard-current';
    $rootScope.settingClass = 'setting-nav';
    $rootScope.intergrationClass = 'intergration-class profile-class';

    // variable to store the information about timeline data
    $scope.timelineData;

    // variable to store the information about user data
    $rootScope.projectUsers;

    /*
     * Get all status timeline
     */
    $scope.loadTimePeriod = function () {

        dropdownListService.getTimePeriod().then(function (response) {
            $scope.timePeriod = response.data;
            $scope.mytime = response.data[2].value;

        }, function (error) {
        });

    };
    $scope.start = function () {
        cfpLoadingBar.start();
    };

    $scope.complete = function () {
        cfpLoadingBar.complete();
    };

    $scope.loadStatusTimeline = function (time) {

        if (time === null || time === undefined)
            time = "Weekly";
        $scope.updateData = {
            projectList: [],
            userList: []
        };

        statusService.getAllTimelineStatus($scope.updateData, time).then(function (response) {
            if (response.success) {
                $scope.timelineData = response.data.gridData;
                $scope.timelineTableData = [];
                angular.forEach(response.data.gridData, function (value, key) {
                    angular.forEach(value.statusList, function (value, key) {
                        $scope.timelineTableData.push(value);
                    });
                });

                $scope.isActive = true;
                $scope.isProject = false;
                $scope.isUser = false;
            }
        });
    };
    // method for pulling status by changing time period
    $scope.timeChange = function (timePeriod) {

        var itemSelected = $rootScope.itemChoose;
        if ($scope.isActive)
            $scope.loadStatusTimeline(timePeriod);
        else if ($scope.isProject)
            $scope.loadProjectMap(itemSelected, timePeriod);
        else if ($scope.isUser)
            $scope.loadUserMap(itemSelected, timePeriod);
    };

    // method to download csv file for timeline
    $scope.csvDownloadTimeline = function (timePeriod) {

        var itemSelected = $rootScope.itemChoose;
        if ($scope.isActive) {
            $scope.updateData.projectList = [];
            $scope.updateData.userList = [];
        } else if ($scope.isProject) {
            $scope.updateData.projectList = [itemSelected.projectId];
            $scope.updateData.userList = [];
        } else if ($scope.isUser) {
            $scope.updateData.projectList = [];
            $scope.updateData.userList = [itemSelected.email];
        }
        $scope.options = {
            value: $scope.updateData,
            timePeriod: timePeriod
        };
    };

    /**
     * Post comment on status
     */
    $scope.formData = {};
    $scope.addComment = function (statusId) {
        $scope.commentData = {
            statusId: statusId,
            commentDesc: $scope.formData.commentDesc
        };
        commentService.postComment($scope.commentData).then(function (response) {
            if (response.success) {
                $scope.message = 'Commented Successfully';
                $scope.formData.commentDesc = '';
            }
            $scope.getAllComments(statusId);
        });
    };

    /*
     * post reply on comment
     */
    $scope.postReply = function (replyObj, commentId) {

        commentService.postReply(replyObj, commentId).then(function (response) {

            if (response.success) {
                replyObj.replyDesc = '';
                $scope.message = 'Replied Successfully';
                $scope.openCommentSideBar($scope.selectedStatusId);
            } else {
                $scope.message = "Reply failed";
            }
        }.bind(this));
        $scope.replyComment = '';
        $scope.loadReply(commentId);
    };

    /*
     * Load all projects of logged in user
     */
    $scope.loadUserProjects = function () {
        projectService.getUserproject().then(function (response) {
            var projectIds = [];
            if (response.success) {
                $scope.userprojects = response.data.gridData;
                angular.forEach(response.data.gridData, function (value, key) {
                    projectIds.push(value.projectId);
                });
            }
            projectService.getprojectsUsers(projectIds).then(function (response) {
                if (response.success) {
                    $rootScope.projectUsers = response.data.gridData;
                }
            });
        });
    };

    // search user using '@' mention
    $scope.searchPeople = function (term) {

        $scope.people = [];

        angular.forEach($scope.projectUsers, function (value, key) {
            if (value.userName.toUpperCase().indexOf(term.toUpperCase()) >= 0 || value.email.toUpperCase().indexOf(term.toUpperCase()) >= 0) {
                $scope.people.push(value);
            }
        });

    };

    // get user selected from @ mention userList
    $scope.getPeopleText = function (item) {
        return '@[' + item.email + ']';
    };

    /*
     * Get status 
     */
    $scope.openCommentSideBar = function (selectedStatusId) {
        $scope.selectedStatusId = selectedStatusId;

        statusService.getStatusWithId(selectedStatusId).then(function (response) {
            if (response.success) {
                $scope.statusDate = response.data.gridData[0].date;
                $scope.statusList = response.data.gridData[0].statusList;
            }
        });

        $scope.getAllComments(selectedStatusId);
    };
    /*
     * Get all comments of status
     */
    $scope.getAllComments = function (selectedStatusId) {
        commentService.getComment(selectedStatusId).then(function (response) {
            if (response.success) {
                $scope.commentCount = response.data.comment_count;
                $scope.allcomments = response.data.gridData;
            }
        });
    };

    /*
     * Get all replies of a comment
     */
    $scope.loadReply = function (commentId) {
        commentService.getReplies(commentId).then(function (response) {
            $scope.commentId = commentId;
            if (response.success) {
                $scope.allreplies = response.data.gridData;
            }
        });
    };

    /*
     * Load status timeline by project
     */
    $scope.loadProjectMap = function (project, time) {
        $scope.delay = 0;
        $scope.minDuration = 0;
        $scope.message = 'Please Wait...';
        $scope.backdrop = true;
        $scope.promise = null;
        $rootScope.itemChoose = project;

        $scope.updateData.projectList = [project.projectId];
        $scope.updateData.userList = [];

        projectService.getprojectsUsers($scope.updateData.projectList).then(function (response) {
            if (response.success) {
                $scope.projectUsers = response.data.gridData;
            }
        });

        statusService.getAllTimelineStatus($scope.updateData, time).then(function (response) {
            if (response.success) {
                $scope.timelineData = response.data.gridData;
                $scope.timelineTableData = [];
                angular.forEach(response.data.gridData, function (value, key) {
                    angular.forEach(value.statusList, function (value, key) {
                        $scope.timelineTableData.push(value);
                    });
                });

                $scope.selected = project;
                $scope.isProject = true;
                $scope.isActive = false;
                $scope.isUser = false;
            }
        });
    };

    /*
     * Load status timeline by user
     */
    $scope.loadUserMap = function (user, time) {

        $rootScope.itemChoose = user;
        $scope.updateData.projectList = [];
        $scope.updateData.userList = [user.email];

        statusService.getAllTimelineStatus($scope.updateData, time).then(function (response) {
            if (response.success) {
                $scope.timelineData = response.data.gridData;

                $scope.timelineTableData = [];
                angular.forEach(response.data.gridData, function (value, key) {
                    angular.forEach(value.statusList, function (value, key) {
                        $scope.timelineTableData.push(value);
                    });
                });

                $scope.selected = user;
                $scope.isUser = true;
                $scope.isProject = false;
                $scope.isActive = false;
            }
        });

    };

    $scope.checked = false; // This will be binded using the ps-open attribute
    $scope.toggle = function () {
        $scope.checked = !$scope.checked;
    };

    //nav active
    $scope.setClickedRow = function (index) {  //function that sets the value of selectedRow to current index
        $scope.selectedRow = index;
        $scope.selectedUser = null;
    };

    $scope.setClickedUser = function (index) {  //function that sets the value of selectedRow to current index
        $scope.selectedUser = index;
        $scope.selectedRow = null;
    };

    //update comment
    $scope.updateComment = function (commentDesc, commentId) {
        commentService.updateComment(commentDesc, commentId).then(function (response) {
            if (response.success) {
                angular.forEach(response.data.gridData, function (value, key) {
                    $scope.allcomments = value.commentList;
                    $scope.isUpdate = true;
                    $scope.isSubmit = false;
                    //$scope.loadComments();
                });
            }
        });
    };

    //update reply comment
    $scope.updateReply = function (replyDesc, commentId, replyId) {
        commentService.updateReply(replyDesc, commentId, replyId).then(function (response) {
            if (response.success) {
                angular.forEach(response.data.gridData, function (value, key) {
                    //$scope.allreplies = value.replyList;
                    $scope.isUpdate = true;
                    $scope.isSubmit = false;
                    //$scope.loadComments();
                });
            }
        });
    };

    //delete comment
    $scope.deleteComment = function (commentId, $index) {
        commentService.deleteComment(commentId).then(function (response) {
            if (response.success) {
                appNotifyService.success('Comment has been successfully deleted.');
            }
            $scope.allcomments.splice($index, 1);
            //$scope.usersstatusLoad();
        });
    };

    //delete reply comment
    $scope.deletereplyComment = function (replyid, commentId, $index) {
        commentService.deletereplyComment(replyid, commentId).then(function (response) {
            if (response.success) {
                appNotifyService.success('Reply has been successfully deleted.');
                $scope.openCommentSideBar($scope.selectedStatusId);
            }
            $scope.allreplies.splice($index, 1);
            //$scope.usersstatusLoad();
            $scope.replyComment = '';
            $scope.loadReply(commentId);
        });
    };

    //setting view type for timeline
    $scope.setViewType = function (viewType) {
        if (viewType) {
            userService.setViewType(viewType).then(function (response) {
                if (response.success) {
                    $rootScope.viewType = response.data.viewType;

                    $localStorage.userInfo.viewType = $rootScope.viewType;
                }
            });
        }
    };

    $rootScope.isOpen = false;
    $rootScope.closeSideNavPanel;
    $scope.openSideNavPanel = function (timePeriod) {
        $mdSidenav('right').open();
        $rootScope.isOpen = !$mdSidenav('right').isOpen();
        $rootScope.isOpen = true;
        $('body').on('click', '.md-sidenav-backdrop ', function () {

            $rootScope.isOpen = false;
            $scope.timeChange(timePeriod);
        });
        $rootScope.closeSideNavPanel = function () {
            $mdSidenav('right').close();
            $rootScope.isOpen = false;
            $scope.timeChange(timePeriod);
        };
    };

    $(window).scroll(function () {
        var scroll = $(window).scrollTop();
        if (scroll > 0) {
            $(".header-dashboard").addClass("activehead");
        } else {
            $(".header-dashboard").removeClass("activehead");
        }
    });

    $('.leftColumn').on('mousewheel DOMMouseScroll', function (e) {

        var e0 = e.originalEvent;
        var delta = e0.wheelDelta || -e0.detail;

        this.scrollTop += (delta < 0 ? 1 : -1) * 30;
        e.preventDefault();
    });

});