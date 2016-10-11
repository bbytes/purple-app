/*
 * Assign Project to User Modal Controller
 */
angular.module('rootApp').controller('assignProjectModalCtrl', function ($scope, options, $uibModalInstance, $uibModal) {

    $scope.title = options.title;

    $scope.allusers = options.data;
    
    $scope.assignProjectToUser = function () {
        
        $uibModalInstance.close($scope.selection);
    };

    $scope.cancel = function () {
        $uibModalInstance.dismiss('cancel');
    };
});