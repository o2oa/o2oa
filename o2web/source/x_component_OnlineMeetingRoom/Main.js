MWF.xApplication.OnlineMeetingRoom.options.multitask = false;
MWF.xApplication.OnlineMeetingRoom.Main = new Class({
	Extends: MWF.xApplication.Common.Main,
	Implements: [Options, Events],

	options: {
		"style": "default",
		"name": "OnlineMeetingRoom",
		"icon": "icon.png",
		"width": "1200",
		"height": "800",
        "url": "",
		"isResize": true,
		"isMax": true,
		"title": MWF.xApplication.OnlineMeetingRoom.LP.title
	},
    onQueryLoad: function(){
        this.lp = MWF.xApplication.OnlineMeetingRoom.LP;
    },
    loadApplication: function(callback) {
        if (!this.options.isRefresh){
            this.maxSize(function(){
                this.open();
            }.bind(this));
        }else{
            this.open();
        }
    },

    open: function(){
        this.node = new Element("iframe", {
            "src": this.options.url,
            "styles": this.css.node,
            "border": "0"
        }).inject(this.content);
    }
});
