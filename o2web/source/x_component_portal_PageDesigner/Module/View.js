MWF.xApplication.portal.PageDesigner.Module.View = MWF.PCView = new Class({
	Extends: MWF.FCView,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"propertyPath": "../x_component_portal_PageDesigner/Module/View/view.html"
	},
	
	initialize: function(form, options){
		this.setOptions(options);
		
		this.path = "../x_component_portal_PageDesigner/Module/View/";
		this.cssPath = "../x_component_portal_PageDesigner/Module/View/"+this.options.style+"/css.wcss";

		this._loadCss();
		this.moduleType = "element";
		this.moduleName = "view";

		this.form = form;
		this.container = null;
		this.containerNode = null;
	}
});
