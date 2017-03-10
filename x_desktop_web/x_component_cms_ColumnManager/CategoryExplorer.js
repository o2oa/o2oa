MWF.xDesktop.requireApp("cms.ColumnManager", "Explorer", null, false);
MWF.xDesktop.requireApp("cms.ColumnManager", "FormExplorer", null, false);
MWF.xDesktop.requireApp("cms.ColumnManager", "ViewExplorer", null, false);
MWF.xApplication.cms.ColumnManager.CategoryExplorer = new Class({
	Extends: MWF.xApplication.cms.ColumnManager.Explorer,
	Implements: [Options, Events],

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

        this.loadCategoryList();

        this.loadForm();

        this.loadView();

        this.loadProperty();
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

        this.rightBottomContent = new Element("div.rightBottomContent", {
            "styles": this.css.rightBottomContent
        }).inject(this.rightContent);

        //this.loadCategoryListResize();

        this.createFormNode();


        this.rightContentResizeNode = new Element("div.rightContentResizeNode", {
            "styles": this.css.rightContentResizeNode
        }).inject(this.rightTopContent);

        this.createViewNode();
        this.formPercent = 0.5;
        this.topPercent = 0.5;
        //this.loadRightContentResize();
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
    createProcessNode : function(){
        this.processAreaNode = new Element("div.processAreaNode" , {
            "styles": this.css.processAreaNode
        }).inject(this.formTitleNode);
        this.processAreaNode.setStyle("display","none");

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

        this.createProcessSelect();
    },
    createProcessSelect : function(){
        this.processSelect = new Element("select").inject( this.processAreaNode );
        new Element( "option" ,　{
            "value" : "",
            "text" : "选择使用的流程"
        }).inject( this.processSelect );
        new Element( "option" ,　{
            "value" : "",
            "text" : "无"
        }).inject( this.processSelect );
        this.actions.listProcess( "12b13867-282e-428b-8b90-7ad12191569f", function( json ){
            json.data.each( function( d ){
                   new Element( "option" ,　{
                       "value" : d.id,
                       "text" : d.name
                   }).inject( this.processSelect )
            }.bind(this))
        }.bind(this));
        this.processSelect.addEvent( "change" , function( ev ){
            var process = this.getSelectProcess();
            this.categoryList.currentCategory.saveProcess( process.id, process.name );
        }.bind(this))
    },
    getSelectProcess : function(){
        this.processSelect.get("option").each( function( option ){
            if( option.selected ){
                return { "id" : option.value , "name" : option.text }
            }
        }.bind(this))
    },
    setProcess : function( value ){
        var flag = true;
        this.processSelect.getElements("option").each( function( option ){
            if( flag ){
                if( option.value == value ){
                    option.selected = true;
                }
                flag = false;
            }
        }.bind(this))
    },

    createFormNode: function(){
        this.formAreaNode = new Element("div.formAreaNode", {
            "styles": this.css.formAreaNode
        }).inject(this.rightTopContent);

        this.formTitleNode = new Element("div.formTitleNode", {
            "styles": this.css.formTitleNode
        }).inject(this.formAreaNode);

        //this.createProcessNode();

        this.formCreateNode = new Element("div.formCreateNode", {
            "styles": this.css.formCreateNode
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

        this.viewCreateNode = new Element("div.viewCreateNode", {
            "styles": this.css.viewCreateNode
        }).inject(this.viewTitleNode);
        this.viewCreateNode.addEvent("click", function(e){
            this.viewExplorer._createView(e);
        }.bind(this));
        this.viewCreateNode.addEvent("mouseover", function(e){
            this.viewCreateNode.setStyles(this.css.viewCreateNode_over);
        }.bind(this));
        this.viewCreateNode.addEvent("mouseout", function(e){
            this.viewCreateNode.setStyles(this.css.viewCreateNode);
        }.bind(this));

        //this.viewTitleSepNode = new Element("div.viewTitleSepNode", {
        //    "styles": this.css.viewTitleSepNode
        //}).inject(this.viewTitleNode);

        this.viewTitleTextNode = new Element("div.viewTitleTextNode", {
            "styles": this.css.viewTitleTextNode,
            "text" : "选择分类列表"
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
    loadCategoryListResize: function(){
        this.categoryistResize = new Drag(this.categoryListResizeNode,{
            "snap": 1,
            "onStart": function(el, e){
                var x = (Browser.name=="firefox") ? e.event.clientX : e.event.x;
                var y = (Browser.name=="firefox") ? e.event.clientY : e.event.y;
                el.store("position", {"x": x, "y": y});

                var size = this.naviContainerNode.getSize();
                el.store("initialWidth", size.x);
            }.bind(this),
            "onDrag": function(el, e){
                var x = (Browser.name=="firefox") ? e.event.clientX : e.event.x;
//				var y = e.event.y;
                var bodySize = this.elementContentNode.getSize();
                var position = el.retrieve("position");
                var initialWidth = el.retrieve("initialWidth").toFloat();
                var dx = x.toFloat() - position.x.toFloat();

                var width = initialWidth+dx;
                if (width> bodySize.x/2) width =  bodySize.x/2;
                if (width<130) width = 130;
                this.naviContainerNode.setStyle("width", width);
                this.rightContent.setStyle("margin-left", width+1);
                var w = bodySize.x-width;
                this.rightContent.setStyle("width", w);
                this.formNode.setStyle("width", ""+(w-15)+"px");
                this.viewNode.setStyle("width", ""+(w-15)+"px");
                if( this.formExplorer )this.formExplorer.setContentSize();
                if( this.viewExplorer )this.viewExplorer.setContentSize();
                //this.tab.pages.each(function(page){
                //this.view.setViewWidth();
                //});
            }.bind(this)
        });
//        this.categoryListResizeNode.addEvents({
//            "touchstart": function(e){
//                el = e.target;
//                var x = (Browser.name=="firefox") ? e.page.clientX : e.page.x;
//                var y = (Browser.name=="firefox") ? e.page.clientY : e.page.y;
//                el.store("position", {"x": x, "y": y});
//
//                var size = this.naviContainerNode.getSize();
//                el.store("initialWidth", size.x);
//            }.bind(this),
//            "touchmove": function(e){
//                //Object.each(e, function(v, k){
//                //    alert(k+": "+ v);
//                //});
//                el = e.target;
//
//                var x = (Browser.name=="firefox") ? e.page.clientX : e.page.x;
////				var y = e.event.y;
//                var bodySize = this.elementContentNode.getSize();
//                var position = el.retrieve("position");
//                var initialWidth = el.retrieve("initialWidth").toFloat();
//                var dx = x.toFloat() - position.x.toFloat();
//
//                var width = initialWidth+dx;
//                if (width> bodySize.x/2) width =  bodySize.x/2;
//                if (width<40) width = 40;
//                this.naviContainerNode.setStyle("width", width);
//                this.rightContent.setStyle("margin-left", width+1);
//                var w = bodySize.x-width-10;
//                this.rightContent.setStyle("width", w);
//                this.formNode.setStyle("width", ""+w+"px");
//                this.viewNode.setStyle("width", ""+w+"px");
//            }.bind(this)
//        });
    },
    loadRightContentResize: function(){
        this.rightContentResize = new Drag(this.rightContentResizeNode, {
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

                this.setRightContentResize();

            }.bind(this)
        });
    },
    setRightContentResize: function(){
        var size = this.rightContent.getSize();
        var resizeNodeSize = this.rightContentResizeNode ? this.rightContentResizeNode.getSize() : {x:0,y:0};
        var topTitleSize = 50; //this.rightTopContent.getSize();
        var bottomTitleSize = 50; //this.rightBottomContent.getSize();
        var height = size.y-resizeNodeSize.y-topTitleSize-bottomTitleSize;

        var topHeight = this.topPercent*height;
        var bottomHeight = height-topHeight;

        this.rightTopContent.setStyle("height", ""+topHeight+"px");
        this.rightBottomContent.setStyle("height", ""+bottomHeight+"px");
        if( this.formExplorer )this.formExplorer.setContentSize();
        if( this.viewExplorer )this.viewExplorer.setContentSize();
    },
    setContentSize: function(){

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

        var topHeight = this.topPercent*height;
        var bottomHeight = height-topHeight;

        this.rightTopContent.setStyle("height", ""+topHeight+"px");
        this.rightBottomContent.setStyle("height", ""+bottomHeight+"px");

        this.formNode.setStyle("height", ""+(topHeight-30)+"px");
        this.viewNode.setStyle("height", ""+(topHeight-30)+"px");

        //var count = (nodeSize.x/282).toInt();
        //var x = count*282;
        //var m = (nodeSize.x-x)/2-10;

        var width = nodeSize.x; //-naviContainerNodeSize.x;
        this.elementContentListNode.setStyles({
            "width": ""+width+"px"//,
            //"margin-left": "" + m + "px"
        });
        this.rightContent.setStyle("width", ""+width+"px");
        //var formTitleSize = this.formTitleNode.getSize();
        //var viewTitleSize = this.viewTitleNode.getSize();
        var resizeNodeSize = this.rightContentResizeNode.getSize();
        var w = width - resizeNodeSize.x;
        this.formNode.setStyle("width", ""+ w*this.formPercent +"px");
        this.viewNode.setStyle("width", ""+ (w-w*this.formPercent) +"px");


    }
});


MWF.xApplication.cms.ColumnManager.CategoryExplorer.CategoryList = new Class({
    Implements: [Options, Events],
    options : {
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
        }.bind(this), 100 );

        //if( !this.category ){
        //    this.explorer.setProcess("");
        //}else if( this.category.options.isNew ) {
        //    this.explorer.setProcess("");
        //}else{
        //    this.explorer.setProcess( category.data.processId );
        //}

        this.explorer.formExplorer.refreshByCategory( category );

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
    },
    loadCategoryById: function( id, relativeNode, relativePosition, callback ){
        this.app.restActions.getCategory( id , function( json ){
            var cData = json.data;
            var category = new MWF.xApplication.cms.ColumnManager.CategoryExplorer.Category( this, this.node, cData, {
                relativeNode: relativeNode,
                relativePosition: relativePosition
            });
            this.categoryObj[cData.id] = category;
            this.categoryArr.push( category )
            if( callback )callback( category );
        }.bind(this) )
    },
    adjustSeq : function( async ){
        var itemNodes = this.node.getElements( ".categoryNaviNode");
        var actions = this.app.restActions;
        itemNodes.each( function( itemNode, i ){
            var category = itemNode.retrieve("category");
            if( !category.options.isNew ){
                var data = category.data;
                var index = "000" + (itemNodes.length - i);
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
        //this.setCurrentCategory( category );
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
                "event": "click",
                "action": "saveCategory",
                "title": MWF.xApplication.cms.ColumnManager.LP.category.saveCategory
            },
            {
                "name": "cancel",
                "icon": "cancel.png",
                "icon_over": "cancel_over.png",
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
                "icon_over": "insertBefore_over.png",
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
                "icon_over": "trash_over.png",
                "event": "click",
                "action": "deleteCategory",
                "title": MWF.xApplication.cms.ColumnManager.LP.category.deleteCategory
            },
            {
                "name": "moveCategory",
                "icon": "turn.png",
                "icon_over": "turn_over.png",
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
    setDefaultCategoryView: function( viewId, viewName ){
        var d = this.data;
        d.defaultViewName = viewId;
        this.app.restActions.saveCategory(  d, function( json ){
            this.app.notice("设置默认视图成功");
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
    saveProcess : function( processId, processName ){
        var d = this.data;
        d.workflowFlag = processId;
        d.workflowAppName = processName;
        this.app.restActions.saveCategory(  d, function( json ){
            this.app.notice("设置流程成功");
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
        if (!this.actionArea_read){
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
                        e.target.setStyle("background", "url("+this.obj.explorer.path+this.obj.options.style+"/icon/"+this.action.icon_over+") no-repeat left center");
                    }.bind({ obj : this, action : action }),
                    "mouseout": function(e){
                        e.target.setStyle("background", "url("+this.obj.explorer.path+this.obj.options.style+"/icon/"+this.action.icon+") no-repeat left center");
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
                        e.target.setStyle("background", "url("+this.obj.explorer.path+this.obj.options.style+"/icon/"+this.action.icon_over+") no-repeat left center");
                    }.bind({ obj : this, action : action }),
                    "mouseout": function(e){
                        e.target.setStyle("background", "url("+this.obj.explorer.path+this.obj.options.style+"/icon/"+this.action.icon+") no-repeat left center");
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
        //debugger;
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
        var formId = category.data.formId;
        var formName = category.data.formName;
        var readFormId = category.data.readFormId;
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
            })
            this.noElementNode = new Element("div", {
                "styles": this.css.noElementNode,
                "text": "请先新建或选择分类"
            }).inject(this.elementContentListNode);
        }else if( category.options.isNew ){
            this.itemArray.each( function( item ){
                item.node.setStyle("display","none");
            })
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
        this.explorer.explorer.viewExplorer.refreshByForm( this.data.id, this.data.name );
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
        this.explorer.app.restActions.deleteForm(this.data.id, function(){
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
    load: function(){
        //this.loadToolbar();
        this.loadContentNode();

        this.setNodeScroll();
        this.loadElementList();
    },
    refreshByCategory : function( category ){
        this.category = category;
        this.options.categoryId = category.data.id;
        this.options.defaultViewId = category.data.defaultViewName;
        this.options.formId = category.data.formId;
        this.options.formName = category.data.formName;
        this.reload();
    },
    refreshByForm : function(formId, formName){
        this.options.formId = formId;
        this.options.formName = formName;
        this.reload();
    },
    loadElementList: function(){
        if( !this.category ){
            var noElementNode = new Element("div", {
                "styles": this.css.noElementNode,
                "text": "请先新建或选择分类"
            }).inject(this.elementContentListNode);
        }else if( this.category.options.isNew ){
            var noElementNode = new Element("div", {
                "styles": this.css.noElementNode,
                "text": "请先保存分类"
            }).inject(this.elementContentListNode);
        }else{
            this._loadItemDataList(function(json){
                if (json.data.length){
                    json.data.each(function(item){
                        var itemObj = this._getItemObject(item, this.itemArray.length+1);
                        itemObj.load();
                        this.itemObject[ item.id ] = itemObj;
                        this.itemArray.push( itemObj );
                    }.bind(this));
                }else{
                    var noElementNode = new Element("div", {
                        "styles": this.css.noElementNode,
                        "text": "未设置表单“"+ (this.options.formName || "") +"”的关联列表，点击创建关联列表"
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
        var categoryId = this.options.categoryId;
        var formId = this.options.formId;
        this.categoryViewData = [];
        this.categoryViewIds = [];
        if( formId ){
            if( categoryId ){
                this.actions.listViewByCategory( categoryId, function(json){
                    this.categoryViewData = json.data;
                    json.data.each( function( v ){
                        this.categoryViewIds.push( v.id );
                    }.bind(this) )
                }.bind(this), null, false );
            }
            this.actions.listViewByForm( formId, callback );
        }else{
            //this.actions.listView(this.app.options.column.id,callback);
            var noElementNode = new Element("div", {
                "styles": this.css.noElementNode,
                "text": categoryId ? "请先设置分类的编辑表单！" : "请先选择分类！"
            }).inject(this.elementContentListNode);
        }
    },
    _createView: function(){
        if( this.options.formId ){
            this._createElementByForm()
        }else{
            this._createElement();
        }
    },
    _createElementByForm: function(e){
        var _self = this;
        layout.desktop.getFormDesignerStyle(function(){
            var options = {
                "style": layout.desktop.formDesignerStyle,
                "onQueryLoad": function(){
                    this.actions = _self.app.restActions;
                    this.column = _self.app.options.column;
                    this.application = _self.app.options.column;
                    this.relativeForm = {"name":_self.options.formName, "id":_self.options.formId};
                },
                "onPostSave" : function(){
                    _self.reload();
                }
            };
            layout.desktop.openApplication(e, "cms.ViewDesigner", options);
        }.bind(this));
    },
    _getItemObject: function(item, index){
        var isDefaultView = this.options.defaultViewId == item.id;
        var view = new MWF.xApplication.cms.ColumnManager.CategoryExplorer.View(this, item, {
            isCategoryView : ( this.categoryViewIds.indexOf( item.id ) > -1 ),
            isCategoryDefaultView : isDefaultView,
            index : index
        });
        if( isDefaultView ) this.defaultView = view;
        return view;
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
    }
});

MWF.xApplication.cms.ColumnManager.CategoryExplorer.View = new Class({
    Extends: MWF.xApplication.cms.ColumnManager.ViewExplorer.View,
    Implements: [Options],
    options: {
        "where": "bottom",
        "isCategoryView": false,
        "isCategoryDefaultView" : false,
        "index" : 1
    },
    initialize: function(explorer, item, options ){
        this.setOptions(options);
        this.explorer = explorer;
        this.css = this.explorer.css;
        this.data = item;
        this.container = this.explorer.elementContentListNode;

        this.icon = this._getIcon();

        //this.icon = "list.png";
    },
    load: function(){
        this.node = new Element("div", {
            "styles": this.css.itemNode,
            "events": {
                "mouseover": function(){
                    this.node.setStyle("background-color","#f0f0f0");
                }.bind(this),
                "mouseout": function(){
                    var bgcolor = this.options.index % 2 == 0 ? "#f7f7f7" : "#fff";
                    this.node.setStyle("background-color",bgcolor);
                }.bind(this)
            }
        }).inject(this.container);

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
            "styles": this.css.itemIconNode
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

        this.cancelCategoryViewAction =  new Element("div.cancelCategoryViewAction",{
            styles : this.css.cancelCategoryViewAction,
            "title" : "取消分类列表"
        }).inject(this.actionsArea);
        this.cancelCategoryViewAction.addEvents({
            "click" : function(ev){
                this.cancelCategoryView();
            }.bind(this)
        });

        this.setCategoryViewAction =  new Element("div.setCategoryViewAction",{
            styles : this.css.setCategoryViewAction,
            "title" : "设为分类列表"
        }).inject(this.actionsArea);
        this.setCategoryViewAction.addEvents({
            "click" : function(ev){
                this.setCategoryView();
            }.bind(this),
            "mouseover" : function(ev){
               this.setCategoryViewAction.setStyles( this.css.setCategoryViewAction_selected )
            }.bind(this),
            "mouseout" : function(ev){
                this.setCategoryViewAction.setStyles( this.css.setCategoryViewAction )
            }.bind(this)
        });

        if( this.options.isCategoryView ){
            this.setCategoryViewAction.setStyle("display","none");
        }else{
            this.cancelCategoryViewAction.setStyle("display","none");
        }

        this.setDefaultCategoryViewAction =  new Element("div.setDefaultCategoryViewAction",{
            styles : this.css.setDefaultCategoryViewAction,
            "title" : "设为分类默认列表"
        }).inject(this.actionsArea);
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
                this.deleteActionNode.setStyles( this.css.deleteAction_over )
            }.bind(this),
               "mouseout" : function(ev){
                this.deleteActionNode.setStyles( this.css.deleteAction )
            }.bind(this)})
        }

        //if (!this.explorer.options.noDelete){
        //    this.deleteActionNode = new Element("div", {
        //        "styles": this.css.deleteActionNode
        //    }).inject(this.node);
        //    this.deleteActionNode.addEvent("click", function(e){
        //        this.deleteItem(e);
        //    }.bind(this));
        //}

        var inforNode = new Element("div", {
            "styles": this.css.itemInforNode
        }).inject(this.node);
        var inforBaseNode = new Element("div", {
            "styles": this.css.itemInforBaseNode
        }).inject(inforNode);

        new Element("div", {
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
        new Element("div", {
            "styles": this.css.itemTextDateNode,
            "text": (this.data.updateTime || "")
        }).inject(inforBaseNode);

        //new Element("div", {
        //    "styles": this.css.itemTextDescriptionNode,
        //    "text": this.data.description || "",
        //    "title": this.data.description || ""
        //}).inject(inforNode);



        //this._customNodes();
        this.setStyle();
        //this._isNew();
    },
    setCategoryView : function(){
        if( this.options.isCategoryView ){
            return;
        }
        this.options.isCategoryView = true;
        this.setCategoryViewAction.setStyle("display","none");
        this.cancelCategoryViewAction.setStyle("display","");
        this.explorer.category.setCategoryView( this.data.id, this.data.name );
        this.setStyle();
    },
    cancelCategoryView : function(){
        if( !this.options.isCategoryView ){
            return;
        }
        this.options.isCategoryView = false;
        this.setCategoryViewAction.setStyle("display","");
        this.cancelCategoryViewAction.setStyle("display","none");
        this.explorer.category.cancelCategoryView( this.data.id, this.data.name );
        this.setStyle();
    },
    setDefaultCategoryView : function(){
        if( this.explorer.defaultView ){
            this.explorer.defaultView.cancelDefaultView();
        }
        this.explorer.category.setDefaultCategoryView( this.data.id, this.data.name );
        this.options.isCategoryDefaultView = true;
        this.explorer.defaultView = this;
        this.setStyle();
    },
    cancelDefaultView : function(){
        this.options.isCategoryDefaultView = false;
        this.setStyle();
    },
    setStyle : function(){
        //if( this.options.isCategoryDefaultView ){
        //    this.setDefaultCategoryViewStyle();
        //}else if( this.options.isCategoryView ){
        //    this.setCategoryViewStyle();
        //}else{
        //    this.setNormalStyle();
        //}
        if( this.options.isCategoryDefaultView  && this.options.isCategoryView ){
            this.itemStatusTopNode.setStyles( { "background-color" : "#3c76b7" });
            this.itemStatusBottomNode.setStyles( { "background-color" : "#50e3c2" });
            this.setDefaultCategoryViewAction.setStyles( this.css.setDefaultCategoryViewAction_selected );
            this.setCategoryViewAction.setStyles( this.css.setCategoryViewAction_selected );
        }else if( this.options.isCategoryView  ){
            this.itemStatusTopNode.setStyles( { "background-color" : "#3c76b7" });
            this.itemStatusBottomNode.setStyles( { "background-color" : "#3c76b7" });
            this.setDefaultCategoryViewAction.setStyles( this.css.setDefaultCategoryViewAction );
            this.setCategoryViewAction.setStyles( this.css.setCategoryViewAction_selected );
        }else if( this.options.isCategoryDefaultView ){
            this.itemStatusTopNode.setStyles( { "background-color" : "#50e3c2" });
            this.itemStatusBottomNode.setStyles( { "background-color" : "#50e3c2" });
            this.setDefaultCategoryViewAction.setStyles( this.css.setDefaultCategoryViewAction_selected );
            this.setCategoryViewAction.setStyles( this.css.setCategoryViewAction );
        }else{
            var bgcolor = this.options.index % 2 == 0 ? "#f7f7f7" : "#fff";
            this.node.setStyle( "background-color" , bgcolor );
            this.itemStatusTopNode.setStyles( { "background-color" : bgcolor });
            this.itemStatusBottomNode.setStyles( { "background-color" : bgcolor });
            this.setDefaultCategoryViewAction.setStyles( this.css.setDefaultCategoryViewAction );
            this.setCategoryViewAction.setStyles( this.css.setCategoryViewAction );
        }
    },
    _open: function(e){
        var _self = this;
        var options = {
            "onQueryLoad": function(){
                this.actions = _self.explorer.actions;
                this.category = _self;
                this.options.id = _self.data.id;
                this.column = _self.explorer.app.options.column;
                this.application = _self.explorer.app.options.column;
                this.options.noModifyName = _self.explorer.options.noModifyName;
                this.options.readMode = _self.explorer.options.readMode,
                    this.options.formId = _self.data.formId;
            }
        };
        this.explorer.app.desktop.openApplication(e, "cms.ViewDesigner", options);
    },
    _getIcon: function(){
        var x = (Math.random()*33).toInt();
        return "process_icon_"+x+".png";
    },
    _getLnkPar: function(){
        return {
            "icon": this.explorer.path+this.explorer.options.style+"/viewIcon/lnk.png",
            "title": this.data.name,
            "par": "cms.ViewDesigner#{\"id\": \""+this.data.id+"\"}"
        };
    },
//	deleteItem: function(e){
//		var _self = this;
//		this.explorer.app.confirm("info", e, this.lp.deleteFormTitle, this.lp.deleteForm, 320, 110, function(){
//			_self.deleteForm();
//			this.close();
//		},function(){
//			this.close();
//		});
//	},
    deleteView: function(callback){
        this.explorer.app.restActions.deleteView(this.data.id, function(){
            this.node.destroy();
            if (callback) callback();
        }.bind(this));
    }
});



MWF.xApplication.cms.ColumnManager.CategoryExplorer.CategoryProperty = new Class({
    initialize: function(app, node, category){
        this.app = app;
        this.node = $(node);
        this.category = category;

        this.controllerData = [];
        this.controllerList = [];
    },
    load: function(){
        this.propertyTitleBar = new Element("div", {
            "styles": this.app.css.propertyTitleBar,
            "text": "分类属性"  //this.data.name || this.data.appName
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
        this.controllerData = [];
        this.controllerList = [];
        if(this.category)this.loadContent();
    },
    loadContent : function(){
        this.publisherContainer = new Element( "div").inject( this.contentAreaNode );
        MWF.xDesktop.requireApp("cms.ColumnManager", "widget.CategoryPublisherSetting", null, false);
        this.publisherSetting = new MWF.xApplication.cms.ColumnManager.CategoryPublisherSetting( this.app,
            this.app.lp.application.publisherSetting, this.publisherContainer, {
                objectId : this.category.data.id,
                objectType : "CATEGORY",
                permission : "PUBLISH"
            }
        );
        this.publisherSetting.load();

        this.permissionContainer = new Element( "div").inject( this.contentAreaNode );
        MWF.xDesktop.requireApp("cms.ColumnManager", "widget.CategoryPermissionSetting", null, false);
        this.permissionSetting = new MWF.xApplication.cms.ColumnManager.CategoryPermissionSetting( this.app,
            this.app.lp.application.availableSetting, this.permissionContainer, {
                objectId : this.category.data.id,
                objectType : "CATEGORY",
                permission : "VIEW"
            }
        );
        this.permissionSetting.load();

        this.listController( function(  ){
            this.createControllerListNode();
        }.bind(this) );
    },
    listController : function( callback ){
        this.app.restActions.listCategoryController(this.category.data.id, function(json){
            json.data = json.data || [];
            this.controllerData = json.data;
            json.data.each(function( d ){
                this.controllerList.push( d.adminName );
            }.bind(this))
            callback.call(  )
        }.bind(this), null ,false)
    },
    setContentHeight: function(){
        var size = this.node.getSize();
        var titleSize = this.propertyTitleBar.getSize();
        var y = size.y-titleSize.y-10;
        this.contentNode.setStyle("height", ""+y+"px");
    },

    createControllerListNode: function(){
        if (!this.personActions) this.personActions = new MWF.xAction.org.express.RestActions();

        this.controllerListTitleNode = new Element("div", {
            "styles": this.app.css.controllerListTitleNode,
            "text": this.app.lp.application.controllerList
        }).inject(this.contentAreaNode);

        this.controllerListContentNode = new Element("div", {"styles": {"overflow": "hidden"}}).inject(this.contentAreaNode);
        this.administratorsContentNode = new Element("div", {"styles": this.app.css.administratorsContentNode}).inject(this.controllerListContentNode);

        var changeAdministrators = new Element("div", {
            "styles": {
                "margin-left": "40px",
                "float": "left",
                "background-color": "#FFF",
                "padding": "4px 14px",
                "border": "1px solid #999",
                "border-radius": "3px",
                "margin-top": "10px",
                "margin-bottom": "20px",
                "font-size": "14px",
                "color": "#666",
                "cursor": "pointer"
            },
            "text": "设置分类管理者"
        }).inject(this.contentAreaNode);
        changeAdministrators.addEvent("click", function(){
            this.changeAdministrators();
        }.bind(this));

        if (this.controllerList){
            var explorer = {
                "actions": this.personActions,
                "app": {
                    "lp": this.app.lp
                }
            }
            this.controllerList.each(function(name){
                if (name) var admin = new MWF.widget.Person({"name": name}, this.administratorsContentNode, explorer, false, null, {"style": "application"});
            }.bind(this));
        }
    },
    changeAdministrators: function(){
        var explorer = {
            "actions": this.personActions,
            "app": {
                "lp": this.app.lp
            }
        };

        var options = {
            "type": "person",
            "title": "设置分类管理者",
            "names": this.controllerList || [],
            "onComplete": function(items){

                this.administratorsContentNode.empty();

                //var controllerList = [];
                //items.each(function(item){
                //    controllerList.push(item.data.name);
                //    var admin = new MWF.widget.Person(item.data, this.administratorsContentNode, explorer, false, null, {"style": "application"});
                //}.bind(this));
                //this.controllerList = controllerList;
                //this.app.restActions.saveApplication(this.data, function(json){
                //
                //}.bind(this));


                var controllerList = [];

                items.each(function(item){
                    controllerList.push(item.data.name);
                    var admin = new MWF.widget.Person(item.data, this.administratorsContentNode, explorer, false, null, {"style": "application"});
                }.bind(this));

                controllerList.each(function(item){
                    if( !this.controllerList.contains( item ) ){
                        var controllerData = {
                            "objectType": "CATEGORY",
                            "objectId": this.data.id,
                            "adminUid": item,
                            "adminName": item,
                            "adminLevel": "ADMIN"
                        }
                        this.app.restActions.saveController(controllerData, function(json){
                            controllerData.id = json.data.id;
                            this.controllerData.push( controllerData );
                        }.bind(this), null, false);
                    }
                }.bind(this))

                this.controllerList.each(function(item){
                    if( !controllerList.contains( item ) ){
                        var ad = null;
                        var id = "";
                        this.controllerData.each(function(data){
                            if( data.adminName == item ){
                                ad = data;
                                id = data.id;
                            }
                        }.bind(this));
                        this.app.restActions.removeController(id, function(json){
                            this.controllerData.erase( ad )
                        }.bind(this), null, false);
                    }
                }.bind(this))

                this.controllerList = controllerList;
                this.app.notice(  MWF.CMSCM.LP.setControllerSuccess  , "success");
            }.bind(this)
        };

        var selector = new MWF.OrgSelector(this.app.content, options);
    }
})


