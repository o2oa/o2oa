MWF.xApplication.cms = MWF.xApplication.cms || {};
MWF.xApplication.cms.Module = MWF.xApplication.cms.Module || {};

MWF.require("MWF.xAction.org.express.RestActions", null,false);
MWF.xDesktop.requireApp("cms.Module", "lp."+MWF.language, null, false);
MWF.xDesktop.requireApp("cms.Module", "package", null, false);
MWF.xDesktop.requireApp("process.Application", "Viewer", null, false);
MWF.xDesktop.requireApp("query.Query", "Viewer", null, false);

MWF.xApplication.cms.Module.ViewExplorer = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],
    options: {
        "style": "default",
        "isAdmin": false,
        "searchKey" : ""
    },
    initialize: function( node, app, columnData, categoryData, revealData, options, searchNode ){
        this.setOptions(options);

        this.node = node;
        this.app = app;
        this.columnData = columnData;
        this.categoryData = categoryData;
        this.revealData = revealData;
        this.searchNode = searchNode;

        this.path = "../x_component_cms_Module/$ViewExplorer/";
        this.cssPath = "../x_component_cms_Module/$ViewExplorer/"+this.options.style+"/css.wcss";
        this._loadCss();
    },
    reload: function(){
        this.node.empty();
        this.searchNode.empty();
        this.load();
    },
    load : function(){
        this.loadContentNode();
        this.loadQuryView();
        //if( this.revealData.viewType ){
        //    this.loadQuryView(); //QeuryView
        //}else{
        //    this.loadView(); //CMSView
        //}
    },
    loadContentNode: function(){
        this.elementContentNode = new Element("div", {
            "styles": this.css.elementContentNode
        }).inject(this.node);

        this.searchContainer = new Element("div",{
            "styles" : this.css.searchContainer
        }).inject( this.searchNode );

        this.resizeFun = function(){this.setContentSize();}.bind(this);

        this.app.addEvent("resize", this.resizeFun );

    },
    loadQuryView : function(){
        var viewJson = {
            "application": this.revealData.appName,
            "viewName": this.revealData.name,
            "isTitle": "yes",
            "select": "none",
            "titleStyles": this.css.normalThNode,
            "itemStyles": {},
            // "isExpand": "no",
            "filter": []
        };

        this.view = new MWF.xApplication.cms.Module.QueryViewer(this.elementContentNode, viewJson, {
            "hasAction" : this.options.isAdmin,
            "resizeNode": true,
            "selectEnable" : this.selectEnable,
            "onSelect": function(){
                this.fireEvent("select");
            }.bind(this)
        }, this.app, this.searchContainer);

        this.setContentSize();
    },
    //loadView : function(){
    //    var viewJson = {
    //        "application": this.columnData.id,
    //        "viewName": this.revealData.name,
    //        "isTitle": "yes",
    //        "select":  "none", //none , single, multi
    //        "titleStyles": this.css.normalThNode,
    //        "isExpand": "no",
    //        "itemStyles": {}
    //    };
    //
    //    this.view = new MWF.xApplication.cms.Module.Viewer(this.elementContentNode, viewJson, {
    //        "type" : "cms",
    //        "hasAction" : this.options.isAdmin,
    //        "actions": {
    //            //"lookup": {"uri": "/jaxrs/queryview/flag/{view}/application/flag/{application}/execute", "method":"PUT"},
    //            //"getView": {"uri": "/jaxrs/queryview/flag/{view}/application/flag/{application}"},
    //            "lookup": {"uri": "/jaxrs/view/{view}/execute", "method":"PUT"},
    //            "getView": {"uri": "/jaxrs/view/{view}"},
    //            "deleteDocument" : {"uri":"/jaxrs/document/{id}","method": "DELETE"}
    //        },
    //        "actionRoot": "x_query_assemble_surface",
    //        "resizeNode": true,
    //        "selectEnable" : this.selectEnable,
    //        "onSelect": function(){
    //            this.fireEvent("select");
    //        }.bind(this)
    //    }, this.app, this.searchContainer);
    //
    //    this.setContentSize();
    //},
    selectMode : function(){
        this.selectEnable = true;
        this.view.selectMode()
    },
    disableSelectMode : function(){
        this.selectEnable = false;
        this.view.disableSelectMode()
    },
    getSelectedIds : function(){
        return this.view.getSelectedIds();
    },
    setContentSize: function(){
        var toolbarSize = this.toolbarNode ? this.toolbarNode.getSize() : {"x":0,"y":0};
        var titlebarSize = {"x":0,"y":0}; //this.app.titleBar ? this.app.titleBar.getSize() : {"x":0,"y":0};
        var nodeSize = this.app.node.getSize();
        var pt = 0; //this.elementContentNode.getStyle("padding-top").toFloat();
        var pb = 0; // this.elementContentNode.getStyle("padding-bottom").toFloat();

        var filterConditionSize = this.filterConditionNode ? this.filterConditionNode.getSize() : {"x":0,"y":0};

        var height = nodeSize.y-toolbarSize.y-pt-pb-filterConditionSize.y-titlebarSize.y;
        this.elementContentNode.setStyle("height", ""+height+"px");
        if( this.view )this.view.setContentHeight();
    }
});


MWF.xApplication.cms.Module.QueryViewer = new Class({
    Implements: [Options, Events],
    Extends: MWF.QViewer,
    options: {
        "style": "default",
        "hasAction" : false, //cxy add
        "resizeNode": true,
        "paging" : "scroll",
        "perPageCount" : 50,
        "selectEnable" : false
    },
    initialize: function(container, json, options, app, searchContainer){
        this.setOptions(options);
        this.app = app;
        this.searchContainer = searchContainer;

        this.path = "../x_component_cms_Module/$ViewExplorer/";
        this.cssPath = "../x_component_cms_Module/$ViewExplorer/"+this.options.style+"/viewer.wcss";
        this._loadCss();
        this.lp = MWF.xApplication.query.Query.LP;

        this.container = $(container);
        this.json = json;

        this.viewJson = null;
        this.filterItems = [];
        this.searchStatus = "none"; //none, custom, default

        this.originalJson = Object.clone(json);

        this.selectEnable = this.options.selectEnable;

        this.items = [];
        this.selectedItems = [];
        this.hideColumns = [];
        this.openColumns = [];

        this.gridJson = null;

        this.init(function(){
            this.load();
        }.bind(this));
    },

    selectMode : function(){
        this.selectEnable = true;
        this.createSelectTh();
        this.items.each( function (it) {
            if( it.clazzType == "category" ){
                it.items.each( function(i){
                    i.createSelectTd();
                })
            }else{
                it.createSelectTd();
            }
        }.bind(this));
    },
    disableSelectMode : function(){
        this.selectEnable = false;
        this.destroySelectTh();
        this.items.each( function (it) {
            if( it.clazzType == "category" ){
                it.items.each( function(i){
                    i.destroySelectTd();
                })
            }else{
                it.destroySelectTd();
            }
        }.bind(this));
    },
    destroySelectTh : function(){
        if( this.selectTh ){
            this.selectTh.destroy();
            this.selectTh = null;
        }
    },
    createSelectTh : function(){
        var viewStyles = this.viewJson.viewStyles;
        var viewTitleCellNode = (viewStyles && viewStyles["titleTd"]) ? viewStyles["titleTd"] : this.css.viewTitleCellNode;

        this.selectTh = new Element("td",{
            styles: viewTitleCellNode
        }).inject(this.viewTitleLine, "top");
        this.selectTh.setStyles({width:"20px"});
        this.checkboxElement = new Element("input", {
            "type": "checkbox"
        }).inject(this.selectTh);
        this.checkboxElement.addEvent("click", function () {
            this.selectAll()
        }.bind(this));
    },
    selectAll : function () {
        var flag = this.checkboxElement.get("checked");
        this.items.each(function (it) {
            if( it.clazzType == "category" ){
                it.expand();
                it.items.each( function(i){
                    if (i.checkboxElement)i.checkboxElement.set("checked", flag)
                })
            }else{
                if (it.checkboxElement)it.checkboxElement.set("checked", flag)
            }
        }.bind(this))
    },
    getSelectedIds : function(){
        var checkedItems = [];
        this.items.each(function (it) {
            if( it.clazzType == "category" ){
                it.items.each( function(i){
                    if (i.checkboxElement.get("checked")) {
                        checkedItems.push( i.data.bundle )
                    }
                })
            }else{
                if (it.checkboxElement.get("checked")) {
                    checkedItems.push( it.data.bundle )
                }
            }
        }.bind(this));
        return checkedItems;
    },
    getSelectedItems : function(){
        var checkedItems = [];
        this.items.each(function (it) {
            if( it.clazzType == "category" ){
                it.items.each( function(i){
                    if (i.checkboxElement.get("checked")) {
                        checkedItems.push( i )
                    }
                })
            }else{
                if (it.checkboxElement.get("checked")) {
                    checkedItems.push( it )
                }
            }
        }.bind(this));
        return checkedItems;
    },

    createViewNode: function(data){
        this.viewAreaNode.empty();

        var viewStyles = this.viewJson.viewStyles;

        this.contentAreaNode = new Element("div", {"styles":
                (viewStyles && viewStyles["container"]) ? viewStyles["container"] : this.css.contentAreaNode
        }).inject(this.viewAreaNode);

        this.viewTable = new Element("table.viewTable", {
            "styles": this.css.viewTitleTableNode,
            "border": "0px",
            "cellPadding": "0",
            "cellSpacing": "0"
        }).inject(this.contentAreaNode);
        if( viewStyles ){
            if( viewStyles["tableProperties"] )this.viewTable.set(viewStyles["tableProperties"]);
            if( viewStyles["table"] )this.viewTable.setStyles(viewStyles["table"]);
        }

        this.createLoadding();

        var viewTitleCellNode = (viewStyles && viewStyles["titleTd"]) ? viewStyles["titleTd"] : this.css.viewTitleCellNode;

        if (this.json.isTitle!=="no"){
            this.viewTitleLine = new Element("tr", {
                "styles": (viewStyles && viewStyles["titleTr"]) ? viewStyles["titleTr"] : this.css.viewTitleLineNode
            }).inject(this.viewTable);

            if( this.selectEnable ){
                this.createSelectTh();
            }

            //if (this.json.select==="single" || this.json.select==="multi") {
            this.selectTitleCell = new Element("td", {
                "styles": viewTitleCellNode
            }).inject(this.viewTitleLine);
            this.selectTitleCell.setStyle("width", "10px");
            // if (this.json.titleStyles) this.selectTitleCell.setStyles(this.json.titleStyles);
            //}

            this.viewJson.firstTdHidden = true;
            if( this.json.defaultSelectedScript )this.json.defaultSelectedScript = "";
            if( this.viewJson.defaultSelectedScript )this.viewJson.defaultSelectedScript = "";

            if( this.json.selectedAbleScript )this.json.selectedAbleScript = "";
            if( this.viewJson.selectedAbleScript )this.viewJson.selectedAbleScript = "";

            if( this.isSelectTdHidden() ){
                this.selectTitleCell.hide();
            }


            //序号
            if (this.viewJson.isSequence==="yes"){
                this.sequenceTitleCell = new Element("td", {
                    "styles": viewTitleCellNode
                }).inject(this.viewTitleLine);
                this.sequenceTitleCell.setStyle("width", "10px");
                // if (this.json.titleStyles) this.sequenceTitleCell.setStyles(this.json.titleStyles);
            }


            this.entries = {};
            this.viewJson.selectList.each(function(column){
                this.entries[column.column] = column;

                if (!column.hideColumn){
                    var viewCell = new Element("td", {
                        "styles": viewTitleCellNode,
                        "text": column.displayName
                    }).inject(this.viewTitleLine);
                    var size = MWF.getTextSize(column.displayName, viewTitleCellNode);
                    viewCell.setStyle("min-width", ""+size.x+"px");
                    // if (this.json.titleStyles) viewCell.setStyles(this.json.titleStyles);
                }else{
                    this.hideColumns.push(column.column);
                }
                if (column.allowOpen) this.openColumns.push(column.column);
            }.bind(this));

            if( this.options.hasAction ){
                var viewCell = new Element("td", {
                    "styles": viewTitleCellNode,
                    "text": this.lp.action
                }).inject(this.viewTitleLine);
                viewCell.setStyle("width","40px");
                if (this.json.titleStyles) viewCell.setStyles(this.json.titleStyles);
            }

            this.lookup(data);
        }else{
            this.entries = {};
            this.viewJson.selectList.each(function(column){
                this.entries[column.column] = column;
                if (column.hideColumn) this.hideColumns.push(column.column);
                if (!column.allowOpen) this.openColumns.push(column.column);
            }.bind(this));
            this.lookup(data);
        }
    },
    loadLayout: function(){
        this.node = new Element("div", {"styles": this.css.node}).inject(this.container);
        this.actionbarAreaNode =  new Element("div.actionbarAreaNode", {"styles": this.css.actionbarAreaNode}).inject(this.node);
        // if (this.options.export) this.exportAreaNode = new Element("div", {"styles": this.css.exportAreaNode}).inject(this.node);
        this.searchAreaNode = new Element("div", {"styles": this.css.searchAreaNode}).inject(this.searchContainer || this.node );
        this.viewAreaNode = new Element("div.viewAreaNode", {"styles": this.css.viewAreaNode}).inject(this.node);
        // this.viewPageNode = new Element("div", {"styles": this.css.viewPageNode}).inject(this.node);
        this.viewPageAreaNode = new Element("div", {"styles": this.css.viewPageAreaNode}).inject(this.node);
    },
    //loadData: function(){
    //    if (this.gridJson.length){
    //        this.gridJson.each(function(line, i){
    //            this.items.push(new MWF.xApplication.cms.Module.QueryViewer.Item(this, line, null, i));
    //        }.bind(this));
    //    }
    //},
    loadData: function(){
        if( this.checkboxElement ){
            this.checkboxElement.set("checked", false )
        }
        if (this.gridJson.length){
            if( !this.options.paging ){
                this.gridJson.each(function(line, i){
                    this.items.push(new MWF.xApplication.cms.Module.QueryViewer.Item(this, line, null, i));
                }.bind(this));
            }else{
                this.loadPaging();
            }
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
                this.items.push(new MWF.xApplication.cms.Module.QueryViewer.Item(this, this.gridJson[i], null, i));
            }
            this.isItemsLoading = false;
            this.pageNumber ++;
            if( to == this.gridJson.length )this.isItemsLoaded = true;
        }
    },
    loadGroupData: function(){
        if( this.checkboxElement ){
            this.checkboxElement.set("checked", false )
        }
        if (this.selectTitleCell && !this.selectTitleCell.retrieve("expandLoaded") ){
            if( this.viewJson.viewStyles && this.viewJson.viewStyles["groupCollapseNode"] ){
                this.expandAllNode = new Element("span", {
                    styles : this.viewJson.viewStyles["groupCollapseNode"]
                }).inject( this.selectTitleCell );
                this.selectTitleCell.setStyle("cursor", "pointer");
            }else{
                this.selectTitleCell.set("html", "<span style='font-family: Webdings'>"+"<img src='../x_component_query_Query/$Viewer/"+this.options.style+"/icon/expand.png'/>"+"</span>");
            }
            this.selectTitleCell.setStyle("cursor", "pointer");
            this.selectTitleCell.addEvent("click", this.expandOrCollapseAll.bind(this));
            this.selectTitleCell.store("expandLoaded", true);
        }
        // this.expandAll = false;
        if (this.gridJson.length){
            var i = 0;
            this.gridJson.each(function(data){
                this.items.push(new MWF.xApplication.cms.Module.QueryViewer.ItemCategory(this, data, i));
                i += data.list.length;
            }.bind(this));

            if (this.getExpandFlag()=="yes")this.expandOrCollapseAll();
        }else{
            if (this.viewPageAreaNode) this.viewPageAreaNode.empty();
        }
    },
    createSearchNode: function(){
        if (this.viewJson.customFilterList && this.viewJson.customFilterList.length){
            this.searchStatus = "default";
            this.loadFilterSearch();

            this.originalSearchContainerWidth = this.searchContainer.getSize().x;

            this.viewSearchCustomActionNode.addEvents({
                "click": function(){
                    var parent = this.searchContainer.getParent();
                    if( parent ){
                        var x = parent.getParent().getSize().x;
                        this.searchContainer.setStyle("width", Math.min( 800, x ) + "px" );
                    }
                }.bind(this)
            });

        }else{
            this.searchStatus = "simple";
            this.loadSimpleSearch();
        }
    },
    loadSimpleSearch: function(){
        return false;
        this.searchSimpleNode = new Element("div", {"styles": this.css.searchSimpleNode}).inject(this.searchAreaNode);
        this.searchSimpleButtonNode = new Element("div", {"styles": this.css.searchSimpleButtonNode}).inject(this.searchSimpleNode);
        this.searchSimpleWarpNode = new Element("div", {"style": "margin-right: 40px; margin-left: 5px; height: 24px; overflow: hidden;"}).inject(this.searchSimpleNode);
        this.searchSimpleInputNode = new Element("input", {"type":"text", "styles": this.css.searchSimpleInputNode, "value": this.lp.searchKeywork}).inject(this.searchSimpleWarpNode);
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
    setContentHeight: function(){
        if( this.viewSearchCustomCloseActionNode && !this.setCustomSearchCloseEvent ){
            this.viewSearchCustomCloseActionNode.addEvent("click", function(){
                this.searchContainer.setStyle("width", this.originalSearchContainerWidth + "px" );
            }.bind(this));
            this.setCustomSearchCloseEvent = true;
        }
        if(this.viewSearchInputAreaNode)this.viewSearchInputAreaNode.setStyle("width","auto");
        debugger;
        if( this.node && this.searchContainer && this.viewAreaNode ){
            var size = this.node.getSize();
            var searchSize;
            var parent = this.searchContainer.getParent();
            if( parent ){
                searchSize = parent.getParent().getSize();
            }else{
                searchSize = this.searchContainer.getSize();
            }
            var h = size.y-searchSize.y; // - 80; //80是视图翻页条的高度
            if( this.actionbarAreaNode ){
                var exportSize = this.actionbarAreaNode.getComputedSize();
                h = h-exportSize.totalHeight;
            }
            var pageSize = this.viewPageAreaNode.getComputedSize();
            h = h-pageSize.totalHeight;
            this.viewAreaNode.setStyle("height", ""+h+"px");
        }
    }
});

MWF.xApplication.cms.Module.QueryViewer.Item = new Class({
    Extends : MWF.xApplication.query.Query.Viewer.Item,
    load: function(){
        this.view.fireEvent("queryLoadItemRow", [null, this]);
        var _self = this;

        var viewStyles = this.view.viewJson.viewStyles;
        var viewContentTdNode = ( viewStyles && viewStyles["contentTd"] ) ? viewStyles["contentTd"] : this.css.viewContentTdNode;

        this.node = new Element("tr", {
            "styles": ( viewStyles && viewStyles["contentTr"] ) ? viewStyles["contentTr"] : this.css.viewContentTrNode
        });
        if (this.prev){
            this.node.inject(this.prev.node, "after");
        }else{
            this.node.inject(this.view.viewTable);
        }
        this.node.addEvents({
            mouseover : function(){ this.setStyles(_self.css.viewContentTrNode_over) },
            mouseout : function(){ this.setStyles(_self.css.viewContentTrNode) }
        });

        if( this.view.selectEnable ){
            this.createSelectTd();
        }

        //if (this.view.json.select==="single" || this.view.json.select==="multi"){
        this.selectTd = new Element("td", { "styles": viewContentTdNode }).inject(this.node);
        this.selectTd.setStyles({"cursor": "pointer"});
        if (this.view.json.itemStyles) this.selectTd.setStyles(this.view.json.itemStyles);
        //}

        if( this.view.isSelectTdHidden() ){
            this.selectTd.hide();
        }

        //Object.each(this.data.data, function(cell, k){
        //    if (this.view.hideColumns.indexOf(k)===-1){
        //        var td = new Element("td", {"styles": this.css.viewContentTdNode}).inject(this.node);
        //        if (k!== this.view.viewJson.group.column){
        //            var v = (this.view.entries[k].code) ? MWF.Macro.exec(this.view.entries[k].code, {"value": cell, "gridData": this.view.gridJson, "data": this.view.viewData, "entry": this.data}) : cell;
        //            td.set("text", v);
        //        }
        //        if (this.view.openColumns.indexOf(k)!==-1){
        //            this.setOpenWork(td)
        //        }
        //        if (this.view.json.itemStyles) td.setStyles(this.view.json.itemStyles);
        //    }
        //}.bind(this));


        this.view.viewJson.selectList.each(function(column){
            var k = column.column;
            var cell = this.data.data[column.column];

            if (this.view.hideColumns.indexOf(k)===-1){
                var td = new Element("td", {"styles": viewContentTdNode}).inject(this.node);
                if (k!== this.view.viewJson.group.column){
                    //var v = (this.view.entries[k].code) ? MWF.Macro.exec(this.view.entries[k].code, {"value": cell, "gridData": this.view.gridJson, "data": this.view.viewData, "entry": this.data}) : cell;
                    //td.set("text", cell);
                    var v = cell;
                    if (column.isHtml){
                        td.set("html", v);
                    }else{
                        td.set("text", v);
                    }
                }
                if (this.view.openColumns.indexOf(k)!==-1){
                    this.setOpenWork(td, column)
                }
                if (this.view.json.itemStyles) td.setStyles(this.view.json.itemStyles);
            }

        }.bind(this));

        if( this.view.options.hasAction ){
            var td = new Element("td", {"styles": this.css.viewContentTdNode}).inject(this.node);
            this.loadActions( td );
            if (this.view.json.itemStyles) td.setStyles(this.view.json.itemStyles);
        }

        this.setEvent();
        this.view.fireEvent("postLoadItemRow", [null, this]);
    },
    destroySelectTd : function(){
        if( this.checboxTd ){
            this.checboxTd.destroy();
            this.checboxTd = null;
        }
    },
    createSelectTd : function(){
        if( this.checboxTd )return;

        var viewStyles = this.view.viewJson.viewStyles;
        var viewContentTdNode = ( viewStyles && viewStyles["contentTd"] ) ? viewStyles["contentTd"] : this.css.viewContentTdNode;

        this.checboxTd = new Element("td", {styles:viewContentTdNode}).inject(this.node, "top");
        this.checkboxElement = new Element("input", {
            "type": "checkbox",
            "events" : { click : function(ev){ ev.stopPropagation(); } }
        }).inject(this.checboxTd);
        this.checboxTd.addEvent("click", function(ev){
            this.checkboxElement.set("checked", !this.checkboxElement.get("checked") );
            ev.stopPropagation();
        }.bind(this));
    },
    loadActions : function( container ){
        this.deleteNode = new Element("div", {"styles": this.css.actionDeleteNode, "title": this.view.lp.delete }).inject(container);
        this.deleteNode.addEvents({
            "mouseover": function(){this.deleteNode.setStyles(this.css.actionDeleteNode_over);}.bind(this),
            "mouseout": function(){this.deleteNode.setStyles(this.css.actionDeleteNode);}.bind(this),
            "mousedown": function(){this.deleteNode.setStyles(this.css.actionDeleteNode_down);}.bind(this),
            "mouseup": function(){this.deleteNode.setStyles(this.css.actionDeleteNode_over);}.bind(this),
            "click": function(e){
                this.remove(e);
                e.stopPropagation();
            }.bind(this)
        });

        this.editNode = new Element("div", {"styles": this.css.actionEditNode, "title": this.view.lp.edit }).inject(container);
        this.editNode.addEvents({
            "mouseover": function(){this.editNode.setStyles(this.css.actionEditNode_over);}.bind(this),
            "mouseout": function(){this.editNode.setStyles(this.css.actionEditNode);}.bind(this),
            "mousedown": function(){this.editNode.setStyles(this.css.actionEditNode_down);}.bind(this),
            "mouseup": function(){this.editNode.setStyles(this.css.actionEditNode_over);}.bind(this),
            "click": function(e){
                this.editCMSDocument();
                e.stopPropagation();
            }.bind(this)
        });
    },
    setOpenWork: function(td, column){
        td.setStyle("cursor", "pointer");
        if( column.clickCode ) {
            // if( !this.view.Macro ){
            //     MWF.require("MWF.xScript.Macro", function () {
            //         this.view.businessData = {};
            //         this.view.Macro = new MWF.Macro.PageContext(this.view);
            //     }.bind(this), false);
            // }
            td.addEvent("click", function( ev ){
                var result = this.view.Macro.fire(column.clickCode, this, ev);
                ev.stopPropagation();
                return result;
            }.bind(this));
        }else{
            if (this.view.json.type==="cms"){
                td.addEvent("click", function(ev){
                    this.openCMSDocument(false)
                    ev.stopPropagation();
                }.bind(this));
            }else{
                td.addEvent("click", function(ev){
                    this.openWorkAndCompleted(ev)
                    ev.stopPropagation();
                }.bind(this));
            }
        }
    },
    openCMSDocument : function( isEdited ){
        var appId = "cms.Document"+this.data.bundle;
        if (layout.desktop.apps[appId]){
            if (!layout.desktop.apps[appId].window){
                layout.desktop.apps[appId] = null;
                layout.openApplication(null, layout.desktop.apps[appId].options.name, layout.desktop.apps[appId].options, layout.desktop.apps[appId].options.app, false, this, false);
            }else{
                layout.desktop.apps[appId].setCurrent();
            }
                //layout.desktop.apps[appId].setCurrent();
        }else {
            var options = {
                "documentId": this.data.bundle,
                "readonly" : !isEdited
            };
            layout.desktop.openApplication(null, "cms.Document", options);
        }
    },
    editCMSDocument : function(){
        this.openCMSDocument( true );
    },
    remove: function(e){
        var text = this.view.lp.deleteConfirmContent;
        var _self = this;
        this.node.setStyles(this.css.viewContentTrNode_delete);
        this.readyRemove = true;
        this.view.app.confirm("warn", e, this.view.lp.deleteConfirmTitle, text, 350, 120, function(){

            _self.removeCMSDocument(_self, false);

            this.close();

        }, function(){
            _self.node.setStyles(_self.css.viewContentTrNode );
            _self.readyRemove = false;
            this.close();
        });
    },
    removeCMSDocument: function(){
        var id = this.data.bundle;
        //this.view.lookupAction.invoke({"name": "deleteDocument","async": true, "parameter": {"id": id },"success": function(json){
        //    this.readyRemove = false;
        //    this.node.destroy();
        //    this.view.app.notice("删除成功", "success");
        //    MWF.release(this);
        //}.bind(this)});
        MWF.Actions.get("x_cms_assemble_control").removeDocument(id, function(json){
            this.readyRemove = false;
            this.node.destroy();
            this.view.app.notice( this.view.lp.deleteSuccessNotice, "success");
            MWF.release(this);
        }.bind(this));
    }

});

MWF.xApplication.cms.Module.QueryViewer.ItemCategory = new Class({
    Extends : MWF.xApplication.query.Query.Viewer.ItemCategory,
    load: function(){
        this.view.fireEvent("queryLoadCategoryRow", [null, this]);

        var viewStyles = this.view.viewJson.viewStyles;

        var viewContentCategoryTdNode = ( viewStyles && viewStyles["contentGroupTd"] ) ? viewStyles["contentGroupTd"] : this.css.viewContentCategoryTdNode;

        this.node = new Element("tr", {
            "styles": (viewStyles && viewStyles["contentTr"]) ? viewStyles["contentTr"] : this.css.viewContentTrNode
        }).inject(this.view.viewTable);
        //if (this.view.json.select==="single" || this.view.json.select==="multi"){
        this.selectTd = new Element("td", {"styles": viewContentCategoryTdNode}).inject(this.node);
        if (this.view.json.itemStyles) this.selectTd.setStyles(this.view.json.itemStyles);
        //}

        var colsapn = this.view.viewJson.selectList.length+1;
        if( this.view.options.hasAction ){
            colsapn ++
        }

        this.categoryTd = new Element("td", {
            "styles": viewContentCategoryTdNode,
            "colspan": colsapn
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

        if( viewStyles && viewStyles["groupCollapseNode"] ){
            this.expandNode = new Element("span", {
                styles : viewStyles["groupCollapseNode"]
            }).inject( this.categoryTd );
            new Element("span", { text : text }).inject( this.categoryTd );
            // this.categoryTd.set("text", text );
        }else{
            this.categoryTd.set("html", "<span style='font-family: Webdings'><img src='../x_component_query_Query/$Viewer/"+this.view.options.style+"/icon/expand.png'/></span> "+text);
        }
        this.expanded = false;
        if (this.view.json.itemStyles) this.categoryTd.setStyles(this.view.json.itemStyles);

        this.setEvent();

        this.view.fireEvent("postLoadCategoryRow", [null, this]);
    },
    expand: function( from ){
        this.items.each(function(item){
            item.node.setStyle("display", "table-row");
        }.bind(this));
               if( this.expandNode ){
            this.expandNode.setStyles( this.view.viewJson.viewStyles["groupExpandNode"] )
        }else{
            this.node.getElement("span").set("html", "<img src='../x_component_query_Query/$Viewer/"+this.view.options.style+"/icon/down.png'/>");
        }
        this.expanded = true;
        if (!this.loadChild){
            //window.setTimeout(function(){
            this.data.list.each(function(line, i){
                var s = this.idx+i;
                this.lastItem = new MWF.xApplication.cms.Module.QueryViewer.Item(this.view, line, (this.lastItem || this), s);
                this.items.push(this.lastItem);
                //this.items.push(new MWF.xApplication.cms.Module.QueryViewer.Item(this.view, line, this));
            }.bind(this));
            this.loadChild = true;
            //}.bind(this), 10);
        }
        if( from !== "view" ){
            this.view.checkExpandAllStatus();
        }
    }
});

//MWF.xApplication.cms.Module.Viewer = new Class({
//    Implements: [Options, Events],
//    Extends: MWF.xApplication.process.Application.Viewer,
//    options: {
//        "style": "default",
//        "hasAction" : false, //cxy add
//        "resizeNode": true,
//        "actions": {
//            //"lookup": {"uri": "/jaxrs/queryview/flag/{view}/application/flag/{application}/execute", "method":"PUT"},
//            //"getView": {"uri": "/jaxrs/queryview/flag/{view}/application/flag/{application}"},
//            "lookup": {"uri": "/jaxrs/view/{view}/execute", "method":"PUT"},
//            "getView": {"uri": "/jaxrs/view/{view}"},
//            "listWorkByJob": {"uri": "/jaxrs/job/{job}/find/work/workcompleted"},
//            "listTaskByWork": {"uri": "/jaxrs/work/{id}/assignment/manage"}
//
//        },
//        "actionRoot": "x_processplatform_assemble_surface"
//    },
//    initialize: function(container, json, options, app, searchContainer){
//        this.setOptions(options);
//        this.app = app;
//        this.searchContainer = searchContainer;
//
//        this.path = "../x_component_cms_Module/$ViewExplorer/";
//        this.cssPath = "../x_component_cms_Module/$ViewExplorer/"+this.options.style+"/viewer.wcss";
//        this._loadCss();
//        this.lp = MWF.xApplication.process.Application.LP;
//
//        this.container = $(container);
//        this.json = json;
//
//        this.viewJson = null;
//        this.filterItems = [];
//        this.searchStatus = "none"; //none, custom, default
//
//
//        this.items = [];
//        this.selectedItems = [];
//        this.hideColumns = [];
//        this.openColumns = [];
//
//        this.gridJson = null;
//
//        this.init(function(){
//            this.load();
//        }.bind(this));
//    },
//    selectMode : function(){
//        this.selectEnable = true;
//        this.createSelectTh();
//        this.items.each( function (it) {
//            it.createSelectTd();
//        }.bind(this));
//    },
//    disableSelectMode : function(){
//        this.selectEnable = false;
//        this.destroySelectTh();
//        this.items.each( function (it) {
//            it.destroySelectTd();
//        }.bind(this));
//    },
//    destroySelectTh : function(){
//        if( this.selectTh ){
//            this.selectTh.destroy();
//            this.selectTh = null;
//        }
//    },
//    createSelectTh : function(){
//        this.selectTh = new Element("th").inject(this.viewTitleLine, "top");
//        this.checkboxElement = new Element("input", {
//            "type": "checkbox"
//        }).inject(this.selectTh);
//        this.checkboxElement.addEvent("click", function () {
//            this.selectAll()
//        }.bind(this));
//    },
//    selectAll : function () {
//        var flag = this.checkboxElement.get("checked");
//        this.items.each(function (it) {
//            if (it.checkboxElement)it.checkboxElement.set("checked", flag)
//        }.bind(this))
//    },
//    getSelectedIds : function(){
//        var checkedItems = [];
//        this.items.each(function (it) {
//            if (it.checkboxElement.get("checked")) {
//                checkedItems.push( it.data.bundle )
//            }
//        }.bind(this));
//        return checkedItems;
//    },
//    getSelectedItems : function(){
//        var checkedItems = [];
//        this.items.each(function (it) {
//            if (it.checkboxElement.get("checked")) {
//                checkedItems.push( it )
//            }
//        }.bind(this));
//        return checkedItems;
//    },
//    createViewNode: function(data){
//        this.viewAreaNode.empty();
//        this.contentAreaNode = new Element("div", {"styles": this.css.contentAreaNode}).inject(this.viewAreaNode);
//
//        this.viewTable = new Element("table", {
//            "styles": this.css.viewTitleTableNode,
//            "border": "0px",
//            "cellPadding": "0",
//            "cellSpacing": "0"
//        }).inject(this.contentAreaNode);
//        this.createLoadding();
//
//        if (this.json.isTitle!=="no"){
//            this.viewTitleLine = new Element("tr", {"styles": this.css.viewTitleLineNode}).inject(this.viewTable);
//
//            //if (this.json.select==="single" || this.json.select==="multi") {
//            this.selectTitleCell = new Element("td", {
//                "styles": this.css.viewTitleCellNode
//            }).inject(this.viewTitleLine);
//            this.selectTitleCell.setStyle("width", "10px");
//            if (this.json.titleStyles) this.selectTitleCell.setStyles(this.json.titleStyles);
//            //}
//            this.entries = {};
//            this.viewJson.selectList.each(function(column){
//                this.entries[column.column] = column;
//
//                if (!column.hideColumn){
//                    var viewCell = new Element("td", {
//                        "styles": this.css.viewTitleCellNode,
//                        "text": column.displayName
//                    }).inject(this.viewTitleLine);
//                    if (this.json.titleStyles) viewCell.setStyles(this.json.titleStyles);
//                }else{
//                    this.hideColumns.push(column.column);
//                }
//                if (column.allowOpen) this.openColumns.push(column.column);
//            }.bind(this));
//
//            if( this.options.hasAction ){
//                var viewCell = new Element("td", {
//                    "styles": this.css.viewTitleCellNode,
//                    "text": "操作"
//                }).inject(this.viewTitleLine);
//                viewCell.setStyle("width","40px");
//                if (this.json.titleStyles) viewCell.setStyles(this.json.titleStyles);
//            }
//
//            this.lookup(data);
//        }else{
//            this.viewJson.selectEntryList.each(function(column){
//                if (column.hideColumn) this.hideColumns.push(column.column);
//                if (!column.allowOpen) this.openColumns.push(column.column);
//            }.bind(this));
//            this.lookup(data);
//        }
//    },
//    lookup: function(data){
//        this.getLookupAction(function(){
//            if (this.json.application){
//                this.lookupAction.invoke({"name": "lookup","async": true, "data": (data || null), "parameter": {"view": this.json.name, "application": this.json.application},"success": function(json){
//                    this.viewData = json.data;
//                    if (this.viewJson.group.column){
//                        this.gridJson = json.data.groupGrid;
//                        this.loadGroupData();
//                    }else{
//                        this.gridJson = json.data.grid;
//                        this.loadData();
//                    }
//                    if (this.loadingAreaNode){
//                        this.loadingAreaNode.destroy();
//                        this.loadingAreaNode = null;
//                    }
//                }.bind(this)});
//            }
//        }.bind(this));
//    },
//    loadLayout: function(){
//        this.node = new Element("div", {"styles": this.css.node}).inject(this.container);
//        this.searchAreaNode = new Element("div", {"styles": this.css.searchAreaNode}).inject(this.searchContainer || this.node );
//        this.viewAreaNode = new Element("div", {"styles": this.css.viewAreaNode}).inject(this.node);
//    },
//    loadData: function(){
//        if (this.gridJson.length){
//            this.gridJson.each(function(line, i){
//                this.items.push(new MWF.xApplication.cms.Module.Viewer.Item(this, line, null, i));
//            }.bind(this));
//        }
//    },
//    loadGroupData: function(){
//        if (this.selectTitleCell){
//            this.selectTitleCell.set("html", "<span style='font-family: Webdings'>"+"<img src='../x_component_process_Application/$Viewer/"+this.options.style+"/icon/expand.png'/>"+"</span>");
//            this.selectTitleCell.setStyle("cursor", "pointer");
//            this.selectTitleCell.addEvent("click", this.expandOrCollapseAll.bind(this));
//        }
//
//        if (this.gridJson.length){
//            this.gridJson.each(function(data){
//                this.items.push(new MWF.xApplication.cms.Module.Viewer.ItemCategory(this, data));
//            }.bind(this));
//
//            if (this.json.isExpand=="yes")this.expandOrCollapseAll();
//        }
//    },
//    setContentHeight: function(){
//        if( this.node && this.searchAreaNode && this.viewAreaNode ){
//            var size = this.node.getSize();
//            var searchSize = this.searchAreaNode.getSize();
//            var h = size.y-searchSize.y;
//            this.viewAreaNode.setStyle("height", ""+h+"px");
//        }
//    }
//});
//
//MWF.xApplication.cms.Module.Viewer.Item = new Class({
//    Extends : MWF.xApplication.process.Application.Viewer.Item,
//    load: function(){
//        var _self = this;
//        this.node = new Element("tr", {"styles": this.css.viewContentTrNode});
//        if (this.prev){
//            this.node.inject(this.prev.node, "after");
//        }else{
//            this.node.inject(this.view.viewTable);
//        }
//        this.node.addEvents({
//            mouseover : function(){ this.setStyles(_self.css.viewContentTrNode_over) },
//            mouseout : function(){ this.setStyles(_self.css.viewContentTrNode) }
//        });
//
//        if( this.view.selectEnable ){
//            this.createSelectTd();
//        }
//
//        //if (this.view.json.select==="single" || this.view.json.select==="multi"){
//        this.selectTd = new Element("td", {"styles": this.css.viewContentTdNode}).inject(this.node);
//        this.selectTd.setStyles({"cursor": "pointer"});
//        if (this.view.json.itemStyles) this.selectTd.setStyles(this.view.json.itemStyles);
//        //}
//
//        debugger;
//        this.view.viewJson.selectList.each(function(column){
//            var k = column.column;
//            var cell = this.data.data[column.column];
//
//            if (this.view.hideColumns.indexOf(k)===-1){
//                var td = new Element("td", {"styles": this.css.viewContentTdNode}).inject(this.node);
//                if (k!== this.view.viewJson.group.column){
//                    var v = (this.view.entries[k].code) ? MWF.Macro.exec(this.view.entries[k].code, {"value": cell, "gridData": this.view.gridJson, "data": this.view.viewData, "entry": this.data}) : cell;
//                    td.set("text", v);
//                }
//                if (this.view.openColumns.indexOf(k)!==-1){
//                    this.setOpenWork(td)
//                }
//                if (this.view.json.itemStyles) td.setStyles(this.view.json.itemStyles);
//            }
//
//        }.bind(this));
//
//        //Object.each(this.data.data, function(cell, k){
//        //    if (this.view.hideColumns.indexOf(k)===-1){
//        //        var td = new Element("td", {"styles": this.css.viewContentTdNode}).inject(this.node);
//        //        if (k!== this.view.viewJson.group.column){
//        //            var v = (this.view.entries[k].code) ? MWF.Macro.exec(this.view.entries[k].code, {"value": cell, "gridData": this.view.gridJson, "data": this.view.viewData, "entry": this.data}) : cell;
//        //            td.set("text", v);
//        //        }
//        //        if (this.view.openColumns.indexOf(k)!==-1){
//        //            this.setOpenWork(td)
//        //        }
//        //        if (this.view.json.itemStyles) td.setStyles(this.view.json.itemStyles);
//        //    }
//        //}.bind(this));
//
//        if( this.view.options.hasAction ){
//            var td = new Element("td", {"styles": this.css.viewContentTdNode}).inject(this.node);
//            this.loadActions( td );
//            if (this.view.json.itemStyles) td.setStyles(this.view.json.itemStyles);
//        }
//
//        this.setEvent();
//    },
//
//    loadActions : function( container ){
//        this.deleteNode = new Element("div", {"styles": this.css.actionDeleteNode, "title": "删除"}).inject(container);
//        this.deleteNode.addEvents({
//            "mouseover": function(){this.deleteNode.setStyles(this.css.actionDeleteNode_over);}.bind(this),
//            "mouseout": function(){this.deleteNode.setStyles(this.css.actionDeleteNode);}.bind(this),
//            "mousedown": function(){this.deleteNode.setStyles(this.css.actionDeleteNode_down);}.bind(this),
//            "mouseup": function(){this.deleteNode.setStyles(this.css.actionDeleteNode_over);}.bind(this),
//            "click": function(e){
//                this.remove(e);
//                e.stopPropagation();
//            }.bind(this)
//        });
//
//        this.editNode = new Element("div", {"styles": this.css.actionEditNode, "title": "编辑"}).inject(container);
//        this.editNode.addEvents({
//            "mouseover": function(){this.editNode.setStyles(this.css.actionEditNode_over);}.bind(this),
//            "mouseout": function(){this.editNode.setStyles(this.css.actionEditNode);}.bind(this),
//            "mousedown": function(){this.editNode.setStyles(this.css.actionEditNode_down);}.bind(this),
//            "mouseup": function(){this.editNode.setStyles(this.css.actionEditNode_over);}.bind(this),
//            "click": function(e){
//                this.editCMSDocument();
//                e.stopPropagation();
//            }.bind(this)
//        });
//    },
//    destroySelectTd : function(){
//        if( this.checboxTd ){
//            this.checboxTd.destroy();
//            this.checboxTd = null;
//        }
//    },
//    createSelectTd : function(){
//        if( this.checboxTd )return;
//        this.checboxTd = new Element("td").inject(this.node, "top");
//        this.checboxTd.addEvent("click", function(ev){
//            ev.stopPropagation();
//        });
//        this.checkboxElement = new Element("input", {
//            "type": "checkbox"
//        }).inject(this.checboxTd);
//    },
//    setOpenWork: function(td){
//        td.setStyle("cursor", "pointer");
//        td.addEvent("click", function(){
//            this.openCMSDocument()
//        }.bind(this));
//    },
//    openCMSDocument : function( isEdited ){
//        var appId = "cms.Document"+this.data.bundle;
//        if (layout.desktop.apps[appId]){
//            layout.desktop.apps[appId].setCurrent();
//        }else {
//            var options = {
//                "documentId": this.data.bundle,
//                "readonly" : !isEdited
//            };
//            layout.desktop.openApplication(null, "cms.Document", options);
//        }
//    },
//    editCMSDocument : function(){
//        this.openCMSDocument( true );
//    },
//    remove: function(e){
//        var text = "删除后不能恢复，你确定要删除该文档？";
//        var _self = this;
//        this.node.setStyles(this.css.viewContentTrNode_delete);
//        this.readyRemove = true;
//        this.view.app.confirm("warn", e, "删除确认", text, 350, 120, function(){
//
//            _self.removeCMSDocument(_self, false);
//
//            this.close();
//
//        }, function(){
//            _self.node.setStyles(_self.css.viewContentTrNode );
//            _self.readyRemove = false;
//            this.close();
//        });
//    },
//    removeCMSDocument: function(){
//        var id = this.data.bundle;
//        MWF.Actions.get("x_cms_assemble_control").removeDocument(id, function(json){
//            //this.viewJson = JSON.decode(json.data.data);
//            //this.json = Object.merge(this.json, json.data);
//            this.readyRemove = false;
//            this.node.destroy();
//            this.view.app.notice("删除成功", "success");
//            MWF.release(this);
//        }.bind(this));
//    }
//
//});
//
//MWF.xApplication.cms.Module.Viewer.ItemCategory = new Class({
//    Extends : MWF.xApplication.process.Application.Viewer.ItemCategory,
//    load: function(){
//        this.node = new Element("tr", {"styles": this.css.viewContentTrNode}).inject(this.view.viewTable);
//        //if (this.view.json.select==="single" || this.view.json.select==="multi"){
//        this.selectTd = new Element("td", {"styles": this.css.viewContentCategoryTdNode}).inject(this.node);
//        if (this.view.json.itemStyles) this.selectTd.setStyles(this.view.json.itemStyles);
//        //}
//
//        var colsapn = this.view.viewJson.selectList.length;
//        if( this.view.options.hasAction ){
//            colsapn ++
//        }
//
//        this.categoryTd = new Element("td", {
//            "styles": this.css.viewContentCategoryTdNode,
//            "colspan": colsapn
//        }).inject(this.node);
//
//        this.groupColumn = null;
//        for (var c = 0; c<this.view.viewJson.selectList.length; c++){
//            if (this.view.viewJson.selectList[c].column === this.view.viewJson.group.column){
//                this.groupColumn = this.view.viewJson.selectList[c];
//                break;
//            }
//        }
//        if (this.groupColumn){
//            var text = (this.groupColumn.code) ? MWF.Macro.exec(this.groupColumn.code, {"value": this.data.group, "gridData": this.view.gridJson, "data": this.view.viewData, "entry": this.data}) : this.data.group;
//        }else{
//            var text = this.data.group;
//        }
//
//        this.categoryTd.set("html", "<span style='font-family: Webdings'><img src='../x_component_process_Application/$Viewer/"+this.view.options.style+"/icon/expand.png'/></span> "+text);
//        if (this.view.json.itemStyles) this.categoryTd.setStyles(this.view.json.itemStyles);
//
//        this.setEvent();
//    },
//    expand: function(){
//        this.items.each(function(item){
//            item.node.setStyle("display", "table-row");
//        }.bind(this));
//        this.node.getElement("span").set("html", "<img src='../x_component_process_Application/$Viewer/"+this.view.options.style+"/icon/down.png'/>");
//        if (!this.loadChild){
//            //window.setTimeout(function(){
//            this.data.list.each(function(line){
//                this.items.push(new MWF.xApplication.cms.Module.Viewer.Item(this.view, line, this));
//            }.bind(this));
//            this.loadChild = true;
//            //}.bind(this), 10);
//        }
//    }
//});
