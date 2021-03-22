MWF.xApplication.cms = MWF.xApplication.cms || {};
MWF.xApplication.cms.Module = MWF.xApplication.cms.Module || {};

MWF.require("MWF.xAction.org.express.RestActions", null,false);
MWF.require("MWF.widget.Mask", null, false);
MWF.require("MWF.widget.O2Identity", null,false);
MWF.xDesktop.requireApp("cms.Module", "lp."+MWF.language, null, false);
MWF.xDesktop.requireApp("cms.Module", "package", null, false);

MWF.xApplication.cms.Module.ListExplorer = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],
    options: {
        "style": "default",
        "status": "published",
        "isAdmin": false,
        "searchKey" : "",
        "tooltip": {
        }
    },

    initialize: function(node, actions, columnData, categoryData, revealData, options, searchNode){
        this.setOptions(options);
        this.setTooltip();
        this.path = "../x_component_cms_Module/$ListExplorer/";
        this.cssPath = "../x_component_cms_Module/$ListExplorer/"+this.options.style+"/css.wcss";
        this._loadCss();

        this.categoryData = categoryData;
        this.columnData = columnData;
        this.revealData = revealData;

        this.actions = actions;
        this.node = $(node);
        this.searchNode = $(searchNode);
        this.initData();
        if (!this.personActions) this.personActions = MWF.Actions.get("x_organization_assemble_express");
    },
    setTooltip: function(tooltip){
        if (tooltip) this.options.tooltip = Object.merge(this.options.tooltip, tooltip);
    },
    initData: function(){
        this.toolItemNodes = [];
    },
    reload: function(){
        this.node.empty();
        this.searchNode.empty();
        this.load();
    },
    load: function(){

        //this.toolbarNode = new Element("div", {"styles": this.css.toolbarNode});
        //this.toolbarNode.inject(this.node);

        //this.loadToolbar();

        //this.filterConditionNode = new Element("div", {
        //    "styles": this.css.filterConditionNode
        //}).inject(this.node );

        this.loadSearchNode();
        this.loadViewData( function(){
            this.loadContentNode();

            this.loadView();
            this.setNodeScroll();
        }.bind(this))


    },
    loadViewData : function( callback ){
        if( this.revealData.isAll ){
            if( callback )callback()
        }else if( this.revealData.id == "defaultList" ){
            if( callback )callback()
        }else if( this.revealData.type == "list" ){
            this.app.restActions.getView( this.revealData.id, function( json ){
                var viewData = this.viewData = json.data;
                if( viewData.content && typeof(viewData.content)=="string"){
                    viewData.content = JSON.parse(viewData.content);
                }
                if( callback )callback()
            }.bind(this))
        }
    },
    loadToolbar: function(){
        var toolbarUrl = this.path+"toolbar.json";
        MWF.getJSON(toolbarUrl, function(json){
            json.each(function(tool){
                this.createToolbarItemNode(tool);
            }.bind(this));
        }.bind(this));
        //this.createSearchElementNode();
    },
    createToolbarItemNode : function( tool ){
        var toolItemNode = new Element("div", {
            "styles": (tool.styles && this.css[tool.styles]) ? this.css[tool.styles] : this.css.toolbarItemNode
        });
        toolItemNode.store("toolData", tool );

        var iconNode =  new Element("div", {
            "styles": this.css.toolbarItemIconNode
        }).inject(toolItemNode);
        iconNode.setStyle("background-image", "url("+this.path+this.options.style+"/icon/"+tool.icon+")");

        var textNode =  new Element("div", {
            "styles": this.css.toolbarItemTextNode,
            "text": tool.title
        });
        textNode.inject(toolItemNode);
        toolItemNode.inject(this.toolbarNode);

        this.toolItemNodes.push(toolItemNode);

        this.setToolbarItemEvent(toolItemNode);

        //this.setNodeCenter(this.node);

    },
    setToolbarItemEvent:function(toolItemNode){
        var _self = this;
        toolItemNode.addEvents({
            "click": function () {
                var data = this.retrieve("toolData");
                if( _self[data.action] )_self[data.action].apply(_self,[this]);
            }
        })
    },

    loadContentNode: function(){
        this.elementContentNode = new Element("div", {
            "styles": this.css.elementContentNode
        }).inject(this.node);
        this.app.addEvent("resize", function(){this.setContentSize();}.bind(this));

    },
    loadView : function(){
        if( this.revealData.isAll ){
            this.view = new MWF.xApplication.cms.Module.ListExplorer.ListForALL(this.elementContentNode, this.app,this, this.viewData, this.options.searchKey );
        }else if( this.revealData.isDraft ){
            this.view = new MWF.xApplication.cms.Module.ListExplorer.ListForDraft(this.elementContentNode, this.app,this, this.viewData, this.options.searchKey );

        }else if( (this.revealData.id == "defaultList") || (this.options.searchKey && this.options.searchKey!="") ){
            this.view = new MWF.xApplication.cms.Module.ListExplorer.DefaultList(this.elementContentNode, this.app,this, this.viewData, this.options.searchKey );
        }else{
            this.view = new MWF.xApplication.cms.Module.ListExplorer.List(this.elementContentNode, this.app,this, this.viewData );
        }
        if(this.selectEnable)this.view.selectEnable = this.selectEnable;
        this.view.load( function(){
            this.setContentSize();
        }.bind(this));
    },

    setContentSize: function(){
        var toolbarSize = this.toolbarNode ? this.toolbarNode.getSize() : {"x":0,"y":0};
        var titlebarSize = this.app.titleBar ? this.app.titleBar.getSize() : {"x":0,"y":0};
        var nodeSize = this.app.node.getSize();
        var pt = 0; //this.elementContentNode.getStyle("padding-top").toFloat();
        var pb = 0; // this.elementContentNode.getStyle("padding-bottom").toFloat();

        var filterConditionSize = this.filterConditionNode ? this.filterConditionNode.getSize() : {"x":0,"y":0};

        var height = nodeSize.y-toolbarSize.y-pt-pb-filterConditionSize.y-titlebarSize.y;
        this.elementContentNode.setStyle("height", ""+height+"px");

        this.pageCount = (height/40).toInt()+5;

        if (this.view.items.length<this.pageCount ){
            if( typeOf(this.view.itemDataCount) === "number" ){
                if( this.view.items.length<this.view.itemDataCount ){
                    this.view.loadElementList(this.pageCount-this.view.items.length);
                }
            }else{
                this.view.loadElementList(this.pageCount-this.view.items.length);
            }
        }
    },
    setNodeScroll: function(){
        var _self = this;
        MWF.require("MWF.widget.ScrollBar", function(){
            new MWF.widget.ScrollBar(this.elementContentNode, {
                "indent": false,"style":"xApp_TaskList", "where": "before", "distance": 30, "friction": 4,	"axis": {"x": false, "y": true},
                "onScroll": function(y){
                    var scrollSize = _self.elementContentNode.getScrollSize();
                    var clientSize = _self.elementContentNode.getSize();
                    var scrollHeight = scrollSize.y-clientSize.y;
                    if (y+200>scrollHeight) {
                        if (!_self.view.isItemsLoaded) _self.view.loadElementList();
                    }
                }
            });
        }.bind(this));
    },

    loadFileter : function( actionNode ){
        //if(!this.filterNode)this.filterNode = new Element("div", {"styles": this.css.filterNode}).inject(this.elementContentNode);
        this._loadFileter( actionNode );
    },
    _loadFileter : function( actionNode ){
        if( !this.filter ){
            this.filter = new MWF.xApplication.cms.Module.Module.Filter(this.app, this,this.toolbarNode, actionNode, this.filterConditionNode, this.actions, this.css );
            this.filter.load();
        }else{
            this.filter.load();
        }
    },
    loadSearchNode: function(){
        this.searchBarAreaNode = new Element("div", {
            "styles": this.css.searchBarAreaNode
        }).inject(this.searchNode);

        this.searchBarNode = new Element("div", {
            "styles": this.css.searchBarNode
        }).inject(this.searchBarAreaNode);

        this.searchBarActionNode = new Element("div", {
            "styles": this.css.searchBarActionNode
        }).inject(this.searchBarNode);
        this.searchBarResetActionNode = new Element("div", {
            "styles": this.css.searchBarResetActionNode
        }).inject(this.searchBarNode);
        this.searchBarResetActionNode.setStyle("display","none");

        this.searchBarInputBoxNode = new Element("div", {
            "styles": this.css.searchBarInputBoxNode
        }).inject(this.searchBarNode);
        this.searchBarInputNode = new Element("input", {
            "type": "text",
            "value": this.options.searchKey!="" ? this.options.searchKey : this.app.lp.searchKey,
            "styles": this.css.searchBarInputNode
        }).inject(this.searchBarInputBoxNode);

        var _self = this;
        this.searchBarActionNode.addEvent("click", function(){
            this.search();
        }.bind(this));
        this.searchBarResetActionNode.addEvent("click", function(){
            this.reset();
        }.bind(this));
        this.searchBarInputNode.addEvents({
            "focus": function(){
                if (this.value==_self.app.lp.searchKey) this.set("value", "");
            },
            "blur": function(){if (!this.value) this.set("value", _self.app.lp.searchKey);},
            "keydown": function(e){
                if (e.code==13){
                    this.search();
                    e.preventDefault();
                }
            }.bind(this),
            "selectstart": function(e){
                e.preventDefault();
            }
        });
    },
    search : function( key ){
        if(!key)key = this.searchBarInputNode.get("value");
        if(key==this.app.lp.searchKey)key="";
        if( key!="" ){
            this.searchBarResetActionNode.setStyle("display","block");
            this.searchBarActionNode.setStyle("display","none");
        }

        this.options.searchKey = key;

        this.clearContentNode();
        this.loadView();

    },
    clearContentNode : function(){
        this.elementContentNode.empty();
    },
    reset : function(){
        this.searchBarInputNode.set("value",this.app.lp.searchKey);
        this.searchBarResetActionNode.setStyle("display","none");
        this.searchBarActionNode.setStyle("display","block");

        this.options.searchKey = "";
        this.clearContentNode();
        this.loadView();
    },
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
    }
});

MWF.xApplication.cms.Module.ListExplorer.DefaultList = new Class({

    initialize: function( container, app,explorer, data, searchKey ){
        this.container = container;
        this.app = app;
        this.explorer = explorer;
        this.css = explorer.css;
        this.actions = explorer.actions;
        this.data = data;
        this.searchKey = searchKey;
    },
    initData: function(){
        this.items=[];
        this.documents = {};
        this.isItemsLoaded = false;
        this.isItemLoadding = false;
        this.loadItemQueue = 0;
        //this.controllers =[];
    },
    load : function( callback ){
        this.initData();

        this.node = new Element("div", {
            "styles": this.css.elementContentListNode
        }).inject(this.container);

        this.table = new Element("table",{ "width" : "100%", "border" : "0", "cellpadding" : "5", "cellspacing" : "0",  "class" : "editTable"}).inject(this.node);
        this.initSortData();
        this.createListHead();
        this.loadElementList( null,  callback );
    },
    initSortData : function(){
    },
    clear: function(){
        this.documents = null;
        MWF.release(this.items);
        this.items=[];
        this.documents = {};
        this.container.empty();
        this.isItemsLoaded = false;
        this.isItemLoadding = false;
        this.loadItemQueue = 0;
    },
    reload: function(){
        this.clear();
        this.node = new Element("div", {
            "styles": this.css.elementContentListNode
        }).inject(this.container);
        this.table = new Element("table",{ "width" : "100%", "border" : "0", "cellpadding" : "5", "cellspacing" : "0",  "class" : "editTable"}).inject(this.node);
        this.createListHead();
        this.loadElementList();
    },
    resort : function(th){
        debugger;
        this.orderField = th.retrieve("field");
        var orderType = th.retrieve("orderType");
        //th.eliminate(orderType);
        if( orderType == "" ){
            this.orderType = "asc";
        }else if( this.orderType == "asc" ){
            this.orderType = "desc";
        }else{
            this.orderField = ""; //this.data.orderField ? this.data.orderField : "";
            this.orderType = ""; //this.data.orderType ? this.data.orderType : "";
        }
        this.reload();
    },

    createListHead : function(){
        var _self = this;
        var headNode = this.headNode = new Element("tr", {"styles": this.css.listHeadNode}).inject(this.table);

        if( this.selectEnable ){
            this.createSelectTh();
        }

        var listItemUrl = this.explorer.path+"listItem.json";
        MWF.getJSON(listItemUrl, function(json){
            this.listItemTemplate = json;
            json.each(function(cell){
                var isShow = true;
                if( cell.access ){
                    if( cell.access == "admin" && !this.explorer.options.isAdmin ){
                        isShow = false;
                    }
                }
                if(isShow) {
                    var th = new Element("th", {
                        "styles": this.css[cell.headStyles],
                        "width": cell.width,
                        "text": cell.title
                    }).inject(headNode);
                    //var thText = new Element("div",{
                    //    "styles" : this.css.thTextNode,
                    //    "text": cell.title
                    //}).inject(th);
                    if( cell.sortByClickTitle == "yes" ){
                        th.store("field",cell.item);
                        if( this.orderField  == cell.item && this.orderType!="" ){
                            th.store("orderType",this.orderType);
                            this.sortIconNode = new Element("div",{
                                "styles": this.orderType == "asc" ? this.css.sortIconNode_asc : this.css.sortIconNode_desc
                            }).inject( th, "bottom" );
                        }else{
                            th.store("orderType","");
                            this.sortIconNode = new Element("div",{"styles":this.css.sortIconNode}).inject( th, "bottom" );
                        }
                        th.setStyle("cursor","pointer");
                        th.addEvent("click",function(){
                            _self.resort( this );
                        })
                    }
                }
            }.bind(this));
        }.bind(this),false);
    },
    selectMode : function(){
        this.selectEnable = true;
        this.createSelectTh();
        this.items.each( function (it) {
            it.createSelectTd();
        }.bind(this));
    },
    disableSelectMode : function(){
        this.selectEnable = false;
        this.destroySelectTh();
        this.items.each( function (it) {
            it.destroySelectTd();
        }.bind(this));
    },
    destroySelectTh : function(){
        if( this.selectTh ){
            this.selectTh.destroy();
            this.selectTh = null;
        }
    },
    createSelectTh : function(){
        this.selectTh = new Element("th",{styles:{width:"20px"}}).inject(this.headNode, "top");
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
            if (it.checkboxElement)it.checkboxElement.set("checked", flag)
        }.bind(this))
    },
    getSelectedIds : function(){
        var checkedItems = [];
        this.items.each(function (it) {
            if (it.checkboxElement.get("checked")) {
                checkedItems.push( it.data.id )
            }
        }.bind(this));
        return checkedItems;
    },
    getSelectedItems : function(){
        var checkedItems = [];
        this.items.each(function (it) {
            if (it.checkboxElement.get("checked")) {
                checkedItems.push( it )
            }
        }.bind(this));
        return checkedItems;
    },
    loadElementList: function(count, callback){
        if (!this.isItemsLoaded){
            if (!this.isItemLoadding){
                this.isItemLoadding = true;
                if( this.itemDataCount && this.itemDataCount <= this.items.length ){
                    this.isItemsLoaded = true;
                    return;
                }
                this._getCurrentPageData(function(json){
                    this.count = json.count;
                    this.itemDataCount = json.count;

                    //if (!this.isCountShow){
                    //    this.filterAllProcessNode.getFirst("span").set("text", "("+this.count+")");
                    //    this.isCountShow = true;
                    //}
                    if (json.count<=this.items.length){
                        this.isItemsLoaded = true;
                    }
                    json.data.each(function(data){
                        if (!this.documents[data.id]){
                            var item = this._createItem(data);
                            this.items.push(item);
                            this.documents[data.id] = item;
                        }
                    }.bind(this));

                    this.isItemLoadding = false;

                    if (this.loadItemQueue>0){
                        this.loadItemQueue--;
                        this.loadElementList();
                    }
                    if(callback)callback();
                }.bind(this), count);
            }else{
                this.loadItemQueue++;
            }
        }
    },
    _getCurrentPageData: function(callback, count){
        if(!count)count=20;
        var id = (this.items.length) ? this.items[this.items.length-1].data.id : "(0)";
        var data = {
            "categoryIdList": [this.explorer.categoryData.id ],
            "statusList": [this.explorer.options.status],
            "orderField" : this.orderField || "publishTime",
            "orderType" : this.orderType || "desc"
        };
        if( this.searchKey && this.searchKey!="" ){
            data.title = this.searchKey;
        }
        if (this.filter && this.filter.filter ){
            var filterResult = this.filter.getFilterResult();
            for(var f in filterResult ){
                data[f] = filterResult[f];
            }
            this.actions.listDocumentFilterNext(id, count || this.pageCount, data, function(json){
                if (callback) callback(json);
            });
        }else{
            this.actions.listDocumentFilterNext(id, count || this.pageCount, data, function(json){
                if (callback) callback(json);
            });
        }
    },

    removeDocument: function(document, isAll){
        var id = document.data.id;
        this.actions.removeDocument(id, function(json){
            //json.data.each(function(item){
            this.items.erase(this.documents[id]);
            this.documents[id].destroy();
            MWF.release(this.documents[id]);
            //delete this.documents[id];
            this.app.notice(this.app.lp.deleteDocumentOK, "success");
            // }.bind(this));
        }.bind(this));
    },

    _createItem: function(data){
        return new MWF.xApplication.cms.Module.ListExplorer.DefaultDocument(this.table, data, this.explorer, this);
    }
});

MWF.xApplication.cms.Module.ListExplorer.ListForALL = new Class({
    Extends: MWF.xApplication.cms.Module.ListExplorer.DefaultList,


    createListHead : function(){
        var _self = this;
        var headNode = this.headNode = new Element("tr", {"styles": this.css.listHeadNode}).inject(this.table);

        if( this.selectEnable ){
            this.createSelectTh();
        }

        var listItemUrl = this.explorer.path+"listItemForAll.json";
        MWF.getJSON(listItemUrl, function(json){
            this.listItemTemplate = json;
            json.each(function(cell){
                var isShow = true;
                if( cell.access ){
                    if( cell.access == "admin" && !this.explorer.options.isAdmin ){
                        isShow = false;
                    }
                }
                if(isShow) {
                    var th = new Element("th", {
                        "styles": this.css[cell.headStyles],
                        "width": cell.width,
                        "text": cell.title
                    }).inject(headNode)
                }
                //var thText = new Element("div",{
                //    "styles" : this.css.thTextNode,
                //    "text": cell.title
                //}).inject(th);
                if( cell.sortByClickTitle == "yes" ){
                    th.store("field",cell.item);
                    if( this.orderField  == cell.item && this.orderType!="" ){
                        th.store("orderType",this.orderType);
                        this.sortIconNode = new Element("div",{
                            "styles": this.orderType == "asc" ? this.css.sortIconNode_asc : this.css.sortIconNode_desc
                        }).inject( th, "bottom" );
                    }else{
                        th.store("orderType","");
                        this.sortIconNode = new Element("div",{"styles":this.css.sortIconNode}).inject( th, "bottom" );
                    }
                    th.setStyle("cursor","pointer");
                    th.addEvent("click",function(){
                        _self.resort( this );
                    })
                }
            }.bind(this));
        }.bind(this),false);
    },
    _getCurrentPageData: function(callback, count){
        if(!count)count=20;
        var id = (this.items.length) ? this.items[this.items.length-1].data.id : "(0)";
        var data = {
            "appIdList": [ this.explorer.columnData.id ],
            "statusList": [ this.explorer.options.status ],
            "orderField" : this.orderField || "publishTime",
            "orderType" : this.orderType || "desc"
        };
        if( this.searchKey && this.searchKey!="" ){
            data.title = this.searchKey
        }
        if (this.filter && this.filter.filter ){
            var filterResult = this.filter.getFilterResult();
            for(var f in filterResult ){
                data[f] = filterResult[f];
            }
            this.actions.listDocumentFilterNext(id, count || this.pageCount, data, function(json){
                if (callback) callback(json);
            });
        }else{
            this.actions.listDocumentFilterNext(id, count || this.pageCount, data, function(json){
                if (callback) callback(json);
            });
        }
    }

});

MWF.xApplication.cms.Module.ListExplorer.ListForDraft = new Class({
    Extends: MWF.xApplication.cms.Module.ListExplorer.DefaultList,

    createListHead : function(){
        var _self = this;
        var headNode = this.headNode = new Element("tr", {"styles": this.css.listHeadNode}).inject(this.table);

        if( this.selectEnable ){
            this.createSelectTh();
        }

        var listItemUrl = this.explorer.path+"listItemForAll.json";
        MWF.getJSON(listItemUrl, function(json){
            this.listItemTemplate = json;
            json.each(function(cell){
                var isShow = true;
                if( cell.access ){
                    if( cell.access == "admin" && !this.explorer.options.isAdmin ){
                        isShow = false;
                    }
                }
                if(isShow) {
                    var th = new Element("th", {
                        "styles": this.css[cell.headStyles],
                        "width": cell.width,
                        "text": cell.title
                    }).inject(headNode)
                }
                //var thText = new Element("div",{
                //    "styles" : this.css.thTextNode,
                //    "text": cell.title
                //}).inject(th);
                if( cell.sortByClickTitle == "yes" ){
                    th.store("field",cell.item);
                    if( this.orderField  == cell.item && this.orderType!="" ){
                        th.store("orderType",this.orderType);
                        this.sortIconNode = new Element("div",{
                            "styles": this.orderType == "asc" ? this.css.sortIconNode_asc : this.css.sortIconNode_desc
                        }).inject( th, "bottom" );
                    }else{
                        th.store("orderType","");
                        this.sortIconNode = new Element("div",{"styles":this.css.sortIconNode}).inject( th, "bottom" );
                    }
                    th.setStyle("cursor","pointer");
                    th.addEvent("click",function(){
                        _self.resort( this );
                    })
                }
            }.bind(this));
        }.bind(this),false);
    },
    _getCurrentPageData: function(callback, count){
        if(!count)count=20;
        var id = (this.items.length) ? this.items[this.items.length-1].data.id : "(0)";
        var data = {
            "appIdList": [ this.explorer.columnData.id ],
            "statusList": [ "draft" ],
            "orderField" : this.orderField || null,
            "orderType" : this.orderType || null
        };
        if( this.searchKey && this.searchKey!="" ){
            data.title = this.searchKey
        }
        if (this.filter && this.filter.filter ){
            var filterResult = this.filter.getFilterResult();
            for(var f in filterResult ){
                data[f] = filterResult[f];
            }
            this.actions.listDocumentFilterNext(id, count || this.pageCount, data, function(json){
                if (callback) callback(json);
            });
        }else{
            this.actions.listDocumentFilterNext(id, count || this.pageCount, data, function(json){
                if (callback) callback(json);
            });
        }
    }

});

MWF.xApplication.cms.Module.ListExplorer.List = new Class({
    Extends: MWF.xApplication.cms.Module.ListExplorer.DefaultList,

    initSortData : function(){
        this.orderField = this.data.orderField ? this.data.orderField : "";
        this.orderType = this.data.orderType ? this.data.orderType : "";
        this.viewId = this.data.id;
        this.categoryId = this.explorer.categoryData.id;
        this.status = this.explorer.options.status;
    },
    createListHead : function(){
        var _self = this;


        var headNode = this.headNode = new Element("tr", {"styles": this.css.listHeadNode}).inject(this.table);

        if( this.selectEnable ){
            this.createSelectTh();
        }

        this.data.content.columns.each(function(column){
            var width = (column.widthType == "px" ? (column.width+"px") : (column.widthPer+"%"));
            var th = new Element("th",{
                "styles":this.css.normalThNode,
                "width" : width,
                "text" : column.title ? column.title : ""
            }).inject(headNode);
            //var thText = new Element("div",{
            //    "styles" : this.css.thTextNode,
            //    "text" : column.title ? column.title : ""
            //}).inject(th);
            if( column.sortByClickTitle == "yes" ){
                th.store("field",column.value);
                if( this.orderField  == column.value && this.orderType!="" ){
                    th.store("orderType",this.orderType);
                    this.sortIconNode = new Element("div",{
                        "styles": this.orderType.toLowerCase() == "asc" ? this.css.sortIconNode_asc : this.css.sortIconNode_desc
                    }).inject( th, "bottom" );
                }else{
                    th.store("orderType","");
                    this.sortIconNode = new Element("div",{"styles":this.css.sortIconNode}).inject( th, "bottom" );
                }
                th.setStyle("cursor","pointer");
                th.addEvent("click",function(){
                    _self.resort( this );
                })
            }

        }.bind(this));
    },
    resort : function(th){
        debugger;
        this.orderField = th.retrieve("field");
        this.orderType = ( th.retrieve("orderType") || "" ).toLowerCase();
        if( this.orderField === this.data.orderField ){
            this.orderType = this.orderType === "asc" ? "desc" : "asc";
        }else{
            if( this.orderType == "" ){
                this.orderType = "asc";
            }else if( this.orderType == "asc" ){
                this.orderType = "desc";
            }else{
                this.orderField = this.data.orderField ? this.data.orderField : "";
                this.orderType = this.data.orderType ? this.data.orderType : "";
            }
        }
        this.reload();
    },
    _getCurrentPageData: function(callback, count){
        if(!count)count=20;
        var id = (this.items.length) ? this.items[this.items.length-1].data.document.id : "(0)";
        var data = {
            "orderField":this.orderField || "publishTime",
            "orderType":this.orderType || "desc",
            "categoryId":this.categoryId,
            "viewId":this.viewId,
            "searchDocStatus":this.status
        };
        this.actions.listViewDataNext(id, count, data, function(json){ //listListDataNext
            json.data.each(function(d){
                if( !d.id )d.id = d.document.id;
                if( !d.title)d.title = d.document.title;
            });
            if (callback) callback(json);
        });
    },
    _createItem: function(data){
        return new MWF.xApplication.cms.Module.ListExplorer.Document(this.table, data, this.explorer, this);
    }

});

MWF.xApplication.cms.Module.ListExplorer.Filter = new Class({

    initialize: function( app,explorer,filterNode, filterActionNode, filterConditionNode, actions, css ){
        this.app = app;
        this.explorer = explorer;
        this.css = css;
        this.actions = actions;
        this.filterNode = $(filterNode);
        this.filterActionNode = $(filterActionNode);
        this.filterConditionNode = filterConditionNode;
    },
    load: function(){
        var filterItemUrl = this.explorer.path+"filterItem.json";
        MWF.getJSON(filterItemUrl, function(json){
            this.filterSetting = json;
            if (!this.isFilterOpen){
                if (!this.filterAreaMorph || !this.filterAreaMorph.isRunning()) this.showFilter();
            }else{
                if (this.filterAreaMorph || !this.filterAreaMorph.isRunning()) this.hideFilter();
            }
        }.bind(this));
    },
    showFilter: function(){
        //this.filterActionNode.setStyles(this.css.filterActionNode_over);

        if (!this.filterAreaNode) this.createFilterAreaNode();

        this.filterAreaTipNode.setStyle("display", "block");
        this.filterAreaNode.setStyle("display", "block");
        this.resizeFilterAreaNode();
        var toStyle = {
            "width": "460px",
            "height": "500px"
        };

        this.isFilterOpen = true;

        this.filterAreaMorph.start(toStyle).chain(function(){
            this.createFilterAreaTitle();
            this.createFilterAreaContent();

            this.hideFilterFun = this.hideFilter.bind(this);
            $(document.body).addEvent("click", this.hideFilterFun);
        }.bind(this));
    },
    hideFilter: function(){
        if (this.filterAreaNode){
            var toStyle = {
                "width": "460px",
                "height": "0px"
            };
            this.filterAreaNode.empty();
            this.isFilterOpen = false;
            this.filterAreaMorph.start(toStyle).chain(function(){
                this.filterAreaNode.eliminate("input");
                this.filterAreaNode.setStyle("display", "none");
                this.filterAreaTipNode.setStyle("display", "none");
                //this.filterActionNode.setStyles(this.css.filterActionNode);
                $(document.body).removeEvent("click", this.hideFilterFun);
            }.bind(this));

            $(document.body).removeEvent("click", this.hideFilterFun);
        }
    },
    createFilterAreaNode: function(){

        this.filterAreaNode = new Element("div", {"styles": this.css.filterAreaNode}).inject(this.app.content);
        this.filterAreaNode.addEvent("click", function(e){e.stopPropagation();});

        this.filterAreaTipNode = new Element("div", {"styles": this.css.filterAreaTipNode}).inject(this.app.content);
        //var size = this.filterActionNode.getSize();
        this.filterAreaNode.setStyles({
            "width": "460px",
            "height": "0px"
        });
        this.filterAreaNode.position({
            relativeTo: this.filterNode,
            position: 'bottomRight',
            edge: 'upperRight',
            offset: {x:-20, y: -1}
        });
        this.filterAreaTipNode.position({
            relativeTo: this.filterNode,
            position: 'bottomRight',
            edge: 'bottomRight',
            offset: {x:-38, y: 0}
        });
        this.app.addEvent("resize", function(){
            this.resizeFilterAreaNode();
        }.bind(this));

        this.filterAreaMorph = new Fx.Morph(this.filterAreaNode, {
            duration: '100',
            transition: Fx.Transitions.Sine.easeInOut
        });

    },
    resizeFilterAreaNode: function(){
        if (this.filterAreaNode){
            this.filterAreaNode.position({
                relativeTo: this.filterNode,
                position: 'bottomRight',
                edge: 'upperRight',
                offset: {x:-20, y: -1}
            });
            if (this.filterAreaTipNode){
                this.filterAreaTipNode.position({
                    relativeTo: this.filterNode,
                    position: 'bottomRight',
                    edge: 'bottomRight',
                    offset: {x:-38, y: 0}
                });
            }
        }
    },
    createFilterAreaTitle: function(){
        var titleNode = new Element("div", {"styles": this.css.filterAreaTitleNode}).inject(this.filterAreaNode);
        var okNode = new Element("div", {"styles": this.css.filterAreaTitleActionOkNode, "text": this.app.lp.ok}).inject(titleNode);
        var clearNode = new Element("div", {"styles": this.css.filterAreaTitleActionClearNode, "text": this.app.lp.clear}).inject(titleNode);
        clearNode.addEvent("click", function(){
            this.filterAreaNode.getElements(".filterItem").each(function(el){
                this.unSelectedFilterItem(el);
            }.bind(this));
            var input = this.filterAreaNode.retrieve("input");
            input.set("value", "");
            this.filter = null;
            this.hideFilter();

            this.setFilterConditions();
            this.explorer.reloadElementContent();
        }.bind(this));
        okNode.addEvent("click", function(){
            var input = this.filterAreaNode.retrieve("input");
            if (!this.filter) this.filter = {};
            var key = input.get("value");
            if (key && key!=this.app.lp.searchKey){
                this.filter.key = key;
            }else{
                this.filter.key = "";
                delete this.filter.key
            }


            this.hideFilter();
            this.setFilterConditions();
            this.explorer.reloadElementContent();
        }.bind(this));

        var searchNode = new Element("div", {"styles": this.css.filterAreaTitleSearchNode}).inject(titleNode);
        var searchIconNode = new Element("div", {"styles": this.css.filterAreaTitleSearchIconNode}).inject(searchNode);
        var searchInputAreaNode = new Element("div", {"styles": this.css.filterAreaTitleSearchInputAreaNode}).inject(searchNode);
        var searchInputNode = new Element("input", {"styles": this.css.filterAreaTitleSearchInputNode, "value": this.app.lp.searchKey}).inject(searchInputAreaNode);
        if (this.filter){
            if (this.filter.key) searchInputNode.set("value", this.filter.key);
        }
        this.filterAreaNode.store("input", searchInputNode);

        var key = this.app.lp.searchKey;
        searchInputNode.addEvents({
            "blur": function(){if (!this.get("value")) this.set("value", key)},
            "focus": function(){if (this.get("value")==key) this.set("value", "")},
            "keydown": function(e){
                if (e.code==13){
                    var input = this.filterAreaNode.retrieve("input");
                    if (!this.filter) this.filter = {};
                    var key = input.get("value");
                    if (key && key!=this.app.lp.searchKey){
                        this.filter.key = key;
                    }else{
                        this.filter.key = "";
                        delete this.filter.key
                    }

                    this.hideFilter();
                    this.setFilterConditions();
                    this.explorer.reloadElementContent();
                }
            }.bind(this)
        });
    },

    createFilterAreaContent: function(){
        var contentScrollNode = new Element("div", {"styles": this.css.applicationFilterAreaContentScrollNode}).inject(this.filterAreaNode);
        var contentNode = new Element("div", {"styles": {"overflow": "hidden"}}).inject(contentScrollNode);

        MWF.require("MWF.widget.ScrollBar", function(){
            new MWF.widget.ScrollBar(contentScrollNode, {
                "style":"xApp_filter", "where": "after", "distance": 30, "friction": 4,	"axis": {"x": false, "y": true}
            });
        }.bind(this));

        var _self = this;

        this._getFilterCount(function(json){
            Object.each(json, function(v, key){
                var categoryNode = new Element("div", {"styles": this.css.applicationFilterCategoryNode}).inject(contentNode);
                categoryNode.set("text", v.name );
                var itemAreaNode = new Element("div", {"styles": this.css.applicationFilterItemAreaNode}).inject(contentNode);

                v.data.each(function(item){
                    var itemNode = new Element("div", {"styles": this.css.applicationFilterItemNode}).inject(itemAreaNode);
                    itemNode.set("text", item.name +"("+item.count+")");
                    itemNode.store("value", item.value );
                    itemNode.store("textname", item.name );
                    itemNode.store("key", key);
                    itemNode.store("resultItemName", item.resultItemName);

                    itemNode.addEvent("click", function(){
                        if (this.hasClass("applicationFilterItemNode_over")){
                            _self.unSelectedFilterItem(this);
                        }else{
                            _self.selectedFilterItem(this);
                        }
                    });
                    if (this.filter){
                        if (this.filter[key]){
                            if (item.value == this.filter[key][0].value){
                                this.selectedFilterItem(itemNode);
                            }
                        }
                    }
                }.bind(this));

            }.bind(this));
        }.bind(this));
    },
    _getFilterCount: function(callback){
        var fun = "listCategoryDraftFilterAttribute";
        if( this.explorer.options.status == "published" ){
            fun = "listCategoryPublishFilterAttribute"
        }else if( this.explorer.options.status == "archived" ){
            fun = "listCategoryArchiveFilterAttribute"
        }
        //this.app.options.id
        this.actions[fun]( this.explorer.categoryData.id , function(json){
            this.filterAttribute = {};
            Object.each(json.data, function(v, key) {
                var setting = this.filterSetting[key];
                if(setting){
                    var dataItem = this.filterAttribute[setting.resultListKey] = {
                        "name" : setting.categoryTitle,
                        "data" : []
                    };
                    v.each(function(item){
                        dataItem.data.push({
                            "name" : item[setting.itemNameKey],
                            "value" : item[setting.itemValueKey],
                            "count" : item.count,
                            "resultItemName": setting.resultItemName
                        })
                    })
                }
            }.bind(this));
            if (callback) callback(this.filterAttribute);
        }.bind(this));
    },
    unSelectedFilterItem: function(item){
        if (item.hasClass("applicationFilterItemNode_over")){
            var value = item.retrieve("value");
            var name = item.retrieve("textname");
            var key = item.retrieve("key");

            item.setStyles(this.css.applicationFilterItemNode);
            item.removeClass("applicationFilterItemNode_over");
            item.addClass("applicationFilterItemNode");

            if (!this.filter) this.filter = {};
            this.filter[key] = null;
            delete this.filter[key];

            item.getParent().eliminate("current");
        }
    },
    selectedFilterItem: function(item){
        if (!item.hasClass("applicationFilterItemNode_over")){
            var current = item.getParent().retrieve("current");
            if (current) this.unSelectedFilterItem(current);

            var value = item.retrieve("value");
            var key = item.retrieve("key");
            var name = item.retrieve("textname");
            var resultItemName = item.retrieve("resultItemName");

            item.setStyles(this.css.applicationFilterItemNode_over);
            item.removeClass("applicationFilterItemNode");
            item.addClass("applicationFilterItemNode_over");

            if (!this.filter) this.filter = {};
            this.filter[key] = [{"value": value, "name": name, "resultItemName":resultItemName}];

            item.getParent().store("current", item);
        }
    },
    searchElement: function(){
        if (!this.filter) this.filter = {};
        var key = this.searchElementInputNode.get("value");
        if (key && key!=this.app.lp.searchKey){
            this.filter.key = key;
            this.hideFilter();
            this.setFilterConditions();
            this.explorer.reloadElementContent();
        }
    },
    setFilterConditions: function(){
        this.filterConditionNode.empty();
        if (this.filter){
            Object.each(this.filter, function(v, key){
                if (key!="key"){
                    this.createFilterItemNode(key, v[0]);
                }
            }.bind(this));
            if (this.filter.key){
                this.createFilterItemNode("key", {"name": this.filter.key});
            }
        }
    },
    createFilterItemNode: function(key, v){
        var _self = this;

        var node = new Element("div", {"styles": this.css.filterListItemNode}).inject(this.filterConditionNode);
        var actionNode = new Element("div", {"styles": this.css.filterListItemActionNode}).inject(node);
        var textNode = new Element("div", {"styles": this.css.filterListItemTextNode}).inject(node);
        if( key != "key" ){
            textNode.set("text", this.filterAttribute[key].name+": "+ v.name);
        }else{
            textNode.set("text", this.filterSetting.key.categoryTitle +": "+ v.name);
        }
        actionNode.store("key", key);
        node.addEvents({
            "mouseover": function(){
                this.setStyles(_self.css.filterListItemNode_over);
                this.getLast().setStyles(_self.css.filterListItemTextNode_over);
                this.getFirst().setStyles(_self.css.filterListItemActionNode_over);
            },
            "mouseout": function(){
                this.setStyles(_self.css.filterListItemNode);
                this.getLast().setStyles(_self.css.filterListItemTextNode);
                this.getFirst().setStyles(_self.css.filterListItemActionNode);
            }
        });
        actionNode.addEvent("click", function(){
            var key = this.retrieve("key");
            if (_self.filter[key]) _self.filter[key] = null;
            delete _self.filter[key];
            this.destroy();
            _self.setFilterConditions();
            _self.explorer.reloadElementContent();
        });
    },
    getFilterResult : function(){
        var result = {};
        Object.each(this.filter, function(v, key){
            if( key == "key" && this.filterSetting.key ){
                result[this.filterSetting.key.resultListKey] = [{
                    "name" : this.filterSetting.key.resultItemName,
                    "value" : v
                }];
            }else{
                result[key] = [{
                    "name" : v[0].resultItemName,
                    "value" : v[0].value
                }];
            }
        }.bind(this));
        return result;
    }
});

MWF.xApplication.cms.Module.ListExplorer.DefaultDocument = new Class({
    initialize: function(container, data, explorer, view){
        this.explorer = explorer;
        this.app = explorer.app;
        this.data = data;
        this.container = container;
        this.view = view;
        this.css = this.explorer.css;

        this.load();
    },

    load: function(){
        this.node = new Element("tr", {"styles": this.css.documentItemNode});

        this.node.inject(this.container);

        if( this.view.selectEnable ){
            this.createSelectTd();
        }

        //this.documentAreaNode =  new Element("td", {"styles": this.css.documentItemDocumentNode}).inject(this.node);

        this.view.listItemTemplate.each(function(cell){
            var isShow = true;
            if( cell.access ){
                if( cell.access == "admin" && !this.explorer.options.isAdmin ){
                    isShow = false;
                }
            }
            if(isShow){
                this[cell.name] = new Element("td",{
                    "styles":this.css[cell.contentStyles],
                    "text" : this.data[cell.item] ? this.data[cell.item] : ""
                }).inject(this.node);
            }
        }.bind(this));

        this.setActions();
        this.setEvents();
    },
    destroySelectTd : function(){
        if( this.selectTd ){
            this.selectTd.destroy();
            this.selectTd = null;
        }
    },
    createSelectTd : function(){
        if( this.selectTd )return;
        this.selectTd = new Element("td").inject(this.node, "top");
        this.checkboxElement = new Element("input", {
            "type": "checkbox",
            "events" : { click : function(ev){ ev.stopPropagation(); } }
        }).inject(this.selectTd);
        this.selectTd.addEvent("click", function(ev){
            this.checkboxElement.set("checked", !this.checkboxElement.get("checked") );
            ev.stopPropagation();
        }.bind(this));
    },
    setEvents: function(){

        this.node.addEvents({
            "mouseover": function(){if (!this.readyRemove) this.node.setStyles(this.css.documentItemDocumentNode_over);}.bind(this),
            "mouseout": function(){if (!this.readyRemove) this.node.setStyles(this.css.documentItemDocumentNode);}.bind(this),
            "click": function(e){
                this.openDocument(e);
            }.bind(this)
        });

        if (this.setTopNode){
            this.setTopNode.addEvents({
                "mouseover": function(){this.setTopNode.setStyles(this.css.actionSetTopNode_over);}.bind(this),
                "mouseout": function(){this.setTopNode.setStyles(this.css.actionSetTopNode);}.bind(this),
                "mousedown": function(){this.setTopNode.setStyles(this.css.actionSetTopNode_down);}.bind(this),
                "mouseup": function(){this.setTopNode.setStyles(this.css.actionSetTopNode_over);}.bind(this),
                "click": function(e){
                    this.setTop(e);
                    e.stopPropagation();
                }.bind(this)
            });
        }

        if (this.shareNode){
            this.shareNode.addEvents({
                "mouseover": function(){this.shareNode.setStyles(this.css.actionShareNode_over);}.bind(this),
                "mouseout": function(){this.shareNode.setStyles(this.css.actionShareNode);}.bind(this),
                "mousedown": function(){this.shareNode.setStyles(this.css.actionShareNode_down);}.bind(this),
                "mouseup": function(){this.shareNode.setStyles(this.css.actionShareNode_over);}.bind(this),
                "click": function(e){
                    this.share(e);
                    e.stopPropagation();
                }.bind(this)
            });
        }

        if (this.openNode){
            this.openNode.addEvents({
                "mouseover": function(){this.openNode.setStyles(this.css.actionOpenNode_over);}.bind(this),
                "mouseout": function(){this.openNode.setStyles(this.css.actionOpenNode);}.bind(this),
                "mousedown": function(){this.openNode.setStyles(this.css.actionOpenNode_down);}.bind(this),
                "mouseup": function(){this.openNode.setStyles(this.css.actionOpenNode_over);}.bind(this),
                "click": function(e){
                    this.openDocument(e);
                    e.stopPropagation();
                }.bind(this)
            });
        }

        if (this.deleteNode){
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
        }

        if (this.editNode){
            this.editNode.addEvents({
                "mouseover": function(){this.editNode.setStyles(this.css.actionEditNode_over);}.bind(this),
                "mouseout": function(){this.editNode.setStyles(this.css.actionEditNode);}.bind(this),
                "mousedown": function(){this.editNode.setStyles(this.css.actionEditNode_down);}.bind(this),
                "mouseup": function(){this.editNode.setStyles(this.css.actionEditNode_over);}.bind(this),
                "click": function(e){
                    this.openDocument( e, true );
                    e.stopPropagation();
                }.bind(this)
            });
        }

    },
    setActions: function(){
        if( this.actionAreaNode ){
            if( this.explorer.options.isAdmin ){
                this.deleteNode = new Element("div", {"styles": this.css.actionDeleteNode, "title": this.explorer.app.lp["delete"]}).inject(this.actionAreaNode);

                this.editNode = new Element("div", {"styles": this.css.actionEditNode, "title": this.explorer.app.lp.edit}).inject(this.actionAreaNode);
            }
        }
    },
    openDocument: function(e, isEdited){
        var appId = "cms.Document"+this.data.id;
        if (this.app.desktop.apps[appId]){
            this.app.desktop.apps[appId].setCurrent();
        }else {
            var options = {
                "documentId": this.data.id,
                "appId": appId,
                "readonly" : !isEdited,
                "postDelete" : function(){
                    this.view.reload();
                }.bind(this)
            };
            this.explorer.app.desktop.openApplication(e, "cms.Document", options);
        }
    },
    remove: function(e){
        var lp = this.explorer.app.lp;
        var title ;
        if( !this.data.title || this.data.title == lp.untitled ){ //"无标题"
            title = lp.currentDocument;
        }else{
            title = "\"" + this.data.title + "\"";
        }
        var text = lp.deleteDocument.replace(/{title}/g, title );
        var _self = this;
        this.node.setStyles(this.css.documentItemDocumentNode_remove);
        this.readyRemove = true;
        this.explorer.app.confirm("warn", e, lp.deleteDocumentTitle, text, 350, 120, function(){

            _self.view.removeDocument(_self, false);

            this.close();

        }, function(){
            _self.node.setStyles(_self.css.documentItemDocumentNode);
            _self.readyRemove = false;
            this.close();
        });
    },

    destroy: function(){
        this.node.destroy();
    },

    setPersonData: function(){
        var explorer = {
            "actions": this.explorer.personActions,
            "app": {
                "lp": this.explorer.app.lp
            }
        };
        new MWF.widget.O2Identity({"name": this.data.creatorIdentity}, this.personAreaNode, {"style": "work"});
    }
});

MWF.xApplication.cms.Module.ListExplorer.Document = new Class({
    Extends: MWF.xApplication.cms.Module.ListExplorer.DefaultDocument,

    load: function(){
        this.node = new Element("tr", {"styles": this.css.documentItemNode}).inject(this.container);

        if( this.view.selectEnable ){
            this.createSelectTd();
        }

        this.view.data.content.columns.each(function(column){
            var value = (column.value && column.value!="") ? (this.data.document[column.value] || this.data.data[column.value] || "") : "";
            if( typeOf(value) == "array" ){
                var values = [];
                value.each( function( v ){
                    if( typeOf( v ) == "object" ){
                        if( v.name ){
                            values.push( v.name );
                        }
                    }else if( typeOf(v) == "string" ){
                        values.push( v );
                    }else if( typeOf(v) == "array" ){
                        values.push( v.join(",") );
                    }else{
                        values.push( v.toString() );
                    }
                });
                value = values.join(",")
            }
            var td = new Element("td",{
                "styles":this.css.normalTdNode,
                "text" : value
            }).inject(this.node);
            if( column.align && column!="center" ){
                if( column.align == "left" ){
                    td.setStyle("text-align","left");
                }else if( column.align == "right" ){
                    td.setStyle("text-align","right");
                }
            }
            if( column.operation ){
                if( this.explorer.options.isAdmin ){
                    if( column.operation.deleteDocument){
                        this.deleteNode = new Element("div", {"styles": this.css.actionDeleteNode, "title": this.explorer.app.lp["delete"]}).inject(td);
                    }
                    if( column.operation.editDocument){
                        this.editNode = new Element("div", {"styles": this.css.actionEditNode, "title": this.explorer.app.lp.edit}).inject(td);
                    }
                }
                if( column.operation.share){
                    this.shareNode = new Element("div", {"styles": this.css.actionShareNode, "title": this.explorer.app.lp.share}).inject(td);
                }
            }
        }.bind(this));

        this.setEvents();
    }
});