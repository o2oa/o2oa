MWF.xApplication.ExeManager = MWF.xApplication.ExeManager || {};
MWF.require("MWF.widget.Identity", null,false);
MWF.xDesktop.requireApp("ExeManager", "Actions.RestActions", null, false);
MWF.xDesktop.requireApp("Template", "Explorer", null, false);
MWF.xApplication.ExeManager.options = { 
	multitask: false,
	executable: true
};
MWF.xApplication.ExeManager.Main = new Class({
	Extends: MWF.xApplication.Common.Main, 
	Implements: [Options, Events],

	options: {
		"style": "default",
		"name": "ExeManager",
		"icon": "icon1.png",
		"width": "1270",
		"height": "700",
		"isResize": false,
		"isMax": true,
		"title": MWF.xApplication.ExeManager.LP.main.topBartitle
	},
	onQueryLoad: function(){
		this.lp = MWF.xApplication.ExeManager.LP;
	},
	loadApplication: function(callback){
		//this.restActions = new MWF.xApplication.ExeManager.Actions.RestActions();
		this.restActions = MWF.Actions.get("x_okr_assemble_control");
		this.orgActions = MWF.Actions.get("x_organization_assemble_express");

		this.createContainer();
		this.createTopBar();
		//this.createMiddleContent();
	},
	createContainer : function(){
		if( !this.container ){
			this.content.setStyle("overflow", "hidden");
			this.container = new Element("div", {
				"styles": this.css.container
			}).inject(this.content);
		}
	},
	createTopBar: function(){
		this.currentTopBarTab = "todo";
		if( this.topBar ){
			this.topBar.empty();
		}else{
			this.topBar = new Element("div.topBar", {
				"styles": this.css.topBar
			}).inject(this.container);
		}

		this.topBarContent = new Element("div", {"styles": this.css.topBarContent}).inject(this.topBar);
		this.topBarTitleLi = new Element("li", {"styles": this.css.topBarTitleLi}).inject(this.topBarContent);
		this.topBarLog = new Element("img",{"styles": this.css.topBarLog,"src": this.path+"default/icon/okr.png"}).inject(this.topBarTitleLi);
		this.topBarTitleSpan = new Element("span",{	"styles": this.css.topBarTitleSpan,"text":this.lp.main.topBartitle}).inject(this.topBarTitleLi);

		var topList = this.lp.main.topBarList;
		for(var l in topList){
			var topBarLi = new Element("li.topBarLi",{"styles": this.css.topBarLi,"id":l}).inject(this.topBarContent);
			var _self = this;
			topBarLi.addEvents({
				"mouseover":function(){ //alert(_self.currentTopBarTab)
					if(_self.currentTopBarTab!=this.get("id")){
						this.setStyles({"background-color":"#124c93"})
					}
				},
				"mouseout":function(){
					if(_self.currentTopBarTab!=this.get("id")){
						this.setStyles({"background-color":"#5c97e1"})
					}
				},
				"click" : function(){
					_self.openContent( this );
				}
			});
			//this.topBarTodoImg = new Element("img",{"styles": this.css.topBarTodoImg,"src": this.path+"default/icon/Outline-104.png"}).inject(this.topBarTodoLi);
			var topBarSpan = new Element("span",{"styles": this.css.topBarSpan,"text":topList[l]}).inject(topBarLi);
		}

		this.topBarContent.getElementById("topTodo").click()

	},
	openContent: function(obj){
		this.currentTopBarTab = obj.get("id");
		this.topBarContent.getElements("li").each(function(d){
			if(d.className=="topBarLi"){
				d.setStyles({"background-color":"#5c97e1"})
			}
		});
		obj.setStyles({"background-color":"#124c93"});

		if( !this.middleContent ){
			this.middleContent = new Element("div.middleContent",{
				"styles": this.css.middleContent
			}).inject(this.container)
		}

		if(this.currentTopBarTab=="topTodo"){
			if(this.middleContent){
				this.middleContent.empty()
			}
			MWF.xDesktop.requireApp("ExeManager", "TodoList", function(){
				var explorer = new MWF.xApplication.ExeManager.TodoList(this.middleContent, this, this.restActions, {});
				explorer.load();
			}.bind(this), true);
		}else if(this.currentTopBarTab=="topCenterWork"){
			if(this.middleContent)this.middleContent.empty();
			MWF.xDesktop.requireApp("ExeManager", "CenterWorkList", function(){
				var explorer = new MWF.xApplication.ExeManager.CenterWorkList(this.middleContent, this, this.restActions, {});
				explorer.load();
			}.bind(this) ,true);
		}else if(this.currentTopBarTab=="topBaseWork"){
			if(this.middleContent)this.middleContent.empty();
			MWF.xDesktop.requireApp("ExeManager", "BaseWorkList", function(){
				var explorer = new MWF.xApplication.ExeManager.BaseWorkList(this.middleContent, this, this.restActions, {});
				explorer.load();
			}.bind(this) ,true);
		}else if(this.currentTopBarTab=="topWorkReport"){
			if(this.middleContent)this.middleContent.empty();
			MWF.xDesktop.requireApp("ExeManager", "WorkReportList", function(){
				var explorer = new MWF.xApplication.ExeManager.WorkReportList(this.middleContent, this, this.restActions, {});
				explorer.load();
			}.bind(this) ,true);
		}else if(this.currentTopBarTab=="topIndentity"){
			if(this.middleContent)this.middleContent.empty();
			MWF.xDesktop.requireApp("ExeManager", "IndentityList", function(){
				var explorer = new MWF.xApplication.ExeManager.IndentityList(this.middleContent, this, this.restActions, {});
				explorer.load();
			}.bind(this) ,true);
		}
	},
	createMiddleContent: function(){
		if( !this.middleContent ){
			this.middleContent = new Element("div.middleContent",{
				"styles": this.css.middleContent
			}).inject(this.container)
		}
	},
	createShade: function(obj,txt){
		var defaultObj = this.content;
		var obj = obj || defaultObj;
		var txt = txt || "loading...";
		if(this.shadeDiv){ this.shadeDiv.destroy()}
		if(this.shadeTxtDiv)  this.shadeTxtDiv.destroy();
		this.shadeDiv = new Element("div.shadeDiv").inject(obj);
		this.inforDiv = new Element("div.inforDiv",{
			styles:{"height":"16px","display":"inline-block","position":"absolute","background-color":"#000000","border-radius":"3px","padding":"5px 10px"}
		}).inject(this.shadeDiv);
		this.loadImg = new Element("img.loadImg",{
			styles:{"width":"16px","height":"16px","float":"left"},
			src:"/x_component_Execution/$Main/default/icon/loading.gif"
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
			})
		}
		this.inforDiv.setStyles({
			"left":(x/2)+"px",
			"top":(y/2)+"px"
		})
	},
	destroyShade : function(){
		if(this.shadeDiv) this.shadeDiv.destroy();
		//if(this.shadeDiv) this.shadeDiv.destroy()
	},
	setScrollBar: function(node, view, style, offset, callback){
		if (!style) style = "attachment";
		if (!offset){
			offset = {
				"V": {"x": 0, "y": 0},
				"H": {"x": 0, "y": 0}
			};
		}
		MWF.require("MWF.widget.ScrollBar", function(){
			if(this.scrollbar) delete this.scrollbar;
			this.scrollbar = new MWF.widget.ScrollBar(node, {
				"style": style,
				"offset": offset,
				"indent": false,
				"distance": 50,
				"onScroll": function (y) {
					var scrollSize = node.getScrollSize();
					var clientSize = node.getSize();
					var scrollHeight = scrollSize.y - clientSize.y;
					//var view = this.baseView || this.centerView;
					if (y + 200 > scrollHeight && view && view.loadElementList) {
						if (! view.isItemsLoaded) view.loadElementList()
					}
				}.bind(this)
			});
			if (callback) callback();
		});
		return false;
	},
	showErrorMessage:function(xhr,text,error){
		var errorText = error;
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

	}
});
