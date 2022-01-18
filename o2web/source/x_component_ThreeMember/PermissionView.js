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
        this.clear();
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
        naviOpt.category = this.options.category || "";
        naviOpt.application = this.options.application;
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
    clear: function () {
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
        status.explorer = "permissionview";
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
        this.menuObjList = [];
        this.load();
    },
    load: function () {
        this.searchInputNode = new Element("input.searchInputNode", {
            "styles": this.css.searchInputNode,
            "placeholder": this.explorer.lp.search,
            "events":{
                "keydown": function(e){
                    if (e.code==13){
                        this.search();
                        e.preventDefault();
                    }
                }.bind(this)
            }
        }).inject(this.node);
        this.searchActionNode = new Element("div.searchActionNode", {
            "styles": this.css.searchActionNode,
            "events":{
                "click": function () {
                    this.search()
                }.bind(this)
            }
        }).inject(this.node);

        this.scrollNode = new Element("div.naviScrollNode", {"styles": this.css.naviScrollNode}).inject(this.node);
        this.areaNode = new Element("div.naviAreaNode", {"styles": this.css.naviAreaNode}).inject(this.scrollNode);

        // this.naviTopNode = new Element("div.naviTopNode", {
        //     "styles": this.css.naviTopNode,
        //     "text": this.explorer.lp.title
        // }).inject(this.areaNode);

        this.explorer.getCategories().each(function (category) {
            debugger;
            this.createMenuNode(category);
        }.bind(this));

        this.setContentSize();
        this.setContentSizeFun = this.setContentSize.bind(this);
        this.app.addEvent("resize", this.setContentSizeFun);
    },
    search: function(){
        var value = this.searchInputNode.get("value");
        if( this.searchValue === value )return;
        this.searchValue = value;
        if( value === "" ){
            this.menuObjList.each(function (menuObj) {
                if (menuObj.itemContainer) {
                    menuObj.itemContainer.getElements(".item").each(function (itemNode) {
                        itemNode.setStyle("display", "");
                    })
                    menuObj.itemContainer.setStyle("display", "none");
                }
                menuObj.iconNode.setStyles( this.css.naviMenuIconNode_collapse );
                menuObj.collapse = true;
            }.bind(this))
        }else{
            this.menuObjList.each(function (menuObj) {
                if (!menuObj.itemContainer) {
                    menuObj.itemContainer = new Element("div").inject(menuObj.node, "after");
                    menuObj.category.action(function (json) {
                        json.data.each(function (application) {
                            var hidden = !(application.appName || application.applicationName || application.name).contains(value);
                            this.createItemNode(application, menuObj, hidden);
                        }.bind(this))
                    }.bind(this));
                } else {
                    menuObj.itemContainer.setStyle("display", "");
                    menuObj.itemContainer.getElements(".item").each(function (itemNode) {
                        var hidden = !itemNode.get("text").contains(value);
                        itemNode.setStyle("display", hidden ? "none" : "");
                    })
                }
                menuObj.iconNode.setStyles( this.css.naviMenuIconNode_expand );
                menuObj.collapse = false;
            }.bind(this))
        }
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
        this.menuObjList.push(menuObj);
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
        if( this.options.category === category.id ){
            menuNode.click();
        }
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
    createItemNode: function (application, menuObj, hidden) {
        var _self = this;
        var itemNode = new Element("div.item", {
            "styles": this.css.naviItemNode
        });
        if(hidden)itemNode.hide();

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

        h = h - this.searchInputNode.getSize().y - this.explorer.getOffsetY(this.searchInputNode);
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
            "action": "loadProcess",
            "text": this.lp.permission.process
        }]
    },
    loadApplication: function () {
        o2.Actions.load("x_processplatform_assemble_designer").ApplicationAction.get(this.options.id, function (json) {
            this.data = json.data;
            new TMPermissionView.ProcessAppUser(this.explorer, this.contentNode, { id: this.options.id }, this.data);
            new TMPermissionView.ProcessAppManager(this.explorer, this.contentNode, { id: this.options.id }, this.data);
        }.bind(this))
    },
    loadProcess: function () {
        o2.Actions.load("x_processplatform_assemble_designer").ProcessAction.listWithApplication(this.options.id, function (json) {
            if( !json.data || !json.data.length ){
                new Element("div", {
                    styles : this.css.noDataTextNode,
                    text: this.lp.noDataText.process
                }).inject(this.contentNode);
            }else{
                json.data.each(function (d) {
                    new Element("div", {
                        styles: this.css.propertyTitleNode,
                        text: d.name
                    }).inject(this.contentNode);
                    var table = new Element("table", this.css.tableProperty).inject(this.contentNode);
                    var tr;
                    tr = new Element("tr").inject(table);
                    new TMPermissionView.ProcessProcessStarter(this.explorer, tr, { id: this.options.id }, d);
                    tr = new Element("tr").inject(table);
                    new TMPermissionView.ProcessProcessManager(this.explorer, tr, { id: this.options.id }, d);
                }.bind(this))
            }
        }.bind(this))
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
        o2.Actions.load("x_cms_assemble_control").AppInfoAction.get(this.options.id, function (json) {
            this.data = json.data;
            new TMPermissionView.CMSAppViewer(this.explorer, this.contentNode, { id: this.options.id }, this.data);
            new TMPermissionView.CMSAppPublisher(this.explorer, this.contentNode, { id: this.options.id }, this.data);
            new TMPermissionView.CMSAppManager(this.explorer, this.contentNode, { id: this.options.id }, this.data);
        }.bind(this))
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
                    var table = new Element("table", this.css.tableProperty).inject(this.contentNode);
                    var tr;
                    tr = new Element("tr").inject(table);
                    new TMPermissionView.CMSCateViewer(this.explorer, tr, { id: d.id }, d);
                    tr = new Element("tr").inject(table);
                    new TMPermissionView.CMSCatePublisher(this.explorer, tr, { id: d.id }, d);
                    tr = new Element("tr").inject(table);
                    new TMPermissionView.CMSCateManager(this.explorer, tr, { id: d.id }, d);
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
    loadPortal: function () {
        o2.Actions.load("x_portal_assemble_designer").PortalAction.get(this.options.id, function (json) {
            this.data = json.data;
            new TMPermissionView.PortalAppUser(this.explorer, this.contentNode, { id: this.options.id }, this.data);
            new TMPermissionView.PortalAppManager(this.explorer, this.contentNode, { id: this.options.id }, this.data);
        }.bind(this))
    }
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
    loadQuery: function () {
        o2.Actions.load("x_query_assemble_designer").QueryAction.get(this.options.id, function (json) {
            this.data = json.data;
            new TMPermissionView.QueryAppUser(this.explorer, this.contentNode, { id: this.options.id }, this.data);
            new TMPermissionView.QueryAppManager(this.explorer, this.contentNode, { id: this.options.id }, this.data);
        }.bind(this))
    },
    loadView: function () {
        o2.Actions.load("x_query_assemble_designer").ViewAction.listWithQuery(this.options.id, function (json) {
            if( !json.data || !json.data.length ){
                new Element("div", {
                    styles : this.css.noDataTextNode,
                    text: this.lp.noDataText.view
                }).inject(this.contentNode);
            }else{
                json.data.each(function (d) {
                    new Element("div", {
                        styles: this.css.propertyTitleNode,
                        text: d.name
                    }).inject(this.contentNode);
                    var table = new Element("table", this.css.tableProperty).inject(this.contentNode);
                    var tr;
                    tr = new Element("tr").inject(table);
                    new TMPermissionView.QueryViewExecutor(this.explorer, tr, { id: this.options.id }, d);
                }.bind(this))
            }
        }.bind(this))
    },
    loadStat: function () {
        o2.Actions.load("x_query_assemble_designer").StatAction.listWithQuery(this.options.id, function (json) {
            debugger;
            if( !json.data || !json.data.length ){
                new Element("div", {
                    styles : this.css.noDataTextNode,
                    text: this.lp.noDataText.stat
                }).inject(this.contentNode);
            }else{
                json.data.each(function (d) {
                    new Element("div", {
                        styles: this.css.propertyTitleNode,
                        text: d.name
                    }).inject(this.contentNode);
                    var table = new Element("table", this.css.tableProperty).inject(this.contentNode);
                    var tr;
                    tr = new Element("tr").inject(table);
                    new TMPermissionView.QueryStatExecutor(this.explorer, tr, { id: this.options.id }, d);
                }.bind(this))
            }
        }.bind(this))
    },
    loadTable: function () {
        o2.Actions.load("x_query_assemble_designer").TableAction.listWithQuery(this.options.id, function (json) {
            if( !json.data || !json.data.length ){
                new Element("div", {
                    styles : this.css.noDataTextNode,
                    text: this.lp.noDataText.table
                }).inject(this.contentNode);
            }else{
                json.data.each(function (d) {
                    new Element("div", {
                        styles: this.css.propertyTitleNode,
                        text: d.name
                    }).inject(this.contentNode);
                    var table = new Element("table", this.css.tableProperty).inject(this.contentNode);
                    var tr;
                    tr = new Element("tr").inject(table);
                    new TMPermissionView.QueryTableReader(this.explorer, tr, { id: this.options.id }, d);
                    tr = new Element("tr").inject(table);
                    new TMPermissionView.QueryTableEditor(this.explorer, tr, { id: this.options.id }, d);
                }.bind(this))
            }
        }.bind(this))
    },
    loadStatement: function () {
        o2.Actions.load("x_query_assemble_designer").StatementAction.listWithQuery(this.options.id, function (json) {
            if( !json.data || !json.data.length ){
                new Element("div", {
                    styles : this.css.noDataTextNode,
                    text: this.lp.noDataText.statement
                }).inject(this.contentNode);
            }else{
                MWF.xDesktop.requireApp("Template", "MDomItem", null, false);
                json.data.each(function (d) {
                    new Element("div", {
                        styles: this.css.propertyTitleNode,
                        text: d.name
                    }).inject(this.contentNode);
                    var table = new Element("table", this.css.tableProperty).inject(this.contentNode);
                    var tr = new Element("tr").inject(table);

                    var td = new Element("td", {
                        styles : this.css.propertyTitleTd,
                        "text": this.lp.anonymousAccessible
                    }).inject(tr);
                    td.setStyles({ "height": "36px", "line-height": "36px" });

                    var td = new Element("td", {
                        styles : this.css.propertyContentTd,
                        colspan : 2
                    }).inject(tr);
                    td.setStyles({ "height": "36px", "line-height": "36px" });

                    var value = (o2.typeOf(d.anonymousAccessible) === "boolean" ? d.anonymousAccessible : true).toString();
                    var anonymousAccessibleItem = new MDomItem(td, {
                        "formStyle": "setting",
                        "name": d.id+"anonymousAccessible",
                        "type": "radio",
                        "selectValue": ["true", "false"],
                        "selectText": [this.lp.yes, this.lp.no],
                        "value": value,
                        "event": {
                            "change": function (item) {
                                executorTr.setStyle( "display", item.getValue() === "true" ? "none" : "" );
                                d.anonymousAccessible = item.getValue() === "true";
                                o2.Actions.load("x_query_assemble_designer").StatementAction.xxx(d.id, d);
                            }
                        }
                    }).load();
                    var executorTr = new Element("tr").inject(table);
                    new TMPermissionView.QueryStatementExecutor(this.explorer, executorTr, { id: this.options.id }, d);
                    if( value === "true" )executorTr.hide();
                }.bind(this))
            }
        }.bind(this))
    },
    loadImporter: function () {
        o2.Actions.load("x_query_assemble_designer").ImportModelAction.listWithQuery(this.options.id, function (json) {
            if( !json.data || !json.data.length ){
                new Element("div", {
                    styles : this.css.noDataTextNode,
                    text: this.lp.noDataText.importer
                }).inject(this.contentNode);
            }else{
                json.data.each(function (d) {
                    new Element("div", {
                        styles: this.css.propertyTitleNode,
                        text: d.name
                    }).inject(this.contentNode);
                    var table = new Element("table", this.css.tableProperty).inject(this.contentNode);
                    var tr;
                    tr = new Element("tr").inject(table);
                    new TMPermissionView.QueryImportModelExecutor(this.explorer, tr, { id: this.options.id }, d);
                }.bind(this))
            }
        }.bind(this))
    }
});



MWF.require("MWF.widget.O2Identity", null, false);
TMPermissionView.CMSAppViewer = new Class({
    Implements: [Options],
    options : {
        id : "", //对象或分类的ID
        orgTypes: ["person","unit","group"],
        title: MWF.xApplication.ThreeMember.LP.viewer
    },
    initialize: function(explorer, node, options, data){
        this.explorer = explorer;
        this.node = $(node);
        this.app = explorer.app;
        this.lp = explorer.lp;
        this.css = explorer.css;
        if(data)this.data = data;
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
    loadOrg : function( data ){
        this.itemsContentNode.empty();
        // this.loadOrgWidget( this.data.personList );
        // this.loadOrgWidget( this.data.unitList );
        // this.loadOrgWidget( this.data.groupList );
        this.loadOrgWidget( data || this.values );
    },
    loadOrgWidget: function(value ){
        this.OrgWidgetList = this.OrgWidgetList || [];
        var options = { "style": "xform", "canRemove": false, "lazy": true };
        var node = this.itemsContentNode;
        (value || []).each(function( item ){
            var dn, data;
            if( o2.typeOf( item ) === "object" ){
                dn = item.distinguishedName;
                data = item;
            }else if( o2.typeOf( item ) === "string" ){
                dn = item;
                data = {
                    "displayName": o2.name.cn(dn),
                    "distinguishedName" : dn,
                    "name": dn
                }
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
        // o2.Actions.load("x_cms_assemble_control").PermissionAction.listAppInfoViewers(this.options.id, function(json){
        //     this.data = json.data;
        //     this.values = ( this.data.personList || [] ).combine( this.data.unitList || []).combine( this.data.groupList || [] );
        //     if( callback )callback( json );
        // }.bind(this), null ,false)
        this.values = ( this.data.viewablePersonList || [] )
            .combine( this.data.viewableUnitList || [])
            .combine( this.data.viewableGroupList || [] );
        if( callback )callback();
    },
    selectData: function(array){
        this.data.viewablePersonList = [];
        this.data.viewableUnitList = [];
        this.data.viewableGroupList = [];
        array.each( function( a ){
            var dn = a.data.distinguishedName;
            var flag = dn.substr(dn.length-1, 1);
            switch (flag.toLowerCase()){
                case "p":
                    this.data.viewablePersonList.push( dn );
                    break;
                case "u":
                    this.data.viewableUnitList.push( dn );
                    break;
                case "g":
                    this.data.viewableGroupList.push( dn );
                    break;
            }
        }.bind(this));
        this.saveData( this.data, function(){
            this.listData( function(){
                this.loadOrg();
            }.bind(this));
        }.bind(this))
    },
    saveData: function( data, callback ){
        o2.Actions.load("x_cms_assemble_control").AppInfoAction
            .updatePermission(this.options.id, data, function (json) {
            if(callback)callback()
        }.bind(this));
    }
});

TMPermissionView.CMSAppPublisher = new Class({
    Extends: TMPermissionView.CMSAppViewer,
    options: {
        title: MWF.xApplication.ThreeMember.LP.publisher
    },
    listData: function( callback ){
        this.values = ( this.data.publishablePersonList || [] )
            .combine( this.data.publishableUnitList || [])
            .combine( this.data.publishableGroupList || [] );
        if( callback )callback();
    },
    selectData: function(array){
        this.data.publishablePersonList = [];
        this.data.publishableUnitList = [];
        this.data.publishableGroupList = [];
        array.each( function( a ){
            var dn = a.data.distinguishedName;
            var flag = dn.substr(dn.length-1, 1);
            switch (flag.toLowerCase()){
                case "p":
                    this.data.publishablePersonList.push( dn );
                    break;
                case "u":
                    this.data.publishableUnitList.push( dn );
                    break;
                case "g":
                    this.data.publishableGroupList.push( dn );
                    break;
            }
        }.bind(this));
        this.saveData( this.data, function(){
            this.listData( function(){
                this.loadOrg();
            }.bind(this));
        }.bind(this))
    },
    saveData: function( data, callback ){
        o2.Actions.load("x_cms_assemble_control").AppInfoAction
            .updatePermission(this.options.id, data, function (json) {
                if(callback)callback()
            }.bind(this));
    }
    // listData: function (callback) {
    //     o2.Actions.load("x_cms_assemble_control").PermissionAction.listAppInfoPublishers(this.options.id, function (json) {
    //         this.data = json.data;
    //         this.values = (this.data.personList || []).combine(this.data.unitList || []).combine(this.data.groupList || []);
    //         if (callback) callback(json);
    //     }.bind(this), null, false)
    // },
    // saveData: function (data, callback) {
    //     o2.Actions.load("x_cms_assemble_control").PermissionAction.saveAppInfoPublisher(this.options.id, data, function (json) {
    //         if (callback) callback()
    //     }.bind(this));
    // }
});

TMPermissionView.CMSAppManager = new Class({
    Extends: TMPermissionView.CMSAppViewer,
    options: {
        title: MWF.xApplication.ThreeMember.LP.manager
    },
    listData: function( callback ){
        this.values = ( this.data.manageablePersonList || [] )
            .combine( this.data.manageableUnitList || [])
            .combine( this.data.manageableGroupList || [] );
        if( callback )callback();
    },
    selectData: function(array){
        this.data.manageablePersonList = [];
        this.data.manageableUnitList = [];
        this.data.manageableGroupList = [];
        array.each( function( a ){
            var dn = a.data.distinguishedName;
            var flag = dn.substr(dn.length-1, 1);
            switch (flag.toLowerCase()){
                case "p":
                    this.data.manageablePersonList.push( dn );
                    break;
                case "u":
                    this.data.manageableUnitList.push( dn );
                    break;
                case "g":
                    this.data.manageableGroupList.push( dn );
                    break;
            }
        }.bind(this));
        this.saveData( this.data, function(){
            this.listData( function(){
                this.loadOrg();
            }.bind(this));
        }.bind(this))
    },
    saveData: function( data, callback ){
        o2.Actions.load("x_cms_assemble_control").AppInfoAction
            .updatePermission(this.options.id, data, function (json) {
                if(callback)callback()
            }.bind(this));
    }
    // listData: function (callback) {
    //     o2.Actions.load("x_cms_assemble_control").PermissionAction.listAppInfoManagers(this.options.id, function (json) {
    //         this.data = json.data;
    //         this.values = (this.data.personList || []).combine(this.data.unitList || []).combine(this.data.groupList || []);
    //         if (callback) callback(json);
    //     }.bind(this), null, false)
    // },
    // saveData: function (data, callback) {
    //     o2.Actions.load("x_cms_assemble_control").PermissionAction.saveAppInfoPublisher(this.options.id, data, function (json) {
    //         if (callback) callback()
    //     }.bind(this));
    // }
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
    listData: function( callback ){
        this.values = ( this.data.viewablePersonList || [] )
            .combine( this.data.viewableUnitList || [])
            .combine( this.data.viewableGroupList || [] );
        if( callback )callback();
    },
    selectData: function(array){
        this.data.viewablePersonList = [];
        this.data.viewableUnitList = [];
        this.data.viewableGroupList = [];
        array.each( function( a ){
            var dn = a.data.distinguishedName;
            var flag = dn.substr(dn.length-1, 1);
            switch (flag.toLowerCase()){
                case "p":
                    this.data.viewablePersonList.push( dn );
                    break;
                case "u":
                    this.data.viewableUnitList.push( dn );
                    break;
                case "g":
                    this.data.viewableGroupList.push( dn );
                    break;
            }
        }.bind(this));
        this.saveData( this.data, function(){
            this.listData( function(){
                this.loadOrg();
            }.bind(this));
        }.bind(this))
    },
    saveData: function( data, callback ){
        o2.Actions.load("x_cms_assemble_control").CategoryInfoAction
            .updatePermission(this.options.id, data, function (json) {
                if(callback)callback()
            }.bind(this));
    }
    // listData: function (callback) {
    //     o2.Actions.load("x_cms_assemble_control").PermissionAction.listCategoryInfoViewers(this.options.id, function (json) {
    //         this.data = json.data;
    //         this.values = (this.data.personList || []).combine(this.data.unitList || []).combine(this.data.groupList || []);
    //         if (callback) callback(json);
    //     }.bind(this), null, false)
    // },
    // saveData: function (data, callback) {
    //     o2.Actions.load("x_cms_assemble_control").PermissionAction.saveCategoryInfoViewer(this.options.id, data, function (json) {
    //         if (callback) callback()
    //     }.bind(this));
    // }
});

TMPermissionView.CMSCatePublisher = new Class({
    Extends: TMPermissionView.CMSCateViewer,
    options: {
        title: MWF.xApplication.ThreeMember.LP.publisher
    },
    listData: function( callback ){
        this.values = ( this.data.publishablePersonList || [] )
            .combine( this.data.publishableUnitList || [])
            .combine( this.data.publishableGroupList || [] );
        if( callback )callback();
    },
    selectData: function(array){
        this.data.publishablePersonList = [];
        this.data.publishableUnitList = [];
        this.data.publishableGroupList = [];
        array.each( function( a ){
            var dn = a.data.distinguishedName;
            var flag = dn.substr(dn.length-1, 1);
            switch (flag.toLowerCase()){
                case "p":
                    this.data.publishablePersonList.push( dn );
                    break;
                case "u":
                    this.data.publishableUnitList.push( dn );
                    break;
                case "g":
                    this.data.publishableGroupList.push( dn );
                    break;
            }
        }.bind(this));
        this.saveData( this.data, function(){
            this.listData( function(){
                this.loadOrg();
            }.bind(this));
        }.bind(this))
    },
    saveData: function( data, callback ){
        o2.Actions.load("x_cms_assemble_control").CategoryInfoAction
            .updatePermission(this.options.id, data, function (json) {
                if(callback)callback()
            }.bind(this));
    }
    // listData: function (callback) {
    //     o2.Actions.load("x_cms_assemble_control").PermissionAction.listCategoryInfoPublishers(this.options.id, function (json) {
    //         this.data = json.data;
    //         this.values = (this.data.personList || []).combine(this.data.unitList || []).combine(this.data.groupList || []);
    //         if (callback) callback(json);
    //     }.bind(this), null, false)
    // },
    // saveData: function (data, callback) {
    //     o2.Actions.load("x_cms_assemble_control").PermissionAction.saveCategoryInfoPublisher(this.options.id, data, function (json) {
    //         if (callback) callback()
    //     }.bind(this));
    // }
});

TMPermissionView.CMSCateManager = new Class({
    Extends: TMPermissionView.CMSCateViewer,
    options: {
        title: MWF.xApplication.ThreeMember.LP.manager
    },
    listData: function( callback ){
        this.values = ( this.data.manageablePersonList || [] )
            .combine( this.data.manageableUnitList || [])
            .combine( this.data.manageableGroupList || [] );
        if( callback )callback();
    },
    selectData: function(array){
        this.data.manageablePersonList = [];
        this.data.manageableUnitList = [];
        this.data.manageableGroupList = [];
        array.each( function( a ){
            var dn = a.data.distinguishedName;
            var flag = dn.substr(dn.length-1, 1);
            switch (flag.toLowerCase()){
                case "p":
                    this.data.manageablePersonList.push( dn );
                    break;
                case "u":
                    this.data.manageableUnitList.push( dn );
                    break;
                case "g":
                    this.data.manageableGroupList.push( dn );
                    break;
            }
        }.bind(this));
        this.saveData( this.data, function(){
            this.listData( function(){
                this.loadOrg();
            }.bind(this));
        }.bind(this))
    },
    saveData: function( data, callback ){
        o2.Actions.load("x_cms_assemble_control").CategoryInfoAction
            .updatePermission(this.options.id, data, function (json) {
                if(callback)callback()
            }.bind(this));
    }
    // listData: function (callback) {
    //     o2.Actions.load("x_cms_assemble_control").PermissionAction.listCategoryInfoManagers(this.options.id, function (json) {
    //         this.data = json.data;
    //         this.values = (this.data.personList || []).combine(this.data.unitList || []).combine(this.data.groupList || []);
    //         if (callback) callback(json);
    //     }.bind(this), null, false)
    // },
    // saveData: function (data, callback) {
    //     o2.Actions.load("x_cms_assemble_control").PermissionAction.saveCategoryInfoManager(this.options.id, data, function (json) {
    //         if (callback) callback()
    //     }.bind(this));
    // }
});

TMPermissionView.ProcessAppUser = new Class({
    Extends: TMPermissionView.CMSAppViewer,
    options: {
        orgTypes: ["identity","unit"],
        title: MWF.xApplication.ThreeMember.LP.viewer
    },
    selectData: function(array){
        this.data.availableIdentityList = [];
        this.data.availableUnitList = [];
        this.data.availableGroupList = [];
        array.each( function( a ){
            var dn = a.data.distinguishedName;
            var flag = dn.substr(dn.length-1, 1);
            switch (flag.toLowerCase()){
                case "i":
                    this.data.availableIdentityList.push( dn );
                    break;
                case "u":
                    this.data.availableUnitList.push( dn );
                    break;
                case "g":
                    this.data.availableGroupList.push( dn );
                    break;
            }
        }.bind(this));
        this.saveData( this.data, function(){
            this.values = ( this.data.availableIdentityList || [] )
                .combine( this.data.availableUnitList || [])
                .combine( this.data.availableGroupList || []);
            this.loadOrg();
        }.bind(this))
    },
    listData: function (callback) {
        this.values = ( this.data.availableIdentityList || [] )
            .combine( this.data.availableUnitList || [])
            .combine( this.data.availableGroupList || []);
        if( callback )callback();
    },
    saveData: function (data, callback) {
        o2.Actions.load("x_processplatform_assemble_designer").ApplicationAction
            .updatePermission(this.data.id, data, function (json) {
            if (callback) callback()
        }.bind(this));
    }
});

TMPermissionView.ProcessAppManager = new Class({
    Extends: TMPermissionView.ProcessAppUser,
    options: {
        orgTypes: ["person"],
        title: MWF.xApplication.ThreeMember.LP.manager
    },
    selectData: function(array){
        this.data.controllerList = [];
        array.each( function( a ){
            var dn = a.data.distinguishedName;
            var flag = dn.substr(dn.length-1, 1);
            switch (flag.toLowerCase()){
                case "p":
                    this.data.controllerList.push( dn );
                    break;
            }
        }.bind(this));
        this.saveData( this.data, function(){
            this.values = this.data.controllerList || [];
            this.loadOrg();
        }.bind(this))
    },
    listData: function (callback) {
        this.values = this.data.controllerList || [];
        if( callback )callback();
    },
    saveData: function (data, callback) {
        o2.Actions.load("x_processplatform_assemble_designer").ApplicationAction.edit(this.data.id, data, function (json) {
            if (callback) callback()
        }.bind(this));
    }
});


TMPermissionView.ProcessProcessStarter = new Class({
    Extends: TMPermissionView.CMSCateViewer,
    options: {
        orgTypes: ["identity","unit", "group"],
        title: MWF.xApplication.ThreeMember.LP.starter
    },
    selectData: function(array){
        this.data.startableIdentityList = [];
        this.data.startableUnitList = [];
        this.data.startableGroupList = [];
        array.each( function( a ){
            var dn = a.data.distinguishedName;
            var flag = dn.substr(dn.length-1, 1);
            switch (flag.toLowerCase()){
                case "i":
                    this.data.startableIdentityList.push( dn );
                    break;
                case "u":
                    this.data.startableUnitList.push( dn );
                    break;
                case "g":
                    this.data.startableGroupList.push( dn );
                    break;
            }
        }.bind(this));
        this.saveData( this.data, function(){
            this.values = (this.data.startableIdentityList || [])
                .combine(this.data.startableUnitList || [])
                .combine(this.data.startableGroupList || []);
            this.loadOrg();
        }.bind(this))
    },
    listData: function (callback) {
        this.values = (this.data.startableIdentityList || [])
            .combine(this.data.startableUnitList || [])
            .combine(this.data.startableGroupList || []);
        if (callback) callback();
    },
    saveData: function (data, callback) {
        o2.Actions.load("x_processplatform_assemble_designer").ProcessAction
            .updatePermission(this.options.id, data, function (json) {
            if (callback) callback()
        }.bind(this));
    }
});

TMPermissionView.ProcessProcessManager = new Class({
    Extends: TMPermissionView.CMSCateViewer,
    options: {
        orgTypes: ["person"],
        title: MWF.xApplication.ThreeMember.LP.manager
    },
    selectData: function(array){
        this.data.controllerList = [];
        array.each( function( a ){
            var dn = a.data.distinguishedName;
            var flag = dn.substr(dn.length-1, 1);
            switch (flag.toLowerCase()){
                case "p":
                    this.data.controllerList.push( dn );
                    break;
            }
        }.bind(this));
        this.saveData( this.data, function(){
            this.values = this.data.controllerList || [];
            this.loadOrg();
        }.bind(this))
    },
    listData: function (callback) {
        this.values = this.data.controllerList || [];
        if (callback) callback();
    },
    saveData: function (data, callback) {
        o2.Actions.load("x_processplatform_assemble_designer").ProcessAction
            .updatePermission(this.options.id, data, function (json) {
            if (callback) callback()
        }.bind(this));
    }
});


TMPermissionView.PortalAppUser = new Class({
    Extends: TMPermissionView.ProcessAppUser,
    saveData: function (data, callback) {
        o2.Actions.load("x_portal_assemble_designer").PortalAction
            .updatePermission(this.data.id, data, function (json) {
            if (callback) callback()
        }.bind(this));
    }
});

TMPermissionView.PortalAppManager = new Class({
    Extends: TMPermissionView.ProcessAppManager,
    saveData: function (data, callback) {
        o2.Actions.load("x_portal_assemble_designer").PortalAction
            .updatePermission(this.data.id, data, function (json) {
            if (callback) callback()
        }.bind(this));
    }
});

TMPermissionView.QueryAppUser = new Class({
    Extends: TMPermissionView.ProcessAppUser,
    saveData: function (data, callback) {
        o2.Actions.load("x_query_assemble_designer").QueryAction
            .updatePermission(this.data.id, data, function (json){
            if (callback) callback()
        }.bind(this));
    }
});

TMPermissionView.QueryAppManager = new Class({
    Extends: TMPermissionView.ProcessAppManager,
    saveData: function (data, callback) {
        o2.Actions.load("x_query_assemble_designer").QueryAction
            .updatePermission(this.data.id, data, function (json) {
            if (callback) callback()
        }.bind(this));
    }
});

TMPermissionView.QueryViewExecutor = new Class({
    Extends: TMPermissionView.CMSCateViewer,
    options: {
        orgTypes: ["identity","unit"],
        title: MWF.xApplication.ThreeMember.LP.executor
    },
    selectData: function(array){
        this.data.availableIdentityList = [];
        this.data.availableUnitList = [];
        this.data.availableGroupList = [];
        array.each( function( a ){
            var dn = a.data.distinguishedName;
            var flag = dn.substr(dn.length-1, 1);
            switch (flag.toLowerCase()){
                case "i":
                    this.data.availableIdentityList.push( dn );
                    break;
                case "u":
                    this.data.availableUnitList.push( dn );
                    break;
                case "g":
                    this.data.availableGroupList.push( dn );
                    break;
            }
        }.bind(this));
        this.saveData( this.data, function(){
            this.values = (this.data.availableIdentityList || [])
                .combine(this.data.availableUnitList || [])
                .combine(this.data.availableGroupList || []);
            this.loadOrg();
        }.bind(this))
    },
    listData: function (callback) {
        this.values = (this.data.availableIdentityList || [])
            .combine(this.data.availableUnitList || [])
            .combine(this.data.availableGroupList || []);
        if (callback) callback();
    },
    saveData: function (data, callback) {
        o2.Actions.load("x_query_assemble_designer").ViewAction
            .updatePermission(this.options.id, data, function (json) {
            if (callback) callback()
        }.bind(this));
    }
});


TMPermissionView.QueryStatExecutor = new Class({
    Extends: TMPermissionView.QueryViewExecutor,
    saveData: function (data, callback) {
        o2.Actions.load("x_query_assemble_designer").StatAction
            .updatePermission(this.options.id, data, function (json) {
            if (callback) callback()
        }.bind(this));
    }
});

TMPermissionView.QueryImportModelExecutor = new Class({
    Extends: TMPermissionView.QueryViewExecutor,
    saveData: function (data, callback) {
        o2.Actions.load("x_query_assemble_designer").ImportModelAction
            .updatePermission(this.options.id, data, function (json) {
            if (callback) callback()
        }.bind(this));
    }
});

TMPermissionView.QueryTableReader = new Class({
    Extends: TMPermissionView.CMSCateViewer,
    options: {
        orgTypes: ["person","unit"],
        title: MWF.xApplication.ThreeMember.LP.dataViewer
    },
    selectData: function(array){
        this.data.readPersonList = [];
        this.data.readUnitList = [];
        this.data.readGroupList = [];
        array.each( function( a ){
            var dn = a.data.distinguishedName;
            var flag = dn.substr(dn.length-1, 1);
            switch (flag.toLowerCase()){
                case "p":
                    this.data.readPersonList.push( dn );
                    break;
                case "u":
                    this.data.readUnitList.push( dn );
                    break;
                case "g":
                    this.data.readGroupList.push( dn );
                    break;
            }
        }.bind(this));
        this.saveData( this.data, function(){
            this.values = (this.data.readPersonList || [])
                .combine(this.data.readUnitList || [])
                .combine(this.data.readGroupList || []);
            this.loadOrg();
        }.bind(this))
    },
    listData: function (callback) {
        this.values = (this.data.readPersonList || [])
            .combine(this.data.readUnitList || [])
            .combine(this.data.readGroupList || []);
        if (callback) callback();
    },
    saveData: function (data, callback) {
        o2.Actions.load("x_query_assemble_designer").TableAction
            .updatePermission(this.options.id, data, function (json) {
            if (callback) callback()
        }.bind(this));
    }
});

TMPermissionView.QueryTableEditor = new Class({
    Extends: TMPermissionView.CMSCateViewer,
    options: {
        orgTypes: ["person","unit"],
        title: MWF.xApplication.ThreeMember.LP.dataEditor
    },
    selectData: function(array){
        this.data.editPersonList = [];
        this.data.editUnitList = [];
        this.data.editGroupList = [];
        array.each( function( a ){
            var dn = a.data.distinguishedName;
            var flag = dn.substr(dn.length-1, 1);
            switch (flag.toLowerCase()){
                case "p":
                    this.data.editPersonList.push( dn );
                    break;
                case "u":
                    this.data.editUnitList.push( dn );
                    break;
                case "g":
                    this.data.editGroupList.push( dn );
                    break;
            }
        }.bind(this));
        this.saveData( this.data, function(){
            this.values = (this.data.editPersonList || [])
                .combine(this.data.editUnitList || [])
                .combine(this.data.editGroupList || []);
            this.loadOrg();
        }.bind(this))
    },
    listData: function (callback) {
        this.values = (this.data.editPersonList || [])
            .combine(this.data.editUnitList || [])
            .combine(this.data.editGroupList || []);
        if (callback) callback();
    },
    saveData: function (data, callback) {
        o2.Actions.load("x_query_assemble_designer").TableAction
            .updatePermission(this.options.id, data, function (json) {
            if (callback) callback()
        }.bind(this));
    }
});

TMPermissionView.QueryStatementExecutor = new Class({
    Extends: TMPermissionView.CMSCateViewer,
    options: {
        orgTypes: ["person","unit"],
        title: MWF.xApplication.ThreeMember.LP.executor
    },
    selectData: function(array){
        this.data.executePersonList = [];
        this.data.executeUnitList = [];
        this.data.executeGroupList = [];
        array.each( function( a ){
            var dn = a.data.distinguishedName;
            var flag = dn.substr(dn.length-1, 1);
            switch (flag.toLowerCase()){
                case "p":
                    this.data.executePersonList.push( dn );
                    break;
                case "u":
                    this.data.executeUnitList.push( dn );
                    break;
                case "g":
                    this.data.executeGroupList.push( dn );
                    break;
            }
        }.bind(this));
        this.saveData( this.data, function(){
            this.values = (this.data.executePersonList || [])
                .combine(this.data.executeUnitList || [])
                .combine(this.data.executeGroupList || []);
            this.loadOrg();
        }.bind(this))
    },
    listData: function (callback) {
        this.values = (this.data.executePersonList || [])
            .combine(this.data.executeUnitList || [])
            .combine(this.data.executeGroupList || []);
        if (callback) callback();
    },
    saveData: function (data, callback) {
        o2.Actions.load("x_query_assemble_designer").StatementAction
            .updatePermission(this.options.id, data, function (json) {
            if (callback) callback()
        }.bind(this));
    }
});