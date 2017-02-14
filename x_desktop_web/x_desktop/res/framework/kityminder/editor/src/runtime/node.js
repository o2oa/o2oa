define(function(require, exports, module) {

    function NodeRuntime() {
        var runtime = this;
        var minder = this.minder;
        var hotbox = this.hotbox;
        var fsm = this.fsm;

        var main = hotbox.state('main');

        var buttons = [
            '前移:Alt+Up:ArrangeUp',
            '下级:Tab|Insert:AppendChildNode',
            '同级:Enter:AppendSiblingNode',
            '后移:Alt+Down:ArrangeDown',
            '删除:Delete|Backspace:RemoveNode',
            '上级:Shift+Tab|Shift+Insert:AppendParentNode'
            //'全选:Ctrl+A:SelectAll'
        ];

        var AppendLock = 0;

        buttons.forEach(function(button) {
            var parts = button.split(':');
            var label = parts.shift();
            var key = parts.shift();
            var command = parts.shift();
            main.button({
                position: 'ring',
                label: label,
                key: key,
                action: function() {
                    if (command.indexOf('Append') === 0) {
                        AppendLock++;
                        minder.execCommand(command, '分支主题');

                        // provide in input runtime
                        function afterAppend () {
                            if (!--AppendLock) {
                                runtime.editText();
                            }
                            minder.off('layoutallfinish', afterAppend);
                        }
                        minder.on('layoutallfinish', afterAppend);
                    } else {
                        minder.execCommand(command);
                        fsm.jump('normal', 'command-executed');
                    }
                },
                enable: function() {
                    return minder.queryCommandState(command) != -1;
                }
            });
        });

        main.button({
            position: 'bottom',
            label: '导入节点',
            key: 'Alt + V',
            enable: function() {
                var selectedNodes = minder.getSelectedNodes();
                return selectedNodes.length == 1;
            },
            action: importNodeData,
            next: 'idle'
        });

        main.button({
            position: 'bottom',
            label: '导出节点',
            key: 'Alt + C',
            enable: function() {
                var selectedNodes = minder.getSelectedNodes();
                return selectedNodes.length == 1;
            },
            action: exportNodeData,
            next: 'idle'
        });

        function importNodeData() {
            minder.fire('importNodeData');
        }

        function exportNodeData() {
            minder.fire('exportNodeData');
        }

        //main.button({
        //    position: 'ring',
        //    key: '/',
        //    action: function(){
        //        if (!minder.queryCommandState('expand')) {
        //            minder.execCommand('expand');
        //        } else if (!minder.queryCommandState('collapse')) {
        //            minder.execCommand('collapse');
        //        }
        //    },
        //    enable: function() {
        //        return minder.queryCommandState('expand') != -1 || minder.queryCommandState('collapse') != -1;
        //    },
        //    beforeShow: function() {
        //        if (!minder.queryCommandState('expand')) {
        //            this.$button.children[0].innerHTML = '展开';
        //        } else {
        //            this.$button.children[0].innerHTML = '收起';
        //        }
        //    }
        //})
    }

    return module.exports = NodeRuntime;
});
