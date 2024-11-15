// o2.widget = o2.widget || {};
o2.require("o2.widget.Common", null, false);
// o2.require("o2.widget.Tree", null, false);
MWF.xApplication.process.FormDesigner.widget.ElTreeEditor = new Class({
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
		
		this.path = "../x_component_process_FormDesigner/widget/$ElTreeEditor/";
		this.cssPath = this.path+this.options.style+"/css.wcss";
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
		
		this.data = content;
		
		this.resizeContentNodeSize();
		
		this.tree = new MWF.xApplication.process.FormDesigner.widget.ElTreeEditor.Tree(this, this.contentNode, {"style": "editor"});
		this.tree.data = this.data;
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
			this.data.push(obj);
			var treeNode = this.tree.appendChild(obj);

			//if (!this.options.expand) this.tree.expandOrCollapseNode(this);
			treeNode.selectNode();
			treeNode.showItemAction();

			treeNode.editItemProperties();

			var textDivNode = treeNode.textNode.getElement("div");
			treeNode.editItem(textDivNode);

			this.fireEvent("change", [{
				compareName: " [addSub]",
				force: true
			}]);
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

MWF.xApplication.process.FormDesigner.widget.ElTreeEditor.Tree = new Class({
	Extends: o2.widget.Common,
	Implements: [Options, Events],
	children: [],
	options: {
		"style": "default",
		"expand": false
	},
	jsonMapping: {
		"id": "id",
		"label": "label",
		"children": "children"
		// "disabled": "disabled",
		// "isLeaf": "isLeaf"
	},
	nodejson: {
		"id": "",
		"label": "[none]",
		"children": []
	},
	initialize: function(editor, tree, options){
		this.setOptions(options);

		this.path = o2.session.path+"/widget/$Tree/";
		this.cssPath = o2.session.path+"/widget/$Tree/"+this.options.style+"/css.wcss";
		this._loadCss();

		this.container = $(tree);
		this.children = [];
		this.data = null;

		this.editor = editor;
	},
	load: function(data){
		if (this.fireEvent("queryLoad")){

			if (data) this.data = data;

			this.node = new Element("div",{
				"styles": this.css.areaNode
			});

			this.loadTree();

			this.node.inject(this.container);

			this.fireEvent("postLoad");
		}
	},
    empty: function(){
        this.children.each(function(o){
            o2.release(o);
        }.bind(this));
        this.node.empty();
    },
	reLoad: function(data){
		if (data) this.data = data;
		this.children = [];
		this.node.empty();
		this.loadTree();
	},
	loadTree: function(){
		if (this.data){
			this.loadJsonTree(this.data, this, this);
		}
		if (this.container) this.node.inject(this.container);
	},


	loadJsonTree: function(data, tree, node){
		data.each(function(item){

			var treeNode = node.appendChild(item);

			if (item.children && item.children.length){
				this.loadJsonTree(item.children, this, treeNode);
			}
		}.bind(tree));
	},
	appendChild: function(obj){
		var treeNode = new MWF.xApplication.process.FormDesigner.widget.ElTreeEditor.Tree.Node(this, obj);
		
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
		this.editor.fireEvent("change", [{
			compareName: "."+treeNode.getLevelName() + ".expand"
		}]);
	},
	expand: function(treeNode){
		if (this.fireEvent("queryExpand", [treeNode])){
			if(treeNode.childrenNode)treeNode.childrenNode.setStyle("display", "block");
		}
		this.fireEvent("postExpand", [treeNode]);
	},
	collapse: function(treeNode){
		if (this.fireEvent("queryCollapse", [treeNode])){
			if(treeNode.childrenNode)treeNode.childrenNode.setStyle("display", "none");
        }
		this.fireEvent("postCollapse", [treeNode]);
	},
	toJson: function(item){
		var json=null;
		var node = item.firstChild;

		json=[];
		while (node){
			json.push(node.data);
			json[json.length-1].children = this.toJson(node);

			node = node.nextSibling;
		}


		return json;
	}
	
});

MWF.xApplication.process.FormDesigner.widget.ElTreeEditor.Tree.Node = new Class({
	Implements: [Options, Events],
	options: {
		"expand": true
		// "label": "",
		// "default" : false,
		// "icon": ""
	},
	srciptOption: {
		"width": 300,
		"height": 300,
		"top": null,
		"left": null,
	},
	imgs: {
		"expand": "expand.gif",
		"collapse":"collapse.gif",
		"blank": "blank.gif"
	},

	initialize: function(tree, data){
		Object.each({
			// "expand": true,
			"label": ""
			// "default" : false,
			// "icon": ""
		}, function(value, key){
			if( !data.hasOwnProperty(key) ){
				data[key] = value;
			}
		});
		this.data = data;
		if (data.icon=="none") this.data.icon = "";

		this.tree = tree;
		this.levelNode = [];
		this.children = [];
		this.parentNode = null;
		this.previousSibling = null;
		this.nextSibling = null;
		this.firstChild = null;

		this.node = new Element("div",{
			"styles": this.tree.css.treeNode
		});
		this.itemNode = new Element("div", {
			"styles": this.tree.css.treeItemNode
		}).inject(this.node);
		this.childrenNode = new Element("div", {
			"styles": this.tree.css.treeChildrenNode
		}).inject(this.node);

		if (!this.options.expand){
			this.childrenNode.setStyle("display", "none");
		}
		
		this.itemNode.addEvents({
			"mouseover": function(){
				if (this.tree.currentEditNode!==this) {
					this.itemNode.setStyles(this.tree.css.treeItemNodeOver);
					this.showItemAction();
				}
			}.bind(this),
			"mouseout": function(){
				if (this.tree.currentEditNode!==this) {
					this.itemNode.setStyles(this.tree.css.treeItemNode);
					this.hideItemAction();
				}
			}.bind(this),
			"click": function () {
				this.editItemProperties();
			}.bind(this)
		});
	},
	load: function(){
		this.tree.fireEvent("beforeLoadTreeNode", [this]);

		this.nodeTable = new Element("table", {
			"border": "0",
			"cellpadding": "0",
			"cellspacing": "0",
			"styles": {"width": "fit-content", "table-layout": "fixed"}
		}).inject(this.itemNode);
		this.nodeTable.setStyles(this.tree.css.nodeTable);

		var tbody = new Element("tbody").inject(this.nodeTable);
		this.nodeArea = new Element("tr").inject(tbody);

		this.createLevelNode();
		this.createOperateNode();
		this.createIconNode();
		this.createTextNode();

		this.tree.fireEvent("afterLoadTreeNode", [this]);
	},
	createLevelNode: function(){
		for (var i=0; i<this.level; i++){
			var td = new Element("td",{
				"styles": this.tree.css.blankLevelNode
			}).inject(this.nodeArea);
			this.levelNode.push(td);
		}
	},
	createOperateNode: function(){
		this.operateNode = new Element("td",{
			"styles": this.tree.css.operateNode
		}).inject(this.nodeArea);

		this.operateNode.addEvent("click", function(){
			this.expandOrCollapse();
		}.bind(this));

        this.operateNode.setStyle("background", "url("+this.tree.path+this.tree.options.style+"/"+this.imgs.blank+") center center no-repeat");

	},
	createIconNode: function(){
		if (this.data.icon){
			this.iconNode = new Element("td",{
				"styles": this.tree.css.iconNode
			}).inject(this.nodeArea);
            this.iconNode.setStyle("background", "url("+this.tree.path+this.tree.options.style+"/"+this.data.icon+") center center no-repeat");
		}
	},
	createTextNode: function(){
		this.textNode = new Element("td",{
			"styles": this.tree.css.textNode
		}).inject(this.nodeArea);

		var textDivNode = new Element("div", {
			"styles": {"display": "inline-block"},
			"text": this.data.label
		});
		textDivNode.setStyles(this.tree.css.textDivNode);

		textDivNode.addEvent("click", function(e){
			this.clickNode(e);
		}.bind(this));

		textDivNode.inject(this.textNode);
		// if( this.data.default ){
		// 	textDivNode.click();
		// }
	},
	clickNode: function(e){
		this.selectNode(e);
		this.doAction(e);
	},

	selectNode: function(){
		this.tree.fireEvent("beforeSelect", [this]);
		if (this.tree.currentNode){
			this.tree.currentNode.fireEvent("unselect");
			var textDivNode = this.tree.currentNode.textNode.getElement("div");
			textDivNode.setStyles(this.tree.css.textDivNode);
		}
		var textDivNode = this.textNode.getElement("div");
		textDivNode.setStyles(this.tree.css.textDivNodeSelected);

		this.tree.currentNode = this;
		this.tree.fireEvent("afterSelect", [this]);
	},
	setOperateIcon: function(){
		var imgStr = (this.options.expand) ? this.imgs.expand : this.imgs.collapse;
		imgStr = this.tree.path+this.tree.options.style+"/"+imgStr;
		if (!this.firstChild) imgStr = this.tree.path+this.tree.options.style+"/"+this.imgs.blank;

        this.operateNode.setStyle("background", "url("+imgStr+") center center no-repeat");
	},
	// insertChild: function(obj){
	// 	var treeNode = new this.tree.$constructor.Node(this.tree, obj);
	//
	// 	var tmpTreeNode = this.previousSibling;
	//
	// 	this.previousSibling = treeNode;
	// 	treeNode.nextSibling = this;
	// 	treeNode.previousSibling = tmpTreeNode;
	// 	if (tmpTreeNode){
	// 		tmpTreeNode.nextSibling = treeNode;
	// 	}else{
	// 		this.parentNode.firstChild = treeNode;
	// 	}
	//
	// 	treeNode.parentNode = this.parentNode;
	// 	treeNode.level = this.level;
	//
	// 	treeNode.load();
	// 	treeNode.node.inject(this.node, "before");
	// 	this.parentNode.children.push(treeNode);
	//
	// 	return treeNode;
	// },
	appendChild: function(obj){
		// if (!this.data.children) this.data.children = [];
		// this.data.children.push(obj);
		
		var treeNode = new MWF.xApplication.process.FormDesigner.widget.ElTreeEditor.Tree.Node(this.tree, obj);
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
	getLevelName: function(){
		var parentTextList = [];
		var parent = this;
		while (parent){
			parentTextList.push( parent.data.label );
			parent = parent.parentNode;
		}
		return parentTextList.reverse().join(".");
	},
	expandOrCollapse: function(){
		this.tree.expandOrCollapseNode(this);
	},
	destroy: function(){
		if (this.previousSibling) this.previousSibling.nextSibling = this.nextSibling;
		if (this.nextSibling) this.nextSibling.previousSibling = this.previousSibling;
		if (this.parentNode){
			if (this.parentNode.firstChild==this){
				this.parentNode.firstChild = this.nextSibling;
			}
            this.parentNode.children.erase(this);
			this.parentNode.data.children.erase(this.data);
		}else{
			this.tree.data.erase(this.data)
		}
		this.node.destroy();
		delete this;
	},
	doAction: function(e){
		var textNode = e.target;
		this.editItem(textNode);
		e.stopPropagation();
	},
	hideItemAction: function(){
		if (this.actionNode) this.actionNode.setStyle("display", "none");
	},
	setActionPosition: function(){
		if (this.actionNode){
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
					e.stopPropagation();
				}.bind(this)
			}
		}).inject(this.actionNode);
		
		// var propertyAction = new Element("div", {
		// 	"styles": this.tree.css.itemPropertyActionNode,
		// 	"title": o2.LP.process.formAction["property"],
		// 	"events": {
		// 		"click": function(e){
		// 			this.editItemProperties(e);
		// 		}.bind(this)
		// 	}
		// }).inject(this.actionNode);

		var addAction = new Element("div", {
			"styles": this.tree.css.itemAddActionNode,
			"title": o2.LP.process.formAction.add,
			"events": {
				"click": function(ev){
					this.addChild();
					ev.stopPropagation();
				}.bind(this)
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
	
	// completeScriptItem: function(){
	// 	this.itemNode.setStyles(this.tree.css.treeItemNode);
	// 	this.isEditScript = false;
	// 	this.tree.currentEditNode = null;
	//
	// 	if (this.scriptArea){
	// 		if (!this.scriptArea.treeEditorMorph){
	// 			this.scriptArea.treeEditorMorph = new Fx.Morph(this.scriptArea.container, {
	// 				"duration": 200
	// 			});
	// 		}
	// 		this.scriptArea.treeEditorMorph.start({
	// 			"height": "0",
	// 			"overflow": "auto"
	// 		}).chain(function(){
	// 			this.scriptArea.container.setStyle("display", "none");
	// 		}.bind(this));
	// 	}
	//
	//
	// },
	// editScriptItem: function(e){
	//
	// 	if (this.tree.currentEditNode!=this){
	// 		if (this.tree.currentEditNode) this.tree.currentEditNode.completeScriptItem();
	//
	// 		this.itemNode.setStyle("background", "#DDD");
	// 		if (!this.scriptArea){
	// 			var node = new Element("div").inject(this.itemNode, "after");
	// 			o2.require("o2.widget.ScriptArea", function(){
	// 				this.scriptArea = new o2.widget.ScriptArea(node, {
	// 					"title": o2.LP.process.formAction["script"],
	// 					"maxObj": this.tree.editor.options.maxObj,
	// 					"style": "treeEditor",
	// 					"onChange": function(){
	// 						this.data.action = this.scriptArea.toJson();
	// 						this.tree.editor.fireEvent("change");
	// 					}.bind(this)
	// 				});
	// 				if (!this.data.action) this.data.action = {};
	// 				this.scriptArea.load(this.data.action);
	//
	// 				this.scriptArea.container.setStyles({
	// 					"overflow": "hidden",
	// 					"height": "0px"
	// 				});
	//
	// 			}.bind(this));
	// 		}
	//
	// 		this.scriptArea.container.setStyle("display", "block");
	// 		if (!this.scriptArea.treeEditorMorph){
	// 			this.scriptArea.treeEditorMorph = new Fx.Morph(this.scriptArea.container, {
	// 				"duration": 200
	// 			});
	// 		}
	// 		this.scriptArea.treeEditorMorph.start({
	// 			"height": "200px",
	// 			"overflow": "auto"
	// 		}).chain(function(){
	// 			this.scriptArea.container.scrollIntoView();
	// 			this.scriptArea.focus();
	// 			this.setActionPosition();
	// 		}.bind(this));
	//
	// 		this.isEditScript = true;
	// 		this.tree.currentEditNode = this;
	// 	}else{
	// 		this.completeScriptItem();
	// 	}
	// },

	completeItemProperties: function(){
		this.hideItemAction();
		this.itemNode.setStyles(this.tree.css.treeItemNode);
		this.isEditProperty = false;
		this.tree.currentEditNode = null;
		this.propertyArea.hide();
	},
	editItemProperties: function(){
		if (this.tree.currentEditNode!=this){
			if (this.tree.currentEditNode) this.tree.currentEditNode.completeItemProperties();

			this.itemNode.setStyle("background", "#DDD");
			if (!this.propertyArea){
				this.propertyArea = new Element("div").inject(this.itemNode, "after");

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
							this.tree.editor.fireEvent("change", [{
								compareName: "."+this.getLevelName() + ".label"
							}]);
						}.bind(this)
					}
				}).inject(td);

				tr = new Element("tr").inject(this.propertyTable);
				td = new Element("td", { text: "id/key" }).inject(tr);
				td = new Element("td").inject(tr);
				this.idInput = new Element("input", {
					value: this.data.id || "",
					events: {
						blur: function () {
							this.data.id = this.idInput.get("value");
							this.tree.editor.fireEvent("change", [{
								compareName: "."+this.getLevelName() + ".id"
							}]);
						}.bind(this)
					}
				}).inject(td);

				tr = new Element("tr").inject(this.propertyTable);
				td = new Element("td", { "colspan": "2" }).inject(tr);
				MWF.require("MWF.widget.Maplist", function() {
					var maplist = new MWF.widget.Maplist(td, {
						"title": "其他属性",
						"collapse": false,
						"onChange": function () {
							// var oldData = this.data[name];
							var data = maplist.toJson();
							for (var key in data) {
								this.data[key] = data[key]
							}
							this.tree.editor.fireEvent("change", [{
								compareName: "."+this.getLevelName() + ".property"
							}]);
						}.bind(this),
						"onDelete": function (key) {
							if (this.data[key]) {
								delete this.data[key];
							}
							this.tree.editor.fireEvent("change", [{
								compareName: "."+this.getLevelName() + ".property"
							}]);
						}.bind(this),
						"isProperty": true
					});
					var data = {};
					for (var key in this.data) {
						if( !["id","label", "children"].contains(key) )data[key] = this.data[key]
					}
					maplist.load(data);
				}.bind(this))
			}

			this.propertyArea.setStyle("display", "block");
			this.propertyArea.scrollIntoView(false);
			this.setActionPosition();

			this.isEditProperty = true;
			this.tree.currentEditNode = this;
		}else{
			this.completeItemProperties();
		}
	},
	
	addChild: function(){
		var obj = Object.clone(this.tree.nodejson);
		if (!this.data.children) this.data.children = [];
		this.data.children.push(obj);
		
		var treeNode = this.appendChild(obj);
		
		if (!this.options.expand) this.tree.expandOrCollapseNode(this);
		treeNode.selectNode();
		treeNode.showItemAction();

		treeNode.editItemProperties();
		
		var textDivNode = treeNode.textNode.getElement("div");
		treeNode.editItem(textDivNode);

		this.tree.editor.fireEvent("change", [{
			compareName: "."+this.getLevelName() + " [addSub]",
			force: true
		}]);
		
	},
	deleteItem: function(e){
		var treeNode = this;
		
		var p = e.target.getPosition();
		var tmpe = {"event": {"x": p.x+40, "y": p.y}};

		MWF.xDesktop.confirm("warn", tmpe, o2.LP.process.notice.deleteTreeNodeTitle, o2.LP.process.notice.deleteTreeNode, 300, 120, function(){
			treeNode.destroy();
			treeNode.tree.editor.fireEvent("change", [{
				compareName: "."+treeNode.getLevelName() + " [delete]",
				force: true
			}]);
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

		this.tree.editor.fireEvent("change", [{
			compareName: "."+this.getLevelName() + ".label"
		}]);
		
		return true;
	}

});
