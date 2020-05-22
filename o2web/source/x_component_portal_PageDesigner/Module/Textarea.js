MWF.xApplication.portal.PageDesigner.Module.Textarea = MWF.PCTextarea = new Class({
	Extends: MWF.FCTextarea,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"propertyPath": "../x_component_portal_PageDesigner/Module/Textarea/textarea.html"
	},
	
	initialize: function(form, options){
		this.setOptions(options);
		
		this.path = "../x_component_portal_PageDesigner/Module/Textarea/";
		this.cssPath = "../x_component_portal_PageDesigner/Module/Textarea/"+this.options.style+"/css.wcss";

		this._loadCss();
		this.moduleType = "element";
		this.moduleName = "textarea";
		
		this.form = form;
		this.container = null;
		this.containerNode = null;
	}
});
