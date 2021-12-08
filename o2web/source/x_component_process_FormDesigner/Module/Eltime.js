MWF.xApplication.process.FormDesigner.Module = MWF.xApplication.process.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.$ElElement", null, false);
MWF.xApplication.process.FormDesigner.Module.Eltime = MWF.FCEltime = new Class({
	Extends: MWF.FC$ElElement,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"propertyPath": "../x_component_process_FormDesigner/Module/Eltime/eltime.html"
	},

	_initModuleType: function(){
		this.className = "Eltime";
		this.moduleType = "element";
		this.moduleName = "eltime";
	},
	_createElementHtml: function() {
		debugger;
		if (this.json.timeSelectType === "select"){
			if (this.json.isRange ) {
				return this.createSelectRangeElementHtml();
			} else {
				return this.createSelectElementHtml();
			}
		}else{
			if (this.json.isRange) {
				return this.createPickerRangeElementHtml();
			} else {
				return this.createPickerElementHtml();
			}
		}
	},
	getCommonHtml: function(){
		var html = "";
		html += " readonly";
		html += " :clearable=\"clearable\"";
		html += " :size=\"size\"";
		html += " :prefix-icon=\"prefixIcon\"";
		html += " :suffix-icon=\"suffixIcon\"";

		if (this.json.elProperties){
			Object.keys(this.json.elProperties).forEach(function(k){
				if (this.json.elProperties[k]) html += " "+k+"=\""+this.json.elProperties[k]+"\"";
			}, this);
		}

		html += " :style=\"elStyles\">";
		if (this.json.vueSlot) html += this.json.vueSlot;
		return html;
	},
	createSelectElementHtml: function(){
		var html = "<el-time-select";
		html += " :value=\"id\"";
		html += this.getCommonHtml();
		html += "</el-time-select>";
		return html;
	},
	createSelectRangeElementHtml: function(){
		var html = "<el-time-select";
		html += " :value=\"id\"";
		html += this.getCommonHtml();
		html += "</el-time-select>";

		html += "<span style='padding: 0px 5px;'>"+this.json.rangeSeparator+"</span>";

		html += "<el-time-select";
		html += " :value=\"id\"";
		html += this.getCommonHtml();
		html += "</el-time-select>";
		return html;
	},
	createPickerElementHtml: function(){
		var html = "<el-time-picker";
		html += " placeholder="+this.json.id;
		html += this.getCommonHtml();
		html += "</el-time-picker>";
		return html;
	},
	createPickerRangeElementHtml: function(){
		var html = "<el-time-picker";
		html += " is-range";
		html += " :range-separator=\"rangeSeparator\"";
		html += " start-placeholder="+this.json.id;
		html += " end-placeholder="+this.json.id;
		html += this.getCommonHtml();
		html += "</el-time-picker>";
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
	_setEditStyle_custom: function(name){
		switch (name){
			case "name": this.setPropertyName(); break;
			case "id": this.setPropertyId(); break;
			case "buttonRadio":
			case "vueSlot":
				if (this.isPropertyLoaded) if (this.vm) this.resetElement(); break;
			case "isRange":
			case "timeSelectType":
				if (this.isPropertyLoaded) if (this.vm) this.resetElement(); break;
			default: break;
		}
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
