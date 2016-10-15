/*
 * Status Controller
 */
angular.module('rootApp').controller('statusCtrl', function ($scope, $rootScope, dropdownListService, statusService, projectService, appNotifyService, settingsService, $filter, cfpLoadingBar) {

    $rootScope.bodyClass = 'body-standalone1';
    $rootScope.navClass = 'nav-control';
    $rootScope.navstatusClass = 'nav navbar-nav';

    $rootScope.statusClass = 'status-current';
    $rootScope.dashboardClass = 'dashboard-nav';
    $rootScope.settingClass = 'setting-nav';
    $rootScope.feedbackClass = 'feedback-log feedback-show';
    $scope.isSubmit = true;

    $scope.submitStatus = function () {

        var status = new Object();
        status.projectId = $scope.project;
        status.hours = $scope.hours;
        status.workingOn = $scope.workingOn;
        status.workedOn = $scope.workedOn;
        status.blockers = $scope.blockers;
        status.dateTime = $scope.statusDate;
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

        var time = "Weekly";

        settingsService.getConfigSetting().then(function (response) {
            if (response.success)
                $rootScope.statusEnable = response.data.statusEnable;
        }, function (error) {
        });

        dropdownListService.getTimePeriod().then(function (response) {
            $scope.timePeriod = response.data;
            $scope.mytime = response.data[2].value;

        }, function (error) {
        });

        statusService.getAllStatus(time).then(function (response) {
            if (response.success) {

                $scope.artists = [];
                $rootScope.dateArr = [];

                var dateResult, statusEnable, i;
                statusEnable = $rootScope.statusEnable;

                for (i = 0; i <= statusEnable; i++) {

                    dateResult = $filter('date')(new Date().setDate(new Date().getDate() - i));
                    $scope.dateArr.push(dateResult);
                }
                angular.forEach(response.data.gridData, function (value, key) {

                    $scope.artists.push(value);
                    $scope.allstatus = value.statusList;
                });
            }
        });
    };

    $scope.loadStatusDates = function () {

        if ($rootScope.statusDateFromLink)
            $scope.statusDate = $filter('date')(new Date(parseInt($rootScope.statusDateFromLink)));

        else
            $scope.statusDate = $filter('date')(new Date());

        settingsService.getConfigSetting().then(function (response) {
            if (response.success)
                $rootScope.statusEnable = response.data.statusEnable;

            $rootScope.dateArray = [];
            var dateResult, statusEnable, i;
            statusEnable = $rootScope.statusEnable;

            for (i = 0; i <= statusEnable; i++) {

                dateResult = $filter('date')(new Date().setDate(new Date().getDate() - i));
                $scope.dateArray.push(dateResult);
            }
        }, function (error) {
        });

    };

    $scope.timePeriodChange = function (timePeriod) {

        statusService.getAllStatus(timePeriod).then(function (response) {
            if (response.success) {

                $scope.artists = [];
                $rootScope.dateArr = [];

                var dateResult, statusEnable, i;
                statusEnable = $rootScope.statusEnable;

                for (i = 0; i <= statusEnable; i++) {
                    dateResult = $filter('date')(new Date().setDate(new Date().getDate() - i));
                    $scope.dateArr.push(dateResult);
                }
                angular.forEach(response.data.gridData, function (value, key) {

                    $scope.artists.push(value);
                    $scope.allstatus = value.statusList;
                });
            }
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
                $("#projectDropdown").trigger("chosen:updated");
            }
        });
        dropdownListService.getHours().then(function (response) {
            $scope.selectables = response.data;
            $("#hoursDropdown").trigger("chosen:updated");
        });
    };

    $scope.deleteStatus = function (id, $index) {
        statusService.deleteStatus(id).then(function (response) {
            if (response.success) {
                appNotifyService.success('Status has been successfully deleted.');
            }
            $scope.allstatus.splice($index, 1);
            $scope.usersstatusLoad();
        });
    };

    /*Update */
    $scope.showUpdatePage = function (id) {

        statusService.getStatusWithId(id).then(function (response) {
            if (response.success) {

                $scope.statusdata = response.data.gridData;

                angular.forEach(response.data.gridData, function (value, key) {

                    $scope.allstatus = value.statusList;
                    $scope.project = $scope.allstatus[0].projectId;
                    $scope.hours = $scope.allstatus[0].hours.toString();
                    $scope.workingOn = $scope.allstatus[0].workingOn;
                    $scope.workedOn = $scope.allstatus[0].workedOn;
                    $rootScope.statusId = $scope.allstatus[0].statusId;
                    $scope.blockers = $scope.allstatus[0].blockers;
                    $scope.statusDate = value.date;
                    $scope.isUpdate = true;
                    $scope.isSubmit = false;
                    $scope.loadProjects();
                    $scope.isDisable = true;

                });
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
                $scope.isUpdate = false;
                $scope.isDisable = false;
                if ($rootScope.statusDateFromLink === undefined && $rootScope.statusDateFromLink === null)
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

        $scope.project = '';
        $scope.hours = '';
        $scope.workingOn = '';
        $scope.workedOn = '';
        $scope.blockers = '';
    };

    //Reset status page
    $scope.reset = function () {

        $scope.isSubmit = true;
        $scope.isUpdate = false;
        $scope.isDisable = false;
    };

    // avoid spacing while copy paste in text angular
    $scope.stripFormat = function ($html) {
        return $filter('htmlToPlaintext')($html);
    };

});