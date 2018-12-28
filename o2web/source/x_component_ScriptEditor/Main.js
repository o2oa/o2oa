//MWF.require("MWF.widget.JavascriptEditor", null, false);
MWF.xDesktop.requireApp("ScriptEditor", "Editor", null, false);
MWF.xApplication.ScriptEditor.Main = new Class({
	Extends: MWF.xApplication.Common.Main,
	Implements: [Options, Events],

	options: {
		"style": "default",
		"name": "ScriptEditor",
		"icon": "icon.png",
		"width": "1000",
		"height": "600",
		"isResize": true,
		"title": MWF.xApplication.ScriptEditor.LP.title
	},
	onQueryLoad: function(){
		this.lp = MWF.xApplication.ScriptEditor.LP;
	},
	createNode: function(){
		this.content.setStyle("overflow", "hidden");
		this.node = new Element("div", {
			"styles": this.css.contentNode
		}).inject(this.content);
	},

	loadApplication: function(callback){
		this.createNode();
		this.editor = new MWF.xApplication.ScriptEditor.Editor(this.node, this);
        this.editor.load();
		// this.editor = new MWF.widget.JavascriptEditor(this.node);
		// this.editor.load();
		//this.node.set("html", "12356<br/>12356<br/>12356<br/>12356<br/>12356<br/>12356<br/>12356<br/>12356<br/>12356<br/>12356<br/>");
	}

});
