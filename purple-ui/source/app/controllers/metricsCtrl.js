
rootApp.controller('metricsCtrl', function ($scope, $rootScope, $state, $mdSidenav, dropdownListService, projectService, appNotifyService, $window, $location, metricsService, commentService, editableOptions, $mdSidenav) {

    $rootScope.bodyClass = 'body-standalone1';
    $rootScope.navClass = 'nav-control';
    $rootScope.navstatusClass = 'nav navbar-nav';

    $rootScope.statusClass = 'status-nav';
    $rootScope.dashboardClass = 'dashboard-current';
    $rootScope.settingClass = 'setting-nav';

    $scope.onClick = function (points, evt) {
        console.log(points, evt);
    };

    $scope.loadTimePeriod = function () {

        dropdownListService.getTimePeriod().then(function (response) {
            $scope.timePeriod = response.data;
            $scope.mytime = response.data[2].value;

        }, function (error) {
        });

    };

    /**
     * Load all projects of logged in user
     */
    $scope.loadUserProjects = function () {
        axes: [{
                name: "EnergyAxis",
                type: "numericY",
                title: "Quadrillion Btu"
            }],
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
                    $scope.projectUsers = response.data.gridData;
                }
            });
        });

    };

    $scope.loadGroupBy = function () {

        dropdownListService.getProjectAndUser().then(function (response) {
            $scope.groupbyProjectUserfield = response.data;
            $scope.projectuser = response.data[0].value;

        }, function (error) {
        });

        dropdownListService.getStatusCountAndHours().then(function (response) {
            $scope.counthoursfield = response.data;
            $scope.counthours = response.data[0].value;

        }, function (error) {
        });

    };

    $scope.loadAllStatusGraph = function (time, projectuser, counthours) {

        if (time == null || time == 'undefined')
            time = "Weekly";
        if (projectuser == null || projectuser == 'undefined')
            projectuser = "Project";
        if (counthours == null || counthours == 'undefined')
            counthours = "Status Hours";
        $scope.updateData = {
            projectList: [],
            userList: [],
            projectUser: projectuser,
            countHours: counthours
        };
        metricsService.getAllStatusAnalytics($scope.updateData, time).then(function (response) {

            $scope.isGroupBy = false;
            $scope.isActive = true;

            $scope.labels = [];
            $scope.series = [];
            $scope.data = [];

            if (response.success) {

                $scope.labels = response.data.labels;
                $scope.series = response.data.series;
                $scope.data = response.data.data;
            }

        });
    };

    $scope.loadStatusGraph = function (time, projectuser, counthours) {

        $rootScope.itemChoose = projectuser;
        var email, projectId;

        if (projectuser.projectId == null || projectuser.projectId == 'undefined')
            email = projectuser.email;
        else
            projectId = projectuser.projectId;
        $scope.updateData = {
            projectList: [projectId],
            userList: [email],
            countHours: counthours
        };
        metricsService.getAllStatusAnalytics($scope.updateData, time).then(function (response) {

            $scope.isGroupBy = true;
            $scope.isActive = false;

            $scope.labels = [];
            $scope.series = [];
            $scope.data = [];

            if (response.success) {

                $scope.labels = response.data.labels;
                $scope.series = response.data.series;
                $scope.data = response.data.data;
            }

        });
    };


    $scope.groupBychange = function (timePeriod, projectuser, counthours) {

        var itemSelected = $rootScope.itemChoose;
        if ($scope.isGroupBy)
            $scope.loadStatusGraph(timePeriod, itemSelected, counthours);
        else
            $scope.loadAllStatusGraph(timePeriod, projectuser, counthours);
    };


    $scope.timeChange = function (timePeriod) {

        $scope.loadAllStatusGraph(timePeriod);
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


    //dropdown
    $scope.project = ["Projects", "Users"];

    $scope.hours = ["Status Count", "Status Hours"];

    $scope.addPoints = function () {
        var seriesArray = $scope.highchartsNG.series
        var rndIdx = Math.floor(Math.random() * seriesArray.length);
        seriesArray[rndIdx].data = seriesArray[rndIdx].data.concat([1, 10, 20])
    };

    $scope.addSeries = function () {
        var rnd = []
        for (var i = 0; i < 10; i++) {
            rnd.push(Math.floor(Math.random() * 20) + 1)
        }
        $scope.highchartsNG.series.push({
            data: rnd
        })
    };

    $scope.removeRandomSeries = function () {
        var seriesArray = $scope.highchartsNG.series
        var rndIdx = Math.floor(Math.random() * seriesArray.length);
        seriesArray.splice(rndIdx, 1);
    };

    $scope.options = {
        type: 'line'
    };

    $scope.swapChartType = function () {
        if (this.highchartsNG.options.chart.type === 'line') {
            this.highchartsNG.options.chart.type = 'bar'
        } else {
            this.highchartsNG.options.chart.type = 'line'
        }
    };

    $scope.highchartsNG = {
        options: {
            chart: {
                type: 'bar'
            }
        },
        series: [{
                data: [10, 15, 12, 8, 7]
            }],
        title: {
            text: 'Hello'
        },
        loading: false
    };

});