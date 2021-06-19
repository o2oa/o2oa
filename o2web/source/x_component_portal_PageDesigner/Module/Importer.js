MWF.xApplication.portal.PageDesigner.Module.Importer = MWF.PCImporter = new Class({
	Extends: MWF.FCImporter,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"propertyPath": "../x_component_portal_PageDesigner/Module/Importer/importer.html"
	},

	initialize: function(form, options){
		this.setOptions(options);

		this.path = "../x_component_portal_PageDesigner/Module/Importer/";
		this.cssPath = "../x_component_portal_PageDesigner/Module/Importer/"+this.options.style+"/css.wcss";

		this._loadCss();
		this.moduleType = "element";
		this.moduleName = "importer";

		this.form = form;
		this.container = null;
		this.containerNode = null;
	}
});
