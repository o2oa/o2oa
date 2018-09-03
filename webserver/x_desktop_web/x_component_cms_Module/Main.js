MWF.xApplication.cms = MWF.xApplication.cms || {};
MWF.CMSE = MWF.xApplication.cms.Module = MWF.xApplication.cms.Module ||{};
MWF.require("MWF.widget.O2Identity", null,false);
//MWF.xDesktop.requireApp("cms.Module", "Actions.RestActions", null, false);
MWF.xApplication.cms.Module.options = {
	multitask: false,
	executable: true
};
MWF.xApplication.cms.Module.Main = new Class({
	Extends: MWF.xApplication.Common.Main,
	Implements: [Options, Events],

	options: {
		"style": "default",
		"name": "cms.Module",
		"icon": "icon.png",
		"width": "1200",
		"height": "700",
		"isResize": true,
		"isMax": true,
		"isCategory" : false,
		"searchKey" : "",
		"title": MWF.xApplication.cms.Module.LP.title
	},
	onQueryLoad: function(){
		this.lp = MWF.xApplication.cms.Module.LP;
	},
	loadApplication: function(callback){
		//this.controllers = [];
		this.isAdmin = false;
		this.restActions = MWF.Actions.get("x_cms_assemble_control"); //new MWF.xApplication.cms.Module.Actions.RestActions();
		this.createNode();
		this.loadApplicationContent();
	},
	createNode: function(){
		this.content.setStyle("overflow", "hidden");
		this.node = new Element("div", {
			"styles": this.css.node
		}).inject(this.content);

		this.naviContainerNode = new Element("div.naviContainerNode", {
			"styles": this.css.naviContainerNode
		}).inject(this.node);
		this.leftTitleNode = new Element("div.leftTitleNode", {
			"styles": this.css.leftTitleNode
		}).inject(this.naviContainerNode);

		this.rightContentNode = new Element("div", {
			"styles":this.css.rightContentNode
		}).inject(this.node);
		this.titleBar = new Element("div", {
			"styles": this.css.titleBar
		}).inject(this.rightContentNode );
	},
	loadApplicationContent: function(){
		if( this.options.columnData ){
			this.setTitle(this.options.columnData.appName);
			this.loadController(function(){
				this.loadTitle(function(){
					this.loadMenu();
				}.bind(this));
			}.bind(this))
		}else if( (this.status && this.status.columnId) || this.options.columnId ){
			var columnId = this.options.columnId || this.status.columnId;
			this.loadColumnData( columnId, function(){
				this.loadController(function(){
					this.loadTitle(function(){
						this.loadMenu();
					}.bind(this));
				}.bind(this))
			}.bind(this))
		}else if( this.options.columnAlias ){
			this.restActions.getColumnByAlias( this.options.columnAlias, function( json ){
				this.options.columnData = json.data;
				this.setTitle(this.options.columnData.appName);
				this.loadController(function(){
					this.loadTitle(function(){
						this.loadMenu();
					}.bind(this));
				}.bind(this))
			}.bind(this))
		}
	},
	loadColumnData : function(columnId, callback){
		this.restActions.getColumn( columnId, function( json ){
			this.options.columnData = json.data;
			this.setTitle(this.options.columnData.appName);
			if(callback)callback()
		}.bind(this))
	},
	loadController: function(callback){
		//this.restActions.listColumnController(this.options.columnData.id, function( json ){
		//	json.data = json.data || [];
		//	json.data.each(function(item){
		//		this.controllers.push(item.adminUid)
		//	}.bind(this));
		//	this.isAdmin = MWF.AC.isCMSManager() || this.controllers.contains(layout.desktop.session.user.distinguishedName);
		//	if(callback)callback(json);
		//}.bind(this));
		this.restActions.isAppInfoManager( this.options.columnData.id, function( json ){
			this.isAdmin = MWF.AC.isCMSManager() || json.data.value;
			if(callback)callback(json);
		}.bind(this))
	},
	loadTitle : function(callback){
		if( this.isAdmin ){
			this.loadImportActionNode();
			this.loadExportActionNode();
		}
		this.loadCreateDocumentActionNode(
			function(){
				this.loadTitleIconNode();
				this.loadTitleContentNode();
				this.loadSearchNode();
				if(callback)callback();
			}.bind(this)
		);
	},
	loadCreateDocumentActionNode: function( callback ) {
		this.restActions.listCategoryByPublisher( this.options.columnData.id, function( json ){
			if( json.data && json.data.length ){
				this.createDocumentAction = new Element("div", {
					"styles": this.css.createDocumentAction,
					"text" : this.lp.start
				}).inject(this.titleBar);
				this.createDocumentAction.addEvents({
					"click": function(e){
						MWF.xDesktop.requireApp("cms.Index", "Newer", null, false);
						this.creater = new MWF.xApplication.cms.Index.Newer( this.options.columnData, null, this, this.view, {
							restrictToColumn : true
						});
						this.creater.load();
					}.bind(this),
					"mouseover" : function(e){
						this.createDocumentAction.setStyles( this.css.createDocumentAction_over )
					}.bind(this),
					"mouseout" : function(e){
						this.createDocumentAction.setStyles( this.css.createDocumentAction )
					}.bind(this)
				});
			}
			if(callback)callback();
		}.bind(this));
	},
	loadImportActionNode : function(){
		this.importAction = new Element("div", {
			"styles": this.css.importAction,
			"text" : this.lp.import
		}).inject(this.titleBar);
		this.importAction.setStyle("display","none");
		this.importAction.addEvents({
			"click": function(e){
				MWF.xDesktop.requireApp("cms.Module", "ExcelForm", null, false);
				var categoryData = this.navi.currentObject.isCategory ? this.navi.currentObject.data : this.navi.currentObject.category.data ;
				this.import = new MWF.xApplication.cms.Module.ImportForm( { app : this }, categoryData, {} );
				this.import.edit();
			}.bind(this),
			"mouseover" : function(e){
				this.importAction.setStyles( this.css.importAction_over )
			}.bind(this),
			"mouseout" : function(e){
				this.importAction.setStyles( this.css.importAction )
			}.bind(this)
		});
	},
	loadExportActionNode : function(){
		this.exportAction = new Element("div", {
			"styles": this.css.exportAction,
			"text" : this.lp.export
		}).inject(this.titleBar);
		this.exportAction.setStyle("display","none");
		this.exportAction.addEvents({
			"click": function(e){
				MWF.xDesktop.requireApp("cms.Module", "ExcelForm", null, false);
				var categoryData = this.navi.currentObject.isCategory ? this.navi.currentObject.data : this.navi.currentObject.category.data ;
				this.export = new MWF.xApplication.cms.Module.ExportForm ( { app : this }, categoryData, {} );
				this.export.edit();
			}.bind(this),
			"mouseover" : function(e){
				this.exportAction.setStyles( this.css.exportAction_over )
			}.bind(this),
			"mouseout" : function(e){
				this.exportAction.setStyles( this.css.exportAction )
			}.bind(this)
		});
	},
	loadTitleIconNode : function(){

		this.defaultColumnIcon = "/x_component_cms_Index/$Main/"+this.options.style+"/icon/column.png";

		var iconAreaNode = this.iconAreaNode = new Element("div",{
			"styles" : this.css.titleIconAreaNode
		}).inject(this.leftTitleNode);

		var iconNode = this.iconNode = new Element("img",{
			"styles" : this.css.titleIconNode
		}).inject(iconAreaNode);
		if (this.options.columnData.appIcon){
			this.iconNode.set("src", "data:image/png;base64,"+this.options.columnData.appIcon+"");
		}else{
			this.iconNode.set("src", this.defaultColumnIcon)
		}
		iconNode.makeLnk({
			"par": this._getLnkPar()
		});
	},
	_getLnkPar: function(){
		var lnkIcon = this.defaultColumnIcon;
		if (this.options.columnData.appIcon) lnkIcon = "data:image/png;base64,"+this.options.columnData.appIcon;

		var appId = "cms.Module"+this.options.columnData.id;
		return {
			"icon": lnkIcon,
			"title": this.options.columnData.appName,
			"par": "cms.Module#{\"columnId\": \""+this.options.columnData.id+"\", \"appId\": \""+appId+"\"}"
		};
	},
	loadTitleContentNode: function(){
		this.titleContentNode = new Element("div.titleContentNode", {
			"styles": this.css.titleContentNode
		}).inject(this.leftTitleNode);

		this.titleTextNode = new Element("div.titleTextNode", {
			"styles": this.css.titleTextNode,
			"text": this.options.columnData.appName,
			"title": this.options.columnData.appName
		}).inject(this.titleContentNode);

		this.titleDescriptionNode =  new Element("div.titleDescriptionNode", {
			"styles": this.css.titleDescriptionNode,
			"text": this.options.columnData.description ? this.options.columnData.description : this.lp.noDescription,
			"title": this.options.columnData.description ? this.options.columnData.description : this.lp.noDescription
		}).inject(this.titleContentNode);
	},
	loadSearchNode : function(){
		this.searchNode = new Element("div").inject( this.titleBar );
	},
	loadMenu: function(callback){

		this.naviNode = new Element("div.naviNode", {
			"styles": this.css.naviNode
		}).inject(this.naviContainerNode);

		//this.setScrollBar(this.naviNode,{"where": "before"});
		MWF.require("MWF.widget.ScrollBar", function(){
			new MWF.widget.ScrollBar(this.naviContainerNode, {
				"style":"xApp_ProcessManager_StartMenu", "distance": 100, "friction": 4,	"axis": {"x": false, "y": true}
			});
		}.bind(this));

		this.addEvent("resize", function(){this.setNaviSize();}.bind(this));

		//MWF.require("MWF.widget.ScrollBar", function(){
		//	new MWF.widget.ScrollBar(this.menuNode, {
		//		"style":"xApp_CMSModule_StartMenu", "distance": 100, "friction": 4,	"axis": {"x": false, "y": true}
		//	});
		//}.bind(this));
		if( this.options.categoryId == "all" ){
			this.options.categoryId = "whole";
		}
		if( this.status && this.status.categoryId ){
			this._loadMenu( this.status );
		}else if( this.options.categoryId && this.options.categoryId != "" ){
			if( this.options.viewId && this.options.viewId!="" ){
				this._loadMenu( { "categoryId" :this.options.categoryId , "viewId" : this.options.viewId } )
			}else{
				//this.getCategoryDefaultList(this.options.categoryId , function(viewId){
				//	if( viewId ){
				//		this._loadMenu( { "categoryId" :this.options.categoryId , "viewId" : viewId, "isCategory" : this.options.isCategory } );
				//	}else{
				//		this._loadMenu( { "categoryId" :this.options.categoryId , "isCategory" : this.options.isCategory, "naviIndex" : (this.options.naviIndex || 0) } );
				//	}
				//}.bind(this))
				this._loadMenu( { "categoryId" :this.options.categoryId , "isCategory" : this.options.isCategory, "naviIndex" : (this.options.naviIndex || 0) } );
			}
		}else if( this.options.categoryAlias && this.options.categoryAlias != "" ){
			this.restActions.getCategoryByAlias( this.options.categoryAlias, function( json ){
				this.options.categoryId = json.data.id;
				if( this.options.viewId && this.options.viewId!="" ){
					this._loadMenu( { "categoryId" :this.options.categoryId , "viewId" : this.options.viewId } )
				}else{
					this._loadMenu( { "categoryId" :this.options.categoryId , "isCategory" : this.options.isCategory, "naviIndex" : (this.options.naviIndex || 0) } );
				}
			}.bind(this))
		}else{
			this._loadMenu( { "categoryId" :"whole" } )
		}
	},
	_loadMenu : function( options ){
		this.navi = new MWF.xApplication.cms.Module.Navi(this, this.naviNode, this.options.columnData, options );
		this.setNaviSize();
	},
	clearContent: function(){
		//debugger;
		if (this.moduleContent){
			if (this.view) delete this.view;
			this.moduleContent.destroy();
			this.searchNode.empty();
			this.moduleContent = null;
		}
	},
	openView : function(el, categoryData, revealData, searchKey, navi){
		if( revealData && revealData.type == "queryview" ){
			this.loadQueryView(el, categoryData, revealData, searchKey, navi);
		}else{
			this.loadList(el, categoryData, revealData, searchKey, navi);
		}
	},
	loadQueryView : function(el, categoryData, revealData, searchKey, navi){
		MWF.xDesktop.requireApp("cms.Module", "ViewExplorer", function(){
			this.clearContent();
			this.moduleContent = new Element("div", {
				"styles": this.css.moduleContent
			}).inject(this.rightContentNode);
			this.view = new MWF.xApplication.cms.Module.ViewExplorer(
				this.moduleContent,
				this,
				this.options.columnData,
				categoryData,
				revealData,
				{"isAdmin": this.isAdmin, "searchKey" : searchKey },
				this.searchNode
			);
			this.view.load();
		}.bind(this))

	},
	loadList : function(el, categoryData, revealData, searchKey, navi){
		MWF.xDesktop.requireApp("cms.Module", "ListExplorer", function(){
			this.clearContent();
			this.moduleContent = new Element("div", {
				"styles": this.css.moduleContent
			}).inject(this.rightContentNode);
			if (!this.restActions) this.restActions = MWF.Actions.get("x_cms_assemble_control"); //new MWF.xApplication.cms.Module.Actions.RestActions();
			this.view = new MWF.xApplication.cms.Module.ListExplorer(
				this.moduleContent,
				this.restActions,
				this.options.columnData,
				categoryData,
				revealData,
				{"isAdmin": this.isAdmin, "searchKey" : searchKey },
				this.searchNode
			);
			this.view.app = this;
			this.view.load();
		}.bind(this));
	},
	recordStatus: function(){
		var currentObject = this.navi.currentObject;
		if( currentObject ){
			var categoryId = currentObject.getCategoryId();
			if (categoryId){
				return {
					"columnId" : this.options.columnData.id,
					"categoryId" :categoryId,
					"isCategory" : currentObject.isCategory,
					"viewId" : currentObject.data.id
				};
			}else{
				return { "columnId" : this.options.columnData.id , "categoryId" : "whole"}
			}
		}else{
			return { "columnId" : this.options.columnData.id , "categoryId" : "whole" }
		}
	},
	setNaviSize: function(){
		//var titlebarSize = this.titleBar ? this.titleBar.getSize() : {"x":0,"y":0};
		var nodeSize = this.node.getSize();
		//var pt = this.naviContainerNode.getStyle("padding-top").toFloat();
		//var pb = this.naviContainerNode.getStyle("padding-bottom").toFloat();

		//var height = nodeSize.y-pt-pb-titlebarSize.y;
		this.naviContainerNode.setStyle("height", ""+nodeSize.y+"px");
	}
});

MWF.xApplication.cms.Module.Navi = new Class({
	Implements: [Options, Events],
	options : {
		"categoryId" :"" ,
		"viewId" : "",
		"isCategory" : false,
		"navi" : -1
	},
	initialize: function(app, node, columnData, options){
		this.setOptions(options);
		this.app = app;
		this.node = $(node);
		this.columnData = columnData;
		this.categoryList = [];
		this.css = this.app.css;
		this.load();
	},
	load: function(){
		var self = this;
		this.allView = new MWF.xApplication.cms.Module.NaviAllView( this, this.node, {}  );
		new Element("div",{
			"styles" : this.css.viewNaviBottom
		}).inject(this.node);

		this.app.restActions.listCategory( this.columnData.id, function( json ) {
			json.data.each(function (d) {
				var isCurrent = false;
				var category = new MWF.xApplication.cms.Module.NaviCategory(this, this.node,d, {} );
				this.categoryList.push( category );
				this.fireEvent("postLoad");
			}.bind(this))
		}.bind(this))
	}
});

MWF.xApplication.cms.Module.NaviCategory = new Class({
	Implements: [Options, Events],
	options: {
		"style": "default"
	},
	initialize: function ( navi, container, data, options) {
		this.setOptions(options);
		this.navi = navi;
		this.app = navi.app;
		this.container = $(container);
		this.data = data;
		this.css = this.app.css;
		this.load();
	},
	load: function () {
		var _self = this;

		this.isCategory = true;
		this.isCurrent = false;
		this.isExpended = false;
		this.hasSub = false;
		this.naviViewList = [];

		if( this.navi.options.categoryId == this.data.id && this.navi.options.isCategory ){
			this.isCurrent = true;
		}

		this.reveal = this.getRevealData();

		this.node = new Element("div.categoryNaviNode", {
			"styles": this.css.categoryNaviNode
		}).inject(this.container);

		this.expendNode = new Element("div.expendNode").inject(this.node);
		this.setExpendNodeStyle();
		if( this.hasSub ){
			this.expendNode.addEvent( "click" , function(ev){
				this.triggerExpend();
				ev.stopPropagation();
			}.bind(this));
		}

		this.textNode = new Element("div.categoryNaviTextNode",{
			"styles": this.css.categoryNaviTextNode,
			"text": this.data.name //this.defaultRevealData.id == "defaultList" ? this.data.name : this.defaultRevealData.showName
		}).inject(this.node);

		this.node.addEvents({
			"mouseover": function(){ if ( !_self.isCurrent )this.setStyles(_self.app.css.categoryNaviNode_over) },
			"mouseout": function(){ if ( !_self.isCurrent )this.setStyles( _self.app.css.categoryNaviNode ) },
			click : function(){ _self.setCurrent(this);}
		});

		this.listNode = new Element("div.viewNaviListNode",{
			"styles" : this.css.viewNaviListNode
		}).inject(this.container);

		this.loadListContent();
		if( this.isCurrent ){
			this.setCurrent();
		}
	},
	getRevealData: function(){
		var j = this.data.extContent;
		if( j ){
			this.extContent = JSON.parse( j );
		}
		if( !this.extContent || !this.extContent.reveal || this.extContent.reveal.length == 0 ){ //兼容以前的设置
			this.extContent = { reveal : [] };
			this.app.restActions.listViewByCategory( this.data.id, function(json){
				( json.data || [] ).each( function(d){
					var itemData = {
						"type" : "list",
						"name" : d.name,
						"showName" : d.name,
						"id" : d.id,
						"alias" : d.alias,
						"appId" : d.appId,
						"formId" : d.formId,
						"formName" : d.formName
					};
					this.extContent.reveal.push( itemData );
				}.bind(this));
			}.bind(this), null, false );
		}

		this.extContent.reveal.each( function( r ){
			if(this.data.defaultViewName && r.id == this.data.defaultViewName ){
				this.defaultRevealData = r;
			}else{
				this.isExpended = true;
				this.hasSub = true;
			}
		}.bind(this));

		if( !this.extContent || !this.extContent.reveal || this.extContent.reveal.length == 0 ){
			this.extContent = { reveal : [{
				id : "defaultList",
				showName : "系统列表",
				name : "系统列表"
			}] };
		}
		this.revealData = this.extContent.reveal;

		if( !this.defaultRevealData ){
			this.defaultRevealData = {
				id : "defaultList",
				showName : "系统列表",
				name : "系统列表"
			}
		}
	},
	setExpendNodeStyle : function(){
		var style;
		if( this.hasSub ){
			if( this.isExpended ){
				if( this.isCurrent ){
					style = this.css.categoryExpendNode_selected;
				}else{
					style = this.css.categoryExpendNode;
				}
			}else{
				if( this.isCurrent ){
					style = this.css.categoryCollapseNode_selected;
				}else{
					style = this.css.categoryCollapseNode;
				}
			}
		}else{
			style = this.css.emptyExpendNode;
		}
		this.expendNode.setStyles( style );
	},
	triggerExpend : function(){
		if( this.hasSub ){
			if( this.isExpended ){
				this.isExpended = false;
				this.listNode.setStyle("display","none")
			}else{
				this.isExpended = true;
				this.listNode.setStyle("display","")
			}
			this.setExpendNodeStyle();
		}
	},
	setCurrent : function(){
		if( this.navi.currentObject ){
			this.navi.currentObject.cancelCurrent();
		}

		this.node.setStyles( this.css.categoryNaviNode_selected );

		if( this.hasSub ){
			if( this.isExpended ){
				this.expendNode.setStyles( this.css.categoryExpendNode_selected );
			}else{
				this.expendNode.setStyles( this.css.categoryCollapseNode_selected );
			}
		}

		this.isCurrent = true;
		this.navi.currentObject = this;

		var action = this.app.importAction;
		if( action ){
			action.setStyle("display", (this.data.importViewId && this.app.isAdmin) ? "" : "none");
		}
		action = this.app.exportAction;
		if( action ){
			action.setStyle("display", (this.data.importViewId && this.app.isAdmin) ? "" : "none");
		}

		this.loadView();
	},
	cancelCurrent : function(){
		this.isCurrent = false;
		this.node.setStyles( this.css.categoryNaviNode );
		if( this.hasSub ){
			if( this.isExpended ){
				this.expendNode.setStyles( this.css.categoryExpendNode );
			}else{
				this.expendNode.setStyles( this.css.categoryCollapseNode );
			}
		}
	},
	loadView: function( searchkey ){
		this.app.openView( this, this.data, this.viewData || this.defaultRevealData, searchkey || "", this );
	},
	loadListContent : function(){
		this.revealData.each( function( d , i){
			if( d.id != this.defaultRevealData.id ){
				var naviView = new MWF.xApplication.cms.Module.NaviView(this.navi, this, this.listNode, d, {
					"style": this.options.style,
					"index" : i
				});
				this.naviViewList.push( naviView );
			}
		}.bind(this));
		new Element("div", {
			"styles": this.css.viewNaviSepartorNode
		}).inject( this.listNode );
	},
	getCategoryId : function(){
		return this.data.id;
	}
});

MWF.xApplication.cms.Module.NaviView = new Class({
	Implements: [Options, Events],
	options: {
		"style": "default",
		"index" : 0
	},
	initialize: function ( navi, category, container, data, options) {
		this.setOptions(options);
		this.navi = navi;
		this.category = category;
		this.app = navi.app;
		this.data = data;
		this.container = $(container);
		this.css = this.app.css;
		this.load();
	},
	load: function(){
		this.isDefault = this.data.id == "defaultList";
		this.isCurrent = false;
		this.isCategory = false;

		if( this.navi.options.categoryId == this.category.data.id && !this.navi.options.isCategory ){
			if( this.navi.options.viewId == "defaultList" && this.isDefault ){
				this.isCurrent = true;
			}else if( this.navi.options.viewId == this.data.id ){
				this.isCurrent = true;
			}else if( this.navi.options.naviIndex == this.options.index ){
				this.isCurrent = true;
			}
		}

		var _self = this;
		this.node = new Element("div.viewNaviNode", {
			"styles": this.css.viewNaviNode,
			"text" : this.isDefault ? this.app.lp.defaultView : this.data.showName
		}).inject(this.container);

		this.node.addEvents({
			"mouseover": function(){ if (!_self.isCurrent)this.setStyles(_self.css.viewNaviNode_over) },
			"mouseout": function(){ if (!_self.isCurrent)this.setStyles( _self.css.viewNaviNode ) },
			"click": function (el) {
				_self.setCurrent();
			}
		});

		if( this.isCurrent ){
			this.setCurrent()
		}
	},
	setCurrent : function(){
		if( this.navi.currentObject ){
			this.navi.currentObject.cancelCurrent();
		}

		this.node.setStyles( this.css.viewNaviNode_selected );

		this.isCurrent = true;
		this.navi.currentObject = this;

		var action = this.app.importAction;
		if( action ){
			action.setStyle("display", (this.category.data.importViewId && this.app.isAdmin) ? "" : "none");
		}
		action = this.app.exportAction;
		if( action ){
			action.setStyle("display", (this.category.data.importViewId && this.app.isAdmin) ? "" : "none");
		}

		this.loadView();
	},
	cancelCurrent : function(){
		this.isCurrent = false;
		this.node.setStyles( this.css.viewNaviNode );
	},
	getCategoryId : function(){
		return this.category.data.id;
	},
	loadView : function( searchKey ){
		this.app.openView( this, this.category.data, this.data, searchKey || "", this );
	}
});

MWF.xApplication.cms.Module.NaviAllView = new Class({
	Implements: [Options, Events],
	options: {
		"style": "default"
	},
	initialize: function ( navi, container, options) {
		this.setOptions(options);
		this.navi = navi;
		this.app = navi.app;
		this.container = $(container);
		this.css = this.app.css;
		this.data = {
			"isAll" : true,
			"id" : "defaultList"
		};
		this.load();
	},
	load: function(){
		var _self = this;
		this.isDefault = true;
		this.isAll = true;
		this.isCurrent = false;
		this.isCategory = false;

		if( this.navi.options.categoryId == "whole" ){
			this.isCurrent = true;
		}

		this.listNode  = new Element("div.viewNaviListNode_all",{
			"styles" : this.css.viewNaviListNode_all
		}).inject(this.container);

		this.node = new Element("div.viewNaviNode_all", {
			"styles": this.css.viewNaviNode_all,
			"text" : this.app.lp.allDocument
		}).inject(this.listNode);

		this.node.addEvents({
			"mouseover": function(){ if ( !_self.isCurrent )this.setStyles(_self.css.viewNaviNode_all_over) },
			"mouseout": function(){ if ( !_self.isCurrent )this.setStyles( _self.css.viewNaviNode_all ) },
			"click": function (el) {
				_self.setCurrent();
			}
		});

		new Element("div", {
			"styles": this.css.viewNaviSepartorNode
		}).inject(this.listNode);

		if( this.isCurrent ){
			this.setCurrent()
		}
	},
	setCurrent : function(){

		if( this.navi.currentObject ){
			this.navi.currentObject.cancelCurrent();
		}

		this.node.setStyles( this.css.viewNaviNode_all_selected );

		this.isCurrent = true;
		this.navi.currentObject = this;

		var action = this.app.importAction;
		if( action ){
			action.setStyle("display","none");
		}
		var action = this.app.exportAction;
		if( action ){
			action.setStyle("display","none");
		}

		this.loadView();
	},
	cancelCurrent : function(){
		this.isCurrent = false;
		this.node.setStyles( this.css.viewNaviNode_all );
	},
	getCategoryId : function(){
		return null;
	},
	loadView : function( searchKey ){
		this.app.openView( this, null, this.data, searchKey || "", this );
	}
});

