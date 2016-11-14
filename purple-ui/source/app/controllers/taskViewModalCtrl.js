/*
 * Task View Modal Controller
 */
angular.module('rootApp').controller('taskViewModalCtrl', function ($scope, modalData, $uibModalInstance, $uibModal) {

    $scope.title = modalData.title;
    $scope.taskList = modalData.taskData;

    $scope.addToWorkedOnList = [];
    $scope.addToWorkingOnList = [];
    $scope.addToBlockersList = [];

    $scope.cancel = function () {
        $uibModalInstance.dismiss('cancel');
    };

    $scope.saveTaskView = function () {
        $scope.addToWorkedOn = "";
        $scope.addToWorkingOn = "";
        $scope.addToBlockers = "";
        angular.forEach($scope.addToWorkedOnList, function (item) {
            $scope.addToWorkedOn = $scope.addToWorkedOn + " #{" + item + "}";
        });
        angular.forEach($scope.addToWorkingOnList, function (item) {
            $scope.addToWorkingOn = $scope.addToWorkingOn + " #{" + item + "}";
        });
        angular.forEach($scope.addToBlockersList, function (item) {
            $scope.addToBlockers = $scope.addToBlockers + " #{" + item + "}";
        });
        $scope.taskObject = {
            "addToWorkedOn": $scope.addToWorkedOn,
            "addToWorkingOn": $scope.addToWorkingOn,
            "addToBlockers": $scope.addToBlockers
        };

        $uibModalInstance.close($scope.taskObject);
    };
    $scope.selectAction = function (selectedAction, index, taskItemId) {
        switch (selectedAction) {
            case "workedOn":
                $scope.addToWorkedOnList[index] = taskItemId;
                break;
            case "workingOn":
                $scope.addToWorkingOnList[index] = taskItemId;
                break;
            case "blockers":
                $scope.addToBlockersList[index] = taskItemId;
                break;
        }
    };
});