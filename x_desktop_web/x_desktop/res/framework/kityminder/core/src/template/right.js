/**
 * @fileOverview
 *
 * 往右布局结构模板
 *
 * @author: techird
 * @copyright: Baidu FEX, 2014
 */
define(function(require, exports, module) {
    var template = require('../core/template');

    template.register('right', {

        getLayout: function(node) {
            return node.getData('layout') || 'right';
        },

        getConnect: function(node) {
            if (node.getLevel() == 1) return 'arc';
            return 'bezier';
        }
    });
});