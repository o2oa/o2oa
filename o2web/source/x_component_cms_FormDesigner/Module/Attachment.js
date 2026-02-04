MWF.xApplication.cms.FormDesigner.Module = MWF.xApplication.cms.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.Attachment", null, false);
MWF.xApplication.cms.FormDesigner.Module.Attachment = MWF.CMSFCAttachment = new Class({
	Extends: MWF.FCAttachment,
	Implements : [MWF.CMSFCMI],
	options: {
		"style": "default",
		"propertyPath": "../x_component_cms_FormDesigner/Module/Attachment/attachment.html"
	},
	loadAttachmentController: function(){
		this.node.set("data-mwf-el-type", "MWFFormDesignerAttachment");
		MWF.require("MWF.widget.AttachmentController", function(){
			this.attachmentController = new MWF.widget.ATTER(this.node, this, {
				"style": this.json.style || "default",
				"title": "Attachment",
				"readonly": true,
				"size": this.json.size,
				"toolbarGroupHidden" : this.json.toolbarGroupHidden || [],
				"availableListStyles" : this.json.availableListStyles || ["list","seq","icon","preview"]
			});
			this.attachmentController.load();
		}.bind(this));
	}
});
