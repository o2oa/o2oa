MWF.xApplication.process.FormDesigner.Module = MWF.xApplication.process.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.$Container", null, false);
MWF.xApplication.process.FormDesigner.Module.Tab$Content = MWF.FCTab$Content = new Class({
	Extends: MWF.FC$Container,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"propertyPath": "../x_component_process_FormDesigner/Module/Tab$Content/tab$Content.html",
		"actions": [],
		"injectActions" : [
			{
				"name" : "top",
				"styles" : "injectActionTop",
				"event" : "click",
				"action" : "injectTop",
				"title": MWF.APPFD.LP.formAction["insertTop"]
			},
			{
				"name" : "bottom",
				"styles" : "injectActionBottom",
				"event" : "click",
				"action" : "injectBottom",
				"title": MWF.APPFD.LP.formAction["insertBottom"]
			}
		]
	},
	
	initialize: function(tab, page, options){
		this.setOptions(options);
		
		this.path = "../x_component_process_FormDesigner/Module/Tab$Content/";
		this.cssPath = "../x_component_process_FormDesigner/Module/Tab$Content/"+this.options.style+"/css.wcss";

		this._loadCss();
		this.moduleType = "container";
		this.moduleName = "tab$Content";
		
		this.form = tab.form;
		this.tab = tab;
		this.page = page;
	},
	_dragInLikeElement: function(module){
		return false;
	},
	load : function(json, node, parent){
		this.json = json;
		this.node= node;
		this.node.store("module", this);
		this.node.setStyles(this.css.moduleNode);

        if (!this.json.id){
	    	var id = this._getNewId(parent.json.id);
	    	this.json.id = id;
        }
		
		node.set({
			"MWFType": "tab$Content",
			"id": this.json.id
		});
		
		if (!this.form.json.moduleList[this.json.id]){
			this.form.json.moduleList[this.json.id] = this.json;
		}
		this._initModule();
		this._loadTreeNode(parent);

        this.setCustomStyles();
		
		this.parentContainer = this.treeNode.parentNode.module;
        this._setEditStyle_custom("id");

		this.parseModules();
        this.json.moduleName = this.moduleName;
	},
	
	_deleteModule: function(){
		this.form.moduleList.erase(this);
		this.form.moduleNodeList.erase(this.node);
		this.form.moduleElementNodeList.erase(this.node);

		//this.tab.containers.erase(this);
        this.node.destroy();
		this.actionArea.destroy();

		delete this.form.json.moduleList[this.json.id];
		this.json = null;
		delete this.json;

		this.treeNode.destroy();
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
		this.node.set("style", this.recoveryWidgetstyle);
		this.recoveryWidgetstyle = null;
	},
	setCustomStyles: function(){
		this._recoveryModuleData();
	}

});
