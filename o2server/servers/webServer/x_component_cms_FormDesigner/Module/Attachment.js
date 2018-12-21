MWF.xApplication.cms.FormDesigner.Module = MWF.xApplication.cms.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.Attachment", null, false);
MWF.xApplication.cms.FormDesigner.Module.Attachment = MWF.CMSFCAttachment = new Class({
	Extends: MWF.FCAttachment,
	Implements : [MWF.CMSFCMI],
	options: {
		"style": "default",
		"propertyPath": "/x_component_cms_FormDesigner/Module/Attachment/attachment.html"
	},
	_setEditStyle_custom: function(name){
		if (name=="size"){
			if (this.json[name]=="min"){
				this.attachmentController.changeControllerSizeToMin();
			}else{
				this.attachmentController.changeControllerSizeToMax();
			}
		}else if(name=="toolbarGroupHidden"){
			this.attachmentController.resetToolbarGroupHidden( this.json[name] );
		}
	},
	loadAttachmentController: function(){
		MWF.xDesktop.requireApp("cms.FormDesigner", "widget.AttachmentController", null, false);
		this.attachmentController = new MWF.xApplication.cms.FormDesigner.widget.AttachmentController(this.node, this, {
			"readonly": true,
			"size": this.json.size,
			"toolbarGroupHidden" : this.json.toolbarGroupHidden || []
		});
		this.attachmentController.load();
	}
});
