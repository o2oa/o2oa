MWF.xApplication.process.FormDesigner.Module = MWF.xApplication.process.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.OOInput", null, false);
MWF.xApplication.process.FormDesigner.Module.OOCascade = MWF.FCOOCascade = new Class({
	Extends: MWF.FCOOInput,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"type": "OOCascade",
		"path": "../x_component_process_FormDesigner/Module/OOCascade/",
		"propertyPath": "../x_component_process_FormDesigner/Module/OOCascade/OOCascade.html"
	},
	_loadNodeStyles: function(){
		this.node.setAttribute('readonly', true);
		if (this.json.innerHTML){
			this.node.set("html", this.json.innerHTML);
		}
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
				this.node.setAttribute("readmode", false);
			}else if (this.json.showMode==="read"){
				this.node.setAttribute("readmode", true);
				this.node.removeAttribute("bgcolor");
			}else{
				this.node.setAttribute("readmode", false);
				this.node.removeAttribute("bgcolor");
			}
		}
		if (name==="required"){
			if (this.json.required){
				this.node.setAttribute("required", true);
			}else{
				this.node.removeAttribute("required");
			}
		}

		if (name==="innerHTML"){
			this.node.set("html", this.json.innerHTML);
		}

		if (this.form.options.mode == "Mobile"){
			if (!this.node.getParent('table.form-datatable')){
				this.node.setAttribute("skin-mode", 'mobile');
			}
		}
	},

	_resetModuleDomNode: function(){
        if (this.json.preprocessing){
            this.node.empty();
			if (this.json.innerHTML) this.node.set("html", this.json.innerHTML);
        }
    },
	
	_createMoveNode: function(){
		this.moveNode = new Element("oo-cascade", {
			"MWFType": "OOCascade",
			"id": this.json.id,
			// "label-style": "width:6.2vw; min-width:5em; max-width:9em",
			"styles": this.css.moduleNodeMove,
			"placeholder": this.json.id,
			"readonly": "true",
			"events": {
				"selectstart": function(){
					return false;
				}
			}
		}).inject(this.form.container);

		// this.moveNode._elements.input.setAttribute("readonly", true);
	},
	setPropertiesOrStyles: function(name){
		if (name=="styles"){
			try{
				this.setCustomStyles();
			}catch(e){}
		}
		if (name=="inputStyles"){
			try{
				this.setCustomInputStyles();
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
