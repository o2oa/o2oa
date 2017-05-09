MWF.xDesktop = MWF.xDesktop || {};
MWF.xApplication = MWF.xApplication || {};
MWF.require("MWF.xAction.Authentication.RestActions", null, false);
MWF.xDesktop.requireApp("Template", "Explorer", null, false);

MWF.xDesktop.Authentication = new Class({
	Extends: MWF.widget.Common,
	Implements: [Options, Events],
	options: {
		"style": "default"
	},
	initialize: function(options, app, node){
		this.setOptions(options);
		this.action = new MWF.xApplication.Authentication.Actions.RestActions();
		this.actions = this.action;
		this.path = MWF.defaultPath+"/xDesktop/$Authentication/";
		this.cssPath = MWF.defaultPath+"/xDesktop/$Authentication/"+this.options.style+"/css.wcss";
		this._loadCss();
		this.lp = MWF.LP.authentication;
		this.app = app || {};
	},
	isAuthenticated: function(success, failure){
		this.action.getAuthentication(success, failure);
	},

	loadLogin: function(node){
		this.popupOptions = {
			"draggable": false,
			"closeAction": false,
			"hasMark" : false,
			"relativeToApp" : false
		};
		this.popupPara = {
			container : node
		};
		this.postLogin = function( json ){
			layout.desktop.session.user = json.data;
			window.location.reload();
		}.bind(this);
		this.openLoginForm( this.popupOptions );
	},
	logout: function(){
		this.action.logout(function(){
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
		} );
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
	Extends: MWF.xApplication.Template.Explorer.PopupForm,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"popupStyle" : "o2platform",
		"width": "650",
		"height": "490",
		"hasTop": true,
		"hasIcon": false,
		"hasTopIcon" : true,
		"hasTopContent" : true,
		"hasBottom": false,
		"title": (layout.config && layout.config.systemTitle) ? layout.config.systemTitle : MWF.LP.authentication.LoginFormTitle,
		"draggable": true,
		"closeAction": true
	},
	_createTableContent: function () {

		this.formTopIconNode.setStyle("cursor","pointer");
		this.formTopIconNode.addEvent("click", function(){
			window.open("http://www.o2oa.io","_blank");
		});

		this.loginType = "captcha";
		this.codeLogin=false;
		this.bindLogin=false;
		this.actions.getLoginMode(function( json ){
			this.codeLogin = json.data.codeLogin;
			this.bindLogin = json.data.bindLogin;
		}.bind(this), null, false);

		this.actions.getRegisterMode(function(json){
			this.signUpMode = json.data.value;
		}.bind(this), null ,false)

		if( this.bindLogin ){
			this.bindLoginTipPic = new Element("div.bindLoginTipPic", {styles : this.css.bindLoginTipPic}).inject( this.formContentNode, "top" );
			this.bindLoginAction = new Element("div.bindLoginAction", {styles : this.css.bindLoginAction}).inject( this.formContentNode, "top" );
			this.bindLoginAction.addEvent("click", function(){
				this.showBindCodeLogin();
			}.bind(this))

			this.backtoPasswordLoginTipPic = new Element("div.backtoPasswordLoginTipPic", {styles : this.css.backtoPasswordLoginTipPic}).inject( this.formContentNode, "top" );
			this.backtoPasswordLoginAction = new Element("div.backtoPasswordLoginAction", {styles : this.css.backtoPasswordLoginAction}).inject( this.formContentNode, "top" );
			this.backtoPasswordLoginAction.addEvent("click", function(){
				this.backtoPasswordLogin();
			}.bind(this))
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
				"</td></tr>"
		}
		html +=  "<tr><td styles='formTableValueTop20' item='loginAction'></td></tr>" +
			"</table>"+
			"<table width='100%' bordr='0' cellpadding='0' cellspacing='0' styles='formTable'>";
		if( this.signUpMode && this.signUpMode != "disable" ){
			html += "<tr><td><div item='signUpAction'></div><div item='forgetPassword'></div></td></tr>";
		}else{
			html += "<tr><td><div styles='signUpAction'></div><div item='forgetPassword'></div></td></tr>";
		}
		html +=	"<tr><td  styles='formTableValue' item='errorArea'></td></tr>" +
			"</table>"

		this.formTableArea.set("html", html);
        new Element("div", {
            "styles": {
                "text-align": "center",
                "height": "40px",
                "line-height": "40px"
            },
            "text": layout.config.systemName
        }).inject(this.formTableArea, "after");


		this.setCaptchaPic();
		this.errorArea = this.formTableArea.getElements("[item=errorArea]")[0];

		MWF.xDesktop.requireApp("Template", "MForm", function () {
			this.form = new MForm(this.formTableArea, this.data, {
				style: this.options.popupStyle,
				verifyType : "single",	//batch一起校验，或alert弹出
				isEdited: this.isEdited || this.isNew,
				itemTemplate: {
					credential: { text: this.lp.userName, defaultValue : this.lp.userName, className : "inputUser",
						notEmpty : true,  defaultValueAsEmpty: true, emptyTip:  this.lp.inputYourUserName,event : {
							focus : function( it, ev ){ if( this.lp.userName == it.getValue() )it.setValue(""); if( !it.warningStatus )it.getElements()[0].setStyles( this.css.inputActive ); }.bind(this),
							blur : function( it, ev ){ if( "" == it.getValue() )it.setValue(this.lp.userName); if( !it.warningStatus )it.getElements()[0].setStyles( this.css.inputUser ); }.bind(this),
							keyup : function( it, ev ){ if( ev.event.keyCode == 13 ){ this.ok() } }.bind(this)
						}, onEmpty : function( it ){
							it.getElements()[0].setStyles( this.css.inputEmpty );
						}.bind(this), onUnempty : function( it ){
							it.getElements()[0].setStyles(  this.css.inputUser );
						}.bind(this) },
					password : { text: this.lp.password, type : "password", defaultValue : "password", className : "inputPassword",
						notEmpty : true, defaultValueAsEmpty: true, emptyTip:  this.lp.inputYourPassword, event : {
							focus : function( it, ev ){  if( "password" == it.getValue() )it.setValue(""); if( !it.warningStatus )it.getElements()[0].setStyles( this.css.inputActive );  }.bind(this),
							blur : function( it, ev ){ if( !it.warningStatus )it.getElements()[0].setStyles( this.css.inputPassword );}.bind(this),
							keyup : function( it, ev ){ if( ev.event.keyCode == 13 ){ this.ok() } }.bind(this)
						}, onEmpty : function( it ){
							it.getElements()[0].setStyles( this.css.inputEmpty );
						}.bind(this), onUnempty : function( it ){
							it.getElements()[0].setStyles(  this.css.inputPassword );
						}.bind(this) },
					captchaAnswer : { tType:"number", text: this.lp.verificationCode, defaultValue : this.lp.verificationCode, className : "inputVerificationCode",
						notEmpty : true, defaultValueAsEmpty: true, emptyTip:  this.lp.inputPicVerificationCode, event : {
							focus : function( it, ev ){ if( this.lp.verificationCode == it.getValue() )it.setValue("");  if( !it.warningStatus )it.getElements()[0].setStyles( this.css.inputActive ); }.bind(this),
							blur : function( it, ev ){ if( "" == it.getValue() )it.setValue(this.lp.verificationCode); if( !it.warningStatus )it.getElements()[0].setStyles( this.css.inputVerificationCode ); }.bind(this),
							keyup : function( it, ev ){ if( ev.event.keyCode == 13 ){ this.ok() } }.bind(this)
						}, onEmpty : function( it ){
							it.getElements()[0].setStyles( this.css.inputEmpty );
						}.bind(this), onUnempty : function( it ){
							it.getElements()[0].setStyles(  this.css.inputVerificationCode );
						}.bind(this) },
					changeCaptchaAction :{ value : this.lp.changeVerification, type : "innerText", className : "verificationChange", event : {
						click : function(it, ev){
							this.setCaptchaPic();
						}.bind(this)
					} },
					codeAnswer : { text: this.lp.verificationCode, defaultValue : this.lp.inputVerificationCode, className : "inputVerificationCode2",
						notEmpty : true,defaultValueAsEmpty: true, emptyTip: this.lp.inputVerificationCode, event : {
							focus : function( it, ev ){ if( this.lp.inputVerificationCode == it.getValue() )it.setValue(""); if( !it.warningStatus )it.getElements()[0].setStyles( this.css.inputActive ); }.bind(this),
							blur : function( it, ev ){ if( "" == it.getValue() )it.setValue(this.lp.inputVerificationCode); if( !it.warningStatus )it.getElements()[0].setStyles( this.css.inputVerificationCode2 ); }.bind(this),
							keyup : function( it, ev ){ if( ev.event.keyCode == 13 ){ this.ok() } }.bind(this)
						}, onEmpty : function( it ){
							it.getElements()[0].setStyles( this.css.inputEmpty );
						}.bind(this), onUnempty : function( it ){
							it.getElements()[0].setStyles(  this.css.inputVerificationCode2 );
						}.bind(this) },
					verificationAction : {value: this.lp.sendVerification, type : "button", className : "inputSendVerification", event : {
						click : function(){ this.sendVerificationAction() }.bind(this)
					} },
					resendVerificationAction : {value: this.lp.resendVerification, type : "button", className : "inputResendVerification"},
					loginAction: {value: this.lp.loginAction, type : "button", className : "inputLogin", event : {
						click : function(){ this.ok() }.bind(this)
					} },
					passwordAction: {value : this.lp.passwordLogin, type : "innerText", className : "titleNode_active", event : {
						click : function(){ if( this.codeLogin )this.showPasswordLogin() }.bind(this)
					}},
					codeAction: {value : this.lp.codeLogin, type : "innerText", className : "titleNode_normal", event : {
						click : function(){ this.showCodeLogin() }.bind(this)
					}},
					signUpAction : { value : this.lp.signUp, type : "innerText", className : "signUpAction", event : {
						click : function(){ this.gotoSignup() }.bind(this)
					} },
					forgetPassword : { value : this.lp.forgetPassword, type : "innerText", className : "forgetPassword", event :{
						click : function(){ this.gotoResetPassword() }.bind(this)
					}}
				}
			}, this.app, this.css);
			this.form.load();
		}.bind(this), true);

		if( this.bindLogin ){
			this.bindLoginContainer = new Element("div", {
				styles : this.css.bindLoginContainer
			}).inject( this.formContentNode )

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
			this.bindBodyArea = this.bindLoginContainer.getElements("[styles='bindBodyArea']")[0];
			this.bindLoginContainer.addEvent("mousemove", function(ev){
				if( this.bindBodyArea.isOutside( ev ) ){
					this.hideExampleArea(ev);
				}else{
					this.showExampleArea(ev);
				}
			}.bind(this));

			this.bindPicArea = this.bindLoginContainer.getElements("[item='bindPicArea']")[0];
			this.setBindPic();
			this.bindExampleArea = this.bindLoginContainer.getElements("[styles='bindExampleArea']")[0];
			this.bindSepArea = this.bindLoginContainer.getElements("[styles='bindSepArea']")[0];
			var link = this.bindLoginContainer.getElements("[styles='bindTipLinkArea']")[0];
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
	},
	showExampleArea: function( ev ){
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
	hideExampleArea: function( ev ){
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
		this.formTableArea.getElements("[item='passwordTr']")[0].setStyle("display","");
		this.formTableArea.getElements("[item='captchaTr']")[0].setStyle("display","");
		this.formTableArea.getElements("[item='codeTr']")[0].setStyle("display","none");
	},
	showCodeLogin: function(){
		this.errorArea.empty();
		this.loginType = "code";
		this.form.getItem("passwordAction").setStyles(this.css.titleNode_normal);
		this.form.getItem("codeAction").setStyles(this.css.titleNode_active);
		this.formTableArea.getElements("[item='passwordTr']")[0].setStyle("display","none");
		this.formTableArea.getElements("[item='captchaTr']")[0].setStyle("display","none");
		this.formTableArea.getElements("[item='codeTr']")[0].setStyle("display","");

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
		var captchaPic = this.formTableArea.getElements("[item='captchaPic']")[0];
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
		if( !credential || credential.trim() == "" ){
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
					if( json.data.name && json.data.name != "anonymous" ){
						this.fireEvent("queryOk");
						this._close();
						if(this.formMarkNode)this.formMarkNode.destroy();
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
		if(this.timer) clearInterval( this.timer )
	},
	ok: function (e) {
		this.fireEvent("queryOk");
		this.errorArea.empty();
		if( this.loginType == "captcha" ){
			this.form.getItem("password").options.notEmpty = true;

			var captchaItem = this.form.getItem("captchaAnswer");
			if( captchaItem )captchaItem.options.notEmpty = true;

			var codeItem = this.form.getItem("codeAnswer");
			if(codeItem)codeItem.options.notEmpty = false;
		}else if( this.loginType == "code" ){
			this.form.getItem("password").options.notEmpty = false;

			var captchaItem = this.form.getItem("captchaAnswer");
			if( captchaItem )captchaItem.options.notEmpty = false;

			var codeItem = this.form.getItem("codeAnswer");
			if(codeItem)codeItem.options.notEmpty = true;
		}
		var data = this.form.getResult(true, ",", true, false, true);
		if (data) {
			this._ok(data, function (json) {
				if (json.type == "error") {
					if(this.app)this.app.notice(json.message, "error");
				} else {
					this._close();
					if( this.formMarkNode )this.formMarkNode.destroy();
					this.formAreaNode.destroy();
					if (this.explorer && this.explorer.view)this.explorer.view.reload();
					if(this.app)this.app.notice( this.lp.loginSuccess, "success");
					this.fireEvent("postOk",json);
				}
			}.bind(this))
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
		if( this.loginType == "captcha" ){
			var d = {
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
		}else if( this.loginType == "code" ){
			var d = {
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
	Extends: MWF.xApplication.Template.Explorer.PopupForm,
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
		var signUpMode;
		this.actions.getRegisterMode(function(json){
			signUpMode = json.data.value;
		}.bind(this), null ,false)

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
		if( signUpMode == "code" ){
			html += "<tr><td styles='formTableTitle' lable='codeAnswer'></td>" +
				"   <td styles='formTableValue'><div item='codeAnswer' style='float:left;'></div><div item='verificationAction' style='float:left;'></div><div item='resendVerificationAction' style='float:left;display:none;'></div></td>" +
				"   <td styles='formTableValue' item='verificationCodeTip'></td></tr>"
		}else{
			html += "<tr><td styles='formTableTitle' lable='captchaAnswer'></td>" +
				"   <td styles='formTableValue'><div item='captchaAnswer' style='float:left;'></div><div item='captchaPic' style='float:left;'></div><div item='changeCaptchaAction' style='float:left;'></div></td>" +
				"   <td styles='formTableValue' item='captchaAnswerTip'></td></tr>"
		}
		html += "<tr><td styles='formTableTitle'></td>" +
			"   <td styles='formTableValue' item='signUpAction'></td>" +
			"   <td styles='formTableValue' item='signUpTip'></td></tr>" +
			"<tr><td></td>" +
			"   <td><div item='hasAccountArea'></div><div item='gotoLoginAction'></div></td>" +
			"   <td></td></tr>" +
			"</table>"
		this.formTableArea.set("html", html);

		if( signUpMode == "captcha" ){
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
					form.getItem("name").tipNode = table.getElements("[item='nameTip']")[0];
					form.getItem("password").tipNode = table.getElements("[item='passwordTip']")[0];
					form.getItem("confirmPassword").tipNode = table.getElements("[item='confirmPasswordTip']")[0];
					//form.getItem("mail").tipNode = table.getElements("[item='mailTip']")[0];
					form.getItem("genderType").tipNode = table.getElements("[item='genderTypeTip']")[0];
					form.getItem("mobile").tipNode = table.getElements("[item='mobileTip']")[0];
					this.tipNode = table.getElements("[item='signUpTip']")[0];
					var captchaAnswer = form.getItem("captchaAnswer");
					if( captchaAnswer ){
						form.getItem("captchaAnswer").tipNode = table.getElements("[item='captchaAnswerTip']")[0];
					}
					var codeAnswer = form.getItem("codeAnswer");
					if( codeAnswer ){
						form.getItem("codeAnswer").tipNode = table.getElements("[item='verificationCodeTip']")[0];
					}
				}.bind(this),
				itemTemplate: {
					name: { text: this.lp.userName, defaultValue : this.lp.userName, className : "inputUser",
						notEmpty : true, defaultValueAsEmpty: true, emptyTip: this.lp.inputYourUserName,
						validRule : { isInvalid : function( value, it ){ return this.checkUserName( value, it ); }.bind(this)},
						validMessage : { isInvalid : this.lp.userExist },
						event : {
							focus : function( it, ev ){ if( this.lp.userName == it.getValue() )it.setValue(""); if( !it.warningStatus )it.getElements()[0].setStyles( this.css.inputActive ); }.bind(this),
							keyup : function( it, ev ){ if( ev.event.keyCode == 13 ){ this.ok() } }.bind(this),
							blur: function (it, ev) {
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
							focus : function( it, ev ){  if( "password" == it.getValue() )it.setValue(""); if( !it.warningStatus )it.getElements()[0].setStyles( this.css.inputActive ); }.bind(this),
							keyup : function(it, ev){ this.pwStrength(it.getValue()) }.bind(this),
							blur: function (it, ev) { it.verify( true ) }.bind(this)
						}, onEmpty : function( it ){
							it.getElements()[0].setStyles( this.css.inputEmpty );
						}.bind(this), onUnempty : function( it ){
							it.getElements()[0].setStyles(  this.css.inputPassword );
						}.bind(this) },
					confirmPassword : { text: this.lp.confirmPassword, type : "password", className : "inputComfirmPassword",
						notEmpty : true,defaultValueAsEmpty: true, emptyTip: this.lp.inputComfirmPassword,
						validRule : { passwordNotEqual: function( value, it ){
							if( it.getValue() == this.form.getItem("password").getValue() )return true;
						}.bind(this)},
						validMessage : { passwordNotEqual : this.lp.passwordNotEqual },
						event : {
							focus : function( it, ev ){  if( "password" == it.getValue() )it.setValue(""); if( !it.warningStatus )it.getElements()[0].setStyles( this.css.inputActive ); }.bind(this),
							keyup : function( it, ev ){ if( ev.event.keyCode == 13 ){ this.ok() } }.bind(this),
							blur: function (it, ev) {
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
							focus : function( it, ev ){  if( this.lp.inputYourMail == it.getValue() )it.setValue(""); if( !it.warningStatus )it.getElements()[0].setStyles( this.css.inputActive ); }.bind(this),
							blur: function (it, ev) { it.verify( true ); if( !it.warningStatus )it.getElements()[0].setStyles( this.css.inputMail ); }.bind(this),
							keyup : function( it, ev ){ if( ev.event.keyCode == 13 ){ this.ok() } }.bind(this)
						}, onEmpty : function( it ){
							it.getElements()[0].setStyles( this.css.inputEmpty );
						}.bind(this), onUnempty : function( it ){
							it.getElements()[0].setStyles(  this.css.inputMail );
						}.bind(this) },
					genderType : { text: this.lp.genderType,  className : "inputGenderType", type : "select", selectValue : this.lp.genderTypeValue.split(","), selectText : this.lp.genderTypeText.split(","),
						notEmpty : true, emptyTip: this.lp.selectGenderType, event : {
							focus: function(it,ev){ if( !it.warningStatus )it.getElements()[0].setStyles( this.css.inputActive ); }.bind(this),
							blur: function (it, ev) { it.verify( true ); if( !it.warningStatus )it.getElements()[0].setStyles( this.css.inputGenderType ); }.bind(this),
							keyup : function( it, ev ){ if( ev.event.keyCode == 13 ){ this.ok() } }.bind(this)
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
							focus : function( it, ev ){  if( this.lp.inputYourMobile == it.getValue() )it.setValue(""); if( !it.warningStatus )it.getElements()[0].setStyles( this.css.inputActive ); }.bind(this),
							keyup : function( it, ev ){ if( ev.event.keyCode == 13 ){ this.ok() } }.bind(this),
							blur: function (it, ev) { if( it.verify( true )){
								if( !it.warningStatus )it.getElements()[0].setStyles( this.css.inputMobile );
							} }.bind(this)
						}, onEmpty : function( it ){
							it.getElements()[0].setStyles( this.css.inputEmpty );
						}.bind(this), onUnempty : function( it ){
							it.getElements()[0].setStyles(  this.css.inputMobile );
						}.bind(this) },
					captchaAnswer: { tType: "number", text: this.lp.verificationCode, defaultValue : this.lp.verificationCode, className : "inputVerificationCode",
						notEmpty : true,defaultValueAsEmpty: true, emptyTip: this.lp.inputPicVerificationCode, event : {
							focus : function( it, ev ){ if( this.lp.verificationCode == it.getValue() )it.setValue(""); if( !it.warningStatus )it.getElements()[0].setStyles( this.css.inputActive ); }.bind(this),
							blur: function (it, ev) { it.verify( true ); if( !it.warningStatus )it.getElements()[0].setStyles( this.css.inputVerificationCode ); }.bind(this),
							keyup : function( it, ev ){ if( ev.event.keyCode == 13 ){ this.ok() } }.bind(this)
						}, onEmpty : function( it ){
							it.getElements()[0].setStyles( this.css.inputEmpty );
						}.bind(this), onUnempty : function( it ){
							it.getElements()[0].setStyles(  this.css.inputVerificationCode );
						}.bind(this) },
					changeCaptchaAction :{ value : this.lp.changeVerification, type : "innerText", className : "verificationChange", event : {
						click : function(it, ev){
							this.setCaptchaPic();
						}.bind(this)
					} },
					codeAnswer : { text: this.lp.verificationCode, defaultValue : this.lp.inputVerificationCode, className : "inputVerificationCode2",
						notEmpty : true,defaultValueAsEmpty: true, emptyTip: this.lp.inputVerificationCode, event : {
							focus : function( it, ev ){ if( this.lp.inputVerificationCode == it.getValue() )it.setValue(""); if( !it.warningStatus )it.getElements()[0].setStyles( this.css.inputActive ); }.bind(this),
							blur: function (it, ev) { it.verify( true ); if( !it.warningStatus )it.getElements()[0].setStyles( this.css.inputVerificationCode2 ); }.bind(this),
							keyup : function( it, ev ){ if( ev.event.keyCode == 13 ){ this.ok() } }.bind(this)
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
		this.actions.checkRegisterMobile( mobile, function( json ){
			flag = true
		}.bind(this), function( errorObj ){
			if( errorObj.status == 404){
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
		this.actions.checkRegisterName( userName, function( json ){
			flag = true
		}.bind(this), function( errorObj ){
			if( errorObj.status == 404){
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
		var captchaPic = this.formTableArea.getElements("[item='captchaPic']")[0];
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
			if( errorObj.status == 404){
				codeIt.setWarning(error.message, this.lp.pageNotFound);
				flag = false
			}else{
				var error = JSON.parse( errorObj.responseText );
				codeIt.setWarning(error.message, "invalid");
				flag = false
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
		var passwordStrengthArea = this.formTableArea.getElements("[item='passwordStrengthArea']")[0];

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
		var level;
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
		if (pwd==null||pwd==''){
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
	ok: function (e) {
		this.tipNode.empty();
		this.fireEvent("queryOk");
		var data = this.form.getResult(true, ",", true, false, true);
		if (data) {
			this._ok(data, function (json) {
				if (json.type == "error") {
					if(this.app)this.app.notice(json.message, "error");
				} else {
					if(this.formMarkNode)this.formMarkNode.destroy();
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
			if( errorObj.status == 404){
				this.setWarning(this.lp.pageNotFound);
			}else{
				var error = JSON.parse( errorObj.responseText );
				this.setWarning(error.message);
			}
		}.bind(this) );
	}
});

MWF.xDesktop.Authentication.ResetPasswordForm = new Class({
	Extends: MWF.xApplication.Template.Explorer.PopupForm,
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
		this.actions.getRegisterMode(function(json){
			this.signUpMode = json.data.value;
		}.bind(this), null ,false);
		if( this.signUpMode && this.signUpMode!="disable" ){
			this.gotoSignupNode = new Element("div", {
				styles : this.css.formTopContentCustomNode,
				text : this.lp.signUp
			}).inject( this.formTopContentNode )
			this.gotoSignupNode.addEvent( "click", function(){ this.gotoSignup() }.bind(this) );

			new Element("div", {styles : this.css.formTopContentSepNode}).inject( this.formTopContentNode );
		}

		this.gotoLoginNode = new Element("div", {
			styles : this.css.formTopContentCustomNode,
			text : this.lp.loginAction
		}).inject( this.formTopContentNode )
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
							focus : function( it, ev ){ if( this.lp.userName == it.getValue() )it.setValue(""); if( !it.warningStatus )it.getElements()[0].setStyles( this.css.inputActive ); }.bind(this),
							blur: function (it, ev) { if( it.getValue() == "" )it.setValue( this.lp.userName ); if( !it.warningStatus )it.getElements()[0].setStyles( this.css.inputUser ); }.bind(this),
							keyup : function( it, ev ){ if( ev.event.keyCode == 13 ){ this.gotoStep( 2 ) } }.bind(this)
						}, onEmpty : function( it ){
							it.getElements()[0].setStyles( this.css.inputEmpty );
						}.bind(this), onUnempty : function( it ){
							it.getElements()[0].setStyles(  this.css.inputUser );
						}.bind(this) },
					codeAnswer : { text: this.lp.verificationCode, defaultValue : this.lp.inputVerificationCode, className : "inputVerificationCode2",
						notEmpty : true,defaultValueAsEmpty: true, emptyTip: this.lp.inputVerificationCode, event : {
							focus : function( it, ev ){ if( this.lp.inputVerificationCode == it.getValue() )it.setValue(""); if( !it.warningStatus )it.getElements()[0].setStyles( this.css.inputActive ); }.bind(this),
							blur: function (it, ev) {
								if( it.getValue() == "" )it.setValue( this.lp.inputVerificationCode );
								if( !it.warningStatus )it.getElements()[0].setStyles( this.css.inputVerificationCode2 );
							}.bind(this),
							keyup : function( it, ev ){ if( ev.event.keyCode == 13 ){ this.gotoStep( 2 ) } }.bind(this)
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
							focus : function( it, ev ){  if( "password" == it.getValue() )it.setValue(""); if( !it.warningStatus )it.getElements()[0].setStyles( this.css.inputActive ); }.bind(this),
							blur : function( it, ev ){ if( !it.warningStatus )it.getElements()[0].setStyles( this.css.inputPassword ); }.bind(this),
							keyup : function(it, ev){ this.pwStrength(it.getValue()) }.bind(this)
						}, onEmpty : function( it ){
							it.getElements()[0].setStyles( this.css.inputEmpty );
						}.bind(this), onUnempty : function( it ){
							it.getElements()[0].setStyles(  this.css.inputPassword );
						}.bind(this) },
					confirmPassword : { text: this.lp.confirmNewPassword, type : "password", className : "inputComfirmPassword",
						notEmpty : true,defaultValueAsEmpty: true, emptyTip: this.lp.inputComfirmPassword,
						validRule : { passwordNotEqual: function( value, it ){
							if( it.getValue() == this.stepForm_2.getItem("password").getValue() )return true;
						}.bind(this)},
						validMessage : { passwordNotEqual : this.lp.passwordNotEqual },
						event : {
							focus : function( it, ev ){  if( "password" == it.getValue() )it.setValue(""); if( !it.warningStatus )it.getElements()[0].setStyles( this.css.inputActive ); }.bind(this),
							blur : function( it, ev ){ if( !it.warningStatus )it.getElements()[0].setStyles( this.css.inputComfirmPassword ); }.bind(this),
							keyup : function( it, ev ){ if( ev.event.keyCode == 13 ){ this.gotoStep( 3 ) } }.bind(this)
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
		if( isSuccess ){
			this.stepNode_3.setStyles( this.css.stepFormResult );
			this.stepNode_3.setStyle("display","");
			new Element("div", {
				styles : this.css.resetPasswordSuccess,
				text : this.lp.resetPasswordSuccess
			}).inject( this.stepNode_3 );
			var area = new Element("div", {
				styles : this.css.resetPasswordResultArea
			}).inject( this.stepNode_3 );
			new Element("div", {
				styles : this.css.resetPasswordResultWord,
				text : this.lp.resetPasswordSuccessWord
			}).inject( area );
			var action = new Element("div", {
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
			var area = new Element("div", {
				styles : this.css.resetPasswordResultArea
			}).inject( this.stepNode_3 );
			new Element("div", {
				styles : this.css.resetPasswordResultWord,
				text : this.lp.resetPasswordFailWord
			}).inject( area );
			var action = new Element("div", {
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
		for( var i = 1; i<=step; i++ ){
			this["step_"+i].setStyles( this.css["step_"+i+"_active"] );
			if( i!=step && this["stepLink_"+i]){
				this["stepLink_"+i].setStyles( this.css["stepLink_"+i+"_active"] );
			}
		}
		for( var i = step+1; i<=3; i++ ){
			this["step_"+i].setStyles( this.css["step_"+i] );
			if( i!=3 ){
				this["stepLink_"+i].setStyles( this.css["stepLink_"+i] );
			}
		}
		for( var i = 1; i<=3; i++ ){
			if( i==step ){
				this["stepNode_"+i].setStyle( "display" , "" );
			}else{
				this["stepNode_"+i].setStyle( "display" , "none" );
			}
		}

		if( step == 3 ){
			var d = {
				credential : this.stepForm_1.getItem("name").getValue(),
				codeAnswer : this.stepForm_1.getItem("codeAnswer").getValue(),
				password : this.stepForm_2.getItem("password").getValue()
			}
			this.actions.resetPassword( d, function( json ){
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
		this.actions.checkRegisterMobile( mobile, function( json ){
			flag = true
		}.bind(this), function( arg1, arg2, arg3 ){
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
			if( errorObj.status == 404){
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
		var passwordStrengthArea = this.formTableArea.getElements("[item='passwordStrengthArea']")[0];

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
		var level;
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
		if (pwd==null||pwd==''){
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