o2.widget = o2.widget || {};
//o2.require("o2.widget.Panel", null, false);
o2.widget.JsonView = new Class({
	Implements: [Options, Events],
	Extends: o2.widget.Common,
	options: {
		"title": "JsonView",
		"style": "default",
		"width": 400,
		"height": 400,
		"top": 0,
		"left": 0,
		
		"isMove": true,
		"isClose": true,
		"isMax": true,
		"isExpand": true,
		"isResize": true,
		
		"target": null
	},
	initialize: function(json, options){
		this.setOptions(options);
		
		this.json = json;
		this.jsonString = JSON.encode(json);
		this.node = new Element("div");
	},
	load: function(){
		if (this.fireEvent("queryLoad")){
			o2.require("o2.widget.Panel", function(){
				this.jsonObjectNode = this.createJsonObjectNode();
				this.jsonStringNode = this.createJsonStringNode();
				
				o2.require("o2.widget.Tab", function(){
					this.tab = new o2.widget.Tab(this.node, {"style": "moduleList"});
					this.tab.load();
					this.objectTabPage = this.tab.addTab(this.jsonObjectNode, "JSON", false);
					this.stringTabPage = this.tab.addTab(this.jsonStringNode, "Text", false);
					this.objectTabPage.showTab();
				}.bind(this));
				
				
				this.options["onResize"] = function(){
					this.setPanelSize();
				}.bind(this);
				this.jsonPanel = new o2.widget.Panel(this.node, this.options);
				this.jsonPanel.load();
				this.jsonPanel.show();
				
				this.setPanelSize();
			}.bind(this));
			
			this.fireEvent("postLoad");
		}
	},
	
	setPanelSize: function(){
		var contentSize = this.jsonPanel.content.getSize();
		var paddingTop = this.jsonPanel.content.getStyle("padding-top").toFloat();
		var paddingBottom = this.jsonPanel.content.getStyle("padding-bottom").toFloat();
		var tabSize = this.tab.tabNodeContainer.getSize();
		var marginTop = this.tab.tabNodeContainer.getStyle("margin-top").toFloat();
		var marginBottom = this.tab.tabNodeContainer.getStyle("margin-bottom").toFloat();
		
		var contentMarginTop = this.stringTabPage.contentNodeArea.getStyle("margin-top").toFloat();
		var contentMarginBottom = this.stringTabPage.contentNodeArea.getStyle("margin-bottom").toFloat();

		var contentHeight = contentSize.y-paddingTop-paddingBottom-tabSize.y-marginTop-marginBottom-contentMarginTop-contentMarginBottom-5;
		if (contentHeight<10) contentHeight = 10;
		
		this.jsonStringNode.setStyle("height", contentHeight);
	//	this.jsonStringNode.setStyle("width", contentSize.x-32);
		this.jsonObjectNode.setStyle("height", contentHeight-20);
	},
	
	createJsonObjectNode: function(){
		this.jsonObjectNode = new Element("div", {
			"styles": {
				"overflow": "hideen",
				"margin-top": "0px",
				"height": "auto"
			}
		});
		this.loadObjectTree();
		return this.jsonObjectNode;
	},
	loadObjectTree: function(){
		if (this.objectTree){
			this.objectTree.node.destroy();
			this.objectTree = null;
		} 
		o2.require("o2.widget.Tree", function(){
			this.objectTree = new o2.widget.Tree(this.jsonObjectNode, {"style": "jsonview"});
			this.objectTree.load();
			
			this.parseJsonObject(this.objectTree, "JSON", this.json, true);
			
//			var topNode = this.objectTree.appendChild({
//				"expand": true,
//				"title": "",
//				"text": "JSON",
//				"action": "",
//				"icon": "object.png"
//			});
//			
//			for (p in this.json){
//				this.parseJsonObject(topNode, p, this.json[p]);
//			}
//			
			
		}.bind(this));
	},
	
	parseJsonObject: function(treeNode, p, v, expand){
		var o = {
			"expand": expand,
			"title": "",
			"text": "",
			"action": "",
			"icon": ""
		};
		
		switch (typeOf(v)){
			case "object":
				o.text = p;
				o.icon = "object.png";
				var node = treeNode.appendChild(o);
				
				for (i in v){
					this.parseJsonObject(node, i, v[i], false);
				}
				break;
				
			case "array":
				o.text = p;
				o.icon = "array.png";
				var node = treeNode.appendChild(o);
				
				v.each(function(item, idx){
					this.parseJsonObject(node, "["+idx+"]", item,false);
				}.bind(this));
				break;
				
			default: 
				o.text = p + " : "+v;
				o.icon = "string.png";
				var node = treeNode.appendChild(o);
		}
	},
	
	createJsonStringNode: function(){
		var jsonStringNode = new Element("textarea", {
			"readonly": false,
			"styles": {
				"width": "99%",
				//"width": "95%",
				"overflow": "auto",
				"border": "0px"
			}
		});
		jsonStringNode.set("text", JSON.format(this.json));
		return jsonStringNode;
	}
});