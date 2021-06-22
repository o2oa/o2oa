MWF.xApplication.query = MWF.xApplication.query || {};
MWF.xApplication.query.Query = MWF.xApplication.query.Query || {};
MWF.require("MWF.widget.Common", null, false);
MWF.require("o2.widget.Paging", null, false);
MWF.require("MWF.xScript.Macro", null, false);
MWF.xDesktop.requireApp("query.Query", "lp."+o2.language, null, false);
MWF.xApplication.query.Query.Viewer = MWF.QViewer = new Class({
    Implements: [Options, Events],
    Extends: MWF.widget.Common,
    options: {
        "style": "default",
        "skin" : null,
        "resizeNode": true,
        "paging" : "scroll",
        "perPageCount" : 50,
        "isload": "true",
        "export": false,
        "moduleEvents": ["queryLoad", "postLoad", "postLoadPageData", "postLoadPage", "selectRow", "unselectRow",
            "queryLoadItemRow", "postLoadItemRow", "queryLoadCategoryRow", "postLoadCategoryRow"]

        // "actions": {
        //     "lookup": {"uri": "/jaxrs/view/flag/{view}/query/{application}/execute", "method":"PUT"},
        //     "getView": {"uri": "/jaxrs/view/flag/{view}/query/{application}"}
        //
        // },
        // "actionRoot": "x_query_assemble_surface"
    },
    initialize: function(container, json, options, app, parentMacro){
        //本类有三种事件，
        //一种是通过 options 传进来的事件，包括 loadView、openDocument、select
        //一种是用户配置的 事件， 在this.options.moduleEvents 中定义的作为类事件
        //还有一种也是用户配置的事件，不在this.options.moduleEvents 中定义的作为 this.node 的DOM事件

        this.setOptions(options);

        this.path = "../x_component_query_Query/$Viewer/";
        this.cssPath = "../x_component_query_Query/$Viewer/"+this.options.style+"/css.wcss";
        this._loadCss();
        this.lp = MWF.xApplication.query.Query.LP;

        this.app = app;

        this.container = $(container);
        this.json = json;

        this.parentMacro = parentMacro;

        this.originalJson = Object.clone(json);

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
        this.loadMacro( function () {
            this._loadModuleEvents();
            if (this.fireEvent("queryLoad")){
                this._loadUserInterface();
                //this._loadStyles();
                this._loadDomEvents();
            }
        }.bind(this))
    },
    _loadUserInterface : function(){
        this.loadLayout();
        this.createActionbarNode();
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
        this.actionbarAreaNode =  new Element("div.actionbarAreaNode", {"styles": this.css.actionbarAreaNode}).inject(this.node);
        //if (this.options.export) this.exportAreaNode = new Element("div", {"styles": this.css.exportAreaNode}).inject(this.node);
        this.searchAreaNode = new Element("div", {"styles": this.css.searchAreaNode}).inject(this.node);
        this.viewAreaNode = new Element("div", {"styles": this.css.viewAreaNode}).inject(this.node);
        // this.viewPageNode = new Element("div", {"styles": this.css.viewPageNode}).inject(this.node);
        this.viewPageAreaNode = new Element("div", {"styles": this.css.viewPageAreaNode}).inject(this.node);
    },
    loadMacro: function (callback) {
        MWF.require("MWF.xScript.Macro", function () {
            this.Macro = new MWF.Macro.ViewContext(this);
            if (callback) callback();
        }.bind(this));
    },
    createExportNode: function(){
        if (this.options.export){
            MWF.require("MWF.widget.Toolbar", function(){
                this.toolbar = new MWF.widget.Toolbar(this.actionbarAreaNode, {"style": "simple"}, this); //this.exportAreaNode
                var actionNode = new Element("div", {
                    "id": "",
                    "MWFnodetype": "MWFToolBarButton",
                    "MWFButtonImage": this.path+""+this.options.style+"/icon/export.png",
                    "title": this.lp.exportExcel,
                    "MWFButtonAction": "exportView",
                    "MWFButtonText": this.lp.exportExcel
                }).inject(this.actionbarAreaNode); //this.exportAreaNode

                this.toolbar.load();
            }.bind(this));
            //this.exportNode = new Element("button", {"text": this.lp.exportExcel}).inject(this.exportAreaNode);
        }
    },
    exportView: function(){
        var _self = this;
        var lp = this.lp.viewExport;
        var node = this.exportExcelDlgNode = new Element("div");
        var html = "<div style=\"line-height: 30px; height: 30px; color: #333333; overflow: hidden;margin-top:20px;\">" + lp.exportRange + "：" +
            "   <input class='start' value='" + ( this.exportExcelStart || 1) +  "'><span>"+ lp.to +"</span>" +
            "   <input class='end' value='"+ ( this.exportExcelEnd || Math.min( ( this.bundleItems || [] ).length, 2000 ) ) +"' ><span>"+lp.item+"</span>" +
            "</div>";
        html += "<div style=\"clear:both; max-height: 300px; margin-bottom:10px; margin-top:10px; overflow-y:auto;\">"+( lp.description.replace("{count}", ( this.bundleItems || [] ).length) )+"</div>";
        node.set("html", html);
        var check = function () {
            if(this.value.length == 1){
                this.value = this.value.replace(/[^1-9]/g,'')
            }else{
                this.value = this.value.replace(/\D/g,'')
            }
            if( this.value.toInt() > _self.bundleItems.length ){
                this.value = _self.bundleItems.length;
            }
        }
        node.getElement(".start").addEvent( "keyup", function(){ check.call(this) } );
        node.getElement(".end").addEvent( "keyup", function(){ check.call(this) } );


        var dlg = o2.DL.open({
            "title": this.lp.exportExcel,
            "style": "user",
            "isResize": false,
            "content": node,
            "width": 600,
            "height" : 260,
            "buttonList": [
                {
                    "type": "ok",
                    "text": MWF.LP.process.button.ok,
                    "action": function (d, e) {
                        var start = node.getElement(".start").get("value");
                        var end = node.getElement(".end").get("value");
                        if( !start || !end ){
                            MWF.xDesktop.notice("error", {"x": "left", "y": "top"}, lp.inputIntegerNotice, node, {"x": 0, "y": 85});
                            return false;
                        }
                        start = start.toInt();
                        end = end.toInt();
                        if( end < start ){
                            MWF.xDesktop.notice("error", {"x": "left", "y": "top"}, lp.startLargetThanEndNotice, node, {"x": 0, "y": 85});
                            return false;
                        }
                        debugger;
                        this.exportExcelStart = start;
                        this.exportExcelEnd = end;
                        var bundleList = this.bundleItems.slice(start-1, end);
                        var excelName = this.json.name + "(" + start + "-" + end + ").xlsx";
                        this._exportView(bundleList, excelName);
                        dlg.close();
                    }.bind(this)
                },
                {
                    "type": "cancel",
                    "text": MWF.LP.process.button.cancel,
                    "action": function () { dlg.close(); }
                }
            ]
        });
    },
    _exportView: function(bundleList, excelName){

        var action = MWF.Actions.get("x_query_assemble_surface");

        var filterData = this.json.filter ? this.json.filter.clone() : [];
        if (this.filterItems.length){
            this.filterItems.each(function(filter){
                filterData.push(filter.data);
            }.bind(this));
        }
        var data = {"filterList": filterData};
        if( bundleList )data.bundleList = bundleList;
        if( excelName )data.excelName = excelName;
        data.key = this.bundleKey;
        action.exportViewWithQuery(this.json.viewName, this.json.application, data, function(json){
            var uri = action.action.actions.getViewExcel.uri;
            uri = uri.replace("{flag}", json.data.id);
            uri = o2.filterUrl( action.action.address+uri );
            var a = new Element("a", {"href": uri, "target":"_blank"});
            a.click();
            a.destroy();
        }.bind(this));
    },
    setContentHeight: function(){
        var size = this.node.getSize();
        var searchSize = this.searchAreaNode.getComputedSize();
        var h = size.y-searchSize.totalHeight;
        //if (this.exportAreaNode){
        //    var exportSize = this.exportAreaNode.getComputedSize();
        //    h = h-exportSize.totalHeight;
        //}
        if( this.actionbarAreaNode ){
            var exportSize = this.actionbarAreaNode.getComputedSize();
            h = h-exportSize.totalHeight;
        }
        var pageSize = this.viewPageAreaNode.getComputedSize();
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
    createActionbarNode : function(){
        this.actionbarAreaNode.empty();
        if( typeOf(this.json.showActionbar) === "boolean" && this.json.showActionbar !== true )return;
        if( typeOf( this.viewJson.actionbarHidden ) === "boolean" ){
            if( this.viewJson.actionbarHidden === true || !this.viewJson.actionbarList || !this.viewJson.actionbarList.length )return;
            this.actionbar = new MWF.xApplication.query.Query.Viewer.Actionbar(this.actionbarAreaNode, this.viewJson.actionbarList[0], this, {});
            this.actionbar.load();
        }else{ //兼容以前的ExportNode
            this.createExportNode();
        }
    },
    createViewNode: function(data, callback){
        this.viewAreaNode.empty();

        this.selectedItems = [];

        var viewStyles = this.viewJson.viewStyles;

        this.contentAreaNode = new Element("div", {"styles":
                (viewStyles && viewStyles["container"]) ? viewStyles["container"] : this.css.contentAreaNode
        }).inject(this.viewAreaNode);

        this.viewTable = new Element("table", {
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

            //if (this.json.select==="single" || this.json.select==="multi") {
            this.selectTitleCell = new Element("td.selectTitleCell", {
                "styles": viewTitleCellNode
            }).inject(this.viewTitleLine);
            this.selectTitleCell.setStyle("width", "10px");
            if (this.json.titleStyles) this.selectTitleCell.setStyles(this.json.titleStyles);
            //}
            if( this.isSelectTdHidden() ){
                this.selectTitleCell.hide();
            }

            if( this.getSelectFlag( true ) !== "multi" || !this.viewJson.allowSelectAll ){
                this.expandTitleCell = this.selectTitleCell;
            }


            //序号
            if (this.viewJson.isSequence==="yes"){
                this.sequenceTitleCell = new Element("td", {
                    "styles": viewTitleCellNode
                }).inject(this.viewTitleLine);
                this.sequenceTitleCell.setStyle("width", "10px");
                if (this.json.titleStyles) this.sequenceTitleCell.setStyles(this.json.titleStyles);
                if( !this.expandTitleCell )this.expandTitleCell = this.sequenceTitleCell;
            }

            this.entries = {};
            debugger;
            this.viewJson.selectList.each(function(column){
                this.entries[column.column] = column;
                if (!column.hideColumn){
                    var viewCell = new Element("td", {
                        "styles": viewTitleCellNode,
                        "text": column.displayName
                    }).inject(this.viewTitleLine);
                    var size = MWF.getTextSize(column.displayName, viewTitleCellNode);
                    viewCell.setStyle("min-width", ""+size.x+"px");
                    if (this.json.titleStyles) viewCell.setStyles(this.json.titleStyles);

                    if( column.groupEntry && !this.expandTitleCell )this.expandTitleCell = viewCell;

                    if( typeOf(column.titleStyles) === "object" )viewCell.setStyles(column.titleStyles);
                    if( typeOf(column.titleProperties) === "object" )viewCell.setProperties(column.titleProperties);

                    if( column.events && column.events.loadTitle && column.events.loadTitle.code ){
                        var code = column.events.loadTitle.code;
                        this.Macro.fire( code, {"node" : viewCell, "json" : column, "data" : column.displayName, "view" : this});
                    }
                }else{
                    this.hideColumns.push(column.column);
                }

                if (column.allowOpen) this.openColumns.push(column.column);
            }.bind(this));
            this.lookup(data, callback);
        }else{
            this.entries = {};
            this.viewJson.selectList.each(function(column){
                this.entries[column.column] = column;
                if (column.hideColumn) this.hideColumns.push(column.column);
                if (!column.allowOpen) this.openColumns.push(column.column);
            }.bind(this));
            this.lookup(data, callback);
        }
    },
    getExpandFlag : function(){
        if( this.options && this.options.isExpand )return this.options.isExpand;
        if( this.json && this.json.isExpand )return this.json.isExpand;
        if( this.viewJson && this.viewJson.isExpand )return this.viewJson.isExpand;
        return "no";
    },
    getSelectFlag : function( ignoreSelectEnable ){
        if( !ignoreSelectEnable && !this.viewSelectedEnable )return "none";
        if( this.options && this.options.select )return this.options.select;
        if( this.json && this.json.select )return this.json.select;
        if( this.viewJson && this.viewJson.select )return this.viewJson.select;
        // if( this.json.select === "single" || this.json.select === "multi" )return this.json.select;
        // if( this.viewJson.select === "single" || this.viewJson.select === "multi" )return this.viewJson.select;
        // if( this.options.select === "single" || this.options.select === "multi"  )return this.options.select;
        return "none";
    },
    isSelectTdHidden :function(){
        if( !this.viewJson.firstTdHidden ){
            return false;
        }
        if( this.viewJson.group && this.viewJson.group.column ){
            return false;
        }
        if( this.json.defaultSelectedScript || this.viewJson.defaultSelectedScript ){
            return false;
        }
        // if( !this.viewSelectedEnable ){
        //     return true;
        // }
        if( this.options && this.options.select ){
            return  this.options.select === "none" || this.options.select === "no";
        }
        if( this.json && this.json.select ){
            return  this.json.select === "none" || this.json.select === "no";
        }
        if( this.viewJson && this.viewJson.select ){
            return  this.viewJson.select === "none" || this.viewJson.select === "no";
        }
        return true;
        // if( this.json.select === "single" || this.json.select === "multi" || this.json.defaultSelectedScript || this.viewJson.defaultSelectedScript ){
        //     return false;
        // }
        // if( this.options.select === "single" || this.options.select === "multi"  ){
        //     return false;
        // }
        // if( this.viewJson.select === "single" || this.viewJson.select === "multi"  ){
        //     return false;
        // }
        // if( this.viewJson.group && this.viewJson.group.column ){
        //     return false;
        // }
        // return true;
    },
    // _loadPageCountNode: function(){
    //     this.viewPageContentNode.empty();
    //
    //     var size = this.viewPageAreaNode.getSize();
    //     var w1 = this.viewPageFirstNode.getSize().x*2;
    //     var w2 = this.viewPageContentNode.getStyle("margin-left").toInt();
    //     var w = size.x-w1-w2;
    //
    //     var bw = this.css.viewPageButtonNode.width.toInt()+this.css.viewPageButtonNode["margin-right"].toInt();
    //     var count = (w/bw).toInt()-2;
    //     if (count>10) count = 10;
    //     this.showPageCount = Math.min(count, this.pages);
    //
    //     var tmp = this.showPageCount/2;
    //     var n = tmp.toInt();
    //     var left = this.currentPage-n;
    //     if (left<=0) left = 1;
    //     var right = this.showPageCount + left-1;
    //     if (right>this.pages) right = this.pages;
    //     left = right-this.showPageCount+1;
    //     if (left<=1) left = 1;
    //
    //     this.viewPagePrevNode = new Element("div", {"styles": this.css.viewPagePrevButtonNode}).inject(this.viewPageContentNode);
    //     this.loadPageButtonEvent(this.viewPagePrevNode, "viewPagePrevButtonNode_over", "viewPagePrevButtonNode_up", "viewPagePrevButtonNode_down", function(){
    //         if (this.currentPage>1) this.currentPage--;
    //         this.loadCurrentPageData();
    //     }.bind(this));
    //
    //     for (i=left; i<=right; i++){
    //         var node = new Element("div", {"styles": this.css.viewPageButtonNode, "text": i}).inject(this.viewPageContentNode);
    //         if (i==this.currentPage){
    //             node.setStyles(this.css.viewPageButtonNode_current);
    //         }else{
    //             this.loadPageButtonEvent(node, "viewPageButtonNode_over", "viewPageButtonNode_up", "viewPageButtonNode_down", function(e){
    //                 this.currentPage = e.target.get("text").toInt();
    //                 this.loadCurrentPageData();
    //             }.bind(this));
    //         }
    //     }
    //     this.viewPageNextNode = new Element("div", {"styles": this.css.viewPageNextButtonNode}).inject(this.viewPageContentNode);
    //     this.loadPageButtonEvent(this.viewPageNextNode, "viewPageNextButtonNode_over", "viewPageNextButtonNode_up", "viewPageNextButtonNode_down", function(){
    //         if (this.currentPage<=this.pages-1) this.currentPage++;
    //         this.loadCurrentPageData();
    //     }.bind(this));
    // },
    // loadPageButtonEvent: function(node, over, out, down, click){
    //     node.addEvents({
    //         "mouseover": function(){node.setStyles(this.css[over])}.bind(this),
    //         "mouseout": function(){node.setStyles(this.css[out])}.bind(this),
    //         "mousedown": function(){node.setStyles(this.css[down])}.bind(this),
    //         "mouseup": function(){node.setStyles(this.css[out])}.bind(this),
    //         "click": click
    //     });
    // },
    // _loadPageNode: function(){
    //     this.viewPageAreaNode.empty();
    //     this.viewPageFirstNode = new Element("div", {"styles": this.css.viewPageFirstLastNode, "text": this.lp.firstPage}).inject(this.viewPageAreaNode);
    //     this.viewPageContentNode = new Element("div", {"styles": this.css.viewPageContentNode}).inject(this.viewPageAreaNode);
    //     this.viewPageLastNode = new Element("div", {"styles": this.css.viewPageFirstLastNode, "text": this.lp.lastPage}).inject(this.viewPageAreaNode);
    //     this._loadPageCountNode();
    //
    //     this.loadPageButtonEvent(this.viewPageFirstNode, "viewPageFirstLastNode_over", "viewPageFirstLastNode_up", "viewPageFirstLastNode_down", function(){
    //         this.currentPage = 1;
    //         this.loadCurrentPageData();
    //     }.bind(this));
    //     this.loadPageButtonEvent(this.viewPageLastNode, "viewPageFirstLastNode_over", "viewPageFirstLastNode_up", "viewPageFirstLastNode_down", function(){
    //         this.currentPage = this.pages;
    //         this.loadCurrentPageData();
    //     }.bind(this));
    // },
    _loadPageNode : function(){
        this.viewPageAreaNode.empty();
        if( !this.paging ){
            var json;
            if( !this.viewJson.pagingList || !this.viewJson.pagingList.length ){
                json = {
                    "firstPageText": this.lp.firstPage,
                    "lastPageText": this.lp.lastPage
                };
            }else{
                json = this.viewJson.pagingList[0];
            }
            this.paging = new MWF.xApplication.query.Query.Viewer.Paging(this.viewPageAreaNode, json, this, {});
            this.paging.load();
        }else{
            this.paging.reload();
        }
    },
    _initPage: function(){
        this.count = this.bundleItems.length;

        var i = this.count/this.json.pageSize;
        this.pages = (i.toInt()<i) ? i.toInt()+1 : i;
        this.currentPage = this.options.defaultPage || 1;
        this.options.defaultPage = null;

        this.exportExcelStart = null;
        this.exportExcelEnd = null;
    },
    lookup: function(data, callback){
        if( this.lookuping )return;
        this.lookuping = true;
        this.getLookupAction(function(){
            if (this.json.application){

                var d = data || {};
                d.count = this.json.count;
                this.lookupAction.bundleView(this.json.id, d, function(json){
                    this.bundleItems = json.data.valueList;
                    this.bundleKey = json.data.key;

                    this._initPage();
                    if (this.bundleItems.length){
                        if( this.noDataTextNode )this.noDataTextNode.destroy();
                        this.loadCurrentPageData( function () {
                            this.fireEvent("postLoad"); //用户配置的事件
                            this.lookuping = false;
                            if(callback)callback(this);
                        }.bind(this));
                    }else{
                        //this._loadPageNode();
                        this.viewPageAreaNode.empty();
                        if( this.viewJson.noDataText ){
                            var noDataTextNodeStyle = this.css.noDataTextNode;
                            if( this.viewJson.viewStyles && this.viewJson.viewStyles["noDataTextNode"] ){
                                noDataTextNodeStyle = this.viewJson.viewStyles["noDataTextNode"];
                            }
                            this.noDataTextNode = new Element( "div", {
                                "styles": noDataTextNodeStyle,
                                "text" : this.viewJson.noDataText
                            }).inject( this.contentAreaNode );
                        }
                        if (this.loadingAreaNode){
                            this.loadingAreaNode.destroy();
                            this.loadingAreaNode = null;
                        }
                        this.fireEvent("postLoad"); //用户配置的事件
                        this.lookuping = false;
                        if(callback)callback(this);
                    }
                }.bind(this));
            }
        }.bind(this));
    },
    loadCurrentPageData: function( callback, async ){
        //是否需要在翻页的时候清空之前的items ?

        if( this.pageloading )return;
        this.pageloading = true;

        this.items = [];

        var p = this.currentPage;
        var d = {};
        var valueList = this.bundleItems.slice((p-1)*this.json.pageSize,this.json.pageSize*p);
        d.bundleList = valueList;
        d.key = this.bundleKey;


        while (this.viewTable.rows.length>1){
            this.viewTable.deleteRow(-1);
        }
        //this.createLoadding();

        this.loadViewRes = this.lookupAction.loadView(this.json.name, this.json.application, d, function(json){
            this.viewData = json.data;

            this.fireEvent("postLoadPageData");

            if (this.viewJson.group.column){
                this.gridJson = json.data.groupGrid;
                this.setSelectedableFlag();
                this.loadGroupData();
            }else{
                this.gridJson = json.data.grid;
                this.setSelectedableFlag();
                this.loadData();
            }
            if (this.gridJson.length) this._loadPageNode();
            if (this.loadingAreaNode){
                this.loadingAreaNode.destroy();
                this.loadingAreaNode = null;
            }

            this.pageloading = false;

            this.fireEvent("loadView"); //options 传入的事件
            this.fireEvent("postLoadPage");

            if(callback)callback();
        }.bind(this), null, async === false ? false : true );
    },
    setSelectedableFlag : function(){
        this.viewSelectedEnable = false;
        var selectedAbleScript = this.json.selectedAbleScript || this.viewJson.selectedAbleScript;
        if (this.viewJson.group.column){
            this.gridJson.each( function( d ){
                d.list.each( function( v ){
                    if( selectedAbleScript ){
                        v.$selectedEnable = this.Macro.exec( selectedAbleScript, { "data" : v, "groupData" : d ,"view": this });
                    }else{
                        v.$selectedEnable = true;
                    }
                    if( v.$selectedEnable ){
                        d.$selectedEnable = true;
                        this.viewSelectedEnable = true;
                    }
                }.bind(this))
            }.bind(this))
        }else{
            this.gridJson.each( function( v ){
                if( selectedAbleScript ){
                    v.$selectedEnable = this.Macro.exec( selectedAbleScript, { "data" : v, "view": this });
                }else{
                    v.$selectedEnable = true;
                }
                if( v.$selectedEnable )this.viewSelectedEnable = true;
            }.bind(this))
        }
    },
    getMode: function(){
        return this.viewJson.group.column ? "group" : "item";
    },
    createSelectAllNode: function(){
        if( this.getSelectFlag() === "multi" && this.viewJson.allowSelectAll ){
            if (this.selectTitleCell && !this.selectTitleCell.retrieve("selectAllLoaded") ){
                if( this.viewJson.viewStyles && this.viewJson.viewStyles["checkboxNode"] ){
                    this.selectAllNode = this.selectTitleCell;
                    this.selectAllNode.setStyles( this.viewJson.viewStyles["checkboxNode"] );
                    // this.selectAllNode = new Element("div", {
                    //     styles : this.viewJson.viewStyles["checkboxNode"]
                    // }).inject( this.selectTitleCell );
                }else{
                    this.selectAllNode.html( "<img src='../x_component_query_Query/$Viewer/"+this.options.style+"/icon/checkbox.png'/>"+"</span>" )
                    // this.selectAllNode = new Element("div",{
                    //     html : "<img src='../x_component_query_Query/$Viewer/"+this.options.style+"/icon/checkbox.png'/>"+"</span>",
                    //     style : "font-family: Webdings"
                    // }).inject( this.selectTitleCell );
                }
                this.selectTitleCell.setStyle("cursor", "pointer");
                this.selectTitleCell.addEvent("click", function () {
                    if( this.getSelectAllStatus() === "all" ){
                        this.unSelectAll("view")
                    }else{
                        this.selectAll("view");
                    }
                }.bind(this));
                this.selectTitleCell.store("selectAllLoaded", true);
            }
        }
    },
    loadData: function(){
        if( this.getSelectFlag() === "multi" && this.viewJson.allowSelectAll ) {
            if(this.selectTitleCell && this.selectTitleCell.retrieve("selectAllLoaded")){
                this.setUnSelectAllStyle();
            }else{
                this.createSelectAllNode();
            }
        }else if(this.selectAllNode){
            this.clearSelectAllStyle();
        }

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
        if( this.getSelectFlag() === "multi" && this.viewJson.allowSelectAll ) {
            if(this.selectTitleCell && this.selectTitleCell.retrieve("selectAllLoaded")){
                this.setUnSelectAllStyle();
            }else{
                this.createSelectAllNode();
            }
        }else if( this.selectAllNode ){
            this.clearSelectAllStyle();
        }

        if( this.expandTitleCell ){
            if ( !this.expandTitleCell.retrieve("expandLoaded") ){
                if( this.viewJson.viewStyles && this.viewJson.viewStyles["groupCollapseNode"] ){
                    this.expandAllNode = new Element("span", {
                        styles : this.viewJson.viewStyles["groupCollapseNode"]
                    }).inject( this.expandTitleCell, "top" );
                    // this.selectTitleCell.setStyle("cursor", "pointer");
                }else{
                    // this.selectTitleCell.set("html", "<span style='font-family: Webdings'>"+"<img src='../x_component_query_Query/$Viewer/"+this.options.style+"/icon/expand.png'/>"+"</span>");
                    this.expandAllNode = new Element("span",{
                        html : "<img src='../x_component_query_Query/$Viewer/"+this.options.style+"/icon/expand.png'/>"+"</span>",
                        style : "font-family: Webdings"
                    }).inject( this.expandTitleCell, "top" );
                }
                this.expandTitleCell.setStyle("cursor", "pointer");
                this.expandTitleCell.addEvent("click", this.expandOrCollapseAll.bind(this));
                this.expandTitleCell.store("expandLoaded", true);
            }else if( this.getExpandFlag() !=="yes" ){
                this.setCollapseAllStyle();
            }
        }


        // this.isAllExpanded = false;

        if (this.gridJson.length){
            var i = 0;
            this.gridJson.each(function(data){
                this.items.push(new MWF.xApplication.query.Query.Viewer.ItemCategory(this, data, i));
                i += data.list.length;
            }.bind(this));

            if (this.getExpandFlag()=="yes") this.expandOrCollapseAll();
        }else{
            if (this.viewPageAreaNode) this.viewPageAreaNode.empty();
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
        if( this.lookuping || this.pageloading )return;
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
    },
    _loadModuleEvents : function(){
        Object.each(this.viewJson.events, function(e, key){
            if (e.code){
                if (this.options.moduleEvents.indexOf(key)!==-1){
                    this.addEvent(key, function(event, target){
                        return this.Macro.fire(e.code, target || this, event);
                    }.bind(this));
                }
            }
        }.bind(this));
    },
    _loadDomEvents: function(){
        Object.each(this.viewJson.events, function(e, key){
            if (e.code){
                if (this.options.moduleEvents.indexOf(key)===-1){
                    this.node.addEvent(key, function(event){
                        return this.Macro.fire(e.code, this, event);
                    }.bind(this));
                }
            }
        }.bind(this));
    },

    //搜索相关开始
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
        debugger;
        if (this.viewJson.customFilterList) {
            var key = this.viewSearchInputNode.get("value");
            if (key && key !== this.lp.searchKeywork) {
                var filterData = this.json.filter ? this.json.filter.clone() : [];
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
                this.filterItems = [];
                var filterData = this.json.filter ? this.json.filter.clone() : [];
                this.createViewNode( {"filterList": filterData} );
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
        if( this.lastFilterItems ){
            this.filterItems = this.lastFilterItems;
        }else{
            this.filterItems = [];
        }
        debugger;
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

            if(this.setContentHeightFun)this.setContentHeightFun();
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
            var selectableList = this.getCustomSelectScriptResult(entry);
            switch (entry.formatType){
                case "numberValue":
                    this.loadComparisonSelect(this.lp.numberFilter);
                    if( selectableList.length > 0 ){
                        this.loadViewSerchCustomSelectByScript(selectableList)
                    }else{
                        this.loadViewSearchCustomValueNumberInput();
                    }
                    break;
                case "dateTimeValue":
                    this.loadComparisonSelect(this.lp.dateFilter);
                    if( selectableList.length > 0 ){
                        this.loadViewSerchCustomSelectByScript(selectableList)
                    }else {
                        this.loadViewSearchCustomValueDateTimeInput();
                    }
                    break;
                case "dateValue":
                    this.loadComparisonSelect(this.lp.dateFilter);
                    if( selectableList.length > 0 ){
                        this.loadViewSerchCustomSelectByScript(selectableList)
                    }else {
                        this.loadViewSearchCustomValueDateInput();
                    }
                    break;
                case "timeValue":
                    this.loadComparisonSelect(this.lp.dateFilter);
                    if( selectableList.length > 0 ){
                        this.loadViewSerchCustomSelectByScript(selectableList)
                    }else {
                        this.loadViewSearchCustomValueTimeInput();
                    }
                    break;
                case "booleanValue":
                    this.loadComparisonSelect(this.lp.booleanFilter);
                    if( selectableList.length > 0 ){
                        this.loadViewSerchCustomSelectByScript(selectableList)
                    }else {
                        this.loadViewSearchCustomValueBooleanInput();
                    }
                    break;
                default:
                    this.loadComparisonSelect(this.lp.textFilter);
                    if( selectableList.length > 0 ){
                        this.loadViewSerchCustomSelectByScript(selectableList)
                    }else {
                        this.loadViewSearchCustomValueTextInput();
                    }
            }
        }
    },
    getCustomSelectScriptResult : function( entry ){
        var scriptResult = [];
        if( entry.valueType === "script" ){
            if( entry.valueScript && entry.valueScript.code ){
                var result = this.Macro.exec(entry.valueScript.code, this);
                var array = typeOf( result ) === "array" ? result : [result];
                for( var i=0; i<array.length; i++ ){
                    if( array[i].indexOf( "|" ) > -1 ){
                        var arr = array[i].split("|");
                        scriptResult.push({ "text" : arr[0], "value" : arr[1] })
                    }else{
                        scriptResult.push({ "text" : array[i], "value" : array[i] })
                    }
                }
            }
        }
        return scriptResult;
    },
    loadViewSerchCustomSelectByScript: function( array ){
        this.viewSearchCustomValueContentNode.empty();
        this.viewSearchCustomValueNode = new Element("select", {
            "styles": this.css.viewFilterSearchCustomComparisonListNode,
            "multiple": true
        }).inject(this.viewSearchCustomValueContentNode);
        array.each(function( v ){
            var option = new Element("option", {
                "style": this.css.viewFilterSearchOptionNode,
                "value": v.value,
                "text": v.text,
                "selected" : array.length === 1
            }).inject(this.viewSearchCustomValueNode);
        }.bind(this));
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
                "secondEnable" : true,
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

                if(this.setContentHeightFun)this.setContentHeightFun();
            }.bind(this));
            this.lastFilterItems = this.filterItems;
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

            if( entry.valueType === "script" && entry.valueScript && entry.valueScript.code  ){
                var idx = this.viewSearchCustomValueNode.selectedIndex;
                if (idx!==-1){
                    var v = this.viewSearchCustomValueNode.options[idx].get("value");
                    value = entry.formatType === "booleanValue" ? (v==="true") : v;
                }
            }else{
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
    //搜索相关结束

    //api 使用 开始
    getParentEnvironment : function(){
        return this.parentMacro ? this.parentMacro.environment : null;
    },
    getViewInfor : function(){
        return this.json;
    },
    getPageInfor : function(){
        return {
            pages : this.pages,
            perPageCount : this.options.perPageCount,
            currentPageNumber : this.currentPage
        };
    },
    getPageData : function () {
        return this.gridJson;
    },
    toPage : function( pageNumber, callback ){
        this.currentPage = pageNumber;
        this.loadCurrentPageData( callback );
    },
    getSelectedData : function(){
        return this.getData();
    },
    getSelectAllStatus : function(){
        if( this.getSelectFlag()!=="multi")return;
        if( !this.items.length )return "none";
        var allFlag = true, noneFlag = true;
        for( var i=0; i<this.items.length; i++ ){
            var item = this.items[i];
            if( item.clazzType === "item" ){
                if( item.data.$selectedEnable ){
                    item.isSelected ? ( noneFlag = false ) : (allFlag = false);
                }
            }else{
                if( item.data.$selectedEnable ) {
                    var f = item.getSelectAllStatus();
                    if (f === "none") {
                        allFlag = false;
                    } else if (f === "all") {
                        noneFlag = false;
                    } else if (f === "some") {
                        allFlag = false;
                        noneFlag = false;
                    }
                }
            }
            if( !allFlag && !noneFlag )return "some"
        }
        if( noneFlag )return "none";
        if( allFlag )return "all";
        return "some";
    },
    checkSelectAllStatus : function(){
        if(!this.selectAllNode)return;
       var status = this.getSelectAllStatus();
       if( status === "all" ){
           this.setSelectAllStyle()
       }else{
           this.setUnSelectAllStyle()
       }
    },
    setSelectAllStyle : function () {
        if(!this.selectAllNode)return;
        if( this.viewJson.viewStyles && this.viewJson.viewStyles["checkedCheckboxNode"] ){
            this.selectAllNode.setStyles( this.viewJson.viewStyles["checkedCheckboxNode"] );
        }else {
            this.selectAllNode.getElement("img").set("src",
                '../x_component_query_Query/$Viewer/" + this.options.style + "/icon/checkbox_checked.png' )
        }
    },
    setUnSelectAllStyle : function () {
        if(!this.selectAllNode)return;
        if( this.viewJson.viewStyles && this.viewJson.viewStyles["checkboxNode"] ){
            this.selectAllNode.setStyles( this.viewJson.viewStyles["checkboxNode"] );
        }else {
            this.selectAllNode.getElement("img").set("src",
                '../x_component_query_Query/$Viewer/" + this.options.style + "/icon/checkbox.png' )
        }
    },
    clearSelectAllStyle : function(){
        debugger;
        if(!this.selectAllNode)return;
        var viewStyles = this.viewJson.viewStyles;
        var viewTitleCellNode = (viewStyles && viewStyles["titleTd"]) ? viewStyles["titleTd"] : this.css.viewTitleCellNode;
        if( !viewTitleCellNode["background"] || !viewTitleCellNode["background-image"] || !viewTitleCellNode["background-color"] ){
            viewTitleCellNode["background"] = "inherit";
        }
        this.selectAllNode.setStyles( viewTitleCellNode );
        var img = this.selectAllNode.getElement("img");
        if( img )img.set("src", '../x_component_query_Query/$Viewer/" + this.options.style + "/icon/blank.png')
    },
    selectAll : function(){
        // var flag = this.json.select || this.viewJson.select ||  "none";
        if ( this.getSelectFlag()==="multi"){
            this.items.each( function (item) {
                if( item.data.$selectedEnable ){
                    if( item.clazzType === "item" ){
                        item.selected("view");
                    }else{
                        item.selectAll("view");
                    }
                }
            })
            if( this.viewJson.allowSelectAll ) {
                this.setSelectAllStyle();
            }
        }
    },
    unSelectAll : function(){
        // var flag = this.json.select || this.viewJson.select ||  "none";
        if ( this.getSelectFlag()==="multi"){
            this.items.each( function (item) {
                if( item.data.$selectedEnable ) {
                    if (item.clazzType === "item") {
                        item.unSelected("view");
                    } else {
                        item.unSelectAll("view");
                    }
                }
            })
            if( this.viewJson.allowSelectAll ) {
                this.setUnSelectAllStyle();
            }
        }
    },

    getExpandAllStatus: function(){
        if( !this.items.length )return "none";
        var allFlag = true, noneFlag = true;
        for( var i=0; i<this.items.length; i++ ){
            var item = this.items[i];
            item.expanded ? ( noneFlag = false ) : (allFlag = false);
            if( !allFlag && !noneFlag )return "some"
        }
        if( allFlag )return "all";
        if( noneFlag )return "none";
        return "some";
    },
    checkExpandAllStatus: function(){
        if(!this.expandAllNode)return;
        var status = this.getExpandAllStatus();
        if( status === "all" ){
            this.setExpandAllStyle()
        }else{
            this.setCollapseAllStyle()
        }
    },
    setExpandAllStyle: function(){
        if(!this.expandAllNode)return;
        if( this.viewJson.viewStyles && this.viewJson.viewStyles["groupExpandNode"] ){
            this.expandAllNode.setStyles( this.viewJson.viewStyles["groupExpandNode"] );
        }else{
            this.expandAllNode.set("html", "<img src='../x_component_query_Query/$Viewer/"+this.options.style+"/icon/expand.png'/>");
        }
    },
    setCollapseAllStyle : function(){
        if(!this.expandAllNode)return;
        if( this.viewJson.viewStyles && this.viewJson.viewStyles["groupCollapseNode"] ){
            this.expandAllNode.setStyles( this.viewJson.viewStyles["groupCollapseNode"] );
        }else{
            this.expandAllNode.set("html", "<img src='../x_component_query_Query/$Viewer/"+this.options.style+"/icon/down.png'/>");
        }
    },
    expandAll: function(){
        this.items.each(function(item, i){
            window.setTimeout(function(){
                item.expand( "view" );
            }.bind(this), 10*i+5);
        }.bind(this));
        this.setExpandAllStyle();
    },
    collapseAll: function(){
        this.items.each(function(item){
            item.collapse( "view" );
        }.bind(this));
        this.setCollapseAllStyle();
    },
    expandOrCollapseAll: function(){
        this.getExpandAllStatus() === "all" ? this.collapseAll() : this.expandAll();
        // if( this.viewJson.viewStyles && this.viewJson.viewStyles["groupCollapseNode"] ){
        //     var span = this.expandAllNode; //this.selectTitleCell.getElement("span");
        //     if( this.isAllExpanded ){
        //         this.items.each(function(item){
        //             item.collapse();
        //             span.setStyles( this.viewJson.viewStyles["groupCollapseNode"] );
        //         }.bind(this));
        //         this.isAllExpanded = false;
        //     }else{
        //         this.items.each(function(item, i){
        //             window.setTimeout(function(){
        //                 item.expand();
        //             }.bind(this), 10*i+5);
        //
        //             span.setStyles( this.viewJson.viewStyles["groupExpandNode"] );
        //             this.isAllExpanded = true;
        //         }.bind(this));
        //     }
        // }else{
        //     var icon = this.expandAllNode; //this.selectTitleCell.getElement("span");
        //     if (icon.get("html").indexOf("expand.png")===-1){
        //         this.items.each(function(item){
        //             item.collapse();
        //             icon.set("html", "<img src='../x_component_query_Query/$Viewer/"+this.options.style+"/icon/expand.png'/>");
        //         }.bind(this));
        //         this.isAllExpanded = false;
        //     }else{
        //         this.items.each(function(item, i){
        //             window.setTimeout(function(){
        //                 item.expand();
        //             }.bind(this), 10*i+5);
        //
        //             icon.set("html", "<img src='../x_component_query_Query/$Viewer/"+this.options.style+"/icon/down.png'/>");
        //         }.bind(this));
        //         this.isAllExpanded = true;
        //     }
        // }
    },

    setFilter : function( filter, callback ){
        if( this.lookuping || this.pageloading )return;
        if( !filter )filter = [];
        if( typeOf( filter ) === "object" )filter = [ filter ];
        this.json.filter = filter;
        if( this.viewAreaNode ){
            this.createViewNode({"filterList": this.json.filter  ? this.json.filter.clone() : null}, callback);
        }
    },
    switchView : function( json ){
        debugger;
        // json = {
        //     "application": application,
        //     "viewName": viewName,
        //     "isTitle": "yes",
        //     "select": "none",
        //     "titleStyles": titleStyles,
        //     "itemStyles": itemStyles,
        //     "isExpand": "no",
        //     "filter": filter
        // }
        this.node.setStyle("display", "block");
        if (this.loadingAreaNode) this.loadingAreaNode.setStyle("display", "block");

        this.searchMorph = null;
        this.viewSearchCustomContentNode = null;

        var newJson = Object.merge( Object.clone(this.originalJson), json );
        this.container.empty();
        this.initialize( this.container, newJson, Object.clone(this.options), this.app, this.parentMacro);
    },
    confirm: function (type, e, title, text, width, height, ok, cancel, callback, mask, style) {
        this.app.confirm(type, e, title, text, width, height, ok, cancel, callback, mask, style)
    },
    alert: function (type, title, text, width, height) {
        this.app.alert(type, "center", title, text, width, height);
    },
    notice: function (content, type, target, where, offset, option) {
        this.app.notice(content, type, target, where, offset, option)
    }
    //api 使用 结束
});

MWF.xApplication.query.Query.Viewer.Item = new Class({
    initialize: function(view, data, prev, i, category){
        this.view = view;
        this.data = data;
        this.css = this.view.css;
        this.isSelected = false;
        this.category = category;
        this.prev = prev;
        this.idx = i;
        this.clazzType = "item";

        this.load();
    },
    load: function(){
        this.view.fireEvent("queryLoadItemRow", [null, this]);

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

        //if (this.view.json.select==="single" || this.view.json.select==="multi"){
        this.selectTd = new Element("td", { "styles": viewContentTdNode }).inject(this.node);
        this.selectTd.setStyles({"cursor": "pointer"});
        if (this.view.json.itemStyles) this.selectTd.setStyles(this.view.json.itemStyles);

        //var selectFlag = this.view.json.select || this.view.viewJson.select ||  "none";
        var selectFlag = this.view.getSelectFlag();
        if ( this.data.$selectedEnable && ["multi","single"].contains(selectFlag) && this.view.viewJson.selectBoxShow==="always") {
            var viewStyles = this.view.viewJson.viewStyles;
            if (viewStyles) {
                if (selectFlag === "single") {
                    this.selectTd.setStyles(viewStyles["radioNode"]);
                } else {
                    this.selectTd.setStyles(viewStyles["checkboxNode"]);
                }
            } else {
                var iconName = "checkbox";
                if (selectFlag === "single") iconName = "radiobox";
                this.selectTd.setStyles({"background": "url(" + "../x_component_query_Query/$Viewer/default/icon/" + iconName + ".png) center center no-repeat"});
            }
        }

        if( this.view.isSelectTdHidden() ){
            this.selectTd.hide();
        }
        //}

        //序号
        if (this.view.viewJson.isSequence==="yes"){
            this.sequenceTd = new Element("td", {"styles": viewContentTdNode}).inject(this.node);
            this.sequenceTd.setStyle("width", "10px");
            var s= 1+this.view.json.pageSize*(this.view.currentPage-1)+this.idx;
            this.sequenceTd.set("text", s);
        }

        debugger;

        Object.each(this.view.entries, function(c, k){
            var cell = this.data.data[k];
            if (cell === undefined) cell = "";
            //if (cell){
            if (this.view.hideColumns.indexOf(k)===-1){
                var td = new Element("td", {"styles": viewContentTdNode}).inject(this.node);
                if (k!== this.view.viewJson.group.column){
                    //var v = (this.view.entries[k].code) ? MWF.Macro.exec(this.view.entries[k].code, {"value": cell, "gridData": this.view.gridJson, "data": this.view.viewData, "entry": this.data}) : cell;
                    var v = cell;
                    if (c.isHtml){
                        td.set("html", v);
                    }else{
                        td.set("text", v);
                    }
                    if( typeOf(c.contentProperties) === "object" )td.setProperties(c.contentProperties);
                    if (this.view.json.itemStyles) td.setStyles(this.view.json.itemStyles);
                    if( typeOf(c.contentStyles) === "object" )td.setStyles(c.contentStyles);
                }else{
                    if (this.view.json.itemStyles) td.setStyles(this.view.json.itemStyles);
                }

                if (this.view.openColumns.indexOf(k)!==-1){
                    this.setOpenWork(td, c)
                }

                if (k!== this.view.viewJson.group.column){
                    Object.each( c.events || {}, function (e , key) {
                        if(e.code){
                            if( key === "loadContent" ){
                                this.view.Macro.fire( e.code,
                                    {"node" : td, "json" : c, "data" : v, "view": this.view, "row" : this});
                            }else if( key !== "loadTitle" ){
                                td.addEvent(key, function(event){
                                    return this.view.Macro.fire(
                                        e.code,
                                        {"node" : td, "json" : c, "data" : v, "view": this.view, "row" : this},
                                        event
                                    );
                                }.bind(this));
                            }
                        }
                    }.bind(this));
                }
            }
            //}
        }.bind(this));


        //默认选中
        var selectedFlag;
        var defaultSelectedScript = this.view.json.defaultSelectedScript || this.view.viewJson.defaultSelectedScript;
        if( !this.isSelected && defaultSelectedScript ){
            // var flag = this.view.json.select || this.view.viewJson.select ||  "none";
            // if ( flag ==="single" || flag==="multi"){
            //
            // }
            selectedFlag = this.view.Macro.exec( defaultSelectedScript,
                {"node" : this.node, "data" : this.data, "view": this.view, "row" : this});
        }
        //判断是不是在selectedItems中，用户手工选择
        if( !this.isSelected && this.view.selectedItems.length ){
            for(var i=0; i<this.view.selectedItems.length; i++){
                if( this.view.selectedItems[i].data.bundle === this.data.bundle ){
                    selectedFlag = "true";
                    break;
                }
            }
        }

        if( selectedFlag ){
            if( selectedFlag === "multi" || selectedFlag === "single" ){
                this.select( selectedFlag );
            }else if( selectedFlag.toString() === "true" ){
                var f = this.view.json.select || this.view.viewJson.select ||  "none";
                if ( f ==="single" || f==="multi"){
                    this.select();
                }
            }
        }

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

        this.view.fireEvent("postLoadItemRow", [null, this]);
    },
    setOpenWork: function(td, column){
        td.setStyle("cursor", "pointer");
        if( column.clickCode ){
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
                    this.openCms(ev)
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
    openCms: function(e){
        var options = {"documentId": this.data.bundle};
        this.view.fireEvent("openDocument", [options, this]); //options 传入的事件
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
        var cssWorksArea = this.css.worksAreaNode
        if (layout.mobile) {
            cssWorksArea = this.css.worksAreaNodeMobile;
        }
        var worksAreaNode = new Element("div", {"styles": cssWorksArea});
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
        this.view.fireEvent("openDocument", [options, this]); //options 传入的事件
        layout.desktop.openApplication(e, "process.Work", options);
    },
    openWorkCompleted: function(id, e){
        var options = {"workCompletedId": id};
        this.view.fireEvent("openDocument", [options, this]); //options 传入的事件
        layout.desktop.openApplication(e, "process.Work", options);
    },

    setEvent: function(){
        var flag = this.view.getSelectFlag();
        if ( this.data.$selectedEnable && (flag ==="single" || flag==="multi")){
            this.node.addEvents({
                "mouseover": function(){
                    if (!this.isSelected && this.view.viewJson.selectBoxShow !=="always" ){
                        var viewStyles = this.view.viewJson.viewStyles;
                        if( viewStyles ){
                            if( flag === "single" ){
                                this.selectTd.setStyles( viewStyles["radioNode"] );
                            }else{
                                this.selectTd.setStyles( viewStyles["checkboxNode"] );
                            }
                        }else{
                            var iconName = "checkbox";
                            if (flag==="single") iconName = "radiobox";
                            this.selectTd.setStyles({"background": "url("+"../x_component_query_Query/$Viewer/default/icon/"+iconName+".png) center center no-repeat"});
                        }
                    }
                }.bind(this),
                "mouseout": function(){
                    if (!this.isSelected && this.view.viewJson.selectBoxShow !=="always") this.selectTd.setStyles({"background": "transparent"});
                }.bind(this),
                "click": function(){this.select();}.bind(this)
            });
        }
    },

    select: function(  force ){
        debugger
        // var flag = force || this.view.json.select || this.view.viewJson.select ||  "none";
        var flag = force || this.view.getSelectFlag();
        if (this.isSelected){
            if (flag==="single"){
                this.unSelectedSingle();
            }else{
                this.unSelected();
            }
        }else{
            if (flag==="single"){
                this.selectedSingle();
            }else{
                this.selected();
            }
        }
        this.view.fireEvent("select"); //options 传入的事件
    },

    selected: function( from ){
        for(var i=0; i<this.view.selectedItems.length; i++){
            var item = this.view.selectedItems[i];
            if( item.data.bundle === this.data.bundle ){
                this.view.selectedItems.erase(item);
                break;
            }
        }
        this.view.selectedItems.push(this);
        var viewStyles = this.view.viewJson.viewStyles;
        if( viewStyles ){
            this.selectTd.setStyles( viewStyles["checkedCheckboxNode"] );
            this.node.setStyles( viewStyles["contentSelectedTr"] );
        }else{
            this.selectTd.setStyles({"background": "url("+"../x_component_query_Query/$Viewer/default/icon/checkbox_checked.png) center center no-repeat"});
            this.node.setStyles(this.css.viewContentTrNode_selected);
        }
        this.isSelected = true;
        if( from !== "view" && from !=="category" && this.view.viewJson.allowSelectAll ){
            this.view.checkSelectAllStatus();
            if( this.category )this.category.checkSelectAllStatus();
        }
        this.view.fireEvent("selectRow", [this]);
    },
    unSelected: function( from ){
        for(var i=0; i<this.view.selectedItems.length; i++){
            var item = this.view.selectedItems[i];
            if( item.data.bundle === this.data.bundle ){
                this.view.selectedItems.erase(item);
                break;
            }
        }
        var viewStyles = this.view.viewJson.viewStyles;
        if( this.view.viewJson.selectBoxShow !=="always" ){
            this.selectTd.setStyles({"background": "transparent"});
        }else{
            if (viewStyles) {
                this.selectTd.setStyles(viewStyles["checkboxNode"]);
            }else{
                this.selectTd.setStyles({"background": "url(" + "../x_component_query_Query/$Viewer/default/icon/checkbox.png) center center no-repeat"});
            }
        }
        if( viewStyles ){
            this.node.setStyles( viewStyles["contentTr"] );
        }else{
            this.node.setStyles(this.css.viewContentTrNode);
        }
        this.isSelected = false;
        if( from !== "view" && from !=="category" && this.view.viewJson.allowSelectAll ){
            this.view.checkSelectAllStatus();
            if( this.category )this.category.checkSelectAllStatus();
        }
        this.view.fireEvent("unselectRow", [this]);
    },
    selectedSingle: function(){
        if (this.view.currentSelectedItem) this.view.currentSelectedItem.unSelectedSingle();
        this.view.selectedItems = [this];
        this.view.currentSelectedItem = this;
        var viewStyles = this.view.viewJson.viewStyles;
        if( viewStyles ){
            this.selectTd.setStyles( viewStyles["checkedRadioNode"] );
            this.node.setStyles( viewStyles["contentSelectedTr"] );
        }else {
            this.selectTd.setStyles({"background": "url(" + "../x_component_query_Query/$Viewer/default/icon/radiobox_checked.png) center center no-repeat"});
            this.node.setStyles(this.css.viewContentTrNode_selected);
        }
        this.isSelected = true;
        this.view.fireEvent("selectRow", [this]);
    },
    unSelectedSingle: function(){
        this.view.selectedItems = [];
        this.view.currentSelectedItem = null;
        var viewStyles = this.view.viewJson.viewStyles;
        if( this.view.viewJson.selectBoxShow !=="always" ){
            this.selectTd.setStyles({"background": "transparent"});
        }else{
            if (viewStyles) {
                this.selectTd.setStyles(viewStyles["radioNode"]);
            }else{
                this.selectTd.setStyles({"background": "url(" + "../x_component_query_Query/$Viewer/default/icon/radiobox.png) center center no-repeat"});
            }
        }
        if( viewStyles ){
            this.node.setStyles( viewStyles["contentTr"] );
        }else{
            this.node.setStyles(this.css.viewContentTrNode);
        }
        this.isSelected = false;
        this.view.fireEvent("unselectRow", [this]);
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
        this.clazzType = "category";
        this.load();
    },
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

        if( this.data.$selectedEnable && this.view.getSelectFlag() === "multi" && this.view.viewJson.allowSelectAll ){
            this.selectAllNode = this.selectTd;
            if( viewStyles && viewStyles["checkboxNode"] ){
                this.selectAllNode.setStyles( viewStyles["checkboxNode"] )
                // this.selectAllNode = new Element("span", {
                //     styles : viewStyles["checkboxNode"]
                // }).inject( this.selectTd );
            }else{
                this.selectAllNode.set( "html", "<img src='../x_component_query_Query/$Viewer/"+this.options.style+"/icon/checkbox.png'/>"+"</span>" )
                // this.selectAllNode = new Element("span",{
                //     html : "<img src='../x_component_query_Query/$Viewer/"+this.options.style+"/icon/checkbox.png'/>"+"</span>",
                //     style : "font-family: Webdings"
                // }).inject( this.selectTd );
            }
            this.selectAllNode.setStyle("cursor", "pointer");
            this.selectAllNode.addEvent("click", function (ev) {
                if( this.getSelectAllStatus() === "all" ){
                    this.unSelectAll("category")
                }else{
                    this.selectAll("category");
                }
                ev.stopPropagation();
            }.bind(this));
        }

        // if( this.view.isSelectTdHidden() ){
        //     this.selectTd.hide();
        // }

        //}
        this.categoryTd = new Element("td", {
            "styles": viewContentCategoryTdNode,
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

        debugger;
        if( this.groupColumn ){
            if( typeOf(this.groupColumn.contentStyles) === "object" )this.categoryTd.setStyles(this.groupColumn.contentStyles);
            if( typeOf(this.groupColumn.contentProperties) === "object" )this.categoryTd.setProperties(this.groupColumn.contentProperties);
        }

        this.setEvent();

        var column = this.groupColumn;
        var td = this.categoryTd;
        Object.each( column.events || {}, function (e , key) {
            if(e.code){
                if( key === "loadContent" ){
                    this.view.Macro.fire( e.code,
                        {"node" : td, "json" : column, "data" : this.data.group, "view": this.view, "row" : this});
                }else if( key !== "loadTitle" ){
                    td.addEvent(key, function(event){
                        return this.view.Macro.fire(
                            e.code,
                            {"node" : td, "json" : column, "data" : this.data.group, "view": this.view, "row" : this},
                            event
                        );
                    }.bind(this));
                }
            }
        }.bind(this));

        this.view.fireEvent("postLoadCategoryRow", [null, this]);
    },
    getSelectAllStatus : function(){
        if ( this.view.getSelectFlag()!=="multi")return;
        if( !this.items.length )return "none";
        var flag = "all";
        var allFlag = true, noneFlag = true;
        for( var i=0; i<this.items.length; i++ ){
            var item = this.items[i];
            if( item.data.$selectedEnable ){
                item.isSelected ? ( noneFlag = false ) : (allFlag = false);
                if( !allFlag && !noneFlag )return "some"
            }
        }
        if( allFlag )return "all";
        if( noneFlag )return "none";
        return "some";
    },
    checkSelectAllStatus : function(){
        if( !this.selectAllNode )return;
        var status = this.getSelectAllStatus();
        if( status === "all" ){
            this.setSelectAllStyle()
        }else{
            this.setUnSelectAllStyle()
        }
    },
    setSelectAllStyle : function () {
        if( !this.selectAllNode )return;
        if( this.view.viewJson.viewStyles && this.view.viewJson.viewStyles["checkedCheckboxNode"] ){
            this.selectAllNode.setStyles( this.view.viewJson.viewStyles["checkedCheckboxNode"] );
        }else {
            this.selectAllNode.getElement("img").set("src",
                '../x_component_query_Query/$Viewer/" + this.options.style + "/icon/checkbox_checked.png' )
        }
    },
    setUnSelectAllStyle : function () {
        if( !this.selectAllNode )return;
        if( this.view.viewJson.viewStyles && this.view.viewJson.viewStyles["checkboxNode"] ){
            this.selectAllNode.setStyles( this.view.viewJson.viewStyles["checkboxNode"] );
        }else {
            this.selectAllNode.getElement("img").set("src",
                '../x_component_query_Query/$Viewer/" + this.options.style + "/icon/checkbox.png' )
        }
    },
    selectAll : function( from ){
        // var flag = this.json.select || this.viewJson.select ||  "none";
        if ( this.view.getSelectFlag()==="multi"){
            this.expand();
            this.items.each( function (item) {
                if( item.data.$selectedEnable ){
                    item.selected( from );
                }
            })
            if( this.view.viewJson.allowSelectAll ){
                this.setSelectAllStyle();
                if( from !== "view" ){
                    this.view.checkSelectAllStatus();
                }
            }
        }

    },
    unSelectAll : function( from ){
        // var flag = this.json.select || this.viewJson.select ||  "none";
        if ( this.view.getSelectFlag()==="multi"){
            this.items.each( function (item) {
                if( item.data.$selectedEnable ) {
                    item.unSelected(from);
                }
            })
            if( this.view.viewJson.allowSelectAll ) {
                this.setUnSelectAllStyle();
                if (from !== "view") {
                    this.view.checkSelectAllStatus();
                }
            }
        }
    },
    setEvent: function(){
        //if (this.selectTd){
        this.node.addEvents({
            "click": function(){this.expandOrCollapse();}.bind(this)
        });
        //}
    },
    expandOrCollapse: function(){
        // var t = this.node.getElement("span").get("html");
        // if (t.indexOf("expand.png")===-1){
        //     this.collapse();
        // }else{
        //     this.expand();
        // }
        if( this.expanded ){
            this.collapse();
        }else{
            this.expand();
        }
    },
    collapse: function( from ){
        this.items.each(function(item){
            item.node.setStyle("display", "none");
        }.bind(this));
        if( this.expandNode ){
            this.expandNode.setStyles( this.view.viewJson.viewStyles["groupCollapseNode"] )
        }else{
            this.node.getElement("span").set("html", "<img src='../x_component_query_Query/$Viewer/"+this.view.options.style+"/icon/expand.png'/>");
        }
        this.expanded = false;
        if( from !== "view" ){
            this.view.checkExpandAllStatus();
        }
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
                this.lastItem = new MWF.xApplication.query.Query.Viewer.Item(this.view, line, (this.lastItem || this), s, this);
                this.items.push(this.lastItem);
            }.bind(this));
            this.loadChild = true;
            //}.bind(this), 10);
        }
        if( from !== "view" ){
            this.view.checkExpandAllStatus();
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

MWF.xApplication.query.Query.Viewer.Actionbar = new Class({
    Implements: [Events],
    options: {
        "style" : "default",
        "moduleEvents": ["load", "queryLoad", "postLoad", "afterLoad"]
    },
    initialize: function(node, json, form, options){

        this.node = $(node);
        this.node.store("module", this);
        this.json = json;
        this.form = form;
        this.view = form;
    },
    hide: function(){
        var dsp = this.node.getStyle("display");
        if (dsp!=="none") this.node.store("mwf_display", dsp);
        this.node.setStyle("display", "none");
    },
    show: function(){
        var dsp = this.node.retrieve("mwf_display", dsp);
        this.node.setStyle("display", dsp);
    },
    load: function(){

        this._loadModuleEvents();
        if (this.fireEvent("queryLoad")){
            //this._queryLoaded();
            this._loadUserInterface();
            this._loadStyles();
            this._loadDomEvents();
            //this._loadEvents();

            //this._afterLoaded();
            this.fireEvent("postLoad");
            this.fireEvent("load");
        }
    },

    _loadStyles: function(){
        if (this.json.styles) Object.each(this.json.styles, function(value, key){
            if ((value.indexOf("x_processplatform_assemble_surface")!=-1 || value.indexOf("x_portal_assemble_surface")!=-1 || value.indexOf("x_cms_assemble_control")!=-1)){
                var host1 = MWF.Actions.getHost("x_processplatform_assemble_surface");
                var host2 = MWF.Actions.getHost("x_portal_assemble_surface");
                var host3 = MWF.Actions.getHost("x_cms_assemble_control");
                if (value.indexOf("/x_processplatform_assemble_surface")!==-1){
                    value = value.replace("/x_processplatform_assemble_surface", host1+"/x_processplatform_assemble_surface");
                }else if (value.indexOf("x_processplatform_assemble_surface")!==-1){
                    value = value.replace("x_processplatform_assemble_surface", host1+"/x_processplatform_assemble_surface");
                }
                if (value.indexOf("/x_portal_assemble_surface")!==-1){
                    value = value.replace("/x_portal_assemble_surface", host2+"/x_portal_assemble_surface");
                }else if (value.indexOf("x_portal_assemble_surface")!==-1){
                    value = value.replace("x_portal_assemble_surface", host2+"/x_portal_assemble_surface");
                }
                if (value.indexOf("/x_cms_assemble_control")!==-1){
                    value = value.replace("/x_cms_assemble_control", host3+"/x_cms_assemble_control");
                }else if (value.indexOf("x_cms_assemble_control")!==-1){
                    value = value.replace("x_cms_assemble_control", host3+"/x_cms_assemble_control");
                }
                value = o2.filterUrl(value);
            }
            this.node.setStyle(key, value);
        }.bind(this));

        // if (["x_processplatform_assemble_surface", "x_portal_assemble_surface"].indexOf(root.toLowerCase())!==-1){
        //     var host = MWF.Actions.getHost(root);
        //     return (flag==="/") ? host+this.json.template : host+"/"+this.json.template
        // }
        //if (this.json.styles) this.node.setStyles(this.json.styles);
    },
    _loadModuleEvents : function(){
        Object.each(this.json.events, function(e, key){
            if (e.code){
                if (this.options.moduleEvents.indexOf(key)!==-1){
                    this.addEvent(key, function(event){
                        return this.form.Macro.fire(e.code, this, event);
                    }.bind(this));
                }
            }
        }.bind(this));
    },
    _loadDomEvents: function(){
        Object.each(this.json.events, function(e, key){
            if (e.code){
                if (this.options.moduleEvents.indexOf(key)===-1){
                    this.node.addEvent(key, function(event){
                        return this.form.Macro.fire(e.code, this, event);
                    }.bind(this));
                }
            }
        }.bind(this));
    },
    _loadEvents: function(){
        Object.each(this.json.events, function(e, key){
            if (e.code){
                if (this.options.moduleEvents.indexOf(key)!==-1){
                    this.addEvent(key, function(event){
                        return this.form.Macro.fire(e.code, this, event);
                    }.bind(this));
                }else{
                    this.node.addEvent(key, function(event){
                        return this.form.Macro.fire(e.code, this, event);
                    }.bind(this));
                }
            }
        }.bind(this));
    },
    addModuleEvent: function(key, fun){
        if (this.options.moduleEvents.indexOf(key)!==-1){
            this.addEvent(key, function(event){
                return (fun) ? fun(this, event) : null;
            }.bind(this));
        }else{
            this.node.addEvent(key, function(event){
                return (fun) ? fun(this, event) : null;
            }.bind(this));
        }
    },
    _loadUserInterface: function(){
        // if (this.form.json.mode == "Mobile"){
        //     this.node.empty();
        // }else if (COMMON.Browser.Platform.isMobile){
        //     this.node.empty();
        // }else{
        this.toolbarNode = this.node.getFirst("div");
        if( !this.toolbarNode ){
            this.toolbarNode = new Element("div").inject( this.node );
        }
        this.toolbarNode.empty();

        MWF.require("MWF.widget.Toolbar", function(){
            this.toolbarWidget = new MWF.widget.Toolbar(this.toolbarNode, {
                "style": this.json.style,
                "onPostLoad" : function(){
                    this.fireEvent("afterLoad");
                }.bind(this)
            }, this);
            if (this.json.actionStyles) this.toolbarWidget.css = this.json.actionStyles;
            //alert(this.readonly)

            if( this.json.multiTools ){
                this.json.multiTools.each( function (tool) {
                    if( tool.system ){
                        if( !this.json.hideSystemTools ){
                            this.setToolbars([tool], this.toolbarNode, this.readonly);
                        }
                    }else{
                        this.setCustomToolbars([tool], this.toolbarNode);
                    }
                }.bind(this));
                this.toolbarWidget.load();
            }else{
                if (this.json.hideSystemTools){
                    this.setCustomToolbars(this.json.tools, this.toolbarNode);
                    this.toolbarWidget.load();
                }else{
                    if (this.json.defaultTools){
                        this.setToolbars(this.json.defaultTools, this.toolbarNode, this.readonly);
                        this.setCustomToolbars(this.json.tools, this.toolbarNode);
                        this.toolbarWidget.load();
                    }else{
                        MWF.getJSON(this.form.path+"toolbars.json", function(json){
                            this.setToolbars(json, this.toolbarNode, this.readonly, true);
                            this.setCustomToolbars(this.json.tools, this.toolbarNode);
                            this.toolbarWidget.load();
                        }.bind(this), null);
                    }
                }
            }

        }.bind(this));
        // }
    },

    setCustomToolbars: function(tools, node){
        var path = "../x_component_process_FormDesigner/Module/Actionbar/";
        var iconPath = "";
        if( this.json.customIconStyle ){
            iconPath = this.json.customIconStyle+"/";
        }
        tools.each(function(tool){
            var flag = true;
            if (this.readonly){
                flag = tool.readShow;
            }else{
                flag = tool.editShow;
            }
            if (flag){
                flag = true;
                // if (tool.control){
                //     flag = this.form.businessData.control[tool.control]
                // }
                if (tool.condition){
                    var hideFlag = this.form.Macro.exec(tool.condition, this);
                    flag = !hideFlag;
                }
                if (flag){
                    var actionNode = new Element("div", {
                        "id": tool.id,
                        "MWFnodetype": tool.type,
                        "MWFButtonImage": path+""+this.form.options.style+"/custom/"+iconPath+tool.img,
                        "title": tool.title,
                        "MWFButtonAction": "runCustomAction",
                        "MWFButtonText": tool.text
                    }).inject(node);
                    if( this.json.customIconOverStyle ){
                        actionNode.set("MWFButtonImageOver" , path+""+this.form.options.style +"/custom/"+this.json.customIconOverStyle+ "/" +tool.img );
                    }
                    if( tool.properties ){
                        actionNode.set(tool.properties);
                    }
                    if (tool.actionScript){
                        actionNode.store("script", tool.actionScript);
                    }
                    if (tool.sub){
                        var subNode = node.getLast();
                        this.setCustomToolbars(tool.sub, subNode);
                    }
                }
            }
        }.bind(this));
    },

    setToolbarItem: function(tool, node, readonly, noCondition){
        //var path = "../x_component_process_FormDesigner/Module/Actionbar/";
        var path = "../x_component_query_ViewDesigner/$View/";
        var flag = true;
        // if (tool.control){
        //     flag = this.form.businessData.control[tool.control]
        // }
        if (!noCondition) if (tool.condition){
            var hideFlag = this.form.Macro.exec(tool.condition, this);
            flag = flag && (!hideFlag);
        }
        if (readonly) if (!tool.read) flag = false;
        if (flag){
            var actionNode = new Element("div", {
                "id": tool.id,
                "MWFnodetype": tool.type,
                //"MWFButtonImage": this.form.path+""+this.form.options.style+"/actionbar/"+tool.img,
                //"MWFButtonImage": path+(this.options.style||"default") +"/tools/"+ (this.json.style || "default") +"/"+tool.img,
                "MWFButtonImage": path+this.options.style+"/actionbar/"+ ( this.json.iconStyle || "default" ) +"/"+tool.img,
                "title": tool.title,
                "MWFButtonAction": tool.action,
                "MWFButtonText": tool.text
            }).inject(node);
            if( this.json.iconOverStyle ){
                actionNode.set("MWFButtonImageOver" , path+""+this.options.style+"/actionbar/"+this.json.iconOverStyle+"/"+tool.img );
                //actionNode.set("MWFButtonImageOver" , path+""+(this.options.style||"default")+"/tools/"+( this.json.iconOverStyle || "default" )+"/"+tool.img );
            }
            if( tool.properties ){
                actionNode.set(tool.properties);
            }
            if (tool.sub){
                var subNode = node.getLast();
                this.setToolbars(tool.sub, subNode, readonly, noCondition);
            }
        }
    },
    setToolbars: function(tools, node, readonly, noCondition){
        tools.each(function(tool){
            this.setToolbarItem(tool, node, readonly, noCondition);
        }.bind(this));
    },
    runCustomAction: function(bt){
        var script = bt.node.retrieve("script");
        this.form.Macro.exec(script, this);
    },
    exportView : function(){
        this.form.exportView();
    },
    deleteWork: function(){
        this.form.deleteWork();
    }
});

MWF.xApplication.query.Query.Viewer.Paging = new Class({
    Implements: [Events],
    options: {
        "style" : "default",
        "moduleEvents": ["load", "queryLoad", "postLoad", "afterLoad","jump"]
    },
    initialize: function(node, json, form, options){
        this.node = $(node);
        this.node.store("module", this);
        this.json = json;
        this.form = form;
        this.view = form;
    },
    hide: function(){
        var dsp = this.node.getStyle("display");
        if (dsp!=="none") this.node.store("mwf_display", dsp);
        this.node.setStyle("display", "none");
    },
    show: function(){
        var dsp = this.node.retrieve("mwf_display", dsp);
        this.node.setStyle("display", dsp);
    },
    load: function(){

        this._loadModuleEvents();
        if (this.fireEvent("queryLoad")){
            //this._queryLoaded();
            this._loadUserInterface();
            this._loadStyles();
            this._loadDomEvents();
            //this._loadEvents();

            //this._afterLoaded();
            this.fireEvent("postLoad");
        }
    },

    _loadStyles: function(){
        if (this.json.styles) Object.each(this.json.styles, function(value, key){
            if ((value.indexOf("x_processplatform_assemble_surface")!=-1 || value.indexOf("x_portal_assemble_surface")!=-1 || value.indexOf("x_cms_assemble_control")!=-1)){
                var host1 = MWF.Actions.getHost("x_processplatform_assemble_surface");
                var host2 = MWF.Actions.getHost("x_portal_assemble_surface");
                var host3 = MWF.Actions.getHost("x_cms_assemble_control");
                if (value.indexOf("/x_processplatform_assemble_surface")!==-1){
                    value = value.replace("/x_processplatform_assemble_surface", host1+"/x_processplatform_assemble_surface");
                }else if (value.indexOf("x_processplatform_assemble_surface")!==-1){
                    value = value.replace("x_processplatform_assemble_surface", host1+"/x_processplatform_assemble_surface");
                }
                if (value.indexOf("/x_portal_assemble_surface")!==-1){
                    value = value.replace("/x_portal_assemble_surface", host2+"/x_portal_assemble_surface");
                }else if (value.indexOf("x_portal_assemble_surface")!==-1){
                    value = value.replace("x_portal_assemble_surface", host2+"/x_portal_assemble_surface");
                }
                if (value.indexOf("/x_cms_assemble_control")!==-1){
                    value = value.replace("/x_cms_assemble_control", host3+"/x_cms_assemble_control");
                }else if (value.indexOf("x_cms_assemble_control")!==-1){
                    value = value.replace("x_cms_assemble_control", host3+"/x_cms_assemble_control");
                }
                value = o2.filterUrl(value);
            }
            this.node.setStyle(key, value);
        }.bind(this));

        // if (["x_processplatform_assemble_surface", "x_portal_assemble_surface"].indexOf(root.toLowerCase())!==-1){
        //     var host = MWF.Actions.getHost(root);
        //     return (flag==="/") ? host+this.json.template : host+"/"+this.json.template
        // }
        //if (this.json.styles) this.node.setStyles(this.json.styles);
    },
    _loadModuleEvents : function(){
        Object.each(this.json.events, function(e, key){
            if (e.code){
                if (this.options.moduleEvents.indexOf(key)!==-1){
                    this.addEvent(key, function(event){
                        return this.form.Macro.fire(e.code, this, event);
                    }.bind(this));
                }
            }
        }.bind(this));
    },
    _loadDomEvents: function(){
        Object.each(this.json.events, function(e, key){
            if (e.code){
                if (this.options.moduleEvents.indexOf(key)===-1){
                    this.node.addEvent(key, function(event){
                        return this.form.Macro.fire(e.code, this, event);
                    }.bind(this));
                }
            }
        }.bind(this));
    },
    _loadEvents: function(){
        Object.each(this.json.events, function(e, key){
            if (e.code){
                if (this.options.moduleEvents.indexOf(key)!==-1){
                    this.addEvent(key, function(event){
                        return this.form.Macro.fire(e.code, this, event);
                    }.bind(this));
                }else{
                    this.node.addEvent(key, function(event){
                        return this.form.Macro.fire(e.code, this, event);
                    }.bind(this));
                }
            }
        }.bind(this));
    },
    addModuleEvent: function(key, fun){
        if (this.options.moduleEvents.indexOf(key)!==-1){
            this.addEvent(key, function(event){
                return (fun) ? fun(this, event) : null;
            }.bind(this));
        }else{
            this.node.addEvent(key, function(event){
                return (fun) ? fun(this, event) : null;
            }.bind(this));
        }
    },
    _loadUserInterface: function(){
        // if (this.form.json.mode == "Mobile"){
        //     this.node.empty();
        // }else if (COMMON.Browser.Platform.isMobile){
        //     this.node.empty();
        // }else{
        this.loadPaging( true )
    },
    reload : function(){
        this.loadPaging( false )
    },
    loadPaging : function( firstLoading ){
        // this.pagingNode = this.node.getFirst("div");
        // if( !this.pagingNode ){
        //     this.pagingNode = new Element("div").inject( this.node );
        // }
        // this.pagingNode.empty();
        this.node.empty();
        this.paging = new o2.widget.Paging(this.node, {
            //style : this.options.skin && this.options.skin.pagingBar ? this.options.skin.pagingBar : "default",
            countPerPage: this.view.json.pageSize || this.view.options.perPageCount,
            visiblePages: layout.mobile?5:(this.json.visiblePages ? this.json.visiblePages.toInt() : 9),
            currentPage: this.view.currentPage,
            itemSize: this.view.count,
            pageSize: this.view.pages,
            hasNextPage: typeOf( this.json.hasPreNextPage ) === "boolean" ? this.json.hasPreNextPage : true,
            hasPrevPage: typeOf( this.json.hasPreNextPage ) === "boolean" ? this.json.hasPreNextPage : true,
            hasTruningBar: typeOf( this.json.hasTruningBar ) === "boolean" ? this.json.hasTruningBar : true,
            hasBatchTuring: typeOf( this.json.hasBatchTuring ) === "boolean" ? this.json.hasBatchTuring : true,
            hasFirstPage: typeOf( this.json.hasFirstLastPage ) === "boolean" ? this.json.hasFirstLastPage : (layout.mobile?false:true),
            hasLastPage: typeOf( this.json.hasFirstLastPage ) === "boolean" ? this.json.hasFirstLastPage : (layout.mobile?false:true),
            hasJumper: typeOf( this.json.hasPageJumper ) === "boolean" ? this.json.hasPageJumper : (layout.mobile?false:true),
            hiddenWithDisable: false,
            hiddenWithNoItem: true,
            text: {
                prePage: this.json.prePageText,
                nextPage: this.json.nextPageText,
                firstPage: this.json.firstPageText,
                lastPage: this.json.lastPageText
            },
            onJumpingPage : function( pageNum, itemNum ){
                this.view.currentPage = pageNum;
                this.fireEvent("jump");
                this.view.loadCurrentPageData( null, false );
            }.bind(this),
            onPostLoad : function () {
                if( firstLoading ){
                    if(this.view.setContentHeightFun)this.view.setContentHeightFun();
                    this.fireEvent("load");
                }
                this.fireEvent("afterLoad");
            }.bind(this)
        }, this.json.pagingStyles || {});
        this.paging.load();
    }
});
