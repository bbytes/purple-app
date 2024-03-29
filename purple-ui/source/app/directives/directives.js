rootApp.directive('capitalize', function () {
    return {
        require: 'ngModel',
        link: function (scope, element, attrs, modelCtrl) {
            var capitalize = function (inputValue) {
                if (inputValue == undefined)
                    inputValue = '';
                var capitalized = inputValue.toUpperCase();
                if (capitalized !== inputValue) {
                    modelCtrl.$setViewValue(capitalized);
                    modelCtrl.$render();
                }
                return capitalized;
            }
            modelCtrl.$parsers.push(capitalize);
            capitalize(scope[attrs.ngModel]);  // capitalize initial value
        }
    };
});

rootApp.directive('ngConfirmClick', [
    function () {
        return {
            priority: 1,
            terminal: true,
            link: function (scope, element, attr) {
                var msg = attr.ngConfirmClick || "Are you sure want to delete?";
                var clickAction = attr.ngClick;
                element.bind('click', function (event) {
                    if (window.confirm(msg)) {
                        scope.$eval(clickAction)
                    }
                });
            }
        };
    }]);

rootApp.directive('validPasswordC', function () {
    return {
        require: 'ngModel',
        scope: {
            reference: '=validPasswordC'

        },
        link: function (scope, elm, attrs, ctrl) {
            ctrl.$parsers.unshift(function (viewValue, $scope) {

                var noMatch = viewValue != scope.reference
                ctrl.$setValidity('noMatch', !noMatch);
                return (noMatch) ? noMatch : undefined;
            });

            scope.$watch("reference", function (value) {
                ;
                ctrl.$setValidity('noMatch', value === ctrl.$viewValue);

            });
        }
    }
});

angular.element(document).ready(function () {

    c = angular.element(document.querySelector('#controller-demo')).scope();
});



/* inline */


var pageslideDirective = angular.module("pageslide-directive", []);

pageslideDirective.directive('pageslide', [
    function () {
        var defaults = {};

        /* Return directive definition object */

        return {
            restrict: "EA",
            replace: false,
            transclude: false,
            scope: true,
            link: function ($scope, el, attrs) {
                /* Inspect */
                //console.log($scope);
                //console.log(el);
                //console.log(attrs);

                /* parameters */
                var param = {};
                param.side = attrs.pageslide || 'left';
                param.speed = attrs.psSpeed || '0.5';
                param.size = attrs.psSize || '35%';
                param.className = attrs.psClass || 'ng-pageslide';

                /* DOM manipulation */
                //console.log(el);
                var content = null;
                if (el.children() && el.children().length) {
                    content = el.children()[0];
                } else {
                    content = (attrs.href) ? document.getElementById(attrs.href.substr(1)) : document.getElementById(attrs.psTarget.substr(1));
                }
                //console.log(content);
                // Check for content
                if (!content)
                    throw new Error('You have to elements inside the <pageslide> or you have not specified a target href');
                var slider = document.createElement('div');
                slider.className = param.className;

                /* Style setup */
                slider.style.transitionDuration = param.speed + 's';
                slider.style.webkitTransitionDuration = param.speed + 's';
                slider.style.zIndex = 1000;
                slider.style.position = 'fixed';
                slider.style.width = 0;
                slider.style.height = 0;
                slider.style.transitionProperty = 'width, height';

                switch (param.side) {
                    case 'right':
                        slider.style.height = attrs.psCustomHeight || '100%';
                        slider.style.top = attrs.psCustomTop || '0px';
                        slider.style.bottom = attrs.psCustomBottom || '0px';
                        slider.style.right = attrs.psCustomRight || '0px';
                        break;
                    case 'left':
                        slider.style.height = attrs.psCustomHeight || '100%';
                        slider.style.top = attrs.psCustomTop || '0px';
                        slider.style.bottom = attrs.psCustomBottom || '0px';
                        slider.style.left = attrs.psCustomLeft || '0px';
                        break;
                    case 'top':
                        slider.style.width = attrs.psCustomWidth || '100%';
                        slider.style.left = attrs.psCustomLeft || '0px';
                        slider.style.top = attrs.psCustomTop || '0px';
                        slider.style.right = attrs.psCustomRight || '0px';
                        break;
                    case 'bottom':
                        slider.style.width = attrs.psCustomWidth || '100%';
                        slider.style.bottom = attrs.psCustomBottom || '0px';
                        slider.style.left = attrs.psCustomLeft || '0px';
                        slider.style.right = attrs.psCustomRight || '0px';
                        break;
                }


                /* Append */
                document.body.appendChild(slider);
                slider.appendChild(content);

                /* Closed */
                function psClose(slider, param) {
                    if (slider.style.width !== 0 && slider.style.width !== 0) {
                        content.style.display = 'none';
                        switch (param.side) {
                            case 'right':
                                slider.style.width = '0px';
                                break;
                            case 'left':
                                slider.style.width = '0px';
                                break;
                            case 'top':
                                slider.style.height = '0px';
                                break;
                            case 'bottom':
                                slider.style.height = '0px';
                                break;
                        }
                    }
                }

                /* Open */
                function psOpen(slider, param) {
                    if (slider.style.width !== 0 && slider.style.width !== 0) {
                        switch (param.side) {
                            case 'right':
                                slider.style.width = param.size;
                                break;
                            case 'left':
                                slider.style.width = param.size;
                                break;
                            case 'top':
                                slider.style.height = param.size;
                                break;
                            case 'bottom':
                                slider.style.height = param.size;
                                break;
                        }
                        setTimeout(function () {
                            content.style.display = 'block';
                        }, (param.speed * 1000));

                    }
                }

                /*
                 * Watchers
                 * */

                $scope.$watch(attrs.psOpen, function (value) {
                    if (!!value) {
                        // Open
                        psOpen(slider, param);
                    } else {
                        // Close
                        psClose(slider, param);
                    }
                });

                // close panel on location change
                if (attrs.psAutoClose) {
                    $scope.$on("$locationChangeStart", function () {
                        psClose(slider, param);
                    });
                    $scope.$on("$stateChangeStart", function () {
                        psClose(slider, param);
                    });
                }



                /*
                 * Events
                 * */
                var close_handler = (attrs.href) ? document.getElementById(attrs.href.substr(1) + '-close') : null;
                if (el[0].addEventListener) {
                    el[0].addEventListener('click', function (e) {
                        e.preventDefault();
                        psOpen(slider, param);
                    });

                    if (close_handler) {
                        close_handler.addEventListener('click', function (e) {
                            e.preventDefault();
                            psClose(slider, param);
                        });
                    }
                } else {
                    // IE8 Fallback code
                    el[0].attachEvent('onclick', function (e) {
                        e.returnValue = false;
                        psOpen(slider, param);
                    });

                    if (close_handler) {
                        close_handler.attachEvent('onclick', function (e) {
                            e.returnValue = false;
                            psClose(slider, param);
                        });
                    }
                }

            }
        };
    }
]);

// Directive for csv file download of status                          
angular.module('rootApp').directive('purpleCsvDownload', function ($document, $timeout, statusService) {
    return {
        restrict: 'AC',
        scope: {
            csvData: '=purpleCsvDownload'
        },
        link: function (scope, element, attrs) {

            function doClick() {
                statusService.csvDownloadAllStatus(scope.csvData).then(function (response) {
                    scope.csv = response.data;
                    var charset = scope.charset || "utf-8";
                    var blob = new Blob([scope.csv], {
                        type: "text/csv;charset=" + charset + ";"
                    });

                    if (window.navigator.msSaveOrOpenBlob) {
                        navigator.msSaveBlob(blob, response.fileName);
                    } else {
                        var downloadContainer = angular.element('<div data-tap-disabled="true"><a></a></div>');
                        var downloadLink = angular.element(downloadContainer.children()[0]);
                        downloadLink.attr('href', window.URL.createObjectURL(blob));
                        downloadLink.attr('download', response.fileName);
                        downloadLink.attr('target', '_blank');

                        $document.find('body').append(downloadContainer);

                        $timeout(function () {
                            downloadLink[0].click();
                            downloadLink.remove();
                        }, null);
                    }
                });
            }

            element.bind('click', function (e) {
                if (scope.csvData) {
                    doClick();
                }
            });
        }
    };
});

// Directive for csv file download of all statuses by user                          
angular.module('rootApp').directive('csvDownloadUser', function ($document, $timeout, statusService) {
    return {
        restrict: 'AC',
        scope: {
            csvData: '=csvDownloadUser'
        },
        link: function (scope, element, attrs) {

            function doClick() {
                statusService.csvDownloadAllStatusByUser(scope.csvData).then(function (response) {
                    scope.csv = response.data;
                    var charset = scope.charset || "utf-8";
                    var blob = new Blob([scope.csv], {
                        type: "text/csv;charset=" + charset + ";"
                    });

                    if (window.navigator.msSaveOrOpenBlob) {
                        navigator.msSaveBlob(blob, response.fileName);
                    } else {
                        var downloadContainer = angular.element('<div data-tap-disabled="true"><a></a></div>');
                        var downloadLink = angular.element(downloadContainer.children()[0]);
                        downloadLink.attr('href', window.URL.createObjectURL(blob));
                        downloadLink.attr('download', response.fileName);
                        downloadLink.attr('target', '_blank');

                        $document.find('body').append(downloadContainer);

                        $timeout(function () {
                            downloadLink[0].click();
                            downloadLink.remove();
                        }, null);
                    }
                });
            }

            element.bind('click', function (e) {
                if (scope.csvData) {
                    doClick();
                }
            });
        }
    };
});


// Directive for csv file download of timeline
angular.module('rootApp').directive('csvDownload', function ($document, $timeout, statusService) {
    return {
        restrict: 'AC',
        scope: {
            csvData: '=csvDownload'
        },
        link: function (scope, element, attrs) {

            function doClick() {
                statusService.csvDownloadByProjectAndUser(scope.csvData).then(function (response) {
                    scope.csv = response.data;
                    var charset = scope.charset || "utf-8";
                    var blob = new Blob([scope.csv], {
                        type: "text/csv;charset=" + charset + ";"
                    });

                    if (window.navigator.msSaveOrOpenBlob) {
                        navigator.msSaveBlob(blob, response.fileName);
                    } else {
                        var downloadContainer = angular.element('<div data-tap-disabled="true"><a></a></div>');
                        var downloadLink = angular.element(downloadContainer.children()[0]);
                        downloadLink.attr('href', window.URL.createObjectURL(blob));
                        downloadLink.attr('download', response.fileName);
                        downloadLink.attr('target', '_blank');

                        $document.find('body').append(downloadContainer);

                        $timeout(function () {
                            downloadLink[0].click();
                            downloadLink.remove();
                        }, null);
                    }
                });
            }

            element.bind('click', function (e) {
                if (scope.csvData) {
                    doClick();
                }
            });
        }
    };
});

// This directive to load searchable dropdowns dynamically
angular.module('rootApp').directive('chosen', function ($timeout) {

    var linker = function (scope, element, attr) {

        scope.$watch(attr.chosen, function () {
            $timeout(function () {
                element.trigger('chosen:updated');
            }, 0, false);
        }, true);

        $timeout(function () {
            element.chosen();
        }, 0, false);
    };

    return {
        restrict: 'AC',
        link: linker
    };
});

// text angular with @ mention for working on 
angular.module('rootApp').directive('workingonTextAngularMentio', ['$rootScope', '$filter', function ($rootScope, $filter) {
        var directiveDefinitionObject = {
            restrict: 'E',
            templateUrl: "app/partials/textAngular-mention-template/workingonTextAngularWithMentio.html",
            require: '^ngModel',
            scope: {
                ngModel: '='
            },
            controller: ['$scope', function ($scope) {
                    $scope.setup = function (element) {
                        element.attr('mentio', 'mentio');
                        element.attr('mentio-typed-term', 'typedTerm');
                        element.attr('mentio-require-leading-space', 'true');
                        element.attr('mentio-id', "'content-editor+$id+1'");
                    };

                    // avoid spacing while copy paste in text angular
                    $scope.stripFormat = function ($html) {
                        return $filter('htmlToPlaintext')($html);
                    };

                    $scope.searchPeople = function (term) {
                        var peopleList = [];

                        angular.forEach($rootScope.projectUsers, function (value, key) {
                            if (value.userName.toUpperCase().indexOf(term.toUpperCase()) >= 0 || value.email.toUpperCase().indexOf(term.toUpperCase()) >= 0) {
                                peopleList.push(value);
                            }
                        });
                        $scope.people = peopleList;
                    };

                    $scope.getPeopleText = function (item) {
                        return '@[' + item.email + ']';
                    };
                }]
        };

        return directiveDefinitionObject;
    }]);

// text angular with @ mention for worked on 
angular.module('rootApp').directive('workedonTextAngularMentio', ['$rootScope', '$filter', function ($rootScope, $filter) {
        var directiveDefinitionObject = {
            restrict: 'E',
            templateUrl: "app/partials/textAngular-mention-template/workedonTextAngularWithMentio.html",
            require: '^ngModel',
            scope: {
                ngModel: '='
            },
            controller: ['$scope', function ($scope) {
                    $scope.setup = function (element) {
                        element.attr('mentio', 'mentio');
                        element.attr('mentio-typed-term', 'typedTerm');
                        element.attr('mentio-require-leading-space', 'true');
                        element.attr('mentio-id', "'content-editor-{{$id}}'");
                    };

                    // avoid spacing while copy paste in text angular
                    $scope.stripFormat = function ($html) {
                        return $filter('htmlToPlaintext')($html);
                    };

                    $scope.searchPeople = function (term) {
                        var peopleList = [];

                        angular.forEach($rootScope.projectUsers, function (value, key) {
                            if (value.userName.toUpperCase().indexOf(term.toUpperCase()) >= 0 || value.email.toUpperCase().indexOf(term.toUpperCase()) >= 0) {
                                peopleList.push(value);
                            }
                        });
                        $scope.people = peopleList;
                    };

                    $scope.getPeopleText = function (item) {
                        return '@[' + item.email + ']';
                    };
                }]
        };

        return directiveDefinitionObject;
    }]);

// text angular with @ mention for blockers
angular.module('rootApp').directive('blockersTextAngularMentio', ['$rootScope', '$filter', function ($rootScope, $filter) {
        var directiveDefinitionObject = {
            restrict: 'E',
            templateUrl: "app/partials/textAngular-mention-template/blockersTextAngularWithMentio.html",
            require: '^ngModel',
            scope: {
                ngModel: '='
            },
            controller: ['$scope', function ($scope) {
                    $scope.setup = function (element) {
                        element.attr('mentio', 'mentio');
                        element.attr('mentio-typed-term', 'typedTerm');
                        element.attr('mentio-require-leading-space', 'true');
                        element.attr('mentio-id', "'content-editor+$id'");
                    };

                    // avoid spacing while copy paste in text angular
                    $scope.stripFormat = function ($html) {
                        return $filter('htmlToPlaintext')($html);
                    };

                    $scope.searchPeople = function (term) {
                        var peopleList = [];
                        angular.forEach($rootScope.projectUsers, function (value, key) {
                            if (value.userName.toUpperCase().indexOf(term.toUpperCase()) >= 0 || value.email.toUpperCase().indexOf(term.toUpperCase()) >= 0) {
                                peopleList.push(value);
                            }
                        });
                        $scope.people = peopleList;
                    };

                    $scope.getPeopleText = function (item) {
                        return '@[' + item.email + ']';
                    };
                }]
        };

        return directiveDefinitionObject;
    }]);

// text angular with @ mention for comment
angular.module('rootApp').directive('commentTextAngularMentio', ['$rootScope', '$filter', function ($rootScope, $filter) {
        var directiveDefinitionObject = {
            restrict: 'E',
            templateUrl: "app/partials/textAngular-mention-template/commentTextAngularWithMentio.html",
            require: '^ngModel',
            scope: {
                ngModel: '='
            },
            controller: ['$scope', function ($scope) {
                    $scope.setup = function (element) {
                        element.attr('mentio', 'mentio');
                        element.attr('mentio-typed-term', 'typedTerm');
                        element.attr('mentio-require-leading-space', 'true');
                        element.attr('mentio-id', "'content-editor+$id'");
                    };

                    // avoid spacing while copy paste in text angular
                    $scope.stripFormat = function ($html) {
                        return $filter('htmlToPlaintext')($html);
                    };

                    $scope.searchPeople = function (term) {
                        var peopleList = [];
                        angular.forEach($rootScope.projectUsers, function (value, key) {
                            if (value.userName.toUpperCase().indexOf(term.toUpperCase()) >= 0 || value.email.toUpperCase().indexOf(term.toUpperCase()) >= 0) {
                                peopleList.push(value);
                            }
                        });
                        $scope.people = peopleList;
                    };

                    $scope.getPeopleText = function (item) {
                        return '@[' + item.email + ']';
                    };
                }]
        };

        return directiveDefinitionObject;
    }]); 