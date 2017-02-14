angular.module('kityminderEditor')
    .directive('arrange', ['commandBinder', function(commandBinder) {
        return {
            restrict: 'E',
            templateUrl: 'ui/directive/arrange/arrange.html',
            scope: {
                minder: '='
            },
            replace: true,
            link: function($scope) {
                var minder = $scope.minder;

                //commandBinder.bind(minder, 'priority', $scope);
            }
        }
    }]);