MWF.xApplication.process.FormDesigner.Module = MWF.xApplication.process.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.Checkbox", null, false);
MWF.xApplication.process.FormDesigner.Module.Elcheckbox = MWF.FCElcheckbox = new Class({
	Extends: MWF.FC$ElElement,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"type": "elcheckbox",
		"path": "../x_component_process_FormDesigner/Module/Elcheckbox/",
		"propertyPath": "../x_component_process_FormDesigner/Module/Elcheckbox/elcheckbox.html"
	},
	_initModuleType: function(){
		this.className = "Elcheckbox";
		this.moduleType = "element";
		this.moduleName = "elcheckbox";
	},
	_createElementHtml: function(){
		var html = "<el-checkbox-group v-model=\""+this.json.id+"\"";

		html += " :text-color=\"textColor\"";
		html += " :fill=\"fillColor\"";
		html += " :size=\"size\"";
		html += " :style=\"elGroupStyles\"";

		html += (this.json.buttonRadio) ? " ><el-checkbox-button" : " ><el-checkbox";
		html += " :border=\"border\"";

		html += " :style=\"elStyles\"";

		html += " :label=\"id\">{{id}}"+((this.json.buttonRadio) ? "</el-checkbox-button></el-checkbox-group>" : "</el-checkbox></el-checkbox-group>");
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
	setPropertyId: function(){
		if (this.isPropertyLoaded) if (this.vm) this.resetElement();
	},
	// _createVueData: function(){
	// 	var data = {};
	// 	data[this.json.id] = [this.json.id];
	// 	return data;
	// },
});
