MWF.require("MWF.widget.MaskNode", null, false);
o2.requireApp("AppMarketV2", "RecommendContent", null, false);
MWF.xApplication.AppMarketV2.Main = new Class({
    Extends: MWF.xApplication.Common.Main,
    Implements: [Options, Events],
    options: {
        "style": "default",
        "mvcStyle": "style.css",
        "name": "AppMarketV2",
        "icon": "icon.png",
        "width": "1000",
        "height": "700",
        "isResize": true,
		"isMax": true,
        "title": MWF.xApplication.AppMarketV2.LP.title,
        "minHeight": 700
    },
    onQueryLoad: function(){
        this.lp = MWF.xApplication.AppMarketV2.LP;
        this.actions = MWF.Actions.load("x_program_center");
		this.viewPath = this.path+this.options.style+"/view.html";
		this.iconPath = this.path+this.options.style+"/icon/";
	},
	mask: function(){
        if (!this.maskNode){
            this.maskNode = new MWF.widget.MaskNode(this.marketnode, {"style": "bam"});
            this.maskNode.load();
        }
    },
    unmask: function(){
        if (this.maskNode) this.maskNode.hide(function(){
            MWF.release(this.maskNode);
            this.maskNode = null;
        }.bind(this));
    },
    loadApplication: function(callback){
		this.content.loadHtml(this.viewPath, {"bind": {"lp": this.lp}, "module": this}, function(){
			if (!this.options.isRefresh){
				this.maxSize(function(){
					this.loadApp(callback);
				}.bind(this));
			}else{
				this.loadApp(callback);
			}
		}.bind(this));
	},
	loadApp: function(callback){
		//this.initNode();
		this.initNodeSize();

		this.recommondLoaded = false;
		this.applicationsLoaded = true;

		this.loadRecommondContent(function(){ this.recommondLoaded = true; this.checkAppLoaded(callback); }.bind(this));
		this.loadApplicationsContent(function(){ this.applicationsLoaded = true; this.checkAppLoaded(callback); }.bind(this));
	},
	checkAppLoaded: function(callback){
		if (this.recommondLoaded && this.applicationsLoaded){
			if (callback) callback();
		}
	},
	initNodeSize: function(){
		this.resizeNodeSize();
		this.addEvent("resize", this.resizeNodeSize.bind(this));
	},
	resizeNodeSize: function(){
		var size = this.content.getSize();
		var edge = this.marketnode.getEdgeHeight();
		var height = size.y - edge;
		if (height<this.options.minHeight) height = this.options.minHeight;
		this.marketnode.setStyle("height", ""+height+"px");
	},
	loadRecommondContent: function(callback){
		debugger;
		this.recommendContent = new MWF.xApplication.AppMarketV2.RecommendContent(this, this.topRecommendNode, {
			"onLoad": function(){if (callback) callback();}
		});
	},

	loadApplicationsContent: function(callback){
		
		o2.requireApp("AppMarketV2", "ApplicationsContent", function(){
			this.applicationsContent = new MWF.xApplication.AppMarketV2.ApplicationsContent(this, this.applicationsNode, {
				"onLoad": function(){if (callback) callback();}
			});
		}.bind(this));
		
	}
});
