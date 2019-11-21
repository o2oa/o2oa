o2.widget = o2.widget || {};
o2.require("o2.widget.Common", null, false);
o2.require("o2.widget.Tree", null, false);
o2.widget.TreeEditor = new Class({
	Implements: [Options, Events],
	Extends: o2.widget.Common,
	options: {
		"style": "default",
		"count": 0,
		"height": 500,
		"width": 500,
		"top": -1,
		"left": -1,
		"maxObj": document.body
	},
	initialize: function(node, options){
		this.setOptions(options);
		this.node = $(node);
		
		this.path = o2.session.path+"/widget/$TreeEditor/";
		this.cssPath = o2.session.path+"/widget/$TreeEditor/"+this.options.style+"/css.wcss";
		this._loadCss();
		
		this.container = new Element("div");
	},
	
	load: function(content){
		if (this.fireEvent("queryLoad")){
			this.container.set("styles", this.css.container);
			this.container.inject(this.node);
						
			this.createTitleNode();
			
			this.createContent(content);
			
			this.fireEvent("postLoad");
		}
	},
	
	createTitleNode: function(){
		this.titleNode = new Element("div", {
			"styles": this.css.titleNode,
			"events": {
				"dblclick": this.toggleSize.bind(this)
			}
		}).inject(this.container);
		
		this.titleActionNode = new Element("div", {
			"styles": this.css.titleActionNode,
			"events": {
				"click": this.addTreeNode.bind(this)
			}
		}).inject(this.titleNode);
		
		this.titleTextNode = new Element("div", {
			"styles": this.css.titleTextNode,
			"text": this.options.title
		}).inject(this.titleNode);
	},
	
	createContent: function(content){
		this.contentNode = new Element("div", {
			"styles": this.css.contentNode
		}).inject(this.container);
		
		this.json = content;
		
		this.resizeContentNodeSize();
		
		this.tree = new o2.widget.TreeEditor.Tree(this, this.contentNode, {"style": "editor"});
		this.tree.treeJson = this.json;
		this.tree.load();
		
	},
	resizeContentNodeSize: function(){
		var titleSize = this.titleNode.getSize();
		var size = this.container.getSize();
		var height = size.y-titleSize.y-2-6;
		this.contentNode.setStyle("min-height", ""+height+"px");
	},
	addTreeNode: function(){
		if (this.tree) {
			var obj = Object.clone(this.tree.nodejson);
			this.json.push(obj);
			var treeNode = this.tree.appendChild(obj);
			treeNode.selectNode();
			
			var textDivNode = treeNode.textNode.getElement("div");
			treeNode.editItem(textDivNode);
		} 
		
	},
	toggleSize: function(e){
		var status = this.titleActionNode.retrieve("status", "max");
		if (status=="max"){
			this.maxSize();
		}else{
			this.returnSize();
		}
	},
	toJson: function(){
		if (this.tree){
			return this.tree.toJson(this.tree);			
		}else{
			return {};
		}
	}
});

o2.widget.TreeEditor.Tree = new Class({
	Extends: o2.widget.Tree,
	nodejson: {
		"expand": true,
		"title": "",
		"text": "[none]",
		"action": "",
		"icon": "none"
	},
	initialize: function(editor, tree, options){

		this.parent(tree, options);
		this.editor = editor;
	},
	appendChild: function(obj){
		var treeNode = new o2.widget.TreeEditor.Tree.Node(this, obj);
		
		if (this.children.length){
			treeNode.previousSibling = this.children[this.children.length-1];
			treeNode.previousSibling.nextSibling = treeNode;
		}else{
			this.firstChild = treeNode;
		}
		
		treeNode.load();
		treeNode.node.inject(this.node);
		this.children.push(treeNode);
		return treeNode;
	},
	expandOrCollapseNode: function(treeNode){
		if (treeNode.options.expand){
			this.collapse(treeNode);
			treeNode.options.expand = false;
		}else{
			this.expand(treeNode);
			treeNode.options.expand = true;
		}
		treeNode.setOperateIcon();
		this.editor.fireEvent("change");
	}
	
});

o2.widget.TreeEditor.Tree.Node = new Class({
	Extends: o2.widget.Tree.Node,
	srciptOption: {
		"width": 300,
		"height": 300,
		"top": null,
		"left": null,
	},

	initialize: function(tree, options){
		this.parent(tree, options);
		
		this.itemNode.addEvents({
			"mouseover": function(){
				if (!this.isEditScript) this.itemNode.setStyles(this.tree.css.treeItemNodeOver);
				this.showItemAction();
			}.bind(this),
			"mouseout": function(){
				if (!this.isEditScript) this.itemNode.setStyles(this.tree.css.treeItemNode);
				this.hideItemAction();
			}.bind(this)
		});
	},

	appendChild: function(obj){
		if (!this.options.sub) this.options.sub = [];
		this.options.sub.push(obj);
		
		var treeNode = new o2.widget.TreeEditor.Tree.Node(this.tree, obj);
		if (this.children.length){
			treeNode.previousSibling = this.children[this.children.length-1];
			treeNode.previousSibling.nextSibling = treeNode;
		}else{
			this.firstChild = treeNode;
			this.setOperateIcon();
		}
		
		treeNode.level = this.level+1;
		treeNode.parentNode = this;
		
		treeNode.load();
		treeNode.node.inject(this.childrenNode);
		this.children.push(treeNode);
		
		return treeNode;
	},
	doAction: function(e){
		var textNode = e.target;
		this.editItem(textNode);
	},
	hideItemAction: function(){
		if (this.actionNode) this.actionNode.setStyle("display", "none");
	},
	setActionPosition: function(){
		if (this.actionNode){
//			var p = this.itemNode.getPosition();
//			var size = this.itemNode.getSize();
//			
//			var x = p.x+size.x-70;
//			var y = p.y+((size.y-22)/2);
//
//			this.actionNode.setStyles({
//				"left": x,
//				"top": y
//			});
			this.actionNode.position({
				relativeTo: this.itemNode,
				position: "rightCenter",
				edge: "rightCenter"
			});
		}
	},
	showItemAction: function(){
		if (!this.actionNode) this.createItemActionNode();
		this.setActionPosition();
		this.actionNode.setStyle("display", "block");
	},
	createItemActionNode: function(){
		this.actionNode = new Element("div", {
			"styles": this.tree.css.itemActionNode
		}).inject(this.itemNode);
		
		var deleteAction = new Element("div", {
			"styles": this.tree.css.itemDeleteActionNode,
			"title": o2.LP.process.formAction["delete"],
			"events": {
				"click": function(e){
					this.deleteItem(e);
				}.bind(this)
			}
		}).inject(this.actionNode);
		
		var scriptAction = new Element("div", {
			"styles": this.tree.css.itemScriptActionNode,
			"title": o2.LP.process.formAction["script"],
			"events": {
				"click": function(e){
					this.editScriptItem(e);
				}.bind(this)
			}
		}).inject(this.actionNode);

		var addAction = new Element("div", {
			"styles": this.tree.css.itemAddActionNode,
			"title": o2.LP.process.formAction.add,
			"events": {
				"click": this.addChild.bind(this)
			}
		}).inject(this.actionNode);
	},
	getScriptDefaultPosition: function(width, height){
		var ph = this.node.getPosition();
		var pw = this.tree.node.getPosition();
		var size = this.node.getSize();
		var bodySize = document.body.getSize();
		
		var x = pw.x-width-10;
		if (x+width>bodySize.x) x = bodySize.x-width;
		if (x<0) x = 0;
		
		var y = ph.y-(height/2)+(size.y/2);
		if (y+height>bodySize.y) y = bodySize.y-height;
		if (y<0) y = 0;

		return {"x": x, "y": y};
	},
	
	createScriptNode: function(){
		this.scriptNode = new Element("div", {
			"styles": this.tree.css.scriptNode
		});
		
		o2.require("o2.widget.ScriptEditor", null, false);
		this.scriptEditor = new o2.widget.ScriptEditor(this.scriptNode, {"style": "process"});
	},
	
	completeScriptItem: function(){
		this.itemNode.setStyles(this.tree.css.treeItemNode);
		this.isEditScript = false;
		this.tree.currentEditNode = null;
	
		if (this.scriptArea){
			if (!this.scriptArea.treeEditorMorph){
				this.scriptArea.treeEditorMorph = new Fx.Morph(this.scriptArea.container, {
					"duration": 200
				});
			}
			this.scriptArea.treeEditorMorph.start({
				"height": "0",
				"overflow": "auto"
			}).chain(function(){
				this.scriptArea.container.setStyle("display", "none");
			}.bind(this));
		}
		

	},
	editScriptItem: function(e){

		if (this.tree.currentEditNode!=this){
			if (this.tree.currentEditNode) this.tree.currentEditNode.completeScriptItem();
			
			this.itemNode.setStyle("background", "#DDD");
			if (!this.scriptArea){
				var node = new Element("div").inject(this.itemNode, "after");
				o2.require("o2.widget.ScriptArea", function(){
					this.scriptArea = new o2.widget.ScriptArea(node, {
						"title": o2.LP.process.formAction["script"],
						"maxObj": this.tree.editor.options.maxObj,
						"style": "treeEditor",
						"onChange": function(){
							this.options.action = this.scriptArea.toJson();
							this.tree.editor.fireEvent("change");
						}.bind(this)
					});
					if (!this.options.action) this.options.action = {};
					this.scriptArea.load(this.options.action);
					
					this.scriptArea.container.setStyles({
						"overflow": "hidden",
						"height": "0px"
					});
					
				}.bind(this));
			}
			
			this.scriptArea.container.setStyle("display", "block");
			if (!this.scriptArea.treeEditorMorph){
				this.scriptArea.treeEditorMorph = new Fx.Morph(this.scriptArea.container, {
					"duration": 200
				});
			}
			this.scriptArea.treeEditorMorph.start({
				"height": "200px",
				"overflow": "auto"
			}).chain(function(){
				this.scriptArea.container.scrollIntoView();
				this.scriptArea.focus();
				this.setActionPosition();
			}.bind(this));;

			this.isEditScript = true;
			this.tree.currentEditNode = this;
		}else{
			this.completeScriptItem();
		}
	},
	
	addChild: function(){
		var obj = Object.clone(this.tree.nodejson);
		if (!this.options.sub) this.options.sub = [];
		this.options.sub.push(obj);
		
		var treeNode = this.appendChild(obj);
		
		if (!this.options.expand) this.tree.expandOrCollapseNode(this);
		treeNode.selectNode();
		
		var textDivNode = treeNode.textNode.getElement("div");
		treeNode.editItem(textDivNode);
		
	},
	deleteItem: function(e){
		var treeNode = this;
		
		var p = e.target.getPosition();
		var tmpe = {"event": {"x": p.x+40, "y": p.y}};

		MWF.xDesktop.confirm("warn", tmpe, o2.LP.process.notice.deleteTreeNodeTitle, o2.LP.process.notice.deleteTreeNode, 300, 120, function(){
			treeNode.destroy();
			treeNode.tree.editor.fireEvent("change");
    		this.close();
		}, function(){
			this.close();
		}, null, null, "o2");
	},
	editItem: function(node, okCallBack){
		var text = node.get("text");
		node.set("html", "");
		
		var div = new Element("div", {
			"styles": this.tree.css.editInputDiv,
		});
		var input = new Element("input", {
			"styles": this.tree.css.editInput,
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
				text = "[none]";
			}
			
			this.options.text = text;
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
//		this.iconNode.setStyle("background", "transparent");
//		this.iconNode.title = "";

		this.tree.editor.fireEvent("change");
		
//		if (addNewItem){
//			this.arraylist.notAddItem = false;
//			this.arraylist.addNewItem(this);
//		}else{
//			this.arraylist.notAddItem = true;
//		}
		
		return true;
	}

});
