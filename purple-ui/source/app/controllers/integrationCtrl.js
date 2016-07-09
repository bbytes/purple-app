/**
 * Integration Controller
 */
rootApp.controller('integrationCtrl', function ($scope, $rootScope, $state, $window, appNotifyService, integrationService) {

    $scope.isActive = function (route) {
        return route === $location.path();
    };

    $rootScope.navClass = 'nav navbar-nav';
    $rootScope.navstatusClass = 'right-nav-ct';
    $rootScope.bodyClass = 'body-standalone1';

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

});
