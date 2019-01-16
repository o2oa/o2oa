MWF.xApplication.portal.PageDesigner.Module.Div = MWF.PCDiv = new Class({
	Extends: MWF.FCDiv,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"propertyPath": "/x_component_portal_PageDesigner/Module/Div/div.html"
	},
	
	initialize: function(form, options){
		this.setOptions(options);
		
		this.path = "/x_component_portal_PageDesigner/Module/Div/";
		this.cssPath = "/x_component_portal_PageDesigner/Module/Div/"+this.options.style+"/css.wcss";

		this._loadCss();
		this.moduleType = "container";
		this.moduleName = "div";
		
		this.Node = null;
		this.form = form;
	}
});
