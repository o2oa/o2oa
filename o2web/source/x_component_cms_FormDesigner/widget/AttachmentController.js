MWF.require("MWF.widget.AttachmentController", null, false);
MWF.xApplication.cms.FormDesigner = MWF.xApplication.cms.FormDesigner || {};
MWF.xApplication.cms.FormDesigner.widget = MWF.xApplication.cms.FormDesigner.widget || {};
MWF.xApplication.cms.FormDesigner.widget.AttachmentController = new Class({
    Extends: MWF.widget.AttachmentController,
    options: {
        "style": "default",
        "listStyle": "icon",
        "size": "max",
        "resize": true,
        "attachmentCount": 0,
        "isUpload": true,
        "isDelete": true,
        "isReplace": true,
        "isDownload": true,
        "isSizeChange": true,
        "readonly": false,
        "toolbarGroupHidden" : [], //edit read list view
        "images": ["bmp", "gif", "png", "jpeg", "jpg", "jpe", "ico"],
        "audios": ["mp3", "wav", "wma", "wmv"],
        "videos": ["avi", "mkv", "mov", "ogg", "mp4", "mpa", "mpe", "mpeg", "mpg", "rmvb"]
    },
    load: function(){
        if (this.options.size==="min"){
            this.loadMin();
        }else{
            this.loadMax();
        }
    },
    createTopNode: function(){
        if (this.options.title){
            if (!this.titleNode) this.titleNode = new Element("div", {"styles": this.css.titleNode, "text": this.options.title}).inject(this.node);
        }
        if( !this.topNode ){
            this.topNode = new Element("div", {"styles": this.css.topNode}).inject(this.node);
        }else{
            this.topNode.empty();
            this.editActionBoxNode = null;
            this.editActionsGroupNode=null;
            this.topNode.setStyle("display","");
            if( this.isHiddenTop ){
                //this.container.setStyle("height", this.container.getSize().y + 45 );
                //this.node.setStyle("height", this.node.getSize().y + 45 );
                if( this.oldContentScrollNodeHeight ){
                    this.contentScrollNode.setStyle("min-height",this.oldContentScrollNodeHeight);
                    this.oldContentScrollNodeHeight = null;
                }
                this.isHiddenTop = false;
            }
        }
        var hiddenGroup = this.options.toolbarGroupHidden;
        if( hiddenGroup.contains("edit") && hiddenGroup.contains("read") && hiddenGroup.contains("list") && hiddenGroup.contains("view")){
            //this.node.setStyle("height", this.node.getSize().y - 50 );
            //this.container.setStyle("height", this.container.getSize().y - 50 );
            this.oldContentScrollNodeHeight = this.contentScrollNode.getStyle("min-height");
            this.contentScrollNode.setStyle("min-height",this.node.getStyle("min-height"));
            this.topNode.setStyle("display","none");
            this.isHiddenTop = true;
            return;
        }
        if( !hiddenGroup.contains("edit") )this.createEditGroupActions();
        if( !hiddenGroup.contains("read") )this.createReadGroupActions();
        if( !hiddenGroup.contains("list") )this.createListGroupActions();
        if( !hiddenGroup.contains("view") )this.createViewGroupActions();
    },
    reloadTopNode : function(){
        //if(this.topNode){
        //    this.topNode.empty();
        //    this.editActionBoxNode = null;
        //    this.editActionsGroupNode=null;
        //}
        this.createTopNode();
    },
    loadMax: function(){
        if (!this.node) this.node = new Element("div", {"styles": this.css.container});

        this.createTopNode();

        if (!this.contentScrollNode){
            //this.createTopNode();
            this.createContentNode();


            if (this.options.resize){
                this.createBottomNode();
                this.createResizeNode();
            }

            this.node.inject(this.container);

            //if (this.options.readonly) this.setReadonly();
            this.checkActions();

            this.setEvent();
        }else{
            this.contentScrollNode.setStyle("display", "block");
            if (this.bottomNode) this.bottomNode.setStyle("display", "block");
            if (this.titleNode) this.titleNode.setStyle("display", "block");
            //this.topNode.setStyle("display", "block");
            this.content.empty();
        }
        var atts = [];
        while (this.attachments.length){
            var att = this.attachments.shift();
            atts.push(new MWF.widget.AttachmentController.Attachment(att.data, this));
        }
        this.attachments = atts;
    },
    loadMin: function(){
        var hiddenGroup = this.options.toolbarGroupHidden;
        var flag = hiddenGroup.contains("edit") && hiddenGroup.contains("read")  && hiddenGroup.contains("view");

        if (!this.node) this.node = new Element("div", {"styles": this.css.container_min});

        if (!this.minActionAreaNode) {
            this.minActionAreaNode = new Element("div", {"styles": this.css.minActionAreaNode}).inject(this.node);
        }else {
            this.minActionAreaNode.setStyle("display","");
            this.minActionAreaNode.empty();
        }

        if( flag )this.minActionAreaNode.setStyle("display","none");

        if( !this.minContent ){
            this.minContent = new Element("div", {"styles": this.css.minContentNode}).inject(this.node);
        }else{
            this.minContent.setStyle("display", "block");
            this.minContent.empty();
        }

            if (!hiddenGroup.contains("edit")){
                this.min_uploadAction = this.createAction(this.minActionAreaNode, "upload", MWF.LP.widget.upload, function (e, node) {
                    this.uploadAttachment(e, node);
                }.bind(this));

                this.min_deleteAction = this.createAction(this.minActionAreaNode, "delete", MWF.LP.widget["delete"], function (e, node) {
                    this.deleteAttachment(e, node);
                }.bind(this));

                this.min_replaceAction = this.createAction(this.minActionAreaNode, "replace", MWF.LP.widget.replace, function (e, node) {
                    this.replaceAttachment(e, node);
                }.bind(this));
            }

            if (!hiddenGroup.contains("read")){
                this.min_downloadAction = this.createAction(this.minActionAreaNode, "download", MWF.LP.widget.download, function (e, node) {
                    this.downloadAttachment(e, node);
                }.bind(this));
            }

            if( !hiddenGroup.contains("edit") || !hiddenGroup.contains("read") ) {
                this.createSeparate(this.minActionAreaNode);
            }

            if( !hiddenGroup.contains("view")){
                this.sizeAction = this.createAction(this.minActionAreaNode, "max", MWF.LP.widget.min, function(){
                    this.changeControllerSize();
                }.bind(this));
            }

            this.node.inject(this.container);

            //if (this.options.readonly) this.setReadonly();
            this.checkActions();

            this.setEvent();

        var atts = [];
        while (this.attachments.length){
            var att = this.attachments.shift();
            atts.push(new MWF.widget.AttachmentController.AttachmentMin(att.data, this));
        }
        this.attachments = atts;
    },
    createEditGroupActions: function(){
        if(!this.editActionBoxNode)this.editActionBoxNode = new Element("div", {"styles": this.css.actionsBoxNode}).inject(this.topNode);
        if(!this.editActionsGroupNode)this.editActionsGroupNode = new Element("div", {"styles": this.css.actionsGroupNode}).inject(this.editActionBoxNode);
        this.uploadAction = this.createAction(this.editActionsGroupNode, "upload", MWF.LP.widget.upload, function(e, node){
            this.uploadAttachment(e, node);
        }.bind(this));

        this.deleteAction = this.createAction(this.editActionsGroupNode, "delete", MWF.LP.widget["delete"], function(e, node){
            this.deleteAttachment(e, node);
        }.bind(this));

        this.replaceAction = this.createAction(this.editActionsGroupNode, "replace", MWF.LP.widget.replace, function(e, node){
            this.replaceAttachment(e, node);
        }.bind(this));

        if( !this.options.toolbarGroupHidden.contains("read") )this.editActionSeparateNode = this.createSeparate(this.editActionsGroupNode);
    },
    createReadGroupActions: function(){
        //this.readActionBoxNode = new Element("div", {"styles": this.css.actionsBoxNode}).inject(this.topNode);
        //this.readActionsGroupNode = new Element("div", {"styles": this.css.actionsGroupNode}).inject(this.readActionBoxNode);
        if(!this.editActionBoxNode)this.editActionBoxNode = new Element("div", {"styles": this.css.actionsBoxNode}).inject(this.topNode);
        if(!this.editActionsGroupNode)this.editActionsGroupNode = new Element("div", {"styles": this.css.actionsGroupNode}).inject(this.editActionBoxNode);
        this.downloadAction = this.createAction(this.editActionsGroupNode, "download", MWF.LP.widget.download, function(){
            this.downloadAttachment();
        }.bind(this));

        //this.createAction(this.readActionsGroupNode, "share", MWF.LP.widget.share, function(){
        //    this.transAttachment();
        //}.bind(this));

        //this.downloadAllAction = this.createAction(this.editActionsGroupNode, "downloadAll", MWF.LP.widget.downloadAll, function(){
        //    this.downloadAllAttachment();
        //}.bind(this));

    },

    checkReplaceAction: function(){
        if (this.options.readonly){
            this.setActionDisabled(this.replaceAction);
            this.setActionDisabled(this.min_replaceAction);
            return false;
        }
        if (!this.options.isReplace){
            this.setActionDisabled(this.replaceAction);
            this.setActionDisabled(this.min_replaceAction);
        }else{
            if (this.selectedAttachments.length && this.selectedAttachments.length==1){
                this.setActionEnabled(this.replaceAction);
                this.setActionEnabled(this.min_replaceAction);
            }else{
                this.setActionDisabled(this.replaceAction);
                this.setActionDisabled(this.min_replaceAction);
            }
        }
    },
    resetToolbarGroupHidden : function( hiddenGroup ){
        this.options.toolbarGroupHidden = hiddenGroup;
        if( this.options.size == "max" ){
            this.reloadTopNode();
        }else{
            this.loadMin();
        }

    }
});

