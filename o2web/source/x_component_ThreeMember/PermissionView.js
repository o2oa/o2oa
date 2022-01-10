MWF.xApplication.ThreeMember = MWF.xApplication.ThreeMember || {};
MWF.require("MWF.widget.O2Identity", null, false);
//MWF.xDesktop.requireApp("ThreeMember", "Access", null, false);
//MWF.xDesktop.requireApp("ThreeMember", "Actions.RestActions", null, false);
MWF.xDesktop.requireApp("Template", "Explorer", null, false);
MWF.xDesktop.requireApp("Template", "MForm", null, false);

MWF.xApplication.ThreeMember.PermissionView = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],
    options: {
        "style": "default",
        "viewPageNum": 1,
        "module": "",
        "operation": "",
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
        this.createTop();
        this.createContent();
        this.loaNavi();

    },
    loaNavi: function (callback) {
        debugger;
        var naviOpt = {};
        naviOpt.module = this.options.module || "all";
        naviOpt.operation = this.options.operation;
        this.navi = new MWF.xApplication.ThreeMember.PermissionView.Navi(this, this.naviNode, naviOpt);
    },
    createTop: function () {
        this.topContainerNode = new Element("div.topContainerNode", {
            "styles": this.css.topContainerNode
        }).inject(this.contentContainerNode);

        this.topNode = new Element("div.topNode", {
            "styles": this.css.topNode
        }).inject(this.topContainerNode);

        this.topContentNode = new Element("div", {
            "styles": this.css.topContentNode
        }).inject(this.topNode);

    },
    createContent: function () {

        this.middleNode = new Element("div.middleNode", {
            "styles": this.css.middleNode
        }).inject(this.contentContainerNode);

        this.contentNode = new Element("div.contentNode", {
            "styles": this.css.contentNode
        }).inject(this.middleNode);

        // this.loadView();

        this.setContentSizeFun = this.setContentSize.bind(this);
        this.app.addEvent("resize", this.setContentSizeFun);
        // this.setContentSize();

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
        var nodeSize = this.node.getSize();
        var h = nodeSize.y - this.getOffsetY(this.node);

        var topY = this.topContainerNode ? (this.getOffsetY(this.topContainerNode) + this.topContainerNode.getSize().y) : 0;
        h = h - topY;

        h = h - this.getOffsetY(this.contentNode);

        this.contentNode.setStyles({
            "height": "" + h + "px",
            "overflow": "auto"
        });
    },
    clearContent: function () {
        if (this.setContentSizeFun) this.removeEvent("resize", this.setContentSizeFun);
        if( this.navi )this.navi.destroy();
        if( this.naviNode ){
            this.naviNode.destroy();
        }
        if (this.contentContainerNode) {
            this.contentContainerNode.destroy();
        }
        this.node.destroy();
    },
    getCategories: function(){
        return [
            {"id":"process", "text": this.lp.process,
                "action": o2.Actions.load("x_processplatform_assemble_designer").ApplicationAction.list },
            {"id":"cms", "text": this.lp.cms,
                "action": o2.Actions.load("x_cms_assemble_control").AppInfoAction.listAllAppInfo },
            {"id":"portal", "text": this.lp.portal,
                "action": o2.Actions.load("x_portal_assemble_designer").PortalAction.list },
            {"id":"query", "text": this.lp.query1,
                "action": o2.Actions.load("x_query_assemble_designer").QueryAction.listAll }
        ];
    },
    loadView: function(object){
        if(this.contentView)this.contentView.clear();
        switch (object.category.id) {
            case "process":
                this.contentView = new MWF.xApplication.ThreeMember.PermissionView.ProcessApplication(this,{});
                this.contentView.load();
                break;
            case "cms":
                this.contentView = new MWF.xApplication.ThreeMember.PermissionView.CMSApplication(this,{});
                this.contentView.load();
                break;
            case "portal":
                this.contentView = new MWF.xApplication.ThreeMember.PermissionView.PortalApplication(this,{});
                this.contentView.load();
                break;
            case "query":
                this.contentView = new MWF.xApplication.ThreeMember.PermissionView.QueryApplication(this,{});
                this.contentView.load();
                break;
        }
    },
    recordStatus: function () {
        var status = this.navi.currentStatus || {};
        status.explorer = "permissionView";
        return status;
    }
});

MWF.xApplication.ThreeMember.PermissionView.Navi = new Class({
    Implements: [Options, Events],
    options: {
        "category": "process"
    },
    initialize: function (explorer, node, options) {
        this.setOptions(options);
        this.app = explorer.app;
        this.explorer = explorer;
        this.node = $(node);
        this.css = this.explorer.css;
        this.currentMenu = null;
        this.currentItem = null;
        this.load();
    },
    load: function () {
        this.scrollNode = new Element("div.naviScrollNode", {"styles": this.css.naviScrollNode}).inject(this.node);
        this.areaNode = new Element("div.naviAreaNode", {"styles": this.css.naviAreaNode}).inject(this.scrollNode);

        this.naviTopNode = new Element("div.naviTopNode", {
            "styles": this.css.naviTopNode,
            "text": this.explorer.lp.title
        }).inject(this.areaNode);

        this.explorer.getCategories().each(function (category) {
            debugger;
            this.createMenuNode(category);
        }.bind(this));

        this.setContentSize();
        this.setContentSizeFun = this.setContentSize.bind(this);
        this.app.addEvent("resize", this.setContentSizeFun);
    },
    destroy: function(){
        if(this.setContentSizeFun)this.app.removeEvent("resize", this.setContentSizeFun );
        this.scrollNode.destroy();
    },
    // setCurrentAll: function () {
    //     this.cancelCurrent();
    //     this.currentStatus = null;
    //     this.currentAll = this.naviAllNode;
    //     this.naviAllNode.setStyles(this.css.naviAllNode_current);
    //     if (this.explorer.form) {
    //         this.explorer.form.reset();
    //         this.explorer.form.getItem("module").items[0].fireEvent("change");
    //     }
    //     this.explorer.loadView()
    // },
    createMenuNode: function (category) {
        var _self = this;
        var menuNode = new Element("div", {
            "styles": this.css.naviMenuNode
        });
        menuNode.store("data", category);
        menuNode.setStyles(this.css.naviMenuNode_collapse);

        var iconNode = new Element("div", {
            "styles": this.css.naviMenuIconNode_collapse
        }).inject(menuNode);

        var textNode = new Element("div", {
            "styles": this.css.naviMenuTextNode,
            "text": category.text
        }).inject(menuNode);
        menuNode.inject(this.areaNode);

        var menuObj = {
            "category": category,
            "collapse": true,
            "node": menuNode,
            "iconNode": iconNode
        };
        iconNode.addEvents({
            click: function (ev) {
                _self.expandOrCollapse(menuObj);
                ev.stopPropagation();
            }
        });
        menuNode.addEvents({
            "mouseover": function () {
                if (_self.currentMenu != this) this.setStyles(_self.explorer.css.naviMenuNode_over);
            },
            "mouseout": function () {
                if (_self.currentMenu != this) this.setStyles(_self.explorer.css.naviMenuNode_normal);
            },
            "click": function (ev) {
                _self.expandOrCollapse(menuObj);
                ev.stopPropagation();
            }
        });
        // if (this.options.module === category.text) {
        //     if (this.options.operation) {
        //         iconNode.click();
        //     } else {
        //         menuNode.click()
        //     }
        // }
    },
    cancelCurrent: function () {
        // if (this.currentMenu) {
        //     this.currentMenu.setStyles(this.css.naviMenuNode);
        //     this.currentMenu.setStyles(this.css.naviMenuNode_normal);
        //     this.currentMenu = false;
        // }
        if (this.currentItem) {
            this.currentItem.setStyles(this.css.naviItemNode);
            this.currentItem = false;
        }
        // if (this.currentAll) {
        //     this.currentAll.setStyles(this.css.naviAllNode_normal);
        //     this.currentAll = false;
        // }
        this.currentStatus = null;
    },
    // setCurrentMenu: function (menuObj) {
    //     this.cancelCurrent();
    //     this.currentStatus = {
    //         module: menuObj.module
    //     };
    //     this.currentMenu = menuObj.node;
    //     menuObj.node.setStyles(this.css.naviMenuNode_current);
    //     this.explorer.form.reset();
    //     this.explorer.form.getItem("module").setValue(menuObj.module);
    //     debugger;
    //     this.explorer.form.getItem("module").items[0].fireEvent("change");
    //     this.explorer.loadView({"module": menuObj.module})
    // },
    expandOrCollapse: function (menuObj) {
        if (!menuObj.itemContainer) {
            menuObj.itemContainer = new Element("div").inject(menuObj.node, "after");
            menuObj.category.action(function (json) {
                json.data.each(function (application) {
                    this.createItemNode(application, menuObj);
                }.bind(this))
            }.bind(this));
        } else {
            menuObj.itemContainer.setStyle("display", menuObj.collapse ? "" : "none");
        }
        menuObj.iconNode.setStyles(menuObj.collapse ? this.css.naviMenuIconNode_expand : this.css.naviMenuIconNode_collapse);
        menuObj.collapse = !menuObj.collapse;
    },
    createItemNode: function (application, menuObj) {
        var _self = this;
        var itemNode = new Element("div", {
            "styles": this.css.naviItemNode
        });

        var itemObj = {
            "category": menuObj.category,
            "application": application,
            "node": itemNode
        };

        var textNode = new Element("div", {
            "styles": this.css.naviItemTextNode,
            "text": application.appName || application.applicationName || application.name
        });
        textNode.inject(itemNode);

        itemNode.inject(menuObj.itemContainer);

        itemNode.addEvents({
            "mouseover": function () {
                if (_self.currentItem != this) this.setStyles(_self.explorer.css.naviItemNode_over);
            },
            "mouseout": function () {
                if (_self.currentItem != this) this.setStyles(_self.explorer.css.naviItemNode);
            },
            "mousedown": function () {
                if (_self.currentItem != this) this.setStyles(_self.explorer.css.naviItemNode_down);
            },
            "mouseup": function () {
                if (_self.currentItem != this) this.setStyles(_self.explorer.css.naviItemNode_over);
            },
            "click": function () {
                _self.setCurrentItem(itemObj);
            }
        });

        if (application.id === this.options.application) {
            itemNode.click();
        }
    },
    setCurrentItem: function (itemObj) {
        this.cancelCurrent();
        this.currentStatus = {
            category: itemObj.category.id,
            application: itemObj.application.id
        };
        this.currentItem = itemObj.node;
        itemObj.node.setStyles(this.css.naviItemNode_current);
        this.explorer.loadView(itemObj);
    },
    setContentSize: function () {
        var nodeSize = this.explorer.node.getSize();
        var h = nodeSize.y - this.explorer.getOffsetY(this.explorer.node);
        this.node.setStyle("height", h);
        this.scrollNode.setStyle("height", h);
    }
});

MWF.xApplication.ThreeMember.PermissionView.ProcessApplication = new Class({
    Implements: [Options, Events],
    options: {
        "id": "id"
    },
    initialize: function (explorer, options) {
        this.setOptions(options);
        this.app = explorer.app;
        this.explorer = explorer;
        this.topNode = explorer.topContentNode;
        this.contentNode = explorer.contentNode;
        this.css = this.explorer.css;
        this.lp = MWF.xApplication.ThreeMember.LP;
    },
    clear: function () {
        this.topNode.empty();
        this.contentNode.empty();
    },
    load: function () {
        this.getNaviData().each(function (data) {
            var naviNode = new Element("div", {
                styles : this.css.topNaviItemNode,
                text: data.text
            }).inject(this.topNode);
            naviNode.store("data", data);
            naviNode.addEvent("click", function () {
                this.setCurrent( naviNode );
            }.bind(this))
        }.bind(this));
    },
    setCurrent: function (naviNode) {
        if( this.currentNaviNode )this.currentNaviNode.setStyles(this.css.topNaviItemNode);
        this.currentNaviNode = naviNode;
        naviNode.setStyles( this.css.topNaviItemNode_current );
        var data = naviNode.retrieve("data");
        this.contentNode.empty();
        if(this[data.action])this[data.action]();
    },
    getNaviData: function () {
        return [{
            "action": "loadApplication",
            "text": this.lp.permission.application
        },{
            "id": "loadProcess",
            "text": this.lp.permission.process
        }]
    },
    loadApplication: function () {

    },
    loadProcess: function () {

    }
});

MWF.xApplication.ThreeMember.PermissionView.CMSApplication = new Class({
    Extends: MWF.xApplication.ThreeMember.PermissionView.ProcessApplication,
    getNaviData: function () {
        return [{
            "action": "loadColumn",
            "text": this.lp.permission.column
        },{
            "id": "loadCategory",
            "text": this.lp.permission.category
        }]
    },
});

MWF.xApplication.ThreeMember.PermissionView.PortalApplication = new Class({
    Extends: MWF.xApplication.ThreeMember.PermissionView.ProcessApplication,
    getNaviData: function () {
        return [{
            "action": "loadPortal",
            "text": this.lp.permission.portal
        }]
    },
});

MWF.xApplication.ThreeMember.PermissionView.QueryApplication = new Class({
    Extends: MWF.xApplication.ThreeMember.PermissionView.ProcessApplication,
    getNaviData: function () {
        return [{
            "action": "loadQuery",
            "text": this.lp.permission.query
        },{
            "action": "loadView",
            "text": this.lp.permission.view
        },{
            "action": "loadStat",
            "text": this.lp.permission.stat
        },{
            "action": "loadTable",
            "text": this.lp.permission.table
        },{
            "action": "loadStatement",
            "text": this.lp.permission.statement
        },{
            "action": "loadImporter",
            "text": this.lp.permission.importer
        }]
    },
});