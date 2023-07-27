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
        "managerEnabled": true,
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
        this.clear();
        this.loadApplicationLayout();
    },
    isAdmin: function () {
        return this.access.isAdmin();
    },
    createNode: function () {
        debugger;
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

        this.loadUser();
        this.checkService(function () {
            this.loadNavi();
        }.bind(this));
        // this.loadLogView();

        // this.createTopNode();
        // this.createContainerNode();
        // this.loaNavi();
    },
    loadUser: function(){
        if( !layout.inBrowser )return;

        var naviRightNode = new Element("div", {
            styles : this.css.naviRightNode
        }).inject( this.naviNode );

        new Element("div", {
            styles : this.css.userNode,
            text: this.lp.welcome + layout.session.user.name
        }).inject(naviRightNode);

        new Element("div", {
            styles : this.css.logoutNode,
            text: this.lp.logout,
            events: {
                click: function () {
                    this.safeLogout();
                }.bind(this)
            }
        }).inject(naviRightNode);
    },
    safeLogout: function(){
        o2.Actions.get("x_organization_assemble_authentication").safeLogout(function () {
            if (this.socket) {
                this.socket.close();
                this.socket = null;
            }
            if (layout.session && layout.session.user) layout.session.user.token = "";
            if (sessionStorage) sessionStorage.removeItem("o2LayoutSessionToken");
            // window.location.reload();
            window.location = "/";
        }.bind(this));
    },
    checkService: function(success){
        if( layout.serviceAddressList["x_auditlog_assemble_control"] ){
            success();
        }else{
            this.contentContainerNode.setStyle("background-color","#fff");
            new Element("div", {
                "styles": this.css.noServiceNode,
                "text": this.lp.noServiceNote
            }).inject( this.contentContainerNode );
        }
    },
    loadNavi : function(){
        this.managerEnabled = this.options.managerEnabled && o2.AC.isManager();
        var naviJson = [
            {
                "id": "logview",
                "title": this.lp.log,
                "action": "loadLogView",
                "icon": "log",
                "display": o2.AC.isSystemManager() || o2.AC.isAuditManager() || this.managerEnabled
            },
            {
                "id": "organizationview",
                "title": this.lp.org,
                "action": "loadOrganizationView",
                "icon": "org",
                "display":  o2.AC.isSystemManager() || o2.AC.isSecurityManager() || this.managerEnabled
            },
            {
                "id": "passwordview",
                "title": this.lp.password,
                "action": "loadPasswordView",
                "icon": "password",
                "display": o2.AC.isSystemManager() || this.managerEnabled
            },
            {
                "id": "permissionview",
                "title": this.lp.permission1,
                "action": "loadPermissionView",
                "icon": "permission",
                "display":  o2.AC.isSecurityManager() || this.managerEnabled
            },
            {
                "id": "gohome",
                "title": this.lp.home,
                "action": "goHome",
                "icon": "home",
                "display": this.inBrowser && (o2.AC.isSystemManager() || this.managerEnabled)
            }
        ];
        var displayNaviJson = naviJson.filter(function (d) { return d.display });
        this.displayNaviId = displayNaviJson.map( function(d){ return d.id } );
        displayNaviJson.each( function( d, idx ){
            this.createNaviNode( d , idx, idx === 0 );
        }.bind(this));
        if( displayNaviJson.length === 0 ){
            this.contentContainerNode.setStyle("background-color","#fff");
            new Element("div", {
                "styles": this.css.noPermissionNode,
                "text": this.lp.noPermissionText
            }).inject(this.contentContainerNode)
        }
    },
    createNaviNode : function( d, index, isFirst ){
        var _self = this;
        var node = new Element("div",{
            text : d.title,
            styles : this.css.naviItemNode,
            events : {
                click : function( ev ){
                    if( _self.currentAction == d.action )return;
                    ev.target.setStyles( _self.css.naviItemNode_selected );
                    ev.target.setStyle("background-image", "url("+_self.path + "icon/" + d.icon + "_click.png)" );
                    if(_self.currentNaviItemNode){
                        var data = _self.currentNaviItemNode.retrieve("data");
                        _self.currentNaviItemNode.setStyles( _self.css.naviItemNode );
                        _self.currentNaviItemNode.setStyle("background-image", "url("+_self.path + "icon/" + data.icon + ".png)" );
                    }
                    _self.currentNaviItemNode = ev.target;
                    _self.currentAction = d.action;
                    _self[ d.action ]();
                }
            }
        }).inject( this.naviNode );
        node.store("data", d);
        node.setStyle("background-image", "url("+this.path + "icon/" + d.icon + ".png)" );
        debugger;
        if( this.status && this.status.es && this.displayNaviId.contains(this.status.es.explorer) && this.status.es.explorer ){
            if(this.status.es.explorer === d.id)node.click();
        }else if( isFirst  ){
            node.click();
        }
    },
    setContentSize: function(){
        var size = this.content.getSize();
        var h = size.y - this.getOffsetY(this.content);
        h = h - this.getOffsetY(this.node);

        h = h - this.naviNode.getSize().y - this.getOffsetY(this.naviNode);

        this.contentContainerNode.setStyle("height", h+"px");

    },
    clear: function () {
        if(this.currentView)this.currentView.clear();
        // if (this.setContentSizeFun) this.removeEvent("resize", this.setContentSizeFun);
    },
    goHome: function(){
        window.location = o2.filterUrl( window.location.protocol + "//" + window.location.host );
    },
    loadLogView: function(){
        if(this.currentView)this.currentView.clear();
        MWF.xDesktop.requireApp("ThreeMember", "LogView", null, false);
        var options = {};
        if( this.status && this.status.es && this.status.es.explorer === "logview" ){
            options = this.status.es;
        }
        this.currentView = new MWF.xApplication.ThreeMember.LogView(this.contentContainerNode, this, options)

    },
    loadPermissionView: function(){
        if(this.currentView)this.currentView.clear();
        MWF.xDesktop.requireApp("ThreeMember", "PermissionView", null, false);
        var options = {};
        if( this.status && this.status.es && this.status.es.explorer === "permissionview" ){
            options = this.status.es;
        }
        this.currentView = new MWF.xApplication.ThreeMember.PermissionView(this.contentContainerNode, this, options)

    },
    loadPasswordView: function(){
        if(this.currentView)this.currentView.clear();
        MWF.xDesktop.requireApp("ThreeMember", "PasswordView", null, false);
        var options = {};
        if( this.status && this.status.es && this.status.es.explorer === "passwordview" ){
            options = this.status.es;
        }
        this.currentView = new MWF.xApplication.ThreeMember.PasswordView(this.contentContainerNode, this, options)

    },
    loadOrganizationView: function(){
        if(this.currentView)this.currentView.clear();
        MWF.xApplication.Org = MWF.xApplication.Org || {};
        MWF.xDesktop.requireApp("Org", "lp."+o2.language, null, false);
        MWF.xDesktop.requireApp("Org", "Main", null, false);
        var options = {};
        if( this.status && this.status.es && this.status.es.explorer === "organizationview" ){
            options = this.status.es;
        }
        options.embededParent = this.contentContainerNode;
        this.currentView = new MWF.xApplication.Org.Main(this.desktop, options);
        this.currentView.status = options;
        this.currentView.clear = function () {
            this.content.empty();
            this.close();
        }.bind(this.currentView);
        this.currentView.load();
        this.currentView.setEventTarget(this);
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
        var explorerStatus;
        if( !this.currentView ){
            explorerStatus = ""
        }else if( this.currentView.options.name === "Org" ){
            explorerStatus = this.currentView.recordStatus();
            explorerStatus.explorer = "organizationview";
        }else{
            explorerStatus = this.currentView.recordStatus();
        }
        if( explorerStatus )status.es = explorerStatus;
        return status
    }
});


