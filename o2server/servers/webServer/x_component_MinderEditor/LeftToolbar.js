MWF.xDesktop.requireApp("MinderEditor", "lp."+MWF.language, null, false);
MWF.xDesktop.requireApp("MinderEditor", "Commands", null, false);

MWF.xApplication.MinderEditor.LeftToolbar = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],
    options: {
        "style": "default"
    },
    initialize: function (container, editor, minder, app ) {
        this.container = container;
        this.app = app;
        this.lp = MWF.xApplication.MinderEditor.LP;
        this.actions = this.app.restActions;
        this.editor = editor;
        this.minder = minder;
        if( this.editor.commands ){
            this.commands = this.editor.commands;
        }else{
            this.commands = new MWF.xApplication.MinderEditor.Commands( this.editor );
            this.commands.load();
        }

        this.path = "/x_component_MinderEditor/$LeftToolbar/";
        this.cssPath = this.path+this.options.style+"/css.wcss";
        this._loadCss();
    },
    load: function (callback) {
        this.creat();
        //this.navMoveNode.click();
    },
    destroy: function(){
        this.node.destroy();
        delete  this;
    },
    getHtml : function(){
        var items;
        var tools = this.editor.options.tools;
        if( tools && tools.left ){
            items = tools.left;
        }else{
           items = [ "zoom", "camera", "resetlayout", "move", "expandLevel", "selectAll", "preview", "template", "theme", "search" ];
        }
        var html = "";
        var disableTools = this.editor.options.disableTools || [];
        disableTools.each( function( tool ){
            items.erase( tool )
        });
        this.itemCount = 0;
       items.each( function( item ){
           if( item != "zoom" )this.itemCount++;
            switch( item ){
                case "zoom":
                    this.hasZoom = true;
                    html +=  "<div item='zoom'></div>";
                    break;
                case "camera" :
                    html +=  "<div item='camera' styles='navButton'></div>";
                    break;
                case "resetlayout" :
                    html += "<div item='resetlayout' styles='" + "navButton" + "'></div>";
                    break;
                case "move" :
                    html +=  "<div item='move' styles='navButton' itemevent='click'></div>";
                    break;
                case "expandLevel" :
                    html +=  "<div item='expandLevel' styles='navButton'></div>";
                    break;
                case "selectAll" :
                    html +=  "<div item='selectAll' styles='" + "navButton" + "'></div>";
                    break;
                case "preview" :
                    html +=  "<div item='preview' subtype='button' itemevent='click' styles='navButton'></div>"+
                        "<div item='preview' subtype='container'></div>";
                    break;
                case "template" :
                    html +=  "<div item='template'  styles='navButton'></div>";
                    break;
                case "theme" :
                    html +=  "<div item='theme' styles='navButton'></div>";
                    break;
                case "search" :
                    html += "<div item='search' subtype='button' itemevent='click' styles='navButton'></div>"+
                        "<div item='search' subtype='container'></div>";
                    break;
            }
       }.bind(this));
        return html;
    },
    setSize : function(){
        var reduce = 0;
        if( !this.hasZoom ) reduce = 131;
        reduce += ( 9 - this.itemCount ) * 27;
        if( reduce > 0 ){
            var height = this.node.getSize().y - 10;
            this.node.setStyle("height", height - reduce );
        }
    },
    creat: function(){
        this.node = new Element("div",{ "styles" : this.css.nav  }).inject(this.container);

        this.node.set("html",this.getHtml());

        this.setSize();

        this.node.getElements("[styles]").each(function (el) {
            if (!el.get("item")) {
                el.setStyles(this.css[el.get("styles")]);
            }
        }.bind(this));

        this.commands.addContainer( "lefttoolbar", this.node, this.css );


        //this.navCameraNode = new Element("div",{ "styles" : this.css.navButton , "title" : this.lp.navCamera  }).inject(this.node);
        //new Element("div",{ "styles" : this.css.navCameraIcon  }).inject(this.navCameraNode);
        //this.navCameraNode.addEvent("click",function(){
        //    this.minder.execCommand('camera', this.minder.getRoot(), 600);
        //    //this.editor.moveToCenter()
        //}.bind(this));
        //
        //this.navExpandNode = new Element("div",{ "styles" : this.css.navButton , "title" : "展开节点"  }).inject(this.node);
        //new Element("div",{ "styles" : this.css.navExpandIcon  }).inject(this.navExpandNode);
        //this.navExpandNode.addEvent("click",function(ev){
        //    this.showExpandNode( ev );
        //    ev.stopPropagation();
        //}.bind(this));

        //this.navTrigger = new Element("div",{ "styles" : this.css.navButton  , "title" : this.lp.navTrigger  }).inject(this.node);
        //new Element("div",{ "styles" : this.css.navTriggerIcon  }).inject(this.navTrigger);
        //this.navTrigger.addEvent( "click", function(){
        //    this.toggleOpenPreViewer();
        //}.bind(this) );
        //this.createrPreViewer();

        //this.navMoveNode = new Element("div",{ "styles" : this.css.navButton , "title" : this.lp.allowDrag  }).inject(this.node);
        //this.navMoveNode.setStyles( this.css.navButton_over );
        //this.moveOpen = true;
        //new Element("div",{ "styles" : this.css.navMoveIcon  }).inject(this.navMoveNode);
        //this.navMoveNode.addEvent("click",function(){
        //    this.moveOpen = !this.moveOpen;
        //    if( this.moveOpen ){
        //        this.navMoveNode.setStyles( this.css.navButton_over );
        //    }else{
        //        this.navMoveNode.setStyles( this.css.navButton );
        //    }
        //    this.minder.execCommand('hand');
        //}.bind(this));

        //this.navTemplateNode = new Element("div",{ "styles" : this.css.navButton , "title" : this.lp.changeTemplate  }).inject(this.node);
        //new Element("div",{ "styles" : this.css.navTemplateIcon  }).inject(this.navTemplateNode);
        //this.navTemplateNode.addEvent("click",function( ev ){
        //    this.selectTemplate( ev );
        //    ev.stopPropagation();
        //}.bind(this));


        //this.navSearchNode = new Element("div",{ "styles" : this.css.navButton , "title" : this.lp.search  }).inject(this.node);
        //new Element("div",{ "styles" : this.css.navSearchIcon  }).inject(this.navSearchNode);
        //
        //this.navSearchNode.addEvent("click",function(){
        //    if( !this.isShowedSearch ){
        //        this.showSearch();
        //    }else{
        //        this.hideSearch();
        //    }
        //}.bind(this));

        //this.app.content.addEvent('keydown', function(e) {
        //    if (e.code == 70 && e.control && !e.shift) {
        //        this.navSearchNode.click();
        //        e.preventDefault();
        //    }
        //}.bind(this));

        //this.minder.on('searchNode', function() {
        //    this.navSearchNode.click();
        //});

        //this.navExpand = new Element("div",{ "styles" : this.css.navButton , "title" : "展开"  }).inject(this.node);
        //new Element("div",{ "styles" : this.css.navExpandIcon  }).inject(this.navExpand);
    }
    //setMoveOpen : function( flag ){
    //    this.navMoveNode.setStyles( flag ? this.css.navButton_over : this.css.navButton );
    //    this.moveOpen = flag ? true : false;
    //},

    //showSearch: function(){
    //    this.isShowedSearch = true;
    //    if( !this.searchBar ){
    //        this.searchBar = new MWF.xApplication.MinderEditor.SearchBar( this, this.node, this.minder, this.app, this.css );
    //        this.searchBar.load();
    //    }else{
    //        this.searchBar.show()
    //    }
    //    this.navSearchNode.setStyles( this.css.navButton_over );
    //},
    //hideSearch: function(){
    //    this.isShowedSearch = false;
    //    this.searchBar.hide();
    //    this.navSearchNode.setStyles( this.css.navButton );
    //}
    //selectTemplate: function(){
    //    this.templateOpen = !this.templateOpen;
    //    if( this.templateOpen ){
    //        if( this.templateSelectNode ){
    //            this.templateSelectNode.setStyle("display","block")
    //        }else{
    //            this.createTemplateSelectNode();
    //        }
    //        this.navTemplateNode.setStyles( this.css.navButton_over );
    //    }else{
    //        this.hideTemplateSelectNode();
    //    }
    //},
    //hideTemplateSelectNode: function(){
    //    this.templateOpen = false;
    //    this.navTemplateNode.setStyles( this.css.navButton );
    //    if( this.templateSelectNode ){
    //        this.templateSelectNode.setStyle("display","none");
    //    }
    //},
    //createTemplateSelectNode: function(){
    //    this.templateSelectNode = new Element("div",{
    //        styles : this.css.templateSelectNode
    //    }).inject(this.node);
    //
    //    this.minderTemplate = new Element("div",{
    //        styles : this.css.minderTemplate,
    //        title : this.lp.minderTemplate
    //    }).inject(this.templateSelectNode);
    //    this.minderTemplate.addEvents({
    //        "mouseover" : function(){ this.minderTemplate.setStyles( this.css.minderTemplate_over )}.bind(this),
    //        "mouseout" : function(){ this.minderTemplate.setStyles( this.css.minderTemplate )}.bind(this),
    //        "click" : function(){
    //            this.editor.templateChanged = true;
    //            this.minder.execCommand('template', "default");
    //            this.hideTemplateSelectNode();
    //            //this.editor.moveToCenter();
    //        }.bind(this)
    //    });
    //
    //    this.fishboneTemplate = new Element("div",{
    //        styles : this.css.fishboneTemplate,
    //        title : this.lp.fishBoneTemplate
    //    }).inject(this.templateSelectNode);
    //    this.fishboneTemplate.addEvents({
    //        "mouseover" : function(){ this.fishboneTemplate.setStyles( this.css.fishboneTemplate_over )}.bind(this),
    //        "mouseout" : function(){ this.fishboneTemplate.setStyles( this.css.fishboneTemplate )}.bind(this),
    //        "click" : function(){
    //            //this.minder.on("execCommand", function (e) {
    //            //    if (e.commandName === "template"  ) {
    //            //        this.editor.moveToCenter();
    //            //    }
    //            //}.bind(this));
    //            this.editor.templateChanged = true;
    //            this.minder.execCommand('template', "fish-bone");
    //            this.hideTemplateSelectNode();
    //            //this.editor.moveToCenter();
    //        }.bind(this)
    //    });
    //
    //    this.app.content.addEvent("click",function(){
    //        this.hideTemplateSelectNode();
    //    }.bind(this))
    //},
    //showExpandNode: function(){
    //    this.expandNodeOpen = !this.expandNodeOpen;
    //    if( this.expandNodeOpen ){
    //        if( this.expandArea ){
    //            this.expandArea.setStyle("display","block")
    //        }else{
    //            this.createExpandArea();
    //        }
    //        this.navExpandNode.setStyles( this.css.navButton_over );
    //    }else{
    //        this.hideExpandArea();
    //    }
    //},
    //hideExpandArea: function(){
    //    this.expandNodeOpen = false;
    //    this.navExpandNode.setStyles( this.css.navButton );
    //    if( this.expandArea ){
    //        this.expandArea.setStyle("display","none");
    //    }
    //},
    //createExpandArea: function(){
    //    var deepestLevel = this.editor.deepestLevel || 6;
    //
    //    this.expandArea = new Element("div",{
    //        styles : this.css.expandArea
    //    }).inject(this.node);
    //    this.expandArea.setStyle("height",this.css.expandNode.height * (deepestLevel+1));
    //
    //    var expandAllNode = new Element("div",{
    //        styles : this.css.expandNode,
    //        text : "展开所有节点"
    //    }).inject(this.expandArea);
    //    expandAllNode.addEvents({
    //        "mouseover" : function(){ this.node.setStyles( this.navi.css.expandNode_over )}.bind( { navi : this, node : expandAllNode} ),
    //        "mouseout" : function(){ this.node.setStyles( this.navi.css.expandNode )}.bind({ navi : this, node : expandAllNode}),
    //        "click" : function(){
    //            this.navi.minder.execCommand('expandtolevel', deepestLevel);
    //            this.navi.hideExpandArea();
    //        }.bind({ navi : this, node : expandAllNode})
    //    })
    //
    //    for( var i=1; i<=deepestLevel; i++ ){
    //        var expandNode = new Element("div",{
    //            styles : this.css.expandNode,
    //            text : "展开到"+i+"级节点"
    //        }).inject(this.expandArea);
    //        expandNode.addEvents({
    //            "mouseover" : function(){ this.node.setStyles( this.navi.css.expandNode_over )}.bind( { navi : this, node : expandNode} ),
    //            "mouseout" : function(){ this.node.setStyles( this.navi.css.expandNode )}.bind({ navi : this, node : expandNode}),
    //            "click" : function(){
    //                this.navi.minder.execCommand('expandtolevel', this.level);
    //                this.navi.hideExpandArea();
    //            }.bind({ navi : this, node : expandNode, level : i})
    //        })
    //    }
    //
    //    this.app.content.addEvent("click",function(){
    //        this.hideExpandArea();
    //    }.bind(this))
    //}
});
