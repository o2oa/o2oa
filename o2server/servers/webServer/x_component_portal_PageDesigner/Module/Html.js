MWF.xApplication.portal.PageDesigner.Module.Html = MWF.PCHtml = new Class({
	Extends: MWF.FCHtml,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"propertyPath": "/x_component_portal_PageDesigner/Module/Html/html.html"
	},
	
	initialize: function(form, options){
		this.setOptions(options);
		
		this.path = "/x_component_portal_PageDesigner/Module/Html/";
		this.cssPath = "/x_component_portal_PageDesigner/Module/Html/"+this.options.style+"/css.wcss";

		this._loadCss();
		this.moduleType = "element";
		this.moduleName = "html";

		this.form = form;
		this.container = null;
		this.containerNode = null;
	}
});
