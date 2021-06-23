MWF.xApplication.process = MWF.xApplication.process || {};
MWF.xApplication.process.Application = MWF.xApplication.process.Application || {};
MWF.require("MWF.widget.Common", null, false);
MWF.require("MWF.xScript.Macro", null, false);
MWF.xDesktop.requireApp("process.Application", "lp."+o2.language, null, false);
MWF.xApplication.process.Application.Viewer = new Class({
    Implements: [Options, Events],
    Extends: MWF.widget.Common,
    options: {
        "style": "default",
        "resizeNode": true,
        "actions": {
            "lookup": {"uri": "/jaxrs/queryview/flag/{view}/application/flag/{application}/execute", "method":"PUT"},
            "getView": {"uri": "/jaxrs/queryview/flag/{view}/application/flag/{application}"},
            "listWorkByJob": {"uri": "/jaxrs/job/{job}/find/work/workcompleted"},
            "listTaskByWork": {"uri": "/jaxrs/work/{id}/assignment/manage"}

        },
        "actionRoot": "x_processplatform_assemble_surface"
    },
    initialize: function(container, json, options){
        this.setOptions(options);

        this.path = "../x_component_process_Application/$Viewer/";
        this.cssPath = "../x_component_process_Application/$Viewer/"+this.options.style+"/css.wcss";
        this._loadCss();
        this.lp = MWF.xApplication.process.Application.LP;

        this.container = $(container);
        this.json = json;

        this.viewJson = null;
        this.filterItems = [];
        this.searchStatus = "none"; //none, custom, default


        this.items = [];
        this.selectedItems = [];
        this.hideColumns = [];
        this.openColumns = [];

        this.gridJson = null;

        this.init(function(){
            this.load();
        }.bind(this));
    },
    init: function(callback){
        if (this.json.data){
            this.viewJson = JSON.decode(this.json.data);
            if (callback) callback();
        }else{
            this.getView(callback);
        }
    },
    load: function(){
        this.loadLayout();
        this.createSearchNode();
        this.createViewNode({"filterList": this.json.filter || null});

        if (this.options.resizeNode){
            this.setContentHeightFun = this.setContentHeight.bind(this);
            this.container.addEvent("resize", this.setContentHeightFun);
            this.setContentHeightFun();
        }
    },
    loadLayout: function(){
        this.node = new Element("div", {"styles": this.css.node}).inject(this.container);
        this.searchAreaNode = new Element("div", {"styles": this.css.searchAreaNode}).inject(this.node);
        this.viewAreaNode = new Element("div", {"styles": this.css.viewAreaNode}).inject(this.node);
    },
    createSearchNode: function(){
        if (this.viewJson.customFilterEntryList && this.viewJson.customFilterEntryList.length){
            this.searchStatus = "default";
            this.loadFilterSearch();
        }else{
            this.loadSimpleSearch();
        }
    },
    loadSimpleSearch: function(){
        this.searchSimpleNode = new Element("div", {"styles": this.css.searchSimpleNode}).inject(this.searchAreaNode);
        this.searchSimpleButtonNode = new Element("div", {"styles": this.css.searchSimpleButtonNode}).inject(this.searchSimpleNode);
        this.searchSimpleInputNode = new Element("input", {"type":"text", "styles": this.css.searchSimpleInputNode, "value": this.lp.searchKeywork}).inject(this.searchSimpleNode);
        this.searchSimpleButtonNode.addEvent("click", function(){
            this.search();
        }.bind(this));
        this.searchSimpleInputNode.addEvents({
            "focus": function(){
                if (this.searchSimpleInputNode.get("value")===this.lp.searchKeywork) this.searchSimpleInputNode.set("value", "");
            }.bind(this),
            "blur": function(){if (!this.searchSimpleInputNode.get("value")) this.searchSimpleInputNode.set("value", this.lp.searchKeywork);}.bind(this),
            "keydown": function(e){
                if (e.code===13) this.search();
            }.bind(this)
        });
    },
    search: function(){
        if (this.gridJson){
            var key = this.searchSimpleInputNode.get("value");
            var rows = this.viewTable.rows;
            var first = (this.json.isTitle!=="no") ? 1 : 0;
            for (var i = first; i<rows.length; i++){
                var tr = rows[i];
                if (!key || key==this.lp.searchKeywork){
                    if (tr.getStyle("display")==="none") tr.setStyle("display", "table-row");
                }else{
                    if (tr.get("text").indexOf(key)!==-1){
                        if (tr.getStyle("display")==="none") tr.setStyle("display", "table-row");
                    }else{
                        if (tr.getStyle("display")!=="none") tr.setStyle("display", "none");
                    }
                }
            }
        }
    },
    loadFilterSearch: function(){
        this.viewSearchCustomActionNode = new Element("div", {"styles": this.css.viewFilterSearchCustomActionNode, "text": this.lp.customSearch}).inject(this.searchAreaNode);
        this.viewSearchInputAreaNode = new Element("div", {"styles": this.css.viewFilterSearchInputAreaNode}).inject(this.searchAreaNode);

        this.viewSearchIconNode = new Element("div", {"styles": this.css.viewFilterSearchIconNode}).inject(this.viewSearchInputAreaNode);
        this.viewSearchInputBoxNode = new Element("div", {"styles": this.css.viewFilterSearchInputBoxNode}).inject(this.viewSearchInputAreaNode);
        this.viewSearchInputNode = new Element("input", {"styles": this.css.viewFilterSearchInputNode, "value": this.lp.searchKeywork}).inject(this.viewSearchInputBoxNode);

        this.viewSearchInputNode.addEvents({
            "focus": function(){
                if (this.viewSearchInputNode.get("value")===this.lp.searchKeywork) this.viewSearchInputNode.set("value", "");
            }.bind(this),
            "blur": function(){if (!this.viewSearchInputNode.get("value")) this.viewSearchInputNode.set("value", this.lp.searchKeywork);}.bind(this),
            "keydown": function(e){
                if (e.code===13) this.searchView();
            }.bind(this)
        });
        this.viewSearchIconNode.addEvents({
            "click": function(){this.searchView();}.bind(this)
        });
        this.viewSearchCustomActionNode.addEvents({
            "click": function(){this.loadCustomSearch();}.bind(this)
        });
    },
    searchView: function(){
        if (this.viewJson.customFilterEntryList){
            var key = this.viewSearchInputNode.get("value");
            if (key && key!==this.lp.searchKeywork){
                var filterData = this.json.filter || [];
                this.viewJson.customFilterEntryList.each(function(entry){
                    if (entry.formatType==="textValue"){
                        var d = {
                            "path": entry.path,
                            "value": key,
                            "formatType": entry.formatType,
                            "logic": "or",
                            "comparison": "like"
                        };
                        filterData.push(d);
                    }
                    if (entry.formatType==="numberValue"){
                        var v = key.toFloat();
                        if (!isNaN(v)){
                            var d = {
                                "path": entry.path,
                                "value": v,
                                "formatType": entry.formatType,
                                "logic": "or",
                                "comparison": "like"
                            };
                            filterData.push(d);
                        }
                    }
                }.bind(this));

                this.createViewNode({"filterList": filterData});
            }
        }
    },
    searchCustomView: function(){
        if (this.filterItems.length){
            var filterData = this.json.filter || [];
            this.filterItems.each(function(filter){
                filterData.push(filter.data);
            }.bind(this));

            this.createViewNode({"filterList": filterData});
        }else{
            this.createViewNode({"filterList": this.json.filter || null});
        }
    },
    loadCustomSearch: function(){
        this.viewSearchIconNode.setStyle("display", "none");
        this.viewSearchInputBoxNode.setStyle("display", "none");
        this.viewSearchCustomActionNode.setStyle("display", "none");

        if (!this.searchMorph) this.searchMorph = new Fx.Morph(this.viewSearchInputAreaNode);
        var x = this.viewSearchInputAreaNode.getParent().getSize().x-2-20;
        this.css.viewFilterSearchInputAreaNode_custom.width = ""+x+"px";

        var x1 = this.viewSearchInputAreaNode.getSize().x-2;
        this.viewSearchInputAreaNode.setStyle("width", ""+x1+"px");
        this.searchMorph.start(this.css.viewFilterSearchInputAreaNode_custom).chain(function(){
            this.searchStatus = "custom";
            this.css.viewFilterSearchInputAreaNode_custom.width = "auto";
            this.viewSearchInputAreaNode.setStyle("width", "auto");

            if (this.viewSearchCustomContentNode){
                this.viewSearchCustomCloseActionNode.setStyle("display", "block");
                this.viewSearchCustomContentNode.setStyle("display", "block");
            }else{
                this.loadCustomSearchContent();
            }

            this.setContentHeightFun();
        }.bind(this));
        this.searchCustomView();
    },
    loadCustomSearchContent: function(){
        this.viewSearchCustomCloseActionNode = new Element("div", {"styles": this.css.viewFilterSearchCustomCloseActionNode}).inject(this.viewSearchInputAreaNode);
        this.viewSearchCustomContentNode = new Element("div", {"styles": this.css.viewFilterSearchCustomContentNode}).inject(this.viewSearchInputAreaNode);

        this.viewSearchCustomPathContentNode = new Element("div", {"styles": this.css.viewFilterSearchCustomPathContentNode}).inject(this.viewSearchCustomContentNode);
        this.viewSearchCustomComparisonContentNode = new Element("div", {"styles": this.css.viewFilterSearchCustomComparisonContentNode}).inject(this.viewSearchCustomContentNode);
        this.viewSearchCustomValueContentNode = new Element("div", {"styles": this.css.viewFilterSearchCustomValueContentNode}).inject(this.viewSearchCustomContentNode);
        this.viewSearchCustomAddContentNode = new Element("div", {"styles": this.css.viewFilterSearchCustomAddContentNode}).inject(this.viewSearchCustomContentNode);
        this.viewSearchCustomAddIconNode = new Element("div", {"styles": this.css.viewFilterSearchCustomAddIconNode}).inject(this.viewSearchCustomAddContentNode);
        this.viewSearchCustomFilterContentNode = new Element("div", {"styles": this.css.viewFilterSearchCustomFilterContentNode}).inject(this.viewSearchCustomContentNode);

        this.loadViewSearchCustomList();

        this.viewSearchCustomCloseActionNode.addEvents({
            "click": function(){this.closeCustomSearch();}.bind(this)
        });
        this.viewSearchCustomAddIconNode.addEvents({
            "click": function(){this.viewSearchCustomAddToFilter();}.bind(this)
        });
    },
    loadViewSearchCustomList: function(){
        this.viewSearchCustomPathListNode = new Element("select", {
            "styles": this.css.viewFilterSearchCustomPathListNode,
            "multiple": true
        }).inject(this.viewSearchCustomPathContentNode);
        this.viewSearchCustomComparisonListNode = new Element("select", {
            "styles": this.css.viewFilterSearchCustomComparisonListNode,
            "multiple": true
        }).inject(this.viewSearchCustomComparisonContentNode);

        this.viewJson.customFilterEntryList.each(function(entry){
            var option = new Element("option", {
                "style": this.css.viewFilterSearchOptionNode,
                "value": entry.path,
                "text": entry.title
            }).inject(this.viewSearchCustomPathListNode);
            option.store("entry", entry);
        }.bind(this));

        this.viewSearchCustomPathListNode.addEvent("change", function(){
            this.loadViewSearchCustomComparisonList();
        }.bind(this));
    },
    loadViewSearchCustomComparisonList: function(){
        var idx = this.viewSearchCustomPathListNode.selectedIndex;
        var option = this.viewSearchCustomPathListNode.options[idx];
        var entry = option.retrieve("entry");
        if (entry){
            switch (entry.formatType){
                case "numberValue":
                    this.loadComparisonSelect(this.lp.numberFilter);
                    this.loadViewSearchCustomValueNumberInput();
                    break;
                case "dateTimeValue":
                    this.loadComparisonSelect(this.lp.dateFilter);
                    this.loadViewSearchCustomValueDateInput();
                    break;
                case "booleanValue":
                    this.loadComparisonSelect(this.lp.booleanFilter);
                    this.loadViewSearchCustomValueBooleanInput();
                    break;
                default:
                    this.loadComparisonSelect(this.lp.textFilter);
                    this.loadViewSearchCustomValueTextInput();
            }
        }
    },
    loadViewSearchCustomValueNumberInput: function(){
        this.viewSearchCustomValueContentNode.empty();
        this.viewSearchCustomValueNode = new Element("input", {
            "styles": this.css.viewFilterSearchCustomValueNode,
            "type": "number"
        }).inject(this.viewSearchCustomValueContentNode);
    },
    loadViewSearchCustomValueDateInput: function(){
        this.viewSearchCustomValueContentNode.empty();
        this.viewSearchCustomValueNode = new Element("input", {
            "styles": this.css.viewFilterSearchCustomValueNode,
            "type": "text",
            "readonly": true
        }).inject(this.viewSearchCustomValueContentNode);
        MWF.require("MWF.widget.Calendar", function(){
            this.calendar = new MWF.widget.Calendar(this.viewSearchCustomValueNode, {
                "style": "xform",
                "isTime": true,
                "target": this.container,
                "format": "db"
            });
        }.bind(this));
    },
    loadViewSearchCustomValueBooleanInput: function(){
        this.viewSearchCustomValueContentNode.empty();
        this.viewSearchCustomValueNode = new Element("select", {
            "styles": this.css.viewFilterSearchCustomValueSelectNode,
            "multiple": true
        }).inject(this.viewSearchCustomValueContentNode);
        new Element("option", {"value": "true","text": this.lp.yes}).inject(this.viewSearchCustomValueNode);
        new Element("option", {"value": "false","text": this.lp.no}).inject(this.viewSearchCustomValueNode);
    },
    loadViewSearchCustomValueTextInput: function(){
        this.viewSearchCustomValueContentNode.empty();
        this.viewSearchCustomValueNode = new Element("textarea", {
            "styles": this.css.viewFilterSearchCustomValueNode
        }).inject(this.viewSearchCustomValueContentNode);
    },
    loadComparisonSelect:function(obj){
        this.viewSearchCustomComparisonListNode.empty();
        Object.each(obj, function(v, k){
            var option = new Element("option", {"value": k,"text": v}).inject(this.viewSearchCustomComparisonListNode);
        }.bind(this));
    },
    closeCustomSearch: function(){
        if (this.viewSearchCustomContentNode && this.viewSearchCustomContentNode.getStyle("display")==="block"){
            this.viewSearchCustomCloseActionNode.setStyle("display", "none");
            this.viewSearchCustomContentNode.setStyle("display", "none");

            var x = this.viewSearchInputAreaNode.getParent().getSize().x;
            x1 = x-2-10-90;
            this.css.viewFilterSearchInputAreaNode.width = ""+x1+"px";

            var x1 = this.viewSearchInputAreaNode.getSize().x-2;
            this.viewSearchInputAreaNode.setStyle("width", ""+x1+"px");

            if (!this.searchMorph) this.searchMorph = new Fx.Morph(this.viewSearchInputAreaNode);
            this.searchMorph.start(this.css.viewFilterSearchInputAreaNode).chain(function(){
                this.searchStatus = "default";
                this.css.viewFilterSearchInputAreaNode.width = "auto";
                this.viewSearchInputAreaNode.setStyle("margin-right", "90px");

                this.viewSearchIconNode.setStyle("display", "block");
                this.viewSearchInputBoxNode.setStyle("display", "block");
                this.viewSearchCustomActionNode.setStyle("display", "block");

                this.setContentHeightFun();
            }.bind(this));
            this.createViewNode({"filterList": this.json.filter || null});
        }
    },
    viewSearchCustomAddToFilter: function(){
        var pathIdx = this.viewSearchCustomPathListNode.selectedIndex;
        var comparisonIdx = this.viewSearchCustomComparisonListNode.selectedIndex;
        if (pathIdx===-1){
            MWF.xDesktop.notice("error", {"x": "left", "y": "top"}, this.lp.filterErrorTitle, this.viewSearchCustomPathListNode, {"x": 0, "y": 85});
            return false;
        }
        if (comparisonIdx===-1){
            MWF.xDesktop.notice("error", {"x": "left", "y": "top"}, this.lp.filterErrorComparison, this.viewSearchCustomComparisonListNode, {"x": 0, "y": 85});
            return false;
        }
        var pathOption = this.viewSearchCustomPathListNode.options[pathIdx];
        var entry = pathOption.retrieve("entry");
        if (entry){
            var pathTitle = entry.title;
            var path = entry.path;
            var comparison = this.viewSearchCustomComparisonListNode.options[comparisonIdx].get("value");
            var comparisonTitle = this.viewSearchCustomComparisonListNode.options[comparisonIdx].get("text");
            var value = "";

            switch (entry.formatType){
                case "numberValue":
                    value = this.viewSearchCustomValueNode.get("value");
                    break;
                case "dateTimeValue":
                    value = this.viewSearchCustomValueNode.get("value");
                    break;
                case "booleanValue":
                    var idx = this.viewSearchCustomValueNode.selectedIndex;
                    if (idx!==-1){
                        var v = this.viewSearchCustomValueNode.options[idx].get("value");
                        value = (v==="true");
                    }
                    break;
                default:
                    value = this.viewSearchCustomValueNode.get("value");
            }
            if (value===""){
                MWF.xDesktop.notice("error", {"x": "left", "y": "top"}, this.lp.filterErrorValue, this.viewSearchCustomValueContentNode, {"x": 0, "y": 85});
                return false;
            }

            this.filterItems.push(new MWF.xApplication.process.Application.Viewer.Filter(this, {
                "logic": "and",
                "path": path,
                "title": pathTitle,
                "comparison": comparison,
                "comparisonTitle": comparisonTitle,
                "value": value,
                "formatType": entry.formatType
            }, this.viewSearchCustomFilterContentNode));

            this.searchCustomView();
        }
    },
    searchViewRemoveFilter: function(filter){
        this.filterItems.erase(filter);
        filter.destroy();
        this.searchCustomView()
    },
    setContentHeight: function(){
        var size = this.node.getSize();
        var searchSize = this.searchAreaNode.getSize();
        var h = size.y-searchSize.y;
        this.viewAreaNode.setStyle("height", ""+h+"px");
    },
    createLoadding: function(){
        this.loadingAreaNode = new Element("div", {"styles": this.css.viewLoadingAreaNode}).inject(this.contentAreaNode);
        new Element("div", {"styles": {"height": "5px"}}).inject(this.loadingAreaNode);
        var loadingNode = new Element("div", {"styles": this.css.viewLoadingNode}).inject(this.loadingAreaNode);
        new Element("div", {"styles": this.css.viewLoadingIconNode}).inject(loadingNode);
        var loadingTextNode = new Element("div", {"styles": this.css.viewLoadingTextNode}).inject(loadingNode);
        loadingTextNode.set("text", "loading...");
    },
    createViewNode: function(data){
        this.viewAreaNode.empty();
        this.contentAreaNode = new Element("div", {"styles": this.css.contentAreaNode}).inject(this.viewAreaNode);

        this.viewTable = new Element("table", {
            "styles": this.css.viewTitleTableNode,
            "border": "0px",
            "cellPadding": "0",
            "cellSpacing": "0"
        }).inject(this.contentAreaNode);
        this.createLoadding();

        if (this.json.isTitle!=="no"){
            this.viewTitleLine = new Element("tr", {"styles": this.css.viewTitleLineNode}).inject(this.viewTable);

            //if (this.json.select==="single" || this.json.select==="multi") {
                this.selectTitleCell = new Element("td", {
                    "styles": this.css.viewTitleCellNode
                }).inject(this.viewTitleLine);
                this.selectTitleCell.setStyle("width", "10px");
                if (this.json.titleStyles) this.selectTitleCell.setStyles(this.json.titleStyles);
            //}
            this.entries = {};
            this.viewJson.selectEntryList.each(function(column){
                this.entries[column.column] = column;

                if (!column.hideColumn){
                    var viewCell = new Element("td", {
                        "styles": this.css.viewTitleCellNode,
                        "text": column.displayName
                    }).inject(this.viewTitleLine);
                    var size = MWF.getTextSize(column.displayName, this.css.viewTitleCellNode);
                    viewCell.setStyle("min-width", ""+size.x+"px");
                    if (this.json.titleStyles) viewCell.setStyles(this.json.titleStyles);
                }else{
                    this.hideColumns.push(column.column);
                }
                if (column.allowOpen) this.openColumns.push(column.column);
            }.bind(this));
            this.lookup(data);
        }else{
            this.viewJson.selectEntryList.each(function(column){
                if (column.hideColumn) this.hideColumns.push(column.column);
                if (!column.allowOpen) this.openColumns.push(column.column);
            }.bind(this));
            this.lookup(data);
        }
    },
    lookup: function(data){
        this.getLookupAction(function(){
            if (this.json.application){
                this.lookupAction.invoke({"name": "lookup","async": true, "data": (data || null), "parameter": {"view": this.json.name, "application": this.json.application},"success": function(json){
                    this.viewData = json.data;
                    if (this.viewJson.groupEntry.column){
                        this.gridJson = json.data.groupGrid;
                        this.loadGroupData();
                    }else{
                        this.gridJson = json.data.grid;
                        this.loadData();
                    }
                    if (this.loadingAreaNode){
                        this.loadingAreaNode.destroy();
                        this.loadingAreaNode = null;
                    }
                }.bind(this)});
            }
        }.bind(this));
    },
    loadData: function(){
        if (this.gridJson.length){
            this.gridJson.each(function(line, i){
                this.items.push(new MWF.xApplication.process.Application.Viewer.Item(this, line, null, i));
            }.bind(this));
        }
    },
    loadGroupData: function(){
        if (this.selectTitleCell){
            this.selectTitleCell.set("html", "<span style='font-family: Webdings'>"+"<img src='../x_component_process_Application/$Viewer/"+this.options.style+"/icon/expand.png'/>"+"</span>");
            this.selectTitleCell.setStyle("cursor", "pointer");
            this.selectTitleCell.addEvent("click", this.expandOrCollapseAll.bind(this));
        }

        if (this.gridJson.length){
            this.gridJson.each(function(data){
                this.items.push(new MWF.xApplication.process.Application.Viewer.ItemCategory(this, data));
            }.bind(this));

            this.expandOrCollapseAll();
        }
    },
    expandOrCollapseAll: function(){
        var icon = this.selectTitleCell.getElement("span");
        if (icon.get("html").indexOf("expand.png")===-1){
            this.items.each(function(item){
                item.collapse();
                icon.set("html", "<img src='../x_component_process_Application/$Viewer/"+this.options.style+"/icon/expand.png'/>");
            }.bind(this));
        }else{
            this.items.each(function(item, i){
                window.setTimeout(function(){
                    item.expand();
                }.bind(this), 10*i+5);

                icon.set("html", "<img src='../x_component_process_Application/$Viewer/"+this.options.style+"/icon/down.png'/>");
            }.bind(this));
        }
    },
    getView: function(callback){
        this.getLookupAction(function(){
            if (this.json.application){
                this.lookupAction.invoke({"name": "getView","async": true, "parameter": {"view": this.json.viewName, "application": this.json.application},"success": function(json){
                    this.viewJson = JSON.decode(json.data.data);
                    this.json = Object.merge(this.json, json.data);
                    //var viewData = JSON.decode(json.data.data);
                    if (callback) callback();
                }.bind(this)});
            }
        }.bind(this));
    },
    getLookupAction: function(callback){
        if (!this.lookupAction){
            var _self = this;
            MWF.require("MWF.xDesktop.Actions.RestActions", function(){
                this.lookupAction = new MWF.xDesktop.Actions.RestActions("", this.options.actionRoot, "");
                this.lookupAction.getActions = function(actionCallback){
                    this.actions = _self.options.actions;
                    if (actionCallback) actionCallback();
                };
                if (callback) callback();
            }.bind(this));
        }else{
            if (callback) callback();
        }
    },
    hide: function(){
        this.node.setStyle("display", "none");
    },
    reload: function(){
        this.node.setStyle("display", "block");
        if (this.loadingAreaNode) this.loadingAreaNode.setStyle("display", "block");

        this.filterItems.each(function(filter){
            filter.destroy();
        }.bind(this));
        this.filterItems = [];
        if (this.viewSearchInputNode) this.viewSearchInputNode.set("text", this.lp.searchKeywork);

        this.closeCustomSearch();

        this.viewAreaNode.empty();
        this.createViewNode({"filterList": this.json.filter || null});
    },
    getFilter: function(){
        var filterData = [];
        if (this.searchStatus==="custom"){
            if (this.filterItems.length){
                this.filterItems.each(function(filter){
                    filterData.push(filter.data);
                }.bind(this));
            }
        }
        if (this.searchStatus==="default"){
            var key = this.viewSearchInputNode.get("value");
            if (key && key!==this.lp.searchKeywork){
                this.viewJson.customFilterEntryList.each(function(entry){
                    if (entry.formatType==="textValue"){
                        var d = {
                            "path": entry.path,
                            "value": key,
                            "formatType": entry.formatType,
                            "logic": "or",
                            "comparison": "like"
                        };
                        filterData.push(d);
                    }
                    if (entry.formatType==="numberValue"){
                        var v = key.toFloat();
                        if (!isNaN(v)){
                            var d = {
                                "path": entry.path,
                                "value": v,
                                "formatType": entry.formatType,
                                "logic": "or",
                                "comparison": "like"
                            };
                            filterData.push(d);
                        }
                    }
                }.bind(this));
            }
        }
        return (filterData.length) ? filterData : null;
    },
    getData: function(){
        if (this.selectedItems.length){
            var arr = [];
            this.selectedItems.each(function(item){
                arr.push(item.data);
            });
            return arr;
        }else{
            return [];
        }
    }
});

MWF.xApplication.process.Application.Viewer.Item = new Class({
    initialize: function(view, data, prev, i){
        this.view = view;
        this.data = data;
        this.css = this.view.css;
        this.isSelected = false;
        this.prev = prev;
        this.load();
    },
    load: function(){
        this.node = new Element("tr", {"styles": this.css.viewContentTrNode});
        if (this.prev){
            this.node.inject(this.prev.node, "after");
        }else{
            this.node.inject(this.view.viewTable);
        }

        //if (this.view.json.select==="single" || this.view.json.select==="multi"){
            this.selectTd = new Element("td", {"styles": this.css.viewContentTdNode}).inject(this.node);
            this.selectTd.setStyles({"cursor": "pointer"});
            if (this.view.json.itemStyles) this.selectTd.setStyles(this.view.json.itemStyles);
        //}

        Object.each(this.data.data, function(cell, k){
            if (this.view.hideColumns.indexOf(k)===-1){
                var td = new Element("td", {"styles": this.css.viewContentTdNode}).inject(this.node);
                if (k!== this.view.viewJson.groupEntry.column){
                    var v = (this.view.entries[k].code) ? MWF.Macro.exec(this.view.entries[k].code, {"value": cell, "gridData": this.view.gridJson, "data": this.view.viewData, "entry": this.data}) : cell
                    td.set("text", v);
                }
                if (this.view.openColumns.indexOf(k)!==-1){
                    this.setOpenWork(td)
                }
                if (this.view.json.itemStyles) td.setStyles(this.view.json.itemStyles);
            }
        }.bind(this));

        this.setEvent();
    },
    setOpenWork: function(td){
        td.setStyle("cursor", "pointer");
        td.addEvent("click", this.openWorkAndCompleted.bind(this));
    },
    openWorkAndCompleted: function(e){
        this.view.lookupAction.invoke({"name": "listWorkByJob","async": true, "parameter": {"job": this.data.job},"success": function(json){
            var workCompletedCount = json.data.workCompletedList.length;
            var workCount = json.data.workList.length;
            var count = workCount+workCompletedCount;
            if (count===1){
                if (workCompletedCount) {
                    this.openWorkCompleted(json.data.workCompletedList[0].id, e);
                }else{
                    this.openWork(json.data.workList[0].id, e);
                }
            }else if (count>1){
                var worksAreaNode = this.createWorksArea();
                json.data.workCompletedList.each(function(work){
                    this.createWorkCompletedNode(work, worksAreaNode);
                }.bind(this));
                json.data.workList.each(function(work){
                    this.createWorkNode(work, worksAreaNode);
                }.bind(this));
                this.showWorksArea(worksAreaNode, e);
            }else{

            }
        }.bind(this)});
    },
    createWorkNode: function(work, worksAreaNode){
        var worksAreaContentNode = worksAreaNode.getLast();
        var node = new Element("div", {"styles": this.css.workAreaNode}).inject(worksAreaContentNode);
        var actionNode = new Element("div", {"styles": this.css.workAreaActionNode, "text": this.view.lp.open}).inject(node);

        actionNode.store("workId", work.id);
        actionNode.addEvent("click", function(e){
            this.openWork(e.target.retrieve("workId"), e);
            this.mask.hide();
            worksAreaNode.destroy();
        }.bind(this));

        var areaNode = new Element("div", {"styles": this.css.workAreaLeftNode}).inject(node);

        var titleNode = new Element("div", {"styles": this.css.workAreaTitleNode, "text": work.title}).inject(areaNode);
        var contentNode = new Element("div", {"styles": this.css.workAreaContentNode}).inject(areaNode);
        new Element("div", {"styles": this.css.workAreaContentTitleNode, "text": this.view.lp.activity+": "}).inject(contentNode);
        new Element("div", {"styles": this.css.workAreaContentTextNode, "text": work.activityName}).inject(contentNode);

        var taskUsers = [];
        this.view.lookupAction.invoke({"name": "listTaskByWork","async": true, "parameter": {"id": work.id},"success": function(json){
            json.data.taskList.each(function(task){
                taskUsers.push(MWF.name.cn(task.person));
            }.bind(this));
            new Element("div", {"styles": this.css.workAreaContentTitleNode, "text": this.view.lp.taskPeople+": "}).inject(contentNode);
            new Element("div", {"styles": this.css.workAreaContentTextNode, "text": taskUsers.join(", ")}).inject(contentNode);
        }.bind(this)});
    },
    createWorkCompletedNode: function(work, worksAreaNode){
        var worksAreaContentNode = worksAreaNode.getLast();

        var node = new Element("div", {"styles": this.css.workAreaNode}).inject(worksAreaContentNode);
        var actionNode = new Element("div", {"styles": this.css.workAreaActionNode, "text": this.view.lp.open}).inject(node);

        actionNode.store("workId", work.id);
        actionNode.addEvent("click", function(e){
            this.mask.hide();
            worksAreaNode.destroy();
            this.openWorkCompleted(e.target.retrieve("workId"), e)
        }.bind(this));

        var areaNode = new Element("div", {"styles": this.css.workAreaLeftNode}).inject(node);

        var titleNode = new Element("div", {"styles": this.css.workAreaTitleNode, "text": work.title}).inject(areaNode);
        var contentNode = new Element("div", {"styles": this.css.workAreaContentNode}).inject(areaNode);

        new Element("div", {"styles": this.css.workAreaContentTitleNode, "text": this.view.lp.activity+": "}).inject(contentNode);
        new Element("div", {"styles": this.css.workAreaContentTextNode, "text": this.view.lp.processCompleted}).inject(contentNode);
    },
    createWorksArea: function(){
        var cssWorksArea = this.css.worksAreaNode
        if (layout.mobile) {
            cssWorksArea = this.css.worksAreaNodeMobile;
        }
        var worksAreaNode = new Element("div", {"styles": cssWorksArea});
        // var worksAreaNode = new Element("div", {"styles": this.css.worksAreaNode});
        var worksAreaTitleNode = new Element("div", {"styles": this.css.worksAreaTitleNode}).inject(worksAreaNode);
        var worksAreaTitleCloseNode = new Element("div", {"styles": this.css.worksAreaTitleCloseNode}).inject(worksAreaTitleNode);
        worksAreaTitleCloseNode.addEvent("click", function(e){
            this.mask.hide();
            e.target.getParent().getParent().destroy();
        }.bind(this));
        var worksAreaContentNode = new Element("div", {"styles": this.css.worksAreaContentNode}).inject(worksAreaNode);

        return worksAreaNode;
    },
    showWorksArea: function(node, e){
        this.mask = new MWF.widget.Mask({"style": "desktop", "loading": false});
        this.mask.loadNode(this.view.container);

        node.inject(this.view.node);
        this.setWorksAreaPosition(node, e.target);
    },
    setWorksAreaPosition: function(node, td){
        var p = td.getPosition(this.view.container);
        var containerS = this.view.container.getSize();
        var containerP = this.view.container.getPosition(this.view.container.getOffsetParent());
        var s = node.getSize();
        var offX = p.x+s.x-containerS.x;
        offX = (offX>0) ? offX+20 : 0;
        var offY = p.y+s.y-containerS.y;
        offY = (offY>0) ? offY+5 : 0;

        node.position({
            "relativeTo": td,
            "position": "lefttop",
            "edge": "lefttop",
            "offset": {
                "x": 0-offX,
                "y": 0-offY
            }
        });
    },
    openWork: function(id, e){
        var options = {"workId": id};
        layout.desktop.openApplication(e, "process.Work", options);
    },
    openWorkCompleted: function(id, e){
        var options = {"workCompletedId": id};
        layout.desktop.openApplication(e, "process.Work", options);
    },

    setEvent: function(){
        if (this.view.json.select==="single" || this.view.json.select==="multi"){
            this.node.addEvents({
                "mouseover": function(){
                    if (!this.isSelected){
                        var iconName = "checkbox";
                        if (this.view.json.select==="single") iconName = "radiobox";
                        this.selectTd.setStyles({"background": "url("+"../x_component_process_Application/$Viewer/default/icon/"+iconName+".png) center center no-repeat"});
                    }
                }.bind(this),
                "mouseout": function(){
                    if (!this.isSelected) this.selectTd.setStyles({"background": "transparent"});
                }.bind(this),
                "click": function(){this.select();}.bind(this)
            });
        }
    },

    select: function(){
        if (this.isSelected){
            if (this.view.json.select==="single"){
                this.unSelectedSingle();
            }else{
                this.unSelected();
            }
        }else{
            if (this.view.json.select==="single"){
                this.selectedSingle();
            }else{
                this.selected();
            }
        }
        this.view.fireEvent("select");
    },

    selected: function(){
        this.view.selectedItems.push(this);
        this.selectTd.setStyles({"background": "url("+"../x_component_process_Application/$Viewer/default/icon/checkbox_checked.png) center center no-repeat"});
        this.node.setStyles(this.css.viewContentTrNode_selected);
        this.isSelected = true;
    },
    unSelected: function(){
        this.view.selectedItems.erase(this);
        this.selectTd.setStyles({"background": "transparent"});
        this.node.setStyles(this.css.viewContentTrNode);
        this.isSelected = false;
    },
    selectedSingle: function(){
        if (this.view.currentSelectedItem) this.view.currentSelectedItem.unSelectedSingle();
        this.view.selectedItems = [this];
        this.view.currentSelectedItem = this;
        this.selectTd.setStyles({"background": "url("+"../x_component_process_Application/$Viewer/default/icon/radiobox_checked.png) center center no-repeat"});
        this.node.setStyles(this.css.viewContentTrNode_selected);
        this.isSelected = true;
    },
    unSelectedSingle: function(){
        this.view.selectedItems = [];
        this.view.currentSelectedItem = null;
        this.selectTd.setStyles({"background": "transparent"});
        this.node.setStyles(this.css.viewContentTrNode);
        this.isSelected = false;
    }
});

MWF.xApplication.process.Application.Viewer.ItemCategory = new Class({
    initialize: function(view, data){
        this.view = view;
        this.data = data;
        this.css = this.view.css;
        this.items = [];
        this.loadChild = false;
        this.load();
    },
    load: function(){
        this.node = new Element("tr", {"styles": this.css.viewContentTrNode}).inject(this.view.viewTable);
        //if (this.view.json.select==="single" || this.view.json.select==="multi"){
            this.selectTd = new Element("td", {"styles": this.css.viewContentCategoryTdNode}).inject(this.node);
            if (this.view.json.itemStyles) this.selectTd.setStyles(this.view.json.itemStyles);
        //}
        this.categoryTd = new Element("td", {
            "styles": this.css.viewContentCategoryTdNode,
            "colspan": this.view.viewJson.selectEntryList.length
        }).inject(this.node);

        this.groupColumn = null;
        for (var c = 0; c<this.view.viewJson.selectEntryList.length; c++){
            if (this.view.viewJson.selectEntryList[c].column === this.view.viewJson.groupEntry.column){
                this.groupColumn = this.view.viewJson.selectEntryList[c];
                break;
            }
        }
        if (this.groupColumn){
            var text = (this.groupColumn.code) ? MWF.Macro.exec(this.groupColumn.code, {"value": this.data.group, "gridData": this.view.gridJson, "data": this.view.viewData, "entry": this.data}) : this.data.group;
        }else{
            var text = this.data.group;
        }

        this.categoryTd.set("html", "<span style='font-family: Webdings'><img src='../x_component_process_Application/$Viewer/"+this.view.options.style+"/icon/expand.png'/></span> "+text);
        if (this.view.json.itemStyles) this.categoryTd.setStyles(this.view.json.itemStyles);

        this.setEvent();
    },
    setEvent: function(){
        //if (this.selectTd){
            this.node.addEvents({
                "click": function(){this.expandOrCollapse();}.bind(this)
            });
        //}
    },
    expandOrCollapse: function(){
        var t = this.node.getElement("span").get("html");
        if (t.indexOf("expand.png")===-1){
            this.collapse();
        }else{
            this.expand();
        }
    },
    collapse: function(){
        this.items.each(function(item){
            item.node.setStyle("display", "none");
        }.bind(this));
        this.node.getElement("span").set("html", "<img src='../x_component_process_Application/$Viewer/"+this.view.options.style+"/icon/expand.png'/>");
    },
    expand: function(){
        this.items.each(function(item){
            item.node.setStyle("display", "table-row");
        }.bind(this));
        this.node.getElement("span").set("html", "<img src='../x_component_process_Application/$Viewer/"+this.view.options.style+"/icon/down.png'/>");
        if (!this.loadChild){
            //window.setTimeout(function(){
            this.data.list.each(function(line){
                this.items.push(new MWF.xApplication.process.Application.Viewer.Item(this.view, line, this));
            }.bind(this));
            this.loadChild = true;
            //}.bind(this), 10);
        }
    }
});

MWF.xApplication.process.Application.Viewer.Filter = new Class({
    initialize: function(viewer, data, node){
        this.viewer = viewer;
        this.data = data;
        this.css = this.viewer.css;
        this.content = node;
        this.load();
    },
    load: function(){
        this.node = new Element("div", {"styles": this.css.viewSearchFilterNode}).inject(this.content);
        if (this.viewer.filterItems.length){
            this.logicNode = new Element("div", {"styles": this.css.viewSearchFilterSelectAreaNode}).inject(this.node);
            this.logicSelectNode = new Element("div", {
                "styles": this.css.viewSearchFilterSelectNode,
                "text": this.viewer.lp.and,
                "value": "and"
            }).inject(this.logicNode);

            this.logicSelectButtonNode = new Element("div", {"styles": this.css.viewSearchFilterSelectButtonNode}).inject(this.logicNode);
            this.logicNode.addEvents({
                "click": function(){
                    var v = this.logicSelectNode.get("value");
                    if (v==="and"){
                        this.logicSelectButtonNode.setStyle("float", "left");
                        this.logicSelectNode.setStyle("float", "right");
                        this.logicSelectNode.set({
                            "text": this.viewer.lp.or,
                            "value": "or"
                        });
                        this.data.logic = "or";
                    }else{
                        this.logicSelectButtonNode.setStyle("float", "right");
                        this.logicSelectNode.setStyle("float", "left");
                        this.logicSelectNode.set({
                            "text": this.viewer.lp.and,
                            "value": "and"
                        });
                        this.data.logic = "and";
                    }
                    this.viewer.searchCustomView();
                }.bind(this)
            });
        }
        this.titleNode = new Element("div", {"styles": this.css.viewSearchFilterTextNode, "text": this.data.title || ""}).inject(this.node);
        this.comparisonTitleNode = new Element("div", {"styles": this.css.viewSearchFilterTextNode, "text": this.data.comparisonTitle}).inject(this.node);
        this.valueNode = new Element("div", {"styles": this.css.viewSearchFilterTextNode, "text": "\""+this.data.value+"\""}).inject(this.node);
        this.deleteNode = new Element("div", {"styles": this.css.viewSearchFilterDeleteNode}).inject(this.node);

        this.node.addEvents({
            "mouseover": function(){
                this.node.setStyles(this.css.viewSearchFilterNode_over);
                this.deleteNode.setStyles(this.css.viewSearchFilterDeleteNode_over);
            }.bind(this),
            "mouseout": function(){
                this.node.setStyles(this.css.viewSearchFilterNode);
                this.deleteNode.setStyles(this.css.viewSearchFilterDeleteNode);
            }.bind(this)
        });
        this.deleteNode.addEvent("click", function(){
            this.viewer.searchViewRemoveFilter(this);
        }.bind(this));
    },
    destroy: function(){
        this.node.destroy();
        MWF.release(this);
    }
});