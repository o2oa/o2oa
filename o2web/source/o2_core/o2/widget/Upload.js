o2.widget = o2.widget || {};
o2.widget.Upload = new Class({
    Extends: o2.widget.Common,
	Implements: [Options, Events],
	options: {
		"data": null,
        "parameter": null,
        "action": null,
        "method": "",
        "style": "default",
        "multiple": true
	},

	initialize: function(container, options){
		this.setOptions(options);
		this.path = o2.session.path+"/widget/$Upload/";
		this.cssPath = o2.session.path+"/widget/$Upload/"+this.options.style+"/css.wcss";
		this._loadCss();
		this.container = $(container);

		this.action = (typeOf(this.options.action)=="string") ? o2.Actions.get(this.options.action).action : this.options.action;
	},
    load: function(){
        if (FormData.expiredIE){
            this.doUpload_InputFile();
        }else{
            this.doUpload_FormData();
        }
    },
    upload:function(){
        if (FormData.expiredIE){
            this.doUpload_InputFile();
        }else{
            if (!this.fileUploadNode) this.formData_CreateUploadArea();
            this.fileUploadNode.click();
        }
    } ,
    doUpload_FormData: function(){
        this.formData_CreateUploadArea();
        this.fileUploadNode.click();
    },
    formData_CreateUploadArea: function(){
        if (!this.uploadFileAreaNode){
            this.uploadFileAreaNode = new Element("div");
            var html = "<input name=\"file\" "+((this.options.multiple) ? "multiple": "")+" type=\"file\" accept=\"*/*\"/>";
            this.uploadFileAreaNode.set("html", html);
            this.fileUploadNode = this.uploadFileAreaNode.getFirst();

            this.fileUploadNode.addEvent("change", this.formData_Upload.bind(this));
        }
    },
    formData_Upload: function(){
        var files = this.fileUploadNode.files;
        if (files.length){
            var count = files.length;
            var current = 0;

            this.isContinue = true;
            this.fireEvent("beforeUpload", [files, this]);

            var uploadBack = function(json){
                if (current == count) this.fireEvent("completed", [json]);
            }.bind(this);

            if (this.isContinue){
                for (var i = 0; i < files.length; i++) {
                    var file = files.item(i);
                    this.fireEvent("beforeUploadEntry", [file, this]);

                    var formData = new FormData();
                    Object.each(this.options.data, function(v, k){
                        formData.append(k, v)
                    });
                    formData.append('file', file);
                    this.action.invoke({
                        "name": this.options.method,
                        "async": true,
                        "data": formData,
                        "file": file,
                        "parameter": this.options.parameter,
                        "success": function(json){
                            current++;
                            this.fireEvent("every", [json, current, count, file]);
                            uploadBack(json);
                        }.bind(this)
                    });
                }
            }
            this.uploadFileAreaNode.destroy();
            this.uploadFileAreaNode = null;
        }
    },

    inputFile_CreateMaskNode: function(){
        this.container.mask({
            "style": {
                "opacity": 0.7,
                "background-color": "#999"
            }
        });
    },

    addUploadMessage: function(fileName){
        var contentHTML = "";
        contentHTML = "<div style=\"overflow: hidden\"><div style=\"height: 2px; border:0px solid #999; margin: 3px 0px\">" +
            "<div style=\"height: 2px; background-color: #acdab9; width: 0px;\"></div></div>" +
            "<div style=\"height: 20px; line-height: 20px\">"+o2.LP.desktop.action.uploadTitle+"</div></div>" +
            "<iframe name='o2_upload_iframe' style='display:none'></iframe>" ;
        var msg = {
            "subject": o2.LP.desktop.action.uploadTitle,
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

    inputFile_CreateInputNode: function(url){
        this.inputUploadAreaNode = new Element("div", {"styles": this.css.inputUploadAreaNode}).inject(this.container);
        this.inputUploadAreaNode.position({
            relativeTo: this.container,
            position: "center"
        });

        var formNode = new Element("form", {
            "method": "POST",
            //"action": url+"/callback/window.frameElement.ownerDocument.defaultView.o2.O2UploadCallbackFun",
            "action": url+"/callback/window.frameElement.ownerDocument.O2UploadCallbackFun",
            //"action": url,
            "enctype": "multipart/form-data",
            "target": "o2_upload_iframe"
        }).inject(this.inputUploadAreaNode);

        var titleNode = new Element("div", {"styles": this.css.inputUploadAreaTitleNode, "text": o2.LP.widget.uploadTitle}).inject(formNode);
        var inforNode = new Element("div", {"styles": this.css.inputUploadAreaInforNode, "text": o2.LP.widget.uploadInfor}).inject(formNode);
        var inputAreaNode = new Element("div", {"styles": this.css.inputUploadAreaInputAreaNode}).inject(formNode);
        var inputNode = new Element("input", {"name":"file", "type": "file", "styles": this.css.inputUploadAreaInputNode}).inject(inputAreaNode);
        var inputNameNode = new Element("input", {"type": "hidden", "name": "fileName"}).inject(inputAreaNode);

        Object.each(this.options.data, function(v, k){
            new Element("input", {"type": "hidden", "name": k, "value": v}).inject(inputAreaNode);
        });

        var actionNode = new Element("div", {"styles": this.css.inputUploadActionNode}).inject(formNode);
        var cancelButton = new Element("button", {"styles": this.css.inputUploadCancelButton, "text": o2.LP.widget.cancel}).inject(actionNode);
        var okButton = new Element("input", {"type": "button", "styles": this.css.inputUploadOkButton, "value": o2.LP.widget.ok}).inject(actionNode);

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
            this.container.unmask();
            this.inputUploadAreaNode.destroy();
        }.bind(this));

        okButton.addEvent("click", function(){
            formNode.mask({
                "style": {
                    "opacity": 0.7,
                    "background-color": "#999"
                }
            });

            this.isContinue = true;
            this.fireEvent("beforeUpload", [inputNameNode.get("value")]);

            if (this.isContinue){
                this.messageItem = this.addUploadMessage(inputNameNode.get("value"));
                formNode.submit();
                this.container.unmask();
                if (this.inputUploadAreaNode) this.inputUploadAreaNode.destroy();
            }
        }.bind(this));
    },

    inputFile_UploadCallback: function(str){
        var json = JSON.decode(str);
        this.messageItem.completed = true;
        if (json.type==="success"){
            //if (every) every(json.data);
            this.setMessageTitle(this.messageItem, o2.LP.desktop.action.uploadComplete);
            this.setMessageText(this.messageItem, o2.LP.desktop.action.uploadComplete);
            this.fireEvent("every", [json]);
            this.fireEvent("completed", [json]);
        }else{
            this.setMessageTitle(this.messageItem, o2.LP.desktop.action.sendError);
            this.setMessageText(this.messageItem, o2.LP.desktop.action.sendError+": "+json.message);
            o2.xDesktop.notice("error", {x: "right", y:"top"}, json.message);
        }
    },

    doUpload_InputFile: function(){
        this.action.getActions(function(){
            var url = this.action.actions[this.options.method];
            url = this.action.address+url.uri;
            Object.each(this.options.parameter, function(v, k){
                url = url.replace("{"+k+"}", v);
            });

            this.messageItem = null;
            //o2.O2UploadCallbackFun = this.inputFile_UploadCallback.bind(this);
            document.O2UploadCallbackFun = this.inputFile_UploadCallback.bind(this);
            this.inputFile_CreateInputNode(url);
        }.bind(this));
    }
});