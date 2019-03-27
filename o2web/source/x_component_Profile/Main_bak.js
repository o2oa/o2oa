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
        "mvcStyle": "view.css",
        "title": MWF.xApplication.Profile.LP.title
    },
    onQueryLoad: function(){
        this.lp = MWF.xApplication.Profile.LP;
    },
    loadApplication: function(callback){


        this.loadTitle();
        this.loadContent();
        if (callback) callback();
    },

    loadTitle: function(){
        this.loadTitleBar();
        this.loadTitleUserNode();
        this.loadTitleTextNode();
    },

    loadTitleBar: function(){
        this.titleBar = new Element("div", {
            "styles": this.css.titleBar
        }).inject(this.content);
    },
    loadTitleUserNode: function(){
        this.titleUserNode = new Element("div", {
            "styles": this.css.titleUserNode
        }).inject(this.titleBar);
        this.titleUserIconNode = new Element("div", {
            "styles": this.css.titleUserIconNode
        }).inject(this.titleUserNode);
        this.titleUserTextNode = new Element("div", {
            "styles": this.css.titleUserTextNode,
            "text": this.desktop.session.user.name
        }).inject(this.titleUserNode);
    },
    loadTitleTextNode: function(){
        this.taskTitleTextNode = new Element("div", {
            "styles": this.css.titleTextNode,
            "text": this.lp.title
        }).inject(this.titleBar);
    },

    loadContent: function(){
        this.contentNode = new Element("div", {"styles": this.css.contentNode}).inject(this.content);
        MWF.require("MWF.widget.Tab", function(){
            this.tab = new MWF.widget.Tab(this.contentNode, {"style": "profile"});
            this.tab.load();

            this.loadInforConfigNode();
            this.loadLayoutConfigNode();
            this.loadIdeaConfigNode();
            this.loadPasswordConfigNode();
            this.loadSSOConfigNode();

            this.inforConfigPage = this.tab.addTab(this.inforConfigNode, this.lp.inforConfig);
            this.layoutConfigPage = this.tab.addTab(this.layoutConfigNode, this.lp.layoutConfig);
            this.ideaConfigPage = this.tab.addTab(this.ideaConfigNode, this.lp.ideaConfig);
            this.passwordConfigPage = this.tab.addTab(this.passwordConfigNode, this.lp.passwordConfig);
            this.ssoConfigPage = this.tab.addTab(this.ssoConfigNode, this.lp.ssoConfig);

            if (this.options.tab){
                this[this.options.tab].showIm();
            }else{
                this.inforConfigPage.showIm();
            }

        }.bind(this));
    },
    loadInforConfigNode: function(){
        this.inforConfigNode = new Element("div", {"styles": this.css.configNode}).inject(this.content);
        this.inforConfigAreaNode = new Element("div", {"styles": this.css.inforConfigAreaNode}).inject(this.inforConfigNode);

        this.getAction(function(){
            this.action.getPerson(function(json){
                this.personData = json.data;

                var _self = this;

                var lineNode = new Element("div", {"styles": this.css.inforLineNode}).inject(this.inforConfigAreaNode);
                var titleNode = new Element("div", {"styles": this.css.inforIconTitleNode, "text": this.lp.icon}).inject(lineNode);
                var contentNode = new Element("div", {"styles": this.css.inforIconContentNode}).inject(lineNode);
                this.contentImgNode = new Element("img", {"styles": this.css.inforIconContentImgNode, "src": this.action.getPersonIcon()}).inject(contentNode);
                var actionNode = new Element("div", {"styles": this.css.inforChangeIconNode, "text": this.lp.changeIcon, "events": {
                    "click": function(){this.changeIcon();}.bind(this)
                }}).inject(lineNode);

                var lineNode = new Element("div", {"styles": this.css.inforLineNode}).inject(this.inforConfigAreaNode);
                var titleNode = new Element("div", {"styles": this.css.inforTitleNode, "text": this.lp.name}).inject(lineNode);
                var contentNode = new Element("div", {"styles": this.css.inforContentNode, "text": json.data.name}).inject(lineNode);

                var lineNode = new Element("div", {"styles": this.css.inforLineNode}).inject(this.inforConfigAreaNode);
                var titleNode = new Element("div", {"styles": this.css.inforTitleNode, "text": this.lp.employee}).inject(lineNode);
                var contentNode = new Element("div", {"styles": this.css.inforContentNode, "text": json.data.employee}).inject(lineNode);

                var lineNode = new Element("div", {"styles": this.css.inforLineNode}).inject(this.inforConfigAreaNode);
                var titleNode = new Element("div", {"styles": this.css.inforTitleNode, "text": this.lp.mail}).inject(lineNode);
                var contentNode = new Element("div", {"styles": this.css.inforContentNode}).inject(lineNode);
                this.mailInputNode = new Element("input", {"styles": this.css.inforContentInputNode, "value": json.data.mail, "events": {
                    "blur": function(){this.setStyles(_self.css.inforContentInputNode);},
                    "focus": function(){this.setStyles(_self.css.inforContentInputNode_focus);}
                }}).inject(contentNode);

                var lineNode = new Element("div", {"styles": this.css.inforLineNode}).inject(this.inforConfigAreaNode);
                var titleNode = new Element("div", {"styles": this.css.inforTitleNode, "text": this.lp.mobile}).inject(lineNode);
                var contentNode = new Element("div", {"styles": this.css.inforContentNode}).inject(lineNode);
                this.mobileInputNode = new Element("input", {"styles": this.css.inforContentInputNode, "value": json.data.mobile, "events": {
                    "blur": function(){this.setStyles(_self.css.inforContentInputNode);},
                    "focus": function(){this.setStyles(_self.css.inforContentInputNode_focus);}
                }}).inject(contentNode);

                var lineNode = new Element("div", {"styles": this.css.inforLineNode}).inject(this.inforConfigAreaNode);
                var titleNode = new Element("div", {"styles": this.css.inforTitleNode, "text": this.lp.officePhone}).inject(lineNode);
                var contentNode = new Element("div", {"styles": this.css.inforContentNode}).inject(lineNode);
                this.officePhoneInputNode = new Element("input", {"styles": this.css.inforContentInputNode, "value": json.data.officePhone, "events": {
                    "blur": function(){this.setStyles(_self.css.inforContentInputNode);},
                    "focus": function(){this.setStyles(_self.css.inforContentInputNode_focus);}
                }}).inject(contentNode);

                var lineNode = new Element("div", {"styles": this.css.inforLineNode}).inject(this.inforConfigAreaNode);
                var titleNode = new Element("div", {"styles": this.css.inforTitleNode, "text": this.lp.weixin}).inject(lineNode);
                var contentNode = new Element("div", {"styles": this.css.inforContentNode}).inject(lineNode);
                this.weixinInputNode = new Element("input", {"styles": this.css.inforContentInputNode, "value": json.data.weixin, "events": {
                    "blur": function(){this.setStyles(_self.css.inforContentInputNode);},
                    "focus": function(){this.setStyles(_self.css.inforContentInputNode_focus);}
                }}).inject(contentNode);

                var lineNode = new Element("div", {"styles": this.css.inforLineNode}).inject(this.inforConfigAreaNode);
                var titleNode = new Element("div", {"styles": this.css.inforTitleNode, "text": this.lp.QQ}).inject(lineNode);
                var contentNode = new Element("div", {"styles": this.css.inforContentNode}).inject(lineNode);
                this.qqInputNode = new Element("input", {"styles": this.css.inforContentInputNode, "value": json.data.qq, "events": {
                    "blur": function(){this.setStyles(_self.css.inforContentInputNode);},
                    "focus": function(){this.setStyles(_self.css.inforContentInputNode_focus);}
                }}).inject(contentNode);

                var lineNode = new Element("div", {"styles": this.css.inforLineNode}).inject(this.inforConfigAreaNode);
                var titleNode = new Element("div", {"styles": this.css.inforTitleNode, "text": this.lp.signature}).inject(lineNode);
                var contentNode = new Element("div", {"styles": this.css.inforContentNode}).inject(lineNode);
                this.signatureInputNode = new Element("input", {"styles": this.css.inforContentInputNode, "value": json.data.signature, "events": {
                    "blur": function(){this.setStyles(_self.css.inforContentInputNode);},
                    "focus": function(){this.setStyles(_self.css.inforContentInputNode_focus);}
                }}).inject(contentNode);

                var lineNode = new Element("div", {"styles": this.css.inforLineNode}).inject(this.inforConfigAreaNode);
                this.saveAction = new Element("div", {"styles": this.css.saveAction, "text": this.lp.saveInfor}).inject(lineNode);
                this.saveAction.addEvent("click", function(){
                    this.savePersonInfor();
                }.bind(this));


            }.bind(this), null, this.desktop.session.user.name)
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


                // this.explorer.actions.changePersonIcon(this.data.id ,function(){
                //     this.iconNode.set("src", "");
                //     if (this.item.iconNode) this.item.iconNode.getElement("img").set("src", "");
                //     window.setTimeout(function(){
                //         this.iconNode.set("src", this._getIcon(true));
                //         if (this.item.iconNode) this.item.iconNode.getElement("img").set("src", this.item._getIcon(true));
                //     }.bind(this), 100);
                // }.bind(this), null, this.image.getFormData(), this.image.resizedImage);
            }
        }
    },

    // changeIcon: function(){
    //     if (!this.uploadFileAreaNode){
    //         this.uploadFileAreaNode = new Element("div");
    //         var html = "<input name=\"file\" type=\"file\"/>";
    //         this.uploadFileAreaNode.set("html", html);
    //
    //         this.fileUploadNode = this.uploadFileAreaNode.getFirst();
    //         this.fileUploadNode.addEvent("change", function(){
    //
    //             var files = fileNode.files;
    //             if (files.length){
    //                 for (var i = 0; i < files.length; i++) {
    //                     var file = files.item(i);
    //
    //                     var formData = new FormData();
    //                     formData.append('file', file);
    //                     //formData.append('name', file.name);
    //                     //formData.append('folder', folderId);
    //                     this.action.changeIcon(function(){
    //                         this.action.getPerson(function(json){
    //                             if (json.data){
    //                                 this.personData = json.data;
    //                                 //if (this.personData.icon){
    //                                     this.contentImgNode.set("src", this.action.getPersonIcon());
    //                                 //}
    //                             }
    //                         }.bind(this))
    //                     }.bind(this), null, formData, file);
    //                 }
    //             }
    //
    //         }.bind(this));
    //     }
    //     var fileNode = this.uploadFileAreaNode.getFirst();
    //     fileNode.click();
    // },
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
    loadLayoutConfigNode: function(){
        this.layoutConfigNode = new Element("div", {"styles": this.css.configNode}).inject(this.content);

        new Element("div", {"styles": this.css.layoutTitleNode, "text": this.lp.layoutAction}).inject(this.layoutConfigNode);

        var buttonNode = new Element("div", {"styles": this.css.buttonNodeArea}).inject(this.layoutConfigNode);

        this.clearDataAction = new Element("div", {"styles": this.css.clearDataAction, "text": this.lp.clear}).inject(buttonNode);
        this.clearDataAction.addEvent("click", function(){
            MWF.require("MWF.widget.UUID", function(){
                //MWF.UD.putData("layout", {}, function(){
                //    this.notice(this.lp.clearok, "success");
                //    this.desktop.notRecordStatus = true;
                //}.bind(this));

                MWF.UD.deleteData("layout", function(){
                    this.notice(this.lp.clearok, "success");
                    this.desktop.notRecordStatus = true;
                }.bind(this));

            }.bind(this));
        }.bind(this));

        if (MWF.AC.isAdministrator()){
            var defaultNode = new Element("div", {"styles": {"overflow":"hidden", "clear": "left"}}).inject(buttonNode);
            this.defaultDataAction = new Element("div", {"styles": this.css.setDefaultDataAction, "text": this.lp.setDefault}).inject(defaultNode);
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

            this.clearDefaultDataAction = new Element("div", {"styles": this.css.setDefaultDataAction, "text": this.lp.clearDefault}).inject(defaultNode);
            this.clearDefaultDataAction.addEvent("click", function(){
                MWF.require("MWF.widget.UUID", function(){
                    MWF.UD.deletePublicData("defaultLayout", function(){
                        this.notice(this.lp.clearok, "success");
                        this.desktop.notRecordStatus = true;
                    }.bind(this));
                    //
                    // var text = this.lp.setDefaultOk;
                    // this.close();
                    // var status = layout.desktop.getLayoutStatusData();
                    // MWF.UD.putPublicData("defaultLayout", status, function(){
                    //     MWF.xDesktop.notice("success", {"x": "right", "y": "top"}, text, layout.desktop.desktopNode);
                    // }.bind(this));
                }.bind(this));
            }.bind(this));


            //var tmpNode = new Element("div", {"styles": {
            //    "width": "300px",
            //    "margin": "-20px auto"
            //}}).inject(this.layoutConfigNode);

            this.forceDataAction = new Element("div", {"styles": this.css.setDefaultDataAction, "text": this.lp.setForce}).inject(buttonNode);
            this.forceDataAction.setStyle("float", "left");
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

            this.deleteForceDataAction = new Element("div", {"styles": this.css.setDefaultDataAction, "text": this.lp.clearForce}).inject(buttonNode);
            this.deleteForceDataAction.addEvent("click", function(){
                MWF.require("MWF.widget.UUID", function(){
                    MWF.UD.deletePublicData("forceLayout", function(){
                        this.notice(this.lp.clearok, "success");
                        this.desktop.notRecordStatus = true;
                    }.bind(this));
                }.bind(this));
            }.bind(this));
        }

        new Element("div", {"styles": this.css.layoutTitleNode, "text": this.lp.desktopBackground}).inject(this.layoutConfigNode);
        var UINode = new Element("div", {"styles": this.css.buttonNodeArea}).inject(this.layoutConfigNode);
        this.loadDesktopBackground(UINode);
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
                var imgArea = new Element("div", {"styles": this.css.previewBackground}).inject(UINode);
                if (currentSrc==style.style){
                    imgArea.setStyles({"border": "4px solid #ffea00"});
                }
                new Element("img", {"src": img}).inject(imgArea);

                imgArea.store("dskimg", style.style);
                var _self = this;
                imgArea.addEvent("click", function(){ _self.selectDesktopImg(this, UINode); });
            }.bind(this));
        }.bind(this));

        //MWF.UD.getPublicData("layoutDesktopImgs", function(json){
        //    if (json) currentSrc = json.src;
        //}.bind(this), false);
        //
        if (MWF.AC.isAdministrator()){

        }
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

    loadIdeaConfigNode: function(){
        this.ideaConfigNode = new Element("div", {"styles": this.css.configNode}).inject(this.content);
        this.ideasArea = new Element("textarea", {"styles": this.css.ideasArea}).inject(this.ideaConfigNode);
        this.ideasSaveAction = new Element("div", {"styles": this.css.ideasSaveAction, "text": this.lp.saveIdea}).inject(this.ideaConfigNode);

        if (MWF.AC.isAdministrator()){
            this.ideasSaveDefaultAction = new Element("div", {"styles": this.css.ideasSaveAction, "text": this.lp.saveIdeaDefault}).inject(this.ideaConfigNode);
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
    loadPasswordConfigNode: function(){
        this.passwordConfigNode = new Element("div", {"styles": this.css.configNode});
        this.passwordConfigAreaNode = new Element("div", {"styles": this.css.inforConfigAreaNode}).inject(this.passwordConfigNode);

        var _self = this;
        var lineNode = new Element("div", {"styles": this.css.inforLineNode}).inject(this.passwordConfigAreaNode);
        var titleNode = new Element("div", {"styles": this.css.inforTitleNode, "text": this.lp.oldPassword}).inject(lineNode);
        var contentNode = new Element("div", {"styles": this.css.inforContentNode}).inject(lineNode);
        this.oldPasswordInputNode = new Element("input", {"type": "password", "styles": this.css.inforContentInputNode, "events": {
            "blur": function(){this.setStyles(_self.css.inforContentInputNode);},
            "focus": function(){this.setStyles(_self.css.inforContentInputNode_focus);}
        }}).inject(contentNode);

        var lineNode = new Element("div", {"styles": this.css.inforLineNode}).inject(this.passwordConfigAreaNode);
        var titleNode = new Element("div", {"styles": this.css.inforTitleNode, "text": this.lp.password}).inject(lineNode);
        var contentNode = new Element("div", {"styles": this.css.inforContentNode}).inject(lineNode);
        this.passwordInputNode = new Element("input", {"type": "password", "styles": this.css.inforContentInputNode, "events": {
            "blur": function(){
                this.setStyles(_self.css.inforContentInputNode);
            },
            "focus": function(){this.setStyles(_self.css.inforContentInputNode_focus);},
            "keyup" : function(){ this.checkPassowrdStrength(  this.passwordInputNode.get("value") ) }.bind(this)
        }}).inject(contentNode);

        var lineNode = new Element("div", {"styles": this.css.inforLineNode}).inject(this.passwordConfigAreaNode);
        var titleNode = new Element("div", {"styles": this.css.inforTitleNode}).inject(lineNode);
        this.passwordRemindContainer = new Element("div", {"styles": this.css.inforContentNode}).inject(lineNode);

        var lineNode = new Element("div", {"styles": this.css.inforLineNode}).inject(this.passwordConfigAreaNode);
        var titleNode = new Element("div", {"styles": this.css.inforTitleNode, "text": this.lp.morePassword}).inject(lineNode);
        var contentNode = new Element("div", {"styles": this.css.inforContentNode}).inject(lineNode);
        this.morePasswordInputNode = new Element("input", {"type": "password", "styles": this.css.inforContentInputNode, "events": {
            "blur": function(){this.setStyles(_self.css.inforContentInputNode);},
            "focus": function(){this.setStyles(_self.css.inforContentInputNode_focus);}
        }}).inject(contentNode);

        var lineNode = new Element("div", {"styles": this.css.inforLineNode}).inject(this.passwordConfigAreaNode);
        this.saveAction = new Element("div", {"styles": this.css.saveAction, "text": this.lp.passwordConfig}).inject(lineNode);
        this.saveAction.addEvent("click", function(){
            this.changePassword();
        }.bind(this));

        this.createPasswordStrengthNode();
        this.passworRemindNode = new Element("div",{"styles": this.css.passwordRemindNode, "text": this.lp.paswordRule }).inject( this.passwordRemindContainer );

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
            // MWF.xDesktop.requireApp("Profile", "Actions.RestActions", function(){
            //     this.action = new MWF.xApplication.Profile.Actions.RestActions();
            //     if (callback) callback();
            // }.bind(this));
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
        this.lowColorNode.setStyles( this.css.passwordStrengthColor );
        this.lowTextNode.setStyles( this.css.passwordStrengthText );
        this.middleColorNode.setStyles( this.css.passwordStrengthColor );
        this.middleTextNode.setStyles( this.css.passwordStrengthText );
        this.highColorNode.setStyles( this.css.passwordStrengthColor );
        this.highTextNode.setStyles( this.css.passwordStrengthText );
        if (pwd==null||pwd==''){
        }else{
            this.getPasswordLevel( pwd, function( level ){
                switch(level) {
                    case 0:
                    case 1:
                    case 2:
                    case 3:
                        this.lowColorNode.setStyles( this.css.passwordStrengthColor_low );
                        this.lowTextNode.setStyles( this.css.passwordStrengthText_current );
                        break;
                    case 4:
                    case 5:
                    case 6:
                        this.middleColorNode.setStyles( this.css.passwordStrengthColor_middle );
                        this.middleTextNode.setStyles( this.css.passwordStrengthText_current );
                        break;
                    default:
                        this.highColorNode.setStyles( this.css.passwordStrengthColor_high );
                        this.highTextNode.setStyles( this.css.passwordStrengthText_current );
                }
            }.bind(this) )

        }
    },
    loadSSOConfigNode: function(){
        this.ssoConfigNode = new Element("div", {"styles": this.css.configNode}).inject(this.content);
        this.ssoConfigTitleNode = new Element("div", {"styles": this.css.ssoConfigTitleNode, "text": this.lp.bindOauth}).inject(this.ssoConfigNode);
        this.ssoConfigAreaNode = new Element("div", {"styles": {"padding": "10px 40px"}}).inject(this.ssoConfigNode);

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


        // this.ideasArea = new Element("textarea", {"styles": this.css.ideasArea}).inject(this.ideaConfigNode);
        // this.ideasSaveAction = new Element("div", {"styles": this.css.ideasSaveAction, "text": this.lp.saveIdea}).inject(this.ideaConfigNode);
        //
        // if (MWF.AC.isAdministrator()){
        //     this.ideasSaveDefaultAction = new Element("div", {"styles": this.css.ideasSaveAction, "text": this.lp.saveIdeaDefault}).inject(this.ideaConfigNode);
        //     this.ideasSaveDefaultAction.addEvent("click", function(){
        //         MWF.require("MWF.widget.UUID", function(){
        //             var data = {};
        //             data.ideas = this.ideasArea.get("value").split("\n");
        //             MWF.UD.putPublicData("idea", data, function(){
        //                 this.notice(this.lp.ideaSaveOk, "success");
        //             }.bind(this));
        //         }.bind(this));
        //     }.bind(this))
        // }
        //
        // MWF.require("MWF.widget.UUID", function(){
        //     MWF.UD.getDataJson("idea", function(json){
        //         if (json){
        //             if (json.ideas) this.ideasArea.set("value", json.ideas.join("\n"));
        //         }
        //     }.bind(this));
        // }.bind(this));
        //
        // this.ideasSaveAction.addEvent("click", function(){
        //     MWF.require("MWF.widget.UUID", function(){
        //         var data = {};
        //         data.ideas = this.ideasArea.get("value").split("\n");
        //         MWF.UD.putData("idea", data, function(){
        //             this.notice(this.lp.ideaSaveOk, "success");
        //         }.bind(this));
        //     }.bind(this));
        // }.bind(this))
    }
});
