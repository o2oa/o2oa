MWF.xApplication.TeamWork = MWF.xApplication.TeamWork || {};
MWF.xApplication.TeamWork.Task = MWF.xApplication.TeamWork.Task || {};
MWF.xApplication.TeamWork.Task.options.multitask = true;
MWF.xDesktop.requireApp("TeamWork", "Task", null, false);
MWF.xApplication.TeamWork.Task.Main = new Class({
	Extends: MWF.xApplication.Common.Main,
	Implements: [Options, Events],

	options: {
		"style": "default",
		"name": "Task",
		"icon": "icon.png",
		"width": "1000",
		"height": "700",
		"isResize": false,
		"isMax": false,
		"title": MWF.xApplication.TeamWork.Task.LP.title
	},
	onQueryLoad: function(){
		this.lp = MWF.xApplication.TeamWork.Task.LP;
		if (this.status){
			this.taskId = this.status.taskId;
		}
	},
	loadApplication: function(callback) {
		//
		this.node = new Element("div", {"styles": this.css.content}).inject(this.content);
		//alert(this.taskId)


		var task = new MWF.xApplication.TeamWork.Task(this,data,opt);
		task.open();
	}

});
