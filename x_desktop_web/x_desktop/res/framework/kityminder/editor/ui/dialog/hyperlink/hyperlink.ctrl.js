angular.module('kityminderEditor')
    .controller('hyperlink.ctrl', function ($scope, $modalInstance, link) {

        $scope.R_URL = /(http|ftp|https):\/\/[\w\-_]+(\.[\w\-_]+)+([\w\-\.,@?^=%&amp;:/~\+#]*[\w\-\@?^=%&amp;/~\+#])?/;

        $scope.url = link.url || '';
        $scope.title = link.title || '';

        setTimeout(function() {
            var $linkUrl = $('#link-url');
            $linkUrl.focus();
            $linkUrl[0].setSelectionRange(0, $scope.url.length);
        }, 30);

        $scope.shortCut = function(e) {
            e.stopPropagation();

            if (e.keyCode == 13) {
                $scope.ok();
            } else if (e.keyCode == 27) {
                $scope.cancel();
            }
        };

        $scope.ok = function () {
            if($scope.R_URL.test($scope.url)) {
                $modalInstance.close({
                    url: $scope.url,
                    title: $scope.title
                });
            } else {
                $scope.urlPassed = false;

                var $linkUrl = $('#link-url');
                $linkUrl.focus();
                $linkUrl[0].setSelectionRange(0, $scope.url.length);
            }
            editor.receiver.selectAll();
        };

        $scope.cancel = function () {
            $modalInstance.dismiss('cancel');
            editor.receiver.selectAll();
        };

    });