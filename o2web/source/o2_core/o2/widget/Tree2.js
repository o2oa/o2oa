o2.widget = o2.widget || {};
o2.require("o2.widget.Tree", null, false);
o2.widget.Tree2 = new Class({
	Extends: o2.widget.Tree,
});

o2.widget.Tree2.Node = new Class({
	Implements: [Options, Events],
	options: {
		"expand": true,
		"title": "",
		"text": "",
		"action": "",
		"icon": "folder.png"
	},
	imgs: {
		"expand": "expand.gif",
		"collapse":"collapse.gif",
		"blank": "blank.gif"
	},
	tree: null,
	level: 0,
	levelNode:[],

	initialize: function(tree, options){
		this.setOptions(options);
		if (options.icon=="none") this.options.icon = "";

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
	},
	
	setText: function(value){
		var textDivNode = this.textNode.getElement("div");
		if (textDivNode) textDivNode.set("text", value);
	},
	setTitle: function(value){
		var textDivNode = this.textNode.getElement("div");
		if (textDivNode) textDivNode.set("title", value);
	},
	
	load: function(){
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
		
	},
	createLevelNode: function(){
		for (var i=0; i<this.level; i++){
			var td = new Element("td",{
				"styles": this.tree.css.blankLevelNode
			}).inject(this.nodeArea);
//			var img = new Element("img", {
//				"src": o2.tree.path+this.tree.options.style+"/"+this.imgs.blank,
//				"width": td.getStyle("width"),
//				"height": td.getStyle("height"),
//				"border": "0"
//			}).inject(td);
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
		
		var img = new Element("img", {
			"src": this.tree.path+this.tree.options.style+"/"+this.imgs.blank,
			"width": this.operateNode.getStyle("width"),
			"height": this.operateNode.getStyle("height"),
			"border": "0",
            "styles": {
                //"margin-top": "6px"
            }
		}).inject(this.operateNode);
		
	},
	createIconNode: function(){
		if (this.options.icon){
			this.iconNode = new Element("td",{
				"styles": this.tree.css.iconNode
			}).inject(this.nodeArea);

            this.iconNode.setStyle("background", "url("+this.tree.path+this.tree.options.style+"/"+this.options.icon+") center center no-repeat");
            //
			//var img = new Element("img",{
			//	"src": this.tree.path+this.tree.options.style+"/"+this.options.icon
			//});
			//img.inject(this.iconNode);
		}
		
	},
	createTextNode: function(){
		this.textNode = new Element("td",{
			"styles": this.tree.css.textNode
		}).inject(this.nodeArea);
		
	//	var width = this.tree.container.getSize().x - (this.level*20+40);
	//	this.textNode.setStyle("width", ""+width+"px");
		
		var textDivNode = new Element("div", {
			"styles": {"display": "inline-block"},
		//	"html": this.options.text,
			"title": this.options.title
		});
		textDivNode.setStyles(this.tree.css.textDivNode);

        if (this.tree.options.text=="html"){
            textDivNode.set("html", this.options.text);
        }else{
            textDivNode.set("text", this.options.text);
        }
		
		textDivNode.addEvent("click", function(e){
			this.clickNode(e);
		}.bind(this));
		
		textDivNode.inject(this.textNode);
	},
	clickNode: function(e){
		this.selectNode(e);
		this.doAction(e);
	},
	
	selectNode: function(){
		if (this.tree.currentNode){
			var textDivNode = this.tree.currentNode.textNode.getElement("div");
			textDivNode.setStyles(this.tree.css.textDivNode);
		}
		var textDivNode = this.textNode.getElement("div");
		textDivNode.setStyles(this.tree.css.textDivNodeSelected);
		this.tree.currentNode = this;
	},
	doAction: function(e){
		if (typeOf(this.options.action)=="string"){
			Browser.exec(this.options.action);
		}else if(typeOf(this.options.action)=="function"){
			this.options.action.apply(this, [this]);
		}
	},
	setOperateIcon: function(){
		var imgStr = (this.options.expand) ? this.imgs.expand : this.imgs.collapse;
		imgStr = this.tree.path+this.tree.options.style+"/"+imgStr;
		if (!this.firstChild) imgStr = this.tree.path+this.tree.options.style+"/"+this.imgs.blank;
		
		var img = this.operateNode.getElement("img");
		if (!img){
			img = new Element("img", {
				"src": imgStr,
				"width": this.operateNode.getStyle("width"),
				"height": this.operateNode.getStyle("height"),
				"border": "0"
			}).inject(this.operateNode);
		}else{
			img.set("src", imgStr);
		}
	},
	insertChild: function(obj){
		var treeNode = new o2.widget.Tree.Node(this.tree, obj);
		
		var tmpTreeNode = this.previousSibling;

		this.previousSibling = treeNode;
		treeNode.nextSibling = this;
		treeNode.previousSibling = tmpTreeNode;
		if (tmpTreeNode){
			tmpTreeNode.nextSibling = treeNode;
		}else{
			this.parentNode.firstChild = treeNode;
		}
		
		treeNode.parentNode = this.parentNode;
		treeNode.level = this.level;
		
		treeNode.load();
		treeNode.node.inject(this.node, "before");
		this.parentNode.children.push(treeNode);
		
		return treeNode;
	},
	appendChild: function(obj){
		var treeNode = new o2.widget.Tree.Node(this.tree, obj);
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
		}		
		this.node.destroy();
		delete this;
	}
});
