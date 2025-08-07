MWF.xApplication.process.FormDesigner.Module = MWF.xApplication.process.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.$ElElement", null, false);
MWF.xApplication.process.FormDesigner.Module.Elcolorpicker = MWF.FCElcolorpicker = new Class({
	Extends: MWF.FC$ElElement,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"propertyPath": "../x_component_process_FormDesigner/Module/Elcolorpicker/elcolorpicker.html"
	},

	_initModuleType: function(){
		this.className = "Elcolorpicker";
		this.moduleType = "element";
		this.moduleName = "elcolorpicker";
	},
	_createElementHtml: function(){
		var html = "<el-color-picker";
		html += " disabled";
		html += " :readonly=\"isReadonly\"";
		html += " :size=\"size\"";

		if (this.json.elProperties){
			Object.keys(this.json.elProperties).forEach(function(k){
				if (this.json.elProperties[k]) html += " "+k+"=\""+this.json.elProperties[k]+"\"";
			}, this);
		}

		html += " :style=\"tmpElStyles\">";
		if (this.json.vueSlot) html += this.json.vueSlot;
		html += "</el-color-picker>";
		return html;

		// return "<el-color-picker></el-color-picker>";
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
		// if (this.json.name){
		// 	var input = this.node.getElement("input");
		// 	if (input) input.set("value", this.json.name);
		// }
	},
	setPropertyId: function(){
		// if (!this.json.name){
		// 	var input = this.node.getElement("input");
		// 	if (input) input.set("value", this.json.id);
		// }
	}
});
