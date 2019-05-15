o2.widget = o2.widget || {};
o2.widget.Maplist = new Class({
	Implements: [Options, Events],
	Extends: o2.widget.Common,
	options: {
		"title": "maplist",
		"style": "default",
		
		"collapse": false,
		"isAdd": true,
		"isDelete": true,
		"isModify": true
	},
	initialize: function(node, options){
		this.setOptions(options);
		
		this.node = $(node);
		this.container = new Element("div");
		
		this.path = o2.session.path+"/widget/$Maplist/";
		this.cssPath = o2.session.path+"/widget/$Maplist/"+this.options.style+"/css.wcss";
		this._loadCss();
		
		this.items = [];
	},
	load: function(obj){
		if (this.fireEvent("queryLoad")){
			this.container.set("styles", this.css.container);
			this.container.inject(this.node);
						
			this.createTitleNode();
			
			this.createContent(obj);
			
			this.fireEvent("postLoad");
			
			if (this.options.collapse){
				var height = this.contentNode.getSize().y.toInt()-2;
				this.contentNode.store("showHeight", height);
				this.contentNode.setStyles({
					"display": "none",
					"height": "0px"
				});
			}
		}
	},
	createTitleNode: function(){
		this.titleNode = new Element("div", {
			"styles": this.css.titleNode
		}).inject(this.container);
		
		this.titleActionNode = new Element("div", {
			"styles": this.css.titleActionNode
		}).inject(this.titleNode);

		this.titleTextNode = new Element("div", {
			"styles": this.css.titleTextNode,
			"text": this.options.title
		}).inject(this.titleNode);
		this.titleTextNode.addEvent("click", function(){
			this.expandOrCollapse();
		}.bind(this));

		this.createTitleActions();
	},
	createTitleActions: function(){
		this.actionNode = new Element("div", {
			"styles": this.css.actionNode
		}).inject(this.titleActionNode);
		this.actionNode.setStyle("background-image", "url("+this.path+this.options.style+"/icon/code_empty.png)");
		this.actionNode.addEvent("click", function(e){
            this.showCode();
            e.stopPropagation();
        }.bind(this));
	},
	showCode: function(){
		var display = this.contentNode.getStyle("display");
		if (display=="none") this.expand();
			
		if (!this.isShowCode){
			this.codeContentNode = new Element("div", {
				"styles": this.css.contentNode
			}).inject(this.container);
			this.codeTextNode = new Element("textarea", {
				"styles": this.css.codeTextNode,
				"events": {
					"blur": function(){
						this.showCode();
						this.fireEvent("change");
					}.bind(this)
				}
			}).inject(this.codeContentNode);
			var size = this.contentNode.getSize();
			this.codeTextNode.setStyle("height", ""+size.y+"px");
			
			o2.require("o2.widget.JsonParse", function(){
				this.json = new o2.widget.JsonParse(this.toJson(), null, this.codeTextNode);
				this.json.load();
			}.bind(this));
			
			this.contentNode.setStyle("display", "none");
			this.titleActionNode.setStyles({
				"border": "1px solid #999",
				"background": "#FFF"
			});
			this.actionNode.setStyle("background-image", "url("+this.path+this.options.style+"/icon/code.png)");
			this.isShowCode = true;
		}else{
			this.contentItemsNode.empty();
			this.loadContent(JSON.decode(this.codeTextNode.get("value")));
			this.fireEvent("change");
			
			this.codeContentNode.destroy();
			this.codeContentNode = null;
			this.codeTextNode = null;
			
			this.contentNode.setStyle("display", "block");
			this.titleActionNode.setStyles({
				"border": "1px solid #EEE",
				"background": "transparent"
			});
			this.actionNode.setStyle("background-image", "url("+this.path+this.options.style+"/icon/code_empty.png)");
			this.isShowCode = false;
		}
	},
    reload: function(json){
        if (!this.isShowCode){
            this.contentItemsNode.empty();
            this.loadContent(json);
        }else{
            this.contentItemsNode.empty();
            this.loadContent(json);

            this.codeContentNode.destroy();
            this.codeContentNode = null;
            this.codeTextNode = null;

            this.contentNode.setStyle("display", "block");
            this.titleActionNode.setStyles({
                "border": "1px solid #EEE",
                "background": "transparent"
            });
            this.actionNode.setStyle("background-image", "url("+this.path+this.options.style+"/icon/code_empty.png)");
            this.isShowCode = false;
        }
    },
	addAction: function(icon, action){
		var actionNode = new Element("div", {
			"styles": this.css.actionNode
		}).inject(this.titleActionNode);
		actionNode.setStyle("background-image", "url("+this.path+this.options.style+"/icon/"+icon+")");
		actionNode.addEvent("click", action);
	},
	
	expandOrCollapse: function(){
		var display = this.contentNode.getStyle("display");
		if (display!="none"){
			this.collapse();
		}else{
			this.expand();
		}
	},
	collapse: function(){
		if (!this.morph){
			this.morph = new Fx.Morph(this.contentNode, {duration: 200});
		}
		var height = this.contentNode.getSize().y.toInt()-2;
		this.contentNode.store("showHeight", height);
		this.morph.start({"height": [height,0]}).chain(function(){
			this.contentNode.setStyle("display", "none");
		}.bind(this));
	},
	expand: function(){
		if (!this.morph){
			this.morph = new Fx.Morph(this.contentNode, {duration: 200});
		}
		var height = this.contentNode.retrieve("showHeight");
		
		this.contentNode.setStyle("display", "block");
		this.morph.start({"height": [0, height]}).chain(function(){
			this.contentNode.setStyle("height", "auto");
		}.bind(this));
	},

	createContent: function(obj){
		this.contentNode = new Element("div", {
			"styles": this.css.contentNode
		}).inject(this.container);
		
		this.contentStartNode = new Element("div", {
			"styles": this.css.contentStartNode,
			"text": "{"
		}).inject(this.contentNode);
		this.contentStartNode.addEvents({
			"mouseover": function(e){
				e.target.setStyles(this.css.contentStartNodeOver);
			}.bind(this),
			"mouseout": function(e){
				e.target.setStyles(this.css.contentStartNode);
			}.bind(this),
			"click": function(e){
				if (this.options.isAdd) this.addNewItem(null, "top");
			}.bind(this)
		});
		
		this.contentItemsNode = new Element("div", {
			"styles": this.css.contentItemsNode
		}).inject(this.contentNode);
		
		this.contentEndNode = new Element("div", {
			"styles": this.css.contentEndNode,
			"text": "}"
		}).inject(this.contentNode);
		this.contentEndNode.addEvents({
			"mouseover": function(e){
				e.target.setStyles(this.css.contentEndNodeOver);
			}.bind(this),
			"mouseout": function(e){
				e.target.setStyles(this.css.contentEndNode);
			}.bind(this),
			"click": function(e){
                if (this.options.isAdd) this.addNewItem(null, "bottom");
			}.bind(this)
		});
		this.loadContent(obj);
	},
    createItem: function(value, key){
        return new o2.widget.Maplist.Item(this, value, key);
    },
	loadContent: function(obj){
		Object.each(obj, function(value, key){
			var item = this.createItem(value, key);
			//new o2.widget.Maplist.Item(this, value, key)
			item.load();
			item.itemNode.inject(this.contentItemsNode);
			this.items.push(item);
		}, this);
	},
	addNewItem: function(item, where){
		if (this.notAddItem){
			this.notAddItem = false;
		}else{
            var newItem = this.createItem("", "");
			//var newItem = new o2.widget.Maplist.Item(this, "", "");
			newItem.load();
			if (item){
				newItem.itemNode.inject(item.itemNode, "after");
			}else{
				newItem.itemNode.inject(this.contentItemsNode, where);
			}
			newItem.isNewItem = true;
			newItem.editKey();
			
			this.items.push(newItem);
		}
	},
	deleteItem: function(item){
		var key = item.key;
		this.notAddItem = false;
		this.items.erase(item);
		item.itemNode.destroy();
		delete item;
		this.fireEvent("delete", [key]);
		this.fireEvent("change");
	},
	toJson: function(){
		var json = {};
		this.items.each(function(item){
			if (item.key){
				json[item.key] = item.value;
			}
		});
		return json;
	},
    getValue: function(){
		return this.toJson();
	}
	
});

o2.widget.Maplist.Item = new Class({
	initialize: function(maplist, value, key){
		this.maplist = maplist;
		this.key = key;
		this.value = value;
	},
	load: function(){
		this.creatItemNode();
		this.setItemEvents();
	},
	creatItemNode: function(){
		this.itemNode = new Element("div", {
			"styles": this.maplist.css.contentItemNode
		});
		
		this.iconNode = new Element("div", {
			"styles": this.maplist.css.contentItemIconNode,
			"events":{
				"mouseover": function(e){e.stopPropagation();},
				"mouseout": function(e){e.stopPropagation();}
			}
		}).inject(this.itemNode);
		
		this.keyNode = new Element("span", {
			"styles": this.maplist.css.contentItemKeyNode,
			"text": this.key
		}).inject(this.itemNode);
		this.colonNode = new Element("span", {
			"styles": this.maplist.css.contentItemColonNode,
			"text": ":"
		}).inject(this.itemNode);
		this.valueNode = new Element("span", {
			"styles": this.maplist.css.contentItemValueNode,
			"text": this.value
		}).inject(this.itemNode);
		
	},
	
	setItemEvents: function(){
		this.itemNode.addEvents({
			"mouseover": function(e){
				e.target.setStyles(this.maplist.css.contentItemNodeOver);
			}.bind(this),
			"mouseout": function(e){
				e.target.setStyles(this.maplist.css.contentItemNode);
			}.bind(this),
			"click": function(e){
                if (this.maplist.options.isAdd) this.maplist.addNewItem(this);
			}.bind(this)
		});
		
		this.keyNodeClick = function(e){
			if (this.maplist.options.isModify) this.editKey(this.keyNode);
			e.stopPropagation();
		}.bind(this);
		this.keyNode.addEvent("click", this.keyNodeClick);
		
		this.valueNodeClick = function(e){
			if (this.maplist.options.isModify) this.editValue();
			e.stopPropagation();
		}.bind(this);
		this.valueNode.addEvent("click", this.valueNodeClick);
	},
	
	editKey: function(){
		this.editItem(this.keyNode, function(flag){
			if (flag) this.keyNode.addEvent("click", this.keyNodeClick);
		}.bind(this));
		this.keyNode.removeEvent("click", this.keyNodeClick);
	},
	editValue: function(){
		this.editItem(this.valueNode, function(flag){
			if (flag) this.valueNode.addEvent("click", this.valueNodeClick);
		}.bind(this));
		this.valueNode.removeEvent("click", this.valueNodeClick);
	},
	
	
	editItem: function(node, okCallBack){
		var text = node.get("text");
		node.set("html", "");
		
		var div = new Element("div", {
			"styles": this.maplist.css.editInputDiv
		});
		var input = new Element("input", {
			"styles": this.maplist.css.editInput,
			"type": "text",
			"value": text
		}).inject(div);
		var w = o2.getTextSize(text+"a").x;
		input.setStyle("width", w);
		div.setStyle("width", w);

		div.inject(node);
		input.select();
		
		input.addEvents({
			"keydown": function(e){
				var x = o2.getTextSize(input.get("value")+"a").x;
				e.target.setStyle("width", x);
				e.target.getParent().setStyle("width", x);
				if (e.code==13){
					this.isEnterKey = true;
					e.target.blur();
				}
                e.stopPropagation();
			}.bind(this),
			"blur": function(e){
				var flag = this.editItemComplate(node, e.target);
				if (okCallBack) okCallBack(flag);
			}.bind(this),
			"click": function(e){
				e.stopPropagation();
			}.bind(this)
		});
		
	},
	
	editItemComplate: function(node, input){
		var text = input.get("value");
		if (node == this.keyNode){
			if (!text){
				this.maplist.deleteItem(this);
			}
			
			var flag = true;
			this.maplist.items.each(function(item){
				if (item.key == text){
					if (item != this) flag = false;
				}
			}.bind(this));
			
			if (flag){
				this.key = text;
				this.editValue();
				this.maplist.notAddItem = true;
			}else{
				this.iconNode.setStyle("background", "url("+this.maplist.path+this.maplist.options.style+"/icon/error.png) center center no-repeat");
				this.iconNode.title = o2.LP.process.repetitions;
				input.select();
				return false;
			}
		}
		
		var addNewItem = false;
		if (node == this.valueNode){
			this.value = text;
			if (this.isEnterKey){
				if (this.isNewItem){
					addNewItem = true;
				}
				this.editOkAddNewItem = false;
			}
			this.isNewItem = false;
		}
		node.set("html", text);
		this.iconNode.setStyle("background", "transparent ");
		this.iconNode.title = "";

		this.maplist.fireEvent("change");
		
		if (addNewItem){
			this.maplist.notAddItem = false;
            if (this.maplist.options.isAdd) this.maplist.addNewItem(this);
		}else{
			this.maplist.notAddItem = true;
		}
		
		return true;
	}
	
});



o2.widget.Maplist.Style = new Class({
    Implements: [Options, Events],
    Extends: o2.widget.Maplist,
    createItem: function(value, key){
        return new o2.widget.Maplist.Style.Item(this, value, key);
    }
});

o2.widget.Maplist.Style.Item = new Class({
    Extends: o2.widget.Maplist.Item,
    imgKeys: ["background", "background-image"],
    creatItemNode: function(){
        this.itemNode = new Element("div", {
            "styles": this.maplist.css.contentItemNode
        });

        this.iconNode = new Element("div", {
            "styles": this.maplist.css.contentItemIconNode,
            "events":{
                "mouseover": function(e){e.stopPropagation();},
                "mouseout": function(e){e.stopPropagation();}
            }
        }).inject(this.itemNode);

        this.keyNode = new Element("span", {
            "styles": this.maplist.css.contentItemKeyNode,
            "text": this.key
        }).inject(this.itemNode);
        this.colonNode = new Element("span", {
            "styles": this.maplist.css.contentItemColonNode,
            "text": ":"
        }).inject(this.itemNode);

        if (this.imgKeys.indexOf(this.key)==-1){
            this.valueNode = new Element("span", {
                "styles": this.maplist.css.contentItemValueNode,
                "text": this.value
            }).inject(this.itemNode);
        }else{
            this.valueNode = new Element("span", {
                "styles": this.maplist.css.contentItemImgValueNode,
                "text": "image"
            }).inject(this.itemNode);
        }
    },
    setItemEvents: function(){
        this.itemNode.addEvents({
            "mouseover": function(e){
                e.target.setStyles(this.maplist.css.contentItemNodeOver);
            }.bind(this),
            "mouseout": function(e){
                e.target.setStyles(this.maplist.css.contentItemNode);
            }.bind(this),
            "click": function(e){
                if (this.maplist.options.isAdd) this.maplist.addNewItem(this);
            }.bind(this)
        });

        this.keyNodeClick = function(e){
            if (this.maplist.options.isModify) this.editKey(this.keyNode);
            e.stopPropagation();
        }.bind(this);
        this.keyNode.addEvent("click", this.keyNodeClick);

        if (this.imgKeys.indexOf(this.key)==-1){
            this.valueNodeClick = function(e){
                if (this.maplist.options.isModify) this.editValue();
                e.stopPropagation();
            }.bind(this);
            this.valueNode.addEvent("click", this.valueNodeClick);
        }else{
            this.valueNode.addEvent("click", this.valueNodeEditImage.bind(this));
        }
    },
    valueNodeEditImage: function(e){
        var p = this.valueNode.getPosition(this.valueNode.getOffsetParent());
        var size = this.maplist.app.content.getSize();
        var width = 500;
        var height = 200;
        if (p.y+height>size.y) p.y = size.y-height-10;
        if (p.y<0) p.y = 10;
        if (p.x+width>size.x) p.x = size.x-width-10;
        if (p.x<0) p.x = 10;

        var _self = this;
        o2.require("o2.xDesktop.Dialog", function(){
            var dlg = new o2.xDesktop.Dialog({
                "title": this.maplist.options.title+" - "+this.key,
                "style": "settingStyle",
                "top": p.y,
                "left": p.x,
                "fromTop":p.y,
                "fromLeft": p.x,
                "width": width,
                "height": height,
                "html": "",
                "maskNode": this.maplist.app.content,
                "container": this.maplist.app.content,
                "buttonList": [
                    {
                        "text": o2.LP.widget.ok,
                        "action": function(){
                            _self.saveImage(this);
                        }
                    },
                    {
                        "text": o2.LP.widget.cancel,
                        "action": function(){this.close();}
                    }
                ]
            });
            dlg.show();
            this.createImageContent(dlg);
        }.bind(this));
        e.stopPropagation();
    },
    saveImage: function(dlg){
        this.textValue = this.valueInput.get("value");
        this.value = (this.imgUrl) ? "url("+this.imgUrl+") " : "";
        this.value += this.textValue;

        this.maplist.fireEvent("change");

        dlg.close();
    },
    createImageContent: function(dlg){
        var content = new Element("div", {"styles": this.maplist.css.imageEditContent}).inject(dlg.content);
        var imgArea = new Element("div", {"styles": this.maplist.css.imageEditArea}).inject(content);
        this.imgPreviewArea = new Element("div", {"styles": this.maplist.css.imageEditPreview}).inject(imgArea);

        var actionArea = new Element("div", {"styles": this.maplist.css.imageEditActionArea}).inject(imgArea);
        this.uploadImageAction = new Element("div", {"styles": this.maplist.css.imageEditActionNode, "text": o2.LP.widget.uploadImg}).inject(actionArea);
        this.clearImageAction = new Element("div", {"styles": this.maplist.css.imageEditActionNode, "text": o2.LP.widget.clearImg}).inject(actionArea);

        var valueArea = new Element("div", {"styles": this.maplist.css.valueEditArea}).inject(content);
        this.valueInput = new Element("input", {"styles": this.maplist.css.valueEditInput}).inject(valueArea);

        var regexp = /(url\().+?\)/gi;
        var r = this.value.match(regexp);

        this.imgUrl = "";
        this.textValue = this.value;
        if(r && r.length){
            this.imgUrl = r[0].substr(0,r[0].lastIndexOf(")"));
            this.imgUrl = this.imgUrl.substr(this.imgUrl.indexOf("(")+1,this.imgUrl.length);
            this.textValue = this.value.replace(/(url\().+?\)/i,"").trim();
        }
        this.valueInput.set("value", this.textValue);
        this.showImg();

        this.uploadImageAction.addEvent("click", function(e){
            this.uploadImage();
        }.bind(this));

        this.clearImageAction.addEvent("click", function(e){
            var _self = this;
            this.maplist.app.confirm("infor", e, o2.LP.widget.clearImg_confirmTitle, o2.LP.widget.clearImg_confirm, 300, 100, function(){
                _self.clearImage(this);
            }, function(){
                this.close();
            })
        }.bind(this));
    },
    uploadImage: function(){
        if (!this.fileNode){
            this.fileNode = new Element("input.file", {
                "styles" : {"display":"none"},
                "type" : "file",
                "accept":"images/*"
            }).inject(this.itemNode);
            this.fileNode.addEvent("change", function(e){
                var file=this.fileNode.files[0];
                this.fileType = file.type;
                this.fileName = file.name;
                this.fileSize = file.size;
                this.loadImage(file);
            }.bind(this));
        }
        this.fileNode.click();


    },
    loadImage: function(file){
        var reader=new FileReader();
        reader.onload=function(){
            this.imgUrl = reader.result;
            this.showImg();
        }.bind(this);
        reader.readAsDataURL(file);
    },

    showImg: function(){
        this.imgPreviewArea.empty();
        if (this.imgUrl){
            this.img = new Element("img", {"styles": this.maplist.css.imageEditPreviewImg, "src": this.imgUrl}).inject(this.imgPreviewArea);
            var size = this.img.getSize();
            var i = Math.max(size.x, size.y);
            var x = 80/i;
            var h = size.y*x;
            var w = size.x*x;
            this.img.setStyles({"width": ""+w+"px", "height": ""+h+"px"});
        }
    },
    clearImage: function(dlg){
        this.imgUrl = "";
        this.showImg();
        dlg.close();
    },
    editItemComplate: function(node, input){
        var text = input.get("value");
        if (node == this.keyNode){
            if (!text){
                this.maplist.deleteItem(this);
            }

            var flag = true;
            this.maplist.items.each(function(item){
                if (item.key == text){
                    if (item != this) flag = false;
                }
            }.bind(this));

            if (flag){
                this.key = text;
                if (this.imgKeys.indexOf(this.key)==-1) this.editValue();
                this.maplist.notAddItem = true;
            }else{
                this.iconNode.setStyle("background", "url("+this.maplist.path+this.maplist.options.style+"/icon/error.png) center center no-repeat");
                this.iconNode.title = o2.LP.process.repetitions;
                input.select();
                return false;
            }
        }

        var addNewItem = false;
        if (node == this.valueNode){
            this.value = text;
            if (this.isEnterKey){
                if (this.isNewItem){
                    addNewItem = true;
                }
                this.editOkAddNewItem = false;
            }
            this.isNewItem = false;
        }
        node.set("html", text);
        this.iconNode.setStyle("background", "transparent ");
        this.iconNode.title = "";

        this.maplist.fireEvent("change");

        if (addNewItem){
            this.maplist.notAddItem = false;
            if (this.maplist.options.isAdd) this.maplist.addNewItem(this);
        }else{
            this.maplist.notAddItem = true;
        }

        return true;
    }
});
