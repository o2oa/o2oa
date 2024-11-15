MWF.xApplication.process.FormDesigner.Module = MWF.xApplication.process.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.Button", null, false);
MWF.xApplication.process.FormDesigner.Module.OOButton = MWF.FCOOButton = new Class({
	Extends: MWF.FCButton,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"type": "OOButton",
		"path": "../x_component_process_FormDesigner/Module/OOButton/",
		"propertyPath": "../x_component_process_FormDesigner/Module/OOButton/OOButton.html"
	},
	initialize: function(form, options){
		this.setOptions(options);

		this.path = this.options.path;
		this.cssPath = this.path+this.options.style+"/css.wcss";

		this._loadCss();
		this.moduleType = "element";
		this.moduleName = this.options.type;

		this.form = form;
		this.container = null;
		this.containerNode = null;
	},
	_createMoveNode: function(){
		this.moveNode = new Element("oo-button", {
			"MWFType": "OOInput",
			"id": this.json.id,
			"styles": this.css.moduleNodeMove,
			"events": {
				"selectstart": function(){
					return false;
				}
			}
		}).inject(this.form.container);
		this.moveNode.setAttribute("text", this.json.name || this.json.id);
	},
	_loadNodeStyles: function(){
		// this.node.setAttribute('readonly', true);
	},
	_setEditStyle_custom: function(name){
		if (name==="name"){
			if (this.json.name){
				this.node.setAttribute('text', this.json.name);
			}else{
				this.node.setAttribute('text', this.json.id);
			}
		}
		if (name==="id"){
			if (!this.json.name){
				this.node.setAttribute('text', this.json.id);
			}
		}
		if (name==="appearance"){
			this.node.setAttribute('type', this.json.appearance || "default");
		}
		if (name==="leftIcon"){
			this.node.setAttribute('left-icon', this.json.leftIcon);
		}
		if (name==="rightIcon"){
			this.node.setAttribute('right-icon', this.json.rightIcon);
		}
		if (name==="disabled"){
			this.node.setAttribute('disabled', this.json.disabled);
		}

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

	setCustomStyles: function(){
		var border = this.node.getStyle("border");
		this._recoveryModuleData();

		this.node.clearStyles();
		this.node.setStyles(this.css.moduleNode);

		if (this.initialStyles) this.node.setStyles(this.initialStyles);

		this.node.setStyle("border", border);

		this.node.setStyles(this.json.styles);
	},
	_preprocessingModuleData: function(){
		this.node.clearStyles();
		this.json.recoveryStyles = Object.clone(this.json.styles);
		this.node.setStyles(this.json.recoveryStyles);
		this.json.styles = {};
		this.json.preprocessing = "y";
	},
});
