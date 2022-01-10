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
    load: function(){
        this.createNode();
        this.loadLayout();
    },

    reload: function () {
        this.clearContent();
        this.loadLayout();
    },
    createNode: function () {
        this.content.setStyle("overflow", "hidden");
        this.node = new Element("div", {
            "styles": this.css.node
        }).inject(this.content);
    },
    loadLayout: function () {
        this.naviNode = new Element("div.naviNode", {
            "styles": this.css.naviNode
        }).inject(this.node);
        this.contentContainerNode = new Element("div.contentContainerNode", {
            "styles": this.css.contentContainerNode
        }).inject(this.node);
        this.createTopNode();
        this.createContainerNode();
        this.loaNavi();

    },
    loaNavi: function (callback) {
        debugger;
        var naviOpt = {};
        naviOpt.module = this.options.module || "all";
        naviOpt.operation = this.options.operation;
        this.navi = new MWF.xApplication.ThreeMember.PermissionView.Navi(this, this.naviNode, naviOpt);
    },
    createTopNode: function () {
        this.topContainerNode = new Element("div.topContainerNode", {
            "styles": this.css.topContainerNode
        }).inject(this.contentContainerNode);

        this.topNode = new Element("div.topNode", {
            "styles": this.css.topNode
        }).inject(this.topContainerNode);

        this.topContentNode = new Element("div", {
            "styles": this.css.topContentNode
        }).inject(this.topNode);

        this.loadFilter();

    },
    createContainerNode: function () {
        this.createContent();
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

        h = h - this.getOffsetY(this.viewContainerNode);

        var pageSize = (this.view && this.view.pagingContainerBottom) ? this.view.pagingContainerBottom.getComputedSize() : {totalHeight: 0};
        h = h - pageSize.totalHeight;

        // this.viewContainerNode.setStyle("height", "" + h + "px");
        this.view.viewWrapNode.setStyles({
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
            //this.middleNode.destroy();
            //this.contentNode.destroy();
        }
    },
    loadFilter: function () {
        var lp = MWF.xApplication.ThreeMember.LP;
        this.fileterNode = new Element("div.fileterNode", {
            "styles": this.css.fileterNode
        }).inject(this.topContentNode);

        var html = "<table width='100%' bordr='0' cellpadding='0' cellspacing='0' styles='filterTable'>" + //style='width: 900px;'
            "<tr>" +
            "    <td styles='filterTableTitle' lable='person'></td>" +
            "    <td styles='filterTableValue' item='person'></td>" +
            "    <td styles='filterTableTitle' lable='module'></td>" +
            "    <td styles='filterTableValue' item='module'></td>" +
            "    <td styles='filterTableTitle' lable='operation'></td>" +
            "    <td styles='filterTableValue' item='operation'></td>" +
            "    <td styles='filterTableTitle' lable='startTime'></td>" +
            "    <td styles='filterTableValue' item='startTime' style='width: 150px;'></td>" +
            "    <td styles='filterTableTitle' lable='endTime'></td>" +
            "    <td styles='filterTableValue' item='endTime' style='width: 150px;'></td>" +
            "    <td styles='filterTableValue' item='action'></td>" +
            "    <td styles='filterTableValue' item='reset'></td>" +
            "</tr>" +
            "</table>";
        this.fileterNode.set("html", html);


        this.form = new MForm(this.fileterNode, {}, {
            style: "attendance",
            isEdited: true,
            itemTemplate: {
                person: {
                    "text": lp.person,
                    "type": "org",
                    "orgType": "identity",
                    "orgOptions": {"resultType": "person"},
                    "style": {"min-width": "100px"},
                    "orgWidgetOptions": {"disableInfor": true}
                },
                module: {
                    "text": lp.module,
                    "type": "select",
                    "style": {"max-width": "150px"},
                    "selectValue": function () {
                        var array = [""];
                        o2.Actions.load("x_auditlog_assemble_control").AuditConfigAction.listModule(function (json) {
                            array = array.concat(json.data.valueList);
                        }.bind(this), null, false);
                        return array;
                    },
                    "event": {
                        "change": function (item, ev) {
                            var array;
                            var v = item.getValue();
                            if (v) {
                                o2.Actions.load("x_auditlog_assemble_control").AuditConfigAction.listOperation(v, function (json) {
                                    array = [""].concat(json.data.valueList);
                                }.bind(this), null, false);
                            } else {
                                array = [];
                            }
                            item.form.getItem("operation").resetItemOptions(array, array)
                        }.bind(this)
                    }
                },
                operation: {text: lp.operation, "type": "select", "style": {"max-width": "150px"}, "selectValue": []},
                startTime: {
                    text: lp.startTime,
                    "tType": "datetime",
                    "calendarOptions": {"secondEnable": true, "format": "db"}
                },
                endTime: {
                    text: lp.endTime,
                    "tType": "datetime",
                    "calendarOptions": {"secondEnable": true, "format": "db"}
                },
                action: {
                    "value": lp.query, type: "button", className: "filterButton", event: {
                        click: function () {
                            var result = this.form.getResult(false, null, false, true, false);
                            for (var key in result) {
                                if (!result[key]) {
                                    delete result[key];
                                } else if (key === "person" && result[key].length > 0) {
                                    result[key] = result[key][0].split("@")[1];
                                }
                            }
                            this.loadView(result);
                        }.bind(this)
                    }
                },
                reset: {
                    "value": lp.reset, type: "button", className: "filterButtonGrey", event: {
                        click: function () {
                            this.form.reset();
                            this.loadView();
                        }.bind(this)
                    }
                },
            }
        }, this, this.css);
        this.form.load();
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
        "module": "all"
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

        [
            {"id":"process", "text": this.explorer.lp.process, "action": o2.Actions.load("x_processplatform_assemble_designer").ApplicationAction.list },
            {"id":"cms", "text": this.explorer.lp.cms, "action": o2.Actions.load("x_cms_assemble_control").AppInfoAction.listAllAppInfo },
            {"id":"portal", "text": this.explorer.lp.portal, "action": o2.Actions.load("x_portal_assemble_designer").PortalAction.list },
            {"id":"query", "text": this.explorer.lp.query1, "action": o2.Actions.load("x_query_assemble_designer").QueryAction.listAll }
        ].each(function (category) {
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
    setCurrentAll: function () {
        this.cancelCurrent();
        this.currentStatus = null;
        this.currentAll = this.naviAllNode;
        this.naviAllNode.setStyles(this.css.naviAllNode_current);
        if (this.explorer.form) {
            this.explorer.form.reset();
            this.explorer.form.getItem("module").items[0].fireEvent("change");
        }
        this.explorer.loadView()
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
                _self.setCurrentMenu(menuObj);
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
        if (this.currentMenu) {
            this.currentMenu.setStyles(this.css.naviMenuNode);
            this.currentMenu.setStyles(this.css.naviMenuNode_normal);
            this.currentMenu = false;
        }
        if (this.currentItem) {
            this.currentItem.setStyles(this.css.naviItemNode);
            this.currentItem = false;
        }
        if (this.currentAll) {
            this.currentAll.setStyles(this.css.naviAllNode_normal);
            this.currentAll = false;
        }
        this.currentStatus = null;
    },
    setCurrentMenu: function (menuObj) {
        this.cancelCurrent();
        this.currentStatus = {
            module: menuObj.module
        };
        this.currentMenu = menuObj.node;
        menuObj.node.setStyles(this.css.naviMenuNode_current);
        this.explorer.form.reset();
        this.explorer.form.getItem("module").setValue(menuObj.module);
        debugger;
        this.explorer.form.getItem("module").items[0].fireEvent("change");
        this.explorer.loadView({"module": menuObj.module})
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

        if (application.id === this.options.applicationId) {
            itemNode.click();
        }
    },
    setCurrentItem: function (itemObj) {
        this.cancelCurrent();
        this.currentStatus = {
            module: itemObj.module,
            operation: itemObj.operation
        };
        this.currentItem = itemObj.node;
        itemObj.node.setStyles(this.css.naviItemNode_current);
        this.explorer.form.reset();
        this.explorer.form.getItem("module").setValue(itemObj.module);
        this.explorer.form.getItem("module").items[0].fireEvent("change");
        this.explorer.form.getItem("operation").setValue(itemObj.operation);
        this.explorer.loadView({
            "module": itemObj.module,
            "operation": itemObj.operation
        })
    },
    setContentSize: function () {
        var nodeSize = this.explorer.node.getSize();
        var h = nodeSize.y - this.explorer.getOffsetY(this.explorer.node);
        this.node.setStyle("height", h);
        this.scrollNode.setStyle("height", h);
    }
});