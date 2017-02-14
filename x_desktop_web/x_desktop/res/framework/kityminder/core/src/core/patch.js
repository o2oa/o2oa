/**
 * @fileOverview
 *
 * 打补丁
 *
 * @author: techird
 * @copyright: Baidu FEX, 2014
 */
define(function(require, exports, module) {
    var kity = require('./kity');
    var Minder = require('./minder');

    function insertNode(minder, info, parent, index) {
        parent = minder.createNode(info.data, parent, index);
        info.children.forEach(function(childInfo, index) {
            insertNode(minder, childInfo, parent, index);
        });
        return parent;
    }

    function applyPatch(minder, patch) {
        // patch.op - 操作，包括 remove, add, replace
        // patch.path - 路径，如 '/root/children/1/data'
        // patch.value - 数据，如 { text: "思路" }
        var path = patch.path.split('/');
        path.shift();

        var changed = path.shift();
        var node;

        if (changed == 'root') {

            var dataIndex = path.indexOf('data');
            if (dataIndex > -1) {
                changed = 'data';
                var dataPath = path.splice(dataIndex + 1);
                patch.dataPath = dataPath;
            } else {
                changed = 'node';
            }

            node = minder.getRoot();
            var segment, index;
            while (segment = path.shift()) {
                if (segment == 'children') continue;
                if (typeof index != 'undefined') node = node.getChild(index);
                index = +segment;
            }
            patch.index = index;
            patch.node = node;
        }

        var express = patch.express = [changed, patch.op].join('.');

        switch (express) {
            case 'theme.replace':
                minder.useTheme(patch.value);
                break;
            case 'template.replace':
                minder.useTemplate(patch.value);
                break;
            case 'node.add':
                insertNode(minder, patch.value, patch.node, patch.index).renderTree();
                minder.layout();
                break;
            case 'node.remove':
                minder.removeNode(patch.node.getChild(patch.index));
                minder.layout();
                break;
            case 'data.add':
            case 'data.replace':
            case 'data.remove':
                var data = patch.node.data;
                var field;
                path = patch.dataPath.slice();
                while (data && path.length > 1) {
                    field = path.shift();
                    if (field in data) {
                        data = data[field];
                    } else if (patch.op != 'remove') {
                        data = data[field] = {};
                    }
                }
                if (data) {
                    field = path.shift();
                    data[field] = patch.value;
                }
                if (field == 'expandState') {
                    node.renderTree();
                } else {
                    node.render();
                }
                minder.layout();
        }

        minder.fire('patch', { 'patch' : patch } );
    }

    kity.extendClass(Minder, {
        applyPatches: function(patches) {
            for (var i = 0; i < patches.length; i++) {
                applyPatch(this, patches[i]);
            }

            this.fire('contentchange');
            return this;
        }
    });

});