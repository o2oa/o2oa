//MWF.xDesktop.requireApp("Minder", "Actions.RestActions", null, false);
MWF.xDesktop.requireApp("Template", "Explorer", null, false);
MWF.xDesktop.requireApp("Template", "MForm", null, false);
MWF.xDesktop.requireApp("Minder", "Common", null, false);
MWF.xApplication.Minder.MineExplorer = new Class({
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
        this.treeNode = new Element("div.treeNode",{
            "styles" : this.css.treeNode
        }).inject(this.container);
        this.tree = new MWF.xApplication.Minder.MineExplorer.Tree( this, this.treeNode, {
            style : this.options.style,
            defaultNode : this.options.defaultTreeNode,
            "minWidth" : 260,
            "maxWidth" : 350,
            onPostLoad : function(){
                this.resizeContent();
            }.bind(this)
        } );

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
            viewType : this.options.defaultViewType
        });
        this.toolbar.load();

        this.listNode = new Element("div",{
            "styles" : this.css.listNode
        }).inject(this.rightNode);

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
        var treeSize = this.treeNode.getSize();
        var naviSize = this.app.naviNode.getSize();

        var x = size.x - treeSize.x - naviSize.x - 5;

        this.treeNode.setStyle("height", size.y);

        this.rightNode.setStyle("width", x );

        this.listNode.setStyle("height", size.y - 92 );
        this.listNode.setStyle("width", x - 43 );

        this.toolbarNode.setStyle("width", x );
    },
    loadList: function( filterData ){
        if (this.currentView) this.currentView.destroy();
        this.currentView = new MWF.xApplication.Minder.List( this.listNode, this.app, this, {
            templateUrl : this.path + this.options.style + ( this.getViewType() == "list" ? "/listItem.json" : "/tileItem.json" ),
            "scrollEnable" : true
        });
        this.currentView.viewType = this.getViewType();
        this.currentView.filterData = filterData;
        this.currentView.load();
    },
    getViewType : function(){
        return this.toolbar.getListType();
    },
    getCurrentFolderData : function(){
        return this.tree.getCurrentFolderData();
    },
    getCurrentFolderId : function(){
        return this.tree.getCurrentFolderId();
    },
    recordStatus : function(){
        return {
            defaultTreeNode : this.getCurrentFolderId(),
            defaultViewType : this.getViewType()
        }
    }
});

MWF.xApplication.Minder.MineExplorer.Tree = new Class({
    Extends: MWF.xApplication.Minder.Tree,
    Implements: [Options, Events],
    loadTreeNode : function( rootData ){
        this.treeNode = new MWF.xApplication.Minder.MineExplorer.Tree.Node( this, this.treeContentNode, rootData, {
            "style" : this.options.style,
            "isCurrent" : this.options.defaultNode == rootData.id,
            "minWidth" : this.options.minWidth,
            "maxWidth" : this.options.maxWidth
        });
    }
});

MWF.xApplication.Minder.MineExplorer.Tree.Node = new Class({
    Extends: MWF.xApplication.Minder.Tree.Node,
    Implements: [Options, Events],
    getTreeNode: function( data ){
        return new MWF.xApplication.Minder.MineExplorer.Tree.Node(this.tree, this.treeContentNode, data, {
            style :this.options.style,
            level : this.options.level + 1,
            isCurrent : this.tree.options.defaultNode == data.id,
            "minWidth" : this.options.minWidth,
            "maxWidth" : this.options.maxWidth
        })
    },
    setTextNodeWidth : function(){
        if( this.options.minWidth ){
            this.itemTextNode.setStyle(  "min-width" , ( this.options.minWidth - this.options.level * 12 - 10 - 80   ) +"px");
            this.itemTextNode.setStyle(  "margin-right" , "0px")
        }
        if( this.options.maxWidth ){
            this.itemTextNode.setStyle(  "max-width" , ( this.options.maxWidth - this.options.level * 12 - 10 - 80  ) +"px");
            this.itemTextNode.setStyle(  "margin-right" , "0px")
        }
    },
    _cancelCurrent : function(){
        if( this.toolbar )this.toolbar.setStyle("display","none");
        this.setTextNodeWidth();
    },
    _setCurrent: function(){
        this.explorer.loadList({
            folderId : this.data.id
        });
        if( !this.toolbar ){
            this.createToolbar()
        }else{
            this.toolbar.setStyle("display","");
        }
        if( this.options.minWidth ){
            this.itemTextNode.setStyle(  "min-width" , ( this.options.minWidth - this.options.level * 12 - 10 - 80 - 50  ) +"px");
            this.itemTextNode.setStyle(  "margin-right" , "50px")
        }
        if( this.options.maxWidth ){
            this.itemTextNode.setStyle(  "max-width" , ( this.options.maxWidth - this.options.level * 12 - 10 - 80 - 50 ) +"px");
            this.itemTextNode.setStyle(  "margin-right" , "50px")
        }
        this.explorer.resizeContent();
    },
    createToolbar : function(){
        if( this.data.id == "root" )return;
        this.toolbar = new Element("div",{
            styles : this.explorer.css.toolbar
        }).inject( this.itemNode );

        this.editAction =  new Element("div.editAction",{
            styles : this.explorer.css.editAction,
            "title" : "编辑"
        }).inject(this.toolbar);
        this.editAction.addEvents({
            "click" : function(ev){
                this.edit( ev );
            }.bind(this),
            "mouseover" : function(ev){
                this.editAction.setStyles( this.explorer.css.editAction_over )
            }.bind(this),
            "mouseout" : function(ev){
                this.editAction.setStyles( this.explorer.css.editAction )
            }.bind(this)
        });


        this.deleteAction = new Element("div.cancelViewAction", {
            "styles": this.explorer.css.deleteAction,
            "title" : "删除"
        }).inject(this.toolbar);
        this.deleteAction.addEvent("click", function(e){
            this.delete(e);
        }.bind(this));
        this.deleteAction.addEvents({
            "mouseover" : function(ev){
                this.deleteAction.setStyles( this.explorer.css.deleteAction_over )
            }.bind(this),
            "mouseout" : function(ev){
                this.deleteAction.setStyles( this.explorer.css.deleteAction )
            }.bind(this)
        });
    },
    checkDelete : function( callback, ev ){
        if( this.children && this.children.length > 0 ){
            this.app.notice( this.app.lp.deleteFolderDisable_hasSubFolder, "error" );
            return false;
        }
        this.app.restActions.listNextMindWithFilter("(0)", 1, { folderId : this.data.id }, function(json){
            if( json.data && json.data.length > 0 ){
                this.app.notice( this.app.lp.deleteFolderDisable_hasFile, "error" );
                return false;
            }else if( callback ){
                callback();
            }
        }.bind(this));
    },
    delete : function( ev ){
        this.checkDelete( function(){
            var _self = this;
            var text = this.app.lp.deleteFolder.replace(/{name}/g, this.data.name );
            this.app.confirm("infor", ev, this.app.lp.deleteFolderTitle, text, 380, 150, function(){
                _self._delete();
                this.close();
            }, function(){
                this.close()
            });
        }.bind(this), ev );
        ev.stopPropagation();
    },
    _delete : function( ev ){
        this.app.restActions.removeFolder( this.data.id, function(){
            if (this.explorer.currentView) this.explorer.currentView.destroy();
            this.tree.reload();
        }.bind(this));
    },
    edit : function( ev ){
        var form = new MWF.xApplication.Minder.FolderForm(this.explorer, this.data, {
            title : "编辑目录"
        }, {
            app: this.app
        });
        form.edit();
        ev.stopPropagation();
    }
});
