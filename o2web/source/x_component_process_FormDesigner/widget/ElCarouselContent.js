// o2.widget = o2.widget || {};
o2.require("o2.widget.Common", null, false);
// o2.require("o2.widget.Tree", null, false);
o2.requireApp("process.FormDesigner", "widget.ElTreeEditor", null, false);
MWF.xApplication.process.FormDesigner.widget.ElCarouselContent = new Class({
	Extends: MWF.xApplication.process.FormDesigner.widget.ElTreeEditor,
	createContent: function(content){
		this.contentNode = new Element("div", {
			"styles": this.css.contentNode
		}).inject(this.container);
		
		this.data = content;
		
		this.resizeContentNodeSize();
		
		this.tree = new MWF.xApplication.process.FormDesigner.widget.ElCarouselContent.Tree(this, this.contentNode, {"style": "editor"});
		this.tree.data = this.data;
		this.tree.load();
		
	}
});

MWF.xApplication.process.FormDesigner.widget.ElCarouselContent.Tree = new Class({
	Extends: MWF.xApplication.process.FormDesigner.widget.ElTreeEditor.Tree,
	nodejson: {
		"type": "img",
		"dataPath": "",
		"styles": {}
	},
	appendChild: function(obj){
		var treeNode = new MWF.xApplication.process.FormDesigner.widget.ElCarouselContent.Tree.Node(this, obj);
		
		if (this.children.length){
			treeNode.previousSibling = this.children[this.children.length-1];
			treeNode.previousSibling.nextSibling = treeNode;
		}else{
			this.firstChild = treeNode;
		}
		treeNode.level = 0;
		
		treeNode.load();
		treeNode.node.inject(this.node);
		this.children.push(treeNode);
		return treeNode;
	}
});

MWF.xApplication.process.FormDesigner.widget.ElCarouselContent.Tree.Node = new Class({
	Extends: MWF.xApplication.process.FormDesigner.widget.ElTreeEditor.Tree.Node,
	options: {
		"expand": true
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
					e.stopPropagation();
				}.bind(this)
			}
		}).inject(this.actionNode);
	},
	editItemProperties: function(){
		if (this.tree.currentEditNode!=this){
			if (this.tree.currentEditNode) this.tree.currentEditNode.completeItemProperties();

			this.itemNode.setStyle("background", "#DDD");
			if (!this.propertyArea){
				this.propertyArea = new Element("div", {
					style : "border-bottom:1px solid #666"
				}).inject(this.itemNode, "after");

				this.propertyTable = new Element("table", {
					"width": "100%",
					"border": "0",
					"cellpadding":"5",
					"cellspacing":"0",
					"class": "editTable"
				}).inject(this.propertyArea);

				tr = new Element("tr").inject(this.propertyTable);
				td = new Element("td", { text: "类型" }).inject(tr);
				td = new Element("td").inject(tr);
				var div = new Element( "div").inject(td);
				var radio_type_1 = new Element( "input", {
					"type" : "radio",
					"checked" : this.data.type === "img",
					"events" : {
						"click": function () {
							this.data.type = "img";
							radio_type_2.checked = false;
						}.bind(this)
					}
				}).inject( div );
				new Element( "span", { "text" : "图片" }).inject(div);
				var radio_type_2 = new Element( "input", {
					"type" : "radio",
					"checked" : this.data.type === "text",
					"events" : {
						"click": function () {
							this.data.type = "text";
							radio_type_1.checked = false;
						}.bind(this)
					}
				}).inject( div );
				new Element( "span", { "text" : "文字" }).inject(div);


				var tr = new Element("tr").inject(this.propertyTable);
				var td = new Element("td", { text: "数据路径" }).inject(tr);
				td = new Element("td").inject(tr);
				this.pathInput = new Element("input", {
					value: this.data.dataPath || "",
					placeholder: "如: data.title",
					events: {
						blur: function () {
							this.data.dataPath = this.pathInput.get("value");
							// this.textNode.getElement("div").set("text", this.data.dataPath);
						}.bind(this)
					}
				}).inject(td);

			}

			this.propertyArea.setStyle("display", "block");
			this.propertyArea.scrollIntoView();
			this.setActionPosition();

			this.isEditProperty = true;
			this.tree.currentEditNode = this;
		}else{
			this.completeItemProperties();
		}
	},
	_loadVue: function(callback){
		if (!window.Vue){
			o2.loadAll({"css": "../o2_lib/vue/element/index.css", "js": ["vue", "elementui"]}, { "sequence": true }, callback);
		}else{
			if (callback) callback();
		}
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
			
			this.data.label = text;
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

		if( this.labelInput )this.labelInput.set("value", text);

		this.tree.editor.fireEvent("change");
		
		return true;
	}

});
