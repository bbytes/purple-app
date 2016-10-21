/* 
 * Interceptor declaration Service 
 */
angular.module('rootApp').factory('authInterceptor', function ($rootScope, $q, $sessionStorage, $injector, $location, appNotifyService) {
    return {
        request: function (config) {
            config.headers = config.headers || {};
            if ($rootScope.authToken) {
                config.headers['x-auth-token'] = $rootScope.authToken;
            }
            if (!$rootScope.authToken && $sessionStorage.userInfo) {
                var userInfo = $sessionStorage.userInfo;
                if (userInfo) {
                    $rootScope.authToken = userInfo.accessToken;
                    $rootScope.loggedInUser = userInfo.email;
                    $rootScope.userName = userInfo.name;
                    $rootScope.userRole = userInfo.userRoles;
                    $rootScope.timePreference = userInfo.timePreference;
                    $rootScope.switchState = userInfo.emailNotificationState;
                    $rootScope.timeZone = userInfo.timeZone;
                }
            }
            return config;
        },
        response: function (response) {

            // Error message for exceptions occured in API
            if (response.data.reason) {
                switch (response.data.reason) {
                    case "buld_uplaod_failed":
                        appNotifyService.error('Some of users already exist.');
                        break;
                    case "account_inactive":
                        appNotifyService.error('Please activaite your account before trying to reset password.');
                        break;
                    case "bad_gateway":
                        appNotifyService.error('You have entered invalid JIRA URL');
                        break;
                    case "authentication_failure":
                        appNotifyService.error('User is not authorized');
                        break;
                    case "password_mistach":
                        appNotifyService.error('Current password is incorrect');
                        break;
                    case "organization_not_unique":
                        appNotifyService.error('Looks like somebody has already signed-up from your Organization!');
                        break;
                    case "email_not_unique":
                        appNotifyService.error('Looks like this email is already registered with us.');
                        break;
                    case "invalid_email":
                        appNotifyService.error('Email domain is not supported');
                        break;
                    case "hours_exceeded":
                        appNotifyService.error('You have exceeded 24 hours in a day!');
                        break;
                    case "pass_duedate_status_edit":
                        appNotifyService.error('You are not allow to enter status pass due date');
                        break;
                    case "future_date_status_edit":
                        appNotifyService.error('Can not allow to enter status for future date');
                        break;
                    case "add_status_failed":
                        appNotifyService.error('Error while adding status.');
                        break;
                    case "update_status_failed":
                        appNotifyService.error('Error while updating status.');
                        break;
                    case "add_project_failed":
                        appNotifyService.error('Error while adding project.');
                        break;
                    case "update_project_failed":
                        appNotifyService.error('Error while updating project.');
                        break;
                    case "status_not_found":
                        appNotifyService.error(response.data.data);
                        break;
                    case "sign_up_failed":
                        appNotifyService.error(response.data.data);
                        break;
                    case "project_owner_delete_failed":
                        appNotifyService.error('The user that you are trying to delete is an owner of other project(s). Please change the owner and then try delete again.');
                        break;
                    case "deletion_not_allowed":
                        appNotifyService.error('You cannot delete the ONLY Admin user. It is mandatory to have atleast one Admin for the application.');
                        break;
                }
            }

            if (response.status === 200) {
                if (response.data && response.data.success === false) {
                    if ($rootScope.authFailureReasons.indexOf(response.data.reason) !== -1) {
                        appNotifyService.error(response.data.data);
                    }
                }
            }

            return response || $q.when(response);
        },
        'responseError': function (errorResponse) {
            var stateService = $injector.get('$state');
            errorResponse.status = (errorResponse.status <= 0) ? 500 : errorResponse.status;
            switch (errorResponse.status) {
                case 401:
                case 403: //Below code for checking external url access denied for applciation urls
                    if (errorResponse.data.reason === 'normal_user_url_access_denied')
                        $location.path('/accessDenied');
                    else
                        stateService.go('login');
                    break;
                case 404:
                    $location.path('/404');
                    break;
                case 500:
                    break;
            }
            return $q.reject(errorResponse);

        }
    };
});

angular.module('rootApp').config(['$httpProvider', function ($httpProvider) {
        $httpProvider.interceptors.push('authInterceptor');
    }]);