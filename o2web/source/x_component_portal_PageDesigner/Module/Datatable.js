MWF.xDesktop.requireApp("process.FormDesigner", "Module.Datatable", null, false);
MWF.xApplication.portal.PageDesigner.Module.Datatable = MWF.PCDatatable = new Class({
	Extends: MWF.FCDatatable,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"propertyPath": "../x_component_portal_PageDesigner/Module/Datatable/datatable.html"
	},

	initialize: function(form, options){
		this.setOptions(options);

		this.path = "../x_component_portal_PageDesigner/Module/Datatable/";
		this.cssPath = "../x_component_portal_PageDesigner/Module/Datatable/"+this.options.style+"/css.wcss";

		this._loadCss();
		this.moduleType = "component";
		this.moduleName = "datatable";

		this.form = form;
		this.container = null;
		this.containerNode = null;
		this.containers = [];
		this.elements = [];
		this.selectedMultiTds = [];
	}

});
