MWF.xApplication.Forum.Navi = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],
    options : {
        "style" : "default",
        "id" :"" ,
        "type" : "all"
    },
    initialize: function(app, container, options){
        this.setOptions(options);
        this.path = "/x_component_Forum/$Navi/";
        this.cssPath = "/x_component_Forum/$Navi/"+this.options.style+"/css.wcss";
        this._loadCss();
        this.app = app;
        this.container = $(container);
    },
    load: function(){
        var self = this;
        this.node = new Element("div.naviNode", {
            "styles": this.css.naviNode
        }).inject(this.container);
        this.allItem = new MWF.xApplication.Forum.AllItem( this, this.node  );
        this.recommandItem = new MWF.xApplication.Forum.RecommandItem( this, this.node  );

        this.categoryItemList = [];
        MWF.Actions.get("x_bbs_assemble_control").listCategoryAll( function( json ) {
            (json.data || []).each(function (d) {
                var categoryItem = new MWF.xApplication.Forum.CategoryItem(this, this.node,d );
                this.categoryItemList.push( categoryItem );
                this.fireEvent("postLoad");
            }.bind(this))
        }.bind(this))
    }
});


MWF.xApplication.Forum.CategoryItem = new Class({
    initialize: function ( navi, container, data ) {
        this.type = "category";
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

        if( this.navi.options.id == this.data.id && this.navi.options.type == "category" ){
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
            "mouseover": function(){ if ( !_self.isCurrent )this.setStyles(_self.css.categoryNode_over) },
            "mouseout": function(){ if ( !_self.isCurrent )this.setStyles( _self.css.categoryNode ) },
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
    setExpendNodeStyle : function(){
        var style;
        if( this.hasSub ){
            if( this.isExpended ){
                if( this.isCurrent ){
                    style = this.css.categoryExpendNode_selected;
                }else{
                    style = this.css.categoryExpendNode;
                }
            }else{
                if( this.isCurrent ){
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
        if(d.forumStatus != "停用" ){
            MWF.Actions.get("x_bbs_assemble_control").listSection(d.id , function ( json ) {
                (json.data || []).each( function( sectiondata ){
                    var sectionItem = new MWF.xApplication.Forum.SectionItem(this.navi, this, this.listNode, sectiondata );
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
    }
});

MWF.xApplication.Forum.SectionItem = new Class({
    initialize: function ( navi, category, container, data) {
        this.type = "section";
        this.navi = navi;
        this.category = category;
        this.app = navi.app;
        this.data = data;
        this.container = $(container);
        this.css = navi.css;
        this.load();
    },
    load: function(){
        var _self = this;
        this.isCurrent = false;

        if( this.navi.options.type == "section" && this.navi.options.id == this.data.id ){
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

        this.loadView();
    },
    cancelCurrent : function(){
        this.isCurrent = false;
        this.node.setStyles( this.css.sectionNode );
    },
    getCategoryId : function(){
        return this.category.data.id;
    },
    loadView : function( searchKey ){
        this.app.openView( this, this.category.data, this.data, searchKey || "", this );
    }
});


MWF.xApplication.Forum.AllItem = new Class({
    initialize: function ( navi, container) {
        this.type = "all";
        this.navi = navi;
        this.app = navi.app;
        this.container = $(container);
        this.css = navi.css;
        this.load();
    },
    load: function(){
        var _self = this;
        this.isCurrent = false;

        if( this.navi.options.type == "all" ){
            this.isCurrent = true;
        }

        var _self = this;
        this.node = new Element("div.allNode", {
            "styles": this.css.allNode,
            "text" : "全部帖子"
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

        this.loadView();
    },
    cancelCurrent : function(){
        this.isCurrent = false;
        this.node.setStyles( this.css.allNode );
    },
    getCategoryId : function(){
        return null;
    },
    loadView : function( searchKey ){
        this.app.openView( this, null, this.data, searchKey || "", this );
    }
});

MWF.xApplication.Forum.RecommandItem = new Class({
    initialize: function ( navi, container) {

        this.type = "recommand";
        this.navi = navi;
        this.app = navi.app;
        this.container = $(container);
        this.css = navi.css;
        this.load();
    },
    load: function(){
        var _self = this;
        this.isCurrent = false;

        if( this.navi.options.type == "recommand" ){
            this.isCurrent = true;
        }

        var _self = this;
        this.node = new Element("div.recommandNode", {
            "styles": this.css.recommandNode,
            "text" : "推荐帖子"
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

        this.loadView();
    },
    cancelCurrent : function(){
        this.isCurrent = false;
        this.node.setStyles( this.css.recommandNode );
    },
    getCategoryId : function(){
        return null;
    },
    loadView : function( searchKey ){
        this.app.openView( this, null, this.data, searchKey || "", this );
    }
});



