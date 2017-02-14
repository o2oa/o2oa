angular.module('kityminderEditor')
    .directive('imageBtn', ['$modal', function($modal) {
        return {
            restrict: 'E',
            templateUrl: 'ui/directive/imageBtn/imageBtn.html',
            scope: {
                minder: '='
            },
            replace: true,
            link: function($scope) {
                var minder = $scope.minder;

                $scope.addImage = function() {

                    var image = minder.queryCommandValue('image');

                    var imageModal = $modal.open({
                        animation: true,
                        templateUrl: 'ui/dialog/image/image.tpl.html',
                        controller: 'image.ctrl',
                        size: 'md',
                        resolve: {
                            image: function() {
                                return image;
                            }
                        }
                    });

                    imageModal.result.then(function(result) {
                        minder.execCommand('image', result.url, result.title || '');
                    });
                }
            }
        }
    }]);