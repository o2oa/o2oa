MWF.widget = MWF.widget || {};
MWF.widget.Tab = new Class({
	Implements: [Options, Events],
    Extends: MWF.widget.Common,
	options: {
		"style": "default"
	},
	initialize: function(container, options){
		this.setOptions(options);
		this.pages = [];

		this.path = MWF.defaultPath+"/widget/$Tab/";
		this.cssPath = MWF.defaultPath+"/widget/$Tab/"+this.options.style+"/css.wcss";
		this._loadCss();
        this.css = Object.clone(this.css);
		this.showPage = null;

		this.node = $(container);
	},
	load: function(){
		if (this.fireEvent("queryLoad")){

			if (!this.tabNodeContainer) this.tabNodeContainer = new Element("div");
			this.tabNodeContainer.set("styles", this.css.tabNodeContainer);
			this.tabNodeContainer.inject(this.node);
			
			if (!this.contentNodeContainer) this.contentNodeContainer = new Element("div");
			this.contentNodeContainer.set("name", "MWFcontentNodeContainer");
			this.contentNodeContainer.set("styles", this.css.contentNodeContainer);
			this.contentNodeContainer.inject(this.node);
			
			this.fireEvent("postLoad");
		}
	},
	rebuildTab: function(contentAreaNode,contentNode, pageNode){

		var tabPage = new MWF.widget.TabPage(contentNode, "", this, {"isClose": false});
		
		tabPage.contentNodeArea = contentAreaNode;
		tabPage.tabNode = pageNode;
		tabPage.textNode = pageNode.getFirst();
		tabPage.closeNode = tabPage.textNode.getFirst();
		tabPage.options.title = tabPage.textNode.get("text");
		
		tabPage.load();
		this.pages.push(tabPage);
		return tabPage;
		
	},
	addTab: function(node, title, isclose, pageNode){
		var tabPage = new MWF.widget.TabPage(node, title, this, {"isClose": isclose, "tabNode": pageNode});
		tabPage.load();
		this.pages.push(tabPage);
		return tabPage;
	}

});

MWF.widget.TabPage = new Class({
	Implements: [Options, Events],
	options: {
		"isClose": true,
		"tabNode": null,
		"title": ""
	},

	initialize: function(node, title, tab, options){
		this.setOptions(options);
		this.options.title = title;
		this.tab = tab;
		this.contentNode = $(node);
	},
	load: function(){
		if (!this.contentNodeArea) this.contentNodeArea = new Element("div");
		this.contentNodeArea.set("styles", this.tab.css.contentNodeArea);
		
		if (!this.tabNode) this.tabNode = new Element("div");
		this.tabNode.set("styles", this.tab.css.tabNode);
		this.tabNode.addEvent("mousedown", function(event){event.stop();});
		
		if (!this.textNode) this.textNode = new Element("div").inject(this.tabNode);
		this.textNode.set({
			"styles": this.tab.css.tabTextNode,
			"text": this.options.title
		});
		this.tabNode.addEvent("click", this._showTab.bind(this));

		if (this.options.isClose){
			if (!this.closeNode) this.closeNode = new Element("div").inject(this.tabNode);
			this.closeNode.set({
				"styles": this.tab.css.tabCloseNode
			});
			this.closeNode.addEvent("click", this.closeTab.bind(this));
		}
		
		this.contentNodeArea.inject(this.tab.contentNodeContainer);
		this.tabNode.inject(this.tab.tabNodeContainer);
		this.contentNode.inject(this.contentNodeArea);
	},
	_showTab: function(){
		this.showTabIm();
	},
	showTabIm: function(callback){
		if (!this.isShow){
			this.tab.pages.each(function(page){
				if (page.isShow) page.hideIm();
			});
			this.showIm(callback);
		}
	},
	showTab: function(callback){
		if (!this.isShow){
			this.tab.pages.each(function(page){
				if (page.isShow) page.hide();
			});
			this.show(callback);
		}
	},
	showIm: function(callback){
		this.fireEvent("queryShow");
		this.tabNode.set("styles", this.tab.css.tabNodeCurrent);
		this.textNode.set("styles", this.tab.css.tabTextNodeCurrent);
		if (this.closeNode) this.closeNode.set("styles", this.tab.css.tabCloseNodeCurrent);
		
		this.contentNodeArea.setStyle("display", "block");
		this.contentNodeArea.setStyle("opacity", 1);
		
		this.isShow = true;
		this.tab.showPage = this;
		if (callback) callback();
		this.fireEvent("show");
		this.fireEvent("postShow");
	},
	show: function(callback){
		this.fireEvent("queryShow");
		this.tabNode.set("styles", this.tab.css.tabNodeCurrent);
		this.textNode.set("styles", this.tab.css.tabTextNodeCurrent);
		if (this.closeNode) this.closeNode.set("styles", this.tab.css.tabCloseNodeCurrent);
		
		this.contentNodeArea.setStyle("display", "block");
		this.contentNodeArea.setStyle("opacity", 1);
		if (!this.morph){
			this.morph = new Fx.Morph(this.contentNodeArea, {duration: 100});
		}
		this.morph.start({
			"opacity": 1
		}).chain(function(){
			this.isShow = true;
			this.tab.showPage = this;
			if (callback) callback();
			this.fireEvent("postShow");
		}.bind(this));
		this.fireEvent("show");
	},
	hideIm: function(){
		if (this.isShow){
			this.fireEvent("queryHide");
			this.tabNode.set("styles", this.tab.css.tabNode);
			this.textNode.set("styles", this.tab.css.tabTextNode);
			if (this.closeNode) this.closeNode.set("styles", this.tab.css.tabCloseNode);
		
			this.contentNodeArea.setStyle("display", "none");
			this.contentNodeArea.setStyle("opacity", 0);

			this.isShow = false;
			this.fireEvent("hide");
			this.fireEvent("postHide");
		}
	},
	hide: function(){
		if (this.isShow){
			this.fireEvent("queryHide");
			this.tabNode.set("styles", this.tab.css.tabNode);
			this.textNode.set("styles", this.tab.css.tabTextNode);
			if (this.closeNode) this.closeNode.set("styles", this.tab.css.tabCloseNode);
		
			if (!this.morph){
				this.morph = new Fx.Morph(this.contentNodeArea, {duration: 100});
			}
			this.morph.start({
				"opacity": 0
			}).chain(function(){

				this.contentNodeArea.setStyle("display", "none");
				this.isShow = false;
				this.fireEvent("postHide");
			}.bind(this));
			this.fireEvent("hide");
		}
	},
	closeTab: function(){
        this.fireEvent("queryClose");
		var prevPage = this.getPrevPage();
		this.contentNodeArea.destroy();
		this.tabNode.destroy();
		var tmp = [];
		
		this.tab.pages = this.tab.pages.erase(this);
		
//		this.tab.pages.each(function(item){
//			if (item!=this){
//				tmp.push(item);
//			}
//		});
//		this.tab.pages = tmp;
		
		if (prevPage){
			prevPage.showTab();
		}else{
			if (this.tab.pages.length) this.tab.pages[this.tab.pages.length-1].showTab();
		}
		this.fireEvent("close");
	},
	getPrevPage: function(){
		var idx = this.tab.pages.indexOf(this);
		var prevIdx = idx-1;
		if (prevIdx<0){
			return null;
		}else{
			return this.tab.pages[prevIdx];
		}
	}
});