MWF.xDesktop.requireApp("process.Xform", "Attachment", null, false);
//MWF.xDesktop.requireApp("cms.FormDesigner", "widget.AttachmentController", null, false);
MWF.xApplication.cms.Xform.AttachmentController = new Class({
    Extends: MWF.xApplication.process.Xform.AttachmentController,
    "options": {
        "checkTextEnable": false
    },
    openInOfficeControl: function (att, office) {
        if (office) {
            if (!office.openedAttachment || office.openedAttachment.id !== att.id) {
                office.save();
                MWF.Actions.get("x_cms_assemble_control").getAttachmentUrl(att.id, this.module.form.businessData.document.id, function (url) {
                    office.openedAttachment = { "id": att.id, "site": this.module.json.name, "name": att.name };
                    office.officeOCX.BeginOpenFromURL(url, true, this.readonly);
                }.bind(this));
            }
        }
    },
    setAttachmentConfig: function (readInput, editInput, controllerInput) {
        if (this.selectedAttachments.length) {
            var readList = readInput.retrieve("data-value");
            var editList = editInput.retrieve("data-value");
            var controllerList = controllerInput.retrieve("data-value");

            var readUnitList = [];
            var readIdentityList = [];
            var editUnitList = [];
            var editIdentityList = [];
            var controllerUnitList = [];
            var controllerIdentityList = [];

            if (readList) {
                readList.each(function (v) {
                    var vName = (typeOf(v) === "string") ? v : v.distinguishedName;
                    var len = vName.length;
                    var flag = vName.substring(len - 1, len);
                    if (flag === "U") readUnitList.push(vName);
                    if (flag === "I") readIdentityList.push(vName);
                });
            }
            if (editList) {
                editList.each(function (v) {
                    var vName = (typeOf(v) === "string") ? v : v.distinguishedName;
                    var len = vName.length;
                    var flag = vName.substring(len - 1, len);
                    if (flag === "U") editUnitList.push(vName);
                    if (flag === "I") editIdentityList.push(vName);
                });
            }
            if (controllerList) {
                controllerList.each(function (v) {
                    var vName = (typeOf(v) === "string") ? v : v.distinguishedName;
                    var len = vName.length;
                    var flag = vName.substring(len - 1, len);
                    if (flag === "U") controllerUnitList.push(vName);
                    if (flag === "I") controllerIdentityList.push(vName);
                });
            }

            this.selectedAttachments.each(function (att) {
                att.data.readUnitList = readUnitList;
                att.data.readIdentityList = readIdentityList;
                att.data.editUnitList = editUnitList;
                att.data.editIdentityList = editIdentityList;
                att.data.controllerUnitList = controllerUnitList;
                att.data.controllerIdentityList = controllerIdentityList;

                o2.Actions.get("x_cms_assemble_control").configAttachment(att.data.id, this.module.form.businessData.document.id, att.data);
            }.bind(this));
        }
    }
});
MWF.xApplication.cms.Xform.Attachment = MWF.CMSAttachment = new Class({
    Extends: MWF.APPAttachment,


    loadAttachmentController: function () {
        //MWF.require("MWF.widget.AttachmentController", function() {
        var options = {
            "style": this.json.style || "default",
            "title": "附件区域",
            "listStyle": this.json.listStyle || "icon",
            "size": this.json.size || "max",
            "resize": (this.json.resize === "y" || this.json.resize === "true"),
            "attachmentCount": this.json.attachmentCount || 0,
            "isUpload": (this.json.isUpload === "y" || this.json.isUpload === "true"),
            "isDelete": (this.json.isDelete === "y" || this.json.isDelete === "true"),
            "isReplace": (this.json.isReplace === "y" || this.json.isReplace === "true"),
            "isDownload": (this.json.isDownload === "y" || this.json.isDownload === "true"),
            "isSizeChange": (this.json.isSizeChange === "y" || this.json.isSizeChange === "true"),
            "readonly": (this.json.readonly === "y" || this.json.readonly === "true"),
            "availableListStyles": this.json.availableListStyles ? this.json.availableListStyles : ["list", "seq", "icon", "preview"],
            "isDeleteOption": this.json.isDelete,
            "isReplaceOption": this.json.isReplace,
            "toolbarGroupHidden": this.json.toolbarGroupHidden || []
            //"downloadEvent" : this.json.downloadEvent
        };
        if (this.readonly) options.readonly = true;
        this.attachmentController = new MWF.xApplication.cms.Xform.AttachmentController(this.node, this, options);
        this.attachmentController.load();

        this.form.businessData.attachmentList.each(function (att) {
            if (att.site == this.json.id) this.attachmentController.addAttachment(att);
            //if (att.fileType.toLowerCase()==this.json.id.toLowerCase()) this.attachmentController.addAttachment(att);
        }.bind(this));
        //}.bind(this));
    },
    loadAttachmentSelecter: function (option, callback) {
        MWF.require("MWF.widget.AttachmentSelector", function () {
            var options = {
                //"style" : "cms",
                "title": "选择附件",
                "listStyle": "icon",
                "selectType": "all",
                "size": "max",
                "attachmentCount": 0,
                "isUpload": true,
                "isDelete": true,
                "isReplace": true,
                "isDownload": true,
                "toBase64": true,
                "base64MaxSize": 800,
                "readonly": false
            };
            options = Object.merge(options, option);
            if (this.readonly) options.readonly = true;
            this.attachmentController = new MWF.widget.AttachmentSelector(this.node, this, options);
            this.attachmentController.load();

            this.postSelect = callback;

            this.form.businessData.attachmentList.each(function (att) {
                this.attachmentController.addAttachment(att);
            }.bind(this));
        }.bind(this));
    },
    selectAttachment: function (e, node, attachments) {
        //if( attachments.length > 0 ){
        //    this.form.documentAction.getAttachmentUrl(attachments[attachments.length-1].data.id, this.form.businessData.document.id, function(url){
        //        if(this.postSelect)this.postSelect( url )
        //    }.bind(this))
        //}
        if (attachments.length > 0) {
            var data = attachments[attachments.length - 1].data;
            this.form.documentAction.getAttachmentUrl(data.id, this.form.businessData.document.id, function (url) {
                if (this.attachmentController.options.toBase64) {
                    this.form.documentAction.getSubjectAttachmentBase64(data.id, this.attachmentController.options.base64MaxSize, function (json) {
                        var base64Code = json.data ? "data:image/png;base64," + json.data.value : null;
                        if (this.postSelect) this.postSelect(url, data, base64Code)
                    }.bind(this))
                } else {
                    if (this.postSelect) this.postSelect(url, data)
                }
            }.bind(this))
        }
    },
    createUploadFileNode: function () {
        var accept = "*";
        if (!this.json.attachmentExtType || (this.json.attachmentExtType.indexOf("other") != -1 && !this.json.attachmentExtOtherType)) {
        } else {
            accepts = [];
            var otherType = this.json.attachmentExtOtherType;
            this.json.attachmentExtType.each(function (v) {
                switch (v) {
                    case "word":
                        accepts.push(".doc, .docx, .dot, .dotx");
                        break;
                    case "excel":
                        accepts.push(".xls, .xlsx, .xlsm, .xlt, .xltx");
                        break;
                    case "ppt":
                        accepts.push(".pptx, .ppt, .pot, .potx, .potm");
                        break;
                    case "txt":
                        accepts.push(".txt");
                        break;
                    case "pic":
                        accepts.push(".bmp, .gif, .psd, .jpeg, .jpg, .png");
                        break;
                    case "pdf":
                        accepts.push(".pdf");
                        break;
                    case "zip":
                        accepts.push(".zip, .rar");
                        break;
                    case "audio":
                        accepts.push(".mp3, .wav, .wma, .wmv, .flac, .ape");
                        break;
                    case "video":
                        accepts.push(".avi, .mkv, .mov, .ogg, .mp4, .mpeg");
                        break;
                    case "other":
                        if (this.json.attachmentExtOtherType) accepts.push(this.json.attachmentExtOtherType);
                        break;
                }
            });
            accept = accepts.join(", ");
        }
        var size = 0;
        if (this.json.attachmentSize) size = this.json.attachmentSize.toFloat();
        this.attachmentController.doUploadAttachment({ "site": this.json.id }, this.form.documentAction.action, "uploadAttachment", { "id": this.form.businessData.document.id }, null, function (o) {
            if (o.id) {
                this.form.documentAction.getAttachment(o.id, this.form.businessData.document.id, function (json) {
                    if (json.data) {
                        if (!json.data.control) json.data.control = {};
                        this.attachmentController.addAttachment(json.data);
                        this.form.businessData.attachmentList.push(json.data);
                    }
                    this.attachmentController.checkActions();

                    this.fireEvent("upload", [json.data]);
                }.bind(this))
            }
            this.attachmentController.checkActions();
        }.bind(this), function (files) {
            if (files.length) {
                if ((files.length + this.attachmentController.attachments.length > this.attachmentController.options.attachmentCount) && this.attachmentController.options.attachmentCount > 0) {
                    var content = MWF.xApplication.cms.Xform.LP.uploadMore;
                    content = content.replace("{n}", this.attachmentController.options.attachmentCount);
                    this.form.notice(content, "error");
                    return false;
                }
            }
            return true;
        }.bind(this), true, accept, size);

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

    deleteAttachments: function (e, node, attachments) {
        var names = [];
        attachments.each(function (attachment) {
            names.push(attachment.data.name);
        }.bind(this));

        var _self = this;
        this.form.confirm("warn", e, MWF.xApplication.cms.Xform.LP.deleteAttachmentTitle, MWF.xApplication.cms.Xform.LP.deleteAttachment + "( " + names.join(", ") + " )", 300, 120, function () {
            while (attachments.length) {
                var attachment = attachments.shift();
                _self.deleteAttachment(attachment);
            }
            this.close();
        }, function () {
            this.close();
        }, null, null, this.form.json.confirmStyle);
    },
    deleteAttachment: function (attachment) {
        this.fireEvent("delete", [attachment.data]);
        this.form.documentAction.deleteAttachment(attachment.data.id, function (json) {
            this.attachmentController.removeAttachment(attachment);
            //this.form.businessData.attachmentList.erase( attachment.data )
            this.attachmentController.checkActions();

            if (this.form.officeList) {
                this.form.officeList.each(function (office) {
                    if (office.openedAttachment) {
                        if (office.openedAttachment.id == id) {
                            office.loadOfficeEdit();
                        }
                    }
                }.bind(this));
            }

            this.fireEvent("afterDelete", [attachment.data]);
        }.bind(this));
    },

    createReplaceFileNode: function (attachment) {
        var accept = "*";
        if (!this.json.attachmentExtType || this.json.attachmentExtType.indexOf("other") != -1 && !this.json.attachmentExtOtherType) {
        } else {
            accepts = [];
            var otherType = this.json.attachmentExtOtherType;
            this.json.attachmentExtType.each(function (v) {
                switch (v) {
                    case "word":
                        accepts.push(".doc, .docx, .dot, .dotx");
                        break;
                    case "excel":
                        accepts.push(".xls, .xlsx, .xlsm, .xlt, .xltx");
                        break;
                    case "ppt":
                        accepts.push(".pptx, .ppt, .pot, .potx, .potm");
                        break;
                    case "txt":
                        accepts.push(".txt");
                        break;
                    case "pic":
                        accepts.push(".bmp, .gif, .psd, .jpeg, .jpg");
                        break;
                    case "pdf":
                        accepts.push(".pdf");
                        break;
                    case "zip":
                        accepts.push(".zip, .rar");
                        break;
                    case "audio":
                        accepts.push(".mp3, .wav, .wma, .wmv, .flac, .ape");
                        break;
                    case "video":
                        accepts.push(".avi, .mkv, .mov, .ogg, .mp4, .mpeg");
                        break;
                    case "other":
                        if (this.json.attachmentExtOtherType) accepts.push(this.json.attachmentExtOtherType);
                        break;
                }
            });
            accept = accepts.join(", ");
        }
        var size = 0;
        if (this.json.attachmentSize) size = this.json.attachmentSize.toFloat();
        this.attachmentController.doUploadAttachment({ "site": this.json.id }, this.form.documentAction.action, "replaceAttachment",
            { "id": attachment.data.id, "documentid": this.form.businessData.document.id }, null, function (o) {
                this.form.documentAction.getAttachment(attachment.data.id, this.form.businessData.document.id, function (json) {
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


    downloadAttachment: function (e, node, attachments) {
        if (this.form.businessData.document) {
            attachments.each(function (att) {
                if (window.o2android && window.o2android.downloadAttachment) {
                    window.o2android.downloadAttachment(att.data.id);
                } else if (window.webkit && window.webkit.messageHandlers && window.webkit.messageHandlers.downloadAttachment) {
                    window.webkit.messageHandlers.downloadAttachment.postMessage({ "id": att.data.id, "site": this.json.id });
                } else {
                    if (layout.mobile) {
                        //移动端 企业微信 钉钉 用本地打开 防止弹出自带浏览器 无权限问题
                        this.form.documentAction.getAttachmentUrl(att.data.id, this.form.businessData.document.id, function (url) {
                            var xtoken = Cookie.read("x-token");
                            window.location = url + "?x-token=" + xtoken;
                        });
                    } else {
                        this.form.documentAction.getAttachmentStream(att.data.id, this.form.businessData.document.id);
                    }
                }
            }.bind(this));
        }
    },
    openAttachment: function (e, node, attachments) {
        if (this.form.businessData.document) {
            attachments.each(function (att) {
                if (window.o2android && window.o2android.downloadAttachment) {
                    window.o2android.downloadAttachment(att.data.id);
                } else if (window.webkit && window.webkit.messageHandlers && window.webkit.messageHandlers.downloadAttachment) {
                    window.webkit.messageHandlers.downloadAttachment.postMessage({ "id": att.data.id, "site": this.json.id });
                } else {
                    if (layout.mobile) {
                        //移动端 企业微信 钉钉 用本地打开 防止弹出自带浏览器 无权限问题
                        this.form.documentAction.getAttachmentUrl(att.data.id, this.form.businessData.document.id, function (url) {
                            var xtoken = Cookie.read("x-token");
                            window.location = url + "?x-token=" + xtoken;
                        });
                    } else {
                        this.form.documentAction.getAttachmentData(att.data.id, this.form.businessData.document.id);
                    }

                }
            }.bind(this));
        }
        //this.downloadAttachment(e, node, attachment);
    },
    getAttachmentUrl: function (attachment, callback) {
        if (this.form.businessData.document) {
            this.form.documentAction.getAttachmentUrl(attachment.data.id, this.form.businessData.document.id, callback);
        }
    },
    validationConfigItem: function (routeName, data) {
        var flag = (data.status == "all") ? true : (routeName == "publish");
        if (flag) {
            var n = this.getData();
            var v = (data.valueType == "value") ? n : n.length;
            switch (data.operateor) {
                case "isnull":
                    if (!v) {
                        this.notValidationMode(data.prompt);
                        return false;
                    }
                    break;
                case "notnull":
                    if (v) {
                        this.notValidationMode(data.prompt);
                        return false;
                    }
                    break;
                case "gt":
                    if (v > data.value) {
                        this.notValidationMode(data.prompt);
                        return false;
                    }
                    break;
                case "lt":
                    if (v < data.value) {
                        this.notValidationMode(data.prompt);
                        return false;
                    }
                    break;
                case "equal":
                    if (v == data.value) {
                        this.notValidationMode(data.prompt);
                        return false;
                    }
                    break;
                case "neq":
                    if (v != data.value) {
                        this.notValidationMode(data.prompt);
                        return false;
                    }
                    break;
                case "contain":
                    if (v.indexOf(data.value) != -1) {
                        this.notValidationMode(data.prompt);
                        return false;
                    }
                    break;
                case "notcontain":
                    if (v.indexOf(data.value) == -1) {
                        this.notValidationMode(data.prompt);
                        return false;
                    }
                    break;
            }
        }
        return true;
    }
}); 