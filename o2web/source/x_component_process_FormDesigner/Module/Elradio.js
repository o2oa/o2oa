MWF.xApplication.process.FormDesigner.Module = MWF.xApplication.process.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.Checkbox", null, false);
MWF.xApplication.process.FormDesigner.Module.Elradio = MWF.FCElradio = new Class({
	Extends: MWF.FC$ElElement,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"type": "elradio",
		"path": "../x_component_process_FormDesigner/Module/Elradio/",
		"propertyPath": "../x_component_process_FormDesigner/Module/Elradio/elradio.html"
	},
	_initModuleType: function(){
		this.className = "Elradio";
		this.moduleType = "element";
		this.moduleName = "elradio";
	},
	_createElementHtml: function(){
		var html = "<el-radio-group";
		// if (this.json.textColor) html += " text-color=\""+this.json.textColor+"\"";
		// if (this.json.fillColor) html += " fill=\""+this.json.fillColor+"\"";
		// if (this.json.size && this.json.size!=="default") html += " size=\""+this.json.size+"\"";

		html += " :text-color=\"textColor\"";
		html += " :fill=\"fillColor\"";
		html += " :size=\"size\"";
		html += " :style=\"elGroupStyles\"";

		html += (this.json.buttonRadio) ? " ><el-radio-button" : " ><el-radio";
		html += " :border=\"border\"";

		html += " :style=\"elStyles\"";

		html += ">{{id}}"+((this.json.buttonRadio) ? "</el-radio-button></el-radio-group>" : "</el-radio></el-radio-group>");
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
	}
});
