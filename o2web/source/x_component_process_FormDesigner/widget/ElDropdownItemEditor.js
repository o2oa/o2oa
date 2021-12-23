// o2.widget = o2.widget || {};
o2.require("o2.widget.Common", null, false);
// o2.require("o2.widget.Tree", null, false);
o2.requireApp("process.FormDesigner", "widget.ElTreeEditor", null, false);
MWF.xApplication.process.FormDesigner.widget.ElDropdownItemEditor = new Class({
	Extends: MWF.xApplication.process.FormDesigner.widget.ElTreeEditor,
	createContent: function(content){
		this.contentNode = new Element("div", {
			"styles": this.css.contentNode
		}).inject(this.container);
		
		this.data = content;
		
		this.resizeContentNodeSize();
		
		this.tree = new MWF.xApplication.process.FormDesigner.widget.ElDropdownItemEditor.Tree(this, this.contentNode, {"style": "editor"});
		this.tree.data = this.data;
		this.tree.load();
		
	}
});

MWF.xApplication.process.FormDesigner.widget.ElDropdownItemEditor.Tree = new Class({
	Extends: MWF.xApplication.process.FormDesigner.widget.ElTreeEditor.Tree,
	nodejson: {
		"label": "[none]",
		"command": "",
		"disabled": false,
		"divided": false,
		"icon":""
	},
	appendChild: function(obj){
		var treeNode = new MWF.xApplication.process.FormDesigner.widget.ElDropdownItemEditor.Tree.Node(this, obj);
		
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

MWF.xApplication.process.FormDesigner.widget.ElDropdownItemEditor.Tree.Node = new Class({
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


				var tr = new Element("tr").inject(this.propertyTable);
				var td = new Element("td", { text: "文本" }).inject(tr);
				td = new Element("td").inject(tr);
				this.labelInput = new Element("input", {
					value: this.data.label || "[none]",
					events: {
						blur: function () {
							this.data.label = this.labelInput.get("value");
							this.textNode.getElement("div").set("text", this.data.label);
						}.bind(this)
					}
				}).inject(td);

				tr = new Element("tr").inject(this.propertyTable);
				td = new Element("td", { text: "指令" }).inject(tr);
				td = new Element("td").inject(tr);
				this.idCommand = new Element("input", {
					value: this.data.command || "",
					events: {
						blur: function () {
							this.data.command = this.idCommand.get("value");
						}.bind(this)
					}
				}).inject(td);

				tr = new Element("tr").inject(this.propertyTable);
				td = new Element("td", { text: "禁用" }).inject(tr);
				td = new Element("td").inject(tr);
				var div = new Element( "div").inject(td);
				var radio_disabled_1 = new Element( "input", {
					"type" : "radio",
					"checked" : !!this.data.disabled,
					"events" : {
						"click": function () {
							this.data.disabled = true;
							radio_disabled_2.checked = false;
						}.bind(this)
					}
				}).inject( div );
				new Element( "span", { "text" : "是" }).inject(div);
				var radio_disabled_2 = new Element( "input", {
					"type" : "radio",
					"checked" : !this.data.disabled,
					"events" : {
						"click": function () {
							this.data.disabled = false;
							radio_disabled_1.checked = false;
						}.bind(this)
					}
				}).inject( div );
				new Element( "span", { "text" : "否" }).inject(div);

				tr = new Element("tr").inject(this.propertyTable);
				td = new Element("td", { text: "显示分割线" }).inject(tr);
				td = new Element("td").inject(tr);
				div = new Element( "div").inject(td);
				var radio_divided_1 = new Element( "input", {
					"type" : "radio",
					"checked" : !!this.data.divided,
					"events" : {
						"click": function(){
							this.data.divided = true;
							radio_divided_2.checked = false;
						}.bind(this)
					}
				}).inject( div );
				new Element( "span", { "text" : "是" }).inject(div);
				var radio_divided_2 = new Element( "input", {
					"type" : "radio",
					"checked" : !this.data.divided,
					"events" : {
						"click": function () {
							this.data.divided = false;
							radio_divided_1.checked = false;
						}.bind(this)
					}
				}).inject( div );
				new Element( "span", { "text" : "否" }).inject(div);

				tr = new Element("tr").inject(this.propertyTable);
				td = new Element("td", { text: "图标" }).inject(tr);
				td = new Element("td").inject(tr);
				this.xxx = new Element("input", {
					value: this.data.id || "",
					events: {
						blur: function () {
							this.xxx.command = this.xxx.get("value");
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
