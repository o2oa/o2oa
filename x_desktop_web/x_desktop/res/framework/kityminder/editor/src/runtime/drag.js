/**
 * @fileOverview
 *
 * 用于拖拽节点时屏蔽键盘事件
 *
 * @author: techird
 * @copyright: Baidu FEX, 2014
 */
define(function(require, exports, module) {

    var Hotbox = require('../hotbox');
    var Debug = require('../tool/debug');
    var debug = new Debug('drag');

    function DragRuntime() {
        var fsm = this.fsm;
        var minder = this.minder;
        var hotbox = this.hotbox;
        var receiver = this.receiver;
        var receiverElement = receiver.element;

        // setup everything to go
        setupFsm();

        // listen the fsm changes, make action.
        function setupFsm() {

            // when jumped to drag mode, enter
            fsm.when('* -> drag', function() {
                // now is drag mode
            });

            fsm.when('drag -> *', function(exit, enter, reason) {
                if (reason == 'drag-finish') {
                    // now exit drag mode
                }
            });
        }

        var downX, downY;
        var MOUSE_HAS_DOWN = 0;
        var MOUSE_HAS_UP = 1;
        var flag = MOUSE_HAS_UP;
        var maxX, maxY, osx, osy;
        var freeHorizen = false, freeVirtical = false;
        var frame;

        function move(direction, speed) {
            if (!direction) {
                freeHorizen = freeVirtical = false;
                frame && kity.releaseFrame(frame);
                frame = null;    
                return;
            }
            if (!frame) {
                frame = kity.requestFrame((function (direction, speed, minder) {
                    return function (frame) {
                        switch (direction) {
                            case 'left':
                                minder._viewDragger.move({x: -speed, y: 0}, 0);
                                break;
                            case 'top':
                                minder._viewDragger.move({x: 0, y: -speed}, 0);
                                break;
                            case 'right':
                                minder._viewDragger.move({x: speed, y: 0}, 0);
                                break;
                            case 'bottom':
                                minder._viewDragger.move({x: 0, y: speed}, 0);
                                break;
                            default:
                                return;
                        }
                        frame.next();
                    };
                })(direction, speed, minder));
            }
        }

        minder.on('mousedown', function(e) {
            flag = MOUSE_HAS_DOWN;
            downX = e.originEvent.clientX;
            downY = e.originEvent.clientY;
            maxX = minder.getPaper().container.clientWidth;
            maxY = minder.getPaper().container.clientHeight;
        });

        minder.on('mousemove', function(e) {
            if (fsm.state() === 'drag' && flag == MOUSE_HAS_DOWN && minder.getSelectedNode()
                && (Math.abs(downX - e.originEvent.clientX) > 10
                    || Math.abs(downY - e.originEvent.clientY) > 10)) {
                osx = e.originEvent.offsetX;
                osy = e.originEvent.offsetY;
                if (osx < 10) {
                    move('right', 10 - osx);
                } else if (osx > maxX - 10) {
                    move('left', 10 + osx - maxX);
                } else {
                    freeHorizen = true;
                }
                if (osy < 10) {
                    move('bottom', osy);
                } else if (osy > maxY - 10) {
                    move('top', 10 + osy - maxY);
                } else {
                    freeVirtical = true;
                }
                if (freeHorizen && freeVirtical) {
                    move(false);
                }
            }
            if (fsm.state() != 'drag'
                && flag == MOUSE_HAS_DOWN
                && minder.getSelectedNode()
                && (Math.abs(downX - e.originEvent.clientX) > 10
                || Math.abs(downY - e.originEvent.clientY) > 10)) {

                if (fsm.state() == 'hotbox') {
                    hotbox.active(Hotbox.STATE_IDLE);
                }

                return fsm.jump('drag', 'user-drag');
            }
        });

        document.body.onmouseup = function(e) {
            flag = MOUSE_HAS_UP;
            if (fsm.state() == 'drag') {
                move(false);
                return fsm.jump('normal', 'drag-finish');
            }
        };

    }

    return module.exports = DragRuntime;
});
