//MWF.xDesktop.requireApp("Minder", "Actions.RestActions", null, false);
MWF.xDesktop.requireApp("Template", "Explorer", null, false);
MWF.xDesktop.requireApp("Template", "MForm", null, false);
MWF.xDesktop.requireApp("Minder", "Common", null, false);
MWF.xApplication.Minder.SharedExplorer = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],
    options: {
        "style": "default",
        "defaultTreeNode" : "root",
        "defaultViewType" : "list"
    },
    initialize: function(node, app, options){
        this.setOptions(options);

        this.path = "../x_component_Minder/$MineExplorer/";
        this.cssPath = "../x_component_Minder/$MineExplorer/"+this.options.style+"/css.wcss";
        this._loadCss();
        this.app = app;
        this.container = $(node);
    },
    load : function(){

        this.rightNode = new Element("div.rightNode",{
            "styles" : this.css.rightNode
        }).inject(this.container);

        //this.historyContentNode = new Element("div",{
        //    "styles" : this.css.historyContentNode
        //}).inject(this.rightNode);

        //this.app.histroy.load( this.historyContentNode );

        this.toolbarNode = new Element("div",{
            "styles" : this.css.toolbarNode
        }).inject(this.rightNode);
        this.toolbar = new MWF.xApplication.Minder.Toolbar(this.toolbarNode, this, {
            "availableTool" : [
                ["createMinder"],
                ["rename", "recycle"],
                //["import", "export"],
                ["share"]
            ],
            viewType : this.options.defaultViewType
        });
        this.toolbar.load();

        this.listNode = new Element("div",{
            "styles" : this.css.listNode
        }).inject(this.rightNode);
        this.loadList({});

        this.resizeContent();
        this.resizeFun = this.resizeContent.bind(this);
        this.app.addEvent("resize", this.resizeFun);
    },
    destroy : function(){
        this.container.empty();
        this.app.removeEvent("resize", this.resizeFun);
    },
    resizeContent : function(){
        var size = this.app.content.getSize();

        this.rightNode.setStyle("width", size.x - 70 );

        this.listNode.setStyle("height", size.y - 92 );
        this.listNode.setStyle("width", size.x - 113 );

        this.toolbarNode.setStyle("width", size.x - 70 );
    },
    loadList: function( filterData ){
        if (this.currentView) this.currentView.destroy();
        this.currentView = new MWF.xApplication.Minder.SharedExplorer.List( this.listNode, this.app, this, {
            templateUrl : this.path + this.options.style + ( this.getViewType() == "list" ? "/listItem_shared.json" : "/tileItem.json" ),
            "scrollEnable" : true
        });
        this.currentView.viewType = this.getViewType();
        this.currentView.filterData = filterData;
        this.currentView.load();
    },
    getViewType : function(){
        return this.toolbar.getListType();
    },
    getCurrentFolderId : function(){
        return this.tree.getCurrentFolderId();
    },
    recordStatus : function(){
        return {
            defaultViewType : this.getViewType()
        }
    }
});

MWF.xApplication.Minder.SharedExplorer.List = new Class({
    Extends: MWF.xApplication.Minder.List,
    options : {
        "scrollEnable" : true,
        "scrollType" : "window"
    },
    _createDocument: function(data, index){
        return new MWF.xApplication.Minder.Document(this.viewNode, data, this.explorer, this, null,  index);
    },
    _getCurrentPageData: function(callback, count){
        if(!count)count=30;
        var id = (this.items.length) ? this.items[this.items.length - 1].data.id : "(0)";

        var filter = this.filterData || {};
        if( this.sortType && this.sortField ){
            filter.orderField = this.sortField;
            filter.orderType = this.sortType;
        }

        //{"name":"","folderId":"root","description":"","creator":"","creatorUnit":"","shared":"","orderField":"","orderType":""}//
        this.actions.listMySharedMind(id, count, filter, function(json){
            if( !json.data )json.data = [];
            if (callback)callback(json);
        });
    }
});
