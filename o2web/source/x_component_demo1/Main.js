MWF.xApplication.demo1.options.multitask = false;
MWF.xApplication.demo1.Main = new Class({
	Extends: MWF.xApplication.Common.Main,
	Implements: [Options, Events],

	options: {
		"style1": "default",
		"style": "default",
		"name": "demo1",
		"mvcStyle": "style.css",
		"icon": "icon.png",
		"title": MWF.xApplication.demo1.LP.title
	},
	onQueryLoad: function(){
		this.lp = MWF.xApplication.demo1.LP;
	},
	loadApplication: function(callback){
		var url = this.path+this.options.style+"/view.html";
		this.content.loadHtml(url, {"bind": {"lp": this.lp}, "module": this}, function(){
			this.loadTaskView();
		}.bind(this));
	},
	loadTaskView: function(){
		o2.Actions.load("x_processplatform_assemble_surface").TaskAction.listMyPaging(1,5, function(json){
			this.taskListView.loadHtml(this.path+this.options.style+"/taskView.html", {"bind": {"lp": this.lp, "data": json.data}, "module": this}, function(){
				this.doSomething();
			}.bind(this));
		}.bind(this));
	},
	doSomething: function(){

	},
	openTask: function(id, e, data){
		o2.api.page.openWork(id);
	},
	openCalendar: function(){
		o2.api.page.openApplication("Calendar");
	},
	openOrganization: function(){
		o2.api.page.openApplication("Org");
	},
	openInBrowser: function() {
		this.openInNewBrowser(true);
	},
	startProcess: function(){
		o2.api.page.startProcess();
	},
	createDocument: function(){
		o2.api.page.createDocument();
	}
});
