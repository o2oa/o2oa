MWF.xApplication.cms.FormDesigner.Module = MWF.xApplication.cms.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.Textarea", null, false);
MWF.xApplication.cms.FormDesigner.Module.Textarea = MWF.CMSFCTextarea = new Class({
	Extends: MWF.FCTextarea,
	Implements : [MWF.CMSFCMI],
	options: {
		"style": "default",
		"propertyPath": "/x_component_cms_FormDesigner/Module/Textarea/textarea.html"
	}
	
});
