define(function(require, exports, module) {
    var kity = require('./kity');
    var utils = require('./utils');
    var Module = require('./module');
    var Minder = require('./minder');
    var MinderNode = require('./node');

    // 连线提供方
    var _connectProviders = {};

    function register(name, provider) {
        _connectProviders[name] = provider;
    }

    register('default', function(node, parent, connection) {
        connection.setPathData([
            'M', parent.getLayoutVertexOut(),
            'L', node.getLayoutVertexIn()
        ]);
    });

    kity.extendClass(MinderNode, {
        /**
         * @private
         * @method getConnect()
         * @for MinderNode
         * @description 获取当前节点的连线类型
         *
         * @grammar getConnect() => {string}
         */
        getConnect: function() {
            return this.data.connect || 'default';
        },

        getConnectProvider: function() {
            return _connectProviders[this.getConnect()] || _connectProviders['default'];
        },

        /**
         * @private
         * @method getConnection()
         * @for MinderNode
         * @description 获取当前节点的连线对象
         *
         * @grammar getConnection() => {kity.Path}
         */
        getConnection: function() {
            return this._connection || null;
        }
    });

    kity.extendClass(Minder, {

        getConnectContainer: function() {
            return this._connectContainer;
        },

        createConnect: function(node) {
            if (node.isRoot()) return;

            var connection = new kity.Path();

            node._connection = connection;

            this._connectContainer.addShape(connection);
            this.updateConnect(node);
        },

        removeConnect: function(node) {
            var me = this;
            node.traverse(function(node) {
                me._connectContainer.removeShape(node._connection);
                node._connection = null;
            });
        },

        updateConnect: function(node) {

            var connection = node._connection;
            var parent = node.parent;

            if (!parent || !connection) return;

            if (parent.isCollapsed()) {
                connection.setVisible(false);
                return;
            }
            connection.setVisible(true);

            var provider = node.getConnectProvider();

            var strokeColor = node.getStyle('connect-color') || 'white',
                strokeWidth = node.getStyle('connect-width') || 2;

            connection.stroke(strokeColor, strokeWidth);

            provider(node, parent, connection, strokeWidth, strokeColor);

            if (strokeWidth % 2 === 0) {
                connection.setTranslate(0.5, 0.5);
            } else {
                connection.setTranslate(0, 0);
            }
        }
    });

    Module.register('Connect', {
        init: function() {
            this._connectContainer = new kity.Group().setId(utils.uuid('minder_connect_group'));
            this.getRenderContainer().prependShape(this._connectContainer);
        },
        events: {
            'nodeattach': function(e) {
                this.createConnect(e.node);
            },
            'nodedetach': function(e) {
                this.removeConnect(e.node);
            },
            'layoutapply layoutfinish noderender': function(e) {
                this.updateConnect(e.node);
            }
        }
    });

    exports.register = register;
});