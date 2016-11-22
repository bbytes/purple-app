/*
 * Integration Controller
 * @author - Akshay
 */
angular.module('rootApp').controller('integrationCtrl', function ($scope, $rootScope, appNotifyService, $state, $location, integrationService) {

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

    // method is used to sync the JIRA Projects
    $scope.syncJiraProject = function () {

        integrationService.getJiraProject().then(function (response) {
            if (response.success) {
                appNotifyService.success('Projects are Sync');
            }
        }, function (error) {
            appNotifyService.error('Error getting JIRA Connection');
        });
    };

    // method is used to sync the JIRA projects to users
    $scope.syncJiraProjectAndUser = function () {

        integrationService.getJiraProjectAndUser().then(function (response) {
            if (response.success) {
                appNotifyService.success('Project to users are sync successfully');
            }
        }, function (error) {
            appNotifyService.error('Error getting JIRA Connection');
        });
    };

    // method is used to disconnect JIRA Connection
    $scope.disconnectJIRAConnection = function () {

        integrationService.deleteJiraIntegration().then(function (response) {
            if (response.success) {
                appNotifyService.success('You have been successfully disconnected to JIRA');
                $scope.isConnected = false;
                $scope.isOffline = true;
            }
        }, function (error) {
            appNotifyService.error('Error disconnecting JIRA Connection');
        });
    };

    // method is used to disconnect SLACK Connection
    $scope.disconnectSlackConnection = function () {

        integrationService.deleteSlackIntegration().then(function (response) {
            if (response.success) {
                appNotifyService.success('You have been successfully disconnected to Slack');
            }
        }, function (error) {
            appNotifyService.error('Error disconnecting Slack Connection');
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

});
