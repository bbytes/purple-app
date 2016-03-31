/**
 * 
 */
/**
 * 
 */
rootApp.controller('activateUserAccountCtrl', function ($scope, $rootScope, $state,$q,$http,$window,$sessionStorage,appNotifyService) {
	
	
	$scope.init = function (){
		console.log($state.params)
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

     			deferred.resolve(response);
     			if(response.data.accountInitialise = true)
     				{
     				 appNotifyService.success('Your account activated successfully..redirecting to settings page');
     				 $state.go("settings-user");
     				}
     		}).error(function() {
     			// Something went wrong.
     			deferred.reject({
     				'success' : false,
     				'msg' : 'Oops! Something went wrong. Please try again later.'
     			});
     		});

     		return deferred.promise;

     	
         
       /*  $scope.activateAccount = function (tokenstored) {
         	
          // var token = $rootScope.authToken;
         
             // Calling login service
        	 activateAccountService.activate(tokenstored).then(function (response) {
             	 if (response.success) {
             		 console.log("jayyaa");
             		// $scope.loadUsers();
                 } else {
                     //Login failed. Showing error notification
                     appNotifyService.error(response.data, 'Invite unsuccesfull.');
                 }

             }, function (error) {
                 //Login failed. Showing error notification
                 appNotifyService.error(error.msg, 'Invite unsuccesfull.');
             });
             
         };*/
	}
});
