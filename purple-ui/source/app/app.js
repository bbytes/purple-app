// Define Angular Module with dependencies 
var rootApp = angular.module('rootApp',
        [
            'ui.router',
            'ui.bootstrap',
            'ngAnimate',
            'templates-main',
            'toaster',
            'ngStorage',
            'angular-md5',
            'angular-hmac-sha512',
            'angularModalService',
            'ngLetterAvatar',
            'ui.calendar',
            'mgcrea.ngStrap',
            'dm.stickyNav',
            'ngMaterial',
            'angular-confirm',
            'textAngular',
            'server-url',
            'angularInlineEdit',
            '720kb.tooltips',
            'angular-notification-icons',
            'xeditable',
            'chart.js',
            'pageslide-directive',
            'chieffancypants.loadingBar',
            'toggle-switch',
            'duScroll',
            'vesparny.fancyModal',
            'duScroll',
            'angular-timezone-selector',
            'vAccordion',
            'duScroll',
            'angular.chosen',
            'ui.bootstrap.datetimepicker',
            'angularUtils.directives.dirPagination',
            'mentio'
        ]);

// avoid the spacing while copy paste in text angular 
rootApp.filter('htmlToPlaintext', function () {
    return function (text) {
        return  text ? String(text).replace(/<[^>]+>/gm, '') : '';
    };
}
);
// Defining global variables
rootApp.run([
    '$rootScope',
    '$state',
    'appAuthenticationService', 'BASE_URL',
    function ($rootScope, $state, appAuthenticationService, BASE_URL) {

        $rootScope.bodyClass = '';
        $rootScope.baseUrl = 'http://localhost:9999/';
        $rootScope.apiUrl = 'api/v1';
        $rootScope.loggedStatus = false;
        $rootScope.authToken = '';
        $rootScope.currentState = '';
        $rootScope.authrization = '';

        $rootScope.authFailureReasons = [
            'auth-token-expired',
            'login-user-id-missing',
            'auth-signature-missing',
            'invalid-auth-signature',
            'user_not_found',
            'bad_credentials',
            'login-failed'
        ];

        $rootScope.$on('$stateChangeSuccess', function (ev, to, toParams, from, fromParams) {

            if (to.data && to.data.authorization !== '') {
                if (!appAuthenticationService.isAuthenticated()) {
                    $state.go(to.data.redirectTo);
                }
            }
            $rootScope.currentState = to.name;
            $rootScope.previouState = from.name;
        });

        $rootScope.$on('$stateChangeStart', function (evt, to, params, from) {
            $rootScope.$state = $state;
            $rootScope.previouState = from.name;
            $rootScope.subMenu = '';
            if (to.redirectTo) {
                evt.preventDefault();
                $state.go(to.redirectTo, params);
            }
        });

    }]);

// Angular ui-router route definitions
rootApp.config([
    '$stateProvider',
    '$urlRouterProvider',
    '$locationProvider',
    '$httpProvider',
    function ($stateProvider, $urlRouterProvider, $locationProvider,
            $httpProvider) {

        // Remove # tag from URL
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
                'main@home': {
                    templateUrl: 'app/partials/home-main.html',
                    controller: 'indexCtrl',
                },
                'footer@home': {
                    templateUrl: 'app/partials/home-footer.html'
                }
            }
        }).state('terms-of-service', {
            url: '/terms-of-service',
            views: {
                '': {
                    templateUrl: 'app/partials/terms-of-service.html',
                    controller: 'termsofserviceCtrl'
                }
            }

        }).state('privacy-policy', {
            url: '/privacy-policy',
            views: {
                '': {
                    templateUrl: 'app/partials/privacy-policy.html',
                    controller: 'privacypolicyCtrl'
                }
            }

        }).state('refund-policy', {
            url: '/refund-policy',
            views: {
                '': {
                    templateUrl: 'app/partials/refund-policy.html',
                    controller: 'refundpolicyCtrl'
                }
            }

        }).state('signup', {
            url: '/signup',
            views: {
                '': {
                    templateUrl: 'app/partials/signup.html',
                    controller: 'signupCtrl'
                }

            }
        }).state('forgot-password', {
            url: '/forgot-password',
            views: {
                '': {
                    templateUrl: 'app/partials/forgot-password.html',
                    controller: 'forgotPasswordCtrl'
                }
            }

        }).state('dashboard', {
            url: '/dashboard',
            views: {
                '': {
                    templateUrl: 'app/partials/home.html'

                },
                'header@dashboard': {
                    templateUrl: 'app/partials/home-header.html'
                },
                'main@dashboard': {
                    templateUrl: 'app/partials/dashboard.html',
                    controller: 'dashboardCtrl'
                },
                'footer@dashboard': {
                    templateUrl: 'app/partials/home-footer.html'
                }
            },
            data: {
                authorization: 'dashboard',
                redirectTo: 'login'
            }
        }).state('metrics', {
            url: '/metrics',
            views: {
                '': {
                    templateUrl: 'app/partials/home.html'

                },
                'header@metrics': {
                    templateUrl: 'app/partials/home-header.html'
                },
                'main@metrics': {
                    templateUrl: 'app/partials/metrics.html',
                    controller: 'metricsCtrl'
                },
                'footer@metrics': {
                    templateUrl: 'app/partials/home-footer.html'
                }
            },
            data: {
                authorization: 'metrics',
                redirectTo: 'login'
            }
        }).state('integration', {
            url: '/integration?app',
            views: {
                '': {
                    templateUrl: 'app/partials/home.html'

                },
                'header@integration': {
                    templateUrl: 'app/partials/home-header.html'
                },
                'main@integration': {
                    templateUrl: 'app/partials/integration.html',
                    controller: 'integrationCtrl'
                },
                'footer@integration': {
                    templateUrl: 'app/partials/home-footer.html'
                }
            },
            data: {
                authorization: 'integration',
                redirectTo: 'login'
            }
        }).state('status', {
            url: '/status',
            views: {
                '': {
                    templateUrl: 'app/partials/home.html'


                },
                'header@status': {
                    templateUrl: 'app/partials/home-header.html'
                },
                'main@status': {
                    templateUrl: 'app/partials/status.html',
                    controller: 'statusCtrl'

                },
                'footer@status': {
                    templateUrl: 'app/partials/home-footer.html'
                }
            },
            data: {
                authorization: 'My Status',
                redirectTo: 'login'
            }
        }).state('tasks', {
            url: '/tasks',
            views: {
                '': {
                    templateUrl: 'app/partials/home.html'


                },
                'header@tasks': {
                    templateUrl: 'app/partials/home-header.html'
                },
                'main@tasks': {
                    templateUrl: 'app/partials/tasks.html',
                    controller: 'tasksCtrl'

                },
                'footer@tasks': {
                    templateUrl: 'app/partials/home-footer.html'
                }
            },
            data: {
                authorization: 'tasks',
                redirectTo: 'login'
            }
        }).state('projects', {
            url: '/projects',
            views: {
                '': {
                    templateUrl: 'app/partials/home.html'

                },
                'header@projects': {
                    templateUrl: 'app/partials/home-header.html'
                },
                'main@projects': {
                    templateUrl: 'app/partials/projects.html',
                    controller: 'projectCtrl'
                },
                'footer@projects': {
                    templateUrl: 'app/partials/home-footer.html'
                }
            },
            data: {
                authorization: 'projects',
                redirectTo: 'login'
            }
        }).state('admin-settings', {
            url: '/admin-settings',
            views: {
                '': {
                    templateUrl: 'app/partials/home.html'

                },
                'header@admin-settings': {
                    templateUrl: 'app/partials/home-header.html'
                },
                'main@admin-settings': {
                    templateUrl: 'app/partials/admin-settings.html',
                    controller: 'adminSettingsCtrl'
                },
                'footer@admin-settings': {
                    templateUrl: 'app/partials/home-footer.html'
                }
            },
            data: {
                authorization: 'settings',
                redirectTo: 'login'
            }
        }).state('settings', {
            url: '/settings',
            views: {
                '': {
                    templateUrl: 'app/partials/home.html'
                },
                'header@settings': {
                    templateUrl: 'app/partials/home-header.html'
                },
                'main@settings': {
                    templateUrl: 'app/partials/settings.html',
                    controller: 'settingsCtrl'
                },
                'footer@settings': {
                    templateUrl: 'app/partials/home-footer.html'
                }
            },
            data: {
                authorization: 'user settings',
                redirectTo: 'login'
            }
        }).state('user-manager', {
            url: '/user-manager',
            views: {
                '': {
                    templateUrl: 'app/partials/home.html'
                },
                'header@user-manager': {
                    templateUrl: 'app/partials/home-header.html'
                },
                'main@user-manager': {
                    templateUrl: 'app/partials/user-manager.html',
                    controller: 'userCtrl'
                },
                'footer@user-manager': {
                    templateUrl: 'app/partials/home-footer.html'
                }
            },
            data: {
                authorization: 'User Manager',
                redirectTo: 'login'
            }
        }).state('billing', {
            url: '/billing',
            views: {
                '': {
                    templateUrl: 'app/partials/home.html'
                },
                'header@billing': {
                    templateUrl: 'app/partials/home-header.html'
                },
                'main@billing': {
                    templateUrl: 'app/partials/billing-nav.html',
                    controller: 'billingInfoCtrl'
                },
                'footer@billing': {
                    templateUrl: 'app/partials/home-footer.html'
                }
            },
            data: {
                authorization: 'User Manager',
                redirectTo: 'login'
            }
        }).state('access-denied', {
            url: '/accessDenied',
            templateUrl: 'app/partials/access-denied.html'
        }).state('activate', {
            url: '/activateAccount?token',
            controller: 'activateAccountCtrl'
        }).state('forgotpassword', {
            url: '/forgotPassword?token',
            templateUrl: 'app/partials/reset-password.html',
            controller: 'resetPasswordCtrl'
        }).state('updatestatus', {
            url: '/updatestatus?token&sd',
            controller: 'updateStatusCtrl'
        }).state('updatesetting', {
            url: '/updateSetting?token',
            controller: 'updateSettingCtrl'
        }).state('status-snippet', {
            url: '/status-snippet?pk&sid',
            templateUrl: 'app/partials/status-snippet.html',
            controller: 'statusSnippetCtrl'
        }).state('comment-snippet', {
            url: '/comment-snippet?pk&cid',
            templateUrl: 'app/partials/comment-snippet.html',
            controller: 'commentSnippetCtrl'
        }).state('reply-snippet', {
            url: '/reply-snippet?pk&cid&rid',
            templateUrl: 'app/partials/reply-snippet.html',
            controller: 'replySnippetCtrl'
        }).state('accountactivate', {
            controller: 'activateAccountCtrl'
        });
    }]);
		