angular.module('kityminderEditor')
    .directive('undoRedo', function() {
        return {
            restrict: 'E',
            templateUrl: 'ui/directive/undoRedo/undoRedo.html',
            scope: {
                editor: '='
            },
            replace: true,
            link: function($scope) {

            }
        }
    });