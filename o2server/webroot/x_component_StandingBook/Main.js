MWF.xApplication.StandingBook.options.multitask = false;

o2.requireApp("StandingBook", "MCommon", null, false);

MWF.xApplication.StandingBook.options.NotCommonlyField = {
	"equals": ["applicationAlias","application","process","job", "categoryAlias","categoryId","appAlias","appId"],
	"endWith": [".distinguishedName","person",".personDn",".personEmployee",
		".personName",".personUnique",".unique",".unit", ".unitName", ".unitLevelName",".id",".description",".dn",".attributeList",
		".typeList",".shortName",".level"]
};
MWF.xApplication.StandingBook.Main = new Class({
	Extends: MWF.xApplication.Common.Main,
	Implements: [Options, Events],

	options: {
		"style1": "default",
		"style": "default",
		"name": "StandingBook",
		"mvcStyle": "style.css",
		"icon": "icon.png",
		"title": MWF.xApplication.StandingBook.LP.title,
		"query":""
	},
	onQueryLoad: function(){
		this.lp = MWF.xApplication.StandingBook.LP;
		this.selectedAppList = [];
	},
	reload: function(){
		this.content.empty();
		this.selectedAppList = [];
		this.loadApplication();
	},
	loadApplication: function(callback){
		this.userData = new MWF.xApplication.StandingBook.UserData();
		var url = this.path+this.options.style+"/main.html";
		this.content.loadHtml(url, {"bind": {"lp": this.lp}, "module": this}, function(){

			this.contentArea.loadCss("../x_component_StandingBook/$IndexView/" + this.options.style + "/style.css");

			var app;
			if( this.status && this.status.app){
				app = this.status.app;
			}else{
				app = this.options.app;
			}

			debugger;
			var p1 =  Promise.resolve( this.userData.getDefaultMenu() );
			var p2 = o2.Actions.load("x_custom_index_assemble_control").IndexAction.available();
			var p3 = o2.Actions.load("x_custom_index_assemble_control").RevealAction.listDirectory();

			Promise.all([p1, p2, p3]).then(function(data){
				var defaultMenuList = data[0];
				this.data = data[1].data;
				this.canCreateConfig = (data[2].data && data[2].data.length > 0);

				if( !this.canCreateConfig )this.configContent.hide();

				var selectedAppList;
				if( defaultMenuList ){
					selectedAppList = defaultMenuList.filter(function(app){
						var key = this.getKeyByCategory( app.category );
						if( typeOf( this.data[key] ) !== "array" )return false;
						for( var i=0; i<this.data[key].length; i++ ){
							var d = this.data[key][i];
							if( (d.id && (d.id === app.id)) || (d.key && (d.key === app.key)) )return true;
						}
						return false;
					}.bind(this));
				}

				this.loadMenu(null,  function () {
					if( selectedAppList && selectedAppList.length ){
						this.openAppList( selectedAppList );
					}else{
						this.introduce();
					}
				}.bind(this), selectedAppList);

			}.bind(this))
			this.status = null;
		}.bind(this));
	},

	getEventTarget: function(e, className) {
		var parentItem = e.target || e;
		if( parentItem.hasClass(className) )return parentItem;
		while ( parentItem && !parentItem.hasClass(className) ){
			parentItem = parentItem.getParent();
			if( parentItem.hasClass(className) )return parentItem;
		}
	},
	switchMenuItem: function(e){
		var childNode = this.getEventTarget(e, "menuItem").getNext();
		if( childNode.getStyle("display") === "none" ){
			childNode.show();
		}else{
			childNode.hide();
		}
	},
	openAppList: function( appList ){
		this.menuContent.getElements(".subItem").each(function (subItem) {
			appList.each(function (a) {
				if( (a.id || a.key) === subItem.get("data-o2-key") ){
					a.node = subItem;
					this._setCheckedStyle(subItem);
				}
			}.bind(this))
		}.bind(this));
		this.selectedAppList = appList || [];
		if( !appList || appList.length === 0 ){
			this.showEmpty();
		}else if( appList[0].category === "reveal" ){
			this.openRevealView( [appList[0]] )
		}else{
			this.openIndexView( appList );
		}
	},
	openRevealView: function( data ){
		if( this.view && this.view.destroy )this.view.destroy();
		this.contentArea.empty();
		o2.requireApp("StandingBook", "RevealView", function () {
			this.view = new MWF.xApplication.StandingBook.RevealView(this.contentArea, this, {}, data);
		}.bind(this), false);
	},
	openIndexView: function( data ){
		if( this.view && this.view.destroy )this.view.destroy();
		this.contentArea.empty();
		o2.requireApp("StandingBook", "IndexView", function () {
			this.view = new MWF.xApplication.StandingBook.IndexView(this.contentArea, this, {}, data);
		}.bind(this), false);
	},
	openConfigView: function(){
		if( this.view && this.view.destroy )this.view.destroy();
		this.appNode.empty();
		o2.requireApp("StandingBook", "ConfigView", function () {
			this.view = new MWF.xApplication.StandingBook.ConfigView(this.appNode, this, {});
		}.bind(this), false);
	},
	iconOver: function(e){
		e.target.addClass('mainColor_color');
	},
	iconOut: function(e){
		e.target.removeClass('mainColor_color');
	},

	inputOver: function(e){
		this.getEventTarget(e, "main_inputArea").addClass('mainColor_border');
	},
	inputOut: function(e){
		this.getEventTarget(e, "main_inputArea").removeClass('mainColor_border');
	},

	iconOver_searchButton: function(e){
		e.target.addClass('mainColor_color');
	},
	iconOut_searchButton: function(e){
		e.target.removeClass('mainColor_color');
	},

	inputOver_searchArea: function(e){
		this.getEventTarget(e, "tl_core_searchArea").addClass('mainColor_border');
	},
	inputOut_searchArea: function(e){
		this.getEventTarget(e, "tl_core_searchArea").removeClass('mainColor_border');
	},
	getKeyByCategory: function(category){
		switch (category) {
			case "reveal": return "revealList";
			case "processPlatform": return "processPlatformList";
			case "cms": return "cmsList";
			default: return "";
		}
	},
	loadMenu: function( ev, callback, selectedAppList){
		this.menuData = [];
		var expandList = [];
		if( selectedAppList && selectedAppList.length ){
			expandList = selectedAppList.map(function (a) {
				return a.category;
			}).unique();
		}else{
			expandList.push( "reveal" );
		}
		Object.each(this.data, function (array, key) {
			var index, category;
			switch (key) {
				case "revealList": index=1; category="reveal"; break;
				case "processPlatformList": index = 2; category="processPlatform"; break;
				case "cmsList": index = 3; category="cms"; break;
				default: index = 4; break;
			}
			if( array.length > 0 )this.menuData.push({
				index: index,
				category: category,
				label: this.lp[category],
				icon: category,
				expand: expandList.contains(category),
				items: array.map(function(k){
					if(!k.category)k.category = category;
					return k;
				})
			});
		}.bind(this));
		this.menuData.sort(function (a, b) {
			return a.index - b.index;
		});
		this.loadMenuContent( callback );
	},
	loadMenuContent: function(callback){
		this.menuContent.empty();
		this.menuContent.loadHtml(this.path+this.options.style+"/menu.html",
			{
				"bind": {"lp": this.lp, "data": this.menuData},
				"module": this,
				"reload": true
			},
			function(){
				if(callback)callback();
			}.bind(this)
		);
	},
	menuItemOver: function(e){
		var node = e.target;
		while (node && !node.hasClass("menuItem")){ node = node.getParent();}
		if (node){
			if(!node.hasClass("menuItem_current"))node.addClass("menuItem_over").addClass("mainColor_bg_opacity");
		}
	},
	menuItemOut: function(e){
		var node = e.target;
		while (node && !node.hasClass("menuItem")){ node = node.getParent();}
		if (node){
			if(!node.hasClass("menuItem_current"))node.removeClass("menuItem_over").removeClass("mainColor_bg_opacity");
		}
	},
	_setCheckedStyle: function(e){
		var subItem = this.getEventTarget(e, "subItem");
		subItem.addClass("menuItem_current").addClass("mainColor_color").addClass("mainColor_bg_opacity");

		var iNode = subItem.getElement("i");
		if(iNode)iNode.removeClass("o2icon-check_box_outline_blank").addClass("o2icon-check_box");
	},
	_setUncheckedStyle: function(e){
		var subItem = this.getEventTarget(e, "subItem");
		subItem.removeClass("menuItem_current").removeClass("mainColor_color").removeClass("mainColor_bg_opacity");

		var iNode = subItem.getElement("i");
		if(iNode)iNode.removeClass("o2icon-check_box").addClass("o2icon-check_box_outline_blank");
	},
	menuItemClick: function(e, item){
		// this.closeSelectModule(e);
		// this.getEventTarget(e, "subItem").addClass("menuItem_current").addClass("mainColor_color");
		this.selectedAppList.each(function (app) {
			this._setUncheckedStyle( app.node );
		}.bind(this));
		this._setCheckedStyle(e);
		if( item.category === "reveal" ){
			this.selectedAppList = [{
				category: item.category,
				id: item.id,
				name: item.name,
				node: this.getEventTarget(e, "subItem")
			}];
			this.openRevealView(this.selectedAppList);
		}else{
			this.selectedAppList = [{
				category: item.category,
				key: item.key,
				name: item.name,
				node: this.getEventTarget(e, "subItem")
			}];
			this.openIndexView(this.selectedAppList);
		}
		this.saveUserData();
	},
	menuItemClick_multi: function(e, item){
		if( !this.selectedAppList )this.selectedAppList = [];

		var revealList = this.selectedAppList.filter(function (app) {
			return  app.category === "reveal";
		});
		revealList.each(function (r) {
			this._setUncheckedStyle( r.node );
			this.selectedAppList.erase( r );
		}.bind(this))

		var index = -1;
		this.selectedAppList.each(function (app, idx) {
			if( app.key === item.key )index = idx;
		});
		if( index > -1 ){ //已经选择了
			this._setUncheckedStyle(e);
			this.selectedAppList.splice(index, 1);
		}else{ //还没有选择
			this._setCheckedStyle(e);
			this.selectedAppList.push({
				category: item.category,
				key: item.key,
				name: item.name,
				node: this.getEventTarget(e, "subItem")
			});
		}
		if( this.selectedAppList.length === 0 ){
			this.showEmpty();
		}else{
			this.openIndexView(this.selectedAppList);
		}
		this.saveUserData();
		e.stopPropagation();
	},
	menuSearchKeydown: function(e){
		if( e.keyCode === 13 ){
			this.searchApp();
		}
	},
	searchApp: function(){
		this.selectedAppList = [];
		var value = this.searchAppInput.get("value");
		if( !value ){
			this.loadMenuContent();
		}else{
			var flag = false;
			this.menuContent.getElements(".menuItemSubArea").each(function (el) {
				var matchCount = 0;
				el.getElements(".menuItem").each(function (ele) {
					if( ele.get("text").contains( value ) ){
						matchCount++;
						ele.show()
					}else{
						ele.hide()
					}
				});

				if( matchCount === 0 ){
					el.hide();
					el.getParent(".menuItemWrap").hide();
				}else{
					flag = true;
					el.show();
					el.getParent(".menuItemWrap").show();
				}
			})
			if( !flag ){
				new Element("div.menuNoMatchItem",{
					text: this.lp.menuNoMatchItem
				}).inject(this.menuContent);
			}
		}
	},
	recordStatus: function () {
		return this.view ? this.view.recordStatus() : "";
	},
	saveUserData: function () {
		var selectedAppList = [];
		this.selectedAppList.each(function (d) {
			var a = {
				category: d.category,
				name: d.name,
			};
			if( d.id )a.id = d.id;
			if( d.key )a.key = d.key;
			selectedAppList.push( a );
		});
		this.userData.saveDefaultMenu( selectedAppList );
	},
	introduce: function () {
		if(this.selectedAppList)this.selectedAppList.each(function (app) {
			this._setUncheckedStyle( app.node );
		}.bind(this));
		if( this.view ){
			this.view.destroy();
			this.view = null;
		}
		this.contentArea.empty();
		if( this.markedHtml ){
			this.readmeScrollNode = new Element("div.readmeScrollNode").inject( this.contentArea );
			this.readmeNode = new Element("div.readmeNode").inject( this.readmeScrollNode );
			this.readmeNode.loadCss(this.path + "md/style.css");
			this.readmeNode.set("html", this.markedHtml);
			return;
		}
		o2.load("../o2_lib/marked/lib/marked.js", function () {
			// marked.setOptions({
			//     gfm: true,
			//     tables: true,
			//     breaks: true,
			//     pedantic: false,
			//     sanitize: true,
			//     smartLists: true,
			//     smartypants: false
			// });
			var rendererMD = new marked.Renderer();
			rendererMD.image = function(href, title, text) {
				return "<img οnclick=\"o2StandingBook_ShowMarkedImage(event, '"+href+"')\" src=\""+href+"\" alt=\""+text+"\" title=\""+(title ? title : '')+"\">"
			};
			window.o2StandingBook_ShowMarkedImage = function(ev, href){
				alert(href);
			};
			marked.setOptions({
				renderer: rendererMD,
				gfm: true,
				tables: true,
				breaks: false,
				pedantic: false,
				sanitize: false,
				smartLists: true,
				smartypants: false,
			});
			MWF.getRequestText(this.path + "md/readme.md", function (responseText, responseXML) {
				this.markedHtml = marked(responseText);
				this.readmeScrollNode = new Element("div.readmeScrollNode").inject( this.contentArea );
				this.readmeNode = new Element("div.readmeNode").inject( this.readmeScrollNode );
				this.readmeNode.loadCss(this.path + "md/style.css");
				this.readmeNode.set("html", this.markedHtml);
			}.bind(this));
		}.bind(this))
	},
	showEmpty: function(){
		if(this.selectedAppList && this.selectedAppList.length)this.selectedAppList.each(function (app) {
			this._setUncheckedStyle( app.node );
		}.bind(this));
		if( this.view && this.view.destroy ){
			this.view.destroy();
		}
		this.view = null;
		this.contentArea.empty();
		var html =
			'<div class="listNoData">'+
			'   <div class="listNoAppIcon"></div>'+
			'   <div class="listNoDataText">'+this.lp.noAppNote+'</div>'+
			'</div>';
		this.contentArea.set("html", html);
	}

});


MWF.xApplication.StandingBook.UserData = new Class({
	initialize: function(){
		this.DEFAULT_MENU_KEY = "menu";
		this.VIEW_DATA_KEY = "view";
		this.REVEAL_DATA_KEY = "reveal";
	},

	getDefaultMenu: function () {
		if( this.menuData )return this.menuData;
		return o2.Actions.load("x_custom_index_assemble_control").CustomAction.get( this.DEFAULT_MENU_KEY, function(js){
			var json = js.data ? JSON.decode(js.data) : null;
			switch (typeOf(json)) {
				case "string":
					this.menuData = JSON.parse(json || "[]"); break;
				case "array":
				case "object":
					this.menuData = json;  break;
				default:
					this.menuData = []; break;
			}
			return this.menuData;
		}.bind(this));
	},
	saveDefaultMenu: function (menuData) {
		this.menuData = menuData || [];
		o2.Actions.load("x_custom_index_assemble_control").CustomAction.update(this.DEFAULT_MENU_KEY, JSON.stringify(this.menuData));
	},


	_getViewData: function () {
		if( this.viewData )return this.viewData;
		return o2.Actions.load("x_custom_index_assemble_control").CustomAction.get( this.VIEW_DATA_KEY, function(js){
			var json = js.data ? JSON.decode(js.data) : null;
			switch (typeOf(json)) {
				case "string":
					this.viewData = JSON.parse(json || "{}"); break;
				case "object":
					this.viewData = json;  break;
				default:
					this.viewData = {}; break;
			}
			return this.viewData;
		}.bind(this));
	},
	getViewData: function (id) {
		var r = this._getViewData();
		return Promise.resolve( r ).then(function (d) {
			return d[id] || {};
		});
	},
	saveViewData: function (id, value) {
		var r = this._getViewData();
		Promise.resolve( r ).then(function () {
			this.viewData[id] = value;
			o2.Actions.load("x_custom_index_assemble_control").CustomAction.update(this.VIEW_DATA_KEY, JSON.stringify(this.viewData));
		}.bind(this));
	},

	_getRevealData: function(){
		if( this.revealData )return this.revealData;
		return o2.Actions.load("x_custom_index_assemble_control").CustomAction.get( this.REVEAL_DATA_KEY, function(js){
			var json = js.data ? JSON.decode(js.data) : null;
			switch (typeOf(json)) {
				case "string":
					this.revealData = JSON.parse(json || "{}"); break;
				case "object":
					this.revealData = json;  break;
				default:
					this.revealData = {}; break;
			}
			return this.revealData;
		}.bind(this));
	},
	getRevealData: function (id) {
		var r = this._getRevealData();
		return Promise.resolve( r ).then(function (d) {
			return d[id] || {};
		});
	},
	saveRevealData: function (id, value) {
		var r = this._getRevealData();
		Promise.resolve( r ).then(function () {
			this.revealData[id] = value;
			o2.Actions.load("x_custom_index_assemble_control").CustomAction.update(this.REVEAL_DATA_KEY, JSON.stringify(this.revealData));
		}.bind(this));
	}
})