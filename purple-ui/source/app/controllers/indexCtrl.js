  
    rootApp.controller('indexCtrl', function ($scope, $rootScope, $state, $window) {
		 $scope.toTheTop = function() {
      $document.scrollTop(0, 5000);
    }
    var section2 = angular.element(document.getElementById('section-2'));
    $scope.toSection2 = function() {
      $document.scrollTo(section2, 40, 1000);
    }
 
 $scope.navClass = 'big';
   angular.element($window).bind(
	"scroll", function() {
         console.log(window.pageYOffset);
         if(window.pageYOffset > 0) {
           $scope.navClass = 'small';
         } else {
           $scope.navClass = 'big';
         }
         $scope.$apply();
	});
	
    });