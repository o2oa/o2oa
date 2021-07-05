MWF.xApplication.Template = MWF.xApplication.Template || {};
MWF.xApplication.Template.Explorer = MWF.xApplication.Template.Explorer || {};

MWF.require("MWF.widget.O2Identity", null, false);
//MWF.xDesktop.requireApp("Template", "lp." + MWF.language, null, false);

String.implement({

    toDOM: function( container, callback ){
        var wrapper =	this.test('^<the|^<tf|^<tb|^<colg|^<ca') && ['<table>', '</table>', 1] ||
            this.test('^<col') && ['<table><colgroup>', '</colgroup><tbody></tbody></table>',2] ||
            this.test('^<tr') && ['<table><tbody>', '</tbody></table>', 2] ||
            this.test('^<th|^<td') && ['<table><tbody><tr>', '</tr></tbody></table>', 3] ||
            this.test('^<li') && ['<ul>', '</ul>', 1] ||
            this.test('^<dt|^<dd') && ['<dl>', '</dl>', 1] ||
            this.test('^<le') && ['<fieldset>', '</fieldset>', 1] ||
            this.test('^<opt') && ['<select multiple="multiple">', '</select>', 1] ||
            ['', '', 0];
        if( container ){
            var el = new Element('div', {html: wrapper[0] + this + wrapper[1]}).getChildren();
            while(wrapper[2]--) el = el[0].getChildren();
            el.inject( container );
            if( callback )callback( container );
            return el;
        }else{
            var div = new Element('div', {html: wrapper[0] + this + wrapper[1]});
            div.setStyle("display","none").inject( $(document.body) );
            if( callback )callback( div );
            var el = div.getChildren();
            while(wrapper[2]--) el = el[0].getChildren();
            div.dispose();
            return el;
        }
    }

});

MWF.xApplication.Template.Explorer.ComplexView = new Class({
    Implements: [Options, Events],
    options: {
        "style": "default",
        "templateUrl": "",
        "scrollEnable" : false,
        "scrollType" : "xApp_TaskList",
        "checkboxEnable" : true,
        "pagingEnable" : false,
        "documentSortable" : false, //item可以拖动排序，和 onSortCompleted 结合使用
        "documentKeyWord" : null,
        "pagingPar" : {
            position : [ "top" , "bottom" ], //分页条，上下
            countPerPage : 0,
            visiblePages : 10,
            currentPage : 1,
            currentItem : null,
            hasPagingBar : true,
            pagingBarUseWidget: false,
            hasTruningBar : true,
            hasNextPage : true,
            hasPrevPage : false,
            hasJumper : true,
            hasReturn : true,
            hiddenWithInvalid : true,
            text : {
                prePage : "",
                nextPage : "",
                firstPage : "",
                lastPage : ""
            }
        }
    },
    initialize: function (container, app, explorer, options, para) {
        this.container = container;
        this.explorer = explorer;
        if( para ){
            this.app = app || para.app || this.explorer.app;
            this.lp = para.lp || this.explorer.lp || this.app.lp;
            this.css = para.css || this.explorer.css || this.app.css;
            this.actions = para.actions || this.explorer.actions || this.app.actions || this.app.restActions;
        }else{
            this.app = app || this.explorer.app;
            this.lp = this.explorer.lp || this.app.lp;
            this.css = this.explorer.css || this.app.css;
            this.actions = this.explorer.actions || this.app.actions || this.app.restActions;
        }

        if (!options.templateUrl) {
            options.templateUrl = this.explorer.path + "listItem.json"
        } else if (options.templateUrl.indexOf("/") == -1) {
            options.templateUrl = this.explorer.path + options.templateUrl;
        }
        this.setOptions(options);

    },
    initData: function () {
        this.items = [];
        this.documents = {};
        this.isItemsLoaded = false;
        this.isItemLoadding = false;
        this.loadItemQueue = 0;
        this.count = 0;
        //this.controllers =[];
    },
    load: function () {
        this.fireEvent("queryLoad");
        this._queryLoad( );
        this.initData();
        this.ayalyseTemplate();

        this.node = new Element("div", {
            "styles": this.css.viewContentListNode
        }).inject(this.container);

        if( this.options.wrapView ){
            this.viewWrapNode = new Element("div").inject(this.node);
        }

        if( this.options.scrollEnable ){
            this.setScroll();
        }
        this.getContentTemplateNode();
        this.createViewNode();
        this.initSortData(); //点击列排序
        this.createViewHead();
        this.createViewBody();
        this._postLoad();
        this.fireEvent("postLoad");
    },
    reload: function () {
        debugger;
        this.clear();
        this.node = new Element("div", {
            "styles": this.css.viewContentListNode
        }).inject(this.container);

        if( this.options.wrapView ){
            this.viewWrapNode = new Element("div").inject(this.node);
        }

        this.createViewNode();
        this.createViewHead();
        this.createViewBody();
        this.fireEvent("postReloadLoad");
    },
    initSortData: function () {
        this.sortField = null;
        this.sortType = null;
        this.sortFieldDefault = null;
        this.sortTypeDefault = null;
    },
    destroy: function(){
        if(this.documentNodeTemplate){
            delete this.documentNodeTemplate;
        }
        if(this.template)delete this.template;
        if( this.scrollBar || this.scrollContainerFun ){
            this.destroyScroll()
        }

        this.clear();
        //delete this;
    },
    clear: function () {
        if(this.pagingContainerTop ){
            if( this.pagingContainerTopCreated ){
                this.pagingContainerTop.destroy();
                this.pagingContainerTop = null;
            }else{
                this.pagingContainerTop.empty();
            }
        }

        if( this.pagingContainerBottom ){
            if( this.pagingContainerBottomCreated ){
                this.pagingContainerBottom.destroy();
                this.pagingContainerBottom = null;
            }else{
                this.pagingContainerBottom.empty();
            }
        }

        if( this.paging )this.paging.destroy();
        this.paging = null;

        if( this.documentDragSort ){
            this.documentDragSort.removeLists( this.viewBodyNode || this.viewNode );
            this.documentDragSort.detach();
            this.documentDragSort = null;
        }
        this.documents = null;
        MWF.release(this.items);
        this.items = [];
        this.documents = {};
        this.viewWrapNode = null;
        this.node.destroy();
        this.container.empty();
        this.isItemsLoaded = false;
        this.isItemLoadding = false;
        this.loadItemQueue = 0;
    },
    clearBody : function(){
        this.items.each( function(item,i){
            item.destroy();
        });
        this.documents = null;
        MWF.release(this.items);
        this.items = [];
        this.documents = {};
    },
    resort: function (el) {
        this.sortField = el.retrieve("sortField");
        var sortType = el.retrieve("sortType");
        if (sortType == "") {
            this.sortType = "asc";
        } else if (this.sortType == "asc") {
            this.sortType = "desc";
        } else {
            this.sortField = null;
            this.sortType = null;
        }
        this.reload();
    },
    destroyScroll: function(){
        if( this.options.scrollType == "window" ){
            if( this.scrollContainerFun ){
                this.container.removeEvent("scroll", this.scrollContainerFun );
                this.scrollContainerFun = null;
            }
        }else{
            if(this.scrollBar.scrollVAreaNode){
                this.scrollBar.scrollVAreaNode.destroy();
            }
            delete this.scrollBar;
        }
    },
    setScroll: function(){
        if( this.options.scrollType == "window" ){
            this.container.setStyle("overflow","auto");
            this.scrollContainerFun = function(){
                if( !this.options.pagingEnable ){
                    var scrollSize = this.container.getScrollSize();
                    var clientSize = this.container.getSize();
                    var scrollHeight = scrollSize.y - clientSize.y;
                    if (this.container.scrollTop + 150 > scrollHeight ) {
                        if (! this.isItemsLoaded) this.loadElementList();
                    }
                }
            }.bind(this);
            this.container.addEvent("scroll", this.scrollContainerFun )
        }else{
            MWF.require("MWF.widget.ScrollBar", function () {
                this.scrollBar = new MWF.widget.ScrollBar(this.container, {
                    "indent": false,
                    "style": this.options.scrollType,
                    "where": "before",
                    "distance": 60,
                    "friction": 4,
                    "axis": {"x": false, "y": true},
                    "onScroll": function (y) {
                        if( !this.options.pagingEnable ){
                            var scrollSize = this.container.getScrollSize();
                            var clientSize = this.container.getSize();
                            var scrollHeight = scrollSize.y - clientSize.y;
                            if (y + 200 > scrollHeight ) {
                                if (! this.isItemsLoaded) this.loadElementList();
                            }
                        }
                    }.bind(this)
                });
            }.bind(this));
        }
    },
    ayalyseTemplate: function () {
        MWF.getJSON(this.options.templateUrl, function (json) {
            this.template = json;
        }.bind(this), false)
    },
    formatElement: function (container, setting, clear ) {
        //container.appendHTML(setting.html);
        var el = setting.html.toDOM( container, function( c , el ){
            this.formatStyles(c);
            this.formatLable(c);
            if(container)this.setEventStyle(c, setting);
        }.bind(this) )[0];
        if( setting.width ){
            el.set("width",setting.width )
        }
        if( clear && container ){
            container.empty();
        }
        return el;
    },
    formatStyles: function ( container ) {
        container.getElements("[class]").each(function (el) {
            var className = el.get("class");
            if (className && this.css[className]) {
                el.setStyles(this.css[className])
            }
        }.bind(this));
        container.getElements("[styles]").each(function (el) {
            var styles = el.get("styles");
            if (styles && this.css[styles]) {
                el.setStyles(this.css[styles])
            }
        }.bind(this));
    },
    formatLable: function (container) {
        container.getElements("[lable]").each(function (el) {
            var lable = el.get("lable");
            if (lable && this.lp[lable]) {
                el.set("text", this.lp[lable] + (el.get("colon") ? ":" : "") )
            }
        }.bind(this))
    },
    createViewNode: function () {
        this.fireEvent("queryCreateViewNode");
        this._queryCreateViewNode( );
        this.viewNode = this.formatElement(this.viewWrapNode || this.node, this.template.viewSetting);
        this._postCreateViewNode( this.viewNode );
        this.fireEvent("postCreateViewNode");
        if (!this.viewNode)return;
    },
    getContentTemplateNode: function(){
        this.documentNodeTemplate = this.formatElement(null, this.template.documentSetting);
        this.template.items.each(function (item) {
            item.nodeTemplate = this.formatElement(null, item.content);
        }.bind(this))
    },
    createViewHead: function () {
        this.fireEvent("queryCreateViewHead");
        this._queryCreateViewHead( );
        if (this.template) {
            if (!this.template.headSetting || this.template.headSetting.disable || !this.template.headSetting.html) {
                return;
            }
        }
        var _self = this;

        var viewHeadNode;
        if( this.template.viewHeadSetting ){
            viewHeadNode = this.viewHeadNode = this.formatElement(this.viewNode, this.template.viewHeadSetting);
        }

        var headNode = this.headNode = this.formatElement( viewHeadNode || this.viewNode, this.template.headSetting);
        this.headItemNodes = {};

        this.template.items.each(function (item) {
            if( !item.head )return;
            ////如果设置了权限，那么options里需要有 对应的设置项才会展现
            // 比如 item.access == isAdmin 那么 this.options.isAdmin要为true才展现
            if (item.access && !this.options[item.access])return;
            if( item.condition && !this.getConditionResult(item.condition))return;
            if (item.head.access && !this.options[item.head.access])return;
            if( item.head.condition && !this.getConditionResult(item.head.condition))return;
            if( item.name == "$checkbox" && !this.options.checkboxEnable  )return;

            var headItemNode = this.formatElement(headNode, item.head);
            if( item.name ){
                this.headItemNodes[item.name] = headItemNode;
            }

            if (item.name == "$checkbox" && this.options.checkboxEnable ) {
                this.checkboxElement = new Element("input", {
                    "type": "checkbox"
                }).inject(headItemNode);
                this.checkboxElement.addEvent("click", function () {
                    this.selectAllCheckbox()
                }.bind(this))
            }
            if (item.defaultSort && item.defaultSort != "") {
                this.sortFieldDefault = item.name;
                this.sortTypeDefault = item.defaultSort;
            }
        }.bind(this));

        this.template.items.each( function(item){
            if (item.name && item.sort && item.sort != "") {
                var headItemNode = this.headItemNodes[item.name];
                headItemNode.store("sortField", item.name);
                if (this.sortField == item.name && this.sortType != "") {
                    headItemNode.store("sortType", this.sortType);
                    this.sortIconNode = new Element("div", {
                        "styles": this.sortType == "asc" ? this.css.sortIconNode_asc : this.css.sortIconNode_desc
                    }).inject(headItemNode, "bottom");
                } else {
                    headItemNode.store("sortType", "");
                    this.sortIconNode = new Element("div.sortIconNode", {"styles": this.css.sortIconNode}).inject(headItemNode, "bottom");
                }
                headItemNode.setStyle("cursor", "pointer");
                headItemNode.addEvent("click", function () {
                    _self.resort(this);
                })
            }
        }.bind(this));
        this.fireEvent("postCreateViewHead");
        this._postCreateViewHead( headNode )
    },
    getConditionResult: function (str) {
        var flag = true;
        if (str && str.substr(0, 8) == "function") { //"function".length
            eval("var fun = " + str);
            flag = fun.call(this, this.data);
        }
        return flag;
    },
    setEventStyle: function (node, setting, bingObj, data) {
        var _self = this;
        var styles, overStyles, downStyles;
        var styleStr = setting.styles;
        if (typeOf(styleStr) == "string"){
            if (styleStr && styleStr.substr(0, "function".length) == "function") {
                eval("var fun = " + styleStr );
                styles = fun.call(bingObj, data);
            }else{
                styles = this.css[styleStr];
            }
        }else if (typeOf(styleStr) == "object"){
            styles = styleStr;
        }else if (typeOf(styleStr) == "function"){
            eval("var fun = " + styleStr );
            styles = fun.call(bingObj, data);
        }

        if (!styles) {
            var s = node.get("styles");
            if (!s)node.get("class");
            if (s)styles = this.css[s]
        }
        if (setting.icon) {
            if (!styles)styles = {};
            styles["background-image"] = "url(" + this.explorer.path + "/" + this.explorer.options.style + "/icon/" + setting.icon + ")";
        }

        if (typeOf(setting.mouseoverStyles) == "string")overStyles = this.css[setting.mouseoverStyles];
        if (typeOf(setting.mouseoverStyles) == "object") overStyles = setting.mouseoverStyles;
        if (setting.mouseoverIcon) {
            if (!overStyles)overStyles = {};
            overStyles["background-image"] = "url(" + this.explorer.path + "/" + this.explorer.options.style + "/icon/" + setting.mouseoverIcon + ")"
        }

        if (typeOf(setting.mousedownStyles) == "string")downStyles = this.css[setting.mousedownStyles];
        if (typeOf(setting.mousedownStyles) == "object") downStyles = setting.mousedownStyles;
        if (setting.mousedownIcon) {
            if (!downStyles)downStyles = {};
            downStyles["background-image"] = "url(" + this.explorer.path + "/" + this.explorer.options.style + "/icon/" + setting.mousedownIcon + ")"
        }

        if (styles)node.setStyles(styles);
        var holdMouseDownStyles = setting.holdMouseDownStyles || false;
        if (overStyles && styles) {
            node.addEvent("mouseover", function (ev) {
                if( !_self.lockNodeStyle  && (!this.holdMouseDownStyles || _self.mousedownNode != this.node ) )this.node.setStyles(this.styles);
            }.bind({"styles": overStyles, "node":node, "holdMouseDownStyles" : holdMouseDownStyles }));
            node.addEvent("mouseout", function (ev) {
                if( !_self.lockNodeStyle && (!this.holdMouseDownStyles || _self.mousedownNode != this.node ) )this.node.setStyles(this.styles);
            }.bind({"styles": styles, "node":node, "holdMouseDownStyles" : holdMouseDownStyles}));
        }
        if (downStyles && ( overStyles || styles)) {
            node.addEvent("mousedown", function (ev) {
                if( !_self.lockNodeStyle )this.node.setStyles(this.styles);
                if( _self.mousedownNode && this.holdMouseDownStyles && _self.mousedownNode != this.node ){
                    _self.mousedownNode.setStyles( this.normalStyle )
                }
                if( this.holdMouseDownStyles ){
                    _self.mousedownNode = this.node;
                }
            }.bind({"styles": downStyles, normalStyle : (styles || overStyles), "node":node, "holdMouseDownStyles" : holdMouseDownStyles}));
            node.addEvent("mouseup", function (ev) {
                if( !_self.lockNodeStyle && (!this.holdMouseDownStyles || _self.mousedownNode != this.node ) )this.node.setStyles(this.styles);
            }.bind({"styles": overStyles || styles, "node":node, "holdMouseDownStyles" : holdMouseDownStyles}))
        }
    },
    selectAllCheckbox: function () {
        var flag = this.checkboxElement.get("checked");
        this.items.each(function (it) {
            if (it.checkboxElement)it.checkboxElement.set("checked", flag)
        }.bind(this))
    },
    getCheckedItems : function(){
        var checkedItems = [];
        this.items.each(function (it) {
            if (it.checkboxElement.get("checked")) {
                checkedItems.push( it )
            }
        }.bind(this));
        return checkedItems;
    },
    createViewBody : function(){
        if( this.template.viewBodySetting ){
            this.viewBodyNode = this.formatElement(this.viewNode, this.template.viewBodySetting);
        }
        this.loadElementList();
    },
    loadElementList: function () {
        if( this.options.pagingEnable ){
            var currentItem = this.options.pagingPar.currentItem;
            var countPerPage = this.options.pagingPar.countPerPage;
            if( currentItem ){
                var pageNum = Math.ceil( currentItem / countPerPage );
                var itemNum = currentItem % countPerPage;
                this.loadPagingElementList( countPerPage , pageNum, currentItem );
            }else{
                this.loadPagingElementList( countPerPage , this.options.pagingPar.currentPage ); //使用分页的方式
            }
        }else{
            countPerPage = this.options.pagingPar.countPerPage;
            this.loadScrollElementList( countPerPage ); //滚动条下拉取下一页
        }
    },
    loadScrollElementList : function( count ){
        if (!this.isItemsLoaded) {
            if (!this.isItemLoadding) {
                this.isItemLoadding = true;
                this._getCurrentPageData(function (json) {
                    var itemList = [];

                    this.fireEvent("queryCreateViewBody",[this]);
                    this._queryCreateViewBody( );

                    var length = this.dataCount = json.count;  //|| json.data.length;
                    if (length <= this.items.length) {
                        this.isItemsLoaded = true;
                    }
                    if( json.data && typeOf( json.data )=="array" ){
                        json.data.each(function (data ) {
                            var key = data[ this.options.documentKeyWord || "id" ];
                            if (!this.documents[key]) {
                                var item = this._createDocument(data, this.items.length);
                                this.items.push(item);
                                if(item)itemList.push( item.node );
                                this.documents[key] = item;
                            }
                        }.bind(this));
                    }
                    this.isItemLoadding = false;

                    if( this.options.documentSortable && itemList.length){
                        if( this.documentDragSort ){
                            this.documentDragSort.addItems( itemList );
                        }else{
                            this.makeSortable();
                        }
                    }
                    this.fireEvent("postCreateViewBody", [this]);
                    this._postCreateViewBody( this.viewBodyNode || this.viewNode );

                    if (this.loadItemQueue > 0) {
                        this.loadItemQueue--;
                        this.loadElementList();
                    }
                }.bind(this), count);
            } else {
                this.loadItemQueue++;
            }
        }
    },
    loadPagingElementList : function( count, pageNum, itemNum ){
        this.currentPage = pageNum || 1;
        this._getCurrentPageData(function (json) {
            this.items = [];
            this.documents = {};

            this.fireEvent("queryCreateViewBody");
            this._queryCreateViewBody( );

            if( this.documentDragSort ){
                this.documentDragSort.removeLists( this.viewBodyNode || this.viewNode );
            }

            this.dataCount = json.count;
            this.createPaging( json.count, this.currentPage );
            json.data.each(function (data ) {
                var item = this._createDocument(data, this.items.length);
                this.items.push( item );
                var key = data[ this.options.documentKeyWord || "id" ];
                this.documents[key] = item;
            }.bind(this));

            if( this.options.documentSortable && this.items.length){
                this.makeSortable();
            }

            var top;
            if( itemNum && this.documents[ itemNum ] ){
                if( this.options.documentKeyWord ){
                    top = this.documents[ itemNum ].node.getTop();
                }else{
                    top = this.items[itemNum-1].node.getTop();
                }
                this.fireEvent( "gotoItem", top );
            }

            this.fireEvent("postCreateViewBody");
            this._postCreateViewBody( this.viewBodyNode || this.viewNode );

        }.bind(this), count, pageNum );
    },
    createPaging : function( itemSize, pageNum ){
        if( !this.options.pagingEnable || this.paging )return;
        if( this.options.pagingPar.position.indexOf("top") > -1 ){
            if( !this.pagingContainerTop ){
                this.pagingContainerTopCreated = true;
                this.pagingContainerTop = new Element("div", {"styles":this.css.pagingContainer}).inject( this.viewWrapNode || this.viewNode, "before" );
            }
        }
        if( this.options.pagingPar.position.indexOf("bottom") > -1 ){
            if( !this.pagingContainerBottom ){
                this.pagingContainerBottomCreated = true;
                this.pagingContainerBottom = new Element("div", {"styles":this.css.pagingContainer}).inject( this.viewWrapNode || this.viewNode, "after" );
            }
        }
        var par = Object.merge( this.options.pagingPar, {
            itemSize : itemSize,
            onJumpingPage : function( arg1, arg2 ){
                if( o2.typeOf(arg1) === "object" ){
                    this.loadPagingElementList( this.options.pagingPar.countPerPage, arg1.pageNum, arg1.itemNum );
                }else{
                    this.loadPagingElementList( this.options.pagingPar.countPerPage, arg1, arg2 );
                }
            }.bind(this)
        });
        if( pageNum )par.currentPage = pageNum;
        if( this.options.pagingPar.hasPagingBar ){
            if( this.options.pagingPar.pagingBarUseWidget ){
                if(this.pagingContainerTop){
                    this.loadWidgetPaging(this.pagingContainerTop, itemSize, par)
                }
                if(this.pagingContainerBottom){
                    this.loadWidgetPaging(this.pagingContainerBottom, itemSize, par)
                }
            }else{
                this.paging = new MWF.xApplication.Template.Explorer.Paging(
                    this.pagingContainerTop, this.pagingContainerBottom, par, this.css);
                this.paging.load();
            }
        }
    },
    loadWidgetPaging: function(node, itemSize, par){
        if(!o2.widget.Paging)MWF.require("o2.widget.Paging", null, false);
        var pageSize = Math.ceil(itemSize/this.options.pagingPar.countPerPage);
        this.paging = new o2.widget.Paging(node, Object.merge({
            countPerPage: 20,
            visiblePages: 9,
            currentPage: 1,
            itemSize: 0,
            pageSize: pageSize,
            hasNextPage: true,
            hasPrevPage: true,
            hasTruningBar: true,
            hasBatchTuring: true,
            hasFirstPage: true,
            hasLastPage: true,
            hasJumper: true,
            hiddenWithDisable: false,
            hiddenWithNoItem: true,
            text: {
                prePage: "",
                nextPage: "",
                firstPage: "",
                lastPage: ""
            }
        }, par), this.options.pagingPar.pagingStyles || {});
        this.paging.load();
    },
    _getCurrentPageData: function (callback, count, page) {
        if( this.options.pagingEnable ){
            this.actions.listDetailFilter(page, count, filter, function (json) {
                if (callback)callback(json);
            }.bind(this))
        }else{
            if (!count)count = 20;
            var id = (this.items.length) ? this.items[this.items.length - 1].data.id : "(0)";
            var filter = this.filterData || {};
            this.actions.listDetailFilterNext(id, count, filter, function (json) {
                if (callback)callback(json);
            }.bind(this))
        }
    },
    getCurrentPageNum: function(){
        return this.paging.options.currentPage;
    },
    getPageSize: function(){
        return this.paging.options.pageSize;
    },
    gotoPage : function( page ){
      this.paging.gotoPage( page );
    },
    makeSortable:function(){
        this.documentDragSort = new Sortables( this.viewBodyNode || this.viewNode,
            {
                clone:true,
                opacity:0.3,
                onStart : function (element, clone) {
                    if(this.css.itemSortCloneNode)clone.setStyles( this.css.itemSortCloneNode );
                    this.fireEvent( "documentSortStart", [ element, clone ] );
                    this._documentSortStart(element, clone);
                }.bind(this),
                onSort : function (element, clone) {
                    this.fireEvent( "documentSort", [ element, clone ] );
                    this._documentSort(element, clone);
                }.bind(this),
                onComplete : function (element) {
                    var serial = this.documentDragSort.serialize();
                    this.fireEvent( "documentSortComplete", [ element, serial ] );
                    this._documentSortComplete(element, serial);
                    //var id = element.get("id");
                    //var idStr = dragSort.serialize();
                    //var submitData = {
                    //    "ordersymbol":this.filter.ordersymbol,
                    //    "ids":idStr
                    //};
                    //this.actions.changeKeyWorkPosition(submitData,function(json){
                    //    this.createViewContent();
                    //}.bind(this));

                }.bind(this)
            }
        )
    },
    _createDocument: function (data, index) {
        return new MWF.xApplication.Template.Explorer.ComplexDocument(this.viewBodyNode || this.viewNode, data, this.explorer, this, null,index);
    },
    _openDocument: function (documentData) {

    },
    _removeDocument: function (documentData, all) {
        //var id = document.data.id;
        //this.actions.removeDocument(id, function(json){
        //    //json.data.each(function(item){
        //    this.items.erase(this.documents[id]);
        //    this.documents[id].destroy();
        //    MWF.release(this.documents[id]);
        //    delete this.documents[id];
        //    this.app.notice(this.app.lp.deleteDocumentOK, "success");
        //    // }.bind(this));
        //}.bind(this));
    },
    _create: function () {
        MWF.xDesktop.requireApp("Template", "MPopupForm", function(){
            this.from = new MPopupForm(this.explorer);
            this.from.create();
        }.bind(this), false);
    },
    _queryLoad: function(){
    },
    _postLoad: function(){
    },
    _queryCreateViewNode: function(){

    },
    _postCreateViewNode: function( viewNode ){

    },
    _queryCreateViewHead:function(){

    },
    _postCreateViewHead: function( headNode ){

    },
    _queryCreateViewBody:function(){

    },
    _postCreateViewBody: function( bodyNode ){

    },
    _documentSortStart: function( element, clone ){

    },
    _documentSort: function( element, clone ){

    },
    _documentSortComplete: function( element, serial ){

    }
});

MWF.xApplication.Template.Explorer.ComplexDocument = new Class({
    Implements: [Options, Events],
    initialize: function (container, data, explorer, view, para, index) {
        this.explorer = explorer;
        this.data = data;
        this.container = container;
        this.view = view;
        this.index = index;
        if( para ){
            this.app = para.app || this.view.app || this.explorer.app;
            this.lp = para.lp || this.view.lp || this.explorer.lp || this.app.lp;
            this.css = para.css || this.view.css || this.explorer.css || this.app.css;
            this.actions = para.actions || this.view.actions || this.explorer.actions || this.app.actions || this.app.restActions;
        }else{
            this.app = this.view.app || this.explorer.app;
            this.lp = this.view.lp || this.explorer.lp || this.app.lp;
            this.css = this.view.css || this.explorer.css || this.app.css;
            this.actions = this.view.actions || this.explorer.actions || this.app.actions || this.app.restActions;
        }

        this.load();
    },

    load: function () {
        this.fireEvent("queryCreateDocumentNode");
        this._queryCreateDocumentNode( this.data );
        var _self = this;

        this.node = this.view.documentNodeTemplate.clone().inject(this.container);

        //this.documentAreaNode =  new Element("td", {"styles": this.css.documentNode}).inject(this.node);

        this._load()
    },
    reload : function(){
        this.preNode = this.node;
        this.node = this.view.documentNodeTemplate.clone().inject(this.preNode,"after");
        this.preNode.destroy();
        this._load()
    },
    _load : function(){
        var _self = this;
        this.view.template.items.each(function (item) {
            var flag = true;
            if( item.condition ){
                flag = this.getConditionResult( item.condition );
            }
            if( flag && item.access ){
                flag = this._getItemAccess(item);
            }
            if( flag ){
                this.loadItem(item.name, item.content, item.nodeTemplate);
            }
        }.bind(this));

        var setting = this.view.template.documentSetting;
        if( setting.styles || setting.mouseoverStyles || setting.mousedownStyles || setting.icon || setting.mouseoverIcon || setting.mousedownIcon ){
            this.view.setEventStyle( this.node, setting, this, this.data );
        }

        var available = this.getConditionResult(setting.condition);
        if( setting.action && this[setting.action] ){
            if ( available ){
                this.node.addEvent("click", function (ev) {
                    this.fun.call(_self, this.node, ev);
                    ev.stopPropagation();
                }.bind({fun: this[setting.action], node : this.node}))
            }
        }
        if( setting.event && available ){
            this.bindEvent( this.node, setting.event );
        }

        this.fireEvent("postCreateDocumentNode");
        this._postCreateDocumentNode( this.node, this.data )
    },
    loadItem: function (name, item, nodeTemplate ) {
        var itemNode = this[name] = nodeTemplate.clone();
        if( this.format(itemNode, name, item) ){
            itemNode.inject(this.node);
        }
        if (item.items) {
            var elements = itemNode.getElements("[item]");
            if( itemNode.get("item") )elements.push(itemNode);
            elements.each(function (el) {
                var key = el.get("item");
                var sub = item.items[key];
                if( sub ){
                    if( !sub.value && sub.value!="" )sub.value = key;
                    if( !this.format(el, name, sub) ){
                        el.dispose()
                    }
                }
            }.bind(this))
        }
    },
    format: function (itemNode, name, item) {
        var _self = this;
        if( name == "$checkbox" && !this.view.options.checkboxEnable )return false;
        if (item.access) {
            if (!this._getItemAccess(item))return false;
        }


        var available = this.getConditionResult(item.condition);
        if ( item.action && !available) {
            return false;
        }

        //if (item.condition) {
        //    if (!this.getConditionResult(item.condition))return false;
        //}
        var show = this.getConditionResult( item.show );
        if( !show )itemNode.setStyle("display","none");

        if (item.text) {
            var text = this.getExecuteResult( item.text );
            //var text = item.text;
            itemNode.set("text", this.view.lp && this.view.lp[text] ? this.view.lp[text] : text);
        }
        if (item.title) {
            var title = this.getExecuteResult( item.title );
            //var title = item.title;
            itemNode.set("title", this.view.lp && this.view.lp[title] ? this.view.lp[title] : title);
        }
        if ( !item.text && item.value && item.value != "") {
            if( item.type == "html" ){
                itemNode.set("html", this.getValue(item.value));
            }else{
                itemNode.set("text", this.getValue(item.value));
            }
        }
        if( item.styles || item.mouseoverStyles || item.mousedownStyles || item.icon || item.mouseoverIcon || item.mousedownIcon ){
            this.view.setEventStyle( itemNode, item, this, this.data );
        }

        var action = item.action && this[item.action];
        if ( action ) {
            if ( available ){
                itemNode.addEvent("click", function (ev) {
                    this.fun.call(_self, this.node, ev);
                    ev.stopPropagation();
                }.bind({fun: action, node : itemNode}))
            }else{
                return false;
            }
        }
        if( item.event && available ){
            this.bindEvent( itemNode, item.event );
        }
        if( item.attr ){
            this.setAttr( itemNode, item.attr );
        }
        if ( name == "$checkbox" && this.view.options.checkboxEnable ) {
            if ( available ){
                this.checkboxElement = new Element("input", {
                    "type": "checkbox"
                }).inject(itemNode);
                if( item.event ){
                    this.bindEvent( this.checkboxElement, item.event );
                }
                if( !item.event || !item.event.contains( "click" ) ){
                    this.checkboxElement.addEvent("click", function (ev) {
                        ev.stopPropagation();
                    }.bind(this));
                }
                itemNode.addEvent("click", function (ev) {
                    this.checkboxElement.set("checked", !this.checkboxElement.get("checked"));
                    ev.stopPropagation();
                }.bind(this))
            }else{
                //return false;
            }
        }
        return true;
    },
    getExecuteResult : function( str ){
        var result = str;
        if (str && str.substr(0, 8) == "function") { //"function".length
            eval("var fun = " + str);
            result = fun.call(this, this.data);
        }
        return result;
    },
    getValue: function (str) {
        if (str.substr(0, 8 ) == "function") { //"function".length
            eval("var fun = " + str);
            return fun.call(this, this.data);
        } else if (typeOf(this.data[str]) == "number") {
            return this.data[str];
        } else {
            return this.data[str] ? this.data[str] : "";
        }
    },
    getConditionResult: function (str) {
        var flag = true;
        if (str && str.substr(0, 8) == "function") { //"function".length
            eval("var fun = " + str);
            flag = fun.call(this, this.data);
        }
        return flag;
    },
    setAttr: function(item, attr){
        if( !attr || attr == "" || attr == "$none" )return;
        if( typeof attr == "string" ){
            if( attr.indexOf("^^") > -1 ){
                var attrsArr = attr.split("##");
                if( attrsArr[0].split("^^").length != 2 )return;
                attrs = {};
                for(var i=0;i<attrsArr.length;i++){
                    var aname = attrsArr[i].split("^^")[0];
                    var afunction = attrsArr[i].split("^^")[1];
                    if( afunction.substr(0, "function".length) == "function" ){
                        eval("var fun = " + afunction );
                        attrs[ aname ] = fun.call(this, this.data);  //字符串变对象或function，方法1
                    }else{
                        attrs[ aname ] = afunction;
                    }
                }
            }else{
                //字符串变对象或function，方法2
                eval( "var attrs = " + attr );
            }
        }
        if( typeOf(attrs) == "object" ){
            for( var a in attrs ){
                item.set( a, attrs[a] );
            }
        }
    },
    bindEvent: function(item,events){
        if( !events || events == "" || events == "$none" )return;
        if( typeof events == "string" ){
            if( events.indexOf("^^") > -1 ){
                var eventsArr = events.split("##");
                if( eventsArr[0].split("^^").length != 2 )return;
                events = {};
                for(var i=0;i<eventsArr.length;i++){
                    var ename = eventsArr[i].split("^^")[0];
                    var efunction = eventsArr[i].split("^^")[1];
                    events[ ename ] = eval( "(function(){ return "+ efunction +" })()" );  //字符串变对象或function，方法1
                }
            }else{
                //字符串变对象或function，方法2
                eval( "var events = " + events );
            }
        }
        if( typeOf(events) == "object" ){
            for( var e in events ){
                item.addEvent( e, function(ev){
                    this.fun.call( this.bingObj, this.target, ev );
                    ev.stopPropagation();
                }.bind({bingObj : this, target: item, fun : events[e]}));
            }
        }
    },
    _getItemAccess: function (item) {
        if (item.access && !this.explorer.options[item.access]) {
            return false;
        } else {
            return true;
        }
    },
    _getActionAccess: function (actionData) {
        return true;
    },
    open: function (e) {
        //var options = {"documentId": this.data.id }//this.explorer.app.options.application.allowControl};
        //this.explorer.app.desktop.openApplication(e, "cms.Document", options);
        this.view._openDocument(this.data);
    },
    remove: function (e) {
        var lp = this.lp || this.view.lp || this.app.lp;
        var text = lp.deleteDocument.replace(/{title}/g, this.data.title);
        var _self = this;
        this.node.setStyles(this.css.documentNode_remove);
        this.readyRemove = true;
        this.view.lockNodeStyle = true;

        //this.explorer.app.confirm("warn", e, lp.deleteDocumentTitle, text, 350, 120, function () {
        this.app.confirm("warn", e, lp.deleteDocumentTitle, text, 350, 120, function () {
            //var inputs = this.content.getElements("input");
            //var flag = "";
            //for (var i=0; i<inputs.length; i++){
            //    if (inputs[i].checked){
            //        flag = inputs[i].get("value");
            //        break;
            //    }
            //}
            //if (flag){
            //if (flag=="all"){
            //_self.explorer.removeDocument(_self, true);
            //}else{
            _self.view._removeDocument(_self.data, false);
            _self.view.lockNodeStyle = false;
            //}
            this.close();
            //}else{
            //    this.content.getElement("#deleteDocument_checkInfor").set("text", lp.deleteAllDocumentCheck).setStyle("color", "red");
            //}
        }, function () {
            _self.node.setStyles(_self.css.documentNode);
            _self.readyRemove = false;
            _self.view.lockNodeStyle = false;
            this.close();
        });
    },

    destroy: function () {
        this.node.destroy();
        //delete this;
    },
    _queryCreateDocumentNode:function( itemData ){

    },
    _postCreateDocumentNode: function( itemNode, itemData ){

    }
});

MWF.xApplication.Template.Explorer.Paging = new Class({
    Implements: [Options, Events],
    options : {
        position : ["top","bottom"],
        countPerPage : 20,
        visiblePages : 10,
        currentPage : 1,
        itemSize : 0,
        pageSize : 0,
        hasNextPage : true,
        hasPrevPage : false,
        hasTruningBar : true,
        hasJumper : true,
        hasReturn : true,
        returnText : "",
        hiddenWithDisable: true,
        text : {
            prePage : "",
            nextPage : "",
            firstPage : "",
            lastPage : ""
        }
    },
    initialize: function (topContainer, bottomContainer, options, css) {
        if (!options.returnText) options.returnText = MWF.xApplication.Template.LP.explorer.returnText;
        this.setOptions( options || {});
        this.topContainer = topContainer;
        this.bottomContainer = bottomContainer;
        this.css = css;
    },
    load : function(){
        this.fireEvent( "queryLoad", this);
        this.options.pageSize = Math.ceil(this.options.itemSize/this.options.countPerPage);

        if( ( (this.options.pageSize == 1 || this.options.pageSize == 0) && this.options.hiddenWithDisable ) && !this.options.hasReturn )return;

        if( this.topContainer ){
            this.topContainer.empty();
            if(  this.options.hasTruningBar && this.options.position.indexOf("top") > -1 ){
                this.createNode( this.topContainer );
            }
        }

        if( this.bottomContainer ){
            this.bottomContainer.empty();
            if( this.options.hasPrevPage ){
                this.createPrevPageNode( this.bottomContainer );
            }
            if( this.options.hasNextPage ){
                this.createNextPageNode( this.bottomContainer );
            }
            if(  this.options.hasTruningBar && this.options.position.indexOf("bottom") > -1 ){
                this.createNode( this.bottomContainer );
            }
        }
        this.fireEvent( "postLoad", this);
    },
    createNode : function( container ){
        var _self = this;

        var visiblePages = this.options.visiblePages;
        var pageSize = this.options.pageSize;
        var currentPage = this.options.currentPage;

        var halfCount = Math.floor( visiblePages / 2);
        var i, max, min;
        if( pageSize <= visiblePages ){
            min = 1;
            max = pageSize;
        }else if( currentPage + halfCount > pageSize ){
            min = pageSize - visiblePages;
            max = pageSize;
        }else if( currentPage - halfCount < 1 ){
            min = 1;
            max = visiblePages;
        }else{
            min = currentPage - halfCount;
            max = currentPage + halfCount;
        }

        var node = this.node = new Element("div.pagingBar", { styles : this.css.pagingBar }  ).inject( container );

        if( this.options.hasReturn ){
            var pageReturn = this.pageReturn = new Element( "div.pageReturn" , { styles : this.css.pageReturn , "text" : this.options.returnText } ).inject(node);
            pageReturn.addEvents( {
                "mouseover" : function( ev ){ ev.target.setStyles( this.css.pageReturn_over ) }.bind(this),
                "mouseout" : function( ev ){ ev.target.setStyles( this.css.pageReturn ) }.bind(this),
                "click" : function(){ this.fireEvent( "pageReturn" , this ) }.bind(this)
            })
        }

        if( (pageSize != 1 && pageSize != 0) || !this.options.hiddenWithDisable ){
            if( min > 1 || !this.options.hiddenWithDisable){
                var firstPage = this.firstPage = new Element( "div.firstPage" , { styles : this.css.firstPage, text : "1..."  }).inject(node);
                if( this.options.text.firstPage )firstPage.set( "text", this.options.text.firstPage );
                firstPage.addEvents( {
                    "mouseover" : function( ev ){ ev.target.setStyles( this.css.firstPage_over ) }.bind(this),
                    "mouseout" : function( ev ){ ev.target.setStyles( this.css.firstPage ) }.bind(this),
                    "click" : function(){ this.gotoPage(1) }.bind(this)
                } )
            }
            if( currentPage != 1 || !this.options.hiddenWithDisable){
                var prePage =  this.prePage =  new Element( "div.prePage" , { styles : this.css.prePage } ).inject(node);
                if( this.options.text.prePage )prePage.set( "text", this.options.text.prePage );
                prePage.addEvents( {
                    "mouseover" : function( ev ){ ev.target.setStyles( this.css.prePage_over ) }.bind(this),
                    "mouseout" : function( ev ){ ev.target.setStyles( this.css.prePage ) }.bind(this),
                    "click" : function(){ this.gotoPage( currentPage-1 ) }.bind(this)
                } )
            }

            this.pageTurnNodes = [];
            for( i=min; i<=max; i++ ){
                if( currentPage == i ){
                    this.currentPage = new Element("div.currentPage", {"styles" : this.css.currentPage, "text" : i }).inject(node);
                }else{
                    var pageTurnNode = new Element("div.pageItem", {"styles" : this.css.pageItem, "text" : i }).inject(node);
                    pageTurnNode.addEvents( {
                        "mouseover" : function( ev ){ ev.target.setStyles( this.css.pageItem_over ) }.bind(this),
                        "mouseout" : function( ev ){ ev.target.setStyles( this.css.pageItem ) }.bind(this),
                        "click" : function(){ this.obj.gotoPage( this.num ) }.bind({ obj : this, num : i })
                    });
                    this.pageTurnNodes.push( pageTurnNode );
                }
            }

            if( this.options.hasJumper ){
                var pageJumper = this.pageJumper = new Element("input.pageJumper", {"styles" : this.css.pageJumper , "title" : MWF.xApplication.Template.LP.explorer.pageJumperText }).inject( node );
                new Element( "div.pageText", {"styles" : this.css.pageText , "text" : "/" + pageSize }).inject( node );
                pageJumper.addEvents( {
                    "focus" : function( ev ){ ev.target.setStyles( this.css.pageJumper_over ) }.bind(this),
                    "blur" : function( ev ){ ev.target.setStyles( this.css.pageJumper ) }.bind(this),
                    "keyup" : function(e){
                        this.value=this.value.replace(/[^0-9_]/g,'')
                    },
                    "keydown" : function(e){
                        if(e.code==13 && this.value!="" ){
                            _self.gotoPage( this.value );
                            e.stopPropagation();
                            //e.preventDefault();
                        }
                    }
                });
            }

            if( currentPage != pageSize || !this.options.hiddenWithDisable){
                var nextPage = this.nextPage = new Element( "div.nextPage" , { styles : this.css.nextPage } ).inject(node);
                if( this.options.text.nextPage )nextPage.set( "text", this.options.text.nextPage );
                nextPage.addEvents( {
                    "mouseover" : function( ev ){ ev.target.setStyles( this.css.nextPage_over ) }.bind(this),
                    "mouseout" : function( ev ){ ev.target.setStyles( this.css.nextPage ) }.bind(this),
                    "click" : function(){ this.gotoPage(  currentPage+1 ) }.bind(this)
                } )
            }

            if( max < pageSize || !this.options.hiddenWithDisable){
                var lastPage = this.lastPage = new Element( "div.lastPage" , { styles : this.css.lastPage, text : "..." + pageSize  }).inject(node);
                if( this.options.text.lastPage )lastPage.set( "text", this.options.text.lastPage );
                lastPage.addEvents( {
                    "mouseover" : function( ev ){ ev.target.setStyles( this.css.lastPage_over ) }.bind(this),
                    "mouseout" : function( ev ){ ev.target.setStyles( this.css.lastPage ) }.bind(this),
                    "click" : function(){ this.gotoPage( pageSize ) }.bind(this)
                } )
            }
        }
    },
    createNextPageNode : function( container ){
        if( this.nextPageNode ){
            this.nextPageNode.destroy();
            delete this.nextPageNode;
        }
        var pageSize = this.options.pageSize;
        if( this.options.currentPage != pageSize && pageSize != 1 && pageSize != 0 ){
            this.nextPageNode = new Element("div.nextPageNode", {
                "styles" : this.css.nextPageNode,
                "text" : MWF.xApplication.Template.LP.explorer.nextPage
            }).inject(container);
            this.nextPageNode.addEvents( {
                "mouseover" : function( ev ){ ev.target.setStyles( this.css.nextPageNode_over ) }.bind(this),
                "mouseout" : function( ev ){ ev.target.setStyles( this.css.nextPageNode ) }.bind(this),
                "click" : function(){ this.gotoPage(  this.options.currentPage+1 ) }.bind(this)
            })
        }
    },
    createPrevPageNode : function( container ){
        if( this.prevPageNode ){
            this.prevPageNode.destroy();
            delete this.prevPageNode;
        }
        var pageSize = this.options.pageSize;
        if( this.options.currentPage != 1 && pageSize != 1 && pageSize != 0 ){
            this.prevPageNode = new Element("div.prevPageNode", {
                "styles" : this.css.prevPageNode,
                "text" : MWF.xApplication.Template.LP.explorer.prePage
            }).inject(container);
            this.prevPageNode.addEvents( {
                "mouseover" : function( ev ){ ev.target.setStyles( this.css.prevPageNode_over ) }.bind(this),
                "mouseout" : function( ev ){ ev.target.setStyles( this.css.prevPageNode ) }.bind(this),
                "click" : function(){ this.gotoPage(  this.options.currentPage-1 ) }.bind(this)
            })
        }
    },
    gotoPage : function( num ){
        if(o2.typeOf(num) === "string")num = num.toInt();
        if( num < 1 || num > this.options.pageSize )return;
        this.fireEvent( "jumpingPage", { pageNum : num }  );
        this.options.currentPage = num;
        this.load();
    },
    gotoItem : function( itemNum ){
        var pageNum = Math.ceil( itemNum / this.options.countPerPage );
        var index = itemNum % this.options.countPerPage;
        this.fireEvent( "jumpingPage", { pageNum : pageNum, itemNum : itemNum, index : index } );
        this.options.currentPage = pageNum;
        this.load();
    },
    destroy : function(){
        if( this.nextPageNode )this.nextPageNode.destroy();
        //delete this;
    }
});

//MWF.xApplication.Template.Explorer.PopupForm = new Class({
//    Extends: MPopupForm
//});
