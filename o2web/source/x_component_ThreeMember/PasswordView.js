MWF.xApplication.ThreeMember = MWF.xApplication.ThreeMember || {};
MWF.require("MWF.widget.O2Identity", null, false);
//MWF.xDesktop.requireApp("ThreeMember", "Access", null, false);
//MWF.xDesktop.requireApp("ThreeMember", "Actions.RestActions", null, false);
MWF.xDesktop.requireApp("Template", "Explorer", null, false);
MWF.xDesktop.requireApp("Template", "MForm", null, false);

MWF.xApplication.ThreeMember.PasswordView = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],
    options: {
        "style": "default",
        "title": MWF.xApplication.ThreeMember.LP.title
    },
    initialize: function(node, app, options){
        this.setOptions(options);

        this.path = "../x_component_ThreeMember/$PermissionView/";
        this.cssPath = "../x_component_ThreeMember/$PermissionView/"+this.options.style+"/css.wcss";
        this._loadCss();
        this.app = app;
        this.content = $(node);
        this.lp = MWF.xApplication.ThreeMember.LP;
        this.load();
    },
    reload: function () {
        this.clearContent();
        this.load();
    },
    load: function () {
        this.content.setStyle("overflow", "hidden");
        this.node = new Element("div", {
            "styles": this.css.node
        }).inject(this.content);

        this.naviNode = new Element("div.naviNode", {
            "styles": this.css.naviNode
        }).inject(this.node);
        this.contentContainerNode = new Element("div.contentContainerNode", {
            "styles": this.css.contentContainerNode
        }).inject(this.node);
        this.createContent();

    },
    createContent: function () {

        this.middleNode = new Element("div.middleNode", {
            "styles": this.css.middleNode
        }).inject(this.contentContainerNode);

        this.contentScrollNode = new Element("div.contentScrollNode", {
            "styles": this.css.contentScrollNode
        }).inject(this.middleNode);

        this.contentNode = new Element("div.contentNode", {
            "styles": this.css.contentNode
        }).inject(this.contentScrollNode);

        // this.loadView();

        this.setContentSizeFun = this.setContentSize.bind(this);
        this.app.addEvent("resize", this.setContentSizeFun);
        this.setContentSize();

    },
    getOffsetY: function (node) {
        return (node.getStyle("margin-top").toInt() || 0) +
            (node.getStyle("margin-bottom").toInt() || 0) +
            (node.getStyle("padding-top").toInt() || 0) +
            (node.getStyle("padding-bottom").toInt() || 0) +
            (node.getStyle("border-top-width").toInt() || 0) +
            (node.getStyle("border-bottom-width").toInt() || 0);
    },
    setContentSize: function () {
        var nodeSize = this.content.getSize();
        var h = nodeSize.y - this.getOffsetY(this.content);

        var topY = this.topContainerNode ? (this.getOffsetY(this.topContainerNode) + this.topContainerNode.getSize().y) : 0;
        h = h - topY;

        h = h - this.getOffsetY(this.contentScrollNode);

        this.contentScrollNode.setStyles({
            "height": "" + h + "px",
            "overflow": "auto"
        });
    },
    clearContent: function () {
        if (this.setContentSizeFun) this.removeEvent("resize", this.setContentSizeFun);
        if (this.contentContainerNode) {
            this.contentContainerNode.destroy();
        }
        this.node.destroy();
    },
    recordStatus: function () {
        var status = this.navi.currentStatus || {};
        status.explorer = "passwordview";
        return status;
    }
});
