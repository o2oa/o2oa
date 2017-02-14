/**
 * @fileOverview
 *
 * 热盒 Runtime
 *
 * @author: techird
 * @copyright: Baidu FEX, 2014
 */
define(function(require, exports, module) {
    var Hotbox = require('../hotbox');

    function HotboxRuntime() {
        var fsm = this.fsm;
        var minder = this.minder;
        var receiver = this.receiver;
        var container = this.container;

        var hotbox = new Hotbox(container);

        hotbox.setParentFSM(fsm);

        fsm.when('normal -> hotbox', function(exit, enter, reason) {
            var node = minder.getSelectedNode();
            var position;
            if (node) {
                var box = node.getRenderBox();
                position = {
                    x: box.cx,
                    y: box.cy
                };
            }
            hotbox.active('main', position);
        });

        fsm.when('normal -> normal', function(exit, enter, reason, e) {
            if (reason == 'shortcut-handle') {
                var handleResult = hotbox.dispatch(e);
                if (handleResult) {
                    e.preventDefault();
                } else {
                    minder.dispatchKeyEvent(e);
                }
            }
        });

        fsm.when('modal -> normal', function(exit, enter, reason, e) {
            if (reason == 'import-text-finish') {
                receiver.element.focus();
            }
        });

        this.hotbox = hotbox;
    }

    return module.exports = HotboxRuntime;
});