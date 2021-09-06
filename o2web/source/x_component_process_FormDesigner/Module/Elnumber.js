MWF.xApplication.process.FormDesigner.Module = MWF.xApplication.process.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.Elinput", null, false);
MWF.xApplication.process.FormDesigner.Module.Elnumber = MWF.FCElnumber = new Class({
	Extends: MWF.FCElinput,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"propertyPath": "../x_component_process_FormDesigner/Module/Elnumber/elnumber.html"
	},

	_initModuleType: function(){
		this.className = "Elnumber";
		this.moduleType = "element";
		this.moduleName = "elnumber";
	},
	_createElementHtml: function(){
		//var html = "<el-input placeholder=\"请输入内容\"></el-input>";
		var html = "<el-input-number";
		// if (this.json.description) html += " placeholder=\""+this.json.description+"\"";
		// if (this.json.max) html += " max=\""+this.json.max+"\"";
		// if (this.json.min) html += " min=\""+this.json.min+"\"";
		// if (this.json.step) html += " step=\""+this.json.step+"\"";
		// if (this.json.stepStrictly) html += " step-strictly";
		// if (this.json.precision) html += " precision=\""+this.json.precision+"\"";
		// if (this.json.size && this.json.size!=="default") html += " size=\""+this.json.size+"\"";
		// //if (this.json.controls===false) html += " controls=\"false\"";
		// if (this.json.controlsPosition && this.json.controlsPosition!=="default") html += " controls-position=\""+this.json.controlsPosition+"\"";

		html += " :placeholder=\"description\"";
		html += " :max=\"max\"";
		html += " :min=\"min\"";
		html += " :step=\"step\"";
		html += " :step-strictly=\"stepStrictly\"";
		html += " :precision=\"precision\"";
		html += " :size=\"size\"";
		html += " :controls-position=\"controlsPosition\"";

		if (this.json.elProperties){
			Object.keys(this.json.elProperties).forEach(function(k){
				if (this.json.elProperties[k]) html += " "+k+"=\""+this.json.elProperties[k]+"\"";
			}, this);
		}
		html += " :style=\"elStyles\"";

		// if (this.json.elStyles){
		// 	var style = "";
		// 	Object.keys(this.json.elStyles).forEach(function(k){
		// 		if (this.json.elStyles[k]) style += k+":"+this.json.elStyles[k]+";";
		// 	}, this);
		// 	html += " style=\""+style+"\"";
		// }

		html += " :value=\"id\">";
		if (this.json.vueSlot) html += this.json.vueSlot;
		html += "</el-input-number>";
		return html;
	}
});
