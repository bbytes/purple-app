/*
 * Task View Modal Controller
 * @author - Akshay
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
            if ($scope.addToWorkedOnList.length !== 0 && $scope.addToWorkedOnList.length !== undefined)
                $scope.addToWorkedOn = $scope.addToWorkedOn + "<p> #{<!-- id:" + item.task.taskItemId + "-->" + item.task.taskListName + "-" + item.task.taskItemName + " - Hrs:" + item.hours + "}</p>";
        });
        angular.forEach($scope.addToWorkingOnList, function (item) {
            if ($scope.addToWorkingOnList.length !== 0 && $scope.addToWorkingOnList.length !== undefined)
                $scope.addToWorkingOn = $scope.addToWorkingOn + "<p> #{<!-- id:" + item.task.taskItemId + "-->" + item.task.taskListName + "-" + item.taskItemName + " - Hrs:" + item.hours + "}</p>";
        });
        angular.forEach($scope.addToBlockersList, function (item) {
            if ($scope.addToBlockersList.length !== 0 && $scope.addToBlockersList.length !== undefined)
                $scope.addToBlockers = $scope.addToBlockers + "<p> #{<!-- id:" + item.task.taskItemId + "-->" + item.task.taskListName + "-" + item.task.taskItemName + " - Hrs:" + item.hours + "}</p>";
        });
        $scope.taskObject = {
            "addToWorkedOn": $scope.addToWorkedOn,
            "addToWorkingOn": $scope.addToWorkingOn,
            "addToBlockers": $scope.addToBlockers
        };
        $uibModalInstance.close($scope.taskObject);
    };
    $scope.selectAction = function (selectedAction, index, taskItem, hours) {
        switch (selectedAction) {
            case "workedOn":
                if (!hours)
                    hours = 1;
                $scope.addToWorkedOnList[index] = {};
                $scope.addToWorkedOnList[index].task = taskItem;
                $scope.addToWorkedOnList[index].hours = hours;
                break;
            case "workingOn":
                if (!hours)
                    hours = 1;
                $scope.addToWorkingOnList[index] = {};
                $scope.addToWorkingOnList[index].task = taskItem;
                $scope.addToWorkingOnList[index].hours = hours;
                break;
            case "blockers":
                if (!hours)
                    hours = 1;
                $scope.addToBlockersList[index] = {};
                $scope.addToBlockersList[index].task = taskItem;
                $scope.addToBlockersList[index].hours = hours;
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