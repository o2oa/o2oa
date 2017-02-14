MWF.xDesktop.requireApp("cms.Xform", "$Module", null, false);
MWF.xApplication.cms.Xform.Attachment = MWF.CMSAttachment =  new Class({
	Extends: MWF.CMS$Module,
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
        MWF.require("MWF.widget.AttachmentController", function() {
            var options = {
                "style" : "cms",
                "title": "附件区域",
                "listStyle": this.json.listStyle || "icon",
                "size": this.json.size || "max",
                "resize": (this.json.size=="true") ? true : false,
                "attachmentCount": this.json.attachmentCount || 0,
                "isUpload": (this.json.isUpload=="true") ? true : false,
                "isDelete": (this.json.isDelete=="true") ? true : false,
                "isReplace": (this.json.isReplace=="true") ? true : false,
                "isDownload": (this.json.isDownload=="true") ? true : false,
                "isSizeChange": (this.json.isSizeChange=="true") ? true : false,
                "readonly": (this.json.readonly=="true") ? true : false
            }
            if (this.readonly) options.readonly = true;
            this.attachmentController = new MWF.widget.ATTER(this.node, this, options);
            this.attachmentController.load();

            this.form.businessData.attachmentList.each(function (att) {
                if (att.fileType.toLowerCase()==this.json.id.toLowerCase()) this.attachmentController.addAttachment(att);
            }.bind(this));
        }.bind(this));
    },

    loadAttachmentSelecter: function( option, callback ){
        MWF.require("MWF.widget.AttachmentSelector", function() {
            var options = {
                "style" : "cms",
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
            }
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
    _loadEvents: function(editorConfig){
        Object.each(this.json.events, function(e, key){
            if (e.code){
                if (this.options.moduleEvents.indexOf(key)!=-1){
                    this.addEvent(key, function(event){
                        return this.form.CMSMacro.fire(e.code, this, event);
                    }.bind(this));
                }else{
                    this.node.addEvent(key, function(event){
                        return this.form.CMSMacro.fire(e.code, this, event);
                    }.bind(this));
                }
            }
        }.bind(this));

    },
    getData: function(){
        return this.attachmentController.getAttachmentNames();
    },
    createUploadFileNode: function(){
        this.uploadFileAreaNode = new Element("div");
        var html = "<input name=\"file\" type=\"file\" multiple/>";
        this.uploadFileAreaNode.set("html", html);

        this.fileUploadNode = this.uploadFileAreaNode.getFirst();
        this.fileUploadNode.addEvent("change", function(){

            var files = this.fileUploadNode.files;
            if (files.length){
                if ((files.length+this.attachmentController.attachments.length > this.attachmentController.options.attachmentCount) && this.attachmentController.options.attachmentCount>0){
                    var content = MWF.xApplication.cms.Xform.LP.uploadMore;
                    content = content.replace("{n}", this.attachmentController.options.attachmentCount);
                    this.form.notice(content, "error");
                }else{
                    for (var i = 0; i < files.length; i++) {
                        var file = files.item(i);

                        var formData = new FormData();
                        formData.append('file', file);
                        formData.append('site', this.json.id);
                        //formData.append('folder', folderId);

                        this.form.documentAction.uploadAttachment(this.form.businessData.document.id ,function(o, text){
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
                        }.bind(this), null, formData, file);
                    }
                }
            }
        }.bind(this));
    },
    uploadAttachment: function(e, node){
        if (!this.uploadFileAreaNode){
            this.createUploadFileNode();
        }
        //var fileNode = this.uploadFileAreaNode.getFirst();
        this.fileUploadNode.click();
    },
    deleteAttachments: function(e, node, attachments){
        var names = [];
        attachments.each(function(attachment){
            names.push(attachment.data.name);
        }.bind(this));

        var _self = this;
        this.form.confirm("warn", e, MWF.xApplication.cms.Xform.LP.deleteAttachmentTitle, MWF.xApplication.cms.Xform.LP.deleteAttachment+"( "+names.join(", ")+" )", 300, 120, function(){
            while (attachments.length){
                attachment = attachments.shift();
                _self.deleteAttachment(attachment);
            }
            this.close();
        }, function(){
            this.close();
        }, null);
    },
    deleteAttachment: function(attachment){
        this.fireEvent("delete", [attachment.data]);
        this.form.documentAction.deleteAttachment(attachment.data.id, this.form.businessData.document.id, function(json ){
            this.attachmentController.removeAttachment(attachment);
            //this.form.businessData.attachmentList.erase( attachment.data )
            this.attachmentController.checkActions();
        }.bind(this));
    },

    replaceAttachment: function(e, node, attachment){
        var _self = this;
        this.form.confirm("warn", e, MWF.xApplication.cms.Xform.LP.replaceAttachmentTitle, MWF.xApplication.cms.Xform.LP.replaceAttachment+"( "+attachment.data.name+" )", 300, 120, function(){
            _self.replaceAttachmentFile(attachment);
            this.close();
        }, function(){
            this.close();
        }, null);
    },

    createReplaceFileNode: function(attachment){
        this.replaceFileAreaNode = new Element("div");
        var html = "<input name=\"file\" type=\"file\" multiple/>";
        this.replaceFileAreaNode.set("html", html);

        this.fileReplaceNode = this.replaceFileAreaNode.getFirst();
        this.fileReplaceNode.addEvent("change", function(){

            var files = this.fileReplaceNode.files;
            if (files.length){
                for (var i = 0; i < files.length; i++) {
                    var file = files.item(i);

                    var formData = new FormData();
                    formData.append('file', file);
                //    formData.append('site', this.json.id);

                    this.form.documentAction.replaceAttachment(attachment.data.id, this.form.businessData.document.id ,function(o, text){
                        this.form.documentAction.getAttachment(attachment.data.id, this.form.businessData.document.id, function(json){
                            attachment.data = json.data;
                            attachment.reload();
                            this.attachmentController.checkActions();
                        }.bind(this))
                    }.bind(this), null, formData, file);
                }
            }
        }.bind(this));
    },
    replaceAttachmentFile: function(attachment){
        if (!this.replaceFileAreaNode){
            this.createReplaceFileNode(attachment);
        }
        this.fileReplaceNode.click();
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
    createErrorNode: function(text){
        var node = new Element("div");
        var iconNode = new Element("div", {
            "styles": {
                "width": "20px",
                "height": "20px",
                "float": "left",
                "background": "url("+"/x_component_cms_Xform/$Form/default/icon/error.png) center center no-repeat"
            }
        }).inject(node);
        var textNode = new Element("div", {
            "styles": {
                "line-height": "20px",
                "margin-left": "20px",
                "color": "red"
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
    validation: function(){
        if (!this.json.validation) return true;
        if (!this.json.validation.code) return true;
        var flag = this.form.CMSMacro.exec(this.json.validation.code, this);
        if (!flag) flag = MWF.xApplication.cms.Xform.LP.notValidation;
        if (flag.toString()!="true"){
            this.notValidationMode(flag);
            return false;
        }
        return true;
    }

}); 