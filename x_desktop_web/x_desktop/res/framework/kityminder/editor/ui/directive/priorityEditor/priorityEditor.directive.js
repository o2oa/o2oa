angular.module('kityminderEditor')

    .directive('priorityEditor', ['commandBinder', function(commandBinder) {
        return {
            restrict: 'E',
            templateUrl: 'ui/directive/priorityEditor/priorityEditor.html',
            scope: {
                minder: '='
            },
            replace: true,
            link: function($scope) {
                var minder = $scope.minder;
                var priorities = [];

                for (var i = 0; i < 10; i++) {
                    priorities.push(i);
                }

	            commandBinder.bind(minder, 'priority', $scope);

	            $scope.priorities = priorities;

	            $scope.getPriorityTitle = function(p) {
		            switch(p) {
			            case 0: return '移除优先级';
			            default: return '优先级' + p;
		            }
	            }
            }

        }
    }]);