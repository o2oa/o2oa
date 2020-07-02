//MWF.xDesktop.requireApp("File", "Actions.RestActions", null, false);
MWF.xDesktop.requireApp("File", "AttachmentController", null, false);
MWF.require("MWF.widget.Tree", null, false);
MWF.xApplication.File.Main = new Class({
	Extends: MWF.xApplication.Common.Main,
	Implements: [Options, Events],

	options: {
		"style": "default",
		"name": "File",
		"icon": "icon.png",
		"width": "1000",
		"height": "600",
		"title": MWF.xApplication.File.LP.title
	},
	onQueryLoad: function(){
		this.lp = MWF.xApplication.File.LP;
        this.restActions = MWF.Actions.get("x_file_assemble_control");
	},
	loadApplication: function(callback){
		this.history = [];
		this.currentHistory = 1;
		this.currentFolder = null;
		
		//this.restActions = new MWF.xApplication.File.Actions.RestActions();
		
		MWF.getJSON("../x_component_File/$Main/icon.json", function(json){
			this.icons = json;
		}.bind(this), false, false);
		
		this.createNode();
		this.loadApplicationContent();
		if (callback) callback();
	},
	createNode: function(){
		this.content.setStyle("overflow", "hidden");
		this.node = new Element("div", {
			"styles": {"width": "100%", "height": "100%", "overflow": "hidden"}
		}).inject(this.content);
	},
	loadApplicationContent: function(){
		this.loadTop();
		
	//	this.loadTools();
		
		this.fileContentNode = new Element("div", {"styles": this.css.fileContentNode}).inject(this.node);
        this.folderContentNode = new Element("div", {"styles": this.css.folderContentNode}).inject(this.fileContentNode);
        this.attachmentContentNode = new Element("div", {"styles": this.css.attachmentContentNode}).inject(this.fileContentNode);


        this.resizeContentNode = new Element("div", {"styles": this.css.resizeContentNode}).inject(this.folderContentNode);

		this.folderTreeAreaNode = new Element("div", {
			"styles": this.css.folderTreeAreaNode
		}).inject(this.folderContentNode);
		
		this.folderTreeAreaScrollNode = new Element("div", {
			"styles": this.css.folderTreeAreaScrollNode
		}).inject(this.folderContentNode);
		this.shareTreeAreaScrollNode = new Element("div", {
			"styles": this.css.folderTreeAreaScrollNode
		}).inject(this.folderContentNode);
        this.editorTreeAreaScrollNode = new Element("div", {
            "styles": this.css.folderTreeAreaScrollNode
        }).inject(this.folderContentNode);
		
		this.loadFileContentAreaNode();
		this.loadFolderTreeNode();
		this.loadShareTreeNode();
        this.loadEditorTreeNode();

        this.shareTree.loadCallback = function(){
            if (this.shareTab.isShow){
                if (this.status){
                    if (this.status.node){
                        var found = false;
                        for (var i=0; i<this.shareTree.children.length; i++){
                            if (this.shareTree.children[i].data.name == this.status.node){
                                this.shareTree.children[i].clickNode();
                                found = true;
                                break;
                            }
                        }
                        if (!found) this.shareTree.firstChild.clickNode();
                    }else{
                        this.shareTree.firstChild.clickNode();
                    }
                }else{
                    this.shareTree.firstChild.clickNode();
                }
            }
        };

        this.editorTree.loadCallback = function(){
            if (this.editorTab.isShow){
                if (this.status){
                    if (this.status.node){
                        var found = false;
                        for (var i=0; i<this.editorTree.children.length; i++){
                            if (this.editorTree.children[i].data.name == this.status.node){
                                this.editorTree.children[i].clickNode();
                                found = true;
                                break;
                            }
                        }
                        if (!found) this.editorTree.firstChild.clickNode();
                    }else{
                        this.editorTree.firstChild.clickNode();
                    }
                }else{
                    this.editorTree.firstChild.clickNode();
                }
            }
        }


        this.treeResize = new Drag(this.resizeContentNode,{
            "snap": 1,
            "onStart": function(el, e){
                //alert(e.event.clientX);
                var x = e.event.clientX;
                var y = e.event.clientY;
                el.store("position", {"x": x, "y": y});

                var size = this.folderContentNode.getSize();
                el.store("initialWidth", size.x);
            }.bind(this),
            "onDrag": function(el, e){
                var x = e.event.clientX;
//				var y = e.event.y;
                var bodySize = this.content.getSize();
                var position = el.retrieve("position");
                var initialWidth = el.retrieve("initialWidth").toFloat();
                var dx = x.toFloat() - position.x.toFloat();

                var width = initialWidth+dx;
                if (width> bodySize.x/2) width =  bodySize.x/2;
                if (width<200) width = 200;
                this.attachmentContentNode.setStyle("margin-left", width);
                this.folderContentNode.setStyle("width", width);
            }.bind(this)
        });
		
		MWF.require("MWF.widget.Tab", function(){
			this.treeTab = new MWF.widget.Tab(this.folderTreeAreaNode, {"style": "processlayout"});
			this.treeTab.load();
			this.fileTabe = this.treeTab.addTab(this.folderTreeAreaScrollNode, this.lp.myFiles, false);
			this.shareTab = this.treeTab.addTab(this.shareTreeAreaScrollNode, this.lp.shareFiles, false);
            this.editorTab = this.treeTab.addTab(this.editorTreeAreaScrollNode, this.lp.editorFiles, false);

            this.fileTabe.addEvent("show", function(){
                if (this.folderTree.currentNode){
                    this.folderTree.currentNode.clickNode();
                }else{
                    if (this.folderTree.firstChild){
                        this.folderTree.firstChild.clickNode();
                    }else{
                        if (this.controller) this.controller.clear();
                    }
                }
                this.checkControllerActionsFile();
            }.bind(this));
            this.shareTab.addEvent("show", function(){
                if (this.shareTree.currentNode){
                    this.shareTree.currentNode.clickNode();
                }else{
                    if (this.shareTree.firstChild){
                        this.shareTree.firstChild.clickNode();
                    }else{
                        if (this.controller) this.controller.clear();
                    }
                }
                this.checkControllerActionsShare();
            }.bind(this));
            this.editorTab.addEvent("show", function(){
                if (this.editorTree.currentNode){
                    this.editorTree.currentNode.clickNode();
                }else{
                    if (this.editorTree.firstChild){
                        this.editorTree.firstChild.clickNode();
                    }else{
                        if (this.controller) this.controller.clear();
                    }
                }
                this.checkControllerActionsEditor();
            }.bind(this));

            var tabName = "file";
            if (this.status){
                if (this.status.tab){
                    tabName = this.status.tab;
                }
            }
            if (tabName == "file") this.fileTabe.showIm();
            if (tabName == "share") this.shareTab.showIm();
            if (tabName == "editor") this.editorTab.showIm();

            this.setContentHeight();
            this.addEvent("resize", function(){this.setContentHeight();}.bind(this));

        }.bind(this));

		MWF.require("MWF.widget.ScrollBar", function(){
			new MWF.widget.ScrollBar(this.folderTreeAreaScrollNode, {
				"style":"xApp_Organization_Explorer", "where": "before", "distance": 100, "friction": 4,	"axis": {"x": false, "y": true}
			});
			new MWF.widget.ScrollBar(this.shareTreeAreaScrollNode, {
				"style":"xApp_Organization_Explorer", "where": "before", "distance": 100, "friction": 4,	"axis": {"x": false, "y": true}
			});
            new MWF.widget.ScrollBar(this.editorTreeAreaScrollNode, {
                "style":"xApp_Organization_Explorer", "where": "before", "distance": 100, "friction": 4,	"axis": {"x": false, "y": true}
            });
			//new MWF.widget.ScrollBar(this.fileContentAreaScrollNode, {
			//	"style":"xApp_Organization_Explorer", "where": "before", "distance": 100, "friction": 4,	"axis": {"x": false, "y": true}
			//});
		}.bind(this));
		
	},
    checkControllerActionsFile: function(){
        this.controller.folderActionBoxNode.setStyle("display", "block");
        //this.controller.editActionBoxNode.setStyle("display", "block");

        this.controller.uploadAction.setStyle("display", "block");
        this.controller.deleteAction.setStyle("display", "block");
        this.controller.replaceAction.setStyle("display", "block");
        this.controller.editActionSeparateNode.setStyle("display", "block");

        this.controller.shareActionBoxNode.setStyle("display", "block");
    },
    checkControllerActionsShare: function(){
        this.controller.folderActionBoxNode.setStyle("display", "none");

        this.controller.uploadAction.setStyle("display", "none");
        this.controller.deleteAction.setStyle("display", "none");
        this.controller.replaceAction.setStyle("display", "none");
        this.controller.editActionSeparateNode.setStyle("display", "none");

     //   this.controller.editActionBoxNode.setStyle("display", "none");
        this.controller.shareActionBoxNode.setStyle("display", "none");
    },
    checkControllerActionsEditor: function(){
        this.controller.folderActionBoxNode.setStyle("display", "none");

        this.controller.uploadAction.setStyle("display", "none");
        this.controller.deleteAction.setStyle("display", "none");
        this.controller.replaceAction.setStyle("display", "block");
        this.controller.editActionSeparateNode.setStyle("display", "block");

        //   this.controller.editActionBoxNode.setStyle("display", "none");
        this.controller.shareActionBoxNode.setStyle("display", "none");
    },
	loadTop: function(){
		this.topNode = new Element("div", {"styles": this.css.topNode}).inject(this.node);
		
		this.leftNode = new Element("div", {"styles": this.css.leftNode}).inject(this.topNode);
		this.leftNode.addEvent("click", function(){
			this.leftPath();
		}.bind(this));
		
		this.rightNode = new Element("div", {"styles": this.css.rightNode}).inject(this.topNode);
		this.rightNode.addEvent("click", function(){
			this.rightPath();
		}.bind(this));
		
		this.refreshNode = new Element("div", {"styles": this.css.refreshNode}).inject(this.topNode);
		this.searchNode = new Element("div", {"styles": this.css.searchNode}).inject(this.topNode);
		this.pathNode = new Element("div", {"styles": this.css.pathNode}).inject(this.topNode);
	},
	//loadTools: function(){
	//	this.toolsNode = new Element("div", {"styles": this.css.toolsNode}).inject(this.node);
	//	this.uploadNode = new Element("div", {"styles": this.css.toolsActionNode, "text": this.lp.upload}).inject(this.toolsNode);
	//	this.createFolderNode = new Element("div", {"styles": this.css.toolsActionNode,	"text": this.lp.createFolder}).inject(this.toolsNode);
	//	this.operationNode = new Element("div", {"styles": this.css.toolsMenuActionNode}).inject(this.toolsNode);
	//	var downTextNode = new Element("div", {"styles": this.css.downTextNode, "text": this.lp.operation}).inject(this.operationNode);
	//	var downIconNode = new Element("div", {"styles": this.css.downIconNode}).inject(this.operationNode);
	//
	//	this.uploadNode.addEvents({
	//		"mouseover": function(){this.uploadNode.setStyles(this.css.toolsActionNode_over);}.bind(this),
	//		"mouseout": function(){this.uploadNode.setStyles(this.css.toolsActionNode);}.bind(this),
	//		"click": function(){this.createUploadFile();}.bind(this)
	//	});
	//	this.createFolderNode.addEvents({
	//		"mouseover": function(){this.createFolderNode.setStyles(this.css.toolsActionNode_over);}.bind(this),
	//		"mouseout": funtion(){this.createFolderNode.setStyles(this.css.toolsActionNode);}.bind(this),
	//		"click": function(){this.createFolder();}.bind(this)
	//	});
	//	this.operationNode.addEvents({
	//		"mouseover": function(){this.operationNode.setStyles(this.css.toolsMenuActionNode_over);}.bind(this),
	//		"mouseout": function(){this.operationNode.setStyles(this.css.toolsMenuActionNode);}.bind(this),
	//		"click": function(){}.bind(this)
	//	});
	//	this.operationMenu = new MWF.xDesktop.Menu(this.operationNode, {
	//		"event": "click",
	//		"style": "fileOperation",
	//		"offsetX": -4,
	//		"offsetY": 4,
	//		"onQueryShow": function(){
	//			this.operationMenu.items.each(function(item){item.setDisable(true);});
	//			if (this.selectedItem){
	//				if (this.selectedItem.type=="folder"){
	//					this.operationMenu.items[0].setText(this.lp.uploadTo+" "+this.selectedItem.data.name+"...");
	//					this.operationMenu.items[0].setDisable(false);
	//					this.operationMenu.items[3].setDisable(false);
	//					this.operationMenu.items[4].setDisable(false);
	//					this.operationMenu.items[7].setDisable(false);
	//				}
	//				if (this.selectedItem.type=="file"){
	//					this.operationMenu.items[0].setText(this.lp.uploadTo);
	//					this.operationMenu.items[0].setDisable(true);
	//					this.operationMenu.items[1].setDisable(false);
	//					this.operationMenu.items[3].setDisable(false);
	//					this.operationMenu.items[4].setDisable(false);
	//					this.operationMenu.items[5].setDisable(false);
	//					this.operationMenu.items[7].setDisable(false);
	//				}
	//			}
	//		}.bind(this)
	//	});
	//	this.operationMenu.load();
	//
	//	var img = this.path+this.options.style+"/operation/upload.png";
	//	this.operationMenu.addMenuItem(this.lp.uploadTo, "click", function(){this.createUploadFile(this.selectedItem);}.bind(this), img);
	//	var img = this.path+this.options.style+"/operation/download.png";
	//	this.operationMenu.addMenuItem(this.lp.download, "click", function(){this.downloadCurrentFile();}.bind(this), img);
	//
	//	this.operationMenu.addMenuLine();
	//
	//	var img = this.path+this.options.style+"/operation/move.png";
	//	this.operationMenu.addMenuItem(this.lp.move, "click", function(){this.moveFileFolder();}.bind(this), img);
	//	var img = this.path+this.options.style+"/operation/rename.png";
	//	this.operationMenu.addMenuItem(this.lp.rename, "click", function(){this.renameFileFolder();}.bind(this), img);
	//	var img = this.path+this.options.style+"/operation/share.png";
	//	this.operationMenu.addMenuItem(this.lp.share, "click", function(){this.shareFile();}.bind(this), img);
	//
	//	this.operationMenu.addMenuLine();
	//
	//	var img = this.path+this.options.style+"/operation/delete.png";
	//	this.operationMenu.addMenuItem(this.lp["delete"], "click", function(e){this.deleteFileFolder(e);}.bind(this), img);
	//},

    openAttachment: function(e, node, attachment){
        this.restActions.getFileUrl(attachment[0].data.id, function(url){
            window.open(o2.filterUrl(url));
        });
    },
    downloadAttachment: function(e, node, attachments){
        attachments.each(function(attachment){
            this.restActions.getFileDownloadUrl(attachment.data.id, function(url){
                window.open(o2.filterUrl(url));
            });
        }.bind(this));
    },
    getAttachmentLinkUrl: function(attachment){
        debugger;
        var url = o2.Actions.get("x_file_assemble_control").action.actions.getAttachmentData.uri;
        return url.replace("{id}", encodeURIComponent(attachment.data.id));
    },
	downloadCurrentFile: function(){
		if (this.selectedItem){
			if (this.selectedItem.type=="file"){
				this.selectedItem.open();
			}
		}
	},
	moveFileFolder: function(){
		//move----------
		//move----------
		//move----------
		//move----------
	},
	renameFileFolder: function(){
		this.content.mask({
			"style": {
				"opacity": 0.7,
				"background-color": "#999"
			}
		});
		
		var renameNode = new Element("div", {"styles": this.css.createFolderNode}).inject(this.content);
		renameNode.position({
			relativeTo: this.node,
		    position: "center"
		});

        var item = this.controller.selectedAttachments[0];
        var treeNode = item.treeNode;

		var titleNode = new Element("div", {"styles": this.css.createFolderTitleNode, "text": this.lp.rename}).inject(renameNode);
		var inforNode = new Element("div", {"styles": this.css.createFolderInforNode, "text": this.lp.inputName}).inject(renameNode);
		var inputAreaNode = new Element("div", {"styles": this.css.createFolderInputAreaNode}).inject(renameNode);
		var inputNode = new Element("input", {"type": "text", "styles": this.css.createFolderInputNode, "value": item.data.name}).inject(inputAreaNode);
		
		var actionNode = new Element("div", {"styles": this.css.createFolderActionNode}).inject(renameNode);
		var cancelButton = new Element("button", {"styles": this.css.createFolderCancelButton, "text": this.lp.cancel}).inject(actionNode);
		var okButton = new Element("button", {"styles": this.css.createFolderOkButton, "text": this.lp.ok}).inject(actionNode);
		
		cancelButton.addEvent("click", function(){
			this.content.unmask();
			renameNode.destroy();
		}.bind(this));
		okButton.addEvent("click", function(){
			if (inputNode.get("value")){
                item.data.name = inputNode.get("value");
				
				if (item.type=="folder"){
					this.restActions.saveFolder(item.data, function(json){
						if (this.currentFolder){
							this.currentFolder.clickNode();
						}else{
							this.topTreeNode.clickNode();
						}
                        treeNode.setText(inputNode.get("value"));
					}.bind(this));
				}else{
					this.restActions.updateAttachment(this.controller.selectedAttachments[0].data.id, this.controller.selectedAttachments[0].data, function(json){
						if (this.currentFolder){
							this.currentFolder.clickNode();
						}else{
							this.topTreeNode.clickNode();
						}
					}.bind(this));
				}
				renameNode.destroy();
				this.content.unmask();
			}else{
				this.notice(this.lp.nameNotEmpty, "error", renameNode);
			}
		}.bind(this));
	},

    deleteAttachments: function(e, node, attachments){
        this.deleteFileFolder(e, attachments);
    },
	deleteFileFolder: function(e, attachments){
		if (attachments.length){
            var _self = this;
            var title = this.lp.deleteFolderFilesTitle;
            var content = this.lp.deleteFolderFiles;
            if (attachments.length==1){
                var title = (attachments[0].type=="folder") ? this.lp.deleteFolderTitle : this.lp.deleteFileTitle;
                var content = (attachments[0].type=="folder") ? this.lp.deleteFolder : this.lp.deleteFile;
                content = content+"（"+attachments[0].data.name+"）";
            }
			var size = this.node.getSize();
	//		var position = this.operationMenu.items[7].item.getPosition(this.operationMenu.items[7].item.getOffsetParent());
			var ep = {"event": {"x": (size.x-300)/2, "y": (size.y-120)/2}};

			this.confirm("infor", ep, title, content, 300, 120, function(){
                var count = attachments.length;
                var current = 0;
                var callback = function(){
                    if (current == count){
                        if (this.currentFolder){
                            this.currentFolder.loaded = false;
                            this.currentFolder.clickNode();
                        }else{
                            this.topTreeNode.loaded = false;
                            this.topTreeNode.clickNode();
                        }
                    }
                };

                attachments.each(function(att){
                    if (att.type=="folder"){
                        att.treeNode.destroy();
                        _self.restActions.deleteFolder(att.data.id, function(){
                            current++;
                            callback.apply(_self);
                        });
                    }else{
                        _self.restActions.deleteFile(att.data.id, function(){
                            current++;
                            callback.apply(_self);
                        });
                    }
                });
				this.close();
			}, function(){
				this.close();
			});
		}
	},
    shareAttachment: function(){
        this.shareFile();
    },
	shareFile: function(){
		this.content.mask({
			"style": {
				"opacity": 0.7,
				"background-color": "#999"
			}
		});
		
		var shareNode = new Element("div", {"styles": this.css.createFolderNode}).inject(this.content);
		shareNode.position({
			relativeTo: this.node,
		    position: "center"
		});
		
		var titleNode = new Element("div", {"styles": this.css.createFolderTitleNode, "text": this.lp.shareFile}).inject(shareNode);
		var inforNode = new Element("div", {"styles": this.css.createFolderInforNode, "text": this.lp.selectShareUser}).inject(shareNode);
		var inputAreaNode = new Element("div", {"styles": this.css.createFolderInputAreaNode}).inject(shareNode);
		var inputNode = new Element("div", {"type": "text", "readonly": true, "styles": this.css.shareFileInputNode}).inject(inputAreaNode);
    //    if (this.controller.selectedAttachments.length==1) inputNode.set("value", this.controller.selectedAttachments[0].data.shareList.join(", "));


		inputNode.addEvent("click", function(){
            MWF.xDesktop.requireApp("Selector", "Person", function(){
				var selector = new MWF.xApplication.Selector.Person(this.node,{
					//"values": this.selectedItem.data.shareList,
					"onComplete": function(items){
                        MWF.require("MWF.widget.O2Identity", function(){
                            var names = [];
                            var texts = [];
                            inputNode.empty();
                            items.each(function(item){
                                names.push(item.data.distinguishedName);
                                texts.push(item.data.name);
                                new MWF.widget.O2Person({"name": item.data.distinguishedName}, inputNode, {"style": "xform"})
                            });
                            inputNode.store("texts", texts);
                            inputNode.set("value", names.join(", "));
                        }.bind(this));

					}.bind(this)
				});
				selector.load();
			}.bind(this));
		}.bind(this));
		
		var actionNode = new Element("div", {"styles": this.css.createFolderActionNode}).inject(shareNode);
		var cancelButton = new Element("button", {"styles": this.css.createFolderCancelButton, "text": this.lp.cancel}).inject(actionNode);
		var okButton = new Element("button", {"styles": this.css.createFolderOkButton, "text": this.lp.ok}).inject(actionNode);
		
		cancelButton.addEvent("click", function(){
			this.content.unmask();
			shareNode.destroy();
		}.bind(this));
		okButton.addEvent("click", function(){
            var count = this.controller.selectedAttachments.length;
            var current = 0;
            var callback = function(){
                if (current === count){
                    if (inputNode.get("value")) this.notice(this.lp.fileShareSuccess+inputNode.retrieve("texts", []).join(","), "success", this.content);
                    shareNode.destroy();
                    this.content.unmask();
                }
            };

            this.controller.selectedAttachments.each(function(att){
                if (att.type!="folder"){
                    att.data.shareList = inputNode.get("value").split(/,\s*/g);
                    this.restActions.updateAttachment(att.data.id, att.data, function(json){
                        current++;
                        callback.apply(this);
                    }.bind(this));
                }else{
                    current++;
                    callback.apply(this);
                }
            }.bind(this));
			
		}.bind(this));
	},

    sendAttachment: function(){
        this.sendFile();
    },
    sendFile: function(){
        this.content.mask({
            "style": {
                "opacity": 0.7,
                "background-color": "#999"
            }
        });

        var shareNode = new Element("div", {"styles": this.css.createFolderNode}).inject(this.content);
        shareNode.position({
            relativeTo: this.node,
            position: "center"
        });

        var titleNode = new Element("div", {"styles": this.css.createFolderTitleNode, "text": this.lp.sendFile}).inject(shareNode);
        var inforNode = new Element("div", {"styles": this.css.createFolderInforNode, "text": this.lp.selectSendUser}).inject(shareNode);
        var inputAreaNode = new Element("div", {"styles": this.css.createFolderInputAreaNode}).inject(shareNode);
        var inputNode = new Element("div", {"type": "text", "readonly": true, "styles": this.css.shareFileInputNode}).inject(inputAreaNode);
        //    if (this.controller.selectedAttachments.length==1) inputNode.set("value", this.controller.selectedAttachments[0].data.shareList.join(", "));

        inputNode.addEvent("click", function(){
            MWF.xDesktop.requireApp("Selector", "Person", function(){
                var selector = new MWF.xApplication.Selector.Person(this.node,{
                    //"values": this.selectedItem.data.shareList,
                    "onComplete": function(items){
                        MWF.require("MWF.widget.O2Identity", function(){
                            var names = [];
                            var texts = [];
                            inputNode.empty();
                            items.each(function(item){
                                names.push(item.data.distinguishedName);
                                texts.push(item.data.name);
                                new MWF.widget.O2Person({"name": item.data.distinguishedName}, inputNode, {"style": "xform"})
                            });
                            //inputNode.set("text", texts.join(", "));
                            inputNode.store("texts", texts);
                            inputNode.set("value", names.join(", "));
                        }.bind(this));

                        // var names = [];
                        // items.each(function(item){
                        //     names.push(item.data.distinguishedName);
                        // });
                        // inputNode.set("value", names.join(", "));
                    }.bind(this)
                });
                selector.load();
            }.bind(this));
        }.bind(this));

        var actionNode = new Element("div", {"styles": this.css.createFolderActionNode}).inject(shareNode);
        var cancelButton = new Element("button", {"styles": this.css.createFolderCancelButton, "text": this.lp.cancel}).inject(actionNode);
        var okButton = new Element("button", {"styles": this.css.createFolderOkButton, "text": this.lp.ok}).inject(actionNode);

        cancelButton.addEvent("click", function(){
            this.content.unmask();
            shareNode.destroy();
        }.bind(this));
        okButton.addEvent("click", function(){

            var count = this.controller.selectedAttachments.length;
            var current = 0;
            var callback = function(){
                if (current == count){
                    if (inputNode.get("value")) this.notice(this.lp.fileSendSuccess+inputNode.retrieve("texts", []).join(","), "success", this.content);
                    shareNode.destroy();
                    this.content.unmask();
                }
            };

            this.controller.selectedAttachments.each(function(att){
                if (att.type!="folder"){
                    att.data.editorList = inputNode.get("value").split(/,\s*/g);
                    this.restActions.updateAttachment(att.data.id, att.data, function(json){
                        current++;
                        callback.apply(this);
                    }.bind(this));
                }else{
                    current++;
                    callback.apply(this);
                }
            }.bind(this));

        }.bind(this));
    },

    uploadAttachment: function(e, node){

        folderId = (this.currentFolder && this.currentFolder.data) ? this.currentFolder.data.id : "";
        this.controller.doUploadAttachment(null, this.restActions.action, "addAttachment", {"folder": folderId || "(0)"}, function(){
            if (this.currentFolder){
                this.currentFolder.clickNode();
            }else{
                this.topTreeNode.clickNode();
            }
        }.bind(this));
    },

    replaceAttachment: function(e, node, attachment){
        var fileId = attachment.data.id;

        this.controller.doReplaceAttachment(null, this.restActions.action, "updateAttachmentData", {"id": fileId}, function(){
            if (this.currentFolder){
                this.currentFolder.clickNode();
            }else{
                this.topTreeNode.clickNode();
            }
        }.bind(this));

        //this.replaceUploadFile(attachment);
    },
    // replaceUploadFile: function(attachment){
    //     if (!this.replaceFileAreaNode){
    //         this.replaceFileAreaNode = new Element("div");
    //         var html = "<input name=\"file\" multiple type=\"file\"/>";
    //         this.replaceFileAreaNode.set("html", html);
    //
    //         this.fileReplaceNode = this.replaceFileAreaNode.getFirst();
    //         this.fileReplaceNode.addEvent("change", function(){
    //             var fileId = attachment.data.id;
    //
    //             var files = fileNode.files;
    //             if (files.length){
    //                 var count = files.length;
    //                 var current = 0;
    //                 var callback = function(){
    //                     if (current == count){
    //                         if (this.currentFolder){
    //                             this.currentFolder.clickNode();
    //                         }else{
    //                             this.topTreeNode.clickNode();
    //                         }
    //                     }
    //                 };
    //                 for (var i = 0; i < files.length; i++) {
    //                     var file = files.item(i);
    //
    //                     var formData = new FormData();
    //                     formData.append('file', file);
    //
    //                     this.restActions.updateAttachmentData(fileId, formData, file, function(){
    //                         current++;
    //                         callback.apply(this);
    //                     }.bind(this));
    //                 }
    //             }
    //
    //         }.bind(this));
    //     }
    //     this.fileReplaceNode.set("accept", "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
    //     this.fileReplaceNode.set("multiple", false);
    //
    //     var fileNode = this.replaceFileAreaNode.getFirst();
    //     fileNode.set("accept", "*/*");
    //     fileNode.click();
    // },

	createFolder: function(){
		this.content.mask({
			"style": {
				"opacity": 0.7,
				"background-color": "#999"
			}
		});
		
		var createFolderNode = new Element("div", {"styles": this.css.createFolderNode}).inject(this.content);
		createFolderNode.position({
			relativeTo: this.node,
		    position: "center"
		});
		
		var titleNode = new Element("div", {"styles": this.css.createFolderTitleNode, "text": this.lp.createFolder}).inject(createFolderNode);
		var inforNode = new Element("div", {"styles": this.css.createFolderInforNode, "text": this.lp.inputFolderName}).inject(createFolderNode);
		var inputAreaNode = new Element("div", {"styles": this.css.createFolderInputAreaNode}).inject(createFolderNode);
		var inputNode = new Element("input", {"type": "text", "styles": this.css.createFolderInputNode}).inject(inputAreaNode);
		
		var actionNode = new Element("div", {"styles": this.css.createFolderActionNode}).inject(createFolderNode);
		var cancelButton = new Element("button", {"styles": this.css.createFolderCancelButton, "text": this.lp.cancel}).inject(actionNode);
		var okButton = new Element("button", {"styles": this.css.createFolderOkButton, "text": this.lp.ok}).inject(actionNode);
		
		cancelButton.addEvent("click", function(){
			this.content.unmask();
			createFolderNode.destroy();
		}.bind(this));
		okButton.addEvent("click", function(){
			if (inputNode.get("value")){
				var data = {
					"name": inputNode.get("value"),
					"superior": (this.currentFolder && this.currentFolder.data) ? this.currentFolder.data.id : ""
				};

				this.restActions.saveFolder(data, function(json){
					data.id = json.data.id;
                    this.restActions.getFolder(data.id, function(folderJson){
                        if (this.currentFolder){
                            var json = {"data":[folderJson.data]};
                            this.createTreeNode(json, this.currentFolder);
                            this.currentFolder.clickNode();
                        }else{
                            this.topTreeNode.clickNode();
                        }
                    }.bind(this));
				}.bind(this));
				createFolderNode.destroy();
				this.content.unmask();
			}else{
				this.notice(this.lp.folderNameNotEmpty, "error", createFolderNode);
			}
			
		}.bind(this));
	},

    getAttachmentUrl: function(attachment, callback){
        this.restActions.getFileUrl(attachment.data.id, callback);
    },

	
	loadFolderTreeNode: function(){
		this.folderTreeNode = new Element("div", {
			"styles": this.css.folderTreeNode
		}).inject(this.folderTreeAreaScrollNode);
		
//		MWF.require("MWF.widget.Tree", function(){
			this.folderTree = new MWF.widget.Tree(this.folderTreeNode, {
				"style": "file"
//				"onQueryExpand": function(node){
//					this.loadFolderTree(node);
//				}.bind(this)
			});
			this.folderTree.load();
			var rootData = {
				"expand": false,
				"title": "root",
				"text": "root",
				"action": function(treeNode){
					this.recordHistory(treeNode);
					this.expand(treeNode, function(){
                        this.loadSub(treeNode);
                    }.bind(this));
				}.bind(this),
				"icon": "folder.png"
			};

			this.topTreeNode = this.folderTree.appendChild(rootData);
			//this.topTreeNode.clickNode();
//			this.loadFolderTree();
//		}.bind(this));
	},
	
	createShareTreeNode: function(json){
		json.data.each(function(data){
		    var name = data.name.substring(0, data.name.indexOf("@"));
			var nodeData = {
				"expand": false,
				"title": name+"("+data.count+")",
				"text": name+"("+data.count+")",
				"action": function(treeNode){
					this.loadShareFile(treeNode);
				}.bind(this),
				"icon": "folder.png"
			};
			var treeNode = this.shareTree.appendChild(nodeData);
			treeNode.data = data;
		}.bind(this));
	},
	loadShareTreeNode: function(){
		this.shareTreeNode = new Element("div", {
			"styles": this.css.folderTreeNode
		}).inject(this.shareTreeAreaScrollNode);
		
	//	MWF.require("MWF.widget.Tree", function(){
			this.shareTree = new MWF.widget.Tree(this.shareTreeNode, {
				"style": "file"
			});
			this.shareTree.load();
			
			this.restActions.listShare(function(json){
				this.createShareTreeNode(json);
			//	node.setOperateIcon();
                if (this.shareTree.loadCallback) this.shareTree.loadCallback.apply(this);
			}.bind(this), null, false);
			
//			this.loadFolderTree();
	//	}.bind(this));
	},
	loadShareFile: function(treeNode){
		var person = treeNode.data.name;
		this.restActions.listShareAttachment(person, function(json){
			//this.fileContentAreaNode.empty();
            this.controller.clear();
			json.data.each(function(file){
                this.controller.addAttachment(file);
				//new MWF.xApplication.File.ShareAttachment(file, this);
			}.bind(this));
		}.bind(this));
	},

    createEditorTreeNode: function(json){
        json.data.each(function(data){
            var name = MWF.name.cn(data.name);
            var nodeData = {
                "expand": false,
                "title": name+"("+data.count+")",
                "text": name+"("+data.count+")",
                "action": function(treeNode){
                    this.loadEditorFile(treeNode);
                }.bind(this),
                "icon": "folder.png"
            };
            var treeNode = this.editorTree.appendChild(nodeData);
            treeNode.data = data;
        }.bind(this));
    },
    loadEditorTreeNode: function(){
        this.editorTreeNode = new Element("div", {
            "styles": this.css.folderTreeNode
        }).inject(this.editorTreeAreaScrollNode);

    //    MWF.require("MWF.widget.Tree", function(){
            this.editorTree = new MWF.widget.Tree(this.editorTreeNode, {
                "style": "file"
            });
            this.editorTree.load();

            this.restActions.listEditor(function(json){
                this.createEditorTreeNode(json);
                //	node.setOperateIcon();
                if (this.editorTree.loadCallback) this.editorTree.loadCallback.apply(this);
            }.bind(this), null, false);

//			this.loadFolderTree();
    //    }.bind(this));
    },
    loadEditorFile: function(treeNode){
        var person = treeNode.data.name;
        this.restActions.listEditorAttachment(person, function(json){
            //this.fileContentAreaNode.empty();
            this.controller.clear();
            json.data.each(function(file){
                this.controller.addAttachment(file);
                //new MWF.xApplication.File.ShareAttachment(file, this);
            }.bind(this));
        }.bind(this));
    },
	
	loadFileContentAreaNode: function(){

        this.controller = new MWF.xApplication.File.AttachmentController(this.attachmentContentNode, this, {
            "resize": false,
            "isSizeChange": false,
        })
        this.controller.load();

		//this.fileContentAreaScrollNode = new Element("div", {
		//	"styles": this.css.fileContentAreaScrollNode
		//}).inject(this.attachmentContentNode);
        //
		//this.fileContentAreaScrollNode.addEvent("click", function(){
		//	if (this.selectedItem) this.selectedItem.unSelected();
		//}.bind(this));
        //
		//this.fileContentAreaNode = new Element("div", {
		//	"styles": this.css.fileContentAreaNode
		//}).inject(this.fileContentAreaScrollNode);
        //
		//this.initDropUpLoad();
	},
	
	createTreeNode: function(json, topNode){
		json.data.each(function(data){
			var nodeData = {
				"expand": false,
				"title": data.name,
				"text": data.name,
				"action": function(treeNode){
					this.recordHistory(treeNode);
					this.expand(treeNode, function(){
                        this.loadSub(treeNode);
                    }.bind(this));
				}.bind(this),
				"icon": "folder.png"
			};
			var treeNode = topNode.appendChild(nodeData);
			treeNode.data = data;
		}.bind(this));
	},
	loadFolderTree: function(node, callback){
		if (!node.loaded){
            node.loaded = true;
			if (node.data){
				this.restActions.listFolder(node.data.id, function(json){
					this.createTreeNode(json, node);
					node.setOperateIcon();
                    if (callback) callback();
				}.bind(this), null, false);
			}else{
				this.restActions.listTopFolder(function(json){
					this.createTreeNode(json, node);
					node.setOperateIcon();
                    if (callback) callback();
				}.bind(this), null, false);
			}
		}else{
            if (callback) callback();
        }
	},
	
	setContentHeight: function(node){
		var size = this.node.getSize();
		var tSize = this.topNode.getSize();
	//	var toolSize = this.toolsNode.getSize();
		
		var mtt = this.topNode.getStyle("margin-top").toFloat();
		var mbt = this.topNode.getStyle("margin-bottom").toFloat();
		
	//	var mttool = this.toolsNode.getStyle("margin-top").toFloat();
	//	var mbtool = this.toolsNode.getStyle("margin-bottom").toFloat();
		
		var mtc = this.fileContentNode.getStyle("margin-top").toFloat();
		var mbc = this.fileContentNode.getStyle("margin-bottom").toFloat();		
		var height = size.y-tSize.y-mtt-mbt-mtc-mbc;
		this.fileContentNode.setStyle("height", height);

        this.attachmentContentNode.setStyle("height", height);

        var attTopSzie = this.controller.topNode.getSize();
        var y = height-attTopSzie.y;
        this.controller.contentScrollNode.setStyle("height", ""+y+"px");
		
//		this.fileContentAreaNode.setStyle("min-height", height);
		
		var tabSize = this.treeTab.tabNodeContainer.getSize();
		var height = height-tabSize.y;
		
		this.folderTreeAreaScrollNode.setStyle("height", height);
		this.shareTreeAreaScrollNode.setStyle("height", height);
        this.editorTreeAreaScrollNode.setStyle("height", height);
	},
	
	expand: function(treeNode, callback){
		if (!treeNode.options.expand){
            this.loadFolderTree(treeNode, function(){
                if (callback) callback();
            }.bind(this));
            this.folderTree.expand(treeNode);
            treeNode.options.expand = true;
            treeNode.setOperateIcon();
		}else{
            if (callback) callback();
        }
	},
	checkHistory: function(){
		if (this.history.length>1){
			if (this.currentHistory>0){
				this.enabledLeftNode();
			}else{
				this.disabledLeftNode();
			}
			
			if (this.currentHistory<this.history.length-1){
				this.enabledRightNode();
			}else{
				this.disabledRightNode();
			}
		}else{
			this.disabledLeftNode();
			this.disabledRightNode();
		}
	},
	enabledLeftNode: function(){
		this.leftNode.setStyle("background-image", "url("+"../x_component_File/$Main/default/icon/left_enabled.png)");
	},
	enabledRightNode: function(){
		this.rightNode.setStyle("background-image", "url("+"../x_component_File/$Main/default/icon/right_enabled.png)");
	},
	disabledLeftNode: function(){
		this.leftNode.setStyle("background-image", "url("+"../x_component_File/$Main/default/icon/left.png)");
	},
	disabledRightNode: function(){
		this.rightNode.setStyle("background-image", "url("+"../x_component_File/$Main/default/icon/right.png)");
	},
	
	leftPath: function(){
		if (this.currentHistory>0){
			this.currentHistory--;
			var treeNode = this.history[this.currentHistory];
			if (treeNode){
				treeNode.selectNode();
				this.loadSub(treeNode);
				this.checkHistory();
			}
		}
	},
	rightPath: function(){
		if (this.currentHistory<this.history.length-1){
			this.currentHistory++;
			var treeNode = this.history[this.currentHistory];
			if (treeNode){
				treeNode.selectNode();
				this.loadSub(treeNode);
				this.checkHistory();
			}
		}
	},
	
	recordHistory: function(treeNode){
		if (!this.history.length || this.history[this.history.length-1]!=treeNode){
			this.history.push(treeNode);
			this.currentHistory = this.history.length-1;
			this.checkHistory();
		}
	},
	setPathNode: function(treeNode){
		this.pathNode.empty();
		var paths = [];
		var tmpNode = treeNode;
		while (tmpNode){
			paths.unshift(tmpNode);
			tmpNode = tmpNode.parentNode;
		};
		var _self = this;
		paths.each(function(node, index){
			this.expand(node);
			var pathNode = new Element("div",{
				"styles": this.css.pathItemNode,
				"text": (node.data) ? node.data.name : "root",
				"events": {
					"mouseover": function(){
						this.setStyles(_self.css.pathItemNode_over);
					},
					"mouseout": function(){
						this.setStyles(_self.css.pathItemNode);
					},
					"click": function(){
						node.clickNode();
					}
				}
			}).inject(this.pathNode);
			if (index<paths.length-1) new Element("div",{"styles": this.css.pathItemIconNode}).inject(this.pathNode);
		}.bind(this));
	},
	loadSub: function(treeNode){
		this.setPathNode(treeNode);
		
		//this.fileContentAreaNode.empty();
        this.controller.clear();
		
		treeNode.children.each(function(node){
            var folder = this.controller.addAttachmentFolder(node.data);
            folder.treeNode = node;
			//var folder = new MWF.xApplication.File.Folder(node.data, this);
			//folder.treeNode = node;
		}.bind(this));
		
		this.currentFolder = treeNode;
		if (treeNode.data){
			this.restActions.listAttachment(treeNode.data.id, function(json){
				json.data.each(function(file){

                    this.controller.addAttachment(file);

					//new MWF.xApplication.File.Attachment(file, this);
				}.bind(this));
			}.bind(this));
		}else{
			this.restActions.listAttachmentTop(function(json){
				json.data.each(function(file){
                    this.controller.addAttachment(file);
					//new MWF.xApplication.File.Attachment(file, this);
				}.bind(this));
			}.bind(this));
		}
	},
	uploadFiles: function(files){
		if (!this.uploadFileList) this.uploadFileList = []; 
		if (!this.uploadFileTotalSize) this.uploadFileTotalSize = 0;
		if (!files || !files.length) return;
 
		for (var i = 0; i < files.length && i < 5; i++) {
			this.uploadFileList.push(files[i]);
			this.uploadFileTotalSize += files[i].size;
		}
		this.uploadNext();
	},
	uploadNext: function(){
		if (this.uploadFileList.length) {
			var nextFile = this.uploadFileList.shift();
			if (nextFile){
				this.uploadFile(nextFile);
				this.uploadNext();
			}
		}
	},
	uploadFile: function(file){
		var formData = new FormData();
		formData.append('file', file);
		formData.append('name', file.name);
		formData.append('folder', (this.currentFolder && this.currentFolder.data) ? this.currentFolder.data.id : "");
	//	xhr.send(formData);

		this.restActions.addAttachment((this.currentFolder && this.currentFolder.data) ? this.currentFolder.data.id : "(0)", formData, file, function(){
			if (!this.uploadFileList.length){
				if (this.currentFolder){
					this.currentFolder.clickNode();
				}else{
					this.topTreeNode.clickNode();
				}
			}
		}.bind(this));

	},
	initDropUpLoad: function(){
		this.fileContentAreaNode.addEventListener('drop', function(e){
			e.stopPropagation();
		    e.preventDefault();

		    if (this.dropUploadInforNode){
	    		this.dropUploadInforNode.destroy();
	    		this.dropUploadInforNode = null;
	    	}
		    if (e.dataTransfer.types.length<2){
		    	this.uploadFiles(e.dataTransfer.files);
		    } 
		}.bind(this), false);
		
		this.fileContentAreaNode.addEventListener('dragover', function(e){
			if (e.dataTransfer.types.length<2){
				e.stopPropagation();
				e.preventDefault();
				
				if (!this.dropUploadInforNode){
					this.dropUploadInforNode = new Element("div", {
						"styles": this.css.dropUploadInforNode,
						"text": this.lp.dropUpload
					}).inject(this.node);
					this.dropUploadInforNode.position({
						relativeTo: this.fileContentAreaNode,
					    position: "centerBottom",
					    edge: "centerBottom",
					    offset: {"x": 0, "y": -100}
					});
					this.dropUploadInforNode.fade(0.7);
				}
			}
			
		}.bind(this), false);
		
		this.fileContentAreaNode.addEventListener('dragleave', function(e){
			e.stopPropagation();
		    e.preventDefault();
		   
		    if (this.dropUploadInforNode) window.setTimeout(function(){
		    	if (this.dropUploadInforNode){
		    		this.dropUploadInforNode.destroy();
		    		this.dropUploadInforNode = null;
		    	}
		    }.bind(this), 2000);
		}.bind(this), false);
		
		
	}
});
MWF.xApplication.File.Attachment = new Class({
	initialize: function(data, file){
		this.data = data;
		this.file = file;
		this.type = "file";
		
		this.extension = this.data.name.substr(this.data.name.lastIndexOf(".")+1, this.data.name.length);
		
		this.load();
	},
	getIcon: function(){
		var iconName = this.file.icons[this.extension] || this.file.icons.unknow;
		return "../x_component_File/$Main/default/file/"+iconName;
	},
	load: function(){
		this.node = new Element("div", {"styles": this.file.css.attachmentNode});
		this.iconNode = new Element("div", {"styles": this.file.css.attachmentIconNode}).inject(this.node);
		this.imgNode = new Element("div", {
			"styles": this.file.css.attachmentImgNode
		}).inject(this.iconNode);
		this.imgNode.setStyle("background-image", "url("+this.getIcon()+")");
		
		this.textNode = new Element("div", {
			"styles": this.file.css.attachmentTextNode,
			"text": this.data.name,
			"title": this.data.name
		}).inject(this.node);
		
		if (this.data.shareList){
			if (this.data.shareList.length){

				this.shareIconNode = new Element("div", {"styles": this.file.css.shareIconNode}).inject(this.node);
//				this.shareIconNode.position({
//					relativeTo: this.node,
//				    position: "centerBottom",
//				    edge: "centerBottom"
//				});
			}
		}
		
		
		this.node.inject(this.file.fileContentAreaNode);
		
		this.setEvents();
	},
	setEvents: function(){
		this.node.addEvents({
			"mouseover": function(){
				if (!this.isSelected) this.node.setStyles(this.file.css.attachmentNode_over);
			}.bind(this),
			"mouseout": function(){
				if (!this.isSelected) this.node.setStyles(this.file.css.attachmentNode);
			}.bind(this),
			"click": function(e){
				this.selected();
				e.stop();
				e.stopPropagation();
			}.bind(this),
			"dblclick": function(){
				this.open();
			}.bind(this)
		});
		this.node.makeLnk({
			"par": {
				"icon": this.getIcon(),
				"title": this.data.name,
				"par": "FileOpen#{\"id\": \""+this.data.id+"\", \"type\": \"file\"}"
			}
		});
	},
	selected: function(){
		if (this.file.selectedItem) this.file.selectedItem.unSelected();
		this.isSelected = true;
		this.node.setStyles(this.file.css.attachmentNode_select);
		this.file.selectedItem = this;
	},
	unSelected: function(){
		this.isSelected = false;
		this.node.setStyles(this.file.css.attachmentNode);
		this.file.selectedItem = null;
	},
	open: function(){
		this.file.restActions.getAttachment(this.data.id);
	}
});

MWF.xApplication.File.Folder = new Class({
	initialize: function(data, file){
		this.data = data;
		this.file = file;
		this.type = "folder";
		
		this.load();
	},
	getIcon: function(){
		return "../x_component_File/$Main/default/file/folder.png";
	},
	load: function(){
		this.node = new Element("div", {"styles": this.file.css.attachmentNode});
	//	this.node.addEvents();
		
		this.iconNode = new Element("div", {"styles": this.file.css.attachmentIconNode}).inject(this.node);
		this.imgNode = new Element("img", {
			"styles": this.file.css.attachmentImgNode,
			"src": this.getIcon(),
			"border": "0"
		}).inject(this.iconNode);
		
		this.textNode = new Element("div", {
			"styles": this.file.css.attachmentTextNode,
			"text": this.data.name
		}).inject(this.node);
		
		
		this.node.inject(this.file.fileContentAreaNode);
		
		this.setEvents();
	},
	setEvents: function(){
		this.node.addEvents({
			"mouseover": function(){
				if (!this.isSelected) this.node.setStyles(this.file.css.attachmentNode_over);
			}.bind(this),
			"mouseout": function(){
				if (!this.isSelected) this.node.setStyles(this.file.css.attachmentNode);
			}.bind(this),
			"click": function(e){
				this.selected();
				e.stop();
				e.stopPropagation();
			}.bind(this),
			"dblclick": function(){
				this.open();
			}.bind(this)
		});
	},
	selected: function(){
		if (this.file.selectedItem) this.file.selectedItem.unSelected();
		this.isSelected = true;
		this.node.setStyles(this.file.css.attachmentNode_select);
		this.file.selectedItem = this;
	},
	unSelected: function(){
		this.isSelected = false;
		this.node.setStyles(this.file.css.attachmentNode);
		this.file.selectedItem = null;
	},
	open: function(){
		this.treeNode.clickNode();
	}
});
MWF.xApplication.File.ShareAttachment = new Class({
	Extends: MWF.xApplication.File.Attachment,
	initialize: function(data, file){
		this.data = data;
		this.file = file;
		this.type = "share";
		
		this.extension = this.data.name.substr(this.data.name.lastIndexOf(".")+1, this.data.name.length);
		
		this.load();
	}
});
