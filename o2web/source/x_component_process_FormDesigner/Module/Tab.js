MWF.xApplication.process.FormDesigner.Module = MWF.xApplication.process.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.$Component", null, false);
MWF.xDesktop.requireApp("process.FormDesigner", "Module.Tab$Page", null, false);
MWF.xDesktop.requireApp("process.FormDesigner", "Module.Tab$Content", null, false);
MWF.require("MWF.widget.Tab", null, false);
MWF.xApplication.process.FormDesigner.Module.Tab = MWF.FCTab = new Class({
	Extends: MWF.FC$Component,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"propertyPath": "../x_component_process_FormDesigner/Module/Tab/tab.html",
		"actions": [
			{
				"name": "move",
				"icon": "move1.png",
				"event": "mousedown",
				"action": "move",
				"title": MWF.APPFD.LP.formAction.move
			},
			{
				"name": "copy",
				"icon": "copy1.png",
				"event": "mousedown",
				"action": "copy",
				"title": MWF.APPFD.LP.formAction.copy
			},
			{
				"name": "add",
				"icon": "add.png",
				"event": "click",
				"action": "addPage",
				"title": MWF.APPFD.LP.formAction.add
			},
			{
				"name": "delete",
				"icon": "delete1.png",
				"event": "click",
				"action": "delete",
				"title": MWF.APPFD.LP.formAction["delete"]
			}
		]
	},
	
	initialize: function(form, options){
		this.setOptions(options);
	
		this.path = "../x_component_process_FormDesigner/Module/Tab/";
		this.cssPath = "../x_component_process_FormDesigner/Module/Tab/"+this.options.style+"/css.wcss";

		this._loadCss();
		this.moduleType = "component";
		this.moduleName = "tab";
		
		this.form = form;
		this.container = null;
		this.containerNode = null;
		this.containers = [];
		this.elements = [];
	},
    clearTemplateStyles: function(styles){
        if (styles){
            if (styles.styles) this.removeStyles(styles.styles, "styles");
            if (styles.properties) this.removeStyles(styles.properties, "properties");
            if (styles.tabAreaStyles) this.removeStyles(styles.tabAreaStyles, "tabNodeContainer");
            if (styles.contentAreaStyles) this.removeStyles(styles.contentAreaStyles, "contentNodeContainer");
            if (styles.tabStyles) this.removeStyles(styles.tabStyles, "tabStyles");
            if (styles.tabTextStyles) this.removeStyles(styles.tabTextStyles, "tabTextStyles");
            if (styles.tabCurrentStyles) this.removeStyles(styles.tabCurrentStyles, "tabCurrentStyles");
            if (styles.tabCurrentTextStyles) this.removeStyles(styles.tabCurrentTextStyles, "tabTextCurrentStyles");
        }
    },

    setTemplateStyles: function(styles){
        if (styles.styles) this.copyStyles(styles.styles, "styles");
        if (styles.properties) this.copyStyles(styles.properties, "properties");
        if (styles.tabAreaStyles) this.copyStyles(styles.tabAreaStyles, "tabNodeContainer");
        if (styles.contentAreaStyles) this.copyStyles(styles.contentAreaStyles, "contentNodeContainer");
        if (styles.tabStyles) this.copyStyles(styles.tabStyles, "tabStyles");
        if (styles.tabTextStyles) this.copyStyles(styles.tabTextStyles, "tabTextStyles");
        if (styles.tabCurrentStyles) this.copyStyles(styles.tabCurrentStyles, "tabCurrentStyles");
        if (styles.tabCurrentTextStyles) this.copyStyles(styles.tabCurrentTextStyles, "tabTextCurrentStyles");
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

        var style = "form";
        if (this.form.options.mode=="Mobile") style = "mobileForm";

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
		debugger;
        var style = "form";
        if (this.form.options.mode=="Mobile") style = "mobileForm";

		//debugger;
		var menuNode = this.node.getElement(".MWFMenu");
		if (menuNode) menuNode.destroy();

		this.tabWidget = new MWF.widget.Tab(this.node, {"style": style});
        this._loadPageStyle();
		//this._setTabWidgetStyles();

		this.tabWidget.tabNodeContainer = this.node.getFirst();
		this.tabWidget.contentNodeContainer = this.tabWidget.tabNodeContainer.getNext();

        var lastNode = this.tabWidget.tabNodeContainer.getLast();
        var tmpTabDivs;
        if (lastNode && lastNode.hasClass("tabNodeContainerLeft")){
            this.tabWidget.tabNodeContainerRight = this.tabWidget.tabNodeContainer.getFirst();
            this.tabWidget.tabNodeContainerLeft = lastNode;
            this.tabWidget.tabNodeContainerArea = lastNode.getFirst();

            this.tabWidget.load();
            tmpTabDivs = this.tabWidget.tabNodeContainerArea.getChildren("div");
        }else{
            tmpTabDivs = this.tabWidget.tabNodeContainer.getChildren("div");
            this.tabWidget.load();
        }
		
		var tmpContentDivs = this.tabWidget.contentNodeContainer.getChildren();
		//var tmpTabDivs = this.tabWidget.tabNodeContainer.getChildren();
		
		tmpContentDivs.each(function(tmpContentDiv, idx){
			var tmpTabDiv = tmpTabDivs[idx];
			var tabPage = new MWF.widget.TabPage(tmpContentDiv.getFirst(), "", this.tabWidget, {"isClose": false});
			tabPage.contentNodeArea = tmpContentDiv;
			if (tmpTabDiv){
				tabPage.tabNode = tmpTabDiv;
				tabPage.textNode = tmpTabDiv.getFirst();
				if(tabPage.textNode)tabPage.closeNode = tabPage.textNode.getFirst();
			}
			tabPage.load();
			this.tabWidget.pages.push(tabPage);
		}.bind(this));
	},
	
	_loadPageStyle: function(){
        if (!this.json.tabNodeContainer || !Object.keys(this.json.tabNodeContainer).length) this.json.tabNodeContainer = Object.clone(this.tabWidget.css.tabNodeContainer);
        if (!this.json.contentNodeContainer || !Object.keys(this.json.contentNodeContainer).length) this.json.contentNodeContainer = Object.clone(this.tabWidget.css.contentNodeContainer);
        if (!this.json.tabStyles || !Object.keys(this.json.tabStyles).length) this.json.tabStyles = Object.clone(this.tabWidget.css.tabNode);
        if (!this.json.tabTextStyles || !Object.keys(this.json.tabTextStyles).length) this.json.tabTextStyles = Object.clone(this.tabWidget.css.tabTextNode);
        if (!this.json.tabCurrentStyles || !Object.keys(this.json.tabCurrentStyles).length) this.json.tabCurrentStyles = Object.clone(this.tabWidget.css.tabNodeCurrent);
        if (!this.json.tabTextCurrentStyles || !Object.keys(this.json.tabTextCurrentStyles).length) this.json.tabTextCurrentStyles = Object.clone(this.tabWidget.css.tabTextNodeCurrent);
        if (!this.json.contentNodeContainer || !Object.keys(this.json.contentNodeContainer).length) this.json.contentNodeContainer.clear = "both";
		this._setTabWidgetStyles();
	},
	_setTabWidgetStyles: function(){
        this.tabWidget.css.tabNodeContainer = this.json.tabNodeContainer;
        this.tabWidget.css.contentNodeContainer = this.json.contentNodeContainer;
		this.tabWidget.css.tabNode = this.json.tabStyles;
		this.tabWidget.css.tabTextNode = this.json.tabTextStyles;
		this.tabWidget.css.tabNodeCurrent = this.json.tabCurrentStyles;
        this.tabWidget.css.contentNodeContainer.clear = "both";
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
					tabPage = new MWF.FCTab$Page(this, page);
					tabPage.page = page;
					tabPage.load(moduleData, page.tabNode, this);
				}else{
					var moduleData = Object.clone(data);
					Object.merge(moduleData, json);
					Object.merge(json, moduleData);
					tabPage = new MWF.FCTab$Page(this, page);
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
					tabContent = new MWF.FCTab$Content(this, page);
					tabContent.page = page;
					tabContent.load(moduleData, page.contentNode, this);
				}else{
					var moduleData = Object.clone(data);
					Object.merge(moduleData, json);
					Object.merge(json, moduleData);
					tabContent = new MWF.FCTab$Content(this, page);
					tabContent.page = page;
					tabContent.load(json, page.contentNode, this);
				}
				if (tabContent) this.containers.push(tabContent);
			}.bind(this));
		}.bind(this));
	},
	_setEditStyle_custom: function(name){
        if (name=="tabNodeContainer"){
            this.tabWidget.tabNodeContainer.clearStyles();
            this.tabWidget.tabNodeContainer.setStyles(this.json.tabNodeContainer);
            this._setTabWidgetStyles();
        }
        if (name=="contentNodeContainer"){
            this.tabWidget.contentNodeContainer.clearStyles();
            this.tabWidget.contentNodeContainer.setStyles(this.json.contentNodeContainer);
            this._setTabWidgetStyles();
        }
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
    setTabStyles: function(){
        this._setEditStyle_custom("tabNodeContainer");
        this._setEditStyle_custom("contentNodeContainer");
        this._setEditStyle_custom("tabStyles");
        this._setEditStyle_custom("tabTextStyles");
        this._setEditStyle_custom("tabCurrentStyles");
        this._setEditStyle_custom("tabTextCurrentStyles");
    },
    setAllStyles: function(){
        this.setPropertiesOrStyles("styles");
        this.setPropertiesOrStyles("properties");

        this.setTabStyles();

        //this.tabWidget.pages.each(function(page){
        //    //if (page.isShow){
        //        page.tabNode.clearStyles();
        //        //page.tabNode.setStyles(this.json.tabCurrentStyles);
        //        //
        //        //Object.each(this.json.tabCurrentStyles, function(v, k){
        //        //    page.tabNode.setStyle(k, v);
        //        //}.bind(this));
        //    //}
        //}.bind(this));

        this.reloadMaplist();
    },

	addPage: function(){
		tabNode = new Element("div");
		var page = this.tabWidget.addTab(tabNode, "page", false);
	
		this.form.getTemplateData("Tab$Page", function(data){
			var moduleData = Object.clone(data);
			moduleData.name = page.tabNode.get("text");
			var tabPage = new MWF.FCTab$Page(this, page);
			tabPage.load(moduleData, page.tabNode, this);
			this.elements.push(tabPage);
		}.bind(this));
		this.form.getTemplateData("Tab$Content", function(data){
			var moduleData = Object.clone(data);
			var tabContent = new MWF.FCTab$Content(this, page);
			tabContent.load(moduleData, page.contentNode, this);
			this.containers.push(tabContent);
		}.bind(this));
		page.showTabIm();
		return page;
	},

    copyComponentJsonData: function(newNode, pid){

        var tabNodeContainer = newNode.getFirst();
        //var contentNodeContainer = newNode.getLast();
		var contentNodeContainer = tabNodeContainer.getNext();

        var tmpContentDivs = contentNodeContainer.getChildren();

		var lastNode = tabNodeContainer.getLast();
		var tmpTabDivs;
		if (lastNode && lastNode.hasClass("tabNodeContainerLeft")){
			var tabNodeContainerArea = lastNode.getFirst();
			var menuNode = newNode.getElement(".MWFMenu");
			if (menuNode) menuNode.destroy();
			tmpTabDivs = tabNodeContainerArea.getChildren("div");
		}else{
			tmpTabDivs = tabNodeContainer.getChildren("div");
		}

       // var tmpTabDivs = tabNodeContainer.getChildren();

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
    },
	_preprocessingModuleData: function(){
		this.recoveryWidgetstyle = this.node.get("style");
		this.node.clearStyles();
		//if (this.initialStyles) this.node.setStyles(this.initialStyles);
		this.json.recoveryStyles = Object.clone(this.json.styles);
		if (this.json.recoveryStyles) Object.each(this.json.recoveryStyles, function(value, key){
			if ((value.indexOf("x_processplatform_assemble_surface")!=-1 || value.indexOf("x_portal_assemble_surface")!=-1)){
				//需要运行时处理
			}else{
				this.node.setStyle(key, value);
				delete this.json.styles[key];
			}
		}.bind(this));
	},
	_recoveryModuleData: function(){
		if (this.recoveryWidgetstyle) this.node.set("style", this.recoveryWidgetstyle);
		this.recoveryWidgetstyle = null;
		if (this.json.recoveryStyles) this.json.styles = this.json.recoveryStyles;
		this.json.recoveryStyles = null;
	}
	// setCustomStyles: function(){
	// 	this._recoveryModuleData();
	// 	//debugger;
	// 	var border = this.node.getStyle("border");
	// 	this.node.clearStyles();
	// 	this.node.setStyles(this.css.moduleNode);
	//
	// 	if (this.initialStyles) this.node.setStyles(this.initialStyles);
	// 	this.node.setStyle("border", border);
	//
	// 	if (this.json.styles) Object.each(this.json.styles, function(value, key){
	// 		if ((value.indexOf("x_processplatform_assemble_surface")!=-1 || value.indexOf("x_portal_assemble_surface")!=-1)){
	// 			var host1 = MWF.Actions.getHost("x_processplatform_assemble_surface");
	// 			var host2 = MWF.Actions.getHost("x_portal_assemble_surface");
	// 			if (value.indexOf("/x_processplatform_assemble_surface")!==-1){
	// 				value = value.replace("/x_processplatform_assemble_surface", host1+"/x_processplatform_assemble_surface");
	// 			}else if (value.indexOf("x_processplatform_assemble_surface")!==-1){
	// 				value = value.replace("x_processplatform_assemble_surface", host1+"/x_processplatform_assemble_surface");
	// 			}
	// 			if (value.indexOf("/x_portal_assemble_surface")!==-1){
	// 				value = value.replace("/x_portal_assemble_surface", host2+"/x_portal_assemble_surface");
	// 			}else if (value.indexOf("x_portal_assemble_surface")!==-1){
	// 				value = value.replace("x_portal_assemble_surface", host2+"/x_portal_assemble_surface");
	// 			}
	// 		}
	//
	// 		var reg = /^border\w*/ig;
	// 		if (!key.test(reg)){
	// 			if (key){
	// 				if (key.toString().toLowerCase()==="display"){
	// 					if (value.toString().toLowerCase()==="none"){
	// 						this.node.setStyle("opacity", 0.3);
	// 					}else{
	// 						this.node.setStyle("opacity", 1);
	// 						this.node.setStyle(key, value);
	// 					}
	// 				}else{
	// 					this.node.setStyle(key, value);
	// 				}
	// 			}
	// 		}
	// 	}.bind(this));
	// }
});
