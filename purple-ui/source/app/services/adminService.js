/**
 * 
 */

rootApp.service('adminService', function ($rootScope, $http, $q,$window) {

    return {
    	inviteUser: function (admin) {

            var deferred = $q.defer();
         
            $http({
                method: 'POST',
                url:$rootScope.baseUrl+'api/v1/admin/user/add',
                data:admin,
                headers: {
                    'Content-Type': 'application/json',
                    
                }
              

                
            }).success(function (response, status, headers, config) {

              

                deferred.resolve(response);
            }).error(function () {
                // Something went wrong.
                deferred.reject({'success': false, 'msg': 'Oops! Something went wrong. Please try again later.'});
            });

            return deferred.promise;

        }
    };
});