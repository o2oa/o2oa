define(function(require, exports, module) {
    var kity = require('./kity');
    var utils = require('./utils');
    var Minder = require('./minder');

    /**
     * @class MinderNode
     *
     * 表示一个脑图节点
     */
    var MinderNode = kity.createClass('MinderNode', {

        /**
         * 创建一个游离的脑图节点
         *
         * @param {String|Object} textOrData
         *     节点的初始数据或文本
         */
        constructor: function(textOrData) {

            // 指针
            this.parent = null;
            this.root = this;
            this.children = [];

            // 数据
            this.data = {
                id: utils.guid(),
                created: +new Date()
            };

            // 绘图容器
            this.initContainers();

            if (utils.isString(textOrData)) {
                this.setText(textOrData);
            } else if (utils.isObject(textOrData)) {
                utils.extend(this.data, textOrData);
            }
        },

        initContainers: function() {
            this.rc = new kity.Group().setId(utils.uuid('minder_node'));
            this.rc.minderNode = this;
        },

        /**
         * 判断节点是否根节点
         */
        isRoot: function() {
            return this.root === this;
        },

        /**
         * 判断节点是否叶子
         */
        isLeaf: function() {
            return this.children.length === 0;
        },

        /**
         * 获取节点的根节点
         */
        getRoot: function() {
            return this.root || this;
        },

        /**
         * 获得节点的父节点
         */
        getParent: function() {
            return this.parent;
        },

        getSiblings: function() {
            var children = this.parent.children;
            var siblings = [];
            var self = this;
            children.forEach(function(child) {
                if (child != self) siblings.push(child);
            });
            return siblings;
        },

        /**
         * 获得节点的深度
         */
        getLevel: function() {
            var level = 0,
                ancestor = this.parent;
            while (ancestor) {
                level++;
                ancestor = ancestor.parent;
            }
            return level;
        },

        /**
         * 获得节点的复杂度（即子树中节点的数量）
         */
        getComplex: function() {
            var complex = 0;
            this.traverse(function() {
                complex++;
            });
            return complex;
        },

        /**
         * 获得节点的类型（root|main|sub）
         */
        getType: function(type) {
            this.type = ['root', 'main', 'sub'][Math.min(this.getLevel(), 2)];
            return this.type;
        },

        /**
         * 判断当前节点是否被测试节点的祖先
         * @param  {MinderNode}  test 被测试的节点
         */
        isAncestorOf: function(test) {
            var ancestor = test.parent;
            while (ancestor) {
                if (ancestor == this) return true;
                ancestor = ancestor.parent;
            }
            return false;
        },

        getData: function(key) {
            return key ? this.data[key] : this.data;
        },

        setData: function(key, value) {
            if (typeof key == 'object') {
                var data = key;
                for (key in data) if (data.hasOwnProperty(key)) {
                    this.data[key] = data[key];
                }
            }
            else {
                this.data[key] = value;
            }
            return this;
        },

        /**
         * 设置节点的文本数据
         * @param {String} text 文本数据
         */
        setText: function(text) {
            return this.data.text = text;
        },

        /**
         * 获取节点的文本数据
         * @return {String}
         */
        getText: function() {
            return this.data.text || null;
        },

        /**
         * 先序遍历当前节点树
         * @param  {Function} fn 遍历函数
         */
        preTraverse: function(fn, excludeThis) {
            var children = this.getChildren();
            if (!excludeThis) fn(this);
            for (var i = 0; i < children.length; i++) {
                children[i].preTraverse(fn);
            }
        },

        /**
         * 后序遍历当前节点树
         * @param  {Function} fn 遍历函数
         */
        postTraverse: function(fn, excludeThis) {
            var children = this.getChildren();
            for (var i = 0; i < children.length; i++) {
                children[i].postTraverse(fn);
            }
            if (!excludeThis) fn(this);
        },

        traverse: function(fn, excludeThis) {
            return this.postTraverse(fn, excludeThis);
        },

        getChildren: function() {
            return this.children;
        },

        getIndex: function() {
            return this.parent ? this.parent.children.indexOf(this) : -1;
        },

        insertChild: function(node, index) {
            if (index === undefined) {
                index = this.children.length;
            }
            if (node.parent) {
                node.parent.removeChild(node);
            }
            node.parent = this;
            node.root = this.root;

            this.children.splice(index, 0, node);
        },

        appendChild: function(node) {
            return this.insertChild(node);
        },

        prependChild: function(node) {
            return this.insertChild(node, 0);
        },

        removeChild: function(elem) {
            var index = elem,
                removed;
            if (elem instanceof MinderNode) {
                index = this.children.indexOf(elem);
            }
            if (index >= 0) {
                removed = this.children.splice(index, 1)[0];
                removed.parent = null;
                removed.root = removed;
            }
        },

        clearChildren: function() {
            this.children = [];
        },

        getChild: function(index) {
            return this.children[index];
        },

        getRenderContainer: function() {
            return this.rc;
        },

        getCommonAncestor: function(node) {
            return MinderNode.getNodeCommonAncestor(this, node);
        },

        contains: function(node) {
            return this == node || this.isAncestorOf(node);
        },

        clone: function() {
            var cloned = new MinderNode();

            cloned.data = utils.clone(this.data);

            this.children.forEach(function(child) {
                cloned.appendChild(child.clone());
            });

            return cloned;
        },

        compareTo: function(node) {

            if (!utils.comparePlainObject(this.data, node.data)) return false;
            if (!utils.comparePlainObject(this.temp, node.temp)) return false;
            if (this.children.length != node.children.length) return false;

            var i = 0;
            while (this.children[i]) {
                if (!this.children[i].compareTo(node.children[i])) return false;
                i++;
            }

            return true;
        },

        getMinder: function() {
            return this.getRoot().minder;
        }
    });

    MinderNode.getCommonAncestor = function(nodeA, nodeB) {
        if (nodeA instanceof Array) {
            return MinderNode.getCommonAncestor.apply(this, nodeA);
        }
        switch (arguments.length) {
            case 1:
                return nodeA.parent || nodeA;

            case 2:
                if (nodeA.isAncestorOf(nodeB)) {
                    return nodeA;
                }
                if (nodeB.isAncestorOf(nodeA)) {
                    return nodeB;
                }
                var ancestor = nodeA.parent;
                while (ancestor && !ancestor.isAncestorOf(nodeB)) {
                    ancestor = ancestor.parent;
                }
                return ancestor;

            default:
                return Array.prototype.reduce.call(arguments,
                    function(prev, current) {
                        return MinderNode.getCommonAncestor(prev, current);
                    },
                    nodeA
                );
        }
    };

    kity.extendClass(Minder, {

        getRoot: function() {
            return this._root;
        },

        setRoot: function(root) {
            this._root = root;
            root.minder = this;
        },

        getAllNode: function() {
            var nodes = [];
            this.getRoot().traverse(function(node) {
                nodes.push(node);
            });
            return nodes;
        },

        getNodeById: function(id) {
            return this.getNodesById([id])[0];
        },

        getNodesById: function(ids) {
            var nodes = this.getAllNode();
            var result = [];
            nodes.forEach(function(node) {
                if (ids.indexOf(node.getData('id')) != -1) {
                    result.push(node);
                }
            });
            return result;
        },

        createNode: function(textOrData, parent, index) {
            var node = new MinderNode(textOrData);
            this.fire('nodecreate', {
                node: node,
                parent: parent,
                index: index
            });
            this.appendNode(node, parent, index);
            return node;
        },

        appendNode: function(node, parent, index) {
            if (parent) parent.insertChild(node, index);
            this.attachNode(node);
            return this;
        },

        removeNode: function(node) {
            if (node.parent) {
                node.parent.removeChild(node);
                this.detachNode(node);
                this.fire('noderemove', {
                    node: node
                });
            }
        },

        attachNode: function(node) {
            var rc = this.getRenderContainer();
            node.traverse(function(current) {
                current.attached = true;
                rc.addShape(current.getRenderContainer());
            });
            rc.addShape(node.getRenderContainer());
            this.fire('nodeattach', {
                node: node
            });
        },

        detachNode: function(node) {
            var rc = this.getRenderContainer();
            node.traverse(function(current) {
                current.attached = false;
                rc.removeShape(current.getRenderContainer());
            });
            this.fire('nodedetach', {
                node: node
            });
        },

        getMinderTitle: function() {
            return this.getRoot().getText();
        }

    });

    module.exports = MinderNode;
});