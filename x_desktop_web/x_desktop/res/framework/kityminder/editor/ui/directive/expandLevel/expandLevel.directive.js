angular.module('kityminderEditor')
    .directive('expandLevel', function() {
        return {
            restrict: 'E',
            templateUrl: 'ui/directive/expandLevel/expandLevel.html',
            scope: {
                minder: '='
            },
            replace: true,
            link: function($scope) {

                $scope.levels = [1, 2, 3, 4, 5, 6];
            }
        }
    });