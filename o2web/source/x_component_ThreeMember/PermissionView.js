MWF.xApplication.ThreeMember = MWF.xApplication.ThreeMember || {};
MWF.require("MWF.widget.O2Identity", null, false);
//MWF.xDesktop.requireApp("ThreeMember", "Access", null, false);
//MWF.xDesktop.requireApp("ThreeMember", "Actions.RestActions", null, false);
MWF.xDesktop.requireApp("Template", "Explorer", null, false);
MWF.xDesktop.requireApp("Template", "MForm", null, false);

var TMPermissionView = MWF.xApplication.ThreeMember.PermissionView = new Class({
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
        this.createTop();
        this.createContent();
        this.loaNavi();

    },
    loaNavi: function (callback) {
        debugger;
        var naviOpt = {};
        naviOpt.module = this.options.module || "all";
        naviOpt.operation = this.options.operation;
        this.navi = new TMPermissionView.Navi(this, this.naviNode, naviOpt);
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
                this.contentView = new TMPermissionView.ProcessApplication(this, { id: object.application.id }, object);
                this.contentView.load();
                break;
            case "cms":
                this.contentView = new TMPermissionView.CMSApplication(this, { id: object.application.id } , object);
                this.contentView.load();
                break;
            case "portal":
                this.contentView = new TMPermissionView.PortalApplication(this, { id: object.application.id }, object);
                this.contentView.load();
                break;
            case "query":
                this.contentView = new TMPermissionView.QueryApplication(this,{ id: object.application.id }, object);
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

TMPermissionView.Navi = new Class({
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
    },
    cancelCurrent: function () {
        if (this.currentItem) {
            this.currentItem.setStyles(this.css.naviItemNode);
            this.currentItem = false;
        }
        this.currentStatus = null;
    },
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

TMPermissionView.ProcessApplication = new Class({
    Implements: [Options, Events],
    options: {
        "id": ""
    },
    initialize: function (explorer, options, data) {
        this.setOptions(options);
        this.app = explorer.app;
        this.explorer = explorer;
        this.topNode = explorer.topContentNode;
        this.contentNode = explorer.contentNode;
        this.data = data;
        this.css = this.explorer.css;
        this.lp = MWF.xApplication.ThreeMember.LP;
    },
    clear: function () {
        this.topNode.empty();
        this.contentNode.empty();
    },
    load: function () {
        new Element("div", {
            styles : this.css.topNaviTitleNode,
            text: this.data.category.text + "-" +
                (this.data.application.name || this.data.application.appName || this.data.application.applicationName)
        }).inject(this.topNode);
        this.getNaviData().each(function (data, i) {
            var naviNode = new Element("div", {
                styles : this.css.topNaviItemNode,
                text: data.text
            }).inject(this.topNode);
            naviNode.store("data", data);
            naviNode.addEvent("click", function () {
                this.setCurrent( naviNode );
            }.bind(this))
            if( i === 0 )naviNode.click();
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
        o2.Actions.load("x_processplatform_assemble_designer").ApplicationAction.get(this.options.id, function (appData) {

        }.bind(this))
    },
    loadProcess: function () {

    }
});

TMPermissionView.CMSApplication = new Class({
    Extends: TMPermissionView.ProcessApplication,
    getNaviData: function () {
        return [{
            "action": "loadColumn",
            "text": this.lp.permission.column
        },{
            "action": "loadCategory",
            "text": this.lp.permission.category
        }]
    },
    loadColumn: function () {
        debugger;
        new TMPermissionView.CMSAppViewer(this.explorer, this.contentNode, { id: this.options.id });
        new TMPermissionView.CMSAppPublisher(this.explorer, this.contentNode, { id: this.options.id });
        new TMPermissionView.CMSAppManager(this.explorer, this.contentNode, { id: this.options.id });
    },
    loadCategory: function () {
        debugger;
        o2.Actions.load("x_cms_assemble_control").CategoryInfoAction.listNextWithFilter("(0)", 1000, this.options.id, {
            appIdList: [this.options.id]
        }, function (json) {
            if( !json.data || !json.data.length ){
                new Element("div", {
                    styles : this.css.noDataTextNode,
                    text: this.lp.noDataText.category
                }).inject(this.contentNode);
            }else{
                json.data.each(function (d) {
                    new Element("div", {
                        styles: this.css.propertyTitleNode,
                        text: d.name || d.categoryName
                    }).inject(this.contentNode);
                    var table = new Element("table", {
                        "width": "90%",
                        "border": "0",
                        "cellpadding":"5",
                        "cellspacing":"0",
                        "styles":{
                            "margin":"0px auto"
                        }
                    }).inject(this.contentNode);
                    var tr;
                    tr = new Element("tr").inject(table);
                    new TMPermissionView.CMSCateViewer(this.explorer, tr, { id: d.id });
                    tr = new Element("tr").inject(table);
                    new TMPermissionView.CMSCatePublisher(this.explorer, tr, { id: d.id });
                    tr = new Element("tr").inject(table);
                    new TMPermissionView.CMSCateManager(this.explorer, tr, { id: d.id });
                }.bind(this))
            }
        }.bind(this))
    }
});

TMPermissionView.PortalApplication = new Class({
    Extends: TMPermissionView.ProcessApplication,
    getNaviData: function () {
        return [{
            "action": "loadPortal",
            "text": this.lp.permission.portal
        }]
    },
});

TMPermissionView.QueryApplication = new Class({
    Extends: TMPermissionView.ProcessApplication,
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



MWF.require("MWF.widget.O2Identity", null, false);
TMPermissionView.CMSAppViewer = new Class({
    Implements: [Options],
    options : {
        id : "", //对象或分类的ID
        orgTypes: ["person","unit","group"],
        title: MWF.xApplication.ThreeMember.LP.viewer
    },
    initialize: function(explorer, node, options){
        this.explorer = explorer;
        this.node = $(node);
        this.app = explorer.app;
        this.lp = explorer.lp;
        this.css = explorer.css;
        this.setOptions( options );
        this.load();
    },
    load: function(){
        this.createNode();
        this.listData( function(){
            this.loadOrg();
        }.bind(this));
    },
    createNode: function(){
        this.titleNode = new Element("div.availableTitleNode", {
            "styles": this.css.itemTitleNode,
            "text": this.options.title
        }).inject(this.node);

        this.contentNode = new Element("div", {"styles": {"overflow": "hidden"}}).inject(this.node);
        this.itemsContentNode = new Element("div", {"styles": this.css.itemsContentNode}).inject(this.contentNode);
        this.actionAreaNode = new Element("div", {"styles": {"overflow": "hidden"}}).inject(this.node);

        var changeAction = new Element("div.selectButtonStyle", {
            "styles": this.css.selectButtonStyle,
            "text": this.lp.set + this.options.title
        }).inject(this.actionAreaNode);
        changeAction.addEvent("click", function(){
            this.change();
        }.bind(this));
    },
    loadOrg : function(){
        this.itemsContentNode.empty();
        // this.loadOrgWidget( this.data.personList );
        // this.loadOrgWidget( this.data.unitList );
        // this.loadOrgWidget( this.data.groupList );
        this.loadOrgWidget( this.values );
    },
    loadOrgWidget: function(value ){
        this.OrgWidgetList = this.OrgWidgetList || [];
        var options = { "style": "xform", "canRemove": false };
        var node = this.itemsContentNode;
        (value || []).each(function( item ){
            var dn, data;
            if( o2.typeOf( item ) === "object" ){
                dn = item.distinguishedName;
                data = item;
            }else if( o2.typeOf( item ) === "string" ){
                dn = item;
                data = { "name" : dn };
            }
            var flag = dn.substr( dn.length-1, 1 );
            var widget;
            switch (flag.toLowerCase()){
                case "i":
                    widget = new MWF.widget.O2Identity( data, node, options );
                    break;
                case "p":
                    widget = new MWF.widget.O2Person(data, node, options);
                    break;
                case "u":
                    widget = new MWF.widget.O2Unit(data, node, options);
                    break;
                case "g":
                    widget = new MWF.widget.O2Group(data, node, options);
                    break;
                default:
                    widget = new MWF.widget.O2Other( data, node, options);
            }
            this.OrgWidgetList.push( widget );
        }.bind(this));
    },
    change: function(){
        MWF.xDesktop.requireApp("Selector", "package", null, false);

        var opt  = {
            "type" : "",
            "types" : this.options.orgTypes,
            "title": this.options.title,
            "count" : 0,
            "values":  this.values,
            "expand": false,
            "onComplete": function( array ){
                this.selectData( array );
            }.bind(this)
        };
        var selector = new MWF.O2Selector(this.app.content, opt );
    },
    listData: function( callback ){
        o2.Actions.load("x_cms_assemble_control").PermissionAction.listAppInfoViewers(this.options.id, function(json){
            this.data = json.data;
            this.values = ( this.data.personList || [] ).combine( this.data.unitList || []).combine( this.data.groupList || [] );
            if( callback )callback( json );
        }.bind(this), null ,false)
    },
    selectData: function(array){
        var data = {
            personList : [], unitList : [],  groupList : []
        };
        array.each( function( a ){
            var dn = a.data.distinguishedName;
            var flag = dn.substr(dn.length-1, 1);
            switch (flag.toLowerCase()){
                case "p":
                    data.personList.push( dn );
                    break;
                case "u":
                    data.unitList.push( dn );
                    break;
                case "g":
                    data.groupList.push( dn );
                    break;
            }
        });
        this.saveData( data, function(){
            this.listData( function(){
                this.loadOrg();
            }.bind(this));
        }.bind(this))
    },
    saveData: function( data, callback ){
        o2.Actions.load("x_cms_assemble_control").PermissionAction.saveAppInfoViewer(this.options.id, data, function (json) {
            if(callback)callback()
        }.bind(this));
    }
});

TMPermissionView.CMSAppPublisher = new Class({
    Extends: TMPermissionView.CMSAppViewer,
    options: {
        title: MWF.xApplication.ThreeMember.LP.publisher
    },
    listData: function (callback) {
        o2.Actions.load("x_cms_assemble_control").PermissionAction.listAppInfoPublishers(this.options.id, function (json) {
            this.data = json.data;
            this.values = (this.data.personList || []).combine(this.data.unitList || []).combine(this.data.groupList || []);
            if (callback) callback(json);
        }.bind(this), null, false)
    },
    saveData: function (data, callback) {
        o2.Actions.load("x_cms_assemble_control").PermissionAction.saveAppInfoPublisher(this.options.id, data, function (json) {
            if (callback) callback()
        }.bind(this));
    }
});

TMPermissionView.CMSAppManager = new Class({
    Extends: TMPermissionView.CMSAppViewer,
    options: {
        title: MWF.xApplication.ThreeMember.LP.manager
    },
    listData: function (callback) {
        o2.Actions.load("x_cms_assemble_control").PermissionAction.listAppInfoManagers(this.options.id, function (json) {
            this.data = json.data;
            this.values = (this.data.personList || []).combine(this.data.unitList || []).combine(this.data.groupList || []);
            if (callback) callback(json);
        }.bind(this), null, false)
    },
    saveData: function (data, callback) {
        o2.Actions.load("x_cms_assemble_control").PermissionAction.saveAppInfoPublisher(this.options.id, data, function (json) {
            if (callback) callback()
        }.bind(this));
    }
});

TMPermissionView.CMSCateViewer = new Class({
    Extends: TMPermissionView.CMSAppViewer,
    options: {
        title: MWF.xApplication.ThreeMember.LP.viewer
    },
    createNode: function(){
        var td = new Element("td", {
            styles : this.css.propertyTitleTd,
            "text": this.options.title
        }).inject(this.node);

        var td = new Element("td", {
            styles : this.css.propertyContentTd
        }).inject(this.node);
        this.contentNode = new Element("div", {"styles": {"overflow": "hidden"}}).inject(td);
        this.itemsContentNode = new Element("div", {"styles": this.css.propertyContentNode}).inject(this.contentNode);

        var td = new Element("td", {
            styles : this.css.propertyActionTd
        }).inject(this.node);
        this.actionAreaNode = new Element("div", {"styles": {"overflow": "hidden"}}).inject(td);
        var changeAction = new Element("div.selectButtonStyle", {
            "styles": this.css.propertyButtonStyle,
            "text": this.lp.set + this.options.title
        }).inject(this.actionAreaNode);
        changeAction.addEvent("click", function(){
            this.change();
        }.bind(this));
    },
    listData: function (callback) {
        o2.Actions.load("x_cms_assemble_control").PermissionAction.listCategoryInfoViewers(this.options.id, function (json) {
            this.data = json.data;
            this.values = (this.data.personList || []).combine(this.data.unitList || []).combine(this.data.groupList || []);
            if (callback) callback(json);
        }.bind(this), null, false)
    },
    saveData: function (data, callback) {
        o2.Actions.load("x_cms_assemble_control").PermissionAction.saveCategoryInfoViewer(this.options.id, data, function (json) {
            if (callback) callback()
        }.bind(this));
    }
});

TMPermissionView.CMSCatePublisher = new Class({
    Extends: TMPermissionView.CMSCateViewer,
    options: {
        title: MWF.xApplication.ThreeMember.LP.publisher
    },
    listData: function (callback) {
        o2.Actions.load("x_cms_assemble_control").PermissionAction.listCategoryInfoPublishers(this.options.id, function (json) {
            this.data = json.data;
            this.values = (this.data.personList || []).combine(this.data.unitList || []).combine(this.data.groupList || []);
            if (callback) callback(json);
        }.bind(this), null, false)
    },
    saveData: function (data, callback) {
        o2.Actions.load("x_cms_assemble_control").PermissionAction.saveCategoryInfoPublisher(this.options.id, data, function (json) {
            if (callback) callback()
        }.bind(this));
    }
});

TMPermissionView.CMSCateManager = new Class({
    Extends: TMPermissionView.CMSCateViewer,
    options: {
        title: MWF.xApplication.ThreeMember.LP.manager
    },
    listData: function (callback) {
        o2.Actions.load("x_cms_assemble_control").PermissionAction.listCategoryInfoManagers(this.options.id, function (json) {
            this.data = json.data;
            this.values = (this.data.personList || []).combine(this.data.unitList || []).combine(this.data.groupList || []);
            if (callback) callback(json);
        }.bind(this), null, false)
    },
    saveData: function (data, callback) {
        o2.Actions.load("x_cms_assemble_control").PermissionAction.saveCategoryInfoManager(this.options.id, data, function (json) {
            if (callback) callback()
        }.bind(this));
    }
});