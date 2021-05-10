
MWF.xDesktop.requireApp("process.FormDesigner", "Module.Datatemplate", null, false);

MWF.xApplication.portal.PageDesigner.Module.Datatemplate = MWF.PCDatatemplate = new Class({
	Extends: MWF.FCDatatemplate,
	options: {
		"style": "default",
		"propertyPath": "../x_component_portal_PageDesigner/Module/Datatemplate/datatemplate.html"
	},
	initialize: function(form, options){
		this.setOptions(options);

		this.path = "../x_component_portal_PageDesigner/Module/Datatemplate/";
		this.cssPath = "../x_component_portal_PageDesigner/Module/Datatemplate/"+this.options.style+"/css.wcss";

		this._loadCss();
		this.moduleType = "container";
		this.moduleName = "datatemplate";

		this.form = form;
	}

});