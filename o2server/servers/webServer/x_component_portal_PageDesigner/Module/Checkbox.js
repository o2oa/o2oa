MWF.xApplication.portal.PageDesigner.Module.Checkbox = MWF.PCCheckbox = new Class({
	Extends: MWF.FCCheckbox,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"propertyPath": "/x_component_portal_PageDesigner/Module/Checkbox/checkbox.html"
	},
	
	initialize: function(form, options){
		this.setOptions(options);
		
		this.path = "/x_component_portal_PageDesigner/Module/Checkbox/";
		this.cssPath = "/x_component_portal_PageDesigner/Module/Checkbox/"+this.options.style+"/css.wcss";

		this._loadCss();
		this.moduleType = "element";
		this.moduleName = "checkbox";
		
		this.form = form;
		this.container = null;
		this.containerNode = null;
	}
});
