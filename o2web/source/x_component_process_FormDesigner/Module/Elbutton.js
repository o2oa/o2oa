MWF.xApplication.process.FormDesigner.Module = MWF.xApplication.process.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.$ElElement", null, false);
MWF.xApplication.process.FormDesigner.Module.Elbutton = MWF.FCElbutton = new Class({
	Extends: MWF.FC$ElElement,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"propertyPath": "../x_component_process_FormDesigner/Module/Elbutton/elbutton.html"
	},

	_initModuleType: function(){
		this.className = "Elbutton";
		this.moduleType = "element";
		this.moduleName = "elbutton";
	},
	_createElementHtml: function(){
		var html = "<el-button";
		// if (this.json.size && this.json.size!=="auto") html += " size=\""+this.json.size+"\"";
		// if (this.json.bttype && this.json.bttype!=="default") html += " type=\""+this.json.bttype+"\"";
		// if (this.json.plain===true) html += " plain";
		// if (this.json.round===true) html += " round";
		// if (this.json.circle===true) html += " circle";
		// if (this.json.icon) html += " icon=\""+this.json.icon+"\"";
		// if (this.json.disabled===true) html += " disabled";
		// if (this.json.loading===true) html += " loading";
		// if (this.json.autofocus===true) html += " autofocus";

		html += " :size=\"size\"";
		html += " :type=\"bttype\"";
		html += " :plain=\"plain\"";
		html += " :round=\"round\"";
		html += " :circle=\"circle\"";
		if( this.json.iconPosition !== "right" )html += " :icon=\"icon\"";
		html += " :disabled=\"disabled\"";
		html += " :loading=\"loading\"";


		if (this.json.elProperties){
			Object.keys(this.json.elProperties).forEach(function(k){
				if (this.json.elProperties[k]) html += " "+k+"=\""+this.json.elProperties[k]+"\"";
			}, this);
		}

		html += " :style=\"tmpElStyles\"";

		// if (this.json.elStyles){
		// 	var style = "";
		// 	Object.keys(this.json.elStyles).forEach(function(k){
		// 		if (this.json.elStyles[k]) style += k+":"+this.json.elStyles[k]+";";
		// 	}, this);
		// 	html += " style=\""+style+"\"";
		// }

		// html += ">"+((this.json.circle!==true && this.json.isText!==false) ? (this.json.name || this.json.id) : "")+"</el-button>";
		html += ">{{(isText===false) ? '' : name||id}}";
		if( this.json.iconPosition === "right" )html += "<i class=\""+ this.json.icon +" el-icon--right\"></i>";
		html += "</el-button>";
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
			case "iconPosition":
			case "icon":
			case "vueSlot":
				if (this.isPropertyLoaded) if (this.vm) this.resetElement(); break;
			default: ;
		}
	},
});
