MWF.xApplication.cms.FormDesigner.Module = MWF.xApplication.cms.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.Attachment", null, false);
MWF.xApplication.cms.FormDesigner.Module.Attachment = MWF.CMSFCAttachment = new Class({
	Extends: MWF.FCAttachment,
	Implements : [MWF.CMSFCMI],
	options: {
		"style": "default",
		"propertyPath": "../x_component_cms_FormDesigner/Module/Attachment/attachment.html"
	}
});
