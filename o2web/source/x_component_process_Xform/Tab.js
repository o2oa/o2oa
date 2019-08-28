MWF.xDesktop.requireApp("process.Xform", "$Module", null, false);
MWF.require("MWF.widget.Tab", null, false);
MWF.xApplication.process.Xform.Tab = MWF.APPTab =  new Class({
	Extends: MWF.APP$Module,

	_loadUserInterface: function(){
        this.elements = [];
        this.containers = [];

        var style = "form";
        if (layout.mobile) style = "mobileForm";

		this.tab = new MWF.widget.Tab(this.node, {"style": style});
		
		this._setTabWidgetStyles();

		this.tab.tabNodeContainer = this.node.getFirst("div");
		this.tab.contentNodeContainer = this.tab.tabNodeContainer.getNext("div");

		var lastNode = this.tab.tabNodeContainer.getLast();
		var tabs;
		if (lastNode && lastNode.hasClass("tabNodeContainerLeft")){
            this.tab.tabNodeContainerRight = this.tab.tabNodeContainer.getFirst();
            this.tab.tabNodeContainerLeft = lastNode;
            this.tab.tabNodeContainerArea = lastNode.getFirst();

            var menuNode = this.node.getElement(".MWFMenu");
            if (menuNode) menuNode.destroy();
            this.tab.load();
            tabs = this.tab.tabNodeContainerArea.getChildren("div");
        }else{
            tabs = this.tab.tabNodeContainer.getChildren("div");
            this.tab.load();
        }

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

            if (page.isShow){
                this.showContentModule.call(page, this);
            }else{
                if (this.json.isDelay){
                    var _self = this;
                    page.showContentModuleFun = function(){_self.showContentModule.call(page, _self)};
                    page.addEvent("show", page.showContentModuleFun);
                }else{
                    this.showContentModule.call(page, this);
                }
            }
        }.bind(this));
    },
    showContentModule: function(_self){
        var node = this.contentNode;
        node.isLoadModule = true;
        json = _self.form._getDomjson(node);
        tab = _self;
        module = _self.form._loadModule(json, node, function(){
            this.tab = tab;
        });
        _self.containers.push(module);
        _self.form.modules.push(module);

        if (this.showContentModuleFun) this.removeEvent("show", this.showContentModuleFun);
    },

	_setTabWidgetStyles: function(){
        if (this.json.tabNodeContainer) this.tab.css.tabNodeContainer = Object.clone(this.json.tabNodeContainer);
        if (this.json.contentNodeContainer) this.tab.css.contentNodeContainer = Object.clone(this.json.contentNodeContainer);
		this.tab.css.tabNode = Object.clone(this.json.tabStyles);
		this.tab.css.tabTextNode = Object.clone(this.json.tabTextStyles);
		this.tab.css.tabNodeCurrent = Object.clone(this.json.tabCurrentStyles);
		this.tab.css.tabTextNodeCurrent = Object.clone(this.json.tabTextCurrentStyles);
	}
});
MWF.xApplication.process.Xform.tab$Page = MWF.APPTab$Page =  new Class({
    Extends: MWF.APP$Module
});
MWF.xApplication.process.Xform.tab$Content = MWF.APPTab$Content =  new Class({
    Extends: MWF.APP$Module,
    _loadUserInterface: function(){
        this.form._loadModules(this.node);
    }
});