//用法，z在 cms / 流程表单 / 门户中插入会议应用
//MWF.xApplication.Calendar = MWF.xApplication.Calendar || {};
//MWF.xApplication.Calendar.options = MWF.xApplication.Calendar.options || {};
//MWF.xDesktop.requireApp("Calendar", "MainInContainer", null, false);
//MWF.xApplication.Calendar.MainInContainer2 = new Class({
//    Extends: MWF.xApplication.Calendar.MainInContainer,
//    loadLayout: function(){
//        this.contentNode = new Element("div", {"styles": this.css.contentNode}).inject(this.node);
//        this.toWeek();
//    }
//});
//var container = this.form.get("weekViewContainer").node;
//var scrollNode = this.target.app.node;
//var calendar = new MWF.xApplication.Calendar.MainInContainer2( this.target.app.desktop, {}, container, this.target.app.content , scrollNode );
//calendar.load();

MWF.xDesktop.requireApp("Calendar", "lp."+MWF.language, null, false);
MWF.xDesktop.requireApp("Calendar", "Main", null, false);
MWF.xApplication.Calendar.MainInContainer = new Class({
    Extends: MWF.xApplication.Calendar.Main,
    Implements: [Options, Events],
    options: {
        "style": "default",
        "name": "Calendar",
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

        this.cssPath =this.path+this.options.style+"/css.wcss" ;
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
        this.container.setStyle("overflow", "hidden");
        this.node = new Element("div", {
            "styles": {"width": "100%", "height": "100%", "overflow": "hidden"}
        }).inject(this.container);

        this.naviContainerNode = new Element("div.naviContainerNode", {
            "styles": this.css.naviContainerNode
        }).inject(this.node);
        this.leftTitleNode = new Element("div.leftTitleNode", {
            "styles": this.css.leftTitleNode
        }).inject(this.naviContainerNode);

        this.rightContentNode = new Element("div", {
            "styles":this.css.rightContentNode
        }).inject(this.node);
    }
});