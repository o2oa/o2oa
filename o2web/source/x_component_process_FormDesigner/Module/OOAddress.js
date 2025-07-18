MWF.xApplication.process.FormDesigner.Module = MWF.xApplication.process.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.OOInput", null, false);
MWF.xApplication.process.FormDesigner.Module.OOAddress = MWF.FCOOAddress = new Class({
	Extends: MWF.FCOOInput,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"type": "OOAddress",
		"path": "../x_component_process_FormDesigner/Module/OOAddress/",
		"propertyPath": "../x_component_process_FormDesigner/Module/OOAddress/OOAddress.html"
	},
	_loadNodeStyles: function(){
		this.node.setAttribute('readonly', 'true');
	},
	_setEditStyle_custom: function(name){
		if (name==="id") this.node.set("placeholder", this.json.id);
		if (["label"].includes(name)){
			this.node.setAttribute(name, this.json[name]);
		}

		this.node.setAttribute('readonly', 'true');
		if (name==="showMode"){
			if (this.json.showMode==="disabled"){
				this.node.setAttribute("bgcolor", "#f3f3f3");
				this.node.setAttribute("readmode", 'false');
			}else if (this.json.showMode==="read"){
				this.node.setAttribute("readmode", 'true');
				this.node.removeAttribute("bgcolor");
			}else{
				this.node.setAttribute("readmode", 'false');
				this.node.removeAttribute("bgcolor");
			}
		}
		if (name==="required"){
			if (this.json.required){
				this.node.setAttribute("required", 'true');
			}else{
				this.node.removeAttribute("required");
			}
		}

		if (this.form.options.mode == "Mobile"){
			if (!this.node.getParent('table.form-datatable')){
				this.node.setAttribute("skin-mode", 'mobile');
			}
		}

	},
	_createMoveNode: function(){
		this.moveNode = new Element("oo-select", {
			"MWFType": "OOAddress",
			"id": this.json.id,
			"styles": this.css.moduleNodeMove,
			"placeholder": this.json.id,
			"readonly": "true",
			"events": {
				"selectstart": function(){
					return false;
				}
			}
		}).inject(this.form.container);
	},
	setPropertiesOrStyles: function(name){
		if (name=="styles"){
			try{
				this.setCustomStyles();
			}catch(e){}
		}
		if (name=="properties"){
			this.node.setProperties(this.json.properties);
		}
	},

	_preprocessingModuleData: function(){
		this.node.clearStyles();
		if (this.json.styles){
			this.json.recoveryStyles = Object.clone(this.json.styles);
			this.ooinput = this.node;
			if (this.json.recoveryStyles) Object.each(this.json.recoveryStyles, function(value, key){
				if ((value.indexOf("x_processplatform_assemble_surface")==-1 && value.indexOf("x_portal_assemble_surface")==-1)){
					this.ooinput.setStyle(key, value);
					delete this.json.styles[key];
				}
			}.bind(this));
		}
		this.json.preprocessing = "y";
	},
});
