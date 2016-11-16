/*
 * Task View Modal Controller
 */
angular.module('rootApp').controller('taskViewModalCtrl', function ($scope, modalData, $uibModalInstance, dropdownListService, $uibModal) {

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
            $scope.addToWorkedOn = $scope.addToWorkedOn + " #<!--" + item.taskItemId + "-->{" + item.taskListName + "-" + item.taskItemName + "}";
        });
        angular.forEach($scope.addToWorkingOnList, function (item) {
            $scope.addToWorkingOn = $scope.addToWorkingOn + " #<!--" + item.taskItemId + "-->{" + item.taskListName + "-" + item.taskItemName + "}";
        });
        angular.forEach($scope.addToBlockersList, function (item) {
            $scope.addToBlockers = $scope.addToBlockers + " #<!--" + item.taskItemId + "-->{" + item.taskListName + "-" + item.taskItemName + "}";
        });
        $scope.taskObject = {
            "addToWorkedOn": $scope.addToWorkedOn,
            "addToWorkingOn": $scope.addToWorkingOn,
            "addToBlockers": $scope.addToBlockers
        };

        $uibModalInstance.close($scope.taskObject);
    };

    $scope.selectAction = function (selectedAction, index, taskItem) {
        switch (selectedAction) {
            case "workedOn":
                $scope.addToWorkedOnList[index] = taskItem;
                break;
            case "workingOn":
                $scope.addToWorkingOnList[index] = taskItem;
                break;
            case "blockers":
                $scope.addToBlockersList[index] = taskItem;
                break;
        }
    };

    // loading all hours dropdown
    $scope.loadHours = function () {
        dropdownListService.getHours().then(function (response) {
            $scope.hoursList = response.data;
        });
    };
});