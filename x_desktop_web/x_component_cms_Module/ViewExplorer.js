MWF.xApplication.cms = MWF.xApplication.cms || {};
MWF.xApplication.cms.Module = MWF.xApplication.cms.Module || {};

MWF.require("MWF.xAction.org.express.RestActions", null,false);
MWF.require("MWF.widget.Mask", null, false);
MWF.require("MWF.widget.Identity", null,false);
MWF.xDesktop.requireApp("cms.Module", "lp."+MWF.language, null, false);
MWF.xDesktop.requireApp("cms.Module", "package", null, false);

MWF.xApplication.cms.Module.ViewExplorer = new Class({
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
	
	initialize: function(node, actions, columnData, categoryData, viewData, options){
		this.setOptions(options);
		this.setTooltip();
		this.path = "/x_component_cms_Module/$ViewExplorer/";
		this.cssPath = "/x_component_cms_Module/$ViewExplorer/"+this.options.style+"/css.wcss";
		this._loadCss();

        this.categoryData = categoryData;
        this.columnData = columnData;
        this.viewData = viewData;

		this.actions = actions;
		this.node = $(node);
		this.initData();
        if (!this.personActions) this.personActions = new MWF.xAction.org.express.RestActions();
	},
	setTooltip: function(tooltip){
		if (tooltip) this.options.tooltip = Object.merge(this.options.tooltip, tooltip);
	},
	initData: function(){
        this.toolItemNodes = [];
	},
    reload: function(){
        this.node.empty();
        this.load();
    },
    load: function(){

        //this.toolbarNode = new Element("div", {"styles": this.css.toolbarNode});
        //this.toolbarNode.inject(this.node);

        //this.loadToolbar();

        //this.filterConditionNode = new Element("div", {
        //    "styles": this.css.filterConditionNode
        //}).inject(this.node );

        this.loadContentNode();

        this.loadView();
        this.setNodeScroll();

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
        if( this.viewData.isAll ){
            this.view = new MWF.xApplication.cms.Module.ViewExplorer.ViewForALL(this.elementContentNode, this.app,this, this.viewData, this.options.searchKey );
        }else if( this.viewData.isDefault || (this.options.searchKey && this.options.searchKey!="") ){
            this.view = new MWF.xApplication.cms.Module.ViewExplorer.DefaultView(this.elementContentNode, this.app,this, this.viewData, this.options.searchKey );
        }else{
            this.view = new MWF.xApplication.cms.Module.ViewExplorer.View(this.elementContentNode, this.app,this, this.viewData );
        }
        this.view.load();
        this.setContentSize();
    },

    setContentSize: function(){
        var toolbarSize = this.toolbarNode ? this.toolbarNode.getSize() : {"x":0,"y":0};
        var titlebarSize = this.app.titleBar ? this.app.titleBar.getSize() : {"x":0,"y":0};
        var nodeSize = this.node.getSize();
        var pt = this.elementContentNode.getStyle("padding-top").toFloat();
        var pb = this.elementContentNode.getStyle("padding-bottom").toFloat();
        //var filterSize = this.filterNode.getSize();
        var filterConditionSize = this.filterConditionNode ? this.filterConditionNode.getSize() : {"x":0,"y":0};

        var height = nodeSize.y-toolbarSize.y-pt-pb-filterConditionSize.y-titlebarSize.y;
        this.elementContentNode.setStyle("height", ""+height+"px");

        this.pageCount = (height/40).toInt()+5;

        if (this.view.items.length<this.pageCount){
            this.view.loadElementList(this.pageCount-this.view.items.length);
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
    }
});


MWF.xApplication.cms.Module.ViewExplorer.DefaultView = new Class({

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
    load : function(){
        this.initData();

        this.node = new Element("div", {
            "styles": this.css.elementContentListNode
        }).inject(this.container);

        this.table = new Element("table",{ "width" : "100%", "border" : "0", "cellpadding" : "5", "cellspacing" : "0",  "class" : "editTable"}).inject(this.node);
        this.initSortData();
        this.createListHead();
        this.loadElementList();
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
    createListHead : function(){
        var headNode = new Element("tr", {"styles": this.css.listHeadNode}).inject(this.table);
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
                    new Element("th", {
                        "styles": this.css[cell.headStyles],
                        "text": cell.title,
                        "width": cell.width
                    }).inject(headNode)
                }
            }.bind(this));
        }.bind(this),false);
    },

    loadElementList: function(count){
        if (!this.isItemsLoaded){
            if (!this.isItemLoadding){
                this.isItemLoadding = true;
                this._getCurrentPageData(function(json){
                    this.count = json.count;

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
            "catagoryIdList": [this.explorer.categoryData.id ],
            "statusList": [this.explorer.options.status]
        }
        if( this.searchKey && this.searchKey!="" ){
            data.titleList = [this.searchKey]
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

    removeDocument: function(document, all){
        var id = document.data.id;
        this.actions.removeDocument(id, function(json){
            //json.data.each(function(item){
            this.items.erase(this.documents[id]);
            this.documents[id].destroy();
            MWF.release(this.documents[id]);
            delete this.documents[id];
            this.app.notice(this.app.lp.deleteDocumentOK, "success");
            // }.bind(this));
        }.bind(this));
    },

    _createItem: function(data){
        return new MWF.xApplication.cms.Module.ViewExplorer.DefaultDocument(this.table, data, this.explorer, this);
    }
})


MWF.xApplication.cms.Module.ViewExplorer.ViewForALL = new Class({
    Extends: MWF.xApplication.cms.Module.ViewExplorer.DefaultView,


    createListHead : function(){
        var headNode = new Element("tr", {"styles": this.css.listHeadNode}).inject(this.table);
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
                    new Element("th", {
                        "styles": this.css[cell.headStyles],
                        "text": cell.title,
                        "width": cell.width
                    }).inject(headNode)
                }
            }.bind(this));
        }.bind(this),false);
    },
    _getCurrentPageData: function(callback, count){
        if(!count)count=20;
        var id = (this.items.length) ? this.items[this.items.length-1].data.id : "(0)";
        var data = {
            "appIdList": [ this.explorer.columnData.id ],
            "statusList": [ this.explorer.options.status ]
        }
        if( this.searchKey && this.searchKey!="" ){
            data.titleList = [this.searchKey]
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

})

MWF.xApplication.cms.Module.ViewExplorer.View = new Class({
    Extends: MWF.xApplication.cms.Module.ViewExplorer.DefaultView,

    initSortData : function(){
        this.orderField = this.data.orderField ? this.data.orderField : "";
        this.orderType = this.data.orderType ? this.data.orderType : "";
        this.viewId = this.data.id;
        this.catagoryId = this.explorer.categoryData.id;
        this.status = this.explorer.options.status;
    },
    createListHead : function(){
        var _self = this;

        var headNode = new Element("tr", {"styles": this.css.listHeadNode}).inject(this.table);
        this.data.content.columns.each(function(column){
            var width = (column.widthType == "px" ? (column.width+"px") : (column.widthPer+"%"));
            var th = new Element("th",{
                "styles":this.css.normalThNode,
                "text" : column.title ? column.title : "",
                "width" : width
            }).inject(headNode);
            if( column.sortByClickTitle == "yes" ){
                th.store("field",column.value);
                if( this.orderField  == column.value && this.orderType!="" ){
                    th.store("orderType",this.orderType);
                    this.sortIconNode = new Element("div",{
                        "styles": this.orderType == "asc" ? this.css.sortIconNode_asc : this.css.sortIconNode_desc
                    }).inject( th, "top" );
                }else{
                    th.store("orderType","");
                    this.sortIconNode = new Element("div",{"styles":this.css.sortIconNode}).inject( th, "top" );
                }
                th.setStyle("cursor","pointer");
                th.addEvent("click",function(){
                    _self.resort( this );
                })
            }

        }.bind(this));
    },
    resort : function(th){
        this.orderField = th.retrieve("field");
        var orderType = th.retrieve("orderType");
        //th.eliminate(orderType);
        if( orderType == "" ){
            this.orderType = "asc";
        }else if( this.orderType == "asc" ){
            this.orderType = "desc";
        }else{
            this.orderField = this.data.orderField ? this.data.orderField : "";
            this.orderType = this.data.orderType ? this.data.orderType : "";
        }
        this.reload();
    },
    _getCurrentPageData: function(callback, count){
        if(!count)count=20;
        var id = (this.items.length) ? this.items[this.items.length-1].data.document.id : "(0)";
        var data = {
            "orderField":this.orderField,
            "orderType":this.orderType,
            "catagoryId":this.catagoryId,
            "viewId":this.viewId,
            "searchDocStatus":this.status
        }
        this.actions.listViewDataNext(id, count, data, function(json){
            json.data.each(function(d){
                if( !d.id )d.id = d.document.id;
                if( !d.title)d.title = d.document.title;
            })
            if (callback) callback(json);
        });
    },
    _createItem: function(data){
        return new MWF.xApplication.cms.Module.ViewExplorer.Document(this.table, data, this.explorer, this);
    }

})


MWF.xApplication.cms.Module.ViewExplorer.Filter = new Class({

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
        }

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
            }
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

                    //             for (var x=0; x<10; x++){

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
                    //           }

            }.bind(this));
        }.bind(this));
    },
    _getFilterCount: function(callback){
        var fun = "listCategoryDraftFilterAttribute"
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
            }.bind(this))
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
        var result = {}
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
        }.bind(this))
        return result;
    }
})


MWF.xApplication.cms.Module.ViewExplorer.DefaultDocument = new Class({
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

        //this.setPersonData();
        //this.setStatusData();
        this.setActions();
        this.setEvents();
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


        //this.titleAreaNode.addEvent("click", function(){
        //    this.loadChild();
        //}.bind(this));

    },
    setActions: function(){
        if( this.actionAreaNode ){
            if( this.explorer.options.isAdmin ){
                this.deleteNode = new Element("div", {"styles": this.css.actionDeleteNode, "title": this.explorer.app.lp.delete}).inject(this.actionAreaNode);

                this.editNode = new Element("div", {"styles": this.css.actionEditNode, "title": this.explorer.app.lp.edit}).inject(this.actionAreaNode);
            }
            //this.shareNode = new Element("div", {"styles": this.css.actionShareNode, "title": this.explorer.app.lp.share}).inject(this.actionAreaNode);
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
                "readonly" : !isEdited
            }//this.explorer.app.options.application.allowControl};
            this.explorer.app.desktop.openApplication(e, "cms.Document", options);
        }
    },
    remove: function(e){
        var lp = this.explorer.app.lp;
        var text = lp.deleteDocument.replace(/{title}/g, this.data.title);
        var _self = this;
        this.node.setStyles(this.css.documentItemDocumentNode_remove);
        this.readyRemove = true;
        this.explorer.app.confirm("warn", e, lp.deleteDocumentTitle, text, 350, 120, function(){

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
            _self.view.removeDocument(_self, false);
            //}
            this.close();
            //}else{
            //    this.content.getElement("#deleteDocument_checkInfor").set("text", lp.deleteAllDocumentCheck).setStyle("color", "red");
            //}
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
        }
        new MWF.widget.Identity({"name": this.data.creatorIdentity}, this.personAreaNode, explorer, false, null, {"style": "work"});
    }
});

MWF.xApplication.cms.Module.ViewExplorer.Document = new Class({
    Extends: MWF.xApplication.cms.Module.ViewExplorer.DefaultDocument,

    load: function(){
        this.node = new Element("tr", {"styles": this.css.documentItemNode}).inject(this.container);

        this.view.data.content.columns.each(function(column){
            var value = (column.value && column.value!="") ? (this.data.document[column.value] || this.data.data[column.value] || "") : "";
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
                        this.deleteNode = new Element("div", {"styles": this.css.actionDeleteNode, "title": this.explorer.app.lp.delete}).inject(td);
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


        //this.setActions();
        this.setEvents();
    }
})