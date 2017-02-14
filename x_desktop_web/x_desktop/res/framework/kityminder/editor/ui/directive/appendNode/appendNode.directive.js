angular.module('kityminderEditor')
    .directive('appendNode', ['commandBinder', function(commandBinder) {
        return {
            restrict: 'E',
            templateUrl: 'ui/directive/appendNode/appendNode.html',
            scope: {
                minder: '='
            },
            replace: true,
            link: function($scope) {
                var minder = $scope.minder;

                commandBinder.bind(minder, 'appendchildnode', $scope)

                $scope.execCommand = function(command) {
                    minder.execCommand(command, '分支主题');
                    editText();
                };

                function editText() {
                    var receiverElement = editor.receiver.element;
                    var fsm = editor.fsm;
                    var receiver = editor.receiver;

                    receiverElement.innerText = minder.queryCommandValue('text');
                    fsm.jump('input', 'input-request');
                    receiver.selectAll();
                }
            }
        }
    }]);