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
		var html = "<el-switch";
		if (this.json.description) html += " placeholder=\""+this.json.description+"\"";
		if (this.json.width) html += " width=\""+(this.json.width||40)+"\"";
		if (this.json.activeText) html += " active-text=\""+this.json.activeText+"\"";
		if (this.json.inactiveText) html += " inactive-text=\""+this.json.inactiveText+"\"";
		if (this.json.activeColor) html += " active-color=\""+(this.json.activeColor||"#409EFF")+"\"";
		if (this.json.inactiveColor) html += " inactive-color=\""+(this.json.inactiveColor||"#C0CCDA")+"\"";
		if (this.json.activeIconClass) html += " active-icon-class=\""+this.json.activeIconClass+"\"";
		if (this.json.inactiveIconClass) html += " inactive-icon-class=\""+this.json.inactiveIconClass+"\"";

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
		html += "></el-switch>";
		debugger;
		return html;
	},
	setPropertyName: function(){},
	setPropertyId: function(){}
});
