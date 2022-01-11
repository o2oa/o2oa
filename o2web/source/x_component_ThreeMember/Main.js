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

        this.loadNavi();
        // this.loadLogView();
        this.loadPermissionView();

        // this.createTopNode();
        // this.createContainerNode();
        // this.loaNavi();
    },
    loadNavi : function(){
        var naviJson = [
            {
                "title": "应用权限",
                "action": "loadPermissionView",
                "icon": "navi_mine"
            },
            {
                "title": "组织管理",
                "action": "openSharedExplorer",
                "icon": "navi_share"
            },
            {
                "title": "密码管理",
                "action": "loadPasswordView",
                "icon": "navi_receive"
            },
            {
                "title": "查看日志",
                "action": "loadLogView",
                "icon": "navi_recycle"
            }
            // {
            //     "title": "来自应用",
            //     "action": "personConfig",
            //     "icon": "navi_fromapp"
            // }
        ];
        naviJson.each( function( d ){
            this.createNaviNode( d );
        }.bind(this))
    },
    createNaviNode : function( d ){
        var _self = this;
        var node = new Element("div",{
            text : d.title,
            styles : this.css.naviItemNode,
            events : {
                click : function( ev ){
                    if( _self.currentAction == d.action )return;
                    ev.target.setStyles( _self.css.naviItemNode_selected );
                    if(_self.currentNaviItemNode)_self.currentNaviItemNode.setStyles( _self.css.naviItemNode );
                    _self.currentNaviItemNode = ev.target;
                    _self.currentAction = d.action;
                    _self[ d.action ]();
                }
            }
        }).inject( this.naviNode );
        node.setStyle("background-image", "url("+this.path + "icon/" + d.icon + ".png)" );
        if( this.status && this.status.action && this.status.action == d.action ){
            node.click();
        }else if( this.options.defaultAction == d.action  ){
            node.click();
        }
    },
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
        if(this.currentView)this.currentView.clearContent();
        MWF.xDesktop.requireApp("ThreeMember", "LogView", null, false);
        var options = {};
        if( this.status && this.status.es && this.status.es.explorer == "logview" ){
            options = this.status.es;
        }
        this.currentView = new MWF.xApplication.ThreeMember.LogView(this.contentContainerNode, this, options)

    },
    loadPermissionView: function(){
        if(this.currentView)this.currentView.clearContent();
        MWF.xDesktop.requireApp("ThreeMember", "PermissionView", null, false);
        var options = {};
        if( this.status && this.status.es && this.status.es.explorer == "permissionview" ){
            options = this.status.es;
        }
        this.currentView = new MWF.xApplication.ThreeMember.PermissionView(this.contentContainerNode, this, options)

    },
    loadPasswordView: function(){
        if(this.currentView)this.currentView.clearContent();
        MWF.xDesktop.requireApp("ThreeMember", "PasswordView", null, false);
        var options = {};
        if( this.status && this.status.es && this.status.es.explorer == "passwordview" ){
            options = this.status.es;
        }
        this.currentView = new MWF.xApplication.ThreeMember.PasswordView(this.contentContainerNode, this, options)

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


