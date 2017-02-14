angular.module('kityminderEditor')
    .directive('searchBtn', function() {
        return {
            restrict: 'E',
            templateUrl: 'ui/directive/searchBtn/searchBtn.html',
            scope: {
                minder: '='
            },
            replace: true,
            link: function (scope) {
                scope.enterSearch = enterSearch;

                function enterSearch() {
                    minder.fire('searchNode');
                }
            }
        }
    });