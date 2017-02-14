angular.module('kityminderEditor')
	.directive('colorPanel', function() {
		return {
			restrict: 'E',
			templateUrl: 'ui/directive/colorPanel/colorPanel.html',
			scope: {
				minder: '='
			},
            replace: true,
			link: function(scope) {

				var minder = scope.minder;
				var currentTheme = minder.getThemeItems();

				scope.$on('colorPicked', function(event, color) {
                    event.stopPropagation();
					scope.bgColor = color;
					minder.execCommand('background', color);
				});

				scope.setDefaultBg = function() {
                    var currentNode = minder.getSelectedNode();
                    var bgColor = minder.getNodeStyle(currentNode, 'background');

                    // 有可能是 kity 的颜色类
                    return typeof bgColor === 'object' ? bgColor.toHEX() : bgColor;
                };

                scope.bgColor = scope.setDefaultBg() || '#fff';

			}
		}
	});