/**
 * Admin Controller
 */
rootApp.controller('integrationCtrl', function ($scope, $rootScope, $state, $window) {
	
	$scope.isActive = function(route) {
		return route === $location.path();
	}

	$rootScope.navClass = 'nav navbar-nav';
	$rootScope.navstatusClass = 'right-nav-ct';
	$rootScope.bodyClass = 'body-standalone1';
    
	//tab
	

});
