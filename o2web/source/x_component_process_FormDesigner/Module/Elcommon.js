MWF.xApplication.process.FormDesigner.Module = MWF.xApplication.process.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.$ElElement", null, false);
MWF.xApplication.process.FormDesigner.Module.Elcommon = MWF.FCElcommon = new Class({
	Extends: MWF.FC$ElElement,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"propertyPath": "../x_component_process_FormDesigner/Module/Elcommon/elcommon.html"
	},

	_initModuleType: function(){
		this.className = "Elcommon";
		this.moduleType = "element";
		this.moduleName = "elcommon";
	},
	_createMoveNode: function(){
		this.moveNode = new Element("div", {
			"MWFType": this.moduleName,
			"id": this.json.id,
			"styles": this.css.moduleNodeMove,
			"events": {
				"selectstart": function(){
					return false;
				}
			}
		}).inject(this.form.container);
	},
	_createElementHtml: function(){
		//if (this.styleNode) this.styleNode.destroy();
		var html = this.json.vueTemplate || "";
		return html;
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
	setPropertyName: function(){
		if (this.json.name){
			var button = this.node.getElement("button");
			if (!button) button = this.node.getFirst("input");
			if (button) button.set("text", this.json.name);
		}
	},
	setPropertyId: function(){
		if (!this.json.name){
			var button = this.node.getElement("button");
			if (!button) button = this.node.getFirst("input");
			if (button) button.set("text", this.json.id);
		}
	}
	// _loadVueCss: function(){
	// 	if (this.json.vueCss && this.json.vueCss.code){
	// 		this.styleNode = this.node.loadCssText(this.json.vueCss.code, {"notInject": true});
	// 		this.styleNode.inject(this.node, "top");
	// 	}
	// },
	// _afterMounted: function(el, callback){
	// 	this.node = el;
	// 	this.node.store("module", this);
	// 	this._loadVueCss();
	// 	if (callback) callback();
	// }
});
