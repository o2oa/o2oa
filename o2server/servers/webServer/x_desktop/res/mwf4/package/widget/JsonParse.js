MWF.widget = MWF.widget || {};
MWF.widget.JsonParse = new Class({
	initialize: function(json, jsonObjectNode, jsonStringNode){
		this.json = json;
		this.jsonObjectNode = jsonObjectNode;
		this.jsonStringNode = jsonStringNode;
		this.stopParseJson = false;
	},
	load: function(){
		this.jsonString = JSON.encode(this.json);
		if (this.jsonStringNode) this.jsonStringNode.set("text", JSON.format(this.json));
		if (this.jsonObjectNode) this.loadObjectTree();
	},
	loadObjectTree: function(){
		if (this.objectTree){
			this.objectTree.node.destroy();
			this.objectTree = null;
		} 
		MWF.require("MWF.widget.Tree", function(){
			this.objectTree = new MWF.widget.Tree(this.jsonObjectNode, {"style": "jsonview", "text": "text"});
			this.objectTree.load();
			
			var str = this.parseJsonObject(0, this.objectTree, "",  "JSON", this.json, true);
			var jsonStr = str.substring(0, str.length-2);
			if (!this.stopParseJson){
				if (this.jsonStringNode) this.jsonStringNode.set("text", jsonStr);
			}else{
				this.stopParseJson = false;
			}
			
		}.bind(this));
	},
	
	parseJsonObject: function(level, treeNode, title, p, v, expand){
		if (this.stopParseJson){
		//	alert(this.stopParseJson);
			return false;
		}
		var o = {
			"expand": expand,
			"title": "",
			"text": "",
			"action": "",
			"icon": ""
		};
		var tab = "";
		for (var i=0; i<level; i++) tab+="  ";
		var title = title;
		if (title) title="\""+title+"\": ";
		var jsonStr = "";
		var nextLevel = level+1;
		
		switch (typeOf(v)){
			case "object":
				o.text = p;
				o.icon = "object.png";
				var node = treeNode.appendChild(o);
								
				var jsonStrBegin = tab+title+"{";
				var jsonStrEnd = tab+"}";
				for (i in v){
					jsonStr += this.parseJsonObject(nextLevel, node, i, i, v[i], false);
				}
				jsonStr = jsonStrBegin+"\n"+jsonStr.substring(0, jsonStr.length-2)+"\n"+jsonStrEnd+",\n";
				break;
				
			case "array":
				o.text = p;
				o.icon = "array.png";
				var node = treeNode.appendChild(o);
				
				var jsonStrBegin = tab+title+"[";
				var jsonStrEnd = tab+"]";
				
				v.each(function(item, idx){
					jsonStr += this.parseJsonObject(nextLevel, node, "", "["+idx+"]", item, false);
				}.bind(this));
				
				jsonStr = jsonStrBegin+"\n"+jsonStr.substring(0, jsonStr.length-2)+"\n"+jsonStrEnd+",\n";
				break;
				
			case "string":	
				jsonStr += tab+title+"\""+v+"\",\n";
				
				o.text = p + " : \""+v+"\"";
				o.icon = "string.png";
				var node = treeNode.appendChild(o);
				
				break;
			case "date":	
				jsonStr += tab+title+"\""+v+"\",\n";
				o.text = p + " : \""+v+"\"";
				o.icon = "string.png";
				var node = treeNode.appendChild(o);
				break;
				
			default: 
				jsonStr += tab+title+v+",\n";
				o.text = p + " : "+v;
				o.icon = "string.png";
				var node = treeNode.appendChild(o);
		}
		return jsonStr;
	}
});