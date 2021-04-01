window.MWFForum = window.MWFForum || MWF.xApplication.Forum;
MWFForum.NaviType = {
    main : "main",
    all : "all",
    recommand : "recommand",
    cream : "cream",
    category : "category",
    section : "section"
};
MWF.xApplication.Forum.NaviMode = MWFForum.NaviMode = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],
    options : {
        "style" : "default",
        "id" :"" ,
        "type" : MWFForum.NaviType.all
    },
    initialize: function(app, container, options){
        this.setOptions(options);
        this.path = "../x_component_Forum/$NaviMode/";
        this.cssPath = "../x_component_Forum/$NaviMode/"+this.options.style+"/css.wcss";
        this._loadCss();
        this.app = app;
        this.container = $(container);
    },
    load: function(){
        var self = this;
        //this.container.setStyles(this.css.contentNode);

        this.naviContainer = new Element("div.naviContainer", {
            "styles": this.css.naviContainer
        }).inject(this.container);

        this.naviNode = new Element("div.naviNode", {
            "styles": this.css.naviNode
        }).inject(this.naviContainer);

        this.viewContainer = new Element("div.viewContainer", {
            "styles": this.css.viewContainer
        }).inject(this.container);

        this.allItem = new MWFForum.NaviMode.AllItem( this, this.naviNode  );
        this.recommandItem = new MWFForum.NaviMode.RecommandItem( this, this.naviNode  );
        this.creamItem = new MWFForum.NaviMode.CreamItem( this, this.naviNode  );

        this.categoryItemList = [];
        this.categoryItemMap = {};
        this.sectionItemMap = {};
        MWF.Actions.get("x_bbs_assemble_control").listCategoryAll( function( json ) {
            (json.data || []).each(function (d) {
                var categoryItem = new MWFForum.NaviMode.CategoryItem(this, this.naviNode,d );
                this.categoryItemList.push( categoryItem );
                this.categoryItemMap[ d.id ] = categoryItem;
                this.fireEvent("postLoad");
            }.bind(this))
        }.bind(this));

        this.setNodeSizeFun = function () {
            this.setNodeSize();
        }.bind(this);
        this.app.addEvent("resize", this.setNodeSizeFun);
        this.setNodeSize();
    },
    close : function(){
        this.back();
    },
    setTitle : function( title ){
        var tail = this.app.inBrowser ? (MWFForum.getSystemConfigValue( MWFForum.BBS_TITLE_TAIL ) || "") : "";
        this.app.setTitle( title + tail );
    },
    back : function( type ){
        var item = this.currentItem;
        if( !type ){
            type = item ? item.type : "main";
        }
        if(this.view)this.view.destroy();
        this.app.clearContent();
        if(this.app.node)this.app.node.destroy();
        switch ( type ){
            case MWFForum.NaviType.category :
                MWF.xDesktop.requireApp("ForumCategory", "MainInContainer", null, false);
                var forumCategory = new MWF.xApplication.ForumCategory.MainInContainer( this.app.desktop, {
                    "hasTop" : true,
                    "hasBreadCrumb" : true,
                    "categoryId" : item.data.id,
                    "naviMode" : false,
                    "autoWidth" : false
                }, this.app.content, this.app.content , this.app.content );
                forumCategory.inBrowser = this.app.inBrowser;
                forumCategory.window = this.app.window;
                forumCategory.taskitem = this.app.taskitem;
                forumCategory.load();
                this.setTitle( item.data.forumName );
                this.destroy();
                break;
            case MWFForum.NaviType.section :
                MWF.xDesktop.requireApp("ForumSection", "MainInContainer", null, false);
                var forumSection = new MWF.xApplication.ForumSection.MainInContainer( this.app.desktop, {
                    "hasTop" : true,
                    "hasBreadCrumb" : true,
                    "sectionId" : item.data.id,
                    "naviMode" : false,
                    "autoWidth" : false
                }, this.app.content, this.app.content , this.app.content );
                forumSection.inBrowser = this.app.inBrowser;
                forumSection.window = this.app.window;
                forumSection.taskitem = this.app.taskitem;
                forumSection.load();
                this.setTitle( item.data.sectionName );
                this.destroy();
                break;
            default :
                MWF.xDesktop.requireApp("Forum", "MainInContainer", null, false);
                var forum = new MWF.xApplication.Forum.MainInContainer( this.app.desktop, {
                    "hasTop" : true,
                    "hasBreadCrumb" : true,
                    "naviMode" : false,
                    "autoWidth" : false
                }, this.app.content, this.app.content , this.app.content );
                forum.inBrowser = this.app.inBrowser;
                forum.window = this.app.window;
                forum.taskitem = this.app.taskitem;
                forum.load();
                this.setTitle( MWFForum.getBBSName() || MWF.xApplication.Forum.LP.title );
                this.destroy();
                break
        }
    },
    goto : function( type, id ){
        switch ( type ) {
            case MWFForum.NaviType.main :
                this.back( MWFForum.NaviType.main );
                break;
            case MWFForum.NaviType.all :
                this.allItem.setCurrent();
                break;
            case MWFForum.NaviType.recommand :
                this.recommandItem.setCurrent();
                break;
            case MWFForum.NaviType.cream :
                this.creamItem.setCurrent();
                break;
            case MWFForum.NaviType.category :
                var category = this.categoryItemMap[id];
                if(category)category.setCurrent();
                break;
            case MWFForum.NaviType.section :
                var section = this.sectionItemMap[id];
                if(section)section.setCurrent();
                break;
            default :
                break;
        }
    },
    scrollToTop : function(){
        this.app.contentContainerNode.scrollTo(0, 0);
    },
    setNodeSize : function(){
        var appSize = this.app.node.getSize();
        var topSize = this.app.topObject ? this.app.topObject.topContainerNode.getSize() : {x:0, y:0};

        var pt = this.app.contentContainerNode.getStyle("padding-top").toFloat();
        var pb = this.app.contentContainerNode.getStyle("padding-bottom").toFloat();
        var height = appSize.y - pt - pb;

        this.app.contentContainerNode.setStyle("height", "" + height + "px");

        var initY = appSize.y - topSize.y - 20;

        var naviSize = this.naviNode.getSize();
        var viewNode = this.viewContainer.getFirst();
        var viewSize = viewNode ? viewNode.getSize() : {x:0, y:0} ;

        //alert( "initY=" + initY +" , naviSize =" + naviSize.y + ", viewSize =" + viewSize.y );

        this.naviContainer.setStyle( "min-height" , Math.max( initY, naviSize.y, viewSize.y ) );
        this.viewContainer.setStyle( "min-height" , Math.max( initY, naviSize.y, viewSize.y ) );
    },
    destroy : function(){
        if(this.setNodeSizeFun)this.app.removeEvent("resize", this.setNodeSizeFun);
        if(this.naviNode)this.naviNode.destroy();
    }
});

MWFForum.NaviMode.CategoryItem = new Class({
    initialize: function ( navi, container, data ) {
        this.type = MWFForum.NaviType.category;
        this.navi = navi;
        this.app = navi.app;
        this.data = data;
        this.container = $(container);
        this.css = navi.css;
        this.load();
    },
    load: function () {
        var _self = this;

        this.isCurrent = false;
        this.isExpended = true;
        this.hasSub = true;
        this.sectionItemList = [];

        if( this.navi.options.id == this.data.id && this.navi.options.type == MWFForum.NaviType.category ){
            this.isCurrent = true;
        }

        this.node = new Element("div.categoryNode", {
            "styles": this.css.categoryNode
        }).inject(this.container);

        this.expendNode = new Element("div.categoryExpendNode").inject(this.node);
        this.setExpendNodeStyle();
        if( this.hasSub ){
            this.expendNode.addEvent( "click" , function(ev){
                this.triggerExpend();
                ev.stopPropagation();
            }.bind(this));
        }

        this.textNode = new Element("div.categoryTextNode",{
            "styles": this.css.categoryTextNode,
            "text": this.data.forumName
        }).inject(this.node);

        this.node.addEvents({
            "mouseover": function(){
                if ( !_self.isCurrent ){
                    this.setStyles(_self.css.categoryNode_over);
                    _self.setExpendNodeStyle( true );
                }
            },
            "mouseout": function(){
                if ( !_self.isCurrent ){
                    this.setStyles( _self.css.categoryNode );
                    _self.setExpendNodeStyle();
                }
            },
            click : function(){ _self.setCurrent(this);}
        });

        this.listNode = new Element("div.sectionListNode",{
            "styles" : this.css.sectionListNode
        }).inject(this.container);

        this.loadListContent();
        if( this.isCurrent ){
            this.setCurrent();
        }
    },
    setExpendNodeStyle : function( isOver ){
        var style;
        if( this.hasSub ){
            if( this.isExpended ){
                if( isOver ){
                    style = this.css.categoryExpendNode_over;
                }else if( this.isCurrent ){
                    style = this.css.categoryExpendNode_selected;
                }else{
                    style = this.css.categoryExpendNode;
                }
            }else{
                if( isOver ){
                    style = this.css.categoryCollapseNode_over;
                }else if( this.isCurrent ){
                    style = this.css.categoryCollapseNode_selected;
                }else{
                    style = this.css.categoryCollapseNode;
                }
            }
        }else{
            style = this.css.emptyExpendNode;
        }
        this.expendNode.setStyles( style );
    },
    triggerExpend : function(){
        if( this.hasSub ){
            if( this.isExpended ){
                this.isExpended = false;
                this.listNode.setStyle("display","none")
            }else{
                this.isExpended = true;
                this.listNode.setStyle("display","")
            }
            this.setExpendNodeStyle();
        }
    },
    setCurrent : function(){
        if( this.navi.currentItem ){
            this.navi.currentItem.cancelCurrent();
        }

        this.node.setStyles( this.css.categoryNode_selected );

        if( this.hasSub ){
            if( this.isExpended ){
                this.expendNode.setStyles( this.css.categoryExpendNode_selected );
            }else{
                this.expendNode.setStyles( this.css.categoryCollapseNode_selected );
            }
        }

        this.isCurrent = true;
        this.navi.currentItem = this;
        this.navi.setTitle( this.data.forumName );

        this.loadView();
    },
    cancelCurrent : function(){
        this.isCurrent = false;
        this.node.setStyles( this.css.categoryNode );
        if( this.hasSub ){
            if( this.isExpended ){
                this.expendNode.setStyles( this.css.categoryExpendNode );
            }else{
                this.expendNode.setStyles( this.css.categoryCollapseNode );
            }
        }
    },
    loadView: function( searchkey ){
        this.app.openView( this, this.data, this.viewData || this.defaultRevealData, searchkey || "", this );
    },
    loadListContent : function(){
        var d = this.data;
        if(d.forumStatus != MWF.xApplication.Forum.LP.disable ){
            MWF.Actions.get("x_bbs_assemble_control").listSection(d.id , function ( json ) {
                (json.data || []).each( function( sectiondata ){
                    var sectionItem = new MWFForum.NaviMode.SectionItem(this.navi, this, this.listNode, sectiondata );
                    this.sectionItemList.push( sectionItem );
                }.bind(this))
            }.bind(this));
        }

        new Element("div", {
            "styles": this.css.categorySepartorNode
        }).inject( this.listNode );
    },
    getCategoryId : function(){
        return this.data.id;
    },
    loadView : function(){
        if(this.navi.view)this.navi.view.destroy();
        this.navi.viewContainer.empty();
        MWF.xDesktop.requireApp("ForumCategory", "MainInContainer", null, false);
        var container = this.navi.viewContainer;
        var scrollNode = this.app.content;
        var forumCategory = this.navi.view = new MWF.xApplication.ForumCategory.MainInContainer( this.app.desktop, {
            "hasTop" : false,
            "hasBreadCrumb" : true,
            "categoryId" : this.data.id,
            "naviMode" : true,
            "autoWidth" : true,
            "onPostLoadCategory" : function(){
                this.navi.setNodeSize();
                this.navi.scrollToTop();
            }.bind(this)
        }, container, this.app.content , scrollNode );
        forumCategory.forumNavi = this.navi;
        forumCategory.inBrowser = this.navi.app.inBrowser;
        forumCategory.window = this.app.window;
        forumCategory.taskitem = this.navi.app.taskitem;
        forumCategory.load();
    }
});

MWFForum.NaviMode.SectionItem = new Class({
    initialize: function ( navi, category, container, data) {
        this.type = MWFForum.NaviType.section;
        this.navi = navi;
        this.category = category;
        this.app = navi.app;
        this.data = data;
        this.container = $(container);
        this.css = navi.css;
        this.load();
    },
    load: function(){
        this.navi.sectionItemMap[ this.data.id ] = this;

        var _self = this;
        this.isCurrent = false;

        if( this.navi.options.type == MWFForum.NaviType.section && this.navi.options.id == this.data.id ){
            this.isCurrent = true;
        }

        var _self = this;
        this.node = new Element("div.sectionNode", {
            "styles": this.css.sectionNode,
            "text" : this.data.sectionName
        }).inject(this.container);

        this.node.addEvents({
            "mouseover": function(){ if (!_self.isCurrent)this.setStyles(_self.css.sectionNode_over) },
            "mouseout": function(){ if (!_self.isCurrent)this.setStyles( _self.css.sectionNode ) },
            "click": function (el) {
                _self.setCurrent();
            }
        });

        if( this.isCurrent ){
            this.setCurrent()
        }
    },
    setCurrent : function(){
        if( this.navi.currentItem ){
            this.navi.currentItem.cancelCurrent();
        }

        this.node.setStyles( this.css.sectionNode_selected );

        this.isCurrent = true;
        this.navi.currentItem = this;
        this.navi.setTitle( this.data.sectionName );

        this.loadView();
    },
    cancelCurrent : function(){
        this.isCurrent = false;
        this.node.setStyles( this.css.sectionNode );
    },
    getCategoryId : function(){
        return this.category.data.id;
    },
    loadView : function(){
        if(this.navi.view)this.navi.view.destroy();
        this.navi.viewContainer.empty();
        MWF.xDesktop.requireApp("ForumSection", "MainInContainer", null, false);
        //MWF.xApplication.Forumsection.MainInContainer2 = new Class({
        //    Extends: MWF.xApplication.ForumSection.MainInContainer,
        //    loadLayout: function(){
        //        this.contentNode = new Element("div", {"styles": this.css.contentNode}).inject(this.node);
        //        this.toWeek();
        //    }
        //});
        var container = this.navi.viewContainer;
        var scrollNode = this.app.content;
        var forumSection = this.navi.view = new MWF.xApplication.ForumSection.MainInContainer( this.app.desktop, {
            "hasTop" : false,
            "hasBreadCrumb" : true,
            "sectionId" : this.data.id,
            "naviMode" : true,
            "autoWidth" : true,
            "onPostCreateViewBody" : function(){
                this.navi.setNodeSize();
                this.navi.scrollToTop();
            }.bind(this)
        }, container, this.app.content , scrollNode );
        forumSection.forumNavi = this.navi;
        forumSection.inBrowser = this.navi.app.inBrowser;
        forumSection.window = this.navi.app.window;
        forumSection.taskitem = this.navi.app.taskitem;
        forumSection.load();
    }
});

MWFForum.NaviMode.AllItem = new Class({
    initialize: function ( navi, container) {
        this.type = MWFForum.NaviType.all;
        this.navi = navi;
        this.app = navi.app;
        this.container = $(container);
        this.css = navi.css;
        this.load();
    },
    load: function(){
        var _self = this;
        this.isCurrent = false;

        if( this.navi.options.type == MWFForum.NaviType.all ){
            this.isCurrent = true;
        }

        var _self = this;
        this.node = new Element("div.allNode", {
            "styles": this.css.allNode,
            "text" : MWF.xApplication.Forum.LP.all1
        }).inject(this.container);

        this.node.addEvents({
            "mouseover": function(){ if (!_self.isCurrent)this.setStyles(_self.css.allNode_over) },
            "mouseout": function(){ if (!_self.isCurrent)this.setStyles( _self.css.allNode ) },
            "click": function (el) {
                _self.setCurrent();
            }
        });

        if( this.isCurrent ){
            this.setCurrent()
        }
    },
    setCurrent : function(){

        if( this.navi.currentItem ){
            this.navi.currentItem.cancelCurrent();
        }

        this.node.setStyles( this.css.allNode_selected );

        this.isCurrent = true;
        this.navi.currentItem = this;
        this.navi.setTitle( MWF.xApplication.Forum.LP.allSubject );

        this.loadView();
    },
    cancelCurrent : function(){
        this.isCurrent = false;
        this.node.setStyles( this.css.allNode );
    },
    getCategoryId : function(){
        return null;
    },
    loadView : function( ){
        if(this.navi.view)this.navi.view.destroy();
        this.navi.viewContainer.empty();

        this.viewWarp = new Element("div").inject( this.navi.viewContainer );

        var topNode = this.topNode = new Element("div.topNode", {
            "styles": this.css.topNode
        }).inject(this.viewWarp );

        var topTitleMiddleNode = new Element("div.topTitleMiddleNode", {
            "styles": this.css.topTitleMiddleNode
        }).inject(topNode);

        var topItemTitleNode = new Element("div.topItemTitleNode", {
            "styles": this.css.topItemTitleNode,
            "text": MWFForum.getBBSName() || MWF.xApplication.Forum.LP.title
        }).inject(topTitleMiddleNode);

        var topItemSepNode = new Element("div.topItemSepNode", {
            "styles": this.css.topItemSepNode,
            "text" : ">"
        }).inject(topTitleMiddleNode);

        topItemTitleNode.addEvent("click", function(){
            this.navi.goto( MWFForum.NaviType.main )
        }.bind(this));

        var topItemTitleNode = new Element("div.topItemTitleNode", {
            "styles": this.css.topItemTitleLastNode,
            "text": MWF.xApplication.Forum.LP.allSubject
        }).inject(topTitleMiddleNode);

        var view = this.navi.view = new MWFForum.NaviMode.AllView( this.viewWarp, this.app, this, {
            templateUrl : this.navi.path + this.navi.options.style + "/"+"listItem.json",
            pagingEnable : true,
            pagingPar : {
                hasReturn : false,
                currentPage : this.navi.options.viewPageNum,
                countPerPage : 30
            },
            onPostCreateViewBody : function(){
                this.navi.setNodeSize();
                this.navi.scrollToTop();
            }.bind(this)
        } );
        view.pagingContainerTop = this.pagingBarTop;
        view.pagingContainerBottom = this.pagingBarBottom;
        view.load();
    }
});

MWFForum.NaviMode.RecommandItem = new Class({
    initialize: function ( navi, container) {

        this.type = MWFForum.NaviType.recommand;
        this.navi = navi;
        this.app = navi.app;
        this.container = $(container);
        this.css = navi.css;
        this.load();
    },
    load: function(){
        var _self = this;
        this.isCurrent = false;

        if( this.navi.options.type == MWFForum.NaviType.recommand ){
            this.isCurrent = true;
        }

        var _self = this;
        this.node = new Element("div.recommandNode", {
            "styles": this.css.recommandNode,
            "text" : MWF.xApplication.Forum.LP.recommanded
        }).inject(this.container);

        this.node.addEvents({
            "mouseover": function(){ if (!_self.isCurrent)this.setStyles(_self.css.recommandNode_over) },
            "mouseout": function(){ if (!_self.isCurrent)this.setStyles( _self.css.recommandNode ) },
            "click": function (el) {
                _self.setCurrent();
            }
        });

        if( this.isCurrent ){
            this.setCurrent()
        }
    },
    setCurrent : function(){

        if( this.navi.currentItem ){
            this.navi.currentItem.cancelCurrent();
        }

        this.node.setStyles( this.css.recommandNode_selected );

        this.isCurrent = true;
        this.navi.currentItem = this;
        this.navi.setTitle( MWF.xApplication.Forum.LP.recommandedSubject );

        this.loadView();
    },
    cancelCurrent : function(){
        this.isCurrent = false;
        this.node.setStyles( this.css.recommandNode );
    },
    getCategoryId : function(){
        return null;
    },
    loadView : function( ){
        if(this.navi.view)this.navi.view.destroy();
        this.navi.viewContainer.empty();

        this.viewWarp = new Element("div").inject( this.navi.viewContainer );

        var topNode = this.topNode = new Element("div.topNode", {
            "styles": this.css.topNode
        }).inject(this.viewWarp );

        var topTitleMiddleNode = new Element("div.topTitleMiddleNode", {
            "styles": this.css.topTitleMiddleNode
        }).inject(topNode);

        var topItemTitleNode = new Element("div.topItemTitleNode", {
            "styles": this.css.topItemTitleNode,
            "text": MWFForum.getBBSName() || MWF.xApplication.Forum.LP.title
        }).inject(topTitleMiddleNode);

        var topItemSepNode = new Element("div.topItemSepNode", {
            "styles": this.css.topItemSepNode,
            "text" : ">"
        }).inject(topTitleMiddleNode);

        topItemTitleNode.addEvent("click", function(){
            this.navi.goto( MWFForum.NaviType.main )
        }.bind(this));

        var topItemTitleNode = new Element("div.topItemTitleNode", {
            "styles": this.css.topItemTitleLastNode,
            "text": MWF.xApplication.Forum.LP.recommandedSubject
        }).inject(topTitleMiddleNode);


        var view = this.navi.view = new MWFForum.NaviMode.RecommandView( this.viewWarp, this.app, this, {
            templateUrl : this.navi.path + this.navi.options.style + "/"+"listItem.json",
            pagingEnable : true,
            pagingPar : {
                hasReturn : false,
                currentPage : this.navi.options.viewPageNum,
                countPerPage : 30
            },
            onPostCreateViewBody : function(){
                this.navi.setNodeSize();
                this.navi.scrollToTop();
            }.bind(this)
        } );
        view.pagingContainerTop = this.pagingBarTop;
        view.pagingContainerBottom = this.pagingBarBottom;
        view.load();
    }
});

MWFForum.NaviMode.CreamItem = new Class({
    initialize: function ( navi, container) {

        this.type = MWFForum.NaviType.cream;
        this.navi = navi;
        this.app = navi.app;
        this.container = $(container);
        this.css = navi.css;
        this.load();
    },
    load: function(){
        var _self = this;
        this.isCurrent = false;

        if( this.navi.options.type == MWFForum.NaviType.cream ){
            this.isCurrent = true;
        }

        var _self = this;
        this.node = new Element("div.cream", {
            "styles": this.css.recommandNode,
            "text" : MWF.xApplication.Forum.LP.prime
        }).inject(this.container);

        this.node.addEvents({
            "mouseover": function(){ if (!_self.isCurrent)this.setStyles(_self.css.recommandNode_over) },
            "mouseout": function(){ if (!_self.isCurrent)this.setStyles( _self.css.recommandNode ) },
            "click": function (el) {
                _self.setCurrent();
            }
        });

        if( this.isCurrent ){
            this.setCurrent()
        }
    },
    setCurrent : function(){

        if( this.navi.currentItem ){
            this.navi.currentItem.cancelCurrent();
        }

        this.node.setStyles( this.css.recommandNode_selected );

        this.isCurrent = true;
        this.navi.currentItem = this;
        this.navi.setTitle( MWF.xApplication.Forum.LP.primeSubject );

        this.loadView();
    },
    cancelCurrent : function(){
        this.isCurrent = false;
        this.node.setStyles( this.css.recommandNode );
    },
    getCategoryId : function(){
        return null;
    },
    loadView : function( ){
        if(this.navi.view)this.navi.view.destroy();
        this.navi.viewContainer.empty();

        this.viewWarp = new Element("div").inject( this.navi.viewContainer );

        var topNode = this.topNode = new Element("div.topNode", {
            "styles": this.css.topNode
        }).inject(this.viewWarp );

        var topTitleMiddleNode = new Element("div.topTitleMiddleNode", {
            "styles": this.css.topTitleMiddleNode
        }).inject(topNode);

        var topItemTitleNode = new Element("div.topItemTitleNode", {
            "styles": this.css.topItemTitleNode,
            "text": MWFForum.getBBSName() || MWF.xApplication.Forum.LP.title
        }).inject(topTitleMiddleNode);

        var topItemSepNode = new Element("div.topItemSepNode", {
            "styles": this.css.topItemSepNode,
            "text" : ">"
        }).inject(topTitleMiddleNode);

        topItemTitleNode.addEvent("click", function(){
            this.navi.goto( MWFForum.NaviType.main )
        }.bind(this));

        var topItemTitleNode = new Element("div.topItemTitleNode", {
            "styles": this.css.topItemTitleLastNode,
            "text": MWF.xApplication.Forum.LP.primeSubject
        }).inject(topTitleMiddleNode);

        var view = this.navi.view = new MWFForum.NaviMode.CreamView( this.viewWarp, this.app, this, {
            templateUrl : this.navi.path + this.navi.options.style + "/"+"listItem.json",
            pagingEnable : true,
            pagingPar : {
                hasReturn : false,
                currentPage : this.navi.options.viewPageNum,
                countPerPage : 30
            },
            onPostCreateViewBody : function(){
                this.navi.setNodeSize();
                this.navi.scrollToTop();
            }.bind(this)
        } );
        view.pagingContainerTop = this.pagingBarTop;
        view.pagingContainerBottom = this.pagingBarBottom;
        view.load();
    }
});


MWFForum.NaviMode.AllView = new Class({
    Extends: MWF.xApplication.Template.Explorer.ComplexView,
    _createDocument: function(data, index){
        return new MWFForum.NaviMode.Document(this.viewNode, data, this.explorer, this, null,  index);
    },
    _getCurrentPageData: function(callback, count, pageNum){
        this.clearBody();
        if(!count)count=30;
        if(!pageNum)pageNum = 1;
        var filter = this.filterData || {};
            //{"withTopSubject":true};
        this.actions.listSubjectFilterPage( pageNum, count, filter, function(json){
            if( !json.data )json.data = [];
            if( !json.count )json.count=0;
            if( callback )callback(json);
        }.bind(this))
    },
    _removeDocument: function(documentData, all){
        this.actions.deleteSubject(documentData.id, function(json){
            this.reload();
            this.app.reloadAllParents( documentData.sectionId );
            this.app.notice(this.app.lp.deleteDocumentOK, "success");
        }.bind(this));
    },
    _create: function(){

    },
    _openDocument: function( documentData,index ){
        var appId = "ForumDocument"+documentData.id;
        if (this.app.desktop.apps[appId]){
            this.app.desktop.apps[appId].setCurrent();
        }else {
            this.app.desktop.openApplication(null, "ForumDocument", {
                "sectionId" : documentData.sectionId,
                "id" : documentData.id,
                "appId": appId,
                "isEdited" : false,
                "isNew" : false,
                "index" : index
            });
        }
    },
    _queryCreateViewNode: function(){
    },
    _postCreateViewNode: function( viewNode ){
    },
    _queryCreateViewHead:function(){
    },
    _postCreateViewHead: function( headNode ){
    },
    isAdmin : function(){
        return false;
    }

});

MWFForum.NaviMode.RecommandView = new Class({
    Extends: MWF.xApplication.Template.Explorer.ComplexView,
    _createDocument: function(data, index){
        return new MWFForum.NaviMode.Document(this.viewNode, data, this.explorer, this, null,  index);
    },
    _getCurrentPageData: function(callback, count, pageNum){
        this.clearBody();
        if(!count)count=30;
        if(!pageNum)pageNum = 1;
        var filter = this.filterData ||
            {"subjectId":"","voteOptionId":"","forumId":"","mainSectionId":"","sectionId":"","searchContent":"","creatorName":"","needPicture":"","withTopSubject":true};
        this.actions.listRecommendedPage( pageNum, count, filter, function(json){
            if( !json.data )json.data = [];
            if( !json.count )json.count=0;
            if( callback )callback(json);
        }.bind(this))
    },
    _removeDocument: function(documentData, all){
        this.actions.deleteSubject(documentData.id, function(json){
            this.reload();
            this.app.reloadAllParents( documentData.sectionId );
            this.app.notice(this.app.lp.deleteDocumentOK, "success");
        }.bind(this));
    },
    _create: function(){

    },
    _openDocument: function( documentData,index ){
        var appId = "ForumDocument"+documentData.id;
        if (this.app.desktop.apps[appId]){
            this.app.desktop.apps[appId].setCurrent();
        }else {
            this.app.desktop.openApplication(null, "ForumDocument", {
                "sectionId" : documentData.sectionId,
                "id" : documentData.id,
                "appId": appId,
                "isEdited" : false,
                "isNew" : false,
                "index" : index
            });
        }
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

MWFForum.NaviMode.CreamView = new Class({
    Extends: MWF.xApplication.Template.Explorer.ComplexView,
    _createDocument: function(data, index){
        return new MWFForum.NaviMode.Document(this.viewNode, data, this.explorer, this, null,  index);
    },
    _getCurrentPageData: function(callback, count, pageNum){
        this.clearBody();
        if(!count)count=30;
        if(!pageNum)pageNum = 1;
        var filter = this.filterData || {};
        this.actions.listCreamSubjectFilterPage( pageNum, count, filter, function(json){
            if( !json.data )json.data = [];
            if( !json.count )json.count=0;
            if( callback )callback(json);
        }.bind(this))
    },
    _removeDocument: function(documentData, all){
        this.actions.deleteSubject(documentData.id, function(json){
            this.reload();
            this.app.reloadAllParents( documentData.sectionId );
            this.app.notice(this.app.lp.deleteDocumentOK, "success");
        }.bind(this));
    },
    _create: function(){

    },
    _openDocument: function( documentData,index ){
        var appId = "ForumDocument"+documentData.id;
        if (this.app.desktop.apps[appId]){
            this.app.desktop.apps[appId].setCurrent();
        }else {
            this.app.desktop.openApplication(null, "ForumDocument", {
                "sectionId" : documentData.sectionId,
                "id" : documentData.id,
                "appId": appId,
                "isEdited" : false,
                "isNew" : false,
                "index" : index
            });
        }
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

MWFForum.NaviMode.Document = new Class({
    Extends: MWF.xApplication.Template.Explorer.ComplexDocument,
    _queryCreateDocumentNode:function( itemData ){
    },
    _postCreateDocumentNode: function( itemNode, itemData ){
    },
    open: function (e) {
        this.view._openDocument(this.data, this.index);
    },
    edit : function(){
        var appId = "ForumDocument"+this.data.id;
        if (this.app.desktop.apps[appId]){
            this.app.desktop.apps[appId].setCurrent();
        }else {
            this.app.desktop.openApplication(null, "ForumDocument", {
                "sectionId" : this.data.sectionId,
                "id" : this.data.id,
                "appId": appId,
                "isEdited" : true,
                "isNew" : false,
                "index" : this.index
            });
        }
    },
    openSection : function( ev ){
        var data = this.data;
        if( this.view.explorer && this.view.explorer.navi ){
            this.view.explorer.navi.goto( MWFForum.NaviType.section, data.sectionId );
        }else{
            var appId = "ForumSection"+ data.sectionId;
            if (this.app.desktop.apps[appId]){
                this.app.desktop.apps[appId].setCurrent();
            }else {
                this.app.desktop.openApplication(ev, "ForumSection", {
                    "sectionId": data.sectionId,
                    "appId": appId
                });
            }
        }
        ev.stopPropagation();
    },
    isAdmin: function(){
        return this.app.access.isAdmin();
    }
});
