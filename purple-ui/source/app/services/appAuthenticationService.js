/**
 * Service to authenticate
 */
rootApp.service('appAuthenticationService', function ($rootScope, $window) {

    this.isAuthenticated = function () {

        if ($window.sessionStorage.token == null) {
            return false;
        }

        // Authentication success.
        return true;
    };

});