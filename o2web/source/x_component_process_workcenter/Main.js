MWF.xApplication.process.workcenter.options.multitask = false;
MWF.xApplication.process.workcenter.Main = new Class({
	Extends: MWF.xApplication.Common.Main,
	Implements: [Options, Events],

	options: {
		"style1": "default",
		"style": "default",
		"name": "process.workcenter",
		"mvcStyle": "style.css",
		"icon": "icon.png",
		"title": MWF.xApplication.process.workcenter.LP.title
	},
	onQueryLoad: function(){
		this.lp = MWF.xApplication.process.workcenter.LP;
	},
	loadApplication: function(callback){
		var url = this.path+this.options.style+"/view/view.html";
		this.content.loadHtml(url, {"bind": {"lp": this.lp}, "module": this}, function(){
			this.setLayout();
			this.loadCount();
			this.loadList("task");
			if (callback) callback();
		}.bind(this));
	},
	setLayout: function(){
		var items = this.content.getElements(".menuItem");
		items.addEvents({
			"mouseover": function(){this.addClass("menuItem_over")},
			"mouseout": function(){this.removeClass("menuItem_over")},
			"click": function(){}
		});
	},
	loadCount: function(){
		var action = o2.Actions.load("x_processplatform_assemble_surface");
		action.WorkAction.countWithPerson(layout.session.user.id).then(function(json){
			this.taskCountNode.set("text", json.data.task);
			this.taskCompletedCountNode.set("text", json.data.taskCompleted);
			this.readCountNode.set("text", json.data.read);
			this.readCompletedCountNode.set("text", json.data.readCompleted);
		}.bind(this));
		action.DraftAction.	listMyPaging(1,1, {}).then(function(json){
			this.draftCountNode.set("text", json.size);
		}.bind(this));
	},
	loadList: function(type){
		if (this.currentMenu) this.setMenuItemStyleDefault(this.currentMenu);
		this.setMenuItemStyleCurrent(this[type+"MenuNode"]);
		this.currentMenu = this[type+"MenuNode"];

		if (this.currentList) this.currentList.hide();
		this.showSkeleton();
		this[("load-"+type).camelCase()]();
	},
	showSkeleton: function(){
		if (this.skeletonNode) this.skeletonNode.inject(this.listContentNode);
	},
	hideSkeleton: function(){
		if (this.skeletonNode) this.skeletonNode.dispose();
	},
	loadTask: function(){
		if (!this.taskList) this.taskList = new MWF.xApplication.process.workcenter.TaskList(this, {
			"onLoadData": this.hideSkeleton.bind(this)
		});
		this.taskList.load();
	},
	loadRead: function(){

	},
	loadTaskCompleted: function(){

	},
	loadReadCompleted: function(){

	},
	loadDraft: function(){

	},
	setMenuItemStyleDefault: function(node){
		node.removeClass("mainColor_bg_opacity");
		node.getFirst().removeClass("mainColor_color");
		node.getLast().removeClass("mainColor_color");
	},
	setMenuItemStyleCurrent: function(node){
		node.addClass("mainColor_bg_opacity");
		node.getFirst().addClass("mainColor_color");
		node.getLast().addClass("mainColor_color");
	}
});

MWF.xApplication.process.workcenter.List = new Class({
	Implements: [Options, Events],
	options: {
		"itemHeight": 70
	},
	initialize: function (app, options) {
		this.setOptions(options);
		this.app = app;
		this.content = app.listContentNode;
		this.countNode = app.taskCountNode;
		this.lp = this.app.lp;
		this.action = o2.Actions.load("x_processplatform_assemble_surface");
		this.init();
		//this.load();
	},
	init: function(){
		this.listHeight = this.content.getSize().y;
		this.size = (this.listHeight/this.options.itemHeight).toInt()
		this.page = 1;
	},
	setLayout: function(){

	},
	load: function(){
		var _self = this;
		this.loadData().then(function(data){
			_self.loadItems(data);
		});
	},
	loadData: function(){
		var _self = this;
		return this.action.TaskAction.listMyPaging(this.page, this.size).then(function(json){
			_self.fireEvent("loadData");
			if (_self.total!==json.size) _self.countNode.set("text", json.size);
			_self.total = json.size;
			return json.data;
		}.bind(this));
	},
	loadItems: function(data){
		var url = this.app.path+this.app.options.style+"/view/list.html";
		this.content.loadHtml(url, {"bind": {"lp": this.lp, "data": data}, "module": this}, function(){

		}.bind(this));
	},

	overTaskItem: function(e){
		e.currentTarget.addClass("listItem_over");
	},
	outTaskItem: function(e){
		e.currentTarget.removeClass("listItem_over");
	},
	openTask: function(id, title){
		//o2.api.page.notice("<input />")
		MWF.xDesktop.notice("error", {x: "right", y:"top"}, "aaa<input />ddd");
		//o2.api.form.openWork(id, "", title);
	}
});
MWF.xApplication.process.workcenter.TaskList = new Class({
	Extends: MWF.xApplication.process.workcenter.List
});
