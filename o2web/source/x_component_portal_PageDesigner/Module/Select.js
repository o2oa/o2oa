MWF.xApplication.portal.PageDesigner.Module.Select = MWF.PCSelect = new Class({
	Extends: MWF.FCSelect,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"propertyPath": "/x_component_portal_PageDesigner/Module/Select/select.html"
	},
	
	initialize: function(form, options){
		this.setOptions(options);
		
		this.path = "/x_component_portal_PageDesigner/Module/Select/";
		this.cssPath = "/x_component_portal_PageDesigner/Module/Select/"+this.options.style+"/css.wcss";

		this._loadCss();
		this.moduleType = "element";
		this.moduleName = "select";
		
		this.form = form;
		this.container = null;
		this.containerNode = null;
	}
});
