MWF.xApplication.portal.PageDesigner.Module.Button = MWF.PCButton = new Class({
	Extends: MWF.FCButton,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"propertyPath": "../x_component_portal_PageDesigner/Module/Button/button.html"
	},
	
	initialize: function(form, options){
		this.setOptions(options);
		
		this.path = "../x_component_portal_PageDesigner/Module/Button/";
		this.cssPath = "../x_component_portal_PageDesigner/Module/Button/"+this.options.style+"/css.wcss";

		this._loadCss();
		this.moduleType = "element";
		this.moduleName = "button";
		
		this.form = form;
		this.container = null;
		this.containerNode = null;
	}
});
