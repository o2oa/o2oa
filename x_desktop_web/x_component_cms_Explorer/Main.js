MWF.xApplication.cms = MWF.xApplication.cms || {};
MWF.CMSE = MWF.xApplication.cms.Explorer = MWF.xApplication.cms.Explorer ||{};
MWF.require("MWF.widget.Identity", null,false);
MWF.xDesktop.requireApp("cms.Explorer", "Actions.RestActions", null, false);
MWF.xApplication.cms.Explorer.options = {
	multitask: false,
	executable: true
}
MWF.xApplication.cms.Explorer.Main = new Class({
	Extends: MWF.xApplication.Common.Main,
	Implements: [Options, Events],

	options: {
		"style": "default",
		"name": "cms.Explorer",
		"icon": "icon.png",
		"width": "1200",
		"height": "700",
		"isResize": false,
		"isMax": true,
		"title": MWF.CMSE.LP.title
	},
	onQueryLoad: function(){
		this.lp = MWF.CMSE.LP;
	},
	loadApplication: function(callback){
		this.restActions = new MWF.xApplication.cms.Explorer.Actions.RestActions();
		this.createNode();
		this.loadApplicationContent();
	},
	createNode: function(){
		this.content.setStyle("overflow", "hidden");
		this.node = new Element("div", {
			"styles": {"width": "100%", "height": "100%", "overflow": "hidden"}
		}).inject(this.content);
	},
	loadApplicationContent: function(){
		this.loadDefaultMenu();
		//this.loadApplicationLayout();
	},
	loadDefaultMenu: function(callback){
		this.defaultMenuNode = new Element("div", {
			"styles": this.css.defaultMenuNode
		}).inject(this.node);


		this.menu = new MWF.xApplication.cms.Explorer.Menu(this, this.defaultMenuNode, {
			"onPostLoad": function(){
				if (this.status){
					if (this.status.navi!=null){
						if( this.status.navi.type == "default" ) {
							this.menu.doAction(this.menu.items[this.status.navi.id]);
						}else if(this.status.navi.type == "column" ) {
							this.menu.doColumnAction(this.menu.items[this.status.navi.id]);
						}else{
							this.menu.doColumnAction(this.menu.items[this.status.navi.columnId], function(){
								this.menu.doCategoryAction(this.menu.items[this.status.navi.id], this.status.view );
							}.bind(this));
						}
					}else{

						if (this.menu.status == "start"){
							this.menu.toNormal();
							this.menu.status = "normal";
						}
						//this.menu.doAction(this.menu.navis[0]);
					}
				}else{
					if (this.menu.status == "start"){
						this.menu.toNormal();
						this.menu.status = "normal";
					}
					//this.menu.doAction(this.menu.navis[0]);
				}
			}.bind(this)
		});
		this.addEvent("resize", function(){
			if (this.menu) this.menu.onResize();
		}.bind(this));
	},
	clearContent: function(){
		//debugger;
		if (this.draftContent){
			if (this.draftExplorer) delete this.draftExplorer;
			this.draftContent.destroy();
			this.draftContent = null;
		}
		if (this.explorerContent){
			if (this.explorer) delete this.explorer;
			this.explorerContent.destroy();
			this.explorerContent = null;
		}
	},
	openDraft : function(){
		this.clearContent();
		this.draftContent = new Element("div", {
			"styles": this.css.rightContentNode
		}).inject(this.node);
		MWF.xDesktop.requireApp("cms.Explorer", "DraftExplorer", function(){
			//MWF.xDesktop.requireApp("cms.Explorer", "Actions.RestActions", function(){
				if (!this.restActions) this.restActions = new MWF.xApplication.cms.Explorer.Actions.RestActions();
				this.draftExplorer = new MWF.xApplication.cms.Explorer.DraftExplorer(this.draftContent, this.restActions);
				this.draftExplorer.app = this;
				this.draftExplorer.load();
			//}.bind(this));
		}.bind(this));
	},
	openRecycleBin : function(){
		alert("openRecycleBin")
	},
	openManager : function(){
		var appId = "cms.Column";
		if (this.desktop.apps[appId]){
			this.desktop.apps[appId].setCurrent();
		}else {
			this.desktop.openApplication(null, "cms.Column", {
				"appId": appId,
				"onQueryLoad": function(){
				}
			});
		}
	},
	openCategory : function(columnData, categoryData, view){
		MWF.xDesktop.requireApp("cms.Explorer", "ViewExplorer", function(){
			this.clearContent();
			this.explorerContent = new Element("div", {
				"styles": this.css.rightContentNode
			}).inject(this.node);
			if (!this.restActions) this.restActions = new MWF.xApplication.cms.Explorer.Actions.RestActions();
			this.explorer = new MWF.xApplication.cms.Explorer.ViewExplorer(this.explorerContent, this.restActions, columnData, categoryData, view ? {"viewId":view } : null );
			this.explorer.app = this;
			this.explorer.load();
		}.bind(this));
	},
	recordStatus: function(){
		if (this.menu.currentNavi){
			var naviType = this.menu.currentNavi.retrieve("type");
			var naviData = this.menu.currentNavi.retrieve("naviData");
			return {
				"navi" :{ "type": naviType, "id": naviData.id, "columnId":naviData.appId},
				"view" : this.explorer.currentViewData.id ? this.explorer.currentViewData.id : "default"
			};
		}
	}
});

MWF.xApplication.cms.Explorer.Menu = new Class({
	Implements: [Options, Events],

	initialize: function(app, node, options){
		this.setOptions(options);
		this.app = app;
		this.node = $(node);
		this.currentNavi = null;
		this.status = "start";
		this.navis = [];
		this.columns = {};
		this.items = {};
		this.load();
	},
	load: function(){
		var menuUrl = this.app.path+"defaultMenu.json";
		MWF.getJSON(menuUrl, function(json){
			json.each(function(navi){
				if( navi.access && navi.access == "admin" ){
					if( MWF.AC.isAdministrator() )this.createDefaultNaviNode(navi);
				}else{
					this.createDefaultNaviNode(navi);
				}
			}.bind(this));

			this.app.restActions.listColumn( function( columns ){
				if( columns.data ){
					columns.data.each(function(column){
						if(!column.name)column.name = column.appName;
						this.createColumnNaviNode(column);
					}.bind(this))
				}
				this.setDefaultMenuWidth();
				this.fireEvent("postLoad");
			}.bind(this),function(){
				this.setDefaultMenuWidth();
				this.fireEvent("postLoad");
			}.bind(this), true)

			//this.setDefaultMenuWidth();
			//this.fireEvent("postLoad");

		}.bind(this));
	},
	createDefaultNaviNode :function(navi){
		var naviNode = new Element("div", {
			"styles": this.app.css.defaultMenuNaviNode
		});
		naviNode.store("naviData", navi);
		naviNode.store("type", "default");

		var iconNode =  new Element("div", {
			"styles": this.app.css.defaultMenuIconNode
		}).inject(naviNode);
		iconNode.setStyle("background-image", "url("+this.app.path+this.app.options.style+"/icon/"+navi.icon+")");

		var textNode =  new Element("div", {
			"styles": this.app.css.defaultMenuTextNode,
			"text": navi.title
		});
		textNode.inject(naviNode);
		naviNode.inject(this.node);

		this.navis.push(naviNode);

		this.items[navi.id] = naviNode;

		this.setDefaultNaviEvent(naviNode, navi);

		this.setNodeCenter(this.node);
	},
	createColumnNaviNode:function(column){

		var columnObj = this.columns[column.id] = this.columns[column.id] || {};

		var naviNode = columnObj.node = new Element("div", {
			"styles": this.app.css.columnMenuNaviNode
		});
		naviNode.store("naviData", column);
		naviNode.store("type", "column");

		var iconNode =  new Element("div", {
			"styles": this.app.css.columnMenuIconNode
		}).inject(naviNode);

		if( column.appIcon && column.appIcon!="" ){
			var imgNode = new Element("img",{
				"src" : "data:image/png;base64,"+column.appIcon
			}).inject( iconNode )
			imgNode.setStyles(this.app.css.columnMenuImgNode);
			//iconNode.setStyle("background-image", "url(data:image/png;base64,"+column.appIcon+")");
		}else{
			iconNode.setStyle("background-image", "url("+this.app.path+this.app.options.style+"/icon/column24.png)");
		}

		var textNode =  new Element("div", {
			"styles": this.app.css.columnMenuTextNode,
			"text": column.name
		});
		textNode.inject(naviNode);
		naviNode.inject(this.node);

		this.navis.push(naviNode);
		this.items[column.id] = naviNode;

		this.setColumnNaviEvent(naviNode);

		this.setNodeCenter(this.node);
	},
	createCategoryNaviNode:function(category,columnId,columnNode){

		var categorys = this.columns[columnId].categoryNodes = this.columns[columnId].categoryNodes || [];

		var naviNode = new Element("div", {
			"styles": this.app.css.categoryMenuNaviNode
		});
		naviNode.setStyle("display","block");

		categorys.push(naviNode);
		naviNode.store("naviData", category);
		naviNode.store("type", "category");

		var iconNode =  new Element("div", {
			"styles": this.app.css.categoryMenuIconNode
		}).inject(naviNode);

		var textNode =  new Element("div", {
			"styles": this.app.css.categoryMenuTextNode,
			"text": category.name
		});
		textNode.inject(naviNode);

		naviNode.inject(columnNode,'after');

		this.navis.push(naviNode);
		this.items[category.id] = naviNode;

		this.setCategoryNaviEvent(naviNode);

		this.setNodeCenter(this.node);
	},
	setDefaultNaviEvent: function(naviNode){
		var _self = this;
		naviNode.addEvents({
			"mouseover": function(){ if (_self.currentNavi!=this) this.setStyles(_self.app.css.defaultMenuNaviNode_over);},
			"mouseout": function(){if (_self.currentNavi!=this) this.setStyles(_self.app.css.defaultMenuNaviNode);},
			"mousedown": function(){if (_self.currentNavi!=this) this.setStyles(_self.app.css.defaultMenuNaviNode_down);},
			"mouseup": function(){if (_self.currentNavi!=this) this.setStyles(_self.app.css.defaultMenuNaviNode_over);},
			"click": function(){
				//if (_self.currentNavi!=this) _self.doAction.apply(_self, [this]);
				_self.doAction.apply(_self, [this]);
			}
		});
	},
	setColumnNaviEvent: function(naviNode){
		var _self = this;
		naviNode.addEvents({
			"mouseover": function(){ if (_self.currentNavi!=this) this.setStyles(_self.app.css.columnMenuNaviNode_over);},
			"mouseout": function(){if (_self.currentNavi!=this) this.setStyles(_self.app.css.columnMenuNaviNode);},
			"mousedown": function(){if (_self.currentNavi!=this) this.setStyles(_self.app.css.columnMenuNaviNode_down);},
			"mouseup": function(){if (_self.currentNavi!=this) this.setStyles(_self.app.css.columnMenuNaviNode_over);},
			"click": function(){
				//if (_self.currentNavi!=this) _self.doAction.apply(_self, [this]);
				_self.doColumnAction.apply(_self, [this]);
			}
		});
	},
	setCategoryNaviEvent: function(naviNode){
		var _self = this;
		naviNode.addEvents({
			"mouseover": function(){ if (_self.currentNavi!=this) this.setStyles(_self.app.css.categoryMenuNaviNode_over);},
			"mouseout": function(){if (_self.currentNavi!=this) this.setStyles(_self.app.css.categoryMenuNaviNode);},
			"mousedown": function(){if (_self.currentNavi!=this) this.setStyles(_self.app.css.categoryMenuNaviNode_down);},
			"mouseup": function(){if (_self.currentNavi!=this) this.setStyles(_self.app.css.categoryMenuNaviNode_over);},
			"click": function(){
				//if (_self.currentNavi!=this) _self.doAction.apply(_self, [this]);
				_self.doCategoryAction.apply(_self, [this]);
			}
		});
	},
	doAction: function(naviNode) {
		this.closeCurrentColumn();

		var navi = naviNode.retrieve("naviData");
		var action = navi.action;

		var type = naviNode.retrieve("type");
		if (!navi.target || navi.target != "_blank") {
			if (this.currentNavi) this.currentNavi.setStyles((this.currentNavi.retrieve("type") != "column") ? this.app.css.defaultMenuNaviNode : this.app.css.columnMenuNaviNode);
			naviNode.setStyles((type != "column") ? this.app.css.defaultMenuNaviNode_current : this.app.css.columnMenuNaviNode_current);
			this.currentNavi = naviNode;
		}

		if (this.app[action]) this.app[action].apply(this.app);

		if (this.status == "start"){
			this.toNormal();
			this.status = "normal";
		}
	},
	doColumnAction: function(naviNode, callback ) {
		var navi = naviNode.retrieve("naviData");
		var action = navi.action;

		this.closeCurrentColumn();
		if( this.columns[navi.id].categoryNodes ) {
			this.columns[navi.id].categoryNodes.each( function(categoryNod){
				categoryNod.setStyle("display","block");
			})
		}else{
			this.app.restActions.listCategory( navi.id,  function( categorys ){
				categorys.data.reverse().each(function(category){
					this.createCategoryNaviNode( category, navi.id , naviNode );
				}.bind(this))
				if( callback )callback();
			}.bind(this))

		}

		this.currentColumn = naviNode;

		var type = naviNode.retrieve("type");
		if (!navi.target || navi.target != "_blank") {
			if (this.currentNavi) this.currentNavi.setStyles((this.currentNavi.retrieve("type") != "column") ? this.app.css.defaultMenuNaviNode : this.app.css.columnMenuNaviNode);
			naviNode.setStyles((type != "column") ? this.app.css.defaultMenuNaviNode_current : this.app.css.columnMenuNaviNode_current);
			this.currentNavi = naviNode;
		}

		if (this.status == "start"){
			this.toNormal();
			this.status = "normal";
		}
	},
	doCategoryAction: function(naviNode, view ) {

		var navi = naviNode.retrieve("naviData");
		var action = navi.action;

		var type = naviNode.retrieve("type");
		if (!navi.target || navi.target != "_blank") {
			if (this.currentNavi) this.currentNavi.setStyles((this.currentNavi.retrieve("type") != "column") ? this.app.css.defaultMenuNaviNode : this.app.css.columnMenuNaviNode);
			naviNode.setStyles((type != "column") ? this.app.css.defaultMenuNaviNode_current : this.app.css.columnMenuNaviNode_current);
			this.currentNavi = naviNode;
		}

		this.app.openCategory.call(this.app, this.currentColumn.retrieve("naviData"), navi, view );

		if (this.status == "start"){
			this.toNormal();
			this.status = "normal";
		}
	},
	closeCurrentColumn:function(){
		if( this.currentColumn ) {
			var curNavi = this.currentColumn.retrieve("naviData");
			if (this.columns[curNavi.id].categoryNodes) {
				this.columns[curNavi.id].categoryNodes.each(function (categoryNod) {
					categoryNod.setStyle("display", "none");
				})
			}
			this.currentColumn = null;
		}
	},
	toNormal: function(){
		var css = this.app.css.normalDefaultMenuNode;
		//if (!this.morph){
		//	this.morph = new Fx.Morph(this.node, {duration: 50, link: "chain"});
		//}
        //this.morph.start(css).chain(function(){
			this.node.setStyles(css);
			MWF.require("MWF.widget.ScrollBar", function(){
				new MWF.widget.ScrollBar(this.node, {
					"style":"xApp_CMSExplorer_StartMenu", "distance": 100, "friction": 4,	"axis": {"x": false, "y": true}
				});
			}.bind(this));
       //}.bind(this));
	},
	setNodeCenter: function(node){
		var size = node.getSize();
		var contentSize = this.app.node.getSize();

		var top = contentSize.y/2 - size.y/2;
		var left = contentSize.x/2 - size.x/2;

		if (left<0) left = 0;
		if (top<0) top = 0;
		node.setStyles({"left": left, "top": top});
	},
	getDefaultMenuNormalSize: function(){
		var naviItemNode = this.node.getFirst();

		var size = naviItemNode.getComputedSize();
		var mt = naviItemNode.getStyle("margin-top").toFloat();
		var mb = naviItemNode.getStyle("margin-bottom").toFloat();
		var height = size.totalWidth+mt+mb;

		var ml = naviItemNode.getStyle("margin-left").toFloat();
		var mr = naviItemNode.getStyle("margin-right").toFloat();
		var width = size.totalWidth+ml+mr;

		return {"width": width, "height": height*this.navis.length};
	},
	setDefaultMenuWidth: function(){
		var naviItemNode = this.node.getFirst();

		var size = naviItemNode.getComputedSize();
		var ml = naviItemNode.getStyle("margin-left").toFloat();
		var mr = naviItemNode.getStyle("margin-right").toFloat();
		var width = size.totalWidth+ml+mr;
		this.node.setStyle("width", (width*this.navis.length)+"px");
	},
	onResize: function(){
		if (this.status == "start"){
			this.setNodeCenter(this.node);
		}
	}
});
