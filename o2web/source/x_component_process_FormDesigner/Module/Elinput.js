MWF.xApplication.process.FormDesigner.Module = MWF.xApplication.process.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.$ElElement", null, false);
MWF.xApplication.process.FormDesigner.Module.Elinput = MWF.FCElinput = new Class({
	Extends: MWF.FC$ElElement,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"propertyPath": "../x_component_process_FormDesigner/Module/Elinput/elinput.html"
	},

	_initModuleType: function(){
		this.className = "Elinput";
		this.moduleType = "element";
		this.moduleName = "elinput";
	},
	_createElementHtml: function(){
		//var html = "<el-input placeholder=\"请输入内容\"></el-input>";
		var html = "<el-input";
		if (this.json.description) html += " placeholder=\""+this.json.description+"\"";
		html += " type=\""+(this.json.inputType || "text")+"\"";
		if (this.json.maxlength) html += " maxlength=\""+this.json.maxlength+"\"";
		if (this.json.showWordLimit) html += " show-word-limit";
		if (this.json.clearable) html += " clearable";
		if (this.json.showPassword) html += " show-password";
		if (this.json.size && this.json.size!=="default") html += " size=\""+this.json.size+"\"";
		html += " rows=\""+(this.json.textareaRows || "2")+"\"";

		if (this.json.autosize){
			var o = {};
			if (this.json.minRows) o.minRows = this.json.minRows;
			if (this.json.maxRows) o.maxRows = this.json.maxRows;
			html += " autosize=\""+JSON.stringify(o)+"\"";
		}
		if (this.json.resize) html += " resize=\""+this.json.resize+"\"";
		if (this.json.prefixIcon) html += " prefix-icon=\""+this.json.prefixIcon+"\"";
		if (this.json.suffixIcon) html += " suffix-icon=\""+this.json.suffixIcon+"\"";

		if (this.json.elProperties){
			Object.keys(this.json.elProperties).forEach(function(k){
				if (this.json.elProperties[k]) html += " "+k+"=\""+this.json.elProperties[k]+"\"";
			}, this);
		}

		if (this.json.elStyles){
			var style = "";
			Object.keys(this.json.elStyles).forEach(function(k){
				if (this.json.elStyles[k]) style += k+":"+this.json.elStyles[k]+";";
			}, this);
			html += " style=\""+style+"\"";
		}

		html += " value=\""+this.json.id+"\">";
		if (this.json.vueSlot) html += this.json.vueSlot;
		html += "</el-input>";
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
