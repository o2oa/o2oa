MWF.xApplication.StandingBook.options.multitask = false;
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
	},
	loadApplication: function(callback, noLoadView){
		var url = this.path+this.options.style+"/main.html";
		this.content.loadHtml(url, {"bind": {"lp": this.lp}, "module": this}, function(){
			// this.loadMenu();
			var tab;
			if( this.status && this.status.tab){
				tab = this.status.tab;
			}else{
				tab = this.options.tab;
			}
			if( tab ){
				this.openIndexView( tab );
			}else{
				this.selectTab();
			}
			this.status = null;
		}.bind(this));
	},
	// loadMenu: function(){
	// 	this.getMenuJson( function (json) {
	// 		this.menuArea.loadHtml(this.path+this.options.style+"/menu.html", {"bind": {"lp": this.lp, "data": json}, "module": this}, function(){
	// 			this.menuArea.getElement(".menuItem").click();
	// 			// this[json[0].action]();
	// 		}.bind(this));
	// 	}.bind(this));
	// },

	getEventTarget: function(e, className) {
		var parentItem = e.target;
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
	openIndexView: function( data ){
		if( this.view && this.view.destroy )this.view.destroy();
		this.contentArea.empty();
		o2.requireApp("StandingBook", "IndexView", function () {
			this.view = new MWF.xApplication.StandingBook.IndexView(this.contentArea, this, {}, data);
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

	closeSelectModule: function(e){
		// var area = (e && e.target) ? e.target.getParent(".tl_area") : this.content.getParent(".tl_area");
		// if(area)area.destroy();
		if( this.tabContent ){
			this.tabContent.destroy();
		}
		this.appNode.show();
	},
	selectTab: function( ev, callback, closeEnable){
		var tabContent = new Element("div.tl_area");
		this.tabContent = tabContent;
		tabContent.loadHtml(this.path+this.options.style+"/tabPage.html",
			{
				"bind": {"lp": this.lp, "status": { closeEnable: closeEnable } },
				"module": this,
				"reload": true
			},
			function(){
				this.loadTabTitles();
				this.loadTabList(function () {
					if(callback)callback();
				});
			}.bind(this)
		);
		this.appNode.hide();
		tabContent.inject(this.content);
	},
	loadTabTitles: function(){
		this.tl_tabTitles.getElements(".tl_tabTitle").each(function (tab, i) {
			tab.addEvent("click", function (e) {
				if( this.currentTab === tab )return;
				if( this.currentTab ){
					this.currentTab.removeClass("mainColor_bg");
					// this.currentTab.removeClass("mainColor_color");
					this.currentTab.removeClass("tl_tabTitleCurrent");
				}
				tab.addClass("mainColor_bg");
				// tab.addClass("mainColor_color");
				tab.addClass("tl_tabTitleCurrent");
				this.currentTab = tab;

				if( this.currentTabContent ){
					this.currentTabContent.hide();
				}
				var tabContentName = tab.get("data-o2-tab-content");
				this.currentTabContent = this[tabContentName];
				if( this.currentTabContent )this.currentTabContent.show();


			}.bind(this))
			if( i === 0 )tab.click();
		}.bind(this));
	},
	loadTabList: function( callback ){
		o2.Actions.load("x_query_assemble_surface").IndexAction.listDirectory().then(function(json){
			this.tabData = json;
			this._loadTabList( callback );
		}.bind(this));
	},
	_loadTabList: function(callback){
		this.tabData.data.each(function(obj){
			if( obj.category === "processPlatform" ){
				obj.keyList.each(function(k){
					if(!k.category)k.category = "processPlatform";
				});
				this.tl_workTabListNode.empty();
				this.tl_workTabListNode.loadHtml(this.path+this.options.style+"/tabList"+ (this.multiSelect ? "_multiselect" : "") +".html",
					{
						"bind": {"lp": this.lp, "data": obj.keyList || []},
						"module": this,
						"reload": true
					},
					function(){
						if(callback)callback();
					}.bind(this)
				);
			}else if( obj.category === "cms" ){
				obj.keyList.each(function(k){
					if(!k.category)k.category = "cms";
				});
				this.tl_docTabListNode.empty();
				this.tl_docTabListNode.loadHtml(this.path+this.options.style+"/tabList"+ (this.multiSelect ? "_multiselect" : "") +".html",
					{
						"bind": {"lp": this.lp, "data": obj.keyList || []},
						"module": this,
						"reload": true
					},
					function(){
						if(callback)callback();
					}.bind(this)
				);
			}
		}.bind(this));
	},
	loadItemIcon: function(application, e){
		var node = e.currentTarget;
		// Promise.resolve(this.getApplicationIcon(application)).then(function(icon){
		//     if (icon.icon){
		//         node.setStyle("background-image", "url(data:image/png;base64,"+icon.icon+")");
		//     }else{
		node.setStyle("background-image", "url("+"../x_component_process_ApplicationExplorer/$Main/default/icon/application.png)");
		//     }
		// });
	},
	coreListItemOver: function(e){
		e.target.addClass("mainColor_bg");
	},
	coreListItemOut: function(e){
		e.target.removeClass("mainColor_bg");
	},
	coreListItemDown: function(e){
		e.target.addClass("mainColor_bg");
	},
	coreListItemUp: function(e){
		e.target.removeClass("mainColor_bg");
	},
	coreListItemClick: function(e, tab){
		// var tList = (data.tabList || []).filter(function (tab) {
		// 	return tab.core === data.core;
		// });
		// if( tList.length ){
			this.closeSelectModule(e);
			this.openIndexView([{
				category: tab.category,
				key: tab.key,
				name: tab.name
			}]);
		// }
	},

	coreItemOver: function(e){
		var node = e.target;
		while (node && !node.hasClass("tl_coreItem")){ node = node.getParent();}
		if (node){
			node.addClass("menuItem_over");
			node.addClass("mainColor_bg");
		}
	},
	coreItemOut: function(e){
		var node = e.target;
		while (node && !node.hasClass("tl_coreItem")){ node = node.getParent();}
		if (node){
			node.removeClass("menuItem_over");
			node.removeClass("mainColor_bg");
		}
	},
	coreItemDown: function(e){
		var node = e.target;
		while (node && !node.hasClass("tl_coreItem")){ node = node.getParent();}
		if (node){
			node.removeClass("menuItem_over");
			node.addClass("mainColor_bg");
		}
	},
	coreItemUp: function(e){
		var node = e.target;
		while (node && !node.hasClass("tl_coreItem")){ node = node.getParent();}
		if (node){
			node.addClass("menuItem_over");
			node.removeClass("mainColor_bg");
		}
	},
	coreItemClick: function(e, tab){
		// var tList = (data.tabList || []).filter(function (tab) {
		// 	return tab.core === data.core;
		// });
		debugger;
		// if( tList.length ){
		// 	this.currentTabObj = tList[0];
			this.closeSelectModule(e);
			this.openIndexView([{
				category: tab.category,
				key: tab.key,
				name: tab.name
			}]);
		// }
	},

	coreListItemOver_multi: function(e){
		this.getEventTarget(e, "tl_coreListAppText").addClass("mainColor_bg_opacity");
	},
	coreListItemOut_multi: function(e){
		this.getEventTarget(e, "tl_coreListAppText").removeClass("mainColor_bg_opacity");
	},
	coreListItemDown_multi: function(e){
		this.getEventTarget(e, "tl_coreListAppText").addClass("mainColor_bg_opacity");
	},
	coreListItemUp_multi: function(e){
		this.getEventTarget(e, "tl_coreListAppText").removeClass("mainColor_bg_opacity");
	},
	coreListItemClick_multi: function(e, item){
		debugger;
		if( !this.curMultiSelectTabList )this.curMultiSelectTabList = [];
		var target = this.getEventTarget(e, "tl_coreListAppText");
		var index = -1;
		this.curMultiSelectTabList.each(function (tab, idx) {
			if( tab.key === item.key )index = idx;
		});
		if( index > -1 ){ //已经选择了
			target.getElement("i.checkbox").removeClass("o2icon-check_box").addClass("o2icon-check_box_outline_blank").removeClass("mainColor_color");
			this.curMultiSelectTabList.splice(index, 1);
		}else{ //还没有选择
			target.getElement("i.checkbox").removeClass("o2icon-check_box_outline_blank").addClass("o2icon-check_box").addClass("mainColor_color");
			this.curMultiSelectTabList.push(item);
		}
	},
	okMultiSelect: function(e){
		if( this.curMultiSelectTabList.length === 0){
			this.notice( this.lp.notSelectNotice, "info");
			return;
		}
		var data = this.curMultiSelectTabList.map(function (tab) {
			return {
				category: tab.category,
				key: tab.key,
				name: tab.name
			}
		});
		this.curMultiSelectTabList = [];
		this.multiSelect = false;
		this.closeSelectModule(e);
		this.openIndexView(data);
	},
	enableMultiSelect: function(e, data){
		this.curMultiSelectTabList = [];
		this.multiSelect = true;
		this.enableMultiNode.hide();
		this.disableMultiNode.setStyle("display","inline-block");
		this.tl_multiActionNode.show();
		this.tl_workTabListNode.setStyle("height", "calc( 100% - 90px )");
		this.tl_docTabListNode.setStyle("height", "calc( 100% - 90px )");
		this._loadTabList(function () {

		})
	},
	disableMultiSelect: function(e, data){
		this.curMultiSelectTabList = [];
		this.multiSelect = false;
		this.disableMultiNode.hide();
		this.enableMultiNode.setStyle("display","inline-block");
		this.tl_multiActionNode.hide();
		this.tl_workTabListNode.setStyle("height", "calc( 100% - 40px )");
		this.tl_docTabListNode.setStyle("height", "calc( 100% - 40px )");
		this._loadTabList(function () {

		})
	},
	searchCoreKeydown: function(e){
		if( e.keyCode === 13 ){
			this.searchCore();
		}
	},
	searchCore: function(){
		this.curMultiSelectTabList = [];
		var value = this.searchCoreInput.get("value");
		if( !value ){
			this._loadTabList();
		}else{
			this._loadTabList( function () {
				this.tl_contentWrap.getElements(".tl_coreListApp").each(function (el) {
					var coreListNode = el.getElement(".tl_coreListAppTitle");

					var isCoreListMatch = coreListNode.get("text").contains( value );
					var coreItems = el.getElements(".tl_coreItemArea");
					var coreItemMatchCount = 0;
					coreItems.each(function (ele) {
						if( ele.get("text").contains( value ) ){
							coreItemMatchCount++;
						}
					});

					if( !isCoreListMatch && coreItemMatchCount === 0 ){
						el.hide()
					}

				})
			}.bind(this));
		}
	},
	recordStatus: function () {
		return this.view ? this.view.recordStatus() : "";
	}
});
