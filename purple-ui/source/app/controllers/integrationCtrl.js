/**
 * Integration Controller
 */
rootApp.controller('integrationCtrl', function ($scope, $rootScope, appNotifyService, $state, $location, integrationService) {

    $rootScope.navClass = 'nav navbar-nav';
    $rootScope.navstatusClass = 'right-nav-ct';
    $rootScope.bodyClass = 'body-standalone1';

    // this variable is used for slack url
    $scope.slackUrl = $rootScope.baseUrl + 'social/slack?emailId=' + $rootScope.loggedInUser;
    // every integration setting mode
    $scope.mode = $state.params.app;

    // used to initialise mode on page load
    $scope.initIntegration = function () {
        $scope.activeTab($scope.mode);
    };

    // checking active tab for given route param mode
    $scope.isActive = function (viewLocation) {
        var active = (viewLocation === $location.url());
        return active;
    };

    // used to initialise mode on slection tab
    $scope.activeTab = function (mode) {

        $scope.mode = mode;
        $scope.activeTabClass = 'tab-pane active';
        $state.go('integration', {app: $scope.mode});
    };

    $scope.connectToJira = function () {

        var dataObj = new Object();
        dataObj.userName = $scope.username;
        dataObj.password = $scope.password;
        dataObj.jiraBaseUrl = $scope.jiraurl;

        integrationService.connectToJira(dataObj).then(function (response) {
            if (response.success) {
                appNotifyService.success('You have been connected to JIRA');
                $scope.isConnected = true;
                $scope.isOffline = false;
            } else {
                $scope.isConnected = false;
                $scope.isOffline = true;
            }
        }, function (error) {
            appNotifyService.error('Error while connecting to JIRA');
        });
    };

    $scope.getJiraConnection = function () {

        integrationService.getJiraConnection().then(function (response) {
            if (response.success) {
                $scope.isConnected = true;
                $scope.isOffline = false;
            } else {
                $scope.isConnected = false;
                $scope.isOffline = true;
            }
        }, function (error) {
            $scope.isConnected = false;
            $scope.isOffline = true;

        });
    };

    $scope.syncJiraProject = function () {

        integrationService.getJiraProject().then(function (response) {
            if (response.success) {
                appNotifyService.success('Projects are Sync');

            }
        }, function (error) {

            appNotifyService.error('Error getting JIRA Connection');
        });

    };

    //Connect to slack
    $scope.getSlackChannels = function () {

        integrationService.getSlackChannels().then(function (response) {
            if (response.success) {
                $scope.channelList = response.data;
                $scope.isSlackConnect = true;
            } else {
                $scope.isSlackConnect = false;
            }
        }, function (error) {
            appNotifyService.error('Error Connecting Slack Channel');
        });
    };

    //Setting slack channel
    $scope.setChannel = function () {

        if (!$scope.selectedChannel) {
            appNotifyService.error('Please select valid slack channel');
            return false;
        }
        integrationService.setSlackChannel($scope.selectedChannel).then(function (response) {
            if (response.success) {
                appNotifyService.success('You have been updated slack channel successfully');
            } else {
                appNotifyService.error('Error while saving Slack Channel');
            }
        }, function (error) {
            appNotifyService.error('Error while saving Slack Channel');
        });
    };

});
