angular.module('kityminderEditor')
	.directive('layout', function() {
		return {
			restrict: 'E',
			templateUrl: 'ui/directive/layout/layout.html',
			scope: {
				minder: '='
			},
            replace: true,
			link: function(scope) {

			}
		}
	});