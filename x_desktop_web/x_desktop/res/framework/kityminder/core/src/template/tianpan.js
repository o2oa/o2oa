/**
 * @fileOverview
 *
 * 天盘模板
 *
 * @author: along
 * @copyright: bpd729@163.com, 2015
 */
define(function(require, exports, module) {
    var template = require('../core/template');

    template.register('tianpan', {
        getLayout: function (node) {
            if (node.getData('layout')) return node.getData('layout');
            var level = node.getLevel();

            // 根节点
            if (level === 0) {
                return 'tianpan';
            }

            return node.parent.getLayout();
        },

        getConnect: function (node) {
            return 'arc_tp';
        }
    });
});