/**
 * 
 */
rootApp.controller('homepageCtrl', function ($scope, $rootScope, $state, projectService,appNotifyService,$window,$location,statusService, commentService) {
  $scope.usersstatusLoad = function(){
	  $scope.updateData = {
			  projectList :[],
			  userList : []
			 
			
	  }
    	 statusService.getAllTimelineStatus($scope.updateData).then(function (response) {
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
     
	
	 //post reply
     $scope.postComment = function(statusId) {
		  $scope.commentData = {
		 statusId : statusId,
		 commentDesc : $scope.commentDesc
	 }
		 commentService.postComment($scope.commentData).then(function (response) {
			 if(response.success){
				$scope.message = 'Commented Successfully';
			 }
		 });
		 //$scope.commentDesc = !$scope.commentDesc;
		 $scope.commentDesc = null
	 }
	 
	 //post reply to reply
	 $scope.postReply = function(replyObj) {
		//  $scope.replyObj = {
		 //statusId : "57021aa67dba891b747bab9a",
		// replyDesc : $scope.replyComment
	// }
		 commentService.postReply(replyObj).then(function (response) {
			 if(response.success){
				$scope.message = 'Replied Successfully';
			 } else {
				 $scope.message = "Reply failed";
			 }
		 });
		 //$scope.commentDesc = !$scope.commentDesc;
		 $scope.replyComment = null
	 }
   
     /* Method to get Loggedin user projects for status*/
     
     $scope.loadUsersProjects = function(){
     	projectService.getUserproject().then(function (response) {
             if (response.success) {
            
                 $scope.userprojects   =  response.data.gridData;
             }
         });
     }
     $scope.initUserProjects = function() {
         $scope.loadUsersProjects();
     };
     
     //get comments
	 $scope.loadComment = function(statusId){
    	commentService.getComment(statusId).then(function (response) {
            if (response.success) {
            	if (response) {
					$scope.commentcount = response.data.length;
				}
            	$scope.commentsCount = response.data.gridData.usersCount;
            	$scope.commentCount = response.data.comment_count;
//            	 repeatSelect: null,
                $scope.allcomments   =  response.data.gridData;
				console.log(response.data.gridData)
            }
        });
    }
	
	 //get replies
	 $scope.loadReply = function(){
		 commentService.getReplies().then(function (response) {
            if (response.success) {
            	if (response) {
					$scope.replycount = response.data.length;
				}
            	$scope.repliesCount = response.data.gridData.repliesCount;
            	$scope.replyCount = response.data.reply_count;
//            	 /repeatSelect: null,
                $scope.allreplies   =  response.data.gridData;
            }
        });
    }
	
	 //get map
	 $scope.loadProjectMap = function(projectId,userName){
		 commentService.getProjectMap(projectId,userName).then(function (response) {
            if (response.success) {
				$scope.projectUsers = response.data.gridData[0].userList;
				$scope.projectName = response.data.gridData[0].projectName
				$scope.artists = response.data.gridData
				$scope.updateData.projectList = [response.data.gridData[0].projectId];

                    statusService.getAllTimelineStatus($scope.updateData).then(function (response) {
                     if (response.success) {
                      $scope.artists = [];
                      angular.forEach(response.data.gridData, function(value, key) {
                      $scope.artists.push(value);
                      });
                      $scope.allstatus   =  response.data.gridData;
                     }
                  });
				
            	if (response) {
					$scope.replyprojectcount = response.data.length;
				}
            	$scope.repliesprojectsCount = response.data.gridData.repliesprojectsCount;
            	$scope.replyprojectCount = response.data.replyproject_count;
//            	 /repeatSelect: null,
                $scope.allrepliesprojects   =  response.data.gridData;
            }
        });
    }
	
	 
});