MWF.xApplication.process.FormDesigner.Module = MWF.xApplication.process.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.$Input", null, false);
MWF.xApplication.process.FormDesigner.Module.OOOrg = MWF.FCOOOrg = new Class({
	Extends: MWF.FC$Input,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"type": "OOOrg",
		"path": "../x_component_process_FormDesigner/Module/OOOrg/",
		"propertyPath": "../x_component_process_FormDesigner/Module/OOOrg/OOOrg.html"
	},
	_loadNodeStyles: function(){
		this.node.setAttribute('readonly', true);
	},
	_setEditStyle_custom: function(name){
		if (name==="id") this.node.set("placeholder", this.json.id);
		if (["label"].includes(name)){
			this.node.setAttribute(name, this.json[name]);
		}

		if (name==="showMode"){
			if (this.json.showMode==="disabled"){
				this.node.setAttribute("bgcolor", "#f3f3f3");
				this.node.setAttribute("readmode", false);
			}else if (this.json.showMode==="read"){
				// this.node.setAttribute("readmode", true);
				this.node.removeAttribute("bgcolor");
			}else{
				this.node.setAttribute("readmode", false);
				this.node.removeAttribute("bgcolor");
			}
		}
		if (name==="dataType"){
			this.node.setAttribute("type", this.json.dataType);
			this.node._elements.input.value = '';
		}

		if (name==="showIcon"){
			if (this.json.showIcon==="yes"){
				this.node.setAttribute("right-icon", "person");
			}else{
				this.node.removeAttribute("right-icon");
			}
		}
	},
	_createMoveNode: function(){
		this.moveNode = new Element("oo-selector", {
			"MWFType": "OOOrg",
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
