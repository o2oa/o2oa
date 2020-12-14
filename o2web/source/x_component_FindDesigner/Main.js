MWF.xApplication.FindDesigner.options.multitask = false;
MWF.xApplication.FindDesigner.Main = new Class({
	Extends: MWF.xApplication.Common.Main,
	Implements: [Options, Events],

	options: {
		"style": "default",
		"name": "FindDesigner",
		"mvcStyle": "style.css",
		"icon": "icon.png",
		"width": "1200",
		"height": "800",
		"isResize": true,
		"isMax": false,
		"title": MWF.xApplication.FindDesigner.LP.title
	},
	onQueryLoad: function(){
		this.lp = MWF.xApplication.FindDesigner.LP;
	},
	loadApplication: function(callback){
		//1var url = "../x_component_Empty/$Main/default/view.html";
		var url = this.path+this.options.style+"/view.html";

		this.content.loadHtml(url, {"bind": {"lp": this.lp}, "module": this}, function(){
			this.doSomething();

			if (callback) callback();
		}.bind(this));
	},
	doSomething: function(){

	},
	loadTask: function(){
		alert("loadTask");
	},
	tabover: function(){
		//alert("tabover");
		this.myNode.addClass("mainColor_bg");
	},
	tabout: function(){
		//alert("tabout")
		this.myNode.removeClass("mainColor_bg");
	},
	clickNode: function(e, data){
		alert(data.title);
	}
});
