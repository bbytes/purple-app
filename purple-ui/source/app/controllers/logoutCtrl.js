/**
 * 
 */
rootApp.controller('logoutCtrl', function ($scope, $rootScope, $state, logoutService,$location,$sessionStorage,$window) {
	
	  $scope.logout = function() {
		  delete $window.sessionStorage.token;
		  $state.go("login");
	    /*	logoutService.logout().then(function (response) {
				
					$location.path("login");
				
			});*/
		};
});
