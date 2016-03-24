rootApp.controller('myHomeCtrl', function ($scope, $rootScope,statusService) {
	
	
	  $scope.deleteStatus =function(id){
		  statusService.deleteStatus(id).then(function (response) {
	    		if (response.success) {
	    			//$route.reload();
	    			$window.location.reload();
	    	}
	    });
	    }

});