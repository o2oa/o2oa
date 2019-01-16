MWF.require("MWF.widget.AttachmentController", null,false);

MWF.xApplication.Strategy.PriorityAttachment = new Class({
    Implements: [Options, Events],
    options: {
        "workId" : "",
        "isNew": false,
        "isEdited" : true,
        "size" : "max",
        "isSizeChange" : true
    },

    initialize: function (node, app, actions, lp, options) {
        this.setOptions(options);
        this.app = app;
        this.node = $(node);
        this.actions = actions;
        this.lp = lp;
    },

    load: function () {
        this.loadAttachmentController();
    },
    loadAttachmentController: function () {
        var options = {
            "style": "default",
            "title": "附件区域",
            "size": this.options.size ,
            "resize": true,
            //"attachmentCount": this.json.attachmentCount || 0,
            "isUpload": (this.options.isNew || this.options.isEdited) ? true : false,
            "isDelete": (this.options.isNew || this.options.isEdited) ? true : false,
            "isReplace": false,
            "isDownload": true,
            "isSizeChange": this.options.isSizeChange,
            "readonly": (!this.options.isNew && !this.options.isEdited ) ? true : false
        };
        this.attachmentController = new MWF.widget.ATTER(this.node, this, options);
        this.attachmentController.load();

        //this.actions.listAttachmentInfo.each(function (att) {
        //    this.attachmentController.addAttachment(att);
        //}.bind(this));

        this.listAttachment( function( json ){
            json.data.each(function (att) {
                this.attachmentController.addAttachment(att);
            }.bind(this));
        }.bind(this))
    },
    transportData : function( json ){
        if( typeOf(json.data) == "array" ){
            json.data.each(function(d){
                //d.person = d.creatorUid;
                d.lastUpdateTime = d.updateTime;
            })
        }else if( typeOf(json.data) == "object" ){
            var d = json.data;
            //d.person = d.creatorUid;
            d.lastUpdateTime = d.updateTime;
        }else{
            json.each(function(d){
                //d.person = d.creatorUid;
                d.lastUpdateTime = d.updateTime;
            })
        }
        return json;
    },
    listAttachment: function( callback ){
        if(this.options.workId){
            this.actions.listPriorityAttachment(this.options.workId, function(json){
                if(callback)callback(this.transportData(json));
            }.bind(this))
        }
    },

    createUploadFileNode: function () {
        this.uploadFileAreaNode = new Element("div");
        var html = "<input name=\"file\" type=\"file\" multiple/>";
        this.uploadFileAreaNode.set("html", html);

        this.fileUploadNode = this.uploadFileAreaNode.getFirst();
        this.fileUploadNode.addEvent("change", function () {
            this.isQueryUploadSuccess = true;
            this.fireEvent( "queryUploadAttachment" );
            if( this.isQueryUploadSuccess ){
                var files = this.fileUploadNode.files;
                if (files.length) {
                    for (var i = 0; i < files.length; i++) {
                        var file = files.item(i);
                        var formData = new FormData();
                        formData.append('file', file);
                        formData.append('site', this.options.workId);

                        this.actions.uploadPriorityAttachment(this.options.workId, function (o, text) {
                            j = JSON.decode(text);
                            //if (j.data.length>0 && j.data[0] && j.data[0].id) {
                            if (j.data && j.data.id) {
                                this.actions.getPriorityAttachment(j.data.id, this.options.workId, function (json) {
                                    json = this.transportData(json);
                                    if (json.data) {
                                        this.attachmentController.addAttachment(json.data);
                                        //this.attachmentList.push(json.data);
                                    }
                                    this.attachmentController.checkActions();

                                    this.fireEvent("upload", [json.data]);
                                }.bind(this))
                            }
                            this.attachmentController.checkActions();
                        }.bind(this), null, formData, file);
                    }
                }
            }else{
                this.uploadFileAreaNode.destroy();
                this.uploadFileAreaNode = false;
            }
        }.bind(this));
    },
    uploadAttachment: function (e, node) {
        if (!this.uploadFileAreaNode) {
            this.createUploadFileNode();
        }
        this.fileUploadNode.click();
    },
    deleteAttachments: function (e, node, attachments) {
        var names = [];
        attachments.each(function (attachment) {
            names.push(attachment.data.name);
        }.bind(this));

        var _self = this;
        this.app.confirm("warn", e, this.lp.deleteAttachmentTitle, this.lp.deleteAttachment + "( " + names.join(", ") + " )", 300, 120, function () {
            while (attachments.length) {
                attachment = attachments.shift();
                _self.deleteAttachment(attachment);
            }
            this.close();
        }, function () {
            this.close();
        }, null);
    },
    deleteAttachment: function (attachment) {
        this.fireEvent("delete", [attachment.data]);
        this.actions.deletePriorityAttachment(attachment.data.id, this.options.workId, function (json) {
            this.attachmentController.removeAttachment(attachment);
            //this.form.businessData.attachmentList.erase( attachment.data )
            this.attachmentController.checkActions();
        }.bind(this));
    },

    replaceAttachment: function (e, node, attachment) {
        var _self = this;
        this.form.confirm("warn", e, this.lp.replaceAttachmentTitle, this.lp.replaceAttachment + "( " + attachment.data.name + " )", 300, 120, function () {
            _self.replaceAttachmentFile(attachment);
            this.close();
        }, function () {
            this.close();
        }, null);
    },

    createReplaceFileNode: function (attachment) {
        this.replaceFileAreaNode = new Element("div");
        var html = "<input name=\"file\" type=\"file\" multiple/>";
        this.replaceFileAreaNode.set("html", html);

        this.fileReplaceNode = this.replaceFileAreaNode.getFirst();
        this.fileReplaceNode.addEvent("change", function () {

            var files = this.fileReplaceNode.files;
            if (files.length) {
                for (var i = 0; i < files.length; i++) {
                    var file = files.item(i);

                    var formData = new FormData();
                    formData.append('file', file);
                    //    formData.append('site', this.json.id);

                    this.actions.replaceAttachment(attachment.data.id, this.options.workId, function (o, text) {
                        this.form.documentAction.getAttachment(attachment.data.id, this.options.workId, function (json) {
                            attachment.data = json.data;
                            attachment.reload();
                            this.attachmentController.checkActions();
                        }.bind(this))
                    }.bind(this), null, formData, file);
                }
            }
        }.bind(this));
    },
    replaceAttachmentFile: function (attachment) {
        if (!this.replaceFileAreaNode) {
            this.createReplaceFileNode(attachment);
        }
        this.fileReplaceNode.click();
    },
    downloadAttachment: function (e, node, attachments) {
        attachments.each(function (att) {
            this.actions.getPriorityAttachmentStream(att.data.id, this.options.workId);
        }.bind(this));
    },
    openAttachment: function (e, node, attachments) {
        attachments.each(function (att) {
            this.actions.getPriorityAttachmentStream(att.data.id, this.options.workId);
        }.bind(this));
    },
    getAttachmentUrl: function (attachment, callback) {
        this.actions.getAttachmentUrl(attachment.data.id, this.options.workId, callback);
    }
}); 