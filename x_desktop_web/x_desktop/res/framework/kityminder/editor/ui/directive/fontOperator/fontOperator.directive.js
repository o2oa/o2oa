angular.module('kityminderEditor')
	.directive('fontOperator', function() {
		return {
			restrict: 'E',
			templateUrl: 'ui/directive/fontOperator/fontOperator.html',
			scope: {
				minder: '='
			},
            replace: true,
			link: function(scope) {
				var minder = scope.minder;
				var currentTheme = minder.getThemeItems();

				scope.fontSizeList = [10, 12, 16, 18, 24, 32, 48];
                scope.fontFamilyList = [{
                    name: '宋体',
                    val: '宋体,SimSun'
                }, {
                    name: '微软雅黑',
                    val: '微软雅黑,Microsoft YaHei'
                }, {
                    name: '楷体',
                    val: '楷体,楷体_GB2312,SimKai'
                }, {
                    name: '黑体',
                    val: '黑体, SimHei'
                }, {
                    name: '隶书',
                    val: '隶书, SimLi'
                }, {
                    name: 'Andale Mono',
                    val: 'andale mono'
                }, {
                    name: 'Arial',
                    val: 'arial,helvetica,sans-serif'
                }, {
                    name: 'arialBlack',
                    val: 'arial black,avant garde'
                }, {
                    name: 'Comic Sans Ms',
                    val: 'comic sans ms'
                }, {
                    name: 'Impact',
                    val: 'impact,chicago'
                }, {
                    name: 'Times New Roman',
                    val: 'times new roman'
                }, {
                    name: 'Sans-Serif',
                    val: 'sans-serif'
                }];

                scope.$on('colorPicked', function(event, color) {
                    event.stopPropagation();

                    scope.foreColor = color;
                    minder.execCommand('forecolor', color);
                });

                scope.setDefaultColor = function() {
                    var currentNode = minder.getSelectedNode();
                    var fontColor = minder.getNodeStyle(currentNode, 'color');

                    // 有可能是 kity 的颜色类
                    return typeof fontColor === 'object' ? fontColor.toHEX() : fontColor;
                };

                scope.foreColor = scope.setDefaultColor() || '#000';

                scope.getFontfamilyName = function(val) {
                    var fontName = '';
                    scope.fontFamilyList.forEach(function(ele, idx, arr) {
                        if (ele.val === val) {
                            fontName = ele.name;
                            return '';
                        }
                    });

                    return fontName;
                }
			}
		}
	});