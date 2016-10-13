/*
 * User Controller
 */
angular.module('rootApp').controller('userCtrl', function ($scope, $rootScope, userService, dropdownListService, appNotifyService) {

    $rootScope.navClass = 'nav navbar-nav';
    $rootScope.navstatusClass = 'right-nav-ct';
    $rootScope.bodyClass = 'body-standalone1';
    // variable to store information about all list of user
    $scope.allusers;
    // this varibale is used to pass no of days the user data need to deleted
    $scope.days = 15;

    $scope.invite = function (isValid) {

        if (!isValid) {
            appNotifyService.error('Please enter a valid email and username.');
            return false;
        }
        if ($scope.admin === null) {
            appNotifyService.error('Please enter a valid email and username.');
        } else {
            userService.inviteUser($scope.admin).then(function (response) {

                if (response.success) {
                    appNotifyService.success('Activation link has been sent to your registered email.');
                    $scope.user = response.data;
                    $scope.allusers.unshift($scope.user);
                    $scope.admin = '';
                    $scope.isSubmitted = true;
                }
            }, function (error) {
                appNotifyService.error('Error while adding users. Please check back again!');
            });
        }
    };

    // Used to re invite user who yet to joined
    $scope.resend = function (name, email) {

        userService.reInviteUser(name, email).then(function (response) {
            if (response.success) {
                appNotifyService.success('You have been successfully invited to user : ' + name);
            }
        });
    };

    $scope.loadUsers = function () {

        dropdownListService.getRole().then(function (response) {
            $scope.userRoles = response.data;
        });
        userService.getAllusers().then(function (response) {
            if (response.success) {
                if (response) {
                    $scope.userscount = response.data.length;
                }
                $scope.joinedCount = response.data.joined_count;
                $scope.pendingCount = response.data.pending_count;
                $scope.allusers = response.data.gridData;
            }
        });
    };

    $scope.initUser = function () {
        $scope.loadUsers();
    };

    // method is used to disable user 
    $scope.disableUser = function (userId, index, state) {
        // calling user service for disabling user with state as "true/false"
        // e.g. state=true means user is disable
        userService.disableUser(userId, state).then(function (response) {
            if (response.success) {
                $scope.updatedUser = response.data;
                $scope.allusers.splice(index, 1);
                $scope.allusers.unshift($scope.updatedUser);
                if ($scope.updatedUser.disableState)
                    appNotifyService.success('User has been sucessfully disabled.');
                else
                    appNotifyService.success('User has been sucessfully enabled.');
            }
        });
    };

    // method is used to set user as mark for delete
    //(user and their statuses,comments will be deleted after 30 Days, it been taking care by server side)
    $scope.markForDelete = function (userId, index, state) {
        // calling user service for marking delete user with state as "true/false"
        // e.g. state=true means user is set as mark for delete
        // $scope.days initialise globally above, the mentioned no of days will be taken care to delete user data after $scope.days
        userService.markForDelete(userId, state, $scope.days).then(function (response) {
            if (response.success) {
                $scope.updatedUser = response.data;
                $scope.allusers.splice(index, 1);
                $scope.allusers.unshift($scope.updatedUser);
                if ($scope.updatedUser.markDeleteState)
                    appNotifyService.success('User has been sucessfully mark for delete');
                else
                    appNotifyService.success('All user information has been restored.');
            }
        });
    };

    $scope.deleteUser = function (email, index) {
        userService.deleteUser(email).then(function (response) {
            if (response.success) {
                $scope.allusers.splice(index, 1);
                appNotifyService.success('User has been sucessfully deleted.');
            }
        });
    };

    // Method is used to assigning user role
    $scope.roleChange = function (userRole, user) {

        var userId = user.id;
        userService.updateUserRole(userId, userRole).then(function (response) {
            if (response.success) {
                appNotifyService.success('Users role has been sucessfully changed.');
            }
        }, function (error) {
            appNotifyService.error('Error while assigning users role. Please check back again!');
        });
    };

    // Method for bulk upload 
    $scope.bulkupload = function (element) {
        var file = element.files[0];
        var fd = new FormData();
        fd.append('file', file);
        userService.bulkupload(fd).then(function (response) {
            var len = response.data.length;
            if (response.success) {

                if (len > 0) {
                    appNotifyService.success('Activation link has been sent to ' + len + ' users.');
                } else {
                    appNotifyService.success('All users are already exist. Please check!');
                }
                $scope.loadUsers();
            }

        }, function (error) {
            appNotifyService.error('Invalid file format');
        });
    };

});
