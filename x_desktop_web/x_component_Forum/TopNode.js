/**
 * Created by CXY on 2017/5/8.
 */
MWF.xApplication.Forum.TopNode = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],
    options: {
        "style": "default",
        "settingEnable" : false
    },
    initialize : function( container, app, explorer, options ){
        this.setOptions(options);
        this.container = container;
        this.app = app;
        this.lp = app.lp;
        this.restActions = app.restActions;
        this.access = app.access;
        this.explorer = explorer;
        this.userName = layout.desktop.session.user.name;

        this.path = "/x_component_Forum/$TopNode/";

        this.cssPath = "/x_component_Forum/$TopNode/" + this.options.style + "/css.wcss";
        this._loadCss();
    },
    load: function(){
        this.createTopNode();
    },
    openMainPage : function(){
        var appId = "Forum";
        if (this.app.desktop.apps[appId]){
            var app = this.app.desktop.apps[appId];
            app.setCurrent();
            app.clearContent();
            app.loadApplicationLayout();
        }else {
            this.app.desktop.openApplication(null, "Forum", { "appId": appId });
            this.app.close();
        }
    },
    createTopNode: function () {
        this.topContainerNode = new Element("div.topContainerNode", {
            "styles": this.css.topContainerNode
        }).inject(this.container);

        this.topNode = new Element("div.topNode", {
            "styles": this.css.topNode
        }).inject(this.topContainerNode);

        this.topMainPageNode = new Element("div.topMainPageNode",{
            "styles" : { "cursor" : "pointer" }
        }).inject(this.topNode);
        this.topMainPageNode.addEvent("click", function(){
            this.openMainPage();
        }.bind(this));

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
                //this.signupIconNode = new Element("div", {
                //    "styles": this.css.signupIconNode
                //}).inject(this.signupNode);
                this.signupTextNode = new Element("div", {
                    "styles": this.css.signupTextNode,
                    "text": this.lp.signup
                }).inject(this.signupNode);
                this.signupNode.addEvent("click", function(){ this.openSignUpForm( ) }.bind(this))
            }

            new Element("div",{
                "styles" : this.css.topSepNode,
                "text" : "/"
            }).inject(this.topContentNode);

            this.loginNode = new Element("div", {
                "styles": this.css.loginNode
            }).inject(this.topContentNode);
            //this.loginIconNode = new Element("div", {
            //    "styles": this.css.loginIconNode
            //}).inject(this.loginNode);
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
                //this.logoutIconNode = new Element("div", {
                //    "styles": this.css.logoutIconNode
                //}).inject(this.logoutNode);
                this.logoutTextNode = new Element("div", {
                    "styles": this.css.logoutTextNode,
                    "text": this.lp.logout
                }).inject(this.logoutNode);
                this.logoutNode.addEvent("click", function(){ this.logout( ) }.bind(this))

                new Element("div",{
                    "styles" : this.css.topSepNode,
                    "text" : "/"
                }).inject(this.topContentNode);
            }

            if( this.options.settingEnable ){
                this.settingNode = new Element("div", {
                    "styles": this.css.settingNode
                }).inject(this.topContentNode);
                //this.settingIconNode = new Element("div", {
                //    "styles": this.css.settingIconNode
                //}).inject(this.settingNode);
                this.settingTextNode = new Element("div", {
                    "styles": this.css.settingTextNode,
                    "text": this.lp.setting
                }).inject(this.settingNode);
                this.settingNode.addEvent("click", function(){ this.app.openSetting( ) }.bind(this));
            }


            if( this.options.settingEnable ) {
                new Element("div", {
                    "styles": this.css.topSepNode,
                    "text": "/"
                }).inject(this.topContentNode);
            }

            this.personNode = new Element("div", {
                "styles": this.css.personNode
            }).inject(this.topContentNode);
            //this.personIconNode = new Element("div", {
            //    "styles": this.css.personIconNode
            //}).inject(this.personNode);
            this.personTextNode = new Element("div", {
                "styles": this.css.personTextNode,
                "text": this.lp.personCenter
            }).inject(this.personNode);
            this.personNode.addEvent("click", function(){ this.openPerson(this.userName ) }.bind(this))
        }


        this.searchDiv = new Element("div.searchDiv",{
            "styles" : this.css.searchDiv
        }).inject(this.topNode);
        this.searchInput = new Element("input.searchInput",{
            "styles" : this.css.searchInput,
            "value" : this.lp.searchKey,
            "title" : this.lp.searchTitle
        }).inject(this.searchDiv);
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
                this.searchInput.setStyles( this.css.searchInput_over );
                this.searchAction.setStyles( this.css.searchAction_over )
            }.bind(this),
            "mouseout" : function(){
                this.searchInput.setStyles( this.css.searchInput );
                this.searchAction.setStyles( this.css.searchAction )
            }.bind(this)
        });


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
        if (this.app.desktop.apps[appId] && !this.inBrowser){
            this.app.desktop.apps[appId].close();
        }
        this.app.desktop.openApplication(null, "ForumSearch", {
            "appId": appId,
            "searchContent" : val
        });
    },
    openPerson : function( userName ){
        var appId = "ForumPerson"+userName;
        if (this.app.desktop.apps[appId]){
            this.app.desktop.apps[appId].setCurrent();
        }else {
            this.app.desktop.openApplication(null, "ForumPerson", {
                "personName" : userName,
                "appId": appId
            });
        }
    }
});