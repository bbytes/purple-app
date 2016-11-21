/*
 * Task View Modal Controller
 * @author - Akshay
 */
angular.module('rootApp').controller('taskViewModalCtrl', function ($scope, modalData, $uibModalInstance, dropdownListService, $uibModal) {

    $scope.title = modalData.title;
    $scope.taskList = modalData.taskData;
    $scope.hoursList = modalData.hoursList;
    $scope.addToWorkedOnList = [];
    $scope.addToWorkingOnList = [];
    $scope.addToBlockersList = [];
    var taskItemMap = modalData.taskItemMap;
    var workedOnTaskMap = taskItemMap.workedOn;
    var workingOnTaskMap = taskItemMap.workingOn;
    var blockersTaskMap = taskItemMap.blockers;
    var itemKey = modalData.itemKey;
    $scope.cancel = function () {
        $uibModalInstance.dismiss('cancel');
    };
    $scope.saveTaskView = function () {
        $scope.addToWorkedOn = "";
        $scope.addToWorkingOn = "";
        $scope.addToBlockers = "";

        angular.forEach($scope.addToWorkedOnList, function (item) {
            if ($scope.addToWorkedOnList.length !== 0 && $scope.addToWorkedOnList.length !== undefined) {
                var genTaskKey;
                genTaskKey = "T" + itemKey;
                $scope.isKeyExist = workedOnTaskMap.hasOwnProperty(genTaskKey);
                if ($scope.isKeyExist) {
                    itemKey++;
                    genTaskKey = "T" + itemKey;
                }
                workedOnTaskMap[genTaskKey] = "id:" + item.task.taskItemId;
                $scope.addToWorkedOn = $scope.addToWorkedOn + "<p style='color:#3b73af;font-weight: bold;'> #{" + genTaskKey + "-" + item.task.taskListName + "-" + item.task.taskItemName + " - Hrs:" + item.hours + "}</p>";
            }
            itemKey++;
        });
        angular.forEach($scope.addToWorkingOnList, function (item) {
            if ($scope.addToWorkingOnList.length !== 0 && $scope.addToWorkingOnList.length !== undefined) {
                var genTaskKey = "T" + itemKey;
                $scope.isKeyExist = workingOnTaskMap.hasOwnProperty(genTaskKey);
                if ($scope.isKeyExist) {
                    itemKey++;
                    genTaskKey = "T" + itemKey;
                }
                workingOnTaskMap[genTaskKey] = "id:" + item.task.taskItemId;
                $scope.addToWorkingOn = $scope.addToWorkingOn + "<p style='color:#3b73af;font-weight: bold;'> #{" + genTaskKey + "-" + item.task.taskListName + "-" + item.task.taskItemName + "}</p>";
            }
            itemKey++;
        });
        angular.forEach($scope.addToBlockersList, function (item) {
            if ($scope.addToBlockersList.length !== 0 && $scope.addToBlockersList.length !== undefined) {
                var genTaskKey = "T" + itemKey;
                $scope.isKeyExist = blockersTaskMap.hasOwnProperty(genTaskKey);
                if ($scope.isKeyExist) {
                    itemKey++;
                    genTaskKey = "T" + itemKey;
                }
                blockersTaskMap[genTaskKey] = "id:" + item.task.taskItemId;
                $scope.addToBlockers = $scope.addToBlockers + "<p style='color:#3b73af;font-weight: bold;'> #{" + genTaskKey + "-" + item.task.taskListName + "-" + item.task.taskItemName + "}</p>";
            }
            itemKey++;
        });
        $scope.taskObject = {
            "addToWorkedOn": $scope.addToWorkedOn,
            "addToWorkingOn": $scope.addToWorkingOn,
            "addToBlockers": $scope.addToBlockers,
            "taskItemMap": taskItemMap,
            "itemKey": itemKey
        };
        $uibModalInstance.close($scope.taskObject);
    };
    $scope.updateHours = function (selectedAction, index, taskItem, hours) {
        if (hours === "") {
            hours = 0;
        }
        switch (selectedAction) {
            case "workedOn":
                if (!hours)
                    hours = 0;
                $scope.addToWorkedOnList[index] = {};
                $scope.addToWorkedOnList[index].task = taskItem;
                $scope.addToWorkedOnList[index].hours = hours;
                break;
            case "workingOn":
                if (!hours)
                    hours = 0;
                $scope.addToWorkingOnList[index] = {};
                $scope.addToWorkingOnList[index].task = taskItem;
                $scope.addToWorkingOnList[index].hours = hours;
                break;
            case "blockers":
                if (!hours)
                    hours = 0;
                $scope.addToBlockersList[index] = {};
                $scope.addToBlockersList[index].task = taskItem;
                $scope.addToBlockersList[index].hours = hours;
                break;
        }
    };
    $scope.selectAction = function (selectedAction, index, taskItem, hours) {
        if (selectedAction === "") {
            $scope.addToWorkedOnList.splice(index, 1);
            $scope.addToWorkingOnList.splice(index, 1);
            $scope.addToBlockersList.splice(index, 1);
        }
        switch (selectedAction) {
            case "workedOn":
                if (!hours)
                    hours = 0;
                $scope.addToWorkedOnList[index] = {};
                $scope.addToWorkedOnList[index].task = taskItem;
                $scope.addToWorkedOnList[index].hours = hours;
                $scope.addToWorkingOnList.splice(index, 1);
                $scope.addToBlockersList.splice(index, 1);
                break;
            case "workingOn":
                if (!hours)
                    hours = 0;
                $scope.addToWorkingOnList[index] = {};
                $scope.addToWorkingOnList[index].task = taskItem;
                $scope.addToWorkingOnList[index].hours = hours;
                $scope.addToWorkedOnList.splice(index, 1);
                $scope.addToBlockersList.splice(index, 1);
                break;
            case "blockers":
                if (!hours)
                    hours = 0;
                $scope.addToBlockersList[index] = {};
                $scope.addToBlockersList[index].task = taskItem;
                $scope.addToBlockersList[index].hours = hours;
                $scope.addToWorkedOnList.splice(index, 1);
                $scope.addToWorkingOnList.splice(index, 1);
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