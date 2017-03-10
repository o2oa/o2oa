MWF.xApplication.cms.FormDesigner.Module = MWF.xApplication.cms.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.Datagrid", null, false);
MWF.xApplication.cms.FormDesigner.Module.Datagrid = MWF.CMSFCDatagrid = new Class({
	Extends: MWF.FCDatagrid,
	Implements : [MWF.CMSFCMI],
	options: {
		"style": "default",
		"propertyPath": "/x_component_cms_FormDesigner/Module/Datagrid/datagrid.html"
	}
});