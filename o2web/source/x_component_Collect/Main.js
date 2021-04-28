MWF.xApplication.Collect.options.multitask = false;
MWF.require("MWF.xDesktop.Access", null, false);
MWF.xDesktop.requireApp("Collect", "Actions.RestActions", null, false);
MWF.xApplication.Collect.Main = new Class({
    Extends: MWF.xApplication.Common.Main,
    Implements: [Options, Events],

    options: {
        "style": "default",
        "name": "Collect",
        "icon": "icon.png",
        "width": "400",
        "height": "500",
        "isResize": false,
        "isMax": false,
        "title": MWF.xApplication.Collect.LP.title
    },
    onQueryLoad: function(){
        this.lp = MWF.xApplication.Collect.LP;
        this.action = MWF.Actions.get("x_program_center");
        //this.action = new MWF.xApplication.Collect.Actions.RestActions();
        this.connected = false;
        this.login = false;

        this.connectChecked = false;
        this.loginChecked = false;

    },
    loadWindow: function(isCurrent){
        this.fireAppEvent("queryLoadWindow");
        this.window = new MWF.xDesktop.WindowTransparent(this, {"container": this.desktop.node});
        this.fireAppEvent("loadWindow");
        this.window.show();
        this.content = this.window.content;

        if (isCurrent) this.setCurrent();
        this.fireAppEvent("postLoadWindow");
        this.fireAppEvent("queryLoadApplication");
        this.loadApplication(function(){
            this.fireAppEvent("postLoadApplication");
        }.bind(this));
    },

    loadApplication: function(callback){
        if (!MWF.AC.isAdministrator()){
            try{
                this.close();
            }catch(e){};
        }else{
            this.node = new Element("div", {"styles": this.css.node}).inject(this.content);
            this.setNodeResize();
            this.node.addEvent("selectstart", function(e){e.target.setStyle("-webkit-user-select", "none")}.bind(this));
            this.loadContent();
            this.addEvent("queryclose", function(){
                if (this.registerForm){
                    if (this.registerForm.codeNextTimer){
                        window.clearTimeout(this.registerForm.codeNextTimer);
                    }
                }
            }.bind(this));
        }
    },
    loadContent: function(){
        this.titleAreaNode = new Element("div", {"styles": this.css.titleAreaNode}).inject(this.node);
        this.backNode = new Element("div", {"styles": this.css.backNode}).inject(this.titleAreaNode);
        this.closeNode = new Element("div", {"styles": this.css.closeNode}).inject(this.titleAreaNode);
        this.closeNode.addEvent("click", function(){this.close();}.bind(this));

        this.titleNode = new Element("div", {"styles": this.css.titleNode, "text": this.lp.title}).inject(this.titleAreaNode);
        this.checkContentNode = new Element("div", {"styles": this.css.contentNode}).inject(this.node);
        this.loginContentNode = new Element("div", {"styles": this.css.contentNode}).inject(this.node);
        this.modifyContentNode = new Element("div", {"styles": this.css.contentNode}).inject(this.node);
        this.modifyPwdContentNode = new Element("div", {"styles": this.css.contentNode}).inject(this.node);
        this.deleteContentNode = new Element("div", {"styles": this.css.contentNode}).inject(this.node);
        this.registerContentNode = new Element("div", {"styles": this.css.registerContentNode}).inject(this.node);

        this.check = new MWF.xApplication.Collect.Check(this);
        this.showContent("checkContentNode");
    },
    showContent: function(node){
        this.checkContentNode.setStyle("display", "none");
        this.loginContentNode.setStyle("display", "none");
        this.modifyContentNode.setStyle("display", "none");
        this.modifyPwdContentNode.setStyle("display", "none");
        this.deleteContentNode.setStyle("display", "none");
        this.registerContentNode.setStyle("display", "none");
        this[node].setStyle("display", "block");
    },
    setNodeResize: function(){
        this.setNodePositionAndSizeFun = this.setNodePositionAndSize.bind(this);
        this.setNodePositionAndSizeFun();
        $(window).addEvent("resize", this.setNodePositionAndSizeFun);
        this.addEvent("queryClose", function(){
            $(window).removeEvent("resize", this.setNodePositionAndSizeFun);
        }.bind(this));
    },
    setNodePositionAndSize: function(){
        this.node.position({
            relativeTo: this.desktop.node,
            position: 'center',
            edge: 'center'
        });
    }
});

MWF.xApplication.Collect.Check = new Class({
    initialize: function(collect){
        this.collect = collect;
        this.lp = this.collect.lp;
        this.css = this.collect.css;
        this.contentNode = this.collect.checkContentNode;
        this.action = this.collect.action;
        this.load();
    },
    load: function(){
        this.connectChecked = true;
        this.loginChecked = false;

        this.statusConnectNode = new Element("div", {"styles": this.css.statusConnectNode}).inject(this.contentNode);
        this.setStatusConnectNode();
        this.statusLoginNode = new Element("div", {"styles": this.css.statusLoginNode}).inject(this.contentNode);
        this.setStatusLoginNode();

        this.actionsNode = new Element("div", {"styles": this.css.actionsNode}).inject(this.contentNode);
    },
    setActionsNode: function(){
        if (this.connectChecked && this.loginChecked) {
            this.actionsNode.empty();
            if (this.connected && this.login){
                this.setLoginInfor();
            }else if (this.connected && !this.login){
                this.setNotLoginInfor();
            }else if (!this.connected && !this.login){
                this.setDisconnectedInfor();
            }else{
                //impossible
            }
        }
    },
    setLoginInfor: function(){
        new Element("div", {"styles": this.css.loginInfor, "html": this.lp.connectedAndLogin}).inject(this.actionsNode);
        new Element("div", {"styles": this.css.loginInfor, "html": this.lp.modifyAccount}).inject(this.actionsNode);
        this.modifyAccountAction = new Element("div", {"styles": this.css.inforAction, "html": this.lp.modifyAccountAction}).inject(this.actionsNode);

        this.disconnectAction = new Element("div", {"styles": this.css.inforAction, "html": this.lp.doDisconnect}).inject(this.actionsNode);
        this.disconnectAction.addEvent("click", this.disconnect.bind(this));

        this.modifyPwdAccountAction = new Element("div", {"styles": this.css.inforAction, "html": this.lp.modifyPwdAccountAction}).inject(this.actionsNode);

        this.deleteAccountAction = new Element("div", {"styles": this.css.inforAction, "html": this.lp.deleteAccountAction}).inject(this.actionsNode);
        this.modifyAccountAction.addEvent("click", this.showModifyForm.bind(this));
        this.modifyPwdAccountAction.addEvent("click", this.showModifyPwdForm.bind(this));
        this.deleteAccountAction.addEvent("click", this.showDeleteForm.bind(this));

        new Element("div", {"styles": this.css.loginInfor, "html": this.lp.loginAccount}).inject(this.actionsNode);
        this.loginAccountAction = new Element("div", {"styles": this.css.inforAction, "html": this.lp.login}).inject(this.actionsNode);
        this.loginAccountAction.addEvent("click", this.showLoginForm.bind(this));

    },
    setNotLoginInfor: function(){
        new Element("div", {"styles": this.css.notLoginInfor, "html": this.lp.notLogin}).inject(this.actionsNode);

        new Element("div", {"styles": this.css.notLoginInfor, "html": this.lp.notLoginInfoLogin}).inject(this.actionsNode);
        this.loginAction = new Element("div", {"styles": this.css.inforAction, "html": this.lp.login}).inject(this.actionsNode);

        new Element("div", {"styles": this.css.notLoginInfor, "html": this.lp.notLoginInfoRegister}).inject(this.actionsNode);
        this.registerAction = new Element("div", {"styles": this.css.inforAction, "html": this.lp.register}).inject(this.actionsNode);

        this.loginAction.addEvent("click", this.showLoginForm.bind(this));
        this.registerAction.addEvent("click", this.showRegisterForm.bind(this));
        //this.modifyAccountAction = new Element("div", {"styles": this.css.modifyAccountAction, "html": this.lp.modifyAccountAction}).inject(this.actionsNode);
    },
    setDisconnectedInfor: function(){
        new Element("div", {"styles": this.css.loginInfor, "html": this.lp.disconnect}).inject(this.actionsNode);
        new Element("div", {"styles": this.css.disconnectInfor, "html": this.lp.disconnectInfo}).inject(this.actionsNode);
    },
    setStatusConnectNode: function(){
        this.statusConnectNode.empty();
        this.setStatusConnectNodeContent();
        this.action.collectConnected(function(json){
            if (json.data.value){
                this.setStatusConnectSuccess();
                this.connected = true;
            }else{
                this.setStatusConnectFailure();
                this.connected = false;
            }
            this.connectChecked = true;
            this.setActionsNode();
        }.bind(this), function(){
            this.setStatusConnectFailure();
            this.connected = false;
            this.connectChecked = true;
            this.setActionsNode();
        }.bind(this));
    },
    setStatusConnectNodeContent: function(){
        this.statusConnectNode.empty();
        this.statusConnectIconsNode = new Element("div", {"styles": this.css.statusConnectIconsNode}).inject(this.statusConnectNode);
        this.statusConnectIconCenterNode = new Element("div", {"styles": this.css.statusConnectIconCenterNode}).inject(this.statusConnectIconsNode);
        this.statusConnectIconConnectNode = new Element("div", {"styles": this.css.statusConnectIconConnectNode}).inject(this.statusConnectIconsNode);
        this.statusConnectIconCollectNode = new Element("div", {"styles": this.css.statusConnectIconCollectNode}).inject(this.statusConnectIconsNode);

        this.statusConnectTextNode = new Element("div", {"styles": this.css.statusConnectTextNode, "text": this.lp.checking}).inject(this.statusConnectNode);
    },
    setStatusConnectSuccess: function(){
        this.statusConnectIconConnectNode.setStyles(this.css.statusConnectIconConnectedNode);
        this.statusConnectTextNode.set("text", this.lp.collectConnected);
    },
    setStatusConnectFailure: function(){
        this.statusConnectIconConnectNode.setStyles(this.css.statusConnectIconDisconnectNode);
        this.statusConnectTextNode.set("text", this.lp.collectDisconnect);
    },
    setStatusLoginNode: function(){
        this.statusLoginNode.empty();
        this.setStatusLoginNodeContent();
        this.action.collectValidate(function(json){
            if (json.data.value){
                this.setStatusLoginSuccess();
                this.login = true;
            }else{
                this.setStatusLoginFailure();
                this.login = false;
            }
            this.recheckActionNode = new Element("div", {"styles": this.css.recheckActionNode, "text": this.lp.recheck}).inject(this.statusLoginTextNode, "before");
            this.recheckActionNode.addEvent("click", function(){this.recheck();}.bind(this));
            this.loginChecked = true;
            this.setActionsNode();
        }.bind(this), function(){
            this.setStatusLoginFailure();
            this.login = false;

            this.recheckActionNode = new Element("div", {"styles": this.css.recheckActionNode, "text": this.lp.recheck}).inject(this.statusLoginTextNode, "before");
            this.recheckActionNode.addEvent("click", function(){this.recheck();}.bind(this));
            this.loginChecked = true;
            this.setActionsNode();
        }.bind(this));
    },
    setStatusLoginNodeContent: function(){
        this.statusLoginIconsNode = new Element("div", {"styles": this.css.statusLoginIconsNode}).inject(this.statusLoginNode);
        this.statusLoginIconCenterNode = new Element("div", {"styles": this.css.statusLoginIconCenterNode}).inject(this.statusLoginIconsNode);
        this.statusLoginIconConnectNode = new Element("div", {"styles": this.css.statusLoginIconConnectNode}).inject(this.statusLoginIconsNode);
        this.statusLoginIconCollectNode = new Element("div", {"styles": this.css.statusLoginIconCollectNode}).inject(this.statusLoginIconsNode);

        this.statusLoginTextNode = new Element("div", {"styles": this.css.statusLoginTextNode, "text": this.lp.checking}).inject(this.statusLoginNode);
    },
    setStatusLoginSuccess: function(){
        this.statusLoginIconConnectNode.setStyles(this.css.statusLoginIconConnectedNode);
        this.statusLoginTextNode.set("text", this.lp.collectLogin);
    },
    setStatusLoginFailure: function(){
        this.statusLoginIconConnectNode.setStyles(this.css.statusLoginIconDisconnectNode);
        this.statusLoginTextNode.set("text", this.lp.collectNotLogin);
    },
    disconnect : function(){
        this.action.disconnect( function(json){
            this.recheck();
        }.bind(this), null , false);
    },
    recheck: function(){
        this.contentNode.empty();
        this.load();
    },
    showModifyForm: function(){
        if (!this.collect.modifyForm){
            this.collect.modifyForm = new MWF.xApplication.Collect.ModifyForm(this.collect);
        }
        this.collect.modifyForm.show();
    },
    showModifyPwdForm: function(){
        if (!this.collect.modifyPwdForm){
            this.collect.modifyPwdForm = new MWF.xApplication.Collect.ModifyPwdForm(this.collect);
        }
        this.collect.modifyPwdForm.show();
    },
    showDeleteForm: function(){
        if (!this.collect.deleteForm){
            this.collect.deleteForm = new MWF.xApplication.Collect.DeleteForm(this.collect);
        }
        this.collect.deleteForm.show();
    },
    showLoginForm: function(){
        if (!this.collect.loginForm){
            this.collect.loginForm = new MWF.xApplication.Collect.LoginForm(this.collect);
        }
        this.collect.loginForm.show();
    },
    showRegisterForm: function(){
        if (!this.collect.registerForm){
            this.collect.registerForm = new MWF.xApplication.Collect.RegisterForm(this.collect);
        }
        this.collect.registerForm.show();
    }
});

MWF.xApplication.Collect.LoginForm = new Class({
    initialize: function(collect){
        this.collect = collect;
        this.lp = this.collect.lp;
        this.css = this.collect.css;
        this.contentNode = this.collect.loginContentNode;
        this.action = this.collect.action;
        this.load();
    },
    load: function(){
        this.usernameNode = new Element("div", {"styles": this.css.loginUsernameNode}).inject(this.contentNode);
        var icon = new Element("div", {"styles": this.css.loginUsernameIconNode}).inject(this.usernameNode);
        var inputArea = new Element("div", {"styles": this.css.loginInputAreaNode}).inject(this.usernameNode);
        this.usernameInput = new Element("input", {"styles": this.css.loginInputNode, "type": "text", "value": this.lp.username}).inject(inputArea);

        var errorNode = new Element("div", {"styles": this.css.loginErrorNode}).inject(this.contentNode);


        this.passwordNode = new Element("div", {"styles": this.css.loginPasswordNode}).inject(this.contentNode);
        icon = new Element("div", {"styles": this.css.loginPasswordIconNode}).inject(this.passwordNode);
        inputArea = new Element("div", {"styles": this.css.loginInputAreaNode}).inject(this.passwordNode);
        this.passwordInput = new Element("input", {"styles": this.css.loginInputNode, "type": "password", "value": this.lp.password}).inject(inputArea);

        errorNode = new Element("div", {"styles": this.css.loginErrorNode}).inject(this.contentNode);
        this.setInputEvent();
        this.setDefaultValue();

        this.loginActionNode = new Element("div", {"styles": this.css.loginActionNode, "text": this.lp.login}).inject(this.contentNode);
        this.loginActionNode.addEvent("click", this.login.bind(this));

        this.otherActionNode = new Element("div", {"styles": this.css.loginOtherActionNode}).inject(this.contentNode);
        this.registerActionNode = new Element("div", {"styles": this.css.loginRegisterActionNode, "text": this.lp.register}).inject(this.otherActionNode);
        this.forgetActionNode = new Element("div", {"styles": this.css.loginForgetActionNode, "text": this.lp.forget}).inject(this.otherActionNode);
        this.setOtherActionEvent();

        this.errorNode = new Element("div", {"styles": this.css.loginErrorNode}).inject(this.contentNode);
    },
    login: function(){
        if (!this.isLogining){
            this.loginActionNode.set("text", this.lp.logining);
            this.isLogining = true;

            this.errorNode.set("text", "");
            var user = this.usernameInput.get("value");
            var pass = this.passwordInput.get("value");
            var errorText = "";
            this.checkLogin = true;
            if (!user || user==this.lp.username) this.errorUsername(this.lp.errorUsername);
            if (!pass || pass==this.lp.password) this.errorPassword(this.lp.errorPassword);
            if (this.checkLogin){
                this.action.collectValidateInput({"name": user, "password": pass}, function(json){
                    if (json.data.value){
                        this.action.updateCollect({"name": user, "password": pass, "enable": true}, function(json){
                            this.loginCanceled();
                            this.collect.showContent("checkContentNode");
                            this.collect.backNode.setStyle("display", "none");
                            this.collect.check.recheck();
                        }.bind(this), function(xhr, text, error){
                            var errorText = error+":"+text;
                            if (xhr) errorText = xhr.responseText;
                            MWF.xDesktop.notice("error", {x: "right", y:"top"}, "request json error: "+errorText);
                            this.loginCanceled();
                        }.bind(this));
                    }else{
                        this.errorNode.set("text", this.lp.loginError);
                        this.loginCanceled();
                    }
                }.bind(this), function(xhr, text, error){
                    if (xhr){
                        var json = JSON.decode(xhr.responseText);
                        this.errorNode.set("text", json.message);
                    }else{
                        var errorText = error+":"+text;
                        MWF.xDesktop.notice("error", {x: "right", y:"top"}, "request json error: "+errorText);
                    }
                    this.loginCanceled();
                }.bind(this));
            }
        }
    },
    loginCanceled: function(){
        this.loginActionNode.set("text", this.lp.login);
        this.isLogining = false;
    },
    errorUsername: function(text){
        if (text){
            this.usernameNode.setStyles(this.css.loginUsernameNode_error);
            this.checkLogin = false;
        }else{
            this.usernameNode.setStyles(this.css.loginUsernameNode);
        }
        this.usernameNode.getNext().set("text", text);
    },
    errorPassword: function(text){
        if (text){
            this.passwordNode.setStyles(this.css.loginPasswordNode_error);
            this.checkLogin = false;
        }else{
            this.passwordNode.setStyles(this.css.loginPasswordNode);
        }
        this.passwordNode.getNext().set("text", text);
    },
    show: function(){
        this.collect.showContent("loginContentNode");
        this.collect.backNode.setStyle("display", "block");
        this.collect.backNode.removeEvents("click");
        this.collect.backNode.addEvent("click", function(){
            this.collect.showContent("checkContentNode");
            this.collect.backNode.setStyle("display", "none");
        }.bind(this));
        this.setDefaultValue();
    },
    setInputEvent: function(){
        this.usernameInput.addEvents({
            "focus": function(){
                this.errorUsername("");
                this.usernameNode.setStyles(this.css.loginUsernameNode_over);
                if (this.usernameInput.get("value")==this.lp.username){
                    this.usernameInput.set("value", "");
                }
            }.bind(this),
            "blur": function(){
                this.usernameNode.setStyles(this.css.loginUsernameNode);
                if (!this.usernameInput.get("value")){
                    this.usernameInput.set("value", this.lp.username);
                    this.errorUsername(this.lp.errorUsername);
                }
            }.bind(this),
            "selectstart": function(e){e.stopPropagation();}
        });

        this.passwordInput.addEvents({
            "focus": function(){
                this.errorPassword("");
                this.passwordNode.setStyles(this.css.loginPasswordNode_over);
                if (this.passwordInput.get("value")==this.lp.password){
                    this.passwordNode.getNext().set("text", "");
                    this.passwordInput.set("value", "");
                }
            }.bind(this),
            "blur": function(){
                this.passwordNode.setStyles(this.css.loginPasswordNode);
                if (!this.passwordInput.get("value")){
                    this.errorPassword(this.lp.errorPassword);
                    this.passwordInput.set("value", this.lp.password);
                }
            }.bind(this),
            "selectstart": function(e){e.stopPropagation();}
        });

    },
    setDefaultValue: function(){
        this.action.getCollectConfig(function(json){
            if (json.data.name) this.usernameInput.set("value", json.data.name);
            if (json.data.password) this.passwordInput.set("value", json.data.password);
        }.bind(this));
    },
    setOtherActionEvent: function(){
        this.registerActionNode.addEvents({
            "mouseover": function(){this.registerActionNode.setStyles(this.css.loginRegisterActionNode_over);}.bind(this),
            "mouseout": function(){this.registerActionNode.setStyles(this.css.loginRegisterActionNode);}.bind(this),
            "click": this.showRegisterForm.bind(this)
        });
        this.forgetActionNode.addEvents({
            "mouseover": function(){this.forgetActionNode.setStyles(this.css.loginForgetActionNode_over);}.bind(this),
            "mouseout": function(){this.forgetActionNode.setStyles(this.css.loginForgetActionNode);}.bind(this),
            "click": this.showModifyForm.bind(this)
        });
    },
    showModifyForm: function(){
        /*if (!this.collect.modifyForm){
            this.collect.modifyForm = new MWF.xApplication.Collect.ModifyForm(this.collect);
        }
        this.collect.modifyForm.show();*/
        if (!this.collect.modifyPwdForm){
            this.collect.modifyPwdForm = new MWF.xApplication.Collect.ModifyPwdForm(this.collect);
        }
        this.collect.modifyPwdForm.show();
    },
    showRegisterForm: function(){
        if (!this.collect.registerForm){
            this.collect.registerForm = new MWF.xApplication.Collect.RegisterForm(this.collect);
        }
        this.collect.registerForm.show();
    }
});

MWF.xApplication.Collect.RegisterForm = new Class({
    initialize: function(collect){
        this.collect = collect;
        this.lp = this.collect.lp;
        this.css = this.collect.css;
        this.contentNode = this.collect.registerContentNode;
        this.action = this.collect.action;
        this.load();
    },
    createNode: function(iconCss, text, type){
        var node = new Element("div", {"styles": this.css.registerAreaNode}).inject(this.contentNode);
        var icon = new Element("div", {"styles": this.css[iconCss]}).inject(node);
        var inputCheckArea = new Element("div", {"styles": this.css.registerCheckIconNode}).inject(node);
        var inputArea = new Element("div", {"styles": this.css.registerInputAreaNode}).inject(node);
        var inputNode = new Element("input", {"styles": this.css.registerInputNode, "type": type || "text", "value": this.lp[text]}).inject(inputArea);
        var errorNode = new Element("div", {"styles": this.css.registerErrorNode}).inject(this.contentNode);
        return node;
    },

    load: function(){
        this.contentNode.setStyle("padding-top", "30px");
        this.usernameNode = this.createNode("registerUsernameIconNode", "username");
        this.usernameInput = this.usernameNode.getElement("input");

        this.mobileNode = this.createNode("registerMobileIconNode", "mobile");
        this.mobileInput = this.mobileNode.getElement("input");

        this.mailNode = this.createNode("registerMailIconNode", "mail");
        this.mailInput = this.mailNode.getElement("input");

        this.codeNode = this.createNode("registerCodeIconNode", "code");
        this.codeInput = this.codeNode.getElement("input");

        this.passwordNode = this.createNode("registerPasswordIconNode", "password", "password");
        this.passwordInput = this.passwordNode.getElement("input");

        this.setInputEvent();

        this.registerActionNode = new Element("div", {"styles": this.css.registerActionNode, "text": this.lp.register}).inject(this.contentNode);
        this.registerActionNode.addEvent("click", this.register.bind(this));

        //this.otherActionNode = new Element("div", {"styles": this.css.loginOtherActionNode}).inject(this.contentNode);
        //this.registerActionNode = new Element("div", {"styles": this.css.loginRegisterActionNode, "text": this.lp.register}).inject(this.otherActionNode);
        //this.forgetActionNode = new Element("div", {"styles": this.css.loginForgetActionNode, "text": this.lp.forget}).inject(this.otherActionNode);
        //this.setOtherActionEvent();

        this.errorNode = new Element("div", {"styles": this.css.registerErrorNode}).inject(this.contentNode);
    },
    errorInput: function(node, text){
        if (text){
            node.setStyles(this.css.registerAreaNode_error);
            this.checkRegister = false;
        }else{
            node.setStyles(this.css.registerAreaNode_normal);
        }
        node.getNext().set("text", text);
    },

    setInputNodeEvent: function(node, input, text, errorText, name){
        input.addEvents({
            "focus": function(){
                this.errorInput(node, "");
                node.setStyles(this.css.registerAreaNode_over);
                if (input.get("value")==text) input.set("value", "");
            }.bind(this),
            "blur": function(){
                node.setStyles(this.css.registerAreaNode_normal);
                if (!input.get("value")) input.set("value", text);
                this[name+"Verification"]();

                //if (!input.get("value")){
                //    input.set("value", text);
                //    this.errorInput(node, errorText);
                //}
            }.bind(this),
            "selectstart": function(e){e.stopPropagation();}
        });
    },
    resetCodeNode: function(){
        this.codeAreaNode = new Element("div", {"styles": this.css.registerCodeAreaNode}).inject(this.codeNode, "before");
        this.codeNode.getNext().inject(this.codeAreaNode);
        this.codeNode.inject(this.codeAreaNode, "top");
        this.codeNode.setStyles({
            "width": "auto",
            "margin": "0px 160px 0px 0px"
        });
        this.getCodeAtionNode = new Element("div", {"styles": this.css.registerGetCodeAtionNode, "text": this.lp.getCodeAtion}).inject(this.codeAreaNode, "top");
        this.getCodeAtionNode.addEvent("click", this.getCode.bind(this));
    },
    getCodeNext: function(){
        this.getCodeAtionNode.setStyles(this.css.registerGetCodeAtionNode_next);
        this.getCodeAtionNode.set("text", this.lp.getCodeNext+"(60)");
        this.getCodeAtionNode.removeEvents("click");
        this.codeNextTimerCount = 60;
    },
    getCodeNextTimer: function(){
        this.codeNextTimerCount--;
        if (this.codeNextTimerCount>0){
            this.getCodeAtionNode.set("text", this.lp.getCodeNext+"("+this.codeNextTimerCount+")");
            this.codeNextTimer = window.setTimeout(function(){
                this.getCodeNextTimer();
            }.bind(this), 1000);
        }else{
            this.regetCode();
        }
    },
    regetCode: function(){
        this.getCodeAtionNode.setStyles(this.css.registerGetCodeAtionNode);
        this.getCodeAtionNode.set("text", this.lp.getCodeAtion);
        this.getCodeAtionNode.addEvent("click", this.getCode.bind(this));
        this.codeNextTimer = null;
    },

    getCode: function(){
        if (this.mobileVerification()){
            this.getCodeNext();
            this.action.getCode(this.mobileInput.get("value"), function(json){
                this.codeNextTimer = window.setTimeout(function(){
                    this.getCodeNextTimer();
                }.bind(this), 1000);
            }.bind(this), function(xhr, text, error){
                if (xhr){
                    var json = JSON.decode(xhr.responseText);
                    this.errorNode.set("text", json.message);
                }else{
                    var errorText = error+":"+text;
                    MWF.xDesktop.notice("error", {x: "right", y:"top"}, "request json error: "+errorText);
                }
                this.regetCode();
            }.bind(this));
        }
    },

    setInputEvent: function(){
        this.setInputNodeEvent(this.usernameNode, this.usernameInput, this.lp.username, this.lp.errorUsername, "username");
        this.setInputNodeEvent(this.passwordNode, this.passwordInput, this.lp.password, this.lp.errorPassword, "password");
        this.setInputNodeEvent(this.mobileNode, this.mobileInput, this.lp.mobile, this.lp.errorMobile, "mobile");
        if( this.mailNode && this.mailInput ){
            this.setInputNodeEvent(this.mailNode, this.mailInput, this.lp.mail, this.lp.errorMail, "mail");
        }
        this.setInputNodeEvent(this.codeNode, this.codeInput, this.lp.code, this.lp.errorCode, "code");
        this.resetCodeNode();
    },

    show: function(){
        this.collect.showContent("registerContentNode");
        this.collect.backNode.setStyle("display", "block");
        this.collect.backNode.removeEvents("click");
        this.collect.backNode.addEvent("click", function(){
            this.collect.showContent("checkContentNode");
            this.collect.backNode.setStyle("display", "none");
        }.bind(this));
    },
    registering: function(){
        this.registerActionNode.removeEvents("click");
        this.registerActionNode.set("text", this.lp.registering);
    },
    registered: function(){
        this.registerActionNode.addEvent("click", this.register.bind(this));
        this.registerActionNode.set("text", this.lp.register);
    },
    register: function(){
        var user = this.usernameInput.get("value");
        var mobile = this.mobileInput.get("value");
        var mail = this.mailInput.get("value");
        var code = this.codeInput.get("value");
        var password = this.passwordInput.get("value");

        if (this.usernameVerification() && this.mobileVerification() && this.mailVerification() && this.codeVerification() && this.passwordVerification()){
            this.registering();
            var data = {
                codeAnswer: code,
                mobile: mobile,
                mail: mail,
                name: user,
                password: password
            };
            this.action.createCollect(data, function(){

                this.action.updateCollect({"name": user, "password": password, "enable": true}, function(json){
                    this.registered();
                    this.collect.showContent("checkContentNode");
                    this.collect.backNode.setStyle("display", "none");
                    window.setTimeout(function(){
                        this.check.recheck();
                    }.bind(this), 1000);
                }.bind(this), function(xhr, text, error){
                    var errorText = error+":"+text;
                    if (xhr) errorText = xhr.responseText;
                    MWF.xDesktop.notice("error", {x: "right", y:"top"}, "request json error: "+errorText);
                    this.registered();
                }.bind(this));

            }.bind(this), function(xhr, text, error){
                if (xhr){
                    var json = JSON.decode(xhr.responseText);
                    this.errorNode.set("text", json.message);
                }else{
                    var errorText = error+":"+text;
                    MWF.xDesktop.notice("error", {x: "right", y:"top"}, "request json error: "+errorText);
                }
                this.registered();
            }.bind(this));
        }
    },
    passwordVerification: function(){
        var password = this.passwordInput.get("value");
        if (!password || password==this.lp.password){
            this.errorInput(this.passwordNode, this.lp.errorPassword);
            return false;
        }
        var flag = "";
        this.action.passwordValidate({"password": password}, function(json){
            //if (json.data.value<4){
            //    flag = this.lp.errorPasswordRule;
            //}
            flag = json.data.value || "";
        }.bind(this), function(xhr, text, error){

        }.bind(this), false);
        if (flag){
            this.errorInput(this.passwordNode, flag);
            return false;
        }
        return true;
    },
    codeVerification: function(){
        var code = this.codeInput.get("value");
        if (!code || code==this.lp.mobile){
            this.errorInput(this.codeNode, this.lp.errorCode);
            return false;
        }
        return true;
    },
    usernameVerification: function(){
        var user = this.usernameInput.get("value");
        if (!user || user==this.lp.username){
            this.errorInput(this.usernameNode, this.lp.errorUsername);
            return false;
        }
        var flag = "";
        this.action.nameExist(user, function(json){
            if (json.data.value){
                flag = this.lp.usernameExisted;
            }
        }.bind(this), function(xhr, text, error){
            if (xhr){
                var json = JSON.decode(xhr.responseText);
                flag = json.message;
                //this.errorNode.set("text", json.message);
            }else{
                var errorText = error+":"+text;
                flag = this.lp.requestError+errorText;
                //MWF.xDesktop.notice("error", {x: "right", y:"top"}, "request json error: "+errorText);
            }
        }.bind(this), false);
        if (flag){
            this.errorInput(this.usernameNode, flag);
            return false;
        }
        return true;
    },
    mobileVerification: function(){
        var mobile = this.mobileInput.get("value");
        if (!mobile || mobile==this.lp.mobile){
            this.errorInput(this.mobileNode, this.lp.errorMobile);
            return false;
        }
        if (mobile.length!=11){
            this.errorInput(this.mobileNode, this.lp.errorMobile);
            return false;
        }
        return true;
    },
    mailVerification: function(){
        var mail = this.mailInput.get("value");
        if (!mail || mail==this.lp.mail){
            this.errorInput(this.mailNode, this.lp.errorMail);
            return false;
        }

        var reg = /^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+((\.[a-zA-Z0-9_-]{2,3}){1,2})$/;
        if (!reg.test(mail)){
            this.errorInput(this.mailNode, this.lp.errorMail);
            return false;
        }
        return true;
    }
});
MWF.xApplication.Collect.ModifyForm = new Class({
    Extends: MWF.xApplication.Collect.RegisterForm,
    initialize: function(collect){
        this.collect = collect;
        this.lp = this.collect.lp;
        this.css = this.collect.css;
        this.contentNode = this.collect.modifyContentNode;
        this.action = this.collect.action;
        this.load();
    },
    load: function(){
        this.contentNode.setStyle("padding-top", "30px");
        this.usernameNode = this.createNode("registerUsernameIconNode", "username");
        this.usernameInput = this.usernameNode.getElement("input");

        this.mobileNode = this.createNode("registerMobileIconNode", "mobile");
        this.mobileInput = this.mobileNode.getElement("input");

        this.codeNode = this.createNode("registerCodeIconNode", "code");
        this.codeInput = this.codeNode.getElement("input");

        this.newNameNode = this.createNode("registerNewNameIconNode");
        this.newNameInput = this.newNameNode.getElement("input");

        this.secretNode = this.createNode("registerSecretIconNode", "secret");
        this.secretInput = this.secretNode.getElement("input");

        this.keyNode = this.createNode("registerKeyIconNode", "key");
        this.keyInput = this.keyNode.getElement("input");

        this.setInputEvent();

        this.nextActionNode = new Element("div", {"styles": this.css.registerActionNode, "text": this.lp.modifyNextStep}).inject(this.contentNode);
        this.nextActionNode.addEvent("click", this.nextStep.bind(this));

        this.errorNode = new Element("div", {"styles": this.css.registerErrorNode}).inject(this.contentNode);

        this.newNameNode.setStyle("display", "none");
        this.newNameNode.getNext().setStyle("display", "none");

        this.secretNode.setStyle("display", "none");
        this.secretNode.getNext().setStyle("display", "none");

        this.keyNode.setStyle("display", "none");
        this.keyNode.getNext().setStyle("display", "none");

        this.setDefaultValue();
    },
    setDefaultValue: function(){
        this.action.getCollectConfig(function(json){
            if (json.data.name) this.usernameInput.set("value", json.data.name);
            if (json.data.name) this.newNameInput.set("value", json.data.name);
            if (json.data.key) this.keyInput.set("value", json.data.key);
            if (json.data.secret) this.secretInput.set("value", json.data.secret);
        }.bind(this));
    },
    nextStepWait: function(){
        this.nextActionNode.removeEvents("click");
        this.nextActionNode.set("text", this.lp.nextStepWait);
        this.errorNode.empty();
    },
    setInputEvent: function(){

        this.setInputNodeEvent(this.usernameNode, this.usernameInput, this.lp.username, this.lp.errorUsername, "username");

        this.setInputNodeEvent(this.newNameNode, this.newNameInput, this.lp.username, this.lp.errorUsername, "newName");
        this.setInputNodeEvent(this.secretNode, this.secretInput, this.lp.secret, this.lp.errorSecret, "secret");
        this.setInputNodeEvent(this.keyNode, this.keyInput, this.lp.key, this.lp.errorKey, "key");

        this.setInputNodeEvent(this.mobileNode, this.mobileInput, this.lp.mobile, this.lp.errorMobile, "mobile");
        this.setInputNodeEvent(this.codeNode, this.codeInput, this.lp.code, this.lp.errorCode, "code");
        this.resetCodeNode();
    },
    nextStepWaited: function(){
        this.nextActionNode.addEvent("click", this.nextStep.bind(this));
        this.nextActionNode.set("text", this.lp.modifyNextStep);
    },
    firstStep: function(){
        this.nextActionNode.addEvent("click", this.nextStep.bind(this));
        this.nextActionNode.set("text", this.lp.modifyNextStep);

        this.newNameNode.setStyle("display", "none");
        this.newNameNode.getNext().setStyle("display", "none");

        this.secretNode.setStyle("display", "none");
        this.secretNode.getNext().setStyle("display", "none");

        this.keyNode.setStyle("display", "none");
        this.keyNode.getNext().setStyle("display", "none");

        this.usernameNode.setStyle("display", "block");
        this.usernameNode.getNext().setStyle("display", "block");

        this.mobileNode.setStyle("display", "block");
        this.mobileNode.getNext().setStyle("display", "block");

        this.codeNode.getParent().setStyle("display", "block");
        this.codeNode.getNext().setStyle("display", "block");

        this.errorNode.empty();
    },
    nextStep: function(){
        var user = this.usernameInput.get("value");
        var mobile = this.mobileInput.get("value");
        var code = this.codeInput.get("value");

        if (this.usernameVerification() & this.mobileVerification() & this.codeVerification()){
            this.nextStepWait();
            this.action.codeValidate({"codeAnswer": code, "mobile": mobile}, function(json){
                if (json.data.value){
                    this.nextActionNode.removeEvents("click");
                    this.nextActionNode.set("text", this.lp.modifyUnit);
                    this.nextActionNode.addEvent("click", this.modifyUnit.bind(this));

                    this.newNameNode.setStyle("display", "block");
                    this.newNameNode.getNext().setStyle("display", "block");

                    this.secretNode.setStyle("display", "block");
                    this.secretNode.getNext().setStyle("display", "block");

                    this.keyNode.setStyle("display", "block");
                    this.keyNode.getNext().setStyle("display", "block");

                    this.usernameNode.setStyle("display", "none");
                    this.usernameNode.getNext().setStyle("display", "none");

                    this.mobileNode.setStyle("display", "none");
                    this.mobileNode.getNext().setStyle("display", "none");

                    this.codeNode.getParent().setStyle("display", "none");
                    this.codeNode.getNext().setStyle("display", "none");

                    this.errorNode.empty();
                }else{
                    this.errorInput(this.codeNode, this.lp.errorCode);
                }
            }.bind(this), function(xhr, text, error){
                if (xhr){
                    var json = JSON.decode(xhr.responseText);
                    this.errorInput(this.codeNode, json.message);
                }else{
                    var errorText = error+":"+text;
                    MWF.xDesktop.notice("error", {x: "right", y:"top"}, "request json error: "+errorText);
                }
                this.nextStepWaited();
            }.bind(this));
        }


    },
    modifyUnit: function(){
        var user = this.usernameInput.get("value");
        var newName = this.newNameInput.get("value");
        var code = this.codeInput.get("value");
        var secret = this.secretInput.get("value");
        var key = this.keyInput.get("value");
        var mobile = this.mobileInput.get("value");

        if (this.newNameVerification()){
            this.nextStepWait();
            var data = {
                mobile: mobile,
                codeAnswer: code,
                newName: newName,
                secret: secret,
                key: key,
                name: user
            };
            this.action.updateUnitCollect(data, function(json){
                this.firstStep();
                this.collect.showContent("checkContentNode");
                this.collect.backNode.setStyle("display", "none");
                this.check.recheck();
            }.bind(this), function(xhr, text, error){
                var errorText = error+":"+text;
                if (xhr) errorText = xhr.responseText;
                MWF.xDesktop.notice("error", {x: "right", y:"top"}, "request json error: "+errorText);
                this.firstStep();
            }.bind(this));
        }
    },
    usernameVerification: function(){
        var user = this.usernameInput.get("value");
        if (!user || user==this.lp.username){
            this.errorInput(this.usernameNode, this.lp.errorUsername);
            return false;
        }
        return true;
    },
    newNameVerification: function(){
        var user = this.usernameInput.get("value");
        if (!user || user==this.lp.username){
            this.errorInput(this.usernameNode, this.lp.errorUsername);
            return false;
        }
        return true;
    },
    keyVerification: function(){
        var user = this.usernameInput.get("value");
        if (!user || user==this.lp.username){
            this.errorInput(this.usernameNode, this.lp.errorUsername);
            return false;
        }
        return true;
    },
    secretVerification: function(){
        var user = this.usernameInput.get("value");
        if (!user || user==this.lp.username){
            this.errorInput(this.usernameNode, this.lp.errorUsername);
            return false;
        }
        return true;
    },
    show: function(){
        this.collect.showContent("modifyContentNode");
        this.collect.backNode.setStyle("display", "block");
        this.collect.backNode.removeEvents("click");
        this.collect.backNode.addEvent("click", function(){
            this.firstStep();
            this.collect.showContent("checkContentNode");
            this.collect.backNode.setStyle("display", "none");
        }.bind(this));
    }
});

MWF.xApplication.Collect.ModifyPwdForm = new Class({
    Extends: MWF.xApplication.Collect.RegisterForm,
    initialize: function(collect){
        this.collect = collect;
        this.lp = this.collect.lp;
        this.css = this.collect.css;
        this.contentNode = this.collect.modifyPwdContentNode;
        this.action = this.collect.action;
        this.load();
    },
    load: function(){
        this.contentNode.setStyle("padding-top", "30px");
        this.usernameNode = this.createNode("registerUsernameIconNode", "username");
        this.usernameInput = this.usernameNode.getElement("input");

        this.mobileNode = this.createNode("registerMobileIconNode", "mobile");
        this.mobileInput = this.mobileNode.getElement("input");

        this.codeNode = this.createNode("registerCodeIconNode", "code");
        this.codeInput = this.codeNode.getElement("input");

        this.passwordNode = this.createNode("registerPasswordIconNode", "password", "password");
        this.passwordInput = this.passwordNode.getElement("input");

        this.setInputEvent();

        this.nextActionNode = new Element("div", {"styles": this.css.registerActionNode, "text": this.lp.modifyNextStep}).inject(this.contentNode);
        this.nextActionNode.addEvent("click", this.nextStep.bind(this));

        //this.otherActionNode = new Element("div", {"styles": this.css.loginOtherActionNode}).inject(this.contentNode);
        //this.registerActionNode = new Element("div", {"styles": this.css.loginRegisterActionNode, "text": this.lp.register}).inject(this.otherActionNode);
        //this.forgetActionNode = new Element("div", {"styles": this.css.loginForgetActionNode, "text": this.lp.forget}).inject(this.otherActionNode);
        //this.setOtherActionEvent();

        this.errorNode = new Element("div", {"styles": this.css.registerErrorNode}).inject(this.contentNode);

        this.passwordNode.setStyle("display", "none");
        this.passwordNode.getNext().setStyle("display", "none");

        this.setDefaultValue();
    },
    setDefaultValue: function(){
        this.action.getCollectConfig(function(json){
            if (json.data.name) this.usernameInput.set("value", json.data.name);
        }.bind(this));
    },
    nextStepWait: function(){
        this.nextActionNode.removeEvents("click");
        this.nextActionNode.set("text", this.lp.nextStepWait);
        this.errorNode.empty();
    },
    nextStepWaited: function(){
        this.nextActionNode.addEvent("click", this.nextStep.bind(this));
        this.nextActionNode.set("text", this.lp.modifyNextStep);
    },
    firstStep: function(){
        this.nextActionNode.addEvent("click", this.nextStep.bind(this));
        this.nextActionNode.set("text", this.lp.modifyNextStep);

        this.passwordNode.setStyle("display", "none");
        this.passwordNode.getNext().setStyle("display", "none");

        this.usernameNode.setStyle("display", "block");
        this.usernameNode.getNext().setStyle("display", "block");

        this.mobileNode.setStyle("display", "block");
        this.mobileNode.getNext().setStyle("display", "block");

        this.codeNode.getParent().setStyle("display", "block");
        this.codeNode.getNext().setStyle("display", "block");

        this.errorNode.empty();
    },
    nextStep: function(){
        var user = this.usernameInput.get("value");
        var mobile = this.mobileInput.get("value");
        var code = this.codeInput.get("value");

        if (this.usernameVerification() & this.mobileVerification() & this.codeVerification()){
            this.nextStepWait();
            this.action.codeValidate({"codeAnswer": code, "mobile": mobile}, function(json){
                if (json.data.value){
                    this.nextActionNode.removeEvents("click");
                    this.nextActionNode.set("text", this.lp.modifyPassword);
                    this.nextActionNode.addEvent("click", this.modifyPassword.bind(this));

                    this.passwordNode.setStyle("display", "block");
                    this.passwordNode.getNext().setStyle("display", "block");

                    this.usernameNode.setStyle("display", "none");
                    this.usernameNode.getNext().setStyle("display", "none");

                    this.mobileNode.setStyle("display", "none");
                    this.mobileNode.getNext().setStyle("display", "none");

                    this.codeNode.getParent().setStyle("display", "none");
                    this.codeNode.getNext().setStyle("display", "none");

                    this.errorNode.empty();
                }else{
                    this.errorInput(this.codeNode, this.lp.errorCode);
                }
            }.bind(this), function(xhr, text, error){
                if (xhr){
                    var json = JSON.decode(xhr.responseText);
                    this.errorInput(this.codeNode, json.message);
                }else{
                    var errorText = error+":"+text;
                    MWF.xDesktop.notice("error", {x: "right", y:"top"}, "request json error: "+errorText);
                }
                this.nextStepWaited();
            }.bind(this));
        }
    },
    modifyPassword: function(){
        var user = this.usernameInput.get("value");
        var mobile = this.mobileInput.get("value");
        var code = this.codeInput.get("value");
        var password = this.passwordInput.get("value");

        if (this.usernameVerification() & this.mobileVerification() & this.codeVerification() & this.passwordVerification()){
            this.nextStepWait();
            var data = {
                codeAnswer: code,
                mobile: mobile,
                name: user,
                password: password
            };
            debugger;
            this.action.resetPassword(data, function(){
                this.action.updateCollect({"name": user, "password": password, "enable": true}, function(json){
                    this.firstStep();
                    this.collect.showContent("checkContentNode");
                    this.collect.backNode.setStyle("display", "none");
                    this.check.recheck();
                }.bind(this), function(xhr, text, error){
                    var errorText = error+":"+text;
                    if (xhr) errorText = xhr.responseText;
                    MWF.xDesktop.notice("error", {x: "right", y:"top"}, "request json error: "+errorText);
                    this.firstStep();
                }.bind(this));
            }.bind(this), function(xhr, text, error){
                if (xhr){
                    var json = JSON.decode(xhr.responseText);
                    this.errorNode.set("text", json.message);
                }else{
                    var errorText = error+":"+text;
                    MWF.xDesktop.notice("error", {x: "right", y:"top"}, "request json error: "+errorText);
                }
                this.firstStep();
            }.bind(this));
        }
    },
    usernameVerification: function(){
        var user = this.usernameInput.get("value");
        if (!user || user==this.lp.username){
            this.errorInput(this.usernameNode, this.lp.errorUsername);
            return false;
        }
        return true;
    },
    show: function(){
        this.collect.showContent("modifyPwdContentNode");
        this.collect.backNode.setStyle("display", "block");
        this.collect.backNode.removeEvents("click");
        this.collect.backNode.addEvent("click", function(){
            this.firstStep();
            this.collect.showContent("checkContentNode");
            this.collect.backNode.setStyle("display", "none");
        }.bind(this));
    }
});


MWF.xApplication.Collect.DeleteForm = new Class({
    Extends: MWF.xApplication.Collect.RegisterForm,
    initialize: function(collect){
        this.collect = collect;
        this.lp = this.collect.lp;
        this.css = this.collect.css;
        this.contentNode = this.collect.deleteContentNode;
        this.action = this.collect.action;
        this.load();
    },
    load: function(){
        this.contentNode.setStyle("padding-top", "30px");
        this.usernameNode = this.createNode("registerUsernameIconNode", "username");
        this.usernameInput = this.usernameNode.getElement("input");

        this.mobileNode = this.createNode("registerMobileIconNode", "mobile");
        this.mobileInput = this.mobileNode.getElement("input");

        this.codeNode = this.createNode("registerCodeIconNode", "code");
        this.codeInput = this.codeNode.getElement("input");

        this.passwordNode = this.createNode("registerPasswordIconNode", "password", "password");
        this.passwordInput = this.passwordNode.getElement("input");

        this.setInputEvent();

        this.nextActionNode = new Element("div", {"styles": this.css.registerActionNode, "text": this.lp.confirmDelete}).inject(this.contentNode);
        this.nextActionNode.addEvent("click", this.deleteCollect.bind(this));

        this.errorNode = new Element("div", {"styles": this.css.registerErrorNode}).inject(this.contentNode);

        this.passwordNode.setStyle("display", "none");
        this.passwordNode.getNext().setStyle("display", "none");

        this.setDefaultValue();
    },
    setDefaultValue: function(){
        this.action.getCollectConfig(function(json){
            if (json.data.name) this.usernameInput.set("value", json.data.name);
        }.bind(this));
    },
    deleteCollect:function(){

        var user = this.usernameInput.get("value");
        var mobile = this.mobileInput.get("value");
        var code = this.codeInput.get("value");

        if (this.usernameVerification() & this.mobileVerification() & this.codeVerification() ){
            this.action.deleteCollect(user,mobile,code, function(json){
                this.collect.showContent("checkContentNode");
                this.collect.backNode.setStyle("display", "none");
            }.bind(this), function(xhr, text, error){
                var errorText = error+":"+text;
                if (xhr) errorText = xhr.responseText;
                MWF.xDesktop.notice("error", {x: "right", y:"top"}, "request json error: "+errorText);
                this.firstStep();
            }.bind(this));
        }
    },
    usernameVerification: function(){
        var user = this.usernameInput.get("value");
        if (!user || user==this.lp.username){
            this.errorInput(this.usernameNode, this.lp.errorUsername);
            return false;
        }
        return true;
    },
    firstStep: function(){

        this.passwordNode.setStyle("display", "none");
        this.passwordNode.getNext().setStyle("display", "none");

        this.usernameNode.setStyle("display", "block");
        this.usernameNode.getNext().setStyle("display", "block");

        this.mobileNode.setStyle("display", "block");
        this.mobileNode.getNext().setStyle("display", "block");

        this.codeNode.getParent().setStyle("display", "block");
        this.codeNode.getNext().setStyle("display", "block");

        this.errorNode.empty();
    },
    show: function(){
        this.collect.showContent("deleteContentNode");
        this.collect.backNode.setStyle("display", "block");
        this.collect.backNode.removeEvents("click");
        this.collect.backNode.addEvent("click", function(){
            this.firstStep();
            this.collect.showContent("checkContentNode");
            this.collect.backNode.setStyle("display", "none");
        }.bind(this));
    }
});
