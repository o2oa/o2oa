MWF.xApplication.AuditLog = MWF.xApplication.AuditLog || {};
MWF.require("MWF.widget.O2Identity", null, false);
//MWF.xDesktop.requireApp("AuditLog", "Access", null, false);
//MWF.xDesktop.requireApp("AuditLog", "Actions.RestActions", null, false);
MWF.xDesktop.requireApp("Template", "Explorer", null, false);
MWF.xDesktop.requireApp("Template", "MForm", null, false);

MWF.xApplication.AuditLog.options = {
    multitask: false,
    executable: true
};
MWF.xApplication.AuditLog.Main = new Class({
    Extends: MWF.xApplication.Common.Main,
    Implements: [Options, Events],
    options: {
        "style": "default",
        "name": "AuditLog",
        "icon": "icon.png",
        "width": "1220",
        "height": "700",
        "isResize": true,
        "isMax": true,
        "viewPageNum": 1,
        "title": MWF.xApplication.AuditLog.LP.title
    },
    onQueryLoad: function () {
        this.lp = MWF.xApplication.AuditLog.LP;
    },
    loadApplication: function (callback) {
        this.userName = layout.desktop.session.user.distinguishedName;

        this.path = "../x_component_AuditLog/$Main/" + this.options.style + "/";
        this.createNode();
        this.loadApplicationContent();
    },
    loadController: function (callback) {

        //this.access = new MWF.xApplication.AuditLog.Access( this.restActions, this.lp );

        if (callback) callback();
    },
    reload: function () {
        this.clearContent();
        if (this.explorer) {
            this.openSetting(this.explorer.currentNaviItem.retrieve("index"))
        } else {
            this.loadApplicationLayout();
        }
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
        this.createTopNode();
        this.createContainerNode();
        this.loaNavi();

    },
    loaNavi: function (callback) {
        debugger;
        var naviOpt = {};
        if (this.status) {
            naviOpt.module = this.status.module || "all";
            naviOpt.operation = this.status.operation;
        } else {
            naviOpt.module = this.options.module || "all";
            naviOpt.operation = this.options.operation;
        }
        this.navi = new MWF.xApplication.AuditLog.Main.Navi(this, this.naviNode, naviOpt);
    },
    createTopNode: function () {
        this.topContainerNode = new Element("div.topContainerNode", {
            "styles": this.css.topContainerNode
        }).inject(this.contentContainerNode);

        this.topNode = new Element("div.topNode", {
            "styles": this.css.topNode
        }).inject(this.topContainerNode);

        // this.topIconNode = new Element("div", {
        //     "styles": this.css.topIconNode
        // }).inject(this.topNode);
        //
        // this.topTextNode = new Element("div", {
        //     "styles": this.css.topTextNode,
        //     "text": this.options.title
        // }).inject(this.topNode);

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
        this.addEvent("resize", this.setContentSizeFun);
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
    loadView: function (filterData) {
        if (this.view) this.view.destroy();
        this.contentNode.empty();
        var viewContainerNode = this.viewContainerNode = new Element("div.viewContainerNode", {
            "styles": this.css.viewContainerNode
        }).inject(this.contentNode);

        this.view = new MWF.xApplication.AuditLog.Main.View(viewContainerNode, this, this, {
            templateUrl: this.path + "listItem.json",
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
        if (filterData) this.view.filterData = filterData;
        this.view.load();
    },
    clearContent: function () {
        if (this.explorer) this.explorer.destroy();
        this.explorer = null;
        if (this.setContentSizeFun) this.removeEvent("resize", this.setContentSizeFun);
        if (this.scrollBar && this.scrollBar.scrollVAreaNode) this.scrollBar.scrollVAreaNode.destroy();
        if (this.scrollBar) delete this.scrollBar;
        if (this.contentContainerNode) {
            this.contentContainerNode.destroy();
            //this.middleNode.destroy();
            //this.contentNode.destroy();
        }
    },
    loadFilter: function () {
        var lp = MWF.xApplication.AuditLog.LP;
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
        return this.navi.currentStatus || {};
    }
});

MWF.xApplication.AuditLog.Main.Navi = new Class({
    Implements: [Options, Events],
    options: {
        "module": "all"
    },
    initialize: function (app, node, options) {
        this.setOptions(options);
        this.app = app;
        this.node = $(node);
        this.css = this.app.css;
        this.currentMenu = null;
        this.currentItem = null;
        this.load();
    },
    load: function () {
        this.scrollNode = new Element("div.naviScrollNode", {"styles": this.css.naviScrollNode}).inject(this.node);
        this.areaNode = new Element("div.naviAreaNode", {"styles": this.css.naviAreaNode}).inject(this.scrollNode);

        this.naviTopNode = new Element("div.naviTopNode", {
            "styles": this.css.naviTopNode,
            "text": this.app.lp.title
        }).inject(this.areaNode);

        this.createAllNode();

        o2.Actions.load("x_auditlog_assemble_control").AuditConfigAction.listModule(function (json) {
            json.data.valueList.each(function (text) {
                this.createMenuNode(text);
            }.bind(this));
            this.setContentSize();
            this.app.addEvent("resize", this.setContentSize.bind(this));
        }.bind(this));
    },
    createAllNode: function () {
        var _self = this;
        this.naviAllNode = new Element("div.naviTopNode", {
            "styles": this.css.naviAllNode,
            "text": this.app.lp.all
        }).inject(this.areaNode);
        this.naviAllNode.addEvents({
            "mouseover": function () {
                if (_self.currentAll != this) this.setStyles(_self.app.css.naviAllNode_over);
            },
            "mouseout": function () {
                if (_self.currentAll != this) this.setStyles(_self.app.css.naviAllNode_normal);
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
        if (this.app.form) {
            this.app.form.reset();
            this.app.form.getItem("module").items[0].fireEvent("change");
        }
        this.app.loadView()
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
                if (_self.currentMenu != this) this.setStyles(_self.app.css.naviMenuNode_over);
            },
            "mouseout": function () {
                if (_self.currentMenu != this) this.setStyles(_self.app.css.naviMenuNode_normal);
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
        this.app.form.reset();
        this.app.form.getItem("module").setValue(menuObj.module);
        debugger;
        this.app.form.getItem("module").items[0].fireEvent("change");
        this.app.loadView({"module": menuObj.module})
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
                if (_self.currentItem != this) this.setStyles(_self.app.css.naviItemNode_over);
            },
            "mouseout": function () {
                if (_self.currentItem != this) this.setStyles(_self.app.css.naviItemNode);
            },
            "mousedown": function () {
                if (_self.currentItem != this) this.setStyles(_self.app.css.naviItemNode_down);
            },
            "mouseup": function () {
                if (_self.currentItem != this) this.setStyles(_self.app.css.naviItemNode_over);
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
        this.app.form.reset();
        this.app.form.getItem("module").setValue(itemObj.module);
        this.app.form.getItem("module").items[0].fireEvent("change");
        this.app.form.getItem("operation").setValue(itemObj.operation);
        this.app.loadView({
            "module": itemObj.module,
            "operation": itemObj.operation
        })
    },
    setContentSize: function () {
        var nodeSize = this.app.node.getSize();
        var h = nodeSize.y - this.app.getOffsetY(this.app.node);
        this.node.setStyle("height", h);
        this.scrollNode.setStyle("height", h);
    }
});

MWF.xApplication.AuditLog.Main.View = new Class({
    Extends: MWF.xApplication.Template.Explorer.ComplexView,
    _createDocument: function (data, index) {
        return new MWF.xApplication.AuditLog.Main.Document(this.viewNode, data, this.explorer, this, null, index);
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
        var form = new MWF.xApplication.AuditLog.Main.LogForm({app: this.app}, documentData );
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

MWF.xApplication.AuditLog.Main.Document = new Class({
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

MWF.xApplication.AuditLog.Main.LogForm = new Class({
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
        "title": MWF.xApplication.AuditLog.LP.logDetail,
        "hideBottomWhenReading": true,
        "closeByClickMaskWhenReading": true,
    },
    _postLoad: function(){
        o2.Actions.load("x_auditlog_assemble_control").AuditLogAction.get(this.data.id, function (json) {
            this.data = json.data;
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
            "<tr><td styles='formTableTitle' lable='httpStatus'></td>" +
            "    <td styles='formTableValue' item='httpStatus'></td>" +
            "    <td styles='formTableTitle' lable='spendTime1'></td>" +
            "    <td styles='formTableValue' item='spendTime1'></td></tr>" +
            "<tr><td styles='formTableTitle' lable='createTime'></td>" +
            "    <td styles='formTableValue' item='createTime'></td>" +
            "    <td styles='formTableTitle' lable='updateTime'></td>" +
            "    <td styles='formTableValue' item='updateTime'></td></tr>" +
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
        result = MWF.xApplication.AuditLog.LP.yesterday + " " + dateTimeStamp.getHours() + ":" + dateTimeStamp.getMinutes();
    } else if (beforYesterday.getFullYear() == dateTimeStamp.getFullYear() && beforYesterday.getMonth() == dateTimeStamp.getMonth() && beforYesterday.getDate() == dateTimeStamp.getDate()) {
        result = MWF.xApplication.AuditLog.LP.twoDaysAgo + " " + dateTimeStamp.getHours() + ":" + dateTimeStamp.getMinutes();
    } else if (yearC > 1) {
        result = dateTimeStamp.getFullYear() + "-" + (dateTimeStamp.getMonth() + 1) + "-" + dateTimeStamp.getDate();
    } else if (monthC >= 1) {
        //result= parseInt(monthC) + "个月前";
        // s.getFullYear()+"年";
        result = dateTimeStamp.getFullYear() + "-" + (dateTimeStamp.getMonth() + 1) + "-" + dateTimeStamp.getDate();
    } else if (weekC >= 1) {
        result = parseInt(weekC) + MWF.xApplication.AuditLog.LP.weekAgo;
    } else if (dayC >= 1) {
        result = parseInt(dayC) + MWF.xApplication.AuditLog.LP.dayAgo;
    } else if (hourC >= 1) {
        result = parseInt(hourC) + MWF.xApplication.AuditLog.LP.hourAgo;
    } else if (minC >= 1) {
        result = parseInt(minC) + MWF.xApplication.AuditLog.LP.minuteAgo;
    } else
        result = MWF.xApplication.AuditLog.LP.publishJustNow;
    return result;
};


