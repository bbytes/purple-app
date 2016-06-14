/**
 * 
 */
/* Interceptor declaration */
rootApp.factory('authInterceptor', function ($rootScope, $q, $sessionStorage, $location,$window) {
    return {
        request: function (config) {
            config.headers = config.headers || {};
            if ($window.sessionStorage.token) {
                config.headers['x-auth-token'] = $window.sessionStorage.token;
            }
            if (!$rootScope.authToken && $sessionStorage.userInfo) {
                var userInfo = $sessionStorage.userInfo;
                if (userInfo) {
                    $rootScope.authToken = userInfo.accessToken;
                    $rootScope.loggedInUser=userInfo.email;
                    $rootScope.userName=userInfo.name;
                    $rootScope.userRole= userInfo.userRoles;  
                    $rootScope.timePreference = userInfo.timePreference;   
                    $rootScope.switchState = userInfo.emailNotificationState;   
                    $rootScope.statusDate =  userInfo.statusDate; 
                    $rootScope.current_date = userInfo.displayDate;
                }
            }
            return config;          
        },
        response: function (response) {
            
            if(response.status === 200){                
                if(response.data && response.data.success === false){
                    if($rootScope.authFailureReasons.indexOf(response.data.reason) !== -1){
                        $location.path('/login');
                    }
                }
            }
            
            if (response.status === 401) {
                $location.path('/');
            }

            return response || $q.when(response);
        },
        'responseError': function (rejection) {
            return $q.reject(rejection);
        }
    };
});

rootApp.config(['$httpProvider', function ($httpProvider) {
        $httpProvider.interceptors.push('authInterceptor');
    }]);