angular.module('kityminderEditor')
    .directive('hyperLink', ['$modal', function($modal) {
        return {
            restrict: 'E',
            templateUrl: 'ui/directive/hyperLink/hyperLink.html',
            scope: {
                minder: '='
            },
            replace: true,
            link: function($scope) {
                var minder = $scope.minder;

                $scope.addHyperlink = function() {

                    var link = minder.queryCommandValue('HyperLink');

                    var hyperlinkModal = $modal.open({
                        animation: true,
                        templateUrl: 'ui/dialog/hyperlink/hyperlink.tpl.html',
                        controller: 'hyperlink.ctrl',
                        size: 'md',
                        resolve: {
                            link: function() {
                                return link;
                            }
                        }
                    });

                    hyperlinkModal.result.then(function(result) {
                        minder.execCommand('HyperLink', result.url, result.title || '');
                    });
                }
            }
        }
    }]);