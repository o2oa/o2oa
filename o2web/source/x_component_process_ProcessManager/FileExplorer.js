MWF.xDesktop.requireApp("process.ProcessManager", "DictionaryExplorer", null, false);
MWF.xApplication.process.ProcessManager.FileExplorer = new Class({
	Extends: MWF.xApplication.process.ProcessManager.DictionaryExplorer,
	Implements: [Options, Events],
    options: {
        "create": MWF.APPPM.LP.file.create,
        "search": MWF.APPPM.LP.file.search,
        "searchText": MWF.APPPM.LP.file.searchText,
        "noElement": MWF.APPPM.LP.file.noDictionaryNoticeText
    },

    // createSearchElementNode: function(){
    //     this.titleActionNodeNode = new Element("div", {
    //         "styles": this.css.titleActionNode,
    //         "text": MWF.APPPM.LP.file.loadFiles
    //     }).inject(this.toolbarNode);
    //     this.titleActionNodeNode.addEvent("click", function(){
    //         this.implodeFiles();
    //     }.bind(this));
    // },
    getNewData: function(){
        return {
            "id": "",
            "name": "",
            "alias": "",
            "description": "",
            "application": (this.app.options.application || this.app.application).id,
            "fileName": ""
        }
    },
    getSizeText: function(s){
        var o = [
            {"t": "K", "i": 1024},
            {"t": "M", "i": 1024*1024},
            {"t": "G", "i": 1024*1024*1024}
        ];
        var i = 0;
        var n = s/o[i].i;
        while (n>1000 && i<2){
            i++;
            n = s/o[i].i;
        }
        n = Math.round(n*100)/100;
        return ""+n+" "+o[i].t;
    },
    implodeFiles: function(){
        if (this.upload){
            this.upload.upload();
        }else{
            MWF.require("MWF.widget.Upload", function(){
                var datas = [];
                this.upload = new MWF.widget.Upload(this.app.content, {
                    "action": MWF.Actions.get("x_processplatform_assemble_designer").action,
                    "multiple": true,
                    "method": "uploadFile",
                    "parameter": {"id": ""},
                    "onBeforeUploadEntry": function(file, up){
                        var data = this.getNewData();
                        data.name = file.name;
                        data.fileName = file.name;
                        data.description = file.name+" "+this.getSizeText(file.size);
                        data.updateTime = (new Date()).format("db");
                        MWF.Actions.get("x_processplatform_assemble_designer").saveFile(data, function(json){
                            up.options.parameter = {"id": json.data.id};

                            var node = this.elementContentListNode.getFirst();
                            if (node) if (node.hasClass("noElementNode")){
                                node.destroy();
                            }
                            datas.push(data);
                        }.bind(this), null, false);
                    }.bind(this),
                    "onEvery": function(json, current, count, file){
                        var data = datas[current-1];
                        var itemObj = this._getItemObject(data);
                        itemObj.load();
                    }.bind(this)
                }).load();
            }.bind(this));
        }
    },

    loadContentNode: function(){
        this.cssPath = this.path+this.options.style+"/file.css";

        this.elementContentNode = new Element("div", {
            "styles": this.css.elementContentNode
        }).inject(this.node);

        this.elementContentNode.addEvent("click", function(){
            while (this.selectMarkItems.length){
                this.selectMarkItems[0].unSelected();
            }
        }.bind(this));

        this.elementContentListNode = new Element("div", {
            "styles": this.css.elementContentListNode
        }).inject(this.elementContentNode);

        this.elementContentListNode.loadCss(this.cssPath);

        this.setContentSize();
        this.app.addEvent("resize", function(){this.setContentSize();}.bind(this));
    },

    _createElement: function(e){
        this.implodeFiles();
    },
    _loadItemDataList: function(callback){
        var id = "";
        if (this.app.application) id = this.app.application.id;
        if (this.app.options.application) id = this.app.options.application.id;
        this.actions.listFile(id,callback);
    },
    _getItemObject: function(item){
        return new MWF.xApplication.process.ProcessManager.FileExplorer.File(this, item)
    },
    setTooltip: function(){
        this.options.tooltip = {
            "create": MWF.APPPM.LP.file.create,
            "search": MWF.APPPM.LP.file.search,
            "searchText": MWF.APPPM.LP.file.searchText,
            "noElement": MWF.APPPM.LP.file.noScriptNoticeText
        };
    },
    deleteItems: function(){
        this.hideDeleteAction();
        while (this.deleteMarkItems.length){
            var item = this.deleteMarkItems.shift();
            if (this.deleteMarkItems.length){
                item.deleteFile();
            }else{
                item.deleteFile(function(){
                }.bind(this));
            }
        }
    },
    destroy: function(){
        this.node.destroy();
        o2.removeCss(this.cssPath);
        o2.release(this);
    },
    getIconJson: function(callback){
        if (!this.icons){
            MWF.getJSON("../x_component_File/$Main/icon.json", function(json){
                this.icons = json;
                if (callback) callback();
            }.bind(this), false, false);
        }else{
            if (callback) callback();
        }
    },
    getIcon: function(fileName){
        this.getIconJson();
        var ext = fileName.substring(fileName.lastIndexOf(".")+1, fileName.length);
        if (!ext) ext="unkonw";
        var iconName = this.icons[ext.toLowerCase()] || this.icons.unknow;
        return "../x_component_File/$Main/default/file/"+iconName;
    }
});

MWF.xApplication.process.ProcessManager.FileExplorer.File = new Class({
	Extends: MWF.xApplication.process.ProcessManager.DictionaryExplorer.Dictionary,

    load: function(){
        var view = this.explorer.path+this.explorer.options.style+"/file.html";
        this.node = new Element("div.o2_fileItemNode", {
            "events": {
                "mouseover": function(){
                    if (this.deleteActionNode) this.deleteActionNode.fade("in");
                    if (this.saveasActionNode) this.saveasActionNode.fade("in");
                }.bind(this),
                "mouseout": function(){
                    if (this.deleteActionNode) this.deleteActionNode.fade("out");
                    if (this.saveasActionNode) this.saveasActionNode.fade("out");
                }.bind(this)
            }
        }).inject(this.container);

        if (this.data.name.icon) this.icon = this.data.name.icon;
        var ext = this.data.fileName.substring(this.data.fileName.lastIndexOf(".")+1, this.data.fileName.length);
        this.data.fileUrl = this._getUrl();

        if (["png","jpg","bmp","gif","jpeg","jpe"].indexOf(ext)!==-1){
            //new Image()
            this.data.iconUrl = this.data.fileUrl;
        }else{
            this.data.iconUrl = this.explorer.getIcon(this.data.fileName);
        }

        //this.data.iconUrl = this.explorer.path+""+this.explorer.options.style+"/processIcon/"+this.icon;


        this.node.loadHtml(view, {"bind": this.data}, function(){
            debugger;
            var itemIconNode = this.node.getElement(".o2_fileItemIconNode");
            var itemImgNode = this.node.getElement(".o2_fileItemImgNode");
            var itemUrlNode = this.node.getElement(".o2_fileItemUrlNode");
            itemUrlNode.setStyle("-webkit-user-select", "text");
            var s = itemIconNode.getSize();
            // if (!s.x) s.x = itemIconNode.getStyle("width").toFloat();
            // if (!s.y) s.y = itemIconNode.getStyle("height").toFloat();
            itemImgNode.setStyles({
                "max-width": ""+s.x+"px",
                "max-height": ""+s.y+"px"
            });
            itemImgNode.set("src", this.data.iconUrl);


            this.deleteActionNode = this.node.getElement(".o2_fileDeleteActionNode");
            var itemTextTitleNode = this.node.getElement(".o2_fileItemTextTitleNode");

            itemIconNode.addEvent("click", function(e){
                this.toggleSelected();
                e.stopPropagation();
            }.bind(this));

            itemIconNode.makeLnk({
                "par": this._getLnkPar()
            });

            if (!this.explorer.options.noDelete) this._createActions();

            itemTextTitleNode.addEvent("click", function(e){
                this._open(e);
                e.stopPropagation();
            }.bind(this));

            this._customNodes();

            this._isNew();
        }.bind(this));
    },
    _createActions: function(){
        if (this.deleteActionNode) this.deleteActionNode.addEvent("click", function(e){
            this.deleteItem(e);
        }.bind(this));
    },

    _customNodes: function(){},
	_open: function(e){
		var _self = this;
        MWF.Actions.get("x_processplatform_assemble_designer").getFile(this.data.id, function(json){
            this.data = json.data;
            new MWF.xApplication.process.ProcessManager.FileDesigner(this.explorer, this.data);
        }.bind(this));
	},
    _getIcon: function(){
        return "file.png";
    },
    _getUrl: function(){
        var url = MWF.Actions.get("x_processplatform_assemble_surface").action.actions.readFile.uri;
        url = url.replace(/{flag}/, this.data.id);
        url = url.replace(/{applicationFlag}/, this.data.application);
        url = "/x_processplatform_assemble_surface"+url;
        return  o2.filterUrl(MWF.Actions.getHost("x_processplatform_assemble_surface")+url);
    },
	_getLnkPar: function(){
		return {
			"icon": this.data.iconUrl,
			"title": this.data.name,
            "par": "@url#"+this._getUrl()
		};
	},
    deleteFile: function(callback){
		this.explorer.app.restActions.deleteFile(this.data.id, function(){
			this.node.destroy();
			if (callback) callback();
		}.bind(this));
	},
    _isNew: function(){
        if (this.data.updateTime){
            var createDate = Date.parse(this.data.updateTime);
            var currentDate = new Date();
            if (createDate.diff(currentDate, "hour")<12) {
                this.newNode = new Element("div", {
                    "styles": this.css.itemFileNewNode
                }).inject(this.node);
                this.newNode.addEvent("click", function(e){
                    this.toggleSelected();
                    e.stopPropagation();
                }.bind(this));
            }
        }
    }

});

MWF.xApplication.process.ProcessManager.FileDesigner = new Class({
    initialize: function(explorer, item){
        this.explorer = explorer;
        this.app = this.explorer.app;
        this.data = item;
        this.container = this.explorer.container;
        this.css = this.explorer.css;
        this.lp = MWF.APPPM.LP;
        this.load();
    },
    getNewData: function(){
        return {
            "id": "",
            "name": "",
            "alias": "",
            "description": "",
            "application": (this.explorer.app.options.application || this.explorer.app.application).id,
            "fileName": ""
        }
    },
    resize: function(){
        var size = this.app.content.getSize();
        var nodeSize = this.fileAreaNode.getSize();
        var x = (size.x-nodeSize.x)/2;
        var y = (size.y-nodeSize.y)/2;
        if (y<0) y=0;
        if (x<0) x=0;
        this.fileAreaNode.setStyles({
            "top": ""+y+"px",
            "left": ""+x+"px"
        });
        var titleSize = this.titleNode.getSize();
        var buttonSize = this.buttonNode.getSize();

        var h = nodeSize.y-titleSize.y-buttonSize.y;
        this.contentNode.setStyle("height", ""+h+"px");
    },
    load: function(){
        if (!this.data) this.data = this.getNewData();

        this.fileMaskNode = new Element("div", {"styles": this.css.createTemplateMaskNode}).inject(this.app.content);
        this.fileAreaNode = new Element("div", {"styles": this.css.createFormTemplateAreaNode}).inject(this.app.content);
        this.fileAreaNode.fade("in");

        this.titleNode = new Element("div", {"styles": this.css.fileDesignerTitleNode}).inject(this.fileAreaNode);
        this.titleIconNode = new Element("div", {"styles": this.css.fileDesignerTitleIconNode}).inject(this.titleNode);
        if (!this.data.id) this.titleIconNode.setStyle("background-image", "url("+this.explorer.path+this.app.options.style+"/icon/new.png)");
        this.titleTextNode = new Element("div", {"styles": this.css.fileDesignerTitleTextNode}).inject(this.titleNode);
        var title = (this.data.name) ? this.data.name : this.explorer.options.tooltip.create;
        this.titleTextNode.set("text", title);

        this.contentNode = new Element("div", {"styles": this.css.fileDesignerContentNode}).inject(this.fileAreaNode);
        this.createContent();

        this.buttonNode = new Element("div", {"styles": this.css.fileDesignerButtonNode}).inject(this.fileAreaNode);
        this.createButton();

        this.resizeFun = this.resize.bind(this);
        this.resizeFun();
        this.app.addEvent("resize", this.resizeFun);

        this.setEvent();
    },
    createContent: function(){
        this.contentAreaNode = new Element("div", {"styles": this.css.fileDesignerContentAreaNode}).inject(this.contentNode);
        this.nameInput = this.createContentLine(this.lp.name, this.data.name);
        this.aliasInput = this.createContentLine(this.lp.alias, this.data.alias);
        this.descriptionInput = this.createContentLine(this.lp.file.description, this.data.description, true);
        this.createContentFile();
        this.createContentFileUrl();
    },
    createContentFileUrl: function(){
        if (this.data.fileName){
            var div = new Element("div", {"styles": this.css.fileDesignerContentLineNode}).inject(this.contentAreaNode);
            var lineTitleNode = new Element("div", {"styles": this.css.fileDesignerContentLineTitleNode, "text": "URL"}).inject(div);
            this.fileUrlNode = new Element("div", {"styles": this.css.fileDesignerContentLineContentNode}).inject(div);
            div.setStyle("height", "80px");
            var url = MWF.Actions.get("x_processplatform_assemble_surface").action.actions.readFile.uri;
            url = url.replace(/{flag}/, this.data.id);
            url = url.replace(/{applicationFlag}/, this.data.application);
            url = "/x_processplatform_assemble_surface"+url;
            this.fileUrlNode.setStyle("line-height", "18px");
            var href = MWF.Actions.getHost("x_processplatform_assemble_surface")+url;
            //this.fileUrlNode.set("html", "<a target='_blank' href='"+href+"'>"+url+"</a>");
            this.fileUrlNode.set("text", url);

            var a = new Element("div", {
                "styles": {"height": "30px"},
                "html": "<a target='_blank' href='"+o2.filterUrl(href)+"'>open</a>"
            }).inject(this.fileUrlNode, "bottom");
        }
    },
    modifyContentFileUrl: function(){
        if (!this.fileUrlNode){
            this.createContentFileUrl();
        }else{
            var url = MWF.Actions.get("x_processplatform_assemble_surface").action.actions.readFile.uri;
            url = url.replace(/{flag}/, this.data.id);
            url = url.replace(/{applicationFlag}/, this.data.application);
            //this.fileUrlNode.set("text", "/x_processplatform_assemble_surface"+url);
            url = "/x_processplatform_assemble_surface"+url;
            this.fileUrlNode.setStyle("line-height", "18px");
            var href = MWF.Actions.getHost("x_processplatform_assemble_surface")+url;
            //this.fileUrlNode.set("html", "<a target='_blank' href='"+href+"'>"+url+"</a>");
            this.fileUrlNode.set("text", url);
            var a = new Element("div", {
                "styles": {"height": "30px"},
                "html": "<a target='_blank' href='"+o2.filterUrl(href)+"'>open</a>"
            }).inject(this.fileUrlNode, "bottom");
        }

    },
    createContentFile: function(){
        var div = new Element("div", {"styles": this.css.fileDesignerContentFileLineNode}).inject(this.contentAreaNode);
        var lineTitleNode = new Element("div", {"styles": this.css.fileDesignerContentFileLineTitleNode, "text": this.lp.attachment}).inject(div);
        var lineRightNode = new Element("div", {"styles": this.css.fileDesignerContentFileLineRightNode}).inject(div);
        this.fileContentNode = new Element("div", {"styles": this.css.fileDesignerContentFileLineContentNode}).inject(div);
        this.uploadFileButton = new Element("div", {"styles": this.css.fileDesignerUploadButtonNode, "text": this.lp.upload}).inject(lineRightNode);

        if (this.data.fileName){
            this.loadFileIcon();
        }
    },
    // getIconJson: function(callback){
    //     if (!this.icons){
    //         MWF.getJSON("../x_component_File/$Main/icon.json", function(json){
    //             this.icons = json;
    //             if (callback) callback();
    //         }.bind(this), false, false);
    //     }else{
    //         if (callback) callback();
    //     }
    // },
    // getIcon: function(ext){
    //     if (!ext) ext="unkonw";
    //     var iconName = this.icons[ext.toLowerCase()] || this.icons.unknow;
    //     return "../x_component_File/$Main/default/file/"+iconName;
    // },
    loadFileIcon: function(){
        debugger;
        this.fileContentNode.empty();
        //var ext = this.data.fileName.substr(this.data.fileName.lastIndexOf(".")+1, this.data.fileName.length);
        //this.explorer.getIconJson(function(){
        var url = this.explorer.getIcon(this.data.fileName);

        var fileIconNode = new Element("div", {"styles": this.css.fileDesignerContentFileLineFileIconNode}).inject(this.fileContentNode);
        fileIconNode.setStyle("background-image", "url('"+url+"')");
        var fileTextNode = new Element("div", {"styles": this.css.fileDesignerContentFileLineFileNameNode, "text": this.data.fileName}).inject(this.fileContentNode);
        var fileSizeNode = new Element("div", {"styles": this.css.fileDesignerContentFileLineFileSizeNode, "text": this.data.description}).inject(this.fileContentNode);

        //}.bind(this));
    },
    createContentLine: function(text, value, readonly){
        var div = new Element("div", {"styles": this.css.fileDesignerContentLineNode}).inject(this.contentAreaNode);
        var lineTitleNode = new Element("div", {"styles": this.css.fileDesignerContentLineTitleNode, "text": text}).inject(div);
        var lineContentNode = new Element("div", {"styles": this.css.fileDesignerContentLineContentNode}).inject(div);
        return new Element("input", {"styles": this.css.fileDesignerContentLineInputNode, "value": value, "readonly": readonly}).inject(lineContentNode);
    },
    createButton: function(){
        this.cancelButton = new Element("div", {"styles": this.css.fileDesignerCancelButtonNode, "text": this.lp.cancel}).inject(this.buttonNode);
        this.okButton = new Element("div", {"styles": this.css.fileDesignerOkButtonNode, "text": this.lp.ok}).inject(this.buttonNode);
    },
    setEvent: function(){
        this.cancelButton.addEvent("click", function(e){ this.close(e); }.bind(this));
        this.okButton.addEvent("click", function(){ this.save(); }.bind(this));
        this.uploadFileButton.addEvent("click", function(){ this.upload(); }.bind(this));
    },
    upload: function(){
        if (!this.data.id){
            //MWF.Actions.get("x_processplatform_assemble_designer").saveFile(this.data, function(){
            //    this.explorer.reload();
                this.uploadFile(function(){
                    this.app.notice(this.lp.file.uploadSuccess, "success");
                }.bind(this));
            //}.bind(this));
        }else{
            this.uploadFile(function(){
                this.app.notice(this.lp.file.uploadSuccess, "success");
            }.bind(this));
        }
    },
    uploadFile: function(callback){
        MWF.require("MWF.widget.Upload", function(){
            new MWF.widget.Upload(this.app.content, {
                "action": MWF.Actions.get("x_processplatform_assemble_designer").action,
                "multiple": false,
                "method": "uploadFile",
                "parameter": {"id": this.data.id},
                "onCompleted": function(){
                    this.loadFileIcon();
                    this.modifyContentFileUrl();
                    this.explorer.reload();
                    if (callback) callback();
                }.bind(this),
                "onBeforeUpload": function(files, up){
                    var name = files[0].name;
                    this.nameInput.set("value", name);
                    var data = this.getData();
                    this.data = Object.merge(this.data, data);
                    MWF.Actions.get("x_processplatform_assemble_designer").saveFile(this.data, function(json){
                        this.explorer.reload();
                        up.options.parameter = {"id": json.data.id};
                    }.bind(this), null, false);
                }.bind(this),
                "onEvery": function(json, current, count, file){
                    //this.data.description = file.name+" "+this.getSizeText(file.size);
                    //this.data.id = json.data.id;
                    this.data.fileName = file.name;
                    this.data.description = file.name+" "+this.getSizeText(file.size);
                    this.descriptionInput.set("value", this.data.description);
                    MWF.Actions.get("x_processplatform_assemble_designer").saveFile(this.data);
                }.bind(this)
            }).load();
        }.bind(this));
    },
    getSizeText: function(s){
        var o = [
            {"t": "K", "i": 1024},
            {"t": "M", "i": 1024*1024},
            {"t": "G", "i": 1024*1024*1024}
        ];
        var i = 0;
        var n = s/o[i].i;
        while (n>1000 && i<2){
            i++;
            n = s/o[i].i;
        }
        n = Math.round(n*100)/100;
        return ""+n+" "+o[i].t;
    },
    getData: function(){
        return {
            "name": this.nameInput.get("value"),
            "alias": this.aliasInput.get("value"),
            "description": this.descriptionInput.get("value")
        }
    },
    close: function(e){
        var data = this.getData();
        var _self = this;
        if (data.name!==this.data.name || data.alias!==this.data.alias || data.description!== this.data.description){
            this.app.confirm("infor", e, this.lp.file.saveConfirm, this.lp.file.saveConfirmText, 350, 120, function(){
                this.close();
                _self.save();
            }, function(){
                this.close();
                _self.destroy();
            })
        }else{
            this.destroy();
        }
    },
    save: function(){
        var data = this.getData();
        this.data = Object.merge(this.data, data);
        MWF.Actions.get("x_processplatform_assemble_designer").saveFile(this.data, function(){
            this.explorer.reload();
            this.app.notice(this.lp.file.saveSuccess, "success");
            this.destroy();
        }.bind(this));
    },

    destroy: function(){
        this.fileMaskNode.destroy();
        this.fileAreaNode.destroy();
        if (this.resizeFun) this.app.removeEvent("resize", this.resizeFun);
        MWF.release(this);
    }
});