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

    _createElement: function(e){
        // var _self = this;
        // var options = {
        //     "onQueryLoad": function(){
        //         this.actions = _self.app.restActions;
        //         this.application = _self.app.options.application || _self.app.application;
        //         this.explorer = _self;
        //     }
        // };
        // this.app.desktop.openApplication(e, "process.FileDesigner", options);
        new MWF.xApplication.process.ProcessManager.FileDesigner(this);
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
                //    this.reloadItems();
                //    this.hideDeleteAction();
                }.bind(this));
            }
        }
    }
});

MWF.xApplication.process.ProcessManager.FileExplorer.File = new Class({
	Extends: MWF.xApplication.process.ProcessManager.DictionaryExplorer.Dictionary,

    _customNodes: function(){
        // if (!this.data.validated){
        //     new Element("div", {"styles": this.explorer.css.itemErrorNode}).inject(this.node);
        //     this.node.setStyle("background-color", "#f9e8e8");
        // }
    },
	_open: function(e){
		var _self = this;
        MWF.Actions.get("x_processplatform_assemble_designer").getFile(this.data.id, function(json){
            this.data = json.data;
            new MWF.xApplication.process.ProcessManager.FileDesigner(this.explorer, this.data);
        }.bind(this));


		// var options = {
		// 	"onQueryLoad": function(){
		// 		this.actions = _self.explorer.actions;
		// 		this.category = _self;
		// 		this.options.id = _self.data.id;
         //        this.application = _self.explorer.app.options.application;
         //        this.explorer = _self.explorer
		// 	}
		// };
		// this.explorer.app.desktop.openApplication(e, "process.FileDesigner", options);
	},
    _getIcon: function(){
        //var x = (Math.random()*33).toInt();
        //return "process_icon_"+x+".png";
        return "file.png";
    },
	_getLnkPar: function(){
		return {
			"icon": this.explorer.path+this.explorer.options.style+"/fileIcon/lnk.png",
			"title": this.data.name,
			"par": "process.FileDesigner#{\"id\": \""+this.data.id+"\", \"applicationId\": \""+this.explorer.app.options.application.id+"\"}"
		};
	},
//	deleteItem: function(e){
//		var _self = this;
//		this.explorer.app.confirm("info", e, this.explorer.app.lp.form.deleteFormTitle, this.explorer.app.lp.form.deleteForm, 320, 110, function(){
//			_self.deleteForm();
//			this.close();
//		},function(){
//			this.close();
//		});
//	},
    deleteFile: function(callback){
		this.explorer.app.restActions.deleteFile(this.data.id, function(){
			this.node.destroy();
			if (callback) callback();
		}.bind(this));
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
        debugger;
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
                "html": "<a target='_blank' href='"+href+"'>open</a>"
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
                "html": "<a target='_blank' href='"+href+"'>open</a>"
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
    getIconJson: function(callback){
        if (!this.icons){
            MWF.getJSON("/x_component_File/$Main/icon.json", function(json){
                this.icons = json;
                if (callback) callback();
            }.bind(this), false, false);
        }else{
            if (callback) callback();
        }
    },
    getIcon: function(ext){
        if (!ext) ext="unkonw";
        var iconName = this.icons[ext.toLowerCase()] || this.icons.unknow;
        return "/x_component_File/$Main/default/file/"+iconName;
    },
    loadFileIcon: function(){
        debugger;
        this.fileContentNode.empty();
        var ext = this.data.fileName.substr(this.data.fileName.lastIndexOf(".")+1, this.data.fileName.length);
        this.getIconJson(function(){
            var url = this.getIcon(ext);

            var fileIconNode = new Element("div", {"styles": this.css.fileDesignerContentFileLineFileIconNode}).inject(this.fileContentNode);
            fileIconNode.setStyle("background-image", "url('"+url+"')");
            var fileTextNode = new Element("div", {"styles": this.css.fileDesignerContentFileLineFileNameNode, "text": this.data.fileName}).inject(this.fileContentNode);
            var fileSizeNode = new Element("div", {"styles": this.css.fileDesignerContentFileLineFileSizeNode, "text": this.data.description}).inject(this.fileContentNode);

        }.bind(this));
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
        debugger;
        if (!this.data.id){
            var data = this.getData();
            this.data = Object.merge(this.data, data);
            MWF.Actions.get("x_processplatform_assemble_designer").saveFile(this.data, function(){
                this.explorer.reload();
                this.uploadFile(function(){
                    this.app.notice(this.lp.file.uploadSuccess, "success");
                }.bind(this));
            }.bind(this));
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
                "method": "uploadFile",
                "parameter": {"id": this.data.id},
                "onCompleted": function(){
                    this.loadFileIcon();
                    this.modifyContentFileUrl();
                    if (callback) callback();
                }.bind(this),
                "onEvery": function(json, current, count, file){
                    //this.data.description = file.name+" "+this.getSizeText(file.size);
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