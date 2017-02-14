define(function(require, exports, module) {
    var kity = require('../core/kity');
    var utils = require('../core/utils');

    var Minder = require('../core/minder');
    var MinderNode = require('../core/node');
    var Command = require('../core/command');
    var Module = require('../core/module');
    var Renderer = require('../core/render');

    /**
     * @command AppendChildNode
     * @description 添加子节点到选中的节点中
     * @param {string|object} textOrData 要插入的节点的文本或数据
     * @state
     *    0: 当前有选中的节点
     *   -1: 当前没有选中的节点
     */
    var AppendChildCommand = kity.createClass('AppendChildCommand', {
        base: Command,
        execute: function(km, text) {
            var parent = km.getSelectedNode();
            if (!parent) {
                return null;
            }
            var node = km.createNode(text, parent);
            km.select(node, true);
            if (parent.isExpanded()) {
                node.render();
            }
            else {
                parent.expand();
                parent.renderTree();
            }
            km.layout(600);
        },
        queryState: function(km) {
            var selectedNode = km.getSelectedNode();
            return selectedNode ? 0 : -1;
        }
    });

    /**
     * @command AppendSiblingNode
     * @description 添加选中的节点的兄弟节点
     * @param {string|object} textOrData 要添加的节点的文本或数据
     * @state
     *    0: 当前有选中的节点
     *   -1: 当前没有选中的节点
     */
    var AppendSiblingCommand = kity.createClass('AppendSiblingCommand', {
        base: Command,
        execute: function(km, text) {
            var sibling = km.getSelectedNode();
            var parent = sibling.parent;
            if (!parent) {
                return km.execCommand('AppendChildNode', text);
            }
            var node = km.createNode(text, parent, sibling.getIndex() + 1);
            node.setGlobalLayoutTransform(sibling.getGlobalLayoutTransform());
            km.select(node, true);
            node.render();
            km.layout(600);
        },
        queryState: function(km) {
            var selectedNode = km.getSelectedNode();
            return selectedNode ? 0 : -1;
        }
    });

    /**
     * @command RemoveNode
     * @description 移除选中的节点
     * @state
     *    0: 当前有选中的节点
     *   -1: 当前没有选中的节点
     */
    var RemoveNodeCommand = kity.createClass('RemoverNodeCommand', {
        base: Command,
        execute: function(km) {
            var nodes = km.getSelectedNodes();
            var ancestor = MinderNode.getCommonAncestor.apply(null, nodes);
            var index = nodes[0].getIndex();

            nodes.forEach(function(node) {
                if (!node.isRoot()) km.removeNode(node);
            });
            if (nodes.length == 1) {
                var selectBack = ancestor.children[index - 1] || ancestor.children[index];
                km.select(selectBack || ancestor || km.getRoot(), true);
            } else {
                km.select(ancestor || km.getRoot(), true);
            }
            km.layout(600);
        },
        queryState: function(km) {
            var selectedNode = km.getSelectedNode();
            return selectedNode && !selectedNode.isRoot() ? 0 : -1;
        }
    });

    var AppendParentCommand = kity.createClass('AppendParentCommand', {
        base: Command,
        execute: function(km, text) {
            var nodes = km.getSelectedNodes();

            nodes.sort(function(a, b) {
                return a.getIndex() - b.getIndex();
            });
            var parent = nodes[0].parent;

            var newParent = km.createNode(text, parent, nodes[0].getIndex());
            nodes.forEach(function(node) {
                newParent.appendChild(node);
            });
            newParent.setGlobalLayoutTransform(nodes[nodes.length >> 1].getGlobalLayoutTransform());

            km.select(newParent, true);
            km.layout(600);
        },
        queryState: function(km) {
            var nodes = km.getSelectedNodes();
            if (!nodes.length) return -1;
            var parent = nodes[0].parent;
            if (!parent) return -1;
            for (var i = 1; i < nodes.length; i++) {
                if (nodes[i].parent != parent) return -1;
            }
            return 0;
        }
    });

    Module.register('NodeModule', function() {
        return {
            commands: {
                'AppendChildNode': AppendChildCommand,
                'AppendSiblingNode': AppendSiblingCommand,
                'RemoveNode': RemoveNodeCommand,
                'AppendParentNode': AppendParentCommand
            },

            'commandShortcutKeys': {
                'appendsiblingnode': 'normal::Enter',
                'appendchildnode': 'normal::Insert|Tab',
                'appendparentnode': 'normal::Shift+Tab|normal::Shift+Insert',
                'removenode': 'normal::Del|Backspace'
            }
        };
    });
});