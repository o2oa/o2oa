MWF.xApplication.cms.FormDesigner.Module = MWF.xApplication.cms.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.Radio", null, false);
MWF.xApplication.cms.FormDesigner.Module.Radio = MWF.CMSFCRadio = new Class({
	Extends: MWF.FCRadio,
	Implements : [MWF.CMSFCMI],
	options: {
		"style": "default",
		"propertyPath": "../x_component_cms_FormDesigner/Module/Radio/radio.html"
	}
});