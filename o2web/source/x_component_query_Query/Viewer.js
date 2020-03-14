MWF.xApplication.query = MWF.xApplication.query || {};
MWF.xApplication.query.Query = MWF.xApplication.query.Query || {};
MWF.require("MWF.widget.Common", null, false);
MWF.require("MWF.xScript.Macro", null, false);
MWF.xDesktop.requireApp("query.Query", "lp.zh-cn", null, false);
MWF.xApplication.query.Query.Viewer = MWF.QViewer = new Class({
    Implements: [Options, Events],
    Extends: MWF.widget.Common,
    options: {
        "style": "default",
        "resizeNode": true,
        "paging" : "scroll",
        "perPageCount" : 50,
        "isload": "true",
        "export": false
        // "actions": {
        //     "lookup": {"uri": "/jaxrs/view/flag/{view}/query/{application}/execute", "method":"PUT"},
        //     "getView": {"uri": "/jaxrs/view/flag/{view}/query/{application}"}
        //
        // },
        // "actionRoot": "x_query_assemble_surface"
    },
    initialize: function(container, json, options){
        this.setOptions(options);

        this.path = "/x_component_query_Query/$Viewer/";
        this.cssPath = "/x_component_query_Query/$Viewer/"+this.options.style+"/css.wcss";
        this._loadCss();
        this.lp = MWF.xApplication.query.Query.LP;

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

        if (this.options.isload){
            this.init(function(){
                this.load();
            }.bind(this));
        }
    },
    loadView: function(){
        if (this.viewJson){
            this.reload();
        }else{
            this.init(function(){
                this.load();
            }.bind(this));
        }
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
        this.createExportNode();
        this.createSearchNode();
        this.createViewNode({"filterList": this.json.filter  ? this.json.filter.clone() : null});

        if (this.options.resizeNode){
            this.setContentHeightFun = this.setContentHeight.bind(this);
            this.container.addEvent("resize", this.setContentHeightFun);
            this.setContentHeightFun();
        }
    },
    loadLayout: function(){
        this.node = new Element("div", {"styles": this.css.node}).inject(this.container);
        if (this.options.export) this.exportAreaNode = new Element("div", {"styles": this.css.exportAreaNode}).inject(this.node);
        this.searchAreaNode = new Element("div", {"styles": this.css.searchAreaNode}).inject(this.node);
        this.viewAreaNode = new Element("div", {"styles": this.css.viewAreaNode}).inject(this.node);
        this.viewPageNode = new Element("div", {"styles": this.css.viewPageNode}).inject(this.node);
        this.viewPageAreaNode = new Element("div", {"styles": this.css.viewPageAreaNode}).inject(this.viewPageNode);
    },
    createExportNode: function(){
        if (this.options.export){
            MWF.require("MWF.widget.Toolbar", function(){
                this.toolbar = new MWF.widget.Toolbar(this.exportAreaNode, {"style": "simple"}, this);
                var actionNode = new Element("div", {
                    "id": "",
                    "MWFnodetype": "MWFToolBarButton",
                    "MWFButtonImage": this.path+""+this.options.style+"/icon/export.png",
                    "title": this.lp.exportExcel,
                    "MWFButtonAction": "exportView",
                    "MWFButtonText": this.lp.exportExcel
                }).inject(this.exportAreaNode);

                this.toolbar.load();
            }.bind(this));
            //this.exportNode = new Element("button", {"text": this.lp.exportExcel}).inject(this.exportAreaNode);
        }
    },
    exportView: function(){
        var action = MWF.Actions.get("x_query_assemble_surface");

        var filterData = this.json.filter ? this.json.filter.clone() : [];
        if (this.filterItems.length){
            this.filterItems.each(function(filter){
                filterData.push(filter.data);
            }.bind(this));
        }
        action.exportViewWithQuery(this.json.viewName, this.json.application, {"filterList": filterData}, function(json){
            var uri = action.action.actions.getViewExcel.uri;
            uri = uri.replace("{flag}", json.data.id);
            uri = action.action.address+uri;
            var a = new Element("a", {"href": uri, "target":"_blank"});
            a.click();
            a.destroy();
        }.bind(this));
    },
    createSearchNode: function(){
        if (this.viewJson.customFilterList && this.viewJson.customFilterList.length){
            this.searchStatus = "default";
            this.loadFilterSearch();
        }else{
            this.loadSimpleSearch();
        }
    },
    loadSimpleSearch: function(){
        return false;
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
            //if (this.viewPageAreaNode) this.viewPageAreaNode.hide();
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
        if (this.viewJson.customFilterList) {
            var key = this.viewSearchInputNode.get("value");
            if (key && key !== this.lp.searchKeywork) {
                var filterData = this.json.filter ? this.json.filter : [];
                this.filterItems = [];
                this.viewJson.customFilterList.each(function (entry) {
                    if (entry.formatType === "textValue") {
                        var d = {
                            "path": entry.path,
                            "value": key,
                            "formatType": entry.formatType,
                            "logic": "or",
                            "comparison": "like"
                        };
                        filterData.push(d);
                        this.filterItems.push({"data":d});
                    }
                    if (entry.formatType === "numberValue") {
                        var v = key.toFloat();
                        if (!isNaN(v)) {
                            var d = {
                                "path": entry.path,
                                "value": v,
                                "formatType": entry.formatType,
                                "logic": "or",
                                "comparison": "like"
                            };
                            filterData.push(d);
                            this.filterItems.push({"data":d});
                        }
                    }
                }.bind(this));

                this.createViewNode({"filterList": filterData});
            }else{
                this.createViewNode();
            }
        }
    },
    searchCustomView: function(){
        if (this.filterItems.length){
            var filterData = this.json.filter ? this.json.filter.clone() : [];
            this.filterItems.each(function(filter){
                filterData.push(filter.data);
            }.bind(this));

            this.createViewNode({"filterList": filterData});
        }else{
            this.createViewNode({"filterList": this.json.filter ? this.json.filter.clone() : null});
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

        this.viewJson.customFilterList.each(function(entry){
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
                    this.loadViewSearchCustomValueDateTimeInput();
                    break;
                case "dateValue":
                    this.loadComparisonSelect(this.lp.dateFilter);
                    this.loadViewSearchCustomValueDateInput();
                    break;
                case "timeValue":
                    this.loadComparisonSelect(this.lp.dateFilter);
                    this.loadViewSearchCustomValueTimeInput();
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
    loadViewSearchCustomValueDateTimeInput: function(){
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
                "isTime": false,
                "target": this.container,
                "format": "%Y-%m-%d"
            });
        }.bind(this));
    },
    loadViewSearchCustomValueTimeInput: function(){
        this.viewSearchCustomValueContentNode.empty();
        this.viewSearchCustomValueNode = new Element("input", {
            "styles": this.css.viewFilterSearchCustomValueNode,
            "type": "text",
            "readonly": true
        }).inject(this.viewSearchCustomValueContentNode);
        MWF.require("MWF.widget.Calendar", function(){
            this.calendar = new MWF.widget.Calendar(this.viewSearchCustomValueNode, {
                "style": "xform",
                "timeOnly": true,
                "target": this.container,
                "format": "%H:%M:%S"
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
            this.createViewNode({"filterList": this.json.filter ? this.json.filter.clone() : null});
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

            this.filterItems.push(new MWF.xApplication.query.Query.Viewer.Filter(this, {
                "logic": "and",
                "path": path,
                "title": pathTitle,
                "comparison": comparison,
                "comparisonTitle": comparisonTitle,
                "value": value,
                "formatType": (entry.formatType=="datetimeValue") ? "dateTimeValue": entry.formatType
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
        var searchSize = this.searchAreaNode.getComputedSize();
        var h = size.y-searchSize.totalHeight;
        if (this.exportAreaNode){
            var exportSize = this.exportAreaNode.getComputedSize();
            h = h-exportSize.totalHeight;
        }
        var pageSize = this.viewPageNode.getComputedSize();
        h = h-pageSize.totalHeight;
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

            //序号
            if (this.viewJson.isSequence==="yes"){
                this.sequenceTitleCell = new Element("td", {
                    "styles": this.css.viewTitleCellNode
                }).inject(this.viewTitleLine);
                this.sequenceTitleCell.setStyle("width", "10px");
                if (this.json.titleStyles) this.sequenceTitleCell.setStyles(this.json.titleStyles);
            }

            this.entries = {};
            this.viewJson.selectList.each(function(column){
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
            this.viewJson.selectList.each(function(column){
                if (column.hideColumn) this.hideColumns.push(column.column);
                if (!column.allowOpen) this.openColumns.push(column.column);
            }.bind(this));
            this.lookup(data);
        }
    },
    _loadPageCountNode: function(){
        this.viewPageContentNode.empty();

        var size = this.viewPageAreaNode.getSize();
        var w1 = this.viewPageFirstNode.getSize().x*2;
        var w2 = this.viewPageContentNode.getStyle("margin-left").toInt();
        var w = size.x-w1-w2;

        var bw = this.css.viewPageButtonNode.width.toInt()+this.css.viewPageButtonNode["margin-right"].toInt();
        var count = (w/bw).toInt()-2;
        if (count>10) count = 10;
        this.showPageCount = Math.min(count, this.pages);

        var tmp = this.showPageCount/2;
        var n = tmp.toInt();
        var left = this.currentPage-n;
        if (left<=0) left = 1;
        var right = this.showPageCount + left-1;
        if (right>this.pages) right = this.pages;
        left = right-this.showPageCount+1;
        if (left<=1) left = 1;

        this.viewPagePrevNode = new Element("div", {"styles": this.css.viewPagePrevButtonNode}).inject(this.viewPageContentNode);
        this.loadPageButtonEvent(this.viewPagePrevNode, "viewPagePrevButtonNode_over", "viewPagePrevButtonNode_up", "viewPagePrevButtonNode_down", function(){
            if (this.currentPage>1) this.currentPage--;
            this.loadCurrentPageData();
        }.bind(this));

        for (i=left; i<=right; i++){
            var node = new Element("div", {"styles": this.css.viewPageButtonNode, "text": i}).inject(this.viewPageContentNode);
            if (i==this.currentPage){
                node.setStyles(this.css.viewPageButtonNode_current);
            }else{
                this.loadPageButtonEvent(node, "viewPageButtonNode_over", "viewPageButtonNode_up", "viewPageButtonNode_down", function(e){
                    this.currentPage = e.target.get("text").toInt();
                    this.loadCurrentPageData();
                }.bind(this));
            }
        }
        this.viewPageNextNode = new Element("div", {"styles": this.css.viewPageNextButtonNode}).inject(this.viewPageContentNode);
        this.loadPageButtonEvent(this.viewPageNextNode, "viewPageNextButtonNode_over", "viewPageNextButtonNode_up", "viewPageNextButtonNode_down", function(){
            if (this.currentPage<=this.pages-1) this.currentPage++;
            this.loadCurrentPageData();
        }.bind(this));
    },
    loadPageButtonEvent: function(node, over, out, down, click){
        node.addEvents({
            "mouseover": function(){node.setStyles(this.css[over])}.bind(this),
            "mouseout": function(){node.setStyles(this.css[out])}.bind(this),
            "mousedown": function(){node.setStyles(this.css[down])}.bind(this),
            "mouseup": function(){node.setStyles(this.css[out])}.bind(this),
            "click": click,
        });
    },
    _loadPageNode: function(){
        this.viewPageAreaNode.empty();
        this.viewPageFirstNode = new Element("div", {"styles": this.css.viewPageFirstLastNode, "text": this.lp.firstPage}).inject(this.viewPageAreaNode);
        this.viewPageContentNode = new Element("div", {"styles": this.css.viewPageContentNode}).inject(this.viewPageAreaNode);
        this.viewPageLastNode = new Element("div", {"styles": this.css.viewPageFirstLastNode, "text": this.lp.lastPage}).inject(this.viewPageAreaNode);
        this._loadPageCountNode();

        this.loadPageButtonEvent(this.viewPageFirstNode, "viewPageFirstLastNode_over", "viewPageFirstLastNode_up", "viewPageFirstLastNode_down", function(){
            this.currentPage = 1;
            this.loadCurrentPageData();
        }.bind(this));
        this.loadPageButtonEvent(this.viewPageLastNode, "viewPageFirstLastNode_over", "viewPageFirstLastNode_up", "viewPageFirstLastNode_down", function(){
            this.currentPage = this.pages;
            this.loadCurrentPageData();
        }.bind(this));
    },
    _initPage: function(){
        this.count = this.bundleItems.length;
        var i = this.count/this.json.pageSize;
        this.pages = (i.toInt()<i) ? i.toInt()+1 : i;
        this.currentPage = 1;
    },
    lookup: function(data){
        this.getLookupAction(function(){
            if (this.json.application){

                var d = data || {};
                d.count = this.json.count;
                this.lookupAction.bundleView(this.json.id, d, function(json){
                    this.bundleItems = json.data.valueList;

                    this._initPage();
                    if (this.bundleItems.length){
                        this.loadCurrentPageData();
                    }else{
                        //this._loadPageNode();
                        this.viewPageAreaNode.empty();
                        if (this.loadingAreaNode){
                            this.loadingAreaNode.destroy();
                            this.loadingAreaNode = null;
                        }
                    }


                }.bind(this));
            }
        }.bind(this));
    },
    loadCurrentPageData: function(){
        var p = this.currentPage;
        var d = {};
        var valueList = this.bundleItems.slice((p-1)*this.json.pageSize,this.json.pageSize*p);
        d.bundleList = valueList;

        while (this.viewTable.rows.length>1){
            this.viewTable.deleteRow(-1);
        }
        //this.createLoadding();

        this.loadViewRes = this.lookupAction.loadView(this.json.name, this.json.application, d, function(json){
            this.viewData = json.data;
            if (this.viewJson.group.column){
                this.gridJson = json.data.groupGrid;
                this.loadGroupData();
            }else{
                this.gridJson = json.data.grid;
                this.loadData();
            }
            if (this.gridJson.length) this._loadPageNode();
            if (this.loadingAreaNode){
                this.loadingAreaNode.destroy();
                this.loadingAreaNode = null;
            }
            this.fireEvent("loadView");
        }.bind(this));
    },


    loadData: function(){
        if (this.gridJson.length){
            // if( !this.options.paging ){
                this.gridJson.each(function(line, i){
                    this.items.push(new MWF.xApplication.query.Query.Viewer.Item(this, line, null, i));
                }.bind(this));
            // }else{
            //     this.loadPaging();
            // }
        }else{
            if (this.viewPageAreaNode) this.viewPageAreaNode.empty();
        }
    },
    loadPaging : function(){
        this.isItemsLoading = false;
        this.pageNumber = 0;
        this.isItemsLoaded = false;
        this.isSetedScroll = false;
        this.setScroll();
        this.loadDataByPaging()
    },
    setScroll : function(){
        if( this.options.paging && !this.isSetedScroll ){
            this.contentAreaNode.setStyle("overflow","auto");
            this.scrollContainerFun = function(){
                var scrollSize = this.contentAreaNode.getScrollSize();
                var clientSize = this.contentAreaNode.getSize();
                var scrollHeight = scrollSize.y - clientSize.y;
                //alert( "clientSize.y=" + clientSize.y + " scrollSize.y="+scrollSize.y + " this.contentAreaNode.scrollTop="+this.contentAreaNode.scrollTop);
                if (this.contentAreaNode.scrollTop + 150 > scrollHeight ) {
                    if (!this.isItemsLoaded) this.loadDataByPaging();
                }
            }.bind(this);
            this.isSetedScroll = true;
            this.contentAreaNode.addEvent("scroll", this.scrollContainerFun )
        }
    },
    loadDataByPaging : function(){
        if( this.isItemsLoading )return;
        if( !this.isItemsLoaded ){
            var from = Math.min( this.pageNumber * this.options.perPageCount , this.gridJson.length);
            var to = Math.min( ( this.pageNumber + 1 ) * this.options.perPageCount + 1 , this.gridJson.length);
            this.isItemsLoading = true;
            for( var i = from; i<to; i++ ){
                this.items.push(new MWF.xApplication.query.Query.Viewer.Item(this, this.gridJson[i], null, i));
            }
            this.isItemsLoading = false;
            this.pageNumber ++;
            if( to == this.gridJson.length )this.isItemsLoaded = true;
        }
    },
    loadGroupData: function(){
        if (this.selectTitleCell){
            this.selectTitleCell.set("html", "<span style='font-family: Webdings'>"+"<img src='/x_component_query_Query/$Viewer/"+this.options.style+"/icon/expand.png'/>"+"</span>");
            this.selectTitleCell.setStyle("cursor", "pointer");
            this.selectTitleCell.addEvent("click", this.expandOrCollapseAll.bind(this));
        }

        if (this.gridJson.length){
            var i = 0;
            this.gridJson.each(function(data){
                this.items.push(new MWF.xApplication.query.Query.Viewer.ItemCategory(this, data, i));
                i += data.list.length;
            }.bind(this));

            if (this.json.isExpand=="yes") this.expandOrCollapseAll();
        }else{
            if (this.viewPageAreaNode) this.viewPageAreaNode.empty();
        }
    },
    expandOrCollapseAll: function(){
        var icon = this.selectTitleCell.getElement("span");
        if (icon.get("html").indexOf("expand.png")===-1){
            this.items.each(function(item){
                item.collapse();
                icon.set("html", "<img src='/x_component_query_Query/$Viewer/"+this.options.style+"/icon/expand.png'/>");
            }.bind(this));
        }else{
            this.items.each(function(item, i){
                window.setTimeout(function(){
                    item.expand();
                }.bind(this), 10*i+5);

                icon.set("html", "<img src='/x_component_query_Query/$Viewer/"+this.options.style+"/icon/down.png'/>");
            }.bind(this));
        }
    },
    getView: function(callback){
        this.getLookupAction(function(){
            if (this.json.application){
                this.getViewRes = this.lookupAction.getView(this.json.viewName, this.json.application, function(json){
                    this.viewJson = JSON.decode(json.data.data);
                    this.json = Object.merge(this.json, json.data);
                    if (callback) callback();
                }.bind(this));

                // this.lookupAction.invoke({"name": "getView","async": true, "parameter": {"view": this.json.viewName, "application": this.json.application},"success": function(json){
                //     this.viewJson = JSON.decode(json.data.data);
                //     this.json = Object.merge(this.json, json.data);
                //     //var viewData = JSON.decode(json.data.data);
                //     if (callback) callback();
                // }.bind(this)});
            }else{
                this.getViewRes = this.lookupAction.getViewById(this.json.viewId, function(json){
                    this.viewJson = JSON.decode(json.data.data);
                    this.json.application = json.data.query;
                    this.json = Object.merge(this.json, json.data);
                    if (callback) callback();
                }.bind(this));
            }
        }.bind(this));
    },
    getLookupAction: function(callback){
        if (!this.lookupAction){
            this.lookupAction = MWF.Actions.get("x_query_assemble_surface");
            if (callback) callback();
            // var _self = this;
            // MWF.require("MWF.xDesktop.Actions.RestActions", function(){
            //     this.lookupAction = new MWF.xDesktop.Actions.RestActions("", this.options.actionRoot, "");
            //     this.lookupAction.getActions = function(actionCallback){
            //         this.actions = _self.options.actions;
            //         if (actionCallback) actionCallback();
            //     };
            //     if (callback) callback();
            // }.bind(this));
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
        this.createViewNode({"filterList": this.json.filter ? this.json.filter.clone() : null});
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
                this.viewJson.customFilterList.each(function(entry){
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

MWF.xApplication.query.Query.Viewer.Item = new Class({
    initialize: function(view, data, prev, i){
        this.view = view;
        this.data = data;
        this.css = this.view.css;
        this.isSelected = false;
        this.prev = prev;
        this.idx = i;
        this.load();
    },
    load: function(){
        debugger;
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

        //序号
        if (this.view.viewJson.isSequence==="yes"){
            this.sequenceTd = new Element("td", {"styles": this.css.viewContentTdNode}).inject(this.node);
            this.sequenceTd.setStyle("width", "10px");
            var s= 1+this.view.json.pageSize*(this.view.currentPage-1)+this.idx;
            this.sequenceTd.set("text", s);
        }

        Object.each(this.view.entries, function(c, k){
            debugger;
            var cell = this.data.data[k];
            if (cell === undefined) cell = "";
            //if (cell){
                if (this.view.hideColumns.indexOf(k)===-1){
                    var td = new Element("td", {"styles": this.css.viewContentTdNode}).inject(this.node);
                    if (k!== this.view.viewJson.group.column){
                        //var v = (this.view.entries[k].code) ? MWF.Macro.exec(this.view.entries[k].code, {"value": cell, "gridData": this.view.gridJson, "data": this.view.viewData, "entry": this.data}) : cell;
                        var v = cell;
                        if (c.isHtml){
                            td.set("html", v);
                        }else{
                            td.set("text", v);
                        }
                    }
                    if (this.view.openColumns.indexOf(k)!==-1){
                        this.setOpenWork(td, c)
                    }
                    if (this.view.json.itemStyles) td.setStyles(this.view.json.itemStyles);
                }
            //}
        }.bind(this));

        // Object.each(this.data.data, function(cell, k){
        //     if (this.view.hideColumns.indexOf(k)===-1){
        //         var td = new Element("td", {"styles": this.css.viewContentTdNode}).inject(this.node);
        //         if (k!== this.view.viewJson.group.column){
        //             var v = (this.view.entries[k].code) ? MWF.Macro.exec(this.view.entries[k].code, {"value": cell, "gridData": this.view.gridJson, "data": this.view.viewData, "entry": this.data}) : cell;
        //             td.set("text", v);
        //         }
        //         if (this.view.openColumns.indexOf(k)!==-1){
        //             this.setOpenWork(td)
        //         }
        //         if (this.view.json.itemStyles) td.setStyles(this.view.json.itemStyles);
        //     }
        // }.bind(this));

        this.setEvent();
    },
    setOpenWork: function(td, column){
        debugger;
        td.setStyle("cursor", "pointer");
        if( column.clickCode ){
            if( !this.view.Macro ){
                MWF.require("MWF.xScript.Macro", function () {
                    this.view.businessData = {};
                    this.view.Macro = new MWF.Macro.PageContext(this.view);
                }.bind(this), false);
            }
            td.addEvent("click", function( ev ){
                return this.view.Macro.fire(column.clickCode, this, ev);
            }.bind(this));
        }else{
            if (this.view.json.type==="cms"){
                td.addEvent("click", this.openCms.bind(this));
            }else{
                td.addEvent("click", this.openWorkAndCompleted.bind(this));
            }
        }

    },
    openCms: function(e){
        var options = {"documentId": this.data.bundle};
        this.view.fireEvent("openDocument", [options, this]);
        layout.desktop.openApplication(e, "cms.Document", options);
    },
    openWorkAndCompleted: function(e){
        MWF.Actions.get("x_processplatform_assemble_surface").listWorkByJob(this.data.bundle, function(json){
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
        }.bind(this));
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
        MWF.Actions.get("x_processplatform_assemble_surface").listTaskByWork(work.id, function(json){
            json.data.each(function(task){
                taskUsers.push(MWF.name.cn(task.person));
            }.bind(this));
            new Element("div", {"styles": this.css.workAreaContentTitleNode, "text": this.view.lp.taskPeople+": "}).inject(contentNode);
            new Element("div", {"styles": this.css.workAreaContentTextNode, "text": taskUsers.join(", ")}).inject(contentNode);
        }.bind(this));
    },
    createWorkCompletedNode: function(work, worksAreaNode){
        var worksAreaContentNode = worksAreaNode.getLast();

        var node = new Element("div", {"styles": this.css.workAreaNode}).inject(worksAreaContentNode);
        var actionNode = new Element("div", {"styles": this.css.workAreaActionNode, "text": this.view.lp.open}).inject(node);

        actionNode.store("workId", work.id);
        actionNode.addEvent("click", function(e){
            this.mask.hide();
            var id = e.target.retrieve("workId");
            worksAreaNode.destroy();
            this.openWorkCompleted(id, e);
        }.bind(this));

        var areaNode = new Element("div", {"styles": this.css.workAreaLeftNode}).inject(node);

        var titleNode = new Element("div", {"styles": this.css.workAreaTitleNode, "text": work.title}).inject(areaNode);
        var contentNode = new Element("div", {"styles": this.css.workAreaContentNode}).inject(areaNode);

        new Element("div", {"styles": this.css.workAreaContentTitleNode, "text": this.view.lp.activity+": "}).inject(contentNode);
        new Element("div", {"styles": this.css.workAreaContentTextNode, "text": this.view.lp.processCompleted}).inject(contentNode);
    },
    createWorksArea: function(){
        var worksAreaNode = new Element("div", {"styles": this.css.worksAreaNode});
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
        MWF.require("MWF.widget.Mask", null, false);
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
        this.view.fireEvent("openDocument", [options, this]);
        layout.desktop.openApplication(e, "process.Work", options);
    },
    openWorkCompleted: function(id, e){
        var options = {"workCompletedId": id};
        this.view.fireEvent("openDocument", [options, this]);
        layout.desktop.openApplication(e, "process.Work", options);
    },

    setEvent: function(){
        if (this.view.json.select==="single" || this.view.json.select==="multi"){
            this.node.addEvents({
                "mouseover": function(){
                    if (!this.isSelected){
                        var iconName = "checkbox";
                        if (this.view.json.select==="single") iconName = "radiobox";
                        this.selectTd.setStyles({"background": "url("+"/x_component_query_Query/$Viewer/default/icon/"+iconName+".png) center center no-repeat"});
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
        this.selectTd.setStyles({"background": "url("+"/x_component_query_Query/$Viewer/default/icon/checkbox_checked.png) center center no-repeat"});
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
        this.selectTd.setStyles({"background": "url("+"/x_component_query_Query/$Viewer/default/icon/radiobox_checked.png) center center no-repeat"});
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

MWF.xApplication.query.Query.Viewer.ItemCategory = new Class({
    initialize: function(view, data, i){
        this.view = view;
        this.data = data;
        this.css = this.view.css;
        this.items = [];
        this.loadChild = false;
        this.idx = i;
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
            "colspan": this.view.viewJson.selectList.length+1
        }).inject(this.node);

        this.groupColumn = null;
        for (var c = 0; c<this.view.viewJson.selectList.length; c++){
            if (this.view.viewJson.selectList[c].column === this.view.viewJson.group.column){
                this.groupColumn = this.view.viewJson.selectList[c];
                break;
            }
        }
        if (this.groupColumn){
            //var text = (this.groupColumn.code) ? MWF.Macro.exec(this.groupColumn.code, {"value": this.data.group, "gridData": this.view.gridJson, "data": this.view.viewData, "entry": this.data}) : this.data.group;
            var text = this.data.group;
        }else{
            var text = this.data.group;
        }

        this.categoryTd.set("html", "<span style='font-family: Webdings'><img src='/x_component_query_Query/$Viewer/"+this.view.options.style+"/icon/expand.png'/></span> "+text);
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
        this.node.getElement("span").set("html", "<img src='/x_component_query_Query/$Viewer/"+this.view.options.style+"/icon/expand.png'/>");
    },
    expand: function(){
        this.items.each(function(item){
            item.node.setStyle("display", "table-row");
        }.bind(this));
        this.node.getElement("span").set("html", "<img src='/x_component_query_Query/$Viewer/"+this.view.options.style+"/icon/down.png'/>");
        if (!this.loadChild){
            //window.setTimeout(function(){
            this.data.list.each(function(line, i){
                var s = this.idx+i;
                this.lastItem = new MWF.xApplication.query.Query.Viewer.Item(this.view, line, (this.lastItem || this), s);
                this.items.push(this.lastItem);
            }.bind(this));
            this.loadChild = true;
            //}.bind(this), 10);
        }
    }
});

MWF.xApplication.query.Query.Viewer.Filter = new Class({
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
        this.titleNode = new Element("div", {"styles": this.css.viewSearchFilterTextNode, "text": this.data.title}).inject(this.node);
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
