/*
 * Assign Project to User Modal Controller
 */
angular.module('rootApp').controller('assignProjectModalCtrl', function ($scope, modalData, appNotifyService, projectService, $uibModalInstance, $uibModal) {

    $scope.title = modalData.title;
    $scope.projectId = modalData.projectId;
    $scope.allusers = modalData.userData;

    $scope.assignProjectOwner = function () {

        $scope.ownerId = $scope.radioValue;
        if ($scope.ownerId !== undefined && $scope.ownerId !== null) {
            projectService.changeProjectOwner($scope.projectId, $scope.ownerId).then(function (response) {
                if (response.success) {
                    $scope.project = response.data;
                    $uibModalInstance.close($scope.project);
                }
            });
        }
    };

    $scope.cancel = function () {
        $uibModalInstance.dismiss('cancel');
    };
});