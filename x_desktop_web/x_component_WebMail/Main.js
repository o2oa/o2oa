MWF.xDesktop.requireApp("WebMail", "Actions.RestActions", null, false);
MWF.xApplication.WebMail.options.multitask = false;
MWF.xApplication.WebMail.Main = new Class({
	Extends: MWF.xApplication.Common.Main,
	Implements: [Options, Events],

	options: {
		"style": "default",
		"name": "WebMail",
		"icon": "icon.png",
		"width": "1000",
		"height": "700",
		"title": MWF.xApplication.WebMail.LP.title
	},

	onQueryLoad: function(){
		this.lp = MWF.xApplication.WebMail.LP;
	},
	loadApplication: function(callback){
        debugger;
        this.action = new MWF.xApplication.WebMail.Actions.RestActions();
        this.createNode();

        if (Browser.ie){
            this.openMailNewWindow();
        }else{
            this.openMail();
        }
	},
    //ssoMail: function(){
    //    var url = "http://mail.vsettan.com.cn/names.nsf?login?login&username="+this.desktop.session.user.unique+"&password=password&RedirectTo=/land/xsso.nsf/(getUserMail)?openpage";
    //
    //    MWF.getJSONP(url, {
    //        "onSuccess": function(json){
    //            if (json.name==this.desktop.session.user.unique){
    //                this.openMail(json.mail);
    //            }
    //            if (callback) callback();
    //        }.bind(this)
    //    }, true);
    //},
    //todo:打开邮件信息
    openMail: function(mail){
        var mailUrl = mail;
        //var url = "http://mail.vsettan.com.cn/names.nsf?login?login&username="+this.desktop.session.user.unique+"&password=password&RedirectTo=/mail/"+this.desktop.session.user.unique+".nsf/test?OpenPage";
        this.action.getPassword(function(json){
            var url = "http://"+layout.config.mail+"/names.nsf?login?login&username="+this.desktop.session.user.unique+"&password="+json.data.password+"&RedirectTo=/";
            //var url = "http://mail.vsettan.com.cn/names.nsf?login?login&username=huqi&password=password&RedirectTo=/mail/huqi.nsf/test?OpenPage";

            // window.open(url);
            this.iframe = new Element("iframe", {
                "src": url,
                "styles": this.css.iframe,
                "border": "0"
            });
            this.iframe.inject(this.node);

        }.bind(this));
    },
    openMailNewWindow: function(){
        this.action.getPassword(function(json){
            var url = "http://"+layout.config.mail+"/names.nsf?login?login&username="+this.desktop.session.user.unique+"&password="+json.data.password+"&RedirectTo=/";
            window.open(url);
            this.close();
        }.bind(this));
    },
    createNode: function(){
        this.content.setStyle("overflow", "hidden");
        this.node = new Element("div", {
            "styles": {
                "width": "100%",
                "height": "100%",
                "overflow": "hidden"
            }
        }).inject(this.content);
    }

});
