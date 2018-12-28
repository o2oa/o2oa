o2.widget = o2.widget || {};
o2.widget.AttachmentSelector = o2.widget.ATTSER  = new Class({
    Extends: o2.widget.AttachmentController,
	Implements: [Options, Events],
	options: {
		"style": "default",
        "listStyle": "icon",
        "selectType" : "all", //images audios videos
        "size": "max",
        "resize": false,
        "attachmentCount": 0,
        "isUpload": true,
        "isDelete": true,
        "isReplace": true,
        "isDownload": true,
        "isSizeChange": false,
        "readonly": false,
        "images": ["bmp", "gif", "png", "jpeg", "jpg", "jpe", "ico"],
        "audios": ["mp3", "wav", "wma", "wmv"],
        "videos": ["avi", "mkv", "mov", "ogg", "mp4", "mpa", "mpe", "mpeg", "mpg", "rmvb"]
	},
	initialize: function(container, module, options){
		this.setOptions(options);
		this.pages = [];

		this.path = o2.session.path+"/widget/$AttachmentSelector/";
		this.cssPath = o2.session.path+"/widget/$AttachmentSelector/"+this.options.style+"/css.wcss";
		this._loadCss();

        //var r = new Request.JSON({
        //    url: this.path +this.options.style+"/css.wcss" ,
        //    secure: false,
        //    async: false,
        //    method: "get",
        //    noCache: false,
        //    onSuccess: function(responseJSON, responseText){
        //        this.css = Object.merge( this.css, responseJSON );
        //    }.bind(this),
        //    onError: function(text, error){
        //        alert(error + text);
        //    }
        //});
        //r.send();

        o2.getJSON("/x_component_File/$Main/icon.json", function(json){
            this.icons = json;
        }.bind(this), false, false);

        this.module = module;

        this.actions = [];
        this.attachments = [];
        this.selectedAttachments = [];
		this.container = $(container);
	},
    load: function(){
        this.markNode = new Element("div", {
            "styles": this.css.markNode,
            "events": {
                "mouseover": function(e){e.stopPropagation();},
                "mouseout": function(e){e.stopPropagation();}
            }
        }).inject(this.container);

        this.node = new Element("div", {
            "styles": this.css.container
        });

        this.fiterByExtension( this.attachments );
        if (this.options.size=="min"){
            this.loadMin();
        }else{
            this.loadMax();
        }

        this.node.inject(this.markNode, "after");
        this.node.fade("in");

        this.setNodeSize();

        var size = this.container.getSize();
        var nodeSize = this.node.getSize();
        this.node.makeDraggable({
            "handle": this.titleNode,
            "limit": {
                "x": [0, size.x-nodeSize.x],
                "y": [0, size.y-nodeSize.y]
            }
        });
    },
    close : function(){
        this.node.destroy();
        this.markNode.destroy();
        delete this;
    },
    fiterByExtension : function( attachments ){
        var availableExtensions = this.options[ this.options.selectType ];
        if( availableExtensions ){
            var atts = [];
            while (attachments.length){
                var att = attachments.shift();
                if( availableExtensions.contains(att.extension) ){
                    atts.push(att);
                }

            }
            attachments = atts;
        }
        return attachments;
    },
	loadMax: function(){
        if (!this.node) this.node = new Element("div", {"styles": this.css.container});

        if (!this.topNode){
            this.createTopNode();
            this.createContentNode();
            this.createBottomToolbarNode();


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
            atts.push(new o2.widget.AttachmentSelector.Attachment(att.data, this));
        }
        this.attachments = atts;
	},
    loadMin: function(){
        if (!this.node) this.node = new Element("div", {"styles": this.css.container_min});

        if (!this.minActionAreaNode){
            this.minActionAreaNode = new Element("div", {"styles": this.css.minActionAreaNode}).inject(this.node);
            this.minContent = new Element("div", {"styles": this.css.minContentNode}).inject(this.node);

            this.min_uploadAction = this.createAction(this.minActionAreaNode, "upload", o2.LP.widget.upload, function(e, node){
                this.uploadAttachment(e, node);
            }.bind(this));

            this.min_deleteAction = this.createAction(this.minActionAreaNode, "delete", o2.LP.widget["delete"], function(e, node){
                this.deleteAttachment(e, node);
            }.bind(this));

            this.min_replaceAction = this.createAction(this.minActionAreaNode, "replace", o2.LP.widget.replace, function(e, node){
                this.replaceAttachment(e, node);
            }.bind(this));

            this.createSeparate(this.minActionAreaNode);

            this.sizeAction = this.createAction(this.minActionAreaNode, "max", o2.LP.widget.min, function(){
                this.changeControllerSize();
            }.bind(this));

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
            atts.push(new o2.widget.AttachmentSelector.AttachmentMin(att.data, this));
        }
        this.attachments = atts;
    },
    createTopNode: function(){
        if (this.options.title){
            if (!this.titleNode){
                this.titleNode = new Element("div", {"styles": this.css.titleNode, "text": this.options.title}).inject(this.node);

                this.titleActionNode = new Element("div", {"styles": this.css.titleActionNode}).inject(this.titleNode);
                this.titleActionNode.addEvent("click",function(){this.close() }.bind(this))
            }
        }
        this.topNode = new Element("div", {"styles": this.css.topNode}).inject(this.node);
        this.createEditGroupActions();
        this.createReadGroupActions();
        this.createListGroupActions();
        this.createViewGroupActions();
    },
    addAttachment: function(data){
        var arr = this.fiterByExtension([data]);
        if( arr.length > 0 ){
            if (this.options.size=="min"){
                this.attachments.push(new o2.widget.AttachmentSelector.AttachmentMin(data, this));
            }else{
                this.attachments.push(new o2.widget.AttachmentSelector.Attachment(data, this));
            }
        }
    },
    selectAttachment: function(e, node, attachments ){
        if (attachments){
            if (this.module) this.module.selectAttachment(e, node, attachments);
        }
        this.close();
    },
    createBottomToolbarNode : function(  ){
        this.bottomToolbarNode = new Element("div",{ styles : this.css.bottomToolbarNode }).inject(this.node);

        this.cancelButton = new Element("div",{
            styles : this.css.cancelButton,
            text : o2.LP.widget.cancel
        }).inject(this.bottomToolbarNode);
        this.cancelButton.addEvent("click",function(){
            this.close();
        }.bind(this));

        this.okButton = new Element("div",{
            styles : this.css.okButton,
            text : o2.LP.widget.ok
        }).inject(this.bottomToolbarNode);
        this.okButton.addEvent("click",function(){
            if( this.selectedAttachments.length ){
                this.selectAttachment(null, null, this.selectedAttachments );
            }
            this.close();
        }.bind(this));
    },
    setNodeSize: function (width, height) {
        width = width || "50%";
        height = height || "50%";
        "string" == typeof width && (1 < width.length && "%" == width.substr(width.length - 1, 1)) && (width = parseInt(window.screen.width * parseInt(width, 10) / 100, 10));
        "string" == typeof height && (1 < height.length && "%" == height.substr(height.length - 1, 1)) && (height = parseInt(window.screen.height * parseInt(height, 10) / 100, 10));
        700 > width && (width = 700);
        420 > height && (height = 420);
        var top = parseInt((window.screen.height - height) / 2, 10);
        var left = parseInt((window.screen.width - width) / 2, 10);
        //c = window.open("", null, d, !0);
        this.node.setStyles({
            "width": "" + width + "px",
            "height": "" + height + "px",
            "top": "" + top + "px",
            "left": "" + left + "px"
        });
        var titleNodeHeight = this.titleNode ? this.titleNode.getSize().y : 0;
        var topNodeHeight = this.topNode ? this.topNode.getSize().y : 0;
        var bottomToolbarNodeHeight = this.bottomToolbarNode ? this.bottomToolbarNode.getSize().y : 0;
        var bottomNodeHeight = this.bottomNode ? this.bottomNode.getSize().y : 0;
        var h = height - titleNodeHeight - topNodeHeight - bottomToolbarNodeHeight - bottomNodeHeight;
        this.contentScrollNode.setStyles({
            "height" : "" + h + "px"
        })
    }

});

o2.widget.AttachmentSelector.Attachment = new Class({
    Extends: o2.widget.AttachmentController.Attachment,
    Implements: [Options, Events],
    load: function(){
        this.node = new Element("div").inject(this.content);
        switch (this.controller.options.listStyle){
            case "list":
                this.loadList();
                break;
            case "icon":
                this.loadIcon();
                break
            case "preview":
                this.loadPreview();
                break;
        }
        this.createInforNode(function(){
            this.tooltip = new mBox.Tooltip({
                content: this.inforNode,
                setStyles: {content: {padding: 15, lineHeight: 20}},
                zIndex: 10013,
                attach: this.node,
                transition: 'flyin'
            });
        }.bind(this));

        this.setEvent();
    },
    setEvent: function(){
        this.node.addEvents({
            "mouseover": function(){if (!this.isSelected) this.node.setStyles(this.css["attachmentNode_"+this.controller.options.listStyle+"_over"])}.bind(this),
            "mouseout": function(){if (!this.isSelected) this.node.setStyles(this.css["attachmentNode_"+this.controller.options.listStyle])}.bind(this),
            "mousedown": function(e){this.selected(e);}.bind(this),
            "dblclick": function(e){this.selectAttachment(e);}.bind(this)
        });
    },
    selectAttachment : function(e){
        this.controller.selectAttachment(e, null, [this]);
    }

});

o2.widget.AttachmentSelector.AttachmentMin = new Class({
    Extends: o2.widget.AttachmentController.AttachmentMin,

    setEvent: function(){
        this.node.addEvents({
            "mouseover": function(){if (!this.isSelected) this.node.setStyles(this.css["minAttachmentNode_list_over"])}.bind(this),
            "mouseout": function(){if (!this.isSelected) this.node.setStyles(this.css["minAttachmentNode_list"])}.bind(this),
            "mousedown": function(e){this.selected(e);}.bind(this),
            "dblclick": function(e){this.selectAttachment(e);}.bind(this)
        });
    },
    selectAttachment : function(e){
        this.controller.selectAttachment(e, null, [this]);
    }

});