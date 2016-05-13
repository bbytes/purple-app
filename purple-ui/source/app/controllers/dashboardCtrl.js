/**
 * Dashboard controller to load status timeline
 */
rootApp.controller('dashboardCtrl', function ($scope, $rootScope, $state, $mdSidenav, dropdownListService, projectService,appNotifyService,$window,$location,statusService, commentService) {
	
	 $scope.commentDesc = '';
    $scope.isActive = function(route) {
        return route === $location.path();
    }
   /**
    * Get all status timeline
    */
    
$scope.loadTimePeriod = function(){

	dropdownListService.getTimePeriod().then(function(response){
            $scope.timePeriod = response.data;
           	$scope.mytime = response.data[1].value;
          
              }, function(error){
        });
}

  $scope.loadStatusTimeline = function(time){

  	if(time == null || time == 'undefined')
  			time = "Weekly";
	  $scope.updateData = {
			  projectList :[],
			  userList : []
	  }

    	 statusService.getAllTimelineStatus($scope.updateData,time).then(function (response) {
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
     // method for pulling status by changing time period
     $scope.timeChange = function(timePeriod) {

		var itemSelected = $rootScope.itemChoose;
		if($scope.isActive)
			$scope.loadStatusTimeline(timePeriod);
		else if($scope.isProject)
		 	$scope.loadProjectMap(itemSelected,timePeriod);
		 else if($scope.isUser)
			 $scope.loadUserMap(itemSelected,timePeriod);
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
	 $scope.loadProjectMap = function(project, time){
		 
		 $rootScope.itemChoose = project;

				$scope.updateData.projectList = [project.projectId];
				$scope.updateData.userList = [];

                    statusService.getAllTimelineStatus($scope.updateData, time).then(function (response) {
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
	 
	 /**
	  * Load status timeline by user
	  */
	 $scope.loadUserMap = function(user, time){

	 			$rootScope.itemChoose = user;
		 		$scope.updateData.projectList = [];
				$scope.updateData.userList = [user.email];

                    statusService.getAllTimelineStatus($scope.updateData, time).then(function (response) {
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

  	//nav active
     $scope.setClickedRow = function(index){  //function that sets the value of selectedRow to current index
     $scope.selectedRow = index;
	 $scope.selectedUser = null;
  }
  
  $scope.setClickedUser = function(index){  //function that sets the value of selectedRow to current index
     $scope.selectedUser = index;
	 $scope.selectedRow = null;
  };
  
  
  
  
  function testTheiaStickySidebars() {
    var me = {};
    me.scrollTopStep = 1;
    me.currentScrollTop = 0;
    me.values = null;

    window.scrollTo(0, 1);
    window.scrollTo(0, 0);

    $(window).scroll(function(me) {
        return function(event) {
            var newValues = [];
            
            // Get sidebar offsets.
            $('.theiaStickySidebar').each(function() {
                newValues.push($(this).offset().top);
            });
            
            if (me.values != null) {
                var ok = true;
                
                for (var j = 0; j < newValues.length; j++) {
                    var diff = Math.abs(newValues[j] - me.values[j]);
                    if (diff > 1) {
                        ok = false;
                        
                        console.log('Offset difference for sidebar #' + (j + 1) + ' is ' + diff + 'px');
                        
                        // Highlight sidebar.
                        $($('.theiaStickySidebar')[j]).css('background', 'yellow');
                    }
                }
                
                if (ok == false) {                    
                    // Stop test.
                    $(this).unbind(event);
                    
                    alert('Bummer. Offset difference is bigger than 1px for some sidebars, which will be highlighted in yellow. Check the logs. Aborting.');
                    
                    return;
                }
            }
            
            me.values = newValues;
            
            // Scroll to bottom. We don't cache ($(document).height() - $(window).height()) since it may change (e.g. after images are loaded).
            if (me.currentScrollTop < ($(document).height() - $(window).height()) && me.scrollTopStep == 1) {
                me.currentScrollTop += me.scrollTopStep;
                window.scrollTo(0, me.currentScrollTop);
            }
            // Then back up.
            else if (me.currentScrollTop > 0) {
                me.scrollTopStep = -1;
                me.currentScrollTop += me.scrollTopStep;
                window.scrollTo(0, me.currentScrollTop);
            }
            // Then stop.
            else {                    
                $(this).unbind(event);
                
                alert("Great success!");
            }
        };
    }(me));
}


(function ($) {
    $.fn.theiaStickySidebar = function (options) {
        var defaults = {
            'containerSelector': '',
            'additionalMarginTop': 0,
            'additionalMarginBottom': 0,
            'updateSidebarHeight': true,
            'minWidth': 0,
            'disableOnResponsiveLayouts': true,
            'sidebarBehavior': 'modern'
        };
        options = $.extend(defaults, options);

        // Validate options
        options.additionalMarginTop = parseInt(options.additionalMarginTop) || 0;
        options.additionalMarginBottom = parseInt(options.additionalMarginBottom) || 0;

        tryInitOrHookIntoEvents(options, this);

        // Try doing init, otherwise hook into window.resize and document.scroll and try again then.
        function tryInitOrHookIntoEvents(options, $that) {
            var success = tryInit(options, $that);

            if (!success) {
                console.log('TST: Body width smaller than options.minWidth. Init is delayed.');

                $(document).scroll(function (options, $that) {
                    return function (evt) {
                        var success = tryInit(options, $that);

                        if (success) {
                            $(this).unbind(evt);
                        }
                    };
                }(options, $that));
                $(window).resize(function (options, $that) {
                    return function (evt) {
                        var success = tryInit(options, $that);

                        if (success) {
                            $(this).unbind(evt);
                        }
                    };
                }(options, $that))
            }
        }

        // Try doing init if proper conditions are met.
        function tryInit(options, $that) {
            if (options.initialized === true) {
                return true;
            }

            if ($('body').width() < options.minWidth) {
                return false;
            }

            init(options, $that);

            return true;
        }

        // Init the sticky sidebar(s).
        function init(options, $that) {
            options.initialized = true;

            // Add CSS
            $('head').append($('<style>.theiaStickySidebar:after {content: ""; display: table; clear: both;}</style>'));

            $that.each(function () {
                var o = {};

                o.sidebar = $(this);

                // Save options
                o.options = options || {};

                // Get container
                o.container = $(o.options.containerSelector);
                if (o.container.size() == 0) {
                    o.container = o.sidebar.parent();
                }

                // Create sticky sidebar
                o.sidebar.parents().css('-webkit-transform', 'none'); // Fix for WebKit bug - https://code.google.com/p/chromium/issues/detail?id=20574
                o.sidebar.css({
                    'position': 'relative',
                    'overflow': 'visible',
                    // The "box-sizing" must be set to "content-box" because we set a fixed height to this element when the sticky sidebar has a fixed position.
                    '-webkit-box-sizing': 'border-box',
                    '-moz-box-sizing': 'border-box',
                    'box-sizing': 'border-box'
                });

                // Get the sticky sidebar element. If none has been found, then create one.
                o.stickySidebar = o.sidebar.find('.theiaStickySidebar');
                if (o.stickySidebar.length == 0) {
                    o.sidebar.find('script').remove(); // Remove <script> tags, otherwise they will be run again on the next line.
                    o.stickySidebar = $('<div>').addClass('theiaStickySidebar').append(o.sidebar.children());
                    o.sidebar.append(o.stickySidebar);
                }

                // Get existing top and bottom margins and paddings
                o.marginTop = parseInt(o.sidebar.css('margin-top'));
                o.marginBottom = parseInt(o.sidebar.css('margin-bottom'));
                o.paddingTop = parseInt(o.sidebar.css('padding-top'));
                o.paddingBottom = parseInt(o.sidebar.css('padding-bottom'));

                // Add a temporary padding rule to check for collapsable margins.
                var collapsedTopHeight = o.stickySidebar.offset().top;
                var collapsedBottomHeight = o.stickySidebar.outerHeight();
                o.stickySidebar.css('padding-top', 1);
                o.stickySidebar.css('padding-bottom', 1);
                collapsedTopHeight -= o.stickySidebar.offset().top;
                collapsedBottomHeight = o.stickySidebar.outerHeight() - collapsedBottomHeight - collapsedTopHeight;
                if (collapsedTopHeight == 0) {
                    o.stickySidebar.css('padding-top', 0);
                    o.stickySidebarPaddingTop = 0;
                }
                else {
                    o.stickySidebarPaddingTop = 1;
                }

                if (collapsedBottomHeight == 0) {
                    o.stickySidebar.css('padding-bottom', 0);
                    o.stickySidebarPaddingBottom = 0;
                }
                else {
                    o.stickySidebarPaddingBottom = 1;
                }

                // We use this to know whether the user is scrolling up or down.
                o.previousScrollTop = null;

                // Scroll top (value) when the sidebar has fixed position.
                o.fixedScrollTop = 0;

                // Set sidebar to default values.
                resetSidebar();

                o.onScroll = function (o) {
                    // Stop if the sidebar isn't visible.
                    if (!o.stickySidebar.is(":visible")) {
                        return;
                    }

                    // Stop if the window is too small.
                    if ($('body').width() < o.options.minWidth) {
                        resetSidebar();
                        return;
                    }

                    // Stop if the sidebar width is larger than the container width (e.g. the theme is responsive and the sidebar is now below the content)
                    if (o.options.disableOnResponsiveLayouts) {
                        var sidebarWidth = o.sidebar.outerWidth(o.sidebar.css('float') == 'none');

                        if (sidebarWidth + 50 > o.container.width()) {
                            resetSidebar();
                            return;
                        }
                    }

                    var scrollTop = $(document).scrollTop();
                    var position = 'static';

                    // If the user has scrolled down enough for the sidebar to be clipped at the top, then we can consider changing its position.
                    if (scrollTop >= o.container.offset().top + (o.paddingTop + o.marginTop - o.options.additionalMarginTop)) {
                        // The top and bottom offsets, used in various calculations.
                        var offsetTop = o.paddingTop + o.marginTop + options.additionalMarginTop;
                        var offsetBottom = o.paddingBottom + o.marginBottom + options.additionalMarginBottom;

                        // All top and bottom positions are relative to the window, not to the parent elemnts.
                        var containerTop = o.container.offset().top;
                        var containerBottom = o.container.offset().top + getClearedHeight(o.container);

                        // The top and bottom offsets relative to the window screen top (zero) and bottom (window height).
                        var windowOffsetTop = 0 + options.additionalMarginTop;
                        var windowOffsetBottom;

                        var sidebarSmallerThanWindow = (o.stickySidebar.outerHeight() + offsetTop + offsetBottom) < $(window).height();
                        if (sidebarSmallerThanWindow) {
                            windowOffsetBottom = windowOffsetTop + o.stickySidebar.outerHeight();
                        }
                        else {
                            windowOffsetBottom = $(window).height() - o.marginBottom - o.paddingBottom - options.additionalMarginBottom;
                        }

                        var staticLimitTop = containerTop - scrollTop + o.paddingTop + o.marginTop;
                        var staticLimitBottom = containerBottom - scrollTop - o.paddingBottom - o.marginBottom;

                        var top = o.stickySidebar.offset().top - scrollTop;
                        var scrollTopDiff = o.previousScrollTop - scrollTop;

                        // If the sidebar position is fixed, then it won't move up or down by itself. So, we manually adjust the top coordinate.
                        if (o.stickySidebar.css('position') == 'fixed') {
                            if (o.options.sidebarBehavior == 'modern') {
                                top += scrollTopDiff;
                            }
                        }

                        if (o.options.sidebarBehavior == 'stick-to-top') {
                            top = options.additionalMarginTop;
                        }

                        if (o.options.sidebarBehavior == 'stick-to-bottom') {
                            top = windowOffsetBottom - o.stickySidebar.outerHeight();
                        }

                        if (scrollTopDiff > 0) { // If the user is scrolling up.
                            top = Math.min(top, windowOffsetTop);
                        }
                        else { // If the user is scrolling down.
                            top = Math.max(top, windowOffsetBottom - o.stickySidebar.outerHeight());
                        }

                        top = Math.max(top, staticLimitTop);

                        top = Math.min(top, staticLimitBottom - o.stickySidebar.outerHeight());

                        // If the sidebar is the same height as the container, we won't use fixed positioning.
                        var sidebarSameHeightAsContainer = o.container.height() == o.stickySidebar.outerHeight();

                        if (!sidebarSameHeightAsContainer && top == windowOffsetTop) {
                            position = 'fixed';
                        }
                        else if (!sidebarSameHeightAsContainer && top == windowOffsetBottom - o.stickySidebar.outerHeight()) {
                            position = 'fixed';
                        }
                        else if (scrollTop + top - o.sidebar.offset().top - o.paddingTop <= options.additionalMarginTop) {
                            // Stuck to the top of the page. No special behavior.
                            position = 'static';
                        }
                        else {
                            // Stuck to the bottom of the page.
                            position = 'absolute';
                        }
                    }

                    /*
                     * Performance notice: It's OK to set these CSS values at each resize/scroll, even if they don't change.
                     * It's way slower to first check if the values have changed.
                     */
                    if (position == 'fixed') {
                        o.stickySidebar.css({
                            'position': 'fixed',
                            'width': o.sidebar.width(),
                            'top': top,
                            'left': o.sidebar.offset().left + parseInt(o.sidebar.css('padding-left'))
                        });
                    }
                    else if (position == 'absolute') {
                        var css = {};

                        if (o.stickySidebar.css('position') != 'absolute') {
                            css.position = 'absolute';
                            css.top = scrollTop + top - o.sidebar.offset().top - o.stickySidebarPaddingTop - o.stickySidebarPaddingBottom;
                        }

                        css.width = o.sidebar.width();
                        css.left = '';

                        o.stickySidebar.css(css);
                    }
                    else if (position == 'static') {
                        resetSidebar();
                    }

                    if (position != 'static') {
                        if (o.options.updateSidebarHeight == true) {
                            o.sidebar.css({
                                'min-height': o.stickySidebar.outerHeight() + o.stickySidebar.offset().top - o.sidebar.offset().top + o.paddingBottom
                            });
                        }
                    }

                    o.previousScrollTop = scrollTop;
                };

                // Initialize the sidebar's position.
                o.onScroll(o);

                // Recalculate the sidebar's position on every scroll and resize.
                $(document).scroll(function (o) {
                    return function () {
                        o.onScroll(o);
                    };
                }(o));
                $(window).resize(function (o) {
                    return function () {
                        o.stickySidebar.css({'position': 'static'});
                        o.onScroll(o);
                    };
                }(o));

                // Reset the sidebar to its default state
                function resetSidebar() {
                    o.fixedScrollTop = 0;
                    o.sidebar.css({
                        'min-height': '1px'
                    });
                    o.stickySidebar.css({
                        'position': 'static',
                        'width': ''
                    });
                }

                // Get the height of a div as if its floated children were cleared. Note that this function fails if the floats are more than one level deep.
                function getClearedHeight(e) {
                    var height = e.height();

                    e.children().each(function () {
                        height = Math.max(height, $(this).height());
                    });

                    return height;
                }
            });
        }
    }
})(jQuery);

  
});