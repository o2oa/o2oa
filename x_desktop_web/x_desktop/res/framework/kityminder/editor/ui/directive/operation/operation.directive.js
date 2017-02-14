angular.module('kityminderEditor')
    .directive('operation', function() {
        return {
            restrict: 'E',
            templateUrl: 'ui/directive/operation/operation.html',
            scope: {
                minder: '='
            },
            replace: true,
            link: function($scope) {
                $scope.editNode = function() {

                    var receiverElement = editor.receiver.element;
                    var fsm = editor.fsm;
                    var receiver = editor.receiver;

                    receiverElement.innerText = minder.queryCommandValue('text');
                    fsm.jump('input', 'input-request');
                    receiver.selectAll();

                }

            }
        }
    });