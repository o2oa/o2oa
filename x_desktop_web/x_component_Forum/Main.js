MWF.xApplication.Forum = MWF.xApplication.Forum || {};
MWF.require("MWF.widget.Identity", null, false);
MWF.xDesktop.requireApp("Forum", "Actions.RestActions", null, false);
MWF.xDesktop.requireApp("Forum", "Access", null, false);
MWF.xDesktop.requireApp("Template", "Explorer", null, false);
MWF.xApplication.Forum.options = {
    multitask: false,
    executable: true
}
MWF.xApplication.Forum.Main = new Class({
    Extends: MWF.xApplication.Common.Main,
    Implements: [Options, Events],

    options: {
        "style": "default",
        "name": "Forum",
        "icon": "icon.png",
        "width": "1210",
        "height": "700",
        "isResize": false,
        "isMax": true,
        "title": MWF.xApplication.Forum.LP.title
    },
    onQueryLoad: function () {
        this.lp = MWF.xApplication.Forum.LP;
    },
    loadApplication: function (callback) {
        this.userName = layout.desktop.session.user.name;
        this.restActions = new MWF.xApplication.Forum.Actions.RestActions();

        this.path = "/x_component_Forum/$Main/" + this.options.style + "/";
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

        var curNavi = {"id": ""}
        if (this.status) {
            curNavi.id = this.status.id
        }
        this.navi = new MWF.xApplication.Forum.Navi(this, this.naviNode, curNavi);
    },
    loadApplicationLayout: function () {
        this.contentContainerNode = new Element("div.contentContainerNode", {
            "styles": this.css.contentContainerNode
        }).inject(this.node);
        this.createTopNode();
        this.createContainerNode();

    },
    createTopNode: function () {
        this.topContainerNode = new Element("div.topContainerNode", {
            "styles": this.css.topContainerNode
        }).inject(this.contentContainerNode);

        this.topNode = new Element("div.topNode", {
            "styles": this.css.topNode
        }).inject(this.topContainerNode);

        this.topMainPageNode = new Element("div.topMainPageNode",{
            "styles" : { "cursor" : "pointer" }
        }).inject(this.topNode)
        this.topMainPageNode.addEvent("click", function(){
            this.openMainPage();
        }.bind(this))

        this.searchDiv = new Element("div.searchDiv",{
            "styles" : this.css.searchDiv
        }).inject(this.topNode)
        this.searchInput = new Element("input.searchInput",{
            "styles" : this.css.searchInput,
            "value" : this.lp.searchKey,
            "title" : this.lp.searchTitle
        }).inject(this.searchDiv)
        var _self = this;
        this.searchInput.addEvents({
            "focus": function(){
                if (this.value==_self.lp.searchKey) this.set("value", "");
            },
            "blur": function(){if (!this.value) this.set("value", _self.lp.searchKey);},
            "keydown": function(e){
                if (e.code==13){
                    this.search();
                    e.preventDefault();
                }
            }.bind(this)
        });

        this.searchAction = new Element("div.searchAction",{
            "styles" : this.css.searchAction
        }).inject(this.searchDiv);
        this.searchAction.addEvents({
            "click": function(){ this.search(); }.bind(this),
            "mouseover": function(e){
                this.searchAction.setStyles( this.css.searchAction_over2 );
                e.stopPropagation();
            }.bind(this),
            "mouseout": function(){ this.searchAction.setStyles( this.css.searchAction ) }.bind(this)
        });
        this.searchDiv.addEvents( {
            "mouseover" : function(){
                this.searchInput.setStyles( this.css.searchInput_over )
                this.searchAction.setStyles( this.css.searchAction_over )
            }.bind(this),
            "mouseout" : function(){
                this.searchInput.setStyles( this.css.searchInput )
                this.searchAction.setStyles( this.css.searchAction )
            }.bind(this)
        } )

        //this.getSystemSetting( "BBS_LOGO_NAME", function( data ){
        this.restActions.getBBSName( function( json ){
            var data = json.data;
            if( data.configValue && data.configValue!="" && data.configValue!="企业论坛" ){
                this.topTextNode = new Element("div.topTextNode", {
                    "styles": this.css.topTextNode,
                    "text": data.configValue
                }).inject(this.topMainPageNode)
            }else{
                this.topIconNode = new Element("div", {
                    "styles": this.css.topIconNode
                }).inject(this.topMainPageNode)
            }
        }.bind(this), null, false );
        //}.bind(this), false )

        this.topContentNode = new Element("div", {
            "styles": this.css.topContentNode
        }).inject(this.topNode);

        if( this.access.isAnonymous() ){
            if( this.access.signUpMode != "disable" ){
                this.signupNode = new Element("div", {
                    "styles": this.css.signupNode
                }).inject(this.topContentNode);
                this.signupIconNode = new Element("div", {
                    "styles": this.css.signupIconNode
                }).inject(this.signupNode);
                this.signupTextNode = new Element("div", {
                    "styles": this.css.signupTextNode,
                    "text": this.lp.signup
                }).inject(this.signupNode);
                this.signupNode.addEvent("click", function(){ this.openSignUpForm( ) }.bind(this))
            }

            this.loginNode = new Element("div", {
                "styles": this.css.loginNode
            }).inject(this.topContentNode);
            this.loginIconNode = new Element("div", {
                "styles": this.css.loginIconNode
            }).inject(this.loginNode);
            this.loginTextNode = new Element("div", {
                "styles": this.css.loginTextNode,
                "text": this.lp.login
            }).inject(this.loginNode);
            this.loginNode.addEvent("click", function(){ this.openLoginForm( ) }.bind(this))

        }else{
            if( this.inBrowser ){
                this.logoutNode = new Element("div", {
                    "styles": this.css.logoutNode
                }).inject(this.topContentNode);
                this.logoutIconNode = new Element("div", {
                    "styles": this.css.logoutIconNode
                }).inject(this.logoutNode);
                this.logoutTextNode = new Element("div", {
                    "styles": this.css.logoutTextNode,
                    "text": this.lp.logout
                }).inject(this.logoutNode);
                this.logoutNode.addEvent("click", function(){ this.logout( ) }.bind(this))
            }

            this.settingNode = new Element("div", {
                "styles": this.css.settingNode
            }).inject(this.topContentNode);
            this.settingIconNode = new Element("div", {
                "styles": this.css.settingIconNode
            }).inject(this.settingNode);
            this.settingTextNode = new Element("div", {
                "styles": this.css.settingTextNode,
                "text": this.lp.setting
            }).inject(this.settingNode);
            this.settingNode.addEvent("click", function(){ this.openSetting( ) }.bind(this));

            this.personNode = new Element("div", {
                "styles": this.css.personNode
            }).inject(this.topContentNode);
            this.personIconNode = new Element("div", {
                "styles": this.css.personIconNode
            }).inject(this.personNode);
            this.personTextNode = new Element("div", {
                "styles": this.css.personTextNode,
                "text": this.lp.personCenter
            }).inject(this.personNode);
            this.personNode.addEvent("click", function(){ this.openPerson(this.userName ) }.bind(this))
        }

        this._createTopContent();
    },
    _createTopContent: function () {

    },
    getSystemSetting : function( code, callback, async ){
      this.restActions.getSystemSettingByCode( {configCode : code }, function(json) {
          if (callback)callback(json.data);
      }.bind(this), null, async )
    },
    search : function(){
        var val = this.searchInput.get("value");
        if( val == "" || val == this.lp.searchKey ){
            this.notice( this.lp.noSearchContentNotice, "error" );
            return;
        }
        var appId = "ForumSearch";
        if (this.desktop.apps[appId] && !this.inBrowser){
            this.desktop.apps[appId].close();
        }
        this.desktop.openApplication(null, "ForumSearch", {
            "appId": appId,
            "searchContent" : val
        });
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
            json.data.each(function (d) {
                if(d.forumStatus != this.lp.invalid ){
                    this._createCategory(d);
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
        recommandTopNode.setStyle( "border-bottom" , "1px solid "+this.lp.defaultForumColor );

        var recommandTopTitleNode = new Element("div.recommandTopTitleNode", {
            "styles": this.css.recommandTopTitleNode,
            "text": this.lp.recommandSubject
        }).inject(recommandTopNode);
        recommandTopTitleNode.setStyle( "background-color" , this.lp.defaultForumColor );
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
        })
        view.load();
    },
    _createCategory: function (d) {
        var categoryNode = new Element("div.categoryNode", {
            "styles": this.css.categoryNode
        }).inject(this.contentNode);

        var categoryTopNode = new Element("div.categoryTopNode", {
            "styles": this.css.categoryTopNode
        }).inject(categoryNode);
        categoryTopNode.setStyle( "border-bottom" , "1px solid "+ d.forumColor || this.lp.defaultForumColor );

        var categoryTopTitleNode = new Element("div.categoryTopTitleNode", {
            "styles": this.css.categoryTopTitleNode,
            "text": d.forumName
        }).inject(categoryTopNode);
        categoryTopTitleNode.addEvents({
            click : function(el){ this.obj.openCategory( this.data ) }.bind({ obj : this, data : d })
        })
        categoryTopTitleNode.setStyle( "background-color" , d.forumColor || this.lp.defaultForumColor );


        var categoryTopRightNode = new Element("div.categoryTopRightNode", {
            "styles": this.css.categoryTopRightNode2
        }).inject(categoryTopNode);
        this.createPersonNode(categoryTopRightNode,d.forumManagerName );

        new Element("div.categoryTopRightNode", {
            "styles": this.css.categoryTopRightNode,
            "text": this.lp.categoryManager + "：" //+ d.forumManagerName
        }).inject(categoryTopNode);

        if (d.indexListStyle == "经典") {
            var view = new MWF.xApplication.Forum.Main.ListView(categoryNode, this, this, {
                templateUrl: this.path + "listItemList.json",
                categoryId: d.id
            }, {
                lp: this.lp
            })
            view.load();
        } else if (d.indexListStyle == "图片矩形") {
            var view = new MWF.xApplication.Forum.Main.ImageView(categoryNode, this, this, {
                templateUrl: this.path + "listItemImage.json",
                categoryId: d.id
            }, {
                lp: this.lp
            })
            view.load();
        } else {
            var view = new MWF.xApplication.Forum.Main.TileView(categoryNode, this, this, {
                templateUrl: this.path + "listItemTile.json",
                categoryId: d.id
            }, {
                lp: this.lp
            })
            view.load();
        }
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
    openMainPage : function(){
        this.clearContent();
        //this.createCategory();
        this.loadApplicationLayout();
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
    openLoginForm : function(){
        //MWF.xDesktop.requireApp("Forum", "Login", null, false);
        //var login = new MWF.xApplication.Forum.Login(this, {
        //    onPostOk : function(){
        //        window.location.reload();
        //    }
        //});
        //login.openLoginForm();
        MWF.require("MWF.xDesktop.Authentication", null, false);
        var authentication = new MWF.xDesktop.Authentication({
            style : "application",
            onPostOk : function(){
                window.location.reload();
            }
        },this);
        authentication.openLoginForm();
    },
    openSignUpForm : function(){
        //MWF.xDesktop.requireApp("Forum", "Login", null, false);
        //var login = new MWF.xApplication.Forum.Login(this, {});
        //login.openSignUpForm();
        MWF.require("MWF.xDesktop.Authentication", null, false);
        var authentication = new MWF.xDesktop.Authentication( {
            style : "application",
            onPostOk : function(){
            }
        }, this);
        authentication.openSignUpForm();
    },
    openSetting: function ( index ) {
        MWF.xDesktop.requireApp("Forum", "Setting", null, false);
        this.clearContent();
        this.contentContainerNode = new Element("div.contentContainerNode", {
            "styles": this.css.contentContainerNode
        }).inject(this.node);
        this.createTopNode();
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
                "text" : userName,
                "styles" : this.css.person
            }).inject(container);
            span.addEvents( {
                mouseover : function(){ this.node.setStyles( this.obj.css.person_over )}.bind( {node:span, obj:this} ),
                mouseout : function(){ this.node.setStyles( this.obj.css.person )}.bind( {node:span, obj:this} ),
                click : function(){ this.obj.openPerson( this.userName ) }.bind( {userName:userName, obj:this} )
            })
            if( i != persons.length - 1 ){
                new Element("span", {
                    "text" : ",",
                }).inject(container);
            }
        }.bind(this))
    },
    logout: function(){
        this.restActions.logout( function(){
            layout.desktop.session.user.name = "anonymous";
            this.clearContent();
            this.loadApplicationContent();
            this.openLoginForm();
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

})

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
})


MWF.xApplication.Forum.Main.TileView = new Class({
    Extends: MWF.xApplication.Template.Explorer.ComplexView,
    _createDocument: function (data, index) {
        if( this.explorer.access.isSectionViewer( data ) ){
            return new MWF.xApplication.Forum.Main.TileDocument(this.viewNode, data, this.explorer, this, null, index);
        }
    },

    _getCurrentPageData: function (callback, count) {
        if (!count)count = 20;
        var id = (this.items.length) ? this.items[this.items.length - 1].data.id : "(0)";
        var filter = this.filterData || {};
        this.actions.listSection(this.options.categoryId, function (json) {
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

})

MWF.xApplication.Forum.Main.TileDocument = new Class({
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
        if( (this.index + 1) % 4 == 0 ){
            itemNode.setStyle("margin-right" , "0px" );
        }

        var personNode = itemNode.getElements("[item='moderatorNames']")[0];
        this.app.createPersonNode( personNode, itemData.moderatorNames )

        var listNode = itemNode.getElements("[styles='documentItemListNode']")[0];
        if (listNode) {
            this._getListData(function (json) {
                json.data.each(function (d,i) {
                    var div = new Element("div", {
                        "styles": this.css.documentItemListItemNode,
                        "text": d.title,
                        "title": d.title
                    }).inject(listNode)
                    div.addEvents({
                        "mouseover": function () {
                            this.node.setStyles(this.obj.css.documentItemListItemNode_over)
                        }.bind({node: div, obj: this}),
                        "mouseout": function () {
                            this.node.setStyles(this.obj.css.documentItemListItemNode)
                        }.bind({node: div, obj: this}),
                        "click" : function(){
                            var appId = "ForumDocument"+this.da.id;
                            if (this.obj.app.desktop.apps[appId]){
                                this.obj.app.desktop.apps[appId].setCurrent();
                            }else {
                                this.obj.app.desktop.openApplication(null, "ForumDocument", {
                                    "sectionId" : this.da.sectionId,
                                    "id" : this.da.id,
                                    "appId": appId,
                                    "isEdited" : false,
                                    "isNew" : false,
                                    "index" : i
                                });
                            }
                        }.bind({da: d, obj: this})
                    })
                }.bind(this))
            }.bind(this), 6)
        }
    },
    _getListData: function (callback, count) {
        if (!count)count = 6;
        var filterData = {
            "sectionId": this.data.id
        }
        this.actions.listSubjectFilterPage(1, count, filterData, function (json) {
            if (!json.data)json.data = [];
            if (callback)callback(json);
        }.bind(this))
    },
    openSection : function( el ){
        var appId = "ForumSection"+ this.data.id;
        if (this.app.desktop.apps[appId]){
            this.app.desktop.apps[appId].setCurrent();
        }else {
            this.app.desktop.openApplication(el, "ForumSection", {
                "sectionId": this.data.id,
                "appId": appId
            });
        }
    }
})


MWF.xApplication.Forum.Main.ListView = new Class({
    Extends: MWF.xApplication.Template.Explorer.ComplexView,
    _createDocument: function (data, index) {
        if( this.explorer.access.isSectionViewer( data ) ){
            return new MWF.xApplication.Forum.Main.ListDocument(this.viewNode, data, this.explorer, this, null, index);
        }
    },

    _getCurrentPageData: function (callback, count) {
        if (!count)count = 20;
        var id = (this.items.length) ? this.items[this.items.length - 1].data.id : "(0)";
        var filter = this.filterData || {};
        this.actions.listSection(this.options.categoryId, function (json) {
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

})

MWF.xApplication.Forum.Main.ListDocument = new Class({
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
        var personNode = itemNode.getElements("[item='moderatorNames']")[0];
        this.app.createPersonNode( personNode, itemData.moderatorNames )

        var listNode = itemNode.getElements("[styles='documentItemListNode_list']")[0];
        var replyListNode = itemNode.getElements("[styles='documentItemReplyListNode_list']")[0];
        if (listNode) {
            this._getListData(function (json) {
                json.data.each(function (d,i) {
                    var div = new Element("div", {
                        "styles": this.css.documentItemListItemNode_list,
                        "text": d.title,
                        "title": d.title
                    }).inject(listNode)
                    div.addEvents({
                        "mouseover": function () {
                            this.node.setStyles(this.obj.css.documentItemListItemNode_list_over)
                        }.bind({node: div, obj: this}),
                        "mouseout": function () {
                            this.node.setStyles(this.obj.css.documentItemListItemNode_list)
                        }.bind({node: div, obj: this}),
                        "click" : function(){
                            var appId = "ForumDocument"+this.da.id;
                            if (this.obj.app.desktop.apps[appId]){
                                this.obj.app.desktop.apps[appId].setCurrent();
                            }else {
                                this.obj.app.desktop.openApplication(null, "ForumDocument", {
                                    "sectionId" : this.da.sectionId,
                                    "id" : this.da.id,
                                    "appId": appId,
                                    "isEdited" : false,
                                    "isNew" : false,
                                    "index" : i
                                });
                            }
                        }.bind({da: d, obj: this})
                    })

                    var replyNode = new Element("div", {
                        "styles": this.css.documentItemReplyListItemNode_list
                    }).inject(replyListNode)

                    var div = new Element("div", {
                        "styles": this.css.documentItemReplyTimeNode_list,
                        "text": getDateDiff(d.createTime),
                        "title": d.createTime
                    }).inject(replyNode)

                    var div = new Element("div", {
                        "styles": this.css.documentItemReplyPersonNode_list,
                        "text": d.creatorName
                    }).inject(replyNode)
                    div.addEvents({
                        "mouseover": function () {
                            this.node.setStyles(this.obj.css.documentItemReplyPersonNode_list_over)
                        }.bind({node: div, obj: this}),
                        "mouseout": function () {
                            this.node.setStyles(this.obj.css.documentItemReplyPersonNode_list)
                        }.bind({node: div, obj: this}),
                        "click" : function(){
                            this.obj.app.openPerson( this.userName );
                        }.bind( {userName : d.creatorName, obj:this} )
                    })
                }.bind(this))
            }.bind(this), 6)
        }


    },
    _getListData: function (callback, count) {
        if (!count)count = 6;
        var filterData = {
            "sectionId": this.data.id
        }
        this.actions.listSubjectFilterPage(1, count, filterData, function (json) {
            if (!json.data)json.data = [];
            if (callback)callback(json);
        }.bind(this))
    },
    removeCenterWork: function (itemData) {
        //if(isAdmin){
        //    return true;
        //}
        return false;
    },
    openSection : function( el ){
        var appId = "ForumSection"+ this.data.id;
        if (this.app.desktop.apps[appId]){
            this.app.desktop.apps[appId].setCurrent();
        }else {
            this.app.desktop.openApplication(el, "ForumSection", {
                "sectionId": this.data.id,
                "appId": appId
            });
        }
    }
})


MWF.xApplication.Forum.Main.ImageView = new Class({
    Extends: MWF.xApplication.Template.Explorer.ComplexView,
    _createDocument: function (data, index) {
        if( this.explorer.access.isSectionViewer( data ) ){
            return new MWF.xApplication.Forum.Main.ImageDocument(this.viewNode, data, this.explorer, this, null, index);
        }
    },

    _getCurrentPageData: function (callback, count) {
        if (!count)count = 20;
        var id = (this.items.length) ? this.items[this.items.length - 1].data.id : "(0)";
        var filter = this.filterData || {};
        this.actions.listSection(this.options.categoryId, function (json) {
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

})

MWF.xApplication.Forum.Main.ImageDocument = new Class({
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
        var personNode = itemNode.getElements("[item='moderatorNames']")[0];
        this.app.createPersonNode( personNode, itemData.moderatorNames )

        var _self = this;
        if( (this.index + 1) % 2 == 0 ){
            itemNode.setStyle("margin-right" , "0px" );
        }

        var imageNode = itemNode.getElements("[styles='documentItemLeftImage']")[0];
        var filterData = {
            "sectionId": this.data.id,
            "needPicture" : true
        }
        this.actions.listSubjectFilterPage(1, 1,  filterData, function( json ){
            if( json.data ){
                var d = json.data[0];
                this.node.set("title", d.title);
                this.node.set("src", d.pictureBase64 );
                this.node.addEvents({
                    "click": function () {
                        var appId = "ForumDocument" + this.da.id;
                        if (_self.app.desktop.apps[appId]) {
                            _self.app.desktop.apps[appId].setCurrent();
                        } else {
                            _self.app.desktop.openApplication(null, "ForumDocument", {
                                "sectionId": this.da.sectionId,
                                "id": this.da.id,
                                "appId": appId,
                                "isEdited": false,
                                "isNew": false
                            });
                        }
                    }.bind({da: d})
                })
            }
        }.bind({ node : imageNode }))

        var listNode = itemNode.getElements("[styles='documentItemListNode']")[0];
        if (listNode) {
            this._getListData(function (json) {
                json.data.each(function (d,i) {
                    var div = new Element("div", {
                        "styles": this.css.documentItemListItemNode,
                        "text": d.title,
                        "title": d.title
                    }).inject(listNode)
                    div.addEvents({
                        "mouseover": function () {
                            this.node.setStyles(this.obj.css.documentItemListItemNode_over)
                        }.bind({node: div, obj: this}),
                        "mouseout": function () {
                            this.node.setStyles(this.obj.css.documentItemListItemNode)
                        }.bind({node: div, obj: this}),
                        "click" : function(){
                            var appId = "ForumDocument"+this.da.id;
                            if (this.obj.app.desktop.apps[appId]){
                                this.obj.app.desktop.apps[appId].setCurrent();
                            }else {
                                this.obj.app.desktop.openApplication(null, "ForumDocument", {
                                    "sectionId" : this.da.sectionId,
                                    "id" : this.da.id,
                                    "appId": appId,
                                    "isEdited" : false,
                                    "isNew" : false,
                                    "index" : i
                                });
                            }
                        }.bind({da: d, obj: this})
                    })
                }.bind(this))
            }.bind(this), 6)
        }


    },
    _getListData: function (callback, count) {
        if (!count)count = 6;
        var filterData = {
            "sectionId": this.data.id
        }
        this.actions.listSubjectFilterPage(1, count, filterData, function (json) {
            if (!json.data)json.data = [];
            if (callback)callback(json);
        }.bind(this))
    },
    removeCenterWork: function (itemData) {
        //if(isAdmin){
        //    return true;
        //}
        return false;
    },
    openSection : function( el ){
        var appId = "ForumSection"+ this.data.id;
        if (this.app.desktop.apps[appId]){
            this.app.desktop.apps[appId].setCurrent();
        }else {
            this.app.desktop.openApplication(el, "ForumSection", {
                "sectionId": this.data.id,
                "appId": appId
            });
        }
    }
})

var getDateDiff = function (publishTime) {
    var dateTimeStamp = Date.parse(publishTime.replace(/-/gi, "/"));
    var minute = 1000 * 60;
    var hour = minute * 60
    var day = hour * 24;
    var halfamonth = day * 15;
    var month = day * 30;
    var year = month * 12;
    var now = new Date().getTime();
    var diffValue = now - dateTimeStamp;
    if (diffValue < 0) {
        //若日期不符则弹出窗口告之
        //alert("结束日期不能小于开始日期！");
    }
    var yesterday = new Date().decrement('day', 1);
    var beforYesterday = new Date().decrement('day', 2);
    var yearC = diffValue / year;
    var monthC = diffValue / month;
    var weekC = diffValue / (7 * day);
    var dayC = diffValue / day;
    var hourC = diffValue / hour;
    var minC = diffValue / minute;
    if (yesterday.getFullYear() == dateTimeStamp.getFullYear() && yesterday.getMonth() == dateTimeStamp.getMonth() && yesterday.getDate() == dateTimeStamp.getDate()) {
        result = "昨天 " + dateTimeStamp.getHours() + ":" + dateTimeStamp.getMinutes();
    } else if (beforYesterday.getFullYear() == dateTimeStamp.getFullYear() && beforYesterday.getMonth() == dateTimeStamp.getMonth() && beforYesterday.getDate() == dateTimeStamp.getDate()) {
        result = "前天 " + dateTimeStamp.getHours() + ":" + dateTimeStamp.getMinutes();
    } else if (yearC > 1) {
        result = dateTimeStamp.getFullYear() + "年" + (dateTimeStamp.getMonth() + 1) + "月" + dateTimeStamp.getDate() + "日";
    } else if (monthC >= 1) {
        //result= parseInt(monthC) + "个月前";
        // s.getFullYear()+"年";
        result = (dateTimeStamp.getMonth() + 1) + "月" + dateTimeStamp.getDate() + "日";
    } else if (weekC >= 1) {
        result = parseInt(weekC) + "周前";
    } else if (dayC >= 1) {
        result = parseInt(dayC) + "天前";
    } else if (hourC >= 1) {
        result = parseInt(hourC) + "小时前";
    } else if (minC >= 1) {
        result = parseInt(minC) + "分钟前";
    } else
        result = "刚刚发表";
    return result;
};


