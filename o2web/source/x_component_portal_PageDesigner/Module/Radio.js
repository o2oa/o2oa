MWF.xApplication.portal.PageDesigner.Module.Radio = MWF.PCRadio = new Class({
	Extends: MWF.FCRadio,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"propertyPath": "../x_component_portal_PageDesigner/Module/Radio/radio.html"
	},
	
	initialize: function(form, options){
		this.setOptions(options);
		
		this.path = "../x_component_portal_PageDesigner/Module/Radio/";
		this.cssPath = "../x_component_portal_PageDesigner/Module/Radio/"+this.options.style+"/css.wcss";

		this._loadCss();
		this.moduleType = "element";
		this.moduleName = "radio";
		
		this.form = form;
		this.container = null;
		this.containerNode = null;
	}
});
