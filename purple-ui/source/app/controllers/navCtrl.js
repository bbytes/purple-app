rootApp.controller('navCtrl', ['$scope', '$location', function ($scope, $location) {
	
	$scope.isActive = function (viewLocation) {
     var active = (viewLocation === $location.path());
     return active;
	 
};

 $(document).ready(function () {
        $('.dropdown-toggle').dropdown();
        });

}]);