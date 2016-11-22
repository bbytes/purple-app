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
        var projectId;
        var taskStateId
        if(project=='All')
        	projectId='All';
        else
        	projectId=project.projectId;
        
        if(taskState=='All')
        	taskStateId='All';
        else
        	taskStateId=taskState.id;
        
        

        $http(
                {
                    method: 'GET',
                    url: $rootScope.baseUrl + 'api/v1/task/taskList/'
                            + projectId + '/' + taskStateId,
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
    this.getTaskItems = function (taskList,state) {
        var deferred = $q.defer();
        var taskStateId;        
        if(state=='All')
        	taskStateId='All';
        else
        	taskStateId=state.id;
        $http(
                {
                    method: 'GET',
                    url: $rootScope.baseUrl + 'api/v1/task/taskItems/'
                            + taskList.taskListId+'/'+taskStateId,
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
    	var tItem= angular.copy(taskItem);
    	if(tItem.users!=null)
    		tItem.users.length=0;
        var deferred = $q.defer();
        $http(
                {
                    method: 'POST',
                    url: $rootScope.baseUrl + 'api/v1/task/taskItem/'
                            + taskList.taskListId,
                    data: tItem,
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
    	 var deferred = $q.defer();
    	 $http(
                 {
                     method: 'POST',
                     url: $rootScope.baseUrl
                             + 'api/v1/task/taskItems/'+taskItem.taskItemId+'/complete',
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
    this.addUsersToItem=function(taskItem,usersToBeAdded){
    	 var deferred = $q.defer();
    	 $http(
                 {
                     method: 'POST',
                     url: $rootScope.baseUrl
                             + 'api/v1/task/taskItems/'+taskItem.taskItemId+'/addusers',
                     data:usersToBeAdded,
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
    this.removeUsersFromItem=function(userid,taskItem){
    	 var deferred = $q.defer();
    	 $http(
                 {
                     method: 'POST',
                     url: $rootScope.baseUrl
                             + 'api/v1/task/taskItems/'+taskItem.taskItemId+'/removeuser/'+userid,
                     headers: {
                         'Content-Type': 'application/json'
                     }

                 }).success(function (response, status, headers, config) {

             deferred.resolve(response);
         }).error(function (response) {
             deferred.reject(response);
         });

         return deferred.promise;
    }
    this.getTaskListforId=function(taskListId){
   	 var deferred = $q.defer();
   	 $http(
                {
                    method: 'GET',
                    url: $rootScope.baseUrl
                            + 'api/v1/task/taskList/'+taskListId,
                    headers: {
                        'Content-Type': 'application/json'
                    }

                }).success(function (response, status, headers, config) {

            deferred.resolve(response);
        }).error(function (response) {
            deferred.reject(response);
        });

        return deferred.promise;
   }
});