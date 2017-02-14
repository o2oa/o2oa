define(function(require, exports, module) {
    var kity = require('./kity');
    var utils = require('./utils');
    var Minder = require('./minder');

    /* 已注册的模块 */
    var _modules = {};

    exports.register = function(name, module) {
        _modules[name] = module;
    };

    /* 模块初始化 */
    Minder.registerInitHook(function() {
        this._initModules();
    });

    // 模块声明周期维护
    kity.extendClass(Minder, {
        _initModules: function() {
            var modulesPool = _modules;
            var modulesToLoad = this._options.modules || utils.keys(modulesPool);

            this._commands = {};
            this._query = {};
            this._modules = {};
            this._rendererClasses = {};

            var i, name, type, module, moduleDeals,
                dealCommands, dealEvents, dealRenderers;

            var me = this;
            for (i = 0; i < modulesToLoad.length; i++) {
                name = modulesToLoad[i];

                if (!modulesPool[name]) continue;

                // 执行模块初始化，抛出后续处理对象

                if (typeof(modulesPool[name]) == 'function') {
                    moduleDeals = modulesPool[name].call(me);
                } else {
                    moduleDeals = modulesPool[name];
                }
                this._modules[name] = moduleDeals;

                if (!moduleDeals) continue;

                if (moduleDeals.defaultOptions) {
                    me.setDefaultOptions(moduleDeals.defaultOptions);
                }

                if (moduleDeals.init) {
                    moduleDeals.init.call(me, this._options);
                }

                /**
                 * @Desc: 判断是否支持原生clipboard事件，如果支持，则对pager添加其监听
                 * @Editor: Naixor
                 * @Date: 2015.9.20
                 */
                /**
                 * 由于当前脑图解构问题，clipboard暂时全权交由玩不托管
                 * @Editor: Naixor
                 * @Date: 2015.9.24
                 */
                // if (name === 'ClipboardModule' && this.supportClipboardEvent  && !kity.Browser.gecko) {
                //     var on = function () {
                //         var clipBoardReceiver = this.clipBoardReceiver || document;
                        
                //         if (document.addEventListener) {
                //             clipBoardReceiver.addEventListener.apply(this, arguments);
                //         } else {
                //             arguments[0] = 'on' + arguments[0];
                //             clipBoardReceiver.attachEvent.apply(this, arguments);
                //         }
                //     }
                //     for (var command in moduleDeals.clipBoardEvents) {
                //         on(command, moduleDeals.clipBoardEvents[command]);
                //     }
                // };

                // command加入命令池子
                dealCommands = moduleDeals.commands;
                for (name in dealCommands) {
                    this._commands[name.toLowerCase()] = new dealCommands[name]();
                }

                // 绑定事件
                dealEvents = moduleDeals.events;
                if (dealEvents) {
                    for (type in dealEvents) {
                        me.on(type, dealEvents[type]);
                    }
                }

                // 渲染器
                dealRenderers = moduleDeals.renderers;

                if (dealRenderers) {

                    for (type in dealRenderers) {
                        this._rendererClasses[type] = this._rendererClasses[type] || [];

                        if (utils.isArray(dealRenderers[type])) {
                            this._rendererClasses[type] = this._rendererClasses[type].concat(dealRenderers[type]);
                        } else {
                            this._rendererClasses[type].push(dealRenderers[type]);
                        }
                    }
                }

                //添加模块的快捷键
                if (moduleDeals.commandShortcutKeys) {
                    this.addCommandShortcutKeys(moduleDeals.commandShortcutKeys);
                }
            }
        },

        _garbage: function() {
            this.clearSelect();

            while (this._root.getChildren().length) {
                this._root.removeChild(0);
            }
        },

        destroy: function() {
            var modules = this._modules;

            this._resetEvents();
            this._garbage();

            for (var key in modules) {
                if (!modules[key].destroy) continue;
                modules[key].destroy.call(this);
            }
        },

        reset: function() {
            var modules = this._modules;

            this._garbage();

            for (var key in modules) {
                if (!modules[key].reset) continue;
                modules[key].reset.call(this);
            }
        }
    });
});