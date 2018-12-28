
MWF.require("MWF.xDesktop.Widget", null, false);
MWF.xApplication.Common.Widget = new Class({
	Extends: MWF.widget.Common,
	Implements: [Options, Events],
	
	options: {
		"style": "default",
        "appName": "Common",
		"name": "Widget",
		"width": "400",
		"height": "400",
        "position": {"right": 10, "bottom": 10}
	},
	initialize: function(desktop, options){
		this.setOptions(options);
        this.desktop = desktop;
        this.path = "/x_component_"+this.options.appName.replace(/\./g, "_")+"/$"+this.options.name.replace(/\./g, "/")+"/";
        this.cssPath = this.path+this.options.style+"/css.wcss";
        this._loadCss();
	},
	fireAppEvent: function(when){
		this.fireEvent(when);
		if (this[("on-"+when).camelCase()]) this[("on-"+when).camelCase()]();
	},
	load : function(isCurrent){
		this.fireAppEvent("queryLoad");
		this.loadWidget();
	},
    loadContent: function(callback){
		if (callback) callback();
	},
    loadWidget: function(){
		this.fireAppEvent("queryLoadWidget");

		var options = {
            "title": this.options.title,
            "width": this.options.width,
            "height": this.options.height,
            "position": this.options.position,
            "onQueryClose": function(){
                this.fireAppEvent("rueryClose");
            }.bind(this),
            "onPostClose": function(){
                this.desktop.closeWidget(this);
                this.fireAppEvent("postClose");
            }.bind(this),
            "onScroll": function(y){
                this.fireEvent("scroll", [y]);
            }.bind(this),
            "onOpen": function(e){
                this.openApplication(e);
            }.bind(this),
            "onDragComplete": function(el, e){
                this.fireEvent("dragComplete", [el, e]);
            }.bind(this)
		};
		this.widget = new MWF.xDesktop.Widget(this.desktop, options);
		this.fireAppEvent("loadWidget");
		this.widget.load();

        this.fireAppEvent("postLoadWidget");
        this.fireAppEvent("queryLoadContent");

        this.content = this.widget.contentNode;
        this.loadContent(function(){
            this.fireAppEvent("postLoadContent");
        }.bind(this));
        this.fireAppEvent("postLoad");

	},
    openApplication: function(e){
        this.desktop.openApplication(e, this.options.appName);
    },

	//widget事件
	onQueryLoad: function(){},
	onQueryLoadWidget: function(){},
	onLoadWidget: function(){},
	onPostLoadWidget: function(){},
	onQueryLoadContent: function(){},
	onPostLoadContent: function(){},
	onPostLoad: function(){},
	onQueryClose: function(){},
	onPostClose: function(){}
});