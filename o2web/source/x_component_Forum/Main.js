MWF.xApplication.Forum = MWF.xApplication.Forum || {};
window.MWFForum = window.MWFForum || MWF.xApplication.Forum;
MWF.require("MWF.widget.O2Identity", null, false);
MWF.xDesktop.requireApp("Forum", "Common", null, false);
//MWF.xDesktop.requireApp("Forum", "Actions.RestActions", null, false);
MWF.xDesktop.requireApp("Forum", "Access", null, false);
MWF.xDesktop.requireApp("Forum", "ColumnTemplate", null, false);
MWF.xDesktop.requireApp("Forum", "TopNode", null, false);
MWF.xDesktop.requireApp("Template", "Explorer", null, false);
MWF.xApplication.Forum.options = {
    multitask: false,
    executable: true
};
MWF.xApplication.Forum.Main = new Class({
    Extends: MWF.xApplication.Common.Main,
    Implements: [Options, Events],

    options: {
        "style": "default",
        "name": "Forum",
        "icon": "icon.png",
        "width": "1230",
        "height": "700",
        "isResize": true,
        "isMax": true,
        "title": MWF.xApplication.Forum.LP.title
    },
    onQueryLoad: function () {
        this.lp = MWF.xApplication.Forum.LP;
        this.setTitle( this.getMainPageTitle() )
    },
    getMainPageTitle : function(){
        var tail = this.inBrowser ? (MWFForum.getSystemConfigValue( MWFForum.BBS_TITLE_TAIL ) || "") : "";
        return ( MWFForum.getBBSName() || MWF.xApplication.Forum.LP.title ) + tail;
    },
    loadApplication: function (callback) {
        this.userName = layout.desktop.session.user.distinguishedName;
        this.restActions = MWF.Actions.get("x_bbs_assemble_control"); //new MWF.xApplication.Forum.Actions.RestActions();

        this.path = "../x_component_Forum/$Main/" + this.options.style + "/";
        this.createNode();
        this.loadApplicationContent();
    },
    loadController: function (callback) {

        this.access = new MWF.xApplication.Forum.Access( this.restActions, this.lp );

        if (callback)callback();
    },
    reload : function(){
        this.clearContent();
        if( this.explorer ){
            this.openSetting( this.explorer.currentNaviItem.retrieve("index") )
        }else{
            this.loadApplicationLayout();
        }
    },
    isAdmin : function(){
      return this.access.isAdmin();
    },
    createNode: function () {
        this.content.setStyle("overflow", "hidden");
        this.node = new Element("div", {
            "styles": this.css.node
        }).inject(this.content);
    },
    loadApplicationContent: function () {
        this.loadController(function () {
            this.access.login( function(){
                if( this.status && this.status.setting ){
                    this.openSetting( this.status.index )
                }else{
                    this.loadApplicationLayout();
                }
            }.bind(this))
        }.bind(this))
    },
    loaNavi: function (callback) {
        this.naviNode = new Element("div.naviNode", {
            "styles": this.css.naviNode
        }).inject(this.node);

        var curNavi = {"id": ""};
        if (this.status) {
            curNavi.id = this.status.id;
        }
        this.navi = new MWF.xApplication.Forum.Navi(this, this.naviNode, curNavi);
    },
    loadApplicationLayout: function () {
        this.contentContainerNode = new Element("div.contentContainerNode", {
            "styles": this.css.contentContainerNode
        }).inject(this.node);
        this.createTopNode( false );
        this.createContainerNode();

    },
    createTopNode: function( naviMode ){
        var node = this.topObject = new MWF.xApplication.Forum.TopNode(this.contentContainerNode, this, this, {
            type: this.options.style,
            settingEnable : true,
            naviModeEnable : true,
            naviMode : naviMode
        });
        node.load();
    },
    createContainerNode: function () {
        this.createContent();
    },
    createContent: function () {

        this.middleNode = new Element("div.middleNode", {
            "styles": this.css.middleNode
        }).inject(this.contentContainerNode);

        this.setContentSizeFun = this.setContentSize.bind(this);
        this.addEvent("resize", this.setContentSizeFun );
        this.setContentSize();

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

        this.createRecommand();

        this.restActions.listCategoryAll(function (json) {
            if( !json.data )json.data = [];
            json.data.each(function (d, idx) {
                if(d.forumStatus != this.lp.invalid ){
                    this._createCategory(d, idx);
                }
            }.bind(this))
        }.bind(this))
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
    createRecommand: function () {
        var recommandNode = new Element("div.recommandNode", {
            "styles": this.css.recommandNode
        }).inject(this.contentNode);

        var recommandTopNode = new Element("div.recommandTopNode", {
            "styles": this.css.recommandTopNode
        }).inject(recommandNode);
        //recommandTopNode.setStyle( "border-bottom" , "1px solid "+this.lp.defaultForumColor );

        var recommandTopTitleNode = new Element("div.recommandTopTitleNode", {
            "styles": this.css.recommandTopTitleNode,
            "text": this.lp.recommandSubject
        }).inject(recommandTopNode);
        //recommandTopTitleNode.setStyle( "background-color" , this.lp.defaultForumColor );
        //categoryTopTitleNode.addEvents({
        //    click : function(el){ this.obj.openCategory( this.data ) }.bind({ obj : this, data : d })
        //})

        //var categoryTopRightNode = new Element("div.categoryTopRightNode", {
        //    "styles": this.css.categoryTopRightNode,
        //    "text": this.lp.recommandSubject
        //}).inject(categoryTopNode);
        var view = new MWF.xApplication.Forum.Main.RecommandView(recommandNode, this, this, {
            templateUrl: this.path + "listItemRecommand.json"
        }, {
            lp: this.lp
        });
        view.load();
    },
    _createCategory: function (d, idx) {
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
            categoryId: d.id
        });
        view.load();
        //if (d.indexListStyle == "经典") {
        //    var view = new MWF.xApplication.Forum.Main.ListView(categoryNode, this, this, {
        //        templateUrl: this.path + "listItemList.json",
        //        categoryId: d.id
        //    }, {
        //        lp: this.lp
        //    });
        //    view.load();
        //} else if (d.indexListStyle == "图片矩形") {
        //    var view = new MWF.xApplication.Forum.Main.ImageView(categoryNode, this, this, {
        //        templateUrl: this.path + "listItemImage.json",
        //        categoryId: d.id
        //    }, {
        //        lp: this.lp
        //    });
        //    view.load();
        //} else {
        //    var view = new MWF.xApplication.Forum.Main.TileView(categoryNode, this, this, {
        //        templateUrl: this.path + "listItemTile.json",
        //        categoryId: d.id
        //    }, {
        //        lp: this.lp
        //    });
        //    view.load();
        //}
    },
    clearContent: function () {
        if (this.explorer)this.explorer.destroy();
        this.explorer = null;
        if(this.setContentSizeFun)this.removeEvent("resize", this.setContentSizeFun );
        if(this.scrollBar && this.scrollBar.scrollVAreaNode)this.scrollBar.scrollVAreaNode.destroy();
        if( this.scrollBar )delete this.scrollBar;
        if (this.contentContainerNode) {
            this.contentContainerNode.destroy();
            //this.middleNode.destroy();
            //this.contentNode.destroy();
        }
    },
    openCategory : function( d ){
        var appId = "ForumCategory"+ d.id;
        if (this.desktop.apps[appId]){
            this.desktop.apps[appId].setCurrent();
        }else {
            this.desktop.openApplication(null, "ForumCategory", {
                "categoryId": d.id,
                "appId": appId
            });
        }

    },
    openView : function(){

    },
    openNavi: function () {
        MWF.xDesktop.requireApp("Forum", "NaviMode", null, false);
        this.clearContent();
        this.contentContainerNode = new Element("div.contentContainerNode", {
            "styles": this.css.contentContainerNode
        }).inject(this.node);
        this.createTopNode( true );
        this.middleNode = new Element("div.middleNode", {
            "styles": this.css.middleNode
        }).inject(this.contentContainerNode);
        this.contentNode = new Element("div.contentNode", {
            "styles": this.css.contentNode
        }).inject(this.middleNode);

        this.navi = new MWF.xApplication.Forum.NaviMode(this, this.contentNode, {});
        this.navi.load();
    },
    closeNavi : function(){
      if( this.navi )this.navi.close();
    },
    openSetting: function ( index ) {
        MWF.xDesktop.requireApp("Forum", "Setting", null, false);
        this.clearContent();
        this.contentContainerNode = new Element("div.contentContainerNode", {
            "styles": this.css.contentContainerNode
        }).inject(this.node);
        this.createTopNode( false );
        this.middleNode = new Element("div.middleNode", {
            "styles": this.css.middleNode
        }).inject(this.contentContainerNode);
        this.contentNode = new Element("div.contentNode", {
            "styles": this.css.contentNode
        }).inject(this.middleNode);
        this.explorer = new MWF.xApplication.Forum.Setting(this.contentNode, this, this.restActions, {"isAdmin": this.isAdmin() , "index" : (index || 0) });
        this.explorer.load();
    },
    recordStatus: function () {
        var status = {};
        if( this.explorer ){
            status = {
                setting : true,
                index : this.explorer.currentNaviItem.retrieve("index")
            };
        }
        return status;
    },
    openPerson : function( userName ){
        var appId = "ForumPerson"+userName;
        if (this.desktop.apps[appId]){
            this.desktop.apps[appId].setCurrent();
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
                "text" : userName.split('@')[0],
                "styles" : this.css.person
            }).inject(container);
            span.addEvents( {
                mouseover : function(){ this.node.setStyles( this.obj.css.person_over )}.bind( {node:span, obj:this} ),
                mouseout : function(){ this.node.setStyles( this.obj.css.person )}.bind( {node:span, obj:this} ),
                click : function(){ this.obj.openPerson( this.userName ) }.bind( {userName:userName, obj:this} )
            });
            if( i != persons.length - 1 ){
                new Element("span", {
                    "text" : "、"
                }).inject(container);
            }
        }.bind(this))
    }
});

MWF.xApplication.Forum.Main.RecommandView = new Class({
    Extends: MWF.xApplication.Template.Explorer.ComplexView,
    _createDocument: function (data, index) {
            return new MWF.xApplication.Forum.Main.RecommandDocument(this.viewNode, data, this.explorer, this, null, index);
    },
    _getCurrentPageData: function (callback, count) {
        if (!count)count = 12;
        this.actions.listRecommendedSubject(count, function (json) {
            if( !json.data )json.data = [];
            if (callback)callback(json);
        }.bind(this))
    },
    _removeDocument: function (documentData, all) {
        //this.actions.deleteSchedule(documentData.id, function(json){
        //    this.reload();
        //    this.app.notice(this.app.lp.deleteDocumentOK, "success");
        //}.bind(this));
    },
    _create: function () {

    },
    _openDocument: function (documentData) {

    },
    _queryCreateViewNode: function () {

    },
    _postCreateViewNode: function (viewNode) {

    },
    _queryCreateViewHead: function () {

    },
    _postCreateViewHead: function (headNode) {

    }

});

MWF.xApplication.Forum.Main.RecommandDocument = new Class({
    Extends: MWF.xApplication.Template.Explorer.ComplexDocument,
    mouseoverDocument: function () {
        //this.node.getElements("[styles='documentItemTitleNode']").setStyles(this.css["documentItemTitleNode_over"]);
        //this.node.getElements("[styles='documentItemIconNode']").setStyles(this.css["documentItemIconNode_over"]);
        //this.node.getElements("[styles='documentItemStatNode']").setStyles(this.css["documentItemStatNode_over"]);
    },
    mouseoutDocument: function () {
        //this.node.getElements("[styles='documentItemTitleNode']").setStyles(this.css["documentItemTitleNode"]);
        //this.node.getElements("[styles='documentItemIconNode']").setStyles(this.css["documentItemIconNode"]);
        //this.node.getElements("[styles='documentItemStatNode']").setStyles(this.css["documentItemStatNode"]);
    },
    _queryCreateDocumentNode: function (itemData) {
    },
    _postCreateDocumentNode: function (itemNode, itemData) {
    },
    open: function(  ){
        var data = this.data;
        var appId = "ForumDocument"+data.id;
        if (this.app.desktop.apps[appId]){
            this.app.desktop.apps[appId].setCurrent();
        }else {
            this.app.desktop.openApplication(null, "ForumDocument", {
                "sectionId" : data.sectionId,
                "id" : data.id,
                "appId": appId,
                "isEdited" : false,
                "isNew" : false
            });
        }
    },
    openSection : function( el ){
        var data = this.data;
        var appId = "ForumSection"+ data.sectionId;
        if (this.app.desktop.apps[appId]){
            this.app.desktop.apps[appId].setCurrent();
        }else {
            this.app.desktop.openApplication(el, "ForumSection", {
                "sectionId": data.sectionId,
                "appId": appId
            });
        }
    }
});


