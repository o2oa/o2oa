MWF.xApplication.cms.FormDesigner.Module = MWF.xApplication.cms.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.Number", null, false);
MWF.xApplication.cms.FormDesigner.Module.Number = MWF.CMSFCNumber = new Class({
	Extends: MWF.FCNumber,
	Implements : [MWF.CMSFCMI],
	options: {
		"style": "default",
		"propertyPath": "/x_component_cms_FormDesigner/Module/Number/number.html"
	}
});
