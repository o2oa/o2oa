MWF.xApplication.process.Application.options.multitask = true;
o2.xDesktop.requireApp("Selector", "package", null, false);
MWF.xDesktop.requireApp("Template", "MPopupForm", null, false);
MWF.xDesktop.requireApp("Template", "MForm", null, false);
MWF.xApplication.process.Application.Main = new Class({
	Extends: MWF.xApplication.Common.Main,
	Implements: [Options, Events],
	options: {
		"style1": "default",
		"style": "default",
		"name": "process.Application",
		"mvcStyle": "style.css",
		"icon": "icon.png",
		"application": "",
		"id": "",
		"navi" : "all",
		"title": MWF.xApplication.process.Application.LP.title
	},
	onQueryLoad: function(){
		this.lp = MWF.xApplication.process.Application.LP;
		this.action = o2.Actions.load("x_processplatform_assemble_surface");
		if (this.status) {
			if(this.status.id)this.options.id = this.status.id;
			if(this.status.navi){
				this.options.navi = this.status.navi;
			}
		}
		this._loadCss();
	},
	loadApplication: function(callback){
		this.initAcl(function (){
			this.loadProcessList(function (){

				this.action.ApplicationAction.get(this.options.id).then(function (json){
					if (json.data){
						this.setTitle(this.lp.title+"-"+json.data.name);
						this.application = json.data;
						var url = this.path+this.options.style+"/view/view.html";
						this.content.loadHtml(url, {"bind": {"acl":this.acl,"lp": this.lp,"data":{"application" : this.application}}, "module": this}, function(){
							this.setLayout();
							this.loadList(this.options.navi);
							if (callback) callback();
						}.bind(this));
					}
				}.bind(this));
			}.bind(this));

		}.bind(this));
	},
	loadApplicationIcon : function (e){
		var node = e.currentTarget;
		if (this.application.icon){
			node.setStyle("background-image", "url(data:image/png;base64,"+this.application.icon+")");
		}else{
			node.setStyle("background-image", "url("+"../x_component_process_Application/$Main/default/icon/application.png)");
		}
	},
	initAcl : function (callback){
		this.acl = {
		}
		this.action.ApplicationAction.isManager(this.options.id).then(function (json){
			this.acl.isAppManager = json.data.value;
			this.action.ProcessAction.listControllableWithApplication(this.options.id).then(function (json){
				var processList = json.data.valueList;
				if(processList.length>0){
					this.acl.isProcessManager = true;
				}else {
					this.acl.isProcessManager = false;
				}
				this.acl.processList = json.data.valueList;

				if(callback) callback();
			}.bind(this));
		}.bind(this));
	},
	loadProcessList : function (callback){
		this.action.ProcessAction.listWithPersonWithApplication(this.options.id).then(function (json){
			this.processList = json.data;
			if(callback) callback();
		}.bind(this));
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
		this.action.WorkAction.countWithPersonAndApplication(layout.session.user.id,this.application.id).then(function(json){
			this.countData.task = json.data.task;
			this.countData.taskCompleted = json.data.taskCompleted;
			this.countData.read = json.data.read;
			this.countData.readCompleted = json.data.readCompleted;
		}.bind(this));
		this.action.DraftAction.listMyPaging(1,1, {"applicationList":[this.application.id]}).then(function(json){
			this.countData.draft = json.count;
		}.bind(this));
	},
	loadList: function(type,ev,data){
		if (this.currentMenu) this.setMenuItemStyleDefault(this.currentMenu);
		this.setMenuItemStyleCurrent(this[type+"MenuNode"]);
		this.currentMenu = this[type+"MenuNode"];
		this._loadListContent(type);
		this.loadCount();
	},
	_loadListContent: function(type){
		this.mainNode.empty();
		list = new MWF.xApplication.process.Application[type.capitalize() +"List"](this.mainNode,this, {
			"onLoadData": function (){
				this.hideSkeleton();
			},
			"type" : type,
			"key" : this.options.key
		});
		this.currentList = list;
	},
	setLayout: function(){
		var items = this.content.getElements(".menuItem");
		items.addEvents({
			"mouseover": function(){this.addClass("menuItem_over")},
			"mouseout": function(){this.removeClass("menuItem_over")},
			"click": function(){}
		});
	},
	startProcess: function(){

		this.action.ProcessAction.listWithPersonWithApplication(this.application.id).then(function (json){
			var node = new Element("div");
			var url = this.path+this.options.style+"/view/dlg/processList.html";
			node.loadHtml(url, {"bind": {"lp": this.lp,"processList":json.data}, "module": this}, function(){
				this.startProcessDlg = o2.DL.open({
					"title": this.lp.startProcess,
					"width": "400px",
					"height": "260px",
					"mask": true,
					"content": node,
					"container": null,
					"positionNode": this.content,
					"onQueryClose": function () {
						node.destroy();
					}.bind(this),
					"buttonList": [

					],
					"onPostShow": function () {
						this.startProcessDlg.reCenter();
					}.bind(this)
				});
			}.bind(this));
		}.bind(this));
	},
	startProcessItemOver: function(e){
		var node = e.target;
		while (node && !node.hasClass("st_processItem")){ node = node.getParent();}
		if (node){
			node.addClass("menuItem_over");
			node.removeClass("mainColor_bg");
		}
	},
	startProcessItemOut: function(e){
		var node = e.target;
		while (node && !node.hasClass("st_processItem")){ node = node.getParent();}
		if (node){
			node.removeClass("menuItem_over");
			node.removeClass("mainColor_bg");
		}
	},
	startProcessItemDown: function(e){
		var node = e.target;
		while (node && !node.hasClass("st_processItem")){ node = node.getParent();}
		if (node){
			node.removeClass("menuItem_over");
			node.addClass("mainColor_bg");
		}
	},
	startProcessItemUp: function(e){
		var node = e.target;
		while (node && !node.hasClass("st_processItem")){ node = node.getParent();}
		if (node){
			node.addClass("menuItem_over");
			node.removeClass("mainColor_bg");
		}
	},
	startProcessItemClick: function(e, data){
		this.startProcessDlg.close();
		MWF.xDesktop.requireApp("process.TaskCenter", "ProcessStarter", function(){
			var starter = new MWF.xApplication.process.TaskCenter.ProcessStarter(data, this, {
				"onStarted": function(workdata, title, processName){
					this.afterStartProcess(workdata, title, processName, data);
				}.bind(this)
			});
			starter.load();
		}.bind(this));
	},
	afterStartProcess: function(data, title, processName, processdata){

		if (data.work){
			this.startProcessDraft(data, title, processName);
		}else{
			this.startProcessInstance(data, title, processName);
		}
	},
	startProcessDraft: function(data, title, processName){
		var work = data.work;
		var options = {"draft": work, "appId": "process.Work"+(new o2.widget.UUID).toString(), "desktopReload": false,
			"onPostClose": function(){
				if (this.currentList.refresh) this.currentList.refresh();
			}.bind(this)
		};
		this.desktop.openApplication(null, "process.Work", options);
	},
	startProcessInstance: function(data, title, processName){
		var workInfors = [];
		var currentTask = [];
		data.each(function(work){
			if (work.currentTaskIndex !== -1) currentTask.push(work.taskList[work.currentTaskIndex].work);
			workInfors.push(this.getStartWorkInforObj(work));
		}.bind(this));

		if (currentTask.length===1){
			var options = {"workId": currentTask[0], "appId": "process.Work"+currentTask[0],
				"onPostClose": function(){
					if (this.currentList.refresh) this.currentList.refresh();
				}.bind(this)
			};
			this.desktop.openApplication(null, "process.Work", options);

			if (layout.desktop.message) this.createStartWorkResault(workInfors, title, processName, false);
		}else{
			if (layout.desktop.message) this.createStartWorkResault(workInfors, title, processName, true);
		}
	},
	getStartWorkInforObj: function(work){
		var users = [];
		var currentTask = "";
		work.taskList.each(function(task, idx){
			users.push(task.person+"("+task.department + ")");
			if (work.currentTaskIndex===idx) currentTask = task.id;
		}.bind(this));
		return {"activity": work.fromActivityName, "users": users, "currentTask": currentTask};
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
	recordStatus: function(){
		return { "id": this.options.id};
	}
});
MWF.xApplication.process.Application.List = new Class({
	Implements: [Options, Events],
	options: {
		"type": "all",
		"itemHeight": 40,
	},
	initialize: function (node,app, options) {
		this.setOptions(options);
		this.app = app;
		this.container = node;
		this.lp = this.app.lp;
		this.css = this.app.css;
		this.action = app.action;
		this.type = this.options.type;

		this.application = app.application;


		var url = this.app.path+this.app.options.style+"/view/content.html";
		this.container.loadHtml(url, {"bind": {"lp": this.lp,"data":{"type":this.type}}, "module": this}, function(){
			this.content = this.listContentNode;

			this.bottomNode = this.listBottomNode;
			this.pageNode = this.pageNumberAreaNode;

			this.init();
			this.load();

		}.bind(this));

	},

	loadFilter: function () {
		var lp = this.lp;

		this.fileterNode = new Element("div.fileterNode", {
			"styles": this.css.fileterNode
		}).inject(this.searchNode);

		var html = "<table bordr='0' cellpadding='0' cellspacing='0' styles='filterTable'>" + //style='width: 900px;'
			"<tr>" +
			"    <td styles='filterTableTitle' lable='title'></td>" +
			"    <td styles='filterTableValue' item='title'></td>" +
			"    <td styles='filterTableTitle' lable='activityName'></td>" +
			"    <td styles='filterTableValue' item='activityName'></td>" +
			"    <td styles='filterTableTitle' lable='creatorUnitList'></td>" +
			"    <td styles='filterTableValue' item='creatorUnitList'></td>" +
			"    <td styles='filterTableTitle' lable='credentialList'></td>" +
			"    <td styles='filterTableValue' item='credentialList'></td>" +
			"</tr>" +
			"<tr style='height: 45px;'>" +

			"    <td styles='filterTableTitle' lable='processName'></td>" +
			"    <td styles='filterTableValue' item='processName'></td>" +
			"    <td styles='filterTableTitle' lable='startTime'></td>" +
			"    <td styles='filterTableValue' item='startTime'></td>" +
			"    <td styles='filterTableTitle' lable='endTime'></td>" +
			"    <td styles='filterTableValue' item='endTime'></td>" +
			"    <td styles='filterTableValue' colspan='2'><div style='float:left' item='action'></div><div item='reset'></div></td>" +
			"</tr>" +
			"</table>";
		this.fileterNode.set("html", html);

		var selectValue = [""];
		var selectText = [""];

		this.app.processList.each(function(d){
			selectValue.push(d.id);
			selectText.push(d.name);
		})
		this.form = new MForm(this.fileterNode, {}, {
			style: "attendance",
			isEdited: true,
			itemTemplate: {
				title: {text: lp.subject, "type": "text", "style": {"min-width": "150px"}},
				activityName: {text: lp.activity, "type": "text", "style": {"min-width": "150px"}},
				processName: {
					"text": lp.process,
					"type": "select",
					"selectValue" :selectValue,
					"selectText" :selectText,
					"style": {"min-width": "150px"},

				},
				credentialList: {
					"text": lp.creator,
					"type": "org",
					"orgType": "identity",
					"orgOptions": {"resultType": "person"},
					"style": {"min-width": "150px"},
					"orgWidgetOptions": {"disableInfor": true}
				},
				creatorUnitList: {
					"text": lp.createunit,
					"type": "org",
					"orgType": "unit",
					"orgOptions": {"resultType": "person"},
					"style": {"min-width": "150px"},
					"orgWidgetOptions": {"disableInfor": true}
				},
				startTime: {
					text: lp.begin,
					"tType": "date",
					"style": {"min-width":"150px"}
				},
				endTime: {
					text: lp.end,
					"tType": "date",
					"style": {"min-width":"150px"}
				},
				action: {
					"value": this.lp.query, type: "button", className: "filterButton", event: {
						click: function () {
							var result = this.form.getResult(false, null, false, false, false);
							for (var key in result) {
								if (!result[key]) {
									delete result[key];
								} else if (key === "activityName" && result[key].length > 0) {
									//result[key] = result[key][0].split("@")[1];
									result["activityNameList"] = [result[key]];
									delete result[key];
								}else if (key === "processName" && result[key] !== "") {
									//result[key] = result[key][0].split("@")[1];
									result["processList"] = [result[key]];
									delete result[key];
								}else if (key === "endTime" && result[key] !== "") {
									result[key] = result[key] + " 23:59:59"

								}
							}
							result.applicationList = this.filterList.applicationList;

							if(result.credentialList) {
								result.creatorPersonList = result.credentialList;
							}

							this.filterList = result;
							this.refresh();
						}.bind(this)
					}
				},
				reset: {
					"value": this.lp.reset, type: "button", className: "filterButtonGrey", event: {
						click: function () {
							this.form.reset();
							this._initFilter();
							this.refresh();
						}.bind(this)
					}
				},
			}
		}, this.app, this.css);
		this.form.load();
	},


	showSkeleton: function(){


		if (this.skeletonNode) this.skeletonNode.inject(this.listContentNode);
	},
	hideSkeleton: function(){

		if (this.skeletonNode) this.skeletonNode.dispose();
	},
	loadListTitle : function (){
		this.listTitleNode.empty();
		this.listTitleNode.loadHtml(this.titleTempleteUrl, {"bind": {"lp": this.lp}, "module": this}, function(){
			this.currentSortNode = this.sortUpdateTimeNode;

			this.currentSortKey = "name";
		}.bind(this));
	},
	selectAll : function (e){

		if (e.currentTarget.get("disabled").toString()!="true"){
			var itemNode = e.currentTarget.getParent(".listItem");
			var iconNode = e.currentTarget.getElement(".selectFlagIcon");

			if (itemNode){
				if (itemNode.hasClass("mainColor_bg_opacity")){
					itemNode.removeClass("mainColor_bg_opacity");
					iconNode.removeClass("icon-xuanzhong");
					iconNode.removeClass("selectFlagIcon_select");
					iconNode.removeClass("mainColor_color");

					this.listContentNode.getElements("tr").each(function (tr){
						tr.removeClass("mainColor_bg_opacity");
						var ss = tr.getElement(".selectFlagIcon");
						tr.getElement(".selectFlag").hide();
						ss.removeClass("icon-xuanzhong");
						ss.removeClass("selectFlagIcon_select");
						ss.removeClass("mainColor_color");

					})
					this.selectedList = [];

				}else{
					itemNode.addClass("mainColor_bg_opacity");
					iconNode.addClass("icon-xuanzhong");
					iconNode.addClass("selectFlagIcon_select");
					iconNode.addClass("mainColor_color");
					this.listContentNode.getElements("tr").each(function (tr){
						tr.getElement(".selectFlag").show();
						tr.addClass("mainColor_bg_opacity");
						var ss = tr.getElement(".selectFlagIcon");

						ss.addClass("icon-xuanzhong");
						ss.addClass("selectFlagIcon_select");
						ss.addClass("mainColor_color");

					})

					this.selectedList.append(this.dataList);
				}
			}
		}
		this._setToolBar();
	},
	loadItems: function(data){

		this.dataList = data;

		this.content.loadHtml(this.listTempleteUrl, {"bind": {"lp": this.lp, "type": this.options.type, "data": data}, "module": this}, function(){
			this.node = this.content.getFirst();
		}.bind(this));
	},
	init: function(){

		this.listHeight = this.content.getSize().y - this.options.itemHeight - 60;
		this.size = (this.listHeight/this.options.itemHeight).toInt();

		this.size = 15;

		this.page = 1;
		this.loadFilter();
		this._initFilter();
		this.filterNameList = {};
	},
	_initFilter : function(){
		this.filterList = {
			applicationList : [this.application.id]
		};
	},
	_initTempate: function () {
		this.titleTempleteUrl = this.app.path + this.app.options.style + "/view/" + this.type + "/list_title.html";
		this.listTempleteUrl = this.app.path + this.app.options.style + "/view/" + this.type + "/list.html";

	},
	load: function(){


		var _self = this;

		this._initToolBar();
		this._initTempate();
		this.loadListTitle();

		if(this.toolbarItems.unSelect.length>0){
			this.loadToolBar(this.toolbarItems.unSelect);
		}else {
			this.loadToolBar(this.toolbarItems.default,true);
		}

		this.selectedList = [];
		this.loadData().then(function(data){
			_self.hide();
			_self.loadPage();
			_self.loadItems(data);
		});
	},
	_initToolBar : function (){

		this.toolbarItems = {
			"default":[

			],
			"unSelect":[

			],
			"selected":[

			],
			"mulSelect":[

			]
		}

	},
	loadToolBar : function (availableTool,disabled){

		this.toolBarNode.empty();
		this.toolbar = new MWF.xApplication.process.Application.Toolbar(this.toolBarNode, this, {
			viewType : this.options.defaultViewType,
			type : this.type,
			disabled : !!disabled,
			availableTool : availableTool
		});
		this.toolbar.load();

		if(availableTool.length===0){
			this.toolBarNode.hide();
		}else {
			this.toolBarNode.show();
		}
	},
	refresh: function(){
		this.hide();
		this.load();
		this.app.loadCount();
	},
	hide: function(){
		if (this.node) this.node.destroy();
	},
	loadPage: function(){
		var totalCount = this.total;
		var pages = totalCount/this.size;

		var pageCount = pages.toInt();
		if (pages !== pageCount) pageCount = pageCount+1;
		this.pageCount = pageCount;
		var size = this.bottomNode.getSize();
		var maxPageSize = 500;//size.x*0.8;
		maxPageSize = maxPageSize - 80*2-24*2-10*3;
		var maxPageCount = (maxPageSize/34).toInt();

		this.loadPageNode(pageCount, maxPageCount);
	},
	loadPageNode: function(pageCount, maxPageCount){
		var pageStart = 1;
		var pageEnd = pageCount;
		if (pageCount>maxPageCount){
			var halfCount = (maxPageCount/2).toInt();
			pageStart = Math.max(this.page-halfCount, 1);
			pageEnd = pageStart+maxPageCount-1;
			pageEnd = Math.min(pageEnd, pageCount);
			pageStart = pageEnd - maxPageCount+1;
		}
		this.pageNode.empty();
		var _self = this;
		for (var i=pageStart; i<=pageEnd; i++){
			var node = new Element("div.pageItem", {
				"text": i,
				"events": { "click": function(){_self.gotoPage(this.get("text"));} }
			}).inject(this.pageNode);
			if (i==this.page) node.addClass("mainColor_bg");
		}
	},
	nextPage: function(){
		this.page++;
		if (this.page>this.pageCount) this.page = this.pageCount;
		this.gotoPage(this.page);
	},
	prevPage: function(){
		this.page--;
		if (this.page<1) this.page = 1;
		this.gotoPage(this.page);
	},
	firstPage: function(){
		this.gotoPage(1);
	},
	lastPage: function(){
		this.gotoPage(this.pageCount);
	},
	gotoPage: function(page){
		this.page = page;
		this.hide();
		this.showSkeleton();
		this.load();
	},
	loadData: function(){

	},
	_fixData : function (dataList){
		dataList.each(function (data){
			data.creatorPersonName = data.creatorPerson.split("@")[0];
			data.creatorUnitName = data.creatorUnit.split("@")[0];
			data.title = data.title || this.lp.unnamed;
		}.bind(this));
		return dataList;
	},
	overTaskItem: function(e){
		e.currentTarget.addClass("listItem_over");

		var iconNode = e.currentTarget.getElement(".selectFlagIcon");
		if (iconNode.hasClass("selectFlagIcon_select")){

		}else{
			e.currentTarget.getElement(".selectFlag").show();
		}
	},
	outTaskItem: function(e){
		e.currentTarget.removeClass("listItem_over");
		var iconNode = e.currentTarget.getElement(".selectFlagIcon");

		if (iconNode.hasClass("selectFlagIcon_select")){

		}else{
			e.currentTarget.getElement(".selectFlag").hide();
		}
	},
	loadItemIcon: function(application, e){
		return
		this.app.loadItemIcon(application, e);
	},
	selectFile: function(id,e, dataList){

		var data ;
		for(var i = 0 ; i < this.dataList.length;i++){
			if(this.dataList[i].id === id){
				data = this.dataList[i];
				break ;
			}
		}
		if (e.currentTarget.get("disabled").toString()!="true"){
			var itemNode = e.currentTarget.getParent(".listItem");
			var iconNode = e.currentTarget.getElement(".selectFlagIcon");

			if (itemNode){
				if (itemNode.hasClass("mainColor_bg_opacity")){
					itemNode.removeClass("mainColor_bg_opacity");
					iconNode.removeClass("icon-xuanzhong");
					iconNode.removeClass("selectFlagIcon_select");
					iconNode.removeClass("mainColor_color");
					this.unselectedFile(data);
				}else{
					itemNode.addClass("mainColor_bg_opacity");
					iconNode.addClass("icon-xuanzhong");
					iconNode.addClass("selectFlagIcon_select");
					iconNode.addClass("mainColor_color");
					this.selectedFile(data);
				}
			}
		}
		this._setToolBar();
	},
	_setToolBar : function (){
		if(this.selectedList.length === 0 ){

			if(this.toolbarItems.unSelect.length>0){
				this.loadToolBar(this.toolbarItems.unSelect);
			}else {
				this.loadToolBar(this.toolbarItems.default,true);
			}

		} else if (this.selectedList.length === 1){
			this.loadToolBar(this.toolbarItems.selected);
		}else{
			this.loadToolBar(this.toolbarItems.mulSelect);
		}
	},
	selectedFile: function(data){

		if (!this.selectedList) this.selectedList = [];
		var idx = this.selectedList.findIndex(function(t){
			return t.id == data.id;
		});
		if (idx===-1) this.selectedList.push(data);
	},
	unselectedFile: function(data){
		// delete data._;
		if (!this.selectedList) this.selectedList = [];
		var idx = this.selectedList.findIndex(function(t){
			return t.id == data.id;
		});
		if (idx!==-1) this.selectedList.splice(idx, 1);
	},
	open : function (work,workCompleted,jobId){
		var options = {
			"workId": work,
			"workCompletedId": workCompleted,
			"appId":  "process.Work" + work
		};
		if(o2.typeOf(jobId)=="string"){
			options.jobId = jobId;
		}

		layout.desktop.openApplication(null, "process.Work", options);

	}
});
MWF.xApplication.process.Application.AllList = new Class({
	Extends: MWF.xApplication.process.Application.List,
	loadData: function(){
		var _self = this;
		this.filterList.relateTask = true;
		return this.action.ReviewAction.V2ListPaging(this.page, this.size, this.filterList||{}).then(function(json){
			_self.fireEvent("loadData");
			_self.total = json.count;
			return _self._fixData(json.data);
		}.bind(this));

	},
	loadFilter: function () {
		var lp = this.lp;

		this.fileterNode = new Element("div.fileterNode", {
			"styles": this.css.fileterNode
		}).inject(this.searchNode);

		var html = "<table bordr='0' cellpadding='0' cellspacing='0' styles='filterTable'>" + //style='width: 900px;'
			"<tr>" +
			"    <td styles='filterTableTitle' lable='title'></td>" +
			"    <td styles='filterTableValue' item='title'></td>" +
			"    <td styles='filterTableTitle' lable='creatorUnitList'></td>" +
			"    <td styles='filterTableValue' item='creatorUnitList'></td>" +
			"    <td styles='filterTableTitle' lable='credentialList'></td>" +
			"    <td styles='filterTableValue' item='credentialList'></td>" +
			"    <td styles='filterTableValue'></td>" +
			"</tr>" +
			"<tr style='height: 45px;'>" +

			"    <td styles='filterTableTitle' lable='processName'></td>" +
			"    <td styles='filterTableValue' item='processName'></td>" +
			"    <td styles='filterTableTitle' lable='startTime'></td>" +
			"    <td styles='filterTableValue' item='startTime'></td>" +
			"    <td styles='filterTableTitle' lable='endTime'></td>" +
			"    <td styles='filterTableValue' item='endTime'></td>" +
			"    <td styles='filterTableValue' style='width: 180px'><div style='float:left' item='action'></div><div item='reset'></div></td>" +
			"</tr>" +
			"</table>";
		this.fileterNode.set("html", html);

		var selectValue = [""];
		var selectText = [""];

		this.app.processList.each(function(d){
			selectValue.push(d.id);
			selectText.push(d.name);
		})
		this.form = new MForm(this.fileterNode, {}, {
			style: "attendance",
			isEdited: true,
			itemTemplate: {
				title: {text: lp.subject, "type": "text", "style": {"min-width": "150px"}},
				processName: {
					"text": lp.process,
					"type": "select",
					"selectValue" :selectValue,
					"selectText" :selectText,
					"style": {"min-width": "150px"},

				},
				credentialList: {
					"text": lp.creator,
					"type": "org",
					"orgType": "identity",
					"orgOptions": {"resultType": "person"},
					"style": {"min-width": "150px"},
					"orgWidgetOptions": {"disableInfor": true}
				},
				creatorUnitList: {
					"text": lp.createunit,
					"type": "org",
					"orgType": "unit",
					"orgOptions": {"resultType": "person"},
					"style": {"min-width": "150px"},
					"orgWidgetOptions": {"disableInfor": true}
				},
				startTime: {
					text: lp.begin,
					"tType": "date",
					"style": {"min-width":"150px"}
				},
				endTime: {
					text: lp.end,
					"tType": "date",
					"style": {"min-width":"150px"}
				},
				action: {
					"value": this.lp.query, type: "button", className: "filterButton", event: {
						click: function () {
							var result = this.form.getResult(false, null, false, false, false);
							for (var key in result) {
								if (!result[key]) {
									delete result[key];
								} else if (key === "activityName" && result[key].length > 0) {
									//result[key] = result[key][0].split("@")[1];
									result["activityNameList"] = [result[key]];
									delete result[key];
								}else if (key === "processName" && result[key] !== "") {
									//result[key] = result[key][0].split("@")[1];
									result["processList"] = [result[key]];
									delete result[key];
								}else if (key === "endTime" && result[key] !== "") {
									result[key] = result[key] + " 23:59:59"

								}
							}
							result.applicationList = this.filterList.applicationList;

							if(result.credentialList) {
								result.creatorPersonList = result.credentialList;
							}

							this.filterList = result;
							this.refresh();
						}.bind(this)
					}
				},
				reset: {
					"value": this.lp.reset, type: "button", className: "filterButtonGrey", event: {
						click: function () {
							this.form.reset();
							this._initFilter();
							this.refresh();
						}.bind(this)
					}
				},
			}
		}, this.app, this.css);
		this.form.load();
	},
	_fixData : function (dataList){
		dataList.each(function (data){
			data.creatorPersonName = data.creatorPerson.split("@")[0];
			data.creatorUnitName = data.creatorUnit.split("@")[0];
			if(data.completed) {
				data.activityName = "结束";
			}else {
				if(data.taskList.length >0){
					data.activityName = data.taskList[0].activityName;
				}
			}
			data.title = data.title || this.lp.unnamed;
		}.bind(this));
		return dataList;
	},
});
MWF.xApplication.process.Application.TaskList = new Class({
	Extends: MWF.xApplication.process.Application.List,
	loadData: function(){
		var _self = this;

		return this.action.TaskAction.V2ListPaging(this.page, this.size, this.filterList||{}).then(function(json){
			_self.fireEvent("loadData");
			_self.total = json.count;
			return _self._fixData(json.data);
		}.bind(this));

	},
});
MWF.xApplication.process.Application.TaskDoneList = new Class({
	Extends: MWF.xApplication.process.Application.List,
	loadData: function(){
		var _self = this;
		this.filterList = this.filterList||{};
		this.filterList.latest = true;

		return this.action.TaskCompletedAction.V2ListPaging(this.page, this.size, this.filterList).then(function(json){
			_self.fireEvent("loadData");
			_self.total = json.count;
			return _self._fixData(json.data);
		}.bind(this));

	},
});
MWF.xApplication.process.Application.ReadList = new Class({
	Extends: MWF.xApplication.process.Application.List,
	loadData: function(){
		var _self = this;
		return this.action.ReadAction.V2ListPaging(this.page, this.size, this.filterList||{}).then(function(json){
			_self.fireEvent("loadData");
			_self.total = json.count;
			return _self._fixData(json.data);
		}.bind(this));

	},
});
MWF.xApplication.process.Application.ReadDoneList = new Class({
	Extends: MWF.xApplication.process.Application.List,
	loadData: function(){
		var _self = this;
		return this.action.ReadCompletedAction.V2ListPaging(this.page, this.size, this.filterList||{}).then(function(json){
			_self.fireEvent("loadData");
			_self.total = json.count;
			return _self._fixData(json.data);
		}.bind(this));

	},
});
MWF.xApplication.process.Application.DraftList = new Class({
	Extends: MWF.xApplication.process.Application.List,
	loadData: function(){
		var _self = this;
		return this.action.DraftAction.listMyPaging(this.page, this.size, this.filterList||{}).then(function(json){
			_self.fireEvent("loadData");
			_self.total = json.count;
			return _self._fixData(json.data);
		}.bind(this));

	},
	_fixData : function (dataList){
		dataList.each(function(d){
			if(d.title === "") d.title = this.lp.unnamed;
		}.bind(this));
		return dataList;
	},
	loadFilter: function () {
		var lp = this.lp;
		this.fileterNode = new Element("div.fileterNode", {
			"styles": this.css.fileterNode
		}).inject(this.searchNode);

		var html = "<table bordr='0' cellpadding='0' cellspacing='0' styles='filterTable'>" + //style='width: 900px;'
			"<tr style='height: 45px;'>" +
			"    <td styles='filterTableTitle' lable='title'></td>" +
			"    <td styles='filterTableValue' item='title'></td>" +
			"    <td styles='filterTableTitle' lable='processName'></td>" +
			"    <td styles='filterTableValue' item='processName'></td>" +
			"    <td styles='filterTableTitle' lable='startTime'></td>" +
			"    <td styles='filterTableValue' item='startTime'></td>" +
			"    <td styles='filterTableTitle' lable='endTime'></td>" +
			"    <td styles='filterTableValue' item='endTime'></td>" +
			"    <td styles='filterTableValue' style='width: 200px'><div style='float:left' item='action'></div><div item='reset'></div></td>" +
			"</tr>" +
			"</table>";
		this.fileterNode.set("html", html);

		var selectValue = [""];
		var selectText = [""];

		this.app.processList.each(function(d){
			selectValue.push(d.id);
			selectText.push(d.name);
		})
		this.form = new MForm(this.fileterNode, {}, {
			style: "attendance",
			isEdited: true,
			itemTemplate: {
				title: {text: lp.subject, "type": "text", "style": {"min-width": "150px"}},
				processName: {
					"text": lp.process,
					"type": "select",
					"selectValue" :selectValue,
					"selectText" :selectText,
					"style": {"min-width": "150px"},

				},
				startTime: {
					text: lp.begin,
					"tType": "date",
					"style": {"min-width":"150px"}
				},
				endTime: {
					text: lp.end,
					"tType": "date",
					"style": {"min-width":"150px"}
				},
				action: {
					"value": lp.query, type: "button", className: "filterButton", event: {
						click: function () {
							var result = this.form.getResult(false, null, false, false, false);
							for (var key in result) {
								if (!result[key]) {
									delete result[key];
								} else if (key === "activityName" && result[key].length > 0) {
									//result[key] = result[key][0].split("@")[1];
									result["activityNameList"] = [result[key]];
									delete result[key];
								}else if (key === "processName" && result[key] !== "") {
									//result[key] = result[key][0].split("@")[1];
									result["processList"] = [result[key]];
									delete result[key];
								}else if (key === "endTime" && result[key] !== "") {
									result[key] = result[key] + " 23:59:59"

								}
							}
							this.filterList = result;
							this.refresh();
						}.bind(this)
					}
				},
				reset: {
					"value": lp.reset, type: "button", className: "filterButtonGrey", event: {
						click: function () {
							this.form.reset();
							this._initFilter();
							this.refresh();
						}.bind(this)
					}
				},
			}
		}, this.app, this.css);
		this.form.load();
	},
	open : function (id){

		var options = {
			"draftId": id,
			"appId":  "process.Work" + id
		};
		layout.desktop.openApplication(null, "process.Work", options);

	}

});
MWF.xApplication.process.Application.WorkList = new Class({
	Extends: MWF.xApplication.process.Application.List,
	loadData: function(){
		var _self = this;
		this.filterList.relateEditionProcess = true;
		return this.action.WorkAction.manageListWithApplicationPaging(this.page, this.size, this.application.id,this.filterList||{}).then(function(json){
			_self.fireEvent("loadData");
			_self.total = json.count;
			return _self._fixData(json.data);
		}.bind(this));

	},
	_initToolBar : function (){

		this.toolbarItems = {
			"default":[
				["delWork","jump","sendRead","rollback"],
				["processing","endWork","addReview"],
				["manage"]
			],
			"unSelect":[
			],
			"selected":[
				["delWork","jump","sendRead","rollback"],
				["processing","endWork","addReview"],
				["manage"]
			],
			"mulSelect":[
				["delWork"],
				["processing","endWork","addReview"]
			]
		}
	},
	open : function (id){
		var options = {
			"workId": id,
			"appId":  "process.Work" + id
		};
		layout.desktop.openApplication(null, "process.Work", options);

	},
	manage : function (id,ev,dataList){
		var data ;
		for(var i = 0 ; i < dataList.length;i++){
			if(dataList[i].id === id){
				data = dataList[i];
				break ;
			}
		}

		this._manage(data);
	},
	_manage : function (data){
		var form;
		form = new MWF.xApplication.process.Application.ManageWorkForm({app: this.app}, data );
		form.open();
	},
	delete : function(id,e){
		var _self = this;

		this.app.confirm("warn", e,"删除确认！！", {
			"html": "<br/>请选择删除方式？ <br/><input type='radio' value='soft' name='delete_type'/>软删除（可恢复）" +
				"<br/><input type='radio' value='delete' name='delete_type'/>硬删除（不能恢复）<div class='checkInfor'></div>"

		}, 400, 200, function(){
			var inputs = this.content.getElements("input");

			var flag = "";
			for (var i=0; i<inputs.length; i++){
				if (inputs[i].checked){
					flag = inputs[i].get("value");
					break;
				}
			}
			if (flag){
				_self.action[flag === "soft"?"SnapAction":"WorkAction"][flag === "soft"?"typeAbandoned":"delete"]( id , function(){
					_self.app.notice("成功删除工作。");
					_self.refresh();
				});
				this.close();
			}else{
				this.content.getElement(".checkInfor").set("text", "请选择删除方式！").setStyle("color", "red");
			}
		}, function(){
			this.close();
		});
	}
});
MWF.xApplication.process.Application.WorkCompletedList = new Class({
	Extends: MWF.xApplication.process.Application.List,
	loadData: function(){
		var _self = this;
		this.filterList.relateEditionProcess = true;
		return this.action.WorkCompletedAction.manageListWithApplicationPaging(this.page, this.size,this.application.id, this.filterList||{}).then(function(json){
			_self.fireEvent("loadData");
			_self.total = json.count;
			return _self._fixData(json.data);
		}.bind(this));

	},
	_initToolBar : function (){

		this.toolbarItems = {
			"default":[
				["delCompletedWork"],
				["rollback","sendRead","addReview"],
				["manage"]
			],
			"unSelect":[
			],
			"selected":[
				["delCompletedWork"],
				["rollback","sendRead","addReview"],
				["manage"]
			],
			"mulSelect":[
				["delCompletedWork"],
				["sendRead","addReview"]
			]
		}
	},
	open : function (id){
		debugger
		var options = {
			"workCompletedId": id,
			"appId":  "process.Work" + id
		};
		layout.desktop.openApplication(null, "process.Work", options);

	},
	manage : function (id,ev,dataList){
		var data ;
		for(var i = 0 ; i < dataList.length;i++){
			if(dataList[i].id === id){
				data = dataList[i];
				break ;
			}
		}

		this._manage(data);
	},
	_manage : function (data){
		var form;
		form = new MWF.xApplication.process.Application.ManageWorkCompletedForm({app: this.app}, data );
		form.open();
	},
	delete : function(id,e){
		var _self = this;

		this.app.confirm("warn", e,"删除确认！！", {
			"html": "<br/>请选择删除方式？ <br/><input type='radio' value='soft' name='delete_type'/>软删除（可恢复）" +
				"<br/><input type='radio' value='delete' name='delete_type'/>硬删除（不能恢复）<div class='checkInfor'></div>"

		}, 400, 200, function(){
			var inputs = this.content.getElements("input");

			var flag = "";
			for (var i=0; i<inputs.length; i++){
				if (inputs[i].checked){
					flag = inputs[i].get("value");
					break;
				}
			}
			if (flag){
				_self.action[flag === "soft"?"SnapAction":"WorkCompletedAction"][flag === "soft"?"typeAbandonedWorkCompleted":"manageDelete"]( id , function(){
					_self.app.notice("成功删除工作。");
					_self.refresh();
				});
				this.close();
			}else{
				this.content.getElement(".checkInfor").set("text", "请选择删除方式！").setStyle("color", "red");
			}
		}, function(){
			this.close();
		});
	}
});
MWF.xApplication.process.Application.SnapList = new Class({
	Extends: MWF.xApplication.process.Application.List,
	loadData: function(){
		var _self = this;
		return this.action.SnapAction.manageListWithApplicationPaging(this.page, this.size, this.application.id,this.filterList||{}).then(function(json){
			_self.fireEvent("loadData");
			_self.total = json.count;
			return _self._fixData(json.data);
		}.bind(this));

	},
	_initToolBar : function (){

		this.toolbarItems = {
			"default":[
				["delSnap"],
				["restore"]
			],
			"unSelect":[

			],
			"selected":[
				["delSnap"],
				["restore"]
			],
			"mulSelect":[
				["delSnap"],
				["restore"]
			]
		}

	},
	open : function (id){
		debugger
		var options = {
			"workCompletedId": id,
			"appId":  "process.Work" + id
		};
		layout.desktop.openApplication(null, "process.Work", options);

	},
	delete : function(id,e){
		var _self = this;
		this.app.confirm("warn", e, "删除确认", "删除后不能恢复。", 350, 120, function () {

			_self.action.SnapAction.delete( id , function(){
				_self.app.notice("成功删除");
				_self.refresh();
			});
			this.close();
		}, function () {
			this.close();
		});
	}
});
MWF.xApplication.process.Application.DictList = new Class({
	Extends: MWF.xApplication.process.Application.List,
	loadData: function(){
		var _self = this;
		return o2.Actions.load("x_processplatform_assemble_designer").ApplicationDictAction.listWithApplication(this.application.id).then(function(json){
			_self.fireEvent("loadData");
			_self.total = json.count;
			return json.data;
		}.bind(this));

	},
	open : function (id){
		debugger
		var options = {
			"id": id,
			"application" : this.app.application.id,
			"appId":  "process.DictionaryDesigner" + id
		};
		layout.desktop.openApplication(null, "process.DictionaryDesigner", options);
	},
	loadFilter: function () {
		this.searchNode.setStyle("height","10px");
	},

});
MWF.xApplication.process.Application.SerialList = new Class({
	Extends: MWF.xApplication.process.Application.List,
	loadData: function(){
		var _self = this;
		return this.action.SerialNumberAction.list(this.application.id).then(function(json){
			_self.fireEvent("loadData");
			_self.total = json.count;
			return json.data;
		}.bind(this));

	},
	loadFilter: function () {
		this.searchNode.setStyle("height","10px");
	},
	_initToolBar : function (){

		this.toolbarItems = {
			"default":[
			],
			"unSelect":[
				["addSerial"]
			],
			"selected":[
				["delSerial"],
				["setSerial"],
			],
			"mulSelect":[
				["delSerial"],
			]
		}

	},
});
MWF.xApplication.process.Application.Toolbar = new Class({
	Extends: MWF.widget.Common,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"viewType" : "list",
		"type" : "all",
		"disabled" : false
	},
	initialize : function( container, explorer, options ) {

		this.container = container;
		this.explorer = explorer;
		this.app = explorer.app;
		this.lp = explorer.app.lp;
		this.css = this.app.css;

		this.action = explorer.action;

		this.setOptions(options);

		this._initTools();
		this.type = this.options.type;

		this.availableTool = this.options.availableTool;

		if(this.explorer.selectedList){
			this.data = this.explorer.selectedList[0];
		}


	},
	_initTools : function (){
		this.tools = {
			delWork :{
				action : "delWork",
				text : this.lp.actionList.delete,
				icon : "icon-upload"
			},
			delCompletedWork : {
				action : "delCompletedWork",
				text : this.lp.actionList.delete,
				icon : "icon-upload"
			},
			processing :{
				action : "processing",
				text : this.lp.actionList.processing,
				icon : "icon-upload"
			},
			addReview :{
				action : "addReview",
				text : this.lp.actionList.addReview,
				icon : "icon-upload"
			},
			endWork :{
				action : "endWork",
				text : this.lp.actionList.endWork,
				icon : "icon-upload"
			},
			jump : {
				action : "jump",
				text : this.lp.actionList.jump,
				icon : "icon-upload"
			},
			manage : {
				action : "manage",
				text : this.lp.actionList.manage,
				icon : "icon-upload"
			},
			sendRead  : {
				action : "sendRead",
				text : this.lp.actionList.sendRead,
				icon : "icon-upload"
			},
			setSerial : {
				action : "setSerial",
				text : this.lp.actionList.setSerial,
				icon : "icon-upload"
			},
			delSerial : {
				action : "delSerial",
				text : this.lp.actionList.delete,
				icon : "icon-upload"
			},
			addSerial : {
				action : "addSerial",
				text : this.lp.actionList.add,
				icon : "icon-upload"
			},
			rollback : {
				action : "rollback",
				text : this.lp.actionList.rollback,
				icon : "icon-newfolder"
			},
			delSnap : {
				action : "delSnap",
				text : this.lp.actionList.delSnap,
				icon : "icon-rename"
			},

			restore: {
				action : "restore",
				text : this.lp.actionList.restore,
				icon : "icon-shareDownload"
			}
		}
	},
	load : function(){

		this.node = new Element("div").inject( this.container );

		this.availableTool.each( function( group ){
			var toolgroupNode = new Element("div.toolgroupNode").inject( this.node );
			var length = group.length;
			group.each( function( t, i ){
				var className;
				if( length == 1 ){
					className = "toolItemNode_single";
				}else{
					if( i == 0 ){
						className = "toolItemNode_left";
					}else if( i + 1 == length ){
						className = "toolItemNode_right";
					}else{
						className = "toolItemNode_center";
					}
				}

				var tool = this.tools[ t ];
				var toolNode;

				if(this.options.disabled){
					toolNode = new Element( "div", {
						class : className,
						style : "height:30px;line-height:30px;padding-left:12px;padding-right:12px;background: rgb(123 177 240);font-size: 13px;color: #FFFFFF;font-weight: 400;",
					}).inject( toolgroupNode );
				}else {
					toolNode = new Element( "div", {
						class : className,
						style : "cursor:pointer;height:30px;line-height:30px;padding-left:12px;padding-right:12px;background: #4A90E2;font-size: 13px;color: #FFFFFF;font-weight: 400;",
						events : {
							click : function( ev ){ this[tool.action]( ev ) }.bind(this)
						}
					}).inject( toolgroupNode );
				}

				//var iconNode = new Element("icon",{"class":"o2WorkApplication " + tool.icon,"style":"margin-right:6px"}).inject(toolNode);
				var textNode = new Element("span").inject(toolNode);
				textNode.set("text",tool.text);


			}.bind(this))
		}.bind(this));

		this.loadRightNode()
	},
	addSerial: function(){

		var _self = this;
		var data = this.explorer.selectedList[0];
		var form = new MWF.xApplication.process.Application.AddSerSialForm(this.explorer, data, {
		}, {
			app: this.app
		});
		form.edit()

	},
	setSerial : function(){

		var _self = this;
		if (this.explorer.selectedList && this.explorer.selectedList.length){
			var data = this.explorer.selectedList[0];
			var form = new MWF.xApplication.process.Application.SetSerSialForm(this.explorer, data, {
			}, {
				app: this.app
			});
			form.edit()
		}else {
			this.app.notice("请先选择文件","error");
			return;
		}

	},
	delWork : function (e){

		var _self = this;
		var dataList = this.explorer.selectedList;

		this.app.confirm("warn", e,"删除确认！！", {
			"html": "<br/>请选择删除方式？ <br/><input type='radio' value='soft' name='delete_type'/>软删除（可恢复）" +
				"<br/><input type='radio' value='delete' name='delete_type'/>硬删除（不能恢复）<div class='checkInfor'></div>"

		}, 400, 200, function(){
			var inputs = this.content.getElements("input");

			var flag = "";
			for (var i=0; i<inputs.length; i++){
				if (inputs[i].checked){
					flag = inputs[i].get("value");
					break;
				}
			}
			if (flag){


				var count = 0;
				dataList.each( function(data){


					if(flag === "soft"){

						_self.action.SnapAction.typeAbandoned( data.id , function(){
							count++;
							if( dataList.length == count ){
								_self.app.notice("成功删除"+count+"个工作。");
								_self.explorer.refresh();
							}
						});
					}else {

						_self.action.WorkAction.delete( data.id , function(){
							count++;
							if( dataList.length == count ){
								_self.app.notice("成功删除"+count+"个工作。");
								_self.explorer.refresh();
							}
						});
					}

				}.bind(this));

				this.close();


			}else{
				this.content.getElement(".checkInfor").set("text", "请选择删除方式！").setStyle("color", "red");
			}
		}, function(){
			this.close();
		});
	},
	processing : function (e){
		var _self = this;
		var dataList = this.explorer.selectedList;
		this.app.confirm("warn", e, "尝试流转确认", "是否尝试流转选中的"+dataList.length+"个文档？", 350, 120, function () {
			var count = 0;
			dataList.each( function(data){
				_self.action.WorkAction.processing( data.id , {},function(){
					count++;
					if( dataList.length == count ){
						_self.app.notice("成功处理"+count+"个文档。");
						_self.explorer.refresh();
					}
				});
			}.bind(this));
			this.close();
		}, function () {
			this.close();
		});
	},
	delCompletedWork : function (e){

		var _self = this;
		var dataList = this.explorer.selectedList;

		this.app.confirm("warn", e,"删除确认！！", {
			"html": "<br/>请选择删除方式？ <br/><input type='radio' value='soft' name='delete_type'/>软删除（可恢复）" +
				"<br/><input type='radio' value='delete' name='delete_type'/>硬删除（不能恢复）<div class='checkInfor'></div>"

		}, 400, 280, function(){
			var inputs = this.content.getElements("input");

			var flag = "";
			for (var i=0; i<inputs.length; i++){
				if (inputs[i].checked){
					flag = inputs[i].get("value");
					break;
				}
			}
			if (flag){


				var count = 0;
				dataList.each( function(data){


					if(flag === "soft"){

						_self.action.SnapAction.typeAbandonedWorkCompleted( data.id , function(){
							count++;
							if( dataList.length == count ){
								_self.app.notice("成功删除"+count+"个工作。");
								_self.explorer.refresh();
							}
						});
					}else {

						_self.action.WorkCompletedAction.manageDelete( data.id , function(){
							count++;
							if( dataList.length == count ){
								_self.app.notice("成功删除"+count+"个工作。");
								_self.explorer.refresh();
							}
						});

					}


				}.bind(this));

				this.close();


			}else{
				this.content.getElement(".checkInfor").set("text", "请选择删除方式！").setStyle("color", "red");
			}
		}, function(){
			this.close();
		});
	},
	endWork : function (e){
		var _self = this;
		var dataList = this.explorer.selectedList;
		this.app.confirm("warn", e, "结束确认", "是否结束选中的"+dataList.length+"个文档？", 350, 120, function () {
			var count = 0;
			dataList.each( function(data){

				_self.action.ProcessAction.getComplex(data.process,function (json){

					var endList = json.data.endList;
					var endActivityId = endList[0].id;

					var body = {
						"activity": endActivityId,
						"activityType": "end",
						"mergeWork": true,
						"manualForceTaskIdentityList": null
					};
					_self.action.WorkAction.V2Reroute(data.id, body, function (json) {
						count++;
						if( dataList.length == count ){
							_self.app.notice("成功结束"+count+"个文档。");
							_self.explorer.refresh();
						}
					});
				});

			}.bind(this));
			this.close();
		}, function () {
			this.close();
		});
	},
	delSnap : function (e){
		var _self = this;
		var dataList = this.explorer.selectedList;
		this.app.confirm("warn", e, "删除确认", "是否删除选中的"+dataList.length+"个文档？删除后不能恢复。", 350, 120, function () {
			var count = 0;
			dataList.each( function(data){
				_self.action.SnapAction.delete( data.id , function(){
					count++;
					if( dataList.length == count ){
						_self.app.notice("成功删除"+count+"个文档。");
						_self.explorer.refresh();
					}
				});
			}.bind(this));
			this.close();
		}, function () {
			this.close();
		});
	},
	restore: function (e){
		var _self = this;
		var dataList = this.explorer.selectedList;
		this.app.confirm("warn", e, "恢复确认", "是否恢复选中的"+dataList.length+"个文档？", 350, 120, function () {
			var count = 0;
			dataList.each( function(data){
				_self.action.SnapAction.restore( data.id , function(){
					count++;
					if( dataList.length == count ){
						_self.app.notice("成功恢复"+count+"个文档。");
						_self.explorer.refresh();
					}
				});
			}.bind(this));
			this.close();
		}, function () {
			this.close();
		});
	},
	delSerial : function (e){
		var _self = this;
		var dataList = this.explorer.selectedList;
		this.app.confirm("warn", e, "删除确认", "是否删除选中的"+dataList.length+"个流水号？删除后不能恢复。", 350, 120, function () {
			var count = 0;
			dataList.each( function(data){
				_self.action.SerialNumberAction.delete( data.id , function(){
					count++;
					if( dataList.length == count ){
						_self.app.notice("成功删除"+count+"个流水号。");
						_self.explorer.refresh();
					}
				});
			}.bind(this));
			this.close();
		}, function () {
			this.close();
		});
	},
	sendRead : function (e){

		var dataList = this.explorer.selectedList;
		var ids = [];
		dataList.each(function (data){
			ids.push(data.id);
		}.bind(this));

		debugger
		var _self = this;

		var reviewNode = new Element("div",{"class":"control","style":"margin:10px"});
		var personNode =  new Element("textarea",{"class":"textarea","placeholder":"参阅人员选择"});
		personNode.inject(reviewNode);
		personNode.addEvent("click",function(){
			var opt = {
				"type": "identity",
				"count": 0,
				"values": personNode.retrieve("dataList") || [],
				"onComplete": function (dataList) {
					debugger
					var arr = [];
					var arr2 = [];
					dataList.each(function (data) {
						arr.push(data.data);
						arr2.push(data.data.name);
					});
					personNode.set("value", arr2.join(","));
					personNode.store("dataList", arr);
				}.bind(this)
			};
			new MWF.O2Selector(_self.app.content, opt);
		}.bind(this));

		var reviewDlg = o2.DL.open({
			"title": "增加参阅",
			"width": "400px",
			"height": "260px",
			"mask": true,
			"content": reviewNode,
			"container": null,
			"positionNode": _self.app.content,
			"onQueryClose": function () {
				reviewNode.destroy();
			}.bind(this),
			"buttonList": [
				{
					"text": "确认",
					"action": function () {
						var personList = personNode.retrieve("dataList") ;
						var arr = [];
						personList.each(function(person){
							arr.push(person.distinguishedName);
						});

						ids.each(function(workId){
							var data = {
								"notify":true,
								"identityList":arr
							}

							if(_self.type === "work"){
								_self.app.action.ReadAction.createWithWork(workId,data,function (){},null,false);
							}else{
								_self.app.action.ReadAction.createWithWorkCompleted(workId,data,function (){},null,false);
							}



						});

						_self.app.notice("增加成功。","success");
						_self.explorer.refresh();

						reviewDlg.close();
					}.bind(this)
				},
				{
					"text": "关闭",
					"action": function () {
						reviewDlg.close();
					}.bind(this)
				}
			],
			"onPostShow": function () {
				reviewDlg.reCenter();
			}.bind(this)
		});


	},
	addReview : function (e){

		var dataList = this.explorer.selectedList;
		var ids = [];
		dataList.each(function (data){
			ids.push(data.id);
		}.bind(this));

		debugger
		var _self = this;
		var reviewNode = new Element("div",{"class":"control","style":"margin:10px"});
		var personNode =  new Element("textarea",{"class":"textarea","placeholder":"参阅人员选择"});
		personNode.inject(reviewNode);
		personNode.addEvent("click",function(){
			var opt = {
				"type": "person",
				"count": 0,
				"values": personNode.retrieve("dataList") || [],
				"onComplete": function (dataList) {
					var arr = [];
					var arr2 = [];
					dataList.each(function (data) {
						arr.push(data.data);
						arr2.push(data.data.name);
					});
					personNode.set("value", arr2.join(","));
					personNode.store("dataList", arr);
				}.bind(this)
			};
			new MWF.O2Selector(_self.app.content, opt);
		}.bind(this));

		var reviewDlg = o2.DL.open({
			"title": "增加参阅",
			"width": "400px",
			"height": "260px",
			"mask": true,
			"content": reviewNode,
			"container": null,
			"positionNode": _self.app.content,
			"onQueryClose": function () {
				reviewNode.destroy();
			}.bind(this),
			"buttonList": [
				{
					"text": "确认",
					"action": function () {
						var personList = personNode.retrieve("dataList") ;
						var arr = [];
						personList.each(function(person){
							arr.push(person.distinguishedName);
						});

						ids.each(function(workId){
							var data = {

								"personList":arr
							}
							if(_self.type === "work"){
								data.work = workId;
								_self.app.action.ReviewAction.createWithWork(data,function (){},null,false);
							}else{
								data.workCompleted = workId;
								_self.app.action.ReviewAction.createWithWorkCompleted(data,function (){},null,false);
							}


						});

						_self.app.notice("增加成功。","success");
						_self.explorer.refresh();

						reviewDlg.close();
					}.bind(this)
				},
				{
					"text": "关闭",
					"action": function () {
						reviewDlg.close();
					}.bind(this)
				}
			],
			"onPostShow": function () {
				reviewDlg.reCenter();
			}.bind(this)
		});
	},
	manage : function (){
		var form;
		if(this.type === "workCompleted"){
			form = new MWF.xApplication.process.Application.ManageWorkCompletedForm({app: this.app}, this.explorer.selectedList[0] );
		}else{
			form = new MWF.xApplication.process.Application.ManageWorkForm({app: this.app}, this.explorer.selectedList[0] );
		}
		form.open();
	},
	jump: function(){

		var data = this.explorer.selectedList[0];
		var lp = this.lp;
		this.readyReroute = true;

		var width = 560;
		var height = 210;
		var p = MWF.getCenterPosition(this.app.content, width, height);

		var _self = this;
		var dlg = new MWF.xDesktop.Dialog({
			"title": "调度",
			"style": "user",
			"top": p.y-100,
			"left": p.x,
			"fromTop": p.y-100,
			"fromLeft": p.x,
			"width": width,
			"height": height,
			"url": this.app.path+"default/view/dlg/reroute.html",
			"container": this.app.content,
			"isClose": true,
			"buttonList": [
				{
					"type": "ok",
					"text": MWF.LP.process.button.ok,
					"action": function (d, e) {
						this.doRerouteWork(dlg);
					}.bind(this)
				},
				{
					"type": "cancel",
					"text": MWF.LP.process.button.cancel,
					"action": function () { dlg.close(); }
				}
			],
			"onPostShow": function(){


				var select = $("rerouteWork_selectActivity");
				_self.action.ProcessAction.getAllowRerouteTo(data.process, function(json){
					if (json.data.agentList) json.data.agentList.each(function(activity){
						new Element("option", {
							"value": activity.id+"#agent",
							"text": activity.name
						}).inject(select);
					}.bind(_self));

					if (json.data.cancelList) json.data.cancelList.each(function(activity){
						new Element("option", {
							"value": activity.id+"#cancel",
							"text": activity.name
						}).inject(select);
					}.bind(_self));

					if (json.data.choiceList) json.data.choiceList.each(function(activity){
						new Element("option", {
							"value": activity.id+"#choice",
							"text": activity.name
						}).inject(select);
					}.bind(_self));

					if (json.data.conditionList) json.data.conditionList.each(function(activity){
						new Element("option", {
							"value": activity.id+"#condition",
							"text": activity.name
						}).inject(select);
					}.bind(_self));

					if (json.data.delayList) json.data.delayList.each(function(activity){
						new Element("option", {
							"value": activity.id+"#delay",
							"text": activity.name
						}).inject(select);
					}.bind(_self));

					if (json.data.embedList) json.data.embedList.each(function(activity){
						new Element("option", {
							"value": activity.id+"#embed",
							"text": activity.name
						}).inject(select);
					}.bind(_self));

					if (json.data.publishList) json.data.publishList.each(function(activity){
						new Element("option", {
							"value": activity.id+"#publish",
							"text": activity.name
						}).inject(select);
					}.bind(_self));

					if (json.data.endList) json.data.endList.each(function(activity){
						new Element("option", {
							"value": activity.id+"#end",
							"text": activity.name
						}).inject(select);
					}.bind(_self));

					if (json.data.invokeList) json.data.invokeList.each(function(activity){
						new Element("option", {
							"value": activity.id+"#invoke",
							"text": activity.name
						}).inject(select);
					}.bind(_self));

					if (json.data.manualList) json.data.manualList.each(function(activity){
						new Element("option", {
							"value": activity.id+"#manual",
							"text": activity.name
						}).inject(select);
					}.bind(_self));

					if (json.data.mergeList) json.data.mergeList.each(function(activity){
						new Element("option", {
							"value": activity.id+"#merge",
							"text": activity.name
						}).inject(select);
					}.bind(_self));

					if (json.data.messageList) json.data.messageList.each(function(activity){
						new Element("option", {
							"value": activity.id+"#message",
							"text": activity.name
						}).inject(select);
					}.bind(_self));

					if (json.data.parallelList) json.data.parallelList.each(function(activity){
						new Element("option", {
							"value": activity.id+"#parallel",
							"text": activity.name
						}).inject(select);
					}.bind(_self));

					if (json.data.serviceList) json.data.serviceList.each(function(activity){
						new Element("option", {
							"value": activity.id+"#service",
							"text": activity.name
						}).inject(select);
					}.bind(_self));

					if (json.data.splitList) json.data.splitList.each(function(activity){
						new Element("option", {
							"value": activity.id+"#split",
							"text": activity.name
						}).inject(select);
					}.bind(_self));
				}.bind(_self));

				var selPeopleButton = this.content.getElement(".rerouteWork_selPeopleButton");
				selPeopleButton.addEvent("click", function () {
					_self.selectReroutePeople(this);
				}.bind(this));
			}
		});
		dlg.show();
	},
	selectReroutePeople: function(dlg){
		var names = dlg.identityList || [];
		var areaNode = dlg.content.getElement(".rerouteWork_selPeopleArea");
		var options = {
			"values": names,
			"type": "identity",
			"count": 0,
			"title": this.explorer.app.lp.reroute,
			"onComplete": function (items) {
				areaNode.empty();
				var identityList = [];
				items.each(function (item) {
					new MWF.widget.O2Identity(item.data, areaNode, { "style": "reset" });
					identityList.push(item.data.distinguishedName);
				}.bind(this));
				dlg.identityList = identityList;
			}.bind(this)
		};
		MWF.xDesktop.requireApp("Selector", "package", function () {
			var selector = new MWF.O2Selector(this.app.content, options);
		}.bind(this));
	},
	doRerouteWork: function(dlg){
		var _self  = this;
		var opinion = $("rerouteWork_opinion").get("value");
		var select = $("rerouteWork_selectActivity");
		var activity = select.options[select.selectedIndex].get("value");
		var activityName = select.options[select.selectedIndex].get("text");
		var tmp = activity.split("#");
		activity = tmp[0];
		var type = tmp[1];

		var nameArr = [];
		var names = dlg.identityList || [];
		names.each(function (n) { nameArr.push(n); });

		MWF.require("MWF.widget.Mask", function(){
			this.mask = new MWF.widget.Mask({"style": "desktop", "zIndex": 50000});
			this.mask.loadNode(this.explorer.app.content);

			this.rerouteWorkToActivity(activity, type, opinion, nameArr, function(){
				// this.explorer.actions.getWork(this.data.id, function(workJson){
				// 	this.data = workJson.data;
				// 	this.workAreaNode.setStyles(this.css.workItemWorkNode);
				// 	this.readyReroute = false;
				// 	this.reload();
				// }.bind(this));
				_self.explorer.refresh();
				dlg.close();
				if (this.mask) {this.mask.hide(); this.mask = null;}
			}.bind(this), function(xhr, text, error){
				var errorText = error+":"+text;
				if (xhr) errorText = xhr.responseText;
				this.app.notice("request json error: "+errorText, "error", dlg.node);
				if (this.mask) {this.mask.hide(); this.mask = null;}
			}.bind(this));
		}.bind(this));
	},
	rerouteWorkToActivity: function(activity, type, opinion, nameArr, success, failure){
		var body = {
			"activity": activity,
			"activityType": type,
			"mergeWork": false,
			"manualForceTaskIdentityList": nameArr
		};
		o2.Actions.load("x_processplatform_assemble_surface").WorkAction.V2Reroute(this.data.id, body, function(){
			if (success) success();
		}.bind(this), function (xhr, text, error) {
			if (failure) failure(xhr, text, error);
		});

		// this.explorer.actions.rerouteWork(this.data.id, activity, type, null, function(json){
		//     if (success) success();
		// }.bind(this), function(xhr, text, error){
		//     if (failure) failure(xhr, text, error);
		// });
	},


	rollback: function (){
		debugger
		var data = this.explorer.selectedList[0];
		var node = new Element("div", { "styles": this.css.rollbackAreaNode });
		var html = "<div style=\"line-height: 30px; height: 30px; color: #333333; overflow: hidden;float:left;\">请选择文件要回溯到的位置：</div>";
		html += "<div style=\"line-height: 30px; height: 30px; color: #333333; overflow: hidden;float:right;\"><input class='rollback_flowOption' checked type='checkbox' />并尝试继续流转</div>";
		html += "<div style=\"clear:both; margin-bottom:10px; margin-top:10px; overflow-y:auto;\"></div>";
		node.set("html", html);
		var rollbackItemNode = node.getLast();
		this.getRollbackLogs(rollbackItemNode,data.id);
		//node.inject(this.app.content);

		var dlg = o2.DL.open({
			"title": MWF.xApplication.process.Work.LP.rollback,
			"style": "user",
			"isResize": false,
			"content": node,
			"width": 600,
			"height" : 400,
			"buttonList": [
				{
					"type": "ok",
					"text": MWF.LP.process.button.ok,
					"action": function (d, e) {
						this.doRollback(node, e, dlg ,data.id);
					}.bind(this)
				},
				{
					"type": "cancel",
					"text": MWF.LP.process.button.cancel,
					"action": function () { dlg.close(); }
				}
			]
		});
	},
	doRollback: function (node, e, dlg ,workid) {
		var lp = MWF.xApplication.process.Work.LP;
		var rollbackItemNode = node.getLast();
		var items = rollbackItemNode.getChildren();
		var flowOption = (node.getElement(".rollback_flowOption").checked);

		var _self = this;
		for (var i = 0; i < items.length; i++) {
			if (items[i].retrieve("isSelected")) {
				var text = lp.rollbackConfirmContent;
				var log = items[i].retrieve("log");
				var checks = items[i].getElements("input:checked");
				var idList = [];
				checks.each(function (check) {
					var id = check.get("value");
					if (idList.indexOf(id) == -1) idList.push(id);
				});

				var opinion = MWF.xApplication.process.Xform.LP.rollbackTo+":"+log.fromActivityName;
				text = text.replace("{log}", log.fromActivityName + "(" + log.arrivedTime + ")");
				this.explorer.app.confirm("infor", e, lp.rollbackConfirmTitle, text, 450, 120, function () {

					_self.app.action[_self.type === "workCompleted"?"WorkCompletedAction":"WorkAction"][_self.type === "workCompleted"?"rollback":"V2Rollback"](workid,{
						"workLog": log.id,
						"distinguishedNameList": idList,
						"processing": !!flowOption,
						"opinion": opinion
					},function (json){
						_self.app.notice(lp.rollbackSuccess);
						_self.explorer.refresh();
					},null,false);

					dlg.close();

					this.close();
				}, function () {
					this.close();
				});
				break;
			}
		}
	},
	getRollbackLogs: function (rollbackItemNode,id) {
		var _self = this;

		var data ;

		this.app.action.WorkLogAction.listRollbackWithWorkOrWorkCompleted(id).then(function (json){
			var dataList = json.data;

			dataList.each(function (log) {
				if (!log.splitting && log.connected) {
					var node = new Element("div", { "styles": this.css.rollbackItemNode }).inject(rollbackItemNode);
					node.store("log", log);
					var iconNode = new Element("div", { "styles": this.css.rollbackItemIconNode }).inject(node);
					var contentNode = new Element("div", { "styles": this.css.rollbackItemContentNode }).inject(node);

					var div = new Element("div", { "styles": { "overflow": "hidden" } }).inject(contentNode);
					var activityNode = new Element("div", { "styles": this.css.rollbackItemActivityNode, "text": log.fromActivityName }).inject(div);
					var timeNode = new Element("div", { "styles": this.css.rollbackItemTimeNode, "text": log.arrivedTime }).inject(div);
					div = new Element("div", { "styles": { "overflow": "hidden" } }).inject(contentNode);
					var taskTitleNode = new Element("div", { "styles": this.css.rollbackItemTaskTitleNode, "text":  "办理人: " }).inject(div);

					if (log.taskCompletedList.length) {
						log.taskCompletedList.each(function (o) {
							var text = o2.name.cn(o.person) + "(" + o.completedTime + ")";
							var check = new Element("input", {
								"value": o.identity,
								"type": "checkbox",
								"disabled": true,
								"styles": this.css.rollbackItemTaskCheckNode
							}).inject(div);
							check.addEvent("click", function (e) {
								e.stopPropagation();
							});
							var taskNode = new Element("div", { "styles": this.css.rollbackItemTaskNode, "text": text }).inject(div);
						}.bind(this));
					} else {
						var text = "系统自动处理";
						var taskNode = new Element("div", { "styles": this.css.rollbackItemTaskNode, "text": text }).inject(div);
					}

					node.addEvents({
						"mouseover": function () {
							var isSelected = this.retrieve("isSelected");
							if (!isSelected) this.setStyles(_self.css.rollbackItemNode_over);
						},
						"mouseout": function () {
							var isSelected = this.retrieve("isSelected");
							if (!isSelected) this.setStyles(_self.css.rollbackItemNode)
						},
						"click": function () {
							var isSelected = this.retrieve("isSelected");
							if (isSelected) {
								_self.setRollBackUnchecked(this);
							} else {
								var items = rollbackItemNode.getChildren();
								items.each(function (item) {
									_self.setRollBackUnchecked(item);
								});
								_self.setRollBackChecked(this);
							}
						}
					});
				}
			}.bind(this));
		}.bind(this));

	},
	setRollBackChecked: function (item) {
		item.store("isSelected", true);
		item.setStyles(this.css.rollbackItemNode_current);

		item.getFirst().setStyles(this.css.rollbackItemIconNode_current);

		var node = item.getLast().getFirst();
		node.getFirst().setStyles(this.css.rollbackItemActivityNode_current);
		node.getLast().setStyles(this.css.rollbackItemTimeNode_current);

		node = item.getLast().getLast();
		node.getFirst().setStyles(this.css.rollbackItemTaskTitleNode_current);
		node.getLast().setStyles(this.css.rollbackItemTaskNode_current);

		var checkeds = item.getElements("input");
		if (checkeds){
			checkeds.set("checked", true);
			checkeds.set("disabled", false);
		}
	},
	setRollBackUnchecked: function (item) {
		item.store("isSelected", false);
		item.setStyles(this.css.rollbackItemNode);

		item.getFirst().setStyles(this.css.rollbackItemIconNode);

		var node = item.getLast().getFirst();
		node.getFirst().setStyles(this.css.rollbackItemActivityNode);
		node.getLast().setStyles(this.css.rollbackItemTimeNode);

		node = item.getLast().getLast();
		node.getFirst().setStyles(this.css.rollbackItemTaskTitleNode);
		node.getLast().setStyles(this.css.rollbackItemTaskNode);

		var checkeds = item.getElements("input");
		if (checkeds) {
			checkeds.set("checked", false);
			checkeds.set("disabled", true);
		}
	},
	loadRightNode : function(){
		this.toolabrRightNode = new Element("div.toolabrRightNode",{
			"style": "float:right"
		}).inject(this.node);

		//this.loadSearch();

	},
	loadSearch : function (){

		var searchNode = new Element("div.ft_titleSearchArea",{"style":"width:300px"}).inject(this.toolabrRightNode);
		var searchIconNode = new Element("div.ft_filterIcon",{"html":'<icon class="o2icon-sousuo mainColor_color"></icon>'}).inject(searchNode);
		var searchBtn = new Element("div",{"class":"ft_searchButton mainColor_bg","text":"搜索"}).inject(searchNode);
		var searchInput = new Element("div.ft_titleInputArea").inject(searchNode);
		new Element("input",{"placeholder" : "请输入关键字"}).inject(searchInput);

	},
	getListType : function(){
		return this.viewType || this.options.viewType
	},
	loadListType : function(){

		this.listViewTypeNode = new Element("div", {
			"style" : "font-size:18px;float:left;margin-right:6px",
			"class" : this.options.viewType == "list" ? "mainColor_color" : "",
			events : {
				click : function(){
					this.viewType = "list";

					this.explorer.options.defaultViewType = this.viewType;
					this.explorer.refresh();
				}.bind(this)
			}
		}).inject(this.toolabrRightNode);
		new Element("icon",{"class":"o2Drive icon-list"}).inject(this.listViewTypeNode);

		this.tileViewTypeNode = new Element("div", {
			"style" : "font-size:18px;float:left",
			"class" : this.options.viewType !== "list" ? "mainColor_color" : "",
			events : {
				click : function(){
					this.viewType = "tile";

					this.explorer.options.defaultViewType = this.viewType;
					this.explorer.refresh();
				}.bind(this)
			}
		}).inject(this.toolabrRightNode);
		new Element("icon",{"class":"o2Drive icon-grid"}).inject(this.tileViewTypeNode);
	}
});
MWF.xApplication.process.Application.AddSerSialForm = new Class({
	Extends: MPopupForm,
	Implements: [Options, Events],
	options: {
		"style": "attendanceV2",
		"width": 500,
		//"height": 300,
		"height": "350",
		"hasTop": true,
		"hasIcon": false,
		"draggable": true,
		"title" : "新增流水号",
		"id" : ""
	},
	_createTableContent: function () {

		var html = "<table width='100%' bordr='0' cellpadding='7' cellspacing='0' styles='formTable' style='margin-top: 20px; '>" +

			"<tr><td styles='formTableTitleRight' lable='process'></td>" +
			"    <td styles='formTableValue1' item='process' colspan='2'></td>" +
			"</tr>" +
			"<tr><td styles='formTableTitleRight' lable='name'></td>" +
			"    <td styles='formTableValue1' item='name' colspan='2'></td>" +
			"</tr>" +
			"<tr><td styles='formTableTitleRight' lable='serial'></td>" +
			"    <td styles='formTableValue' item='serial' colspan='2'></td></tr>" +
			"</table>";
		this.formTableArea.set("html", html);

		this.form = new MForm(this.formTableArea, this.data || {}, {
			isEdited: true,
			style : "attendance",
			hasColon : true,
			itemTemplate: {
				name: { text : "关键字", notEmpty : true },
				serial: { text : "流水号", notEmpty : true },
				process: {
					"text": "流程",
					"type": "text",
					"notEmpty" : true,
					"defaultValue":this.data.processName,
					"event": {

						"click": function (item, ev){

							var v = item.getValue();
							o2.xDesktop.requireApp("Selector", "package", function(){
								var options = {
									"type": "Process",
									"values": [],
									"count": 1,
									"applicationId" : this.app.application.id,
									"onComplete": function (items) {
										if(items.length>0){

											var d = items[0].data;

											this.data.processId = d.id;

											item.setValue(d.name);
										}
									}.bind(this)
								};
								new o2.O2Selector(this.app.desktop.node, options);
							}.bind(this),false);
						}.bind(this)}
				}
			}
		}, this.app);
		this.form.load();

	},
	_createBottomContent: function () {

		if (this.isNew || this.isEdited) {

			this.okActionNode = new Element("button.inputOkButton", {
				"styles": this.css.inputOkButton,
				"text": "确定"
			}).inject(this.formBottomNode);

			this.okActionNode.addEvent("click", function (e) {
				this.save(e);
			}.bind(this));
		}

		this.cancelActionNode = new Element("button.inputCancelButton", {
			"styles": (this.isEdited || this.isNew || this.getEditPermission() ) ? this.css.inputCancelButton : this.css.inputCancelButton_long,
			"text": "关闭"
		}).inject(this.formBottomNode);

		this.cancelActionNode.addEvent("click", function (e) {
			this.close(e);
		}.bind(this));

	},
	save: function(){


		var data = this.form.getResult(true,null,true,false,true);
		if( data ){
			debugger
			this.app.action.SerialNumberAction.create({
				"application" : this.app.application.id,
				"process" : data.processId,
				"name" : data.name,
				"serial" : data.serial
			}).then(function (){
				this.app.notice("添加成功");
				this.explorer.refresh();
				this.close();
			}.bind(this));
		}
	}
});
MWF.xApplication.process.Application.SetSerSialForm = new Class({
	Extends: MPopupForm,
	Implements: [Options, Events],
	options: {
		"style": "attendanceV2",
		"width": 500,
		//"height": 300,
		"height": "200",
		"hasTop": true,
		"hasIcon": false,
		"draggable": true,
		"title" : "设置流水号",
		"id" : ""
	},
	_createTableContent: function () {

		var html = "<table width='100%' bordr='0' cellpadding='7' cellspacing='0' styles='formTable' style='margin-top: 20px; '>" +
			"<tr><td styles='formTableTitle' lable='serial' width='25%'></td>" +
			"    <td styles='formTableValue14' item='serial' colspan='3'></td></tr>" +
			"</table>";
		this.formTableArea.set("html", html);

		this.form = new MForm(this.formTableArea, this.data || {}, {
			isEdited: true,
			style : "minder",
			hasColon : true,
			itemTemplate: {
				serial: { text : "流水号", notEmpty : true }
			}
		}, this.app);
		this.form.load();

	},
	_createBottomContent: function () {

		if (this.isNew || this.isEdited) {

			this.okActionNode = new Element("button.inputOkButton", {
				"styles": this.css.inputOkButton,
				"text": "确定"
			}).inject(this.formBottomNode);

			this.okActionNode.addEvent("click", function (e) {
				this.save(e);
			}.bind(this));
		}

		this.cancelActionNode = new Element("button.inputCancelButton", {
			"styles": (this.isEdited || this.isNew || this.getEditPermission() ) ? this.css.inputCancelButton : this.css.inputCancelButton_long,
			"text": "关闭"
		}).inject(this.formBottomNode);

		this.cancelActionNode.addEvent("click", function (e) {
			this.close(e);
		}.bind(this));

	},
	save: function(){


		var data = this.form.getResult(true,null,true,false,true);
		if( data ){
			debugger
			this.app.action.SerialNumberAction.update(data.id,{
				"application" : data.application,
				"process" : data.process,
				"serial" : data.serial
			}).then(function (){
				this.app.notice("更新成功");
				this.explorer.refresh();
				this.close();
			}.bind(this));
		}
	}
});
MWF.xApplication.process.Application.ManageWorkForm = new Class({
	Extends: MPopupForm,
	Implements: [Options, Events],
	options: {
		"style": "attendanceV2",
		"width": "1000",
		"height": "700",
		"hasTop": true,
		"hasIcon": false,
		"hasTopIcon" : false,
		"hasTopContent" : false,
		"draggable": true,
		"maxAction" : true,
		"resizeable" : true,
		"closeAction": true,
		"title": MWF.xApplication.process.Application.LP.adminwork,
		"hideBottomWhenReading": true,
		"closeByClickMaskWhenReading": true,
	},
	_postLoad: function(){
		if(this.data.completedTime){
			this.isCompletedWork = true;
		}
		this._createTableContent_();
	},
	_createTableContent: function(){},
	_createTableContent_: function () {

		//this.formTableArea.set("html", this.getHtml());
		this.formTableContainer.setStyle("width","95%");
		this.formTableContainer.setStyle("margin","0px auto 10px");
		this.loadTab();

	},
	loadTab : function (){

		this.tabNode = new Element("div",{"styles" : this.css.tabNode }).inject(this.formTableArea);

		this.taskArea = new Element("div",{"style" : "height:500px;overflow:auto" }).inject(this.tabNode);
		this.taskDoneArea = new Element("div",{"styles" : this.css.tabPageContainer }).inject(this.tabNode);
		this.readArea = new Element("div",{"styles" : this.css.tabPageContainer }).inject(this.tabNode);
		this.readDoneArea = new Element("div",{"styles" : this.css.tabPageContainer }).inject(this.tabNode);
		this.reviewArea = new Element("div",{"styles" : this.css.tabPageContainer }).inject(this.tabNode);
		this.attachementArea = new Element("div",{"styles" : this.css.tabPageContainer }).inject(this.tabNode);
		this.recordArea = new Element("div",{"style" : "height:550px;overflow:auto" }).inject(this.tabNode);
		this.businessDataArea = new Element("div",{"styles" : this.css.tabPageContainer }).inject(this.tabNode);

		this.dataRecordArea = new Element("div",{"styles" : this.css.tabPageContainer }).inject(this.tabNode);

		MWF.require("MWF.widget.Tab", function(){

			this.tabs = new MWF.widget.Tab(this.tabNode, {"style": "attendance"});
			this.tabs.load();

			this.taskPage = this.tabs.addTab(this.taskArea, this.lp.task, false);
			this.taskPage.addEvent("show",function(){
				if(!this.initTask) this.loadTask();
			}.bind(this));

			this.taskDonePage = this.tabs.addTab(this.taskDoneArea, this.lp.taskDone, false);
			this.taskDonePage.addEvent("show",function(){
				if(!this.initTaskDone) this.loadTaskDone();
			}.bind(this));

			this.readPage = this.tabs.addTab(this.readArea, this.lp.read, false);
			this.readPage.addEvent("show",function(){
				if(!this.initRead) this.loadRead();
			}.bind(this));

			this.readDonePage = this.tabs.addTab(this.readDoneArea, this.lp.readDone, false);
			this.readDonePage.addEvent("show",function(){
				if(!this.initReadDone) this.loadReadDone();
			}.bind(this));

			this.recordPage = this.tabs.addTab(this.recordArea, this.lp.workLog, false);
			this.recordPage.addEvent("show",function(){
				if(!this.initRecord) this.loadRecord();
			}.bind(this));

			this.reviewPage = this.tabs.addTab(this.reviewArea, this.lp.review, false);
			this.reviewPage.addEvent("show",function(){
				if(!this.initReview) this.loadReview();
			}.bind(this));


			this.attachementPage = this.tabs.addTab(this.attachementArea, this.lp.attachment, false);
			this.attachementPage.addEvent("show",function(){
				if(!this.initAttachement) this.loadAttachement();
			}.bind(this));

			this.businessDataPage = this.tabs.addTab(this.businessDataArea,this.lp.businessData, false);
			this.businessDataPage.addEvent("show",function(){
				if(!this.initBusinessData) this.loadBusinessData();
			}.bind(this));

			this.dataRecordPage = this.tabs.addTab(this.dataRecordArea,this.lp.dataRecord, false);
			this.dataRecordPage.addEvent("show",function(){
				if(!this.initDataRecord) this.loadDataRecord();
			}.bind(this));

			this.tabs.pages[0].showTab();
		}.bind(this));
	},
	loadTask : function () {
		this.app.action.TaskAction.listWithWork(this.data.id,function (json){

			this.taskList = json.data;
			this._loadTask();
			this.initTask = true;
		}.bind(this),function (){
			this.taskArea.empty();
			this.initTask = true;
			return true;
		}.bind(this));
	},
	_loadTask : function (){
		this.taskArea.empty();
		this.taskContentNode = new Element("div").inject(this.taskArea);
		var taskTableNode = new Element("table.table",{
			"border" : 0,
			"cellpadding" : 5,
			"cellspacing" : 0
		}).inject(this.taskContentNode);

		var taskTableTheadNode = new Element("thead").inject(taskTableNode);
		var taskTableTbodyNode = new Element("tbody").inject(taskTableNode);
		var taskTableTheadTrNode = new Element("tr").inject(taskTableTheadNode);
		Array.each([this.lp.currentPerson, this.lp.currentDept, this.lp.activity, this.lp.startTime, this.lp.action], function (text) {
			new Element("th", {"text": text}).inject(taskTableTheadTrNode);
		});
		this.taskList.each(function (task) {
			var trNode = new Element("tr").inject(taskTableTbodyNode);
			trNode.store("data", task);

			Array.each([task.person.split("@")[0], task.unit.split("@")[0], task.activityName, task.startTime], function (text) {
				new Element("td", {
					text: text
				}).inject(trNode);
			});
			var tdOpNode = new Element("td").inject(trNode);
			var restButton = new Element("button", {"text": this.lp.resetAction, "class": "button"}).inject(tdOpNode);
			var deleteButton = new Element("button", {"text": this.lp.remove, "class": "button"}).inject(tdOpNode);
			var addSignButton = new Element("button", {"text": this.lp.addSign, "class": "button"}).inject(tdOpNode);
			var flowButton = new Element("button", {"text": this.lp.flow, "class": "button"}).inject(tdOpNode);

			_self = this;
			deleteButton.addEvent("click", function (e) {
				_self.app.confirm("warn", e, _self.lp.tip, this.lp.tip_remove, 350, 120, function () {
					_self.app.action.TaskAction.manageDelete(task.id,function (){},null,false);
					_self.loadTask();
					this.close();
				}, function(){
					this.close();
				});

			}.bind(this));
			restButton.addEvent("click", function (ev) {

				var opt = {
					"type": "identity",
					"count": 0,
					"title": _self.lp.resetAction,
					"onComplete": function (items) {
						var _self = this;
						var nameArr = [];
						items.each(function(item){
							nameArr.push(item.data.name)
						});
						this.explorer.app.confirm("warn", ev,_self.lp.tip_reset, {
							"html": _self.lp.rest_content.replace("(name)",nameArr.join())
						}, 400, 300, function(){
							var inputs = this.content.getElements("input");
							var opinion = this.content.getElement("textarea").get("value");
							var flag = "";
							for (var i=0; i<inputs.length; i++){
								if (inputs[i].checked){
									flag = inputs[i].get("value");
									break;
								}
							}
							inputs.addEvent("click",function (){
								this.content.getElement("#reset_checkInfor").set("text","");
							}.bind(this));
							if (flag){

								var keep = true;
								if(flag==="no"){
									keep = false;
								}

								var nameList = [];
								items.each(function (identity) {
									nameList.push(identity.data.distinguishedName);
								});
								var data = {
									"routeName":_self.lp.resetAction,
									"opinion":opinion,
									"identityList":nameList,
									"keep" : keep
								}
								_self.app.action.TaskAction.V2Reset(task.id,data,function(json){
								},null,false);


								_self.loadTask();
								this.close();
							}else{
								this.content.getElement("#reset_checkInfor").set("text", _self.lp.tip_keetask).setStyle("color", "red");
							}
						}, function(){
							this.close();
						});

					}.bind(this)
				};
				new MWF.O2Selector(this.app.content, opt);

			}.bind(this));

			addSignButton.addEvent("click", function (e) {

				this._addSign(task);
			}.bind(this));

			flowButton.addEvent("click", function (e) {

				this._flow(task,e);
			}.bind(this));

		}.bind(this));
	},
	_flow : function (taskData,ev){
		var _self = this;

		var processNode = new Element("div");
		var dlg = o2.DL.open({
			"title": _self.lp.flow,
			"width": "600px",
			"height": "420px",
			"mask": true,
			"content": processNode,
			"container": null,
			"positionNode": this.explorer.app.content,
			"onQueryClose": function () {
				processNode.destroy();
			}.bind(this),
			"onPostShow": function () {
				dlg.reCenter();

				o2.xDesktop.requireApp("process.Work", "Processor", function(){
					new o2.xApplication.process.Work.Processor(processNode, taskData, {
						"style": "task",
						"isManagerProcess" : true,
						"onCancel": function(){
							dlg.close();
						},
						"onSubmit": function(routeName, opinion){

							var taskId = taskData.id;
							var data = {
								"routeName": routeName,
								"opinion": opinion
							};
							_self.app.action.TaskAction.processing(taskId,data,function(json){
								dlg.close();
								_self.loadTask();
							},null,false);

						}
					});
				}.bind(this));
			}.bind(this)
		});
	},
	_addSign : function(task){

		var _self = this;
		var opt = {};

		o2.DL.open({
			"title": o2.xApplication.process.Xform.LP.form.addTask,
			"style":  "user",
			"width":   (layout.mobile) ? "100%" : 680,
			"height":  (layout.mobile) ? "100%" : 380,
			"url": this.app.path+"default/view/dlg/addSign.html",
			"lp": o2.xApplication.process.Xform.LP.form,
			"container": this.app.content,
			"maskNode": this.app.content,
			"offset": (layout.mobile) ? null : {y: -120},

			"buttonList": [
				{
					"type": "ok",
					"text": o2.LP.process.button.ok,
					"action": function (d, e) {
						if( !this.identityList || !this.identityList.length ){
							_self.app.notice(o2.xApplication.process.Xform.LP.inputAddTaskPeople, "error", this.node);
						}else{
							_self.doAddTask(task,this);
						}
					}
				},
				{
					"type": "cancel",
					"text": MWF.LP.process.button.cancel,
					"action": function () {
						this.close();
					}
				}
			],
			"onPostShow": function () {
				var selPeopleButton = this.content.getElement(".addTask_selPeopleButton");
				selPeopleButton.addEvent("click", function () {
					_self.selectPeopleAll(this,0);
				}.bind(this));
			}

		});
	},

	doAddTask: function(task,dlg){
		MWF.require("MWF.widget.Mask", function () {

			var position = this.getRadioValue(dlg.content, ".addTask_type") || "after";
			var mode = this.getRadioValue(dlg.content, ".mode_type") || "single";

			if (dlg.identityList && dlg.identityList.length){
				this.mask = new MWF.widget.Mask({ "style": "desktop", "zIndex": 50000 });
				if( layout.mobile ){
					this.mask.load();
				}else{
					this.mask.loadNode(this.app.content);
				}

				var nameArr = dlg.identityList.map(function(id){
					return o2.name.cn(id);
				});

				var opinion = dlg.content.getElement(".addTask_opinion").get("value");

				this.doAddTaskToPeople(task,dlg.identityList, opinion, mode, position === "before", "", function (json) {
					this.fireEvent("afterAddTask");
					if (this.app && this.app.fireEvent) this.app.fireEvent("afterAddTask");
					this.app.notice(MWF.xApplication.process.Xform.LP.addTaskOk + ": " + nameArr, "success");

					dlg.close();
					if (this.mask) this.mask.hide();


				}.bind(this), function (xhr, text, error) {
					var errorText = error + ":" + text;
					if (xhr) errorText = xhr.responseText;
					this.app.notice("request json error: " + errorText, "error", dlg ? dlg.node : null);

					if (this.mask) this.mask.hide();
				}.bind(this))

			}else{
				if (this.mask)  this.mask.hide();
			}

		}.bind(this));
	},
	getRadioValue: function(node, selector){
		var nodes = node.getElements(selector);
		for (var i=0; i<nodes.length; i++){
			if (nodes[i].checked){
				return nodes[i].value;
			}
		}
		return "";
	},
	doAddTaskToPeople: function (task,names, opinion, mode, before, routeName, success, failure) {

		var lp = o2.xApplication.process.Xform.LP.form;

		var leftText = (!!before ? lp.addTaskBefore : lp.addTaskAfter)+lp[mode];

		var nameArr = names.map(function(id){
			return o2.name.cn(id);
		});
		var n = nameArr.length > 3 ? (nameArr[0]+"、"+nameArr[1]+"、"+nameArr[2]+"...") : nameArr.join(", ");
		var routeName = leftText+":"+n;

		if (!opinion) opinion = leftText+":"+nameArr.join(", "); //o2.xApplication.process.Xform.LP.form.addTask+":"+nameArr.join(", ");

		var data = {
			"mode": mode,
			"before": !!before,
			"opinion": opinion,
			"routeName": routeName,
			"distinguishedNameList": names
		};
		o2.Actions.load("x_processplatform_assemble_surface").TaskAction.v3Add(
			//this.workAction.resetWork(
			function (json) {
				if (success) success(json);
			}.bind(this),
			function (xhr, text, error) {
				if (failure) failure(xhr, text, error);
			},
			task.id, data
		);
	},
	selectPeopleAll: function (dlg, count) {
		var names = dlg.identityList || [];
		var areaNode = $("resetWork_selPeopleArea");
		var options = {
			"values": names,
			"type": "identity",
			"count": count,
			"onComplete": function (items) {
				areaNode.empty();
				var identityList = [];
				items.each(function (item) {
					new MWF.widget.O2Identity(item.data, areaNode, { "style": "reset" });
					identityList.push(item.data.distinguishedName);
				}.bind(this));
				dlg.identityList = identityList;
			}.bind(this)
		};
		MWF.xDesktop.requireApp("Selector", "package", function () {
			var selector = new MWF.O2Selector(this.app.content, options);
		}.bind(this));

	},
	loadTaskDone : function () {
		this.app.action.TaskCompletedAction.listWithJob(this.data.job, function (json) {
			this.taskDoneList = json.data;
			this._loadTaskDone();
			this.initTaskDone = true;
		}.bind(this));
	},
	_loadTaskDone : function (){
		var lp = this.lp;
		this.taskDoneArea.empty();
		this.taskDoneContentNode = new Element("div").inject(this.taskDoneArea);

		var taskDoneTableNode = new Element("table.table",{
			"border" : 0,
			"cellpadding" : 5,
			"cellspacing" : 0
		}).inject(this.taskDoneContentNode);

		var taskDoneTableTheadNode = new Element("thead").inject(taskDoneTableNode);
		var taskDoneTableTbodyNode = new Element("tbody").inject(taskDoneTableNode);
		var taskDoneTableTheadTrNode = new Element("tr").inject(taskDoneTableTheadNode);
		Array.each([lp.currentPerson, lp.currentDept, lp.activity, lp.startTime, lp.endTime, lp.routerName, lp.idea, lp.action], function (text) {

			var tmpthNode = new Element("th", {"text": text}).inject(taskDoneTableTheadTrNode);
			if(text===lp.idea){
				tmpthNode.setStyle("width","150px");
			}
		});

		this.taskDoneList.each(function (taskDone) {
			var trNode = new Element("tr").inject(taskDoneTableTbodyNode);
			trNode.store("data", taskDone);

			Array.each([taskDone.person.split("@")[0], taskDone.unit.split("@")[0], taskDone.activityName, taskDone.startTime, taskDone.updateTime, taskDone.routeName, taskDone.opinion], function (text) {
				new Element("td", {
					text: text
				}).inject(trNode);
			});
			var tdOpNode = new Element("td").inject(trNode);
			var setOpinionButton = new Element("button", {"text": lp.idea, "class": "button"}).inject(tdOpNode);
			var deleteButton = new Element("button", {"text": lp.remove, "class": "button"}).inject(tdOpNode);

			deleteButton.addEvent("click", function (e) {
				_self = this;
				this.app.confirm("warn", e, lp.tip, lp.tip_remove, 350, 120, function () {
					_self.app.action.TaskCompletedAction.manageDelete(taskDone.id,function (){},null,false);
					_self.loadTaskDone();
					this.close();
				}, function(){
					this.close();
				});

			}.bind(this));

			setOpinionButton.addEvent("click", function () {
				var ideaNode = new Element("div", {"class": "control", "style": "margin:10px"});
				var textareaNode = new Element("textarea", {"style":"height:80px","class": "textarea", "text": taskDone.opinion});
				textareaNode.inject(ideaNode);

				var ideaDlg = o2.DL.open({
					"title": lp.editIdea,
					"width": "400px",
					"height": "260px",
					"mask": true,
					"content": ideaNode,
					"container": null,
					"positionNode": this.app.content,
					"onQueryClose": function () {
						ideaNode.destroy();
					}.bind(this),
					"buttonList": [
						{
							"text": lp.ok,
							"action": function () {
								this.app.action.TaskCompletedAction.manageOpinion(taskDone.id,{"opinion":textareaNode.get("value")},function(json){
								},null,false);
								this.loadTaskDone();
								ideaDlg.close();
							}.bind(this)
						},
						{
							"text": lp.close,
							"action": function () {
								ideaDlg.close();
							}.bind(this)
						}
					],
					"onPostShow": function () {
						ideaDlg.reCenter();
					}.bind(this)
				});
			}.bind(this));
		}.bind(this));
	},
	loadRead : function () {
		this.app.action.ReadAction.listWithJob(this.data.job, function (json) {
			this.readList = json.data;
			this._loadRead();
			this.initRead = true;
		}.bind(this));
	},
	_loadRead : function (){
		var lp = this.lp;
		this.readArea.empty();
		this.readContentNode = new Element("div").inject(this.readArea);
		var readTableNode = new Element("table.table",{
			"border" : 0,
			"cellpadding" : 5,
			"cellspacing" : 0
		}).inject(this.readContentNode);

		var readTableTheadNode = new Element("thead").inject(readTableNode);
		var readTableTbodyNode = new Element("tbody").inject(readTableNode);
		var readTableTheadTrNode = new Element("tr").inject(readTableTheadNode);
		Array.each([lp.readPerson, lp.currentDept, lp.activity, lp.startTime, lp.action], function (text) {
			new Element("th", {"text": text}).inject(readTableTheadTrNode);
		});

		this.readList.each(function (read) {
			var trNode = new Element("tr").inject(readTableTbodyNode);
			trNode.store("data", read);

			Array.each([read.person.split("@")[0], read.unit.split("@")[0], read.activityName, read.startTime], function (text) {
				new Element("td", {
					text: text
				}).inject(trNode);
			});
			var tdOpNode = new Element("td").inject(trNode);
			var readButton = new Element("button", {"text": lp.readDone, "class": "button"}).inject(tdOpNode);
			var restButton = new Element("button", {"text": lp.resetAction, "class": "button"}).inject(tdOpNode);
			var deleteButton = new Element("button", {"text": lp.remove, "class": "button"}).inject(tdOpNode);

			deleteButton.addEvent("click", function (e) {
				var _self = this;
				_self.app.confirm("warn", e, lp.tip, lp.tip_remove, 350, 120, function () {
					_self.app.action.ReadAction.manageDelete(read.id,function (){},null,false);
					_self.loadRead();
					this.close();
				}, function(){
					this.close();
				});
			}.bind(this));

			restButton.addEvent("click", function () {
				var opt = {
					"type": "identity",
					"count": 0,
					"title": lp.resetAction,
					"onComplete": function (items) {

						var nameList = [];
						var nameDnList = [];
						items.each(function (identity) {
							nameList.push(identity.data.name);
							nameDnList.push(identity.data.distinguishedName);
						});
						var data = {
							"opinion":lp.resetActionFor + ": " + nameList.join(","),
							"identityList":nameDnList
						}
						this.app.action.ReadAction.manageResetRead(read.id,data,function(json){
						},null,false);

						this.loadRead();
					}.bind(this)
				};

				new MWF.O2Selector(this.app.content, opt);

			}.bind(this));


			readButton.addEvent("click", function () {

				var ideaNode = new Element("div", { "style": "margin:10px"});
				var textareaNode = new Element("textarea", {"class":"textarea","style": "min-width:100%;height: 100px", "text": read.opinion});
				textareaNode.inject(ideaNode);

				var ideaDlg = o2.DL.open({
					"title": lp.readProcess,
					"width": "400px",
					"height": "260px",
					"mask": true,
					"content": ideaNode,
					"container": null,
					"positionNode": this.app.content,
					"onQueryClose": function () {
						ideaNode.destroy();
					}.bind(this),
					"buttonList": [
						{
							"text": lp.ok,
							"action": function () {
								this.app.action.ReadAction.manageProcessing(read.id,{"opinion":textareaNode.get("value")},function(json){
								},null,false);
								this.loadRead();
								ideaDlg.close();

							}.bind(this)
						},
						{
							"text": lp.close,
							"action": function () {
								ideaDlg.close();
							}.bind(this)
						}
					],
					"onPostShow": function () {
						ideaDlg.reCenter();
					}.bind(this)
				});

			}.bind(this));

		}.bind(this));
	},
	loadReadDone : function () {
		this.app.action.ReadCompletedAction.listWithJob(this.data.job, function (json) {
			this.readDoneList = json.data;
			this._loadReadDone();
			this.initReadDone = true;
		}.bind(this), null, false);
	},
	_loadReadDone : function (){
		var lp = this.lp;
		this.readDoneArea.empty();
		this.readDoneContentNode = new Element("div").inject(this.readDoneArea);
		var readDoneTableNode = new Element("table.table",{
			"border" : 0,
			"cellpadding" : 5,
			"cellspacing" : 0
		}).inject(this.readDoneContentNode);

		var readDoneTableTheadNode = new Element("thead").inject(readDoneTableNode);
		var readDoneTableTbodyNode = new Element("tbody").inject(readDoneTableNode);
		var readDoneTableTheadTrNode = new Element("tr").inject(readDoneTableTheadNode);

		Array.each([lp.currentPerson, lp.currentDept, lp.activity, lp.startTime, lp.endTime, lp.idea, lp.action], function (text) {
			new Element("th", {"text": text}).inject(readDoneTableTheadTrNode);
		});

		this.readDoneList.each(function (readDone) {
			var trNode = new Element("tr").inject(readDoneTableTbodyNode);
			trNode.store("data", readDone);

			Array.each([readDone.person.split("@")[0], readDone.unit.split("@")[0], readDone.activityName, readDone.startTime, readDone.updateTime, readDone.opinion], function (text) {
				new Element("td", {
					text: text
				}).inject(trNode);
			});
			var tdOpNode = new Element("td").inject(trNode);
			var setOpinionButton = new Element("button", {"text": lp.idea, "class": "button"}).inject(tdOpNode);
			var deleteButton = new Element("button", {"text": lp.remove, "class": "button"}).inject(tdOpNode);

			deleteButton.addEvent("click", function (e) {
				_self = this;
				this.app.confirm("warn", e, lp.tip, lp.tip_remove, 350, 120, function () {
					_self.app.action.ReadCompletedAction.manageDelete(readDone.id,function (){},null,false);
					_self.loadReadDone();
					this.close();
				}, function(){
					this.close();
				});
			}.bind(this));

			setOpinionButton.addEvent("click", function () {
				var ideaNode = new Element("div", {"class": "control", "style": "margin:10px"});
				var textareaNode = new Element("textarea", {"class": "textarea", "text": readDone.opinion});
				textareaNode.inject(ideaNode);

				var ideaDlg = o2.DL.open({
					"title": lp.editIdea,
					"width": "400px",
					"height": "260px",
					"mask": true,
					"content": ideaNode,
					"container": null,
					"positionNode": this.app.content,
					"onQueryClose": function () {
						ideaNode.destroy();
					}.bind(this),
					"buttonList": [
						{
							"text": lp.ok,
							"action": function () {
								this.app.action.ReadCompletedAction.manageUpdate(readDone.id,{"opinion":textareaNode.get("value")},function(json){
								},null,false);
								this.loadReadDone();
								ideaDlg.close();

							}.bind(this)
						},
						{
							"text": lp.close,
							"action": function () {
								ideaDlg.close();
							}.bind(this)
						}
					],
					"onPostShow": function () {
						ideaDlg.reCenter();
					}.bind(this)
				});
			}.bind(this));

		}.bind(this));
	},
	loadRecord : function (){
		this.app.action.RecordAction.listWithJob(this.data.job, function (json) {
			this.recordList = json.data;
			this._loadRecord();
			this.initRecord = true;
		}.bind(this), null, false);
	},
	_loadRecord : function (){
		var lp = this.lp;
		this.recordArea.empty();
		this.recordContentNode = new Element("div").inject(this.recordArea);
		var recordTableNode = new Element("table.table",{
			"border" : 0,
			"cellpadding" : 5,
			"cellspacing" : 0
		}).inject(this.recordContentNode);

		var recordTableTheadNode = new Element("thead").inject(recordTableNode);
		var recordTableTbodyNode = new Element("tbody").inject(recordTableNode);
		var recordTableTheadTrNode = new Element("tr").inject(recordTableTheadNode);
		Array.each([lp.currentPerson, lp.currentDept, lp.activity, lp.startTime, lp.endTime, lp.routerName, lp.idea, lp.action], function (text) {

			var tmpthNode = new Element("th", {"text": text}).inject(recordTableTheadTrNode);
			if(text===lp.idea){
				tmpthNode.setStyle("width","150px");
			}
		});

		this.recordList.each(function (record) {

			if(record.type!=="currentTask"){

				var trNode = new Element("tr").inject(recordTableTbodyNode);
				trNode.store("data", record);
				console.log(record)
				var routeName;
				if(record.type === "reroute"){
					routeName = lp.reroute;
				}else if(record.type === "reset"){
					routeName = lp.resetAction;
				}else {
					routeName = record.properties.routeName;
				}
				Array.each([record.person.split("@")[0], record.unit.split("@")[0], record.fromActivityName, record.properties.startTime, record.updateTime, routeName, record.properties.opinion], function (text) {
					new Element("td", {
						text: text
					}).inject(trNode);
				});
				var tdOpNode = new Element("td").inject(trNode);
				var setOpinionButton = new Element("button", {"text": lp.idea, "class": "button"}).inject(tdOpNode);

				var modifyButton = new Element("button", {"text": lp.modify, "class": "button"}).inject(tdOpNode);
				var copyButton = new Element("button", {"text": lp.copy, "class": "button"}).inject(tdOpNode);
				var deleteButton = new Element("button", {"text": lp.remove, "class": "button"}).inject(tdOpNode);
				deleteButton.addEvent("click", function (e) {
					_self = this;
					this.app.confirm("warn", e, lp.tip, lp.tip_remove, 350, 120, function () {
						_self.app.action.RecordAction.manageDelete(record.id,function(json){

							_self.loadRecord();
						},null,false);

						this.close();
					}, function(){
						this.close();
					});

				}.bind(this));
				setOpinionButton.addEvent("click", function () {
					_self = this;
					var ideaNode = new Element("div", {"class": "control", "style": "margin:10px"});
					var textareaNode = new Element("textarea", {"style":"height:80px","class": "textarea", "text": record.opinion});
					textareaNode.inject(ideaNode);

					var ideaDlg = o2.DL.open({
						"title": lp.editIdea,
						"width": "400px",
						"height": "260px",
						"mask": true,
						"content": ideaNode,
						"container": null,
						"positionNode": this.app.content,
						"onQueryClose": function () {
							ideaNode.destroy();
						}.bind(this),
						"buttonList": [
							{
								"text": lp.ok,
								"action": function () {
									record.opinion = textareaNode.get("value");
									_self.app.action.RecordAction.manageEdit(record.id,record,function(json){
										_self.loadRecord();
									},null,false);
									ideaDlg.close();

								}.bind(this)
							},
							{
								"text": lp.close,
								"action": function () {
									ideaDlg.close();
								}.bind(this)
							}
						],
						"onPostShow": function () {
							ideaDlg.reCenter();
						}.bind(this)
					});
				}.bind(this));
				modifyButton.addEvent("click", function () {
					var recordNode = new Element("div", {"class": "control", "style": "margin:10px"});
					var textareaNode = new Element("textarea", {"style":"height:350px","class": "textarea", "text": JSON.stringify(record,null,"\t")});
					textareaNode.inject(recordNode);

					var recordDlg = o2.DL.open({
						"title": lp.recordEdit,
						"width": "800px",
						"height": "500",
						"mask": true,
						"content": recordNode,
						"container": null,
						"positionNode": this.app.content,
						"onQueryClose": function () {
							recordNode.destroy();
						}.bind(this),
						"buttonList": [
							{
								"text": lp.ok,
								"action": function () {
									record = JSON.parse(textareaNode.get("value"));
									this.app.action.RecordAction.manageEdit(record.id,record,function(json){
										this.loadRecord();
									}.bind(this),null,false);
									recordDlg.close();

								}.bind(this)
							},
							{
								"text": lp.close,
								"action": function () {
									recordDlg.close();
								}.bind(this)
							}
						],
						"onPostShow": function () {
							recordDlg.reCenter();
						}.bind(this)
					});
				}.bind(this));
				copyButton.addEvent("click", function () {
					var recordNode = new Element("div", {"class": "control", "style": "margin:10px"});
					var textareaNode = new Element("textarea", {"style":"height:350px","class": "textarea", "text": JSON.stringify(record,null,"\t")});
					textareaNode.inject(recordNode);

					var recordDlg = o2.DL.open({
						"title": lp.recordAdd,
						"width": "800px",
						"height": "500",
						"mask": true,
						"content": recordNode,
						"container": null,
						"positionNode": this.app.content,
						"onQueryClose": function () {
							recordNode.destroy();
						}.bind(this),
						"buttonList": [
							{
								"text": lp.ok,
								"action": function () {
									record = JSON.parse(textareaNode.get("value"));
									this.app.action.RecordAction.manageCreateWithJob(record.job,record,function(json){
										this.loadRecord();
									}.bind(this),null,false);
									recordDlg.close();

								}.bind(this)
							},
							{
								"text": lp.close,
								"action": function () {
									recordDlg.close();
								}.bind(this)
							}
						],
						"onPostShow": function () {
							recordDlg.reCenter();
						}.bind(this)
					});
				}.bind(this));
			}

		}.bind(this));
	},
	loadReview : function (){
		this.app.action.ReviewAction.listWithJob(this.data.job, function (json) {
			this.reviewList = json.data;
			this._loadReview();
			this.initReview = true;
		}.bind(this), null, false);
	},
	_loadReview : function (){
		var lp = this.lp;
		this.reviewArea.empty();
		this.reviewContentNode = new Element("div").inject(this.reviewArea)
		var reviewTableNode = new Element("table.table",{
			"border" : 0,
			"cellpadding" : 5,
			"cellspacing" : 0
		}).inject(this.reviewContentNode);

		var reviewTableTheadNode = new Element("thead").inject(reviewTableNode);
		var reviewTableTbodyNode = new Element("tbody").inject(reviewTableNode);
		var reviewTableTheadTrNode = new Element("tr").inject(reviewTableTheadNode);
		Array.each([lp.reviewitem, lp.action], function (text) {
			new Element("th", {"text": text}).inject(reviewTableTheadTrNode);
		});

		this.reviewList.each(function (review) {
			var trNode = new Element("tr").inject(reviewTableTbodyNode);
			trNode.store("data", review);

			Array.each([review.person], function (text) {
				new Element("td", {
					text: text
				}).inject(trNode);
			});
			var tdOpNode = new Element("td").inject(trNode);
			var deleteButton = new Element("button", {"text": lp.remove, "class": "button"}).inject(tdOpNode);

			deleteButton.addEvent("click", function (e) {

				_self = this;
				this.app.confirm("warn", e, lp.tip, lp.tip_remove, 350, 120, function () {
					_self.app.action.ReviewAction.manageDelete(review.id,review.application,function(json){
						_self.loadReview();
					},null,false);
					this.close();
				}, function(){
					this.close();
				});

			}.bind(this));

		}.bind(this));
	},

	loadDataRecord : function (){
		this.app.action.DataRecordAction.listWithJob(this.data.job, function (json) {
			this.dataRecordList = json.data;
			this._loadDataRecord();
			this.initAttachement = true;
		}.bind(this), null, false);
	},
	_loadDataRecord : function (){
		var lp = this.lp;
		this.dataRecordArea.empty();
		this.dataRecordContentNode = new Element("div").inject(this.dataRecordArea)
		var dataRecordTableNode = new Element("table.table",{
			"border" : 0,
			"cellpadding" : 5,
			"cellspacing" : 0
		}).inject(this.dataRecordContentNode);

		var dataRecordTableTheadNode = new Element("thead").inject(dataRecordTableNode);
		var dataRecordTableTbodyNode = new Element("tbody").inject(dataRecordTableNode);
		var dataRecordTableTheadTrNode = new Element("tr").inject(dataRecordTableTheadNode);
		Array.each([this.lp.fieldName,lp.dataRecordView.person,lp.dataRecordView.time,lp.dataRecordView.active,lp.dataRecordView.old,lp.dataRecordView.new], function (text) {
			new Element("th", {"text": text}).inject(dataRecordTableTheadTrNode);
		});


		this.dataRecordList.each(function (dataRecord) {

			if(dataRecord.updateNum && dataRecord.updateNum>1){
				var path = dataRecord.path;
				var updateNum = dataRecord.updateNum;

				this.app.action.DataRecordAction.getWithJobPath(this.data.job, dataRecord.path,function (json) {
					var dataRecordList = json.data;

					dataRecordList.dataRecordItemList.each(function (item,index) {
						var trNode = new Element("tr").inject(dataRecordTableTbodyNode);
						if(index === 0 ){
							new Element("td", {
								text: path,
								rowspan : updateNum,
								style : "border-right:1px solid #E6E6E6;background:#F7F7F7;font-weight:500"
							}).inject(trNode);
						}
						item.personName = item.person.split("@")[0];

						Array.each([item.personName,item.updateDate,item.activityName,item.oldData,item.newData], function (text) {
							new Element("td", {
								text: text
							}).inject(trNode);
						}.bind(this));
					}.bind(this));
				})
			}
		}.bind(this));

	},
	_loadDataRecordDetail : function (path){
		_self = this;
		var lp = this.lp;
		var recordNode = new Element("div");

		var dataRecordContentNode = new Element("div").inject(recordNode)
		var dataRecordTableNode = new Element("table.table",{
			"border" : 0,
			"cellpadding" : 5,
			"cellspacing" : 0
		}).inject(dataRecordContentNode);

		var dataRecordTableTheadNode = new Element("thead").inject(dataRecordTableNode);
		var dataRecordTableTbodyNode = new Element("tbody").inject(dataRecordTableNode);
		var dataRecordTableTheadTrNode = new Element("tr").inject(dataRecordTableTheadNode);
		Array.each([lp.dataRecordView.person,lp.dataRecordView.time,lp.dataRecordView.active,lp.dataRecordView.old,lp.dataRecordView.new], function (text) {
			new Element("th", {"text": text}).inject(dataRecordTableTheadTrNode);
		});

		this.app.action.DataRecordAction.getWithJobPath(this.data.job, path,function (json) {
			var dataRecordList = json.data;

			dataRecordList.dataRecordItemList.each(function (item) {
				item.personName = item.person.split("@")[0];

				var trNode = new Element("tr").inject(dataRecordTableTbodyNode);

				Array.each([item.personName,item.updateDate,item.activityName,item.oldData,item.newData], function (text, index) {
					new Element("td", {
						text: text
					}).inject(trNode);
				}.bind(this));

			}.bind(this));


			var viewDlg = o2.DL.open({
				"title": path + " : " + this.lp.dataRecordView.record,
				"width": "800px",
				"height": "500",
				"mask": true,
				"content": recordNode,
				"container": this.app.content,
				"positionNode": this.app.content,
				"onQueryClose": function () {
					recordNode.destroy();
				}.bind(this),
				"buttonList": [

					{
						"text": lp.close,
						"action": function () {
							viewDlg.close();
						}.bind(this)
					}
				],
				"onPostShow": function () {
					viewDlg.reCenter();
				}.bind(this)
			});
		}.bind(this),null,false);

	},

	loadAttachement : function (){
		this.app.action.AttachmentAction.listWithJob(this.data.job, function (json) {
			this.attachmentList = json.data;
			this._loadAttachement();
			this.initAttachement = true;
		}.bind(this), null, false);
	},
	_loadAttachement : function (){
		this.attachementArea.empty();
		this.attachmentContentNode = new Element("div").inject(this.attachementArea)
		var attachmentTableNode = new Element("table.table",{
			"border" : 0,
			"cellpadding" : 5,
			"cellspacing" : 0
		}).inject(this.attachmentContentNode);

		var attachmentTableTheadNode = new Element("thead").inject(attachmentTableNode);
		var attachmentTableTbodyNode = new Element("tbody").inject(attachmentTableNode);
		var attachmentTableTheadTrNode = new Element("tr").inject(attachmentTableTheadNode);
		Array.each(["附件名称", "上传环节", "上传人", "上传时间", "标识","大小" ,"排序","操作"], function (text) {
			new Element("th", {"text": text}).inject(attachmentTableTheadTrNode);
		});

		var siteArr = [];
		this.attachmentList.each(function (attachment) {

			if(!siteArr.contains(attachment.site)) siteArr.push(attachment.site);
			var trNode = new Element("tr").inject(attachmentTableTbodyNode);
			trNode.store("data", attachment);

			Array.each([attachment.name, attachment.activityName, attachment.person.split("@")[0], attachment.createTime ,attachment.site,attachment["length"],attachment.orderNumber], function (text, index) {
				new Element("td", {
					text: index ===5 ? this.getFileSize(text) : text
				}).inject(trNode);
			}.bind(this));

			var tdOpNode = new Element("td").inject(trNode);
			var deleteButton = new Element("button", {"text": "删除", "class": "button"}).inject(tdOpNode);
			deleteButton.addEvent("click", function (e) {
				_self = this;
				this.app.confirm("warn", e, "提示", "确认是否删除", 350, 120, function () {
					_self.app.action.AttachmentAction.delete(attachment.id,function(json){
						_self.loadAttachement();
					},null,false);
					this.close();
				}, function(){
					this.close();
				});
			}.bind(this));
			var downButton = new Element("button", {"text": "下载", "class": "button"}).inject(tdOpNode);
			downButton.addEvent("click", function () {

				var locate = window.location;
				var protocol = locate.protocol;
				var addressObj = layout.serviceAddressList["x_processplatform_assemble_surface"];

				var defaultPort = layout.config.app_protocol==='https' ? "443" : "80";
				var appPort = addressObj.port || window.location.port;
				var address = protocol+"//"+(addressObj.host || window.location.hostname)+((!appPort || appPort.toString()===defaultPort) ? "" : ":"+appPort)+addressObj.context;
				window.open(o2.filterUrl(address) + "/jaxrs/attachment/download/"+ attachment.id +"/stream")

			}.bind(this));


			var sortButton = new Element("button", {"text": "排序", "class": "button"}).inject(tdOpNode);
			sortButton.addEvent("click", function () {

				var sortNode = new Element("div", {"class": "control", "style": "margin:10px"});
				var inputNode = new Element("input", {"class": "input", "text": attachment.orderNumber});
				inputNode.inject(sortNode);

				var sortDlg = o2.DL.open({
					"title": "排序号修改",
					"width": "400px",
					"height": "260px",
					"mask": true,
					"content": sortNode,
					"container": null,
					"positionNode": this.app.content,
					"onQueryClose": function () {
						sortNode.destroy();
					}.bind(this),
					"buttonList": [
						{
							"text": "确认",
							"action": function () {
								var orderNumber = inputNode.get("value");
								this.app.action.AttachmentAction.changeOrderNumber(attachment.id,attachment.id,orderNumber,function( json ){
									this.loadAttachement();
								}.bind(this),null,false);
								sortDlg.close();

							}.bind(this)
						},
						{
							"text": "关闭",
							"action": function () {
								sortDlg.close();
							}.bind(this)
						}
					],
					"onPostShow": function () {
						sortDlg.reCenter();
					}.bind(this)
				});

			}.bind(this));

		}.bind(this));


		var attachmentUploadDiv = new Element("div").inject(this.attachmentContentNode);

		var siteSelect = new Element("select",{"class":"select","style":"float:left"}).inject(attachmentUploadDiv);
		new Element("option",{value:"",text:""}).inject(siteSelect);
		siteArr.each(function(site){
			new Element("option",{value:site,text:site}).inject(siteSelect);
		});
		siteSelect.addEvent("change",function(){
			uploadSite.set("value",siteSelect.get("value"));
		});
		var uploadSite = new Element("input",{
			"class":"input",
			"placeholder":"对应上传的附件标识",
			"style" :"width:200px;float:left"
		}).inject(attachmentUploadDiv);
		var uploadButton = new Element("button", {"text": "上传", "class": "button"}).inject(attachmentUploadDiv);

		uploadButton.addEvent("click", function () {

			if(uploadSite.get("value")==""){
				this.app.notice("对应上传的附件标识不能为空","error");
				return false;
			}
			var options = {
				"title": "附件区域"
			};

			var site = uploadSite.get("value");

			var uploadAction = this.isCompletedWork?"uploadAttachmentByWorkCompleted":"uploadAttachment";
			o2.require("o2.widget.Upload", null, false);
			var upload = new o2.widget.Upload(this.app.content, {
				"action": o2.Actions.get("x_processplatform_assemble_surface").action,
				"method": uploadAction,
				"parameter": {
					"id": this.data.id
				},
				"data":{
					"site": site
				},
				"onCompleted": function(){
					this.loadAttachement();
				}.bind(this)
			});
			upload.load();


		}.bind(this));
	},
	loadBusinessData : function (){
		this.app.action.DataAction.getWithJob(this.data.job, function (json) {
			this.workData = json.data;
			this._loadBusinessData();
			this.initBusinessData = true;
		}.bind(this), null, false);
	},
	_loadBusinessData : function (){
		var workData = this.workData;
		var workDataContentNode = new Element("div",{"style":"margin:5px"}).inject(this.businessDataArea);

		this.workDataContentNode = workDataContentNode;

		var html = "<table bordr='0' cellpadding='0' cellspacing='0' styles='filterTable'>" +
			"<tr>" +
			"    <td styles='filterTableTitle' lable='fieldList'></td>" +
			"    <td styles='filterTableTitle' item='fieldList'></td>" +
			"    <td styles='filterTableTitle' lable='fieldType'></td>" +
			"    <td styles='filterTableTitle' item='fieldType'></td>" +
			"    <td styles='filterTableTitle' lable='fieldName'></td>" +
			"    <td styles='filterTableTitle' item='fieldName'></td>" +
			"</tr>" +
			"<tr>" +
			"    <td styles='filterTableTitle' lable='fieldValue'></td>" +
			"    <td styles='filterTableValue' item='fieldValue'  colspan=3></td>" +
			"   <td styles='filterTableValue' colspan=2><div item='action' ></div></td>" +
			"</tr>" +
			"</table><div item='workData'></div>"

		workDataContentNode.set("html", html);

		this.form = new MForm(workDataContentNode, {}, {
			style: "attendance",
			isEdited: true,
			itemTemplate: {
				fieldList: {
					"text": "字段列表",
					"type": "select",
					"style": {"max-width": "150px"},
					"selectValue": function () {
						var arr = [""];
						arr.append(Object.keys(workData));
						return arr;
					},
					"event": {
						"change": function (item, ev) {

							var type = typeof(workData[item.getValue()]);
							item.form.getItem("fieldType").setValue(type);
							item.form.getItem("fieldName").setValue(item.getValue());

							if(type === "object" || type === "array"){
								item.form.getItem("fieldValue").setValue(JSON.stringify(workData[item.getValue()]));
							}else {
								item.form.getItem("fieldValue").setValue(workData[item.getValue()]);
							}


						}.bind(this)
					}
				},
				fieldType: {
					"text": "字段类型",
					"type": "select",
					"style": {"max-width": "150px"},
					"selectValue": function () {
						var array = ["","array","boolean","string","number","object"];
						return array;
					},
					"event": {
						"change": function (item, ev) {

						}.bind(this)
					}
				},
				fieldName: {text: "字段名", "type": "text", "style": {"min-width": "100px"}},
				fieldValue: {text: "字段值", "type": "textarea", "style": {"width": "100%","margin-left": "10px"}},

				action: {
					"value": "修改", type: "button", className: "filterButton", event: {
						click: function (e) {
							var result = this.form.getResult(false, null, false, false, false);

							var fieldName = result["fieldName"];
							var fieldType = result["fieldType"];
							var fieldValue = result["fieldValue"];

							if (!fieldName) return false;
							workData[fieldName] = (fieldType === "object" ? JSON.parse(fieldValue) : fieldValue);

							_self = this;
							this.app.confirm("warn", e.node, "提示", "确认是否修改", 350, 120, function () {
								if(_self.isCompletedWork){
									_self.app.action.DataAction.updateWithWorkCompleted(_self.data.id,workData,function (json){},null,false);
								}else{
									_self.app.action.DataAction.updateWithWork(_self.data.id,workData,function (json){},null,false);
								}
								_self.app.notice("success");

								_self.loadScriptEditor();

								this.close();
							}, function(){
								this.close();
							});

						}.bind(this)
					}
				}
			}
		}, this.app, this.css);
		this.form.load();
		this.loadScriptEditor();
	},
	loadScriptEditor:function(){
		if( !this.workData )return;
		MWF.require("MWF.widget.JavascriptEditor", null, false);

		var workDataNode = this.formTableContainer.getElement('[item="workData"]');

		this.scriptEditor = new MWF.widget.JavascriptEditor(workDataNode, {
			"forceType": "ace",
			"option": { "mode" : "json" }
		});
		this.scriptEditor.load(function(){
			this.scriptEditor.setValue(JSON.stringify(this.workData, null, "\t"));
			this.scriptEditor.editor.setReadOnly(true);
			this.addEvent("afterResize", function () {
				this.resizeScript();
			}.bind(this))
			this.addEvent("queryClose", function () {

			}.bind(this))
			this.resizeScript();
		}.bind(this));
	},
	resizeScript: function () {
		var size = this.formTableContainer.getSize();
		var tableSize = this.formTableContainer.getElement('table').getSize();
		this.formTableContainer.getElement('[item="workData"]').setStyle("height", size.y - 200);
		if (this.scriptEditor && this.scriptEditor.editor) this.scriptEditor.editor.resize();
	},
	getFileSize: function (size) {
		if (!size)
			return "";
		var num = 1024.00; //byte
		if (size < num)
			return size + "B";
		if (size < Math.pow(num, 2))
			return (size / num).toFixed(2) + "K"; //kb
		if (size < Math.pow(num, 3))
			return (size / Math.pow(num, 2)).toFixed(2) + "M"; //M
		if (size < Math.pow(num, 4))
			return (size / Math.pow(num, 3)).toFixed(2) + "G"; //G
	},

});
MWF.xApplication.process.Application.ManageWorkCompletedForm = new Class({
	Extends: MWF.xApplication.process.Application.ManageWorkForm,
	loadTask : function () {
		this.app.action.TaskAction.listWithJob(this.data.job).then(function(json){
			this.taskList = json.data;
			this._loadTask();
			this.initTask = true;
		}.bind(this));
	},
	loadTab : function (){

		this.tabNode = new Element("div",{"styles" : this.css.tabNode }).inject(this.formTableArea);
		this.taskDoneArea = new Element("div",{"styles" : this.css.tabPageContainer }).inject(this.tabNode);
		this.readArea = new Element("div",{"styles" : this.css.tabPageContainer }).inject(this.tabNode);
		this.readDoneArea = new Element("div",{"styles" : this.css.tabPageContainer }).inject(this.tabNode);
		this.reviewArea = new Element("div",{"styles" : this.css.tabPageContainer }).inject(this.tabNode);
		this.attachementArea = new Element("div",{"styles" : this.css.tabPageContainer }).inject(this.tabNode);
		this.recordArea = new Element("div",{"styles" : this.css.tabPageContainer }).inject(this.tabNode);
		this.businessDataArea = new Element("div",{"styles" : this.css.tabPageContainer }).inject(this.tabNode);
		this.dataRecordArea = new Element("div",{"styles" : this.css.tabPageContainer }).inject(this.tabNode);
		MWF.require("MWF.widget.Tab", function(){

			this.tabs = new MWF.widget.Tab(this.tabNode, {"style": "attendance"});
			this.tabs.load();

			this.taskDonePage = this.tabs.addTab(this.taskDoneArea, this.lp.taskDone, false);
			this.taskDonePage.addEvent("show",function(){
				if(!this.initTaskDone) this.loadTaskDone();
			}.bind(this));

			this.readPage = this.tabs.addTab(this.readArea, this.lp.read, false);
			this.readPage.addEvent("show",function(){
				if(!this.initRead) this.loadRead();
			}.bind(this));

			this.readDonePage = this.tabs.addTab(this.readDoneArea, this.lp.readDone, false);
			this.readDonePage.addEvent("show",function(){
				if(!this.initReadDone) this.loadReadDone();
			}.bind(this));

			this.recordPage = this.tabs.addTab(this.recordArea, this.lp.workLog, false);
			this.recordPage.addEvent("show",function(){
				if(!this.initRecord) this.loadRecord();
			}.bind(this));

			this.reviewPage = this.tabs.addTab(this.reviewArea, this.lp.review, false);
			this.reviewPage.addEvent("show",function(){
				if(!this.initReview) this.loadReview();
			}.bind(this));


			this.attachementPage = this.tabs.addTab(this.attachementArea, this.lp.attachment, false);
			this.attachementPage.addEvent("show",function(){
				if(!this.initAttachement) this.loadAttachement();
			}.bind(this));

			this.businessDataPage = this.tabs.addTab(this.businessDataArea, this.lp.businessData, false);
			this.businessDataPage.addEvent("show",function(){
				if(!this.initBusinessData) this.loadBusinessData();
			}.bind(this));
			this.dataRecordPage = this.tabs.addTab(this.dataRecordArea,this.lp.dataRecord, false);
			this.dataRecordPage.addEvent("show",function(){
				if(!this.initDataRecord) this.loadDataRecord();
			}.bind(this));
			this.tabs.pages[0].showTab();
		}.bind(this));
	}
});
