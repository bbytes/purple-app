// Define Angular Module with dependencies 
var rootApp = angular.module('rootApp',
        [
            'ui.router',
            'ui.bootstrap',
            'ngAnimate',
            'templates-main',
            'toaster'
        ]);

// Defining global variables 
rootApp.run(['$rootScope', '$state', function ($rootScope, $state) {

        $rootScope.baseUrl = '';
        $rootScope.apiUrl = '';

        $rootScope.currentState = '';

        $rootScope.$on('$stateChangeSuccess', function (ev, to, toParams, from, fromParams) {
            $rootScope.currentState = to.name;
            console.log('Previous state:' + from.name);
            console.log('Current state:' + to.name);
        });

    }]);

// Angular ui-router route definitions 
rootApp.config(['$stateProvider', '$urlRouterProvider', '$locationProvider', function ($stateProvider, $urlRouterProvider, $locationProvider) {

        //Remove # tag from URL
        $locationProvider.html5Mode(true);

        $urlRouterProvider.otherwise('/home');

        $stateProvider.state('login', {
            url: '/login',
            controller: 'loginCtrl',
            templateUrl: 'app/partials/login.html'

        }).state('home', {

            url: '/home',
            views: {
                '': {
                    templateUrl: 'app/partials/home.html'
                },
                'header@home': {
                    templateUrl: 'app/partials/home-header.html'
                },
                'main@home': {
                    templateUrl: 'app/partials/home-main.html'
                },
                'footer@home': {
                    templateUrl: 'app/partials/home-footer.html'
                }
            }
        }).state('signup', {
            url: '/signup',
            views: {
                '': {
                    templateUrl: 'app/partials/signup.html',
                     controller: 'signupCtrl',
                }
              
            }
        }).state('home-page', {
            url: '/home-page',
            views: {
                '': {
                    templateUrl: 'app/partials/home-page.html',
                     controller: 'home-pageCtrl',
                 },
                'header@home': {
                    templateUrl: 'app/partials/home-header.html'
                },
                'main@home': {
                    templateUrl: 'app/partials/home-main.html'
                },
                'footer@home': {
                    templateUrl: 'app/partials/home-footer.html'
                }
            }
        }).state('status', {
            url: '/status',
            views: {
                '': {
                    templateUrl: 'app/partials/status.html',
                     controller: 'statusCtrl',
                 },
                'header@home': {
                    templateUrl: 'app/partials/home-header.html'
                },
                'main@home': {
                    templateUrl: 'app/partials/home-main.html'
                },
                'footer@home': {
                    templateUrl: 'app/partials/home-footer.html'
                }
            }
        }).state('projects', {
            url: '/projects',
            views: {
                '': {
                    templateUrl: 'app/partials/projects.html',
                     controller: 'projectsCtrl',
                 },
                'header@home': {
                    templateUrl: 'app/partials/home-header.html'
                },
                'main@home': {
                    templateUrl: 'app/partials/home-main.html'
                },
                'footer@home': {
                    templateUrl: 'app/partials/home-footer.html'
                }
            }
        }).state('settings', {
            url: '/settings',
            views: {
                '': {
                    templateUrl: 'app/partials/settings.html',
                     controller: 'settingsCtrl',
                 },
                'header@home': {
                    templateUrl: 'app/partials/home-header.html'
                },
                'main@home': {
                    templateUrl: 'app/partials/home-main.html'
                },
                'footer@home': {
                    templateUrl: 'app/partials/home-footer.html'
                }
            }
        }).state('settings-user', {
            url: '/settings-user',
            views: {
                '': {
                    templateUrl: 'app/partials/settings-user.html',
                     controller: 'settings-userCtrl',
                 },
                'header@home': {
                    templateUrl: 'app/partials/home-header.html'
                },
                'main@home': {
                    templateUrl: 'app/partials/home-main.html'
                },
                'footer@home': {
                    templateUrl: 'app/partials/home-footer.html'
                }
            }
        }).state('user-mgr', {
            url: '/user-mgr',
            views: {
                '': {
                    templateUrl: 'app/partials/user-mgr.html',
                     controller: 'user-mgrCtrl',
                 },
                'header@home': {
                    templateUrl: 'app/partials/home-header.html'
                },
                'main@home': {
                    templateUrl: 'app/partials/home-main.html'
                },
                'footer@home': {
                    templateUrl: 'app/partials/home-footer.html'
                }
            }
        })

        .state('home.myalerts', {
            url: '/myalerts',
            templateUrl: 'app/partials/home-myalerts.html',
            controller: 'myAlertsCtrl'
        }).state('admin', {
            url: '/admin',
            controller: 'adminCtrl',
            redirectTo: 'admin.users',
            views: {
                '': {
                    templateUrl: 'app/partials/home.html'
                },
                'header@admin': {
                    templateUrl: 'app/partials/home-header.html'
                },
                'main@admin': {
                    templateUrl: 'app/partials/admin-main.html'
                },
                'footer@admin': {
                    templateUrl: 'app/partials/home-footer.html'
                }
            }
        }).state('admin.users', {
            url: '/users',
            templateUrl: 'app/partials/admin-users.html',
            controller: 'adminUsersCtrl'
        });
}]);