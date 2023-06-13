MWF.require("MWF.widget.AttachmentController", null,false);

MWF.xApplication.Forum.AttachmentController = new Class({
    Extends: o2.widget.AttachmentController,

    reloadAttachments: function(){
        if (this.options.size==="min"){
            this.minContent.empty();
            var atts = this.attachments;
            this.attachments = [];
            while (atts.length){
                var att = atts.shift();
                this.attachments.push(new MWF.xApplication.Forum.AttachmentController.AttachmentMin(att.data, this));
            }
        }else{
            this.content.empty();
            var atts = this.attachments;
            this.attachments = [];
            while (atts.length){
                var att = atts.shift();
                this.attachments.push(new MWF.xApplication.Forum.AttachmentController.Attachment(att.data, this));
            }
        }
        this.checkActions();
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
        var atts = this.attachments;
        this.attachments = [];
        while (atts.length){
            var att = atts.shift();
            this.attachments.push(new MWF.xApplication.Forum.AttachmentController.Attachment(att.data, this));
        }
        this.checkActions();
        //this.attachments = atts;
    },
    loadMin: function(){

        if (!this.node) this.node = new Element("div", {"styles": this.css.container_min});

        if (!this.minActionAreaNode){
            this.minActionAreaNode = new Element("div", {"styles": this.css.minActionAreaNode }).inject(this.node);
            //this.minContent = new Element("div", {"styles": this.css.minContentNode}).inject(this.node);

            this.loadMinActions();

            this.node.inject(this.container);

            this.setEvent();
        }else{
            this.minActionAreaNode.setStyle("display", "");

            this.minActionAreaNode.empty();

            this.loadMinActions();

            this.setEvent();
        }
        var hiddenGroup = this.options.toolbarGroupHidden;
        var flag = hiddenGroup.contains("edit") && hiddenGroup.contains("read")  && hiddenGroup.contains("view");

        if( flag )this.minActionAreaNode.setStyle("display","none");

        if( !this.minContent ){

            this.minContent = new Element("div", {"styles":
                    layout.mobile ? this.css.minContentNode_mobile : this.css.minContentNode
            }).inject(this.node);
            if( layout.mobile ){
                this.minContent.setStyle("clear","both");
            }
        }else{
            this.minContent.setStyle("display", "block");
            this.minContent.empty();
        }

        var atts = this.attachments;
        this.attachments = [];
        while (atts.length){
            var att = atts.shift();
            this.attachments.push(new MWF.xApplication.Forum.AttachmentController.AttachmentMin(att.data, this));
        }
        this.checkActions();
        //this.attachments = atts;
    },
    addAttachment: function(data, messageId, isCheckPosition){
        if (this.options.size=="min"){
            this.attachments.push(new MWF.xApplication.Forum.AttachmentController.AttachmentMin(data, this, messageId, isCheckPosition));
        }else{
            this.attachments.push(new MWF.xApplication.Forum.AttachmentController.Attachment(data, this, messageId, isCheckPosition));
        }
        this.checkActions();
    }
});

MWF.xApplication.Forum.AttachmentController.Attachment = new Class({
    Extends: o2.widget.AttachmentController.Attachment,
    initialize: function(data, controller, messageId, isCheckPosition){
        this.data = data;

        this.controller = controller;
        this.css = this.controller.css;
        this.listStyle = this.controller.options.listStyle;
        this.content = this.controller.content;
        this.isSelected = false;
        this.seq = this.controller.attachments.length+1;
        this.isCheckPosition = isCheckPosition;
        this.actions = [];

        if (messageId && this.controller.messageItemList) {
            this.message = this.controller.messageItemList[messageId];
        }

        if( this.controller.isAnonymous && this.data.creatorUid === this.controller.anonymousPerson ){
            this.data.person = this.controller.anonymousName;
        }else if(MWFForum.isUseNickName()){
            this.data.person = this.data.nickName;
        }else if(!this.data.person && this.data.creatorUid ){
            this.data.person = this.data.creatorUid;
        }

        this.load();
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
        this.inforNode = new Element("div", {"styles": this.css.attachmentInforNode});

        //var person = MWFForum.isUseNickName()?this.data.nickName:( this.data.person || this.data.creatorUid );
        var person = this.data.person;

        var html = "<div style='overflow:hidden; font-weight: bold'>"+this.data.name+"</div>";
        html += "<div style='clear: both; overflow:hidden'><div style='width:40px; float:left; font-weight: bold'>"+o2.LP.widget.uploader+": </div><div style='width:120px; float:left; margin-left:10px'>"+ person +"</div></div>";
        html += "<div style='clear: both; overflow:hidden'><div style='width:40px; float:left; font-weight: bold'>"+o2.LP.widget.uploadTime+": </div><div style='width:120px; float:left; margin-left:10px'>"+this.data.createTime+"</div></div>";
        html += "<div style='clear: both; overflow:hidden'><div style='width:40px; float:left; font-weight: bold'>"+o2.LP.widget.modifyTime+": </div><div style='width:120px; float:left; margin-left:10px'>"+this.data.lastUpdateTime+"</div></div>";
        if(this.data.activityName)html += "<div style='clear: both; overflow:hidden'><div style='width:40px; float:left; font-weight: bold'>"+o2.LP.widget.uploadActivity+": </div><div style='width:120px; float:left; margin-left:10px'>"+(this.data.activityName || o2.LP.widget.unknow)+"</div></div>";
        html += "<div style='clear: both; overflow:hidden'><div style='width:40px; float:left; font-weight: bold'>"+o2.LP.widget.size+": </div><div style='width:120px; float:left; margin-left:10px'>"+size+"</div></div>";
        this.inforNode.set("html", html);

        if (callback) callback();
    }
});
MWF.xApplication.Forum.AttachmentController.AttachmentMin = new Class({
    Extends: MWF.xApplication.Forum.AttachmentController.Attachment,

    initialize: function(data, controller, messageId, isCheckPosition){
        this.data = data;

//        if( !this.data.person && this.data.creatorUid )this.data.person = this.data.creatorUid;

        this.controller = controller;
        this.css = this.controller.css;
        this.content = this.controller.minContent;
        this.isSelected = false;
        this.isCheckPosition = isCheckPosition;
        this.seq = this.controller.attachments.length+1;

        if (messageId && this.controller.messageItemList) {
            this.message = this.controller.messageItemList[messageId];
        }

        if( this.controller.isAnonymous && this.data.creatorUid === this.controller.anonymousPerson ){
            this.data.person = this.controller.anonymousName;
        }else if(MWFForum.isUseNickName()){
            this.data.person = this.data.nickName;
        }else if(!this.data.person && this.data.creatorUid ){
            this.data.person = this.data.creatorUid;
        }

        this.load();
    },
    load: function(){
        debugger;
        if (this.message){
            this.node = new Element("div").inject(this.message.node, "after");
            this.message.node.destroy();
            delete this.controller.messageItemList[this.message.data.id];
        }else{
            this.node = new Element("div").inject(this.content);
        }

        if( this.isCheckPosition && this.isNumber(this.data.orderNumber) ){
            var attList = this.controller.attachments;
            for( var i=0; i<attList.length; i++ ){
                var att = attList[i];
                if( !this.isNumber(att.data.orderNumber) || this.data.orderNumber < att.data.orderNumber ){
                    this.node.inject( att.node, "before" );
                    break;
                }
            }
        }

        //this.node = new Element("div").inject(this.content);
        //this.loadList();
        switch (this.controller.options.listStyle){
            case "list":
                this.loadList();
                break;
            case "icon":
                this.loadIcon();
                break;
            case "preview":
                this.loadPreview();
                break;
            case "sequence":
                this.loadSequence();
                break;
        }

        this.createInforNode();
        if (!Browser.Platform.ios && !layout.mobile){
            this.tooltip = new mBox.Tooltip({
                content: this.inforNode,
                setStyles: {content: {padding: 15, lineHeight: 20}},
                attach: this.iconImgNode,
                transition: 'flyin'
            });
        }
        this.setEvent();
    },
    loadList: function() {
        debugger;
        this.node.setStyles( layout.mobile ? this.css.minAttachmentNode_list_mobile : this.css.minAttachmentNode_list);

        if( !layout.mobile ){
            this.sepNode = new Element("div", {"styles": this.css.minAttachmentSepNode_list}).inject(this.node);
        }

        this.actionAreaNode = new Element("div", {"styles": this.css.minAttachmentActionAreaNode}).inject(this.node);

        if ( this.controller.isAttDownloadAvailable(this) ) {
            this.downloadAction = this.createAction(this.actionAreaNode, "download_single", "download_single_over", o2.LP.widget.download, function (e, node) {
                this.controller.downloadAttachment(e, node);
            }.bind(this));
        }
        //this.actions.push( this.downloadAction );

        if ( this.controller.isAttDeleteAvailable(this) ) {
            this.deleteAction = this.createAction(this.actionAreaNode, "delete_single", "delete_single_over", o2.LP.widget["delete"], function (e, node) {
                this.controller.deleteAttachment(e, node);
            }.bind(this));
        }
        //this.actions.push( this.deleteAction );

        if (this.controller.configAttachment) {
            if ( this.controller.isAttConfigAvailable(this) ) {
                this.configAction = this.createAction(this.actionAreaNode, "config_single", "config_single_over", o2.LP.widget.configAttachment, function (e, node) {
                    this.controller.configAttachment(e, node);
                }.bind(this), o2.LP.widget.configAttachmentText );
                //this.actions.push( this.configAction );
            }
        }

        if (this.isSelected) this.node.setStyles(this.css.minAttachmentNode_list_selected);

        this.iconNode = new Element("div", {"styles": this.css.minAttachmentIconNode_list}).inject(this.node);
        this.iconImgAreaNode = new Element("div", {"styles": this.css.minAttachmentIconImgAreaNode_list}).inject(this.iconNode);
        this.iconImgNode = new Element("img", {"styles": this.css.minAttachmentIconImgNode_list}).inject(this.iconImgAreaNode);
        this.iconImgNode.set({"src": this.getIcon(), "border": 0});

        this.textNode = new Element("div", {"styles": this.css.minAttachmentTextNode_list}).inject(this.node);
        this.textNode.set("text", this.data.name);

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
        this.textSizeNode = new Element("div", {"styles": this.css.minAttachmentSizeNode_list}).inject(this.textNode);
        this.textSizeNode.set("text", "（"+size+"）");

        this.node.set("title",this.data.name + "（"+size+"）");

    },
    loadSequence: function(){
        this.node.setStyles(this.css.minAttachmentNode_sequence);

        this.actionAreaNode = new Element("div", {"styles":this.css.minAttachmentActionAreaNode}).inject(this.node);

        if ( this.controller.isAttDownloadAvailable(this) ) {
            this.downloadAction = this.createAction(this.actionAreaNode, "download_single", "download_single_over", o2.LP.widget.download, function (e, node) {
                this.controller.downloadAttachment(e, node);
            }.bind(this));
        }
        //this.actions.push( this.downloadAction );

        if ( this.controller.isAttDeleteAvailable(this) ) {
            this.deleteAction = this.createAction(this.actionAreaNode, "delete_single", "delete_single_over", o2.LP.widget["delete"], function (e, node) {
                this.controller.deleteAttachment(e, node);
            }.bind(this));
        }
        //this.actions.push( this.deleteAction );


        if (this.controller.configAttachment) {
            if ( this.controller.isAttConfigAvailable(this) ) {
                this.configAction = this.createAction(this.actionAreaNode, "config_single", "config_single_over", MWF.LP.widget.configAttachment, function (e, node) {
                    this.controller.configAttachment(e, node);
                }.bind(this));
                //this.actions.push( this.configAction );
            }
        }

        if (this.isSelected) this.node.setStyles(this.css.minAttachmentNode_list_selected);

        this.sequenceNode = new Element("div", {"styles": this.css.attachmentSeqNode_sequence, "text": (this.seq || 1)}).inject(this.node);
        this.iconNode = new Element("div", {"styles": this.css.minAttachmentIconNode_list}).inject(this.node);
        this.iconImgAreaNode = new Element("div", {"styles": this.css.minAttachmentIconImgAreaNode_list}).inject(this.iconNode);
        this.iconImgNode = new Element("img", {"styles": this.css.minAttachmentIconImgNode_list}).inject(this.iconImgAreaNode);
        this.iconImgNode.set({"src": this.getIcon(), "border": 0});

        this.textNode = new Element("div", {"styles": this.css.minAttachmentTextNode_list}).inject(this.node);
        this.textNode.set("text", this.data.name);
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
        this.textSizeNode = new Element("div", {"styles": this.css.minAttachmentSizeNode_list}).inject(this.textNode);
        this.textSizeNode.set("text", "（"+size+"）");
    },
    setEvent: function(){
        this.node.addEvents({
            "mouseover": function(){
                if (!this.isSelected){
                    if (this.controller.options.listStyle==="list" || this.controller.options.listStyle==="sequence"){
                        this.node.setStyles(this.css["minAttachmentNode_"+this.controller.options.listStyle+"_over"]);
                    }else{
                        this.node.setStyles(this.css["attachmentNode_"+this.controller.options.listStyle+"_over"]);
                    }
                }
            }.bind(this),
            "mouseout": function(){
                if (!this.isSelected){
                    if (this.controller.options.listStyle==="list" || this.controller.options.listStyle==="sequence"){
                        var cssKey = "minAttachmentNode_"+this.controller.options.listStyle + ( layout.mobile ? "_mobile" : "" );
                        this.node.setStyles(this.css[cssKey]);
                    }else{
                        this.node.setStyles(this.css["attachmentNode_"+this.controller.options.listStyle]);
                    }
                }
            }.bind(this),
            "mousedown": function(e){this.selected(e);e.stopPropagation();}.bind(this),
            "click": function(e){e.stopPropagation();}.bind(this),
            "dblclick": function(e){this.openAttachment(e);}.bind(this)
        });
    }

});

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
        this.attachmentController = new MWF.xApplication.Forum.AttachmentController(this.node, this, options);
        this.attachmentController.isAnonymous = this.isAnonymous;
        this.attachmentController.anonymousPerson = this.anonymousPerson;
        this.attachmentController.anonymousName = this.anonymousName;
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
                            var xtoken = layout.session.token;
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
                            var xtoken = layout.session.token;
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
