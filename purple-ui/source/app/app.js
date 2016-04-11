// Define Angular Module with dependencies 
var rootApp = angular.module('rootApp', [ 'ui.router', 'ui.bootstrap',
		'ngAnimate', 'templates-main', 'toaster', 'ngStorage', 'angular-md5',
		'angular-hmac-sha512','angularModalService','ngLetterAvatar','frapontillo.bootstrap-switch','ui.calendar','mgcrea.ngStrap','dm.stickyNav','pageslide-directive','ngMaterial','angular-confirm']);

// Defining global variables
rootApp.run([
		'$rootScope',
		'$state',
		'appAuthenticationService',
		function($rootScope, $state,appAuthenticationService) {

			$rootScope.bodyClass = '';
        $rootScope.baseUrl = 'http://localhost:9999/';
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
		
		
			   

			   // $httpProvider.defaults.headers.post['X-AUTH-TOKEN'] =  sessionStorage.token;
			
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
			
			}).state('home-page', {
				url : '/home-page',
				views : {
					'' : {
						templateUrl : 'app/partials/home.html'
						
					},
					'header@home-page' : {
						templateUrl : 'app/partials/home-header.html'
					},
					'main@home-page' : {
						templateUrl : 'app/partials/home-page.html',
						controller : 'homepageCtrl'
					},
					'footer@home-page' : {
						templateUrl : 'app/partials/home-footer.html'
					}
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
	                authorization: 'My Projects',
	                redirectTo: 'login'
	            }
			}).state('settings', {
				url : '/settings',
				views : {
					'' : {
						templateUrl : 'app/partials/home.html'
						
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
		                authorization: 'My Settings',
		                redirectTo: 'login'
		            }
			}).state('settings-user', {
				url : '/settings-user',
				views : {
					'' : {
						templateUrl : 'app/partials/home.html',
						
					},
					'header@settings-user' : {
						templateUrl : 'app/partials/home-header.html'
					},
					'main@settings-user' : {
						templateUrl : 'app/partials/settings-user.html',
						controller : 'userSettingsCtrl'
					},
					'footer@settings-user' : {
						templateUrl : 'app/partials/home-footer.html'
					}
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
			}).state('home.myalerts', {
				url : '/myalerts',
				templateUrl : 'app/partials/home-myalerts.html',
				controller : 'myAlertsCtrl'
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
				}
			}).state('activate', {
				url : '/activateAccount?token',
				templateUrl : 'app/partials/activateAccount.html',
				controller : 'activateAccountCtrl'
			}).state('useractivate', {
				url : '/activateUserAccount?token',
				templateUrl : 'app/partials/activateUserAccount.html',
				controller : 'activateUserAccountCtrl'
			}).state('accountactivate', {
				controller : 'activateAccountCtrl'
			});

			// $httpProvider.defaults.headers.common['Access-Control-Allow-Origin']
			// = '*';
			
			
		} ]);
		
		
		