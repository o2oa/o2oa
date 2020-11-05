o2.widget = o2.widget || {};
o2.widget.Tab = new Class({
	Implements: [Options, Events],
    Extends: o2.widget.Common,
	options: {
		"style": "default"
	},
	initialize: function(container, options){
		this.setOptions(options);
		this.pages = [];

		this.path = o2.session.path+"/widget/$Tab/";
		this.cssPath = o2.session.path+"/widget/$Tab/"+this.options.style+"/css.wcss";
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

			if (!this.tabNodeContainerRight) this.tabNodeContainerRight = new Element("div.tabNodeContainerRight");
            this.tabNodeContainerRight.set("styles", this.css.tabNodeContainerRight);
            this.tabNodeContainerRight.inject(this.tabNodeContainer);

            if (!this.tabNodeContainerLeft) this.tabNodeContainerLeft = new Element("div.tabNodeContainerLeft");
            this.tabNodeContainerLeft.set("styles", this.css.tabNodeContainerLeft);
            this.tabNodeContainerLeft.inject(this.tabNodeContainer);

            if (!this.tabNodeContainerArea) this.tabNodeContainerArea = new Element("div.tabNodeContainerArea");
            this.tabNodeContainerArea.set("styles", this.css.tabNodeContainerArea);
            this.tabNodeContainerArea.inject(this.tabNodeContainerLeft);


			if (!this.contentNodeContainer) this.contentNodeContainer = new Element("div");
			this.contentNodeContainer.set("name", "MWFcontentNodeContainer");
			this.contentNodeContainer.set("styles", this.css.contentNodeContainer);
			this.contentNodeContainer.inject(this.node);

            this.tabNodeContainerRight.addEvents({
                "mouseover": function(){this.tabNodeContainerRight.setStyles(this.css.tabNodeContainerRight_over)}.bind(this),
                "mouseout": function(){this.tabNodeContainerRight.setStyles(this.css.tabNodeContainerRight)}.bind(this)
            });

            o2.require("o2.xDesktop.Menu", function(){
                this.tabMenu = new o2.xDesktop.Menu(this.tabNodeContainerRight, {
                    "style": "tab",
                    "event": "click",
                    "container": this.tabNodeContainerRight,
                    "where": {"x": "right", "y": "bottom"},
                    "onQueryShow": function(){
                        this.loadOverMenu();
                    }.bind(this)
                });
                this.tabMenu.load();
            }.bind(this));
            this.tabNodeContainerRight.hide();
			
			this.fireEvent("postLoad");
		}
	},
	rebuildTab: function(contentAreaNode,contentNode, pageNode){

		var tabPage = new o2.widget.TabPage(contentNode, "", this, {"isClose": false});
		
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
		var tabPage = new o2.widget.TabPage(node, title, this, {"isClose": isclose, "tabNode": pageNode});
		tabPage.load();
		this.pages.push(tabPage);
		return tabPage;
	},
    loadOverMenu: function(e){
        this.tabMenu.clearItems();
        var _self = this;
		for (var n=this.showTabIndex; n<this.pages.length; n++){
			var page = this.pages[n];
            var menuItem = this.tabMenu.addMenuItem(page.options.title || page.tabNode.get("text"), "click", function(){
				_self.showOverPage(this);
			});
            menuItem.tabPage = page;
		}
	},
    showOverPage: function(item){
		var page = item.tabPage;
		if (page){
            this.pages.erase(page);
            this.pages.unshift(page);
            page.tabNode.inject(this.tabNodeContainerArea, "top");
            page.showTabIm();
            this.resize();
		}
	},

    resize: function(){
		var size = this.tabNodeContainerLeft.getSize();
		var tabWidth = 0;
        for (var i=0; i<this.pages.length; i++){
            var tabSize = this.pages[i].tabNode.getSize();
            tabWidth += tabSize.x;
            if (tabWidth>size.x) break;
		}
        this.showTabIndex = i;
		if (tabWidth>size.x && i<this.pages.length){
            this.tabNodeContainerRight.show();
		}else{
            this.tabNodeContainerRight.hide();
		}
	}

});

o2.widget.TabPage = new Class({
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
		this.tabNode.inject(this.tab.tabNodeContainerArea);
		this.contentNode.inject(this.contentNodeArea);

		this.tab.resize();
	},
	_showTab: function(){
		this.showTabIm();
	},
	showTabIm: function(callback){
		if (!this.isShow){
			// if (!this.tabNode.isIntoView()){
             //    this.tab.pages.erase(this);
             //    this.tab.pages.unshift(this);
             //    this.tabNode.inject(this.tab.tabNodeContainerArea, "top");
             //    this.tab.resize();
			// }
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
		this.tabNode.setStyle("display","");
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
		this.tabNode.setStyle("display","");
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
	enableTab : function( notShow ){
		this.disabled = false;
		if( notShow ){
			this.tabNode.show();
		}else{
			this.showTab();
		}
	},
	disableTab : function( notShowSibling ){
		this.disabled = true;
		this.hideTab( notShowSibling );
	},
	hideTab: function( notShowSibling ){
		this.fireEvent("queryHide");
		this.tabNode.hide();
		this.contentNodeArea.hide();
		var tmp = [];

		if( !notShowSibling ){
			var prevPage = this.getPrevPage();
			if (prevPage && !prevPage.disabled){
				prevPage.showTab();
			}else{
				if (this.tab.pages.length){
					for( var i=0; i<this.tab.pages.length;  i++ ){
						var page = this.tab.pages[i];
						if( !page.disabled ){
							page.showTab();
							break;
						}
					}
					//this.tab.pages[this.tab.pages.length-1].showTab();
				}
			}
		}
		this.isShow = false;
		this.fireEvent("hide");
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
		
		if (prevPage && !prevPage.disabled){
			prevPage.showTab();
		}else{
			if (this.tab.pages.length){
				for( var i=0; i<this.tab.pages.length;  i++ ){
					var page = this.tab.pages[i];
					if( !page.disabled ){
						page.showTab();
						break;
					}
				}
			}
			//if (this.tab.pages.length) this.tab.pages[this.tab.pages.length-1].showTab();
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