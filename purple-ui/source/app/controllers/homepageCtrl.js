/**
 * 
 */
rootApp.controller('homepageCtrl', function ($scope, $rootScope, $state, projectService,appNotifyService,$window,$location,statusService, commentService) {
	
	 $scope.commentDesc = '';
    $scope.isActive = function(route) {
        return route === $location.path();
    }

	
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
                 $scope.isActive = true;
                 $scope.isProject = false;
                 $scope.isUser = false;
                
             }
         });
     }
     
     $scope.initStatus = function() {
         $scope.usersstatusLoad();
     };
     
	
	 //post comment
	 $scope.formData = {};
     $scope.addComment = function(statusId) {
		 console.log($scope.selectedStatusId);
		 console.log(statusId);
		  $scope.commentData = {
		 statusId : statusId,
		 commentDesc : $scope.formData.commentDesc
	 }
		 commentService.postComment($scope.commentData).then(function (response) {
			 if(response.success){
				$scope.message = 'Commented Successfully';
				$scope.formData.commentDesc = '';
			 }
			 
		 });
		  
		 //$scope.commentDesc = !$scope.commentDesc;
		 //$scope.commentDesc = '';
	 }
	 
	 //post comment reply
	 $scope.postReply = function(replyObj, commentId) {
		console.log(commentId)
		console.log($scope.commentId)
		 commentService.postReply(replyObj, commentId).then(function (response) {
			 if(response.success){
				$scope.message = 'Replied Successfully';
			 } else {
				 $scope.message = "Reply failed";
			 }
		 });
		 //$scope.commentDesc = !$scope.commentDesc;
		 $scope.replyComment = '';
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
	 $scope.openCommentSideBar = function(selectedStatusId){
		 $scope.selectedStatusId = selectedStatusId;
    	commentService.getComment(selectedStatusId).then(function (response) {
            if (response.success) {
            	if (response) {
					$scope.commentcount = response.data.length;
				}
            	$scope.commentsCount = response.data.gridData.commentCount;
            	$scope.commentCount = response.data.comment_count;
//            	 repeatSelect: null,
                $scope.allcomments   =  response.data.gridData;
            }
        });
    }
	
	 //get replies
	 $scope.loadReply = function(statusId){
		 commentService.getReplies(statusId).then(function (response) {
			 $scope.commentId = commentId;
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
	 $scope.loadProjectMap = function(projectId){
		 commentService.getProjectMap(projectId).then(function (response) {
            if (response.success) {
				$scope.projectUsers = response.data.gridData[0].userList;
				$scope.projectName = response.data.gridData[0].projectName
				//$scope.artists = response.data.gridData
				$scope.updateData.projectList = [response.data.gridData[0].projectId];

                    statusService.getAllTimelineStatus($scope.updateData).then(function (response) {
                     if (response.success) {
        
                      $scope.artists = [];
                      angular.forEach(response.data.gridData, function(value, key) {
                      $scope.artists.push(value);
                      });
                      $scope.allstatus   =  response.data.gridData;
                      
                      $scope.selected = $scope.allstatus[0].statusList[0];
                      $scope.isProject = true;
                      $scope.isActive = false;
                      $scope.isUser = false;
                     }
                  });
            }
        });
    }

	 
	 $scope.loadUserMap = function(email){
		 		$scope.updateData.projectList = [];
				$scope.updateData.userList = [email];

                    statusService.getAllTimelineStatus($scope.updateData).then(function (response) {
                     if (response.success) {
        
                      $scope.artists = [];
                      angular.forEach(response.data.gridData, function(value, key) {
                      $scope.artists.push(value);
                      });
                      $scope.allstatus   =  response.data.gridData;
                      $scope.selected = $scope.allstatus[0].statusList[0];
                      $scope.isUser = true;
                      $scope.isProject = false;
                      $scope.isActive = false;
                     }
                  });
				
    }
	 

	            $scope.checked = false; // This will be binded using the ps-open attribute
                $scope.toggle = function(){
                $scope.checked = !$scope.checked
					
                }
				
	
	
    $scope.selected = 0;

    $scope.select= function(index) {
       $scope.selected = index; 
    };
            

});