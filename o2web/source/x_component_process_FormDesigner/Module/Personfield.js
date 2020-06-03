MWF.xApplication.process.FormDesigner.Module = MWF.xApplication.process.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.$Input", null, false);
MWF.xApplication.process.FormDesigner.Module.Personfield = MWF.FCPersonfield = new Class({
	Extends: MWF.FC$Input,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"type": "personfield",
		"path": "../x_component_process_FormDesigner/Module/Personfield/",
		"propertyPath": "../x_component_process_FormDesigner/Module/Personfield/personfield.html"
	},
	_preprocessingModuleData: function(){
		this.node.clearStyles();
		this.recoveryIconNode = this.node.getFirst();
		this.recoveryIconNode.dispose();
		this.recoveryTextNode = this.node.getFirst();
		this.recoveryTextNode.dispose();

		var inputNode = new Element("div", {
			"styles": {
				"background": "transparent",
				"border": "0px",
				"min-height": "24px"
			}
		}).inject(this.node);
		this.node.setStyles({
			"overflow": "hidden",
			"position": "relative",
			"margin-right": "20px",
			"min-height": "24px"
		});

		if (this.json.styles){
			this.json.recoveryStyles = Object.clone(this.json.styles);
			if (this.json.recoveryStyles) Object.each(this.json.recoveryStyles, function(value, key){
				if ((value.indexOf("x_processplatform_assemble_surface")==-1 && value.indexOf("x_portal_assemble_surface")==-1)){
					this.node.setStyle(key, value);
					delete this.json.styles[key];
				}
			}.bind(this));
		}
		if (this.json.inputStyles){
			this.json.recoveryInputStyles = Object.clone(this.json.inputStyles);
			var inputNode = this.node.getFirst();
			if (inputNode){
				if (this.json.recoveryInputStyles) Object.each(this.json.recoveryInputStyles, function(value, key){
					if ((value.indexOf("x_processplatform_assemble_surface")==-1 && value.indexOf("x_portal_assemble_surface")==-1)){
						inputNode.setStyle(key, value);
						delete this.json.inputStyles[key];
					}
				}.bind(this));
			}
		}
		this.json.preprocessing = "y";
	},
	_recoveryModuleData: function(){
		if (this.json.recoveryStyles) this.json.styles = this.json.recoveryStyles;
		if (this.json.recoveryInputStyles) this.json.inputStyles = this.json.recoveryInputStyles;

		if (this.recoveryTextNode) {
			this.node.empty();
			this.recoveryTextNode.inject(this.node, "top");
		}
		if (this.recoveryIconNode) {
			this.recoveryIconNode.inject(this.node, "top");
		}

		this.json.recoveryStyles = null;
		this.json.recoveryInputStyles = null;
		this.recoveryIconNode = null;
		this.recoveryTextNode = null;
	},
});
