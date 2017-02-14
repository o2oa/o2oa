angular.module('kityminderEditor')
    .directive('resourceEditor', function () {
        return {
            restrict: 'E',
            templateUrl: 'ui/directive/resourceEditor/resourceEditor.html',
            scope: {
                minder: '='
            },
            replace: true,
            controller: function ($scope) {
                var minder = $scope.minder;

	            var isInteracting = false;

                minder.on('interactchange', function () {
                    var enabled = $scope.enabled = minder.queryCommandState('resource') != -1;
                    var selected = enabled ? minder.queryCommandValue('resource') : [];
                    var used = minder.getUsedResource().map(function (resourceName) {
                        return {
                            name: resourceName,
                            selected: selected.indexOf(resourceName) > -1
                        }
                    });
                    $scope.used = used;

	                isInteracting = true;
                    $scope.$apply();
	                isInteracting = false;
                });

                $scope.$watch('used', function (used) {
                    if (minder.queryCommandState('resource') != -1 && used) {
                        var resource = used.filter(function (resource) {
                            return resource.selected;
                        }).map(function (resource) {
                            return resource.name;
                        });

	                    // 由于 interactchange 带来的改变则不用执行 resource 命令
	                    if (isInteracting) {
		                    return;
	                    }
                        minder.execCommand('resource', resource);
                    }
                }, true);

                $scope.resourceColor = function (resource) {
                    return minder.getResourceColor(resource).toHEX();
                };

                $scope.addResource = function (resourceName) {
	                var origin = minder.queryCommandValue('resource');
                    if (!resourceName || !/\S/.test(resourceName)) return;

	                if (origin.indexOf(resourceName) == -1) {
		                $scope.used.push({
			                name: resourceName,
			                selected: true
		                });
	                }

                    $scope.newResourceName = null;
                };

            }
        };
    })

    .directive('clickAnywhereButHere', ['$document', function ($document) {
        return {
            link: function(scope, element, attrs) {
                var onClick = function (event) {
                    var isChild = $('#resource-dropdown').has(event.target).length > 0;
                    var isSelf = $('#resource-dropdown') == event.target;
                    var isInside = isChild || isSelf;
                    if (!isInside) {
                        scope.$apply(attrs.clickAnywhereButHere)
                    }
                };

                scope.$watch(attrs.isActive, function(newValue, oldValue) {
                    if (newValue !== oldValue && newValue == true) {
                        $document.bind('click', onClick);
                    }
                    else if (newValue !== oldValue && newValue == false) {
                        $document.unbind('click', onClick);
                    }
                });
            }
        };
    }]);