MWF.xApplication.cms.FormDesigner.Module = MWF.xApplication.cms.FormDesigner.Module || {};
MWF.xDesktop.requireApp("cms.FormDesigner", "Module.$Component", null, false);
MWF.xDesktop.requireApp("cms.FormDesigner", "Module.Tab$Page", null, false);
MWF.xDesktop.requireApp("cms.FormDesigner", "Module.Tab$Content", null, false);
MWF.require("MWF.widget.Tab", null, false);
MWF.xApplication.cms.FormDesigner.Module.Tab = MWF.CMSFCTab = new Class({
	Extends: MWF.CMSFC$Component,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"propertyPath": "/x_component_cms_FormDesigner/Module/Tab/tab.html",
		"actions": [
			{
				"name": "move",
				"icon": "move1.png",
				"event": "mousedown",
				"action": "move",
				"title": MWF.CMSFD.LP.formAction.move
			},
			{
				"name": "copy",
				"icon": "copy1.png",
				"event": "mousedown",
				"action": "copy",
				"title": MWF.CMSFD.LP.formAction.copy
			},
			{
				"name": "add",
				"icon": "add.png",
				"event": "click",
				"action": "addPage",
				"title": MWF.CMSFD.LP.formAction.add
			},
			{
				"name": "delete",
				"icon": "delete1.png",
				"event": "click",
				"action": "delete",
				"title": MWF.CMSFD.LP.formAction["delete"]
			}
		]
	},
	
	initialize: function(form, options){
		this.setOptions(options);
	
		this.path = "/x_component_cms_FormDesigner/Module/Tab/";
		this.cssPath = "/x_component_cms_FormDesigner/Module/Tab/"+this.options.style+"/css.wcss";

		this._loadCss();
		this.moduleType = "component";
		this.moduleName = "tab";
		
		this.form = form;
		this.container = null;
		this.containerNode = null;
		this.containers = [];
		this.elements = [];
	},
	
	_createMoveNode: function(){
		this.moveNode = new Element("div", {
			"styles": this.css.moduleNodeMove
		}).inject(this.form.container);
		
		var divTab1 = new Element("div", {
			"styles": this.css.moduleNodeMove_tab_current
		});
		var divTab2 = new Element("div", {
			"styles": this.css.moduleNodeMove_tab
		});
		
		var divContent = new Element("div", {
			"styles": this.css.moduleNodeMove_content
		});
		
		divTab1.inject(this.moveNode);
		divTab2.inject(this.moveNode);
		divContent.inject(this.moveNode);
	},
	_createNode: function(callback){
		this.node = new Element("div", {
			"id": this.json.id,
			"MWFType": "tab",
			"styles": this.css.moduleNode,
			"events": {
				"selectstart": function(e){
					e.preventDefault();
				}
			}
			
		}).inject(this.form.node);

        var style = "form"
        if (this.form.options.mode=="Mobile") style = "formMobile";

		this.tabWidget = new MWF.widget.Tab(this.node, {"style": style});
		this._loadPageStyle();
		this.tabWidget.load();
		
		var tabNode = new Element("div");
		this.tabWidget.addTab(tabNode, "page1", false);
		
		tabNode = new Element("div");
		this.tabWidget.addTab(tabNode, "page2", false);
		
		tabNode = new Element("div");
		this.tabWidget.addTab(tabNode, "page3", false);
		
		tabNode = new Element("div");
		this.tabWidget.addTab(tabNode, "page4", false);
	},
	
	_createTabWidget: function(){
        var style = "form"
        if (this.form.options.mode=="Mobile") style = "formMobile";

		this.tabWidget = new MWF.widget.Tab(this.node, {"style": style});
		this._setTabWidgetStyles();
		this.tabWidget.tabNodeContainer = this.node.getFirst();
		this.tabWidget.contentNodeContainer = this.node.getLast();
		this.tabWidget.load();
		
		var tmpContentDivs = this.tabWidget.contentNodeContainer.getChildren();
		var tmpTabDivs = this.tabWidget.tabNodeContainer.getChildren();
		
		tmpContentDivs.each(function(tmpContentDiv, idx){
			var tmpTabDiv = tmpTabDivs[idx];
			var tabPage = new MWF.widget.TabPage(tmpContentDiv.getFirst(), "", this.tabWidget, {"isClose": false});
			tabPage.contentNodeArea = tmpContentDiv;
			if (tmpTabDiv){
				tabPage.tabNode = tmpTabDiv;
				tabPage.textNode = tmpTabDiv.getFirst();
				tabPage.closeNode = tabPage.textNode.getFirst();
			}
			tabPage.load();
			this.tabWidget.pages.push(tabPage);
		}.bind(this));
	},
	
	_loadPageStyle: function(){
		this.json.tabStyles = Object.clone(this.tabWidget.css.tabNode);
		this.json.tabTextStyles = Object.clone(this.tabWidget.css.tabTextNode);
		this.json.tabCurrentStyles = Object.clone(this.tabWidget.css.tabNodeCurrent);
		this.json.tabTextCurrentStyles = Object.clone(this.tabWidget.css.tabTextNodeCurrent);
		
		this._setTabWidgetStyles();
	},
	_setTabWidgetStyles: function(){
		this.tabWidget.css.tabNode = this.json.tabStyles;
		this.tabWidget.css.tabTextNode = this.json.tabTextStyles;
		this.tabWidget.css.tabNodeCurrent = this.json.tabCurrentStyles;
		this.tabWidget.css.tabTextNodeCurrent = this.json.tabTextCurrentStyles;
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
					tabPage = new MWF.CMSFCTab$Page(this, page);
					tabPage.page = page;
					tabPage.load(moduleData, page.tabNode, this);
				}else{
					tabPage = new MWF.CMSFCTab$Page(this, page);
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
					tabContent = new MWF.CMSFCTab$Content(this, page);
					tabContent.page = page;
					tabContent.load(moduleData, page.contentNode, this);
				}else{
					tabContent = new MWF.CMSFCTab$Content(this, page);
					tabContent.page = page;
					tabContent.load(json, page.contentNode, this);
				}
				if (tabContent) this.containers.push(tabContent);
			}.bind(this));
		}.bind(this));
	},
	_setEditStyle_custom: function(name){
		if (name=="tabStyles"){
			this.tabWidget.pages.each(function(page){
				if (!page.isShow){
					page.tabNode.clearStyles();
					page.tabNode.setStyles(this.json.tabStyles);
				}
			}.bind(this));
			this._setTabWidgetStyles();
		}
		if (name=="tabTextStyles"){
			this.tabWidget.pages.each(function(page){
				if (!page.isShow){
					page.textNode.clearStyles();
					page.textNode.setStyles(this.json.tabTextStyles);
				}
			}.bind(this));
			this._setTabWidgetStyles();
		}
		if (name=="tabCurrentStyles"){
			this.tabWidget.pages.each(function(page){
				if (page.isShow){
					page.tabNode.clearStyles();
					page.tabNode.setStyles(this.json.tabCurrentStyles);
				}
			}.bind(this));
			this._setTabWidgetStyles();
		}
		if (name=="tabTextCurrentStyles"){
			this.tabWidget.pages.each(function(page){
				if (page.isShow){
					page.textNode.clearStyles();
					page.textNode.setStyles(this.json.tabTextCurrentStyles);
				}
			}.bind(this));
			this._setTabWidgetStyles();
		}
	},
	
	addPage: function(){
		tabNode = new Element("div");
		var page = this.tabWidget.addTab(tabNode, "page", false);
	
		this.form.getTemplateData("Tab$Page", function(data){
			var moduleData = Object.clone(data);
			moduleData.name = page.tabNode.get("text");
			var tabPage = new MWF.CMSFCTab$Page(this, page);
			tabPage.load(moduleData, page.tabNode, this);
			this.elements.push(tabPage);
		}.bind(this));
		this.form.getTemplateData("Tab$Content", function(data){
			var moduleData = Object.clone(data);
			var tabContent = new MWF.CMSFCTab$Content(this, page);
			tabContent.load(moduleData, page.contentNode, this);
			this.containers.push(tabContent);
		}.bind(this));
		page.showTabIm();
		return page;
	},

    copyComponentJsonData: function(newNode, pid){

        var tabNodeContainer = newNode.getFirst();
        var contentNodeContainer = newNode.getLast();

        var tmpContentDivs = contentNodeContainer.getChildren();
        var tmpTabDivs = tabNodeContainer.getChildren();

        tmpContentDivs.each(function(tmpContentDiv, idx){
            var newContainerJson = Object.clone(this.containers[idx].json);
            var newElementJson = Object.clone(this.elements[idx].json);
            var tmpTabDiv = tmpTabDivs[idx];

            newContainerJson.id = this.containers[idx]._getNewId(pid);
            this.form.json.moduleList[newContainerJson.id] = newContainerJson;
            tmpContentDiv.getFirst().set("id", newContainerJson.id);

            newElementJson.id = this.elements[idx]._getNewId(pid);
            this.form.json.moduleList[newElementJson.id] = newElementJson;
            tmpTabDiv.set("id", newElementJson.id);

        }.bind(this));
    }
});