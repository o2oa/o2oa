MWF.xApplication.portal.PageDesigner.Module.Textfield = MWF.PCTextfield = new Class({
	Extends: MWF.FCTextfield,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"propertyPath": "../x_component_portal_PageDesigner/Module/Textfield/textfield.html"
	},
	
	initialize: function(form, options){
		this.setOptions(options);
		
		this.path = "../x_component_portal_PageDesigner/Module/Textfield/";
		this.cssPath = "../x_component_portal_PageDesigner/Module/Textfield/"+this.options.style+"/css.wcss";

		this._loadCss();
		this.moduleType = "element";
		this.moduleName = "textfield";
		
		this.form = form;
		this.container = null;
		this.containerNode = null;
	}
});
