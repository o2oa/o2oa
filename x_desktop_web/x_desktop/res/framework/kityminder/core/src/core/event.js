define(function(require, exports, module) {
    var kity = require('./kity');
    var utils = require('./utils');
    var Minder = require('./minder');

    /**
     * @class MinderEvent
     * @description 表示一个脑图中发生的事件
     */
    var MinderEvent = kity.createClass('MindEvent', {
        constructor: function(type, params, canstop) {
            params = params || {};
            if (params.getType && params.getType() == 'ShapeEvent') {

                /**
                 * @property kityEvent
                 * @for MinderEvent
                 * @description 如果事件是从一个 kity 的事件派生的，会有 kityEvent 属性指向原来的 kity 事件
                 * @type {KityEvent}
                 */
                this.kityEvent = params;

                /**
                 * @property originEvent
                 * @for MinderEvent
                 * @description 如果事件是从原声 Dom 事件派生的（如 click、mousemove 等），会有 originEvent 指向原来的 Dom 事件
                 * @type {DomEvent}
                 */
                this.originEvent = params.originEvent;

            } else if (params.target && params.preventDefault) {
                this.originEvent = params;
            } else {
                kity.Utils.extend(this, params);
            }

            /**
             * @property type
             * @for MinderEvent
             * @description 事件的类型，如 `click`、`contentchange` 等
             * @type {string}
             */
            this.type = type;
            this._canstop = canstop || false;
        },

        /**
         * @method getPosition()
         * @for MinderEvent
         * @description 如果事件是从一个 kity 事件派生的，会有 `getPosition()` 获取事件发生的坐标
         *
         * @grammar getPosition(refer) => {kity.Point}
         *
         * @param {string|kity.Shape} refer
         *     参照的坐标系，
         *     `"screen"` - 以浏览器屏幕为参照坐标系
         *     `"minder"` - （默认）以脑图画布为参照坐标系
         *     `{kity.Shape}` - 指定以某个 kity 图形为参照坐标系
         */
        getPosition: function(refer) {
            if (!this.kityEvent) return;
            if (!refer || refer == 'minder') {
                return this.kityEvent.getPosition(this.minder.getRenderContainer());
            }
            return this.kityEvent.getPosition.call(this.kityEvent, refer);
        },

        /**
         * @method getTargetNode()
         * @for MinderEvent
         * @description 当发生的事件是鼠标事件时，获取事件位置命中的脑图节点
         *
         * @grammar getTargetNode() => {MinderNode}
         */
        getTargetNode: function() {
            var findShape = this.kityEvent && this.kityEvent.targetShape;
            if (!findShape) return null;
            while (!findShape.minderNode && findShape.container) {
                findShape = findShape.container;
            }
            var node = findShape.minderNode;
            if (node && findShape.getOpacity() < 1) return null;
            return node || null;
        },

        /**
         * @method stopPropagation()
         * @for MinderEvent
         * @description 当发生的事件是鼠标事件时，获取事件位置命中的脑图节点
         *
         * @grammar getTargetNode() => {MinderNode}
         */
        stopPropagation: function() {
            this._stoped = true;
        },

        stopPropagationImmediately: function() {
            this._immediatelyStoped = true;
            this._stoped = true;
        },

        shouldStopPropagation: function() {
            return this._canstop && this._stoped;
        },

        shouldStopPropagationImmediately: function() {
            return this._canstop && this._immediatelyStoped;
        },
        preventDefault: function() {
            this.originEvent.preventDefault();
        },
        isRightMB: function() {
            var isRightMB = false;
            if (!this.originEvent) {
                return false;
            }
            if ('which' in this.originEvent)
                isRightMB = this.originEvent.which == 3;
            else if ('button' in this.originEvent)
                isRightMB = this.originEvent.button == 2;
            return isRightMB;
        },
        getKeyCode: function() {
            var evt = this.originEvent;
            return evt.keyCode || evt.which;
        }
    });

    Minder.registerInitHook(function(option) {
        this._initEvents();
    });

    kity.extendClass(Minder, {

        _initEvents: function() {
            this._eventCallbacks = {};
        },

        _resetEvents: function() {
            this._initEvents();
            this._bindEvents();
        },

        _bindEvents: function() {
            /* jscs:disable maximumLineLength */
            this._paper.on('click dblclick mousedown contextmenu mouseup mousemove mouseover mousewheel DOMMouseScroll touchstart touchmove touchend dragenter dragleave drop', this._firePharse.bind(this));
            if (window) {
                window.addEventListener('resize', this._firePharse.bind(this));
            }
        },

        /**
         * @method dispatchKeyEvent
         * @description 派发键盘（相关）事件到脑图实例上，让实例的模块处理
         * @grammar dispatchKeyEvent(e) => {this}
         * @param  {Event} e 原生的 Dom 事件对象
         */
        dispatchKeyEvent: function(e) {
            this._firePharse(e);
        },

        _firePharse: function(e) {
            var beforeEvent, preEvent, executeEvent;

            if (e.type == 'DOMMouseScroll') {
                e.type = 'mousewheel';
                e.wheelDelta = e.originEvent.wheelDelta = e.originEvent.detail * -10;
                e.wheelDeltaX = e.originEvent.mozMovementX;
                e.wheelDeltaY = e.originEvent.mozMovementY;
            }

            beforeEvent = new MinderEvent('before' + e.type, e, true);
            if (this._fire(beforeEvent)) {
                return;
            }
            preEvent = new MinderEvent('pre' + e.type, e, true);
            executeEvent = new MinderEvent(e.type, e, true);

            if (this._fire(preEvent) ||
                this._fire(executeEvent))
                this._fire(new MinderEvent('after' + e.type, e, false));
        },

        _interactChange: function(e) {
            var me = this;
            if (me._interactScheduled) return;
            setTimeout(function() {
                me._fire(new MinderEvent('interactchange'));
                me._interactScheduled = false;
            }, 100);
            me._interactScheduled = true;
        },

        _listen: function(type, callback) {
            var callbacks = this._eventCallbacks[type] || (this._eventCallbacks[type] = []);
            callbacks.push(callback);
        },

        _fire: function(e) {
            /**
             * @property minder
             * @description 产生事件的 Minder 对象
             * @for MinderShape
             * @type {Minder}
             */
            e.minder = this;

            var status = this.getStatus();
            var callbacks = this._eventCallbacks[e.type.toLowerCase()] || [];

            if (status) {
                callbacks = callbacks.concat(this._eventCallbacks[status + '.' + e.type.toLowerCase()] || []);
            }

            if (callbacks.length === 0) {
                return;
            }

            var lastStatus = this.getStatus();
            for (var i = 0; i < callbacks.length; i++) {
                callbacks[i].call(this, e);

                /* this.getStatus() != lastStatus ||*/
                if (e.shouldStopPropagationImmediately()) {
                    break;
                }
            }

            return e.shouldStopPropagation();
        },

        on: function(name, callback) {
            var km = this;
            name.split(/\s+/).forEach(function(n) {
                km._listen(n.toLowerCase(), callback);
            });
            return this;
        },

        off: function(name, callback) {

            var types = name.split(/\s+/);
            var i, j, callbacks, removeIndex;
            for (i = 0; i < types.length; i++) {

                callbacks = this._eventCallbacks[types[i].toLowerCase()];
                if (callbacks) {
                    removeIndex = null;
                    for (j = 0; j < callbacks.length; j++) {
                        if (callbacks[j] == callback) {
                            removeIndex = j;
                        }
                    }
                    if (removeIndex !== null) {
                        callbacks.splice(removeIndex, 1);
                    }
                }
            }
        },

        fire: function(type, params) {
            var e = new MinderEvent(type, params);
            this._fire(e);
            return this;
        }
    });

    module.exports = MinderEvent;
});
