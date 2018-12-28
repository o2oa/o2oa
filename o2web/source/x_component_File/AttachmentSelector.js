MWF.xDesktop.requireApp("File", "Actions.RestActions", null, false);
MWF.xDesktop.requireApp("File", "AttachmentController", null, false);
MWF.xApplication.File.AttachmentSelector = new Class({
	Extends: MWF.xApplication.File.AttachmentController,
    options: {
        "style": "default",
        "listStyle": "icon"
    },
    addAttachment: function(data){
        if (this.options.size=="min"){
            this.attachments.push(new MWF.widget.AttachmentController.AttachmentMin(data, this));
        }else{
            this.attachments.push(new MWF.xApplication.File.AttachmentSelector.Attachment(data, this));
        }
    },
    addAttachmentFolder: function(data){
        var folder = new MWF.xApplication.File.AttachmentSelector.Folder(data, this);
        this.attachments.push(folder);
        return folder;
    }

});
MWF.xApplication.File.AttachmentSelector.Attachment = new Class({
    Extends: MWF.xApplication.File.AttachmentController.Attachment,
    load: function(){
        this.node = new Element("div").inject(this.content);
        switch (this.controller.options.listStyle){
            case "list":
                this.loadList();
                break;
            case "icon":
                this.loadIcon();
                break
            case "preview":
                this.loadPreview();
                break;
        }
        this.createInforNode(function(){
            this.tooltip = new mBox.Tooltip({
                content: this.inforNode,
                setStyles: {content: {padding: 15, lineHeight: 20}},
                attach: this.node,
                zIndex: 10013,
                transition: 'flyin'
            });
        }.bind(this));

        this.setEvent();
    }
});

MWF.xApplication.File.AttachmentSelector.Folder = new Class({
    Extends: MWF.xApplication.File.AttachmentController.Folder,
    load: function(){
        this.node = new Element("div").inject(this.content);
        switch (this.controller.options.listStyle){
            case "list":
                this.loadList();
                break;
            case "icon":
                this.loadIcon();
                break
            case "preview":
                this.loadPreview();
                break;
        }
        this.createInforNode(function(){
            this.tooltip = new mBox.Tooltip({
                content: this.inforNode,
                setStyles: {content: {padding: 15, lineHeight: 20}},
                attach: this.node,
                zIndex: 10013,
                transition: 'flyin'
            });
        }.bind(this));

        this.setEvent();
    }
});
