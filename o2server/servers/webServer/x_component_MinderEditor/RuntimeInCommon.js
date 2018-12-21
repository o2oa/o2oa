
// 用于拖拽节点时屏蔽键盘事件
MWF.xApplication.MinderEditor.Drag = new Class({
    initialize : function( editor ){
        this.editor = editor;
        this.fsm = editor.fsm;
        this.minder = editor.minder;
        this.popmenu = editor.popmenu;
        this.receiver =  editor.receiver;
        this.receiverElement = this.receiver.element;
        this.setupFsm();

        var downX, downY;
        var MOUSE_HAS_DOWN = 0;
        var MOUSE_HAS_UP = 1;
        var BOUND_CHECK = 20;
        var flag = MOUSE_HAS_UP;
        var maxX, maxY, osx, osy, containerY;
        var freeHorizen = this.freeHorizen = false;
        var freeVirtical = this.freeVirtical = false;
        this.frame = null;

        this.minder.on('mousedown', function(e) {
            flag = MOUSE_HAS_DOWN;
            var rect = this.minder.getPaper().container.getBoundingClientRect();
            downX = e.originEvent.clientX;
            downY = e.originEvent.clientY;
            containerY = rect.top;
            maxX = rect.width;
            maxY = rect.height;
        }.bind(this));

        this.minder.on('mousemove', function(e) {
            if ( this.fsm.state() === 'drag' && flag == MOUSE_HAS_DOWN && this.minder.getSelectedNode()
                && (Math.abs(downX - e.originEvent.clientX) > BOUND_CHECK
                || Math.abs(downY - e.originEvent.clientY) > BOUND_CHECK)) {
                osx = e.originEvent.clientX;
                osy = e.originEvent.clientY - containerY;

                if (osx < BOUND_CHECK) {
                    this.move('right', BOUND_CHECK - osx);
                } else if (osx > maxX - BOUND_CHECK) {
                    this.move('left', BOUND_CHECK + osx - maxX);
                } else {
                    freeHorizen = true;
                }
                if (osy < BOUND_CHECK) {
                    this.move('bottom', osy);
                } else if (osy > maxY - BOUND_CHECK) {
                    this.move('top', BOUND_CHECK + osy - maxY);
                } else {
                    freeVirtical = true;
                }
                if (freeHorizen && freeVirtical) {
                    this.move(false);
                }
            }
            if (this.fsm.state() !== 'drag'
                && flag === MOUSE_HAS_DOWN
                && this.minder.getSelectedNode()
                && (Math.abs(downX - e.originEvent.clientX) > BOUND_CHECK
                || Math.abs(downY - e.originEvent.clientY) > BOUND_CHECK)) {

                if (this.fsm.state() === 'popmenu') {
                    popmenu.active(Popmenu.STATE_IDLE);
                }

                return this.fsm.jump('drag', 'user-drag');
            }
        }.bind(this));

        window.addEventListener('mouseup', function () {
            flag = MOUSE_HAS_UP;
            if (this.fsm.state() === 'drag') {
                this.move(false);
                return this.fsm.jump('normal', 'drag-finish');
            }
        }.bind(this), false);
    },
    setupFsm: function(){

        // when jumped to drag mode, enter
        this.fsm.when('* -> drag', function() {
            // now is drag mode
        });

        this.fsm.when('drag -> *', function(exit, enter, reason) {
            if (reason == 'drag-finish') {
                // now exit drag mode
            }
        });
    },
    move: function(direction, speed) {
        if (!direction) {
            this.freeHorizen = this.freeVirtical = false;
            this.frame && kity.releaseFrame(this.frame);
            this.frame = null;
            return;
        }
        if (!this.frame) {
            this.frame = kity.requestFrame((function (direction, speed, minder) {
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
            })(direction, speed, this.minder));
        }
    }
});

MWF.xApplication.MinderEditor.FSM = new Class({
    initialize: function( defaultState ){
        this.currentState = defaultState;
        this.BEFORE_ARROW = ' - ';
        this.AFTER_ARROW = ' -> ';
        this.handlers = [];
        this.debug = new MWF.xApplication.MinderEditor.Debug('fsm');
    },
    /**
     * 状态跳转
     *
     * 会通知所有的状态跳转监视器
     *
     * @param  {string} newState  新状态名称
     * @param  {any} reason 跳转的原因，可以作为参数传递给跳转监视器
     */
    jump: function(newState, reason) {
        if (!reason) throw new Error('Please tell fsm the reason to jump');

        var oldState = this.currentState;
        var notify = [oldState, newState].concat([].slice.call(arguments, 1));
        var i, handler;

        // 跳转前
        for (i = 0; i < this.handlers.length; i++) {
            handler = this.handlers[i];
            if (this.handlerConditionMatch(handler.condition, 'before', oldState, newState)) {
                if (handler.apply(null, notify)) return;
            }
        }

        this.currentState = newState;
        this.debug.log('[{0}] {1} -> {2}', reason, oldState, newState);

        // 跳转后
        for (i = 0; i < this.handlers.length; i++) {
            handler = this.handlers[i];
            if (this.handlerConditionMatch(handler.condition, 'after', oldState, newState)) {
                handler.apply(null, notify);
            }
        }
        return this.currentState;
    },
    /**
     * 返回当前状态
     * @return {string}
     */
    state : function() {
        return this.currentState;
    },
    /**
     * 添加状态跳转监视器
     *
     * @param {string} condition
     *     监视的时机
     *         "* => *" （默认）
     *
     * @param  {Function} handler
     *     监视函数，当状态跳转的时候，会接收三个参数
     *         * from - 跳转前的状态
     *         * to - 跳转后的状态
     *         * reason - 跳转的原因
     */
    when : function(condition, handler) {
        this.debug.log('[{0}] {1} ', condition, handler );
        if (arguments.length == 1) {
            handler = condition;
            condition = '* -> *';
        }

        var when, resolved, exit, enter;

        resolved = condition.split(this.BEFORE_ARROW);
        if (resolved.length == 2) {
            when = 'before';
        } else {
            resolved = condition.split(this.AFTER_ARROW);
            if (resolved.length == 2) {
                when = 'after';
            }
        }
        if (!when) throw new Error('Illegal fsm condition: ' + condition);

        exit = resolved[0];
        enter = resolved[1];

        handler.condition = {
            when: when,
            exit: exit,
            enter: enter
        };

        this.handlers.push(handler);
    },
    handlerConditionMatch: function (condition, when, exit, enter) {
        if (condition.when != when) return false;
        if (condition.enter != '*' && condition.enter != enter) return false;
        if (condition.exit != '*' && condition.exit != exit) return;
        return true;
    }
});

//键盘事件接收/分发器
MWF.xApplication.MinderEditor.Receiver = new Class({
    initialize: function( editor ){
        this.editor = editor;

        this.minder = editor.minder;
        this.fsm = editor.fsm;
        this.key = editor.key;

        // 接收事件的 div
        var element = this.element = document.createElement('div');
        element.contentEditable = true;
        /**
         * @Desc: 增加tabindex属性使得element的contenteditable不管是trur还是false都能有focus和blur事件
         * @Editor: Naixor
         * @Date: 2015.09.14
         */
        element.setAttribute("tabindex", -1);
        element.classList.add('receiver');
        element.onkeydown = element.onkeypress = element.onkeyup = this.dispatchKeyEvent.bind(this);
        this.editor.contentNode.appendChild(element);

        this.selectAll();

        this.minder.on('beforemousedown', this.selectAll.bind(this));
        this.minder.on('receiverfocus', this.selectAll.bind(this));
        this.minder.on('readonly', function() {
            // 屏蔽minder的事件接受，删除receiver和popmenu
            this.minder.disable();
            this.element.parentElement.removeChild(this.element);
            //this.editor.popmenu.$container.removeChild(editor.popmenu.$element);
        }.bind(this));

        // 侦听器，接收到的事件会派发给所有侦听器
        this.listeners = [];

    },
    selectAll: function() {
        // 保证有被选中的
        if (!this.element.innerHTML) this.element.innerHTML = '&nbsp;';
        var range = document.createRange();
        var selection = window.getSelection();
        range.selectNodeContents(this.element);
        selection.removeAllRanges();
        selection.addRange(range);
        this.element.focus();


    },
    /**
     * @Desc: 增加enable和disable方法用于解决热核态的输入法屏蔽问题
     * @Editor: Naixor
     * @Date: 2015.09.14
     */
    enable: function() {
        this.element.setAttribute("contenteditable", true);
    },
    disable: function() {
        this.element.setAttribute("contenteditable", false);
    },
    /**
     * @Desc: hack FF下div contenteditable的光标丢失BUG
     * @Editor: Naixor
     * @Date: 2015.10.15
     */
    fixFFCaretDisappeared: function() {
        this.element.removeAttribute("contenteditable");
        this.element.setAttribute("contenteditable", "true");
        this.element.blur();
        this.element.focus();
    },
    /**
     * 以此事件代替通过mouse事件来判断receiver丢失焦点的事件
     * @editor Naixor
     * @Date 2015-12-2
     */
    onblur: function (handler) {
        this.element.onblur = handler;
    },
    // 侦听指定状态下的事件，如果不传 state，侦听所有状态
    listen : function(state, listener) {
        if (arguments.length == 1) {
            listener = state;
            state = '*';
        }
        listener.notifyState = state;
        this.listeners.push(listener);
    },
    dispatchKeyEvent: function (e) {
        var _self = this;
        e.is = function(keyExpression) {
            var subs = keyExpression.split('|');
            for (var i = 0; i < subs.length; i++) {
                if (_self.key.is(this, subs[i])) return true;
            }
            return false;
        };
        var listener, jumpState;
        for (var i = 0; i < this.listeners.length; i++) {

            listener = this.listeners[i];
            // 忽略不在侦听状态的侦听器
            if (listener.notifyState != '*' && listener.notifyState != this.fsm.state()) {
                continue;
            }

            /**
             *
             * 对于所有的侦听器，只允许一种处理方式：跳转状态。
             * 如果侦听器确定要跳转，则返回要跳转的状态。
             * 每个事件只允许一个侦听器进行状态跳转
             * 跳转动作由侦听器自行完成（因为可能需要在跳转时传递 reason），返回跳转结果即可。
             * 比如：
             *
             * ```js
             *  receiver.listen('normal', function(e) {
                 *      if (isSomeReasonForJumpState(e)) {
                 *          return fsm.jump('newstate', e);
                 *      }
                 *  });
             * ```
             */
            if ( listener.call(null, e)) {
                return;
            }
        }
    }
});