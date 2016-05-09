/**
 * Dashboard controller to load status timeline
 */
rootApp.controller('dashboardCtrl', function ($scope, $rootScope, $state, $mdSidenav, projectService,appNotifyService,$window,$location,statusService, commentService) {
	
	 $scope.commentDesc = '';
    $scope.isActive = function(route) {
        return route === $location.path();
    }
   /**
    * Get all status timeline
    */
    
  $scope.loadStatusTimeline = function(){
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
	
	 /**
	  * Post comment on status
	  */
	 $scope.formData = {};
     $scope.addComment = function(statusId) {
		  $scope.commentData = {
		 statusId : statusId,
		 commentDesc : $scope.formData.commentDesc
	 }
		 commentService.postComment($scope.commentData).then(function (response) {
			 if(response.success){
				$scope.message = 'Commented Successfully';
				$scope.formData.commentDesc = '';
			 }
			 $scope.getAllComments(statusId); 
		 });
		  
	 }
	 
	 /**
	  * post reply on comment
	  */
	  
	 $scope.postReply = function(replyObj, commentId) {

		 commentService.postReply(replyObj, commentId).then(function (response) {

			 if(response.success){
				replyObj.replyDesc = '';
				$scope.message = 'Replied Successfully';
				$scope.openCommentSideBar($scope.selectedStatusId)
			 } else {
				 $scope.message = "Reply failed";
			 }
		 }.bind(this));
		 $scope.replyComment = '';
		 $scope.loadReply(commentId);
	 }
   
     /**
      * Load all projects of logged in user
      */
     $scope.loadUserProjects = function(){
     	projectService.getUserproject().then(function (response) {
     			var	projectIds = [];
             if (response.success) {
                 $scope.userprojects   =  response.data.gridData;
                 angular.forEach(response.data.gridData, function(value, key) {
                	 projectIds.push(value.projectId);
                     });
             }
     		 projectService.getprojectsUsers(projectIds).then(function (response) {
     	            if (response.success) {
     	            	$scope.projectUsers = response.data.gridData;
     	            }
     	            });
         });
     }
    /**
     * Get status 
     */
	 $scope.openCommentSideBar = function(selectedStatusId){
		 $scope.selectedStatusId = selectedStatusId;
		 
		 statusService.getStatusWithId(selectedStatusId).then(function (response) {
            if (response.success) {
                $scope.statusDate   =  response.data.gridData[0].date;
                $scope.statusList   =  response.data.gridData[0].statusList;
            }
        });
		 
		 $scope.getAllComments(selectedStatusId);
    }
	 /**
	  * Get all comments of status
	  */
	 $scope.getAllComments = function (selectedStatusId){
		 commentService.getComment(selectedStatusId).then(function (response) {
	            if (response.success) {
	            	$scope.commentCount = response.data.comment_count;
	                $scope.allcomments   =  response.data.gridData;
	            }
	        });
		 
	 }
	
	 /**
	  * Get all replies of a comment
	  */
	 $scope.loadReply = function(commentId){
		 commentService.getReplies(commentId).then(function (response) {
			 $scope.commentId = commentId;
            if (response.success) {
                $scope.allreplies   =  response.data.gridData;
            }
        });
    }
	
	/**
	 * Load status timeline by project
	 */
	 $scope.loadProjectMap = function(project){
		 
		 commentService.getProjectMap(project.projectId).then(function (response) {
            if (response.success) {
				$scope.projectUsers = response.data.gridData[0].userList;
				$scope.projectName = response.data.gridData[0].projectName
				$scope.updateData.projectList = [response.data.gridData[0].projectId];
				$scope.updateData.userList = [];

                    statusService.getAllTimelineStatus($scope.updateData).then(function (response) {
                     if (response.success) {
        
        			//	if(response.data.gridData.length > 0){
                      $scope.artists = [];
                      angular.forEach(response.data.gridData, function(value, key) {
                      $scope.artists.push(value);
                      });
                      
                      $scope.selected = project;
                      $scope.isProject = true;
                      $scope.isActive = false;
                      $scope.isUser = false;                     
                 }
                  });
            }
        });
    } 
	 
	 /**
	  * Load status timeline by user
	  */
	 $scope.loadUserMap = function(user){

		 		$scope.updateData.projectList = [];
				$scope.updateData.userList = [user.email];

                    statusService.getAllTimelineStatus($scope.updateData).then(function (response) {
                     if (response.success) {
        			
                      $scope.artists = [];
                      angular.forEach(response.data.gridData, function(value, key) {
                      $scope.artists.push(value);
                      });
                  	$scope.selected = user;
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

	$scope.showMobileMainHeader = true;
	$scope.openSideNavPanel = function() {
		$mdSidenav('left').open();
	};
	$scope.closeSideNavPanel = function() {
		$mdSidenav('left').close();
	};
	
	$scope.select= function(item) {
        $scope.selected = item; 
 };

 $scope.isActive = function(item) {
        return $scope.selected === item;
 };	
});