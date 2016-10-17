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

    // variable is used to intialise time period first time when page get load
    var time = "Weekly";
    // variable to store the information about status enable days
    $scope.statusEnable;
    // variable to store the information abou all user statuses
    $scope.allstatus;

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
        $scope.loadProjects();
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