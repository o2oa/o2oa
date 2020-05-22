MWF.xApplication.cms.FormDesigner.Module = MWF.xApplication.cms.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.Textfield", null, false);
MWF.xApplication.cms.FormDesigner.Module.Textfield = MWF.CMSFCTextfield = new Class({
	Extends: MWF.FCTextfield,
	Implements : [MWF.CMSFCMI],
	options: {
		"style": "default",
		"propertyPath": "../x_component_cms_FormDesigner/Module/Textfield/textfield.html"
	}
});
