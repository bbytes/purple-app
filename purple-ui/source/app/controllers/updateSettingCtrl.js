    /**
     *  Update Status Controller
     */
    rootApp.controller('updateSettingCtrl', function ($scope, $rootScope, $state,$q,$http,$window,$sessionStorage,appNotifyService) {
        
            $window.sessionStorage.token = $state.params.token;
          
             $rootScope.authToken = $state.params.token;
             
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

                    var userInfo = {
                    email: $rootScope.loggedInUser,
                    name: $rootScope.userName,
                    userRoles:  $rootScope.userRole,
                    timePreference :  $rootScope.timePreference,
                   };
              
                 $sessionStorage.userInfo =  userInfo;
                 $state.go("settings");
                    deferred.resolve(response);
                }).error(function(response) {
                    deferred.reject(response);
                    appNotifyService.error('The link is expired');
                     $state.go("login");
                });

                 
                 return deferred.promise;   
    });