MWF.xApplication.Profile.options.multitask = false;
MWF.xApplication.Profile.Main = new Class({
    Extends: MWF.xApplication.Common.Main,
    Implements: [Options, Events],

    options: {
        "style": "default",
        "name": "Profile",
        "icon": "icon.png",
        "width": "800",
        "height": "600",
        "isResize": false,
        "isMax": false,
        "mvcStyle": "style.css",
        "title": MWF.xApplication.Profile.LP.title
    },
    _loadCss: function(){},
    loadCss: function(file, callback){
        var path = (file && typeOf(file)==="string") ? file : "style.css";
        var cb = (file && typeOf(file)==="function") ? file : callback;
        var cssPath = this.path+this.options.style+"/"+path;
        this.content.loadCss(cssPath, cb);
    },
    onQueryLoad: function(){
        this.lp = MWF.xApplication.Profile.LP;
        this.action = MWF.Actions.get("x_organization_assemble_personal");
    },
    loadApplication: function(callback){
        this.action.getPerson(function(json){
            this.personData = json.data;
            this.personData.personIcon = this.action.getPersonIcon();

            this.content.loadHtml(this.path+this.options.style+"/"+((this.inBrowser)? "viewBrowser": "view")+".html", {"bind": {"data": this.personData, "lp": this.lp}}, function(){
                this.loadContent()
            }.bind(this));
        }.bind(this));
        //this.loadCss();

        // this.loadTitle();
        // this.loadContent();
        if (callback) callback();
    },

    loadContent: function(){
        var pageConfigNodes = this.content.getElements(".o2_profile_configNode");

        this.contentNode = this.content.getElement(".o2_profile_contentNode");
        MWF.require("MWF.widget.Tab", function(){
            this.tab = new MWF.widget.Tab(this.contentNode, {"style": "profile"});
            this.tab.load();

            pageConfigNodes.each(function(node){
                this.tab.addTab(node, node.get("title"));
            }.bind(this));

            if (this.options.tab){
                this.tab.pages[this.options.tab].showIm();
            }else{
                this.tab.pages[0].showIm();
            }

            this.loadInforConfigActions();
            if (!this.inBrowser){
                this.loadLayoutConfigActions();
            }else{

            }

            this.loadIdeaConfigActions();
            this.loadPasswordConfigActions();
            this.loadSSOConfigAction();

        }.bind(this));
    },
    loadInforConfigActions: function(){
        this.contentImgNode = this.content.getElement(".o2_profile_inforIconContentImg");
        this.content.getElement(".o2_profile_inforIconChange").addEvent("click", function(){
            this.changeIcon();
        }.bind(this));

        var inputs = this.tab.pages[0].contentNode.getElements("input");
        this.mailInputNode = inputs[0];
        this.mobileInputNode = inputs[1];
        this.officePhoneInputNode = inputs[2];
        this.weixinInputNode = inputs[3];
        this.qqInputNode = inputs[4];
        this.signatureInputNode = inputs[5];

        this.content.getElement(".o2_profile_saveInforAction").addEvent("click", function(){
            this.savePersonInfor();
        }.bind(this));
    },

    loadLayoutConfigActions: function(){
        var buttons = this.tab.pages[1].contentNode.getElements(".o2_profile_layoutClearDataAction");
        this.clearDataAction = buttons[0];
        this.defaultDataAction = (buttons.length>1) ? buttons[1]: null;
        this.clearDefaultDataAction = (buttons.length>2) ? buttons[2]: null;
        this.forceDataAction = (buttons.length>3) ? buttons[3]: null;
        this.deleteForceDataAction = (buttons.length>4) ? buttons[4]: null;

        this.clearDataAction.addEvent("click", function(){
            MWF.require("MWF.widget.UUID", function(){
                MWF.UD.deleteData("layout", function(){
                    this.notice(this.lp.clearok, "success");
                    this.desktop.notRecordStatus = true;
                }.bind(this));
            }.bind(this));
        }.bind(this));

        if( MWF.AC.isAdministrator() ){
            this.defaultDataAction.addEvent("click", function(){
                MWF.require("MWF.widget.UUID", function(){
                    var text = this.lp.setDefaultOk;
                    this.close();
                    var status = layout.desktop.getLayoutStatusData();
                    MWF.UD.putPublicData("defaultLayout", status, function(){
                        MWF.xDesktop.notice("success", {"x": "right", "y": "top"}, text, layout.desktop.desktopNode);
                    }.bind(this));
                }.bind(this));
            }.bind(this));

            this.clearDefaultDataAction.addEvent("click", function(){
                MWF.require("MWF.widget.UUID", function(){
                    MWF.UD.deletePublicData("defaultLayout", function(){
                        this.notice(this.lp.clearok, "success");
                        this.desktop.notRecordStatus = true;
                    }.bind(this));
                }.bind(this));
            }.bind(this));

            this.forceDataAction.addEvent("click", function(){
                MWF.require("MWF.widget.UUID", function(){
                    var text = this.lp.setForceOk;
                    this.close();
                    var status = layout.desktop.getLayoutStatusData();
                    MWF.UD.putPublicData("forceLayout", status, function(){
                        MWF.xDesktop.notice("success", {"x": "right", "y": "top"}, text, layout.desktop.desktopNode);
                    }.bind(this));
                }.bind(this));
            }.bind(this));

            this.deleteForceDataAction.addEvent("click", function(){
                MWF.require("MWF.widget.UUID", function(){
                    MWF.UD.deletePublicData("forceLayout", function(){
                        this.notice(this.lp.clearok, "success");
                        this.desktop.notRecordStatus = true;
                    }.bind(this));
                }.bind(this));
            }.bind(this));
        }


        var UINode = this.tab.pages[1].contentNode.getLast();
        this.loadDesktopBackground(UINode);
    },

    loadIdeaConfigActions: function(){
        var i = (this.inBrowser)? 1 : 2;
        this.ideasArea = this.tab.pages[i].contentNode.getElement("textarea");
        this.ideasSaveAction = this.ideasArea.getNext();
        this.ideasSaveDefaultAction = this.ideasSaveAction.getNext() || null;

        if (MWF.AC.isAdministrator()){
            this.ideasSaveDefaultAction.addEvent("click", function(){
                MWF.require("MWF.widget.UUID", function(){
                    var data = {};
                    data.ideas = this.ideasArea.get("value").split("\n");
                    MWF.UD.putPublicData("idea", data, function(){
                        this.notice(this.lp.ideaSaveOk, "success");
                    }.bind(this));
                }.bind(this));
            }.bind(this))
        }

        MWF.require("MWF.widget.UUID", function(){
            MWF.UD.getDataJson("idea", function(json){
                if (json){
                    if (json.ideas) this.ideasArea.set("value", json.ideas.join("\n"));
                }
            }.bind(this));
        }.bind(this));

        this.ideasSaveAction.addEvent("click", function(){
            MWF.require("MWF.widget.UUID", function(){
                var data = {};
                data.ideas = this.ideasArea.get("value").split("\n");
                MWF.UD.putData("idea", data, function(){
                    this.notice(this.lp.ideaSaveOk, "success");
                }.bind(this));
            }.bind(this));
        }.bind(this))
    },

    loadPasswordConfigActions: function(){
        var i = (this.inBrowser)? 2 : 3;
        var inputs = this.tab.pages[i].contentNode.getElements("input");
        this.oldPasswordInputNode = inputs[0];
        this.passwordInputNode = inputs[1];
        this.morePasswordInputNode = inputs[2];
        this.savePasswordAction = this.tab.pages[i].contentNode.getElement(".o2_profile_savePasswordAction");

        this.oldPasswordInputNode.addEvents({
            "blur": function(){this.removeClass("o2_profile_inforContentInput_focus");},
            "focus": function(){this.addClass("o2_profile_inforContentInput_focus");}
        });
        this.passwordInputNode.addEvents({
            "blur": function(){this.removeClass("o2_profile_inforContentInput_focus");},
            "focus": function(){this.addClass("o2_profile_inforContentInput_focus");},
            "keyup" : function(){ this.checkPassowrdStrength(  this.passwordInputNode.get("value") ) }.bind(this)
        });
        this.morePasswordInputNode.addEvents({
            "blur": function(){this.removeClass("o2_profile_inforContentInput_focus");},
            "focus": function(){this.addClass("o2_profile_inforContentInput_focus");}
        });
        this.savePasswordAction.addEvent("click", function(){
            this.changePassword();
        }.bind(this));
    },
    loadSSOConfigAction: function(){
        var i = (this.inBrowser)? 3 : 4;
        this.ssoConfigAreaNode = this.tab.pages[i].contentNode.getElement(".o2_profile_ssoConfigArea");
        MWF.Actions.get("x_organization_assemble_authentication").listOauthServer(function(json){
            json.data.each(function(d){
                var node = new Element("a", {
                    "styles": {"font-size": "14px", "display": "block", "margin-bottom": "10px"},
                    "text": d.displayName,
                    "target": "_blank",
                    "href": "/x_desktop/oauth.html?oauth="+encodeURIComponent(d.name)+"&redirect="+"&method=oauthBind"
                }).inject(this.ssoConfigAreaNode)
            }.bind(this));
        }.bind(this));
    },

    changeIcon: function(){
        var options = {};
        var width = "668";
        var height = "510";
        width = width.toInt();
        height = height.toInt();

        var size = this.content.getSize();
        var x = (size.x-width)/2;
        var y = (size.y-height)/2;
        if (x<0) x = 0;
        if (y<0) y = 0;
        if (layout.mobile){
            x = 20;
            y = 0;
        }

        var _self = this;
        MWF.require("MWF.xDesktop.Dialog", function() {
            MWF.require("MWF.widget.ImageClipper", function(){
                var dlg = new MWF.xDesktop.Dialog({
                    "title": this.lp.changePersonIcon,
                    "style": "image",
                    "top": y,
                    "left": x - 20,
                    "fromTop": y,
                    "fromLeft": x - 20,
                    "width": width,
                    "height": height,
                    "html": "<div></div>",
                    "maskNode": this.content,
                    "container": this.content,
                    "buttonList": [
                        {
                            "text": MWF.LP.process.button.ok,
                            "action": function () {
                                //_self.uploadPersonIcon();
                                _self.image.uploadImage( function( json ){
                                    _self.action.getPerson(function(json){
                                        if (json.data){
                                            this.personData = json.data;
                                            _self.contentImgNode.set("src", _self.action.getPersonIcon());
                                        }
                                        this.close();
                                    }.bind(this));
                                }.bind(this), null );
                            }
                        },
                        {
                            "text": MWF.LP.process.button.cancel,
                            "action": function () {
                                _self.image = null;
                                this.close();
                            }
                        }
                    ]
                });
                dlg.show();

                this.image = new MWF.widget.ImageClipper(dlg.content.getFirst(), {
                    "aspectRatio": 1,
                    "description" : "",
                    "imageUrl" : this.action.getPersonIcon(),
                    "resetEnable" : false,

                    "data": null,
                    "parameter": null,
                    "action": this.action.action,
                    "method": "changeIcon"
                });
                this.image.load();
            }.bind(this));
        }.bind(this))
    },
    uploadPersonIcon: function(){
        if (this.image){
            if( this.image.getResizedImage() ){

                this.action.changeIcon(function(){
                    this.action.getPerson(function(json){
                        if (json.data){
                            this.personData = json.data;
                            //if (this.personData.icon){
                            this.contentImgNode.set("src", this.action.getPersonIcon());
                            //}
                        }
                    }.bind(this))
                }.bind(this), null, this.image.getFormData(), this.image.resizedImage);
            }
        }
    },

    savePersonInfor: function(){
        this.personData.officePhone = this.officePhoneInputNode.get("value");
        this.personData.mail = this.mailInputNode.get("value");
        this.personData.mobile = this.mobileInputNode.get("value");
        this.personData.weixin = this.weixinInputNode.get("value");
        this.personData.qq = this.qqInputNode.get("value");
        this.personData.signature = this.signatureInputNode.get("value");
        this.action.updatePerson(this.personData, function(){
            this.notice(this.lp.saveInforOk, "success");
        }.bind(this));
    },

    loadDesktopBackground: function(UINode){
        var currentSrc = layout.desktop.options.style;
        MWF.UD.getDataJson("layoutDesktop", function(json){
            if (json) currentSrc = json.src;
        }.bind(this), false);

        MWF.getJSON(layout.desktop.path+"styles.json", function(json){
            json.each(function(style){
                var img = MWF.defaultPath+"/xDesktop/$Layout/"+style.style+"/preview.jpg";
                //var dskImg = MWF.defaultPath+"/xDesktop/$Layout/"+style.style+"/desktop.jpg";
                var imgArea = new Element("div.o2_profile_previewBackground").inject(UINode);
                if (currentSrc==style.style){
                    imgArea.setStyles({"border": "4px solid #ffea00"});
                }
                new Element("img", {"src": img}).inject(imgArea);

                imgArea.store("dskimg", style.style);
                var _self = this;
                imgArea.addEvent("click", function(){ _self.selectDesktopImg(this, UINode); });
            }.bind(this));
        }.bind(this));
    },
    selectDesktopImg: function(item, UINode){
        var desktopImg = item.retrieve("dskimg");
        MWF.UD.putData("layoutDesktop", {"src": desktopImg}, function(){
            UINode.getChildren().each(function(node){
                node.setStyles({"border": "4px solid #eeeeee"});
            }.bind(this));
            item.setStyles({"border": "4px solid #ffea00"});

            var dskImg = MWF.defaultPath+"/xDesktop/$Layout/"+desktopImg+"/desktop.jpg";
            layout.desktop.node.setStyle("background-image", "url("+dskImg+")");
        }.bind(this));
    },
    changePassword: function(){
        var oldPassword = this.oldPasswordInputNode.get("value");
        var password = this.passwordInputNode.get("value");
        var morePassword = this.morePasswordInputNode.get("value");

        if (password!=morePassword){
            this.notice(this.lp.passwordNotMatch, "error");
            this.passwordInputNode.setStyles(this.css.inforContentInputNode_error);
            this.morePasswordInputNode.setStyles(this.css.inforContentInputNode_error);
        }else{
            this.action.changePassword(oldPassword, password, morePassword, function(){
                this.oldPasswordInputNode.set("value", "");
                this.passwordInputNode.set("value", "");
                this.morePasswordInputNode.set("value", "");
                this.notice(this.lp.changePasswordOk, "success");
            }.bind(this));

            if (layout.config.mail){
                var url = "http://"+layout.config.mail+"//names.nsf?changepassword&password="+encodeURIComponent(oldPassword)+"&passwordnew="+encodeURIComponent(password)+"&passwordconfirm="+encodeURIComponent(password);
                var iframe = new Element("iframe", {"styles": {"display": "none"}}).inject(this.desktop.desktopNode);
                iframe.set("src", url);
                window.setTimeout(function(){
                    iframe.destroy();
                }.bind(this), 2000);
            }
        }
    },

    getAction: function(callback){
        if (!this.acrion){
            this.action = MWF.Actions.get("x_organization_assemble_personal");
            if (callback) callback();
        }else{
            if (callback) callback();
        }
    },
    createPasswordStrengthNode : function(){
        var passwordStrengthArea = this.passwordRemindContainer;

        var lowNode = new Element( "div", {styles : this.css.passwordStrengthNode }).inject( passwordStrengthArea );
        this.lowColorNode = new Element( "div", {styles : this.css.passwordStrengthColor }).inject( lowNode );
        this.lowTextNode = new Element( "div", {styles : this.css.passwordStrengthText, text : this.lp.weak }).inject( lowNode );

        var middleNode = new Element( "div" , {styles : this.css.passwordStrengthNode }).inject( passwordStrengthArea );
        this.middleColorNode = new Element( "div", {styles : this.css.passwordStrengthColor }).inject( middleNode );
        this.middleTextNode = new Element( "div", {styles : this.css.passwordStrengthText, text : this.lp.middle }).inject( middleNode );

        var highNode = new Element("div", {styles : this.css.passwordStrengthNode }).inject( passwordStrengthArea );
        this.highColorNode = new Element( "div", {styles : this.css.passwordStrengthColor }).inject( highNode );
        this.highTextNode = new Element( "div", {styles : this.css.passwordStrengthText, text : this.lp.high }).inject( highNode );
    },
    getPasswordLevel: function( password, callback ){
        /*Level（级别）
         •0-3 : [easy]
         •4-6 : [midium]
         •7-9 : [strong]
         •10-12 : [very strong]
         •>12 : [extremely strong]
         */
        this.getAction( function( ){
            this.action.checkPassword( password, function( json ){
                if(callback)callback( json.data.value );
            }.bind(this), null, false );
        }.bind(this) );
    },
    checkPassowrdStrength: function(pwd){
        var i = (this.inBrowser)? 2 : 3;
        var passwordStrengthNode = this.tab.pages[i].contentNode.getElement(".o2_profile_passwordStrengthArea");
        var nodes = passwordStrengthNode.getElements(".o2_profile_passwordStrengthColor");
        var lowColorNode = nodes[0];
        var middleColorNode = nodes[1];
        var highColorNode = nodes[2];
        nodes = passwordStrengthNode.getElements(".o2_profile_passwordStrengthText");
        var lowTextNode = nodes[0];
        var middleTextNode = nodes[1];
        var highTextNode = nodes[2];

        lowColorNode.removeClass("o2_profile_passwordStrengthColor_low");
        middleColorNode.removeClass("o2_profile_passwordStrengthColor_middle");
        highColorNode.removeClass("o2_profile_passwordStrengthColor_high");
        lowTextNode.removeClass("o2_profile_passwordStrengthText_current");
        middleTextNode.removeClass("o2_profile_passwordStrengthText_current");
        highTextNode.removeClass("o2_profile_passwordStrengthText_current");

        if (pwd==null||pwd==''){
        }else{
            this.getPasswordLevel( pwd, function( level ){
                switch(level) {
                    case 0:
                    case 1:
                    case 2:
                    case 3:
                        lowColorNode.addClass("o2_profile_passwordStrengthColor_low");
                        lowTextNode.addClass("o2_profile_passwordStrengthText_current");
                        break;
                    case 4:
                    case 5:
                    case 6:
                        middleColorNode.addClass("o2_profile_passwordStrengthColor_middle");
                        middleTextNode.addClass("o2_profile_passwordStrengthText_current");
                        break;
                    default:
                        highColorNode.addClass("o2_profile_passwordStrengthColor_high");
                        highTextNode.addClass("o2_profile_passwordStrengthText_current");
                }
            }.bind(this) )

        }
    }
});