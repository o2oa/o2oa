MWF.xApplication.process.FormDesigner.Module = MWF.xApplication.process.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.$Element", null, false);
MWF.xApplication.process.FormDesigner.Module.Button = MWF.FCButton = new Class({
	Extends: MWF.FC$Element,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"propertyPath": "/x_component_process_FormDesigner/Module/Button/button.html"
	},
	
	initialize: function(form, options){
		this.setOptions(options);
		
		this.path = "/x_component_process_FormDesigner/Module/Button/";
		this.cssPath = "/x_component_process_FormDesigner/Module/Button/"+this.options.style+"/css.wcss";

		this._loadCss();
		this.moduleType = "element";
		this.moduleName = "button";
		
		this.form = form;
		this.container = null;
		this.containerNode = null;
	},
	
	_createMoveNode: function(){
		this.moveNode = new Element("div", {
			"MWFType": "button",
			"id": this.json.id,
			"styles": this.css.moduleNodeMove,
			"events": {
				"selectstart": function(){
					return false;
				}
			}
		}).inject(this.form.container);
		var button = new Element("button", {
			"styles": this.css.buttonIcon,
			"text": this.json.name || this.json.id
		}).inject(this.moveNode);
	},
	_loadNodeStyles: function(){
		var button = this.node.getFirst("button");
		if (!button) button = this.node.getFirst("input");
        if (button)button.setStyles(this.css.buttonIcon);
	},
	
	unSelected: function(){
		this.node.setStyles({
			"border-top": "1px solid #999",
			"border-left": "1px solid #999",
			"border-right": "1px solid #333",
			"border-bottom": "1px solid #333"
		});
		if (this.actionArea) this.actionArea.setStyle("display", "none");
		this.form.currentSelectedModule = null;
		
		this.hideProperty();
	},
	unOver: function(){
		if (!this.form.moveModule) if (this.form.currentSelectedModule!=this) this.node.setStyles({
			"border-top": "1px solid #999",
			"border-left": "1px solid #999",
			"border-right": "1px solid #333",
			"border-bottom": "1px solid #333"
		});
	},
	_createCopyNode: function(){
		this.copyNode = new Element("div", {
			"styles": this.css.moduleNodeShow
		});
		this.copyNode.addEvent("selectstart", function(){
			return false;
		});
	},
	_getCopyNode: function(){
		if (!this.copyNode) this._createCopyNode();
		this.copyNode.setStyle("display", "inline-block");
		return this.copyNode;
	},
	
	_setEditStyle_custom: function(name){
		if (name=="name"){
			if (this.json.name){
				var button = this.node.getElement("button");
                if (!button) button = this.node.getFirst("input");
				if (button) button.set("text", this.json.name);
			}
		}
		if (name=="id"){
			if (!this.json.name){
				var button = this.node.getElement("button");
                if (!button) button = this.node.getFirst("input");
                if (button) button.set("text", this.json.id);
			}
		}
	}

	
});
