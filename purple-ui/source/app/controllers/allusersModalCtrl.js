rootApp.controller('allusersModalCtrl', function ($scope,$rootScope,adminService,options,$uibModalInstance,$uibModal) {

	$scope.selection =[];
   $scope.title = options.title;
    
    $scope.allusers = options.data;
    console.log( $scope.allusers);
    
 
    
    
    $scope.toggleSelection = function toggleSelection(id) {
        var idx = $scope.selection.indexOf(id);

        // is currently selected
        if (idx > -1) {
            $scope.selection.splice(idx, 1);
          }

          // is newly selected
          else {
            $scope.selection.push(id);
          }
       
        // is newly selected
         
         
        
      };
   $scope.ok = function(){
	    console.log($scope.selection);
	
	  
    	$uibModalInstance.close($scope.selection);
    	
    	
    }
    
    $scope.cancel = function () {
        $uibModalInstance.dismiss('cancel');
    };
});