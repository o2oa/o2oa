MWF.xApplication.portal.PageDesigner.Module.Iframe = MWF.PCIframe = new Class({
	Extends: MWF.FCIframe,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"propertyPath": "/x_component_portal_PageDesigner/Module/Iframe/iframe.html"
	},
	initialize: function(form, options){
		this.setOptions(options);
		
		this.path = "/x_component_portal_PageDesigner/Module/Iframe/";
		this.cssPath = "/x_component_portal_PageDesigner/Module/Iframe/"+this.options.style+"/css.wcss";

		this._loadCss();
		this.moduleType = "element";
		this.moduleName = "iframe";

		this.form = form;
		this.container = null;
		this.containerNode = null;
	}
});
