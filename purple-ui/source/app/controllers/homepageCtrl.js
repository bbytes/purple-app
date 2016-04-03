/**
 * 
 */
rootApp.controller('homepageCtrl', function ($scope, $rootScope, $state, projectService,appNotifyService,$window,$location,statusService, commentService) {
  $scope.usersstatusLoad = function(){
    	 statusService.getAllStatus().then(function (response) {
             if (response.success) {
            	 $scope.artists = [];
         	    angular.forEach(response.data.gridData, function(value, key) {
         	        $scope.artists.push(value);
         	    });
                 $scope.allstatus   =  response.data.gridData;
             }
         });
     }
     
     $scope.initStatus = function() {
         $scope.usersstatusLoad();
     };
     
	
	 
     $scope.postComment = function() {
		  $scope.commentData = {
		 statusId : "56fe6795055f8a080c01f67d",
		 commentDesc : $scope.commentDesc
	 }
		 commentService.postComment($scope.commentData);
		 //$scope.commentDesc = !$scope.commentDesc;
		 $scope.commentDesc = null
	 }
   
     
});