MWF.xApplication.cms.FormDesigner.Module = MWF.xApplication.cms.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.Datatable", null, false);
MWF.xApplication.cms.FormDesigner.Module.Datatable = MWF.CMSFCDatatable = new Class({
	Extends: MWF.FCDatatable,
	Implements : [MWF.CMSFCMI],
	options: {
		"style": "default",
		"propertyPath": "../x_component_cms_FormDesigner/Module/Datatable/datatable.html"
	}
});