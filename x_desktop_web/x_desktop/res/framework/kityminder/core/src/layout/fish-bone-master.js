/**
 * @fileOverview
 *
 * 鱼骨图主骨架布局
 *
 * @author: techird
 * @copyright: Baidu FEX, 2014
 */

define(function(require, exports, module) {
    var kity = require('../core/kity');
    var Layout = require('../core/layout');

    Layout.register('fish-bone-master', kity.createClass('FishBoneMasterLayout', {
        base: Layout,

        doLayout: function(parent, children, round) {

            var upPart = [],
                downPart = [];

            var child = children[0];
            var pBox = parent.getContentBox();

            parent.setVertexOut(new kity.Point(pBox.right, pBox.cy));
            parent.setLayoutVectorOut(new kity.Vector(1, 0));

            if (!child) return;

            var cBox = child.getContentBox();
            var pMarginRight = parent.getStyle('margin-right');
            var cMarginLeft = child.getStyle('margin-left');
            var cMarginTop = child.getStyle('margin-top');
            var cMarginBottom = child.getStyle('margin-bottom');

            children.forEach(function(child, index) {
                child.setLayoutTransform(new kity.Matrix());
                var cBox = child.getContentBox();

                if (index % 2) {
                    downPart.push(child);
                    child.setVertexIn(new kity.Point(cBox.left, cBox.top));
                    child.setLayoutVectorIn(new kity.Vector(1, 1));
                }
                else {
                    upPart.push(child);
                    child.setVertexIn(new kity.Point(cBox.left, cBox.bottom));
                    child.setLayoutVectorIn(new kity.Vector(1, -1));
                }

            });

            this.stack(upPart, 'x');
            this.stack(downPart, 'x');

            this.align(upPart, 'bottom');
            this.align(downPart, 'top');

            var xAdjust = pBox.right + pMarginRight + cMarginLeft;
            var yAdjustUp = pBox.cy - cMarginBottom - parent.getStyle('margin-top');
            var yAdjustDown = pBox.cy + cMarginTop + parent.getStyle('margin-bottom');

            this.move(upPart, xAdjust, yAdjustUp);
            this.move(downPart, xAdjust + cMarginLeft, yAdjustDown);
        }
    }));

});