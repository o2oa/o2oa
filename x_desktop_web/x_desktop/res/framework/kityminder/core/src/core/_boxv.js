/**
 * @fileOverview
 *
 * 调试工具：为 kity.Box 提供一个可视化的渲染
 *
 * @author: techird
 * @copyright: Baidu FEX, 2014
 */

define(function(require, exports, module) {
    var kity = require('./kity');
    var Minder = require('./minder');

    if (location.href.indexOf('boxv') != -1) {

        var vrect;

        Object.defineProperty(kity.Box.prototype, 'visualization', {
            get: function() {
                if (!vrect) return null;
                return vrect.setBox(this);
            }
        });

        Minder.registerInitHook(function() {
            this.on('paperrender', function() {
                vrect = new kity.Rect();
                vrect.fill('rgba(200, 200, 200, .5)');
                vrect.stroke('orange');
                this.getRenderContainer().addShape(vrect);
            });
        });
    }
});