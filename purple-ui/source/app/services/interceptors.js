/**
 * 
 */
/* Interceptor declaration */
rootApp.factory('authInterceptor', function ($rootScope, $q, $sessionStorage, $location,$window) {
    return {
        request: function (config) {
        	//config.headers['Content-Type'] = 'application/json';
            config.headers = config.headers || {};
            if ($window.sessionStorage.token) {
                //config.headers.Authorization = $window.sessionStorage.token;
                console.log(config.headers.Authorization);
                config.headers['x-auth-token'] = $window.sessionStorage.token;
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
      // $httpProvider.interceptors.push('headerInterceptor');
        $httpProvider.interceptors.push('authInterceptor');
    }]);
