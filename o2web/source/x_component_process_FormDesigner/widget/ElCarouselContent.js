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
		
	},
	addTreeNode: function(){
		if (this.tree) {
			var obj = Object.clone(this.tree.nodejson);
			this.data.push(obj);
			var treeNode = this.tree.appendChild(obj);

			//if (!this.options.expand) this.tree.expandOrCollapseNode(this);
			treeNode.selectNode();
			treeNode.showItemAction();

			treeNode.editItemProperties();
		}

	},
});

MWF.xApplication.process.FormDesigner.widget.ElCarouselContent.Tree = new Class({
	Extends: MWF.xApplication.process.FormDesigner.widget.ElTreeEditor.Tree,
	nodejson: {
		"type": "img",
		"dataPath": "",
		"styles": {},
		"srcScript": {
			"code": "",
			"html": ""
		},
		"clickScript": {
			"code": "",
			"html": ""
		}
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
	getTitle: function(){
		if( this.data.type === "img" ){
			return "图片";
		}else if( this.data.type === "text" ){
			return "文本";
		}else{
			return "";
		}
	},
	setTitle : function(){
		this.textDivNode.set("text", this.getTitle());
	},
	createTextNode: function(){
		this.textNode = new Element("td",{
			"styles": this.tree.css.textNode
		}).inject(this.nodeArea);

		var textDivNode = this.textDivNode = new Element("div", {
			"styles": {"display": "inline-block"},
			"text": this.getTitle()
		});
		textDivNode.setStyles(this.tree.css.textDivNode);

		textDivNode.addEvent("click", function(e){
			this.clickNode(e);
		}.bind(this));

		textDivNode.inject(this.textNode);
	},
	selectNode: function(){
		this.tree.fireEvent("beforeSelect", [this]);
		if (this.tree.currentNode){
			this.tree.currentNode.fireEvent("unselect");
			var textDivNode = this.tree.currentNode.textNode.getElement("div");
			textDivNode.setStyles(this.tree.css.textDivNode);
		}
		var textDivNode = this.textNode.getElement("div");
		// textDivNode.setStyles(this.tree.css.textDivNodeSelected);

		this.tree.currentNode = this;
		this.tree.fireEvent("afterSelect", [this]);
	},
	clickNode: function(e){
		this.selectNode(e);
	},
	addChild: function(){
		debugger;
		var obj = Object.clone(this.tree.nodejson);
		if (!this.data.children) this.data.children = [];
		this.data.children.push(obj);

		var treeNode = this.appendChild(obj);

		if (!this.options.expand) this.tree.expandOrCollapseNode(this);
		treeNode.selectNode();
		treeNode.showItemAction();

		treeNode.editItemProperties();
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
							this.setTitle();
							this.srcScriptTr.setStyle("display", "");
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
							this.setTitle();
							this.srcScriptTr.setStyle("display", "none");
						}.bind(this)
					}
				}).inject( div );
				new Element( "span", { "text" : "文本" }).inject(div);


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

				//styles
				var tr = new Element("tr").inject(this.propertyTable);
				td = new Element("td", { "colspan": "2" }).inject(tr);
				MWF.require("MWF.widget.Maplist", function() {
					var maplist = new MWF.widget.Maplist(td, {
						"title": "样式",
						"collapse": false,
						"onChange": function () {
							var data = maplist.toJson();
							this.data.styles = data;
						}.bind(this),
						"onDelete": function (key) {
							if (this.data.styles && this.data.styles[key]) {
								delete this.data.styles[key];
							}
						}.bind(this),
						"isProperty": false
					});
					maplist.load(this.data.styles);
				}.bind(this));

				//srcScript
				var tr = new Element("tr").inject(this.propertyTable);
				td = new Element("td", { "colspan": "2" }).inject(tr);
				this.srcScriptTr = tr;
				MWF.require("MWF.widget.ScriptArea", function(){
					this.srcScriptEditor = new MWF.widget.ScriptArea(td, {
						"title": "图片资源脚本",
						"mode": "javascript",
						// "maxObj": this.designer.formContentNode || this.designer.pageContentNode,
						"onChange": function(){
							var json = this.srcScriptEditor.toJson();
							this.data.srcScript.code = json.code;
							//this.data[name].html = json.html;
						}.bind(this),
						"onSave": function(){
							//this.designer.saveForm();
						}.bind(this),
						"style": "formula",
						"runtime": "web"
					});
					this.srcScriptEditor.load(this.data.srcScript);
				}.bind(this));
				if( this.data.type !== "img"){
					this.srcScriptTr.hide();
				}

				//clickScript
				var tr = new Element("tr").inject(this.propertyTable);
				td = new Element("td", { "colspan": "2" }).inject(tr);
				MWF.require("MWF.widget.ScriptArea", function(){
					this.clickScriptEditor = new MWF.widget.ScriptArea(td, {
						"title": "点击事件脚本",
						"mode": "javascript",
						// "maxObj": this.designer.formContentNode || this.designer.pageContentNode,
						"onChange": function(){
							var json = this.clickScriptEditor.toJson();
							this.data.clickScript.code = json.code;
							//this.data[name].html = json.html;
						}.bind(this),
						"onSave": function(){
							//this.designer.saveForm();
						}.bind(this),
						"style": "formula",
						"runtime": "web"
					});
					this.clickScriptEditor.load(this.data.clickScript);
				}.bind(this));


			}

			this.propertyArea.setStyle("display", "block");
			this.propertyArea.scrollIntoView();
			this.setActionPosition();

			this.isEditProperty = true;
			this.tree.currentEditNode = this;
		}else{
			this.completeItemProperties();
		}
	}

});
