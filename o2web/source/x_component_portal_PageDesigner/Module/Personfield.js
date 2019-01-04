MWF.xApplication.portal.PageDesigner.Module.Personfield = MWF.PCPersonfield = new Class({
	Extends: MWF.FCPersonfield,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"propertyPath": "/x_component_portal_PageDesigner/Module/Personfield/personfield.html"
	},
	
	initialize: function(form, options){
		this.setOptions(options);
		
		this.path = "/x_component_portal_PageDesigner/Module/Personfield/";
		this.cssPath = "/x_component_portal_PageDesigner/Module/Personfield/"+this.options.style+"/css.wcss";

		this._loadCss();
		this.moduleType = "element";
		this.moduleName = "personfield";
		
		this.form = form;
		this.container = null;
		this.containerNode = null;
	}
});
