MWF.xApplication.cms = MWF.xApplication.cms || {};
MWF.CMSE = MWF.xApplication.cms.Module = MWF.xApplication.cms.Module ||{};
MWF.require("MWF.widget.Identity", null,false);
MWF.xDesktop.requireApp("cms.Module", "Actions.RestActions", null, false);
MWF.xApplication.cms.Module.options = {
	multitask: false,
	executable: true
}
MWF.xApplication.cms.Module.Main = new Class({
	Extends: MWF.xApplication.Common.Main,
	Implements: [Options, Events],

	options: {
		"style": "default",
		"name": "cms.Module",
		"icon": "icon.png",
		"width": "1200",
		"height": "700",
		"isResize": false,
		"isMax": true,
		"isCategory" : false,
		"searchKey" : "",
		"title": MWF.xApplication.cms.Module.LP.title
	},
	onQueryLoad: function(){
		this.lp = MWF.xApplication.cms.Module.LP;
	},
	loadApplication: function(callback){
		this.controllers = [];
		this.isAdmin = false;
		this.restActions = new MWF.xApplication.cms.Module.Actions.RestActions();
		this.createNode();
		this.loadApplicationContent();
	},
	createNode: function(){
		this.content.setStyle("overflow", "hidden");
		this.node = new Element("div", {
			"styles": this.css.node
		}).inject(this.content);
	},
	loadApplicationContent: function(){
		if( this.options.columnData ){
			this.setTitle(this.options.columnData.appName);
			this.loadController(function(){
				this.loadTitle();
				this.loadMenu();
			}.bind(this))
		}else if( this.status && this.status.columnId ){
			this.loadColumnData( this.status.columnId, function(){
				this.loadController(function(){
					this.loadTitle();
					this.loadMenu();
				}.bind(this))
			}.bind(this))
		}
	},
	loadColumnData : function(columnId, callback){
		this.restActions.getColumn( {"id":columnId }, function( json ){
			this.options.columnData = json.data;
			this.setTitle(this.options.columnData.appName);
			if(callback)callback()
		}.bind(this))
	},
	loadController: function(callback){
		this.restActions.listColumnController(this.options.columnData.id, function( json ){
			json.data = json.data || [];
			json.data.each(function(item){
				this.controllers.push(item.adminUid)
			}.bind(this))
			this.isAdmin = MWF.AC.isAdministrator() || this.controllers.contains(layout.desktop.session.user.name);
			if(callback)callback(json);
		}.bind(this));
	},
	loadTitle : function(){
		this.loadTitleBar();
		this.loadCreateDocumentActionNode();
		this.loadTitleIconNode();
		this.loadTitleTextNode();
		this.loadRefreshNode();
		this.loadSearchNode();
	},
	loadTitleBar: function(){
		this.titleBar = new Element("div", {
			"styles": this.css.titleBar
		}).inject(this.node);
	},
	loadCreateDocumentActionNode: function() {
		this.createDocumentAction = new Element("div", {
			"styles": this.css.createDocumentAction
		}).inject(this.titleBar);
		this.createDocumentAction.addEvents({
			"click": function(e){
				if( this.creater ){
					this.creater.load();
				}else{
					MWF.xDesktop.requireApp("cms.Index", "Creater", function(){
						this.creater = new MWF.xApplication.cms.Index.Creater(this,this.options.columnData,this.view );
						this.creater.load();
					}.bind(this));
				}
			}.bind(this)
		});
	},
	loadTitleIconNode : function(){

		this.defaultColumnIcon = "/x_component_cms_Index/$Main/"+this.options.style+"/icon/column.png";

		var iconAreaNode = this.iconAreaNode = new Element("div",{
			"styles" : this.css.titleIconAreaNode
		}).inject(this.titleBar);

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
	loadTitleTextNode: function(){
		this.titleTextNode = new Element("div", {
			"styles": this.css.titleTextNode,
			"text": this.options.columnData.appName
		}).inject(this.titleBar);
	},
	loadSearchNode: function(){
		this.searchBarAreaNode = new Element("div", {
			"styles": this.css.searchBarAreaNode
		}).inject(this.titleBar);

		this.searchBarNode = new Element("div", {
			"styles": this.css.searchBarNode
		}).inject(this.searchBarAreaNode);

		this.searchBarActionNode = new Element("div", {
			"styles": this.css.searchBarActionNode
		}).inject(this.searchBarNode);
		this.searchBarResetActionNode = new Element("div", {
			"styles": this.css.searchBarResetActionNode
		}).inject(this.searchBarNode);
		this.searchBarResetActionNode.setStyle("display","none");

		this.searchBarInputBoxNode = new Element("div", {
			"styles": this.css.searchBarInputBoxNode
		}).inject(this.searchBarNode);
		this.searchBarInputNode = new Element("input", {
			"type": "text",
			"value": this.options.searchKey!="" ? this.options.searchKey : this.lp.searchKey,
			"styles": this.css.searchBarInputNode
		}).inject(this.searchBarInputBoxNode);

		var _self = this;
		this.searchBarActionNode.addEvent("click", function(){
			this.search();
		}.bind(this));
		this.searchBarResetActionNode.addEvent("click", function(){
			this.reset();
		}.bind(this));
		this.searchBarInputNode.addEvents({
			"focus": function(){
				if (this.value==_self.lp.searchKey) this.set("value", "");
			},
			"blur": function(){if (!this.value) this.set("value", _self.lp.searchKey);},
			"keydown": function(e){
				if (e.code==13){
					this.search();
					e.preventDefault();
				}
			}.bind(this),
			"selectstart": function(e){
				e.preventDefault();
			}
		});
	},
	loadRefreshNode : function(){
		this.refreshAreaNode = new Element("div", {
			"styles": this.css.refreshAreaNode
		}).inject(this.titleBar);

		this.refreshActionNode = new Element("div", {
			"styles": this.css.refreshActionNode,
			"title" : this.lp.refresh
		}).inject(this.refreshAreaNode);
		this.refreshActionNode.addEvent("click", function(){
			this.reloadView();
		}.bind(this));
	},
	loadMenu: function(callback){
		this.naviContainerNode = new Element("div.naviContainerNode", {
			"styles": this.css.naviContainerNode
		}).inject(this.node);

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
		if( this.status && this.status.categoryId ){
			this._loadMenu( this.status );
		}else if( this.options.categoryId && this.options.categoryId != "" ){
			if( this.options.viewId && this.options.viewId!="" ){
				this._loadMenu( { "categoryId" :this.options.categoryId , "viewId" : this.options.viewId } )
			}else{
				this.getCategoryDefaultView(this.options.categoryId , function(viewId){
					if( viewId ){
						this._loadMenu( { "categoryId" :this.options.categoryId , "viewId" : viewId, "isCategory" : this.options.isCategory } );
					}else{
						this._loadMenu( { "categoryId" :this.options.categoryId , "isCategory" : this.options.isCategory, "naviIndex" : (this.options.naviIndex || 0) } );
					}
				}.bind(this))
			}
		}else{
			this._loadMenu( { "categoryId" :"all" } )
		}
	},
	reloadView : function(){
		this.reset();
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
			this.moduleContent = null;
		}
	},
	openView : function(el, categoryData, viewData, searchKey, navi){
		if( (!searchKey || searchKey !="") && this.options.searchKey != "" ){
			searchKey = this.options.searchKey;
			//if(el)el.setStyles( this.css.viewNaviNode );
			//this.currentViewNaviNode = el;
			//if(navi)navi.currentViewNaviNode = null;
			this.options.searchKey = "";
		}
		MWF.xDesktop.requireApp("cms.Module", "ViewExplorer", function(){
			this.clearContent();
			this.moduleContent = new Element("div", {
				"styles": this.css.rightContentNode
			}).inject(this.node);
			if (!this.restActions) this.restActions = new MWF.xApplication.cms.Module.Actions.RestActions();
			this.view = new MWF.xApplication.cms.Module.ViewExplorer(
				this.moduleContent,
				this.restActions,
				this.options.columnData,
				categoryData,
				viewData,
				{"isAdmin": this.isAdmin, "searchKey" : searchKey }
			);
			this.view.app = this;
			this.view.load();
			if( !searchKey || searchKey==""  ){
				this.searchBarResetActionNode.setStyle("display","none");
				this.searchBarActionNode.setStyle("display","block");
				this.searchBarInputNode.set("value",this.lp.searchKey);
			}
		}.bind(this));
	},
	getCategoryDefaultView : function(categoryId, callback){
		MWF.UD.getDataJson("cms_defaultView_" + categoryId, function( data ){
			if(callback)callback( data ? data.id : null );
		}.bind(this))
	},
	setCategoryDefaultView : function(categoryId, viewId){
		MWF.UD.putData("cms_defaultView_" + categoryId , { "id":viewId }, function(){
			this.app.notice(this.app.lp.setDefaultSuccess, "success");
		}.bind(this))
	},
	search : function( key ){
		if(!key)key = this.searchBarInputNode.get("value");
		if(key==this.lp.searchKey)key="";
		if( key!="" ){
			this.searchBarResetActionNode.setStyle("display","block");
			this.searchBarActionNode.setStyle("display","none");
		}

		if(this.navi.currentViewNaviNode){
			//this.navi.currentViewNaviNode.setStyles( this.css.viewNaviNode );
			//this.currentViewNaviNode = this.navi.currentViewNaviNode
			var viewNaviNode = this.navi.currentViewNaviNode;
			//this.navi.currentViewNaviNode = null;
			var viewData = viewNaviNode.retrieve("viewData");
			var categoryId = viewNaviNode.retrieve("categoryId");
			if( viewData.content && typeof(viewData.content)=="string"){
				viewData.content = JSON.parse(viewData.content);
			}
			this.openView( viewNaviNode, this.navi.categorys[categoryId].data, viewData , key);
		}

	},
	reset : function(){
		this.searchBarInputNode.set("value",this.lp.searchKey);
		this.searchBarResetActionNode.setStyle("display","none");
		this.searchBarActionNode.setStyle("display","block");

		if(this.navi.currentViewNaviNode){
			var viewNaviNode = this.navi.currentViewNaviNode;
		}else{
			var viewNaviNode = this.navi.categorys.all.views.default.node;
		}
		this.navi.setCurrentViewNode( viewNaviNode );
		this.currentViewNaviNode = null;

		//var viewData = viewNaviNode.retrieve("viewData");
		//var categoryId = viewNaviNode.retrieve("categoryId");
		//if( viewData.content && typeof(viewData.content)=="string"){
		//	viewData.content = JSON.parse(viewData.content);
		//}
		//this.openView( viewNaviNode, this.navi.categorys[categoryId].data, viewData )

	},
	recordStatus: function(){
		var viewNaviNode = this.navi.currentViewNaviNode;
		if( viewNaviNode ){
			var viewData = viewNaviNode.retrieve("viewData");
			var categoryId = viewNaviNode.retrieve("categoryId");
			var isCategory = viewNaviNode.retrieve("isCategory");
			if (categoryId){
				return {
					"columnId" : this.options.columnData.id,
					"categoryId" :categoryId,
					"isCategory" : isCategory,
					"viewId" : viewData.id ? viewData.id : "default"
				};
			}else{
				return { "columnId" : this.options.columnData.id }
			}
		}else{
			return { "columnId" : this.options.columnData.id }
		}
	},
	setNaviSize: function(){
		var titlebarSize = this.titleBar ? this.titleBar.getSize() : {"x":0,"y":0};
		var nodeSize = this.node.getSize();
		var pt = this.naviContainerNode.getStyle("padding-top").toFloat();
		var pb = this.naviContainerNode.getStyle("padding-bottom").toFloat();

		var height = nodeSize.y-pt-pb-titlebarSize.y;
		this.naviContainerNode.setStyle("height", ""+height+"px");
	}
});

MWF.xApplication.cms.Module.Navi = new Class({
	Implements: [Options, Events],
	options : {
		"categoryId" :"" ,
		"viewId" : "",
		"isCategory" : false,
		"navi" : -1,
	},
	initialize: function(app, node, columnData, options){
		this.setOptions(options);
		this.app = app;
		this.node = $(node);
		this.columnData = columnData;
		this.categorys = {};
		this.load();
	},
	load: function(){
		var self = this;
		this.loadAllDocNaviNode();
		new Element("div",{
			"styles" : this.app.css.viewNaviBottom
		}).inject(this.node);

		this.app.restActions.listCategory( this.columnData.id, function( json ){
			json.data.each(function(categroyData){

				var categoryNaviNode = new Element("div.categoryNaviNode", {
					"styles": this.app.css.categoryNaviNode,
					"text": categroyData.name
				}).inject(this.node);

				this.categorys[categroyData.id] = {};
				this.categorys[categroyData.id].data = categroyData;
				this.categorys[categroyData.id].node = categoryNaviNode;
				this.categorys[categroyData.id].views = {};

				categoryNaviNode.store( "categoryId" , categroyData.id );
				categoryNaviNode.store( "isCategory" , true );
				categoryNaviNode.addEvents({
					"mouseover": function(){ if (self.currentViewNaviNode!=this)this.setStyles(self.app.css.categoryNaviNode_over) },
					"mouseout": function(){ if (self.currentViewNaviNode!=this)this.setStyles( self.app.css.categoryNaviNode ) },
					click : function(){self.setCurrentViewNode(this);}
				});
				if( !categroyData.defaultViewName || categroyData.defaultViewName == "default" || categroyData.defaultViewName == ""){
					categoryNaviNode.store( "viewData" , {"isDefault":true} );
					if( this.options.categoryId == categroyData.id && this.options.isCategory ){
						this.setCurrentViewNode( categoryNaviNode );
					}
				}else{
					this.app.restActions.getView( categroyData.defaultViewName, function(json){
						categoryNaviNode.store( "viewData" , json.data );
						if( this.options.categoryId == categroyData.id && this.options.isCategory ){
							this.setCurrentViewNode( categoryNaviNode );
						}
					}.bind(this));
				}

				var viewNaviListNode = new Element("div.viewNaviListNode",{
					"styles" : this.app.css.viewNaviListNode
				}).inject(this.node);

				//this.app.restActions.listCategoryViewByCatagory( categroyData.id, function (data) {
				//	var index = 0
				//	for(var i=0;i<data.data.length;i++){
				//		if(data.data[i].viewId == "default" ){
				//			this.createViewNaviNode(viewNaviListNode, {"isDefault":true}, categroyData.id, index++ );
				//			break;
				//		}
				//	}
				//	this.app.restActions.listViewByCategory( categroyData.id, function (d) {
				//		d.data.each(function(viewData ){
				//			this.createViewNaviNode(viewNaviListNode, viewData, categroyData.id, index++ );
				//		}.bind(this));
				//		new Element("div", {
				//			"styles": this.app.css.viewNaviSepartorNode
				//		}).inject(viewNaviListNode);
				//	}.bind(this));
				//}.bind(this))

				var index = 0;
				this.app.restActions.listViewByCategory( categroyData.id, function (d) {
					//this.createViewNaviNode(viewNaviListNode, {"isDefault":true}, categroyData.id, index++ );
					//this.createViewNaviNode(viewNaviListNode, {"isDefault":true}, categroyData.id, index++ );
					//this.createViewNaviNode(viewNaviListNode, {"isDefault":true}, categroyData.id, index++ );
					d.data.each(function(viewData ){
						this.createViewNaviNode(viewNaviListNode, viewData, categroyData.id, index++ );
					}.bind(this));
					new Element("div", {
						"styles": this.app.css.viewNaviSepartorNode
					}).inject(viewNaviListNode);

				}.bind(this));

			}.bind(this))
			this.fireEvent("postLoad");
		}.bind(this),function(){
			this.fireEvent("postLoad");
		}.bind(this), true)

	},
	loadAllDocNaviNode : function(){
		var _self = this;


		this.categorys.all = {}
		this.categorys.all.data = {"isAll":true}
		this.categorys.all.views = {};

		var viewNaviListNode = this.viewNaviListNode_all  = new Element("div.viewNaviListNode_all",{
			"styles" : this.app.css.viewNaviListNode_all
		}).inject(this.node);
		var viewNaviNode = this.viewNaviNode_all = new Element("div.viewNaviNode_all", {
			"styles": this.app.css.viewNaviNode_all,
			"text" : this.app.lp.allDocument //+ this.columnData.appName
		}).inject(viewNaviListNode);
		var viewData = {
			"isDefault" : true,
			"isAll" : true
		}
		viewNaviNode.store("isAll",true);
		viewNaviNode.store("viewData",viewData);
		viewNaviNode.store("categoryId","all");
		var view = this.categorys.all.views.default = {};
		view.data = viewData;
		view.node = viewNaviNode;

		viewNaviNode.addEvents({
			"mouseover": function(){ if (_self.currentViewNaviNode!=this)this.setStyles(_self.app.css.viewNaviNode_all_over) },
			"mouseout": function(){ if (_self.currentViewNaviNode!=this)this.setStyles( _self.app.css.viewNaviNode_all ) },
			"click": function (el) {
				_self.setCurrentViewNode(this);
			}
		})

		new Element("div", {
			"styles": this.app.css.viewNaviSepartorNode
		}).inject(viewNaviListNode);

		if( this.options.categoryId == "all" ){
			this.setCurrentViewNode(viewNaviNode)
		}

	},
	createViewNaviNode : function(viewNaviListNode, viewData, categoryId,index){
		var _self = this;
		var viewNaviNode = new Element("div.viewNaviNode", {
			"styles": this.app.css.viewNaviNode,
			"text" : viewData.isDefault ? this.app.lp.defaultView : viewData.name
		}).inject(viewNaviListNode);
		viewNaviNode.store("viewData",viewData);
		viewNaviNode.store("categoryId",categoryId);

		var key = viewData.isDefault ? "default" : viewData.id;
		var view = this.categorys[categoryId].views[ key ] = {};
		view.data = viewData;
		view.node = viewNaviNode;

		viewNaviNode.addEvents({
			"mouseover": function(){ if (_self.currentViewNaviNode!=this)this.setStyles(_self.app.css.viewNaviNode_over) },
			"mouseout": function(){ if (_self.currentViewNaviNode!=this)this.setStyles( _self.app.css.viewNaviNode ) },
			"click": function (el) {
				_self.setCurrentViewNode(this);
			}
		})

		if( this.options.categoryId == categoryId && !this.options.isCategory ){
			if( this.options.viewId == "default" && viewData.isDefault ){
				this.setCurrentViewNode(viewNaviNode);
			}else if( this.options.viewId == viewData.id ){
				this.setCurrentViewNode(viewNaviNode);
			}else if( this.options.naviIndex == index ){
				this.setCurrentViewNode(viewNaviNode);
			}
		}

	},
	setCurrentViewNode : function( viewNaviNode ){
		if(this.currentViewNaviNode){
			if( this.currentViewNaviNode.retrieve("isAll")  ){
				this.currentViewNaviNode.setStyles( this.app.css.viewNaviNode_all );
			}else if( this.currentViewNaviNode.retrieve("isCategory") ){
				this.currentViewNaviNode.setStyles( this.app.css.categoryNaviNode );
			}else{
				this.currentViewNaviNode.setStyles( this.app.css.viewNaviNode );
			}
		}
		if( viewNaviNode.retrieve("isAll")  ){
			viewNaviNode.setStyles( this.app.css.viewNaviNode_all_selected );
		}else if( viewNaviNode.retrieve("isCategory") ){
			viewNaviNode.setStyles( this.app.css.categoryNaviNode_selected );
		}else{
			viewNaviNode.setStyles( this.app.css.viewNaviNode_selected );
		}
		this.currentViewNaviNode = viewNaviNode;
		var viewData = viewNaviNode.retrieve("viewData");
		var categoryId = viewNaviNode.retrieve("categoryId");
		if( viewData.content && typeof(viewData.content)=="string"){
			viewData.content = JSON.parse(viewData.content);
		}
		this.app.openView( viewNaviNode, this.categorys[categoryId].data, viewData, "", this );
	}
})


