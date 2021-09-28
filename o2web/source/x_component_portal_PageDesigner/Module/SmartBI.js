MWF.xApplication.portal.PageDesigner.Module.SmartBI = MWF.PCSmartBI = new Class({
	Extends: MWF.FCSmartBI,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"propertyPath": "../x_component_portal_PageDesigner/Module/SmartBI/smartbi.html"
	},
	
	initialize: function(form, options){
		this.setOptions(options);
		
		this.path = "../x_component_portal_PageDesigner/Module/SmartBI/";
		this.cssPath = "../x_component_portal_PageDesigner/Module/SmartBI/"+this.options.style+"/css.wcss";

		this._loadCss();
		this.moduleType = "container";
		this.moduleName = "SmartBI";
		
		this.Node = null;
		this.form = form;
	},
});
