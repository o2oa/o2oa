MWF.xDesktop.requireApp("process.Xform", "Attachment", null, false);
MWF.xDesktop.requireApp("cms.FormDesigner", "widget.AttachmentController", null, false);
MWF.xApplication.cms.Xform.AttachmentController = new Class({
    Extends: MWF.xApplication.cms.FormDesigner.widget.AttachmentController,
    "options": {
        "officeFiles": ["doc","docx","dotx","dot","xls","xlsx","xlsm","xlt","xltx","pptx","ppt","pot","potx","potm","pdf"]
    },
    checkDeleteAction: function(){
        if (this.options.readonly){
            this.setActionDisabled(this.deleteAction);
            this.setActionDisabled(this.min_deleteAction);
            return false;
        }
        if (this.options.isDeleteOption!=="y" && this.options.isDeleteOption!=="n"){
            if (this.selectedAttachments.length && this.selectedAttachments.length==1){
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
    openInOfficeControl: function(att, office){
        if (office){
            if (!office.openedAttachment || office.openedAttachment.id!==att.id){
                office.save();
                MWF.Actions.get("x_cms_assemble_control").getAttachmentUrl(att.id, this.module.form.businessData.document.id, function(url){
                        office.openedAttachment = {"id": att.id, "site": this.module.json.name, "name": att.name};
                        office.officeOCX.BeginOpenFromURL(url, true, this.readonly);
                    }.bind(this));
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
    },
    replaceAttachment: function(e, node){
        var att = this.selectedAttachments[0].data;

        if (this.module.json.isOpenInOffice && this.module.json.officeControlName && (this.options.officeFiles.indexOf(att.extension)!==-1)){
            var office = this.module.form.all[this.module.json.officeControlName];
            if (office){
                if (this.min_closeOfficeAction) this.setActionEnabled(this.min_closeOfficeAction);
                if (this.closeOfficeAction) this.setActionEnabled(this.closeOfficeAction);
                this.openInOfficeControl(att, office);
            }else{
                if (this.selectedAttachments.length && this.selectedAttachments.length==1){
                    if (this.module) this.module.replaceAttachment(e, node, this.selectedAttachments[0]);
                }
            }
        }else{
            if (this.selectedAttachments.length && this.selectedAttachments.length==1){
                if (this.module) this.module.replaceAttachment(e, node, this.selectedAttachments[0]);
            }
        }
    },

    //createTopNode: function(){
    //    if (this.options.title){
    //        if (!this.titleNode) this.titleNode = new Element("div", {"styles": this.css.titleNode, "text": this.options.title}).inject(this.node);
    //    }
    //    this.topNode = new Element("div", {"styles": this.css.topNode}).inject(this.node);
    //    this.createEditGroupActions();
    //    this.createReadGroupActions();
    //    this.createListGroupActions();
    //    if (this.module.json.isOpenInOffice && this.module.json.officeControlName) this.createOfficeGroupActions();
    //    this.createViewGroupActions();
    //},
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
        this.checkActions();
    },
    createOfficeGroupActions: function(){
        this.officeActionBoxNode = new Element("div", {"styles": this.css.actionsBoxNode}).inject(this.topNode);
        this.officeActionsGroupNode = new Element("div", {"styles": this.css.actionsGroupNode}).inject(this.officeActionBoxNode);

        this.closeOfficeAction = this.createAction(this.officeActionsGroupNode, "closeOffice", MWF.LP.widget.closeOffice, function(e, node){
            this.closeAttachmentOffice(e, node);
        }.bind(this));
        if (this.closeOfficeAction) this.setActionDisabled(this.closeOfficeAction);
    },
    loadMinActions: function(){
        this.min_uploadAction = this.createAction(this.minActionAreaNode, "upload", MWF.LP.widget.upload, function(e, node){
            this.uploadAttachment(e, node);
        }.bind(this));

        this.min_deleteAction = this.createAction(this.minActionAreaNode, "delete", MWF.LP.widget["delete"], function(e, node){
            this.deleteAttachment(e, node);
        }.bind(this));

        this.min_replaceAction = this.createAction(this.minActionAreaNode, "replace", MWF.LP.widget.replace, function(e, node){
            this.replaceAttachment(e, node);
        }.bind(this));
        this.min_downloadAction = this.createAction(this.minActionAreaNode, "download", MWF.LP.widget.download, function(e, node){
            this.downloadAttachment(e, node);
        }.bind(this));

        if (this.module.json.isOpenInOffice && this.module.json.officeControlName){
            this.min_closeOfficeAction = this.createAction(this.minActionAreaNode, "closeOffice", MWF.LP.widget.closeOffice, function(e, node){
                this.closeAttachmentOffice(e, node);
            }.bind(this));
            if (this.min_closeOfficeAction) this.setActionDisabled(this.closeOfficeAction);
        }

        this.createSeparate(this.minActionAreaNode);

        this.sizeAction = this.createAction(this.minActionAreaNode, "max", MWF.LP.widget.min, function(){
            this.changeControllerSize();
        }.bind(this));
    },
    closeAttachmentOffice: function(){
        var office = this.module.form.all[this.module.json.officeControlName];
        if (office){
            office.openFile();
            if (this.min_closeOfficeAction) this.setActionDisabled(this.min_closeOfficeAction);
            if (this.closeOfficeAction) this.setActionDisabled(this.closeOfficeAction);
        }
    }
});
MWF.xApplication.cms.Xform.Attachment = MWF.CMSAttachment =  new Class({
	Extends: MWF.APPAttachment,
    options: {
        "moduleEvents": ["upload", "delete", "afterDelete", "load","queryLoad","postLoad"]
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
        this.fireEvent("load");
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
                "isUpload": (this.json.isUpload==="y" || this.json.isUpload==="true"),
                "isDelete": (this.json.isDelete==="y" || this.json.isDelete==="true"),
                "isReplace": (this.json.isReplace==="y" || this.json.isReplace==="true"),
                "isDownload": (this.json.isDownload==="y" || this.json.isDownload==="true"),
                "isSizeChange": (this.json.isSizeChange==="y" || this.json.isSizeChange==="true"),
                "readonly": (this.json.readonly==="y" || this.json.readonly==="true"),
                "availableListStyles" : this.json.availableListStyles ? this.json.availableListStyles : ["list","seq","icon","preview"],
                "isDeleteOption": this.json.isDelete,
                "isReplaceOption": this.json.isReplace,
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
    uploadAttachment: function(e, node){
        if (window.o2android && window.o2android.uploadAttachment){
            window.o2android.uploadAttachment(this.json.id);
        }else if(window.webkit && window.webkit.messageHandlers) {
            window.webkit.messageHandlers.uploadAttachment.postMessage({"site": this.json.id});
        }else{
            // if (!this.uploadFileAreaNode){
                 this.createUploadFileNode();
            // }
            // this.fileUploadNode.click();
        }
    },

    getData: function(){
        return this.attachmentController.getAttachmentNames();
    },
    //getInputData : function(){
    //    return this.getData();
    //},

    deleteAttachments: function(e, node, attachments){
        var names = [];
        attachments.each(function(attachment){
            names.push(attachment.data.name);
        }.bind(this));

        var _self = this;
        this.form.confirm("warn", e, MWF.xApplication.cms.Xform.LP.deleteAttachmentTitle, MWF.xApplication.cms.Xform.LP.deleteAttachment+"( "+names.join(", ")+" )", 300, 120, function(){
            while (attachments.length){
                var attachment = attachments.shift();
                _self.deleteAttachment(attachment);
            }
            this.close();
        }, function(){
            this.close();
        }, null);
    },
    deleteAttachment: function(attachment){
        this.fireEvent("delete", [attachment.data]);
        this.form.documentAction.deleteAttachment(attachment.data.id, function(json ){
            this.attachmentController.removeAttachment(attachment);
            //this.form.businessData.attachmentList.erase( attachment.data )
            this.attachmentController.checkActions();
            this.fireEvent("afterDelete", [attachment.data]);
        }.bind(this));
    },

    replaceAttachment: function(e, node, attachment){
        if (window.o2android && window.o2android.replaceAttachment){
            window.o2android.replaceAttachment(attachment.data.id, this.json.id);
        }else if(window.webkit && window.webkit.messageHandlers && window.webkit.messageHandlers.replaceAttachment) {
            window.webkit.messageHandlers.replaceAttachment.postMessage({"id": attachment.data.id, "site": this.json.id});
        }else {
            var _self = this;
            this.form.confirm("warn", e, MWF.xApplication.process.Xform.LP.replaceAttachmentTitle, MWF.xApplication.process.Xform.LP.replaceAttachment+"( "+attachment.data.name+" )", 350, 120, function(){
                _self.replaceAttachmentFile(attachment);
                this.close();
            }, function(){
                this.close();
            }, null);
        }
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

    replaceAttachmentFile: function(attachment){
        //if (!this.replaceFileAreaNode){
            this.createReplaceFileNode(attachment);
        // }
        // this.fileReplaceNode.click();
    },
    downloadAttachment: function(e, node, attachments){
        if (this.form.businessData.document){
            attachments.each(function(att){
                if (window.o2android && window.o2android.downloadAttachment){
                    window.o2android.downloadAttachment(att.data.id);
                }else if(window.webkit && window.webkit.messageHandlers) {
                    window.webkit.messageHandlers.downloadAttachment.postMessage({"id": att.data.id, "site": this.json.id});
                }else{
                    this.form.documentAction.getAttachmentStream(att.data.id, this.form.businessData.document.id);
                }
            }.bind(this));
        }
    },
    openAttachment: function(e, node, attachments){
        if (this.form.businessData.document){
            attachments.each(function(att){
                if (window.o2android && window.o2android.downloadAttachment){
                    window.o2android.downloadAttachment(att.data.id);
                }else if(window.webkit && window.webkit.messageHandlers) {
                    window.webkit.messageHandlers.downloadAttachment.postMessage({"id": att.data.id, "site": this.json.id});
                }else {
                    this.form.documentAction.getAttachmentData(att.data.id, this.form.businessData.document.id);
                 }
            }.bind(this));
        }
        //this.downloadAttachment(e, node, attachment);
    },
    getAttachmentUrl: function(attachment, callback){
        if (this.form.businessData.document){
            this.form.documentAction.getAttachmentUrl(attachment.data.id, this.form.businessData.document.id, callback);
        }
    },
    createErrorNode: function(text){
        var node = new Element("div");
        var iconNode = new Element("div", {
            "styles": {
                "width": "20px",
                "height": "20px",
                "float": "left",
                "background": "url("+"/x_component_process_Xform/$Form/default/icon/error.png) center center no-repeat"
            }
        }).inject(node);
        var textNode = new Element("div", {
            "styles": {
                "line-height": "20px",
                "margin-left": "20px",
                "color": "red",
                "word-break": "keep-all"
            },
            "text": text
        }).inject(node);
        return node;
    },
    notValidationMode: function(text){
        if (!this.isNotValidationMode){
            this.isNotValidationMode = true;
            this.node.store("borderStyle", this.node.getStyles("border-left", "border-right", "border-top", "border-bottom"));
            this.node.setStyle("border", "1px solid red");

            this.errNode = this.createErrorNode(text).inject(this.node, "after");
            this.showNotValidationMode(this.node);
        }
    },
    showNotValidationMode: function(node){
        var p = node.getParent("div");
        if (p){
            if (p.get("MWFtype") == "tab$Content"){
                if (p.getParent("div").getStyle("display")=="none"){
                    var contentAreaNode = p.getParent("div").getParent("div");
                    var tabAreaNode = contentAreaNode.getPrevious("div");
                    var idx = contentAreaNode.getChildren().indexOf(p.getParent("div"));
                    var tabNode = tabAreaNode.getChildren()[idx];
                    tabNode.click();
                    p = tabAreaNode.getParent("div");
                }
            }
            this.showNotValidationMode(p);
        }
    },
    validationMode: function(){
        if (this.isNotValidationMode){
            this.isNotValidationMode = false;
            this.node.setStyles(this.node.retrieve("borderStyle"));
            if (this.errNode){
                this.errNode.destroy();
                this.errNode = null;
            }
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
    },
    validationConfig: function(routeName, opinion){
        if (this.json.validationConfig){
            if (this.json.validationConfig.length){
                for (var i=0; i<this.json.validationConfig.length; i++) {
                    var data = this.json.validationConfig[i];
                    if (!this.validationConfigItem(routeName, data)) return false;
                }
            }
            return true;
        }
        return true;
    },
    validation: function(routeName, opinion){
        if (!this.validationConfig(routeName, opinion))  return false;

        if (!this.json.validation) return true;
        if (!this.json.validation.code) return true;
        var flag = this.form.Macro.exec(this.json.validation.code, this);
        if (!flag) flag = MWF.xApplication.process.Xform.LP.notValidation;
        if (flag.toString()!="true"){
            this.notValidationMode(flag);
            return false;
        }
        return true;
    }

}); 