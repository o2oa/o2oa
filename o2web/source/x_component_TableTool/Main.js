MWF.xApplication.TableTool = MWF.xApplication.TableTool || {};
MWF.require("MWF.widget.O2Identity", null, false);
//MWF.xDesktop.requireApp("TableTool", "Access", null, false);
//MWF.xDesktop.requireApp("TableTool", "Actions.RestActions", null, false);
MWF.xDesktop.requireApp("Template", "Explorer", null, false);
MWF.xDesktop.requireApp("Template", "MForm", null, false);
MWF.require("MWF.widget.JavascriptEditor", null, false);

MWF.xApplication.TableTool.options = {
    multitask: false,
    executable: true
};
MWF.xApplication.TableTool.Main = new Class({
    Extends: MWF.xApplication.Common.Main,
    Implements: [Options, Events],
    options: {
        "style": "default",
        "name": "TableTool",
        "icon": "icon.png",
        "width": "1220",
        "height": "700",
        "isResize": true,
        "isMax": true,
        "viewPageNum": 1,
        "title": MWF.xApplication.TableTool.LP.title
    },
    onQueryLoad: function () {
        this.lp = MWF.xApplication.TableTool.LP;
    },
    loadApplication: function (callback) {
        this.userName = layout.desktop.session.user.distinguishedName;

        this.path = "../x_component_TableTool/$Main/" + this.options.style + "/";
        this.createNode();
        this.loadApplicationContent();
    },
    loadController: function (callback) {

        //this.access = new MWF.xApplication.TableTool.Access( this.restActions, this.lp );

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
        // if (this.status) {
        //     naviOpt.module = this.status.module || "all";
        //     naviOpt.operation = this.status.operation;
        // } else {
        //     naviOpt.module = this.options.module || "all";
        //     naviOpt.operation = this.options.operation;
        // }
        this.navi = new MWF.xApplication.TableTool.Main.Navi(this, this.naviNode, naviOpt);
    },
    createTopNode: function () {
        this.topContainerNode = new Element("div.topContainerNode", {
            "styles": this.css.topContainerNode
        }).inject(this.contentContainerNode);

        this.topNode = new Element("div.topNode", {
            "styles": this.css.topNode
        }).inject(this.topContainerNode);

        this.topOperateNode = new Element("div", {
            "styles": this.css.topOperateNode
        }).inject(this.topNode);

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
        this.loadOperate();
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

        this.view = new MWF.xApplication.TableTool.Main.View(viewContainerNode, this, this, {
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
        this.view.table = this.currentTable;
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
    loadOperate: function () {
        var lp = MWF.xApplication.TableTool.LP;
        this.fileterNode = new Element("div.operateNode", {
            "styles": this.css.fileterNode
        }).inject(this.topOperateNode);

        var html = "<table width='100%' bordr='0' cellpadding='0' cellspacing='0' styles='filterTable'>" +
            "<tr>" +
            "    <td styles='filterTableValue' item='remove'></td>" +
            "    <td styles='filterTableValue' item='add'></td>" +
            "</tr>" +
            "</table>";
        this.fileterNode.set("html", html);

        this.fileterForm = new MForm(this.fileterNode, {}, {
            style: "attendance",
            isEdited: true,
            itemTemplate: {
                remove: {
                    "value": "删除", type: "button", className: "filterButtonGrey", event: {
                        click: function (e) {

                            var checkedItems = this.view.getCheckedItems();
                            var _self = this;
                            this.form.app.confirm("warn", e.node, "提示", "您确定要删除吗？", 350, 120, function () {

                                checkedItems.each(function (item){
                                    //item.node.setStyles(item.css.documentNode_remove);
                                    _self.view._remove(item.data);
                                }.bind(this));

                                this.close();
                                _self.form.app.notice("删除成功","success");
                                _self.loadView();

                            }, function () {

                                this.close();
                            });


                        }.bind(this)
                    }
                },
                add: {
                    "value": "添加", type: "button", className: "filterButtonGrey", event: {
                        click: function (e) {

                            this.view._openDocument(this.getNewData(this.view.fieldList));


                        }.bind(this)
                    }
                },
            }
        }, this.app, this.css);
        this.fileterForm.load();
    },
    getNewData : function (fieldList){
        var newLineData = {};
        fieldList.each(function (field) {
            switch (field.type) {
                case "string":
                    newLineData[field.name] = "";
                    break;
                case "integer":
                case "long":
                case "double":
                    newLineData[field.name] = 0;
                    break;
                case "date":
                    var str = new Date().format("%Y-%m-%d");
                    newLineData[field.name] = str;
                    break;
                case "time":
                    var str = new Date().format("%H:%M:%S");
                    newLineData[field.name] = str;
                    break;
                case "dateTime":
                    var str = new Date().format("db");
                    newLineData[field.name] = str;
                    break;
                case "boolean":
                    newLineData[field.name] = true;
                    break;
                case "stringList":
                case "integerList":
                case "longList":
                case "doubleList":
                    newLineData[field.name] = [];
                    break;
                case "stringLob":
                    newLineData[field.name] = "";
                    break;
                case "stringMap":
                    newLineData[field.name] = {};
                    break;
            }
        });
        return newLineData;
    },
    loadFilter: function () {
        var lp = MWF.xApplication.TableTool.LP;
        this.fileterNode = new Element("div.fileterNode", {
            "styles": this.css.fileterNode
        }).inject(this.topContentNode);

        var html = "<table width='100%' bordr='0' cellpadding='0' cellspacing='0' styles='filterTable'>" + //style='width: 900px;'
            "<tr>" +
            "    <td styles='filterTableTitle' lable='searchKey'></td>" +
            "    <td styles='filterTableValue' item='searchKey'></td>" +
            "    <td styles='filterTableValue' item='action'></td>" +
            "    <td styles='filterTableValue' item='reset'></td>" +
            "</tr>" +
            "</table>";
        this.fileterNode.set("html", html);


        this.form = new MForm(this.fileterNode, {}, {
            style: "attendance",
            isEdited: true,
            itemTemplate: {
                searchKey: {text: "where条件查询", "type": "text", "style": {"min-width": "450px"}},
                action: {
                    "value": lp.query, type: "button", className: "filterButton", event: {
                        click: function () {
                            var result = this.form.getResult(false, null, false, true, false);
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

MWF.xApplication.TableTool.Main.Navi = new Class({
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
        this.isLoad = false;
        this.load();
    },
    load: function () {
        this.scrollNode = new Element("div.naviScrollNode", {"styles": this.css.naviScrollNode}).inject(this.node);
        this.areaNode = new Element("div.naviAreaNode", {"styles": this.css.naviAreaNode}).inject(this.scrollNode);

        this.naviTopNode = new Element("div.naviTopNode", {
            "styles": this.css.naviTopNode,
            "text": this.app.lp.title
        }).inject(this.areaNode);
        o2.Actions.load("x_query_assemble_designer").QueryAction.listAll(function (json) {
            json.data.each(function (data) {
                this.createMenuNode(data.id,data.name);
            }.bind(this));
            this.setContentSize();
            this.app.addEvent("resize", this.setContentSize.bind(this));
        }.bind(this));
    },
    createMenuNode: function (id,text) {

        o2.Actions.load("x_query_assemble_designer").TableAction.listWithQuery(id, function (json) {

            if(json.data.length === 0){

            }else {

                if(!this.options.operation && !this.isLoad){
                    this.options.module = text;
                    this.options.operation = json.data[0].name;
                    this.isLoad = true;
                }
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
                    "iconNode": iconNode,
                    "dataList" : json.data
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
                        _self.expandOrCollapse(menuObj);
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
            }
        }.bind(this), null, true);
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

    },
    expandOrCollapse: function (menuObj) {
        if (!menuObj.itemContainer) {
            menuObj.itemContainer = new Element("div").inject(menuObj.node, "after");

                menuObj.dataList.each(function (data) {
                    this.createItemNode(data, menuObj);
                }.bind(this))

        } else {
            menuObj.itemContainer.setStyle("display", menuObj.collapse ? "" : "none");
        }
        menuObj.iconNode.setStyles(menuObj.collapse ? this.css.naviMenuIconNode_expand : this.css.naviMenuIconNode_collapse);
        menuObj.collapse = !menuObj.collapse;
    },
    createItemNode: function (data, menuObj) {

        var _self = this;
        var itemNode = new Element("div", {
            "styles": this.css.naviItemNode
        });

        var itemObj = {
            "module": menuObj.module,
            "operation": data.name,
            "node": itemNode,
            "data" : data
        };

        var textNode = new Element("div", {
            "styles": this.css.naviItemTextNode,
            "text": data.name
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

        if (data.name === this.options.operation) {
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
        this.app.currentTable = itemObj.data.name;
        itemObj.node.setStyles(this.css.naviItemNode_current);
        this.app.form.reset();

        this.app.loadView()
    },
    setContentSize: function () {
        var nodeSize = this.app.node.getSize();
        var h = nodeSize.y - this.app.getOffsetY(this.app.node);
        this.node.setStyle("height", h);
        this.scrollNode.setStyle("height", h);
    }
});

MWF.xApplication.TableTool.Main.View = new Class({
    Extends: MWF.xApplication.Template.Explorer.ComplexView,
    _createDocument: function (data, index) {
        return new MWF.xApplication.TableTool.Main.Document(this.viewNode, data, this.explorer, this, null, index);
    },
    ayalyseTemplate: function () {
        //
        // console.log(this.options.data)
        MWF.getJSON(this.options.templateUrl, function (json) {
            this.template = json;

            o2.Actions.load("x_query_assemble_designer").TableAction.get(this.table, function( json ){
                this.fieldList = JSON.parse(json.data.data).fieldList;
                this.fieldList.each(function (field){
                    var d = {
                        "name": field.name,
                        "head": {
                            "html": "<th styles='normalThNode' >" + (field.description!==""?field.description:field.name) + "</th>",
                            "width" : "100px"
                        },
                        "content": {
                            "html": "<td styles='normalTdNode' item='"+field.name+"'></td>",
                            "items": { }
                        }
                    }
                    d.content.items[field.name] = {};
                    this.template.items.push( d );
                }.bind(this));

            }.bind(this),null,false);

        }.bind(this), false)
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

        if(pageNum === 1) {
            firstResult = 0;
        }else{
            firstResult = (pageNum - 1) * count ;
        }

        var whereArr = [];
        whereArr.push("1>0");

        if(this.filterData){
            if(this.filterData.searchKey !== ""){
                whereArr.push(this.filterData.searchKey);
            }

        }

        var data = {};
        data["type"] = "select";
        data["data"] = "select o from " + this.table +" o where "+ whereArr.join(" and ") +" order by o.updateTime desc";
        data["countData"] = "select count(o) from "+ this.table +" o where " + whereArr.join(" and ");
        data["maxResults"] = count;
        data["firstResult"] = firstResult;


        o2.Actions.load("x_query_assemble_designer").TableAction.execute(this.table,data, function (json) {
            if (!json.data) json.data = [];
            if (!json.count) json.count = 0;
            if (callback) callback(json);
        }.bind(this))
    },
    _remove: function (data) {
        o2.Actions.load("x_query_assemble_designer").TableAction.rowDelete(this.table, data.id ,function (json) {

        }.bind(this),null,false);
    },
    _create: function () {

    },
    _openDocument: function (documentData) {
        console.log(documentData)

        var div = new Element("div",{"style":"height:450px;border:1px solid rgb(230, 230, 230)"});
        var editor;
        var dlg = o2.DL.open({
            "title": "修改",
            "width": "800px",
            "height": "560px",
            "mask": true,
            "content": div,
            "container": null,
            "positionNode": this.app.content,
            "onQueryClose": function () {
                div.destroy();
            }.bind(this),
            "buttonList": [
                {
                    "text": "保存",
                    "action": function () {
                        var str = editor.editor.getValue();
                        o2.Actions.load("x_query_assemble_designer").TableAction.rowSave(this.table,JSON.parse(str),function (json){
                            dlg.close();
                            this.app.loadView();
                        }.bind(this),null,false);

                    }.bind(this)
                },
                {
                    "text": "关闭",
                    "action": function () {
                        dlg.close();
                    }.bind(this)
                }
            ],
            "onPostShow": function () {

                editor = new o2.widget.JavascriptEditor(div, { "forceType": "ace","option": { "mode": "json" } });
                editor.load(function () {
                    editor.editor.setValue(JSON.stringify(documentData, null, "\t"));
                });

                dlg.reCenter();
            }.bind(this)
        });

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

MWF.xApplication.TableTool.Main.Document = new Class({
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
