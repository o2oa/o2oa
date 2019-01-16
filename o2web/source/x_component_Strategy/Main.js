MWF.xApplication.Strategy = MWF.xApplication.Strategy || {};
MWF.require("MWF.widget.Identity", null,false);
//MWF.xDesktop.requireApp("Strategy", "Actions.RestActions", null, false);
MWF.xDesktop.requireApp("Template", "Explorer", null, false);
MWF.xDesktop.requireApp("Template", "MDomItem", null, false);
MWF.xApplication.Strategy.options.multitask = true;
MWF.xApplication.Strategy.Main = new Class({
	Extends: MWF.xApplication.Common.Main,
	Implements: [Options, Events],

	options: {
		"style": "default",
		"name": "Strategy",
		"icon": "icon.png",
		"width": "1200",
		"height": "700",
		"isResize": true,
		"isMax": true,
		"title": MWF.xApplication.Strategy.LP.title
	},
	onQueryLoad: function(){
		this.lp = MWF.xApplication.Strategy.LP;
		this.app = this;
	},
	loadApplication: function(){
		this.user = layout.desktop.session.user.name;
		this.distinguishedName = layout.desktop.session.user.distinguishedName;
		this.userGender = layout.desktop.session.user.genderType;
		//this.actions = new MWF.xApplication.Strategy.Actions.RestActions();
		this.actions = MWF.Actions.get("x_strategydeploy_assemble_control");

		this.initProfile(this.createContainer());

		this.addEvent("resize", function(){
			this.resizeContent();
		}.bind(this));
	},
	recordStatus: function(){
		var status = {
			currentNavi : this.currentNavi
		};
		return status;
	},
	resizeContent : function(){
		var size = this.container.getSize();
		this.middleContent.setStyles({"height":(size.y-this.naviTab.getHeight())+"px"});

		//var y = size.y-300;
		//var x = size.x-450;
		//if(this.todoListContent){this.todoListContent.setStyles({"height":y+"px"});this.todoListContentY = y}
		//if(this.workListContent){this.workListContent.setStyles({"height":y+"px"});this.todoListContentY = y}
		//if(this.workConditionContentDiv)this.workConditionContentDiv.setStyles({"height":y+"px"});
		//if(this.leftContent)this.leftContent.setStyles({"width":x+"px"});
	},
	createShade: function(o,txtInfo){
		var defaultObj = this.content;
		var obj = o || defaultObj;
		var txt = txtInfo || "loading...";
		if(this.shadeDiv){
			$(this.shadeDiv).destroy();
		}
		if(this["shadeTxtDiv"])  this["shadeTxtDiv"].destroy();
		this.shadeDiv = new Element("div.shadeDiv").inject(obj);
		this.inforDiv = new Element("div.inforDiv",{
			styles:{"height":"16px","display":"inline-block","position":"absolute","background-color":"#000000","border-radius":"3px","padding":"5px 10px"}
		}).inject(this.shadeDiv);
		this.loadImg = new Element("img.loadImg",{
			styles:{"width":"16px","height":"16px","float":"left"},
			src:this.path+"default/icon/loading.gif"
		}).inject(this.inforDiv);

		this.shadeTxtSpan = new Element("span.shadeTxtSpan").inject(this.inforDiv);
		this.shadeTxtSpan.set("text",txt);
		this.shadeDiv.setStyles({
			"width":"100%","height":"100%","position":"absolute","opacity":"0.6","background-color":"#cccccc","z-index":"999"
		});
		this.shadeTxtSpan.setStyles({"color":"#ffffff","font-size":"12px","display":"inline-block","line-height":"16px","padding-left":"5px"});

		var x = obj.getSize().x;
		var y = obj.getSize().y;
		this.shadeDiv.setStyles({
			"left":(obj.getLeft()-defaultObj.getLeft())+"px",
			"top":(obj.getTop()-defaultObj.getTop())+"px",
			"width":x+"px",
			"height":y+"px"
		});
		if(obj.getStyle("position")=="absolute"){
			this.shadeDiv.setStyles({
				"left":"0px",
				"top":"0px"
			});
		}
		this.inforDiv.setStyles({
			"left":(x/2)+"px",
			"top":(y/2)+"px"
		});
	},
	destroyShade : function(){
		if(this.shadeDiv) $(this.shadeDiv).destroy();
		//if(this.shadeDiv) this.shadeDiv.destroy()
	},
	showErrorMessage:function(xhr,text,error){
		var errorText = error;
		var errorMessage;
		if (xhr) errorMessage = xhr.responseText;
		if(errorMessage!=""){
			var e = JSON.parse(errorMessage);
			if(e.message){
				this.notice( e.message,"error");
			}else{
				this.notice( errorText,"error");
			}
		}else{
			this.notice(errorText,"error");
		}

	},

	initProfile:function(callback){
		// get some profile data
		//...

		if(callback)callback();
	},

	createContainer: function(){
		//var _self = this;
		if( !this.container ){
			this.content.setStyle("overflow", "hidden");
			this.container = new Element("div.container", {
				"styles": this.css.container
			}).inject(this.content);
		}

		//navi
		this.naviTab = new Element("div.naviTab",{"styles":this.css.naviTab}).inject(this.container);
		//公司工作重点
		this.keyWorkTab = new Element("div.keyWorkTab",{"styles":this.css.keyWorkTab}).inject(this.naviTab);
		this.keyWorkTabImg = new Element("div.keyWorkTabImg",{
			"styles":this.css.keyWorkTabImg
		}).inject(this.keyWorkTab);
		this.keyWorkTabLabel = new Element("div.keyWorkTabLabel",{
			"styles":this.css.keyWorkTabLabel,
			"text":this.lp.keyWork.name
		}).inject(this.keyWorkTab);
		this.keyWorkTab.addEvents({
			"click":function(){
				this.openTab("keyWork")
			}.bind(this),
			"mouseover":function(){
				if(this.currentNavi != "keyWork"){
					this.keyWorkTabImg.setStyles({"background-image":"url('/x_component_Strategy/$Main/default/icon/icon_zdgj_click.png')"});
					this.keyWorkTabLabel.setStyles({"color":"#3C76B7"})
				}
			}.bind(this),
			"mouseout":function(){
				if(this.currentNavi != "keyWork"){
					this.keyWorkTabImg.setStyles({"background-image":"url('/x_component_Strategy/$Main/default/icon/icon_zdgj.png')"});
					this.keyWorkTabLabel.setStyles({"color":"#333333"})
				}
			}.bind(this)
		});
		//举措
		this.measureTab = new Element("div.measureTab",{"styles":this.css.measureTab}).inject(this.naviTab);
		this.measureTabImg = new Element("div.measureTabImg",{
			"styles":this.css.measureTabImg
		}).inject(this.measureTab);
		this.measureTabLabel = new Element("div.measureTabLabel",{
			"styles":this.css.measureTabLabel,
			"text":this.lp.measure.name
		}).inject(this.measureTab);
		this.measureTab.addEvents({
			"click":function(){
				this.openTab("measure")
			}.bind(this),
			"mouseover":function(){
				if(this.currentNavi != "measures"){
					this.measureTabImg.setStyles({"background-image":"url('/x_component_Strategy/$Main/default/icon/icon_zljc_click.png')"});
					this.measureTabLabel.setStyles({"color":"#3C76B7"})
				}
			}.bind(this),
			"mouseout":function(){
				if(this.currentNavi != "measure"){
					this.measureTabImg.setStyles({"background-image":"url('/x_component_Strategy/$Main/default/icon/icon_zljc.png')"});
					this.measureTabLabel.setStyles({"color":"#333333"})
				}
			}.bind(this)
		});
		//五项
		//this.priorityTab = new Element("div.priorityTab",{"styles":this.css.priorityTab}).inject(this.naviTab);
		//this.priorityTabImg = new Element("div.priorityTabImg",{
		//	"styles":this.css.priorityTabImg
		//}).inject(this.priorityTab);
		//this.priorityTabLabel = new Element("div.priorityTabLabel",{
		//	"styles":this.css.priorityTabLabel,
		//	"text":this.lp.priority.name
		//}).inject(this.priorityTab);
		//this.priorityTab.addEvents({
		//	"click":function(){
		//		this.openTab("priority")
		//	}.bind(this),
		//	"mouseover":function(){
		//		if(this.currentNavi != "priority"){
		//			this.priorityTabImg.setStyles({"background-image":"url('/x_component_Strategy/$Main/default/icon/icon_5x_click.png')"});
		//			this.priorityTabLabel.setStyles({"color":"#3C76B7"})
		//		}
		//	}.bind(this),
		//	"mouseout":function(){
		//		if(this.currentNavi != "priority"){
		//			this.priorityTabImg.setStyles({"background-image":"url('/x_component_Strategy/$Main/default/icon/icon_5x.png')"});
		//			this.priorityTabLabel.setStyles({"color":"#333333"})
		//		}
		//	}.bind(this)
		//});


		//content
		this.middleContent = new Element("div.middleContent",{"styles":this.css.middleContent}).inject(this.container);

		this.openTab(this.status ? this.status.currentNavi:null);

		this.resizeContent();

	},
	openTab:function(str){
		var tab = str || "keyWork";
		this.changeNaviSelected(tab);
		this.currentNavi = tab;
		if(tab == "keyWork"){
			this.middleContent.empty();
			MWF.xDesktop.requireApp("Strategy", "KeyWorkList", function(){
				this.keyWorkList = new MWF.xApplication.Strategy.KeyWorkList(this.middleContent,this,this.actions);
				this.keyWorkList.load();
			}.bind(this))
		}else if(tab == "measure"){
			this.middleContent.empty();
			MWF.xDesktop.requireApp("Strategy", "MeasureList", function(){
				this.measureList = new MWF.xApplication.Strategy.MeasureList(this.middleContent,this,this.actions);
				this.measureList.load();
			}.bind(this))
		}else if(tab == "priority"){
			this.middleContent.empty();
			MWF.xDesktop.requireApp("Strategy", "PriorityList", function(){
				this.priorityList = new MWF.xApplication.Strategy.PriorityList(this.middleContent,this,this.actions);
				this.priorityList.load();
			}.bind(this))
		}
	},
	changeNaviSelected:function(str){
		if(str == "keyWork"){
			this.keyWorkTab.setStyles({"border-bottom":"2px solid #4990E2"});
			this.measureTab.setStyles({"border-bottom":"0px"});
			//this.priorityTab.setStyles({"border-bottom":"0px"});

			this.keyWorkTabImg.setStyles({"background-image":"url('/x_component_Strategy/$Main/default/icon/icon_zdgj_click.png')"});
			this.keyWorkTabLabel.setStyles({"color":"#3C76B7"});
			this.measureTabImg.setStyles({"background-image":"url('/x_component_Strategy/$Main/default/icon/icon_zljc.png')"});
			this.measureTabLabel.setStyles({"color":"#333333"});
			//this.priorityTabImg.setStyles({"background-image":"url('/x_component_Strategy/$Main/default/icon/icon_5x.png')"});
			//this.priorityTabLabel.setStyles({"color":"#333333"})
		}else if(str == "measure"){
			this.keyWorkTab.setStyles({"border-bottom":"0px"});
			this.measureTab.setStyles({"border-bottom":"2px solid #4990E2"});
			//this.priorityTab.setStyles({"border-bottom":"0px"});

			this.keyWorkTabImg.setStyles({"background-image":"url('/x_component_Strategy/$Main/default/icon/icon_zdgj.png')"});
			this.keyWorkTabLabel.setStyles({"color":"#333333"});
			this.measureTabImg.setStyles({"background-image":"url('/x_component_Strategy/$Main/default/icon/icon_zljc_click.png')"});
			this.measureTabLabel.setStyles({"color":"#3C76B7"});
			//this.priorityTabImg.setStyles({"background-image":"url('/x_component_Strategy/$Main/default/icon/icon_5x.png')"});
			//this.priorityTabLabel.setStyles({"color":"#333333"});
		}else if(str == "priority"){
			this.keyWorkTab.setStyles({"border-bottom":"0px"});
			this.measureTab.setStyles({"border-bottom":"0px"});
			//this.priorityTab.setStyles({"border-bottom":"2px solid #4990E2"});

			this.keyWorkTabImg.setStyles({"background-image":"url('/x_component_Strategy/$Main/default/icon/icon_zdgj.png')"});
			this.keyWorkTabLabel.setStyles({"color":"#333333"});
			this.measureTabImg.setStyles({"background-image":"url('/x_component_Strategy/$Main/default/icon/icon_zljc.png')"});
			this.measureTabLabel.setStyles({"color":"#333333"});
			//this.priorityTabImg.setStyles({"background-image":"url('/x_component_Strategy/$Main/default/icon/icon_5x_click.png')"});
			//this.priorityTabLabel.setStyles({"color":"#3C76B7"});
		}

	}
});
