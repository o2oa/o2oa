MWF.xApplication.Setting.mobile = MWF.xApplication.Setting.mobile || {};
MWF.xApplication.Setting.mobile.Account = new Class({
    Implements: [Options, Events],

    initialize: function(explorer){
        this.explorer = explorer;
        this.app = this.explorer.app;
        this.actions = this.app.actions;
        this.css = this.app.css;
        this.content = this.explorer.accountContent;
        this.page = this.app.mobilePage;
        this.load();
    },
    load: function(){
        this.inforNode = new Element("div", {"styles": this.css.mobileAccountInforNode}).inject(this.content);
        this.actionNode = new Element("div", {"styles": this.css.mobileAccountActionNode}).inject(this.content);

        this.titleNode = new Element("div", {"styles": this.css.mobileAccountTitleNode, "text": this.app.lp.loginCollect}).inject(this.inforNode);
        this.usernameNode = new Element("div", {"styles": this.css.mobileAccountUsernameNode}).inject(this.inforNode);
        this.usernameIconNode = new Element("div", {"styles": this.css.mobileAccountUsernameIconNode}).inject(this.usernameNode);
        this.usernameInputNode = new Element("div", {"styles": this.css.mobileAccountUsernameInputNode}).inject(this.usernameNode);
        this.usernameInput = new Element("input", {
            "styles": this.css.mobileAccountUsernameInput,
            "type": "text",
            "value": "Username"
        }).inject(this.usernameInputNode);

        this.passwordNode = new Element("div", {"styles": this.css.mobileAccountPasswordNode}).inject(this.inforNode);
        this.passwordIconNode = new Element("div", {"styles": this.css.mobileAccountPasswordIconNode}).inject(this.passwordNode);
        this.passwordInputNode = new Element("div", {"styles": this.css.mobileAccountPasswordInputNode}).inject(this.passwordNode);
        this.passwordInput = new Element("input", {
            "styles": this.css.mobileAccountPasswordInput,
            "type": "password",
            "value": "password"
        }).inject(this.passwordInputNode);

        this.codeNode = new Element("div", {"styles": this.css.mobileAccountCodeNode}).inject(this.inforNode);
        this.codeIconNode = new Element("div", {"styles": this.css.mobileAccountCodeIconNode}).inject(this.codeNode);
        this.codePicNode = new Element("div", {"styles": this.css.mobileAccountCodePicNode}).inject(this.codeNode);
        this.codeInputNode = new Element("div", {"styles": this.css.mobileAccountCodeInputNode}).inject(this.codeNode);
        this.codeInput = new Element("input", {
            "styles": this.css.mobileAccountCodeInput,
            "type": "text"
        }).inject(this.codeInputNode);

        this.inforTextNode = new Element("div", {"styles": this.css.mobileAccountInforTextNode}).inject(this.inforNode);

        this.loginAction = new Element("div", {
            "styles": this.css.mobileAccountLoginActionNode,
            "text": this.app.lp.loginText
        }).inject(this.inforNode);


        this.registerAction = new Element("div", {
            "styles": this.css.mobileAccountRegisterActionNode,
            "html": this.app.lp.registerText
        }).inject(this.inforNode);

        this.app.actions.getResCollect(function(json){
            this.usernameInput.set("value", json.data.name);
            this.passwordInput.set("value", json.data.password);
        }.bind(this));

        this.getCaptcha();

        this.setEvent();

        //if (this.explorer.checkOnline){
        //    //if (!this.explorer.checkOnline.checkConnectStatus){
        //        this.maskNode = new Element("div", {"styles": this.css.mobileAccountMaskNode}).inject(this.content, "after");
        //    //}
        //}
    },
    getCaptcha: function(){
        this.app.actions.getCaptcha(function(json){
            this.codePicNode.empty();
            var picNode = new Element("img", {
                "styles": {
                    "margin-top": "10px",
                    "width": "120px",
                    "height": "40px"
                },
                "src": "data:image/png;base64,"+json.data.image
            }).inject(this.codePicNode);
            this.captchaKey = json.data.key;
            this.codePicNode.store("key", json.data.key);
        }.bind(this));
    },
    setEvent: function(){
        this.codePicNode.addEvent("click", function(){
            this.getCaptcha();
        }.bind(this));

        this.usernameInput.addEvents({
            "focus": function(){if (this.usernameInput.get("value")=="Username") this.usernameInput.set("value", "");}.bind(this),
            "blur": function(){if (this.usernameInput.get("value")=="") this.usernameInput.set("value", "Username");}.bind(this),
            "keydown": function(){this.usernameInput.setStyle("background", "transparent");}.bind(this),
        });
        this.passwordInput.addEvents({
            "focus": function(){if (this.passwordInput.get("value")=="password") this.passwordInput.set("value", "");}.bind(this),
            "blur": function(){if (this.passwordInput.get("value")=="") this.passwordInput.set("value", "password");}.bind(this)
        });
        this.codeInput.addEvents({
            "keydown": function(){this.codeInput.setStyle("background", "transparent");}.bind(this)
        });

        this.loginAction.addEvent("click", function(){
            if (this.token){
                this.logout();
            }else{
                this.login();
            }
        }.bind(this));

        this.registerAction.addEvent("click", function(){
            this.register();
        }.bind(this));
    },
    register: function(){
        new MWF.xApplication.Setting.mobile.Account.Register(this);
    },
    logout: function(){
        this.inforTextNode.set("text", "");

        this.app.actions.logoutCollect(function(json){

            var data = {
                "name": "",
                "password": "",
                "enable": false
            }
            this.app.actions.updateResCollect(data, function(){
                this.token = "";
                this.name = "";
                this.tokenType = "";

                this.loginAction.set({
                    "styles": this.css.mobileAccountLoginActionNode,
                    "text": this.app.lp.loginText
                });
                this.titleNode.set({
                    "styles": this.css.mobileAccountTitleNode,
                    "text": this.app.lp.loginCollect
                });
                this.registerAction.setStyle("display", "block");
                this.getCaptcha();
            }.bind(this));

        }.bind(this), function(xhr, text, error){
            var json = JSON.decode(xhr.responseText);
            this.inforTextNode.set("text", json.message);
            this.getCaptcha();
        }.bind(this));
    },
    login: function(){
        this.inforTextNode.set("text", "");

        var name = this.usernameInput.get("value");
        var pass = this.passwordInput.get("value");
        var code = this.codeInput.get("value");
        if (!name){
            this.inforTextNode.set("text", this.app.lp.loginInputUsername);
            this.usernameInput.setStyle("background", "#ffebeb");
            return false;
        }
        if (!code){
            this.inforTextNode.set("text", this.app.lp.loginInputCode);
            this.codeInput.setStyle("background", "#ffebeb");
            return false;
        }
        var data = {
            "credential": name,
            "password": pass
        }
        this.app.actions.loginCollect(this.captchaKey, code, data, function(json){
            var data = {
                "name": name,
                "password": pass,
                "enable": true
            }
            this.app.actions.updateResCollect(data, function(){
                this.token = json.data.token;
                this.name = json.data.name;
                this.tokenType = json.data.tokenType;

                this.loginAction.set({
                    "styles": this.css.mobileAccountLogoutActionNode,
                    "text": this.app.lp.logoutText
                });
                this.titleNode.set({
                    "styles": this.css.mobileAccountTitleLoginNode,
                    "text": this.app.lp.loggedCollect
                });
                this.registerAction.setStyle("display", "none");
            }.bind(this));
        }.bind(this), function(xhr, text, error){
            var json = JSON.decode(xhr.responseText);
            this.inforTextNode.set("text", json.message);
            this.getCaptcha();
        }.bind(this));
    },
    destroy: function(){
        this.content.destroy();
        MWF.release(this);
    }
});

MWF.xApplication.Setting.mobile.Account.Register = new Class({
    initialize: function(account){
        this.account = account;
        this.explorer = this.account.explorer;
        this.app = this.explorer.app;
        this.actions = this.app.actions;
        this.css = this.app.css;
        this.content = this.explorer.accountContent;
        this.page = this.app.mobilePage;
        this.load();
    },
    load: function(){
        this.maskNode = new Element("div", {"styles": this.css.registerMaskNode}).inject(this.app.content);
        this.areaNode = new Element("div", {"styles": this.css.registerAreaNode}).inject(this.app.content);

        this.node = new Element("div", {"styles": this.css.registerNode}).inject(this.areaNode);
        this.setNodePositionFun = this.setNodePosition.bind(this);
        this.app.addEvent("resize", this.setNodePositionFun);
        this.setNodePosition();

        this.titleNode = new Element("div", {"styles": this.css.registerTitleNode, "text": this.app.lp.registerTitle}).inject(this.node);

        this.usernameNode = new Element("div", {"styles": this.css.registerUsernameNode}).inject(this.node);
        this.usernameIconNode = new Element("div", {"styles": this.css.registerUsernameIconNode}).inject(this.usernameNode);
        this.usernameInputNode = new Element("div", {"styles": this.css.registerUsernameInputNode}).inject(this.usernameNode);
        this.usernameInput = new Element("input", {
            "styles": this.css.registerUsernameInput,
            "type": "text",
            "value": this.app.lp.companyName
        }).inject(this.usernameInputNode);

        this.phoneNode = new Element("div", {"styles": this.css.registerUsernameNode}).inject(this.node);
        this.phoneIconNode = new Element("div", {"styles": this.css.registerPhoneIconNode}).inject(this.phoneNode);
        this.phoneInputNode = new Element("div", {"styles": this.css.registerUsernameInputNode}).inject(this.phoneNode);
        this.phoneInput = new Element("input", {
            "styles": this.css.registerUsernameInput,
            "type": "text",
            "value": this.app.lp.phone
        }).inject(this.phoneInputNode);

        this.codeNode = new Element("div", {"styles": this.css.registerCodeNode}).inject(this.node);
        this.codeIconNode = new Element("div", {"styles": this.css.registerCodeIconNode}).inject(this.codeNode);
        this.codeActionNode = new Element("div", {"styles": this.css.registerCodeActionNode, "text": this.app.lp.getPhoneCode}).inject(this.codeNode);
        this.codeInputNode = new Element("div", {"styles": this.css.registerCodeInputNode}).inject(this.codeNode);
        this.codeInput = new Element("input", {
            "styles": this.css.registerCodeInput,
            "type": "text",
            "value": this.app.lp.code
        }).inject(this.codeInputNode);

        this.passwordNode = new Element("div", {"styles": this.css.registerUsernameNode}).inject(this.node);
        this.passwordIconNode = new Element("div", {"styles": this.css.registerPasswordIconNode}).inject(this.passwordNode);
        this.passwordInputNode = new Element("div", {"styles": this.css.registerUsernameInputNode}).inject(this.passwordNode);
        this.passwordInput = new Element("input", {
            "styles": this.css.registerUsernameInput,
            "type": "password"
        }).inject(this.passwordInputNode);
        this.passwordHihtNode = new Element("div", {"styles": this.css.registerPasswordHintNode, "text": this.app.lp.password}).inject(this.passwordNode);


        this.confirmNode = new Element("div", {"styles": this.css.registerUsernameNode}).inject(this.node);
        this.confirmIconNode = new Element("div", {"styles": this.css.registerPasswordIconNode}).inject(this.confirmNode);
        this.confirmInputNode = new Element("div", {"styles": this.css.registerUsernameInputNode}).inject(this.confirmNode);
        this.confirmInput = new Element("input", {
            "styles": this.css.registerUsernameInput,
            "type": "password"
        }).inject(this.confirmInputNode);
        this.confirmHihtNode = new Element("div", {"styles": this.css.registerConfirmHintNode, "text": this.app.lp.confirmPassword}).inject(this.confirmNode);

        this.registerAction = new Element("div", {
            "styles": this.css.registerActionNode,
            "text": this.app.lp.registerActionText
        }).inject(this.node);

        this.cancelAction = new Element("div", {
            "styles": this.css.registerCancelActionNode,
            "text": this.app.lp.registerCancelActionText
        }).inject(this.node);

        this.inforTextNode = new Element("div", {"styles": this.css.registerInforTextNode}).inject(this.node);

        this.setEvent();

    },
    setEvent: function(){
        this.passwordHihtNode.addEvent("mousedown", function(){
            this.passwordHihtNode.setStyle("display", "none");
            var errorNode = this.passwordInput.retrieve("errorNode");
            if (errorNode){
                errorNode.setStyle("display", "none");
                this.passwordInputNode.setStyle("background", "transparent");
            }
            this.passwordInput.focus();
        }.bind(this));
        this.confirmHihtNode.addEvent("mousedown", function(){
            this.confirmHihtNode.setStyle("display", "none");
            var errorNode = this.confirmInput.retrieve("errorNode");
            if (errorNode){
                errorNode.setStyle("display", "none");
                this.confirmInputNode.setStyle("background", "transparent");
            }
            this.confirmInput.focus();
        }.bind(this));

        this.passwordInput.addEvents({
            "blur": function(){
                if (!this.passwordInput.get("value")){this.passwordHihtNode.setStyle("display", "block");}
            }.bind(this)
        });
        this.confirmInput.addEvents({
            "blur": function(){
                if (!this.confirmInput.get("value")){this.confirmHihtNode.setStyle("display", "block");}
            }.bind(this)
        });

        this.usernameInput.addEvents({
            "focus": function(){
                if (this.usernameInput.get("value")==this.app.lp.companyName){this.usernameInput.set("value", "");}
            }.bind(this),
            "blur": function(){
                if (!this.usernameInput.get("value")){this.usernameInput.set("value", this.app.lp.companyName);}
            }.bind(this)
        });
        this.phoneInput.addEvents({
            "focus": function(){
                if (this.phoneInput.get("value")==this.app.lp.phone){this.phoneInput.set("value", "");}
            }.bind(this),
            "blur": function(){
                if (!this.phoneInput.get("value")){this.phoneInput.set("value", this.app.lp.phone);}
            }.bind(this)
        });
        this.codeInput.addEvents({
            "focus": function(){
                if (this.codeInput.get("value")==this.app.lp.code){this.codeInput.set("value", "");}
            }.bind(this),
            "blur": function(){
                if (!this.codeInput.get("value")){this.codeInput.set("value", this.app.lp.code);}
            }.bind(this)
        });

        this.codeActionNode.addEvent("click", function(){
            if (!this.codeSend) this.getCode();
        }.bind(this));

        this.registerAction.addEvent("click", function(){
            this.register();
        }.bind(this));

        this.cancelAction.addEvent("click", function(e){
            this.cancelRegister(e);
        }.bind(this));



    },
    getCode: function(){
        var phone = this.phoneInput.get("value");
        if (!phone || phone==this.app.lp.phone){
            this.fieldError(this.phoneInput, this.app.lp.phoneError);
            return false;
        }
        if (isNaN(phone)){
            this.fieldError(this.phoneInput, this.app.lp.phoneTypeError);
            return false;
        }
        this.app.actions.getCode({"mobile": phone}, function(json){
            this.codeWaitTime = 60;
            this.codeActionNode.set("text", this.app.lp.getPhoneCodeWait+"("+this.codeWaitTime+")");
            this.codeActionNode.setStyle("color", "#EEE");
            this.codeSend = true;
            this.waitCode = window.setInterval(function(){
                this.codeWaitTime--;
                this.codeActionNode.set("text", this.app.lp.getPhoneCodeWait+"("+this.codeWaitTime+")");
                if (this.codeWaitTime==0){
                    this.codeActionNode.setStyle("color", "#FFF");
                    this.codeWaitTime = 60;
                    this.codeSend = false;
                    this.codeActionNode.set("text", this.app.lp.getPhoneCode);
                    window.clearInterval(this.waitCode);
                }
            }.bind(this), 1000);
        }.bind(this));
    },

    register: function(){
        var name = this.usernameInput.get("value");
        var phone = this.phoneInput.get("value");
        var code = this.codeInput.get("value");
        var pass = this.passwordInput.get("value");
        var conf = this.confirmInput.get("value");

        var flag = true;
        if (!name || name == this.app.lp.companyName){
            this.fieldError(this.usernameInput, this.app.lp.nameError);
            flag = false;
        }
        if (!code || code == this.app.lp.code){
            this.fieldError(this.codeInput, this.app.lp.codeError);
            flag = false;
        }
        if (!phone || phone==this.app.lp.phone || isNaN(phone)){
            this.fieldError(this.phoneInput, this.app.lp.phoneTypeError);
            flag = false;
        }
        if (!pass){
            this.fieldError(this.passwordInput, this.app.lp.passwordError);
            flag = false;
        }
        if (!conf){
            this.fieldError(this.confirmInput, this.app.lp.confirmError);
            flag = false;
        }
        if (pass && conf){
            if (pass != conf){
                this.fieldError(this.confirmInput, this.app.lp.confirmPasswordError);
                flag = false;
            }
        }
        if (!flag) return false;

        this.data = {
            "mobile": phone,
            "code": code,
            "name": name,
            "password": pass
        }
        this.app.actions.register(this.data, function(json){
            //this.app.lp.registerSuccess
            //this.successNode = new Element("div", {"styles": this.css.registerErrorNode}).inject(this.node);
            this.node.empty();
            this.titleNode = new Element("div", {"styles": this.css.registerTitleNode, "text": this.app.lp.registerSuccessTitle}).inject(this.node);

            var text = this.app.lp.registerSuccess.replace(/{name}/g, name);
            var inforNode = new Element("div", {"styles": this.css.registerSuccessInforNode, "html": text}).inject(this.node);
            inforNode.addEvent("click", function(){
                this.successClose();
            }.bind(this));

            window.setTimeout(function(){
                this.successClose();
            }.bind(this), 3000);


        }.bind(this), function(xhr, text, error){
            var json = JSON.decode(xhr.responseText);
            this.inforTextNode.set("text", json.message)
        }.bind(this));
    },
    successClose: function(){
        this.account.usernameInput.set("value", this.data.name);
        this.account.passwordInput.set("value", this.data.password);
        this.account.getCaptcha();
        this.destroy();
    },
    cancelRegister: function(e){
        var _self = this;
        this.app.confirm("warm", e, this.app.lp.cancelRegisterTitle, this.app.lp.cancelRegister, 300, 120, function(){
            _self.destroy();
            this.close();
        }, function(){
            this.close();
        });
    },
    destroy: function(){
        this.maskNode.destroy();
        this.areaNode.destroy();
        MWF.release(this);
    },

    fieldError: function(field, text){
        var errorNode = field.retrieve("errorNode");
        if (!errorNode){
            errorNode = new Element("div", {"styles": this.css.registerErrorNode}).inject(field, "after");
            field.store("errorNode", errorNode);
            errorNode.store("node", field);
            errorNode.addEvent("mousedown", function(){
                var field = this.retrieve("node");
                field.getParent().setStyle("background", "transparent");
                errorNode.setStyle("display", "none");
                field.focus();
            });
        }
        field.getParent().setStyle("background", "#ffebeb");
        errorNode.set("text", text);
        errorNode.setStyle("display", "block");
    },

    setNodePosition: function(){
        var contentSize = this.areaNode.getSize();
        var size = this.node.getSize();
        var x = (contentSize.x - size.x)/2;
        var y = (contentSize.y - size.y)/2;
        this.node.setStyles({
            "top": ""+y+"px",
            "left": ""+x+"px"
        });
    }
});