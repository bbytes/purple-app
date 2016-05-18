// Define Angular Module with dependencies 
var rootApp = angular.module('rootApp', [ 'ui.router', 'ui.bootstrap',
		'ngAnimate', 'templates-main', 'toaster', 'ngStorage' ,'angular-md5',
		'angular-hmac-sha512','angularModalService','ngLetterAvatar','frapontillo.bootstrap-switch','ui.calendar','mgcrea.ngStrap','dm.stickyNav','ngMaterial','angular-confirm','textAngular','angular-timezone-select','server-url','angularInlineEdit','720kb.tooltips','angular-notification-icons', 'xeditable']);
 
 // avoid the spacing while copy paste in text angular 
  rootApp.filter('htmlToPlaintext', function() {
    return function(text) {
      return  text ? String(text).replace(/<[^>]+>/gm, '') : '';
    };
  }
);
// Defining global variables
rootApp.run([
		'$rootScope',
		'$state',
		'appAuthenticationService','BASE_URL',
		function($rootScope, $state,appAuthenticationService,BASE_URL) {

			$rootScope.bodyClass = '';
        	$rootScope.baseUrl = BASE_URL;
			$rootScope.apiUrl = 'api/v1';
			$rootScope.loggedStatus = false;
			$rootScope.authToken = '';
			$rootScope.currentState = '';
			$rootScope.authrization = '';

			  $rootScope.$on('$stateChangeSuccess', function (ev, to, toParams, from, fromParams) {

		            if (to.data && to.data.authorization !== '') {
		                if (!appAuthenticationService.isAuthenticated()) {
		                    $state.go(to.data.redirectTo);
		                }
		            }

		            $rootScope.currentState = to.name;
		            $rootScope.previouState = from.name;

		            console.log('Previous state:' + from.name);
		            console.log('Current state:' + to.name);
		        });
			  
			  $rootScope.$on('$stateChangeStart', function (evt, to, params,from) {
		            $rootScope.$state = $state;
		            $rootScope.previouState = from.name;
		            $rootScope.subMenu = '';
		            if (to.redirectTo) {
		                evt.preventDefault();
		                $state.go(to.redirectTo, params);
		            }
		        });

		} ]);

// Angular ui-router route definitions
rootApp.config([
		'$stateProvider',
		'$urlRouterProvider',
		'$locationProvider',
		'$httpProvider',
	
		function($stateProvider, $urlRouterProvider, $locationProvider,
				$httpProvider) {
		
			// Remove # tag from URL
			$locationProvider.html5Mode(true);

			$urlRouterProvider.otherwise('/home');

			$stateProvider.state('login', {
				url : '/login',
				controller : 'loginCtrl',
				templateUrl : 'app/partials/login.html'

			}).state('home', {

				url : '/home',
				views : {
					'' : {
						templateUrl : 'app/partials/home.html'
					},
					
					'main@home' : {
						templateUrl : 'app/partials/home-main.html'
					},
					'footer@home' : {
						templateUrl : 'app/partials/home-footer.html'
					}
				}
			}).state('signup', {
				url : '/signup',
				views : {
					'' : {
						templateUrl : 'app/partials/signup.html',
						controller : 'signupCtrl',
					}

				}
				}).state('forgot-password', {
				url : '/forgot-password',
				views : {
					'' : {
						templateUrl : 'app/partials/forgot-password.html',
						controller : 'forgotPasswordCtrl',
					}
				}
			
			}).state('dashboard', {
				url : '/dashboard',
				views : {
					'' : {
						templateUrl : 'app/partials/home.html'
						
					},
					'header@dashboard' : {
						templateUrl : 'app/partials/home-header.html'
					},
					'main@dashboard' : {
						templateUrl : 'app/partials/dashboard.html',
						controller : 'dashboardCtrl'
					},
					'footer@dashboard' : {
						templateUrl : 'app/partials/home-footer.html'
					}
				},
				data: {
		                authorization: 'dashboard',
		                redirectTo: 'login'
		            }
			}).state('status', {
				url : '/status',
				views : {
					'' : {
						templateUrl : 'app/partials/home.html'
					   
						
					},
					'header@status' : {
						templateUrl : 'app/partials/home-header.html'
					},
					'main@status' : {
						templateUrl : 'app/partials/status.html',
						   controller: 'statusCtrl'
						 
					},
					'footer@status' : {
						templateUrl : 'app/partials/home-footer.html'
					}
				},
				 data: {
		                authorization: 'My Status',
		                redirectTo: 'login'
		            }
			}).state('projects', {
				url : '/projects',
				views : {
					'' : {
						templateUrl : 'app/partials/home.html'
						
					},
					'header@projects' : {
						templateUrl : 'app/partials/home-header.html'
					},
					'main@projects' : {
						templateUrl : 'app/partials/projects.html',
						controller : 'projectCtrl'
					},
					'footer@projects' : {
						templateUrl : 'app/partials/home-footer.html'
					}
				},
				data: {
	                authorization: 'projects',
	                redirectTo: 'login'
	            }
			}).state('admin-settings', {
				url : '/admin-settings',
				views : {
					'' : {
						templateUrl : 'app/partials/home.html'
						
					},
					'header@admin-settings' : {
						templateUrl : 'app/partials/home-header.html'
					},
					'main@admin-settings' : {
						templateUrl : 'app/partials/admin-settings.html',
						controller : 'adminSettingsCtrl'
					},
					'footer@admin-settings' : {
						templateUrl : 'app/partials/home-footer.html'
					}
				},
				 data: {
		                authorization: 'settings',
		                redirectTo: 'login'
		            }
			}).state('settings', {
				url : '/settings',
				views : {
					'' : {
						templateUrl : 'app/partials/home.html',
						
					},
					'header@settings' : {
						templateUrl : 'app/partials/home-header.html'
					},
					'main@settings' : {
						templateUrl : 'app/partials/settings.html',
						controller : 'settingsCtrl'
					},
					'footer@settings' : {
						templateUrl : 'app/partials/home-footer.html'
					}
				},
				 data: {
		                authorization: 'user settings',
		                redirectTo: 'login'
		            }
			}).state('user-mgr', {
				url : '/user-mgr',
				views : {
					'' : {
						templateUrl : 'app/partials/home.html',
					
					},
					'header@user-mgr' : {
						templateUrl : 'app/partials/home-header.html'
					},
					'main@user-mgr' : {
						templateUrl : 'app/partials/user-mgr.html',
						controller : 'adminCtrl'
					},
					'footer@user-mgr' : {
						templateUrl : 'app/partials/home-footer.html'
					}
				},
				 data: {
		                authorization: 'User Manager',
		                redirectTo: 'login'
		            }
			}).state('admin', {
				url : '/admin',
				controller : 'adminCtrl',
				redirectTo : 'admin.users',
				views : {
					'' : {
						templateUrl : 'app/partials/home.html'
					},
					'header@admin' : {
						templateUrl : 'app/partials/home-header.html'
					},
					'main@admin' : {
						templateUrl : 'app/partials/admin-main.html'
					},
					'footer@admin' : {
						templateUrl : 'app/partials/home-footer.html'
					}
				},
				 data: {
		                authorization: 'admin',
		                redirectTo: 'login'
		            }
			}).state('activate', {
				url : '/activateAccount?token',
				templateUrl : 'app/partials/activateAccount.html',
				controller : 'activateAccountCtrl'
			}).state('forgotpassword', {
				url : '/forgotPassword?token',
				templateUrl : 'app/partials/reset-password.html',
				controller : 'resetPasswordCtrl'
			}).state('updatestatus', {
				url : '/updatestatus?token&sd',
				controller : 'updateStatusCtrl'
			}).state('accountactivate', {
				controller : 'activateAccountCtrl'
			});		
} ]);
		
		
		