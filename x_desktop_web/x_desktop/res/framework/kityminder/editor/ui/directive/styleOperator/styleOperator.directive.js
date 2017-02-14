angular.module('kityminderEditor')
	.directive('styleOperator', function() {
		return {
			restrict: 'E',
			templateUrl: 'ui/directive/styleOperator/styleOperator.html',
			scope: {
				minder: '='
			},
            replace: true
		}
	});