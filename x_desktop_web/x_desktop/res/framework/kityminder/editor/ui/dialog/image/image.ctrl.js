angular.module('kityminderEditor')
    .controller('image.ctrl', ['$http', '$scope', '$modalInstance', 'image', function($http, $scope, $modalInstance, image) {


        $scope.data = {
            list: [],
            url: image.url || '',
            title: image.title || '',
            R_URL: /^https?\:\/\/(\w+\.)+\w+/
        };

        setTimeout(function() {
            var $imageUrl = $('#image-url');
            $imageUrl.focus();
            $imageUrl[0].setSelectionRange(0, $scope.data.url.length);
        }, 300);


        // 搜索图片按钮点击事件
        $scope.searchImage = function() {
            $scope.list = [];

            getImageData()
                .success(function(json) {
                    if(json && json.data) {
                        for(var i = 0; i < json.data.length; i++) {
                            if(json.data[i].objURL) {
                                $scope.list.push({
                                    title: json.data[i].fromPageTitleEnc,
                                    src: json.data[i].middleURL,
                                    url: json.data[i].middleURL
                                });
                            }
                        }
                    }
                })
                .error(function() {

                });
        };

        // 选择图片的鼠标点击事件
        $scope.selectImage = function($event) {
            var targetItem = $('#img-item'+ (this.$index));
            var targetImg = $('#img-'+ (this.$index));

            targetItem.siblings('.selected').removeClass('selected');
            targetItem.addClass('selected');

            $scope.data.url = targetImg.attr('src');
            $scope.data.title = targetImg.attr('alt');
        };

        $scope.shortCut = function(e) {
            e.stopPropagation();

            if (e.keyCode == 13) {
                $scope.ok();
            } else if (e.keyCode == 27) {
                $scope.cancel();
            }
        };

        $scope.ok = function () {
            if($scope.data.R_URL.test($scope.data.url)) {
                $modalInstance.close({
                    url: $scope.data.url,
                    title: $scope.data.title
                });
            } else {
                $scope.urlPassed = false;

                var $imageUrl = $('#image-url');
                $imageUrl.focus();
                $imageUrl[0].setSelectionRange(0, $scope.data.url.length);
            }

            editor.receiver.selectAll();
        };

        $scope.cancel = function () {
            $modalInstance.dismiss('cancel');
            editor.receiver.selectAll();
        };

        function getImageData(){
            var key = $scope.data.searchKeyword2;
            var currentTime = new Date();
            var url = 'http://image.baidu.com/search/acjson?tn=resultjson_com&ipn=rj&ct=201326592&fp=result&queryWord='+ key +'&cl=2&lm=-1&ie=utf-8&oe=utf-8&st=-1&ic=0&word='+ key +'&face=0&istype=2&nc=1&pn=60&rn=60&gsm=3c&'+ currentTime.getTime() +'=&callback=JSON_CALLBACK';

            return $http.jsonp(url);
        }
    }]);