define(function(require, exports, module) {
    var kity = require('./kity');
    var utils = require('./utils');
    var Minder = require('./minder');
    var MinderNode = require('./node');
    var MinderEvent = require('./event');
    var Command = require('./command');

    var _layouts = {};
    var _defaultLayout;

    function register(name, layout) {
        _layouts[name] = layout;
        _defaultLayout = _defaultLayout || name;
    }

    /**
     * @class Layout 布局基类，具体布局需要从该类派生
     */
    var Layout = kity.createClass('Layout', {

        /**
         * @abstract
         *
         * 子类需要实现的布局算法，该算法输入一个节点，排布该节点的子节点（相对父节点的变换）
         *
         * @param  {MinderNode} node 需要布局的节点
         *
         * @example
         *
         * doLayout: function(node) {
         *     var children = node.getChildren();
         *     // layout calculation
         *     children[i].setLayoutTransform(new kity.Matrix().translate(x, y));
         * }
         */
        doLayout: function(parent, children) {
            throw new Error('Not Implement: Layout.doLayout()');
        },

        /**
         * 对齐指定的节点
         *
         * @param {Array<MinderNode>} nodes 要对齐的节点
         * @param {string} border 对齐边界，允许取值 left, right, top, bottom
         *
         */
        align: function(nodes, border, offset) {
            var me = this;
            offset = offset || 0;
            nodes.forEach(function(node) {
                var tbox = me.getTreeBox([node]);
                var matrix = node.getLayoutTransform();
                switch (border) {
                    case 'left':
                        return matrix.translate(offset - tbox.left, 0);
                    case 'right':
                        return matrix.translate(offset - tbox.right, 0);
                    case 'top':
                        return matrix.translate(0, offset - tbox.top);
                    case 'bottom':
                        return matrix.translate(0, offset - tbox.bottom);
                }
            });
        },

        stack: function(nodes, axis, distance) {
            var me = this;

            var position = 0;

            distance = distance || function(node, next, axis) {
                return node.getStyle({
                    x: 'margin-right',
                    y: 'margin-bottom'
                }[axis]) + next.getStyle({
                    x: 'margin-left',
                    y: 'margin-top'
                }[axis]);
            };

            nodes.forEach(function(node, index, nodes) {
                var tbox = me.getTreeBox([node]);

                var size = {
                    x: tbox.width,
                    y: tbox.height
                }[axis];
                var offset = {
                    x: tbox.left,
                    y: tbox.top
                }[axis];

                var matrix = node.getLayoutTransform();

                if (axis == 'x') {
                    matrix.translate(position - offset, 0);
                } else {
                    matrix.translate(0, position - offset);
                }
                position += size;
                if (nodes[index + 1])
                    position += distance(node, nodes[index + 1], axis);
            });
            return position;
        },

        move: function(nodes, dx, dy) {
            nodes.forEach(function(node) {
                node.getLayoutTransform().translate(dx, dy);
            });
        },

        /**
         * 工具方法：获取给点的节点所占的布局区域
         *
         * @param  {MinderNode[]} nodes 需要计算的节点
         *
         * @return {Box} 计算结果
         */
        getBranchBox: function(nodes) {
            var box = new kity.Box();
            var i, node, matrix, contentBox;
            for (i = 0; i < nodes.length; i++) {
                node = nodes[i];
                matrix = node.getLayoutTransform();
                contentBox = node.getContentBox();
                box = box.merge(matrix.transformBox(contentBox));
            }

            return box;
        },

        /**
         * 工具方法：计算给定的节点的子树所占的布局区域
         *
         * @param  {MinderNode} nodes 需要计算的节点
         *
         * @return {Box} 计算的结果
         */
        getTreeBox: function(nodes) {

            var i, node, matrix, treeBox;

            var box = new kity.Box();

            if (!(nodes instanceof Array)) nodes = [nodes];

            for (i = 0; i < nodes.length; i++) {
                node = nodes[i];
                matrix = node.getLayoutTransform();

                treeBox = node.getContentBox();

                if (node.isExpanded() && node.children.length) {
                    treeBox = treeBox.merge(this.getTreeBox(node.children));
                }

                box = box.merge(matrix.transformBox(treeBox));
            }

            return box;
        },

        getOrderHint: function(node) {
            return [];
        }
    });

    Layout.register = register;

    Minder.registerInitHook(function(options) {
        this.refresh();
    });

    /**
     * 布局支持池子管理
     */
    utils.extend(Minder, {

        getLayoutList: function() {
            return _layouts;
        },

        getLayoutInstance: function(name) {
            var LayoutClass = _layouts[name];
            if (!LayoutClass) throw new Error('Missing Layout: ' + name);
            var layout = new LayoutClass();
            return layout;
        }
    });

    /**
     * MinderNode 上的布局支持
     */
    kity.extendClass(MinderNode, {

        /**
         * 获得当前节点的布局名称
         *
         * @return {String}
         */
        getLayout: function() {
            var layout = this.getData('layout');

            layout = layout || (this.isRoot() ? _defaultLayout : this.parent.getLayout());

            return layout;
        },

        setLayout: function(name) {
            if (name) {
                if (name == 'inherit') {
                    this.setData('layout');
                } else {
                    this.setData('layout', name);
                }
            }
            return this;
        },

        layout: function(name) {

            this.setLayout(name).getMinder().layout();

            return this;
        },

        getLayoutInstance: function() {
            return Minder.getLayoutInstance(this.getLayout());
        },

        getOrderHint: function(refer) {
            return this.parent.getLayoutInstance().getOrderHint(this);
        },

        /**
         * 获取当前节点相对于父节点的布局变换
         */
        getLayoutTransform: function() {
            return this._layoutTransform || new kity.Matrix();
        },

        /**
         * 第一轮布局计算后，获得的全局布局位置
         *
         * @return {[type]} [description]
         */
        getGlobalLayoutTransformPreview: function() {
            var pMatrix = this.parent ? this.parent.getLayoutTransform() : new kity.Matrix();
            var matrix = this.getLayoutTransform();
            var offset = this.getLayoutOffset();
            if (offset) {
                matrix = matrix.clone().translate(offset.x, offset.y);
            }
            return pMatrix.merge(matrix);
        },

        getLayoutPointPreview: function() {
            return this.getGlobalLayoutTransformPreview().transformPoint(new kity.Point());
        },

        /**
         * 获取节点相对于全局的布局变换
         */
        getGlobalLayoutTransform: function() {
            if (this._globalLayoutTransform) {
                return this._globalLayoutTransform;
            } else if (this.parent) {
                return this.parent.getGlobalLayoutTransform();
            } else {
                return new kity.Matrix();
            }
        },

        /**
         * 设置当前节点相对于父节点的布局变换
         */
        setLayoutTransform: function(matrix) {
            this._layoutTransform = matrix;
            return this;
        },

        /**
         * 设置当前节点相对于全局的布局变换（冗余优化）
         */
        setGlobalLayoutTransform: function(matrix) {
            this.getRenderContainer().setMatrix(this._globalLayoutTransform = matrix);
            return this;
        },

        setVertexIn: function(p) {
            this._vertexIn = p;
        },

        setVertexOut: function(p) {
            this._vertexOut = p;
        },

        getVertexIn: function() {
            return this._vertexIn || new kity.Point();
        },

        getVertexOut: function() {
            return this._vertexOut || new kity.Point();
        },

        getLayoutVertexIn: function() {
            return this.getGlobalLayoutTransform().transformPoint(this.getVertexIn());
        },

        getLayoutVertexOut: function() {
            return this.getGlobalLayoutTransform().transformPoint(this.getVertexOut());
        },

        setLayoutVectorIn: function(v) {
            this._layoutVectorIn = v;
            return this;
        },

        setLayoutVectorOut: function(v) {
            this._layoutVectorOut = v;
            return this;
        },

        getLayoutVectorIn: function() {
            return this._layoutVectorIn || new kity.Vector();
        },

        getLayoutVectorOut: function() {
            return this._layoutVectorOut || new kity.Vector();
        },

        getLayoutBox: function() {
            var matrix = this.getGlobalLayoutTransform();
            return matrix.transformBox(this.getContentBox());
        },

        getLayoutPoint: function() {
            var matrix = this.getGlobalLayoutTransform();
            return matrix.transformPoint(new kity.Point());
        },

        getLayoutOffset: function() {
            if (!this.parent) return new kity.Point();

            // 影响当前节点位置的是父节点的布局
            var data = this.getData('layout_' + this.parent.getLayout() + '_offset');

            if (data) return new kity.Point(data.x, data.y);

            return new kity.Point();
        },

        setLayoutOffset: function(p) {
            if (!this.parent) return this;

            this.setData('layout_' + this.parent.getLayout() + '_offset', p ? {
                x: p.x,
                y: p.y
            } : undefined);

            return this;
        },

        hasLayoutOffset: function() {
            return !!this.getData('layout_' + this.parent.getLayout() + '_offset');
        },

        resetLayoutOffset: function() {
            return this.setLayoutOffset(null);
        },

        getLayoutRoot: function() {
            if (this.isLayoutRoot()) {
                return this;
            }
            return this.parent.getLayoutRoot();
        },

        isLayoutRoot: function() {
            return this.getData('layout') || this.isRoot();
        }
    });

    /**
     * Minder 上的布局支持
     */
    kity.extendClass(Minder, {

        layout: function() {

            var duration = this.getOption('layoutAnimationDuration');

            this.getRoot().traverse(function(node) {
                // clear last results
                node.setLayoutTransform(null);
            });

            function layoutNode(node, round) {

                // layout all children first
                // 剪枝：收起的节点无需计算
                if (node.isExpanded() || true) {
                    node.children.forEach(function(child) {
                        layoutNode(child, round);
                    });
                }

                var layout = node.getLayoutInstance();
                // var childrenInFlow = node.getChildren().filter(function(child) {
                //     return !child.hasLayoutOffset();
                // });
                layout.doLayout(node, node.getChildren(), round);
            }

            // 第一轮布局
            layoutNode(this.getRoot(), 1);

            // 第二轮布局
            layoutNode(this.getRoot(), 2);

            var minder = this;
            this.applyLayoutResult(this.getRoot(), duration, function() {
                /**
                 * 当节点>200, 不使用动画时, 此处逻辑变为同步逻辑, 外部minder.on事件无法
                 * 被提前录入, 因此增加setTimeout
                 * @author Naixor
                 */
                setTimeout(function () {
                    minder.fire('layoutallfinish');
                }, 0);
            });

            return this.fire('layout');
        },

        refresh: function() {
            this.getRoot().renderTree();
            this.layout().fire('contentchange')._interactChange();
            return this;
        },

        applyLayoutResult: function(root, duration, callback) {
            root = root || this.getRoot();
            var me = this;

            var complex = root.getComplex();

            function consume() {
                if (!--complex) {
                    if (callback) {
                        callback();
                    }
                }
            }

            // 节点复杂度大于 100，关闭动画
            if (complex > 200) duration = 0;

            function applyMatrix(node, matrix) {
                node.setGlobalLayoutTransform(matrix);

                me.fire('layoutapply', {
                    node: node,
                    matrix: matrix
                });
            }

            function apply(node, pMatrix) {
                var matrix = node.getLayoutTransform().merge(pMatrix.clone());
                var lastMatrix = node.getGlobalLayoutTransform() || new kity.Matrix();

                var offset = node.getLayoutOffset();
                matrix.translate(offset.x, offset.y);

                matrix.m.e = Math.round(matrix.m.e);
                matrix.m.f = Math.round(matrix.m.f);

                // 如果当前有动画，停止动画
                if (node._layoutTimeline) {
                    node._layoutTimeline.stop();
                    node._layoutTimeline = null;
                }

                // 如果要求以动画形式来更新，创建动画
                if (duration) {
                    node._layoutTimeline = new kity.Animator(lastMatrix, matrix, applyMatrix)
                        .start(node, duration, 'ease')
                        .on('finish', function() {
                            //可能性能低的时候会丢帧，手动添加一帧
                            setTimeout(function() {
                                applyMatrix(node, matrix);
                                me.fire('layoutfinish', {
                                    node: node,
                                    matrix: matrix
                                });
                                consume();
                            }, 150);
                        });
                }

                // 否则直接更新
                else {
                    applyMatrix(node, matrix);
                    me.fire('layoutfinish', {
                        node: node,
                        matrix: matrix
                    });
                    consume();
                }

                for (var i = 0; i < node.children.length; i++) {
                    apply(node.children[i], matrix);
                }
            }
            apply(root, root.parent ? root.parent.getGlobalLayoutTransform() : new kity.Matrix());
            return this;
        }
    });

    module.exports = Layout;
});
