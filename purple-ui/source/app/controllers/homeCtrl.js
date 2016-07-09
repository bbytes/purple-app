/*
 * Logout Controller
 */
rootApp.controller('homeCtrl', ["$scope", "ModalService", function ($scope, ModalService) {

        $scope.showAModal = function () {

            // Just provide a template url, a controller and call 'showModal'.
            ModalService.showModal({
                templateUrl: "app/partials/feedback.html",
                controller: "feedbackCtrl"
            }).then(function (modal) {
                // The modal object has the element built, if this is a bootstrap modal
                // you can call 'modal' to show it, if it's a custom modal just show or hide
                // it as you need to.
                modal.element.modal();
                modal.close.then(function (result) {
                    $scope.message = result ? "You said Yes" : "You said No";
                });
            });

        };

    }]);

