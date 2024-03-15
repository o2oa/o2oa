MWF.xApplication.ThreeMember = MWF.xApplication.ThreeMember || {};
MWF.require("MWF.widget.O2Identity", null, false);
//MWF.xDesktop.requireApp("ThreeMember", "Access", null, false);
//MWF.xDesktop.requireApp("ThreeMember", "Actions.RestActions", null, false);
MWF.xDesktop.requireApp("Template", "Explorer", null, false);
MWF.xDesktop.requireApp("Template", "MForm", null, false);

MWF.xApplication.ThreeMember.LogView = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],
    options: {
        "style": "default",
        "viewPageNum": 1,
        "module": "",
        "operation": "",
        "filterModule": false,
        "title": MWF.xApplication.ThreeMember.LP.title
    },
    initialize: function(node, app, options){
        this.setOptions(options);

        this.path = "../x_component_ThreeMember/$LogView/";
        this.cssPath = "../x_component_ThreeMember/$LogView/"+this.options.style+"/css.wcss";
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
        this.navi = new MWF.xApplication.ThreeMember.LogView.Navi(this, this.naviNode, naviOpt);
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

        this.loadLogFilter();

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
    toConfig: function(){
        if (this.view) {
            this.view.destroy();
            this.view = null;
        }
        this.configNavi = null;
        this.naviNode.empty();
        this.topContentNode.empty();
        this.contentNode.empty();
        this.createConfigAction = new Element("button", {
            "type": "button",
            "text": this.lp.createConfig,
            "styles": this.css.createConfigAction,
            "events": {
                click: function () {
                    this.view._create();
                }.bind(this)
            }
        }).inject( this.topContentNode, "before" );
        this.loadConfigFilter();
        this.navi = new MWF.xApplication.ThreeMember.LogView.ConfigNavi(this, this.naviNode, {});
        this.configNavi = this.navi;
    },
    toView: function(){
        if (this.view) {
            this.view.destroy();
            this.view = null;
        }
        this.configNavi = null;
        this.naviNode.empty();
        this.topContentNode.empty();
        this.contentNode.empty();
        if(this.createConfigAction){
            this.createConfigAction.destroy();
            this.createConfigAction = null;
        }
        this.loadLogFilter();
        this.navi = new MWF.xApplication.ThreeMember.LogView.Navi(this, this.naviNode, {});
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
    loadView: function (filterData) {
        if (this.view) this.view.destroy();
        this.contentNode.empty();
        var viewContainerNode = this.viewContainerNode = new Element("div.viewContainerNode", {
            "styles": this.css.viewContainerNode
        }).inject(this.contentNode);

        this.view = new MWF.xApplication.ThreeMember.LogView.View(viewContainerNode, this.app, this, {
            templateUrl: this.path + this.options.style + "/listItem.json",
            "pagingEnable": true,
            "wrapView": true,
            "noItemText": this.lp.noItem,
            // "scrollType": "window",
            "pagingPar": {
                pagingBarUseWidget: true,
                position: ["bottom"],
                style: "blue_round",
                hasReturn: false,
                currentPage: this.options.viewPageNum,
                countPerPage: 15,
                visiblePages: 9,
                hasNextPage: true,
                hasPrevPage: true,
                hasTruningBar: true,
                hasJumper: true,
                returnText: "",
                hiddenWithDisable: false,
                text: {
                    prePage: "",
                    nextPage: "",
                    firstPage: this.lp.firstPage,
                    lastPage: this.lp.lastPage
                },
                onPostLoad: function () {
                    debugger;
                    this.setContentSize();
                }.bind(this)
            }
        }, {
            lp: this.lp
        });
        var fData = filterData || {};
        if( !fData.startTime )fData.startTime = this.form.getItem("startTime").getValue();
        if( !fData.endTime )fData.endTime = this.form.getItem("endTime").getValue();
        Object.each( (this.navi ? this.navi.currentStatus : {}) || {}, function (value, key) {
            fData[key] = value;
        });
        this.view.filterData = fData;
        this.view.load();
    },
    loadConfigView: function(filterData){
        if (this.view) this.view.destroy();
        this.contentNode.empty();
        var viewContainerNode = this.viewContainerNode = new Element("div.viewContainerNode", {
            "styles": this.css.viewContainerNode
        }).inject(this.contentNode);

        this.view = new MWF.xApplication.ThreeMember.LogView.ConfigView(viewContainerNode, this.app, this, {
            templateUrl: this.path + this.options.style + "/listItemConfig.json",
            "pagingEnable": true,
            "wrapView": true,
            "noItemText": this.lp.noItem,
            // "scrollType": "window",
            "pagingPar": {
                pagingBarUseWidget: true,
                position: ["bottom"],
                style: "blue_round",
                hasReturn: false,
                currentPage: this.options.viewPageNum,
                countPerPage: 15,
                visiblePages: 9,
                hasNextPage: true,
                hasPrevPage: true,
                hasTruningBar: true,
                hasJumper: true,
                returnText: "",
                hiddenWithDisable: false,
                text: {
                    prePage: "",
                    nextPage: "",
                    firstPage: this.lp.firstPage,
                    lastPage: this.lp.lastPage
                },
                onPostLoad: function () {
                    debugger;
                    this.setContentSize();
                }.bind(this)
            }
        }, {
            lp: this.lp
        });
        var fData = filterData || {};
        Object.each( (this.configNavi ? this.configNavi.currentStatus : {}) || {}, function (value, key) {
            fData[key] = value;
        });
        this.view.filterData = fData;
        this.view.load();
    },
    clear: function () {
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
        this.node.destroy();
    },
    loadConfigFilter: function () {
        var lp = MWF.xApplication.ThreeMember.LP;
        this.fileterNode = new Element("div.fileterNode", {
            "styles": this.css.fileterNode
        }).inject(this.topContentNode);

        var html = "<table width='100%' bordr='0' cellpadding='0' cellspacing='0' styles='filterTable'>" + //style='width: 900px;'
            "<tr>";
        if( this.options.filterModule ) {
            html +=   "<td styles='filterTableTitle' lable='module'></td>" +
                "    <td styles='filterTableValue' item='module'></td>"+
            "    <td styles='filterTableTitle' lable='operation'></td>" +
            "    <td styles='filterTableValue' item='operation'></td>";
        }
        html +=
            "    <td styles='filterTableTitle' lable='httpType'></td>" +
            "    <td styles='filterTableValue' item='httpType'></td>"+
            "    <td styles='filterTableTitle' lable='matchUrl'></td>" +
            "    <td styles='filterTableValue' item='matchUrl'></td>"+
            "    <td styles='filterTableValue' item='action'></td>" +
            "    <td styles='filterTableValue' item='reset'></td>" +
            "</tr>" +
            "</table>";
        this.fileterNode.set("html", html);


        this.configForm = new MForm(this.fileterNode, {}, {
            style: "attendance",
            isEdited: true,
            itemTemplate: {
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
                httpType: {text: lp.httpType, "type": "select", "selectValue": ["","GET","POST","PUT","DELETE"]},
                matchUrl: {
                    text: lp.httpUrl
                },
                action: {
                    "value": lp.query, type: "button", className: "filterButton", event: {
                        click: function () {
                            var result = this.configForm.getResult(false, null, false, true, false);
                            for (var key in result) {
                                if (!result[key]) {
                                    delete result[key];
                                } else if (key === "person" && result[key].length > 0) {
                                    result[key] = result[key][0].split("@")[1];
                                }
                            }
                            this.loadConfigView(result);
                        }.bind(this)
                    }
                },
                reset: {
                    "value": lp.reset, type: "button", className: "filterButtonGrey", event: {
                        click: function () {
                            this.configForm.reset();
                            this.loadConfigView();
                        }.bind(this)
                    }
                },
            }
        }, this, this.css);
        this.configForm.load();
    },
    loadLogFilter: function () {
        var lp = MWF.xApplication.ThreeMember.LP;
        this.fileterNode = new Element("div.fileterNode", {
            "styles": this.css.fileterNode
        }).inject(this.topContentNode);

        var html = "<table width='100%' bordr='0' cellpadding='0' cellspacing='0' styles='filterTable'>" + //style='width: 900px;'
            "<tr>";
            if( !o2.AC.isAuditManager() || o2.AC.isSystemManager() || this.app.managerEnabled ){
                html += "<td styles='filterTableTitle' lable='person'></td>" +
                "<td styles='filterTableValue' item='person'></td>";
            }
            if( this.options.filterModule ) {
                html +=   "<td styles='filterTableTitle' lable='module'></td>" +
                "    <td styles='filterTableValue' item='module'></td>" +
                "    <td styles='filterTableTitle' lable='operation'></td>" +
                "    <td styles='filterTableValue' item='operation'></td>";
            }
            html +=  "<td styles='filterTableTitle' lable='startTime'></td>" +
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
                    "defaultValue": new Date().decrement('day', 1).format("%Y-%m-%d") + " 00:00:00",
                    "calendarOptions": {"secondEnable": true, "format": "db", "clearEnable": false}
                },
                endTime: {
                    text: lp.endTime,
                    "tType": "datetime",
                    "defaultValue": new Date().format("%Y-%m-%d") + " 23:59:59",
                    "calendarOptions": {"secondEnable": true, "format": "db", "clearEnable": false}
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
        status.explorer = "logview";
        return status;
    }
});

MWF.xApplication.ThreeMember.LogView.Navi = new Class({
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
        this.naviActionNode = new Element("div.naviTopNode", {
            "styles": this.css.naviActionNode,
            "text": this.explorer.lp.syncLog,
            "events":{
                click: function () {
                    o2.Actions.load("x_auditlog_assemble_control").AuditLogAction.executeTodayDispatch(function(json){
                        if(json.data.value){
                            this.app.notice(this.explorer.lp.syncLogSuccess)
                        }
                    }.bind(this))
                }.bind(this)
            }
        }).inject(this.node);

        this.scrollNode = new Element("div.naviScrollNode", {"styles": this.css.naviScrollNode}).inject(this.node);
        this.areaNode = new Element("div.naviAreaNode", {"styles": this.css.naviAreaNode}).inject(this.scrollNode);

        if( !o2.AC.isAuditManager() || o2.AC.isSystemManager() || this.app.managerEnabled ){
            this.configNode = new Element("div.naviConfigNode", {
                "styles": this.css.naviConfigNode,
                "text": this.explorer.lp.logConfig,
                "events": {
                    "click": function () {
                        this.explorer.toConfig();
                    }.bind(this)
                }
            }).inject(this.node);

            new Element("div.naviConfigIconNode", {
                "styles": this.css.naviConfigIconNode
            }).inject(this.configNode, "top");
        }


        // this.naviTopNode = new Element("div.naviTopNode", {
        //     "styles": this.css.naviTopNode,
        //     "text": this.explorer.lp.title
        // }).inject(this.areaNode);

        this.createAllNode();

        o2.Actions.load("x_auditlog_assemble_control").AuditConfigAction.listModule(function (json) {
            json.data.valueList.each(function (text) {
                this.createMenuNode(text);
            }.bind(this));
            this.setContentSize();
            this.setContentSizeFun = this.setContentSize.bind(this);
            this.app.addEvent("resize", this.setContentSizeFun);
        }.bind(this));
    },
    destroy: function(){
        if(this.setContentSizeFun)this.app.removeEvent("resize", this.setContentSizeFun );
        this.scrollNode.destroy();
    },
    createAllNode: function () {
        var _self = this;
        this.naviAllNode = new Element("div.naviAllNode", {
            "styles": this.css.naviAllNode,
            "text": this.explorer.lp.all
        }).inject(this.areaNode);
        this.naviAllNode.addEvents({
            "mouseover": function () {
                if (_self.currentAll != this) this.setStyles(_self.explorer.css.naviAllNode_over);
            },
            "mouseout": function () {
                if (_self.currentAll != this) this.setStyles(_self.explorer.css.naviAllNode_normal);
            },
            "click": function (ev) {
                _self.setCurrentAll();
                ev.stopPropagation();
            }
        });
        if (this.options.module === "all") {
            this.naviAllNode.click();
        }
    },
    setCurrentAll: function () {
        this.cancelCurrent();
        this.currentStatus = null;
        this.currentAll = this.naviAllNode;
        this.naviAllNode.setStyles(this.css.naviAllNode_current);
        if (this.explorer.form) {
            // this.explorer.form.reset();
            if(this.explorer.options.filterModule){
                this.explorer.form.getItem("module").items[0].fireEvent("change");
            }
        }
        this.explorer.loadView()
    },
    createMenuNode: function (text) {
        var _self = this;
        var menuNode = new Element("div", {
            "styles": this.css.naviMenuNode
        });
        menuNode.setStyles(this.css.naviMenuNode_collapse);

        var iconNode = new Element("div", {
            "styles": this.css.naviMenuIconNode_collapse
        }).inject(menuNode);

        var textNode = new Element("div", {
            "styles": this.css.naviMenuTextNode,
            "text": text
        }).inject(menuNode);
        menuNode.inject(this.areaNode);

        var menuObj = {
            "module": text,
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
        if (this.options.module === text) {
            if (this.options.operation) {
                iconNode.click();
            } else {
                menuNode.click()
            }
        }
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
        // this.explorer.form.reset();
        if(this.explorer.options.filterModule) {
            this.explorer.form.getItem("module").setValue(menuObj.module);
            this.explorer.form.getItem("module").items[0].fireEvent("change");
        }
        this.explorer.loadView({"module": menuObj.module})
    },
    expandOrCollapse: function (menuObj) {
        if (!menuObj.itemContainer) {
            menuObj.itemContainer = new Element("div").inject(menuObj.node, "after");
            o2.Actions.load("x_auditlog_assemble_control").AuditConfigAction.listOperation(menuObj.module, function (json) {
                json.data.valueList.each(function (operation) {
                    this.createItemNode(operation, menuObj);
                }.bind(this))
            }.bind(this));
        } else {
            menuObj.itemContainer.setStyle("display", menuObj.collapse ? "" : "none");
        }
        menuObj.iconNode.setStyles(menuObj.collapse ? this.css.naviMenuIconNode_expand : this.css.naviMenuIconNode_collapse);
        menuObj.collapse = !menuObj.collapse;
    },
    createItemNode: function (operation, menuObj) {
        var _self = this;
        var itemNode = new Element("div", {
            "styles": this.css.naviItemNode
        });

        var itemObj = {
            "module": menuObj.module,
            "operation": operation,
            "node": itemNode
        };

        var textNode = new Element("div", {
            "styles": this.css.naviItemTextNode,
            "text": operation
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

        if (operation === this.options.operation) {
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
        //this.explorer.form.reset();
        if(this.explorer.options.filterModule) {
            this.explorer.form.getItem("module").setValue(itemObj.module);
            this.explorer.form.getItem("module").items[0].fireEvent("change");
            this.explorer.form.getItem("operation").setValue(itemObj.operation);
        }
        this.explorer.loadView({
            "module": itemObj.module,
            "operation": itemObj.operation
        })
    },
    setContentSize: function () {
        // var nodeSize = this.explorer.node.getSize();
        // var h = nodeSize.y - this.explorer.getOffsetY(this.explorer.node);
        // this.node.setStyle("height", h);
        //
        // if( this.naviActionNode ){
        //     h = h - this.naviActionNode.getSize().y - this.explorer.getOffsetY(this.naviActionNode);
        // }
        // this.scrollNode.setStyle("height", h);
    }
});

MWF.xApplication.ThreeMember.LogView.View = new Class({
    Extends: MWF.xApplication.Template.Explorer.ComplexView,
    _createDocument: function (data, index) {
        return new MWF.xApplication.ThreeMember.LogView.Document(this.viewNode, data, this.explorer, this, null, index);
    },
    _getCurrentPageData: function (callback, count, pageNum) {
        this.clearBody();
        if (!count) count = 15;
        if (!pageNum) {
            if (this.pageNum) {
                pageNum = this.pageNum = this.pageNum + 1;
            } else {
                pageNum = this.pageNum = 1;
            }
        } else {
            this.pageNum = pageNum;
        }

        var filter = this.filterData || {};
        o2.Actions.load("x_auditlog_assemble_control").AuditLogAction.listPaging(pageNum, count, filter, function (json) {
            if (!json.data) json.data = [];
            if (!json.count) json.count = 0;
            if (callback) callback(json);
        }.bind(this))
    },
    _removeDocument: function (documentData, all) {

    },
    _create: function () {

    },
    _openDocument: function (documentData) {
        var form = new MWF.xApplication.ThreeMember.LogView.LogForm({app: this.app}, documentData );
        form.open();
    },
    _queryCreateViewNode: function () {

    },
    _postCreateViewNode: function (viewNode) {

    },
    _queryCreateViewHead: function () {

    },
    _postCreateViewHead: function (headNode) {

    }

});

MWF.xApplication.ThreeMember.LogView.Document = new Class({
    Extends: MWF.xApplication.Template.Explorer.ComplexDocument,
    mouseoverDocument: function (itemNode, ev) {
        var removeNode = itemNode.getElements("[styles='removeNode']")[0];
        if (removeNode) removeNode.setStyle("opacity", 1)
    },
    mouseoutDocument: function (itemNode, ev) {
        var removeNode = itemNode.getElements("[styles='removeNode']")[0];
        if (removeNode) removeNode.setStyle("opacity", 0)
    },
    _queryCreateDocumentNode: function (itemData) {
    },
    _postCreateDocumentNode: function (itemNode, itemData) {
        //var iconNode = itemNode.getElements("[item='icon']")[0];
        //MWF.getJSON( this.view.pictureUrlHost + iconNode.get("picUrl"), function( json ){
        //    iconNode.set("src", json.data.value);
        //} )
    },
    open: function () {
        this.view._openDocument(this.data);
    }
});

MWF.xApplication.ThreeMember.LogView.LogForm = new Class({
    Extends: MPopupForm,
    Implements: [Options, Events],
    options: {
        "style": "attendanceV2",
        "width": "800",
        "height": "700",
        "hasTop": true,
        "hasIcon": false,
        "hasTopIcon" : false,
        "hasTopContent" : false,
        "draggable": true,
        "maxAction" : true,
        "resizeable" : true,
        "closeAction": true,
        "title": MWF.xApplication.ThreeMember.LP.logDetail,
        "hideBottomWhenReading": true,
        "closeByClickMaskWhenReading": true,
    },
    _postLoad: function(){
        o2.Actions.load("x_auditlog_assemble_control").AuditLogAction.get(this.data.id, function (json) {
            this.data = json.data;
            if( this.data.sendData ){
                this.setSize( null, 700 );
            }else{
                this.setSize( null, 400 );
            }
            this._createTableContent_();
        }.bind(this))
    },
    _createTableContent: function(){},
    _createTableContent_: function () {
        var data = this.data;
        data.spendTime1 = data.spendTime+this.lp.millisecond;
        data.person1 = data.person.split("@")[0];

        this.formTableArea.set("html", this.getHtml());
        this.formTableContainer.setStyle("width","90%");

        MWF.xDesktop.requireApp("Template", "MForm", function () {
            this.form = new MForm(this.formTableArea, data, {
                isEdited: this.isEdited || this.isNew,
                style : "attendance",
                hasColon : true,
                itemTemplate: {
                    person1: { text : this.lp.person, type : "innertext" },
                    node: { text : this.lp.node, type : "innertext" },
                    clientIp: { text : this.lp.clientIp, type : "innertext" },
                    httpType: { text : this.lp.httpType, type : "innertext" },
                    module: { text : this.lp.module, type : "innertext" },
                    operation: { text : this.lp.operation, type : "innertext" },
                    httpStatus: { text : this.lp.httpStatus, type : "innertext" },
                    spendTime1: { text : this.lp.spendTime1, type : "innertext" },
                    requestTime: { text : this.lp.requestTime, type : "innertext" },
                    createTime: { text : this.lp.createTime, type : "innertext" },
                    updateTime: { text : this.lp.updateTime, type : "innertext" },
                    httpUrl: { text : this.lp.httpUrl, type : "innertext" },
                    sendData1: { text : this.lp.sendData, type : "innertext" },
                }
            }, this.app);
            this.form.load();
            this.loadScriptEditor();
        }.bind(this), true);
    },
    getHtml : function(){
        return  "<table width='100%' bordr='0' cellpadding='7' cellspacing='0' styles='formTable' style='table-layout:fixed;'>" +
            "<tr><td styles='formTableTitle' lable='person1' width='70' ></td>" +
            "    <td styles='formTableValue' item='person1' width='200'></td>" +
            "    <td styles='formTableTitle' lable='node' width='100'></td>" +
            "    <td styles='formTableValue' item='node' width='200'></td></tr>" +
            "<tr><td styles='formTableTitle' lable='clientIp'></td>" +
            "    <td styles='formTableValue' item='clientIp'></td>" +
            "    <td styles='formTableTitle' lable='httpType'></td>" +
            "    <td styles='formTableValue' item='httpType'></td></tr>" +
            "<tr><td styles='formTableTitle' lable='module'></td>" +
            "    <td styles='formTableValue' item='module'></td>" +
            "    <td styles='formTableTitle' lable='operation'></td>" +
            "    <td styles='formTableValue' item='operation'></td></tr>" +
            "<tr><td styles='formTableTitle' lable='requestTime'></td>" +
            "    <td styles='formTableValue' item='requestTime'></td>" +
            "    <td styles='formTableTitle' lable='spendTime1'></td>" +
            "    <td styles='formTableValue' item='spendTime1'></td></tr>" +
            "<tr><td styles='formTableTitle' lable='httpStatus'></td>" +
            "    <td styles='formTableValue' item='httpStatus'></td>" +
            "    <td styles='formTableTitle'></td>" +
            "    <td styles='formTableValue'></td></tr>" +
            // "<tr><td styles='formTableTitle' lable='createTime'></td>" +
            // "    <td styles='formTableValue' item='createTime'></td>" +
            // "    <td styles='formTableTitle' lable='updateTime'></td>" +
            // "    <td styles='formTableValue' item='updateTime'></td></tr>" +
            "<tr><td styles='formTableTitle' lable='httpUrl'></td>" +
            "    <td styles='formTableValue' item='httpUrl' colspan='3'></td></tr>" +
            "<tr><td styles='formTableTitle' lable='sendData1'></td>" +
            "    <td styles='formTableValue' item='sendData1' colspan='3'></td></tr>" +
            "</table>"+
            "<div item='sendData'></div>"
    },
    loadScriptEditor:function(){
        if( !this.data.sendData )return;
        MWF.require("MWF.widget.JavascriptEditor", null, false);
        var value;
        try{
            debugger;
            var json = JSON.parse(this.data.sendData);
            value = JSON.stringify(json, null, "\t");
        }catch (e) {}

        var sendDataNode = this.formTableContainer.getElement('[item="sendData"]');
        if( value ){
            this.scriptEditor = new MWF.widget.JavascriptEditor(sendDataNode, {
                "forceType": "ace",
                "option": {"value": value, "mode" : "json" }
            });
            this.scriptEditor.load(function(){
                this.scriptEditor.setValue(value);
                this.scriptEditor.editor.setReadOnly(true);
                this.addEvent("afterResize", function () {
                    this.resizeScript();
                }.bind(this))
                this.addEvent("queryClose", function () {
                    this.scriptEditor.destroy();
                }.bind(this))
                this.resizeScript();
            }.bind(this));
        }else{
            this.form.getItem("sendData1").container.set("text", this.data.sendData);
            sendDataNode.hide();
        }
    },
    resizeScript: function () {
        var size = this.formTableContainer.getSize();
        var tableSize = this.formTableContainer.getElement('table').getSize();
        this.formTableContainer.getElement('[item="sendData"]').setStyle("height", size.y - tableSize.y);
        if(this.scriptEditor && this.scriptEditor.editor)this.scriptEditor.editor.resize();
    }
});

MWF.xApplication.ThreeMember.LogView.ConfigNavi = new Class({
    Extends: MWF.xApplication.ThreeMember.LogView.Navi,
    load: function () {

        this.scrollNode = new Element("div.naviScrollNode", {"styles": this.css.naviScrollNode}).inject(this.node);
        this.areaNode = new Element("div.naviAreaNode", {"styles": this.css.naviAreaNode}).inject(this.scrollNode);


        this.configNode = new Element("div.naviViewNode", {
            "styles": this.css.naviViewNode,
            "text": this.explorer.lp.viewLog,
            "events": {
                "click": function () {
                    this.explorer.toView();
                }.bind(this)
            }
        }).inject(this.node);

        new Element("div.naviViewIconNode", {
            "styles": this.css.naviViewIconNode
        }).inject(this.configNode, "top");

        this.createAllNode();

        this.configItemMap = {};

        o2.Actions.load("x_auditlog_assemble_control").AuditConfigAction.listModule(function (json) {
            this.moduleData = json.data.valueList;
            json.data.valueList.each(function (text) {
                this.createConfigItemNode(text);
            }.bind(this));
            this.setContentSize();
            this.setContentSizeFun = this.setContentSize.bind(this);
            this.app.addEvent("resize", this.setContentSizeFun);
        }.bind(this));
    },
    createAllNode: function () {
        var _self = this;
        this.naviAllNode = new Element("div.naviAllConfigNode", {
            "styles": this.css.naviAllConfigNode,
            "text": this.explorer.lp.allConfig
        }).inject(this.areaNode);
        this.naviAllNode.addEvents({
            "mouseover": function () {
                if (_self.currentAll != this) this.setStyles(_self.explorer.css.naviAllNode_over);
            },
            "mouseout": function () {
                if (_self.currentAll != this) this.setStyles(_self.explorer.css.naviAllNode_normal);
            },
            "click": function (ev) {
                _self.setCurrentAll();
                ev.stopPropagation();
            }
        });
        if (this.options.module === "all") {
            this.naviAllNode.click();
        }
    },
    setCurrentAll: function () {
        this.cancelCurrentConfig();
        this.currentStatus = null;
        this.currentAll = this.naviAllNode;
        this.naviAllNode.setStyles(this.css.naviAllNode_current);
        if (this.explorer.configForm) {
            // this.explorer.form.reset();
            if(this.explorer.options.filterModule){
                this.explorer.configForm.getItem("module").items[0].fireEvent("change");
            }
        }
        this.explorer.loadConfigView();
    },
    createConfigItemNode: function (text) {
        var _self = this;
        var configItemNode = new Element("div", {
            "styles": this.css.naviConfigItemNode,
            "text": text
        });

        this.configItemMap[text] = configItemNode;

        // var textNode = new Element("div", {
        //     "styles": this.css.naviConfigItemTextNode,
        //     "text": text
        // }).inject(configItemNode);

        configItemNode.inject(this.areaNode);

        var configItemObj = {
            "module": text,
            "collapse": true,
            "node": configItemNode
        };
        configItemNode.addEvents({
            "mouseover": function () {
                if (_self.currentConfigNode != this) this.setStyles(_self.explorer.css.naviConfigItemNode_over);
            },
            "mouseout": function () {
                if (_self.currentConfigNode != this) this.setStyles(_self.explorer.css.naviConfigItemNode);
            },
            "click": function (ev) {
                _self.setCurrentConfig(configItemObj);
                ev.stopPropagation();
            }
        });
        if (this.options.module === text) {
            configItemNode.click()
        }
    },
    destroy: function(){
        if(this.setContentSizeFun)this.app.removeEvent("resize", this.setContentSizeFun );
        this.scrollNode.destroy();
    },
    cancelCurrentConfig: function () {
        if (this.currentAll) {
            this.currentAll.setStyles(this.css.naviAllNode_normal);
            this.currentAll = false;
        }
        if (this.currentConfigNode) {
            this.currentConfigNode.setStyles(this.css.naviConfigItemNode);
            this.currentConfigNode = false;
        }
        this.currentStatus = null;
    },
    setCurrentConfig: function (configItemObj) {
        this.cancelCurrentConfig();
        this.currentStatus = {
            module: configItemObj.module
        };
        this.currentConfigNode = configItemObj.node;
        configItemObj.node.setStyles(this.css.naviConfigItemNode_current);

        // if(this.explorer.options.filterModule) {
        //     this.explorer.form.getItem("module").setValue(menuObj.module);
        //     this.explorer.form.getItem("module").items[0].fireEvent("change");
        // }
        this.explorer.loadConfigView({"module": configItemObj.module});
    },
    checkReload: function ( module ) {
        if( this.moduleData.contains( module ) ){
            if( this.currentStatus && this.currentStatus.module === module ){

            }else{
                this.configItemMap[module].click();
            }
            return false;
        }else{
            this.options.module = module;
            this.reloadConfigItems();
            return true;
        }
    },
    reloadConfigItems: function () {
        Object.each(this.configItemMap, function (item) {
            item.destroy();
        });
        this.configItemMap = {};
        o2.Actions.load("x_auditlog_assemble_control").AuditConfigAction.listModule(function (json) {
            this.moduleData = json.data.valueList;
            json.data.valueList.each(function (text) {
                this.createConfigItemNode(text);
            }.bind(this));
        }.bind(this));
    }
});

MWF.xApplication.ThreeMember.LogView.ConfigView = new Class({
    Extends: MWF.xApplication.Template.Explorer.ComplexView,
    _createDocument: function (data, index) {
        return new MWF.xApplication.ThreeMember.LogView.ConfigDocument(this.viewNode, data, this.explorer, this, null, index);
    },
    _getCurrentPageData: function (callback, count, pageNum) {
        this.clearBody();
        if (!count) count = 15;
        if (!pageNum) {
            if (this.pageNum) {
                pageNum = this.pageNum = this.pageNum + 1;
            } else {
                pageNum = this.pageNum = 1;
            }
        } else {
            this.pageNum = pageNum;
        }

        var filter = this.filterData || {};
        o2.Actions.load("x_auditlog_assemble_control").AuditConfigAction.listPaging(pageNum, count, filter, function (json) {
            if (!json.data) json.data = [];
            if (!json.count) json.count = 0;
            if (callback) callback(json);
        }.bind(this))
    },
    _removeDocument: function (documentData, all) {
        var id = documentData.id;
        o2.Actions.load("x_auditlog_assemble_control").AuditConfigAction.delete(documentData.id, function (json) {
           this.items.erase(this.documents[id]);
           this.documents[id].destroy();
           MWF.release(this.documents[id]);
           delete this.documents[id];
            this.app.notice(this.app.lp.deleteConfigOK, "success");
        }.bind(this))
    },
    _create: function () {
        var form = new MWF.xApplication.ThreeMember.LogView.ConfigForm({app: this.app, view:this}, {} );
        form.create();
    },
    _openDocument: function (documentData) {
        var form = new MWF.xApplication.ThreeMember.LogView.ConfigForm({app: this.app, view:this}, documentData );
        form.edit();
    },
    _queryCreateViewNode: function () {

    },
    _postCreateViewNode: function (viewNode) {

    },
    _queryCreateViewHead: function () {

    },
    _postCreateViewHead: function (headNode) {

    }

});

MWF.xApplication.ThreeMember.LogView.ConfigDocument = new Class({
    Extends: MWF.xApplication.Template.Explorer.ComplexDocument,
    mouseoverDocument: function (itemNode, ev) {
        var removeNode = itemNode.getElements("[styles='removeNode']")[0];
        if (removeNode) removeNode.setStyle("opacity", 1)
    },
    mouseoutDocument: function (itemNode, ev) {
        var removeNode = itemNode.getElements("[styles='removeNode']")[0];
        if (removeNode) removeNode.setStyle("opacity", 0)
    },
    _queryCreateDocumentNode: function (itemData) {
    },
    _postCreateDocumentNode: function (itemNode, itemData) {
        //var iconNode = itemNode.getElements("[item='icon']")[0];
        //MWF.getJSON( this.view.pictureUrlHost + iconNode.get("picUrl"), function( json ){
        //    iconNode.set("src", json.data.value);
        //} )
    },
    open: function () {
        this.view._openDocument(this.data);
    },
    remove: function (e) {
        var lp = this.lp || this.view.lp || this.app.lp;
        var text = lp.deleteConfigText.replace(/{title}/g, this.data.title);
        var _self = this;
        this.node.setStyles(this.css.documentNode_remove);
        this.readyRemove = true;
        this.view.lockNodeStyle = true;

        this.app.confirm("warn", e, lp.deleteConfig, text, 350, 120, function () {
            _self.view._removeDocument(_self.data, false);
            _self.view.lockNodeStyle = false;
            this.close();
        }, function () {
            _self.node.setStyles(_self.css.documentNode);
            _self.readyRemove = false;
            _self.view.lockNodeStyle = false;
            this.close();
        });
    },
});

MWF.xApplication.ThreeMember.LogView.ConfigForm = new Class({
    Extends: MPopupForm,
    Implements: [Options, Events],
    options: {
        "style": "attendanceV2",
        "width": "800",
        "height": "700",
        "hasTop": true,
        "hasIcon": false,
        "hasTopIcon" : false,
        "hasTopContent" : false,
        "draggable": true,
        "maxAction" : true,
        "resizeable" : true,
        "closeAction": true,
        "title": MWF.xApplication.ThreeMember.LP.configDetail,
        "hideBottomWhenReading": true,
        "closeByClickMaskWhenReading": true,
        "buttonList": [{ "type":"ok" }, { "type":"cancel" }]
    },
    _postLoad: function(){
        if( this.data.id ){
            o2.Actions.load("x_auditlog_assemble_control").AuditConfigAction.get(this.data.id, function (json) {
                this.data = json.data;
                this._createTableContent_();
            }.bind(this))
        }else{
            this._createTableContent_();
        }
    },
    _createTableContent: function(){},
    _createTableContent_: function () {
        var data = this.data;
        this.formTableArea.set("html", this.getHtml());
        this.formTableContainer.setStyle("width","90%");

        var contextText = [""], contextValue = [""];
        Object.each(layout.serviceAddressList, function (value, key) {
            contextText.push( value.name + "(" + key +")" );
            contextValue.push( key );
        });
        var context = "";
        if( this.data.matchUrl ){
            context = this.data.matchUrl.split("/")[1] || "";
        }

        MWF.xDesktop.requireApp("Template", "MForm", function () {
            this.form = new MForm(this.formTableArea, data, {
                isEdited: this.isEdited || this.isNew,
                style : "attendance",
                hasColon : true,
                itemTemplate: {
                    context: { text : this.lp.moduleSelect, type : "select", selectValue: contextValue, selectText: contextText, value: context, event:{
                        change: function () {
                            this.setActionSelect()
                        }.bind(this)
                    }},
                    action: {"text": this.lp.actionSelect, type:"select", event:{
                            change: function () {
                                this.setMethodSelect()
                            }.bind(this)
                        }},
                    method: {"text": this.lp.methodSelect, type:"select", event:{
                        change: function (item, ev) {
                            debugger;
                            var v = item.getValue();
                            if( v.contains("||") ){
                                var vs = v.split("||");
                                this.form.getItem("httpType").setValue(vs[0]);
                                var url = "/"+ this.form.getItem("context").getValue() + "/"+ vs[1];
                                this.form.getItem("matchUrl").setValue( url );
                            }else{
                                this.form.getItem("httpType").setValue("");
                                this.form.getItem("matchUrl").setValue( "" );
                            }
                        }.bind(this)
                    }},
                    module: { text : this.lp.module, notEmpty: true },
                    operation: { text : this.lp.operation, type : "text", notEmpty: true },
                    httpType: { text : this.lp.httpType, type : "select", selectValue: ["", "GET","POST","PUT","DELETE"] , notEmpty: true},
                    status: { text : this.lp.httpStatus, type : "select", selectValue: ["true", "false"], selectText:["启用","禁用"], notEmpty: true, value: this.data.status === false ? "false": "true" },
                    matchUrl: { text : this.lp.httpUrl, notEmpty: true },
                }
            }, this.app);
            this.form.load();
            //this.loadScriptEditor();
            this.setActionSelect();
            this.setMethodSelect();
        }.bind(this), true);
    },
    setActionSelect: function(){
        var root = this.form.getItem("context").getValue();
        if( root ){
            var json = this.getApi( root );
            var selectValue =  json.map(function (j) {
                return j.name;
            });
            this.form.getItem("action").resetItemOptions( [""].concat(selectValue) );
        }else{
            this.form.getItem("action").resetItemOptions( [] );
        }
        this.form.getItem("method").resetItemOptions( [] );
    },
    setMethodSelect: function(){
        var root = this.form.getItem("context").getValue();
        var action = this.form.getItem("action").getValue();
        if( root && action ){
            var json = this.getApi( root );
            var texts = [""];
            var values = [""];
            json.each(function (j) {
                if( j.name === action ){
                    j.methods.each(function (m) {
                        texts.push(m.name);
                        values.push( m.method + "||" + m.uri.replace( /{[^}]+}/g, "*"));
                        debugger;
                    });
                }
            });
            this.form.getItem("method").resetItemOptions( values, texts );
        }else{
            this.form.getItem("method").resetItemOptions( [] );
        }
    },
    getApi: function( root ){
        if( !this.loadedActions )this.loadedActions = {};
        if (this.loadedActions[root]) return this.loadedActions[root];
        //if (MWF.Actions.loadedActions[root]) return MWF.Actions.loadedActions[root];
        var jaxrs = null;
        var url = MWF.Actions.getHost(root)+"/"+root+"/describe/api.json";
        MWF.getJSON(url, function(json){jaxrs = json.jaxrs;}.bind(this), false, false, false);
        this.loadedActions[root] = jaxrs;
        return jaxrs;
    },
    getHtml : function(){
        return  "<table width='100%' bordr='0' cellpadding='7' cellspacing='0' styles='formTable' style='table-layout:fixed;'>" +
            "<tr><td styles='formTableTitle' lable='context' width='70' ></td>" +
            "    <td styles='formTableValue' item='context' colspan='3'></td>" +
            "<tr><td styles='formTableTitle' lable='action' width='70' ></td>" +
            "    <td styles='formTableValue' item='action' width='200'></td>" +
            "    <td styles='formTableTitleRight' lable='method' width='100'></td>" +
            "    <td styles='formTableValue' item='method' width='200'></td></tr>" +
            "<tr><td styles='formTableTitle' width='70' ></td>" +
            "    <td styles='formTableValue' colspan='3'>"+this.lp.selectNote+"</td>" +
            "<tr><td styles='formTableTitle' lable='module'></td>" +
            "    <td styles='formTableValue' item='module'></td>" +
            "    <td styles='formTableTitleRight' lable='operation'></td>" +
            "    <td styles='formTableValue' item='operation'></td></tr>" +
            "<tr><td styles='formTableTitle' lable='httpType'></td>" +
            "    <td styles='formTableValue' item='httpType'></td>" +
            "    <td styles='formTableTitleRight' lable='status'></td>" +
            "    <td styles='formTableValue' item='status'></td></tr>" +
            "<tr><td styles='formTableTitle' lable='matchUrl'></td>" +
            "    <td styles='formTableValue' item='matchUrl' colspan='3'></td></tr>" +
            "</table>";
    },
    _ok: function (data, callback) {
        debugger;
        this.data.status = this.data.status !== "false";
        if( this.data.id ){
            o2.Actions.load("x_auditlog_assemble_control").AuditConfigAction.update( this.data.id, data, function(json){
                if( callback )callback(json);
            }.bind(this), function( errorObj ){
                var error = JSON.parse( errorObj.responseText );
                this.app.notice( error.message, error );
            }.bind(this));
        }else{
            o2.Actions.load("x_auditlog_assemble_control").AuditConfigAction.save( data, function(json){
                //this.explorer.view = null;
                debugger;
                if( this.explorer.view.explorer.navi.checkReload( data.module ) ){
                    this.explorer.view = null;
                }
                if( callback )callback(json);
            }.bind(this), function( errorObj ){
                var error = JSON.parse( errorObj.responseText );
                this.app.notice( error.message, error );
            }.bind(this));
        }
    }
    // loadScriptEditor:function(){
    //     if( !this.data.sendData )return;
    //     MWF.require("MWF.widget.JavascriptEditor", null, false);
    //     var value;
    //     try{
    //         debugger;
    //         var json = JSON.parse(this.data.sendData);
    //         value = JSON.stringify(json, null, "\t");
    //     }catch (e) {}
    //
    //     var sendDataNode = this.formTableContainer.getElement('[item="sendData"]');
    //     if( value ){
    //         this.scriptEditor = new MWF.widget.JavascriptEditor(sendDataNode, {
    //             "forceType": "ace",
    //             "option": {"value": value, "mode" : "json" }
    //         });
    //         this.scriptEditor.load(function(){
    //             this.scriptEditor.setValue(value);
    //             this.scriptEditor.editor.setReadOnly(true);
    //             this.addEvent("afterResize", function () {
    //                 this.resizeScript();
    //             }.bind(this))
    //             this.addEvent("queryClose", function () {
    //                 this.scriptEditor.destroy();
    //             }.bind(this))
    //             this.resizeScript();
    //         }.bind(this));
    //     }else{
    //         this.form.getItem("sendData1").container.set("text", this.data.sendData);
    //         sendDataNode.hide();
    //     }
    // },
    // resizeScript: function () {
    //     var size = this.formTableContainer.getSize();
    //     var tableSize = this.formTableContainer.getElement('table').getSize();
    //     this.formTableContainer.getElement('[item="sendData"]').setStyle("height", size.y - tableSize.y);
    //     if(this.scriptEditor && this.scriptEditor.editor)this.scriptEditor.editor.resize();
    // }
});

var getDateDiff = function (publishTime) {
    if (!publishTime) return "";
    var dateTimeStamp = Date.parse(publishTime.replace(/-/gi, "/"));
    var minute = 1000 * 60;
    var hour = minute * 60;
    var day = hour * 24;
    var halfamonth = day * 15;
    var month = day * 30;
    var year = month * 12;
    var now = new Date().getTime();
    var diffValue = now - dateTimeStamp;
    if (diffValue < 0) {
        //若日期不符则弹出窗口告之
        //alert("结束日期不能小于开始日期！");
    }
    var yesterday = new Date().decrement('day', 1);
    var beforYesterday = new Date().decrement('day', 2);
    var yearC = diffValue / year;
    var monthC = diffValue / month;
    var weekC = diffValue / (7 * day);
    var dayC = diffValue / day;
    var hourC = diffValue / hour;
    var minC = diffValue / minute;
    if (yesterday.getFullYear() == dateTimeStamp.getFullYear() && yesterday.getMonth() == dateTimeStamp.getMonth() && yesterday.getDate() == dateTimeStamp.getDate()) {
        result = MWF.xApplication.ThreeMember.LP.yesterday + " " + dateTimeStamp.getHours() + ":" + dateTimeStamp.getMinutes();
    } else if (beforYesterday.getFullYear() == dateTimeStamp.getFullYear() && beforYesterday.getMonth() == dateTimeStamp.getMonth() && beforYesterday.getDate() == dateTimeStamp.getDate()) {
        result = MWF.xApplication.ThreeMember.LP.twoDaysAgo + " " + dateTimeStamp.getHours() + ":" + dateTimeStamp.getMinutes();
    } else if (yearC > 1) {
        result = dateTimeStamp.getFullYear() + "-" + (dateTimeStamp.getMonth() + 1) + "-" + dateTimeStamp.getDate();
    } else if (monthC >= 1) {
        //result= parseInt(monthC) + "个月前";
        // s.getFullYear()+"年";
        result = dateTimeStamp.getFullYear() + "-" + (dateTimeStamp.getMonth() + 1) + "-" + dateTimeStamp.getDate();
    } else if (weekC >= 1) {
        result = parseInt(weekC) + MWF.xApplication.ThreeMember.LP.weekAgo;
    } else if (dayC >= 1) {
        result = parseInt(dayC) + MWF.xApplication.ThreeMember.LP.dayAgo;
    } else if (hourC >= 1) {
        result = parseInt(hourC) + MWF.xApplication.ThreeMember.LP.hourAgo;
    } else if (minC >= 1) {
        result = parseInt(minC) + MWF.xApplication.ThreeMember.LP.minuteAgo;
    } else
        result = MWF.xApplication.ThreeMember.LP.publishJustNow;
    return result;
};


