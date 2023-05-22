MWF.xApplication.ftsearch.options.multitask = false;
MWF.xApplication.ftsearch.Main = new Class({
	Extends: MWF.xApplication.Common.Main,
	Implements: [Options, Events],

	options: {
		"style1": "default",
		"style": "default",
		"name": "ftsearch",
		"mvcStyle": "style.css",
		"icon": "icon.png",
		"title": MWF.xApplication.ftsearch.LP.title,
		"query":""
	},
	onQueryLoad: function(){
		this.lp = MWF.xApplication.ftsearch.LP;
	},
	loadApplication: function(callback){
		var url;
		if( layout.config.searchEnable === false){
			url = this.path+this.options.style+"/disabled.html";
			this.content.loadHtml(url, {"bind": {"lp": this.lp}});
		}else{
			url = this.path+this.options.style+"/main.html";
			this.content.loadHtml(url, {"bind": {"lp": this.lp}, "module": this}, function(){
				var query = this.options.query || "";
				if( this.status && this.status.query ){
					query = this.status.query;
				}
				if( query ){
					this.openFTSearchView( query );
				}
				this.status = null;
			}.bind(this));
		}
	},
	getEventTarget: function(e, className) {
		var parentItem = e.target;
		if( parentItem.hasClass(className) )return parentItem;
		while ( parentItem && !parentItem.hasClass(className) ){
			parentItem = parentItem.getParent();
			if( parentItem.hasClass(className) )return parentItem;
		}
	},
	searchKeydown: function(e){
		if( e.keyCode === 13 ){
			var query = this.searchInput.get("value");
			if(query)this.openFTSearchView( query );
		}
	},
	searchClick: function(e){
		var query = this.searchInput.get("value");
		if(query)this.openFTSearchView( query );
	},
	doSearch: function(query){
		this.openFTSearchView(query);
	},
	openFTSearchView: function( query ){
		if( this.view && this.view.destroy )this.view.destroy();
		this.contentArea.empty();
		o2.requireApp("ftsearch", "FTSearchView", function () {
			this.view = new MWF.xApplication.ftsearch.FTSearchView(this.contentArea, this, {
				query: query
			});
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

	recordStatus: function () {
		return this.view ? this.view.recordStatus() : "";
	}
});
