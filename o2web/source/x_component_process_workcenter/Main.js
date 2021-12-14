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
	createCountData: function(){
		var _self = this;
		if (!this.countData){
			this.countData = {"data": {}};
			var createDefineObject = function(p){
				return {
					"get": function(){return this.data[p]},
					"set": function(v){
						this.data[p] = v;
						_self[p+"CountNode"].set("text", v);
						if (_self[p+"List"]) _self[p+"List"].loadPage();
					}
				}
			};
			var o = {
				"task": createDefineObject("task"),
				"taskCompleted": createDefineObject("taskCompleted"),
				"read": createDefineObject("read"),
				"readCompleted": createDefineObject("readCompleted"),
				"draft": createDefineObject("draft"),
			};
			MWF.defineProperties(this.countData, o);
		}
	},
	loadCount: function(){
		this.createCountData();

		var action = o2.Actions.load("x_processplatform_assemble_surface");
		action.WorkAction.countWithPerson(layout.session.user.id).then(function(json){
			this.countData.task = json.data.task;
			this.countData.taskCompleted = json.data.taskCompleted;
			this.countData.read = json.data.read;
			this.countData.readCompleted = json.data.readCompleted;

			// this.pageData = Object.assign(this.pageData, json.data);
			// this.taskCountNode.set("text", json.data.task);
			// this.taskCompletedCountNode.set("text", json.data.taskCompleted);
			// this.readCountNode.set("text", json.data.read);
			// this.readCompletedCountNode.set("text", json.data.readCompleted);
		}.bind(this));
		action.DraftAction.	listMyPaging(1,1, {}).then(function(json){
			this.countData.draft = json.size;
			// this.pageData = Object.assign(this.pageData, {"draft": json.size});
			// this.draftCountNode.set("text", json.size);
		}.bind(this));
	},
	loadList: function(type){
		this.loadCount();
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
		this.currentList = this.taskList;
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
	},
	getApplicationIcon: function(application){
		var icon = (this.appIcons) ? this.appIcons[application] : null;
		if (!icon) {
			var action = o2.Actions.load("x_processplatform_assemble_surface");
			return action.ApplicationAction.getIcon(application).then(function(json){
				if (json.data){
					debugger;
					if (!this.appIcons) this.appIcons = {};
					this.appIcons[application] = json.data;
					return json.data;
				}
				return {
					"icon": "iVBORw0KGgoAAAANSUhEUgAAAEAAAABACAYAAACqaXHeAAAEgElEQVR4Xu1aPXMTSRB9vUaGQqs6iCgi4Bfgq7I2lqWrSwgQMQHyPzAJIeguvOT4BxbBxYjgkquTrFiiCvkXYCKKCFMSFEjs9tWsrEKWvTtfuyvXrTbd6ZnuN69fz842IecP5Tx+rAFYMyDnCKxTYBUE4MrWta9UuLu49hWeHlJveJy1P6kzQAT7eWPzPgN1MFeI6FpckMx8DKIeAe2iP3mVNiipADALuvAIQAOgLbtd5SGAVtGfvkgDjMQB+Fz1ngXgPdlO64IimOGAnhe7/d90bePGJwbAuOY9AqMJwu0kHTwzF+MIhKbb6b9IYh1rAATdxxub+yRyPMOHgbbrT3Zt08IKgHGlvIUN7NvnuSlyPISPXbc3EDph9BgDMPplu4KAXiad67pRhFXD4Qelf1/3dG3FeCMARPDEzoHJgmnZMAU7JiBoAyBozw4OVr3zy0AKJlCAHd100ALgpL4frC7nZfzhYdGf7ugIoxYAo5r3Mmu1l4V8hglAu9TpP1C1UwZgXC03QLSvOvFKxzHvut1BS8UHDQC8t6kfclQ8VhnDOHK7/TsqQ5UAGFW9JhGeqUy4PIZu3AR/eG9iChtbcPDY7b5+LltYCkB40nMKb01U/9Kv93D5yVN8++N3fP/nb5kvp97b2IqJRFVwg+kdmSBKARhXt/dAzp9a3gOYBzC30wHBxvaUnwoskANQK7/RLXvLAeiAYGN7dpN46HYGP8dtXiwAJ5cZH3V2X+Tt1b/akSZxTIgKfj7Zl4d1bT0p+pPrcWkQC4Bp6ZMFch4IJjZKGyMpibEAjGpem4D7SgstDdIJSGesri8MvCp1+pGf6vEAVMsfTdR/7qRKYGKsqBRRj454njeHqAal7uB61PzxKVDzWBfx5fEyEOLmtw1+Prfb6UfGGfnCRACjgjEBIanghU9GACT9za8DQpLBh4eimLuCSAYkDYBwRAWEpINfA3BRGKCy+zonRh1xNkqB3IugQHic5zIoABjVyscE+kmHbotjZbQXgpf6QQj8qdQZRP6QXR+F43Y39x9DJkL4v/ocDoWw6g1BONXNIdMEm0sNG9szfjEO3W4/tj9BfiOU9yux2e/vwpFJNbC52LSxDY+/4E+uP71tfSkalsM8X4vP82pc9URnxi1Z/l+I94x3brev1Kki1YAfAOT819jsZGh+R5gVM2R3gMt+KDMgFBbR/uZs9nTLYlbBg3FYDCYVmfAt+qMFQHguEA0SG+iZVIU0gRCqTz4qqTZIzANI47bIFpzMWmQWQQBTe9VMEDsP4rpJf5CIRTsFFncqbJNzqLUyTWAcIuCGLu2tNGCZqieNki3TP0im1Bdq7/qTho7gnbeWFQNOsUG00IBEq2y6hyXGO4Cbqi0wMoATA+DHgWl7j4maSWtDqPIsApd3fciCTjQFzltsdl641ACchrU+iDxH0CoG31u2dE81BaJQn4FRqDNRXRylZMwIVR3UI+Z2MZi20wg6dQaoUDDsNV54TMuYylpxYxLXAFuHsrZfA5A14hdtvTUDLtqOZO1P7hnwH8CljF98DV13AAAAAElFTkSuQmCC",
					"iconHue": "#4e82bd"
				};
			}.bind(this), function(){
				return {
					"icon": "iVBORw0KGgoAAAANSUhEUgAAAEAAAABACAYAAACqaXHeAAAEgElEQVR4Xu1aPXMTSRB9vUaGQqs6iCgi4Bfgq7I2lqWrSwgQMQHyPzAJIeguvOT4BxbBxYjgkquTrFiiCvkXYCKKCFMSFEjs9tWsrEKWvTtfuyvXrTbd6ZnuN69fz842IecP5Tx+rAFYMyDnCKxTYBUE4MrWta9UuLu49hWeHlJveJy1P6kzQAT7eWPzPgN1MFeI6FpckMx8DKIeAe2iP3mVNiipADALuvAIQAOgLbtd5SGAVtGfvkgDjMQB+Fz1ngXgPdlO64IimOGAnhe7/d90bePGJwbAuOY9AqMJwu0kHTwzF+MIhKbb6b9IYh1rAATdxxub+yRyPMOHgbbrT3Zt08IKgHGlvIUN7NvnuSlyPISPXbc3EDph9BgDMPplu4KAXiad67pRhFXD4Qelf1/3dG3FeCMARPDEzoHJgmnZMAU7JiBoAyBozw4OVr3zy0AKJlCAHd100ALgpL4frC7nZfzhYdGf7ugIoxYAo5r3Mmu1l4V8hglAu9TpP1C1UwZgXC03QLSvOvFKxzHvut1BS8UHDQC8t6kfclQ8VhnDOHK7/TsqQ5UAGFW9JhGeqUy4PIZu3AR/eG9iChtbcPDY7b5+LltYCkB40nMKb01U/9Kv93D5yVN8++N3fP/nb5kvp97b2IqJRFVwg+kdmSBKARhXt/dAzp9a3gOYBzC30wHBxvaUnwoskANQK7/RLXvLAeiAYGN7dpN46HYGP8dtXiwAJ5cZH3V2X+Tt1b/akSZxTIgKfj7Zl4d1bT0p+pPrcWkQC4Bp6ZMFch4IJjZKGyMpibEAjGpem4D7SgstDdIJSGesri8MvCp1+pGf6vEAVMsfTdR/7qRKYGKsqBRRj454njeHqAal7uB61PzxKVDzWBfx5fEyEOLmtw1+Prfb6UfGGfnCRACjgjEBIanghU9GACT9za8DQpLBh4eimLuCSAYkDYBwRAWEpINfA3BRGKCy+zonRh1xNkqB3IugQHic5zIoABjVyscE+kmHbotjZbQXgpf6QQj8qdQZRP6QXR+F43Y39x9DJkL4v/ocDoWw6g1BONXNIdMEm0sNG9szfjEO3W4/tj9BfiOU9yux2e/vwpFJNbC52LSxDY+/4E+uP71tfSkalsM8X4vP82pc9URnxi1Z/l+I94x3brev1Kki1YAfAOT819jsZGh+R5gVM2R3gMt+KDMgFBbR/uZs9nTLYlbBg3FYDCYVmfAt+qMFQHguEA0SG+iZVIU0gRCqTz4qqTZIzANI47bIFpzMWmQWQQBTe9VMEDsP4rpJf5CIRTsFFncqbJNzqLUyTWAcIuCGLu2tNGCZqieNki3TP0im1Bdq7/qTho7gnbeWFQNOsUG00IBEq2y6hyXGO4Cbqi0wMoATA+DHgWl7j4maSWtDqPIsApd3fciCTjQFzltsdl641ACchrU+iDxH0CoG31u2dE81BaJQn4FRqDNRXRylZMwIVR3UI+Z2MZi20wg6dQaoUDDsNV54TMuYylpxYxLXAFuHsrZfA5A14hdtvTUDLtqOZO1P7hnwH8CljF98DV13AAAAAElFTkSuQmCC",
					"iconHue": "#4e82bd"
				};
			});
		}else{
			return icon;
		}
	},
});

MWF.xApplication.process.workcenter.List = new Class({
	Implements: [Options, Events],
	options: {
		"itemHeight": 60
	},
	initialize: function (app, options) {
		this.setOptions(options);
		this.app = app;
		this.content = app.listContentNode;
		this.pageNode = app.listBottomNode;
		this.lp = this.app.lp;
		this.action = o2.Actions.load("x_processplatform_assemble_surface");
		this.init();
		//this.load();
	},
	init: function(){
		this.listHeight = this.content.getSize().y;
		this.size = (this.listHeight/this.options.itemHeight).toInt()
		this.page = 1;
		this.totalCount = this.app.countData.task;
	},
	setLayout: function(){

	},
	load: function(){
		var _self = this;
		this.loadData().then(function(data){
			_self.loadItems(data);
		});
	},
	hide: function(){
		if (this.node) this.node.destroy();
	},
	loadPage: function(){
		var totalCount = this.app.countData.task;
		var pages = totalCount/this.size;
		var pageCount = pages.toInt();
		if (pages !== pageCount) pageCount = pageCount+1;



	},
	loadData: function(){
		var _self = this;
		return this.action.TaskAction.listMyPaging(this.page, this.size).then(function(json){
			_self.fireEvent("loadData");
			//if (_self.total!==json.size) _self.countNode.set("text", json.size);
			_self.total = json.size;
			return json.data;
		}.bind(this));
	},
	loadItems: function(data){
		var url = this.app.path+this.app.options.style+"/view/list.html";
		this.content.loadHtml(url, {"bind": {"lp": this.lp, "data": data}, "module": this}, function(){
			this.node = this.content.getFirst();
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
		//MWF.xDesktop.notice("error", {x: "right", y:"top"}, "aaa<input />ddd");
		o2.api.form.openWork(id, "", title);
	},
	loadItemIcon: function(application, e){
		var node = e.currentTarget;
		Promise.resolve(this.app.getApplicationIcon(application)).then(function(icon){
			if (icon.icon){
				node.setStyle("background-image", "url(data:image/png;base64,"+icon.icon+")");
			}else{
				node.setStyle("background-image", "url("+"../x_component_process_ApplicationExplorer/$Main/default/icon/application.png)");
			}
		});
	},
	loadItemFlag: function(e, data){
		var node = e.currentTarget;
		var iconNode = node.getElement(".listItemFlag");
		var expireNode = node.getElement(".listItemExpire");

		if (data.completed){
			iconNode.setStyle("background-image", "url("+"../x_component_process_workcenter/$Main/default/icons/pic_ok.png)");
			return true;
		}

		var start = new Date().parse(data.startTime);
		var now = new Date();
		if (now.getTime()-start.getTime()<86400000){
			iconNode.setStyle("background-image", "url("+"../x_component_process_workcenter/$Main/default/icons/pic_new.png)");
		}

		if (data.expireTime){
			var d1 = Date.parse(data.expireTime);
			var d2 = Date.parse(data.createTime);

			var time1 = d2.diff(now, "second");
			var time2 = now.diff(d1, "second");
			var time3 = d2.diff(d1, "second");
			var n = time1/time3;

			var img = "";
			var text = this.lp.expire1;
			text = text.replace(/{time}/g, data.expireTime);
			if (n<0.5){
				img = "1.png";
			}else if (n<0.75){
				img = "2.png";
			}else if (n<1){
				text = this.lp.expire2.replace(/{time}/g, data.expireTime);
				img = "3.png";
				iconNode.setStyle("background-image", "url("+"../x_component_process_workcenter/$Main/default/icons/pic_jichao.png)");
			}else if (n<2){
				text = this.lp.expire3.replace(/{time}/g, data.expireTime);
				img = "4.png";
				iconNode.setStyle("background-image", "url("+"../x_component_process_workcenter/$Main/default/icons/pic_yichao.png)");
			}else{
				text = this.lp.expire3.replace(/{time}/g, data.expireTime);
				img = "5.png";
				iconNode.setStyle("background-image", "url("+"../x_component_process_workcenter/$Main/default/icons/pic_yanchao.png)");
			}
			expireNode.setStyle("background-image", "url(../"+this.app.path+this.app.options.style+"/icons/"+img+")");
			expireNode.set("title", text);
		}
	}
});
MWF.xApplication.process.workcenter.TaskList = new Class({
	Extends: MWF.xApplication.process.workcenter.List
});
