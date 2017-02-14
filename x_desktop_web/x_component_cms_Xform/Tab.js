MWF.xDesktop.requireApp("cms.Xform", "$Module", null, false);
MWF.require("MWF.widget.Tab", null, false);
MWF.xApplication.cms.Xform.Tab = MWF.CMSTab =  new Class({
	Extends: MWF.CMS$Module,

	_loadUserInterface: function(){
        this.elements = [];
        this.containers = [];
		this.tab = new MWF.widget.Tab(this.node, {"style": "formMobile"});
		
		this._setTabWidgetStyles();
		
		this.tab.tabNodeContainer = this.node.getFirst("div");
		this.tab.contentNodeContainer = this.node.getLast("div");
		this.tab.load();
		
		var tabs = this.tab.tabNodeContainer.getChildren("div");
		var contents = this.tab.contentNodeContainer.getChildren("div");
		tabs.each(function(tab, idx){
			
			this.tab.rebuildTab(contents[idx], contents[idx].getFirst(), tab);
		}.bind(this));
		
		this.tab.pages[0]._showTab();
        this.loadSubModule();
	},
    loadSubModule: function(){
        this.tab.pages.each(function(page){
            var node = page.tabNode;
            var json = this.form._getDomjson(node);
            var tab = this;
            var module = this.form._loadModule(json, node, function(){
                this.tab = tab;
            });
            this.elements.push(module);
            this.form.modules.push(module);

            node = page.contentNode;
            json = this.form._getDomjson(node);
            tab = this;
            module = this.form._loadModule(json, node, function(){
                this.tab = tab;
            });
            this.containers.push(module);
            this.form.modules.push(module);

        }.bind(this));
    },

	_setTabWidgetStyles: function(){
		this.tab.css.tabNode = this.json.tabStyles;
		this.tab.css.tabTextNode = this.json.tabTextStyles;
		this.tab.css.tabNodeCurrent = this.json.tabCurrentStyles;
		this.tab.css.tabTextNodeCurrent = this.json.tabTextCurrentStyles;
	},
}); 