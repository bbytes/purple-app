/*
 * Logout Controller
 */
rootApp.controller('logoutCtrl', function ($scope, $rootScope, $state, logoutService,$location,$sessionStorage,$window) {

	
	  $scope.logout = function() {
		  delete $window.sessionStorage.token;
		  delete $rootScope.statusDate;
		  $sessionStorage.$reset();
		  $state.go("login");
		};
});
