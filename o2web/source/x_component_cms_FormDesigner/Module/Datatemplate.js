MWF.xApplication.cms.FormDesigner.Module = MWF.xApplication.cms.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.Datatemplate", null, false);
MWF.xApplication.cms.FormDesigner.Module.Datatemplate = MWF.CMSFCDatatemplate = new Class({
	Extends: MWF.FCDatatemplate,
	Implements : [MWF.CMSFCMI],
	options: {
		"style": "default",
		"propertyPath": "../x_component_cms_FormDesigner/Module/Datatemplate/datatemplate.html"
	}
});