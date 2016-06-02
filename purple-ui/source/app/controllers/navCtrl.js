

rootApp.controller('navCtrl', ['$scope', '$location', function ($scope, $location,cfpLoadingBar) {
	
	$scope.isActive = function (viewLocation) {
     var active = (viewLocation === $location.path());
     return active;
	 
};

 $(document).ready(function () {
        $('.dropdown-toggle').dropdown();
        });

}]);