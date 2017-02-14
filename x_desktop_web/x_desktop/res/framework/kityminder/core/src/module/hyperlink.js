define(function(require, exports, module) {
    var kity = require('../core/kity');
    var utils = require('../core/utils');

    var Minder = require('../core/minder');
    var MinderNode = require('../core/node');
    var Command = require('../core/command');
    var Module = require('../core/module');
    var Renderer = require('../core/render');

    // jscs:disable maximumLineLength
    var linkShapePath = 'M16.614,10.224h-1.278c-1.668,0-3.07-1.07-3.599-2.556h4.877c0.707,0,1.278-0.571,1.278-1.278V3.834 c0-0.707-0.571-1.278-1.278-1.278h-4.877C12.266,1.071,13.668,0,15.336,0h1.278c2.116,0,3.834,1.716,3.834,3.834V6.39 C20.448,8.508,18.73,10.224,16.614,10.224z M5.112,5.112c0-0.707,0.573-1.278,1.278-1.278h7.668c0.707,0,1.278,0.571,1.278,1.278 S14.765,6.39,14.058,6.39H6.39C5.685,6.39,5.112,5.819,5.112,5.112z M2.556,3.834V6.39c0,0.707,0.573,1.278,1.278,1.278h4.877 c-0.528,1.486-1.932,2.556-3.599,2.556H3.834C1.716,10.224,0,8.508,0,6.39V3.834C0,1.716,1.716,0,3.834,0h1.278 c1.667,0,3.071,1.071,3.599,2.556H3.834C3.129,2.556,2.556,3.127,2.556,3.834z';

    Module.register('hyperlink',{
        'commands': {

            /**
             * @command HyperLink
             * @description 为选中的节点添加超链接
             * @param {string} url 超链接的 URL，设置为 null 移除
             * @param {string} title 超链接的说明
             * @state
             *   0: 当前有选中的节点
             *  -1: 当前没有选中的节点
             * @return 返回首个选中节点的超链接信息，JSON 对象： `{url: url, title: title}`
             */
            'hyperlink': kity.createClass('hyperlink', {
                base: Command,

                execute: function(km, url, title) {
                    var nodes = km.getSelectedNodes();
                    nodes.forEach(function(n) {
                        n.setData('hyperlink', url);
                        n.setData('hyperlinkTitle', url && title);
                        n.render();
                    });
                    km.layout();
                },
                queryState: function(km) {
                    var nodes = km.getSelectedNodes(),
                        result = 0;
                    if (nodes.length === 0) {
                        return -1;
                    }
                    nodes.forEach(function(n) {
                        if (n && n.getData('hyperlink')) {
                            result = 0;
                            return false;
                        }
                    });
                    return result;
                },
                queryValue: function(km) {
                    var node = km.getSelectedNode();
                    return {
                        url: node.getData('hyperlink'),
                        title: node.getData('hyperlinkTitle')
                    };
                }
            })
        },
        'renderers': {
            right: kity.createClass('hyperlinkrender', {
                base: Renderer,

                create: function() {

                    var link = new kity.HyperLink();
                    var linkshape = new kity.Path();
                    var outline = new kity.Rect(24, 22, -2, -6, 4).fill('rgba(255, 255, 255, 0)');


                    linkshape.setPathData(linkShapePath).fill('#666');
                    link.addShape(outline);
                    link.addShape(linkshape);
                    link.setTarget('_blank');
                    link.setStyle('cursor', 'pointer');

                    link.on('mouseover', function() {
                        outline.fill('rgba(255, 255, 200, .8)');
                    }).on('mouseout', function() {
                        outline.fill('rgba(255, 255, 255, 0)');
                    });
                    return link;
                },

                shouldRender: function(node) {
                    return node.getData('hyperlink');
                },

                update: function(link, node, box) {

                    var href = node.getData('hyperlink');
                    link.setHref(href);
                    var title = node.getData('hyperlinkTitle');

                    if (title) {
                        title = [title, '(', href, ')'].join('');
                    } else {
                        title = href;
                    }

                    link.node.setAttributeNS('http://www.w3.org/1999/xlink', 'title', title);

                    var spaceRight = node.getStyle('space-right');

                    link.setTranslate(box.right + spaceRight + 2, -5);
                    return new kity.Box({
                        x: box.right + spaceRight,
                        y: -11,
                        width: 24,
                        height: 22
                    });
                }
            })
        }
    });
});