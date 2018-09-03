MWF.widget = MWF.widget || {};
MWF.widget.AttachmentController = MWF.widget.ATTER  = new Class({
    Extends: MWF.widget.Common,
	Implements: [Options, Events],
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
        "images": ["bmp", "gif", "png", "jpeg", "jpg", "jpe", "ico"],
        "audios": ["mp3", "wav", "wma", "wmv"],
        "videos": ["avi", "mkv", "mov", "ogg", "mp4", "mpa", "mpe", "mpeg", "mpg", "rmvb"]
	},
	initialize: function(container, module, options){
		this.setOptions(options);
		this.pages = [];

		this.path = MWF.defaultPath+"/widget/$AttachmentController/";
		this.cssPath = MWF.defaultPath+"/widget/$AttachmentController/"+this.options.style+"/css.wcss";
		this._loadCss();

        MWF.getJSON("/x_component_File/$Main/icon.json", function(json){
            this.icons = json;
        }.bind(this), false, false);

        this.module = module;

        this.actions = [];
        this.attachments = [];
        this.selectedAttachments = [];
		this.container = $(container);
	},
    load: function(){
        if (this.options.size==="min"){
            this.loadMin();
        }else{
            this.loadMax();
        }
    },
	loadMax: function(){
        if (!this.node) this.node = new Element("div", {"styles": this.css.container});

        if (!this.topNode){
            this.createTopNode();
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
            this.topNode.setStyle("display", "block");
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
        if (!this.node) this.node = new Element("div", {"styles": this.css.container_min});

        if (!this.minActionAreaNode){
            this.minActionAreaNode = new Element("div", {"styles": this.css.minActionAreaNode}).inject(this.node);
            this.minContent = new Element("div", {"styles": this.css.minContentNode}).inject(this.node);

            this.loadMinActions();

            this.node.inject(this.container);

            //if (this.options.readonly) this.setReadonly();
            this.checkActions();

            this.setEvent();
        }else{
            this.minActionAreaNode.setStyle("display", "block");
            this.minContent.setStyle("display", "block");
            this.minContent.empty();
        }
        var atts = [];
        while (this.attachments.length){
            var att = this.attachments.shift();
            atts.push(new MWF.widget.AttachmentController.AttachmentMin(att.data, this));
        }
        this.attachments = atts;
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

        this.createSeparate(this.minActionAreaNode);

        this.sizeAction = this.createAction(this.minActionAreaNode, "max", MWF.LP.widget.min, function(){
            this.changeControllerSize();
        }.bind(this));
    },

    setEvent: function(){
        if (this.contentScrollNode) this.contentScrollNode.addEvents({
            "mousedown": this.unSelectedAttachments.bind(this)
        });
        if (this.minContent) this.minContent.addEvents({
            "mousedown": this.unSelectedAttachments.bind(this)
        });
    },
    createContentNode: function(){
        this.contentScrollNode = new Element("div.contentScrollNode", {"styles": this.css.contentScrollNode}).inject(this.node);
        this.content = new Element("div.content", {"styles": this.css.contentNode}).inject(this.contentScrollNode);

        MWF.require("MWF.widget.ScrollBar", function(){
            new MWF.widget.ScrollBar(this.contentScrollNode, {
                "style":"attachment", "where": "before", "distance": 30, "friction": 4,	"axis": {"x": false, "y": true}
            });
        }.bind(this));
    },
    createBottomNode: function(){
        this.bottomNode = new Element("div", {"styles": this.css.bottomNode}).inject(this.node);
    },
    createResizeNode: function(){
        this.resizeNode = new Element("div", {"styles": this.css.resizeNode}).inject(this.bottomNode);
        this.resizeDrag = new Drag(this.resizeNode, {
            "snap": "2",
            "onStart": function(el, e){
                el.store("startY", e.event.y);
                el.store("nodeHeight", this.node.getSize().y);

                if (!this.nodeHeight){
                    this.nodeHeight = this.node.getStyle("min-height").toFloat();
                }
            }.bind(this),
            "onDrag": function(el, e){
                var y = el.retrieve("startY");
                var nodeHeight = el.retrieve("nodeHeight");

                var setY = (e.event.y-y)+nodeHeight;
                if (setY<this.nodeHeight) setY = this.nodeHeight;

                var setContentY = setY-50;

                this.node.setStyle("height", ""+setY+"px");
                this.contentScrollNode.setStyle("height", ""+setContentY+"px");
            }.bind(this)
        });
    },

    createTopNode: function(){
        if (this.options.title){
            if (!this.titleNode) this.titleNode = new Element("div", {"styles": this.css.titleNode, "text": this.options.title}).inject(this.node);
        }
        this.topNode = new Element("div", {"styles": this.css.topNode}).inject(this.node);
        this.createEditGroupActions();
        this.createReadGroupActions();
        this.createListGroupActions();
        this.createViewGroupActions();
    },

    createEditGroupActions: function(){
        this.editActionBoxNode = new Element("div", {"styles": this.css.actionsBoxNode}).inject(this.topNode);
        this.editActionsGroupNode = new Element("div", {"styles": this.css.actionsGroupNode}).inject(this.editActionBoxNode);
        this.uploadAction = this.createAction(this.editActionsGroupNode, "upload", MWF.LP.widget.upload, function(e, node){
            this.uploadAttachment(e, node);
        }.bind(this));

        this.deleteAction = this.createAction(this.editActionsGroupNode, "delete", MWF.LP.widget["delete"], function(e, node){
            this.deleteAttachment(e, node);
        }.bind(this));

        this.replaceAction = this.createAction(this.editActionsGroupNode, "replace", MWF.LP.widget.replace, function(e, node){
            this.replaceAttachment(e, node);
        }.bind(this));

        // this.officeAction = this.createAction(this.editActionsGroupNode, "office", MWF.LP.widget.office, function(e, node){
        //     this.openInOfficeControl(e, node);
        // }.bind(this));

        this.editActionSeparateNode = this.createSeparate(this.editActionsGroupNode);
    },

    createReadGroupActions: function(){
        //this.readActionBoxNode = new Element("div", {"styles": this.css.actionsBoxNode}).inject(this.topNode);
        //this.readActionsGroupNode = new Element("div", {"styles": this.css.actionsGroupNode}).inject(this.readActionBoxNode);
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
    createListGroupActions: function(){
        this.listActionBoxNode = new Element("div", {"styles": this.css.actionsBoxNode}).inject(this.topNode);
        this.listActionsGroupNode = new Element("div", {"styles": this.css.actionsGroupNode}).inject(this.listActionBoxNode);

        this.listAction = this.createAction(this.listActionsGroupNode, "list", MWF.LP.widget.list, function(){
            this.changeListStyle("list");
        }.bind(this));

        this.iconAction = this.createAction(this.listActionsGroupNode, "icon", MWF.LP.widget.icon, function(){
            this.changeListStyle("icon");
        }.bind(this));

        this.previewAction = this.createAction(this.listActionsGroupNode, "preview", MWF.LP.widget.preview, function(){
            this.changeListStyle("preview");
        }.bind(this));
    },

    createViewGroupActions: function(){
        this.viewActionBoxNode = new Element("div", {"styles": this.css.actionsBoxNode}).inject(this.topNode);
        this.viewActionBoxNode.setStyles({"float": "right", "margin-right": "7px"});
        this.viewActionsGroupNode = new Element("div", {"styles": this.css.actionsGroupNode}).inject(this.viewActionBoxNode);
        this.sizeAction = this.createAction(this.viewActionsGroupNode, "min", MWF.LP.widget.min, function(){
            this.changeControllerSize();
        }.bind(this));
    },



    createSeparate: function(groupNode){
        var separateNode = new Element("div", {"styles": this.css.separateNode}).inject(groupNode);
        return separateNode;
    },
    createAction: function(groupNode, img, title, click){
        var actionNode = new Element("div", {"styles": this.css.actionNode, "title": title}).inject(groupNode);
        var actionIconNode = new Element("div", {"styles": this.css.actionIconNode}).inject(actionNode);
        actionIconNode.setStyle("background-image", "url("+MWF.defaultPath+"/widget/$AttachmentController/"+this.options.style+"/icon/"+img+".png)");

        var _self = this;
        actionNode.addEvents({
            "mouseover": function(){
                if (!this.retrieve("disabled")) if (!this.retrieve("selected")) this.setStyle("background", "url("+MWF.defaultPath+"/widget/$AttachmentController/"+_self.options.style+"/overbg.png)");
            },
            "mouseout": function(){
                if (!this.retrieve("disabled")) if (!this.retrieve("selected")) this.setStyle("background", "transparent");
            },
            "click": function(e){
                if (!this.retrieve("disabled")) _self.doAction(e, this, click);
            }
        });
        this.actions.push(actionNode);
        return actionNode;
    },
    checkActions: function(){
    //    if (this.options.readonly){
    //        this.setReadonly();
    //    }else{
            this.checkUploadAction();
            this.checkDeleteAction();
            this.checkReplaceAction();
            //this.checkOfficeAction();
            this.checkDownloadAction();
            this.checkSizeAction();

            this.checkListStyleAction();
    //    }
    },
    checkUploadAction: function(){
        if (this.options.readonly){
            this.setActionDisabled(this.uploadAction);
            this.setActionDisabled(this.min_uploadAction);
            return false;
        }
        if (!this.options.isUpload){
            this.setActionDisabled(this.uploadAction);
            this.setActionDisabled(this.min_uploadAction);
        }else{
            if (this.options.attachmentCount!=0){
                if (this.attachments.length>=this.options.attachmentCount){
                    this.setActionDisabled(this.uploadAction);
                    this.setActionDisabled(this.min_uploadAction);
                }else{
                    this.setActionEnabled(this.uploadAction);
                    this.setActionEnabled(this.min_uploadAction);
                }
            }else{
                this.setActionEnabled(this.uploadAction);
                this.setActionEnabled(this.min_uploadAction);
            }
        }
    },
    checkDeleteAction: function(){
        if (this.options.readonly){
            this.setActionDisabled(this.deleteAction);
            this.setActionDisabled(this.min_deleteAction);
            return false;
        }
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
    },
    // checkOfficeAction: function(){
    //     if (this.officeAction) this.officeAction.setStyle("display", "none");
    //     if (this.min_officeAction) this.min_officeAction.setStyle("display", "none");
    // },
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
    checkDownloadAction: function(){
        if (!this.options.isDownload){
            this.setActionDisabled(this.downloadAction);
            this.setActionDisabled(this.downloadAllAction);
        }else{
            if (this.selectedAttachments.length){
                this.setActionEnabled(this.downloadAction);
            }else{
                this.setActionDisabled(this.downloadAction);
            }
            this.setActionEnabled(this.downloadAllAction);
        }
    },
    checkSizeAction: function(){
        if (this.options.isSizeChange){
            this.setActionEnabled(this.sizeAction);
        }else{
            this.setActionDisabled(this.sizeAction);
        }
    },
    checkListStyleAction: function(){
        switch (this.options.listStyle){
            case "list":
                this.setActionSelcted(this.listAction);
                this.setActionUnSelcted(this.iconAction);
                this.setActionUnSelcted(this.previewAction);
                break;
            case "icon":
                this.setActionUnSelcted(this.listAction);
                this.setActionSelcted(this.iconAction);
                this.setActionUnSelcted(this.previewAction);
                break;
            case "preview":
                this.setActionUnSelcted(this.listAction);
                this.setActionUnSelcted(this.iconAction);
                this.setActionSelcted(this.previewAction);
                break
        }
    },
    setActionSelcted: function(action){
        if (action){
            if (!action.retrieve("selected")){
                action.setStyle("background", "url("+MWF.defaultPath+"/widget/$AttachmentController/"+this.options.style+"/selectedbg.png)");
                action.store("selected", true);
            }
        }

    },
    setActionUnSelcted: function(action){
        if (action){
            if (action.retrieve("selected")){
                action.setStyle("background", "");
                action.store("selected", false);
            }
        }
    },

    setActionEnabled: function(action){
        if (action){
            if (action.retrieve("disabled")){
                var iconNode = action.getFirst();
                var icon = iconNode.getStyle("background-image");
                var ext = icon.substr(icon.lastIndexOf(".")+1, icon.length);
                icon = icon.substr(0, icon.lastIndexOf("_gray"))+"."+ext;
                iconNode.setStyle("background-image", icon);
                action.store("disabled", false);
            }
        }
    },
    setActionDisabled: function(action){
        if (action){
            if (!action.retrieve("disabled")){
                var iconNode = action.getFirst();
                var icon = iconNode.getStyle("background-image");
                var ext = icon.substr(icon.lastIndexOf(".")+1, icon.length);
                icon = icon.substr(0, icon.lastIndexOf("."))+"_gray."+ext;
                iconNode.setStyle("background-image", icon);
                action.store("disabled", true);
            }
        }
    },
    setReadonly: function() {
        this.actions.each(function(action){
            this.setActionDisabled(action);
        }.bind(this));
    },


    doAction: function(e, node, action){
        if (action){
            action.apply(this, [e, node]);
        }
    },

    uploadAttachment: function(e, node){
        if (this.module) this.module.uploadAttachment(e, node);
    },
    doUploadAttachment: function(obj, action, invokeUrl, parameter, finish, every, beforeUpload, multiple, accept){
        if (FormData.expiredIE){
            this.doInputUploadAttachment(obj, action, invokeUrl, parameter, finish, every, beforeUpload, multiple, accept);
        }else{
            this.doFormDataUploadAttachment(obj, action, invokeUrl, parameter, finish, every, beforeUpload, multiple, accept);
        }
    },
    addUploadMessage: function(fileName){
        var contentHTML = "";
        contentHTML = "<div style=\"overflow: hidden\"><div style=\"height: 2px; border:0px solid #999; margin: 3px 0px\">" +
            "<div style=\"height: 2px; background-color: #acdab9; width: 0px;\"></div></div>" +
            "<div style=\"height: 20px; line-height: 20px\">"+MWF.LP.desktop.action.uploadTitle+"</div></div>" +
            "<iframe name='o2_upload_iframe' style='display:none'></iframe>" ;
        var msg = {
            "subject": MWF.LP.desktop.action.uploadTitle,
            "content": fileName+"<br/>"+contentHTML
        };
        if (layout.desktop.message){
            var messageItem = layout.desktop.message.addMessage(msg);
            messageItem.close = function(callback, e){
                if (!messageItem.completed){

                }else{
                    messageItem.closeItem(callback, e);
                }
            };
        }
        window.setTimeout(function(){
            if (layout.desktop.message) if (!layout.desktop.message.isShow) layout.desktop.message.show();
        }.bind(this), 300);

        return messageItem;
    },
    setMessageTitle: function(messageItem, text){
        if (messageItem) messageItem.subjectNode.set("text", text);
    },
    setMessageText: function(messageItem, text){
        if (messageItem){
            var progressNode = messageItem.contentNode.getFirst("div").getFirst("div");
            var progressPercentNode = progressNode.getFirst("div");
            var progressInforNode = messageItem.contentNode.getFirst("div").getLast("div");
            progressInforNode.set("text", text);
            messageItem.dateNode.set("text", (new Date()).format("db"));
        }

    },
    doInputUploadAttachment: function(obj, action, invokeUrl, parameter, finish, every, beforeUpload, multiple, accept){
        var restActions = action;
        if (typeOf(action)=="string"){
            restActions = MWF.Actions.get(action).action;
        }
        restActions.getActions(function(){
            var url = restActions.actions[invokeUrl];
            url = restActions.address+url.uri;

            Object.each(parameter, function(v, k){
                url = url.replace("{"+k+"}", v);
            });

            debugger;
            var maskNode = this.module.content;
            if (!maskNode){
                if (this.module.form){
                    maskNode = this.module.form.app.content;
                }
            }
            if (!maskNode) maskNode = this.node;
            //var maskNode = this.module.content || this.module.node;
            if (maskNode){
                maskNode.mask({
                    "style": {
                        "opacity": 0.7,
                        "background-color": "#999"
                    }
                });
            }

            this.inputUploadAreaNode = new Element("div", {"styles": this.css.inputUploadAreaNode}).inject(this.container);
            this.inputUploadAreaNode.position({
                relativeTo: this.module.content,
                position: "center"
            });

            var messageItem = null;
            document.O2UploadCallbackFun = function(str){
                var json = JSON.decode(str);
                messageItem.completed = true;
                if (json.type==="success"){
                    if (every) every(json.data);
                    this.setMessageTitle(messageItem, MWF.LP.desktop.action.uploadComplete);
                    this.setMessageText(messageItem, MWF.LP.desktop.action.uploadComplete);
                    if(finish) finish();
                }else{
                    //formNode.unmask();
                    this.setMessageTitle(messageItem, MWF.LP.desktop.action.sendError);
                    this.setMessageText(messageItem, MWF.LP.desktop.action.sendError+": "+json.message);
                    MWF.xDesktop.notice("error", {x: "right", y:"top"}, json.message);
                }
            }.bind(this);

            var formNode = new Element("form", {
                "method": "POST",
                "action": url+"/callback/window.frameElement.ownerDocument.O2UploadCallbackFun",
                //"action": url,
                "enctype": "multipart/form-data",
                "target": "o2_upload_iframe"
            }).inject(this.inputUploadAreaNode);

            var titleNode = new Element("div", {"styles": this.css.inputUploadAreaTitleNode, "text": MWF.LP.widget.uploadTitle}).inject(formNode);
            var inforNode = new Element("div", {"styles": this.css.inputUploadAreaInforNode, "text": MWF.LP.widget.uploadInfor}).inject(formNode);
            var inputAreaNode = new Element("div", {"styles": this.css.inputUploadAreaInputAreaNode}).inject(formNode);
            var inputNode = new Element("input", {"name":"file", "type": "file", "styles": this.css.inputUploadAreaInputNode}).inject(inputAreaNode);
            var inputNameNode = new Element("input", {"type": "hidden", "name": "fileName"}).inject(inputAreaNode);

            Object.each(obj, function(v, k){
                new Element("input", {"type": "hidden", "name": k, "value": v}).inject(inputAreaNode);
            });

            var actionNode = new Element("div", {"styles": this.css.inputUploadActionNode}).inject(formNode);
            var cancelButton = new Element("button", {"styles": this.css.inputUploadCancelButton, "text": MWF.LP.widget.cancel}).inject(actionNode);
            var okButton = new Element("input", {"type": "button", "styles": this.css.inputUploadOkButton, "value": MWF.LP.widget.ok}).inject(actionNode);

            inputNode.addEvent("change", function(){
                var fileName = inputNode.get("value");
                if (fileName){
                    var tmpv = fileName.replace(/\\/g, "/");
                    var i = tmpv.lastIndexOf("/");
                    var fname = (i===-1) ? tmpv : tmpv.substr(i+1, tmpv.length-i);
                    inputNameNode.set("value", fname);
                }
            }.bind(this));

            cancelButton.addEvent("click", function(){
                if (maskNode) maskNode.unmask();
                this.inputUploadAreaNode.destroy();
            }.bind(this));

            okButton.addEvent("click", function(){
                formNode.mask({
                    "style": {
                        "opacity": 0.7,
                        "background-color": "#999"
                    }
                });

                var isContinue = true;
                if (beforeUpload) isContinue = beforeUpload([inputNameNode.get("value")]);
                if (isContinue){
                    messageItem = this.addUploadMessage(inputNameNode.get("value"));
                    formNode.submit();
                    if (maskNode) maskNode.unmask();
                    if (this.inputUploadAreaNode) this.inputUploadAreaNode.destroy();
                }
            }.bind(this));
        }.bind(this));
    },
    doFormDataUploadAttachment: function(obj, action, invokeUrl, parameter, finish, every, beforeUpload, multiple, accept){
        if (!this.uploadFileAreaNode){
            this.uploadFileAreaNode = new Element("div");
            var html = "<input name=\"file\" multiple type=\"file\" accept=\"*/*\"/>";
            this.uploadFileAreaNode.set("html", html);

            this.fileUploadNode = this.uploadFileAreaNode.getFirst();
            this.fileUploadNode.addEvent("change", function(){
                var files = this.fileUploadNode.files;
                if (files.length){
                    var count = files.length;
                    var current = 0;

                    var restActions = action;
                    if (typeOf(action)=="string"){
                        restActions = MWF.Actions.get(action).action;
                    }
                    //var url = restActions.action.actions[invokeUri];

                    var callback = function(){
                        if (current == count){
                            if(finish) finish();
                        }
                    };

                    var isContinue = true;
                    if (beforeUpload) isContinue = beforeUpload(files);

                    if (isContinue){
                        for (var i = 0; i < files.length; i++) {
                            var file = files.item(i);
                            var formData = new FormData();
                            Object.each(obj, function(v, k){
                                formData.append(k, v)
                            });
                            formData.append('file', file);
                            restActions.invoke({
                                "name": invokeUrl,
                                "async": true,
                                "data": formData,
                                "file": file,
                                "parameter": parameter,
                                "success": function(json){
                                    current++;
                                    if (every) every(json, current, count);
                                    callback();
                                }
                            });
                        }
                    }
                    this.uploadFileAreaNode.destroy();
                    this.uploadFileAreaNode = null;
                }
            }.bind(this));
        }
        this.fileUploadNode.set("accept", accept || "*/*");
        this.fileUploadNode.set("multiple", (multiple!==false));
        this.fileUploadNode.click();
    },


    openInOfficeControl: function(e, node){},
    deleteAttachment: function(e, node){
        if (this.selectedAttachments.length){
            if (this.module) this.module.deleteAttachments(e, node, this.selectedAttachments);
        }
    },
    replaceAttachment: function(e, node){
        if (this.selectedAttachments.length && this.selectedAttachments.length==1){
            if (this.module) this.module.replaceAttachment(e, node, this.selectedAttachments[0]);
        }
    },
    doReplaceAttachment: function(obj, action, invokeUrl, parameter, callback, multiple, accept){
        if (FormData.expiredIE){
            this.doInputUploadAttachment(obj, action, invokeUrl, parameter, callback, multiple, accept);
        }else{
            this.doFormDataUploadAttachment(obj, action, invokeUrl, parameter, callback, multiple, accept);
        }
    },



    downloadAttachment: function(e, node){
        if (this.selectedAttachments.length){
            if (this.module) this.module.downloadAttachment(e, node, this.selectedAttachments);
        }
    },

    openAttachment: function(e, node, attachment){
        if (attachment){
            if (this.module) this.module.openAttachment(e, node, attachment);
        }
    },

    downloadAllAttachment: function(e, node){
        if (this.module) this.module.downloadAttachment(e, node, this.attachments);
    },
    changeListStyle: function(style){
        this.options.listStyle = style;
        this.attachments.each(function(attachment){
            attachment.changeListStyle(style);
        }.bind(this));
        this.checkListStyleAction();
    },
    changeControllerSize: function(e, node){
        if (this.options.size=="max"){
            this.changeControllerSizeToMin();
        }else{
            this.changeControllerSizeToMax();
        }
    },

    changeControllerSizeToMin: function(){
        if (this.options.size!="min"){
            if (this.contentScrollNode) this.contentScrollNode.setStyle("display", "none");
            if (this.bottomNode) this.bottomNode.setStyle("display", "none");
            if (this.topNode) this.topNode.setStyle("display", "none");
            if (this.titleNode) this.titleNode.setStyle("display", "none");

            if (!this.nodeMorph) this.nodeMorph = new Fx.Morph(this.node, {"duration": 100});

            this.nodeMorph.start(this.css.container_min).chain(function(){
                this.options.size = "min";
                this.loadMin();
            }.bind(this));
        }
    },
    changeControllerSizeToMax: function(){
        if (this.options.size!="max") {
            if (this.minActionAreaNode) this.minActionAreaNode.setStyle("display", "none");
            if (this.minContent) this.minContent.setStyle("display", "none");

            if (!this.nodeMorph) this.nodeMorph = new Fx.Morph(this.node, {"duration": 100});

            this.nodeMorph.start(this.css.container).chain(function () {
                this.options.size = "max";
                this.loadMax();
            }.bind(this));
        }
    },

    getAttachmentNames: function(){
        var names = [];
        this.attachments.each(function(attachment){
            names.push(attachment.data.name);
        });
        return names;
    },

    addAttachment: function(data){
        if (this.options.size=="min"){
            this.attachments.push(new MWF.widget.AttachmentController.AttachmentMin(data, this));
        }else{
            this.attachments.push(new MWF.widget.AttachmentController.Attachment(data, this));
        }
    },
    removeAttachment: function(attachment){
        this.attachments.erase(attachment);
        this.selectedAttachments.erase(attachment);
        attachment.node.destroy();
        delete attachment;
    },

    unSelectedAttachments: function(){
        while (this.selectedAttachments.length){
            var attachment = this.selectedAttachments.shift();
            attachment.unSelected();
        }
        this.checkActions();
    },
    clear: function(){
        this.selectedAttachments = [];
        this.attachments.each(function(att){
            att.node.destroy();
            MWF.release(att);
        });
        this.attachments = [];
        this.content.empty();
    }

});

MWF.widget.AttachmentController.Attachment = new Class({
	Implements: [Events],
	initialize: function(data, controller){
		this.data = data;
        this.controller = controller;
        this.css = this.controller.css;
        this.listStyle = this.controller.options.listStyle;
        this.content = this.controller.content;
        this.isSelected = false;
        this.load();
	},
    load: function(){
        this.node = new Element("div").inject(this.content);
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
        }
        this.createInforNode(function(){
            if (!Browser.Platform.ios){
                this.tooltip = new mBox.Tooltip({
                    content: this.inforNode,
                    setStyles: {content: {padding: 15, lineHeight: 20}},
                    attach: this.node,
                    transition: 'flyin'
                });
            }
        }.bind(this));

        this.setEvent();
    },
    createInforNode: function(callback){
        var size = "";
        var k = this.data.length/1204;
        if (k>1024){
            var m = k/1024;
            m = Math.round(m*100)/100;
            size = m+"M";
        }else{
            k = Math.round(k*100)/100;
            size = k+"K";
        }
        this.inforNode = new Element("div", {"styles": this.css.attachmentInforNode});
        var html = "<div style='overflow:hidden; font-weight: bold'>"+this.data.name+"</div>";
        html += "<div style='clear: both; overflow:hidden'><div style='width:40px; float:left; font-weight: bold'>"+MWF.LP.widget.uploader+": </div><div style='width:120px; float:left; margin-left:10px'>"+this.data.person+"</div></div>";
        html += "<div style='clear: both; overflow:hidden'><div style='width:40px; float:left; font-weight: bold'>"+MWF.LP.widget.uploadTime+": </div><div style='width:120px; float:left; margin-left:10px'>"+this.data.createTime+"</div></div>";
        html += "<div style='clear: both; overflow:hidden'><div style='width:40px; float:left; font-weight: bold'>"+MWF.LP.widget.modifyTime+": </div><div style='width:120px; float:left; margin-left:10px'>"+this.data.lastUpdateTime+"</div></div>";
        html += "<div style='clear: both; overflow:hidden'><div style='width:40px; float:left; font-weight: bold'>"+MWF.LP.widget.uploadActivity+": </div><div style='width:120px; float:left; margin-left:10px'>"+(this.data.activityName || MWF.LP.widget.unknow)+"</div></div>";
        html += "<div style='clear: both; overflow:hidden'><div style='width:40px; float:left; font-weight: bold'>"+MWF.LP.widget.size+": </div><div style='width:120px; float:left; margin-left:10px'>"+size+"</div></div>";
        this.inforNode.set("html", html);

        if (callback) callback();
    },
    getIcon: function(){
        if (!this.data.extension) this.data.extension="unkonw";
        var iconName = this.controller.icons[this.data.extension.toLowerCase()] || this.controller.icons.unknow;
        return "/x_component_File/$Main/default/file/"+iconName;
    },

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
        var k = this.data.length/1204;
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
        this.textUploaderNode.set("text", this.data.person);

        this.textTimeNode = new Element("div", {"styles": this.css.attachmentTextTimeNode_list}).inject(this.textNode);
        this.textTimeNode.set("text", this.data.lastUpdateTime);

        this.textActivityNode = new Element("div", {"styles": this.css.attachmentTextActivityNode_list}).inject(this.textNode);
        this.textActivityNode.set("text", this.data.activityName || MWF.LP.widget.unknow);

        this.custom_List();
    },
    custom_List: function(){},

    loadIcon: function(){
        this.node.setStyles(this.css.attachmentNode_icon);
        if (this.isSelected) this.node.setStyles(this.css.attachmentNode_icon_selected);

        this.iconNode = new Element("div", {"styles": this.css.attachmentIconNode}).inject(this.node);
        this.iconImgAreaNode = new Element("div", {"styles": this.css.attachmentIconImgAreaNode}).inject(this.iconNode);
        this.iconImgNode = new Element("img", {"styles": this.css.attachmentIconImgNode}).inject(this.iconImgAreaNode);
        this.iconImgNode.set({"src": this.getIcon(), "border": 0});

        this.textNode = new Element("div", {"styles": this.css.attachmentTextNode}).inject(this.node);
        this.textNode.set("text", this.data.name);
        this.custom_Icon();
    },
    custom_Icon: function(){},

    loadPreview: function(){
        this.node.setStyles(this.css.attachmentNode_preview);
        if (this.isSelected) this.node.setStyles(this.css.attachmentNode_preview_selected);

        this.iconNode = new Element("div", {"styles": this.css.attachmentPreviewIconNode}).inject(this.node);
        this.iconImgAreaNode = new Element("div", {"styles": this.css.attachmentPreviewIconImgAreaNode}).inject(this.iconNode);
        this.iconImgNode = new Element("img", {"styles": this.css.attachmentPreviewIconImgNode}).inject(this.iconImgAreaNode);

        var icon = this.getIcon();
        if (this.controller.options.images.indexOf(this.data.extension.toLowerCase())!==-1){
            this.controller.module.getAttachmentUrl(this, function(url){
                icon = url;
                this.getPreviewImgSize(icon, function(size){
                    this.iconImgNode.set({"src": icon, "border": 0});

                    this.iconImgAreaNode.setStyles({
                        "width": ""+size.x+"px",
                        "height": ""+size.y+"px"
                    });
                    this.iconImgNode.setStyles({
                        "width": ""+size.x+"px",
                        "height": ""+size.y+"px",
                        "margin-top": ""+size.top+"px"
                    });

                    window.setTimeout(function(){
                        this.getPreviewImgSize(icon, function(size){
                            if (this.iconImgAreaNode) this.iconImgAreaNode.setStyles({
                                "width": ""+size.x+"px",
                                "height": ""+size.y+"px"
                            });
                            if (this.iconImgNode) this.iconImgNode.setStyles({
                                "width": ""+size.x+"px",
                                "height": ""+size.y+"px",
                                "margin-top": ""+size.top+"px"
                            });
                        }.bind(this))
                    }.bind(this), 100);

                }.bind(this));
            }.bind(this));
            this.textNode = new Element("div", {"styles": this.css.attachmentPreviewTextNode}).inject(this.node);
            this.textNode.set("text", this.data.name);
        }else if (this.controller.options.audios.indexOf(this.data.extension.toLowerCase())!==-1){
            this.textNode = new Element("div", {"styles": this.css.attachmentPreviewTextNode}).inject(this.node);
            this.textNode.set("text", this.data.name);

            this.controller.module.getAttachmentUrl(this, function(url){
                this.iconImgNode.set({"src": icon, "border": 0});
                this.iconAudioNode = new Element("div", {
                    "styles": this.css.attachmentPreviewAudioNode,
                    "html": "<audio preload=\"metadata\" controls=\"controls\" style=\"width: 100%; height: 100%\"><source src=\""+url+"\" type=\"audio/mpeg\"></source></audio>"
                    //"html": "<audio controls=\"controls\" style=\"width: 100%; height: 100%\"><source src=\""+MWF.defaultPath+"/xApplication/File/$Main/qnyh.mp3\"></source></audio>"
                }).inject(this.textNode, "after");
                this.iconAudioNode.addEvent("dblclick", function(e){e.stopPropagation();});
            }.bind(this));
        }else if (this.controller.options.videos.indexOf(this.data.extension.toLowerCase())!==-1){
            this.controller.module.getAttachmentUrl(this, function(url){
                this.iconNode.empty();
                this.iconVideoNode = new Element("div", {
                    "styles": this.css.attachmentPreviewVideoNode,
                    //"html": "<video preload=\"metadata\" controls=\"controls\" style=\"width: 100%; height: 100%\"><source src=\""+MWF.defaultPath+"/xApplication/File/$Main/IMG_1615.MOV\"></source></video>"
                    "html": "<video preload=\"metadata\" controls=\"controls\" style=\"width: 100%; height: 100%\"><source src=\""+url+"\"></source></video>"
                }).inject(this.iconNode);
                this.iconVideoNode.addEvent("dblclick", function(e){e.stopPropagation();});

                this.textNode = new Element("div", {"styles": this.css.attachmentPreviewTextNode}).inject(this.node);
                this.textNode.set("text", this.data.name);
            }.bind(this));
        }else{
            this.iconImgNode.set({"src": icon, "border": 0});
            this.textNode = new Element("div", {"styles": this.css.attachmentPreviewTextNode}).inject(this.node);
            this.textNode.set("text", this.data.name);
        }
        this.custom_Preview();
    },
    custom_Preview: function(){},

    getPreviewImgSize: function(icon, callback){
        var areaSize = this.iconNode.getSize();
        var img = new Element("img", {"src": icon}).inject($(document.body));
        img.set("src", icon);
        var size = img.getSize();
        img.destroy();
        var x, y;
        var zoom = areaSize.x/size.x;
        var y = size.y*zoom;
        if (y<=areaSize.y){
            x = areaSize.x;
        }else{
            zoom = areaSize.y/size.y;
            x = size.x*zoom;
            y = areaSize.y;
        }
        var size = {"x": x, "y": y, "top": (areaSize.y-y)/2};
        if (callback) callback(size);
    },

    setEvent: function(){
        this.node.addEvents({
            "mouseover": function(){if (!this.isSelected) this.node.setStyles(this.css["attachmentNode_"+this.controller.options.listStyle+"_over"])}.bind(this),
            "mouseout": function(){if (!this.isSelected) this.node.setStyles(this.css["attachmentNode_"+this.controller.options.listStyle])}.bind(this),
            "mousedown": function(e){this.selected(e);}.bind(this),
            "dblclick": function(e){this.openAttachment(e);}.bind(this)
        });
    },

    selected: function(e){
        if (!e.event.ctrlKey) this.controller.unSelectedAttachments();
        //if (!this.isSelected){
        this.controller.selectedAttachments.push(this);
        this.isSelected = true;
        this.node.setStyles(this.css["attachmentNode_"+this.controller.options.listStyle+"_selected"]);
        //}
        if (e) e.stopPropagation();
        this.controller.checkActions();
    },

    unSelected: function(){
        this.isSelected = false;
        this.node.setStyles(this.css["attachmentNode_"+this.controller.options.listStyle]);
        this.controller.selectedAttachments.erase(this);
    },

    changeListStyle: function(style){
        if (this.listStyle!=style){
            this.node.empty();
            switch (style){
                case "list":
                    this.loadList();
                    break;
                case "icon":
                    this.loadIcon();
                    break;
                case "preview":
                    this.loadPreview();
                    break;
            }
            this.listStyle = style;
        }
    },
    reload: function(){
        var node = new Element("div").inject(this.node, "after");

        this.inforNode.destroy();
        this.inforNode = null;
        if (this.tooltip) this.tooltip.destroy();
        this.tooltip = null;

        this.node.destroy();
        delete this.node;
        this.node = node;

        switch (this.listStyle){
            case "list":
                this.loadList();
                break;
            case "icon":
                this.loadIcon();
                break;
            case "preview":
                this.loadPreview();
                break;
        }

        this.createInforNode();
        if (!Browser.Platform.ios){
            this.tooltip = new mBox.Tooltip({
                content: this.inforNode,
                setStyles: {content: {padding: 15, lineHeight: 20}},
                attach: this.node,
                transition: 'flyin'
            });
        }


        this.setEvent();
    },
    openAttachment: function(e){
        if (this.controller.module) this.controller.module.openAttachment(e, null, [this]);
    }
});

MWF.widget.AttachmentController.AttachmentMin = new Class({
    Extends: MWF.widget.AttachmentController.Attachment,

    initialize: function(data, controller){
        this.data = data;
        this.controller = controller;
        this.css = this.controller.css;
        this.content = this.controller.minContent;
        this.isSelected = false;
        this.load();
    },
    load: function(){
        this.node = new Element("div").inject(this.content);
        this.loadList();

        this.createInforNode();
        if (!Browser.Platform.ios){
            this.tooltip = new mBox.Tooltip({
                content: this.inforNode,
                setStyles: {content: {padding: 15, lineHeight: 20}},
                attach: this.node,
                transition: 'flyin'
            });
        }
        this.setEvent();
    },
    loadList: function(){
        this.node.setStyles(this.css.minAttachmentNode_list);
        if (this.isSelected) this.node.setStyles(this.css.minAttachmentNode_list_selected);

        this.iconNode = new Element("div", {"styles": this.css.minAttachmentIconNode_list}).inject(this.node);
        this.iconImgAreaNode = new Element("div", {"styles": this.css.minAttachmentIconImgAreaNode_list}).inject(this.iconNode);
        this.iconImgNode = new Element("img", {"styles": this.css.minAttachmentIconImgNode_list}).inject(this.iconImgAreaNode);
        this.iconImgNode.set({"src": this.getIcon(), "border": 0});

        this.textNode = new Element("div", {"styles": this.css.minAttachmentTextNode_list}).inject(this.node);
        this.textNode.set("text", this.data.name);
    },
    setEvent: function(){
        this.node.addEvents({
            "mouseover": function(){if (!this.isSelected) this.node.setStyles(this.css["minAttachmentNode_list_over"])}.bind(this),
            "mouseout": function(){if (!this.isSelected) this.node.setStyles(this.css["minAttachmentNode_list"])}.bind(this),
            "mousedown": function(e){this.selected(e);}.bind(this),
            "dblclick": function(e){this.openAttachment(e);}.bind(this)
        });
    },
    selected: function(e){
        if (!e.event.ctrlKey) this.controller.unSelectedAttachments();
        //if (!this.isSelected){
        this.controller.selectedAttachments.push(this);
        this.isSelected = true;
        this.node.setStyles(this.css["minAttachmentNode_list_selected"]);
        //}
        if (e) e.stopPropagation();
        this.controller.checkActions();
    },
    reload: function(){
        var node = new Element("div").inject(this.node, "after");

        this.inforNode.destroy();
        this.inforNode = null;
        if (this.tooltip) this.tooltip.destroy();
        this.tooltip = null;

        this.node.destroy();
        delete this.node;
        this.node = node;

        this.loadList();

        this.createInforNode();
        if (!Browser.Platform.ios){
            this.tooltip = new mBox.Tooltip({
                content: this.inforNode,
                setStyles: {content: {padding: 15, lineHeight: 20}},
                attach: this.node,
                transition: 'flyin'
            });
        }

        this.setEvent();
    },
    unSelected: function(){
        this.isSelected = false;
        this.node.setStyles(this.css["minAttachmentNode_list"]);
        this.controller.selectedAttachments.erase(this);
    }

});