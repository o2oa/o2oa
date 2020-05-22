MWF.xApplication.cms.FormDesigner.Module = MWF.xApplication.cms.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.Checkbox", null, false);
MWF.xApplication.cms.FormDesigner.Module.Checkbox = MWF.CMSFCCheckbox = new Class({
	Extends: MWF.FCCheckbox,
	Implements : [MWF.CMSFCMI],
	options: {
		"style": "default",
		"propertyPath": "../x_component_cms_FormDesigner/Module/Checkbox/checkbox.html"
	}
});
