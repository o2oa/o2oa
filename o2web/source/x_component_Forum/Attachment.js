MWF.require("MWF.widget.AttachmentController", null,false);

MWF.xApplication.Forum.Attachment = new Class({
    Implements: [Options, Events],
    options: {
        "documentId" : "",
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
            "style": "cms",
            "title": this.lp.attachmentArea,
            "size": this.options.size ,
            "resize": true,
            //"attachmentCount": this.json.attachmentCount || 0,
            "isUpload": (this.options.isNew || this.options.isEdited),
            "isDelete": (this.options.isNew || this.options.isEdited),
            "isReplace": false,
            "isDownload": true,
            "isSizeChange": this.options.isSizeChange,
            "readonly": (!this.options.isNew && !this.options.isEdited )
        };
        this.attachmentController = new MWF.widget.ATTER(this.node, this, options);
        this.attachmentController.load();

        //this.actions.listAttachmentInfo.each(function (att) {
        //    this.attachmentController.addAttachment(att);
        //}.bind(this));
        if( this.data ){
            this.data.each(function (att) {
                this.attachmentController.addAttachment(att);
            }.bind(this));
        }else if( !this.options.isNew && this.options.documentId && this.options.documentId!="" ){
            this.listAttachment( function( json ){
                json.data.each(function (att) {
                    this.attachmentController.addAttachment(att);
                }.bind(this));
            }.bind(this))
        }
    },
    transportData : function( json ){
        if( typeOf(json.data) == "array" ){
            json.data.each(function(d){
                d.person = d.creatorUid;
                d.lastUpdateTime = d.updateTime;
            })
        }else if( typeOf(json.data) == "object" ){
            var d = json.data;
            d.person = d.creatorUid;
            d.lastUpdateTime = d.updateTime;
        }else{
            json.each(function(d){
                d.person = d.creatorUid;
                d.lastUpdateTime = d.updateTime;
            })
        }
        return json;
    },
    listAttachment: function( callback ){

        if( !this.options.isNew && this.options.documentId && this.options.documentId!="" ){
            this.actions.listAttachment(this.options.documentId, function(json){
                if(callback)callback(this.transportData(json));
            }.bind(this))
        }
    },

    createUploadFileNode: function () {
        this.attachmentController.doUploadAttachment(
            {"site": this.options.documentId},
            this.actions.action,
            "uploadAttachment",
            {"id": this.options.documentId, "documentid":this.options.documentId},
            null,
            function(o){
                var j = o;
                if ( j.data ) {
                    //j.userMessage
                    var aid = typeOf( j.data ) == "object" ? j.data.id : j.data[0].id;
                    this.actions.getAttachment(aid, this.options.documentId, function (json) {
                        json = this.transportData(json);
                        if (json.data) {
                            this.attachmentController.addAttachment(json.data, o.messageId);
                            //this.attachmentList.push(json.data);
                        }
                        this.attachmentController.checkActions();

                        this.fireEvent("upload", [json.data]);
                        this.fireEvent("change");
                    }.bind(this))
                }
                this.attachmentController.checkActions();
            }.bind(this),
            function(files){
                this.isQueryUploadSuccess = true;
                this.fireEvent( "queryUploadAttachment" );
                return this.isQueryUploadSuccess;
            }.bind(this),
            null, null, null,
            function (o) { //错误的回调
            if (o.messageId && this.attachmentController.messageItemList) {
                var message = this.attachmentController.messageItemList[o.messageId];
                if( message && message.node )message.node.destroy();
            }
        }.bind(this));
        // this.uploadFileAreaNode = new Element("div");
        // var html = "<input name=\"file\" type=\"file\" multiple/>";
        // this.uploadFileAreaNode.set("html", html);
        //
        // this.fileUploadNode = this.uploadFileAreaNode.getFirst();
        // this.fileUploadNode.addEvent("change", function () {
        //     this.isQueryUploadSuccess = true;
        //     this.fireEvent( "queryUploadAttachment" );
        //     if( this.isQueryUploadSuccess ){
        //         var files = this.fileUploadNode.files;
        //         if (files.length) {
        //             for (var i = 0; i < files.length; i++) {
        //                 var file = files.item(i);
        //
        //                 var formData = new FormData();
        //                 formData.append('file', file);
        //                 formData.append('site', this.options.documentId);
        //
        //                 this.actions.uploadAttachment(this.options.documentId, function (o, text) {
        //                     j = JSON.decode(text);
        //                     if ( j.data ) {
        //                         //j.userMessage
        //                         var aid = typeOf( j.data ) == "object" ? j.data.id : j.data[0].id;
        //                         this.actions.getAttachment(aid, this.options.documentId, function (json) {
        //                             json = this.transportData(json);
        //                             if (json.data) {
        //                                 this.attachmentController.addAttachment(json.data);
        //                                 //this.attachmentList.push(json.data);
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
        //     }else{
        //         this.uploadFileAreaNode.destroy();
        //         this.uploadFileAreaNode = false;
        //     }
        // }.bind(this));
    },
    uploadAttachment: function (e, node) {
        //if (!this.uploadFileAreaNode) {
            this.createUploadFileNode();
        //}
        //this.fileUploadNode.click();
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
        this.actions.deleteAttachment(attachment.data.id, this.documentId, function (json) {
            this.attachmentController.removeAttachment(attachment);
            //this.form.businessData.attachmentList.erase( attachment.data )
            this.attachmentController.checkActions();
        }.bind(this));
    },

    replaceAttachment: function (e, node, attachment) {
        return false;
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

                    this.actions.replaceAttachment(attachment.data.id, this.options.documentId, function (o, text) {
                        this.form.documentAction.getAttachment(attachment.data.id, this.opetions.documentId, function (json) {
                            attachment.data = json.data;
                            attachment.reload();

                            if (o.messageId && this.attachmentController.messageItemList) {
                                var message = this.attachmentController.messageItemList[o.messageId];
                                if( message && message.node )message.node.destroy();
                            }

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
    //小程序文件是否支持打开
    checkMiniProgramFile: function(ext) {
        var exts = ["doc", "docx", "xls", "xlsx", "ppt", "pptx", "pdf"];
        for(var i = 0; i < exts.length; i++){
            if(ext === exts[i]){
                return true;
            }
        }
        return false;
    },
    downloadAttachment: function (e, node, attachments) {
        //if( this.app.access.isAnonymousDynamic() ){
        //    this.app.openLoginForm( function(){ this.app.reload() }.bind(this) )
        //}else {
            attachments.each(function (att) {
                if (window.o2android && window.o2android.downloadAttachment) {
                    window.o2android.downloadAttachment(att.data.id);
                } else if (window.webkit && window.webkit.messageHandlers && window.webkit.messageHandlers.downloadAttachment) {
                    window.webkit.messageHandlers.downloadAttachment.postMessage({ "id": att.data.id, "site": "bbs" });
                } else if (window.wx && window.__wxjs_environment === 'miniprogram' && this.checkMiniProgramFile(att.data.extension)) { //微信小程序
                    wx.miniProgram.navigateTo({ 
                        url: '../file/download?attId=' + att.data.id + '&type=bbs&subjectId=' + this.options.documentId
                    });
                } else {
                    if (layout.mobile) {
                        //移动端 企业微信 钉钉 用本地打开 防止弹出自带浏览器 无权限问题
                        this.actions.getAttachmentUrl(att.data.id, this.options.documentId, function (url) {
                            var xtoken = Cookie.read(o2.tokenName);
                            window.location = o2.filterUrl(url + "?"+o2.tokenName+"=" + xtoken);
                        });
                    } else {
                        this.actions.getAttachmentStream(att.data.id, this.options.documentId);
                    }
                }
            }.bind(this));
        //}
    },
    openAttachment: function (e, node, attachments) {
        //if( this.app.access.isAnonymousDynamic() ){
        //    this.app.openLoginForm( function(){ this.app.reload() }.bind(this) )
        //}else{
            attachments.each(function (att) {
                if (window.o2android && window.o2android.downloadAttachment) {
                    window.o2android.downloadAttachment(att.data.id);
                } else if (window.webkit && window.webkit.messageHandlers && window.webkit.messageHandlers.downloadAttachment) {
                    window.webkit.messageHandlers.downloadAttachment.postMessage({ "id": att.data.id, "site": "bbs" });
                } else if (window.wx && window.__wxjs_environment === 'miniprogram' && this.checkMiniProgramFile(att.data.extension)) { //微信小程序
                    wx.miniProgram.navigateTo({ 
                        url: '../file/download?attId=' + att.data.id + '&type=bbs&subjectId=' + this.options.documentId
                    });
                } else {
                    if (layout.mobile) {
                        //移动端 企业微信 钉钉 用本地打开 防止弹出自带浏览器 无权限问题
                        this.actions.getAttachmentUrl(att.data.id, this.options.documentId, function (url) {
                            var xtoken = Cookie.read(o2.tokenName);
                            window.location = o2.filterUrl(url + "?"+o2.tokenName+"=" + xtoken);
                        });
                    } else {
                        this.actions.getAttachmentData(att.data.id, this.options.documentId);
                    }
                }
                
            }.bind(this));
        //}
    },
    getAttachmentUrl: function (attachment, callback) {
        this.actions.getAttachmentUrl(attachment.data.id, this.options.documentId, callback);
    },
    getAttachmentData : function(){
        var data = [];
        this.attachmentController.attachments.each(function( att ){
            data.push(att.data)
        });
        return data;
    },
    getAttachmentIds : function(){
        var ids = [];
        this.attachmentController.attachments.each(function( att ){
            ids.push(att.data.id)
        });
        return ids;
    },
    loadAttachmentSelecter: function( option, callback ){
        MWF.require("MWF.widget.AttachmentSelector", function() {
            var options = {
                "style" : "cms",
                "title": this.lp.selectAttachment,
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
            this.attachmentController = new MWF.widget.AttachmentSelector(document.body, this, options);
            this.attachmentController.load();

            this.postSelect = callback;
            if( this.data ){
                this.data.each(function (att) {
                    this.attachmentController.addAttachment(att);
                }.bind(this));
            }else{
                this.listAttachment( function( json ){
                    json.data.each(function (att) {
                        this.attachmentController.addAttachment(att);
                    }.bind(this));
                }.bind(this))
            }
        }.bind(this));
    },
    selectAttachment: function(e, node, attachments){
        if( attachments.length > 0 ){
            var data = attachments[attachments.length-1].data;
            this.actions.getAttachmentUrl(  data.id, this.options.documentId, function(url){
                if( this.attachmentController.options.toBase64 ){
                    this.actions.getSubjectAttachmentBase64( data.id, this.attachmentController.options.base64MaxSize, function( json ){
                        var base64Code = json.data ? "data:image/png;base64,"+json.data.value : null;
                        if(this.postSelect)this.postSelect( url , data, base64Code )
                    }.bind(this) )
                }else{
                    if(this.postSelect)this.postSelect( url , data )
                }
            }.bind(this))
        }
    }

}); 
