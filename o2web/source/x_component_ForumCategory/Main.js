MWF.xApplication.Forum = MWF.xApplication.Forum || {};
MWF.xApplication.ForumCategory = MWF.xApplication.ForumCategory || {};
MWF.require("MWF.widget.O2Identity", null, false);
MWF.xDesktop.requireApp("Forum", "Common", null, false);
//MWF.xDesktop.requireApp("Forum", "Actions.RestActions", null, false);
MWF.xDesktop.requireApp("Forum", "lp."+MWF.language, null, false);
MWF.xDesktop.requireApp("Template", "Explorer", null, false);
MWF.xDesktop.requireApp("Forum", "Access", null, false);
MWF.xDesktop.requireApp("Forum", "ColumnTemplate", null, false);
MWF.xDesktop.requireApp("Forum", "TopNode", null, false);
MWF.xApplication.ForumCategory.options = {
    multitask: false,
    executable: true
};
MWF.xApplication.ForumCategory.Main = new Class({
    Extends: MWF.xApplication.Common.Main,
    Implements: [Options, Events],

    options: {
        "style": "default",
        "name": "ForumCategory",
        "icon": "icon.png",
        "width": "1230",
        "height": "700",
        "isResize": false,
        "isMax": true,
        "naviMode" : false,
        "hasTop" : true,
        "hasBreadCrumb" : true,
        "autoWidth" : false,
        "title": MWF.xApplication.ForumCategory.LP.title
    },
    onQueryLoad: function () {
        this.lp = MWF.xApplication.Forum.LP;
    },
    loadApplication: function (callback) {
        this.userName = layout.desktop.session.user.distinguishedName;
        this.restActions = MWF.Actions.get("x_bbs_assemble_control"); //new MWF.xApplication.Forum.Actions.RestActions();

        this.path = "../x_component_ForumCategory/$Main/" + this.options.style + "/";
        this.createNode();
        this.loadApplicationContent();
    },
    loadController: function (callback) {
        this.access = new MWF.xApplication.Forum.Access( this.restActions, this.lp );
        if (callback)callback();
    },
    createNode: function () {
        this.content.setStyle("overflow", "hidden");
        this.node = new Element("div", {
            "styles": this.css.node
        }).inject(this.content);
    },
    reload : function(){
        this.status = {
            categoryId : this.options.categoryId,
            noteHidden : this.noteNodeHidden
        };
        this.openMainPage();
    },
    loadApplicationContent: function () {
        if( !this.options.categoryId && this.status && this.status.categoryId ){
            this.options.categoryId = this.status.categoryId;
        }
        this.loadController(function () {
            this.access.login( function(){
                this.loadApplicationLayout();
            }.bind(this))
        }.bind(this))
    },
    loadApplicationLayout: function () {
        this.contentContainerNode = new Element("div.contentContainerNode", {
            "styles":  this.options.autoWidth ? this.css.contentContainerNode_inContaienr : this.css.contentContainerNode
        }).inject(this.node);

        this.restActions.getCategory( this.options.categoryId, function (json) {
            this.data = json.data;

            var tail = this.inBrowser ? (MWFForum.getSystemConfigValue( MWFForum.BBS_TITLE_TAIL ) || "") : "";
            this.setTitle( this.data.forumName + tail);
            this.createTopNode();

            this.middleNode = new Element("div.middleNode", {
                "styles": this.options.autoWidth ? this.css.middleNode_inContainer : this.css.middleNode
            }).inject(this.contentContainerNode);

            this.createNoteNode();
            this.createContainerNode();
        }.bind(this))

    },
    loadTopObject : function(naviMode){
        if( this.options.hasTop ){
            var node = new MWF.xApplication.Forum.TopNode(this.contentContainerNode, this, this, {
                type: this.options.style,
                naviModeEnable : true,
                naviMode : naviMode
            });
            node.load();
        }
    },
    createTopNode: function () {
        this.loadTopObject( false );

        var forumColor = MWF.xApplication.Forum.ForumSetting[this.options.categoryId].forumColor;

        if( this.options.hasBreadCrumb || (this.data.forumNotice && this.data.forumNotice!="") ){

            var topNode = this.topNode = new Element("div.topNode", {
                "styles": this.options.autoWidth ? this.css.topNode_inContainer : this.css.topNode
            }).inject(this.contentContainerNode);
            //topNode.setStyle( "border-bottom" , "1px solid "+forumColor );
        }

        if( this.options.hasBreadCrumb ){

            var topTitleMiddleNode = new Element("div.topTitleMiddleNode", {
                "styles": this.css.topTitleMiddleNode
            }).inject(topNode);
            //topTitleMiddleNode.setStyle( "background-color" , forumColor )

            var topItemTitleNode = new Element("div.topItemTitleNode", {
                "styles": this.css.topItemTitleNode,
                "text": MWFForum.getBBSName() || MWF.xApplication.Forum.LP.title
            }).inject(topTitleMiddleNode);

            var topItemSepNode = new Element("div.topItemSepNode", {
                "styles": this.css.topItemSepNode,
                "text" : ">"
            }).inject(topTitleMiddleNode);

            topItemTitleNode.addEvent("click", function(){
                if( this.options.naviMode && this.forumNavi ){
                    this.forumNavi.goto( MWFForum.NaviType.main )
                }else{
                    var appId = "Forum";
                    if (this.desktop.apps[appId]){
                        this.desktop.apps[appId].setCurrent();
                    }else {
                        this.desktop.openApplication(null, "Forum", { "appId": appId });
                    }
                    if( !this.inBrowser ){
                        this.close();
                    }
                }
            }.bind(this));

            var topItemTitleNode = new Element("div.topItemTitleNode", {
                "styles": this.css.topItemTitleLastNode,
                "text": this.data.forumName
            }).inject(topTitleMiddleNode);
        }

        if( this.data.forumNotice && this.data.forumNotice!="" ){
            var topRightNode = new Element("div", {
                "styles": this.css.topRightNode
            }).inject(topNode);

            topRightNode.addEvents({
                "click" :function(){
                    if( !this.noteNodeHidden ){
                        this.noteNode.setStyle("display","none");
                        this.topRightIconNode.setStyles(this.css.topRightIconDownNode);
                        this.noteNodeHidden = true;
                    }else{
                        this.noteNode.setStyle("display","");
                        this.topRightIconNode.setStyles(this.css.topRightIconNode);
                        this.noteNodeHidden = false;
                    }
                }.bind(this)
            });

            var topRightIconNode = this.topRightIconNode = new Element("div", {
                "styles": this.css.topRightIconNode
            }).inject(topRightNode);

            if( this.status && this.status.noteHidden ){
                this.topRightIconNode.setStyles(this.css.topRightIconDownNode);
                this.noteNodeHidden = true;
            }

        }

        this._createTopContent();
    },
    _createTopContent: function () {

    },
    createContainerNode: function () {
        this.createCategory();
    },
    createCategory: function () {

        if( !this.options.naviMode ){
            this.setContentSizeFun = this.setContentSize.bind(this);
            this.addEvent("resize", this.setContentSizeFun );
            this.setContentSize();
        }

        //MWF.require("MWF.widget.ScrollBar", function () {
        //    this.scrollBar = new MWF.widget.ScrollBar(this.contentContainerNode, {
        //        "indent": false,
        //        "style": "xApp_TaskList",
        //        "where": "before",
        //        "distance": 30,
        //        "friction": 4,
        //        "axis": {"x": false, "y": true},
        //        "onScroll": function (y) {
        //        }
        //    });
        //}.bind(this));

        this.contentNode = new Element("div.contentNode", {
            "styles": this.css.contentNode
        }).inject(this.middleNode);

        this._createCategory( this.data )

    },
    createNoteNode : function(){
        if( !this.data.forumNotice || this.data.forumNotice.trim() =="" ){
            return;
        }
        var noteNode = this.noteNode = new Element("div.noteNode", {
            "styles": this.css.noteNode
        }).inject(this.middleNode);

        var noteTopNode = new Element("div.noteTopNode", {
            "styles": this.css.noteTopNode
        }).inject(noteNode);
        var noteTopContent = new Element("div.noteTopContent", {
            "styles": this.css.noteTopContent
        }).inject(noteTopNode);
        var noteIcon = new Element("div.noteIcon", {
            "styles": this.css.noteIcon
        }).inject(noteTopContent);
        var noteTopText = new Element("div.noteTopText", {
            "styles": this.css.noteTopText,
            "text" : this.lp.forumNotice
        }).inject(noteTopContent);


        var noteContent = new Element("div.noteContent", {
            "styles": this.css.noteContent,
            "html" : this.data.forumNotice
        }).inject(noteNode);

        if( this.status && this.status.noteHidden ){
            noteNode.setStyle("display" , "none");
        }
    },
    setContentSize: function () {
       //var topSize = this.topNode ? this.topNode.getSize() : {"x": 0, "y": 0};
        var topSize = {"x": 0, "y": 0};
        var nodeSize = this.node.getSize();
        var pt = this.contentContainerNode.getStyle("padding-top").toFloat();
        var pb = this.contentContainerNode.getStyle("padding-bottom").toFloat();

        var height = nodeSize.y - topSize.y - pt - pb;
        this.contentContainerNode.setStyle("height", "" + height + "px");
    },
    _createCategory: function (d) {
        //var categoryNode = new Element("div.categoryNode", {
        //    "styles": this.css.categoryNode
        //}).inject(this.contentNode);

        var categoryNode = new Element("div.categoryNode", {
            "styles": this.css.categoryNode
        }).inject(this.contentNode);

        var categoryTopNode = new Element("div.categoryTopNode", {
            "styles": this.css.categoryTopNode
        }).inject(categoryNode);
        //categoryTopNode.setStyle( "border-bottom" , "1px solid "+ d.forumColor || this.lp.defaultForumColor );

        var categoryTopTitleNode = new Element("div.categoryTopTitleNode", {
            "styles": this.css.categoryTopTitleNode,
            "text": d.forumName
        }).inject(categoryTopNode);
        categoryTopTitleNode.addEvents({
            click : function(el){ this.obj.openCategory( this.data ) }.bind({ obj : this, data : d })
        });
        categoryTopTitleNode.setStyle( "color" , d.forumColor || this.lp.defaultForumColor );


        var categoryTopRightNode = new Element("div.categoryTopRightNode", {
            "styles": this.css.categoryTopRightNode2
        }).inject(categoryTopNode);
        this.createPersonNode(categoryTopRightNode,d.forumManagerName );

        new Element("div.categoryTopRightNode", {
            "styles": this.css.categoryTopRightNode,
            "text": this.lp.categoryManager + "：" //+ d.forumManagerName
        }).inject(categoryTopNode);


        var view = new MWF.xApplication.Forum.ColumnTemplate(categoryNode, this, this, {
            type: d.indexListStyle || "type_1_0",
            style : this.options.naviMode ? "naviMode" : "default",
            naviMode : this.options.naviMode,
            categoryId: d.id,
            onPostLoad : function(){
                this.fireEvent("postLoadCategory")
            }.bind(this)
        });
        view.forumNavi = this.forumNavi;
        view.load();

        //
        //if (d.indexListStyle == "经典") {
        //    var view = new MWF.xApplication.ForumCategory.Main.ListView(categoryNode, this, this, {
        //        templateUrl: this.path + "listItemList.json",
        //        categoryId: d.id
        //    }, {
        //        lp: this.lp
        //    })
        //    view.load();
        //} else if (d.indexListStyle == "图片矩形") {
        //    var view = new MWF.xApplication.ForumCategory.Main.ImageView(categoryNode, this, this, {
        //        templateUrl: this.path + "listItemImage.json",
        //        categoryId: d.id
        //    }, {
        //        lp: this.lp
        //    })
        //    view.load();
        //} else {
        //    var view = new MWF.xApplication.ForumCategory.Main.TileView(categoryNode, this, this, {
        //        templateUrl: this.path + "listItemTile.json",
        //        categoryId: d.id
        //    }, {
        //        lp: this.lp
        //    })
        //    view.load();
        //}
    },
    openNavi: function () {
        MWF.xDesktop.requireApp("Forum", "NaviMode", null, false);
        this.clearContent();
        this.contentContainerNode = new Element("div.contentContainerNode", {
            "styles": this.css.contentContainerNode
        }).inject(this.node);
        this.loadTopObject( true );
        this.middleNode = new Element("div.middleNode", {
            "styles": this.css.middleNode
        }).inject(this.contentContainerNode);
        this.contentNode = new Element("div.contentNode", {
            "styles": this.css.contentNode
        }).inject(this.middleNode);

        this.navi = new MWF.xApplication.Forum.NaviMode(this, this.contentNode, {
            type : MWFForum.NaviType.category,
            id : this.options.categoryId
        });
        this.navi.load();
    },
    closeNavi : function(){
        if( this.navi )this.navi.close();
    },
    destroy : function(){
        this.clearContent();
    },
    clearContent: function () {
        if (this.explorer)this.explorer.destroy();
        if(this.setContentSizeFun)this.removeEvent("resize", this.setContentSizeFun );
        if(this.scrollBar && this.scrollBar.scrollVAreaNode)this.scrollBar.scrollVAreaNode.destroy();
        if( this.scrollBar )delete this.scrollBar;
        if (this.contentContainerNode) {
            this.contentContainerNode.destroy();
            //this.middleNode.destroy();
            //this.contentNode.destroy();
        }
    },
    openMainPage : function(){
        this.clearContent();
        //this.createCategory();
        this.loadApplicationLayout();
    },
    recordStatus: function () {
        return {
            categoryId : this.options.categoryId,
            noteHidden : this.noteNodeHidden
        };
    },
    openPerson : function( userName ){
        var appId = "ForumPerson"+userName;
        if (this.desktop.apps[userName]){
            this.desktop.apps[userName].setCurrent();
        }else {
            this.desktop.openApplication(null, "ForumPerson", {
                "personName" : userName,
                "appId": appId
            });
        }
    },
    createPersonNode : function( container, personName ){
        var persons = personName.split(",");
        persons.each( function(userName, i){
            var span = new Element("span", {
                "text" : userName.split("@")[0],
                "styles" : this.css.person
            }).inject(container);
            span.addEvents( {
                mouseover : function(){ this.node.setStyles( this.obj.css.person_over )}.bind( {node:span, obj:this} ),
                mouseout : function(){ this.node.setStyles( this.obj.css.person )}.bind( {node:span, obj:this} ),
                click : function(){ this.obj.openPerson( this.userName ) }.bind( {userName:userName, obj:this} )
            });
            if( i != persons.length - 1 ){
                new Element("span", {
                    "text" : ","
                }).inject(container);
            }
        }.bind(this))
    }
});



