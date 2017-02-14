MWF.xApplication.Template = MWF.xApplication.Template || {};
MWF.xApplication.Template.Explorer = MWF.xApplication.Template.Explorer || {};

MWF.require("MWF.widget.Identity", null, false);
MWF.xDesktop.requireApp("Template", "lp." + MWF.language, null, false);

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
            el.inject( container )
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
        "pagingEnable" : false,
        "documentKeyWord" : null,
        "pagingPar" : {
            position : [ "top" , "bottom" ], //分页条，上下
            countPerPage : 20,
            visiblePages : 10,
            currentPage : 1,
            currentItem : null,
            hasPagingBar : true,
            hasTruningBar : true,
            hasNextPage : true,
            hasPrevPage : false,
            hasReturn : true
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
        this.initData();
        this.ayalyseTemplate();

        this.node = new Element("div", {
            "styles": this.css.viewContentListNode
        }).inject(this.container);

        if( this.options.scrollEnable ){
            this.setScroll();
        }
        this.getContentTemplateNode();
        this.createViewNode();
        this.initSortData();
        this.createViewHead();
        this.createViewBody();
    },
    reload: function () {
        this.clear();
        this.node = new Element("div", {
            "styles": this.css.viewContentListNode
        }).inject(this.container);
        this.createViewNode();
        this.createViewHead();
        this.createViewBody();
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
        if( this.scrollBar ){
            if(this.scrollBar.scrollVAreaNode){
                this.scrollBar.scrollVAreaNode.destroy();
            }
            delete this.scrollBar;
        }

        if(this.pagingContainerTop ){
            if( this.pagingContainerTopCreated ){
                this.pagingContainerTop.destroy();
            }else{
                this.pagingContainerTop.empty();
            }
        }

        if( this.pagingContainerBottom ){
            if( this.pagingContainerBottomCreated ){
                this.pagingContainerBottom.destroy();
            }else{
                this.pagingContainerBottom.empty();
            }
        }

        if( this.paging )this.paging.destroy();

        this.clear();
        delete this;
    },
    clear: function () {
        //if( this.options.pagingEnable ){
        //    this.documents = null;
        //    MWF.release(this.items);
        //    this.items = [];
        //    this.documents = {};
        //    this.node.destroy();
        //    this.container.empty();
        //    this.node.destroy();
        //    this.container.empty();
        //}else{
        //    this.documents = null;
        //    MWF.release(this.items);
        //    this.items = [];
        //    this.documents = {};
        //    this.node.destroy();
        //    this.container.empty();
        //    this.isItemsLoaded = false;
        //    this.isItemLoadding = false;
        //    this.loadItemQueue = 0;
        //}
        this.documents = null;
        MWF.release(this.items);
        this.items = [];
        this.documents = {};
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
    setScroll: function(){
        MWF.require("MWF.widget.ScrollBar", function () {
            this.scrollBar = new MWF.widget.ScrollBar(this.container, {
                "indent": false,
                "style": "xApp_TaskList",
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
        }.bind(this))
        container.getElements("[styles]").each(function (el) {
            var styles = el.get("styles");
            if (styles && this.css[styles]) {
                el.setStyles(this.css[styles])
            }
        }.bind(this))
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
        this.fireEvent("queryCreateViewNode")
        this._queryCreateViewNode( )
        this.viewNode = this.formatElement(this.node, this.template.viewSetting)
        this._postCreateViewNode( this.viewNode )
        this.fireEvent("postCreateViewNode")
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
        this._queryCreateViewHead( )
        if (this.template) {
            if (!this.template.headSetting || this.template.headSetting.disable || !this.template.headSetting.html) {
                return;
            }
        }
        var _self = this;
        var headNode = this.headNode = this.formatElement(this.viewNode, this.template.headSetting)

        this.template.items.each(function (item) {
            if( !item.head )return;
            ////如果设置了权限，那么options里需要有 对应的设置项才会展现
            // 比如 item.access == isAdmin 那么 this.options.isAdmin要为true才展现
            if (item.access && !this.options[item.access])return;
            if (item.head.access && !this.options[item.head.access])return;

            var headItemNode = this.formatElement(headNode, item.head);

            if (item.name == "$checkbox") {
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
            if (item.sort && item.sort != "") {
                headItemNode.store("sortField", item.name);
                if (this.sortField == item.name && this.sortType != "") {
                    headItemNode.store("sortType", this.sortType);
                    this.sortIconNode = new Element("div", {
                        "styles": this.sortType == "asc" ? this.css.sortIconNode_asc : this.css.sortIconNode_desc
                    }).inject(headItemNode, "top");
                } else {
                    headItemNode.store("sortType", "");
                    this.sortIconNode = new Element("div", {"styles": this.css.sortIconNode}).inject(headItemNode, "top");
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
        if (overStyles && styles) {
            node.addEvent("mouseover", function (ev) {
                if( !_self.lockNodeStyle )this.node.setStyles(this.styles);
            }.bind({"styles": overStyles, "node":node }));
            node.addEvent("mouseout", function (ev) {
                if( !_self.lockNodeStyle )this.node.setStyles(this.styles);
            }.bind({"styles": styles, "node":node}));
        }
        if (downStyles && ( overStyles || styles)) {
            node.addEvent("mousedown", function (ev) {
                if( !_self.lockNodeStyle )this.node.setStyles(this.styles);
            }.bind({"styles": downStyles, "node":node}));
            node.addEvent("mouseup", function (ev) {
                if( !_self.lockNodeStyle )this.node.setStyles(this.styles);
            }.bind({"styles": overStyles || styles, "node":node}))
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
        this.loadElementList();
    },
    loadElementList: function (count) {
        if( this.options.pagingEnable ){
            var currentItem = this.options.pagingPar.cloaurrentItem;
            var countPerPage = this.options.pagingPar.countPerPage;
            if( currentItem ){
                var pageNum = Math.ceil( currentItem / countPerPage );
                var itemNum = currentItem % countPerPage;
                this.loadPagingElementList( count , pageNum, currentItem );
            }else{
                this.loadPagingElementList( count , this.options.pagingPar.currentPage ); //使用分页的方式
            }
        }else{
            this.loadScrollElementList( count ); //滚动条下拉取下一页
        }
    },
    loadScrollElementList : function( count ){
        if (!this.isItemsLoaded) {
            if (!this.isItemLoadding) {
                this.isItemLoadding = true;
                this._getCurrentPageData(function (json) {
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
                                this.documents[key] = item;
                            }
                        }.bind(this));
                    }
                    this.isItemLoadding = false;

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
            this.dataCount = json.count;
            this.createPaging( json.count, pageNum );
            json.data.each(function (data ) {
                var item = this._createDocument(data, this.items.length);
                this.items.push(item);
                var key = data[ this.options.documentKeyWord || "id" ];
                this.documents[key] = item;
            }.bind(this));
            if( itemNum ){
                if( this.options.documentKeyWord ){
                    var top = this.documents[ itemNum ].node.getTop();
                }else{
                    var top = this.items[itemNum-1].node.getTop();
                }
                this.fireEvent( "gotoItem", top );
            }
        }.bind(this), count, pageNum );
    },
    createPaging : function( itemSize, pageNum ){
        if( !this.options.pagingEnable || this.paging )return;
        if( this.options.pagingPar.position.indexOf("top") > -1 ){
            if( !this.pagingContainerTop ){
                this.pagingContainerTopCreated = true;
                this.pagingContainerTop = new Element("div", {"styles":this.css.pagingContainer}).inject( this.viewNode, "before" );
            }
        }
        if( this.options.pagingPar.position.indexOf("bottom") > -1 ){
            if( !this.pagingContainerBottom ){
                this.pagingContainerBottomCreated = true;
                this.pagingContainerBottom = new Element("div", {"styles":this.css.pagingContainer}).inject( this.viewNode, "after" );
            }
        }
        var par = Object.merge( this.options.pagingPar, {
            itemSize : itemSize,
            onJumpingPage : function( par ){
                this.loadPagingElementList( this.options.pagingPar.countPerPage, par.pageNum, par.itemNum );
            }.bind(this)
        });
        if( pageNum )par.currentPage = pageNum;
        if( this.options.pagingPar.hasPagingBar ){
            this.paging = new MWF.xApplication.Template.Explorer.Paging(this.pagingContainerTop, this.pagingContainerBottom, par, this.css)
            this.paging.load();
        }
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
    _createDocument: function (data, index) {
        return new MWF.xApplication.Template.Explorer.ComplexDocument(this.viewNode, data, this.explorer, this, null,index);
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
        this.from = new MWF.xApplication.Template.Explorer.PopupForm(this.explorer);
        this.from.create();
    },
    _queryCreateViewNode: function(){

    },
    _postCreateViewNode: function( viewNode ){

    },
    _queryCreateViewHead:function(){

    },
    _postCreateViewHead: function( headNode ){

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

        this.view.template.items.each(function (item) {
            if( item.access && this._getItemAccess(item) ){
                this.loadItem(item.name, item.content, item.nodeTemplate)
            }else{
                this.loadItem(item.name, item.content, item.nodeTemplate)
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
        if (item.access) {
            if (!this._getItemAccess(item))return false;
        }
        //if (item.condition) {
        //    if (!this.getConditionResult(item.condition))return false;
        //}
        var show = this.getConditionResult( item.show );
        if( !show )itemNode.setStyle("display","none");

        var available = this.getConditionResult(item.condition);

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
        if (item.action && this[item.action]) {
            if ( available ){
                itemNode.addEvent("click", function (ev) {
                    this.fun.call(_self, this.node, ev);
                    ev.stopPropagation();
                }.bind({fun: this[item.action], node : itemNode}))
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
        if ( name == "$checkbox" ) {
            if ( available ){
                this.checkboxElement = new Element("input", {
                    "type": "checkbox"
                }).inject(itemNode);
                this.checkboxElement.addEvent("click", function (ev) {
                    ev.stopPropagation();
                }.bind(this));
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
        var lp = this.app.lp;
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
        delete this;
    },
    _queryCreateDocumentNode:function( itemData ){

    },
    _postCreateDocumentNode: function( itemNode, itemData ){

    }
});

MWF.xApplication.Template.Explorer.PopupForm = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],
    options: {
        "style": "default",
        "width": 500,
        "height": 450,
        "top": 0,
        "left": 0,
        "hasTop": false,
        "hasTopIcon" : false,
        "hasTopContent" : false,
        "hasIcon": true,
        "hasScroll" : true,
        "hasBottom": true,
        "hasMark" : true,
        "title": "",
        "draggable": false,
        "maxAction" : "false",
        "closeAction": true,
        "relativeToApp" : true,
        "sizeRelateTo" : "app" //desktop
    },
    initialize: function (explorer, data, options, para) {
        this.setOptions(options);
        this.explorer = explorer;
        if( para ){
            if( this.options.relativeToApp ){
                this.app = para.app || this.explorer.app;
                this.container = para.container || this.app.content;
                this.lp = para.lp || this.explorer.lp || this.app.lp;
                this.css = para.css || this.explorer.css || this.app.css;
                this.actions = para.actions || this.explorer.actions || this.app.actions || this.app.restActions;
            }else{
                this.container = para.container;
                this.lp = para.lp || this.explorer.lp;
                this.css = para.css || this.explorer.css;
                this.actions = para.actions || this.explorer.actions;
            }
        }else{
            if( this.options.relativeToApp ){
                this.app = this.explorer.app;
                this.container = this.app.content;
                this.lp = this.explorer.lp || this.app.lp;
                this.css = this.explorer.css || this.app.css;
                this.actions = this.explorer.actions || this.app.actions || this.app.restActions;
            }else{
                this.container = window.document.body;
                this.lp = this.explorer.lp;
                this.css = this.explorer.css;
                this.actions = this.explorer.actions;
            }
        }
        this.data = data || {};

        this.load();
    },
    load: function () {

    },

    open: function (e) {
        this.fireEvent("queryOpen");
        this.isNew = false;
        this.isEdited = false;
        this._open();
        this.fireEvent("postOpen");
    },
    create: function () {
        this.fireEvent("queryCreate");
        this.isNew = true;
        this._open();
        this.fireEvent("postCreate");
    },
    edit: function () {
        this.fireEvent("queryEdit");
        this.isEdited = true;
        this._open();
        this.fireEvent("postEdit");
    },
    _open: function () {
        if( this.options.hasMark ){
            this.formMarkNode = new Element("div.formMarkNode", {
                "styles": this.css.formMarkNode,
                "events": {
                    "mouseover": function (e) {
                        e.stopPropagation();
                    },
                    "mouseout": function (e) {
                        e.stopPropagation();
                    },
                    "click": function (e) {
                        e.stopPropagation();
                    }
                }
            }).inject( this.container || this.app.content);
        }

        this.formAreaNode = new Element("div.formAreaNode", {
            "styles": this.css.formAreaNode
        });

        this.createFormNode();

        this.formAreaNode.inject(this.formMarkNode || this.container || this.app.content, "after");
        this.formAreaNode.fade("in");

        this.setFormNodeSize();
        this.setFormNodeSizeFun = this.setFormNodeSize.bind(this);
        if( this.app )this.app.addEvent("resize", this.setFormNodeSizeFun);

        if (this.options.draggable && this.formTopNode) {
            var size = (this.container || this.app.content).getSize();
            var nodeSize = this.formAreaNode.getSize();
            this.formAreaNode.makeDraggable({
                "handle": this.formTopNode,
                "limit": {
                    "x": [0, size.x - nodeSize.x],
                    "y": [0, size.y - nodeSize.y]
                }
            });
        }

    },
    createFormNode: function () {
        var _self = this;

        this.formNode = new Element("div.formNode", {
            "styles": this.css.formNode
        }).inject(this.formAreaNode);

        if (this.options.hasTop) {
            this.createTopNode();
        }

        if (this.options.hasIcon) {
            this.formIconNode = new Element("div.formIconNode", {
                "styles": this.isNew ? this.css.formNewNode : this.css.formIconNode
            }).inject(this.formNode);
        }

        this.createContent();
        //formContentNode.set("html", html);

        if (this.options.hasBottom) {
            this.createBottomNode();
        }

        if( this.options.hasScroll ){
            //this.setScrollBar(this.formTableContainer)
            MWF.require("MWF.widget.ScrollBar", function () {
                new MWF.widget.ScrollBar(this.formTableContainer, {
                    "indent": false,
                    "style": "xApp_TaskList",
                    "where": "before",
                    "distance": 30,
                    "friction": 4,
                    "axis": {"x": false, "y": true},
                    "onScroll": function (y) {
                        //var scrollSize = _self.viewContainerNode.getScrollSize();
                        //var clientSize = _self.viewContainerNode.getSize();
                        //var scrollHeight = scrollSize.y - clientSize.y;
                        //if (y + 200 > scrollHeight && _self.view && _self.view.loadElementList) {
                        //    if (!_self.view.isItemsLoaded) _self.view.loadElementList();
                        //}
                    }
                });
            }.bind(this));
        }
    },
    createTopNode: function () {

        if (!this.formTopNode) {
            this.formTopNode = new Element("div.formTopNode", {
                "styles": this.css.formTopNode
            }).inject(this.formNode);

            if(this.options.hasTopIcon){
                this.formTopIconNode = new Element("div", {
                    "styles": this.css.formTopIconNode
                }).inject(this.formTopNode)
            }

            this.formTopTextNode = new Element("div", {
                "styles": this.css.formTopTextNode,
                "text": this.options.title
            }).inject(this.formTopNode);

            if (this.options.closeAction) {
                this.formTopCloseActionNode = new Element("div", {"styles": this.css.formTopCloseActionNode}).inject(this.formTopNode);
                this.formTopCloseActionNode.addEvent("click", function () {
                    this.close()
                }.bind(this))
            }

            if(this.options.hasTopContent){
                this.formTopContentNode = new Element("div.formTopContentNode", {
                    "styles": this.css.formTopContentNode
                }).inject(this.formTopNode);

                this._createTopContent();
            }

        }

        //if (!this.formTopNode) {
        //    this.formTopNode = new Element("div.formTopNode", {
        //        "styles": this.css.formTopNode,
        //        "text": this.options.title
        //    }).inject(this.formNode);
        //
        //    this._createTopContent();
        //
        //    if (this.options.closeAction) {
        //        this.formTopCloseActionNode = new Element("div.formTopCloseActionNode", {"styles": this.css.formTopCloseActionNode}).inject(this.formTopNode);
        //        this.formTopCloseActionNode.addEvent("click", function () {
        //            this.close()
        //        }.bind(this))
        //    }
        //}
    },
    _createTopContent: function () {

    },
    createContent: function () {
        this.formContentNode = new Element("div.formContentNode", {
            "styles": this.css.formContentNode
        }).inject(this.formNode);

        this.formTableContainer = new Element("div.formTableContainer", {
            "styles": this.css.formTableContainer
        }).inject(this.formContentNode);

        this.formTableArea = new Element("div.formTableArea", {
            "styles": this.css.formTableArea
        }).inject(this.formTableContainer);


        this._createTableContent();
    },
    _createTableContent: function () {

        var html = "<table width='100%' bordr='0' cellpadding='5' cellspacing='0' styles='formTable'>" +
                //"<tr><td colspan='2' styles='formTableHead'>申诉处理单</td></tr>" +
            "<tr><td styles='formTableTitle' lable='empName'></td>" +
            "    <td styles='formTableValue' item='empName'></td></tr>" +
            "<tr><td styles='formTableTitle' lable='departmentName'></td>" +
            "    <td styles='formTableValue' item='departmentName'></td></tr>" +
            "<tr><td styles='formTableTitle' lable='recordDateString'></td>" +
            "    <td styles='formTableValue' item='recordDateString'></td></tr>" +
            "<tr><td styles='formTableTitle' lable='status'></td>" +
            "    <td styles='formTableValue' item='status'></td></tr>" +
            "<tr><td styles='formTableTitle' lable='appealReason'></td>" +
            "    <td styles='formTableValue' item='appealReason'></td></tr>" +
            "<tr><td styles='formTableTitle' lable='appealDescription'></td>" +
            "    <td styles='formTableValue' item='appealDescription'></td></tr>" +
            "<tr><td styles='formTableTitle' lable='opinion1'></td>" +
            "    <td styles='formTableValue' item='opinion1'></td></tr>" +
            "</table>"
        this.formTableArea.set("html", html);

        MWF.xDesktop.requireApp("Template", "MForm", function () {
            this.form = new MForm(this.formTableArea, {empName: "xadmin"}, {
                isEdited: this.isEdited || this.isNew,
                itemTemplate: {
                    empName: {text: "姓名", type: "innertext"},
                    departmentName: {text: "部门", tType: "department", notEmpty: true},
                    recordDateString: {text: "日期", tType: "date"},
                    status: {text: "状态", tType: "number"},
                    appealReason: {
                        text: "下拉框",
                        type: "select",
                        selectValue: ["测试1", "测试2"]
                    },
                    appealDescription: {text: "描述", type: "textarea"},
                    opinion1: {text: "测试", type: "button", "value": "测试"}
                }
            }, this.app);
            this.form.load();
        }.bind(this), true);
    },
    createBottomNode: function () {
        this.formBottomNode = new Element("div.formBottomNode", {
            "styles": this.css.formBottomNode
        }).inject(this.formNode);

        this._createBottomContent()
    },
    _createBottomContent: function () {
        this.cancelActionNode = new Element("div.formCancelActionNode", {
            "styles": this.css.formCancelActionNode,
            "text": this.lp.cancel
        }).inject(this.formBottomNode);


        this.cancelActionNode.addEvent("click", function (e) {
            this.cancel(e);
        }.bind(this));

        if (this.isNew || this.isEdited) {

            this.okActionNode = new Element("div.formOkActionNode", {
                "styles": this.css.formOkActionNode,
                "text": this.lp.ok
            }).inject(this.formBottomNode);

            this.okActionNode.addEvent("click", function (e) {
                this.ok(e);
            }.bind(this));
        }
    },
    cancel: function (e) {
        this.fireEvent("queryCancel");
        this.close();
        this.fireEvent("postCancel");
    },
    close: function (e) {
        this.fireEvent("queryClose");
        this._close();
        if(this.setFormNodeSizeFun && this.app ){
            this.app.removeEvent("resize",this.setFormNodeSizeFun);
        }
        if( this.formMarkNode )this.formMarkNode.destroy();
        this.formAreaNode.destroy();
        this.fireEvent("postClose");
        delete this;
    },
    _close: function(){

    },
    ok: function (e) {
        this.fireEvent("queryOk");
        var data = this.form.getResult(true, ",", true, false, true);
        if (data) {
            this._ok(data, function (json) {
                if (json.type == "error") {
                    if( this.app )this.app.notice(json.userMessage, "error");
                } else {
                    if( this.formMarkNode )this.formMarkNode.destroy();
                    this.formAreaNode.destroy();
                    if (this.explorer && this.explorer.view)this.explorer.view.reload();
                    if( this.app )this.app.notice(this.isNew ? this.lp.createSuccess : this.lp.updateSuccess, "success");
                    this.fireEvent("postOk");
                }
            }.bind(this))
        }
    },
    _ok: function (data, callback) {
        //this.app.restActions.saveDocument( this.data.id, data, function(json){
        //    if( callback )callback(json);
        //}.bind(this), function( errorObj ){
        //    var error = JSON.parse( errorObj.responseText );
        //    this.app.notice( error.message, error );
        //}.bind(this));
    },
    setFormNodeSize: function (width, height, top, left) {
        if (!width)width = this.options.width ? this.options.width : "50%";
        if (!height)height = this.options.height ? this.options.height : "50%";
        if (!top) top = this.options.top ? this.options.top : 0;
        if (!left) left = this.options.left ? this.options.left : 0;

        //var appTitleSize = this.app.window.title.getSize();

        var allSize = ( this.container || this.app.content).getSize();
        var limitWidth = allSize.x; //window.screen.width
        var limitHeight = allSize.y; //window.screen.height

        "string" == typeof width && (1 < width.length && "%" == width.substr(width.length - 1, 1)) && (width = parseInt(limitWidth * parseInt(width, 10) / 100, 10));
        "string" == typeof height && (1 < height.length && "%" == height.substr(height.length - 1, 1)) && (height = parseInt(limitHeight * parseInt(height, 10) / 100, 10));
        300 > width && (width = 300);
        220 > height && (height = 220);

        top = top || parseInt((limitHeight - height) / 2, 10); //+appTitleSize.y);
        left = left || parseInt((limitWidth - width) / 2, 10);

        this.formAreaNode.setStyles({
            "width": "" + width + "px",
            "height": "" + height + "px",
            "top": "" + top + "px",
            "left": "" + left + "px"
        });

        this.formNode.setStyles({
            "width": "" + width + "px",
            "height": "" + height + "px"
        });

        var iconSize = this.formIconNode ? this.formIconNode.getSize() : {x: 0, y: 0};
        var topSize = this.formTopNode ? this.formTopNode.getSize() : {x: 0, y: 0};
        var bottomSize = this.formBottomNode ? this.formBottomNode.getSize() : {x: 0, y: 0};

        var contentHeight = height - iconSize.y - topSize.y - bottomSize.y;
        //var formMargin = formHeight -iconSize.y;
        this.formContentNode.setStyles({
            "height": "" + contentHeight + "px"
        });
        this.formTableContainer.setStyles({
            "height": "" + contentHeight + "px"
        });
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
        hasReturn : true,
        returnText : "返回首页"
    },
    initialize: function (topContainer, bottomContainer, options, css) {
        this.setOptions( options || {});
        this.topContainer = topContainer;
        this.bottomContainer = bottomContainer;
        this.css = css;
    },
    load : function(){
        this.fireEvent( "queryLoad", this);
        this.options.pageSize = Math.ceil(this.options.itemSize/this.options.countPerPage);

        if( (this.options.pageSize == 1 || this.options.pageSize == 0) && !this.options.hasReturn )return;

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

        var node = new Element("div.pagingBar", { styles : this.css.pagingBar }  ).inject( container );

        if( this.options.hasReturn ){
            var pageReturn = new Element( "div.pageReturn" , { styles : this.css.pageReturn , "text" : this.options.returnText } ).inject(node);
            pageReturn.addEvents( {
                "mouseover" : function( ev ){ ev.target.setStyles( this.css.pageReturn_over ) }.bind(this),
                "mouseout" : function( ev ){ ev.target.setStyles( this.css.pageReturn ) }.bind(this),
                "click" : function(){ this.fireEvent( "pageReturn" , this ) }.bind(this)
            })
        }

        if( pageSize != 1 && pageSize != 0 ){
            if( currentPage != 1 ){
                var prePage = new Element( "div.prePage" , { styles : this.css.prePage } ).inject(node);
                prePage.addEvents( {
                    "mouseover" : function( ev ){ ev.target.setStyles( this.css.prePage_over ) }.bind(this),
                    "mouseout" : function( ev ){ ev.target.setStyles( this.css.prePage ) }.bind(this),
                    "click" : function(){ this.gotoPage( currentPage-1 ) }.bind(this)
                } )
            }
            if( min > 1 ){
                var firstPage = new Element( "div.pageItem" , { styles : this.css.pageItem, text : "1..."  }).inject(node);
                firstPage.addEvents( {
                    "mouseover" : function( ev ){ ev.target.setStyles( this.css.pageItem_over ) }.bind(this),
                    "mouseout" : function( ev ){ ev.target.setStyles( this.css.pageItem ) }.bind(this),
                    "click" : function(){ this.gotoPage(1) }.bind(this)
                } )
            }


            for( i=min; i<=max; i++ ){
                if( currentPage == i ){
                    new Element("div.currentPage", {"styles" : this.css.currentPage, "text" : i }).inject(node);
                }else{
                    var pageTurnNode = new Element("div.pageItem", {"styles" : this.css.pageItem, "text" : i }).inject(node);
                    pageTurnNode.addEvents( {
                        "mouseover" : function( ev ){ ev.target.setStyles( this.css.pageItem_over ) }.bind(this),
                        "mouseout" : function( ev ){ ev.target.setStyles( this.css.pageItem ) }.bind(this),
                        "click" : function(){ this.obj.gotoPage( this.num ) }.bind({ obj : this, num : i })
                    })
                }
            }
            var pageJumper = new Element("input.pageJumper", {"styles" : this.css.pageJumper , "title" : "输入页码，按回车跳转"}).inject( node );
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

            if( max < pageSize ){
                var lastPage = new Element( "div.pageItem" , { styles : this.css.pageItem, text : "..." + pageSize  }).inject(node);
                lastPage.addEvents( {
                    "mouseover" : function( ev ){ ev.target.setStyles( this.css.pageItem_over ) }.bind(this),
                    "mouseout" : function( ev ){ ev.target.setStyles( this.css.pageItem ) }.bind(this),
                    "click" : function(){ this.gotoPage( pageSize ) }.bind(this)
                } )
            }
            if( currentPage != pageSize ){
                var nextPage = new Element( "div.nextPage" , { styles : this.css.nextPage } ).inject(node);
                nextPage.addEvents( {
                    "mouseover" : function( ev ){ ev.target.setStyles( this.css.nextPage_over ) }.bind(this),
                    "mouseout" : function( ev ){ ev.target.setStyles( this.css.nextPage ) }.bind(this),
                    "click" : function(){ this.gotoPage(  currentPage+1 ) }.bind(this)
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
                "text" : "下一页"
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
                "text" : "上一页"
            }).inject(container);
            this.prevPageNode.addEvents( {
                "mouseover" : function( ev ){ ev.target.setStyles( this.css.prevPageNode_over ) }.bind(this),
                "mouseout" : function( ev ){ ev.target.setStyles( this.css.prevPageNode ) }.bind(this),
                "click" : function(){ this.gotoPage(  this.options.currentPage-1 ) }.bind(this)
            })
        }
    },
    gotoPage : function( num ){
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
        delete this;
    }
});