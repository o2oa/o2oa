MWF.xApplication.cms = MWF.xApplication.cms || {};
MWF.CMSE = MWF.xApplication.cms.Index = MWF.xApplication.cms.Index ||{};
MWF.require("MWF.widget.O2Identity", null,false);
//MWF.xDesktop.requireApp("cms.Index", "Actions.RestActions", null, false);
MWF.xApplication.cms.Index.options = {
	multitask: false,
	executable: true
};
MWF.xApplication.cms.Index.Main = new Class({
	Extends: MWF.xApplication.Common.Main,
	Implements: [Options, Events],

	options: {
		"style": "default",
		"name": "cms.Index",
		"icon": "icon.png",
		"width": "1220",
		"height": "680",
		"isResize": true,
		"isMax": true,
		"title": MWF.CMSE.LP.title
	},
	onQueryLoad: function(){
		this.lp = MWF.xApplication.cms.Index.LP;
	},
	loadApplication: function(callback){
		this.columns = [];
		this.restActions = MWF.Actions.get("x_cms_assemble_control"); //new MWF.xApplication.cms.Index.Actions.RestActions();
		this.createNode();
		this.loadApplicationContent();
	},
	reload : function(){
		this.scrollNode.destroy();
		this.loadContent();
		this.setCurrentAppType( this.currentAppType, this.currentAppTypeNode  );
	},
	createNode: function(){
		this.content.setStyle("overflow", "hidden");
		this.node = new Element("div", {
			"styles": this.css.container
		}).inject(this.content);
	},
	loadApplicationContent: function(){
		this.loadTitle();
		this.loadContent();
		this.setCurrentAppType( "all", this.allTypeNode );
	},
	loadTitle : function(){
		this.loadTitleBar();
		this.loadAllTypeNode();
		this.loadCreateDocumentActionNode();
		this.loadSearchNode();
		this.loadAppType();
	},
	loadTitleBar: function(){
		this.titleBarContainer = new Element("div", {
			"styles": this.css.titleBarContainer
		}).inject(this.node);
		this.titleBar = new Element("div", {
			"styles": this.css.titleBar
		}).inject(this.titleBarContainer);
	},
	loadAllTypeNode : function(){
		this.allTypeNode =  new Element("div.columnTop_All",{
			"styles" : this.css.columnTop_All,
			"text" : "全部栏目"
		}).inject( this.titleBar );
		this.allTypeNode.addEvents({
			"mouseover" : function(){
				if( this.currentAppTypeNode !== this.allTypeNode )this.allTypeNode.setStyles( this.css.columnTop_All_over );
			}.bind(this),
			"mouseout" : function(){
				if( this.currentAppTypeNode !== this.allTypeNode )this.allTypeNode.setStyles( this.css.columnTop_All );
			}.bind(this),
			"click": function () {
				this.setCurrentAppType( "all", this.allTypeNode );
			}.bind(this)
		});
	},
	loadCreateDocumentActionNode: function() {
		this.createDocumentAction = new Element("div", {
			"styles": this.css.createDocumentAction,
			"text" : this.lp.start
		}).inject(this.titleBar);
		this.createDocumentAction.addEvents({
			"click": function(e){
				MWF.xDesktop.requireApp("cms.Index", "Newer", function(){
					this.creater = new MWF.xApplication.cms.Index.Newer(null,null,this,this);
					this.creater.load();
				}.bind(this));
			}.bind(this),
			"mouseover" : function(e){
				this.createDocumentAction.setStyles( this.css.createDocumentAction_over );
				this.createDocumentAction.addClass( "o2_cms_index_createDocument_over" );
			}.bind(this),
			"mouseout" : function(e){
				this.createDocumentAction.setStyles( this.css.createDocumentAction );
				this.createDocumentAction.removeClass( "o2_cms_index_createDocument_over" );
			}.bind(this)
		});
	},
	//loadTitleTextNode: function(){
	//	this.titleTextNode = new Element("div", {
	//		"styles": this.css.titleTextNode,
	//		"text": this.lp.title
	//	}).inject(this.titleBar);
	//},
	loadSearchNode: function(){
		this.searchBarAreaNode = new Element("div", {
			"styles": this.css.searchBarAreaNode
		}).inject(this.titleBar);

		this.searchBarNode = new Element("div", {
			"styles": this.css.searchBarNode
		}).inject(this.searchBarAreaNode);

		this.searchBarActionNode = new Element("div", {
			"styles": this.css.searchBarActionNode,
			"title" : "搜索"
		}).inject(this.searchBarNode);
		this.searchBarResetActionNode = new Element("div", {
			"styles": this.css.searchBarResetActionNode,
			"title" : "重置"
		}).inject(this.searchBarNode);
		this.searchBarResetActionNode.setStyle("display","none");

		this.searchBarInputBoxNode = new Element("div", {
			"styles": this.css.searchBarInputBoxNode
		}).inject(this.searchBarNode);
		this.searchBarInputNode = new Element("input", {
			"type": "text",
			"value": this.lp.searchKey,
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

	loadAppType : function(){
		var _self = this;
		this.typeListContainer = new Element("div.columnTop_category", {
			"styles": this.css.columnTop_category
		}).inject(this.titleBar);

		this.restActions.listAllAppType( function( json ){
			(json.data || []).each( function( typeObject ){
				new Element( "div.columnTop_category", {
					"styles" : this.css.columnTop_categoryItem,
					"text" : typeObject.appType + "(" + typeObject.count + ")",
					"events" : {
						"mouseover" : function( ev ){
							if( this.currentAppTypeNode !== ev.target )ev.target.setStyles( this.css.columnTop_categoryItem_over );
						}.bind(this),
						"mouseout" : function( ev ){
							if( this.currentAppTypeNode !== ev.target )ev.target.setStyles( this.css.columnTop_categoryItem );
						}.bind(this),
						"click": function ( ev ) {
							_self.setCurrentAppType( this, ev.target );
						}.bind( typeObject.appType )
					}
				}).inject( this.typeListContainer )
			}.bind(this))
			if (this.typeListContainer.getScrollSize().y> Math.round(this.typeListContainer.getSize().y) && !this.columnTypeExpandNode ) this.createTypeExpandButton();
		}.bind(this))
	},
	createTypeExpandButton : function(){
		this.columnTypeExpandNode =  new Element("div.columnTop_categoryExpandButton",{
			"styles" : this.css.columnTop_categoryExpandButton
		}).inject( this.typeListContainer, "before" );
		this.columnTypeExpandNode.addEvent("click", this.expandOrCollapseCategory.bind(this));
	},
	expandOrCollapseCategory : function(e){
		if (!this.categoryMorph) this.categoryMorph = new Fx.Morph(this.typeListContainer, {"duration": 100});
		if( !this.expand ){
			this.typeListContainer.setStyle( "width", this.typeListContainer.getSize().x + "px" );
			this.typeListContainer.setStyles( this.css.columnTop_category_more );
			this.categoryMorph.start({"height": ""+this.typeListContainer.getScrollSize().y+"px"});

			this.expandOrCollapseCategoryFun = this.expandOrCollapseCategory.bind(this);
			this.content.addEvent("click", this.expandOrCollapseCategoryFun);
			this.expand = true;
		}else{
			this.typeListContainer.setStyle( "width", "auto" );
			this.typeListContainer.setStyles( this.css.columnTop_category );
			this.categoryMorph.start({"height": ""+this.titleBar.getSize().y+"px"});
			if (this.expandOrCollapseCategoryFun) this.content.removeEvent("click", this.expandOrCollapseCategoryFun);
			this.expand = false;
		}
		e.stopPropagation();
	},
	setCurrentAppType : function( appType, target ){
		if( this.currentAppType ){
			if( this.currentAppType === "all" ){
				this.currentAppTypeNode.setStyles( this.css.columnTop_All );
				this.currentAppTypeNode.removeClass("o2_cms_index_all_current");
			}else{
				this.currentAppTypeNode.setStyles( this.css.columnTop_categoryItem );
				this.currentAppTypeNode.removeClass("o2_cms_index_categoryItem_current");
			}
		}
		if( appType === "all" ){
			target.setStyles( this.css.columnTop_All_current );
			target.addClass("o2_cms_index_all_current");
		}else{
			target.setStyles( this.css.columnTop_categoryItem_current );
			target.addClass("o2_cms_index_categoryItem_current");
		}
		this.currentAppType = appType;
		this.currentAppTypeNode = target;

		this.createColumnNodes();
	},

	//loadRefreshNode : function(){
	//	this.refreshAreaNode = new Element("div", {
	//		"styles": this.css.refreshAreaNode
	//	}).inject(this.titleBar);
    //
	//	this.refreshActionNode = new Element("div", {
	//		"styles": this.css.refreshActionNode,
	//		"title" : this.lp.refresh
	//	}).inject(this.refreshAreaNode);
	//	this.refreshActionNode.addEvent("click", function(){
	//		this.reload();
	//	}.bind(this));
	//},
	loadContent: function(callback){

		//this.container = new Element("div", {
		//	"styles": this.css.container
		//}).inject(this.node);
		this.scrollNode = new Element("div", {
			"styles": this.css.scrollNode
		}).inject(this.node);
		this.contentWarpNode = new Element("div", {
			"styles": this.css.node
		}).inject(this.scrollNode);

		this.contentContainerNode = new Element("div",{
			"styles" : this.css.contentContainerNode
		}).inject(this.contentWarpNode);
		this.contentNode = new Element("div", {
			"styles": this.css.contentNode
		}).inject(this.contentContainerNode);

		//this.createColumnNodes();

		//MWF.require("MWF.widget.ScrollBar", function(){
		//	new MWF.widget.ScrollBar(this.contentContainerNode, {
		//		"indent": false,"style":"xApp_TaskList", "where": "before", "distance": 30, "friction": 4,	"axis": {"x": false, "y": true},
		//		"onScroll": function(y){
		//			//var scrollSize = _self.elementContentNode.getScrollSize();
		//			//var clientSize = _self.elementContentNode.getSize();
		//			//var scrollHeight = scrollSize.y-clientSize.y;
		//			//if (y+200>scrollHeight) {
		//			//	if (!_self.view.isItemsLoaded) _self.view.loadElementList();
		//			//}
		//		}
		//	});
		//}.bind(this));
        //
		this.setContentSize();

		this.addEvent("resize", function(){
			this.setContentSize();
		}.bind(this));
	},
	createColumnNodes: function(){
		this.contentNode.empty();
		if( this.currentAppType === "all" ){
			this.restActions.listColumn( function(json){
				this._createColumnNodes( json )
			}.bind(this));
		}else{
			this.restActions.listWhatICanViewWithAppType(this.currentAppType, function(json){
				this._createColumnNodes( json )
			}.bind(this));
		}
	},
	_createColumnNodes : function( json ){
		if( typeOf(json.data)!="array" )return;
		var tmpArray = json.data;
		tmpArray.sort(function( a, b ){
			return parseFloat(a.appInfoSeq) - parseFloat(b.appInfoSeq)
		});
		json.data = tmpArray;

		var i = 0;
		json.data.each(function(column){
			var column = new MWF.xApplication.cms.Index.Column(this, column, {"index" : i++ });
			column.load();
			this.columns.push(column);
		}.bind(this));
	},
	search : function( key ){
		if(!key)key = this.searchBarInputNode.get("value");
		if(key==this.lp.searchKey)key="";
		if( key!="" ){
			this.searchBarResetActionNode.setStyle("display","block");
			this.searchBarActionNode.setStyle("display","none");
		}
		this.columns.each(function( column ){
			column.search( key );
		}.bind(this))
	},
	reset : function(){
		this.searchBarInputNode.set("value",this.lp.searchKey);
		this.searchBarResetActionNode.setStyle("display","none");
		this.searchBarActionNode.setStyle("display","block");
		this.columns.each(function( column ){
			column.loadList();
		}.bind(this))
	},

	clearContent: function(){
		//if (this.indexContent){
		//	if (this.index) delete this.index;
		//	this.indexContent.destroy();
		//	this.indexContent = null;
		//}
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
	recordStatus: function(){
		//if (this.menu.currentNavi){
		//	var naviType = this.menu.currentNavi.retrieve("type");
		//	var naviData = this.menu.currentNavi.retrieve("naviData");
		//	return {
		//		"navi" :{ "type": naviType, "id": naviData.id, "columnId":naviData.appId},
		//		"view" : this.index.currentViewData.id ? this.index.currentViewData.id : "default"
		//	};
		//}
	},
	setContentSize: function(){
		//var titlebarSize = this.titleBar ? this.titleBar.getSize() : {"x":0,"y":0};
		//var nodeSize = this.node.getSize();
		//var pt = this.contentContainerNode.getStyle("padding-top").toFloat();
		//var pb = this.contentContainerNode.getStyle("padding-bottom").toFloat();
        //
		//var height = nodeSize.y-pt-pb-titlebarSize.y;
		//this.contentContainerNode.setStyle("height", ""+height+"px");

		var nodeSize = this.content.getSize();
		var titlebarSize = this.titleBarContainer ? this.titleBarContainer.getSize() : {"x":0,"y":0};

		this.scrollNode.setStyle("height", ""+(nodeSize.y-titlebarSize.y)+"px");

		if (this.contentWarpNode){
			var count = (nodeSize.x/550).toInt();
			var x = 550 * count;
			var m = (nodeSize.x-x)/2-10;
			this.contentWarpNode.setStyles({
				"width": ""+x+"px",
				"margin-left": ""+m+"px"
			});
			//this.titleBar.setStyles({
			//	"margin-left": ""+(m+10)+"px",
			//	"margin-right": ""+(m+10)+"px"
			//});
			if( this.typeListContainer ){
				if ( this.typeListContainer.getScrollSize().y> Math.round(this.typeListContainer.getSize().y)) {
					if( !this.columnTypeExpandNode ){
						this.createTypeExpandButton();
					}else{
						this.columnTypeExpandNode.setStyle("display","")
					}
				}else{
					if(this.columnTypeExpandNode)this.columnTypeExpandNode.setStyle("display","none");
				}
			}
		}
	}
});

MWF.xApplication.cms.Index.Column = new Class({
	Implements: [Options, Events],
	options: {
		"where": "bottom",
		"index" : 0
	},

	initialize: function (app, data, options) {
		this.setOptions(options);
		this.app = app;
		this.container = this.app.contentNode;
		this.data = data;
		this.isNew = false;
		this.defaultColumnIcon = "/x_component_cms_Index/$Main/"+this.app.options.style+"/icon/column.png";
	},
	load : function(){
		this.loadNode();
		this.loadTop();
		this.loadCategory();
		this.loadList();
	},
	loadNode : function(){
		this.node = new Element("div.columnItem", {
			"styles": this.app.css.columnItemNode
		}).inject(this.container,this.options.where);

		//if( this.options.index % 2 == 1 ){
		//	this.node.setStyles({
		//		"margin-left" : "0px",
		//		"margin-right" : "0px"
		//	})
		//}else{
		//	this.node.setStyles({
		//		"margin-left" : "0px",
		//		"margin-right" : "10px"
		//	})
		//}

		var leftNode = this.leftNode = new Element("div.columnItemLeftNode", {
			"styles": this.app.css.columnItemLeftNode
		}).inject(this.node);

		var rightNode = this.rightNode = new Element("div.columnItemRightNode", {
			"styles": this.app.css.columnItemRightNode
		}).inject(this.node);

		this.categoryContainer = new Element("div.categoryContainer",{
			"styles" : this.app.css.categoryContainer
		}).inject(this.rightNode);

		this.categoryList = new Element("div.categoryList",{
			"styles" : this.app.css.categoryList
		}).inject(this.categoryContainer);

		this.documentList = new Element("div",{
			"styles" : this.app.css.documentList
		}).inject(this.rightNode);
	},
	loadTop: function () {
		this.data.name = this.data.appName;
		var columnName = this.data.appName;
		var alias = this.data.appAlias;
		var memo = this.data.description;
		var order = this.data.appInfoSeq;
		var creator =this.data.creatorUid;
		var createTime = this.data.createTime;

		var leftNode = this.leftNode;


		//var iconNode = this.iconNode = new Element("div",{
		//	"styles" : this.app.css.columnItemIconNode
		//}).inject(topNode);
		//
		//if (this.data.appIcon){
		//	this.iconNode.setStyle("background-image", "url(data:image/png;base64,"+this.data.appIcon+")");
		//}else{
		//	this.iconNode.setStyle("background-image", "url("+this.defaultColumnIcon+")")
		//}

		var iconAreaNode = this.iconAreaNode = new Element("div",{
			"styles" : this.app.css.columnItemIconAreaNode
		}).inject(leftNode);
		//var mod = this.options.index % this.backgroundColors.length;
		//this.color = this.backgroundColors[mod];
		//iconAreaNode.setStyle("background-color",this.color);

		var iconNode = this.iconNode = new Element("img",{
			"styles" : this.app.css.columnItemIconNode
		}).inject(iconAreaNode);
		if (this.data.appIcon){
			this.iconNode.set("src", "data:image/png;base64,"+this.data.appIcon+"");
		}else{
			this.iconNode.set("src", this.defaultColumnIcon)
		}
		iconNode.makeLnk({
			"par": this._getLnkPar()
		});

		var textNode = new Element("div",{
			"styles" : this.app.css.columnItemTextNode
		}).inject(leftNode);

		var titleNode = new Element("div",{
			"styles" : this.app.css.columnItemTitleNode,
			"text" : columnName,
			"title": (alias) ? columnName+" ("+alias+") " : columnName
		}).inject(textNode);

		var description = ( memo && memo!="") ? memo : this.app.lp.noDescription;
		var descriptionNode = new Element("div",{
			"styles" : this.app.css.columnItemDescriptionNode,
			"text" : description,
			"title" : description
		}).inject(textNode);

		var _self = this;
		leftNode.addEvents({
			//"mouseover": function(){if (!_self.selected) this.setStyles(_self.app.css.columnItemNode_over);},
			//"mouseout": function(){if (!_self.selected) this.setStyles(_self.app.css.columnItemNode);},
			"click": function(e){_self.clickColumnNode(_self,this,e)}
		});
	},
	_getLnkPar: function(){
		var lnkIcon = this.defaultColumnIcon;
		if (this.data.appIcon) lnkIcon = "data:image/png;base64,"+this.data.appIcon;

		var appId = "cms.Module"+this.data.id;
		return {
			"icon": lnkIcon,
			"title": this.data.appName,
			"par": "cms.Module#{\"columnId\": \""+this.data.id+"\", \"appId\": \""+appId+"\"}"
		};
	},
	clickColumnNode : function(_self, el, e ){
		this.openModule("all",e);
	},
	clickMoreLink : function(e){
		var key = this.app.searchBarInputNode.get("value");
		if(key==this.app.lp.searchKey)key="";
		this.openModule("all",e, key);
	},
	openModule : function( categoryId , e , searchKey, isCategory ){
		var appId = "cms.Module"+this.data.id;
		if (this.app.desktop.apps[appId]){
			if( searchKey ){
				this.app.desktop.apps[appId].close();
			}else{
				this.app.desktop.apps[appId].setCurrent();
				return;
			}
		}
		this.app.desktop.openApplication(e, "cms.Module", {
			"columnData": this.data,
			"appId": appId,
			"categoryId": categoryId,
			//"viewId" : "default",
			"isCategory" : isCategory || false,
			"searchKey" : searchKey
		});
	},
	loadCategory : function(){
		var _self = this;
		if( typeOf(this.data.wrapOutCategoryList) != "array" )return;
		var tmpArray = this.data.wrapOutCategoryList;
		tmpArray.sort(function( a, b ){
			return parseFloat(a.categorySeq) - parseFloat(b.categorySeq)
		});
		this.data.wrapOutCategoryList = tmpArray;
		this.data.wrapOutCategoryList.each(function(category){
			var categoryNode = new Element("div.categoryItem",{
				"text" : category.categoryName,
				"styles" : this.app.css.categoryItem
			}).addClass("o2_cms_index_categoryItem_text").inject( this.categoryList, "top" );

			categoryNode.store("category",category);
			categoryNode.addEvents({
				"mouseover" : function(){
					this.setStyles(_self.app.css.categoryItem_over);
					this.addClass("o2_cms_index_categoryItem_text_over");
				},
				"mouseout" : function(){
					this.setStyles(_self.app.css.categoryItem);
					this.removeClass("o2_cms_index_categoryItem_text_over");
				},
				"click" : function(e){
					_self.openModule( this.retrieve("category").id , e , "", true)
				}
			})
		}.bind(this));

		if( this.categoryList.getScrollSize().y > this.categoryContainer.getSize().y ){
			this.categoryArrowNode = new Element("div.categoryArrowNode",{
				"styles" : this.app.css.categoryArrowNode
			}).inject( this.categoryContainer );
			this.categoryArrowNode.addEvents({
				"mouseover" : function(){
					this.categoryArrowNode.setStyles( this.categoryArrow != "down" ? this.app.css.categoryArrowNode_over : this.app.css.categoryArrowNode_down_over);
				}.bind(this),
				"mouseout" : function(){
					this.categoryArrowNode.setStyles( this.categoryArrow != "down" ? this.app.css.categoryArrowNode : this.app.css.categoryArrowNode_down);
				}.bind(this),
				"click" : function( e ){
					if( this.categoryArrow != "down" ){
						this.openCategoryList( e );
					}else{
						this.closeCategoryList( e )
					}
				}.bind(this)
			});
		}
	},
	openCategoryList : function( e ){
		this.categoryArrow = "down";
		this.categoryArrowNode.setStyles(this.app.css.categoryArrowNode_down_over );
		this.categoryList.setStyles(this.app.css.categoryList_all);
		window.closeCategoryList = this.closeCategoryList.bind(this);
		this.app.content.addEvent("click", window.closeCategoryList );
		e.stopPropagation();
	},
	closeCategoryList : function( e ){
		this.categoryArrow = "up";
		this.categoryArrowNode.setStyles(this.app.css.categoryArrowNode );
		this.categoryList.setStyles(this.app.css.categoryList);
		this.app.content.removeEvent("click" , window.closeCategoryList );
		e.stopPropagation();
	},
	destroy: function(){
		this.node.destroy();
		MWF.release(this);
		delete this;
	},
	search : function(key){
		if( !key || key==""){
			this.loadList();
			return;
		}
		if(this.documentList)this.documentList.empty();
		if(this.moreArea)this.moreArea.destroy();
		var filter = {
			"title": key,
			"appIdList": [this.data.id],
			"statusList": ["published"]
		};
		this.getDocumentData(function(json){
			//json.count //栏目下文档总数
			//json.size //当前条数
			if( json.count > json.size ){
				this.loadMoreItem( json.count, json.size )
			}
			json.data.each(function(data){
				this.listDocument(data);
			}.bind(this))
		}.bind(this), null, filter );
	},
	loadList: function(){
		if(this.documentList)this.documentList.empty();
		if(this.moreArea)this.moreArea.destroy();
		this.getDocumentData(function(json){
			//json.count //栏目下文档总数
			//json.size //当前条数
			//if( json.count > json.size ){
			//	this.loadMoreItem( json.count, json.size )
			//}
			json.data.each(function(data){
				this.listDocument(data);
			}.bind(this))
		}.bind(this));
	},
	listDocument:function(data){
		var _self = this;
		var documentItem = new Element("div",{
			"text" : data.title,
			"title" : data.title,
			"styles" : this.app.css.documentItem
		}).inject(this.documentList);
		documentItem.store("documentId",data.id);
		documentItem.addEvents({
			"mouseover" : function(){
				this.setStyles(_self.app.css.documentItem_over);
				this.addClass("mainColor_color");
			},
			"mouseout" : function(){
				this.setStyles(_self.app.css.documentItem);
				this.removeClass("mainColor_color");
			},
			"click" : function(){
				var documentId = this.retrieve("documentId");
				var appId = "cms.Document"+documentId;
				if (_self.app.desktop.apps[appId]){
					_self.app.desktop.apps[appId].setCurrent();
				}else {
					var options = {
						"documentId": documentId,
						"appId": appId,
						"readonly": true
					};
					_self.app.desktop.openApplication(null, "cms.Document", options);
				}
			}
		})
	},
	getDocumentData: function(callback, count, filter){
		if(!count)count=6;
		var id = "(0)";
		if(!filter){
			filter = {
				"appIdList": [this.data.id],
				"statusList": ["published"]
			}
		}
		this.app.restActions.listDocumentFilterNext(id, count, filter, function(json){
			if (callback) callback(json);
		});
	},
	loadMoreItem: function(total, size){
		var _self = this;
		this.moreArea = new Element("div",{
			"styles" : this.app.css.moreArea
		}).inject(this.rightNode);
		this.moreLinkText = new Element("div",{
			"styles" : this.app.css.moreLinkText,
			"text" : "更多("+(total-size)+")"
		}).inject(this.moreArea);
		this.moreLinkImage = new Element("div",{
			"styles" : this.app.css.moreLinkImage
		}).inject(this.moreArea);
		this.moreArea.addEvents({
			"mouseover" : function(){
				this.moreLinkText.setStyles(_self.app.css.moreLinkText_over);
				this.moreLinkImage.setStyles(_self.app.css.moreLinkImage_over)
			}.bind(this),
			"mouseout" : function(){
				this.moreLinkText.setStyles(_self.app.css.moreLinkText);
				this.moreLinkImage.setStyles(_self.app.css.moreLinkImage)
			}.bind(this),
			"click" : function(e){_self.clickMoreLink( e )}
		});
		//this.moreArea.setStyle("background-color",this.color)
	}


});
