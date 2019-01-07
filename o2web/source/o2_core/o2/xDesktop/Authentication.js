MWF.xDesktop = MWF.xDesktop || {};
MWF.xApplication = MWF.xApplication || {};
MWF.xDesktop.requireApp("Template", "MPopupForm", null, false);

MWF.xDesktop.Authentication = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],
    options: {
        "style": "default"
    },
    initialize: function(options, app, node){
        this.setOptions(options);
        this.node = node;
        this.path = MWF.defaultPath+"/xDesktop/$Authentication/";

        var css = null;
        MWF.UD.getPublicData("loginStyleList", function(json){
            if (json && json.enabledId){
                MWF.UD.getPublicData(json.enabledId, function(json){
                    if (json && json.data){
                        css = json.data;
                    }
                }.bind(this), false);
            }
        }.bind(this), false);

        if (!css){
            this.cssPath = MWF.defaultPath+"/xDesktop/$Authentication/"+this.options.style+"/css.wcss";
            this._loadCss();
        }else{
            this.css = css;
        }

        this.lp = MWF.LP.authentication;
        this.app = app || {};
    },
    isAuthenticated: function(success, failure){
        MWF.Actions.get("x_organization_assemble_authentication").getAuthentication(success, failure);
    },

    loadLogin: function(node){
        if (layout.config.loginPage && layout.config.loginPage.enable && layout.config.loginPage.portal){
            MWF.xDesktop.loadPortal(layout.config.loginPage.portal);
            return "";
        }
        this.popupOptions = {
            "draggable": false,
            "closeAction": false,
            "hasMask" : false,
            "relativeToApp" : false
        };
        this.popupPara = {
            container : node
        };
        this.postLogin = function( json ){
            layout.desktop.session.user = json.data;
            layout.session.user = json.data;
            layout.session.token = layout.session.user.token;
            var user = layout.desktop.session.user;

            if (user.roleList) {
                var userRoleName = [];
                user.roleList.each(function(role){
                    userRoleName.push(role.substring(0, role.indexOf("@")));
                });
                user.roleList = user.roleList.concat(userRoleName);
            }
            window.location.reload();
        }.bind(this);
        this.openLoginForm( this.popupOptions );
    },
    logout: function(){
        MWF.Actions.get("x_organization_assemble_authentication").logout(function(){
            if (this.socket){
                this.socket.close();
                this.socket = null;
            }
            Cookie.dispose("x-token", {
                "domain": ".ctc.com",
                "path": "/"
            });
            window.location.reload();
        }.bind(this));
    },
    openLoginForm: function( options, callback ){
        var opt = Object.merge( this.popupOptions || {}, options || {}, {
            onPostOk : function(json){
                if( callback )callback(json);
                if( this.postLogin  )this.postLogin( json );
                this.fireEvent("postOk",json)
            }.bind(this)
        });
        var form = new MWF.xDesktop.Authentication.LoginForm(this, {}, opt, this.popupPara );
        form.create();
    },
    openSignUpForm: function( options, callback ){
        var opt = Object.merge( this.popupOptions || {}, options || {}, {
            onPostOk : function(json){
                if( callback )callback(json);
                this.fireEvent("postOk",json)
            }.bind(this)
        } );
        var form = new MWF.xDesktop.Authentication.SignUpForm(this, {}, opt, this.popupPara );
        form.create();
    },
    openResetPasswordForm: function( options, callback ){
        var opt = Object.merge( this.popupOptions || {}, options || {}, {
            onPostOk : function(json){
                if( callback )callback(json);
                this.fireEvent("postOk",json)
            }.bind(this)
        } );
        var form = new MWF.xDesktop.Authentication.ResetPasswordForm(this, {}, opt, this.popupPara );
        form.create();
    }

});

MWF.xDesktop.Authentication.LoginForm = new Class({
    Extends: MPopupForm,
    Implements: [Options, Events],
    options: {
        "style": "default",
        "popupStyle" : "o2platform",
        "width": "650",
        "height": "460",
        "hasTop": true,
        "hasIcon": false,
        "hasTopIcon" : true,
        "hasTopContent" : true,
        "hasBottom": false,
        "hasScroll" : false,
        "hasMark": false,
        "title": (layout.config && (layout.config.systemTitle || layout.config.title)) ? (layout.config.title || layout.config.systemTitle) : MWF.LP.authentication.LoginFormTitle,
        "draggable": true,
        "closeAction": true
    },

    //Camera Login
    _createTopContent: function (){
        this.actions = MWF.Actions.get("x_organization_assemble_authentication");

        this.faceLogin = false;
        this.actions.getLoginMode(function( json ){
            this.faceLogin = json.data.faceLogin;
        }.bind(this), null, false);

        if (this.faceLogin){
            if (navigator.mediaDevices && navigator.mediaDevices.getUserMedia) {
                COMMON.AjaxModule.loadDom("/o2_lib/adapter/adapter.js", function () {
                    if (navigator.mediaDevices && navigator.mediaDevices.getUserMedia) {
                        //暂时隐藏此功能
                        this.cameraLoginIcon = new Element("div", {"styles": this.explorer.css.cameraLoginIcon}).inject(this.formTopContentNode);
                        this.cameraLoginIcon.addEvent("click", function () {
                            if (!this.isCameraLogin) {
                                this.cameraLogin();
                                this.isCameraLogin = true;
                            } else {
                                this.closeCamera();
                                this.isCameraLogin = false;
                            }
                        }.bind(this));
                    }
                }.bind(this));
            }
        }
    },
    closeCamera: function(){
        if (this.cameraLoginVideoNode){
            if (this.video) this.video.destroy();
            if (this.canvas) this.canvas.destroy();
            if (this.cameraLoginVideoNode) this.cameraLoginVideoNode.destroy();
            this.video = null;
            this.canvas = null;
            this.cameraLoginVideoNode = null;
        }
        this.cameraLoginIcon.setStyles(this.explorer.css.cameraLoginIcon);
    },
    cameraLoginInit: function(){
        this.cameraLoginConfig = {
            "maxStep": 1,
            "step": 0,
            "count": 0,
            "max": 2,
            "errorCount": 0,
            "errorMax": 3,
            "user": "",
            "tokens":[]
        };
    },
    cameraLoginReset: function(resetError){
        this.cameraLoginConfig.count = 0;
        this.cameraLoginConfig.user = "";
        if (resetError) this.cameraLoginConfig.errorCount = 0;
    },
    cameraLogin: function(){
        this.cameraLoginInit();
        this.cameraLoginIcon.setStyles(this.explorer.css.closeCameraLoginIcon);
        this.createCameraLoginNode();
        this.startCameraLogin();
    },
    createCameraLoginNode: function(){
        this.cameraLoginVideoNode = new Element("div", {"styles": this.explorer.css.cameraLoginVideoNode}).inject(this.container);
        var size = this.formNode.getSize();
        var topSize = this.formTopNode.getSize();
        var h = size.y-topSize.y;
        this.cameraLoginVideoNode.setStyles({
            "width": ""+size.x+"px",
            "height": ""+h+"px"
        });
        this.cameraLoginVideoNode.position({
            "relativeTo": this.formContentNode,
            "position": "upperLeft",
            "edge": "upperLeft"
        });
    },
    startCameraLogin: function(){
        this.cameraLoginVideoAreaNode = new Element("div").inject(this.cameraLoginVideoNode);
        this.cameraLoginVideoInfoNode = new Element("div", {"styles": this.explorer.css.cameraLoginVideoInfoNode}).inject(this.cameraLoginVideoNode);
        this.cameraLoginVideoInfoNode.set("text", MWF.LP.desktop.login.camera_logining);
        var size = this.cameraLoginVideoNode.getSize();
        var infoSize = this.cameraLoginVideoInfoNode.getSize();
        var h = size.y-infoSize.y;
        this.cameraLoginVideoAreaNode.setStyle("height", ""+h+"px");

        this.cameraLoginVideoAreaNode.set("html", "<video autoplay></video>");
        this.video = this.cameraLoginVideoAreaNode.getFirst().setStyles({
            "background-color": "#000000",
            "width": ""+size.x+"px",
            "height": ""+h+"px"
        });
        this.videoStart();
    },
    videoStart: function(){
        alert("??????");
        debugger;
        this.video.addEventListener("canplay", function(){
            window.setTimeout(function(){
                this.startCameraAuthentication();
            }.bind(this), 500);
        }.bind(this));

        navigator.mediaDevices.getUserMedia({
            audio: false,
            video: true
        }).then(function(stream){
            this.video.srcObject = stream;
        }.bind(this))["catch"](function(error){
            console.log('navigator.getUserMedia error: ', error);
            this.closeCamera();
        }.bind(this));
    },
    getFormData: function(){
        if (!this.canvas){
            this.canvas = new Element("canvas", {"styles": {"display": "none"}}).inject(this.cameraLoginVideoNode);
            this.canvas.width = this.video.videoWidth;
            this.canvas.height = this.video.videoHeight;
        }
        this.canvas.getContext('2d').drawImage(this.video, 0, 0, this.canvas.width, this.canvas.height);

        var blob = this.toBlob(this.canvas.toDataURL());
        var formData = new FormData();
        formData.append('file', blob);

        this.canvas.destroy();
        return {"data":formData, "size": blob.size};
    },
    //检测出身份
    checkUserFace: function(formData){

        var faceset = window.location.host;
        faceset = faceset.replace(/\./g, "_");
        this.faceAction.search(faceset, formData.data, {"name": "pic", "size": formData.size}, function(json){
            if (json.data.results && json.data.results.length){
                var hold = json.data.thresholds["1e-5"];
                if (json.data.results[0].confidence>hold){
                    var token = json.data.faces[0].face_token;
                    var user = json.data.results[0].user_id;
                    if ((!this.cameraLoginConfig.user) || (this.cameraLoginConfig.user===user)){
                        this.cameraLoginConfig.count++;
                        this.cameraLoginConfig.user = user;
                        this.cameraLoginConfig.tokens.push(token);
                        this.cameraLoginConfig.step++;
                        this.cameraLoginConfig.errorCount = 0;
                        this.cameraLoginVideoInfoNode.set("text", MWF.LP.desktop.login["camera_logining_"+this.cameraLoginConfig.step]);
                    }else{
                        this.cameraLoginReset();
                        this.cameraLoginConfig.errorCount++;
                    }
                }else{
                    this.cameraLoginReset();
                    this.cameraLoginConfig.errorCount++;
                }
            }else{
                this.cameraLoginReset();
                this.cameraLoginConfig.errorCount++;
            }
            this.checkCameraLogin();
        }.bind(this), function(){
            window.setTimeout(function(){ this.startCameraAuthentication(); }.bind(this), 500);
        }.bind(this));
    },
    checkUserAlive: function(formData, attr, method){
        this.faceAction.detectattr(attr, formData.data, {"name": "pic", "size": formData.size}, function(json){
            if (json.data.faces && json.data.faces.length){
                if (this[method](json.data.faces[0].attributes)){
                    this.cameraLoginConfig.step++;
                    this.cameraLoginConfig.errorCount = 0;
                    this.cameraLoginVideoInfoNode.set("text", MWF.LP.desktop.login["camera_logining_"+this.cameraLoginConfig.step]);
                }else{
                    //可能是照片
                    this.cameraLoginConfig.errorCount++;
                }
            }else{
                this.cameraLoginConfig.errorCount++;
            }
            this.checkCameraLogin();
        }.bind(this), function(){
            this.cameraLoginConfig.errorCount++;
            this.checkCameraLogin();
        }.bind(this));
    },
    //检测微笑
    checkUserSmile: function(attr){
        return attr.smile.value>attr.smile.threshold;
    },
    //检测抬头
    checkUserPitch: function(attr){
        return attr.headpose.pitch_angle<-10;
    },
    startCameraAuthentication: function(){
        if (this.video){
            var formData = this.getFormData();
            if (!this.faceAction) this.faceAction = MWF.Actions.get("x_faceset_control");

            if (this.cameraLoginConfig.step===0){
                this.checkUserFace(formData);
            }
            if (this.cameraLoginConfig.step===1){
                this.checkUserAlive(formData, "smiling", "checkUserSmile");
            }
            if (this.cameraLoginConfig.step===2){
                this.checkUserAlive(formData, "headpose", "checkUserPitch");
            }
        }
    },

    toBlob : function( base64 ){
        var bytes;
        if( base64.substr( 0, 10 ) === 'data:image' ){
            bytes=window.atob(base64.split(',')[1]);
        }else{
            bytes=window.atob(base64);
        }
        var ab = new ArrayBuffer(bytes.length);
        var ia = new Uint8Array(ab);
        for (var i = 0; i < bytes.length; i++) {
            ia[i] = bytes.charCodeAt(i);
        }
        return new Blob( [ab] , {type : "image/png" });
    },
    getDIF: function(arr){
        var max = Math.max.apply(this, arr);
        var min = Math.min.apply(this, arr);
        return max-min;
    },
    checkCameraLogin: function(){
        if (this.cameraLoginConfig.errorCount>this.cameraLoginConfig.errorMax){
            this.cameraLoginVideoInfoNode.set("text", MWF.LP.desktop.login.camera_loginError);
        }else{
            if (this.cameraLoginConfig.step>=this.cameraLoginConfig.maxStep){
                var text = MWF.LP.desktop.login.camera_loginSuccess.replace("{name}", this.cameraLoginConfig.user);
                this.cameraLoginVideoInfoNode.set("text", text);
                this.cameraLoginSuccess();
            }else{
                window.setTimeout(function(){ this.startCameraAuthentication(); }.bind(this), 100);
            }
        }
    },

    cameraLoginSuccess: function(){
        COMMON.AjaxModule.loadDom(["/o2_lib/CryptoJS/tripledes.js", "/o2_lib/CryptoJS/mode-ecb.js"], function(){
            //COMMON.AjaxModule.loadDom(, function(){

                var addressObj = layout.desktop.serviceAddressList["x_organization_assemble_authentication"];
                var url = layout.config.app_protocol+"//"+addressObj.host+(addressObj.port===80 ? "" : ":"+addressObj.port)+addressObj.context;

                var code = this.crypDES();
                var json = {"client": "oa", "token": code};
                var res = new Request.JSON({
                    "method": "POST",
                    "url" : url+"/jaxrs/sso",
                    "data": JSON.stringify(json),
                    secure: false,
                    emulation: false,
                    noCache: true,
                    withCredentials: true,
                    "headers": {
                        "Content-Type": "application/json; charset=utf-8"
                    },
                    onSuccess : function(responseJSON) {
                        this._close();
                        this.closeCamera();
                        if( this.formMaskNode )this.formMaskNode.destroy();
                        this.formAreaNode.destroy();
                        if (this.explorer && this.explorer.view)this.explorer.view.reload();
                        if(this.app)this.app.notice( this.lp.loginSuccess, "success");
                        this.fireEvent("postOk",responseJSON);
                    }.bind(this),
                    onError : function() {
                        this.cameraLoginVideoInfoNode.set("text", MWF.LP.desktop.login.camera_loginError2);
                    }.bind(this)
                });
                res.send();

            //}.bind(this));
        }.bind(this));
    },

    crypDES: function(){
        var key = "xplatform";
        var userId = this.cameraLoginConfig.user;
        var d = (new Date()).getTime();

        var keyHex = CryptoJS.enc.Utf8.parse(key);

        var xtoken = CryptoJS.DES.encrypt(userId + "#" + d, keyHex, {
            mode : CryptoJS.mode.ECB,
            padding : CryptoJS.pad.Pkcs7
        });
        var str = xtoken.ciphertext.toString(CryptoJS.enc.Base64);
        str = str.replace(/=/g, "");
        str = str.replace(/\+/g, "-");
        str = str.replace(/\//g, "_");
        return str;
    },

    _createTableContent: function () {
        this.loginType = "captcha";
        this.codeLogin=false;
        this.bindLogin=false;
        this.actions.getLoginMode(function( json ){
            this.codeLogin = json.data.codeLogin;
            this.bindLogin = json.data.bindLogin;
        }.bind(this), null, false);

        MWF.Actions.get("x_organization_assemble_personal").getRegisterMode(function(json){
            this.signUpMode = json.data.value;
        }.bind(this), null ,false);

        if( this.bindLogin ){
            this.bindLoginTipPic = new Element("div.bindLoginTipPic", {styles : this.css.bindLoginTipPic}).inject( this.formContentNode, "top" );
            this.bindLoginAction = new Element("div.bindLoginAction", {styles : this.css.bindLoginAction}).inject( this.formContentNode, "top" );
            this.bindLoginAction.addEvent("click", function(){
                this.showBindCodeLogin();
            }.bind(this));

            this.backtoPasswordLoginTipPic = new Element("div.backtoPasswordLoginTipPic", {styles : this.css.backtoPasswordLoginTipPic}).inject( this.formContentNode, "top" );
            this.backtoPasswordLoginAction = new Element("div.backtoPasswordLoginAction", {styles : this.css.backtoPasswordLoginAction}).inject( this.formContentNode, "top" );
            this.backtoPasswordLoginAction.addEvent("click", function(){
                this.backtoPasswordLogin();
            }.bind(this));
        }

        var html =
            "<table width='100%' bordr='0' cellpadding='0' cellspacing='0' styles='formTable'>" +
            "<tr><div><div item='passwordAction'>";
        if( this.codeLogin ){
            html+="</div><div styles='titleSep'></div><div item='codeAction'></div></tr>";
        }
        html+="</table>";

        html+="<table width='100%' bordr='0' cellpadding='0' cellspacing='0' styles='formTable'>" +
            "<tr item='credentialTr'><td styles='formTableValueTop20' item='credential'></td></tr>" +
            "<tr item='passwordTr'><td styles='formTableValueTop20' item='password'></td></tr>" +
            "<tr item='captchaTr'><td styles='formTableValueTop20'>"+
            "<div item='captchaAnswer' style='float:left;'></div><div item='captchaPic' style='float:left;'></div><div item='changeCaptchaAction' style='float:left;'></div>"+
            "</td></tr>";
        if( this.codeLogin ){
            html += "<tr item='codeTr' style='display: none'><td styles='formTableValueTop20'>"+
                "   <div item='codeAnswer' style='float:left;'></div>"+
                "   <div item='verificationAction' style='float:left;'></div>"+
                "   <div item='resendVerificationAction' style='float:left;display:none;'></div>" +
                "</td></tr>";
        }
        html +=  "<tr><td styles='formTableValueTop20' item='loginAction'></td></tr>" +
            "</table>"+
            "<table width='100%' bordr='0' cellpadding='0' cellspacing='0' styles='formTable'>";
        if( this.signUpMode && this.signUpMode !== "disable" ){
            html += "<tr><td><div item='signUpAction'></div><div item='forgetPassword'></div></td></tr>";
        }else{
            html += "<tr><td><div styles='signUpAction'></div><div item='forgetPassword'></div></td></tr>";
        }
        html +=	"<tr><td  styles='formTableValue' item='errorArea'></td></tr>" +
              "<tr><td  styles='formTableValue' item='oauthArea'></td></tr>" +
            "</table>";

        this.formTableArea.set("html", html);
        new Element("div", {
            "styles": {
                "text-align": "center",
                "height": "40px",
                "line-height": "40px"
            },
            "text": layout.config.footer || layout.config.systemName
        }).inject(this.formTableArea, "after");


        this.setCaptchaPic();
        this.errorArea = this.formTableArea.getElement("[item=errorArea]");

        this.oauthArea = this.formTableArea.getElement("[item=oauthArea]");

        MWF.xDesktop.requireApp("Template", "MForm", function () {
            this.form = new MForm(this.formTableArea, this.data, {
                style: this.options.popupStyle,
                verifyType : "single",	//batch一起校验，或alert弹出
                isEdited: this.isEdited || this.isNew,
                itemTemplate: {
                    credential: {
                        text: this.lp.userName,
                        defaultValue : this.lp.userName,
                        className : "inputUser",
                        notEmpty : true,
                        defaultValueAsEmpty: true,
                        emptyTip:  this.lp.inputYourUserName,
                        event : {
                            focus : function( it ){
                                if (this.lp.userName === it.getValue()) it.setValue("");
                                if (!it.warningStatus) it.getElements()[0].setStyles( this.css.inputActive );
                            }.bind(this),
                            blur : function( it ){
                                if ("" === it.getValue()) it.setValue(this.lp.userName);
                                if (!it.warningStatus) it.getElements()[0].setStyles( this.css.inputUser );
                            }.bind(this),
                            keyup : function( it, ev ){
                                if (ev.event.keyCode === 13) this.ok();
                            }.bind(this)
                        },
                        onEmpty : function( it ){
                            it.getElements()[0].setStyles(this.css.inputEmpty);
                        }.bind(this),
                        onUnempty : function( it ){
                            it.getElements()[0].setStyles(this.css.inputUser);
                        }.bind(this)
                    },
                    password : {
                        text: this.lp.password,
                        type : "password",
                        defaultValue : "password",
                        className : "inputPassword",
                        notEmpty : true,
                        defaultValueAsEmpty: true,
                        emptyTip:  this.lp.inputYourPassword,
                        event : {
                            focus : function(it){
                                if ("password" === it.getValue()) it.setValue("");
                                if(!it.warningStatus) it.getElements()[0].setStyles(this.css.inputActive);
                            }.bind(this),
                            blur : function( it ){
                                if (!it.warningStatus) it.getElements()[0].setStyles(this.css.inputPassword);
                            }.bind(this),
                            keyup : function(it, ev){
                                if( ev.event.keyCode === 13 ) this.ok();
                            }.bind(this)
                        },
                        onEmpty : function( it ){
                            it.getElements()[0].setStyles( this.css.inputEmpty );
                        }.bind(this),
                        onUnempty : function( it ){
                            it.getElements()[0].setStyles(  this.css.inputPassword );
                        }.bind(this)
                    },
                    captchaAnswer : {
                        tType:"number",
                        text: this.lp.verificationCode,
                        defaultValue : this.lp.verificationCode,
                        className : "inputVerificationCode",
                        notEmpty : true,
                        defaultValueAsEmpty: true,
                        emptyTip:  this.lp.inputPicVerificationCode,
                        event : {
                            focus : function( it ){
                                if (this.lp.verificationCode === it.getValue()) it.setValue("");
                                if (!it.warningStatus) it.getElements()[0].setStyles( this.css.inputActive );
                            }.bind(this),
                            blur : function( it ){
                                if ("" === it.getValue()) it.setValue(this.lp.verificationCode);
                                if (!it.warningStatus) it.getElements()[0].setStyles( this.css.inputVerificationCode );
                            }.bind(this),
                            keyup : function( it, ev ){
                                if (ev.event.keyCode === 13) this.ok();
                            }.bind(this)
                        },
                        onEmpty : function( it ){
                            it.getElements()[0].setStyles( this.css.inputEmpty );
                        }.bind(this),
                        onUnempty : function( it ){
                            it.getElements()[0].setStyles(  this.css.inputVerificationCode );
                        }.bind(this)
                    },
                    changeCaptchaAction :{
                        value : this.lp.changeVerification,
                        type : "innerText",
                        className : "verificationChange",
                        event : {
                            click : function(){
                                this.setCaptchaPic();
                            }.bind(this)
                        }
                    },
                    codeAnswer : {
                        text: this.lp.verificationCode,
                        defaultValue : this.lp.inputVerificationCode,
                        className : "inputVerificationCode2",
                        notEmpty : true,
                        defaultValueAsEmpty: true,
                        emptyTip: this.lp.inputVerificationCode,
                        event : {
                            focus : function( it ){
                                if (this.lp.inputVerificationCode === it.getValue()) it.setValue("");
                                if (!it.warningStatus) it.getElements()[0].setStyles( this.css.inputActive );
                            }.bind(this),
                            blur : function( it ){
                                if ("" === it.getValue()) it.setValue(this.lp.inputVerificationCode);
                                if (!it.warningStatus) it.getElements()[0].setStyles( this.css.inputVerificationCode2 );
                            }.bind(this),
                            keyup : function( it, ev ){
                                if (ev.event.keyCode === 13) this.ok();
                            }.bind(this)
                        },
                        onEmpty : function( it ){
                            it.getElements()[0].setStyles( this.css.inputEmpty );
                        }.bind(this),
                        onUnempty : function( it ){
                            it.getElements()[0].setStyles(  this.css.inputVerificationCode2 );
                        }.bind(this)
                    },
                    verificationAction : {
                        value: this.lp.sendVerification,
                        type : "button",
                        className : "inputSendVerification",
                        event : {
                            click : function(){
                                this.sendVerificationAction();
                            }.bind(this)
                        }
                    },
                    resendVerificationAction : {
                        value: this.lp.resendVerification,
                        type : "button",
                        className : "inputResendVerification"
                    },
                    loginAction: {
                        value: this.lp.loginAction,
                        type : "button",
                        className : "inputLogin",
                        event : {
                            click : function(){
                                this.ok();
                            }.bind(this)
                        }
                    },
                    passwordAction: {
                        value : this.lp.passwordLogin,
                        type : "innerText",
                        className : "titleNode_active",
                        event : {
                            click : function(){
                                if( this.codeLogin )this.showPasswordLogin();
                            }.bind(this)
                        }
                    },
                    codeAction: {
                        value : this.lp.codeLogin,
                        type : "innerText",
                        className : "titleNode_normal",
                        event : {
                            click : function(){
                                this.showCodeLogin();
                            }.bind(this)
                        }
                    },
                    signUpAction : {
                        value : this.lp.signUp,
                        type : "innerText",
                        className : "signUpAction",
                        event : {
                            click : function(){
                                this.gotoSignup();
                            }.bind(this)
                        }
                    },
                    forgetPassword : {
                        value : this.lp.forgetPassword,
                        type : "innerText",
                        className : "forgetPassword",
                        event :{
                            click : function(){
                                this.gotoResetPassword();
                            }.bind(this)
                        }
                    }
                }
            }, this.app, this.css);
            this.form.load();
        }.bind(this), true);

        if( this.bindLogin ){
            this.bindLoginContainer = new Element("div", {
                styles : this.css.bindLoginContainer
            }).inject( this.formContentNode );

            var html2 = "<div item='bindLoginTitle' styles='bindTitleNode'></div>"+
                "<div styles='bindBodyArea'>" +
                "<div item='bindPicArea' styles='bindPicArea'></div>" +
                "<div styles='bindSepArea'></div>" +
                "<div styles='bindExampleArea'></div>" +
                "</div>"+
                "<div styles='bindTipArea'>"+
                "   <div styles='bindTipIconArea'></div>" +
                "   <div styles='bindTipTextArea'>"+
                "       <div>打开<div styles='bindTipLinkArea'>O2APP</div>扫一扫</div>" +
                "       <div>登录网页版</div>" +
                "</div>";

            this.bindLoginContainer.set("html",html2);

            this.isShowEnable = true;
            this.bindBodyArea = this.bindLoginContainer.getElement("[styles='bindBodyArea']");
            this.bindLoginContainer.addEvent("mousemove", function(ev){
                if( this.bindBodyArea.isOutside( ev ) ){
                    this.hideExampleArea(ev);
                }else{
                    this.showExampleArea(ev);
                }
            }.bind(this));

            this.bindPicArea = this.bindLoginContainer.getElement("[item='bindPicArea']");
            this.setBindPic();
            this.bindExampleArea = this.bindLoginContainer.getElement("[styles='bindExampleArea']");
            this.bindSepArea = this.bindLoginContainer.getElement("[styles='bindSepArea']");
            var link = this.bindLoginContainer.getElement("[styles='bindTipLinkArea']");
            link.addEvent("click", function(){
                window.open( this.lp.o2downloadLink, "_blank" );
            }.bind(this));

            MWF.xDesktop.requireApp("Template", "MForm", function () {
                this.bindform = new MForm(this.bindLoginContainer, {}, {
                    style: "o2platform",
                    verifyType : "single",	//batch一起校验，或alert弹出
                    isEdited: this.isEdited || this.isNew,
                    itemTemplate: {
                        bindLoginTitle: {value : this.lp.bingLoginTitle, type : "innerText"}
                    }
                }, this.app, this.css);
                this.bindform.load();
            }.bind(this), true);
        }

        this.loadOauthContent()

    },
    _beforeFormNodeSize : function(){
        if( !this.isPlusOauthSize && this.oauthListNode ){
            this.options.height = parseInt(this.options.height) + this.oauthArea.getSize().y ;
            this.isPlusOauthSize = true;
        }
    },
    loadOauthContent : function(){
        this.actions.listOauthServer(function( json ){
            this.oauthList = json.data || [];
            if( this.oauthList.length > 0 ){
                this.oauthListNode = new Element("div", {  styles : this.css.oauthListNode } ).inject( this.oauthArea );
                this.oauthList.each( function( d ){
                    this.loadOauthItem( d );
                }.bind(this));
            }
        }.bind(this), null, false);
    },
    loadOauthItem : function( data ){
        var url = data.icon.indexOf("http") == 0 ? data.icon : ("data:image/png;base64,"+data.icon);

        var itemNode = new Element("div", {
            styles : this.css.oauthItemNode,
            events : {
                click : function(){
                    window.location = "/x_desktop/oauth.html?oauth="+ encodeURIComponent(this.name);
                }.bind(data)
            }
        } ).inject( this.oauthListNode );
        var iconNode = new Element("img", {
            styles : this.css.oauthItemIconNode,
            src : url
        } ).inject( itemNode );
        var textNode = new Element("div", {
            styles : this.css.oauthItemTextNode,
            text : data.name
        } ).inject( itemNode );
    },
    showExampleArea: function( ){
        if( this.isHiddingExample || this.isShowingExample)return;
        if( !this.isShowEnable )return;
        this.isShowingExample = true;
        var left = this.bindBodyArea.getPosition( this.bindBodyArea.getParent()).x;
        this.intervalId = setInterval( function(){
            if( left > 136 ){
                this.bindBodyArea.setStyle("margin-left",(left-5)+"px");
                left = left -5;
            }else{
                clearInterval(this.intervalId);
                this.bindBodyArea.setStyle("width","400px");
                this.bindSepArea.setStyle("display", "");
                this.bindExampleArea.setStyle("display", "");
                this.isHidEnable = true;
                this.isShowEnable = false;
                this.isShowingExample = false;
            }
        }.bind(this), 10 )
    },
    hideExampleArea: function( ){
        if( this.isShowingExample || this.isHiddingExample )return;
        if( !this.isHidEnable )return;
        this.isHiddingExample = true;
        var left = this.bindBodyArea.getPosition( this.bindBodyArea.getParent()).x;
        this.bindSepArea.setStyle("display", "none");
        this.bindExampleArea.setStyle("display", "none");
        this.intervalId2 = setInterval( function(){
            if( left < 220 ){
                this.bindBodyArea.setStyle("margin-left",(left+5)+"px");
                left = left + 5;
            }else{
                clearInterval(this.intervalId2);
                this.bindBodyArea.setStyle("width","204px");
                this.isHidEnable = false;
                this.isShowEnable = true;
                this.isHiddingExample = false;
            }
        }.bind(this), 10 )
    },
    showPasswordLogin: function(){
        this.errorArea.empty();
        this.loginType = "captcha";
        this.form.getItem("passwordAction").setStyles(this.css.titleNode_active);
        this.form.getItem("codeAction").setStyles(this.css.titleNode_normal);
        this.formTableArea.getElement("[item='passwordTr']").setStyle("display","");
        this.formTableArea.getElement("[item='captchaTr']").setStyle("display","");
        this.formTableArea.getElement("[item='codeTr']").setStyle("display","none");
    },
    showCodeLogin: function(){
        this.errorArea.empty();
        this.loginType = "code";
        this.form.getItem("passwordAction").setStyles(this.css.titleNode_normal);
        this.form.getItem("codeAction").setStyles(this.css.titleNode_active);
        this.formTableArea.getElement("[item='passwordTr']").setStyle("display","none");
        this.formTableArea.getElement("[item='captchaTr']").setStyle("display","none");
        this.formTableArea.getElement("[item='codeTr']").setStyle("display","");

    },
    showBindCodeLogin:function(){
        this.errorArea.empty();
        this.formTableContainer.setStyle("display","none");
        this.bindLoginContainer.setStyle("display","");
        this.bindLoginTipPic.setStyle("display","none");
        this.bindLoginAction.setStyle("display","none");
        this.backtoPasswordLoginTipPic.setStyle("display","");
        this.backtoPasswordLoginAction.setStyle("display","");
        this.checkBindStatus();
    },
    backtoPasswordLogin: function(){
        this.errorArea.empty();
        if( this.bindStatusInterval )clearInterval( this.bindStatusInterval );
        this.formTableContainer.setStyle("display","");
        this.bindLoginContainer.setStyle("display","none");
        this.bindLoginTipPic.setStyle("display","");
        this.bindLoginAction.setStyle("display","");
        this.backtoPasswordLoginTipPic.setStyle("display","none");
        this.backtoPasswordLoginAction.setStyle("display","none");
    },
    setBindPic: function(){
        this.bindPicArea.empty();
        this.actions.getLoginBind( function( json ){
            this.bindMeta = json.data.meta;
            new Element("img", {
                src : "data:image/png;base64,"+json.data.image
            }).inject( this.bindPicArea );
        }.bind(this))
    },
    setCaptchaPic: function(){
        var captchaPic = this.formTableArea.getElement("[item='captchaPic']");
        captchaPic.empty();
        this.actions.getLoginCaptcha(120, 50, function( json ){
            this.captcha = json.data.id;
            new Element("img", {
                src : "data:image/png;base64,"+json.data.image,
                styles : this.css.verificationImage
            }).inject( captchaPic );
        }.bind(this))
    },
    sendVerificationAction: function(){
        var flag = true;
        var credentialItem = this.form.getItem("credential");
        var credential = credentialItem.getValue();
        if( !credential || credential.trim() === "" ){
            credentialItem.setWarning( this.lp.inputYourUserName, "empty");
            return;
        }else{
            this.actions.checkCredential( credential, function( json ){
                if( !json.data.value ){
                    flag = false;
                    credentialItem.setWarning( this.lp.userNotExist , "invalid");
                }
            }.bind(this),function( errorObj ){
                flag = false;
                var error = JSON.parse( errorObj.responseText );
                credentialItem.setWarning( error.message , "invalid");
            }.bind(this), false )
        }
        if( !flag ){
            return;
        }else{
            credentialItem.clearWarning("invalid");
        }
        this.actions.createCredentialCode( credential, function( json ){
        }, function( errorObj ){
            var error = JSON.parse( errorObj.responseText );
            this.setWarning( error.message );
            flag = false
        }.bind(this));
        if( !flag ){
            return;
        }else{
            this.errorArea.empty();
        }
        this.form.getItem("verificationAction").container.setStyle("display","none");
        this.setResendVerification();
    },
    setResendVerification: function(){
        var resendItem = this.form.getItem("resendVerificationAction");
        resendItem.container.setStyle("display","");
        this.resendElement = resendItem.getElements()[0];
        this.resendElement.set("text", this.lp.resendVerification + "(60)");

        var i=60;
        this.timer = setInterval( function(){
            if( i > 0 ){
                this.resendElement.set("text", this.lp.resendVerification + "("+ --i +")")
            }else{
                this.form.getItem("verificationAction").container.setStyle("display","");
                resendItem.container.setStyle("display","none");
                clearInterval( this.timer )
            }
        }.bind(this), 1000 )
    },
    gotoSignup: function(){
        this.explorer.openSignUpForm();
        this.close();
    },
    gotoResetPassword: function(){
        this.explorer.openResetPasswordForm();
        this.close();
    },
    checkBindStatus: function(){
        this.bindStatusInterval = setInterval( function(){
            this.actions.checkBindStatus( this.bindMeta, function( json ){
                if( json.data ){
                    if( json.data.name && json.data.name !== "anonymous" ){
                        this.fireEvent("queryOk");
                        this._close();
                        if(this.formMaskNode)this.formMaskNode.destroy();
                        this.formAreaNode.destroy();
                        if (this.explorer && this.explorer.view)this.explorer.view.reload();
                        if(this.app)this.app.notice( this.lp.loginSuccess, "success");
                        this.fireEvent("postOk", json);
                    }
                }
            }.bind(this), function( errorObj ){
                //var error = JSON.parse( errorObj.responseText );
                //this.setWarning( error.message );
            }.bind(this))
        }.bind(this) , 3000 );

    },
    _close: function(){
        if( this.bindStatusInterval )clearInterval( this.bindStatusInterval );
        if(this.timer) clearInterval( this.timer );
    },
    ok: function () {
        this.fireEvent("queryOk");
        this.errorArea.empty();
        var captchaItem = null;
        var codeItem = null;
        if( this.loginType === "captcha" ){
            this.form.getItem("password").options.notEmpty = true;

            captchaItem = this.form.getItem("captchaAnswer");
            if( captchaItem )captchaItem.options.notEmpty = true;

            codeItem = this.form.getItem("codeAnswer");
            if(codeItem)codeItem.options.notEmpty = false;
        }else if( this.loginType === "code" ){
            this.form.getItem("password").options.notEmpty = false;

            captchaItem = this.form.getItem("captchaAnswer");
            if( captchaItem )captchaItem.options.notEmpty = false;

            codeItem = this.form.getItem("codeAnswer");
            if(codeItem)codeItem.options.notEmpty = true;
        }
        var data = this.form.getResult(true, ",", true, false, true);
        if (data) {
            this._ok(data, function (json) {
                if (json.type === "error") {
                    if(this.app)this.app.notice(json.message, "error");
                } else {
                    this._close();
                    if( this.formMaskNode ) this.formMaskNode.destroy();
                    this.formAreaNode.destroy();
                    if (this.explorer && this.explorer.view)this.explorer.view.reload();
                    if(this.app)this.app.notice( this.lp.loginSuccess, "success");
                    this.fireEvent("postOk",json);
                }
            }.bind(this));
        }
    },
    setWarning : function(text){
        this.errorArea.empty();
        new Element("div",{
            "text" : text,
            "styles" : this.css.warningMessageNode
        }).inject( this.errorArea );
    },
    _ok: function (data, callback) {
        var d = null;
        if( this.loginType === "captcha" ){
            d = {
                credential : data.credential,
                password : data.password,
                captchaAnswer : data.captchaAnswer,
                captcha : this.captcha
            };
            this.actions.loginByCaptcha( d, function( json ){
                if( callback )callback( json );
                //this.fireEvent("postOk")
            }.bind(this), function( errorObj ){
                var error = JSON.parse( errorObj.responseText );
                this.setWarning( error.message );
                this.setCaptchaPic();
                this.form.getItem( "captchaAnswer").setValue("");
            }.bind(this));
        }else if( this.loginType === "code" ){
            d = {
                credential : data.credential,
                codeAnswer : data.codeAnswer
            };
            this.actions.loginByCode( d, function( json ){
                if( callback )callback( json );
                //this.fireEvent("postOk")
            }.bind(this), function( errorObj ){
                var error = JSON.parse( errorObj.responseText );
                this.setWarning( error.message );
            }.bind(this ) );
        }
    }
});

MWF.xDesktop.Authentication.SignUpForm = new Class({
    Extends: MPopupForm,
    Implements: [Options, Events],
    options: {
        "style": "default",
        "popupStyle" : "o2platformSignup",
        "width": "900",
        "height": "620",
        "hasTop": true,
        "hasIcon": false,
        "hasTopIcon" : true,
        "hasTopContent" : true,
        "hasBottom": false,
        "title": MWF.LP.authentication.SignUpFormTitle,
        "draggable": true,
        "closeAction": true
    },
    _createTableContent: function () {

        this.actions = MWF.Actions.get("x_organization_assemble_personal");

        var signUpMode = "code";
        this.actions.getRegisterMode(function(json){
            signUpMode = json.data.value;
        }.bind(this), null ,false);

        this.formTableContainer.setStyles({
            "width" : "900px",
            "margin-top" : "50px"
        });

        var html = "<table width='100%' bordr='0' cellpadding='5' cellspacing='0' styles='formTable'>" +
            "<tr><td styles='formTableTitle' lable='name' width='200'></td>" +
            "   <td styles='formTableValue' item='name' width='350'></td>" +
            "   <td styles='formTableValue' item='nameTip'></td></tr>" +
            "<tr><td styles='formTableTitle' lable='password'></td>" +
            "   <td styles='formTableValue' item='password'></td>" +
            "   <td styles='formTableValue'><div item='passwordStrengthArea'></div></div><div item='passwordTip'></div></td></tr>" +
            "<tr><td styles='formTableTitle' lable='confirmPassword'></td>" +
            "   <td styles='formTableValue' item='confirmPassword'></td>" +
            "   <td styles='formTableValue' item='confirmPasswordTip'></td></tr>" +
            "<tr><td styles='formTableTitle' lable='genderType'></td>" +
            "   <td styles='formTableValue' item='genderType'></td>" +
            "   <td styles='formTableValue' item='genderTypeTip'></td></tr>" +
            //"<tr><td styles='formTableTitle' lable='mail'></td>" +
            //"   <td styles='formTableValue' item='mail'></td>" +
            //"   <td styles='formTableValue' item='mailTip'></td></tr>" +
            "<tr><td styles='formTableTitle' lable='mobile'></td>" +
            "   <td styles='formTableValue' item='mobile'></td>" +
            "   <td styles='formTableValue' item='mobileTip'></td></tr>";
        if( signUpMode === "code" ){
            html += "<tr><td styles='formTableTitle' lable='codeAnswer'></td>" +
                "   <td styles='formTableValue'><div item='codeAnswer' style='float:left;'></div><div item='verificationAction' style='float:left;'></div><div item='resendVerificationAction' style='float:left;display:none;'></div></td>" +
                "   <td styles='formTableValue' item='verificationCodeTip'></td></tr>";
        }else{
            html += "<tr><td styles='formTableTitle' lable='captchaAnswer'></td>" +
                "   <td styles='formTableValue'><div item='captchaAnswer' style='float:left;'></div><div item='captchaPic' style='float:left;'></div><div item='changeCaptchaAction' style='float:left;'></div></td>" +
                "   <td styles='formTableValue' item='captchaAnswerTip'></td></tr>";
        }
        html += "<tr><td styles='formTableTitle'></td>" +
            "   <td styles='formTableValue' item='signUpAction'></td>" +
            "   <td styles='formTableValue' item='signUpTip'></td></tr>" +
            "<tr><td></td>" +
            "   <td><div item='hasAccountArea'></div><div item='gotoLoginAction'></div></td>" +
            "   <td></td></tr>" +
            "</table>";
        this.formTableArea.set("html", html);

        if( signUpMode === "captcha" ){
            this.setCaptchaPic();
        }
        this.createPasswordStrengthNode();

        MWF.xDesktop.requireApp("Template", "MForm", function () {
            this.form = new MForm(this.formTableArea, this.data, {
                style: this.options.popupStyle,
                verifyType : "batchSingle",	//batch一起校验，或alert弹出
                isEdited: this.isEdited || this.isNew,
                onPostLoad: function(){
                    var form = this.form;
                    var table = this.formTableArea;
                    form.getItem("name").tipNode = table.getElement("[item='nameTip']");
                    form.getItem("password").tipNode = table.getElement("[item='passwordTip']");
                    form.getItem("confirmPassword").tipNode = table.getElement("[item='confirmPasswordTip']");
                    //form.getItem("mail").tipNode = table.getElement("[item='mailTip']")[0];
                    form.getItem("genderType").tipNode = table.getElement("[item='genderTypeTip']");
                    form.getItem("mobile").tipNode = table.getElement("[item='mobileTip']");
                    this.tipNode = table.getElement("[item='signUpTip']");
                    var captchaAnswer = form.getItem("captchaAnswer");
                    if( captchaAnswer ){
                        form.getItem("captchaAnswer").tipNode = table.getElement("[item='captchaAnswerTip']");
                    }
                    var codeAnswer = form.getItem("codeAnswer");
                    if( codeAnswer ){
                        form.getItem("codeAnswer").tipNode = table.getElement("[item='verificationCodeTip']");
                    }
                }.bind(this),
                itemTemplate: {
                    name: { text: this.lp.userName, defaultValue : this.lp.userName, className : "inputUser",
                        notEmpty : true, defaultValueAsEmpty: true, emptyTip: this.lp.inputYourUserName,
                        validRule : { isInvalid : function( value, it ){ return this.checkUserName( value, it ); }.bind(this)},
                        validMessage : { isInvalid : this.lp.userExist },
                        event : {
                            focus : function( it ){ if( this.lp.userName === it.getValue() )it.setValue(""); if( !it.warningStatus )it.getElements()[0].setStyles( this.css.inputActive ); }.bind(this),
                            keyup : function( it, ev ){ if( ev.event.keyCode === 13 ){ this.ok() } }.bind(this),
                            blur: function (it) {
                                if( it.verify( true ) ){
                                    if( !it.warningStatus )it.getElements()[0].setStyles( this.css.inputUser );
                                }
                            }.bind(this)
                        }, onEmpty : function( it ){
                            it.getElements()[0].setStyles( this.css.inputEmpty );
                        }.bind(this), onUnempty : function( it ){
                            it.getElements()[0].setStyles(  this.css.inputUser );
                        }.bind(this) },
                    password : { text: this.lp.password, type : "password", className : "inputPassword",
                        notEmpty : true, defaultValueAsEmpty: true, emptyTip: this.lp.inputYourPassword,
                        validRule : { passwordIsWeak: function( value, it ){
                            if( this.getPasswordLevel( it.getValue() ) > 3 )return true;
                        }.bind(this)},
                        validMessage : { passwordIsWeak : this.lp.passwordIsSimple },
                        event : {
                            focus : function( it ){  if( "password" === it.getValue() )it.setValue(""); if( !it.warningStatus )it.getElements()[0].setStyles( this.css.inputActive ); }.bind(this),
                            keyup : function(it){ this.pwStrength(it.getValue()) }.bind(this),
                            blur: function (it) { it.verify( true ) }.bind(this)
                        }, onEmpty : function( it ){
                            it.getElements()[0].setStyles( this.css.inputEmpty );
                        }.bind(this), onUnempty : function( it ){
                            it.getElements()[0].setStyles(  this.css.inputPassword );
                        }.bind(this) },
                    confirmPassword : { text: this.lp.confirmPassword, type : "password", className : "inputComfirmPassword",
                        notEmpty : true,defaultValueAsEmpty: true, emptyTip: this.lp.inputComfirmPassword,
                        validRule : { passwordNotEqual: function( value, it ){
                            if( it.getValue() === this.form.getItem("password").getValue() )return true;
                        }.bind(this)},
                        validMessage : { passwordNotEqual : this.lp.passwordNotEqual },
                        event : {
                            focus : function( it ){  if( "password" === it.getValue() )it.setValue(""); if( !it.warningStatus )it.getElements()[0].setStyles( this.css.inputActive ); }.bind(this),
                            keyup : function( it, ev ){ if( ev.event.keyCode === 13 ){ this.ok() } }.bind(this),
                            blur: function (it) {
                                if( it.verify( true ) ) {
                                    if( !it.warningStatus )it.getElements()[0].setStyles( this.css.inputComfirmPassword );
                                }
                            }.bind(this)
                        }, onEmpty : function( it ){
                            it.getElements()[0].setStyles( this.css.inputEmpty );
                        }.bind(this), onUnempty : function( it ){
                            it.getElements()[0].setStyles(  this.css.inputComfirmPassword );
                        }.bind(this) },
                    mail : { text: this.lp.mail, defaultValue : this.lp.inputYourMail, className : "inputMail",
                        notEmpty : true, defaultValueAsEmpty: true, emptyTip: this.lp.inputYourMail, validRule: { email : true }, event : {
                            focus : function( it ){  if( this.lp.inputYourMail === it.getValue() )it.setValue(""); if( !it.warningStatus )it.getElements()[0].setStyles( this.css.inputActive ); }.bind(this),
                            blur: function (it) { it.verify( true ); if( !it.warningStatus )it.getElements()[0].setStyles( this.css.inputMail ); }.bind(this),
                            keyup : function( it, ev ){ if( ev.event.keyCode === 13 ){ this.ok() } }.bind(this)
                        }, onEmpty : function( it ){
                            it.getElements()[0].setStyles( this.css.inputEmpty );
                        }.bind(this), onUnempty : function( it ){
                            it.getElements()[0].setStyles(  this.css.inputMail );
                        }.bind(this) },
                    genderType : { text: this.lp.genderType,  className : "inputGenderType", type : "select", selectValue : this.lp.genderTypeValue.split(","), selectText : this.lp.genderTypeText.split(","),
                        notEmpty : true, emptyTip: this.lp.selectGenderType, event : {
                            focus: function(it){ if( !it.warningStatus )it.getElements()[0].setStyles( this.css.inputActive ); }.bind(this),
                            blur: function (it) { it.verify( true ); if( !it.warningStatus )it.getElements()[0].setStyles( this.css.inputGenderType ); }.bind(this),
                            keyup : function( it, ev ){ if( ev.event.keyCode === 13 ){ this.ok() } }.bind(this)
                        }, onEmpty : function( it ){
                            it.getElements()[0].setStyles( this.css.inputEmpty );
                        }.bind(this), onUnempty : function( it ){
                            it.getElements()[0].setStyles(  this.css.inputGenderType );
                        }.bind(this) },
                    mobile : { text: this.lp.mobile, defaultValue : this.lp.inputYourMobile, className : "inputMobile", tType : "number",
                        notEmpty : true, defaultValueAsEmpty: true, emptyTip: this.lp.inputYourMobile,
                        validRule : { isInvalid: function( value, it ){ return this.checkMobile( value, it ); }.bind(this)},
                        validMessage : { isInvalid : this.lp.mobileIsRegisted },
                        event : {
                            focus : function( it ){  if( this.lp.inputYourMobile === it.getValue() )it.setValue(""); if( !it.warningStatus )it.getElements()[0].setStyles( this.css.inputActive ); }.bind(this),
                            keyup : function( it, ev ){ if( ev.event.keyCode === 13 ){ this.ok() } }.bind(this),
                            blur: function (it) { if( it.verify( true )){
                                if( !it.warningStatus )it.getElements()[0].setStyles( this.css.inputMobile );
                            } }.bind(this)
                        }, onEmpty : function( it ){
                            it.getElements()[0].setStyles( this.css.inputEmpty );
                        }.bind(this), onUnempty : function( it ){
                            it.getElements()[0].setStyles(  this.css.inputMobile );
                        }.bind(this) },
                    captchaAnswer: { tType: "number", text: this.lp.verificationCode, defaultValue : this.lp.verificationCode, className : "inputVerificationCode",
                        notEmpty : true,defaultValueAsEmpty: true, emptyTip: this.lp.inputPicVerificationCode, event : {
                            focus : function( it ){ if( this.lp.verificationCode === it.getValue() )it.setValue(""); if( !it.warningStatus )it.getElements()[0].setStyles( this.css.inputActive ); }.bind(this),
                            blur: function (it) { it.verify( true ); if( !it.warningStatus )it.getElements()[0].setStyles( this.css.inputVerificationCode ); }.bind(this),
                            keyup : function( it, ev ){ if( ev.event.keyCode === 13 ){ this.ok() } }.bind(this)
                        }, onEmpty : function( it ){
                            it.getElements()[0].setStyles( this.css.inputEmpty );
                        }.bind(this), onUnempty : function( it ){
                            it.getElements()[0].setStyles(  this.css.inputVerificationCode );
                        }.bind(this) },
                    changeCaptchaAction :{ value : this.lp.changeVerification, type : "innerText", className : "verificationChange", event : {
                        click : function(){
                            this.setCaptchaPic();
                        }.bind(this)
                    } },
                    codeAnswer : { text: this.lp.verificationCode, defaultValue : this.lp.inputVerificationCode, className : "inputVerificationCode2",
                        notEmpty : true,defaultValueAsEmpty: true, emptyTip: this.lp.inputVerificationCode, event : {
                            focus : function( it ){ if( this.lp.inputVerificationCode === it.getValue() )it.setValue(""); if( !it.warningStatus )it.getElements()[0].setStyles( this.css.inputActive ); }.bind(this),
                            blur: function (it) { it.verify( true ); if( !it.warningStatus )it.getElements()[0].setStyles( this.css.inputVerificationCode2 ); }.bind(this),
                            keyup : function( it, ev ){ if( ev.event.keyCode === 13 ){ this.ok() } }.bind(this)
                        }, onEmpty : function( it ){
                            it.getElements()[0].setStyles( this.css.inputEmpty );
                        }.bind(this), onUnempty : function( it ){
                            it.getElements()[0].setStyles(  this.css.inputVerificationCode2 );
                        }.bind(this) },
                    verificationAction : {value: this.lp.sendVerification, type : "button", className : "inputSendVerification", event : {
                        click : function(){ this.sendVerificationAction() }.bind(this)
                    } },
                    resendVerificationAction : {value: this.lp.resendVerification, type : "button", className : "inputResendVerification"},
                    signUpAction: {value: this.lp.signUp, type : "button", className : "inputSignUp", event : {
                        click : function(){ this.ok() }.bind(this)
                    } },
                    hasAccountArea : { value : this.lp.hasAccount, type : "innerText", className : "hasCountArea" },
                    gotoLoginAction : { value : this.lp.gotoLogin, type : "innerText", className : "gotoLoginAction", event : {
                        click : function(){ this.gotoLogin() }.bind(this)
                    }}
                }
            }, this.app, this.css);
            this.form.load();
        }.bind(this), true);
    },
    checkMobile: function( mobile, it ){
        var flag = true;
        this.actions.checkRegisterMobile( mobile, function( ){
            flag = true
        }.bind(this), function( errorObj ){
            if( errorObj.status === 404){
                it.options.validMessage.isInvalid = this.lp.pageNotFound;
                flag = false;
            }else{
                var error = JSON.parse( errorObj.responseText );
                it.options.validMessage.isInvalid = error.message;
                flag = false;
            }
        }.bind(this), false );
        return flag;
    },
    checkUserName: function( userName, it ){
        var flag = true;
        this.actions.checkRegisterName( userName, function( ){
            flag = true
        }.bind(this), function( errorObj ){
            if( errorObj.status === 404){
                it.options.validMessage.isInvalid = this.lp.pageNotFound;
                flag = false
            }else{
                var error = JSON.parse( errorObj.responseText );
                it.options.validMessage.isInvalid = error.message;
                flag = false
            }
        }.bind(this), false );
        return flag;
    },
    setCaptchaPic: function(){
        var captchaPic = this.formTableArea.getElement("[item='captchaPic']");
        captchaPic.empty();
        this.actions.getRegisterCaptcha(120, 50, function( json ){
            this.captcha = json.data.id;
            new Element("img", {
                src : "data:image/png;base64,"+json.data.image,
                styles : this.css.verificationImage
            }).inject( captchaPic );
        }.bind(this))
    },
    sendVerificationAction: function(){
        var flag = true;
        var it = this.form.getItem("mobile");
        if( it.verify( true )){
            it.clearWarning();
            it.getElements()[0].setStyles(this.css.inputMobile);
        }else{
            return;
        }
        this.actions.createRegisterCode( it.getValue(), function( json ){

        }, function( errorObj ){
            var codeIt = this.form.getItem("codeAnswer");
            var error = JSON.parse( errorObj.responseText );
            if( errorObj.status === 404){
                codeIt.setWarning(error.message, this.lp.pageNotFound);
                flag = false;
            }else{
                codeIt.setWarning(error.message, "invalid");
                flag = false;
            }
        }.bind(this), false);

        if( !flag )return false;
        this.form.getItem("verificationAction").container.setStyle("display","none");
        this.setResendVerification();
    },
    setResendVerification: function(){
        var resendItem = this.form.getItem("resendVerificationAction");
        resendItem.container.setStyle("display","");
        this.resendElement = resendItem.getElements()[0];
        this.resendElement.set("text", this.lp.resendVerification + "(60)");

        var i=60;
        this.timer = setInterval( function(){
            if( i > 0 ){
                this.resendElement.set("text", this.lp.resendVerification + "("+ --i +")")
            }else{
                this.form.getItem("verificationAction").container.setStyle("display","");
                resendItem.container.setStyle("display","none");
                clearInterval( this.timer )
            }
        }.bind(this), 1000 )
    },
    createPasswordStrengthNode : function(){
        var passwordStrengthArea = this.formTableArea.getElement("[item='passwordStrengthArea']");

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
    getPasswordLevel: function( password ){
        // Level（级别）
        //  •0-3 : [easy]
        //  •4-6 : [midium]
        //  •7-9 : [strong]
        //  •10-12 : [very strong]
        //  •>12 : [extremely strong]
        var level = 0;
        this.actions.checkRegisterPassword( password, function( json ){
            level = json.data.value;
        }.bind(this), null, false );
        return level;
    },
    pwStrength: function(pwd){
        this.lowColorNode.setStyles( this.css.passwordStrengthColor );
        this.lowTextNode.setStyles( this.css.passwordStrengthText );
        this.middleColorNode.setStyles( this.css.passwordStrengthColor );
        this.middleTextNode.setStyles( this.css.passwordStrengthText );
        this.highColorNode.setStyles( this.css.passwordStrengthColor );
        this.highTextNode.setStyles( this.css.passwordStrengthText );
        if (!pwd){
        }else{
            //var level = this.checkStrong(pwd);
            var level = this.getPasswordLevel(pwd);
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
        }
    },
    gotoLogin: function(){
        this.explorer.openLoginForm( {}, function(){ window.location.reload(); } );
        this.close();
    },
    setWarning : function(text){
        this.tipNode.empty();
        new Element("div",{
            "text" : text,
            "styles" : this.css.warningMessageNode
        }).inject( this.tipNode );
    },
    setNotice : function(text){
        this.tipNode.empty();
        new Element("div",{
            "text" : text,
            "styles" : this.css.noticeMessageNode
        }).inject( this.tipNode );
    },
    ok: function () {
        this.tipNode.empty();
        this.fireEvent("queryOk");
        var data = this.form.getResult(true, ",", true, false, true);
        if (data) {
            this._ok(data, function (json) {
                if (json.type === "error") {
                    if(this.app)this.app.notice(json.message, "error");
                } else {
                    if(this.formMaskNode)this.formMaskNode.destroy();
                    this.formAreaNode.destroy();
                    this.setNotice( this.lp.registeSuccess );
                    if(this.app)this.app.notice( this.lp.registeSuccess, "success");
                    this.fireEvent("postOk", json);
                    this.gotoLogin();
                }
            }.bind(this))
        }
    },
    _ok: function (data, callback) {
        data.captcha = this.captcha;
        this.actions.register( data, function(json){
            if( callback )callback( json );
        }.bind(this), function( errorObj ){
            if( errorObj.status === 404){
                this.setWarning(this.lp.pageNotFound);
            }else{
                var error = JSON.parse( errorObj.responseText );
                this.setWarning(error.message);
            }
        }.bind(this) );
    }
});

MWF.xDesktop.Authentication.ResetPasswordForm = new Class({
    Extends: MPopupForm,
    Implements: [Options, Events],
    options: {
        "style": "default",
        "popupStyle" : "o2platformSignup",
        "width": "700",
        "height": "450",
        "hasTop": true,
        "hasIcon": false,
        "hasTopIcon" : true,
        "hasTopContent" : true,
        "hasBottom": false,
        "title": MWF.LP.authentication.ResetPasswordFormTitle,
        "draggable": true,
        "closeAction": true
    },
    _createTopContent: function(){

        this.actions = MWF.Actions.get("x_organization_assemble_personal");

        this.actions.getRegisterMode(function(json){
            this.signUpMode = json.data.value;
        }.bind(this), null ,false);
        if( this.signUpMode && this.signUpMode!=="disable" ){
            this.gotoSignupNode = new Element("div", {
                styles : this.css.formTopContentCustomNode,
                text : this.lp.signUp
            }).inject( this.formTopContentNode );
            this.gotoSignupNode.addEvent( "click", function(){ this.gotoSignup() }.bind(this) );

            new Element("div", {styles : this.css.formTopContentSepNode}).inject( this.formTopContentNode );
        }

        this.gotoLoginNode = new Element("div", {
            styles : this.css.formTopContentCustomNode,
            text : this.lp.loginAction
        }).inject( this.formTopContentNode );
        this.gotoLoginNode.addEvent( "click", function(){ this.gotoLogin() }.bind(this) );
    },
    _createTableContent: function () {
        this.formTableContainer.setStyles( this.css.formTableContainer2 );
        this.loadSteps();
        this.loadStepForm_1();
        this.loadStepForm_2();
        this.loadStepForm_3();

    },
    reset: function(){
        this.formTableArea.empty();
        this._createTableContent();
    },
    loadSteps: function() {
        var stepsContainer = new Element("div", { styles : this.css.stepsContainer }).inject( this.formTableArea );
        this.step_1 = new Element( "div", {
            styles : this.css.step_1_active,
            text : this.lp.shotMessageCheck
        }).inject(stepsContainer);
        this.stepLink_1 = new Element( "div", { styles : this.css.stepLink_1 }).inject(this.step_1);

        this.step_2 = new Element( "div", {
            styles : this.css.step_2,
            text : this.lp.setMewPassword
        }).inject(stepsContainer);
        this.stepLink_2 = new Element( "div", { styles : this.css.stepLink_2 }).inject(this.step_2);

        this.step_3 = new Element( "div", {
            styles : this.css.step_3,
            text : this.lp.completed
        }).inject(stepsContainer);

    },
    loadStepForm_1: function(){
        var html = "<table width='100%' bordr='0' cellpadding='5' cellspacing='0' styles='formTable'>" +
            "<tr><td styles='formTableTitle' lable='name' width='80'></td>" +
            "   <td styles='formTableValue' item='name' width='350'></td></tr>" +
            "<tr><td styles='formTableTitle' lable='codeAnswer'></td>" +
            "   <td styles='formTableValue'>" +
            "       <div item='codeAnswer' style='float:left;'></div>" +
            "       <div item='verificationAction' style='float:left;'></div>" +
            "       <div item='resendVerificationAction' style='float:left;display:none;'></div></td>" +
            "   </tr>"+
            "<tr><td styles='formTableTitle'></td>" +
            "   <td styles='formTableValue' item='nextStep'></td></tr>" +
            "</table>";
        this.stepNode_1 = new Element("div", {html: html , styles : {display:""} }).inject(this.formTableArea );
        MWF.xDesktop.requireApp("Template", "MForm", function () {
            this.stepForm_1 = new MForm(this.stepNode_1, {}, {
                style: this.options.popupStyle,
                verifyType : "single",	//batch一起校验，或alert弹出
                isEdited: this.isEdited || this.isNew,
                itemTemplate: {
                    name: { text: this.lp.userName, defaultValue : this.lp.userName, className : "inputUser",
                        notEmpty : true, defaultValueAsEmpty: true, emptyTip: this.lp.inputYourUserName,
                        validRule : { isInvalid: function( value, it ){ return this.checkUserName( value, it ); }.bind(this)},
                        validMessage : { isInvalid : this.lp.userNotExist },
                        event : {
                            focus : function( it ){ if( this.lp.userName === it.getValue() )it.setValue(""); if( !it.warningStatus )it.getElements()[0].setStyles( this.css.inputActive ); }.bind(this),
                            blur: function (it) { if( it.getValue() === "" )it.setValue( this.lp.userName ); if( !it.warningStatus )it.getElements()[0].setStyles( this.css.inputUser ); }.bind(this),
                            keyup : function( it, ev ){ if( ev.event.keyCode === 13 ){ this.gotoStep( 2 ) } }.bind(this)
                        }, onEmpty : function( it ){
                            it.getElements()[0].setStyles( this.css.inputEmpty );
                        }.bind(this), onUnempty : function( it ){
                            it.getElements()[0].setStyles(  this.css.inputUser );
                        }.bind(this) },
                    codeAnswer : { text: this.lp.verificationCode, defaultValue : this.lp.inputVerificationCode, className : "inputVerificationCode2",
                        notEmpty : true,defaultValueAsEmpty: true, emptyTip: this.lp.inputVerificationCode, event : {
                            focus : function( it ){ if( this.lp.inputVerificationCode === it.getValue() )it.setValue(""); if( !it.warningStatus )it.getElements()[0].setStyles( this.css.inputActive ); }.bind(this),
                            blur: function (it) {
                                if( it.getValue() === "" )it.setValue( this.lp.inputVerificationCode );
                                if( !it.warningStatus )it.getElements()[0].setStyles( this.css.inputVerificationCode2 );
                            }.bind(this),
                            keyup : function( it, ev ){ if( ev.event.keyCode === 13 ){ this.gotoStep( 2 ) } }.bind(this)
                        }, onEmpty : function( it ){
                            it.getElements()[0].setStyles( this.css.inputEmpty );
                        }.bind(this), onUnempty : function( it ){
                            it.getElements()[0].setStyles(  this.css.inputVerificationCode2 );
                        }.bind(this) },
                    verificationAction : {value: this.lp.sendVerification, type : "button", className : "inputSendVerification", event : {
                        click : function(){ this.sendVerificationAction() }.bind(this)
                    } },
                    resendVerificationAction : {value: this.lp.resendVerification, type : "button", className : "inputResendVerification"},
                    nextStep: {value: this.lp.nextStep, type : "button", className : "inputSignUp", event : {
                        click : function(){ this.gotoStep( 2 ) }.bind(this)
                    } }
                }
            }, this.app, this.css);
            this.stepForm_1.load();
        }.bind(this), true);
    },
    loadStepForm_2 : function(){
        html = "<table width='100%' bordr='0' cellpadding='5' cellspacing='0' styles='formTable'>" +
            "<tr><td styles='formTableTitle' lable='password' width='80'></td>" +
            "   <td styles='formTableValue' item='password' width='350'></td>" +
            "   <td styles='formTableValue'><div item='passwordStrengthArea'></div></td></tr>" +
            "<tr><td styles='formTableTitle' lable='confirmPassword'></td>" +
            "   <td styles='formTableValue' item='confirmPassword'></td>" +
            "   <td styles='formTableValue'></td></tr>" +
            "<tr><td styles='formTableTitle'></td>" +
            "   <td styles='formTableValue' item='nextStep'></td>"+
            "   <td styles='formTableValue'></td></tr>" +
            "</table>";
        this.stepNode_2 = new Element("div", {
            html: html,
            styles : { "display" : "none" }
        }).inject(this.formTableArea );
        MWF.xDesktop.requireApp("Template", "MForm", function () {
            this.stepForm_2 = new MForm(this.stepNode_2, {}, {
                style: "o2platformSignup",
                verifyType : "single",	//batch一起校验，或alert弹出
                isEdited: this.isEdited || this.isNew,
                itemTemplate: {
                    password : { text: this.lp.setNewPassword, type : "password", className : "inputPassword",
                        notEmpty : true, defaultValueAsEmpty: true, emptyTip: this.lp.inputYourPassword,
                        validRule : { passwordIsWeak: function( value, it ){
                            if( this.getPasswordLevel( it.getValue() ) > 3 )return true;
                        }.bind(this)},
                        validMessage : { passwordIsWeak : this.lp.passwordIsWeak },
                        event : {
                            focus : function( it ){  if( "password" === it.getValue() )it.setValue(""); if( !it.warningStatus )it.getElements()[0].setStyles( this.css.inputActive ); }.bind(this),
                            blur : function( it ){ if( !it.warningStatus )it.getElements()[0].setStyles( this.css.inputPassword ); }.bind(this),
                            keyup : function(it){ this.pwStrength(it.getValue()) }.bind(this)
                        }, onEmpty : function( it ){
                            it.getElements()[0].setStyles( this.css.inputEmpty );
                        }.bind(this), onUnempty : function( it ){
                            it.getElements()[0].setStyles(  this.css.inputPassword );
                        }.bind(this) },
                    confirmPassword : { text: this.lp.confirmNewPassword, type : "password", className : "inputComfirmPassword",
                        notEmpty : true,defaultValueAsEmpty: true, emptyTip: this.lp.inputComfirmPassword,
                        validRule : { passwordNotEqual: function( value, it ){
                            if( it.getValue() === this.stepForm_2.getItem("password").getValue() )return true;
                        }.bind(this)},
                        validMessage : { passwordNotEqual : this.lp.passwordNotEqual },
                        event : {
                            focus : function( it ){  if( "password" === it.getValue() )it.setValue(""); if( !it.warningStatus )it.getElements()[0].setStyles( this.css.inputActive ); }.bind(this),
                            blur : function( it ){ if( !it.warningStatus )it.getElements()[0].setStyles( this.css.inputComfirmPassword ); }.bind(this),
                            keyup : function( it, ev ){ if( ev.event.keyCode === 13 ){ this.gotoStep( 3 ) } }.bind(this)
                        }, onEmpty : function( it ){
                            it.getElements()[0].setStyles( this.css.inputEmpty );
                        }.bind(this), onUnempty : function( it ){
                            it.getElements()[0].setStyles(  this.css.inputComfirmPassword );
                        }.bind(this) },
                    nextStep: {value: this.lp.nextStep, type : "button", className : "inputSignUp", event : {
                        click : function(){ this.gotoStep( 3 ) }.bind(this)
                    } }
                }
            }, this.app, this.css);
            this.stepForm_2.load();
        }.bind(this), true);

        this.createPasswordStrengthNode();
    },
    loadStepForm_3: function(){
        this.stepNode_3 = new Element("div", {
            styles : this.css.stepFormResult
        }).inject(this.formTableArea );
    },
    createSetForm_3 : function( isSuccess, text ){
        var area = null;
        var action = null;
        if( isSuccess ){
            this.stepNode_3.setStyles( this.css.stepFormResult );
            this.stepNode_3.setStyle("display","");
            new Element("div", {
                styles : this.css.resetPasswordSuccess,
                text : this.lp.resetPasswordSuccess
            }).inject( this.stepNode_3 );
            area = new Element("div", {
                styles : this.css.resetPasswordResultArea
            }).inject( this.stepNode_3 );
            new Element("div", {
                styles : this.css.resetPasswordResultWord,
                text : this.lp.resetPasswordSuccessWord
            }).inject( area );
            action = new Element("div", {
                styles : this.css.resetPasswordResultAction,
                text : this.lp.gotoLogin
            }).inject( area );
            action.addEvent("click", function(){ this.gotoLogin() }.bind(this));
        }else{
            this.stepNode_3.setStyles( this.css.stepFormResult_fail );
            this.stepNode_3.setStyle("display","");
            new Element("div", {
                styles : this.css.resetPasswordFail,
                text : text + this.lp.resetPasswordFail
            }).inject( this.stepNode_3 );
            area = new Element("div", {
                styles : this.css.resetPasswordResultArea
            }).inject( this.stepNode_3 );
            new Element("div", {
                styles : this.css.resetPasswordResultWord,
                text : this.lp.resetPasswordFailWord
            }).inject( area );
            action = new Element("div", {
                styles : this.css.resetPasswordResultAction,
                text : this.lp.backtoModify
            }).inject( area );
            action.addEvent("click", function(){ this.reset() }.bind(this));
        }
    },
    gotoStep: function( step ){
        var form = this["stepForm_"+(step-1)];
        form.clearWarning();
        var data = form.getResult(true, ",", true, false, true);
        if( !data ){
            return;
        }
        var i;
        for( i = 1; i<=step; i++ ){
            this["step_"+i].setStyles( this.css["step_"+i+"_active"] );
            if( i!==step && this["stepLink_"+i]){
                this["stepLink_"+i].setStyles( this.css["stepLink_"+i+"_active"] );
            }
        }
        for( i = step+1; i<=3; i++ ){
            this["step_"+i].setStyles( this.css["step_"+i] );
            if( i!==3 ){
                this["stepLink_"+i].setStyles( this.css["stepLink_"+i] );
            }
        }
        for( i = 1; i<=3; i++ ){
            if( i===step ){
                this["stepNode_"+i].setStyle( "display" , "" );
            }else{
                this["stepNode_"+i].setStyle( "display" , "none" );
            }
        }

        if( step === 3 ){
            var d = {
                credential : this.stepForm_1.getItem("name").getValue(),
                codeAnswer : this.stepForm_1.getItem("codeAnswer").getValue(),
                password : this.stepForm_2.getItem("password").getValue()
            };
            this.actions.resetPassword( d, function( ){
                this.createSetForm_3( true )
            }.bind(this), function( errorObj ){
                var error = JSON.parse( errorObj.responseText );
                //this.app.notice( error.message );
                this.createSetForm_3( false, error.message )
            }.bind(this), false );
        }
    },
    checkMobile: function( mobile ){
        var flag = true;
        this.actions.checkRegisterMobile( mobile, function(){
            flag = true
        }.bind(this), function(){
            flag = false;
        }.bind(this), false );
        return flag;
    },
    checkUserName: function( userName, it ){
        var flag = false;
        this.actions.checkCredentialOnResetPassword( userName, function( json ){
            if( json.data ){
                flag = json.data.value;
            }
        }.bind(this), function( errorObj ){
            if( errorObj.status === 404){
                it.options.validMessage.isInvalid = this.lp.pageNotFound;
                flag = false
            }else{
                var error = JSON.parse( errorObj.responseText );
                it.options.validMessage.isInvalid = error.message;
                flag = false
            }
        }.bind(this), false );
        return flag;
    },
    sendVerificationAction: function(){
        var flag = true;
        var it = this.stepForm_1.getItem("name");
        this.stepForm_1.clearWarning();
        if( it.verify( true )){
        }else{
            return;
        }
        this.actions.createCodeOnResetPassword( it.getValue(), function( json ){

        }, function( errorObj ){
            var error = JSON.parse( errorObj.responseText );
            it.setWarning(error.message, "invalid");
            flag = false
        }.bind(this), false);

        if( !flag )return false;
        this.stepForm_1.getItem("verificationAction").container.setStyle("display","none");
        this.setResendVerification();
    },
    setResendVerification: function(){
        var resendItem = this.stepForm_1.getItem("resendVerificationAction");
        resendItem.container.setStyle("display","");
        this.resendElement = resendItem.getElements()[0];
        this.resendElement.set("text", this.lp.resendVerification + "(60)");

        var i=60;
        this.timer = setInterval( function(){
            if( i > 0 ){
                this.resendElement.set("text", this.lp.resendVerification + "("+ --i +")")
            }else{
                this.stepForm_1.getItem("verificationAction").container.setStyle("display","");
                resendItem.container.setStyle("display","none");
                clearInterval( this.timer )
            }
        }.bind(this), 1000 )
    },
    createPasswordStrengthNode : function(){
        var passwordStrengthArea = this.formTableArea.getElement("[item='passwordStrengthArea']");

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
    getPasswordLevel: function( password ){
        /*Level（级别）
         •0-3 : [easy]
         •4-6 : [midium]
         •7-9 : [strong]
         •10-12 : [very strong]
         •>12 : [extremely strong]
         */
        var level = 0;
        this.actions.checkRegisterPassword( password, function( json ){
            level = json.data.value;
        }.bind(this), null, false );
        return level;
    },
    pwStrength: function(pwd){
        this.lowColorNode.setStyles( this.css.passwordStrengthColor );
        this.lowTextNode.setStyles( this.css.passwordStrengthText );
        this.middleColorNode.setStyles( this.css.passwordStrengthColor );
        this.middleTextNode.setStyles( this.css.passwordStrengthText );
        this.highColorNode.setStyles( this.css.passwordStrengthColor );
        this.highTextNode.setStyles( this.css.passwordStrengthText );
        if (!pwd){
        }else{
            //var level = this.checkStrong(pwd);
            var level = this.getPasswordLevel(pwd);
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
        }
    },
    gotoLogin: function(){
        this.explorer.openLoginForm(  {}, function(){ window.location.reload(); }  );
        this.close();
    },
    gotoSignup: function(){
        this.explorer.openSignUpForm();
        this.close();
    }
});
