angular.module('kityminderEditor')
	.directive('progressEditor', ['commandBinder', function(commandBinder) {
		return {
			restrict: 'E',
			templateUrl: 'ui/directive/progressEditor/progressEditor.html',
			scope: {
				minder: '='
			},
            replace: true,
			link: function($scope) {
				var minder = $scope.minder;
				var progresses = [];

				for (var i = 0; i < 10; i++) {
					progresses.push(i);
				}

				commandBinder.bind(minder, 'progress', $scope);

				$scope.progresses = progresses;

				$scope.getProgressTitle = function(p) {
					switch(p) {
						case 0: return '移除进度';
						case 1: return '未开始';
						case 9: return '全部完成';
						default: return '完成' + (p - 1) + '/8';

					}
				}
			}
		}
	}])