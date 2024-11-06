MWF.xApplication.process.FormDesigner.Module = MWF.xApplication.process.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.Radio", null, false);
MWF.xApplication.process.FormDesigner.Module.OORadioGroup = MWF.FCOORadioGroup = new Class({
	Extends: MWF.FCRadio,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"type": "OORadioGroup",
		"path": "../x_component_process_FormDesigner/Module/OORadioGroup/",
		"propertyPath": "../x_component_process_FormDesigner/Module/OORadioGroup/OORadioGroup.html",
		"tag": "oo-radio-group"
	},

	_createMoveNode: function(){
		this.moveNode = new Element("div", {
			"MWFType": this.options.type,
			"id": this.json.id,
			"styles": this.css.moduleNodeMove,
			"events": {
				"selectstart": function(){
					return false;
				}
			}
		}).inject(this.form.container);

		this._createNodeContent();
	},
	_createNodeContent: function(){
		var radioNode = new Element(this.options.tag, {styles:{"float": "left"}}).inject(this.moveNode || this.node);

		var infoNode = new Element("div", {
			styles: {
				"display": "flex",
				"align-items": "center",
				"gap": "0.25em",
				"padding": "0 0.2em"
			}
		}).inject(this.moveNode || this.node);

		var icon = new Element("div", {
			"styles": this.css.textfieldIcon
		}).inject(infoNode);
		var text = new Element("div", {
			"styles": this.css.moduleText,
			"text": this.json.id
		}).inject(infoNode);
	},

	_resetModuleDomNode: function(){
		if (this.json.preprocessing){
			this.node.empty();
			this._createNodeContent();
		}
	},

	_loadNodeStyles: function(){
		this.node.setStyles(this.css.moduleNodeMove);
	},

	setPropertiesOrStyles: function(name){
		if (name==="styles"){
			try{
				this.setCustomStyles();
			}catch(e){}
		}
		if (name==="properties"){
			this.node.getElement(this.options.tag).setProperties(this.json.properties);
		}
	},
	setCustomStyles: function(){
		this._recoveryModuleData();
		this.node.clearStyles();
		if (this.node){
			this.node.setProperties(this.json.properties);
			this.node.clearStyles();
			this.node.setStyles(this.json.styles);
		}
		this.node.setStyles(this.css.moduleNode);

		if (this.initialStyles) this.node.setStyles(this.initialStyles);

		this._setEditStyle_custom('label');
		this._setEditStyle_custom('showMode');
	},
	_setEditStyle_custom: function(name){
		if (name==="id"){
			this.node.getLast().getLast().set("text", this.json.id);
		}
		if (name==="label"){
			this.node.getElement(this.options.tag).setAttribute("label", this.json.label||'');
		}
		if (name==="showMode"){
			if (this.json.showMode==="disabled"){
				this.node.setStyle("background-color", "#f3f3f3");
				this.node.getLast().getFirst().show();
			}else if (this.json.showMode==="read"){
				this.node.getLast().getFirst().hide();
				this.node.setStyle("background-color", "#ffffff");
			}else{
				this.node.setStyle("background-color", "#ffffff");
				this.node.getLast().getFirst().show();
			}
		}
	},
	setCustomInputStyles: function(){
		this._recoveryModuleData();
	}
});
