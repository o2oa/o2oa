MWF.xApplication.cms.FormDesigner.Module = MWF.xApplication.cms.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.Combox", null, false);
MWF.xApplication.cms.FormDesigner.Module.Combox = MWF.CMSFCCombox = new Class({
	Extends: MWF.FCCombox,
    Implements : [MWF.CMSFCMI],
	options: {
		"style": "default",
		"propertyPath": "/x_component_cms_FormDesigner/Module/Combox/combox.html"
	}
});
