//用法，z在 cms / 流程表单 / 门户中插入会议应用
//MWF.xApplication.Meeting = MWF.xApplication.Meeting || {};
//MWF.xApplication.Meeting.options = MWF.xApplication.Meeting.options || {};
//MWF.xDesktop.requireApp("Meeting", "MainInContainer", null, false);
//var container = this.form.get("div").node;
//var scrollNode = container;
//var meeting = new MWF.xApplication.Meeting.MainInContainer( this.form.getApp().desktop, {}, container, this.form.getApp().content , scrollNode );
//meeting.load();

MWF.xDesktop.requireApp("Meeting", "lp."+MWF.language, null, false);
MWF.xDesktop.requireApp("Meeting", "Main", null, false);
MWF.xApplication.Meeting.MainInContainer = new Class({
    Extends: MWF.xApplication.Meeting.Main,
    Implements: [Options, Events],
    options: {
        "style": "default" ,
        "name": "Meeting",
        "sideBarEnable" : false
    },
    initialize: function(desktop, options, container, content, scrollNode){
        this.setOptions(options);
        this.desktop = desktop;
        this.container = container;
        this.content = content;
        this.scrollNode = scrollNode; 
        this.path = "/x_component_"+this.options.name.replace(/\./g, "_")+"/$Main/";
        this.options.icon = this.path+this.options.style+"/"+this.options.icon;

        this.cssPath =this.path+this.options.style+"/css.wcss";
        this.inBrowser = true;
        this.inContainer = true;
        this._loadCss();
    },
    loadInBrowser: function(){
        this.window = {
            "isHide": false,
            "isMax": true,
            "maxSize": function(){},
            "restore": function(){},
            "setCurrent": function(){},
            "hide": function(){},
            "maxOrRestoreSize": function(){},
            "restoreSize": function(){},
            "close": function(){},
            "titleText" : {
                set : function(){}
            }
        };
        this.window.content = this.content;
        //this.content = this.window.content;

        //this.content.setStyles({"height": "100%", "overflow": "hidden"});

        //window.addEvent("resize", function(){
        //    this.fireAppEvent("resize");
        //}.bind(this));
        //window.onbeforeunload = function(e){
        //    this.fireAppEvent("queryClose");
        //}.bind(this);

        this.fireAppEvent("postLoadWindow");
        this.fireAppEvent("queryLoadApplication");
        this.setContentEvent();
        this.loadApplication(function(){
            this.fireAppEvent("postLoadApplication");
        }.bind(this));

        //this.content.setStyle("height", document.body.getSize().y);

        this.fireAppEvent("postLoad");
    },
    createNode: function(){
        this.content.setStyle("overflow", "hidden");
        this.node = new Element("div.reportNode", {
            "styles": {"width": "100%", "height": "100%", "overflow": "hidden", "position":"relative","background-color":"#f0f0f0"}
        }).inject(this.container);
    }
});