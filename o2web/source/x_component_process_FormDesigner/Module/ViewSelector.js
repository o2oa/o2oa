MWF.xApplication.process.FormDesigner.Module = MWF.xApplication.process.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.$Element", null, false);
MWF.xApplication.process.FormDesigner.Module.ViewSelector = MWF.FCViewSelector = new Class({
	Extends: MWF.xApplication.process.FormDesigner.Module.Button,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"propertyPath": "../x_component_process_FormDesigner/Module/ViewSelector/ViewSelector.html"
	},
	
	initialize: function(form, options){
		this.setOptions(options);
		
		this.path = "../x_component_process_FormDesigner/Module/ViewSelector/";
		this.cssPath = "../x_component_process_FormDesigner/Module/ViewSelector/"+this.options.style+"/css.wcss";

		this._loadCss();
		this.moduleType = "element";
		this.moduleName = "viewSelector";

		this.form = form;
		this.container = null;
		this.containerNode = null;
	}
});
