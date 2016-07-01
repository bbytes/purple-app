    /**
     *  Update Status Controller
     */
    rootApp.controller('updateStatusCtrl', function ($scope, $rootScope, $state,$q,$http,$window,$sessionStorage,appNotifyService) {
    	
    		$window.sessionStorage.token = $state.params.token;
          
             $rootScope.authToken = $state.params.token;
             $rootScope.statusDateFromLink = $state.params.sd;
             
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
                    $rootScope.timePreference = response.data.timePreference;
                    $rootScope.switchState = response.data.emailNotificationState;
                    $rootScope.timeZone = response.data.timeZone;

                    var userInfo = {
                    email: $rootScope.loggedInUser,
                    name: $rootScope.userName,
                    userRoles:  $rootScope.userRole,
                    timePreference :  $rootScope.timePreference,
                    emailNotificationState : $rootScope.switchState,  
                    timeZone : $rootScope.timeZone,
                   };
              
                 $sessionStorage.userInfo =  userInfo;
                  $state.go("status");

                    deferred.resolve(response);
                }).error(function(response) {
                    deferred.reject(response);
                    appNotifyService.error('The link is expired');
                     $state.go("login");
                });

                
                 return deferred.promise;   
    });