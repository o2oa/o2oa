MWF.xApplication.query = MWF.xApplication.query || {};
MWF.xApplication.query.Query = MWF.xApplication.query.Query || {};
MWF.require("MWF.widget.Common", null, false);
MWF.require("o2.widget.Paging", null, false);
MWF.require("MWF.xScript.Macro", null, false);
MWF.xDesktop.requireApp("query.Query", "lp."+o2.language, null, false);
/** @classdesc View 数据中心的视图。本章节的脚本上下文请看<b>{@link module:queryView|queryView}。</b>
 * @class
 * @o2cn 视图
 * @o2category QueryView
 * @o2range {QueryView}
 * @hideconstructor
 * @example
 * //在视图的事件中获取该类
 * var view = this.target;
 * @example
 * //在视图的条目中，操作条组件中，分页事件中获取该类
 * var view = this.target.view;
 * @example
 * //调用api进行提示
 * this.queryView.notice("this is my information", "info");
 * */
MWF.xApplication.query.Query.Viewer = MWF.QViewer = new Class(
    /** @lends MWF.xApplication.query.Query.Viewer# */
    {
    Implements: [Options, Events],
    Extends: MWF.widget.Common,
    options: {
        "style": "default",
        "skin" : null,
        "resizeNode": true,
        "paging" : "scroll",
        "perPageCount" : 50,
        "isload": "true",
        "isloadTitle": true,
        "isloadContent": true,
        "isloadActionbar": true,
        "isloadSearchbar": true,
        "export": false,
        "lazy": false,
        "defaultBundles": [],
        "moduleEvents": [
            /**
             * 加载前触发。可通过this.target获取当前对象。
             * @event MWF.xApplication.query.Query.Viewer#queryLoad
             */
            "queryLoad",
            /**
             * 视图界面和当前页数据加载后执行。需注意，翻页也会执行本事件。可通过this.target获取当前对象。
             * @event MWF.xApplication.query.Query.Viewer#postLoad
             */
            "postLoad",
            /**
             * 加载当前页数据后，渲染界面前执行，翻页后也会执行本事件。可通过this.target获取当前对象。
             * @event MWF.xApplication.query.Query.Viewer#postLoadPageData
             */
            "postLoadPageData",
            /**
             * 渲染当前页内容后执行，翻页后也会执行本事件。可通过this.target获取当前对象。
             * @event MWF.xApplication.query.Query.Viewer#postLoadPage
             */
            "postLoadPage",
            /**
             * 选择行后执行。可通过this.target获取视图对象，通过this.event获取行对象。
             * @event MWF.xApplication.query.Query.Viewer#selectRow
             */
            "selectRow",
            /**
             * 取消选择行后执行。可通过this.target获取视图对象，通过this.event获取行对象。
             * @event MWF.xApplication.query.Query.Viewer#unselectRow
             */
            "unselectRow",

            /**
             * 加载每行之前执行（非分类行）。可通过this.target获取视图对象，通过this.event获取行对象。
             * @event MWF.xApplication.query.Query.Viewer#queryLoadItemRow
             */
            "queryLoadItemRow",
            /**
             * 加载每行之后执行（非分类行）。可通过this.target获取视图对象，通过this.event获取行对象。
             * @event MWF.xApplication.query.Query.Viewer#postLoadItemRow
             */
            "postLoadItemRow",
            /**
             * 加载分类行之前执行。可通过this.target获取视图对象，通过this.event获取行对象。
             * @event MWF.xApplication.query.Query.Viewer#queryLoadCategoryRow
             */
            "queryLoadCategoryRow",
            /**
             * 加载分类行后执行。可通过this.target获取视图对象，通过this.event获取行对象。
             * @event MWF.xApplication.query.Query.Viewer#postLoadCategoryRow
             */
            "postLoadCategoryRow",

            /**
             * 导出查询Excel的事件，这个时候导出数据已经准备好，this.target可获得查询视图对象。this.event如下：
             * <pre><code class='language-js'>{
             *       headText: headText, //文本，表格头部文本
             *       headStyle: headStyle, //对象，表格头部样式
             *       titleStyle: titleStyle, //对象，表格标题样式
             *       contentStyle: contentStyle, //对象，表格内容样式
             *       data : data, //对象数组，导出的数据，第一个数组为标题。修改后导出的excel内容也会修改。
             *       colWidthArray : colWidthArr, //数组，每列的宽度
             *       title : excelName //字符串，导出的文件名
             * }</code></pre>
             * @event MWF.xApplication.query.Query.Viewer#export
             */
            "export",

            /**
             * 导出查询Excel，产生每行后执行的事件，this.target可获得查询视图对象，可以通过this.event获取下列内容
             * <pre><code class='language-js'>{
             *       data : data, //对象，当前行导出的数据。修改后导出的excel内容也会修改。
             *       index : 1, //数字，导出的行号，从1开始
             *       source : source //对象，从后台获取的源数据
             * }</code></pre>
             * @event MWF.xApplication.query.Query.Viewer#exportRow
             */
            "exportRow"
         ]
        // "actions": {
        //     "lookup": {"uri": "/jaxrs/view/flag/{view}/query/{application}/execute", "method":"PUT"},
        //     "getView": {"uri": "/jaxrs/view/flag/{view}/query/{application}"}
        //
        // },
        // "actionRoot": "x_query_assemble_surface"
    },
    initialize: function(container, json, options, app, parentMacro){
        //本类有三种事件，
        //一种是通过 options 传进来的事件，包括 loadView、openDocument、select、unselect
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

        /**
         * @summary 视图的详细配置信息，比如条目的选择类型等.
         * @member {Object}
         * @example
         *  //可以在视图脚本中获取视图基本信息（视图事件中）
         * var json = this.target.viewJson; //视图配置信息
         * var name = json.selectList; //视图的列配置
         * @example
         *  //可以在视图的组件中获取视图基本信息(在视图的操作条组件中，分页事件中)
         * var json = this.target.view.viewJson; //视图配置信息
         * var name = json.selectList; //视图的列配置
         */
        this.viewJson = null;
        this.filterItems = [];
        this.searchStatus = "none"; //none, custom, default

        /**
         * @summary 视图当前页的所有行对象数组.
         * @member {Array}
         * @example
         * //获取视图当前页的所有行对象数组
         * var itemList = this.target.items;
         */
        this.items = [];

        /**
         * @summary 视图选中行的对象数组.
         * @member {Array}
         * @example
         * //获取视图选中行的对象数组
         * var itemList = this.target.selectedItems;
         * itemList.each(function(item){
         *      //取消选中
         *     item.unSelected()
         * })
         */
        this.selectedItems = [];
        this.hideColumns = [];
        this.openColumns = [];

        this.gridJson = null;

        if (this.options.isload){
            this.init(function(){
                if( this.json.isTitle==="no" )this.options.isloadTitle = false;
                this.load();
            }.bind(this));
        }

    },
    loadView: function( callback ){
        if (this.viewJson){
            this.reload( callback );
        }else{
            this.init(function(){
                if( this.json.isTitle==="no" )this.options.isloadTitle = false;
                this.load( callback );
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
    load: function( callback ){
        this.loadResource(function () {
            this.loadLanguage( function () {
                this._loadModuleEvents();
                if (this.fireEvent("queryLoad")){
                    this._loadUserInterface( callback );
                    //this._loadStyles();
                    this._loadDomEvents();
                }
            }.bind(this))
        }.bind(this));
    },
    _loadUserInterface: function( callback ){
        this.viewJson = this.bindLP( this.viewJson );

        var defaultSelectedScript, selectedAbleScript;
        if( typeOf(this.json.defaultSelectedScript) === "function" )defaultSelectedScript = this.json.defaultSelectedScript;
        if( typeOf(this.json.selectedAbleScript) === "function" )selectedAbleScript = this.json.selectedAbleScript;
        this.json = this.bindLP( this.json );
        if(defaultSelectedScript)this.json.defaultSelectedScript = defaultSelectedScript;
        if(selectedAbleScript)this.json.selectedAbleScript = selectedAbleScript;

        this.loadLayout();
        if( this.options.isloadActionbar )this.createActionbarNode();
        if( this.options.isloadSearchbar )this.createSearchNode();
        this.createViewNode({"filterList": this.json.filter  ? this.json.filter.clone() : null}, callback);

        if (this.options.resizeNode){
            this.setContentHeightFun = this.setContentHeight.bind(this);
            this.container.addEvent("resize", this.setContentHeightFun);
            this.setContentHeightFun();
        }
    },
    loadLayout: function(){

        /**
         * @summary 视图的节点，mootools封装过的Dom对象，可以直接使用原生的js和moootools方法访问和操作该对象。
         * @see https://mootools.net/core/docs/1.6.0/Element/Element
         * @member {Element}
         * @example
         *  //可以在视图脚本中获取视图的Dom
         * var node = this.target.node;
         * @example
         *  //可以在视图的组件中获取视图的Dom(在视图的操作条组件中，分页事件中)
         * var node = this.target.view.node;
         */
        this.node = new Element("div", {"styles": this.css.node}).inject(this.container);
        /**
         * @summary 操作组件容器
         * @member {Element}
         */
        this.actionbarAreaNode =  new Element("div.actionbarAreaNode", {"styles": this.css.actionbarAreaNode}).inject(this.node);
        //if (this.options.export) this.exportAreaNode = new Element("div", {"styles": this.css.exportAreaNode}).inject(this.node);
        /**
         * @summary 搜索界面容器
         * @member {Element}
         */
        this.searchAreaNode = new Element("div", {"styles": this.css.searchAreaNode}).inject(this.node);
        /**
         * @summary 表头和条目容器，
         * @member {Element}
         */
        this.viewAreaNode = new Element("div", {"styles": this.css.viewAreaNode}).inject(this.node);
        // this.viewPageNode = new Element("div", {"styles": this.css.viewPageNode}).inject(this.node);
        /**
         * @summary 分页组件容器，
         * @member {Element}
         */
        this.viewPageAreaNode = new Element("div", {"styles": this.css.viewPageAreaNode}).inject(this.node);

        this.fireEvent("loadLayout");
    },
    loadResource: function( callback ){
        if( this.options.lazy ){
            var loadedIOResource = false;
            var callback2 = function(){
                if( this.Macro && loadedIOResource )callback();
            }.bind(this)
            this.loadMacro( callback2 );
            this.loadIOResource( function(){
                loadedIOResource = true;
                callback2();
            }.bind(this));
        }else{
            this.loadMacro( callback );
        }
    },
    loadMacro: function (callback) {
        MWF.require("MWF.xScript.Macro", function () {
            this.Macro = new MWF.Macro.ViewContext(this);
            if (callback) callback();
        }.bind(this));
    },
    loadLanguage: function(callback){
        if (this.viewJson.languageType!=="script"){
            if (callback) callback();
            return true;
        }

        var language = MWF.xApplication.query.Query.LP.form;
        var languageJson = null;

        if (this.viewJson.languageType=="script"){
            if (this.viewJson.languageScript ){
                languageJson = this.Macro.exec(this.viewJson.languageScript, this);
            }
        }

        if (languageJson){
            if (languageJson.then && o2.typeOf(languageJson.then)=="function"){
                languageJson.then(function(json) {
                    if (!json.data){
                        var o = Object.clone(json);
                        json.data = o;
                    }
                    MWF.xApplication.query.Query.LP.form = Object.merge(MWF.xApplication.query.Query.LP.form, json);
                    if (callback) callback(true);
                }, function(){
                    if (callback) callback(true);
                })
            }else{
                MWF.xApplication.query.Query.LP.form = Object.merge(MWF.xApplication.query.Query.LP.form, languageJson);
                if (callback) callback(true);
            }
        }else{
            if (callback) callback(true);
        }

    },
    setContentHeight: function(){
        var size;
        if( !this.node.offsetParent === null ){
            size = this.node.getStyle("height");
        }else{
            size = this.node.getSize()
        }
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

        var paddingTop = (this.viewAreaNode.getStyle("padding-top") || "0").toInt();
        var paddingBottom = (this.viewAreaNode.getStyle("padding-bottom") || "0").toInt();
        h = h - paddingTop - paddingBottom;

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
            /**
             * @summary 视图的操作条对象.
             * @member {Object}
             * @example
             * var actionbar = this.target.actionbar;
             */
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

        this.loadObserver();

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

        if( this.options.isloadContent )this.createLoadding();

        var viewTitleCellNode = (viewStyles && viewStyles["titleTd"]) ? viewStyles["titleTd"] : this.css.viewTitleCellNode;
        if (this.options.isloadTitle){
            this.viewTitleLine = new Element("tr.viewTitleLine", {
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
                    "styles": viewTitleCellNode,
                    "text": this.lp.sequence
                }).inject(this.viewTitleLine);
                this.sequenceTitleCell.setStyles({
                    "width": "30px",
                    "text-align": "center"
                });
                if (this.json.titleStyles) this.sequenceTitleCell.setStyles(this.json.titleStyles);
                if( !this.expandTitleCell )this.expandTitleCell = this.sequenceTitleCell;
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
            if( this.options.isloadContent )this.lookup(data, callback);
        }else{
            this.entries = {};
            this.viewJson.selectList.each(function(column){
                this.entries[column.column] = column;
                if (column.hideColumn) this.hideColumns.push(column.column);
                if (column.allowOpen) this.openColumns.push(column.column);
            }.bind(this));
            if( this.options.isloadContent )this.lookup(data, callback);
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
        if( this.viewJson.pagingbarHidden === true ){
            return;
        }
        if( !this.paging ){
            var json;

            if( !this.viewJson.pagingList || !this.viewJson.pagingList.length ){
                json = {
                    "firstPageText": this.lp.firstPage,
                    "lastPageText": this.lp.lastPage
                };
            }else{
                json = this.viewJson.pagingList[0];
                // var jsonStr = JSON.stringify(json);
                // jsonStr = o2.bindJson(jsonStr, {"lp": MWF.xApplication.query.Query.LP.form});
                // json = JSON.parse(jsonStr);
            }
            /**
             * @summary 视图的分页组件对象.
             * @member {Object}
             * @example
             * var paging = this.target.paging;
             */
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
                            if( this.options.defaultBundles.length && !this.isDefaultDataLoaded ){
                                this.loadDefaultData(function () {
                                    this.loadCurrentPageData( function () {
                                        this.fireEvent("postLoad"); //用户配置的事件
                                        this.lookuping = false;
                                        if(callback)callback(this);
                                    }.bind(this));
                                }.bind(this))
                            }else{
                                this.loadCurrentPageData( function () {
                                    this.fireEvent("postLoad"); //用户配置的事件
                                    this.lookuping = false;
                                    if(callback)callback(this);
                                }.bind(this));
                            }
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
                        this.fireEvent("loadView"); //options 传入的事件
                        this.fireEvent("postLoad"); //用户配置的事件
                        this.lookuping = false;
                        if(callback)callback(this);
                    }
                }.bind(this));
            }
        }.bind(this));
    },
    loadDefaultData: function( callback ){
        var d = {};
        d.bundleList = this.options.defaultBundles;
        d.key = this.bundleKey;
        this.lookupAction.loadView(this.json.name, this.json.application, d, function(json){
            var resultJson, viewData = json.data;

            if (this.viewJson.group.column){
                resultJson = [];
                json.data.groupGrid.each(function (g) {
                    resultJson = resultJson.concat( g.list );
                })
            }else{
                resultJson = json.data.grid;
            }

            resultJson.each(function (data) {
                this.selectedItems.push({
                    data: data
                })
            }.bind(this));

            this.isDefaultDataLoaded = true;
            if(callback)callback();
        }.bind(this), function () {
            this.isDefaultDataLoaded = true;
            if(callback)callback();
        }, true );
    },
    loadCurrentPageData: function( callback, async ){
        //是否需要在翻页的时候清空之前的items ?

        if( this.pageloading )return;
        this.pageloading = true;

        if( this.io ){
            this.items.each(function(item){
                this.io.unobserve(item.node);
            }.bind(this))
        }

        this.items = [];

        var p = this.currentPage;
        var d = {};
        var valueList = this.bundleItems.slice((p-1)*this.json.pageSize,this.json.pageSize*p);
        d.bundleList = valueList;
        d.key = this.bundleKey;

        while (this.viewTable.rows.length>1){
            this.viewTable.deleteRow(-1);
        }
        if( this.viewTable.rows.length>0 && !this.viewTable.rows[0].hasClass("viewTitleLine") ){
            this.viewTable.deleteRow(0);
        }

        this.contentAreaNode.scrollTo(0, 0);

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
    showAssociatedDocumentResult: function(failureList, successList){
        debugger;
        var fL = [];
        failureList.each(function( f ){
            if( f.properties.view === this.json.id )fL.push( f.targetBundle );
        }.bind(this));

        var sl = [];
        successList.each(function( f ){
            if( f.properties.view === this.json.id )sl.push( f.targetBundle );
        }.bind(this));

        var d = {};
        d.bundleList = fL.concat( sl );
        d.key = this.bundleKey;

        if( d.bundleList.length ){
            this.lookupAction.loadView(this.json.name, this.json.application, d, function(json){
                var resultJson, viewData = json.data;

                if (this.viewJson.group.column){
                    resultJson = [];
                    json.data.groupGrid.each(function (g) {
                        resultJson = resultJson.concat( g.list );
                    })
                }else{
                    resultJson = json.data.grid;
                }

                this._showAssociatedDocumentResult( resultJson, failureList, successList );

            }.bind(this), null, true );
        }else{
            this._showAssociatedDocumentResult( [], failureList, successList );
        }

    },
    _showAssociatedDocumentResult: function(resultJson, failureList, successList){
        this.entries.$result = {
            "id": "$result",
            "column": "$result",
            "events": {},
            "allowOpen": false,
            "numberOrder": false,
            "groupEntry": false,
            "hideColumn": false,
            "isName": false,
            "isHtml": false,
            "path": "$result"
        };

        var viewStyles = this.viewJson.viewStyles;
        if (this.options.isloadTitle && this.selectTitleCell){
            //if (this.json.select==="single" || this.json.select==="multi") {
            var titleCell = new Element("td.titleCell", {
                "styles": (viewStyles && viewStyles["titleTd"]) ? viewStyles["titleTd"] : this.css.viewTitleCellNode,
                "text": this.lp.associationResult
            }).inject(this.viewTitleLine);
            if (this.json.titleStyles) titleCell.setStyles(this.json.titleStyles);
        }

        if(this.expandAllNode){
            this.expandAllNode.hide();
        }

        while (this.viewTable.rows.length>1){
            this.viewTable.deleteRow(-1);
        }
        if( this.viewTable.rows.length>0 && !this.viewTable.rows[0].hasClass("viewTitleLine") ){
            this.viewTable.deleteRow(0);
        }

        this.contentAreaNode.scrollTo(0, 0);

        this.gridJson = resultJson.map(function (item) {
            var flag = true;
            if( !item.data )item.data = {};
            failureList.each(function (d) {
                if( item.bundle === d.bundle ){
                    item.$failure = true;
                    item.data.$result = d.$result || this.lp.noPermission;
                    flag = false;
                }
            }.bind(this));
            if( flag ){
                item.data.$result = this.lp.associationSuccess;
            }
            item.$selectedEnable = false;
            return item;
        }.bind(this));
        //if( this.paging )this.paging.hide();
        if(this.actionbarAreaNode)this.actionbarAreaNode.hide();
        if(this.searchAreaNode)this.searchAreaNode.hide();
        if(this.viewPageAreaNode)this.viewPageAreaNode.hide();

        //this.selectedItems = [];

        if(this.selectAllNode){
            this.clearSelectAllStyle();
        }

        if (this.gridJson.length){
            this.gridJson.each(function(line, i){
                new MWF.xApplication.query.Query.Viewer.AssociatedResultItem(this, line, null, i, null, false);
            }.bind(this));
        }else{
            if (this.viewPageAreaNode) this.viewPageAreaNode.empty();
        }
    },
    setSelectedableFlag : function(){
        this.viewSelectedEnable = false;
        var selectedAbleScript = this.json.selectedAbleScript || this.viewJson.selectedAbleScript;
        if (this.viewJson.group.column){
            this.gridJson.each( function( d ){
                d.list.each( function( v ){
                    switch (typeOf(selectedAbleScript)) {
                        case "string":
                            if( selectedAbleScript ){
                                v.$selectedEnable = this.Macro.exec( selectedAbleScript, { "data" : v, "groupData" : d ,"view": this });
                            }else{
                                v.$selectedEnable = true;
                            }
                            break;
                        case "function":
                            v.$selectedEnable =  selectedAbleScript({ "data" : v, "groupData" : d ,"view": this });
                            break;
                        default:
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
                switch (typeOf(selectedAbleScript)) {
                    case "string":
                        if( selectedAbleScript ){
                            v.$selectedEnable = this.Macro.exec( selectedAbleScript, { "data" : v, "view": this });
                        }else{
                            v.$selectedEnable = true;
                        }
                        break;
                    case "function":
                        v.$selectedEnable =  selectedAbleScript({ "data" : v, "view": this });
                        break;
                    default:
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
                this.items.push(new MWF.xApplication.query.Query.Viewer.Item(this, line, null, i, null, this.options.lazy));
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
                this.items.push(new MWF.xApplication.query.Query.Viewer.Item(this, this.gridJson[i], null, i, null, this.options.lazy));
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
    bindLP: function( json ){
        var jsonStr = JSON.stringify( json );
        jsonStr = o2.bindJson(jsonStr, {"lp": MWF.xApplication.query.Query.LP.form});
        return JSON.parse(jsonStr);
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
    /**
     * @summary 刷新视图。
     *  @param {Function} [callback] - 可选，刷新视图后的回调.
     *  @example
     *  this.target.reload();
     */
    reload: function( callback ){
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
        this.createViewNode({"filterList": this.json.filter ? this.json.filter.clone() : null}, callback);
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
        if( this.viewJson.searchbarHidden === true ){
            return;
        }
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
            var first = (this.options.isloadTitle) ? 1 : 0;
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
                var filterData = [];
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
                                "comparison": "equals"
                            };
                            filterData.push(d);
                            this.filterItems.push({"data":d});
                        }
                    }
                }.bind(this));

                if( this.json.filter ){
                    this.json.filter.clone().each(function(f){
                        filterData.push(f);
                    })
                }

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
                    this.loadComparisonSelect(this.lp.dateTimeFilter);
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
                    this.loadComparisonSelect(this.lp.dateTimeFilter);
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
                "target": this.app ? this.app.content : this.container,
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
                "target": this.app ? this.app.content : this.container,
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
                "target": this.app ? this.app.content : this.container,
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

    /**
     * @summary 如果当前视图是嵌入在表单或者页面中，使用该方法获取表单或页面的上下文。
     * @see {@link module:queryView.getParentEnvironment|详情查看 this.queryViewer.getParentEnvironment}
     * @return {Object}
     * @example
     *  this.target.getParentEnvironment();
     */
    getParentEnvironment : function(){
        return this.parentMacro ? this.parentMacro.environment : null;
    },
    /**
     * @summary 获取视图的配置信息。
     * @see {@link module:queryView.getViewInfor|详情查看 this.queryViewer.getViewInfor}
     * @return {Object}
     * @example
     *  this.target.getViewInfor();
     */
    getViewInfor : function(){
        return this.json;
    },
    /**
     * @summary 获取视图当前页的基本信息。
     * @see {@link module:queryView.getPageInfor|详情查看 this.queryViewer.getPageInfor}
     * @return {Object}
     * @example
     *  this.target.getPageInfor();
     */
    getPageInfor : function(){
        return {
            pages : this.pages,
            perPageCount : this.options.perPageCount,
            currentPageNumber : this.currentPage
        };
    },
    /**
     * @summary 获取当前页的数据。
     * @see {@link module:queryView.getPageData|详情查看 this.queryViewer.getPageData}
     * @return {Object}
     * @example
     *  this.target.getPageData();
     */
    getPageData : function () {
        return this.gridJson;
    },
    /**
     * @summary 跳转到指定的页面。
     * @see {@link module:queryView.toPage|详情查看 this.queryViewer.toPage}
     * @param {Number} pageNumber 需要跳转的页码
     * @param {Function} callback 跳转的页面数据加载完成以后的回调方法。
     * @example
     *  //　跳转到第2页并且获取该页的数据。
     *  this.target.toPage( 2, function(){
     *      var data = this.target.getPageData();
     *  }.bind(this) )
     */
    toPage : function( pageNumber, callback ){
        this.currentPage = pageNumber;
        this.loadCurrentPageData( callback );
    },
    /**
     * 获取选中的条目的数据。
     * @method getSelectedData
     * @see {@link module:queryView.getSelectedData|详情查看 this.queryViewer.getSelectedData}
     * @memberOf module:queryView
     * @static
     * @return {Object[]} 选中的条目的数据。
     * <div>格式如下：</div>
     * <pre><code class='language-js'>
     * [
     {
        "bundle": "099ed3c9-dfbc-4094-a8b7-5bfd6c5f7070", //cms 的 documentId, process 的 jobId
        "data": {  //视图中配置的数据
          "title": "考勤管理-配置-统计周期设置", //列名称及列值
          "time": "2018-08-25 11:29:45"
        }
      },
     ...
     * ]
     </code></pre>
     * @o2syntax
     * var data = this.target.getSelectedData();
     */
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

    /**
     * @summary 设置视图的过滤条件，该方法不能修改视图中默认的过滤条件（在开发视图的时候添加的过滤条件），而是在这上面新增。
     * @see {@link module:queryView.setFilter|详情查看 this.queryViewer.setFilter}
     * @param {(ViewFilter[]|ViewFilter|Null)} [filter] 过滤条件
     * @param {Function} callback 过滤完成并重新加载数据后的回调方法。
     */
    setFilter : function( filter, callback ){
        if( this.lookuping || this.pageloading )return;
        if( !filter )filter = [];
        if( typeOf( filter ) === "object" )filter = [ filter ];
        this.json.filter = filter;
        if( this.viewAreaNode ){
            this.createViewNode({"filterList": this.json.filter  ? this.json.filter.clone() : null}, callback);
        }
    },
    /**
     * @summary 把当前视图切换成另外一个视图。
     * @see {@link module:queryView.switchView|详情查看 this.queryViewer.switchView}
     * @param {(ViewFilter[]|ViewFilter|Null)} [filter] 过滤条件
     * @param {Object} options 需要跳转的参数配置
     */
    switchView : function( json ){
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
        this.paging = null;

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
    },
    dialog: function( options ){
        if( !options )options = {};
        var opts = {
            "style" : options.style || "user",
            "title": options.title || "",
            "width": options.width || 300,
            "height" : options.height || 150,
            "isMax": o2.typeOf( options.isMax ) === "boolean" ? options.isMax : false,
            "isClose": o2.typeOf( options.isClose ) === "boolean"  ? options.isClose : true,
            "isResize": o2.typeOf( options.isResize ) === "boolean"  ? options.isResize : true,
            "isMove": o2.typeOf( options.isMove ) === "boolean"  ? options.isMove : true,
            "isTitle": o2.typeOf( options.isTitle ) === "boolean"  ? options.isTitle : true,
            "offset": options.offset || null,
            "mask": o2.typeOf( options.mask ) === "boolean"  ? options.mask : true,
            "container": options.container ||  ( layout.mobile ? $(document.body) : this.app.content ),
            "duration": options.duration || 200,
            "lp": options.lp || null,
            "zindex": ( options.zindex || 100 ).toInt(),
            "buttonList": options.buttonList || [
                {
                    "type": "ok",
                    "text": MWF.LP.process.button.ok,
                    "action": function(){
                        if(options.ok){
                            var flag = options.ok.call( this );
                            if( flag === true || o2.typeOf(flag) === "null" )this.close();
                        }else{
                            this.close();
                        }

                    }
                },
                {
                    "type": "cancel",
                    "text": MWF.LP.process.button.cancel,
                    "action": function(){
                        if(options.close){
                            var flag = options.close.call(this);
                            if( flag === true || o2.typeOf(flag) === "null" )this.close();
                        }else{
                            this.close();
                        }
                    }
                }
            ]
        };

        var positionNode;
        if( options.content ) {
            opts.content = options.content;
            var parent = opts.content.getParent();
            if(parent)positionNode = new Element("div", {style:"display:none;"}).inject( opts.content, "before" );
        }

        opts.onQueryClose = function(){
            if( positionNode && opts.content ){
                opts.content.inject( positionNode, "after" );
                positionNode.destroy();
            }
            if( o2.typeOf(options.onQueryClose) === "function" )options.onQueryClose.call( this );
        }

        for( var key in options ){
            if( !opts.hasOwnProperty( key ) ){
                opts[key] = options[key];
            }
        }
        var dialog;
        MWF.require("MWF.xDesktop.Dialog", function(){
            dialog = o2.DL.open(opts)
        }, null, false);
        return dialog;
    },

    //api 使用 结束

    loadObserver: function(){
        if( this.io ){
            this.io.disconnect();
            this.io = null;
        }
        if( !this.options.lazy )return;
        if (typeof window !== 'undefined' && window.IntersectionObserver) {
            this.io = new IntersectionObserver( function (entries, observer) {
                entries.forEach(function (entry) {
                    if (entry.intersectionRatio > 0 || entry.isIntersecting) {
                        var item = entry.target.retrieve("item");
                        if( item && !item.loading && !item.loaded){ //已经加载完成
                            item.active();
                        }
                        observer.unobserve(entry.target);
                    }
                })
            }, {
                root: this.contentAreaNode,
                rootMargin: "10px 0px 0px 0px"
            });
        }
    },
    loadIOResource: function (callback) {
        if( !this.options.lazy ){
            callback();
            return;
        }
        var observerPath = "../o2_lib/IntersectionObserver/intersection-observer.min.js";
        var observerPath_ie11 = "../o2_lib/IntersectionObserver/polyfill_ie11.min.js";
        if( window.IntersectionObserver && window.MutationObserver) {
            if(callback)callback();
        }else if( !!window.MSInputMethodContext && !!document.documentMode ){ //ie11
            o2.load(observerPath_ie11, function () { callback(); }.bind(this));
        }else{
            o2.load([observerPath, observerPath_ie11], function () { callback(); }.bind(this));
        }
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
        getExportTotalCount: function(){
            return ( this.bundleItems || [] ).length;
        },
        getExportMaxCount: function(){
            return 2000;
        },
        exportView: function(){
            var _self = this;
            var total = this.getExportTotalCount();
            var max = this.getExportMaxCount();

            var lp = this.lp.viewExport;
            var node = this.exportExcelDlgNode = new Element("div");
            var html = "<div style=\"line-height: 30px; height: 30px; color: #333333; overflow: hidden;margin-top:20px;\">" + lp.fileName + "：" +
                "   <input class='filename' value='' style='margin-left: 14px;width: 350px;'><span>"+
                "</div>";
            html += "<div style=\"line-height: 30px; height: 30px; color: #333333; overflow: hidden;margin-top:20px;\">" + lp.exportRange + "：" +
                "   <input class='start' value='" + ( this.exportExcelStart || 1) +  "'><span>"+ lp.to +"</span>" +
                "   <input class='end' value='"+ ( this.exportExcelEnd || Math.min( total, max ) ) +"' ><span>"+lp.item+"</span>" +
                "</div>";
            html += "<div style=\"clear:both; max-height: 300px; margin-bottom:10px; margin-top:10px; overflow-y:auto;\">"+( lp.description.replace("{count}", total ))+"</div>";
            node.set("html", html);
            var check = function () {
                if(this.value.length == 1){
                    this.value = this.value.replace(/[^1-9]/g,'')
                }else{
                    this.value = this.value.replace(/\D/g,'')
                }
                if( this.value.toInt() > total ){
                    this.value = total;
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
                            var filename = node.getElement(".filename").get("value");
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
                            this.exportExcelStart = start;
                            this.exportExcelEnd = end;
                            this._exportView(start, end, filename);
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
        // _exportView: function(start, end, filename){
        //
        //     var bundleList = this.bundleItems.slice(start-1, end);
        //     var excelName = filename || (this.json.name + "(" + start + "-" + end + ").xlsx");
        //
        //     var action = MWF.Actions.get("x_query_assemble_surface");
        //
        //     var filterData = this.json.filter ? this.json.filter.clone() : [];
        //     if (this.filterItems.length){
        //         this.filterItems.each(function(filter){
        //             filterData.push(filter.data);
        //         }.bind(this));
        //     }
        //     var data = {"filterList": filterData};
        //     if( bundleList )data.bundleList = bundleList;
        //     if( excelName )data.excelName = excelName;
        //     data.key = this.bundleKey;
        //     action.exportViewWithQuery(this.json.viewName, this.json.application, data, function(json){
        //         var uri = action.action.actions.getViewExcel.uri;
        //         uri = uri.replace("{flag}", json.data.id);
        //         uri = o2.filterUrl( action.action.address+uri );
        //         var a = new Element("a", {"href": uri, "target":"_blank"});
        //         a.click();
        //         a.destroy();
        //     }.bind(this));
        // },
        _exportView: function(start, end, filename){
            var excelName = filename || (this.json.name + "(" + start + "-" + end + ").xlsx");

            // var p = this.currentPage;
            // var d = {
            //     "filterList": this.filterList,
            //     "parameter": this.parameter
            // };

            this.createLoadding();

            var exportArray = [];

            var titleArray = [];
            var colWidthArr = [];
            var dateIndexArray = [];
            var numberIndexArray = [];
            var totalArray = [];
            var idx = 0;

            if (this.viewJson.isSequence === "yes") {
                titleArray.push( this.lp.sequence );
                colWidthArr.push(100);
                totalArray.push('');
                idx = idx + 1;
            }

            Object.each(this.entries, function (c, k) {
                if (this.hideColumns.indexOf(k) === -1 && c.exportEnable !== false) {
                    titleArray.push(c.displayName);
                    colWidthArr.push(c.exportWidth || 200);
                    if( c.isTime )dateIndexArray.push(idx);
                    if( c.isNumber )numberIndexArray.push(idx);
                    totalArray.push( ['number', 'count'].contains(c.total) ? new Decimal(0) : '' );
                    idx++;
                }
            }.bind(this));
            exportArray.push(titleArray);

            this.loadExportData(start, end, function (dataList) {
                var rowIndex = 0;
                dataList.grid.each(function (data, i) {
                    // data.each(function (d, i) {
                    var d = data.data;

                        rowIndex = rowIndex + 1;

                        var columnIndex = 0;

                        var dataArray = [];
                        if (this.viewJson.isSequence === "yes") {
                            dataArray.push( (start-1)+rowIndex );
                            columnIndex++;
                        }
                        Object.each(this.entries, function (c, k) {
                            if (this.hideColumns.indexOf(k) === -1 && c.exportEnable !== false) {
                                var text = this.getExportText(c, k, d);
                                dataArray.push( text );
                                switch (c.total){
                                    case 'number':
                                        if( parseFloat(text).toString() !== "NaN" ) { //可以转成数字
                                            totalArray[columnIndex] = totalArray[columnIndex].plus(text);
                                        }
                                        break;
                                    case 'count':
                                        totalArray[columnIndex] = totalArray[columnIndex].plus(1);
                                        break;
                                }

                                columnIndex++;
                            }
                        }.bind(this));
                        //exportRow事件
                        var argu = {"index":rowIndex, "source": d, "data":dataArray};
                        this.fireEvent("exportRow", [argu]);
                        exportArray.push( argu.data || dataArray );
                    // }.bind(this));
                }.bind(this));

                var hasTotal = false;
                totalArray = totalArray.map(function (d){
                    if( d ){
                        hasTotal = true;
                        return d.toString();
                    }else{
                        return '';
                    }
                });

                if( hasTotal ){
                    totalArray[0] = MWF.xApplication.query.Query.LP.total + " " + totalArray[0];
                    exportArray.push( totalArray );
                }


                var headTextScript = this.viewJson.exportHeadText;
                var headText = headTextScript ? this.Macro.exec(headTextScript, this) : '';

                var headStyleScript = this.viewJson.exportHeadStyle;
                var headStyle = headStyleScript ? this.Macro.exec(headStyleScript, this) : null;

                var titleStyleScript = this.viewJson.exportColumnTitleStyle;
                var titleStyle = titleStyleScript ? this.Macro.exec(titleStyleScript, this) : null;

                var contentStyleScript = this.viewJson.exportColumnContentStyle;
                var contentStyle = contentStyleScript ? this.Macro.exec(contentStyleScript, this) : null;

                //export事件
                var arg = {
                    headText: headText,
                    headStyle: headStyle,
                    titleStyle: titleStyle,
                    contentStyle: contentStyle,
                    data : exportArray,
                    colWidthArray : colWidthArr,
                    title : excelName
                };
                this.fireEvent("export", [arg]);

                if (this.loadingAreaNode) {
                    this.loadingAreaNode.destroy();
                    this.loadingAreaNode = null;
                }

                var options = {};
                if( arg.headText )options.headText = arg.headText;
                if( arg.headStyle )options.headStyle = arg.headStyle;
                if( arg.titleStyle )options.columnTitleStyle = arg.titleStyle;
                if( arg.contentStyle )options.columnContentStyle = arg.contentStyle;

                new MWF.xApplication.query.Query.Viewer.ExcelUtils(
                    options
                ).exportToExcel(
                    arg.data || exportArray,
                    arg.title || excelName,
                    arg.colWidthArray || colWidthArr,
                    dateIndexArray,  //日期格式列下标
                    numberIndexArray  //数字格式列下标
                );

            }.bind(this))
        },
        loadExportData: function(start, end, callback){

            var bundleList = this.bundleItems.slice(start-1, end);

            var filterData = this.json.filter ? this.json.filter.clone() : [];
            if (this.filterItems.length){
                this.filterItems.each(function(filter){
                    filterData.push(filter.data);
                }.bind(this));
            }
            var data = {"filterList": filterData};
            if( bundleList )data.bundleList = bundleList;
            data.key = this.bundleKey;

            var p = o2.Actions.load("x_query_assemble_surface").ViewAction.executeWithQuery(
                this.json.viewName,
                this.json.application,
                data
            );
            Promise.resolve( p ).then(function (json) {
                callback(json.data);
            });
        },
        getExportText: function(column, key, data){
            var text = data[key];
            switch (typeOf(text)){
                case 'string':
                    return text;
                case 'array':
                    return text.join(',');
                default:
                    return text;
            }
        }

});


/** @classdesc ViewerItem 数据中心的视图条目。本章节的脚本上下文请看<b>{@link module:queryView|queryView}。</b>
 * @class
 * @o2cn 视图条目（行）
 * @o2category QueryView
 * @o2range {QueryView}
 * @hideconstructor
 * @example
 * //在视图中获取行
 * var item = this.target.items[0];
 */
MWF.xApplication.query.Query.Viewer.Item = new Class(
    /** @lends MWF.xApplication.query.Query.Viewer.Item# */
    {
    initialize: function(view, data, prev, i, category, lazy){
        /**
         * 加载对应列的每个单元格后触发。可通过this.target获取以下对象：
         * <pre><code class='language-js'>{
         *  "json ": {}, //当前行配置
         *  "data": "",  //当前单元格数据，可能是字符串、数组、布尔值。
         *  "node": td, //当前单元格
         *  "view": view, //当前视图对象
         *  "row": {} //当前行的平台类对象
         * }</code></pre>
         * @event MWF.xApplication.query.Query.Viewer.Item#loadContent
         */
        /**
         * 加载对应列的每个单元格后触发。可通过this.target获取以下对象：
         * <pre><code class='language-js'>{
         *  "json ": {}, //当前行配置
         *  "data": "",  //当前单元格数据，可能是字符串、数组、布尔值。
         *  "node": td, //当前单元格
         *  "view": view, //当前视图对象
         *  "row": {} //当前行的平台类对象
         * }</code></pre>
         * @event MWF.xApplication.query.Query.Viewer.Item#click
         */

        /**
         * @summary 行所属视图.
         * @member {Object}
         */
        this.view = view;
        /**
         * @summary 行数据.
         * @member {Object}
         */
        this.data = data;
        this.css = this.view.css;
        /**
         * @summary 行是否被选中.
         * @member {Boolean}
         */
        this.isSelected = false;
        /**
         * @summary 如果视图有分类，获取分类对象。
         * @member {Object}
         */
        this.category = category;
        this.prev = prev;
        this.idx = i;
        this.clazzType = "item";
        this.lazy = lazy;
        this.odd = this.view.items.length % 2 === 1;
        this.load();
    },
    load: function(){
        if( this.lazy && this.view.io ){
            this.view.fireEvent("queryLoadItemRow", [this]);
            this.loadNode();
            this.view.io.observe( this.node );
        }else{
            this._load();
        }
    },
    active: function(){
        if( !this.loaded && !this.loading ){
            this._load();
        }
    },
    loadNode: function(){
        var viewStyles = this.view.viewJson.viewStyles;
        var trStyle = ( viewStyles && viewStyles["contentTr"] ) ? viewStyles["contentTr"] : this.css.viewContentTrNode;

        this.node = new Element("tr", {
            "styles": trStyle
        });
        if (this.prev){
            this.node.inject(this.prev.node, "after");
        }else{
            this.node.inject(this.view.viewTable);
        }
        this.node.store("item", this);
        if( this.lazy && this.view.io ) {
            var viewContentTdNode = ( viewStyles && viewStyles["contentTd"] ) ? viewStyles["contentTd"] : this.css.viewContentTdNode;
            this.placeholderTd = new Element("td", {
                "styles": viewContentTdNode
            }).inject(this.node)
        }
    },
    _load: function(){
        this.loading = true;

        if(!this.node)this.view.fireEvent("queryLoadItemRow", [this]);

        var viewStyles = this.view.viewJson.viewStyles;
        var viewContentTdNode = ( viewStyles && viewStyles["contentTd"] ) ? viewStyles["contentTd"] : this.css.viewContentTdNode;

        if( this.odd ){
            viewContentTdNode = ( viewStyles && viewStyles["zebraContentTd"] && Object.keys(viewStyles["zebraContentTd"].length > 0)) ? viewStyles["zebraContentTd"] : viewContentTdNode;
        }
        if(!this.node)this.loadNode();

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
        var sequence = 1+this.view.json.pageSize*(this.view.currentPage-1)+this.idx;
        this.data["$sequence"] = sequence;
        if (this.view.viewJson.isSequence==="yes"){
            this.sequenceTd = new Element("td", {"styles": viewContentTdNode}).inject(this.node);
            this.sequenceTd.setStyles({
                "width": "30px",
                "text-align": "center"
            });
            if (this.view.json.itemStyles) this.sequenceTd.setStyles(this.view.json.itemStyles);
            this.sequenceTd.set("text", sequence);
        }

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

        if(this.placeholderTd){
            this.placeholderTd.destroy();
            this.placeholderTd = null;
        }

        //默认选中
        var selectedFlag;
        var defaultSelectedScript = this.view.json.defaultSelectedScript || this.view.viewJson.defaultSelectedScript;
        if( !this.isSelected && defaultSelectedScript ){
            // var flag = this.view.json.select || this.view.viewJson.select ||  "none";
            // if ( flag ==="single" || flag==="multi"){
            //
            // }
            switch (typeOf(defaultSelectedScript)) {
                case "string":
                    selectedFlag = this.view.Macro.exec( defaultSelectedScript,
                        {"node" : this.node, "data" : this.data, "view": this.view, "row" : this});
                    break;
                case "function":
                    selectedFlag =  defaultSelectedScript({"node" : this.node, "data" : this.data, "view": this.view, "row" : this});
                    break;
            }
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

        this.view.fireEvent("postLoadItemRow", [this]);

        this.loading = false;
        this.loaded = true;
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
        var options = {"jobId": this.data.bundle};
        this.view.fireEvent("openDocument", [options, this]); //options 传入的事件
        layout.desktop.openApplication(e, "process.Work", options);

        // MWF.Actions.get("x_processplatform_assemble_surface").listWorkByJob(this.data.bundle, function(json){
        //     var workCompletedCount = json.data.workCompletedList.length;
        //     var workCount = json.data.workList.length;
        //     var count = workCount+workCompletedCount;
        //     if (count===1){
        //         if (workCompletedCount) {
        //             this.openWorkCompleted(json.data.workCompletedList[0].id, e);
        //         }else{
        //             this.openWork(json.data.workList[0].id, e);
        //         }
        //     }else if (count>1){
        //         var worksAreaNode = this.createWorksArea();
        //         json.data.workCompletedList.each(function(work){
        //             this.createWorkCompletedNode(work, worksAreaNode);
        //         }.bind(this));
        //         json.data.workList.each(function(work){
        //             this.createWorkNode(work, worksAreaNode);
        //         }.bind(this));
        //         this.showWorksArea(worksAreaNode, e);
        //     }else{
        //
        //     }
        // }.bind(this));
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
    },

    /**
     * @summary 选中（多选）。
     * @example
     *  item = this.target.items[0];
     *  item.selected();
     */
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
        this.view.fireEvent("select", [{
            "selected": true,
            "item": this,
            "data": this.data
        }]); //options 传入的事件
    },

    /**
     * @summary 取消选中（多选）。
     * @example
     *  item = this.target.items[0];
     *  item.unSelected();
     */
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
        this.view.fireEvent("unselect", [{
            "selected": false,
            "item": this,
            "data": this.data
        }]); //options 传入的事件
    },

    /**
     * @summary 选中（单选）。
     * @example
     *  item = this.target.items[0];
     *  item.selectedSingle();
     */
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
        this.view.fireEvent("select", [{
            "selected": true,
            "item": this,
            "data": this.data
        }]); //options 传入的事件
    },

    /**
     * @summary 取消选中（单选）。
     * @example
     *  item = this.target.items[0];
     *  item.unSelectedSingle();
     */
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
        this.view.fireEvent("unselect", [{
            "selected": false,
            "item": this,
            "data": this.data
        }]); //options 传入的事件
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
        this.view.fireEvent("queryLoadCategoryRow", [this]);

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

        this.view.fireEvent("postLoadCategoryRow", [this]);
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
                this.lastItem = new MWF.xApplication.query.Query.Viewer.Item(this.view, line, (this.lastItem || this), s, this, this.view.options.lazy);
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

/** @class Actionbar 视图操作条组件。
 * @o2cn 视图操作条
 * @example
 * //可以在视图中获取该组件
 * var actionbar = this.target.actionbar; //在视图中获取操作条
 * //方法2
 * var actionbar = this.target; //在操作条和操作本身的事件脚本中获取
 * @o2category QueryView
 * @o2range {QueryView}
 * @hideconstructor
 */
MWF.xApplication.query.Query.Viewer.Actionbar = new Class(
    /** @lends MWF.xApplication.query.Query.Viewer.Actionbar# */
    {
    Implements: [Events],
    options: {
        "style" : "default",
        "moduleEvents": [
            /**
             * 视图操作条加载前触发。
             * @event MWF.xApplication.query.Query.Viewer.Actionbar#queryLoad
             */
            "queryLoad",
            /**
             * 视图加载时触发。
             * @event MWF.xApplication.query.Query.Viewer.Actionbar#load
             */
            "load",
            /**
             * 视图操作条加载后事件.由于加载过程中有异步处理，这个时候操作条有可能还未生成。
             * @event MWF.xApplication.query.Query.Viewer.Actionbar#postLoad
             */
            "postLoad",
            /**
             * 视图操作条加载后事件。这个时候操作条已生成。
             * @event MWF.xApplication.query.Query.Viewer.Actionbar#afterLoad
             */
            "afterLoad"
        ]
    },
    initialize: function(node, json, form, options){
        /**
         * @summary 操作条组件容器.
         * @member {Element}
         */
        this.node = $(node);
        this.node.store("module", this);
        /**
         * @summary 操作条组件配置数据.
         * @member {Object}
         */
        this.json = json;
        this.form = form;
        /**
         * @summary 操作条组件所属视图.
         * @member {Object}
         */
        this.view = form;
    },
    /**
     * @summary 隐藏操作条。
     */
    hide: function(){
        var dsp = this.node.getStyle("display");
        if (dsp!=="none") this.node.store("mwf_display", dsp);
        this.node.setStyle("display", "none");
    },
    /**
     * @summary 显示操作条。
     */
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
                // var jsonStr = JSON.stringify(this.json.multiTools);
                // jsonStr = o2.bindJson(jsonStr, {"lp": MWF.xApplication.query.Query.LP.form});
                // this.json.multiTools = JSON.parse(jsonStr);
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
                        "MWFButtonImage": path+""+this.options.style+"/custom/"+iconPath+tool.img,
                        "title": tool.title,
                        "MWFButtonAction": "runCustomAction",
                        "MWFButtonText": tool.text
                    }).inject(node);
                    if( this.json.customIconOverStyle ){
                        actionNode.set("MWFButtonImageOver" , path+""+this.options.style +"/custom/"+this.json.customIconOverStyle+ "/" +tool.img );
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

/** @class Actionbar 视图分页组件。
 * @o2cn 视图分页组件
 * @example
 * //可以在视图中获取该组件
 * var actionbar = this.target.paging; //在视图中获取操作条
 * //方法2
 * var actionbar = this.target; //在分页组件本身的事件脚本中获取
 * @o2category QueryView
 * @o2range {QueryView}
 * @hideconstructor
 */
MWF.xApplication.query.Query.Viewer.Paging = new Class(
    /** @lends MWF.xApplication.query.Query.Viewer.Paging# */
    {
    Implements: [Options, Events],
    options: {
        "style" : "default",
        "useMainColor": false,
        "moduleEvents": [
            /**
             * 分页加载前触发。
             * @event MWF.xApplication.query.Query.Viewer.Paging#queryLoad
             */
            "queryLoad",
            /**
             * 分页加载时触发。
             * @event MWF.xApplication.query.Query.Viewer.Paging#load
             */
            "load",
            /**
             * 分页加载后事件.由于加载过程中有异步处理，这个时候分页组件有可能还未生成。
             * @event MWF.xApplication.query.Query.Viewer.Paging#postLoad
             */
            "postLoad",
            /**
             * 分页加载后事件。这个时候分页界面已生成。
             * @event MWF.xApplication.query.Query.Viewer.Paging#afterLoad
             */
             "afterLoad",
            /**
             * 跳页或者分页后执行。
             * @event MWF.xApplication.query.Query.Viewer.Paging#jump
             */
             "jump"
        ]
    },
    initialize: function(node, json, form, options){
        this.setOptions(options);
        /**
         * @summary 分页组件容器.
         * @member {Element}
         */
        this.node = $(node);
        this.node.store("module", this);
        /**
         * @summary 分页组件所属视图.
         * @member {Object}
         */
        this.json = json;
        this.form = form;
        /**
         * @summary 分页所属视图.
         * @member {Object}
         */
        this.view = form;
    },
    /**
     * @summary 隐藏分页。
     */
    hide: function(){
        var dsp = this.node.getStyle("display");
        if (dsp!=="none") this.node.store("mwf_display", dsp);
        this.node.setStyle("display", "none");
    },
    /**
     * @summary 显示分页。
     */
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
            hasInfor: this.json.showPagingInfor,
            inforPosition: this.json.pagingPosition,
            inforTextStyle: this.json.textStyle,
            useMainColor: this.options.useMainColor,
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


MWF.xApplication.query.Query.Viewer.AssociatedResultItem = new Class({
    Extends: MWF.xApplication.query.Query.Viewer.Item,
    _load: function(){
        this.loading = true;

        if(!this.node)this.view.fireEvent("queryLoadItemRow", [this]);

        var viewStyles = this.view.viewJson.viewStyles;
        var viewContentTdNode = ( viewStyles && viewStyles["contentTd"] ) ? viewStyles["contentTd"] : this.css.viewContentTdNode;

        if(!this.node)this.loadNode();

        //if (this.view.json.select==="single" || this.view.json.select==="multi"){
        this.selectTd = new Element("td", { "styles": viewContentTdNode }).inject(this.node);
        if (this.view.json.itemStyles) this.selectTd.setStyles(this.view.json.itemStyles);
        this.selectTd.setStyles({"cursor": "default"});
        if( this.data.$failure ){
            this.selectTd.setStyles({"background": "url(" + "../x_component_query_Query/$Viewer/default/icon/" + "error" + ".png) center center no-repeat"});
        }else{
            this.selectTd.setStyles({"background": "url(" + "../x_component_query_Query/$Viewer/default/icon/" + "success" + ".png) center center no-repeat"});
        }

        //序号
        var sequence = 1+this.idx;
        this.data["$sequence"] = sequence;
        if (this.view.viewJson.isSequence==="yes"){
            this.sequenceTd = new Element("td", {"styles": viewContentTdNode}).inject(this.node);
            this.sequenceTd.setStyles({
                "width": "30px",
                "text-align": "center"
            });
            this.sequenceTd.set("text", sequence);
        }

        Object.each(this.view.entries, function(c, k){
            var cell = this.data.data[k];
            if (cell === undefined) cell = "";
            //if (cell){
            if (this.view.hideColumns.indexOf(k)===-1){
                var td = new Element("td", {"styles": viewContentTdNode}).inject(this.node);
                //if (k!== this.view.viewJson.group.column){
                    var v = cell;
                    if (c.isHtml){
                        td.set("html", v);
                    }else{
                        td.set("text", v);
                    }
                    if( typeOf(c.contentProperties) === "object" )td.setProperties(c.contentProperties);
                    if (this.view.json.itemStyles) td.setStyles(this.view.json.itemStyles);
                    if( typeOf(c.contentStyles) === "object" )td.setStyles(c.contentStyles);
                // }else{
                //     if (this.view.json.itemStyles) td.setStyles(this.view.json.itemStyles);
                // }

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

        if(this.placeholderTd){
            this.placeholderTd.destroy();
            this.placeholderTd = null;
        }

        //默认选中
        //判断是不是在selectedItems中，用户手工选择

        this.setEvent();

        this.view.fireEvent("postLoadItemRow", [this]);

        this.loading = false;
        this.loaded = true;
    },
})


MWF.xDesktop.requireApp("Template", "utils.ExcelUtils", null, false);
MWF.xApplication.query.Query.Viewer.ExcelUtils = new Class({
    Extends: MWF.xApplication.Template.utils.ExcelUtils
});
