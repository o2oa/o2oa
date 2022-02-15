MWF.xApplication.process.FormDesigner.Module = MWF.xApplication.process.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.$ElElement", null, false);
MWF.xApplication.process.FormDesigner.Module.Eldatetime = MWF.FCEldatetime = new Class({
	Extends: MWF.FC$ElElement,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"propertyPath": "../x_component_process_FormDesigner/Module/Eldatetime/eldatetime.html"
	},

	_initModuleType: function(){
		this.className = "Eldatetime";
		this.moduleType = "element";
		this.moduleName = "eldatetime";
	},
	_createElementHtml: function(){

		var html = "<el-date-picker";
		html += " :type=\"selectType\"";
		html += " :size=\"size\"";
		html += " :prefix-icon=\"prefixIcon\"";
		html += " :range-separator=\"rangeSeparator\"";
		html += " readonly";
		html += " :placeholder=\"id\"";
		html += " :start-placeholder=\"id\"";
		html += " :end-placeholder=\"id\"";

		if (this.json.elProperties){
			Object.keys(this.json.elProperties).forEach(function(k){
				if (this.json.elProperties[k]) html += " "+k+"=\""+this.json.elProperties[k]+"\"";
			}, this);
		}

		html += " :style=\"elStyles\">";
		if (this.json.vueSlot) html += this.json.vueSlot;
		html += "</el-date-picker>";
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
