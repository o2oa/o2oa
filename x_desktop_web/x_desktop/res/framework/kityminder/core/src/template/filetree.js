/**
 * @fileOverview
 *
 * 文件夹模板
 *
 * @author: techird
 * @copyright: Baidu FEX, 2014
 */
define(function(require, exports, module) {
    var template = require('../core/template');

    template.register('filetree', {

        getLayout: function(node) {
            if (node.getData('layout')) return node.getData('layout');
            if (node.isRoot()) return 'bottom';

            return 'filetree-down';
        },

        getConnect: function(node) {
            if (node.getLevel() == 1) {
                return 'poly';
            }
            return 'l';
        }
    });
});