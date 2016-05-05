rootApp.directive('capitalize', function() {
	   return {
	     require: 'ngModel',
	     link: function(scope, element, attrs, modelCtrl) {
	        var capitalize = function(inputValue) {
	           if(inputValue == undefined) inputValue = '';
	           var capitalized = inputValue.toUpperCase();
	           if(capitalized !== inputValue) {
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
                                 function(){
                                     return {
                                         priority: 1,
                                         terminal: true,
                                         link: function (scope, element, attr) {
                                             var msg = attr.ngConfirmClick || "Are you sure want to delete?";
                                             var clickAction = attr.ngClick;
                                             element.bind('click',function (event) {
                                                 if ( window.confirm(msg) ) {
                                                     scope.$eval(clickAction)
                                                 }
                                             });
                                         }
                                     };
                             }]);

rootApp.directive('validPasswordC', function() {
	  return {
	    require: 'ngModel',
	    scope: {

	      reference: '=validPasswordC'

	    },
	    link: function(scope, elm, attrs, ctrl) {
	      ctrl.$parsers.unshift(function(viewValue, $scope) {

	        var noMatch = viewValue != scope.reference
	        ctrl.$setValidity('noMatch', !noMatch);
	        return (noMatch)?noMatch:undefined;
	      });

	      scope.$watch("reference", function(value) {;
	        ctrl.$setValidity('noMatch', value === ctrl.$viewValue);

	      });
	    }
	  }
	});
	
	angular.element(document).ready(function(){

                c = angular.element(document.querySelector('#controller-demo')).scope();
            });

           

/* inline */


var pageslideDirective = angular.module("pageslide-directive", []);

pageslideDirective.directive('pageslide', [
    function (){
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
                param.side = attrs.pageslide || 'right';
                param.speed = attrs.psSpeed || '0.5';
                param.size = attrs.psSize || '60%';
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

                switch (param.side){
                    case 'right':
                        slider.style.height = attrs.psCustomHeight || '100%'; 
                        slider.style.top = attrs.psCustomTop ||  '0px';
                        slider.style.bottom = attrs.psCustomBottom ||  '0px';
                        slider.style.right = attrs.psCustomRight ||  '0px';
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
                function psClose(slider,param){
                    if (slider.style.width !== 0 && slider.style.width !== 0){
                        content.style.display = 'none';
                        switch (param.side){
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
                function psOpen(slider,param){
                    if (slider.style.width !== 0 && slider.style.width !== 0){
                        switch (param.side){
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
                        setTimeout(function(){
                            content.style.display = 'block';
                        },(param.speed * 1000));

                    }
                }

                /*
                * Watchers
                * */

                $scope.$watch(attrs.psOpen, function (value){
                    if (!!value) {
                        // Open
                        psOpen(slider,param);
                    } else {
                        // Close
                        psClose(slider,param);
                    }
                });

                // close panel on location change
                if(attrs.psAutoClose){
                    $scope.$on("$locationChangeStart", function(){
                        psClose(slider, param);
                    });
                    $scope.$on("$stateChangeStart", function(){
                        psClose(slider, param);
                    });
                }

               

                /*
                * Events
                * */
                var close_handler = (attrs.href) ? document.getElementById(attrs.href.substr(1) + '-close') : null;
                if (el[0].addEventListener) {
                    el[0].addEventListener('click',function(e){
                        e.preventDefault();
                        psOpen(slider,param);                    
                    });

                    if (close_handler){
                        close_handler.addEventListener('click', function(e){
                            e.preventDefault();
                            psClose(slider,param);
                        });
                    }
                } else {
                    // IE8 Fallback code
                    el[0].attachEvent('onclick',function(e){
                        e.returnValue = false;
                        psOpen(slider,param);                    
                    });

                    if (close_handler){
                        close_handler.attachEvent('onclick', function(e){
                            e.returnValue = false;
                            psClose(slider,param);
                        });
                    }
                }

            }
        };
    }
]);

.directive('timezoneSelector', ['_', 'moment', 'timezoneFactory', 'zoneToCC', 'CCToCountryName', function (_, moment, timezoneFactory, zoneToCC, CCToCountryName) {
    return {
      restrict: 'E',
      replace: true,
      template: '<select style="min-width:300px;"></select>',
      scope: {
        ngModel: '=',
        translations: '='
      },
      link: function ($scope, elem, attrs) {
        var data = []
        var timezones = timezoneFactory.get()

        // Group the timezones by their country code
        var timezonesGroupedByCC = {}
        _.forEach(timezones, function (timezone) {
          if (_.has(zoneToCC, timezone.id)) {
            var CC = zoneToCC[timezone.id]
            timezonesGroupedByCC[CC] = !timezonesGroupedByCC[CC] ? [] : timezonesGroupedByCC[CC]
            timezonesGroupedByCC[CC].push(timezone)
          }
        })

        // Add the grouped countries to the data array with their country name as the group option
        _.forEach(timezonesGroupedByCC, function (zonesByCountry, CC) {
          var zonesForCountry = {
            text: CCToCountryName[CC] + ': ',
            children: zonesByCountry,
            firstNOffset: zonesByCountry[0].nOffset
          }

          data.push(zonesForCountry)
        })

        // Sort by UTC or country name
        if (attrs.sortBy === 'offset') {
          data = _.sortBy(data, 'firstNOffset')
          _.forEach(data, function (zonesForCountry, key) {
            zonesForCountry.children = _.sortBy(zonesForCountry.children, 'nOffset')
          })
        } else {
          data = _.sortBy(data, 'text')
        }

        // add initial options forlocal
        if (attrs.showLocal !== undefined) {
          if (jstz !== undefined) {
            // Make sure the tz from jstz has underscores replaced with spaces so it matches
            // the format used in timezoneFactory
            var extraTZs = _.filter(timezones, { 'id': jstz.determine().name() })
          } else {
            var localUTC = 'UTC' + moment().format('Z')
            extraTZs = _.filter(timezones, {'offset': localUTC})
          }

          if (extraTZs !== undefined && extraTZs.length > 0) {
            data.splice(0, 0, {
              text: _.get($scope, 'translations.local', 'Local') + ': ',
              children: extraTZs,
              firstNOffset: extraTZs[0].nOffset,
              firstOffset: extraTZs[0].offset
            })
          }
        }

        if (attrs.setLocal !== undefined) {
          if (jstz !== undefined) {
            $scope.ngModel || ($scope.ngModel = jstz.determine().name())
          }
        }

        // add initial options
        if (attrs.primaryChoices !== undefined) {
          var primaryChoices = []
          _.forEach(attrs.primaryChoices.split(' '), function (choice) {
            primaryChoices.push(choice.replace('_', ' '))
          })
          extraTZs = _.filter(timezones, function (tz) { return _.includes(primaryChoices, tz.name) })

          if (extraTZs !== undefined && extraTZs.length > 0) {
            data.splice(0, 0, {
              text: _.get($scope, 'translations.primary', 'Primary') + ': ',
              children: extraTZs,
              firstNOffset: extraTZs[0].nOffset,
              firstOffset: extraTZs[0].offset
            })
          }
        }

        // Construct a select box with the timezones grouped by country
        _.forEach(data, function (group) {
          var optgroup = $('<optgroup label="' + group.text + '">')
          group.children.forEach(function (option) {
            if (attrs.displayUtc === 'true' && option.name.indexOf('(UTC') === -1) {
              option.name = option.name + ' (' + option.offset + ')'
            }

            optgroup.append('<option value="' + option.id + '">' +
              option.name + '</option>')
          })
          elem.append(optgroup)
        })

        // Initialise the chosen box
        elem.chosen({
          width: attrs.width || '300px',
          include_group_label_in_selected: true,
          search_contains: true,
          no_results_text: _.get($scope, 'translations.no_results_text',
              'No results, try searching for the name of your country or nearest major city.'),
          placeholder_text_single: _.get($scope, 'translations.placeholder', 'Choose a timezone')
        })

        // Update the box if ngModel changes
        $scope.$watch('ngModel', function () {
          elem.val($scope.ngModel)
          elem.trigger('chosen:updated')
        })
      }
    }
  }])
  
	
                             
                             