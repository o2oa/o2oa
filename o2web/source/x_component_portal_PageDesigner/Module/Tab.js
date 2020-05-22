MWF.xApplication.process.FormDesigner.Module = MWF.xApplication.process.FormDesigner.Module || {};
MWF.xDesktop.requireApp("portal.PageDesigner", "Module.Tab$Page", null, false);
MWF.xDesktop.requireApp("portal.PageDesigner", "Module.Tab$Content", null, false);
MWF.require("MWF.widget.Tab", null, false);
MWF.xApplication.portal.PageDesigner.Module.Tab = MWF.PCTab = new Class({
	Extends: MWF.FCTab,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"propertyPath": "../x_component_portal_PageDesigner/Module/Tab/tab.html"
	},
	
	initialize: function(form, options){
		this.setOptions(options);
	
		this.path = "../x_component_portal_PageDesigner/Module/Tab/";
		this.cssPath = "../x_component_portal_PageDesigner/Module/Tab/"+this.options.style+"/css.wcss";

		this._loadCss();
		this.moduleType = "component";
		this.moduleName = "tab";
		
		this.form = form;
		this.container = null;
		this.containerNode = null;
		this.containers = [];
		this.elements = [];
	},
    _getElements: function(){
        //this.elements.push(this);
        if (!this.tabWidget) this._createTabWidget();
        this.form.getTemplateData("Tab$Page", function(data){
            this.tabWidget.pages.each(function(page){
                var tabPage = null;
                var json = this.form.getDomjson(page.tabNode);
                if (!json){
                    var moduleData = Object.clone(data);
                    moduleData.name = page.tabNode.get("text");
                    tabPage = new MWF.PCTab$Page(this, page);
                    tabPage.page = page;
                    tabPage.load(moduleData, page.tabNode, this);
                }else{
                    tabPage = new MWF.PCTab$Page(this, page);
                    tabPage.page = page;
                    tabPage.load(json, page.tabNode, this);
                }
                if (tabPage) this.elements.push(tabPage);
            }.bind(this));
        }.bind(this));
        if (!this.tabWidget.showPage) this.tabWidget.pages[0].showTabIm();
    },
    _getContainers: function(){
        if (!this.tabWidget) this._createTabWidget();
        this.form.getTemplateData("Tab$Content", function(data){
            this.tabWidget.pages.each(function(page){
                var tabContent = null;
                var json = this.form.getDomjson(page.contentNode);
                if (!json){
                    var moduleData = Object.clone(data);
                    tabContent = new MWF.PCTab$Content(this, page);
                    tabContent.page = page;
                    tabContent.load(moduleData, page.contentNode, this);
                }else{
                    tabContent = new MWF.PCTab$Content(this, page);
                    tabContent.page = page;
                    tabContent.load(json, page.contentNode, this);
                }
                if (tabContent) this.containers.push(tabContent);
            }.bind(this));
        }.bind(this));
    },
    addPage: function(){
        tabNode = new Element("div");
        var page = this.tabWidget.addTab(tabNode, "page", false);

        this.form.getTemplateData("Tab$Page", function(data){
            var moduleData = Object.clone(data);
            moduleData.name = page.tabNode.get("text");
            var tabPage = new MWF.PCTab$Page(this, page);
            tabPage.load(moduleData, page.tabNode, this);
            this.elements.push(tabPage);
        }.bind(this));
        this.form.getTemplateData("Tab$Content", function(data){
            var moduleData = Object.clone(data);
            var tabContent = new MWF.PCTab$Content(this, page);
            tabContent.load(moduleData, page.contentNode, this);
            this.containers.push(tabContent);
        }.bind(this));
        page.showTabIm();
        return page;
    }
});