/**
 * 
 */
rootApp.controller('homepageCtrl', function ($scope, $rootScope, $state, projectService,appNotifyService,$window,$location,statusService) {
  $scope.usersstatusLoad = function(){
    	 statusService.getAllStatus().then(function (response) {
             if (response.success) {
            
                 $scope.allstatus   =  response.data.gridData;
             }
         });
     }
     
     $scope.initStatus = function() {
         $scope.usersstatusLoad();
     };
     
     
     $scope.deleteStatus =function(id,$index){
    	
		  statusService.deleteStatus(id).then(function (response) {
	    		if (response.success =true) {
	    		appNotifyService.success( 'Status has been deleted.');
	    	}
	    $scope.allstatus.splice($index, 1);
	    });
	    }
     
});