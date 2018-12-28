MWF.xApplication.cms.FormDesigner.Module = MWF.xApplication.cms.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.Personfield", null, false);
MWF.xApplication.cms.FormDesigner.Module.Personfield = MWF.CMSFCPersonfield = new Class({
	Extends: MWF.FCPersonfield,
	Implements : [MWF.CMSFCMI],
	options: {
		"style": "default",
		"propertyPath": "/x_component_cms_FormDesigner/Module/Personfield/personfield.html"
	}
});
