MWF.xApplication.process.FormDesigner.Module = MWF.xApplication.process.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.Log", null, false);
MWF.xApplication.process.FormDesigner.Module.OOLog = MWF.FCOOLog = new Class({
	Extends: MWF.FCLog,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"propertyPath": "../x_component_process_FormDesigner/Module/OOLog/OOLog.html"
	},
	
	initialize: function(form, options){
		this.setOptions(options);
		
		this.path = "../x_component_process_FormDesigner/Module/OOLog/";
		this.cssPath = "../x_component_process_FormDesigner/Module/OOLog/"+this.options.style+"/css.wcss";

		this._loadCss();
		this.moduleType = "element";
		this.moduleName = "OOLog";

		this.form = form;
		this.container = null;
		this.containerNode = null;
	}
});
