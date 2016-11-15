/*
 * Tasks Service
 */

angular.module('rootApp').service('tasksService', function ($rootScope, $http, $q) {

    this.getAllTasksStates = function () {

        var deferred = $q.defer();

        $http({
            method: 'GET',
            url: $rootScope.baseUrl + 'api/v1/task/taskStates',
            headers: {
                'Content-Type': 'application/json'
            }

        }).success(function (response, status, headers, config) {

            deferred.resolve(response);
        }).error(function (response) {
            deferred.reject(response);
        });

        return deferred.promise;
    };
    this.getAllTasksForState = function (taskState) {

        var deferred = $q.defer();

        $http(
                {
                    method: 'GET',
                    url: $rootScope.baseUrl + 'api/v1/task/taskList/state/'
                            + taskState.id,
                    headers: {
                        'Content-Type': 'application/json'
                    }

                }).success(function (response, status, headers, config) {

            deferred.resolve(response);
        }).error(function (response) {
            deferred.reject(response);
        });

        return deferred.promise;
    };
    this.getAllTasksForProject = function (projectId) {
        var deferred = $q.defer();

        $http(
                {
                    method: 'GET',
                    url: $rootScope.baseUrl
                            + 'api/v1/task/taskList/project/'
                            + projectId,
                    headers: {
                        'Content-Type': 'application/json'
                    }

                }).success(function (response, status, headers, config) {

            deferred.resolve(response);
        }).error(function (response) {
            deferred.reject(response);
        });

        return deferred.promise;

    };
    this.getAllTasksForProjectAndState = function (project, taskState) {
        var deferred = $q.defer();

        $http(
                {
                    method: 'GET',
                    url: $rootScope.baseUrl + 'api/v1/task/taskList/'
                            + project.projectId + '/' + taskState.id,
                    headers: {
                        'Content-Type': 'application/json'
                    }

                }).success(function (response, status, headers, config) {

            deferred.resolve(response);
        }).error(function (response) {
            deferred.reject(response);
        });

        return deferred.promise;
    };
    this.createTaskList = function (taskList) {
        var deferred = $q.defer();

        $http({
            method: 'POST',
            url: $rootScope.baseUrl + 'api/v1/task/taskList',
            data: taskList,
            headers: {
                'Content-Type': 'application/json'
            }

        }).success(function (response, status, headers, config) {

            deferred.resolve(response);
        }).error(function (response) {
            deferred.reject(response);
        });

        return deferred.promise;
    };
    this.getTaskItems = function (taskList) {
        var deferred = $q.defer();

        $http(
                {
                    method: 'GET',
                    url: $rootScope.baseUrl + 'api/v1/task/taskItems/'
                            + taskList.taskListId,
                    headers: {
                        'Content-Type': 'application/json'
                    }

                }).success(function (response, status, headers, config) {

            deferred.resolve(response);
        }).error(function (response) {
            deferred.reject(response);
        });

        return deferred.promise;
    };
    this.createTaskItem = function (taskList, taskItem) {
        var deferred = $q.defer();
        $http(
                {
                    method: 'POST',
                    url: $rootScope.baseUrl + 'api/v1/task/taskItem/'
                            + taskList.taskListId,
                    data: taskItem,
                    headers: {
                        'Content-Type': 'application/json'
                    }

                }).success(function (response, status, headers, config) {

            deferred.resolve(response);
        }).error(function (response) {
            deferred.reject(response);
        });

        return deferred.promise;
    };
    this.deleteTaskList = function (taskList) {
        var deferred = $q.defer();
        $http(
                {
                    method: 'DELETE',
                    url: $rootScope.baseUrl
                            + 'api/v1/task/taskList/'
                            + taskList.taskListId,
                    headers: {
                        'Content-Type': 'application/json'
                    }

                }).success(function (response, status, headers, config) {

            deferred.resolve(response);
        }).error(function (response) {
            deferred.reject(response);
        });

        return deferred.promise;
    };
    this.deleteTaskItem = function (taskItem) {
        var deferred = $q.defer();
        $http(
                {
                    method: 'DELETE',
                    url: $rootScope.baseUrl
                            + 'api/v1/task/taskItem/'
                            + taskItem.taskItemId,
                    headers: {
                        'Content-Type': 'application/json'
                    }

                }).success(function (response, status, headers, config) {

            deferred.resolve(response);
        }).error(function (response) {
            deferred.reject(response);
        });

        return deferred.promise;
    };
    this.markCompleted=function(taskItem){
    	 $http(
                 {
                     method: 'POST',
                     url: $rootScope.baseUrl
                             + 'api/v1/task/taskItems/'+taskItem.taskItemId+'/complete',
                     headers: {
                         'Content-Type': 'application/json'
                     }

                 });
    };
});