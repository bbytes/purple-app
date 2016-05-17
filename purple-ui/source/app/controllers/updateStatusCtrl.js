    /**
     *  Update Status Controller
     */
    rootApp.controller('updateStatusCtrl', function ($scope, $rootScope, $state,$q,$http,$window,$sessionStorage,appNotifyService) {
    	
    		$window.sessionStorage.token = $state.params.token;
          
             $rootScope.authToken = $state.params.token;
             $rootScope.statusDate = $state.params.sd;
             
         		var deferred = $q.defer();
               
                    $http({
                    method : 'GET',
                    url : $rootScope.baseUrl + 'api/v1/currentUser',
                
                    headers : {
                        'Content-Type' : 'application/json',
                    }
                }).success(function(response, status, headers, config) {

                    $rootScope.userName = response.data.userName;
                    $rootScope.userRole = response.data.userRole.id;

                    var userInfo = {
                    email: $rootScope.loggedInUser,
                    name: $rootScope.userName,
                    userRoles:  $rootScope.userRole,
                   };
              
                 $sessionStorage.userInfo =  userInfo;

                    deferred.resolve(response);
                }).error(function(response) {
                    deferred.reject(response);
                });

                 $state.go("status");
                 return deferred.promise;   
    });