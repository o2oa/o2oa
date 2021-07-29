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
		this.className = "Elradio"
		this.moduleType = "element";
		this.moduleName = "elradio";
	},
	_createElementHtml: function(){
		debugger;
		var html = "<el-radio-group style='box-sizing: border-box!important'";
		if (this.json.textColor) html += " text-color=\""+this.json.textColor+"\"";
		if (this.json.fillColor) html += " fill=\""+this.json.fillColor+"\"";
		if (this.json.size && this.json.size!=="default") html += " size=\""+this.json.size+"\"";

		if (this.json.elGroupProperties){
			Object.keys(this.json.elGroupProperties).forEach(function(k){
				if (this.json.elGroupProperties[k]) html += " "+k+"=\""+this.json.elGroupProperties[k]+"\"";
			}, this);
		}
		if (this.json.elGroupStyles){
			var style = "";
			Object.keys(this.json.elGroupStyles).forEach(function(k){
				if (this.json.elGroupStyles[k]) style += k+":"+this.json.elGroupStyles[k]+";";
			}, this);
			html += " style=\""+style+"\"";
		}


		html += (this.json.buttonRadio) ? " ><el-radio-button" : " ><el-radio";
		if (this.json.border===true) html += " border";


		if (this.json.elProperties){
			Object.keys(this.json.elProperties).forEach(function(k){
				if (this.json.elProperties[k]) html += " "+k+"=\""+this.json.elProperties[k]+"\"";
			}, this);
		}

		var radiostyle = "box-sizing: border-box!important;";
		if (this.json.elStyles){
			Object.keys(this.json.elStyles).forEach(function(k){
				if (this.json.elStyles[k]) radiostyle += k+":"+this.json.elStyles[k]+";";
			}, this);
		}
		html += " style=\""+radiostyle+"\"";

		html += ">"+this.json.id+((this.json.buttonRadio) ? "</el-radio-button></el-radio-group>" : "</el-radio></el-radio-group>");
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
