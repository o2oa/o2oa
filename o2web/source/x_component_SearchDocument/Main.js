MWF.xApplication.SearchDocument.options.multitask = false;
MWF.xApplication.SearchDocument.Main = new Class({
	Extends: MWF.xApplication.Common.Main,
	Implements: [Options, Events],

	options: {
		"style1": "default",
		"style": "default",
		"name": "SearchDocument",
		"mvcStyle": "style.css",
		"icon": "icon.png",
		"title": MWF.xApplication.SearchDocument.LP.title
	},
	onQueryLoad: function(){
		this.lp = MWF.xApplication.SearchDocument.LP;
	},
	loadApplication: function(callback){
		o2.requireApp("SearchDocument", "SearchView", function () {
			var query = this.status ? this.status.query : "";
			if( !query )query = this.options.query || "";
			this.view = new MWF.xApplication.SearchDocument.SearchView(this.content, this, {query : query || ""});
		}.bind(this), false);
		// var url = this.path+this.options.style+"/main.html";
		// this.content.loadHtml(url, {"bind": {"lp": this.lp}, "module": this}, function(){
		// 	this.loadMenu();
		// }.bind(this));
	},
	doSearch: function(query){
		o2.requireApp("SearchDocument", "SearchView", function () {
			this.content.empty();
			this.view = new MWF.xApplication.SearchDocument.SearchView(this.content, this, {query : query || ""});
		}.bind(this), false);
	},

	// loadMenu: function(){
	// 	this.getMenuJson( function (json) {
	// 		this.menuArea.loadHtml(this.path+this.options.style+"/menu.html", {"bind": {"lp": this.lp, "data": json}, "module": this}, function(){
	// 			this.menuArea.getElement(".menuItem").click();
	// 			// this[json[0].action]();
	// 		}.bind(this));
	// 	}.bind(this));
	// },
	// loadTaskView: function(){
	// 	o2.Actions.load("x_processplatform_assemble_surface").TaskAction.listMyPaging(1,5, function(json){
	// 		this.taskListView.loadHtml(this.path+this.options.style+"/taskView.html", {"bind": {"lp": this.lp, "data": json.data}, "module": this}, function(){
	// 			this.doSomething();
	// 		}.bind(this));
	// 	}.bind(this));
	// },
	// doSomething: function(){
	//
	// },

	getEventTarget: function(e, className) {
		var parentItem = e.target;
		if( parentItem.hasClass(className) )return parentItem;
		while ( parentItem && !parentItem.hasClass(className) ){
			parentItem = parentItem.getParent();
			if( parentItem.hasClass(className) )return parentItem;
		}
	},
	recordStatus: function () {
		return this.view ? this.view.recordStatus() : "";
	}
	// switchMenuItem: function(e){
	// 	var childNode = this.getEventTarget(e, "menuItem").getNext();
	// 	if( childNode.getStyle("display") === "none" ){
	// 		childNode.show();
	// 	}else{
	// 		childNode.hide();
	// 	}
	// },
	// menuItemOver: function(e){
	// 	this.getEventTarget(e, "menuItem").addClass('menuItem_over');
	// },
	// menuItemOut: function(e){
	// 	this.getEventTarget(e, "menuItem").removeClass('menuItem_over');
	// },
	// selectMenuItem: function(action, e){
	// 	if( this.currentMenuItem ){
	// 		this.currentMenuItem.removeClass('menuItem_current');
	// 		this.currentMenuItem.removeClass('mainColor_color');
	// 	}
	// 	var menuItem = this.getEventTarget(e, "menuItem")
	// 	menuItem.addClass('menuItem_current');
	// 	menuItem.addClass('mainColor_color');
	// 	this.currentMenuItem = menuItem;
	// 	if( this[action] )this[action]();
	// },
	// openSearchView: function(){
	// 	// this.contentArea.empty();
	// 	if(this.view)this.view.destroy();
	// 	o2.requireApp("SearchDocument", "SearchView", function () {
	// 		this.view = new MWF.xApplication.SearchDocument.SearchView(this.contentArea, this, {});
	// 	}.bind(this), false);
	//
	// }
});
