MWF.xDesktop.requireApp("File", "Actions.RestActions", null, false);
MWF.require("MWF.widget.AttachmentController", null, false);
MWF.xApplication.File.AttachmentController = new Class({
	Extends: MWF.widget.AttachmentController,

    createTopNode: function(){
        this.topNode = new Element("div", {"styles": this.css.topNode}).inject(this.node);
        this.createFolderGroupActions();

        this.createEditGroupActions();
        this.createReadGroupActions();
        this.createListGroupActions();

        this.createShareGroupActions();

        this.createViewGroupActions();
    },
    checkActions: function(){
        this.checkFolderAction();
        this.checkUploadAction();
        this.checkDeleteAction();
        this.checkReplaceAction();
        this.checkDownloadAction();
        this.checkSizeAction();
        this.checkShareAction();
        this.checkListStyleAction();
    },
    createFolderGroupActions: function(){
        this.folderActionBoxNode = new Element("div", {"styles": this.css.actionsBoxNode}).inject(this.topNode);
        this.folderActionsGroupNode = new Element("div", {"styles": this.css.actionsGroupNode}).inject(this.folderActionBoxNode);
        this.createFolderAction = this.createAction(this.folderActionsGroupNode, "createFolder", MWF.LP.widget.createFolder, function(){
            this.createFolder();
        }.bind(this));
        this.renameAction = this.createAction(this.folderActionsGroupNode, "rename", MWF.LP.widget.rename, function(){
            this.renameFolder();
        }.bind(this));
    },
    createShareGroupActions: function(){
        this.shareActionBoxNode = new Element("div", {"styles": this.css.actionsBoxNode}).inject(this.topNode);
        this.shareActionsGroupNode = new Element("div", {"styles": this.css.actionsGroupNode}).inject(this.shareActionBoxNode);

        this.shareAction = this.createAction(this.shareActionsGroupNode, "share", MWF.LP.widget.share, function(){
            this.shareAttachment();
        }.bind(this));
        this.sendAction = this.createAction(this.shareActionsGroupNode, "send", MWF.LP.widget.send, function(){
            this.sendAttachment();
        }.bind(this));
        // this.createSeparate(this.shareActionsGroupNode);
        // this.propertyAction = this.createAction(this.shareActionsGroupNode, "property", MWF.LP.widget.property, function(){
        //     this.showProperty();
        // }.bind(this));
        //property
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
                if (this.selectedAttachments[0].type=="folder"){
                    this.setActionDisabled(this.replaceAction);
                    this.setActionDisabled(this.min_replaceAction);
                }else{
                    this.setActionEnabled(this.replaceAction);
                    this.setActionEnabled(this.min_replaceAction);
                }
            }else{
                this.setActionDisabled(this.replaceAction);
                this.setActionDisabled(this.min_replaceAction);
            }
        }
    },

    checkFolderAction: function(){
        if (this.selectedAttachments.length==1){
        //    if (this.selectedAttachments[0].type=="folder"){
                this.setActionEnabled(this.renameAction);
        //    }else{
        //        this.setActionDisabled(this.renameAction);
        //    }
        }else{
            this.setActionDisabled(this.renameAction);
        }
    },

    checkShareAction: function(){
        if (this.selectedAttachments.length){
            if (this.selectedAttachments.length==1 && this.selectedAttachments[0].type=="folder"){
                this.setActionDisabled(this.shareAction);
                this.setActionDisabled(this.sendAction);
            }else{
                this.setActionEnabled(this.shareAction);
                this.setActionEnabled(this.sendAction);
            }
            // if (this.selectedAttachments.length===1){
            //     this.setActionEnabled(this.propertyAction);
            // }else{
            //     this.setActionDisabled(this.propertyAction);
            // }
        }else{
            this.setActionDisabled(this.shareAction);
            this.setActionDisabled(this.sendAction);
            //this.setActionDisabled(this.propertyAction);
        }
    },
    addAttachment: function(data){
        if (this.options.size=="min"){
            this.attachments.push(new MWF.widget.AttachmentController.AttachmentMin(data, this));
        }else{
            this.attachments.push(new MWF.xApplication.File.AttachmentController.Attachment(data, this));
        }
    },
    addAttachmentFolder: function(data){
        var folder = new MWF.xApplication.File.AttachmentController.Folder(data, this);
        this.attachments.push(folder);
        return folder;
    },

    createFolder: function(e, node){
        if (this.module) this.module.createFolder(e, node);
    },
    renameFolder: function(e, node){
        if (this.module) this.module.renameFileFolder(e, node);
    },
    shareAttachment: function(){
        if (this.module) this.module.shareAttachment();
    },
    sendAttachment: function(){
        if (this.module) this.module.sendAttachment();
    }

});
MWF.xApplication.File.AttachmentController.Attachment = new Class({
    Extends: MWF.widget.AttachmentController.Attachment,
    loadList: function(){
        this.node.setStyles(this.css.attachmentNode_list);
        if (this.isSelected) this.node.setStyles(this.css.attachmentNode_list_selected);

        this.iconNode = new Element("div", {"styles": this.css.attachmentIconNode_list}).inject(this.node);
        this.iconImgAreaNode = new Element("div", {"styles": this.css.attachmentIconImgAreaNode_list}).inject(this.iconNode);
        this.iconImgNode = new Element("img", {"styles": this.css.attachmentIconImgNode_list}).inject(this.iconImgAreaNode);
        this.iconImgNode.set({"src": this.getIcon(), "border": 0});

        this.textNode = new Element("div", {"styles": this.css.attachmentTextNode_list}).inject(this.node);
        this.textTitleNode = new Element("div", {"styles": this.css.attachmentTextTitleNode_list}).inject(this.textNode);
        this.textTitleNode.set("text", this.data.name);

        var size = "";
        var k = this.data.length/1024;
        if (k>1024){
            var m = k/1024;
            m = Math.round(m*100)/100;
            size = m+"M";
        }else{
            k = Math.round(k*100)/100;
            size = k+"K";
        }
        this.textSizeNode = new Element("div", {"styles": this.css.attachmentTextSizeNode_list}).inject(this.textNode);
        this.textSizeNode.set("text", size);

        this.textUploaderNode = new Element("div", {"styles": this.css.attachmentTextUploaderNode_list}).inject(this.textNode);
        this.textUploaderNode.set("text", o2.name.cn(this.data.person || this.data.creatorUid ));

        this.textTimeNode = new Element("div", {"styles": this.css.attachmentTextTimeNode_list}).inject(this.textNode);
        this.textTimeNode.set("text", this.data.lastUpdateTime);

        // this.textActivityNode = new Element("div", {"styles": this.css.attachmentTextActivityNode_list}).inject(this.textNode);
        // this.textActivityNode.set("text", this.data.activityName || o2.LP.widget.unknow);

        this.extensionNode = new Element("div", {"styles": this.css.attachmentTextActivityNode_list}).inject(this.textNode);
        this.extensionNode.set("text", this.data.extension || o2.LP.widget.unknow);



        this.custom_List();
    },
    loadSequence: function(){
        this.node.setStyles(this.css.attachmentNode_sequence);
        if (this.isSelected) this.node.setStyles(this.css.attachmentNode_sequence_selected);

        this.sequenceNode = new Element("div", {"styles": this.css.attachmentSeqNode_sequence, "text": (this.seq || 1)}).inject(this.node);

        this.iconNode = new Element("div", {"styles": this.css.attachmentIconNode_list}).inject(this.node);
        this.iconImgAreaNode = new Element("div", {"styles": this.css.attachmentIconImgAreaNode_list}).inject(this.iconNode);
        this.iconImgNode = new Element("img", {"styles": this.css.attachmentIconImgNode_list}).inject(this.iconImgAreaNode);
        this.iconImgNode.set({"src": this.getIcon(), "border": 0});

        this.textNode = new Element("div", {"styles": this.css.attachmentTextNode_sequence}).inject(this.node);
        this.textTitleNode = new Element("div", {"styles": this.css.attachmentTextTitleNode_list}).inject(this.textNode);
        this.textTitleNode.set("text", this.data.name);

        var size = "";
        var k = this.data.length/1024;
        if (k>1024){
            var m = k/1024;
            m = Math.round(m*100)/100;
            size = m+"M";
        }else{
            k = Math.round(k*100)/100;
            size = k+"K";
        }
        this.textSizeNode = new Element("div", {"styles": this.css.attachmentTextSizeNode_list}).inject(this.textNode);
        this.textSizeNode.set("text", size);

        this.textUploaderNode = new Element("div", {"styles": this.css.attachmentTextUploaderNode_list}).inject(this.textNode);
        this.textUploaderNode.set("text", o2.name.cn(this.data.person || this.data.creatorUid));

        this.textTimeNode = new Element("div", {"styles": this.css.attachmentTextTimeNode_list}).inject(this.textNode);
        this.textTimeNode.set("text", this.data.lastUpdateTime);

        // this.textActivityNode = new Element("div", {"styles": this.css.attachmentTextActivityNode_list}).inject(this.textNode);
        // this.textActivityNode.set("text", this.data.activityName || o2.LP.widget.unknow);

        this.extensionNode = new Element("div", {"styles": this.css.attachmentTextActivityNode_list}).inject(this.textNode);
        this.extensionNode.set("text", this.data.extension || o2.LP.widget.unknow);

        this.custom_Sequence();
    },
    createInforNode: function(callback){
        var size = "";
        var k = this.data.length/1024;
        if (k>1024){
            var m = k/1024;
            m = Math.round(m*100)/100;
            size = m+"M";
        }else{
            k = Math.round(k*100)/100;
            size = k+"K";
        }
        var shareList = (this.data.shareList) ? this.data.shareList.map(function(item){return item.substring(0, item.indexOf("@"));}).join(",") : "";
        var editorList = (this.data.editorList) ? this.data.editorList.map(function(item){return item.substring(0, item.indexOf("@"));}).join(",") : "";
        this.inforNode = new Element("div", {"styles": this.css.attachmentInforNode});
        var html = "<div style='overflow:hidden; font-weight: bold'>"+this.data.name+"</div>";
        html += "<div style='clear: both; overflow:hidden'><div style='width:40px; float:left; font-weight: bold'>"+MWF.LP.widget.uploader+": </div><div style='width:120px; float:left; margin-left:10px'>"+MWF.name.cn(this.data.person)+"</div></div>";
        html += "<div style='clear: both; overflow:hidden'><div style='width:40px; float:left; font-weight: bold'>"+MWF.LP.widget.uploadTime+": </div><div style='width:120px; float:left; margin-left:10px'>"+this.data.createTime+"</div></div>";
        html += "<div style='clear: both; overflow:hidden'><div style='width:40px; float:left; font-weight: bold'>"+MWF.LP.widget.modifyTime+": </div><div style='width:120px; float:left; margin-left:10px'>"+this.data.lastUpdateTime+"</div></div>";
        html += "<div style='clear: both; overflow:hidden'><div style='width:40px; float:left; font-weight: bold'>"+MWF.LP.widget.size+": </div><div style='width:120px; float:left; margin-left:10px'>"+size+"</div></div>";
        html += "<div style='clear: both; overflow:hidden'><div style='width:40px; float:left; font-weight: bold'>"+MWF.LP.widget.share+": </div><div style='width:120px; float:left; margin-left:10px'>"+shareList+"</div></div>";
        html += "<div style='clear: both; overflow:hidden'><div style='width:40px; float:left; font-weight: bold'>"+MWF.LP.widget.send+": </div><div style='width:120px; float:left; margin-left:10px'>"+editorList+"</div></div>";
        this.inforNode.set("html", html);

        if (callback) callback();
    },
    custom_Preview: function(){
        var shareList = (this.data.shareList) ? this.data.shareList.join(",") : "";
        var sendList = (this.data.editorList) ? this.data.editorList.join(",") : "";
        if (shareList || sendList){
            var flagNode = new Element("div", {
                "styles": {
                    "width": "24px",
                    "height": "24px",
                    "background": "url("+"../x_component_File/$Main/default/icon/share_flag_preview.png) center center no-repeat",
                    "position": "relative",
                    "top": "-28px"
                }
            }).inject(this.iconImgAreaNode);
        }
    },
    custom_Icon: function(){
        var shareList = (this.data.shareList) ? this.data.shareList.join(",") : "";
        var sendList = (this.data.editorList) ? this.data.editorList.join(",") : "";
        if (shareList || sendList){
            var flagNode = new Element("div", {
                "styles": {
                    "width": "16px",
                    "height": "16px",
                    "background": "url("+"../x_component_File/$Main/default/icon/share_flag_icon.png) center center no-repeat",
                    "position": "relative",
                    "top": "-18px"
                }
            }).inject(this.iconImgAreaNode);
        }
    },
    custom_List: function(){
        var shareList = (this.data.shareList) ? this.data.shareList.join(",") : "";
        var sendList = (this.data.editorList) ? this.data.editorList.join(",") : "";
        if (shareList || sendList){
            var flagNode = new Element("div", {
                "styles": {
                    "width": "10px",
                    "height": "10px",
                    "background": "url("+"../x_component_File/$Main/default/icon/share_flag_list.png) center center no-repeat",
                    "position": "relative",
                    "top": "-14px"
                }
            }).inject(this.iconImgAreaNode);
        }
    }
});

MWF.xApplication.File.AttachmentController.Folder = new Class({
    Extends: MWF.widget.AttachmentController.Attachment,
    initialize: function(data, controller){
        this.data = data;
        this.controller = controller;
        this.css = this.controller.css;
        this.listStyle = this.controller.options.listStyle;
        this.content = this.controller.content;
        this.isSelected = false;
        this.type = "folder";
        this.load();
    },

    getIcon: function(){
        //if (!this.data.extension) this.data.extension="unkonw";
        //var iconName = this.controller.icons[this.data.extension.toLowerCase()] || this.controller.icons.unknow;
        return "../x_component_File/$Main/default/file/folder.png";
    },
    createInforNode: function(callback){
        var size = "";
        var k = this.data.size/1024;
        if (k>1024){
            var m = k/1024;
            m = Math.round(m*100)/100;
            size = m+"M";
        }else{
            k = Math.round(k*100)/100;
            size = k+"K";
        }

   //     this.controller.module.restActions.listComplex(function(json){
   //         var attCount = json.data.attachmentList.length;
   //         var folderCount = json.data.folderList.length;
            this.inforNode = new Element("div", {"styles": this.css.attachmentInforNode});
            var html = "<div style='overflow:hidden; font-weight: bold'>"+this.data.name+"</div>";
            html += "<div style='clear: both; overflow:hidden'><div style='width:40px; float:left; font-weight: bold'>"+MWF.LP.widget.attCount+": </div><div style='width:120px; float:left; margin-left:10px'>"+this.data.attachmentCount+"</div></div>";
            html += "<div style='clear: both; overflow:hidden'><div style='width:40px; float:left; font-weight: bold'>"+MWF.LP.widget.folderCount+": </div><div style='width:120px; float:left; margin-left:10px'>"+this.data.folderCount+"</div></div>";
            html += "<div style='clear: both; overflow:hidden'><div style='width:40px; float:left; font-weight: bold'>"+MWF.LP.widget.uploadTime+": </div><div style='width:120px; float:left; margin-left:10px'>"+this.data.createTime+"</div></div>";
            html += "<div style='clear: both; overflow:hidden'><div style='width:40px; float:left; font-weight: bold'>"+MWF.LP.widget.modifyTime+": </div><div style='width:120px; float:left; margin-left:10px'>"+this.data.updateTime+"</div></div>";
            html += "<div style='clear: both; overflow:hidden'><div style='width:40px; float:left; font-weight: bold'>"+MWF.LP.widget.size+": </div><div style='width:120px; float:left; margin-left:10px'>"+size+"</div></div>";
            this.inforNode.set("html", html);

            if (callback) callback();
   //     }.bind(this), null, this.data.id);
    },
    loadPreview: function(){
        this.node.setStyles(this.css.attachmentNode_preview);
        if (this.isSelected) this.node.setStyles(this.css.attachmentNode_preview_selected);

        this.iconNode = new Element("div", {"styles": this.css.attachmentPreviewIconNode}).inject(this.node);
        this.iconImgAreaNode = new Element("div", {"styles": this.css.attachmentPreviewIconImgAreaNode}).inject(this.iconNode);
        this.iconImgNode = new Element("img", {"styles": this.css.attachmentPreviewIconImgNode}).inject(this.iconImgAreaNode);

        var icon = this.getIcon();
        this.iconImgNode.set({"src": icon, "border": 0});

        this.textNode = new Element("div", {"styles": this.css.attachmentPreviewTextNode}).inject(this.node);
        this.textNode.set("text", this.data.name);
    },
    setEvent: function(){
        this.node.addEvents({
            "mouseover": function(){if (!this.isSelected) this.node.setStyles(this.css["attachmentNode_"+this.controller.options.listStyle+"_over"])}.bind(this),
            "mouseout": function(){if (!this.isSelected) this.node.setStyles(this.css["attachmentNode_"+this.controller.options.listStyle])}.bind(this),
            "mousedown": function(e){this.selected(e);}.bind(this),
            "dblclick": function(e){this.openFolder(e);}.bind(this)
        });
    },
    openFolder: function(){
        this.treeNode.clickNode();
    }
});
