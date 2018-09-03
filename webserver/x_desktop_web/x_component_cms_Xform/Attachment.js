MWF.xDesktop.requireApp("process.Xform", "Attachment", null, false);
MWF.xDesktop.requireApp("cms.FormDesigner", "widget.AttachmentController", null, false);
MWF.xApplication.cms.Xform.AttachmentController = new Class({
    Extends: MWF.xApplication.cms.FormDesigner.widget.AttachmentController,
    checkDeleteAction: function(){
        if (this.options.readonly){
            this.setActionDisabled(this.deleteAction);
            this.setActionDisabled(this.min_deleteAction);
            return false;
        }
        if (this.options.isDeleteOption!=="y" && this.options.isDeleteOption!=="n"){
            if (this.selectedAttachments.length){
                var flag = true;
                if (this.options.isDeleteOption==="o"){
                    for (var i=0; i<this.selectedAttachments.length; i++){
                        if (this.selectedAttachments[i].data.person!==layout.desktop.session.user.distinguishedName){
                            flag = false;
                            break;
                        }
                    }
                }else if (this.options.isDeleteOption==="a"){
                    for (var i=0; i<this.selectedAttachments.length; i++){
                        if (this.selectedAttachments[i].data.activity!==this.module.form.businessData.activity.id){
                            flag = false;
                            break;
                        }
                    }
                }else if (this.options.isDeleteOption==="ao"){
                    for (var i=0; i<this.selectedAttachments.length; i++){
                        if ((this.selectedAttachments[i].data.activity!==this.module.form.businessData.activity.id) || (this.selectedAttachments[i].data.person!==layout.desktop.session.user.distinguishedName)){
                            flag = false;
                            break;
                        }
                    }
                }

                if (flag){
                    this.setActionEnabled(this.deleteAction);
                    this.setActionEnabled(this.min_deleteAction);
                }else{
                    this.setActionDisabled(this.deleteAction);
                    this.setActionDisabled(this.min_deleteAction);
                }
            }else{
                this.setActionDisabled(this.deleteAction);
                this.setActionDisabled(this.min_deleteAction);
            }
        }else{
            if (!this.options.isDelete){
                this.setActionDisabled(this.deleteAction);
                this.setActionDisabled(this.min_deleteAction);
            }else{
                if (this.selectedAttachments.length){
                    this.setActionEnabled(this.deleteAction);
                    this.setActionEnabled(this.min_deleteAction);
                }else{
                    this.setActionDisabled(this.deleteAction);
                    this.setActionDisabled(this.min_deleteAction);
                }
            }
        }
    },
    checkReplaceAction: function(){
        if (this.options.readonly){
            this.setActionDisabled(this.replaceAction);
            this.setActionDisabled(this.min_replaceAction);
            return false;
        }

        if (this.options.isReplaceOption!=="y" && this.options.isReplaceOption!=="n") {
            if (this.selectedAttachments.length && this.selectedAttachments.length===1){
                var flag;

                if (this.options.isReplaceOption==="o"){
                    flag = this.selectedAttachments[0].data.person === layout.desktop.session.user.distinguishedName;
                }
                if (this.options.isReplaceOption==="a"){
                    flag = this.selectedAttachments[0].data.activity===this.module.form.businessData.activity.id;
                }
                if (this.options.isReplaceOption==="ao"){
                    flag = (this.selectedAttachments[0].data.person === layout.desktop.session.user.distinguishedName && this.selectedAttachments[0].data.activity===this.module.form.businessData.activity.id);
                }

                if (flag) {
                    this.setActionEnabled(this.replaceAction);
                    this.setActionEnabled(this.min_replaceAction);
                }else{
                    this.setActionDisabled(this.replaceAction);
                    this.setActionDisabled(this.min_replaceAction);
                }
            } else {
                this.setActionDisabled(this.replaceAction);
                this.setActionDisabled(this.min_replaceAction);
            }
        }else{
            if (!this.options.isReplace){
                this.setActionDisabled(this.replaceAction);
                this.setActionDisabled(this.min_replaceAction);
            }else{
                if (this.selectedAttachments.length && this.selectedAttachments.length===1){
                    this.setActionEnabled(this.replaceAction);
                    this.setActionEnabled(this.min_replaceAction);
                }else{
                    this.setActionDisabled(this.replaceAction);
                    this.setActionDisabled(this.min_replaceAction);
                }
            }
        }
    }
});
MWF.xApplication.cms.Xform.Attachment = MWF.CMSAttachment =  new Class({
	Extends: MWF.APPAttachment,
    options: {
        "moduleEvents": ["upload", "delete"]
    },

    initialize: function(node, json, form, options){
        this.node = $(node);
        this.node.store("module", this);
        this.json = json;
        this.form = form;
        this.field = true;
    },

	_loadUserInterface: function(){
		this.node.empty();
        this.loadAttachmentController();
	},
    loadAttachmentController: function(){
        //MWF.require("MWF.widget.AttachmentController", function() {
            var options = {
                //"style" : "cms",
                "title": "附件区域",
                "listStyle": this.json.listStyle || "icon",
                "size": this.json.size || "max",
                "resize": (this.json.size=="true") ? true : false,
                "attachmentCount": this.json.attachmentCount || 0,
                "isUpload": (this.json.isUpload=="y") ? true : false,
                "isDelete": (this.json.isDelete=="y") ? true : false,
                "isReplace": (this.json.isReplace=="y") ? true : false,
                "isDownload": (this.json.isDownload=="y") ? true : false,
                "isSizeChange": (this.json.isSizeChange=="y") ? true : false,
                "readonly": (this.json.readonly=="y") ? true : false,
                "toolbarGroupHidden" : this.json.toolbarGroupHidden || []
                //"downloadEvent" : this.json.downloadEvent
            };
            if (this.readonly) options.readonly = true;
            this.attachmentController = new MWF.xApplication.cms.Xform.AttachmentController(this.node, this, options);
            this.attachmentController.load();

            this.form.businessData.attachmentList.each(function (att) {
                if (att.site==this.json.id) this.attachmentController.addAttachment(att);
                //if (att.fileType.toLowerCase()==this.json.id.toLowerCase()) this.attachmentController.addAttachment(att);
            }.bind(this));
        //}.bind(this));
    },
    loadAttachmentSelecter: function( option, callback ){
        MWF.require("MWF.widget.AttachmentSelector", function() {
            var options = {
                //"style" : "cms",
                "title": "选择附件",
                "listStyle": "icon",
                "selectType" : "all",
                "size": "max",
                "attachmentCount": 0,
                "isUpload": true,
                "isDelete": true,
                "isReplace": true,
                "isDownload": true,
                "toBase64" : true,
                "base64MaxSize" : 800,
                "readonly": false
            };
            options = Object.merge( options, option );
            if (this.readonly) options.readonly = true;
            this.attachmentController = new MWF.widget.AttachmentSelector(this.node, this, options);
            this.attachmentController.load();

            this.postSelect = callback;

            this.form.businessData.attachmentList.each(function (att) {
                this.attachmentController.addAttachment(att);
            }.bind(this));
        }.bind(this));
    },
    selectAttachment: function(e, node, attachments){
        //if( attachments.length > 0 ){
        //    this.form.documentAction.getAttachmentUrl(attachments[attachments.length-1].data.id, this.form.businessData.document.id, function(url){
        //        if(this.postSelect)this.postSelect( url )
        //    }.bind(this))
        //}
        if( attachments.length > 0 ){
            var data = attachments[attachments.length-1].data;
            this.form.documentAction.getAttachmentUrl(  data.id, this.form.businessData.document.id, function(url){
                if( this.attachmentController.options.toBase64 ){
                    this.form.documentAction.getSubjectAttachmentBase64( data.id, this.attachmentController.options.base64MaxSize, function( json ){
                        var base64Code = json.data ? "data:image/png;base64,"+json.data.value : null;
                        if(this.postSelect)this.postSelect( url , data, base64Code )
                    }.bind(this) )
                }else{
                    if(this.postSelect)this.postSelect( url , data )
                }
            }.bind(this))
        }
    },
    createUploadFileNode: function(){
        this.attachmentController.doUploadAttachment({"site": this.json.id}, this.form.documentAction.action, "uploadAttachment", {"id": this.form.businessData.document.id}, null, function(o){
            if (o.id){
                this.form.documentAction.getAttachment(o.id, this.form.businessData.document.id, function(json){
                    if (json.data){
                        this.attachmentController.addAttachment(json.data);
                        this.form.businessData.attachmentList.push(json.data);
                    }
                    this.attachmentController.checkActions();

                    this.fireEvent("upload", [json.data]);
                }.bind(this))
            }
            this.attachmentController.checkActions();
        }.bind(this), function(files){
            if (files.length){
                if ((files.length+this.attachmentController.attachments.length > this.attachmentController.options.attachmentCount) && this.attachmentController.options.attachmentCount>0){
                    var content = MWF.xApplication.cms.Xform.LP.uploadMore;
                    content = content.replace("{n}", this.attachmentController.options.attachmentCount);
                    this.form.notice(content, "error");
                    return false;
                }
            }
            return true;
        }.bind(this));

        // this.uploadFileAreaNode = new Element("div");
        // var html = "<input name=\"file\" type=\"file\" multiple/>";
        // this.uploadFileAreaNode.set("html", html);
        //
        // this.fileUploadNode = this.uploadFileAreaNode.getFirst();
        // this.fileUploadNode.addEvent("change", function(){
        //     this.validationMode();
        //     var files = this.fileUploadNode.files;
        //     if (files.length){
        //         if ((files.length+this.attachmentController.attachments.length > this.attachmentController.options.attachmentCount) && this.attachmentController.options.attachmentCount>0){
        //             var content = MWF.xApplication.cms.Xform.LP.uploadMore;
        //             content = content.replace("{n}", this.attachmentController.options.attachmentCount);
        //             this.form.notice(content, "error");
        //         }else{
        //             for (var i = 0; i < files.length; i++) {
        //                 var file = files.item(i);
        //
        //                 var formData = new FormData();
        //                 formData.append('file', file);
        //                 formData.append('site', this.json.id);
        //                 //formData.append('folder', folderId);
        //
        //                 this.form.documentAction.uploadAttachment(this.form.businessData.document.id ,function(o, text){
        //                     if (o.id){
        //                         this.form.documentAction.getAttachment(o.id, this.form.businessData.document.id, function(json){
        //                             if (json.data){
        //                                 this.attachmentController.addAttachment(json.data);
        //                                 this.form.businessData.attachmentList.push(json.data);
        //                             }
        //                             this.attachmentController.checkActions();
        //
        //                             this.fireEvent("upload", [json.data]);
        //                         }.bind(this))
        //                     }
        //                     this.attachmentController.checkActions();
        //                 }.bind(this), null, formData, file);
        //             }
        //         }
        //     }
        // }.bind(this));
    },

    getData: function(){
        return this.attachmentController.getAttachmentNames();
    },
    //getInputData : function(){
    //    return this.getData();
    //},

    deleteAttachment: function(attachment){
        this.fireEvent("delete", [attachment.data]);
        this.form.documentAction.deleteAttachment(attachment.data.id, function(json ){
            this.attachmentController.removeAttachment(attachment);
            //this.form.businessData.attachmentList.erase( attachment.data )
            this.attachmentController.checkActions();
        }.bind(this));
    },

    createReplaceFileNode: function(attachment){
        this.attachmentController.doUploadAttachment({"site": this.json.id}, this.form.documentAction.action, "replaceAttachment",
            {"id": attachment.data.id, "documentid": this.form.businessData.document.id}, null, function(o){
                this.form.documentAction.getAttachment(attachment.data.id, this.form.businessData.document.id, function(json){
                    attachment.data = json.data;
                    attachment.reload();
                    this.attachmentController.checkActions();
                }.bind(this))
            }.bind(this), null);

        // this.replaceFileAreaNode = new Element("div");
        // var html = "<input name=\"file\" type=\"file\" multiple/>";
        // this.replaceFileAreaNode.set("html", html);
        //
        // this.fileReplaceNode = this.replaceFileAreaNode.getFirst();
        // this.fileReplaceNode.addEvent("change", function(){
        //     var files = this.fileReplaceNode.files;
        //     if (files.length){
        //         for (var i = 0; i < files.length; i++) {
        //             var file = files.item(i);
        //
        //             var formData = new FormData();
        //             formData.append('file', file);
        //             //    formData.append('site', this.json.id);
        //
        //             this.form.documentAction.replaceAttachment(attachment.data.id, this.form.businessData.document.id ,function(o, text){
        //                 this.form.documentAction.getAttachment(attachment.data.id, this.form.businessData.document.id, function(json){
        //                     attachment.data = json.data;
        //                     attachment.reload();
        //                     this.attachmentController.checkActions();
        //                 }.bind(this))
        //             }.bind(this), null, formData, file);
        //         }
        //     }
        // }.bind(this));
    },
    downloadAttachment: function(e, node, attachments){
        if (this.form.businessData.document){
            attachments.each(function(att){
                this.form.documentAction.getAttachmentStream(att.data.id, this.form.businessData.document.id);
            }.bind(this));
        }
    },
    openAttachment: function(e, node, attachments){
        if (this.form.businessData.document){
            attachments.each(function(att){
                this.form.documentAction.getAttachmentData(att.data.id, this.form.businessData.document.id);
            }.bind(this));
        }
        //this.downloadAttachment(e, node, attachment);
    },
    getAttachmentUrl: function(attachment, callback){
        if (this.form.businessData.document){
            this.form.documentAction.getAttachmentUrl(attachment.data.id, this.form.businessData.document.id, callback);
        }
    },
    validationConfigItem: function(routeName, data){
        var flag = (data.status=="all") ? true: (routeName == "publish");
        if (flag){
            var n = this.getData();
            var v = (data.valueType=="value") ? n : n.length;
            switch (data.operateor){
                case "isnull":
                    if (!v){
                        this.notValidationMode(data.prompt);
                        return false;
                    }
                    break;
                case "notnull":
                    if (v){
                        this.notValidationMode(data.prompt);
                        return false;
                    }
                    break;
                case "gt":
                    if (v>data.value){
                        this.notValidationMode(data.prompt);
                        return false;
                    }
                    break;
                case "lt":
                    if (v<data.value){
                        this.notValidationMode(data.prompt);
                        return false;
                    }
                    break;
                case "equal":
                    if (v==data.value){
                        this.notValidationMode(data.prompt);
                        return false;
                    }
                    break;
                case "neq":
                    if (v!=data.value){
                        this.notValidationMode(data.prompt);
                        return false;
                    }
                    break;
                case "contain":
                    if (v.indexOf(data.value)!=-1){
                        this.notValidationMode(data.prompt);
                        return false;
                    }
                    break;
                case "notcontain":
                    if (v.indexOf(data.value)==-1){
                        this.notValidationMode(data.prompt);
                        return false;
                    }
                    break;
            }
        }
        return true;
    }

}); 