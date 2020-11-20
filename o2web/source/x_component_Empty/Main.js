MWF.xApplication.Empty.options.multitask = true;
MWF.xApplication.Empty.Main = new Class({
	Extends: MWF.xApplication.Common.Main,
	Implements: [Options, Events],

	options: {
		"style1": "default",
		"style": "default",
		"name": "Empty",
		"mvcStyle": "style.css",
		"icon": "icon.png",
		"width": "400",
		"height": "700",
		"isResize": false,
		"isMax": false,
		"title": MWF.xApplication.Empty.LP.title
	},
	onQueryLoad: function(){
		this.lp = MWF.xApplication.Empty.LP;
	},
	loadApplication: function(callback){
		//1var url = "../x_component_Empty/$Main/default/view.html";
		var url = this.path+this.options.style+"/view.html";
		var url2 = this.path+this.options.style+"/view2.html";
		o2.Actions.load("x_processplatform_assemble_surface").TaskAction.listMyPaging(1,20, function(json){

			this.content.loadHtml(url, {"bind": {"lp": this.lp, "data": json}, "module": this}, function(){
				this.doSomething();
			}.bind(this));

			// this.content.loadHtml(url, {"bind": {"lp": this.lp, "data": json}, "module": this}, function(){
			// 	this.doSomething();
			// }.bind(this));
			//
			// o2.load(["js1", "js2"], {}, function(){});	//js
			//
			// o2.loadCss	//css
			// o2.loadHtml("", {"dom": this.content})
			// o2.loadAll	//js,css,html
			//
			// o2.loadAll({
			// 	"css": [],
			// 	"js":[],
			// 	"html": []
			// },
			// 	)
			//



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
