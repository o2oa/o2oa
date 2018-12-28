o2.widget = o2.widget || {};
o2.require("o2.widget.Common", null, false);
o2.widget.Tree = new Class({
	Extends: o2.widget.Common,
	Implements: [Options, Events],
	children: [],
	options: {
		"style": "default",
		"expand": false,
        "text": "html"
	},
	jsonMapping: {
		"expand": "expand",
		"title": "title",
		"text": "text",
		"action": "action",
		"icon": "icon",
        "style": "",
		"sub": "sub"
	},
	initialize: function(container, options){
		this.setOptions(options);

		this.path = o2.session.path+"/widget/$Tree/";
		this.cssPath = o2.session.path+"/widget/$Tree/"+this.options.style+"/css.wcss";
		this._loadCss();

		this.container = $(container);
		this.children = [];
		this.treeJson = null;
		this.treeXML = null;
	},
	load: function(json){
		if (this.fireEvent("queryLoad")){
			
			if (json) this.treeJson = json;
			
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
	reLoad: function(json){
		if (json) this.treeJson = json;
		this.children = [];
		this.node.empty();
		this.loadTree();
	},
	loadTree: function(){
		if (this.treeJson){
			this.loadJsonTree(this.treeJson, this, this);
		}else if (this.treeXML){
			this.loadXMLTree();
		}
		if (this.container) this.node.inject(this.container);
	},
	
	mappingJson: function(mapping){
		if (mapping.expand) this.jsonMapping.expand = mapping.expand;
		if (mapping.title) this.jsonMapping.title = mapping.title;
		if (mapping.text) this.jsonMapping.text = mapping.text;
		if (mapping.action) this.jsonMapping.action = mapping.action;
		if (mapping.icon) this.jsonMapping.icon = mapping.icon;
        if (mapping.style) this.jsonMapping.style = mapping.style;
		if (mapping.sub) this.jsonMapping.sub = mapping.sub;
	},
	
	loadJsonTree: function(treeJson, tree, node){
		treeJson.each(function(item){
			var options = {};
			if (item[this.jsonMapping.expand]!=undefined) options.expand = item[this.jsonMapping.expand];
			if (item[this.jsonMapping.title]) options.title = item[this.jsonMapping.title];
			if (item[this.jsonMapping.text]) options.text = item[this.jsonMapping.text];
			if (item[this.jsonMapping.action]) options.action = item[this.jsonMapping.action];
            if (item[this.jsonMapping.style]) options.style = item[this.jsonMapping.style];
			if (item[this.jsonMapping.icon]) options.icon = item[this.jsonMapping.icon];
			
			var treeNode = node.appendChild(options);

			if (item[this.jsonMapping.sub]){
				this.loadJsonTree(item[this.jsonMapping.sub], this, treeNode);
			}
		}.bind(tree));
	},
	
	appendChild: function(obj){
		var treeNode = new this.$constructor.Node(this, obj);
		
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
	},
	toJson: function(item){
		var json=null;
		var node = item.firstChild;
		
		json=[];
		while (node){
			json.push(this.transObj(node.options));
			json[json.length-1].sub = this.toJson(node);
			
			node = node.nextSibling;
		}
		

		return json;
	},
	transObj: function(options){
		var obj = {};
		obj[this.jsonMapping.expand] = options.expand;
		obj[this.jsonMapping.title] = options.title;
		obj[this.jsonMapping.text] = options.text;
		obj[this.jsonMapping.action] = options.action;
        obj[this.jsonMapping.style] = options.style;
		obj[this.jsonMapping.icon] = (options.icon) ? options.icon : "none";
		return obj;
	}

});

o2.widget.Tree.Node = new Class({
	Implements: [Options, Events],
	options: {
		"expand": true,
		"title": "",
		"text": "",
		"action": "",
        "style": "",
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

        if (this.options.style){
            if (this.tree.css[this.options.style]){
                this.node.setStyles(this.tree.css[this.options.style].treeNode);
                this.itemNode.setStyles(this.tree.css[this.options.style].treeItemNode);
                this.childrenNode.setStyles(this.tree.css[this.options.style].treeChildrenNode);
            }
        }

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
			"styles": this.tree.css.nodeTable
		}).inject(this.itemNode);

        if (this.options.style){
            if (this.tree.css[this.options.style]){
                this.nodeTable.setStyles(this.tree.css[this.options.style].nodeTable);
            }
        }

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
            if (this.options.style){
                if (this.tree.css[this.options.style]){
                    td.setStyles(this.tree.css[this.options.style].blankLevelNode);
                }
            }
			this.levelNode.push(td);
		}
	},
	createOperateNode: function(){
		this.operateNode = new Element("td",{
			"styles": this.tree.css.operateNode
		}).inject(this.nodeArea);

        if (this.options.style){
            if (this.tree.css[this.options.style]){
                this.operateNode.setStyles(this.tree.css[this.options.style].operateNode);
            }
        }

		this.operateNode.addEvent("click", function(){
			this.expandOrCollapse();
		}.bind(this));

        this.operateNode.setStyle("background", "url("+this.tree.path+this.tree.options.style+"/"+this.imgs.blank+") center center no-repeat");

		//var img = new Element("img", {;
		//	"src": this.tree.path+this.tree.options.style+"/"+this.imgs.blank,
		//	"width": this.operateNode.getStyle("width"),
		//	"height": this.operateNode.getStyle("height"),
		//	"border": "0",
         //   "styles": {
         //       //"margin-top": "6px"
         //   }
		//}).inject(this.operateNode);
		
	},
	createIconNode: function(){
		if (this.options.icon){
			this.iconNode = new Element("td",{
				"styles": this.tree.css.iconNode
			}).inject(this.nodeArea);
            if (this.options.style){
                if (this.tree.css[this.options.style]){
                    this.iconNode.setStyles(this.tree.css[this.options.style].iconNode);
                }
            }
            this.iconNode.setStyle("background", "url("+this.tree.path+this.tree.options.style+"/"+this.options.icon+") center center no-repeat");
		}
	},
	createTextNode: function(){
		this.textNode = new Element("td",{
			"styles": this.tree.css.textNode
		}).inject(this.nodeArea);
        if (this.options.style){
            if (this.tree.css[this.options.style]){
                this.textNode.setStyles(this.tree.css[this.options.style].textNode);
            }
        }
	//	var width = this.tree.container.getSize().x - (this.level*20+40);
	//	this.textNode.setStyle("width", ""+width+"px");
		
		var textDivNode = new Element("div", {
			"styles": this.tree.css.textDivNode,
		//	"html": this.options.text,
			"title": this.options.title
		});
        if (this.options.style){
            if (this.tree.css[this.options.style]){
                textDivNode.setStyles(this.tree.css[this.options.style].textDivNode);
            }
        }

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
            if (this.tree.currentNode.options.style){
                if (this.tree.css[this.tree.currentNode.options.style]){
                    textDivNode.setStyles(this.tree.css[this.tree.currentNode.options.style].textDivNode);
                }
            }
		}
		var textDivNode = this.textNode.getElement("div");
		textDivNode.setStyles(this.tree.css.textDivNodeSelected);
        if (this.options.style){
            if (this.tree.css[this.options.style]){
                textDivNode.setStyles(this.tree.css[this.options.style].textDivNodeSelected);
            }
        }
		this.tree.currentNode = this;
	},
	doAction: function(e){
		var t = typeOf(this.options.action);
		if (t=="string"){
			Browser.exec(this.options.action);
		}else if(t=="function"){
			this.options.action.apply(this, [this]);
		}else if(t=="object"){
			if (this.tree.form){
                this.tree.form.Macro.exec(this.options.action.code, this)
			}
            //this.options.action.apply(this, [this]);
        }
	},
	setOperateIcon: function(){
		var imgStr = (this.options.expand) ? this.imgs.expand : this.imgs.collapse;
		imgStr = this.tree.path+this.tree.options.style+"/"+imgStr;
		if (!this.firstChild) imgStr = this.tree.path+this.tree.options.style+"/"+this.imgs.blank;

        this.operateNode.setStyle("background", "url("+imgStr+") center center no-repeat");

		//var img = this.operateNode.getElement("img");
		//if (!img){
		//	img = new Element("img", {
		//		"src": imgStr,
		//		"width": this.operateNode.getStyle("width"),
		//		"height": this.operateNode.getStyle("height"),
		//		"border": "0"
		//	}).inject(this.operateNode);
		//}else{
		//	img.set("src", imgStr);
		//}
	},
	insertChild: function(obj){
		var treeNode = new this.tree.$constructor.Node(this.tree, obj);
		
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
		var treeNode = new this.tree.$constructor.Node(this.tree, obj);
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