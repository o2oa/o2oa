MWF.xDesktop.requireApp("cms.ColumnManager", "Explorer", null, false);
MWF.xDesktop.requireApp("cms.ColumnManager", "FormExplorer", null, false);
MWF.xDesktop.requireApp("cms.ColumnManager", "ViewExplorer", null, false);
MWF.xApplication.cms.ColumnManager.CategoryExplorer = new Class({
    Extends: MWF.xApplication.cms.ColumnManager.Explorer,
    Implements: [Options, Events],
    options: {
        "style": "default",
        "title" : "",
        "currentCategoryId" : "",
        "tooltip": {
            "create": MWF.CMSCM.LP.category.create,
            "search": MWF.CMSCM.LP.category.search,
            "searchText": MWF.CMSCM.LP.category.searchText,
            "noElement": MWF.CMSCM.LP.category.noCategoryNoticeText
        }
    },
    initialize: function(node, naviNode, actions, options){
        this.setOptions(options);
        this.setTooltip();

        this.path = "/x_component_cms_ColumnManager/$CategoryExplorer/";
        this.cssPath = "/x_component_cms_ColumnManager/$CategoryExplorer/"+this.options.style+"/css.wcss";

        this._loadCss();

        this.actions = actions;
        this.node = $(node);
        this.naviNode = naviNode;
        this.initData();
    },
    load: function(){
        //this.loadToolbar();
        this.loadContentNode();

        //this.setNodeScroll();
        //this.loadElementList();
    },
    reload : function(){
        if( !this.node )return;
        this.initData();
        this.node.empty();
        this.naviNode.empty();
        this.load();
    },
    refresh: function(){
        this.setContentSize();
        this.viewExplorer.reload();
        this.formExplorer.reload();
        this.categoryProperty.reload();
    },
    loadContentNode: function(){
        this.elementContentNode = new Element("div.elementContentNode", {
            "styles": this.css.elementContentNode
        }).inject(this.node);

        this.elementContentListNode = new Element("div.elementContentListNode", {
            "styles": this.css.elementContentListNode
        }).inject(this.elementContentNode);


        this.naviContainerNode = this.naviNode;

        this.loadContentNodes();


        this.setContentSize();
        this.app.addEvent("resize", function(){this.setContentSize();}.bind(this));

        this.loadForm();

        this.loadView();

        this.loadProperty();

        this.loadCategoryList();
    },
    loadContentNodes: function(){
        //this.naviContainerNode = new Element("div.naviContainerNode", {
        //    "styles": this.css.naviContainerNode
        //}).inject(this.elementContentListNode);
        //
        //this.naviArea = new Element("div.naviArea", {
        //    "styles": this.css.naviArea
        //}).inject(this.naviContainerNode);
        //
        //this.naviNode = new Element("div.naviNode", {
        //    "styles": this.css.naviNode
        //}).inject(this.naviArea);


        //this.categoryListResizeNode = new Element("div.categoryListResizeNode", {
        //    "styles": this.css.categoryListResizeNode
        //}).inject(this.elementContentListNode);

        this.rightContent = new Element("div.rightContent", {
            "styles": this.css.rightContent
        }).inject(this.elementContentListNode);

        this.rightTopContent = new Element("div.rightTopContent", {
            "styles": this.css.rightTopContent
        }).inject(this.rightContent);

        this.rightHorizontalResizeNode = new Element("div.rightHorizontalResizeNode", {
            "styles": this.css.rightHorizontalResizeNode
        }).inject(this.rightContent);

        this.rightBottomContent = new Element("div.rightBottomContent", {
            "styles": this.css.rightBottomContent
        }).inject(this.rightContent);

        //this.loadCategoryListResize();

        this.createFormNode();


        //this.rightVerticalResizeNode = new Element("div.rightVerticalResizeNode", {
        //    "styles": this.css.rightVerticalResizeNode
        //}).inject(this.rightTopContent);

        this.createViewNode();
        this.formPercent = 0.5;
        this.topPercent = 0.5;
        //this.loadRightVerticalResize();
        this.loadRightHorizontalResize();
        //this.setNodeScroll();
    },
    setNodeScroll: function(){
        MWF.require("MWF.widget.DragScroll", function(){
            new MWF.widget.DragScroll(this.naviArea);
        }.bind(this));
        MWF.require("MWF.widget.ScrollBar", function(){
            new MWF.widget.ScrollBar(this.naviArea, {"indent": false});
        }.bind(this));
    },
    loadCategoryList : function( options ){
        this.categoryList = new MWF.xApplication.cms.ColumnManager.CategoryExplorer.CategoryList(this, this.naviNode, {
            currentCategoryId : this.options.currentCategoryId,
            columnId : this.app.options.column.id,
            onPostLoad : function(){
                this.fireEvent( "postLoadCategoryList", this.categoryList );
            }.bind(this)
        } );
    },
    _createElement: function(){
        this.categoryList.newCategory();
    },
    loadProperty: function(){
        this.categoryProperty = new MWF.xApplication.cms.ColumnManager.CategoryExplorer.CategoryProperty(this.app, this.rightBottomContent);
        this.categoryProperty.load();
    },
    createFormNode: function(){
        this.formAreaNode = new Element("div.formAreaNode", {
            "styles": this.css.formAreaNode
        }).inject(this.rightTopContent);

        this.formTitleNode = new Element("div.formTitleNode", {
            "styles": this.css.formTitleNode
        }).inject(this.formAreaNode);

        this.formCreateNode = new Element("div.formCreateNode", {
            "styles": this.css.formCreateNode,
            "title" : "新建表单"
        }).inject(this.formTitleNode);
        this.formCreateNode.addEvent("click", function(e){
            this.formExplorer._createElement(e);
        }.bind(this));
        this.formCreateNode.addEvent("mouseover", function(e){
            this.formCreateNode.setStyles(this.css.formCreateNode_over);
        }.bind(this));
        this.formCreateNode.addEvent("mouseout", function(e){
            this.formCreateNode.setStyles(this.css.formCreateNode);
        }.bind(this));
        this.formCreateNode.setStyle("display","none");

        //this.formTitleSepNode = new Element("div.formTitleSepNode", {
        //    "styles": this.css.formTitleSepNode
        //}).inject(this.formTitleNode);

        this.formTitleTextNode = new Element("div.formTitleTextNode", {
            "styles": this.css.formTitleTextNode,
            "text" : "选择分类表单"
        }).inject(this.formTitleNode);

        //this.formContainerNode = new Element("div.formContainerNode", {
        //     "styles": this.css.formContainerNode
        // }).inject(this.rightContent);

        this.formNode = new Element("div.formNode", {
            "styles": this.css.formNode
        }).inject(this.formAreaNode);
    },
    loadForm : function( ){
        this.formExplorer = new MWF.xApplication.cms.ColumnManager.CategoryExplorer.FormExplorer(this.formNode, this.app.restActions, {style : "full"});
        this.formExplorer.app = this.app;
        this.formExplorer.explorer = this;
        this.formExplorer.load();
    },
    createViewNode: function(){
        this.viewAreaNode = new Element("div.viewAreaNode", {
            "styles": this.css.viewAreaNode
        }).inject(this.rightTopContent);

        this.viewTitleNode = new Element("div.viewTitleNode", {
            "styles": this.css.viewTitleNode
        }).inject(this.viewAreaNode);


        this.viewAddNode = new Element("div.viewAddNode", {
            "styles": this.css.viewAddNode,
            "text" : "选择数据视图",
            "title" : "从已有数据视图中选择"
        }).inject(this.viewTitleNode);
        this.viewAddNode.addEvent("click", function(e){
            this.viewExplorer._selectView(e);
        }.bind(this));
        this.viewAddNode.addEvent("mouseover", function(e){
            this.viewAddNode.setStyles(this.css.viewAddNode_over);
        }.bind(this));
        this.viewAddNode.addEvent("mouseout", function(e){
            this.viewAddNode.setStyles(this.css.viewAddNode);
        }.bind(this));
        this.viewAddNode.setStyle("display","none");

        this.listAddNode = new Element("div.listAddNode", {
            "styles": this.css.viewAddNode,
            "text" : "选择列表",
            "title" : "从已有列表中选择"
        }).inject(this.viewTitleNode);
        this.listAddNode.addEvent("click", function(e){
            this.viewExplorer._selectList(e);
        }.bind(this));
        this.listAddNode.addEvent("mouseover", function(e){
            this.listAddNode.setStyles(this.css.viewAddNode_over);
        }.bind(this));
        this.listAddNode.addEvent("mouseout", function(e){
            this.listAddNode.setStyles(this.css.viewAddNode);
        }.bind(this));
        this.listAddNode.setStyle("display","none");

        //this.viewTitleSepNode = new Element("div.viewTitleSepNode", {
        //    "styles": this.css.viewTitleSepNode
        //}).inject(this.viewTitleNode);

        this.viewTitleTextNode = new Element("div.viewTitleTextNode", {
            "styles": this.css.viewTitleTextNode,
            "text" : "分类展现"
        }).inject(this.viewTitleNode);

        //this.viewContainerNode = new Element("div.viewContainerNode", {
        //    "styles": this.css.viewContainerNode
        //}).inject(this.rightContent);

        this.viewNode = new Element("div.viewNode", {
            "styles": this.css.viewNode
        }).inject(this.viewAreaNode);
    },
    loadView : function( ){
        this.viewExplorer = new MWF.xApplication.cms.ColumnManager.CategoryExplorer.ViewExplorer(this.viewNode, this.app.restActions, {style : "full"});
        this.viewExplorer.app = this.app;
        this.viewExplorer.explorer = this;
        this.viewExplorer.load();
    },
    loadRightVerticalResize: function(){
        this.rightVerticalResize = new Drag(this.rightVerticalResizeNode, {
            "snap": 1,
            "onStart": function(el, e){
                var x = (Browser.name=="firefox") ? e.event.clientX : e.event.x;
                var y = (Browser.name=="firefox") ? e.event.clientY : e.event.y;
                el.store("position", {"x": x, "y": y});

                var size = this.formNode.getSize();
                el.store("initialHeight", size.y);
            }.bind(this),
            "onDrag": function(el, e){
                var size = this.rightContent.getSize();

                //			var x = e.event.x;
                var y = (Browser.name=="firefox") ? e.event.clientY : e.event.y;
                var position = el.retrieve("position");
                var dy = y.toFloat()-position.y.toFloat();

                var initialHeight = el.retrieve("initialHeight").toFloat();
                var height = initialHeight+dy;
                if (height<40) height = 40;
                if (height> size.y-40) height = size.y-40;

                this.topPercent = height/size.y;

                this.setRightVerticalResize();

            }.bind(this)
        });
    },
    setRightVerticalResize: function(){
        var nodeSize = this.node.getSize();
        var width = nodeSize.x; //-naviContainerNodeSize.x;
        this.elementContentListNode.setStyles({
            "width": ""+width+"px"//,
            //"margin-left": "" + m + "px"
        });
        this.rightContent.setStyle("width", ""+width+"px");
        //var formTitleSize = this.formTitleNode.getSize();
        //var viewTitleSize = this.viewTitleNode.getSize();
        var resizeNodeSize = this.rightVerticalResizeNode ? this.rightVerticalResizeNode.getSize() : {x:0,y:0};
        var w = width - resizeNodeSize.x - 2;
        var formWidth = w*this.formPercent;
        var viewWidth =  w-w*this.formPercent;
        this.formAreaNode.setStyle("width", ""+ formWidth +"px");
        this.formNode.setStyle("width", ""+ formWidth +"px");
        this.viewAreaNode.setStyle("width", ""+ viewWidth +"px");
        this.viewNode.setStyle("width", ""+ viewWidth +"px");
    },
    loadRightHorizontalResize: function(){
        this.rightHorizontalResize = new Drag(this.rightHorizontalResizeNode, {
            "snap": 1,
            "onStart": function(el, e){
                var x = (Browser.name=="firefox") ? e.event.clientX : e.event.x;
                var y = (Browser.name=="firefox") ? e.event.clientY : e.event.y;
                el.store("position", {"x": x, "y": y});

                var size = this.rightTopContent.getSize();
                el.store("initialHeight", size.y);
            }.bind(this),
            "onDrag": function(el, e){
                var size = this.rightContent.getSize();

                //			var x = e.event.x;
                var y = (Browser.name=="firefox") ? e.event.clientY : e.event.y;
                var position = el.retrieve("position");
                var dy = y.toFloat()-position.y.toFloat();

                var initialHeight = el.retrieve("initialHeight").toFloat();
                var height = initialHeight+dy;
                if (height<120) height = 120;
                if (height> size.y-120) height = size.y-120;

                this.topPercent = height/size.y;

                this.setRightHorizontalResize();


                if( this.formExplorer )this.formExplorer.setContentSize();
                if( this.viewExplorer )this.viewExplorer.setContentSize();
                if( this.categoryProperty )this.categoryProperty.setContentHeight();

            }.bind(this)
        });
    },
    setRightHorizontalResize: function(){
        //var toolbarSize = this.toolbarNode.getSize();
        var nodeSize = this.node.getSize();
        var pt = this.elementContentNode.getStyle("padding-top").toFloat();
        var pb = this.elementContentNode.getStyle("padding-bottom").toFloat();
        var naviContainerNodeSize = this.naviContainerNode.getSize();

        var height = nodeSize.y; //nodeSize.y-toolbarSize.y-pt-pb;
        this.elementContentListNode.setStyle("height", ""+height+"px");

        //this.naviArea.setStyle("height", ""+height+"px");
        //this.categoryListResizeNode.setStyle("height", ""+height+"px");

        this.rightContent.setStyle("height", ""+height+"px");


        var resizeHorizontalNodeSize = this.rightHorizontalResizeNode ? this.rightHorizontalResizeNode.getSize() : {x:0,y:0};
        var h = height - resizeHorizontalNodeSize.y;
        var topHeight = this.topPercent*h;
        var bottomHeight = h-topHeight;

        this.rightTopContent.setStyle("height", ""+topHeight+"px");
        this.rightBottomContent.setStyle("height", ""+bottomHeight+"px");

        this.formNode.setStyle("height", ""+(topHeight-50)+"px");
        this.viewNode.setStyle("height", ""+(topHeight-50)+"px");
    },
    setContentSize: function(){

        this.setRightHorizontalResize();

        //var count = (nodeSize.x/282).toInt();
        //var x = count*282;
        //var m = (nodeSize.x-x)/2-10;


        this.setRightVerticalResize();


    },
    afterClickCategory: function( category ){
        this.fireEvent("postClickSub",[category])
    }
});


MWF.xApplication.cms.ColumnManager.CategoryExplorer.CategoryList = new Class({
    Implements: [Options, Events],
    options : {
        currentCategoryId : "",
        columnId : ""
    },
    initialize: function(explorer, node, options){
        this.setOptions(options);
        this.explorer = explorer;
        this.app = explorer.app;
        this.node = $(node);
        this.css = explorer.css;

        this.categoryArr = [];
        this.categoryObj = {};
        this.currentCategory = null;
        this.currentView = null;

        this.load();
    },
    load: function(){
        var self = this;

        this.app.restActions.listCategory( this.options.columnId, function( json ){
            json.data.each(function(cData){

                this.loadCategoryByData( cData );

            }.bind(this));
            this.fireEvent("postLoad");
        }.bind(this),function(){
            this.fireEvent("postLoad");
        }.bind(this), true)

    },
    getCurrentNode : function(){
        return this.currentNode;
    },
    getCategoryNodes : function(){
        var nodes = [];
        this.categoryArr.each( function( category ){
            nodes.push( category.node );
        }.bind(this) );
        return nodes;
    },
    cancelCurrentNode : function(){
        if( this.currentNode ){
            if( this.currentNodeType == "category" ){
                this.currentNode.setStyles( this.css.categoryNaviNode );
                this.currentCategory._hideActions();
            }else{
                this.currentNode.setStyles( this.css.viewNaviNode )
            }
            this.currentNode = null;
        }
    },
    setCurrentCategoryById : function( categoryId ){
        if( categoryId && this.categoryObj[categoryId]){
            this.setCurrentCategory( this.categoryObj[categoryId] );
        }
    },
    setCurrentCategory : function( category ){
        this.cancelCurrentNode();

        this.currentCategory = category;
        this.currentNodeType = "category";
        this.currentNode = category.node;
        category.node.setStyles( this.css.categoryNaviNode_selected );
        this.currentTimeout = setTimeout( function(){
            category._showActions();
            this.currentTimeout = null;
            this.explorer.afterClickCategory( category );
        }.bind(this), 100 );

        if( category.options.isNew ){
            this.explorer.formCreateNode.setStyle("display","none");
        }else{
            this.explorer.formCreateNode.setStyle("display","");
        }
        this.explorer.formExplorer.refreshByCategory( category );

        if( category.options.isNew ){
            this.explorer.viewAddNode.setStyle("display","none");
            this.explorer.listAddNode.setStyle("display","none");
        }else{
            this.explorer.viewAddNode.setStyle("display","");
            this.explorer.listAddNode.setStyle("display","");
        }
        this.explorer.viewExplorer.refreshByCategory( category );
        this.explorer.categoryProperty.reload( category );
    },
    setCurrentView : function( view ){
        this.cancelCurrentNode();

        this.currentView = view;
        this.currentNodeType = "view";
        this.currentNode = view.node;
        view.node.setStyles( this.css.viewNaviNode_selected )
    },
    destroyCategory : function( category ){
        if( category.options.isNew ){
        }else{
            var id = category.data.id;
            delete this.categoryObj[id];
            var idx = this.categoryArr.indexOf( category );
            if( idx > -1 ){
                this.categoryArr.splice( idx,1);
            }
        }
        category.destroy();
    },
    loadCategoryByData: function( cData, relativeNode, relativePosition, callback ){
        var category = new MWF.xApplication.cms.ColumnManager.CategoryExplorer.Category( this, this.node, cData, {
            relativeNode : relativeNode,
            relativePosition: relativePosition
        });
        this.categoryObj[cData.id] = category;
        this.categoryArr.push( category );
        if( callback )callback( category );
        if( this.options.currentCategoryId && this.options.currentCategoryId == cData.id ){
            this.setCurrentCategory( category );
            this.options.currentCategoryId = "";
        }
    },
    loadCategoryById: function( id, relativeNode, relativePosition, callback ){
        this.app.restActions.getCategory( id , function( json ){
            var cData = json.data;
            var category = new MWF.xApplication.cms.ColumnManager.CategoryExplorer.Category( this, this.node, cData, {
                relativeNode: relativeNode,
                relativePosition: relativePosition
            });
            this.categoryObj[cData.id] = category;
            this.categoryArr.push( category );
            if( callback )callback( category );
            if( this.options.currentCategoryId && this.options.currentCategoryId == cData.id ){
                this.setCurrentCategory( category );
                this.options.currentCategoryId = "";
            }
        }.bind(this) )
    },
    adjustSeq : function( async ){
        var itemNodes = this.node.getElements( ".categoryNaviNode");
        var actions = this.app.restActions;
        itemNodes.each( function( itemNode, i ){
            var category = itemNode.retrieve("category");
            if( !category.options.isNew ){
                var data = category.data;
                var index = "000" + i; //(itemNodes.length - i);
                data.categorySeq = index.substr( index.length-3 ,3);
                actions.saveCategory(  data, null, null, async === false ? false : true );
            }
        })
    },
    newCategory : function( relativeNode, positon ){
        var category = new MWF.xApplication.cms.ColumnManager.CategoryExplorer.Category( this, this.node, {}, {
            "isNew" : true,
            "relativeNode" : relativeNode,
            "relativePosition" : positon
        });
        this.setCurrentCategory( category );
        //this.newCategoryNode = new Element("div.newCategoryNaviNode", {
        //    "styles": this.css.newCategoryNaviNode
        //}).inject( relativeNode || this.node , positon || "bottom" );
        //
        //this.input = new Element("input", {
        //    "type": "text",
        //    "value" : "请输入分类名称",
        //    "styles": this.css.categoryInput
        //}).inject(this.newCategoryNode);
        //
        //this.input.addEvents({
        //    "focus" : function( ev ){
        //        if( ev.target.value == "请输入分类名称" ){
        //            ev.target.value = "";
        //        }
        //    }.bind(this),
        //    "blur" : function( ev ){
        //        if( ev.target.value == "" ){
        //            ev.target.value = "请输入分类名称";
        //        }
        //    }.bind(this)
        //});
    }
});

MWF.xApplication.cms.ColumnManager.CategoryExplorer.Category = new Class({
    Implements: [Options, Events],
    options: {
        "style": "default",
        "isNew" : false,
        "relativeNode" : null,
        "relativePosition" : "bottom",
        //"propertyPath": "/x_component_process_FormDesigner/Module/Table$Td/table$td.html",
        "actions_edit" : [
            {
                "name": "saveCategory",
                "icon": "save.png",
                "icon_over": "save_over.png",
                "unselectedIcon" : "save_over.png",
                "unselectedIcon_over" : "save_unselected_over.png",
                "event": "click",
                "action": "saveCategory",
                "title": MWF.xApplication.cms.ColumnManager.LP.category.saveCategory
            },
            {
                "name": "cancel",
                "icon": "cancel.png",
                "icon_over": "cancel_over.png",
                "unselectedIcon" : "cancel_over.png",
                "unselectedIcon_over" : "cancel_unselected_over.png",
                "event": "click",
                "action": "cancel",
                "title": MWF.xApplication.cms.ColumnManager.LP.category.cancelEdit
            }
        ],
        "actions_read": [
            {
                "name": "editCategory",
                "icon": "edit.png",
                "icon_over": "edit_over.png",
                "event": "click",
                "action": "editCategory",
                "title": MWF.xApplication.cms.ColumnManager.LP.category.editCategory
            },
            {
                "name": "insertCateogryBefore",
                "icon": "insertBefore.png",
                "icon_over": "insertBefore_gray.png",
                "event": "click",
                "action": "insertCateogryBefore",
                "title": MWF.xApplication.cms.ColumnManager.LP.category.insertCateogryBefore
            },
            {
                "name": "insertCateogryAfter",
                "icon": "insertAfter.png",
                "icon_over": "insertAfter_over.png",
                "event": "click",
                "action": "insertCateogryAfter",
                "title": MWF.xApplication.cms.ColumnManager.LP.category.insertCateogryAfter
            },
            {
                "name": "deleteCategory",
                "icon": "trash.png",
                "icon_over": "trash_gray.png",
                "event": "click",
                "action": "deleteCategory",
                "title": MWF.xApplication.cms.ColumnManager.LP.category.deleteCategory
            },
            {
                "name": "moveCategory",
                "icon": "turn.png",
                "icon_over": "turn_gray.png",
                "event": "click",
                "action": "moveCategory",
                "title": MWF.xApplication.cms.ColumnManager.LP.category.moveCategory
            }
        ]
    },
    initialize: function ( list, container, data, options) {
        this.setOptions(options);
        this.list = list;
        this.explorer = list.explorer;
        this.app = list.app;
        this.css = list.css;
        this.container = $(container);
        this.data = data;
        this.lp = this.app.lp.category;
        this.categoryViewArr = [];
        this.categoryViewObj = {};

        this.load();
    },
    load: function( ){
        var _self = this;
        this.node = new Element("div.categoryNaviNode", {
            "styles": this.css.categoryNaviNode
        }).inject( this.options.relativeNode || this.container, this.options.relativePosition || "bottom"  );
        this.node.store("category", this);

        this.textNode = new Element("div.categoryNaviTextNode", {
            "styles": this.css.categoryNaviTextNode,
            "text": this.data.name
        }).inject(this.node);

        this.node.addEvents({
            "mouseover": function(){ if (_self.list.getCurrentNode() !=this && !_self.list.isOnDragging )this.setStyles(_self.css.categoryNaviNode_over) },
            "mouseout": function(){ if (_self.list.getCurrentNode() !=this)this.setStyles( _self.css.categoryNaviNode ) },
            "click" : function(){
                if( !_self.list.isOnDragging ){
                    _self.setCurrentNode(this);
                }
            }
        });

        this.loadViewNode();
        this.createIconAction();
        if( this.options.isNew ){
            this.editCategory();
        }
    },
    loadViewNode : function(){
        var viewNaviListNode = this.viewNaviListNode = new Element("div.viewNaviListNode",{
            "styles" : this.css.viewNaviListNode
        }).inject(this.node, "after");

        //this.app.restActions.listViewByCategory( this.data.id, function (d) {
        //    d.data.each(function(viewData ){
        //        this.createViewsNode(viewNaviListNode, viewData);
        //    }.bind(this));
        //    new Element("div", {
        //        "styles": this.css.viewNaviSepartorNode
        //    }).inject(viewNaviListNode);
        //
        //}.bind(this));
    },
    destroy: function(){
        this.node.destroy();
        this.viewNaviListNode.destroy();
        delete this;
    },
    setCategoryView : function(viewId, viewName){
        var data = {
            "categoryId" : this.data.id,
            "viewId" : viewId
        };
        this.app.restActions.addCategoryView(data, function(json){
            this.app.restActions.getView( viewId, function( js ){
                //this.createViewsNode( this.viewNaviListNode, js.data );
                this.app.notice("设置分类列表成功");
            }.bind(this) )
        }.bind(this), null, false);
    },
    setDefaultCategoryView: function( viewId, silent ){
        var d = this.data;
        d.defaultViewName = viewId;
        this.app.restActions.saveCategory(  d, function( json ){
            if(!silent)this.app.notice("设置默认视图成功");
        }.bind(this))
    },
    cancelCategoryView : function(viewId, viewName){
        this.app.restActions.listCategoryViewByCategory( this.data.id, function(json){
            json.data.each(function( d  ){
                if(d.viewId == viewId ){
                    this.app.restActions.deleteCategoryView(d.id, function(json){
                        var categoryView = this.categoryViewObj[viewId];
                        delete this.categoryViewObj[viewId];
                        this.categoryViewArr.erase( categoryView );
                        categoryView.destroy();
                        this.app.notice("取消分类列表成功");
                    }.bind(this), null, false);
                }
            }.bind(this));
        }.bind(this),null,false);

    },
    setImportView : function( viewId, viewAppId ){
        var d = this.data;
        d.importViewId = viewId;
        d.importViewAppId = viewAppId;
        var json = {
            viewId : viewId,
            viewAppId : viewAppId
        };
        this.app.restActions.saveCategoryImportView(d.id, json, function( json ){
            this.app.notice("设置分类导入导出视图成功");
        }.bind(this))
    },
    setCategoryAlias : function( alias ){
        var d = this.data;
        d.categoryAlias = alias;
        d.alias = alias;
        this.app.restActions.saveCategory(  d, function( json ){
            this.app.notice("设置分类别名成功");
        }.bind(this))
    },
    setDocumentType : function( documentType ){
        var d = this.data;
        d.documentType = documentType;
        this.app.restActions.saveCategory(  d, function( json ){
            this.app.notice("设置文档类型成功");
        }.bind(this))
    },
    setEditForm : function( formId, formName ){
        var d = this.data;
        d.formId = formId;
        d.formName = formName;
        this.app.restActions.saveCategory(  d, function( json ){
            this.app.notice("设置编辑表单成功");
        }.bind(this))
    },
    setReadForm : function( readformId, readformName ){
        var d = this.data;
        d.readFormId = readformId;
        d.readFormName = readformName;
        this.app.restActions.saveCategory(  d, function( json ){
            this.app.notice("设置阅读表单成功");
        }.bind(this))
    },
    saveCategory : function(){
        var d = this.data || {};
        if( this.options.isNew ){
            d.isNew = this.options.isNew;
            d.appId = this.app.options.column.id;
        }
        if( this.editMode && this.input ){
            var value = this.input.get("value");
            if( value == "" || value == "请输入分类名称" ){
                this.app.notice("请输入分类名称","error");
                return;
            }else{
                d.categoryName = value;
                d.name = value;
            }
        }
        this.app.restActions.saveCategory(  d, function( json ){
            this.list.loadCategoryById( json.data.id, this.node, "before", function( category ){
                this.list.setCurrentCategory( category );
                this.list.adjustSeq(false);
                this.list.destroyCategory( this );
            }.bind(this));
        }.bind(this));
    },
    cancel : function(){
        if( this.options.isNew ){
            this.destroy();
        }else{
            this.editMode = false;
            this.input.destroy();
            this.textNode.setStyle("display","");
            this._showActions();
        }
    },
    editCategory : function(){
        this.textNode.setStyle("display","none");
        this.editMode = true;
        this.input = new Element("input", {
            "type": "text",
            "value" : this.options.isNew ? "请输入分类名称" : this.data.name,
            "styles": this.css.categoryInput
        }).inject(this.node, "top");
        this.input.addEvents( {
            "click" : function(ev){
                this._showActions(true);
                if( this.list.currentCategory != this ){
                    this.setCurrentNode();
                }
                ev.stopPropagation();
            }.bind(this),
            "focus" : function( ev ){
                if( ev.target.value == "请输入分类名称" ){
                    ev.target.value = "";
                }
            }.bind(this),
            "blur" : function( ev ){
                if( ev.target.value == "" ){
                    ev.target.value = "请输入分类名称";
                }
            }.bind(this)
        });
        this._showActions(true);
    },
    setCurrentNode : function(){
        this.list.setCurrentCategory( this );
    },
    createViewsNode: function( container, data ){
        var view = new MWF.xApplication.cms.ColumnManager.CategoryExplorer.CategoryView( this, container, data  );
        var key = data.isDefault ? "default" : data.id;
        this.categoryViewObj[key] = view;
        this.categoryViewArr.push( view )
    },
    _showActions: function( ){
        if( this.editMode ){
            if (this.actionArea_edit){
                this.actionArea_edit.setStyle("display", "");
            }
            if (this.actionArea_read){
                this.actionArea_read.setStyle("display", "none");
            }
        }else{
            if (this.actionArea_edit){
                this.actionArea_edit.setStyle("display", "none");
            }
            if (this.actionArea_read){
                this.actionArea_read.setStyle("display", "");
            }
        }
    },
    _hideActions: function(){
        if (this.actionArea_read) this.actionArea_read.setStyle("display", "none");
        if (this.actionArea_edit){
            this.actionArea_edit.setStyle("display", "none");
        }
    },
    createIconAction: function(){
        this.actionNodes = this.actionNodes || {};
        if (!this.actionArea_read && !this.options.isNew){
            this.actionArea_read = new Element("div", {
                styles: this.css.actionArea
            }).inject(this.node);

            this.options.actions_read.each(function(action){
                var actionNode = this.actionNodes[action.name] = new Element("div", {
                    "styles": this.css.actionNodeStyles,
                    "title": action.title
                }).inject(this.actionArea_read);
                actionNode.setStyle("background", "url("+this.explorer.path+this.options.style+"/icon/"+action.icon+") no-repeat left center");
                actionNode.addEvent(action.event, function(e){
                    this[action.action](e);
                    e.stopPropagation();
                }.bind(this));
                actionNode.addEvents({
                    "mouseover": function(e){
                        if( this.obj.list.currentCategory == this.obj || !this.action.unselectedIcon_over ){
                            e.target.setStyle("background", "url("+this.obj.explorer.path+this.obj.options.style+"/icon/"+this.action.icon_over+") no-repeat left center");
                        }else{
                            e.target.setStyle("background", "url("+this.obj.explorer.path+this.obj.options.style+"/icon/"+this.action.unselectedIcon_over+") no-repeat left center");
                        }
                    }.bind({ obj : this, action : action }),
                    "mouseout": function(e){
                        if( this.obj.list.currentCategory == this.obj || !this.action.unselectedIcon ){
                            e.target.setStyle("background", "url("+this.obj.explorer.path+this.obj.options.style+"/icon/"+this.action.icon+") no-repeat left center");
                        }else{
                            e.target.setStyle("background", "url("+this.obj.explorer.path+this.obj.options.style+"/icon/"+this.action.unselectedIcon+") no-repeat left center");
                        }
                    }.bind({ obj : this, action : action })
                });


            }.bind(this));
        }
        if( !this.actionArea_edit ){
            this.actionArea_edit = new Element("div", {
                styles: this.css.actionArea
            }).inject(this.node);

            this.options.actions_edit.each(function(action){
                var actionNode = this.actionNodes[action.name] = new Element("div", {
                    "styles": this.css.actionNodeStyles,
                    "title": action.title
                }).inject(this.actionArea_edit);
                actionNode.setStyle("background", "url("+this.explorer.path+this.options.style+"/icon/"+action.icon+") no-repeat left center");
                actionNode.addEvent(action.event, function(e){
                    this[action.action](e);
                    e.stopPropagation();
                }.bind(this));
                actionNode.addEvents({
                    "mouseover": function(e){
                        if( this.obj.list.currentCategory == this.obj || !this.action.unselectedIcon_over ){
                            e.target.setStyle("background", "url("+this.obj.explorer.path+this.obj.options.style+"/icon/"+this.action.icon_over+") no-repeat left center");
                        }else{
                            e.target.setStyle("background", "url("+this.obj.explorer.path+this.obj.options.style+"/icon/"+this.action.unselectedIcon_over+") no-repeat left center");
                        }
                    }.bind({ obj : this, action : action }),
                    "mouseout": function(e){
                        if( this.obj.list.currentCategory == this.obj || !this.action.unselectedIcon ){
                            e.target.setStyle("background", "url("+this.obj.explorer.path+this.obj.options.style+"/icon/"+this.action.icon+") no-repeat left center");
                        }else{
                            e.target.setStyle("background", "url("+this.obj.explorer.path+this.obj.options.style+"/icon/"+this.action.unselectedIcon+") no-repeat left center");
                        }
                    }.bind({ obj : this, action : action })
                });


            }.bind(this));
        }
    },
    insertCateogryBefore: function(ev){
        this.list.newCategory( this.node, "before" );
    },
    insertCateogryAfter: function(ev){
        this.list.newCategory( this.viewNaviListNode, "after" );
    },
    deleteCategory: function(ev){
        var _self = this;
        this.app.confirm("warn", this.actionNodes.deleteCategory, this.lp.deleteCategoryTitle, this.lp.deleteCategoryConfirm, 300, 120, function(){
            _self._deleteCategory();
            this.close();
        }, function(){
            this.close();
        });
    },
    _deleteCategory: function(callback){
        this.app.restActions.removeCategory(this.data.id, function(){
            this.node.destroy();
            this.viewNaviListNode.destroy();
            if (callback) callback();
        }.bind(this),function(xhr, text, error){
            var errorText = error;
            if (xhr) errorText = xhr.responseText;
            if( errorText.indexOf( "referenced" ) > -1 ){
                var lp = this.explorer.app.lp.category;
                var text = lp.deleteFailAsHasDocument.replace(/{title}/g, this.data.name );
                this.app.notice( text,"error");
            }
        }.bind(this));
    },
    moveCategory: function(e){
        this._createMoveNode();
        this._setNodeMove(e);
        //this._hideActions();
    },
    _createMoveNode: function(){
        this.moveNode = new Element("div", {
            "styles": this.css.moduleNodeMove,
            "text": this.node.get("text"),
            "events": {
                "selectstart": function(){
                    return false;
                }
            }
        }).inject(this.container);
    },
    _setNodeMove: function(e){
        this._setMoveNodePosition(e);

        var droppables = this.list.getCategoryNodes(); //[this.container].concat(this.view.node, this.view.areaNode,this.view.columns);
        var nodeDrag = new Drag.Move(this.moveNode, {
            "droppables": droppables,
            "onEnter": function(dragging, inObj){
                var category = inObj.retrieve("category");
                if (category) category._dragIn(this);
            }.bind(this),
            "onLeave": function(dragging, inObj){
                var category = inObj.retrieve("category");
                if (category) category._dragOut(this);
            }.bind(this),
            "onDrag": function(e){
                this.list.isOnDragging = true;
                //this._setScroll();
            }.bind(this),
            "onDrop": function(dragging, inObj, e){
                if (inObj){
                    var category = inObj.retrieve("category");
                    if (category){
                        this._dragComplete( category );
                        category._dragDrop(this);
                    }else{
                        this._dragCancel(dragging);
                    }
                }else{
                    this._dragCancel(dragging);
                }
                if( this.dragInterval ){
                    clearInterval( this.dragInterval );
                    this.dragInterval = null;
                }
                setTimeout( function(){
                    this.list.isOnDragging = false;
                }.bind(this), 100 );
                e.stopPropagation();
            }.bind(this),
            "onCancel": function(dragging){
                if( this.dragInterval ){
                    clearInterval( this.dragInterval );
                    this.dragInterval = null;
                }
                setTimeout( function(){
                    this.list.isOnDragging = false;
                }.bind(this), 100 )
            }.bind(this)
        });
        nodeDrag.start(e);


        // this.form.moveModule = this;
        //this.form.recordCurrentSelectedModule = this.form.currentSelectedModule;

        //this.form.selected();

    },
    _dragIn : function(){   //移动时鼠标进入
        this.viewNaviListNode.setStyles( this.css.viewNaviListNode_dragIn );
    },
    _dragOut : function(){  //移动时鼠标移出
        this.viewNaviListNode.setStyles( this.css.viewNaviListNode );
    },
    _dragDrop : function(){ //移动到该对象时鼠标松开
        this.viewNaviListNode.setStyles( this.css.viewNaviListNode );
    },
    _dragComplete: function( category ){ //拖拽完成
        this.node.inject(category.viewNaviListNode,"after");
        this.viewNaviListNode.inject(this.node,"after");
        this.setCurrentNode();
        if (this.moveNode) this.moveNode.destroy();
        this.moveNode = null;
        this.list.adjustSeq();
    },
    _dragCancel: function(){  //拖拽取消
        if (this.moveNode) this.moveNode.destroy();
        this.moveNode = null;
    },
    _setScroll : function(){
        var areaNode = this.explorer.categoryScrollWrapNode || this.explorer.naviArea;

        var areaCrd = areaNode.getCoordinates();
        var topPoint = areaCrd.top;
        var bottomPoint = topPoint + areaCrd.height;

        var node = this.explorer.categoryScrollContentNode || this.explorer.naviNode;
        var coordinates = this.moveNode.getCoordinates();
        if( coordinates.top < topPoint && coordinates.bottom > topPoint ) {
            if (!this.dragInterval) {
                this.dragInterval = setInterval(function () {
                    if( areaNode.getScroll().y - 15  > 0 ){
                        areaNode.scrollTo( 0, areaNode.getScroll().y - 15);
                    }else{
                        areaNode.scrollTo(0, 0);
                    }
                }.bind(this), 100)
            }
        }else if( coordinates.top < bottomPoint &&  coordinates.bottom > bottomPoint ){
            if (!this.dragInterval) {
                this.dragInterval = setInterval(function () {
                    if( areaNode.getScroll().y + 15 < node.getSize().y ){
                        areaNode.scrollTo(0, areaNode.getScroll().y + 15);
                    }else{
                        areaNode.scrollTo( 0, node.getSize().y );
                    }
                }.bind(this), 100)
            }
        }else{
            if( this.dragInterval ){
                clearInterval( this.dragInterval );
                this.dragInterval = null;
            }
        }
    },
    _setMoveNodePosition: function(e){
        var x = e.page.x+2;
        var y = e.page.y+2;
        this.moveNode.positionTo(x, y);
    }
});

MWF.xApplication.cms.ColumnManager.CategoryExplorer.CategoryView = new Class({
    Implements: [Options, Events],
    initialize: function ( category, container, data, options) {
        this.setOptions(options);
        this.category = category;
        this.list = category.list;
        this.explorer = category.explorer;
        this.app = category.app;
        this.css = category.css;
        this.container = $(container);
        this.data = data;
        this.load();
    },
    destroy: function(){
        this.node.destroy();
        delete this;
    },
    load: function(){
        var _self = this;
        this.node = new Element("div.viewNaviNode", {
            "styles": this.css.viewNaviNode,
            "text" : this.data.isDefault ? this.app.lp.defaultView : this.data.name
        }).inject( this.container );

        this.node.addEvents({
            "mouseover": function(){ if (_self.list.getCurrentNode()!=this)this.setStyles(_self.css.viewNaviNode_over) },
            "mouseout": function(){ if (_self.list.getCurrentNode()!=this)this.setStyles( _self.css.viewNaviNode ) },
            "click": function (el) {
                _self.setCurrentNode(this);
            }
        })

    },
    setCurrentNode : function(){
        this.list.setCurrentView( this );
    }
});

MWF.xApplication.cms.ColumnManager.CategoryExplorer.FormExplorer =  new Class({
    Extends: MWF.xApplication.cms.ColumnManager.FormExplorer,
    Implements: [Options, Events],
    load: function(){
        //this.loadToolbar();
        this.loadContentNode();

        this.setNodeScroll();
        //this.loadElementList();
        this.refresh();
    },

    reload: function(){
        if( !this.node )return;
        this.initData();
        this.node.empty();
        this.loadContentNode();
        this.setNodeScroll();
        if( this.currentCategory ){
            this.refreshByCategory( this.currentCategory );
        }else{
            this.refresh();
        }
    },
    refreshByCategory: function( category ){
        this.currentCategory = category;
        var formId, formName, readFormId;
        if( category.data ){
            formId = category.data.formId;
            formName = category.data.formName;
            readFormId = category.data.readFormId;
        }
        if( this.currentEditForm ){
            this.currentEditForm.isEditForm = false;
            this.currentEditForm.setStyle();
            this.currentEditForm = null;
        }
        if( this.currentReadForm ){
            this.currentReadForm.isReadForm = false;
            this.currentReadForm.setStyle();
            this.currentReadForm = null;
        }
        this.refresh( category, function(){
            this.itemArray.each( function( form ){
                var d = form.data;
                if( d.id == formId ){
                    this.currentEditForm = form;
                    form.isEditForm = true;
                }
                if( d.id == readFormId ){
                    this.currentReadForm = form;
                    form.isReadForm = true;
                }
                form.setStyle();
            }.bind(this));
        }.bind(this));
    },
    refresh: function( category, callback ){
        if( this.noElementNode )this.noElementNode.destroy();
        if( !category ){
            this.itemArray.each( function( item ){
                item.node.setStyle("display","none");
            });
            this.noElementNode = new Element("div", {
                "styles": this.css.noElementNode,
                "text": "请先新建或选择分类"
            }).inject(this.elementContentListNode);
        }else if( category.options.isNew ){
            this.itemArray.each( function( item ){
                item.node.setStyle("display","none");
            });
            this.noElementNode = new Element("div", {
                "styles": this.css.noElementNode,
                "text": "请先保存分类"
            }).inject(this.elementContentListNode);
        }else{
            if( this.itemArray && this.itemArray.length ){
                this.itemArray.each( function( item ){
                    item.node.setStyle("display","");
                });
                if( callback )callback();
            }else{
                this.loadElementList( callback );
            }
        }
    },
    loadContentNode: function(){
        this.elementContentNode = new Element("div", {
            "styles": this.css.elementContentNode
        }).inject(this.node);

        this.elementContentListNode = new Element("div", {
            "styles": this.css.elementContentListNode
        }).inject(this.elementContentNode);

        this.setContentSize();
        this.app.addEvent("resize", function(){this.setContentSize();}.bind(this));
    },
    setContentSize: function(){
        var toolbarSize = this.toolbarNode ? this.toolbarNode.getSize() : { x : 0 , y : 0 };
        var nodeSize = this.node.getSize();
        var pt = this.elementContentNode.getStyle("padding-top").toFloat();
        var pb = this.elementContentNode.getStyle("padding-bottom").toFloat();

        var height = nodeSize.y-toolbarSize.y-pt-pb;
        var width = nodeSize.x; //-50;
        this.elementContentNode.setStyle("height", ""+height+"px");
        this.elementContentListNode.setStyles({
            "width": ""+width+"px"
        });
    },

    loadElementList: function( callback ){
        this._loadItemDataList(function(json){
            if (json.data.length){
                json.data.each(function(item){
                    var itemObj = this._getItemObject(item, this.itemArray.length+1);
                    itemObj.load();
                    this.itemObject[ item.id ] = itemObj;
                    this.itemArray.push( itemObj );
                }.bind(this));
                if( callback )callback();
            }else{
                var noElementNode = this.noElementNode = new Element("div", {
                    "styles": this.css.noElementNode,
                    "text": this.options.tooltip.noElement
                }).inject(this.elementContentListNode);
                noElementNode.addEvent("click", function(e){
                    this._createElement(e);
                }.bind(this));
            }
        }.bind(this));
    },
    _getItemObject: function(item, index){
        return new MWF.xApplication.cms.ColumnManager.CategoryExplorer.Form(this, item, {"index":index});
    },
    _loadItemDataList: function(callback){
        this.app.restActions.listForm(this.app.options.column.id,callback,null, false);
    }

});

MWF.xApplication.cms.ColumnManager.CategoryExplorer.Form = new Class({
    Extends: MWF.xApplication.cms.ColumnManager.FormExplorer.Form,
    options: {
        "where": "bottom",
        "index" : 1
    },
    initialize: function(explorer, item, options ){
        this.setOptions(options);
        this.explorer = explorer;
        this.app = explorer.app;
        this.css = this.explorer.css;
        this.lp = this.app.lp.form;
        this.data = item;
        this.container = this.explorer.elementContentListNode;

        this.icon = this._getIcon(); //"form.png";
    },
    load: function(){
        this.node = new Element("div", {
            "styles": this.css.itemNode,
            "events": {
                "mouseover": function(){
                    if (!this.isEditForm) this.node.setStyle("background-color","#f0f0f0");
                }.bind(this),
                "mouseout": function(){
                    var bgcolor = this.options.index % 2 == 0 ? "#f7f7f7" : "#fff";
                    if (!this.isEditForm) this.node.setStyle("background-color",bgcolor);
                }.bind(this)
            }
        }).inject(this.container);
        //if( this.options.index % 2 == 0 ){
        //    this.node.setStyle( "background-color", "#f7f7f7" );
        //}

        this.itemStatusNode = new Element("div", {
            "styles": this.css.itemStatusNode
        }).inject(this.node);
        this.itemStatusTopNode = new Element("div", {
            "styles": this.css.itemStatusTopNode
        }).inject(this.itemStatusNode);
        this.itemStatusBottomNode = new Element("div", {
            "styles": this.css.itemStatusBottomNode
        }).inject(this.itemStatusNode);

        if (this.data.name.icon) this.icon = this.data.name.icon;
        var iconUrl = this.explorer.path+""+this.explorer.options.style+"/processIcon/"+this.icon;

        var itemIconNode = new Element("div", {
            "styles": this.css.itemIconNode,
            "title" : "可以拖动到桌面"
        }).inject(this.node);
        itemIconNode.setStyle("background", "url("+iconUrl+") center center no-repeat");
        //new Element("img", {
        //    "src": iconUrl, "border": "0"
        //}).inject(itemIconNode);

        itemIconNode.makeLnk({
            "par": this._getLnkPar()
        });

        this.actionsArea = new Element("div.actionsArea",{
            styles : this.css.actionsArea
        }).inject(this.node);

        this.setEditAction =  new Element("div.setEditAction",{
            styles : this.css.setEditAction,
            "title" : "设为编辑表单"
        }).inject(this.actionsArea);
        this.setEditAction.addEvents({
            "click" : function(ev){ this.setEditForm(); }.bind(this),
            "mouseover" : function(ev){
                if( !this.isEditForm )this.setEditAction.setStyles( this.css.setEditAction_selected );
            }.bind(this),
            "mouseout" : function(ev){
                if( !this.isEditForm )this.setEditAction.setStyles( this.css.setEditAction );
            }.bind(this)
        });
        this.setReadAction =  new Element("div.setReadAction",{
            styles : this.css.setReadAction,
            "title" : "设为阅读表单"
        }).inject(this.actionsArea);
        this.setReadAction.addEvents({
            "click" : function(ev){
                this.setReadForm();
            }.bind(this),
            "mouseover" : function(ev){
                if( !this.isReadForm )this.setReadAction.setStyles( this.css.setReadAction_selected );
            }.bind(this),
            "mouseout" : function(ev){
                if( !this.isReadForm )this.setReadAction.setStyles( this.css.setReadAction );
            }.bind(this)
        });
        if (!this.explorer.options.noDelete){
            this.deleteActionNode = new Element("div.deleteAction", {
                "styles": this.css.deleteAction,
                "title" : "删除"
            }).inject(this.actionsArea);
            this.deleteActionNode.addEvent("click", function(e){
                this.deleteItem(e);
            }.bind(this));
            this.deleteActionNode.addEvents({
                "mouseover" : function(ev){
                    this.deleteActionNode.setStyles( this.css.deleteAction_over );
                }.bind(this),
                "mouseout" : function(ev){
                    this.deleteActionNode.setStyles( this.css.deleteAction );
                }.bind(this)
            });
        }

        var inforNode = new Element("div.itemInforNode", {
            "styles": this.css.itemInforNode
        }).inject(this.node);
        var inforBaseNode = new Element("div.itemInforBaseNode", {
            "styles": this.css.itemInforBaseNode
        }).inject(inforNode);

        this.itemTextTitleNode = new Element("div.itemTextTitleNode", {
            "styles": this.css.itemTextTitleNode,
            "text": this.data.name,
            "title": this.data.name,
            "events": {
                "click": function(e){this._open(e);e.stopPropagation();}.bind(this)
            }
        }).inject(inforBaseNode);

        //new Element("div", {
        //    "styles": this.css.itemTextAliasNode,
        //    "text": this.data.alias,
        //    "title": this.data.alias
        //}).inject(inforBaseNode);
        new Element("div.itemTextDateNode", {
            "styles": this.css.itemTextDateNode,
            "text": (this.data.updateTime || "")
        }).inject(inforBaseNode);

        //new Element("div", {
        //    "styles": this.css.itemTextDescriptionNode,
        //    "text": this.data.description || "",
        //    "title": this.data.description || ""
        //}).inject(inforNode);


        //this._isNew();
        //if( this.data.id == this.explorer.options.categoryFormId ){
        //    this.setEditFormStyle();
        //}else if( this.data.id == this.explorer.options.categoryReadFormId ){
        //    this.setReadFormStyle();
        //}
    },
    setEditForm : function(){
        if( this.explorer.currentEditForm ){
            this.explorer.currentEditForm.isEditForm = false;
            //this.explorer.currentEditForm.setNormalStyle();
            this.explorer.currentEditForm.setStyle();
        }
        this.explorer.currentEditForm = this;
        this.isEditForm = true;
        //this.setEditFormStyle();
        this.setStyle();
        this.explorer.currentCategory.setEditForm( this.data.id, this.data.name );
        //this.explorer.explorer.viewExplorer.refreshByForm( this.data.id, this.data.name );
    },
    setReadForm : function(){
        if( this.explorer.currentReadForm ){
            this.explorer.currentReadForm.isReadForm = false;
            //this.explorer.currentReadForm.setNormalStyle();
            this.explorer.currentReadForm.setStyle();
        }
        this.isReadForm = true;
        //this.setReadFormStyle();
        this.setStyle();
        this.explorer.currentCategory.setReadForm( this.data.id, this.data.name );
        this.explorer.currentReadForm = this;
    },
    setStyle : function(){
        if( this.isEditForm && this.isReadForm ){
            this.itemStatusTopNode.setStyles( { "background-color" : "#3c76b7" });
            this.itemStatusBottomNode.setStyles( { "background-color" : "#50e3c2" });
            this.node.setStyles( { "background-color" : "#f2f8ff" } );
            this.itemTextTitleNode.setStyles( { "color" : "#447bbb" } );
            this.setEditAction.setStyles( this.css.setEditAction_selected );
            this.setReadAction.setStyles( this.css.setReadAction_selected );
        }else if( this.isEditForm ){
            this.itemStatusTopNode.setStyles( { "background-color" : "#3c76b7" });
            this.itemStatusBottomNode.setStyles( { "background-color" : "#3c76b7" });
            this.node.setStyles( { "background-color" : "#f2f8ff" } );
            this.itemTextTitleNode.setStyles( { "color" : "#447bbb" } );
            this.setEditAction.setStyles( this.css.setEditAction_selected );
            this.setReadAction.setStyles( this.css.setReadAction );
        }else if( this.isReadForm ){
            this.itemStatusTopNode.setStyles( { "background-color" : "#50e3c2" });
            this.itemStatusBottomNode.setStyles( { "background-color" : "#50e3c2" });
            var bgcolor = this.options.index % 2 == 0 ? "#f7f7f7" : "#fff";
            this.node.setStyles( { "background-color" : bgcolor });
            this.itemTextTitleNode.setStyles( { "color" : "#888" } );
            this.setEditAction.setStyles( this.css.setEditAction );
            this.setReadAction.setStyles( this.css.setReadAction_selected );
        }else{
            var bgcolor = this.options.index % 2 == 0 ? "#f7f7f7" : "#fff";
            this.itemStatusTopNode.setStyles( { "background-color" : bgcolor });
            this.itemStatusBottomNode.setStyles( { "background-color" : bgcolor });
            this.node.setStyles( { "background-color" : bgcolor });
            this.itemTextTitleNode.setStyles( { "color" : "#888" } );
            this.setEditAction.setStyles( this.css.setEditAction );
            this.setReadAction.setStyles( this.css.setReadAction );
        }
    },

    _open: function(e){
        layout.desktop.getFormDesignerStyle(function(){
            var _self = this;
            var options = {
                "style": layout.desktop.formDesignerStyle,
                "onQueryLoad": function(){
                    this.actions = _self.explorer.actions;
                    this.category = _self;
                    this.options.id = _self.data.id;
                    this.column = _self.explorer.app.options.column;
                    this.application = _self.explorer.app.options.column;
                }
            };
            this.explorer.app.desktop.openApplication(e, "cms.FormDesigner", options);
        }.bind(this));
    },

    //deleteItem: function(ev){
    //    var _self = this;
    //    this.explorer.app.confirm("warn", ev.target, this.lp.deleteFormTitle, this.lp.deleteForm, 300, 120, function(){
    //        _self._deleteItem();
    //        this.close();
    //    }, function(){
    //        this.close();
    //    });
    //},
    _deleteItem: function(callback){
        this.explorer.app.restActions.removeForm(this.data.id, function(){
            this.node.destroy();
            if (callback) callback();
        }.bind(this));
    }
});

MWF.xApplication.cms.ColumnManager.CategoryExplorer.ViewExplorer =  new Class({
    Extends: MWF.xApplication.cms.ColumnManager.ViewExplorer,
    Implements: [Options, Events],
    initialize: function(node, actions, options){
        this.setOptions(options);
        this.setTooltip();

        this.path = "/x_component_cms_ColumnManager/$Explorer/";
        this.cssPath = "/x_component_cms_ColumnManager/$Explorer/"+this.options.style+"/css.wcss";

        this._loadCss();

        this.actions = actions;
        this.node = $(node);
        this.initData();
    },
    initData: function(){
        this.revealData = [];
        this.revealIds = [];

        this.itemArray = [];
        this.itemObject = {};
        this.deleteMarkItems = [];
    },
    reload: function(){
        if( !this.node )return;
        this.initData();
        this.node.empty();
        this.load();
    },
    load: function(){
        //this.loadToolbar();
        this.loadContentNode();

        this.setNodeScroll();
        this.loadElementList();
    },
    refreshByCategory : function( category ){
        this.category = category;
        this.options.categoryId = category.data.id;
        this.options.defaultViewId = category.data.defaultViewName || "defaultList";
        this.options.formId = category.data.formId;
        this.options.formName = category.data.formName;

        this.reload();
    },
    //refreshByForm : function(formId, formName){
    //    this.options.formId = formId;
    //    this.options.formName = formName;
    //    this.reload();
    //},
    loadElementList: function(){
        if( !this.category ){
            this.elementContentListNode.empty();
            var noElementNode = new Element("div", {
                "styles": this.css.noElementNode,
                "text": "请先新建或选择分类"
            }).inject(this.elementContentListNode);
        }else if( this.category.options.isNew ){
            this.elementContentListNode.empty();
            var noElementNode = new Element("div", {
                "styles": this.css.noElementNode,
                "text": "请先保存分类"
            }).inject(this.elementContentListNode);
        }else{
            this._loadItemDataList(function(json){
                if ( json && json.length){
                    json.each(function(item){
                        var itemObj = this._getItemObject(item, this.itemArray.length+1);
                        itemObj.load();
                        this.itemObject[ item.id ] = itemObj;
                        this.itemArray.push( itemObj );
                        this.setViewsBackground()
                    }.bind(this));
                }else{
                    this.elementContentListNode.empty();
                    var noElementNode = new Element("div", {
                        "styles": this.css.noElementNode,
                        "text": "未添加列表或者数据视图"
                    }).inject(this.elementContentListNode);
                    if (!this.options.noCreate){
                        noElementNode.addEvent("click", function(e){
                            this._createView(e);
                        }.bind(this));
                    }
                }
            }.bind(this));
        }

    },
    _loadItemDataList: function(callback){
        //var categoryId = this.options.categoryId;
        //var formId = this.options.formId;
        //this.categoryViewData = [];
        //this.categoryViewIds = [];
        //if( formId ){
        //    if( categoryId ){
        //        this.actions.listViewByCategory( categoryId, function(json){
        //            json.data = json.data || [];
        //            this.categoryViewData = json.data;
        //            json.data.each( function( v ){
        //                this.categoryViewIds.push( v.id );
        //            }.bind(this) )
        //        }.bind(this), null, false );
        //    }
        //    this.actions.listViewByForm( formId, callback );
        //}else{
        //    this.elementContentListNode.empty();
        //    var noElementNode = new Element("div", {
        //        "styles": this.css.noElementNode,
        //        "text": categoryId ? "请先设置分类的编辑表单！" : "请先选择分类！"
        //    }).inject(this.elementContentListNode);
        //}

        var categoryId = this.options.categoryId;
        var formId = this.options.formId;
        if( categoryId ){
            this.actions.getCategory( categoryId, function(json){
                var j = json.data.extContent;
                if( j ){
                    this.extContent = JSON.parse( j );
                }else{
                    this.extContent = { reveal : [] };
                    this.actions.listViewByCategory( categoryId, function(json){
                        ( json.data || [] ).each( function(d){
                            var itemData = {
                                "type" : "list",
                                "name" : d.name,
                                "showName" : d.name,
                                "id" : d.id,
                                "alias" : d.alias,
                                "appId" : d.appId,
                                "formId" : d.formId,
                                "formName" : d.formName
                            };
                            this.extContent.reveal.push( itemData );
                        }.bind(this));
                    }.bind(this), null, false );
                }
                if( !this.extContent || !this.extContent.reveal || this.extContent.reveal.length == 0 ){
                    this.extContent = { reveal : [{
                        id : "defaultList",
                        showName : "系统列表",
                        name : "系统列表"
                    }] };
                }
                this.revealData = this.extContent.reveal;
                this.revealData.each( function( d ){
                    this.revealIds.push( d.id );
                }.bind(this) );

                if(callback)callback(this.revealData);
            }.bind(this), null, false );
        }else{
            this.elementContentListNode.empty();
            var noElementNode = new Element("div", {
                "styles": this.css.noElementNode,
                "text": "请先选择分类！"
            }).inject(this.elementContentListNode);
        }

        //if( formId ){
        //    if( categoryId ){
        //        this.actions.listViewByCategory( categoryId, function(json){
        //            json.data = json.data || [];
        //            this.categoryViewData = json.data;
        //            json.data.each( function( v ){
        //                this.categoryViewIds.push( v.id );
        //            }.bind(this) )
        //        }.bind(this), null, false );
        //    }
        //    this.actions.listViewByForm( formId, callback );
        //}else{
        //    this.elementContentListNode.empty();
        //    var noElementNode = new Element("div", {
        //        "styles": this.css.noElementNode,
        //        "text": categoryId ? "请先设置分类的编辑表单！" : "请先选择分类！"
        //    }).inject(this.elementContentListNode);
        //}
    },
    _selectList: function(){
        MWF.xDesktop.requireApp("cms.ColumnManager", "widget.CMSListSelector", null, false);
        var opt  = {
            "count": 0,
            "title": "选择列表",
            "values": [],
            "appId" : this.app.options.column.id,
            "onComplete": function( array ){
                this.selectedList( array )
            }.bind(this)
        };
        var selector = new MWF.xApplication.Selector.ListSelector(this.app.content, opt );
        selector.load();
    },
    selectedList : function( array ){
        var repeated = [];
        var userful = [];
        array.each( function( a ){
            if( this.revealIds.contains(a.data.id ) ){
                repeated.push( a.data.name );
            }else{
                userful.push( a.data );
            }
        }.bind(this));
        userful.each( function(d){
            this.revealIds.push( d.id );
            var itemData = {
                "type" : "list",
                "name" : d.name,
                "showName" : d.name,
                "id" : d.id,
                "alias" : d.alias,
                "appId" : d.appId,
                "formId" : d.formId,
                "formName" : d.formName
            };
            this.revealData.push(itemData);
            var itemObj = this._getItemObject(itemData);
            itemObj.load();
            this.itemObject[ itemData.id ] = itemObj;
            this.itemArray.push( itemObj );
            if( d.id != "defaultList" ){
                this.category.setCategoryView( d.id, d.name );
            }
        }.bind(this));
        if( userful.length > 0 ){
            this.setViewsBackground();
            this.saveExtContent();
        }
        if( repeated.length > 0 ){
            this.app.notice( "列表" + repeated.join("、") + "已经存在了，不再重复添加！", "error" );
        }
    },
    _selectView: function(){
        MWF.xDesktop.requireApp("Selector", "package", null, false);

        var opt  = {
            "type" : "QueryView", //CMSView
            "title": "选择视图",
            "count" : 0,
            "values": [],
            "onComplete": function( array ){
                this.selectedView( array )
            }.bind(this)
        };
        var selector = new MWF.O2Selector(this.app.content, opt );

    },
    selectedView : function( array ){
        var repeated = [];
        var userful = [];
        array.each( function( a ){
            if( this.revealIds.contains(a.data.id ) ){
                repeated.push( a.data.name );
            }else{
                userful.push( a.data );
            }
        }.bind(this));
        userful.each( function(d){
            this.revealIds.push( d.id );
            var itemData = {
                "type" : "queryview",
                "name" : d.name,
                "showName" : d.name,
                "id" : d.id,
                "viewType" : d.type,
                "alias" : d.alias,
                //"appId" : d.appId,
                "appName" : d.appName || d.applicationName
            };
            var itemObj = this._getItemObject(itemData);
            itemObj.load();
            this.itemObject[ itemData.id ] = itemObj;
            this.itemArray.push( itemObj );
            this.revealData.push(itemData)
        }.bind(this));
        if( userful.length > 0 ){
            this.setViewsBackground();
            this.saveExtContent();
        }
        if( repeated.length > 0 ){
            this.app.notice( "视图" + repeated.join("、") + "已经存在了，不再重复添加！" , "error");
        }
    },
    //_createElementByForm: function(e){
    //    var _self = this;
    //    layout.desktop.getFormDesignerStyle(function(){
    //        var options = {
    //            "style": layout.desktop.formDesignerStyle,
    //            "onQueryLoad": function(){
    //                this.actions = _self.app.restActions;
    //                this.column = _self.app.options.column;
    //                this.application = _self.app.options.column;
    //                this.relativeForm = {"name":_self.options.formName, "id":_self.options.formId};
    //            },
    //            "onPostSave" : function(){
    //                _self.reload();
    //            }
    //        };
    //        layout.desktop.openApplication(e, "cms.ViewDesigner", options);
    //    }.bind(this));
    //},
    _getItemObject: function(item, index){
        var isDefaultView = this.options.defaultViewId == item.id;
        var view = new MWF.xApplication.cms.ColumnManager.CategoryExplorer.View(this, item, {
            //isCategoryView : ( this.categoryViewIds.indexOf( item.id ) > -1 ),
            isCategoryDefaultView : isDefaultView,
            index : index || 1
        });
        if( isDefaultView ) this.defaultView = view;
        return view;
    },
    loadContentNode: function(){
        if( this.elementContentNode )this.elementContentNode.destroy();
        this.elementContentNode = new Element("div.elementContentNode", {
            "styles": this.css.elementContentNode
        }).inject(this.node);

        this.elementContentListNode = new Element("div.elementContentListNode", {
            "styles": this.css.elementContentListNode
        }).inject(this.elementContentNode);

        var table = this.elementTable = new Element( "table", {
            "width":"99%",
            "border":"0",
            "cellpadding":"5",
            "cellspacing":"0",
            "styles" : this.css.listTable
        }).inject( this.elementContentListNode  );

        //var tr = new Element("tr").inject( table );
        //
        //new Element("td", {
        //    "text" : "显示标题",
        //    "styles": this.css.listTableHead
        //}).inject( tr );
        //
        //new Element("td", {
        //    "text" : "名称",
        //    "styles": this.css.listTableHead
        //}).inject( tr );
        //
        //new Element("td", {
        //    "text" : "类型",
        //    "styles": this.css.listTableHead
        //}).inject( tr );
        //
        //new Element("td", {
        //    "text" : "操作",
        //    "width" : "80",
        //    "styles": this.css.listTableHead
        //}).inject( tr );

        this.setContentSize();
        this.app.addEvent("resize", function(){this.setContentSize();}.bind(this));
    },
    saveExtContent : function(){
        this.extContent = { reveal : this.revealData };
        var extContentString = JSON.stringify( this.extContent );
        this.category.data.extContent = extContentString;
        this.app.restActions.saveCategoryExtContent(  { id : this.options.categoryId, content : extContentString }  ); //this.options.categoryId,
        if( !this.revealIds.contains( this.options.defaultViewId ) ){
            this.category.setDefaultCategoryView( "", true );
        }
    },
    saveByDom : function(){
       var trs = this.elementTable.getElements("tr");
        this.revealData = [];
        this.revealIds = [];
        for( var i=0; i<trs.length; i++ ){
            var tr = trs[i];
            var d = tr.retrieve("view").data;
            this.revealData.push( d );
            this.revealIds.push(d.id );
            var bgcolor = ( (i % 2 == 1) ? "#f7f7f7" : "#fff");
            tr.setStyle("background-color",bgcolor);
        }
        this.saveExtContent();
    },
    setViewsBackground : function(){
        this.elementTable.getElements("tr").each( function( tr, i ){
            tr.setStyle("background-color" , (i % 2 == 1) ? "#f7f7f7" : "#fff")
        }.bind(this))
    },
    setContentSize: function(){
        var toolbarSize = this.toolbarNode ? this.toolbarNode.getSize() : { x : 0 , y : 0 };
        var nodeSize = this.node.getSize();
        var pt = this.elementContentNode.getStyle("padding-top").toFloat();
        var pb = this.elementContentNode.getStyle("padding-bottom").toFloat();

        var height = nodeSize.y-toolbarSize.y-pt-pb;
        var width = nodeSize.x; //-50;
        this.elementContentNode.setStyle("height", ""+height+"px");
        this.elementContentListNode.setStyles({
            "width": ""+width+"px"
        });
    }
});

MWF.xApplication.cms.ColumnManager.CategoryExplorer.View = new Class({
    Extends: MWF.xApplication.cms.ColumnManager.ViewExplorer.View,
    Implements: [Options],
    options: {
        //"isCategoryView": false,
        "isCategoryDefaultView": false,
        "index": 1
    },
    initialize: function(explorer, item, options ){
        this.setOptions(options);
        this.explorer = explorer;
        this.css = this.explorer.css;
        this.data = item;
        if( !this.explorer.elementTable ){
            this.explorer.loadContentNode();
            this.container = this.explorer.elementTable;
        }else{
            this.container = this.explorer.elementTable;
        }

    },
    load: function(){
        this.node = new Element("tr", {
            "styles": this.css.listTableTr,
            "events": {
                "mouseover": function(){
                    this.nodeBackgroundColor = this.node.getStyle("background-color");
                    this.node.setStyle("background-color","#f0f0f0");
                }.bind(this),
                "mouseout": function(){
                    this.node.setStyle("background-color",this.nodeBackgroundColor);
                }.bind(this)
            }
        }).inject(this.container,  this.options.isCategoryDefaultView ? "top" : "bottom" );

        this.node.store("view", this);

        this.showNameNode = new Element("td", {
            "styles": this.css.listTableContent,
            "text" : this.data.showName,
            "events": {
                "click": function(e){this._open(e);e.stopPropagation();}.bind(this)
            }
        }).inject(this.node);
        if( this.options.isCategoryDefaultView ){
            this.showNameNode.setStyles({
                "background": "url(/x_component_cms_Module/$Main/default/icon/category_folder.png) no-repeat 10px 12px",
                "font-size" : "15px",
                "padding-left":"35px"
            });
        }else{
            this.showNameNode.setStyle("padding-left","35px");
        }

        //this.nameNode = new Element("td", {
        //    "styles": this.css.listTableContent,
        //    "text" : this.data.name,
        //    "events": {
        //        "click": function(e){this._open(e);e.stopPropagation();}.bind(this)
        //    }
        //}).inject(this.node);

        this.typeNode = new Element("td", {
            "styles": this.css.listTableContent,
            "width" : "30",
            "text" : this.data.type == "list" ? "列表" : "视图"
        }).inject(this.node);
        this.typeNode.setStyle("font-size","12px");

        this.actionsArea = new Element("td.actionsArea",{
            styles : this.css.listTableContent,
            "width" : "80"
        }).inject(this.node);

        this.createAction_read();
        this.createAction_edit();

        this.setStyle();
        this.loadTooltip();
        //this._isNew();
    },
    loadTooltip:function(){
        var tooltip = new MWF.xApplication.cms.ColumnManager.CategoryExplorer.ViewTooltip(this.explorer.app.content, this.node, this.explorer.app, this.data, {
            axis : "x",
            hiddenDelay : 100,
            displayDelay : 100,
            nodeStyles : {
                "z-index" : "101"
            }
        });
        this.node.store("tooltip",tooltip);
    },
    createAction_read: function(){
        this.readActionsArea = new Element("div").inject( this.actionsArea );
        this.editAction =  new Element("div.editViewAction",{
            styles : this.css.editViewAction,
            "title" : "编辑"
        }).inject(this.readActionsArea);
        this.editAction.addEvents({
            "click" : function(ev){
                this.editView();
            }.bind(this),
            "mouseover" : function(ev){
                this.editAction.setStyles( this.css.editViewAction_over )
            }.bind(this),
            "mouseout" : function(ev){
                this.editAction.setStyles( this.css.editViewAction )
            }.bind(this)
        });

        this.setDefaultCategoryViewAction =  new Element("div.setDefaultCategoryViewAction",{
            styles : this.css.setDefaultCategoryViewAction,
            "title" : "设为分类默认列表"
        }).inject(this.readActionsArea);
        this.setDefaultCategoryViewAction.addEvents({
            "click" : function(ev){
                this.setDefaultCategoryView();
            }.bind(this),
            "mouseover" : function(ev){
                if( !this.options.isCategoryDefaultView )this.setDefaultCategoryViewAction.setStyles( this.css.setDefaultCategoryViewAction_selected )
            }.bind(this),
            "mouseout" : function(ev){
                if( !this.options.isCategoryDefaultView )this.setDefaultCategoryViewAction.setStyles( this.css.setDefaultCategoryViewAction )
            }.bind(this)
        });


        this.cancelViewAction = new Element("div.cancelViewAction", {
            "styles": this.css.cancelViewAction,
            "title" : "取消"
        }).inject(this.readActionsArea);
        this.cancelViewAction.addEvent("click", function(e){
            this.cancelView(e);
        }.bind(this));
        this.cancelViewAction.addEvents({
            "mouseover" : function(ev){
                this.cancelViewAction.setStyles( this.css.cancelViewAction_over )
            }.bind(this),
            "mouseout" : function(ev){
                this.cancelViewAction.setStyles( this.css.cancelViewAction )
            }.bind(this)});


        this.trunViewAction =  new Element("div.trunViewAction",{
            styles : this.css.trunViewAction,
            "title" : "调整顺序"
        }).inject(this.readActionsArea);
        this.trunViewAction.addEvents({
            "click" : function(ev){
                this.turnViewOrder(ev);
            }.bind(this),
            "mouseover" : function(ev){
                this.trunViewAction.setStyles( this.css.turnViewAction_over )
            }.bind(this),
            "mouseout" : function(ev){
                this.trunViewAction.setStyles( this.css.trunViewAction )
            }.bind(this)
        });
    },

    createAction_edit: function(){
        this.editActionsArea = new Element("div").inject( this.actionsArea );
        this.editActionsArea.setStyle("display","none");

        this.saveViewAction =  new Element("div.saveViewAction",{
            styles : this.css.saveViewAction,
            "title" : "保存"
        }).inject(this.editActionsArea);
        this.saveViewAction.addEvents({
            "click" : function(ev){
                this.saveView();
            }.bind(this),
            "mouseover" : function(ev){
                this.saveViewAction.setStyles( this.css.saveViewAction_over )
            }.bind(this),
            "mouseout" : function(ev){
                this.saveViewAction.setStyles( this.css.saveViewAction )
            }.bind(this)
        });

        this.cancelEditViewAction =  new Element("div.cancelEditViewAction",{
            styles : this.css.cancelEditViewAction,
            "title" : "取消编辑"
        }).inject(this.editActionsArea);
        this.cancelEditViewAction.addEvents({
            "click" : function(ev){
                this.cancelEditView();
            }.bind(this),
            "mouseover" : function(ev){
                this.cancelEditViewAction.setStyles( this.css.cancelEditViewAction_over )
            }.bind(this),
            "mouseout" : function(ev){
                this.cancelEditViewAction.setStyles( this.css.cancelEditViewAction )
            }.bind(this)
        });
    },
    _open: function(e){
        var _self = this;
        if( this.explorer.isOnDragging )return;
        if( this.editMode )return; 
        if( this.data.id == "defaultList" ){

        }else if( this.data.type == "list" ){
            var options = {
                "onQueryLoad": function(){
                    this.actions = _self.explorer.actions;
                    this.category = _self;
                    this.options.id = _self.data.id;
                    this.column = _self.explorer.app.options.column;
                    this.application = _self.explorer.app.options.column;
                    this.options.noModifyName = _self.explorer.options.noModifyName;
                    this.options.readMode = _self.explorer.options.readMode;
                    this.options.formId = _self.data.formId;
                }
            };
            this.explorer.app.desktop.openApplication(e, "cms.ViewDesigner", options);
        }else if(this.data.viewType){  //数据中心
            MWF.Actions.get("x_query_assemble_designer").getApplication( this.data.appName, function( json ){
                var options = {
                    "onQueryLoad": function(){
                        //this.actions = _self.explorer.actions;
                        //this.category = _self;
                        this.options.id = _self.data.id;
                        this.options.application = json.data;
                        this.application = json.data;
                        //this.explorer = _self.explorer;
                    }
                };
                this.explorer.app.desktop.openApplication(e, "query.ViewDesigner", options);
            }.bind(this))
        }else{
            var options = {
                "onQueryLoad": function(){
                    this.actions = _self.explorer.actions;
                    this.category = _self;
                    this.options.id = _self.data.id;
                    this.application = _self.explorer.app.options.column;
                    this.explorer = _self.explorer;
                }
            };
            this.explorer.app.desktop.openApplication(e, "cms.QueryViewDesigner", options);
        }
    },
    //cancelCategoryView : function(){
    //    if( !this.options.isCategoryView ){
    //        return;
    //    }
    //    this.options.isCategoryView = false;
    //    this.setCategoryViewAction.setStyle("display","");
    //    this.cancelCategoryViewAction.setStyle("display","none");
    //    this.explorer.category.cancelCategoryView( this.data.id, this.data.name );
    //    this.setStyle();
    //},
    //setCategoryView : function(){
    //    if( this.options.isCategoryView ){
    //        return;
    //    }
    //    this.options.isCategoryView = true;
    //    this.setCategoryViewAction.setStyle("display","none");
    //    this.cancelCategoryViewAction.setStyle("display","");
    //    this.explorer.category.setCategoryView( this.data.id, this.data.name );
    //    this.setStyle();
    //},
    cancelView : function(){
        var tooltip = this.node.retrieve("tooltip");
        if( tooltip )tooltip.destroy();
        this.node.destroy();
        this.explorer.category.cancelCategoryView( this.data.id, this.data.name );
        this.explorer.saveByDom();
        delete this;
    },
    deleteView: function(callback){
        this.explorer.app.restActions.deleteView(this.data.id, function(){
            this.node.destroy();
            if (callback) callback();
        }.bind(this));
    },
    setDefaultCategoryView : function(){
        if( this.explorer.defaultView ){
            this.explorer.defaultView.cancelDefaultView();
        }
        this.options.isCategoryDefaultView = true;
        this.explorer.defaultView = this;
        this.explorer.options.defaultViewId = this.data.id;

        this.node.inject( this.container, "top" );
        this.showNameNode.setStyles({
            "background": "url(/x_component_cms_Module/$Main/default/icon/category_folder.png) no-repeat 10px 12px",
            "font-size" : "15px",
            "padding-left":"35px"
        });

        this.setStyle();

        this.explorer.saveByDom();
        this.explorer.category.setDefaultCategoryView( this.data.id );
    },
    cancelDefaultView : function(){
        this.options.isCategoryDefaultView = false;
        this.showNameNode.setStyles({
            "background": "",
            "font-size" : "14px",
            "padding-left":"35px"
        });
        this.setStyle();
    },
    saveView : function(){
       this.data.showName = this.input.get("value");
        this.explorer.saveByDom();
        this.showNameNode.empty();
        this.editMode = false;
        this.explorer.editMode = false;
        this.editActionsArea.setStyle("display","none");
        this.readActionsArea.setStyle("display","");
        this.showNameNode.set("text", this.data.showName);
    },
    editView : function(){
        this.showNameNode.empty();
        this.editMode = true;
        this.explorer.editMode = true;
        this.editActionsArea.setStyle("display","");
        this.readActionsArea.setStyle("display","none");
        this.input = new Element("input", {
            "type": "text",
            "value" : this.data.showName,
            "styles": this.css.viewInput
        }).inject(this.showNameNode);
    },
    cancelEditView : function(){
        this.showNameNode.empty();
        this.editMode = false;
        this.explorer.editMode = false;
        this.editActionsArea.setStyle("display","none");
        this.readActionsArea.setStyle("display","");
        this.showNameNode.set("text", this.data.showName);
    },
    turnViewOrder: function(e){
        this._createMoveNode();
        this._setNodeMove(e);
        //this._hideActions();
    },
    _createMoveNode: function(){
        this.moveNode = new Element("div", {
            "styles": this.css.moduleNodeMove,
            "text": this.data.showName,
            "events": {
                "selectstart": function(){
                    return false;
                }
            }
        }).inject(this.container);
    },
    _setNodeMove: function(e){
        this._setMoveNodePosition(e);

        var nodeDrag = new Drag.Move(this.moveNode, {
            "droppables": this.container.getElements("tr"),
            "onEnter": function(dragging, inObj){
                inObj.setStyle( "border-bottom" , "1px solid #ffa200" )
            }.bind(this),
            "onLeave": function(dragging, inObj){
                inObj.setStyle( "border-bottom" , "0px" )
            }.bind(this),
            "onDrag": function(e){
                this.explorer.isOnDragging = true;
            }.bind(this),
            "onDrop": function(dragging, inObj, e){
                if (inObj){
                    var view = inObj.retrieve("view");
                    if (view){
                        this._dragComplete( view );
                        inObj.setStyle( "border-bottom" , "0px" )
                    }else{
                        this._dragCancel(dragging);
                    }
                }else{
                    this._dragCancel(dragging);
                }
                setTimeout( function(){
                    this.explorer.isOnDragging = false;
                }.bind(this), 100 );
                e.stopPropagation();
            }.bind(this),
            "onCancel": function(dragging){
                setTimeout( function(){
                    this.explorer.isOnDragging = false;
                }.bind(this), 100 )
            }.bind(this)
        });
        nodeDrag.start(e);


        // this.form.moveModule = this;
        //this.form.recordCurrentSelectedModule = this.form.currentSelectedModule;

        //this.form.selected();

    },
    _dragComplete: function( view ){ //拖拽完成
        this.node.inject(view.node,"after");
        if (this.moveNode) this.moveNode.destroy();
        this.moveNode = null;
        this.explorer.saveByDom();
    },
    _dragCancel: function(){  //拖拽取消
        if (this.moveNode) this.moveNode.destroy();
        this.moveNode = null;
    },
    _setMoveNodePosition: function(e){
        var x = e.page.x+2;
        var y = e.page.y+2;
        this.moveNode.positionTo(x, y);
    },
    setStyle : function(){
        if( this.options.isCategoryDefaultView  && this.options.isCategoryView ){
            this.setDefaultCategoryViewAction.setStyles( this.css.setDefaultCategoryViewAction_selected );
        }else if( this.options.isCategoryView  ){
            this.setDefaultCategoryViewAction.setStyles( this.css.setDefaultCategoryViewAction );
            this.setCategoryViewAction.setStyles( this.css.setCategoryViewAction_selected );
        }else if( this.options.isCategoryDefaultView ){
            this.setDefaultCategoryViewAction.setStyles( this.css.setDefaultCategoryViewAction_selected );
        }else{
            this.setDefaultCategoryViewAction.setStyles( this.css.setDefaultCategoryViewAction );
        }
    }
});

MWF.xApplication.cms.ColumnManager.CategoryExplorer.CategoryProperty = new Class({
    initialize: function(app, node, category){
        this.app = app;
        this.node = $(node);
        this.category = category;
        this.lp = this.app.lp.category;
    },
    load: function(){
        this.propertyTitleBar = new Element("div", {
            "styles": this.app.css.propertyTitleBar,
            "text": this.lp.categoryProperty  //this.data.name || this.data.appName
        }).inject(this.node);

        this.contentNode =  new Element("div", {
            "styles": this.app.css.propertyContentNode
        }).inject(this.node);
        this.contentAreaNode =  new Element("div", {
            "styles": this.app.css.propertyContentAreaNode
        }).inject(this.contentNode);

        this.setContentHeight();
        this.setContentHeightFun = this.setContentHeight.bind(this);
        this.app.addEvent("resize", this.setContentHeightFun);
        MWF.require("MWF.widget.ScrollBar", function(){
            new MWF.widget.ScrollBar(this.contentNode, {"indent": false});
        }.bind(this));
    },
    reload: function( category ){
        if(category)this.category = category;
        this.contentAreaNode.empty();
        if(this.category && !this.category.options.isNew )this.loadContent();
    },
    loadContent : function(){
        //var properArea = new Element("div", {"styles" : this.app.css.categoryPropertyArea} ).inject(this.contentAreaNode);
        //new Element("div",{
        //    "styles" : this.app.css.catetoryPropertyTitle,
        //    text : this.lp.idLabel
        //}).inject(properArea);
        //new Element("div",{
        //    "styles" : this.app.css.catetoryPropertyValue,
        //    text : this.category.data.id
        //}).inject(properArea);

        this.baseActionAreaNode = new Element("div.baseActionAreaNode", {
            "styles": this.app.css.availableTitleNode
        }).inject(this.contentAreaNode);

        this.baseActionNode = new Element("div.propertyInforActionNode", {
            "styles": this.app.css.propertyInforActionNode
        }).inject(this.baseActionAreaNode);
        this.baseTextNode = new Element("div.baseTextNode", {
            "styles": this.app.css.baseTextNode,
            "text": this.app.lp.category.baseProperty
        }).inject(this.baseActionAreaNode);

        this.createPropertyContentNode();

        this.processContainer = new Element( "div").inject( this.contentAreaNode );
        this.createProcessNode();

        this.viewerContainer = new Element( "div").inject( this.contentAreaNode );
        MWF.xDesktop.requireApp("cms.ColumnManager", "widget.CategoryViewerSetting", null, false);
        this.viewerSetting = new MWF.xApplication.cms.ColumnManager.CategoryViewerSetting( this.app,
            this.app.lp.application.viewerSetting, this.viewerContainer, {
                objectId : this.category.data.id
            }
        );
        this.viewerSetting.category = this.category;
        this.viewerSetting.load();

        this.publisherContainer = new Element( "div").inject( this.contentAreaNode );
        MWF.xDesktop.requireApp("cms.ColumnManager", "widget.CategoryPublisherSetting", null, false);
        this.publisherSetting = new MWF.xApplication.cms.ColumnManager.CategoryPublisherSetting( this.app,
            this.app.lp.application.publisherSetting, this.publisherContainer, {
                objectId : this.category.data.id
            }
        );
        this.publisherSetting.category = this.category;
        this.publisherSetting.load();

        this.managerContainer = new Element( "div").inject( this.contentAreaNode );
        MWF.xDesktop.requireApp("cms.ColumnManager", "widget.CategoryManagerSetting", null, false);
        this.viewerSetting = new MWF.xApplication.cms.ColumnManager.CategoryManagerSetting( this.app,
            this.app.lp.application.managerSetting, this.managerContainer, {
                objectId : this.category.data.id
            }
        );
        this.viewerSetting.category = this.category;
        this.viewerSetting.load();
    },

    saveProcessApp : function( appId, appName ){
        var d = this.category.data;
        d.workflowAppId = appId;
        d.workflowAppName = appName;
        this.app.restActions.saveCategory(  d, function( json ){
            this.app.notice(this.lp.setProcessAppSucess);
        }.bind(this))
    },
    saveProcess : function( processId, processName ){
        var d = this.category.data;
        d.workflowFlag = processId;
        d.workflowName = processName;
        d.workflowType = processId ? "固定审批流" : "禁用审批流";
        this.app.restActions.saveCategory(  d, function( json ){
            this.app.notice(this.lp.setProcessSucess);
        }.bind(this))
    },
    createProcessNode : function(){
        this.processTitleNode = new Element("div.availableTitleNode", {
            "styles": this.app.css.availableTitleNode,
            "text": this.lp.useProcess
        }).inject(this.processContainer);

        this.processContentNode = new Element("div", {"styles": {"overflow": "hidden"}}).inject(this.processContainer);
        //this.itemsContentNode = new Element("div.availableItemsContentNode", {"styles": this.app.css.availableItemsContentNode}).inject(this.processContentNode);

        this.processAreaNode = new Element("div.processAreaNode" , {
            "styles": this.app.css.processAreaNode
        }).inject(this.processContentNode);
        //this.processAreaNode.setStyle("display","none");

        //new Element("div.formTitleSepNode" , {
        //    "styles": this.css.formTitleSepNode
        //}).inject(this.formTitleNode);

        //this.processTitleNode = new Elements("div.processTitleNode" , {
        //    "styles": this.css.processTitleNode,
        //    "text" : "流程"
        //}).inject(this.processAreaNode);

        //this.processSelectNode = new Elements("div.processSelectNode" , {
        //    "styles": this.css.processSelectNode
        //}).inject(this.processAreaNode);
        this.createProcessAppSelect( this.category.data.workflowAppId || "" );
        this.createProcessSelect( this.category.data.workflowAppId || "", this.category.data.workflowFlag || "" );
    },
    createProcessAppSelect : function( appId ){
        this.processAppSelect = new Element("select", { styles : this.app.css.processSelect }).inject( this.processAreaNode );
        new Element( "option" ,　{
            "value" : "",
            "text" : this.lp.selectProcessApp
        }).inject( this.processAppSelect );
        new Element( "option" ,　{
            "value" : "",
            "text" : this.lp.none
        }).inject( this.processAppSelect );
        MWF.Actions.get("x_processplatform_assemble_designer").listApplication( null, function( json ){
            json.data.each( function( d ){
                var opt = new Element( "option" ,　{
                    "value" : d.id,
                    "text" : d.name
                }).inject( this.processAppSelect );
                if( d.id == appId )opt.selected = true;
            }.bind(this))
        }.bind(this));
        this.processAppSelect.addEvent( "change" , function( ev ){
            var app = this.getSelectProcessApp();
            this.createProcessSelect( app.id );
            this.saveProcessApp( app.id, app.name );
        }.bind(this))
    },
    getSelectProcessApp : function(){
        var app;
        this.processAppSelect.getElements("option").each( function( option ){
            if( option.selected ){
                app = { "id" : option.value , "name" : option.text }
            }
        }.bind(this));
        return app;
    },
    setProcessApp : function( value ){
        var flag = true;
        this.processAppSelect.getElements("option").each( function( option ){
            if( flag ){
                if( option.value == value ){
                    option.selected = true;
                    flag = false;
                }
            }
        }.bind(this))
    },

    createProcessSelect : function( appId, processId ){
        if( this.processSelect )this.processSelect.destroy();
        if( !appId )return;
        this.processSelect = new Element("select", { styles : this.app.css.processSelect }).inject( this.processAreaNode );
        new Element( "option" ,　{
            "value" : "",
            "text" : this.lp.selectProcess
        }).inject( this.processSelect );
        new Element( "option" ,　{
            "value" : "",
            "text" : this.lp.none
        }).inject( this.processSelect );
        MWF.Actions.get("x_processplatform_assemble_designer").listProcess( appId, function( json ){
            json.data.each( function( d ){
                var opt = new Element( "option" ,　{
                    "value" : d.id,
                    "text" : d.name
                }).inject( this.processSelect );
                if( d.id == processId )opt.selected = true;
            }.bind(this))
        }.bind(this));
        this.processSelect.addEvent( "change" , function( ev ){
            var process = this.getSelectProcess();
            this.saveProcess( process.id, process.name );
        }.bind(this))
    },
    getSelectProcess : function(){
        var process;
        if( this.processSelect ){
            this.processSelect.getElements("option").each( function( option ){
                if( option.selected ){
                    process = { "id" : option.value , "name" : option.text }
                }
            }.bind(this))
        }
        return process;
    },
    setProcess : function( value ){
        var flag = true;
        if( this.processSelect ){
            this.processSelect.getElements("option").each( function( option ){
                if( flag ){
                    if( option.value == value ){
                        option.selected = true;
                        flag = false;
                    }
                }
            }.bind(this))
        }
    },

    setContentHeight: function(){
        var size = this.node.getSize();
        var titleSize = this.propertyTitleBar.getSize();
        var y = size.y-titleSize.y;
        this.contentNode.setStyle("height", ""+y+"px");
    },

    createPropertyContentNode: function(){

        this.propertyContentNode = new Element("div", {"styles": {"overflow": "hidden"}}).inject(this.contentAreaNode);

        var html = "<table cellspacing='0' cellpadding='0' border='0' width='95%' align='center'>";
        html += "<tr><td class='formTitle'>"+this.app.lp.category.idLabel +"</td><td id='formCategoryId' class='formValue'>"+this.category.data.id+"</td></tr>";
        html += "<tr><td class='formTitle'>"+this.app.lp.category.aliasLabel +"</td><td id='formCategoryAlias' class='formValue'></td></tr>"; //"+this.category.data.categoryAlias+"
       // html += "<tr><td class='formTitle'>"+this.app.lp.application.name+"</td><td id='formApplicationName'></td></tr>";
        html += "<tr><td class='formTitle'>"+this.app.lp.category.documentType +"</td><td id='formCategoryType' class='formValue'>"+"</td></tr>"; //(this.category.data.documentType || "" )+
        //     html += "<tr><td class='formTitle'>"+this.app.lp.application.icon+"</td><td id='formApplicationIcon'></td></tr>";
        html += "<tr><td class='formTitle'>"+this.app.lp.category.excelImportView +"</td><td class='formValue'><div id='formImportViewId' style='float:left;overflow:hidden;border:1px solid #ccc;border-radius: 3px;'></div></td></td></tr>"; //(this.category.data.documentType || "" )+
        html += "</table>";
        this.propertyContentNode.set("html", html);
        this.propertyContentNode.getElements("td.formTitle").setStyles(this.app.css.propertyBaseContentTdTitle);
        this.propertyContentNode.getElements("td.formValue").setStyles(this.app.css.propertyBaseContentTdValue);


        this.aliasInput = new MWF.xApplication.cms.ColumnManager.Input(this.propertyContentNode.getElement("#formCategoryAlias"), this.category.data.categoryAlias, this.category.css.formInput);
        this.aliasInput.editMode();
        this.aliasInput.input.addEvent("change", function( el ){
            this.category.setCategoryAlias( el.target.get("value") );
        }.bind(this));

        this.typeSelect = new MDomItem( this.propertyContentNode.getElement("#formCategoryType"), {
            type : "select",
            value : this.category.data.documentType || "信息",
            selectValue : [ "信息", "数据" ],
            event : {
                change : function( item ){
                    this.category.setDocumentType( item.getValue() );
                }.bind(this)
            }
        });
        this.typeSelect.load();

        var value = this.category.data.importViewId ? [{
            distinguishedName : this.category.data.importViewName,
            id : this.category.data.importViewId,
            name : this.category.data.importViewName
        }] : "不使用";
        this.importViewIdSelect = new MDomItem( this.propertyContentNode.getElement("#formImportViewId"), {
            type : "org",
            orgType : "QueryView",
            orgWidgetOptions : {
                canRemove : false
            },
            //value : this.category.data.importViewName || this.category.data.importViewId || "不使用",//this.category.data.documentType || "信息",
            value : value,
            event : {
                change : function( item ){
                    if( item.orgObject && item.orgObject.length > 0  ){
                        this.category.setImportView( item.orgObject[0].data.id, item.orgObject[0].data.applicationName );
                    }else{
                        this.category.setImportView( "", "" );
                    }
                }.bind(this)
            }
        }, null, this.app);
        this.importViewIdSelect.load();

        //this.nameInput = new MWF.xApplication.cms.ColumnManager.Input(this.propertyContentNode.getElement("#formApplicationName"), this.data.name || this.data.appName, this.app.css.formInput);
        //this.descriptionInput = new MWF.xApplication.cms.ColumnManager.Input(this.propertyContentNode.getElement("#formApplicationDescription"), this.data.description, this.app.css.formInput);
        //this.sortInput = new MWF.xApplication.cms.ColumnManager.Input(this.propertyContentNode.getElement("#formApplicationSort"), this.data.appInfoSeq, this.app.css.formInput);

        //this.typeInput = new MWF.xApplication.cms.ColumnManager.Input(this.propertyContentNode.getElement("#formApplicationType"), this.data.applicationCategory, this.app.css.formInput);
    }

});

MWF.xDesktop.requireApp("Template", "MTooltips", null, false);
MWF.xApplication.cms.ColumnManager.CategoryExplorer.ViewTooltip = new Class({
    Extends: MTooltips,
    _loadCustom : function( callback ){
        if(callback)callback();
    },
    _getHtml : function(){
        var data = this.data;
        var titleStyle = "font-size:12px;color:#333";
        var valueStyle = "font-size:12px;color:#666;padding-right:20px";

       if( data.type == "list" ){
           var html =
               "<table width='100%' bordr='0' cellpadding='7' cellspacing='0' style='margin:13px 13px 13px 13px;'>" +
               "<tr><td style='"+titleStyle+";' width='70'>"+"类型"+":</td>" +
               "    <td style='"+valueStyle+";'>" + "列表" + "</td></tr>" +
               "<tr><td style='"+titleStyle+";' width='70'>"+"列表名称"+":</td>" +
               "    <td style='"+valueStyle+";'>" + data.name + "</td></tr>" +
               "<tr><td style='"+titleStyle+"'>"+"关联表单" +":</td>" +
               "    <td style='"+valueStyle+"'>"+ (data.formName || "") +"</td></tr>" +
               "<tr><td style='"+titleStyle+"'>"+"别名"+":</td>" +
               "    <td style='"+valueStyle+"'>"+(data.alias||"") +"</td></tr>" +
               "</table>";
       }else{
           var html =
               "<table width='100%' bordr='0' cellpadding='7' cellspacing='0' style='margin:13px 13px 13px 13px;'>" +
               "<tr><td style='"+titleStyle+";' width='70'>"+"类型"+":</td>" +
               "    <td style='"+valueStyle+";'>" + "数据视图" + "</td></tr>" +
               "<tr><td style='"+titleStyle+";' width='70'>"+"视图名称"+":</td>" +
               "    <td style='"+valueStyle+";'>" + data.name + "</td></tr>" +
               "<tr><td style='"+titleStyle+"'>"+"栏目" +":</td>" +
               "    <td style='"+valueStyle+"'>"+ (data.appName || "") +"</td></tr>" +
               "<tr><td style='"+titleStyle+"'>"+"别名"+":</td>" +
               "    <td style='"+valueStyle+"'>"+(data.alias||"")+"</td></tr>" +
               "</table>";
       }
        return html;
    }
});


