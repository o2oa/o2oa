/**
 * @fileOverview
 *
 * 组织结构图模板
 *
 * @author: techird
 * @copyright: Baidu FEX, 2014
 */
define(function(require, exports, module) {
    var template = require('../core/template');

    template.register('structure', {

        getLayout: function(node) {
            return node.getData('layout') || 'bottom';
        },

        getConnect: function(node) {
            return 'poly';
        }
    });
});