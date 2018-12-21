MWF.widget = MWF.widget || {};
MWF.widget.Arraylist = new Class({
	Implements: [Options, Events],
	Extends: MWF.widget.Common,
	options: {
		"title": "arraylist",
		"style": "default",
		
		"isAdd": true,
		"isDelete": true,
		"isModify": true
	},
	initialize: function(node, options){
		this.setOptions(options);
		
		this.node = $(node);
		this.container = new Element("div");
		
		this.path = MWF.defaultPath+"/widget/$Arraylist/";
		this.cssPath = MWF.defaultPath+"/widget/$Arraylist/"+this.options.style+"/css.wcss";
		this._loadCss();
		
		this.items = [];
	},
	load: function(arr){
		if (this.fireEvent("queryLoad")){
			this.container.set("styles", this.css.container);
			this.container.inject(this.node);
						
			this.createTitleNode();
			
			this.createContent(arr);
			
			this.fireEvent("postLoad");
		}
	},
	createTitleNode: function(){
		this.titleNode = new Element("div", {
			"styles": this.css.titleNode
		}).inject(this.container);
		
		this.titleActionNode = new Element("div", {
			"styles": this.css.titleActionNode,
			"events": {
				"click": function(e){
					this.addNewItem(null, "top");
				}.bind(this)
			}
		}).inject(this.titleNode);
		
		this.titleTextNode = new Element("div", {
			"styles": this.css.titleTextNode,
			"text": this.options.title
		}).inject(this.titleNode);	
	},
	
	createContent: function(arr){
		this.contentNode = new Element("div", {
			"styles": this.css.contentNode
		}).inject(this.container);
		
		this.contentItemsNode = new Element("div", {
			"styles": this.css.contentItemsNode
		}).inject(this.contentNode);
		
		this.loadContent(arr);
	},
	loadContent: function(arr){
		arr.each(function(text){
			var item = new MWF.widget.Arraylist.Item(this, text);
			item.load();
			item.itemNode.inject(this.contentItemsNode);
			this.items.push(item);
		}.bind(this));
	},
	addNewItem: function(item, where){
		if (this.notAddItem){
			this.notAddItem = false;
		}else{
			var newItem = new MWF.widget.Arraylist.Item(this, "", "");
			newItem.load();
			if (item){
				newItem.itemNode.inject(item.itemNode, "after");
			}else{
				newItem.itemNode.inject(this.contentItemsNode, where);
			}
			newItem.isNewItem = true;
			newItem.editValue();
			
			this.items.push(newItem);
		}
	},
	
	deleteItem: function(item){
		this.notAddItem = false;
		this.items.erase(item);
		item.itemNode.destroy();
		delete item;
		this.fireEvent("change");
	},
	toArray: function(){
        var itemNodes = this.contentItemsNode.getChildren("div");
		var arr = [];
		itemNodes.each(function(itemNode){
            var item = itemNode.retrieve("item");
			if (item.text){
				arr.push(item.text);
			}
		});
		return arr;
	}
	
});

MWF.widget.Arraylist.Item = new Class({
	initialize: function(arraylist, text){
		this.arraylist = arraylist;
		this.text = text;
	},
	load: function(){
		this.creatItemNode();
		this.setItemEvents();
	},
	creatItemNode: function(){
		this.itemNode = new Element("div", {
			"styles": this.arraylist.css.contentItemNode
		});
        this.itemNode.store("item", this);

		this.iconNode = new Element("div", {
			"styles": this.arraylist.css.contentItemIconNode,
			"events":{
				"mouseover": function(e){e.stopPropagation();},
				"mouseout": function(e){e.stopPropagation();}
			}
		}).inject(this.itemNode);
		
		this.valueNode = new Element("span", {
			"styles": this.arraylist.css.contentItemValueNode,
			"text": this.text
		}).inject(this.itemNode);
		
	},
	
	setItemEvents: function(){
		this.itemNode.addEvents({
			"mouseover": function(e){
				e.target.setStyles(this.arraylist.css.contentItemNodeOver);
			}.bind(this),
			"mouseout": function(e){
				e.target.setStyles(this.arraylist.css.contentItemNode);
			}.bind(this),
			"click": function(e){
				this.arraylist.addNewItem(this);
			}.bind(this)
		});
		
		this.valueNodeClick = function(e){
			if (this.arraylist.options.isModify) this.editValue();
			e.stopPropagation();
		}.bind(this);
		this.valueNode.addEvent("click", this.valueNodeClick);
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
			"styles": this.arraylist.css.editInputDiv
		});
		var input = new Element("input", {
			"styles": this.arraylist.css.editInput,
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
	//	if (node == this.keyNode){
			if (!text){
				this.arraylist.deleteItem(this);
			}
			
			var flag = true;
			this.arraylist.items.each(function(item){
				if (item.text == text){
					if (item != this) flag = false;
				}
			}.bind(this));
			
			if (flag){
				this.text = text;
				this.arraylist.notAddItem = true;
			}else{
				this.iconNode.setStyle("background", "url("+this.arraylist.path+this.arraylist.options.style+"/icon/error.png) center center no-repeat");
				this.iconNode.title = MWF.LP.process.repetitionsValue;
				input.select();
				return false;
			}
	//	}

		var addNewItem = false;
		if (this.isEnterKey){
			if (this.isNewItem){
				addNewItem = true;
			}
			this.editOkAddNewItem = false;
		}
		this.isNewItem = false;

		node.set("html", text);
		this.iconNode.setStyle("background", "transparent");
		this.iconNode.title = "";

		this.arraylist.fireEvent("change");
		
		if (addNewItem){
			this.arraylist.notAddItem = false;
			this.arraylist.addNewItem(this);
		}else{
			this.arraylist.notAddItem = true;
		}
		
		return true;
	}
	
});




