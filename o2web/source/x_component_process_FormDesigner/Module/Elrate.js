MWF.xApplication.process.FormDesigner.Module = MWF.xApplication.process.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.$ElElement", null, false);
MWF.xApplication.process.FormDesigner.Module.Elrate = MWF.FCElrate = new Class({
	Extends: MWF.FC$ElElement,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"propertyPath": "../x_component_process_FormDesigner/Module/Elrate/elrate.html"
	},

	_initModuleType: function(){
		this.className = "Elrate";
		this.moduleType = "element";
		this.moduleName = "elrate";
	},
	_createElementHtml: function(){
		var html = "<el-rate";
		html += " disabled";
		html += " :readonly=\"isReadonly\"";
		html += " :max=\"max\"";
		html += " :allow-half=\"allowHalf\"";
		html += " :low-threshold=\"lowThreshold\"";
		html += " :high-threshold=\"highThreshold\"";
		html += " :void-color=\"voidColor\"";
		html += " disabled-void-color=\"#C6D1DE\"";

		if (this.json.elProperties){
			Object.keys(this.json.elProperties).forEach(function(k){
				if (this.json.elProperties[k]) html += " "+k+"=\""+this.json.elProperties[k]+"\"";
			}, this);
		}

		html += " :style=\"elStyles\">";
		if (this.json.vueSlot) html += this.json.vueSlot;
		html += "</el-rate>";
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
			var input = this.node.getElement("input");
			if (input) input.set("value", this.json.name);
		}
	},
	setPropertyId: function(){
		if (!this.json.name){
			var input = this.node.getElement("input");
			if (input) input.set("value", this.json.id);
		}
	}
});
