/*
 * All User Modal Controller
 */
angular.module('rootApp').controller('allusersModalCtrl', function ($scope, options, $uibModalInstance, $uibModal) {

    $scope.selection = [];
    $scope.title = options.title;

    $scope.allusers = options.data;

    $scope.toggleSelection = function toggleSelection(id) {
        var idx = $scope.selection.indexOf(id);

        // is currently selected
        if (idx > -1) {
            $scope.selection.splice(idx, 1);
        }
        // is newly selected
        else {
            $scope.selection.push(id);
        }
    };
    $scope.ok = function () {
        $uibModalInstance.close($scope.selection);
    };

    $scope.cancel = function () {
        $uibModalInstance.dismiss('cancel');
    };
});