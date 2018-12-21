MWF.xApplication.portal.PageDesigner.Module.Table$Td = MWF.PCTable$Td = new Class({
	Extends: MWF.FCTable$Td,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"propertyPath": "/x_component_portal_PageDesigner/Module/Table$Td/table$td.html"
	},
	initialize: function(form, options){
		this.setOptions(options);
		
		this.path = "/x_component_portal_PageDesigner/Module/Table$Td/";
		this.cssPath = "/x_component_portal_PageDesigner/Module/Table$Td/"+this.options.style+"/css.wcss";

		this._loadCss();
		this.moduleType = "container";
		this.moduleName = "table$Td";
		
		this.Node = null;
		this.form = form;
	}
});
