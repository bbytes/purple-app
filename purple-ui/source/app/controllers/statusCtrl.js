/*
 * Status Controller
 */
angular.module('rootApp').controller('statusCtrl', function ($scope, $rootScope, dropdownListService, statusService, projectService, appNotifyService, settingsService, $filter, cfpLoadingBar, $uibModal, tasksService) {

    $rootScope.bodyClass = 'body-standalone1';
    $rootScope.navClass = 'nav-control';
    $rootScope.navstatusClass = 'nav navbar-nav';

    $rootScope.statusClass = 'status-current';
    $rootScope.dashboardClass = 'dashboard-nav';
    $rootScope.settingClass = 'setting-nav';
    $rootScope.feedbackClass = 'feedback-log feedback-show';
    $rootScope.intergrationClass = 'intergration-class profile-class';
    $scope.isSubmit = true;
    $scope.selectables;
    // varibale to store all task list and task item list
    $scope.taskList;
    $scope.hours;

    // this map is create for storing key-value pair for taskitems and storing into db
    var taskItemMap;
    // used to generate task item key for map
    var itemKey = 1;
    // variable is used to intialise time period first time when page get load
    var time = "Weekly";
    // variable to store the information about status enable days
    $scope.statusEnable;
    // variable to store the information abou all user statuses
    $scope.allstatus;

    $scope.submitButtonText = "SUBMIT";

    initialise();

    function initialise() {
        $scope.isMobileMode = mobilecheck();
        if (!$scope.isMobileMode)
            $scope.version = 'mobile-version';
        else {
            $scope.version = 'desktop-version';
        }
    }

    // this method is used to check the mode of browser (either web or mobile)
    function mobilecheck() {
        var check = false;
        (function (a) {
            if (/(android|bb\d+|meego).+mobile|avantgo|bada\/|blackberry|blazer|compal|elaine|fennec|hiptop|iemobile|ip(hone|od)|iris|kindle|lge |maemo|midp|mmp|mobile.+firefox|netfront|opera m(ob|in)i|palm( os)?|phone|p(ixi|re)\/|plucker|pocket|psp|series(4|6)0|symbian|treo|up\.(browser|link)|vodafone|wap|windows ce|xda|xiino/i.test(a) || /1207|6310|6590|3gso|4thp|50[1-6]i|770s|802s|a wa|abac|ac(er|oo|s\-)|ai(ko|rn)|al(av|ca|co)|amoi|an(ex|ny|yw)|aptu|ar(ch|go)|as(te|us)|attw|au(di|\-m|r |s )|avan|be(ck|ll|nq)|bi(lb|rd)|bl(ac|az)|br(e|v)w|bumb|bw\-(n|u)|c55\/|capi|ccwa|cdm\-|cell|chtm|cldc|cmd\-|co(mp|nd)|craw|da(it|ll|ng)|dbte|dc\-s|devi|dica|dmob|do(c|p)o|ds(12|\-d)|el(49|ai)|em(l2|ul)|er(ic|k0)|esl8|ez([4-7]0|os|wa|ze)|fetc|fly(\-|_)|g1 u|g560|gene|gf\-5|g\-mo|go(\.w|od)|gr(ad|un)|haie|hcit|hd\-(m|p|t)|hei\-|hi(pt|ta)|hp( i|ip)|hs\-c|ht(c(\-| |_|a|g|p|s|t)|tp)|hu(aw|tc)|i\-(20|go|ma)|i230|iac( |\-|\/)|ibro|idea|ig01|ikom|im1k|inno|ipaq|iris|ja(t|v)a|jbro|jemu|jigs|kddi|keji|kgt( |\/)|klon|kpt |kwc\-|kyo(c|k)|le(no|xi)|lg( g|\/(k|l|u)|50|54|\-[a-w])|libw|lynx|m1\-w|m3ga|m50\/|ma(te|ui|xo)|mc(01|21|ca)|m\-cr|me(rc|ri)|mi(o8|oa|ts)|mmef|mo(01|02|bi|de|do|t(\-| |o|v)|zz)|mt(50|p1|v )|mwbp|mywa|n10[0-2]|n20[2-3]|n30(0|2)|n50(0|2|5)|n7(0(0|1)|10)|ne((c|m)\-|on|tf|wf|wg|wt)|nok(6|i)|nzph|o2im|op(ti|wv)|oran|owg1|p800|pan(a|d|t)|pdxg|pg(13|\-([1-8]|c))|phil|pire|pl(ay|uc)|pn\-2|po(ck|rt|se)|prox|psio|pt\-g|qa\-a|qc(07|12|21|32|60|\-[2-7]|i\-)|qtek|r380|r600|raks|rim9|ro(ve|zo)|s55\/|sa(ge|ma|mm|ms|ny|va)|sc(01|h\-|oo|p\-)|sdk\/|se(c(\-|0|1)|47|mc|nd|ri)|sgh\-|shar|sie(\-|m)|sk\-0|sl(45|id)|sm(al|ar|b3|it|t5)|so(ft|ny)|sp(01|h\-|v\-|v )|sy(01|mb)|t2(18|50)|t6(00|10|18)|ta(gt|lk)|tcl\-|tdg\-|tel(i|m)|tim\-|t\-mo|to(pl|sh)|ts(70|m\-|m3|m5)|tx\-9|up(\.b|g1|si)|utst|v400|v750|veri|vi(rg|te)|vk(40|5[0-3]|\-v)|vm40|voda|vulc|vx(52|53|60|61|70|80|81|83|85|98)|w3c(\-| )|webc|whit|wi(g |nc|nw)|wmlb|wonu|x700|yas\-|your|zeto|zte\-/i.test(a.substr(0, 4)))
                check = true;
        })(navigator.userAgent || navigator.vendor || window.opera);
        return check;
    }
    ;

    // this is calling when page getting load once and intialise the values
    $scope.initStatus = function () {

        $scope.loadConfigSetting();
        $scope.loadTimePeriods();
        $scope.loadUserProjects();
        $scope.loadTasks();
    };

    $scope.loadTasks = function () {

        taskItemMap = {};
        // this is to create map for workedon, workingon and blockers to store the taskItemId
        var workedOnTaskMap = {};
        taskItemMap["workedOn"] = workedOnTaskMap;
        var workingOnTaskMap = {};
        taskItemMap["workingOn"] = workingOnTaskMap;
        var blockersTaskMap = {};
        taskItemMap["blockers"] = blockersTaskMap;

    };

    // getting all config setting (status enable days)
    $scope.loadConfigSetting = function () {

        settingsService.getConfigSetting().then(function (response) {
            if (response.success) {
                $scope.statusEnable = response.data.statusEnable;
                $scope.loadStatusDates();
                $scope.usersstatusLoad();
            }
        }, function (error) {
        });
    };

    // loading all values of time period like weekly,monthly,etc
    $scope.loadTimePeriods = function () {
        dropdownListService.getTimePeriod().then(function (response) {
            $scope.timePeriod = response.data;
            $scope.timeValue = response.data[2].value;

        }, function (error) {
        });
    };

    $scope.submitStatus = function () {

        var status = new Object();
        status.projectId = $scope.project;
        status.hours = $scope.hours;
        status.workingOn = $scope.workingOn;
        status.workedOn = $scope.workedOn;
        status.blockers = $scope.blockers;
        status.dateTime = $scope.statusDate;
        status.taskDataMap = taskItemMap;
        if (!status.projectId) {
            appNotifyService.error('Please select a valid project');
            return false;
        } else if (!status.hours) {
            appNotifyService.error('Please fill in the hours for the selected project.');
            return false;
        } else if (!status.workedOn && !status.workingOn) {
            appNotifyService.error('WorkedOn or WorkingOn field can not be empty');
            return false;
        }

        statusService.submitStatus(status).then(function (response) {
            if (response.success) {
                $scope.clearStatus();
                $scope.usersstatusLoad();
                appNotifyService.success('Status for ' + response.data.gridData[0].date + ' is added successfully');
            }

        }, function (error) {
            appNotifyService.error('Error while submitting status');
        });
    };

    $scope.usersstatusLoad = function () {

        statusService.getAllStatus(time).then(function (response) {
            if (response.success) {
                $scope.allstatus = response.data.gridData;
                $rootScope.dateArr = [];

                var dateResult, i;

                for (i = 0; i <= $scope.statusEnable; i++) {

                    dateResult = $filter('date')(new Date().setDate(new Date().getDate() - i));
                    $scope.dateArr.push(dateResult);
                }
            }
        });
    };

    $scope.loadStatusDates = function () {

        if ($rootScope.statusDateFromLink)
            $scope.statusDate = $filter('date')(new Date(parseInt($rootScope.statusDateFromLink)));

        else
            $scope.statusDate = $filter('date')(new Date());

        $rootScope.dateArray = [];
        var dateResult, i;

        for (i = 0; i <= $scope.statusEnable; i++) {
            dateResult = $filter('date')(new Date().setDate(new Date().getDate() - i));
            $scope.dateArray.push(dateResult);
        }
    };

    $scope.timePeriodChange = function (timePeriod) {

        statusService.getAllStatus(timePeriod).then(function (response) {
            if (response.success) {
                $scope.allstatus = response.data.gridData;
                $rootScope.dateArr = [];
                var dateResult, statusEnable, i;
                statusEnable = $scope.statusEnable;

                for (i = 0; i <= statusEnable; i++) {
                    dateResult = $filter('date')(new Date().setDate(new Date().getDate() - i));
                    $scope.dateArr.push(dateResult);
                }
            }
        });
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

    $scope.showEditIcon = function (date) {
        var result = false;
        angular.forEach($rootScope.dateArr, function (value)
        {
            if (date.valueOf() === value.valueOf())
                result = true;
        });
        return result;
    };
    $scope.loadProjects = function () {
        $scope.isDisable = false;
        projectService.getUserproject().then(function (response) {
            if (response.success) {
                $scope.allprojects = response.data.gridData;
            }
        });
        dropdownListService.getHours().then(function (response) {
            $scope.selectables = response.data;
        });
    };

    $scope.deleteStatus = function (id, dateIndex, statusIndex) {
        statusService.deleteStatus(id).then(function (response) {
            if (response.success) {
                appNotifyService.success('Status has been successfully deleted.');
            }
            $scope.allstatus[dateIndex].statusList.splice(statusIndex, 1);
        });
    };

    /*Update */
    $scope.showUpdatePage = function (id) {

        statusService.getStatusWithId(id).then(function (response) {
            if (response.success) {

                // always return one object when query to get status by statusId
                $scope.statusdata = response.data.gridData[0];

                $scope.status = $scope.statusdata.statusList;
                $scope.project = $scope.status[0].projectId;
                $scope.hours = $scope.status[0].hours.toString();
                $scope.workingOn = $scope.status[0].workingOn;
                $scope.workedOn = $scope.status[0].workedOn;
                $rootScope.statusId = $scope.status[0].statusId;
                $scope.blockers = $scope.status[0].blockers;
                $scope.statusDate = $scope.statusdata.date;
                taskItemMap = $scope.status[0].taskDataMap;
                $scope.submitButtonText = "UPDATE";
                $scope.isSubmit = false;
                $scope.isDisable = true;
            }
        });
    };
    //ends

    $scope.updateStatus = function () {

        var id = $rootScope.statusId;
        var newstatus = new Object();
        newstatus.projectId = $scope.project;
        newstatus.hours = $scope.hours;
        newstatus.workingOn = $scope.workingOn;
        newstatus.workedOn = $scope.workedOn;
        newstatus.blockers = $scope.blockers;
        newstatus.dateTime = $scope.statusDate;
        newstatus.taskDataMap = taskItemMap;

        if (!newstatus.projectId) {
            appNotifyService.error('Please select a valid project');
            return false;
        } else if (!newstatus.hours) {
            appNotifyService.error('Please fill in the hours for the selected project.');
            return false;
        } else if (!newstatus.workedOn && !newstatus.workingOn) {
            appNotifyService.error('WorkedOn or WorkingOn field can not be empty');
            return false;
        }
        // calling status service
        statusService.updateStatus(newstatus, id).then(function (response) {
            if (response.success) {

                $scope.clearStatus();
                $scope.usersstatusLoad();
                $scope.isSubmit = true;
                $scope.submitButtonText = "SUBMIT";
                $scope.isDisable = false;
                if ($rootScope.statusDateFromLink === undefined || $rootScope.statusDateFromLink === null)
                    $scope.statusDate = $filter('date')(new Date());
                else
                    $scope.statusDate = $filter('date')(new Date(parseInt($rootScope.statusDateFromLink)));
                appNotifyService.success('Status for ' + response.data.gridData[0].date + ' is updated successfully');

            }

        }, function (error) {
            appNotifyService.error('Error while updating status');
        });
    };

    // Clearing the text area for status page
    $scope.clearStatus = function () {

        $scope.project = "";
        $scope.hours = "";
        $scope.workingOn = '';
        $scope.workedOn = '';
        $scope.blockers = '';
        $scope.loadTasks();
    };

    //Reset status page
    $scope.reset = function () {

        $scope.project = "";
        $scope.hours = "";
        $scope.statusDate = $filter('date')(new Date());
        $scope.isSubmit = true;
        $scope.submitButtonText = "SUBMIT";
        $scope.isDisable = false;
        $scope.loadTasks();
    };

    // this is to open task view modal
    $scope.openTaskModal = function (projectId) {

        if (!projectId) {
            appNotifyService.error('Please select valid project to view task');
            return false;
        }
        tasksService.getAllTasksForProject(projectId).then(function (response) {
            if (response.success) {
                $scope.taskList = response.data;
                if ($scope.taskList.length !== 0) {
                    showModal();
                } else {
                    appNotifyService.error('No tasks for selected project');
                    return false;
                }
            }
        });

        function showModal(projectId) {
            var uibModalInstance = $uibModal.open({
                animation: true,
                templateUrl: 'app/partials/taskView-modal.html',
                controller: 'taskViewModalCtrl',
                backdrop: 'static',
                size: 'xl',
                resolve: {
                    modalData: function () {
                        return {
                            "title": 'Task View',
                            "taskData": $scope.taskList,
                            "taskItemMap": taskItemMap,
                            "itemKey": itemKey,
                            "hoursList": $scope.selectables,
                            "projectId": projectId
                        };
                    }
                }
            });

            uibModalInstance.result.then(function (taskObject) {
                if (!$scope.workedOn)
                    $scope.workedOn = '';
                if (!$scope.workingOn)
                    $scope.workingOn = '';
                if (!$scope.blockers)
                    $scope.blockers = '';
                $scope.workedOn = $scope.workedOn + taskObject.addToWorkedOn;
                $scope.workingOn = $scope.workingOn + taskObject.addToWorkingOn;
                $scope.blockers = $scope.blockers + taskObject.addToBlockers;
                taskItemMap = taskObject.taskItemMap;
                itemKey = taskObject.itemKey;
                if (taskObject.totalHrs > 0 && !$scope.hours)
                    $scope.hours = taskObject.totalHrs.toString();
                else {
                    var updatedHours = parseFloat($scope.hours);
                    updatedHours = updatedHours + taskObject.totalHrs;
                    if (updatedHours > 12) {
                        appNotifyService.success('Maximum 12 hours allowed to add');
                        updatedHours = 12;
                    }
                    $scope.hours = updatedHours.toString();
                }
            });
        }
    };
});