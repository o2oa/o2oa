MWF.xDesktop.requireApp("OnlineMeeting", "Actions.RestActions", null, false);
MWF.require("MWF.widget.MaskNode", null, false);
MWF.xApplication.OnlineMeeting.options.multitask = false;
MWF.xApplication.OnlineMeeting.Main = new Class({
	Extends: MWF.xApplication.Common.Main,
	Implements: [Options, Events],

	options: {
		"style": "default",
		"name": "OnlineMeeting",
		"icon": "icon.png",
		"width": "500",
		"height": "600",
		"isResize": true,
		"isMax": true,
		"title": MWF.xApplication.OnlineMeeting.LP.title
	},
    onQueryLoad: function(){
        this.lp = MWF.xApplication.OnlineMeeting.LP;
        this.actions = new MWF.xApplication.OnlineMeeting.Actions.RestActions();
    },
    getLogin: function(success, failure){
        //if (success) success();
        if (failure) failure();
    },
    loginOpenMeeting: function(data, success, failure){
        var uri = this.actions.getLoginUri(data);
        var iframe = new Element("iframe", {"src": uri, "styles": {"display": "none"}}).inject(this.content);
        //window.open(uri);
        //window.setTimeout(function(){
            if (success) success();
        //}.bind(this), 5000);
    },
    mask: function(){
        if (!this.maskNode){
            this.maskNode = new MWF.widget.MaskNode(this.content, {"style": "bam"});
            this.maskNode.load();
        }
    },
    unmask: function(){
        if (this.maskNode) this.maskNode.hide(function(){
            MWF.release(this.maskNode);
            this.maskNode = null;
        }.bind(this));
    },
    loadApplication: function(callback) {
        this.actions.getOpenMeeting(function(json){
            this.meetingLoginData = json.data;
            this.getLogin(function(){
                this.loadMeetingRoom();
            }.bind(this), function(){
                if (this.meetingLoginData){
                    this.mask();
                    this.loginOpenMeeting(this.meetingLoginData, function(){
                        this.loadMeetingRoom();
                    }.bind(this));
                }
            }.bind(this));
        }.bind(this));
    },
    loadMeetingRoom: function(){
        this.titleNode = new Element("div", {"styles": this.css.titleNode}).inject(this.content);
        //this.titleIconNode = new Element("div", {"styles": this.css.titleNode});
        //this.titleTextNode = new Element("div", {"styles": this.css.titleNode});

        this.titleNode.set("text", this.lp.netMeetingRoom);

        this.contentNode = new Element("div", {"styles": this.css.contentNode}).inject(this.content);

        this.setContentSize();
        this.addEvent("resize", this.setContentSize.bind(this));

        //this.content.setStyle("overflow": "")

        this.loadCountent();
    },
    setContentSize: function(){
        var size = this.content.getSize();
        var titleSize = this.titleNode.getSize();
        var h = size.y-titleSize.y;
        this.contentNode.setStyle("height", ""+h+"px");
    },
    loadCountent: function(){
        this.actions.listRoom(function(json){
            json.data.each(function(d){
                d.url = this.actions.getRoomUri(this.meetingLoginData, d);
                new MWF.xApplication.OnlineMeeting.room(this, d);
            }.bind(this));
            this.unmask();
        }.bind(this));
    }
});

MWF.xApplication.OnlineMeeting.room = new Class({
    initialize: function(app, data){
        this.data = data
        this.app = app
        this.css = this.app.css;
        this.container = this.app.contentNode;
        this.lp = this.app.lp;
        this.load();
    },
    load: function(){
        this.node = new Element("div", {"styles": this.css.roomNode}).inject(this.container);
        this.iconNode = new Element("div", {"styles": this.css.roomIconNode}).inject(this.node);
        this.textNode = new Element("div", {"styles": this.css.roomTextNode}).inject(this.node);
        this.textNode.set("text", this.data.name);

        this.node.addEvent("click", function(e){
            window.open(this.data.url);
             // var _self = this;
             // var options = {"url": this.data.url};
             // this.app.desktop.openApplication(e, "OnlineMeetingRoom", options);
        }.bind(this));
    }
});