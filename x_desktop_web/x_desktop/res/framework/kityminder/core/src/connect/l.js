/**
 * @fileOverview
 *
 * "L" 连线
 *
 * @author: techird
 * @copyright: Baidu FEX, 2014
 */

define(function(require, exports, module) {
    var kity = require('../core/kity');
    var connect = require('../core/connect');

    connect.register('l', function(node, parent, connection) {

        var po = parent.getLayoutVertexOut();
        var pi = node.getLayoutVertexIn();
        var vo = parent.getLayoutVectorOut();

        var pathData = [];
        var r = Math.round,
            abs = Math.abs;

        pathData.push('M', po.round());
        if (abs(vo.x) > abs(vo.y)) {
            pathData.push('H', r(pi.x));
        } else {
            pathData.push('V', pi.y);
        }
        pathData.push('L', pi);

        connection.setPathData(pathData);
    });
});