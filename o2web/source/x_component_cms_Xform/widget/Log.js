MWF.xApplication.cms.Xform.widget = MWF.xApplication.cms.Xform.widget || {};
MWF.xApplication.cms.Xform.widget.Log = new Class({
    Implements: [Options, Events],
    Extends: MWF.widget.Common,
    options: {
        "style": "default",
        "mode" : "table",
        "documentId" : "",
        "textStyle" : ""
    },
    initialize: function (app, node, options) {
        this.setOptions(options);
        this.app = app;
        this.node = node;
        this.path = "../x_component_cms_Xform/widget/$Log/";
        this.cssPath = "../x_component_cms_Xform/widget/$Log/" + this.options.style + "/css.wcss";
        this._loadCss();

        MWF.xDesktop.requireApp("cms.Xform", "lp."+MWF.language, null, false);
        this.lp = MWF.xApplication.cms.Xform.LP;
    },
    load : function(){
        this.items = [];
        this.documents = {};
        this.isItemsLoaded = false;
        this.isItemLoadding = false;
        this.loadItemQueue = 0;
        this.count = 0;
        this.lineHeight = this.options.mode != "text" ? 32 : 25;
        this.countPerPage = 20;

        this.container = new Element("div",{styles:this.css.container}).inject( this.node );
        this.loadTitle();
        this.loadContent();
        this.loadElementList();
        this.loadBottom();
    },
    loadTitle : function(){
        this.titleNode = new Element("div", {"styles": this.css.titleNode, "text": this.lp.readedLogTitle}).inject(this.container);
    },
    loadTotal: function(){
        this.getAction( function(){
            if (this.options.documentId){
                this.restAction.invoke({"name": "getReadCount","async": true, "parameter": {"docId": this.options.documentId },"success": function(json){
                    if( json && json.data ){
                        this.titleCountNode = new Element("div", {
                            styles : this.css.titleCountNode,
                            text : this.lp.readedCountText.replace("{count}" , json.data.count).replace("{person}", this.dataCount )
                        }).inject(this.titleNode);
                    }
                }.bind(this)});
            }
        }.bind(this));
    },
    loadContent : function(){
        //this.contentContainerNode = new Element("div", {"styles": this.css.contentContainerNode }).inject(this.container);
        this.contentScrollNode = new Element("div", {"styles": this.css.contentScrollNode }).inject(this.container);
        this.contentScrollNode.setStyles({
            "width" : this.node.getSize().x-10,
            "margin-right" : "10px"
        });
        this.contentWrapNode = new Element("div", {"styles": this.css.contentWrapNode }).inject(this.contentScrollNode);
        this.setScroll();
        if( this.options.mode == "table" ){
            this.loadItemTitleTable();
        }
    },
    loadBottom: function(){
       var bottomNode = new Element("div",{
            "styles" : this.css.bottomNode
        }).inject( this.container );
        var resizeNode = new Element("div",{
            "styles" : this.css.bottomResizeNode,
            "text" : "◢"
        }).inject(bottomNode);

        var xLimit = this.contentScrollNode.getSize().x;

       this.contentScrollNode.makeResizable({
            "handle": resizeNode,
            "limit": {x:[xLimit, xLimit], y:[50, null]},
            "onDrag": function(){
                var y = this.contentScrollNode.getSize().y;
                if( y > ( this.lineHeight * this.countPerPage - 20 ) ){
                    this.countPerPage = parseInt( y / this.lineHeight ) + 2
                }
                this.contentScrollNode.fireEvent("resize");
            }.bind(this),
            "onComplete": function(){
                this.scrollBar.checkScroll();
                this.loadElementList();
            }.bind(this)
        });
    },
    loadElementList : function( count ){
        if (!this.isItemsLoaded) {
            if (!this.isItemLoadding) {
                this.isItemLoadding = true;
                this._getCurrentPageData(function (json) {
                    var length = this.dataCount = json.count;  //|| json.data.length;
                    if( !this.titleCountNode ){
                        this.loadTotal();
                    }
                    if( this.items.length == 0 )this.setSize();
                    if (length <= this.items.length) {
                        this.isItemsLoaded = true;
                    }
                    if( json.data && typeOf( json.data )=="array" ){
                        json.data.each(function (data ) {
                            var key = data.id;
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
    _getCurrentPageData : function(callback){
        var id = (this.items.length) ? this.items[this.items.length - 1].data.id : "(0)";
        this.getAction( function(){
            if (this.options.documentId){
                this.restAction.invoke({"name": "listReadedLog","async": true, "parameter": { "docId": this.options.documentId, "id": id, "count":this.countPerPage },"success": function(json){
                    if (callback) callback(json);
                }.bind(this)});
            }
        }.bind(this));
    },
    getAction: function(callback){
        if (!this.action){
            MWF.require("MWF.xDesktop.Actions.RestActions", function(){
                this.restAction = new MWF.xDesktop.Actions.RestActions("", "x_cms_assemble_control", "");
                this.restAction.getActions = function(actionCallback){
                    this.actions = {
                        "getReadCount" : {"uri":"/jaxrs/document/{docId}/view/count"},
                        "listReadedLog": {"uri": "/jaxrs/viewrecord/document/{docId}/filter/list/{id}/next/{count}", "method":"GET"}
                    };
                    if (actionCallback) actionCallback();
                };
                if (callback) callback();
            }.bind(this));
        }else{
            if (callback) callback();
        }
    },
    setSize : function(){
        var lineHeight = this.lineHeight;
        var maxCount = this.options.mode == "text" ? 10 : 8;
        if( this.dataCount > maxCount ){
            var height = maxCount*lineHeight - 15;
        }else if( this.dataCount <= 1 ){
            var height = 1*lineHeight;
        }else{
            var height = this.dataCount*lineHeight;
        }
        if( this.options.mode != "text" )height = height + lineHeight;
        this.contentScrollNode.setStyle("height", height+"px");
    },
    setScroll: function(){
        MWF.require("MWF.widget.ScrollBar", function () {
            this.scrollBar = new MWF.widget.ScrollBar(this.contentScrollNode, {
                "indent": false,
                "style": "default",
                "where": "before",
                "distance": 60,
                "friction": 4,
                "axis": {"x": false, "y": true},
                "onScroll": function (y) {
                    var scrollSize = this.contentScrollNode.getScrollSize();
                    var clientSize = this.contentScrollNode.getSize();
                    var scrollHeight = scrollSize.y - clientSize.y;
                    if (y + 30 > scrollHeight ) {
                        if (! this.isItemsLoaded) this.loadElementList();
                    }
                }.bind(this)
            });
        }.bind(this));
    },
    _createDocument: function( data ){
        var itemNode;
        if( this.options.mode == "text" ){
            if( this.options.textStyle ){
                itemNode = this.loadItemNodeText( data );
            }else{
                itemNode = this.loadItemNodeDefault( data );
            }
        }else{
            itemNode = this.loadItemNodeTable( data );
        }
        return {
            node : itemNode,
            data : data
        }
    },
    loadItemTitleTable: function(){

        var xSize = this.contentScrollNode.getSize().x;
        this.table = new Element("table", {
            "styles": this.css.logTable,
            "border": "0",
            "cellSpacing": "0",
            "cellpadding": "3px",
            "width": xSize - 10
        }).inject( this.contentWrapNode );
        this.tbody = new Element("tbody").inject( this.table );

        this.table.setStyles({
            "margin-left" : "8px"
        });
        var tr = new Element("tr").inject( this.tbody );
        tr.setStyles(this.css.logTableTitleTr);

        var td = new Element("td", { styles : this.css.logTableTitle }).inject( tr );
        td.set("text", this.lp.person);
        var td = new Element("td", { styles : this.css.logTableTitle }).inject( tr );
        td.set("text", this.lp.department);
        var td = new Element("td", { styles : this.css.logTableTitle }).inject( tr );
        td.set("text", this.lp.firstDate);
        var td = new Element("td", { styles : this.css.logTableTitle }).inject( tr );
        td.set("text", this.lp.readDate);
        var td = new Element("td", { styles : this.css.logTableTitle }).inject( tr );
        td.set("text", this.lp.readCount);
    },
    loadItemNodeTable: function(data){

        var tr = new Element("tr").inject( this.tbody );
        tr.setStyles(this.css.logTableContentTr);

        var td = new Element("td", { styles : this.css.logTableContent }).inject( tr );
        td.set("text", this.getShortName(data.viewerName) || "");
        td = new Element("td", { styles : this.css.logTableContent }).inject( tr );
        td.set("text", this.getShortName(data.viewerUnitName) || "");
        td = new Element("td", { styles : this.css.logTableContent }).inject( tr );
        td.set("text", data.createTime);
        td = new Element("td", { styles : this.css.logTableContent }).inject( tr );
        td.set("text", data.lastViewTime);
        td = new Element("td", { styles : this.css.logTableContent }).inject( tr );
        td.set("text", data.viewCount);
    },
    loadItemNodeText: function(data, textStyle){
        var itemNode =  new Element("div",{ "styles" : this.css.defaultItemNode  }).inject(this.contentWrapNode);
        var html = textStyle || this.options.textStyle;
        html = html.replace(/\{person\}/g, this.getShortName( data.viewerName));
        html = html.replace(/\{unitName\}/g, this.getShortName( data.viewerUnitName ) || "");
        html = html.replace(/\{topUnitName\}/g, this.getShortName( data.viewerTopUnitName ) || "");
        html = html.replace(/\{firstDate\}/g, data.createTime);
        html = html.replace(/\{date\}/g, data.lastViewTime);
        html = html.replace(/\{count\}/g, data.viewCount);
        itemNode.set("html", html);

        return itemNode;
    },
    loadItemNodeDefault: function( data ){
        //var itemNode =  new Element("div",{ "styles" : this.css.defaultItemNode  }).inject(this.contentWrapNode);
        //var personNode = new Element("div",{ styles : this.css.defaultItemPersonNode ,text : data.viewerName }).inject(itemNode);
        //if(data.viewerOrganization){
        //    var departmentNode = new Element("div",{ styles : this.css.defaultItemDepartmentNode ,text : "（"+data.viewerOrganization+"）" }).inject(itemNode);
        //}
        //
        //new Element("div",{ styles : this.css.defaultItemTextNode , text : this.lp.at }).inject(itemNode);
        //var timeNode = new Element("div",{ styles : this.css.defaultItemTimeNode , text : data.lastViewTime }).inject(itemNode);
        //new Element("div",{ styles : this.css.defaultItemTextNode , text : this.lp.readdDocument }).inject(itemNode);
        //
        //new Element("div",{ styles : this.css.defaultItemTextNode , text : this.lp.historyRead }).inject(itemNode);
        //var countNode = new Element("div",{ styles : this.css.defaultItemCountNode ,text : data.viewCount }).inject(itemNode);
        //new Element("div",{ styles : this.css.defaultItemTextNode , text : this.lp.times }).inject(itemNode);
        return this.loadItemNodeText( data, this.lp.defaultReadedLogText );
    },
    getShortName : function( dn ){
        if( dn && dn.contains("@") ){
            return dn.split("@")[0];
        }else{
            return dn;
        }
    }
});