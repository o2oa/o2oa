angular.module('kityminderEditor')
    .controller('imExportNode.ctrl', function ($scope, $modalInstance, title, defaultValue, type) {

        $scope.title = title;

        $scope.value = defaultValue;

        $scope.type = type;

        $scope.ok = function () {
            if ($scope.value == '') {
                return;
            }
            $modalInstance.close($scope.value);
            editor.receiver.selectAll();
        };

        $scope.cancel = function () {
            $modalInstance.dismiss('cancel');
            editor.receiver.selectAll();
        };

        setTimeout(function() {
            $('.single-input').focus();

            $('.single-input')[0].setSelectionRange(0, defaultValue.length);

        }, 30);

        $scope.shortCut = function(e) {
            e.stopPropagation();

            //if (e.keyCode == 13 && e.shiftKey == false) {
            //    $scope.ok();
            //}

            if (e.keyCode == 27) {
                $scope.cancel();
            }

            // tab 键屏蔽默认事件 和 backspace 键屏蔽默认事件
            if (e.keyCode == 8 && type == 'export') {
                e.preventDefault();
            }

            if (e.keyCode == 9) {
                e.preventDefault();
                var $textarea = e.target;
                var pos = getCursortPosition($textarea);
                var str = $textarea.value;
                $textarea.value = str.substr(0, pos) + '\t' + str.substr(pos);
                setCaretPosition($textarea, pos + 1);
            }

        };

        /*
        * 获取 textarea 的光标位置
        * @Author: Naixor
        * @date: 2015.09.23
        * */
        function getCursortPosition (ctrl) {
            var CaretPos = 0;	// IE Support
            if (document.selection) {
                ctrl.focus ();
                var Sel = document.selection.createRange ();
                Sel.moveStart ('character', -ctrl.value.length);
                CaretPos = Sel.text.length;
            }
            // Firefox support
            else if (ctrl.selectionStart || ctrl.selectionStart == '0') {
                CaretPos = ctrl.selectionStart;
            }
            return (CaretPos);
        }

        /*
         * 设置 textarea 的光标位置
         * @Author: Naixor
         * @date: 2015.09.23
         * */
        function setCaretPosition(ctrl, pos){
            if(ctrl.setSelectionRange) {
                ctrl.focus();
                ctrl.setSelectionRange(pos,pos);
            } else if (ctrl.createTextRange) {
                var range = ctrl.createTextRange();
                range.collapse(true);
                range.moveEnd('character', pos);
                range.moveStart('character', pos);
                range.select();
            }
        }

    });