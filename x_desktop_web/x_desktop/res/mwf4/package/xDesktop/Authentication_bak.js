MWF.xDesktop = MWF.xDesktop || {};
MWF.xApplication = MWF.xApplication || {};
MWF.require("MWF.xDesktop.Actions.RestActions", null, false);

MWF.xDesktop.Authentication = new Class({
	Extends: MWF.widget.Common,
	Implements: [Options, Events],
	options: {
		"style": "default"
	},
	initialize: function(options){
		this.setOptions(options);
		this.action = new MWF.xDesktop.Actions.RestActions("/xDesktop/Actions/action.json", "x_organization_assemble_authentication");
		this.path = MWF.defaultPath+"/xDesktop/$Authentication/";
		this.cssPath = MWF.defaultPath+"/xDesktop/$Authentication/"+this.options.style+"/css.wcss";
		this._loadCss();
	},
	isAuthenticated: function(success, failure){
		this.action.getAuthentication(success, failure);
	},
	
	loadLogin: function(node){
        this.loginNode = new Element("div", {"styles": this.css.loginNode});
        //this.qrAreaNode = new Element("div", {"styles": this.css.qrAreaNode}).inject(this.loginNode);
        //this.qrNode = new Element("div", {"styles": this.css.qrNode}).inject(this.qrAreaNode);
        //this.qrTextNode = new Element("div", {"styles": this.css.qrTextNode, "text": MWF.LP.desktop.login.mobileDownload}).inject(this.qrAreaNode);
        //this.qrIconNode = new Element("div", {"styles": this.css.qrIconNode}).inject(this.qrAreaNode);

        this.loginAreaNode = new Element("div", {"styles": this.css.loginAreaNode}).inject(this.loginNode);
		this.loginTitleNode = new Element("div", {"styles": this.css.loginTitleNode, "text": MWF.LP.desktop.login.title}).inject(this.loginAreaNode);

		this.loginUserNode = new Element("div", {"styles": this.css.loginUserNode}).inject(this.loginAreaNode);
		this.userInput = new Element("input",{
			"styles": this.css.loginInputNode,
			"type": "text",
			"value": "Username"
		}).inject(this.loginUserNode);
		
		this.loginPassNode = new Element("div", {"styles": this.css.loginPassNode}).inject(this.loginAreaNode);
		this.passInput = new Element("input",{
			"styles": this.css.loginInputNode,
			"type": "password",
			"value": "Password"
		}).inject(this.loginPassNode);
		
		this.loginButtonNode = new Element("button", {"styles": this.css.loginButtonNode, "text": MWF.LP.desktop.login.loginButton}).inject(this.loginAreaNode);
		this.loginInforNode = new Element("div", {"styles": this.css.loginInforNode}).inject(this.loginAreaNode);
		
		this.setEvent();
		this.show(node);
	},
	setEvent: function(){
		this.userInput.addEvents({
			"focus": function(){if (this.get("value")=="Username") this.set("value", "");},
			"blur": function(){if (!this.get("value")) this.set("value", "Username");},
			"keydown": function(e){if (e.code==13) this.passInput.focus();}.bind(this)
		});
		this.passInput.addEvents({
			"focus": function(){if (this.get("value")=="Password") this.set("value", "");},
			"blur": function(){if (!this.get("value")) this.set("value", "Password");},
			"keydown": function(e){if (e.code==13) this.login();}.bind(this)
		});
		this.loginButtonNode.addEvents({
            "click": function(){this.login();}.bind(this)
		});
	},
	show: function(node){
		this.loginNode.inject(node);
		var size = this.loginNode.getSize();
        //var qrsize = this.qrNode.getSize();
		var bodySize = node.getSize();
		var startTop = 0-size.y;
		var startLeft = (bodySize.x-size.x)/2
		var toTop = (bodySize.y-size.y)/2;
        //this.qrNode.setStyles({
        //    "top": ""+startTop+"px",
        //    "left": ""+startLeft+"px"
        //});
        //var startLoginLeft = startLeft+qrsize.x;
		this.loginNode.setStyles({
			"top": ""+startTop+"px",
			"left": ""+startLeft+"px"
		});
		
		this.loginNode.set("morph", {duration: 600, transition: Fx.Transitions.Elastic.easeOut});
		this.loginNode.morph({"top": ""+toTop+"px"});

        //this.qrNode.set("morph", {duration: 600, transition: Fx.Transitions.Elastic.easeOut});
        //this.qrNode.morph({"top": ""+toTop+"px"});
	},
	errorEffect: function(){
		var left = this.loginNode.getPosition().x.toInt();
		new Fx.Morph(this.loginNode, {duration: 20}).start({"left": left-8}).chain(function(){
			new Fx.Morph(this.loginNode, {duration: 20}).start({"left": left+8}).chain(function(){
				new Fx.Morph(this.loginNode, {duration: 10}).start({"left": left-4}).chain(function(){
					new Fx.Morph(this.loginNode, {duration: 10}).start({"left": left+4}).chain(function(){
						new Fx.Morph(this.loginNode, {duration: 10}).start({"left": left}).chain(function(){
							
						}.bind(this));
					}.bind(this));
				}.bind(this));
			}.bind(this));
		}.bind(this));
	},
	login: function(){
        debugger;
		var user = this.userInput.get("value");
		var pass = this.passInput.get("value");
		
		if (!user || !pass){
			this.loginInforNode.set("text", MWF.LP.desktop.login.inputUsernamePassword);
			this.errorEffect();
		}else{
			this.loginInforNode.set("text", MWF.LP.desktop.login.loginWait);
			
			var data = {
				"credential": user.trim(),
				"password": pass
			};
			this.action.login(data, function(json){
            //    Cookie.write("x_token", json.data.token);
                layout.desktop.session.user = json.data;
				var top = 0-this.loginNode.getSize().y;
				new Fx.Morph(this.loginNode, {duration: 200}).start({"top": top}).chain(function(){
					this.loginNode.destroy();
					this.fireEvent("login");
				}.bind(this));
			}.bind(this), function(){
				this.loginInforNode.set("text", MWF.LP.desktop.login.loginError);
				this.errorEffect();
			}.bind(this));
		}
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
	}
	
});