o2.widget = o2.widget || {};
o2.require("o2.widget.Common", null, false);
o2.require("o2.widget.Tree", null, false);
o2.widget.LeftNavi = new Class({
	Extends: o2.widget.Tree,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"expand": false
	},
	jsonMapping: {
		"expand": "expand",
		"title": "title",
		"text": "text",
		"action": "action",
		"icon": "icon",
		"sub": "sub"
	},
	
	initialize: function(container, options){
		this.setOptions(options);

		this.path = o2.session.path+"/widget/$LeftNavi/";
		this.cssPath = o2.session.path+"/widget/$LeftNavi/"+this.options.style+"/css.wcss";
		this._loadCss();

		this.container = $(container);
		this.children = [];
		this.treeJson = null;
		this.treeXML = null;
	},
	
	appendChild: function(obj){
		var treeNode = new o2.widget.LeftNavi.Menu(this, obj);
		
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
	
	expand: function(treeNode){
		if (this.fireEvent("queryExpand", [treeNode])){
			treeNode.childrenNode.setStyle("display", "block");
		}
		this.fireEvent("postExpand", [treeNode]);
	},
	collapse: function(treeNode){
		if (this.fireEvent("queryCollapse", [treeNode])){
			treeNode.childrenNode.setStyle("display", "none");
		}
		this.fireEvent("postCollapse", [treeNode]);
	}

});
o2.widget.LeftNavi.Menu = new Class({
	Extends: o2.widget.Tree.Node,
	Implements: [Options, Events],
	
	insertChild: function(obj){
		var treeNode = new o2.widget.LeftNavi.Node(this.tree, obj);
		
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
		var treeNode = new o2.widget.LeftNavi.Node(this.tree, obj);
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
	load: function(){
		this.nodeTable = new Element("table", {
			"border": "0",
			"cellpadding": "0",
			"cellspacing": "0",
			"styles": this.tree.css.nodeTable
		}).inject(this.itemNode);
		var tbody = new Element("tbody").inject(this.nodeTable);
		this.nodeArea = new Element("tr").inject(tbody);
		
		this.createLevelNode();
		this.createOperateNode();
		this.createIconNode();
		this.createTextNode();

		this.setOtherEvent();
	},
	setOtherEvent: function(){
		this.operateNode.removeEvents("click");
		this.textNode.getFirst("div").removeEvents("click");
		this.itemNode.addEvent("click", function(){
			this.clickNode();
		}.bind(this));
	},
	clickNode: function(){
		this.expand();
	},
	expand: function(){
		if (this.tree.currentMenu != this){
			this.selectNode();
		}
	},
	selectNode: function(){
		if (this.tree.currentMenu){
			this.tree.currentMenu.itemNode.setStyles(this.tree.css.treeItemNode);
			this.tree.currentMenu.collapseNode();
		}
		this.itemNode.setStyles(this.tree.css.treeItemNodeSelected);
		this.expandNode();
		this.tree.currentMenu = this;
	},
	expandNode: function(){	
		var morph = new Fx.Morph(this.childrenNode, {duration: 100});
		this.childrenNode.setStyles({
			"display": "block",
			"height": "0px"
		});
		morph.start({
			"height": this.children.length*40
		}).chain(function(){}.bind(this));
	},
	collapseNode: function(){
		var morph = new Fx.Morph(this.childrenNode, {duration: 100});
		morph.start({
			"height": 0
		}).chain(function(){
			this.childrenNode.setStyle("display", "none");
		}.bind(this));
	}
	
	
});
o2.widget.LeftNavi.Node = new Class({
	Extends: o2.widget.Tree.Node,
	Implements: [Options, Events],
	
	insertChild: function(obj){
		var treeNode = new o2.widget.LeftNavi.Node(this.tree, obj);
		
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
		var treeNode = new o2.widget.LeftNavi.Node(this.tree, obj);
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
	load: function(){
		this.nodeTable = new Element("table", {
			"border": "0",
			"cellpadding": "0",
			"cellspacing": "0",
			"styles": this.tree.css.nodeTable
		}).inject(this.itemNode);
		var tbody = new Element("tbody").inject(this.nodeTable);
		this.nodeArea = new Element("tr").inject(tbody);
		
		this.createLevelNode();
		this.createOperateNode();
		this.createIconNode();
		this.createTextNode();
		
		this.setLevelStyle();
		this.setOtherEvent();
	},
	setOtherEvent: function(){
		if (this.level==0){
			this.operateNode.removeEvents("click");
			this.itemNode.addEvent("click", function(){
				this.menuClickNode();
			}.bind(this));
		}
	},
	
	menuClickNode: function(){
		this.selectNode();
		this.expandOrCollapse();
	},
	
	selectNode: function(){
		if (this.tree.currentNode){
			var itemNode = this.tree.currentNode.itemNode;
			if (this.level>0){
				itemNode.setStyles(this.tree.css.treeItemNodeSub);
			}else{
				itemNode.setStyles(this.tree.css.treeItemNode);
			}
		}
		var itemNode = this.itemNode;
		if (this.level>0){
			itemNode.setStyles(this.tree.css.treeItemNodeSelectedSub);
		}else{
			itemNode.setStyles(this.tree.css.treeItemNodeSelected);
		}
		this.tree.currentNode = this;
	},
	
	setLevelStyle: function(){
		if (this.tree.css.treeNodeSub) this.node.setStyles(this.tree.css.treeNodeSub);
		if (this.tree.css.treeItemNodeSub) this.itemNode.setStyles(this.tree.css.treeItemNodeSub);
		if (this.tree.css.treeChildrenNodeSub) this.childrenNode.setStyles(this.tree.css.treeChildrenNodeSub);
		if (this.tree.css.nodeTableSub) this.nodeTable.setStyles(this.tree.css.nodeTableSub);
		if (this.tree.css.textDivNodeSub) this.textNode.getFirst("div").setStyles(this.tree.css.textDivNodeSub);
		if (this.tree.css.iconNodeSub) this.iconNode.setStyles(this.tree.css.iconNodeSub);

		
		if (this.tree.css.blankLevelNodeSub){
			this.levelNode.each(function(node){
				node.setStyles(this.tree.css.blankLevelNodeSub);
			});
		}
	}
	
});