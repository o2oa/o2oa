//弹出选择框列表类
MWF.xApplication.AI.O2Select = new Class({
    Implements: [Options, Events],
    options: {
        mask: true,
        nodeStyles: null,
        value: "",
        selectValueList: [],
        selectTextList: [],
    },
    initialize: function (options) {
        this.setOptions(options);
        this.html = '<div class="MWF_select_node">' +
            '<div class="MWF_select_content" style="font-size:16px;line-height:21px;color:#333;text-align:center;width:100%;overflow:auto;"></div>' +
            '<div class="MWF_select_cancelNode" style="color:#32aaff;font-size:18px;background:#fff;height:50px;line-height:50px;border-radius: 10px;text-align:center;margin-top:10px;margin-bottom:10px;">取&nbsp;消</div>' +
            '</div>';
        this.css = {
            mask: {
                "height": "100%",
                "width": "100%",
                //"opacity": 0.3,
                "position": "absolute",
                "top": "0px",
                "left": "0px",
                "z-index": "9800",
                "background": "rgba(0,0,0,0.2)"
            },
            itemNode: {
                "padding": "10px",
                "min-height": "20px",
                "padding-top": "12px",
                "padding-bottom": "12px",
                "line-height": "20px",
                "background": "#fff",
                "color": "#000",
                "border-bottom": "1px solid #d1dbe1"
            },
            itemNode_top: {
                "border-top-left-radius": "10px",
                "border-top-right-radius": "10px"
            },
            itemNode_bottom: {
                "border-bottom-left-radius": "10px",
                "border-bottom-right-radius": "10px"
            },
            itemNode_selected: {
                "color": "#32AAFF",
                "background-color": "#f0f0f0"
            }
        };

    },
    getMaxIndex: function () {
        var zIndex = 100000;
        $(document.body).getChildren().each(function (node) {
            var z = node.getStyle("z-index");
            if (z && parseInt(z) > zIndex) {
                zIndex = parseInt(z) + 1;
            }
        })
        this.zIndex = zIndex;
    },
    load: function () {
        this.getMaxIndex();
        this.css.mask["z-index"] = this.zIndex;
        this.createMaskNode();
        this.wrapNode = new Element("div", { html: this.html });
        this.node = this.wrapNode.getElement(".MWF_select_node");
        this.contentNode = this.wrapNode.getElement(".MWF_select_content");
        this.cancelNode = this.wrapNode.getElement(".MWF_select_cancelNode");
        if (this.cancelNode) {
            this.cancelNode.addEvent("click", function () {
                this.close();
            }.bind(this))
        }

        this.node.inject($(document.body));

        if (this.options.nodeStyles) this.node.setStyles(this.options.nodeStyles);
        this.loadItem();
        this.wrapNode.destroy();

        this.setSize();
    },
    loadItem: function () {
        var _self = this;
        var array = (this.options.selectValueList || []);
        array.each(function (value, i) {
            var text = this.options.selectTextList[i];
            var itemNode = new Element("div", {
                styles: this.css.itemNode,
                text: text,
                events: {
                    click: function () {
                        _self.fireEvent("change", [this.value, this.text]);
                        _self.close();
                    }.bind({ value: value, text: text })
                }
            }).inject(this.contentNode);
            if (i === 0) itemNode.setStyles(this.css.itemNode_top);
            if (i === array.length - 1) itemNode.setStyles(this.css.itemNode_bottom);
            if (this.options.value === value) itemNode.setStyles(this.css.itemNode_selected)
        }.bind(this));
    },
    setSize: function () {
        var bodySize = $(document.body).getSize();
        var cancelNodeY = this.cancelNode.getSize().y + 20;
        if ((bodySize.y - cancelNodeY) < this.contentNode.getSize().y) { //超过body高度，显示滚动条
            this.contentNode.setStyle("height", bodySize.y - cancelNodeY);
        }
        this.node.setStyles({
            "position": "absolute",
            "z-index": this.zIndex + 1,
            "width": bodySize.x - 30 + "px",
            "bottom": "0px",
            "left": "15px",
            "z-index": this.zIndex + 1
        })
    },
    close: function () {
        this.maskNode.destroy();
        this.node.destroy();

    },
    createMaskNode: function () {
        if (this.options.mask) {
            this.maskNode = new Element("div.formMaskNode", {
                "styles": this.css.mask,
                "events": {
                    "mouseover": function (e) { e.stopPropagation(); },
                    "mouseout": function (e) { e.stopPropagation(); },
                    "click": function (e) {
                        this.close();
                        e.stopPropagation();
                    }.bind(this),
                    "mousewheel": function (e) {
                        if (e.stopPropagation) e.stopPropagation();
                        else e.cancelBubble = true;

                        if (e.preventDefault) e.preventDefault();
                        else e.returnValue = false;
                    },
                    "DOMMouseScroll": function (e) {
                        if (e.stopPropagation) e.stopPropagation();
                        else e.cancelBubble = true;

                        if (e.preventDefault) e.preventDefault();
                        else e.returnValue = false;
                    }
                }
            }).inject($(document.body));
        }
    }
});
