define(function(require, exports, module) {
    var kity = require('../core/kity');
    var utils = require('../core/utils');

    var Minder = require('../core/minder');
    var MinderNode = require('../core/node');
    var Command = require('../core/command');
    var Module = require('../core/module');
    var Renderer = require('../core/render');

    Module.register('PriorityModule', function() {
        var minder = this;

        // Designed by Akikonata
        // [MASK, BACK]
        var PRIORITY_COLORS = [null, ['#FF1200', '#840023'], // 1 - red
            ['#0074FF', '#01467F'], // 2 - blue
            ['#00AF00', '#006300'], // 3 - green
            ['#FF962E', '#B25000'], // 4 - orange
            ['#A464FF', '#4720C4'], // 5 - purple
            ['#A3A3A3', '#515151'], // 6,7,8,9 - gray
            ['#A3A3A3', '#515151'],
            ['#A3A3A3', '#515151'],
            ['#A3A3A3', '#515151'],
        ]; // hue from 1 to 5

        // jscs:disable maximumLineLength
        var BACK_PATH = 'M0,13c0,3.866,3.134,7,7,7h6c3.866,0,7-3.134,7-7V7H0V13z';
        var MASK_PATH = 'M20,10c0,3.866-3.134,7-7,7H7c-3.866,0-7-3.134-7-7V7c0-3.866,3.134-7,7-7h6c3.866,0,7,3.134,7,7V10z';

        var PRIORITY_DATA = 'priority';

        // 进度图标的图形
        var PriorityIcon = kity.createClass('PriorityIcon', {
            base: kity.Group,

            constructor: function() {
                this.callBase();
                this.setSize(20);
                this.create();
                this.setId(utils.uuid('node_priority'));
            },

            setSize: function(size) {
                this.width = this.height = size;
            },

            create: function() {
                var white, back, mask, number; // 4 layer

                white = new kity.Path().setPathData(MASK_PATH).fill('white');
                back = new kity.Path().setPathData(BACK_PATH).setTranslate(0.5, 0.5);
                mask = new kity.Path().setPathData(MASK_PATH).setOpacity(0.8).setTranslate(0.5, 0.5);

                number = new kity.Text()
                    .setX(this.width / 2 - 0.5).setY(this.height / 2)
                    .setTextAnchor('middle')
                    .setVerticalAlign('middle')
                    .setFontItalic(true)
                    .setFontSize(12)
                    .fill('white');

                this.addShapes([back, mask, number]);
                this.mask = mask;
                this.back = back;
                this.number = number;
            },

            setValue: function(value) {
                var back = this.back,
                    mask = this.mask,
                    number = this.number;

                var color = PRIORITY_COLORS[value];

                if (color) {
                    back.fill(color[1]);
                    mask.fill(color[0]);
                }

                number.setContent(value);
            }
        });

        /**
         * @command Priority
         * @description 设置节点的优先级信息
         * @param {number} value 要设置的优先级（添加一个优先级小图标）
         *     取值为 0 移除优先级信息；
         *     取值为 1 - 9 设置优先级，超过 9 的优先级不渲染
         * @state
         *    0: 当前有选中的节点
         *   -1: 当前没有选中的节点
         */
        var PriorityCommand = kity.createClass('SetPriorityCommand', {
            base: Command,
            execute: function(km, value) {
                var nodes = km.getSelectedNodes();
                for (var i = 0; i < nodes.length; i++) {
                    nodes[i].setData(PRIORITY_DATA, value || null).render();
                }
                km.layout();
            },
            queryValue: function(km) {
                var nodes = km.getSelectedNodes();
                var val;
                for (var i = 0; i < nodes.length; i++) {
                    val = nodes[i].getData(PRIORITY_DATA);
                    if (val) break;
                }
                return val || null;
            },

            queryState: function(km) {
                return km.getSelectedNodes().length ? 0 : -1;
            }
        });
        return {
            'commands': {
                'priority': PriorityCommand
            },
            'renderers': {
                left: kity.createClass('PriorityRenderer', {
                    base: Renderer,

                    create: function(node) {
                        return new PriorityIcon();
                    },

                    shouldRender: function(node) {
                        return node.getData(PRIORITY_DATA);
                    },

                    update: function(icon, node, box) {
                        var data = node.getData(PRIORITY_DATA);
                        var spaceLeft = node.getStyle('space-left'),
                            x, y;

                        icon.setValue(data);
                        x = box.left - icon.width - spaceLeft;
                        y = -icon.height / 2;

                        icon.setTranslate(x, y);

                        return new kity.Box({
                            x: x,
                            y: y,
                            width: icon.width,
                            height: icon.height
                        });
                    }
                })
            }
        };
    });
});