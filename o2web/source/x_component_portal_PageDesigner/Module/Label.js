MWF.xApplication.portal.PageDesigner.Module.Label = MWF.PCLabel = new Class({
	Extends: MWF.FCLabel,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"propertyPath": "../x_component_portal_PageDesigner/Module/Label/label.html"
	},
	initialize: function(form, options){
		this.setOptions(options);
		
		this.path = "../x_component_portal_PageDesigner/Module/Label/";
		this.cssPath = "../x_component_portal_PageDesigner/Module/Label/"+this.options.style+"/css.wcss";

		this._loadCss();
		this.moduleType = "element";
		this.moduleName = "label";

		this.form = form;
		this.container = null;
		this.containerNode = null;
	}
});
