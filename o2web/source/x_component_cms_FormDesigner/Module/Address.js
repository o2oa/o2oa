MWF.xApplication.cms.FormDesigner.Module = MWF.xApplication.cms.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.Address", null, false);
MWF.xApplication.cms.FormDesigner.Module.Address = MWF.CMSFCAddress = new Class({
	Extends: MWF.FCAddress,
	Implements : [MWF.CMSFCMI],
	options: {
		"style": "default",
		"propertyPath": "../x_component_cms_FormDesigner/Module/Address/address.html"
	}
});
