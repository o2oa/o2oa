MWF.xApplication.process.FormDesigner.Module = MWF.xApplication.process.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.Elinput", null, false);
MWF.xApplication.process.FormDesigner.Module.Elswitch = MWF.FCElswitch = new Class({
	Extends: MWF.FCElinput,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"propertyPath": "../x_component_process_FormDesigner/Module/Elswitch/elswitch.html"
	},

	_initModuleType: function(){
		this.className = "Elswitch";
		this.moduleType = "element";
		this.moduleName = "elswitch";
	},
	_createElementHtml: function(){
		//var html = "<el-input placeholder=\"请输入内容\"></el-input>";
		var html = "<el-switch v-model=\""+this.json.id+"\"";
		// if (this.json.description) html += " placeholder=\""+this.json.description+"\"";
		// if (this.json.width) html += " width=\""+(this.json.width||40)+"\"";
		// if (this.json.activeText) html += " active-text=\""+this.json.activeText+"\"";
		// if (this.json.inactiveText) html += " inactive-text=\""+this.json.inactiveText+"\"";
		// if (this.json.activeColor) html += " active-color=\""+(this.json.activeColor||"#409EFF")+"\"";
		// if (this.json.inactiveColor) html += " inactive-color=\""+(this.json.inactiveColor||"#C0CCDA")+"\"";
		// if (this.json.activeIconClass) html += " active-icon-class=\""+this.json.activeIconClass+"\"";
		// if (this.json.inactiveIconClass) html += " inactive-icon-class=\""+this.json.inactiveIconClass+"\"";
		html += " :width=\"width\"";
		html += " :active-text=\"activeText\"";
		html += " :inactive-text=\"inactiveText\"";
		html += " :active-color=\"activeColor\"";
		html += " :inactive-color=\"inactiveColor\"";
		html += " :active-icon-class=\"activeIconClass\"";
		html += " :inactive-icon-class=\"inactiveIconClass\"";

		html += " :style=\"elStyles\"";

		html += "></el-switch>";
		return html;
	},
	_createVueData: function(){
		//var data = this.json;
		return function(){
			this.json[this.json.id] = true;
			return Object.assign(this.json, this.tmpVueData||{});
		}.bind(this);
	},
	_setEditStyle_custom: function(name){
		switch (name){
			case "name": this.setPropertyName(); break;
			case "id": this.setPropertyId(); break;
			case "width":
				this.json.width = this.json.width.toFloat();
				if (this.isPropertyLoaded) if (this.vm) this.resetElement(); break;
			default: ;
		}
	},
	setPropertyName: function(){},
	setPropertyId: function(){}
});
