    /**
     *  Activate Account Controller
     */
    rootApp.controller('activateAccountCtrl', function ($scope, $rootScope, $state,$q,$http,$window,$sessionStorage,appNotifyService) {
    	
    	$scope.init = function (){
    		$window.sessionStorage.token = $state.params.token;
          
             $rootScope.authToken = $state.params.token;
             
             var tokenstored = $rootScope.authToken;
         		var deferred = $q.defer();

         		$http({
         			method : 'GET',
         			url : $rootScope.baseUrl + 'api/v1/admin/activateAccount',
         		
         			headers : {
         				'Content-Type' : 'application/json',
         			}
         		}).success(function(response, status, headers, config) {

                    $rootScope.userRole = response.data.userRole.id;

                     var userInfo = {
                    name: $rootScope.userName,
                    userRoles:  $rootScope.userRole,
                };
              
                $sessionStorage.userInfo =  userInfo;
         			deferred.resolve(response);
         			if(response.data.accountInitialise = true && $sessionStorage.userInfo.userRoles == "NORMAL")
         				{
         				 appNotifyService.success('Your account has been activated successfully. Redirecting to settings.');
         				 $state.go("settings-user");
         				}
         			else{
         				appNotifyService.success('Your account has been activated successfully. Why dont you invite users?');
        				 $state.go("user-mgr");
         			}
         		}).error(function() {
         			deferred.reject({
         				'success' : false,
         				'msg' : 'Oops! Something went wrong. Please try again later.'
         			});
         		});

         		return deferred.promise;
    	}
    });