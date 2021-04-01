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
			this.marketnode.setStyle("overflow","hidden");
            this.maskNode = new MWF.widget.MaskNode(this.marketnode, {"style": "bam"});
			this.maskNode.load();
        }
    },
    unmask: function(){
        if (this.maskNode) this.maskNode.hide(function(){
            MWF.release(this.maskNode);
			this.maskNode = null;
			this.marketnode.setStyle("overflow","auto");
        }.bind(this));
    },
    loadApplication: function(callback){
		if (MWF.AC.isAdministrator()){	//this.checkO2Collect();
		
			this.content.loadHtml(this.viewPath, {"bind": {"lp": this.lp}, "module": this}, function(){
				if (!this.options.isRefresh){
					this.maxSize(function(){
						//检查是否在云服务器上已注册
						this.actions.CollectAction.login(//平台封装好的方法
							function( json ){ //服务调用成功的回调函数, json为服务传回的数据
								if (json.type && json.type=="success"){
									this.loadApp(callback);
								}
							}.bind(this),
							function( json ){ //服务调用成功的回调函数, json为服务传回的数据
								errtype = JSON.parse(json.response).type;
								if (errtype && errtype=="error"){
									//o2.xDesktop.notice("error", {x: "right", y:"top"}, JSON.parse(json.response).message+"请至系统配置——云服务配置——连接配置注册并连接到O2云");
									this.loadCloudConnectTip(callback);
								}
							}.bind(this)
							,false //同步执行 
						);
						
					}.bind(this));
				}else{
					this.loadApp(callback);
				}
			}.bind(this));
		}else{
			o2.xDesktop.notice("error", {x: "right", y:"top"}, this.lp.accessDenyNotice);
		}
	},
	loadApp: function(callback){
		//this.initNode();
		this.initNodeSize();

		this.recommondLoaded = false;
		this.applicationsLoaded = true;

		this.loadRecommondContent(function(){ this.recommondLoaded = true; this.checkAppLoaded(callback); }.bind(this));
		this.loadApplicationsContent(function(){ this.applicationsLoaded = true; this.checkAppLoaded(callback); }.bind(this));
	},
	loadCloudConnectTip:function(callback){
		this.initNodeSize();
		o2.requireApp("AppMarketV2", "CloudConnectTip", function(){
			this.cloudTip = new MWF.xApplication.AppMarketV2.CloudConnectTip(this, this.cloudConnectTipNode, {
				"onLoad": function(){if (callback) callback();}
			});
		}.bind(this));
		
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
