MWF.xApplication.ThreeMember = MWF.xApplication.ThreeMember || {};
MWF.require("MWF.widget.O2Identity", null, false);
//MWF.xDesktop.requireApp("ThreeMember", "Access", null, false);
//MWF.xDesktop.requireApp("ThreeMember", "Actions.RestActions", null, false);
MWF.xDesktop.requireApp("Template", "Explorer", null, false);
MWF.xDesktop.requireApp("Template", "MForm", null, false);

MWF.xApplication.ThreeMember.options = {
    multitask: false,
    executable: true
};
MWF.xApplication.ThreeMember.Main = new Class({
    Extends: MWF.xApplication.Common.Main,
    Implements: [Options, Events],
    options: {
        "style": "default",
        "name": "ThreeMember",
        "icon": "icon.png",
        "width": "1220",
        "height": "700",
        "isResize": true,
        "isMax": true,
        "viewPageNum": 1,
        "title": MWF.xApplication.ThreeMember.LP.title
    },
    onQueryLoad: function () {
        this.lp = MWF.xApplication.ThreeMember.LP;
    },
    loadApplication: function (callback) {
        this.userName = layout.desktop.session.user.distinguishedName;

        this.path = "../x_component_ThreeMember/$Main/" + this.options.style + "/";
        this.createNode();
        this.loadApplicationContent();
    },
    loadController: function (callback) {

        //this.access = new MWF.xApplication.ThreeMember.Access( this.restActions, this.lp );

        if (callback) callback();
    },
    reload: function () {
        this.clearContent();
        this.loadApplicationLayout();
    },
    isAdmin: function () {
        return this.access.isAdmin();
    },
    createNode: function () {
        this.content.setStyle("overflow", "hidden");
        this.node = new Element("div", {
            "styles": this.css.node
        }).inject(this.content);
    },
    loadApplicationContent: function () {
        this.loadController(function () {
            this.loadApplicationLayout();
        }.bind(this))
    },
    loadApplicationLayout: function () {
        this.naviNode = new Element("div.naviNode", {
            "styles": this.css.naviNode
        }).inject(this.node);
        this.contentContainerNode = new Element("div.contentContainerNode", {
            "styles": this.css.contentContainerNode
        }).inject(this.node);

        this.setContentSize();
        this.setContentSizeFun = this.setContentSize.bind(this);
        this.addEvent("resize", this.setContentSizeFun);

        // this.loadLogView();
        this.loadPermissionView();

        // this.createTopNode();
        // this.createContainerNode();
        // this.loaNavi();
    },
    loaNavi: function (callback) {
        debugger;
        // var naviOpt = {};
        // if (this.status) {
        //     naviOpt.module = this.status.module || "all";
        //     naviOpt.operation = this.status.operation;
        // } else {
        //     naviOpt.module = this.options.module || "all";
        //     naviOpt.operation = this.options.operation;
        // }
        // this.navi = new MWF.xApplication.ThreeMember.Main.Navi(this, this.naviNode, naviOpt);
    },
    // createTopNode: function () {
    //     this.topContainerNode = new Element("div.topContainerNode", {
    //         "styles": this.css.topContainerNode
    //     }).inject(this.contentContainerNode);
    //
    //     this.topNode = new Element("div.topNode", {
    //         "styles": this.css.topNode
    //     }).inject(this.topContainerNode);
    //
    //     this.topContentNode = new Element("div", {
    //         "styles": this.css.topContentNode
    //     }).inject(this.topNode);
    //
    //     // this.loadFilter();
    //
    // },
    // createContainerNode: function () {
    //     this.createContent();
    // },
    // createContent: function () {
    //
    //     this.middleNode = new Element("div.middleNode", {
    //         "styles": this.css.middleNode
    //     }).inject(this.contentContainerNode);
    //
    //     this.contentNode = new Element("div.contentNode", {
    //         "styles": this.css.contentNode
    //     }).inject(this.middleNode);
    //
    //     // this.loadView();
    //
    //     // this.setContentSizeFun = this.setContentSize.bind(this);
    //     // this.addEvent("resize", this.setContentSizeFun);
    //
    // },
    setContentSize: function(){
        var size = this.content.getSize();
        var h = size.y - this.getOffsetY(this.content);
        h = h - this.getOffsetY(this.node);

        this.contentContainerNode.setStyle("height", h+"px");

    },
    clearContent: function () {
        if(this.currentView)this.currentView.clearContent();
        // if (this.setContentSizeFun) this.removeEvent("resize", this.setContentSizeFun);
    },
    loadLogView: function(){
        MWF.xDesktop.requireApp("ThreeMember", "LogView", null, false);
        var options = {};
        if( this.status && this.status.es && this.status.es.explorer == "logview" ){
            options = this.status.es;
        }
        this.currentView = new MWF.xApplication.ThreeMember.LogView(this.contentContainerNode, this, options)

    },
    loadPermissionView: function(){
        MWF.xDesktop.requireApp("ThreeMember", "PermissionView", null, false);
        var options = {};
        if( this.status && this.status.es && this.status.es.explorer == "permissionview" ){
            options = this.status.es;
        }
        this.currentView = new MWF.xApplication.ThreeMember.PermissionView(this.contentContainerNode, this, options)

    },
    getOffsetY: function (node) {
        return (node.getStyle("margin-top").toInt() || 0) +
            (node.getStyle("margin-bottom").toInt() || 0) +
            (node.getStyle("padding-top").toInt() || 0) +
            (node.getStyle("padding-bottom").toInt() || 0) +
            (node.getStyle("border-top-width").toInt() || 0) +
            (node.getStyle("border-bottom-width").toInt() || 0);
    },
    recordStatus: function () {
        var status = {};
        var explorerStatus = this.currentView ? this.currentView.recordStatus() : "";
        if( explorerStatus )status.es = explorerStatus;
        return status
    }
});


