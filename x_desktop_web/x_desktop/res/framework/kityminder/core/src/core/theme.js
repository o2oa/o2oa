define(function(require, exports, module) {
    var kity = require('./kity');
    var utils = require('./utils');
    var Minder = require('./minder');
    var MinderNode = require('./node');
    var Module = require('./module');
    var Command = require('./command');

    var cssLikeValueMatcher = {
        left: function(value) {
            return 3 in value && value[3] ||
                1 in value && value[1] ||
                value[0];
        },
        right: function(value) {
            return 1 in value && value[1] || value[0];
        },
        top: function(value) {
            return value[0];
        },
        bottom: function(value) {
            return 2 in value && value[2] || value[0];
        }
    };

    var _themes = {};

    /**
     * 注册一个主题
     *
     * @param  {String} name  主题的名称
     * @param  {Plain} theme 主题的样式描述
     *
     * @example
     *     Minder.registerTheme('default', {
     *         'root-color': 'red',
     *         'root-stroke': 'none',
     *         'root-padding': [10, 20]
     *     });
     */
    function register(name, theme) {
        _themes[name] = theme;
    }
    exports.register = register;

    utils.extend(Minder, {
        getThemeList: function() {
            return _themes;
        }
    });

    kity.extendClass(Minder, {

        /**
         * 切换脑图实例上的主题
         * @param  {String} name 要使用的主题的名称
         */
        useTheme: function(name) {

            this.setTheme(name);
            this.refresh(800);

            return true;
        },

        setTheme: function(name) {
            if (name && !_themes[name]) throw new Error('Theme ' + name + ' not exists!');
            var lastTheme = this._theme;
            this._theme = name || null;
            var container = this.getRenderTarget();
            if (container) {
                container.classList.remove('km-theme-' + lastTheme);
                if (name) {
                    container.classList.add('km-theme-' + name);
                }
                container.style.background = this.getStyle('background');
            }
            this.fire('themechange', {
                theme: name
            });
            return this;
        },

        /**
         * 获取脑图实例上的当前主题
         * @return {[type]} [description]
         */
        getTheme: function(node) {
            return this._theme || this.getOption('defaultTheme') || 'fresh-blue';
        },

        getThemeItems: function(node) {
            var theme = this.getTheme(node);
            return _themes[this.getTheme(node)];
        },

        /**
         * 获得脑图实例上的样式
         * @param  {String} item 样式名称
         */
        getStyle: function(item, node) {
            var items = this.getThemeItems(node);
            var segment, dir, selector, value, matcher;

            if (item in items) return items[item];

            // 尝试匹配 CSS 数组形式的值
            // 比如 item 为 'pading-left'
            // theme 里有 {'padding': [10, 20]} 的定义，则可以返回 20
            segment = item.split('-');
            if (segment.length < 2) return null;

            dir = segment.pop();
            item = segment.join('-');

            if (item in items) {
                value = items[item];
                if (utils.isArray(value) && (matcher = cssLikeValueMatcher[dir])) {
                    return matcher(value);
                }
                if (!isNaN(value)) return value;
            }

            return null;
        },

        /**
         * 获取指定节点的样式
         * @param  {String} name 样式名称，可以不加节点类型的前缀
         */
        getNodeStyle: function(node, name) {
            var value = this.getStyle(node.getType() + '-' + name, node);
            return value !== null ? value : this.getStyle(name, node);
        }
    });

    kity.extendClass(MinderNode, {
        getStyle: function(name) {
            return this.getMinder().getNodeStyle(this, name);
        }
    });

    Module.register('Theme', {
        defaultOptions: {
            defaultTheme: 'fresh-blue'
        },
        commands: {
            /**
             * @command Theme
             * @description 设置当前脑图的主题
             * @param {string} name 主题名称
             *    允许使用的主题可以使用 `kityminder.Minder.getThemeList()` 查询
             * @state
             *   0: 始终可用
             * @return 返回当前的主题名称
             */
            'theme': kity.createClass('ThemeCommand', {
                base: Command,

                execute: function(km, name) {
                    return km.useTheme(name);
                },

                queryValue: function(km) {
                    return km.getTheme() || 'default';
                }
            })
        }
    });

    Minder.registerInitHook(function() {
        this.setTheme();
    });

});