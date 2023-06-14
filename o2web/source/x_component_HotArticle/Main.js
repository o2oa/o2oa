MWF.xApplication.HotArticle.options.multitask = false;
MWF.xDesktop.requireApp("Template", "MPopupForm", null, false);
MWF.xDesktop.requireApp("Template", "MForm", null, false);
MWF.xApplication.HotArticle.Main = new Class({
	Extends: MWF.xApplication.Common.Main,
	Implements: [Options, Events],
	options: {
		"style1": "default",
		"style": "default",
		"name": "HotArticle",
		"mvcStyle": "style.css",
		"icon": "icon.png",
		"title": MWF.xApplication.HotArticle.LP.title,
		"key" : ""
	},
	onQueryLoad: function(){
		this.lp = MWF.xApplication.HotArticle.LP;
		this.action = o2.Actions.load("x_hotpic_assemble_control");
	},
	loadApplication: function(callback){
		var url = this.path+this.options.style+"/view/view.html";
		this.content.loadHtml(url, {"bind": {"lp": this.lp,"data":{}}, "module": this}, function(){
			this.setLayout();
			this.loadList("all");
			if (callback) callback();
		}.bind(this));
	},
	loadList: function(type){

		if (this.currentMenu) this.setMenuItemStyleDefault(this.currentMenu);
		this.setMenuItemStyleCurrent(this[type+"MenuNode"]);
		this.currentMenu = this[type+"MenuNode"];
		this._loadListContent(type);
	},
	_loadListContent: function(type){

		this.mainNode.empty();

		list = new MWF.xApplication.HotArticle[type.capitalize() +"List"](this.mainNode,this, {
			"onLoadData": function (){
				this.hideSkeleton();
			},
			"type" : type,
			"key" : this.options.key
		});
		this.currentList = list;
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
	setLayout: function(){

	},
	recordStatus: function(){
		// return {"navi": this.currentList.options.type};
	}
});

MWF.xApplication.HotArticle.List = new Class({
	Implements: [Options, Events],
	options: {
		"type": "all",
		"defaultViewType" : "list",
		"key" : ""

	},
	initialize: function (node,app, options) {
		this.setOptions(options);
		this.app = app;
		this.container = node;
		this.lp = this.app.lp;

		this.action = app.action;
		this.type = this.options.type;
		var url = this.app.path + this.app.options.style + "/view/content.html";
		this.container.loadHtml(url, {"bind": {"lp": this.lp,"data":{"type":this.type}}, "module": this}, function(){
			this.content = this.listContentNode;
			this.bottomNode = this.listBottomNode;
			this.pageNode = this.pageNumberAreaNode;
			this.init();
			this.load();

		}.bind(this));

	},
	showSkeleton: function(){


		if (this.skeletonNode) this.skeletonNode.inject(this.listContentNode);
	},
	hideSkeleton: function(){

		if (this.skeletonNode) this.skeletonNode.dispose();
	},

	inputFilter: function(e){
		if (e.keyCode==13) this.doFilter();
	},
	doFilter: function(){
		var key = this.searchKeyNode.get("value");
		this.searchKeyNode.set("value","");

		this.app.options.key = key;
		this.app.loadList("all");

	},

	loadListTitle : function (){
		this.listTitleNode.empty();
		this.listTitleNode.loadHtml(this.titleTempleteUrl, {"bind": {"lp": this.lp}, "module": this}, function(){
			this.currentSortNode = this.sortUpdateTimeNode;

			this.currentSortKey = "name";
		}.bind(this));
	},

	selectAllFile : function (e){

		if (e.currentTarget.get("disabled").toString()!="true"){
			var itemNode = e.currentTarget.getParent(".listItem");
			var iconNode = e.currentTarget.getElement(".selectFlagIcon");

			if (itemNode){
				if (itemNode.hasClass("mainColor_bg_opacity")){
					itemNode.removeClass("mainColor_bg_opacity");
					iconNode.removeClass("iconfont-workCompleted");
					iconNode.removeClass("selectFlagIcon_select");
					iconNode.removeClass("mainColor_color");


					this.listContentNode.getElements(this.toolbar.options.viewType === "list"?"tr":".listItem").each(function (tr){
						tr.removeClass("mainColor_bg_opacity");
						var ss = tr.getElement(".selectFlagIcon");
						tr.getElement(".selectFlag").hide();
						ss.removeClass("iconfont-workCompleted");
						ss.removeClass("selectFlagIcon_select");
						ss.removeClass("mainColor_color");

					})

					this.selectedList = [];

				}else{
					itemNode.addClass("mainColor_bg_opacity");
					iconNode.addClass("iconfont-workCompleted");
					iconNode.addClass("selectFlagIcon_select");
					iconNode.addClass("mainColor_color");
					this.listContentNode.getElements(this.toolbar.options.viewType === "list"?"tr":".listItem").each(function (tr){
						tr.getElement(".selectFlag").show();
						tr.addClass("mainColor_bg_opacity");
						var ss = tr.getElement(".selectFlagIcon");

						ss.addClass("iconfont-workCompleted");
						ss.addClass("selectFlagIcon_select");
						ss.addClass("mainColor_color");

					})
					this.selectedList = this.dataList;
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
		this.size = 20;
		this.page = 1;

		if(this.options.key!==""){
			var keyContainer = new Element("div.ft_filterItem").inject(this.pathNode);
			new Element("div",{"class":"ft_filterItemTitle mainColor_color","text": this.lp.key + "："}).inject(keyContainer);
			new Element("div",{"class":"ft_filterItemName","text":this.options.key}).inject(keyContainer);
			var iconNode = new Element("icon",{"class":"iconfont-off ft_filterItemDel"}).inject(keyContainer);

			iconNode.addEvent("click",function (ev){
				ev.target.getParent().hide();
				this.app.options.key = "";
				this.app.loadList(this.options.type);
			}.bind(this))
		}


	},
	_initTempate: function (){
		this.titleTempleteUrl = this.app.path+this.app.options.style+"/view/all/"+this.options.defaultViewType+"_title.html";
		this.listTempleteUrl = this.app.path+this.app.options.style+"/view/all/" +this.options.defaultViewType + ".html";

	},
	load: function(){


		var _self = this;

		this._initToolBar();
		this._initTempate();
		this.loadListTitle();

		this.loadToolBar(this.toolbarItems.unSelect);
		this.selectedList = [];
		this.loadData().then(function(data){
			_self.hide();
			_self.loadItems(data);
		});
	},
	_initToolBar : function (){

		this.toolbarItems = {
			"unSelect":[
				["rename", "delete"]
			],
			"selected":[
				["rename", "delete"]
			],
			"mulSelect":[
				["delete"]
			]
		}

	},
	loadToolBar : function (availableTool){

		this.toolBarNode.empty();
		this.toolbar = new MWF.xApplication.HotArticle.Toolbar(this.toolBarNode, this, {
			viewType : this.options.defaultViewType,
			type : this.type,
			availableTool : availableTool
		});
		this.toolbar.load();
	},
	refresh: function(){
		this.hide();
		this.load();
	},
	hide: function(){
		if (this.node) this.node.destroy();
	},
	loadData: function(){
		var _self = this;

		var data = {}
		if(this.options.key!==""){
			data.title = this.options.key;
		}
		return this.action.HotPictureInfoAction.listForPage(this.page,this.size,data).then(function(json){

			_self.fireEvent("loadData");
			_self.total = json.count;
			_self.loadPage();
			return _self._fixData(json.data);
		});

	},
	_fixData : function (dataList){
		dataList.each(function (data){

			if(data.application === "BBS"){
				data.applicationName = this.lp.bbsHotArticele;
			}

			if(data.application === "CMS"){
				data.applicationName = this.lp.infoHotArticele;
			}
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
	open: function(id,e){
		var data ;
		for(var i = 0 ; i < this.dataList.length;i++){
			if(this.dataList[i].id === id){
				data = this.dataList[i];
				break ;
			}
		}

		if( data.application == "BBS" ){
			var appId = "ForumDocument"+data.infoId;
			if (this.app.desktop.apps[appId]){
				this.app.desktop.apps[appId].setCurrent();
			}else {
				this.app.desktop.openApplication(null, "ForumDocument", {
					"id" : data.infoId,
					"appId": appId,
					"isEdited" : false,
					"isNew" : false
				});
			}
		}else{
			var appId = "cms.Document"+data.infoId;
			if (this.app.desktop.apps[appId]){
				this.app.desktop.apps[appId].setCurrent();
			}else {
				this.app.desktop.openApplication(null, "cms.Document", {
					"documentId" : data.infoId,
					"appId": appId,
					"readonly" : true
				});
			}
		}


	},

	selectFile: function(id,e, dataList){
		e.stopPropagation()
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
					iconNode.removeClass("iconfont-workCompleted");
					iconNode.removeClass("selectFlagIcon_select");
					iconNode.removeClass("mainColor_color");
					this.unselectedFile(data);
				}else{
					itemNode.addClass("mainColor_bg_opacity");
					iconNode.addClass("iconfont-workCompleted");
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



			this.loadToolBar(this.toolbarItems.unSelect);
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
});
MWF.xApplication.HotArticle.AllList = new Class({
	Extends: MWF.xApplication.HotArticle.List
});

MWF.xApplication.HotArticle.CMSList = new Class({
	Extends: MWF.xApplication.HotArticle.AllList,
	loadData: function(){
		var _self = this;
		var data = {}
		if(this.options.key!==""){
			data.title = this.options.key;
		}
		data.application = "CMS";
		return this.action.HotPictureInfoAction.listForPage(this.page,this.size,data).then(function(json){
			_self.fireEvent("loadData");
			_self.total = json.count;
			_self.loadPage();
			return _self._fixData(json.data);
		});


	}
});

MWF.xApplication.HotArticle.BBSList = new Class({
	Extends: MWF.xApplication.HotArticle.AllList,
	loadData: function(){
		var _self = this;
		var data = {}
		if(this.options.key!==""){
			data.title = this.options.key;
		}
		data.application = "BBS";
		return this.action.HotPictureInfoAction.listForPage(this.page,this.size,data).then(function(json){
			_self.fireEvent("loadData");
			_self.total = json.count;
			_self.loadPage();
			return _self._fixData(json.data);
		});


	}
});

MWF.xApplication.HotArticle.Toolbar = new Class({
	Extends: MWF.widget.Common,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"viewType" : "list",
		"type" : "all"
	},
	initialize : function( container, explorer, options ) {

		this.container = container;
		this.explorer = explorer;
		this.app = explorer.app;
		this.lp = explorer.app.lp;

		this.action = explorer.action;

		this.setOptions(options);

		this._initTools();
		this.type = this.options.type;

		this.availableTool = this.options.availableTool;


	},
	_initTools : function (){
		this.tools = {
			rename : {
				action : "rename",
				text : this.lp.button.rename,
				icon : "iconfont-edit"
			},
			delete : {
				action : "delete",
				text : this.lp.button.delete,
				icon : "iconfont-delete"
			},
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

				var toolNode = new Element( "div", {
					class : className,
					style : "cursor:pointer;height:30px;line-height:30px;padding-left:12px;padding-right:12px;background: #4A90E2;font-size: 13px;color: #FFFFFF;font-weight: 400;",
					events : {
						click : function( ev ){ this[tool.action]( ev ) }.bind(this)
					}
				}).inject( toolgroupNode );

				var iconNode = new Element("icon",{"class":"o2Drive " + tool.icon,"style":"margin-right:6px"}).inject(toolNode);
				var textNode = new Element("span").inject(toolNode);
				textNode.set("text",tool.text);


			}.bind(this))
		}.bind(this));

		this.loadRightNode()
	},
	rename : function(){

		var _self = this;
		if (this.explorer.selectedList && this.explorer.selectedList.length){
			var data = this.explorer.selectedList[0];
			var form = new MWF.xApplication.HotArticle.ReNameForm(this.explorer, data, {
			}, {
				app: this.app
			});
			form.edit()
		}else {
			this.app.notice(this.lp.tip.selectFile,"error");
			return;
		}

	},
	delete : function (e){

		if (this.explorer.selectedList && this.explorer.selectedList.length){
			var _self = this;
			var dataList = this.explorer.selectedList;

			this.app.confirm("warn", e, this.lp.tip.removeConfirmTitle, this.lp.tip.removeConfirm.replace("{length}",dataList.length), 350, 120, function () {
				var count = 0;
				dataList.each( function(data){
					_self.action.HotPictureInfoAction.delete(data.application, data.infoId , function(){
						count++;
						if( dataList.length == count ){
							_self.app.notice(_self.lp.tip.removeSuccess.replace("{count}",count));
							_self.explorer.refresh();
						}
					});
				}.bind(this));
				this.close();
			}, function () {
				this.close();
			});
		}else {
			this.app.notice(this.lp.tip.selectFile,"error");
			return;
		}



	},

	loadRightNode : function(){
		this.toolabrRightNode = new Element("div.toolabrRightNode",{
			"style": "float:right"
		}).inject(this.node);

		this.loadListType();

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
		new Element("icon",{"class":"iconfont-list"}).inject(this.listViewTypeNode);

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
		new Element("icon",{"class":"iconfont-grid"}).inject(this.tileViewTypeNode);
	}
});

MWF.xApplication.HotArticle.ReNameForm = new Class({
	Extends: MPopupForm,
	Implements: [Options, Events],
	options: {
		"style": "attendanceV2",
		"width": 700,
		//"height": 300,
		"height": "200",
		"hasTop": true,
		"hasIcon": false,
		"draggable": true,
		"title" : MWF.xApplication.HotArticle.LP.tip.modifyTitle,
		"id" : ""
	},
	_createTableContent: function () {

		var html = "<table width='100%' bordr='0' cellpadding='7' cellspacing='0' styles='formTable' style='margin-top: 20px; '>" +
			"<tr>" +
			"    <td styles='formTableValue14' item='title' ></td></tr>" +
			"</table>";
		this.formTableArea.set("html", html);

		this.form = new MForm(this.formTableArea, this.data || {}, {
			isEdited: true,
			style : "minder",
			hasColon : true,
			itemTemplate: {
				title: { text : MWF.xApplication.HotArticle.LP.list.title, notEmpty : true }
			}
		}, this.app);
		this.form.load();

	},
	_createBottomContent: function () {

		if (this.isNew || this.isEdited) {

			this.okActionNode = new Element("button.inputOkButton", {
				"styles": this.css.inputOkButton,
				"text": MWF.xApplication.HotArticle.LP.button.ok
			}).inject(this.formBottomNode);

			this.okActionNode.addEvent("click", function (e) {
				this.save(e);
			}.bind(this));
		}

		this.cancelActionNode = new Element("button.inputCancelButton", {
			"styles": (this.isEdited || this.isNew || this.getEditPermission() ) ? this.css.inputCancelButton : this.css.inputCancelButton_long,
			"text": MWF.xApplication.HotArticle.LP.button.close
		}).inject(this.formBottomNode);

		this.cancelActionNode.addEvent("click", function (e) {
			this.close(e);
		}.bind(this));

	},
	save: function(){

		var data = this.form.getResult(true,null,true,false,true);

		if( data ){
			this.app.action.HotPictureInfoAction.changeTitle({
				"id" : data.id,
				"application" : data.application,
				"infoId" : data.infoId,
				"title" : data.title,
				"summary" : data.summary,
				"picId" : data.picId,
				"creator" : data.creator,
				"createTime" : data.createTime,
				"updateTime" : data.updateTime
			}).then(function (){
				this.app.notice(MWF.xApplication.HotArticle.LP.tip.success);
				this.explorer.refresh();
				this.close();
			}.bind(this));
		}
	}
});


