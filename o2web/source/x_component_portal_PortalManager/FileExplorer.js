MWF.xDesktop.requireApp("process.ProcessManager", "FileExplorer", null, false);
MWF.xApplication.portal.PortalManager.FileExplorer = new Class({
	Extends: MWF.xApplication.process.ProcessManager.FileExplorer,
	Implements: [Options, Events],
    options: {
        "create": MWF.APPPM.LP.file.create,
        "search": MWF.APPPM.LP.file.search,
        "searchText": MWF.APPPM.LP.file.searchText,
        "noElement": MWF.APPPM.LP.file.noDictionaryNoticeText
    },

    // _createElement: function(e){
    //     // var _self = this;
    //     // var options = {
    //     //     "onQueryLoad": function(){
    //     //         this.actions = _self.app.restActions;
    //     //         this.application = _self.app.options.application || _self.app.application;
    //     //         this.explorer = _self;
    //     //     }
    //     // };
    //     // this.app.desktop.openApplication(e, "process.FileDesigner", options);
    //     new MWF.xApplication.portal.PortalManager.FileDesigner(this);
    // },
    getNewData: function(){
        return {
            "id": "",
            "name": "",
            "alias": "",
            "description": "",
            "portal": (this.app.options.application || this.app.application).id,
            "fileName": ""
        }
    },
    implodeFiles: function(){
        if (this.upload){
            this.upload.upload();
        }else{

        }
        MWF.require("MWF.widget.Upload", function(){
            var datas = [];
            new MWF.widget.Upload(this.app.content, {
                "action": MWF.Actions.get("x_portal_assemble_designer").action,
                "multiple": true,
                "method": "uploadFile",
                "parameter": {"id": ""},
                "onBeforeUploadEntry": function(file, up){
                    var data = this.getNewData();
                    data.name = file.name;
                    data.fileName = file.name;
                    data.description = file.name+" "+this.getSizeText(file.size);
                    data.updateTime = (new Date()).format("db");
                    MWF.Actions.get("x_portal_assemble_designer").saveFile(data, function(json){
                        up.options.parameter = {"id": json.data.id};

                        var node = this.elementContentListNode.getFirst();
                        if (node) if (node.hasClass("noElementNode")){
                            node.destroy();
                        }
                        datas.push(data);
                        // var itemObj = this._getItemObject(data);
                        // itemObj.load();
                    }.bind(this), null, false);
                }.bind(this),
                "onEvery": function(json, current, count, file){
                    var data = datas[current-1];
                    var itemObj = this._getItemObject(data);
                    itemObj.load();
                }.bind(this)
            }).load();
        }.bind(this));
    },
    _getItemObject: function(item){
        return new MWF.xApplication.portal.PortalManager.FileExplorer.File(this, item)
    }
});

MWF.xApplication.portal.PortalManager.FileExplorer.File = new Class({
	Extends: MWF.xApplication.process.ProcessManager.FileExplorer.File,

	_open: function(e){
		var _self = this;
        MWF.Actions.get("x_portal_assemble_designer").getFile(this.data.id, function(json){
            this.data = json.data;
            new MWF.xApplication.portal.PortalManager.FileDesigner(this.explorer, this.data);
        }.bind(this));
	},
    _getUrl: function(){
        var url = MWF.Actions.get("x_portal_assemble_surface").action.actions.readFile.uri;
        url = url.replace(/{flag}/, this.data.id);
        url = url.replace(/{applicationFlag}/, this.data.portal);
        url = "/x_portal_assemble_surface"+url;
        return MWF.Actions.getHost("x_portal_assemble_surface")+url;
    },
    _getIcon: function(){
        return "file.png";
    },
	_getLnkPar: function(){
        var url = MWF.Actions.get("x_portal_assemble_surface").action.actions.readFile.uri;
        url = url.replace(/{flag}/, this.data.id);
        url = url.replace(/{applicationFlag}/, this.data.portal);
        url = "/x_portal_assemble_surface"+url;
        var href = MWF.Actions.getHost("x_portal_assemble_surface")+url;

		return {
			"icon": this.data.iconUrl,
			"title": this.data.name,
            "par": "@url#"+href
		};
	},

    deleteFile: function(callback){
		this.explorer.app.restActions.deleteFile(this.data.id, function(){
			this.node.destroy();
			if (callback) callback();
		}.bind(this));
	}
});

MWF.xApplication.portal.PortalManager.FileDesigner = new Class({
    Extends: MWF.xApplication.process.ProcessManager.FileDesigner,

    getNewData: function(){
        return {
            "id": "",
            "name": "",
            "alias": "",
            "description": "",
            "portal": (this.explorer.app.options.application || this.explorer.app.application).id,
            "fileName": ""
        }
    },
    createContentFileUrl: function(){
        if (this.data.fileName){
            var div = new Element("div", {"styles": this.css.fileDesignerContentLineNode}).inject(this.contentAreaNode);
            var lineTitleNode = new Element("div", {"styles": this.css.fileDesignerContentLineTitleNode, "text": "URL"}).inject(div);
            this.fileUrlNode = new Element("div", {"styles": this.css.fileDesignerContentLineContentNode}).inject(div);
            div.setStyle("height", "80px");
            var url = MWF.Actions.get("x_portal_assemble_surface").action.actions.readFile.uri;
            url = url.replace(/{flag}/, this.data.id);
            url = url.replace(/{applicationFlag}/, this.data.portal);
            url = "/x_portal_assemble_surface"+url;
            this.fileUrlNode.setStyle("line-height", "18px");
            var href = MWF.Actions.getHost("x_portal_assemble_surface")+url;
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
            var url = MWF.Actions.get("x_portal_assemble_surface").action.actions.readFile.uri;
            url = url.replace(/{flag}/, this.data.id);
            url = url.replace(/{applicationFlag}/, this.data.portal);
            //this.fileUrlNode.set("text", "/x_processplatform_assemble_surface"+url);
            url = "/x_portal_assemble_surface"+url;
            this.fileUrlNode.setStyle("line-height", "18px");
            var href = MWF.Actions.getHost("x_portal_assemble_surface")+url;
            //this.fileUrlNode.set("html", "<a target='_blank' href='"+href+"'>"+url+"</a>");
            this.fileUrlNode.set("text", url);
            var a = new Element("div", {
                "styles": {"height": "30px"},
                "html": "<a target='_blank' href='"+href+"'>open</a>"
            }).inject(this.fileUrlNode, "bottom")
        }

    },

    upload: function(){
        if (!this.data.id){
            // var data = this.getData();
            // this.data = Object.merge(this.data, data);
            // MWF.Actions.get("x_portal_assemble_designer").saveFile(this.data, function(){
            //     this.explorer.reload();
                this.uploadFile(function(){
                    this.app.notice(this.lp.file.uploadSuccess, "success");
                }.bind(this));
            // }.bind(this));
        }else{
            this.uploadFile(function(){
                this.app.notice(this.lp.file.uploadSuccess, "success");
            }.bind(this));
        }
    },
    uploadFile: function(callback){
        MWF.require("MWF.widget.Upload", function(){
            new MWF.widget.Upload(this.app.content, {
                "action": MWF.Actions.get("x_portal_assemble_designer").action,
                "method": "uploadFile",
                "parameter": {"id": this.data.id},
                "onCompleted": function(){
                    this.loadFileIcon();
                    this.modifyContentFileUrl();
                    if (callback) callback();
                }.bind(this),
                "onBeforeUpload": function(files, up){
                    var name = files[0].name;
                    this.nameInput.set("value", name);
                    var data = this.getData();
                    this.data = Object.merge(this.data, data);
                    MWF.Actions.get("x_portal_assemble_designer").saveFile(this.data, function(json){
                        this.explorer.reload();
                        up.options.parameter = {"id": json.data.id};
                    }.bind(this), null, false);
                }.bind(this),
                "onEvery": function(json, current, count, file){
                    debugger;
                    //this.data.description = file.name+" "+this.getSizeText(file.size);
                    //this.data.id = json.data.id;
                    this.data.fileName = file.name;
                    this.data.description = file.name+" "+this.getSizeText(file.size);
                    this.descriptionInput.set("value", this.data.description);
                    MWF.Actions.get("x_portal_assemble_designer").saveFile(this.data);
                }.bind(this)
            }).load();
        }.bind(this));
    },

    save: function(){
        var data = this.getData();
        this.data = Object.merge(this.data, data);
        MWF.Actions.get("x_portal_assemble_designer").saveFile(this.data, function(){
            this.explorer.reload();
            this.app.notice(this.lp.file.saveSuccess, "success");
            this.destroy();
        }.bind(this));
    }

});