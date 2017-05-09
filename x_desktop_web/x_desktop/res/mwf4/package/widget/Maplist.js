MWF.widget = MWF.widget || {};
MWF.widget.Maplist = new Class({
	Implements: [Options, Events],
	Extends: MWF.widget.Common,
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
		
		this.path = MWF.defaultPath+"/widget/$Maplist/";
		this.cssPath = MWF.defaultPath+"/widget/$Maplist/"+this.options.style+"/css.wcss";
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
		if (!display!="none") this.expand();
			
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
			
			MWF.require("MWF.widget.JsonParse", function(){
				this.json = new MWF.widget.JsonParse(this.toJson(), null, this.codeTextNode);
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
				this.addNewItem(null, "top");
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
				this.addNewItem(null, "bottom");
			}.bind(this)
		});
		
		
		this.loadContent(obj);
	},
	loadContent: function(obj){
		Object.each(obj, function(value, key){
			var item = new MWF.widget.Maplist.Item(this, value, key);
			item.load();
			item.itemNode.inject(this.contentItemsNode);
			this.items.push(item);
		}, this);
	},
	addNewItem: function(item, where){
		if (this.notAddItem){
			this.notAddItem = false;
		}else{
			var newItem = new MWF.widget.Maplist.Item(this, "", "");
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
	}
	
});

MWF.widget.Maplist.Item = new Class({
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
				this.maplist.addNewItem(this);
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
			"styles": this.maplist.css.editInputDiv,
		});
		var input = new Element("input", {
			"styles": this.maplist.css.editInput,
			"type": "text",
			"value": text
		}).inject(div);
		var w = MWF.getTextSize(text+"a").x;
		input.setStyle("width", w);
		div.setStyle("width", w);

		div.inject(node);
		input.select();
		
		input.addEvents({
			"keydown": function(e){
				var x = MWF.getTextSize(input.get("value")+"a").x;
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
				this.iconNode.title = MWF.LP.process.repetitions;
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
			this.maplist.addNewItem(this);
		}else{
			this.maplist.notAddItem = true;
		}
		
		return true;
	}
	
});




