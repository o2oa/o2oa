MWF.xApplication.process.FormDesigner.Module = MWF.xApplication.process.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.$Input", null, false);
MWF.xApplication.process.FormDesigner.Module.OOCurrency = MWF.FCOOCurrency = new Class({
	Extends: MWF.FC$Input,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"type": "OOCurrency",
		"path": "../x_component_process_FormDesigner/Module/OOCurrency/",
		"propertyPath": "../x_component_process_FormDesigner/Module/OOCurrency/OOCurrency.html"
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

		if (name==="showIcon"){
			if (this.json.showIcon==="yes"){
				this.node.setAttribute("right-icon", this.json.properties["right-icon"] || "currency");
			}else{
				this.node.removeAttribute("right-icon");
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
		//['preset','currency','prefixUse','prefix','suffix','thousands','decimal']
		//precision,allowBlank,disableNegative,maximum,minimum,round

		if(name === 'preset'){
			debugger;
			if( this.json.preset === 'currency' ){
				this._setAttributeWithPreset();
			}else{
				this.node.setAttribute('prefix', this.json.prefix);
				this.node.setAttribute('suffix', this.json.suffix||'');
				this.node.setAttribute('thousands', this.json.thousands);
				this.node.setAttribute('decimal', this.json.decimal);
			}
		}
		if (name==="currency"){
			this._setAttributeWithPreset();
		}
		if (name==="prefixuse"){
			this._setAttributeWithPreset();
		}
		if( [
			'prefix', 'suffix', 'thousands', 'decimal',
			'precision','allowblank','disablenegative',
			'maximum','minimum','round'
		].contains(name) ){
			if(this.json[name] === 'null' || o2.typeOf(this.json[name]) === 'null'){
				this.node.removeAttribute(name);
			}else{
				this.node.setAttribute(name, this.json[name]);
			}
		}

		if (this.form.options.mode == "Mobile"){
			if (!this.node.getParent('table.form-datatable')){
				this.node.setAttribute("skin-mode", 'mobile');
			}
		}
	},
	_setAttributeWithPreset: function(){
		if( this.json.preset === 'currency' ){
			var OOCurrency = window.customElements.get('oo-currency');
			var preset = OOCurrency.preset[this.json.currency];
			this.node.setAttribute('prefix', preset[this.json.prefixuse]);
			this.node.setAttribute('thousands', preset.thousands);
			this.node.setAttribute('decimal', preset.decimal);
			this.node.setAttribute("suffix", preset.suffix||'');
		}
	},
	_createMoveNode: function(){
		this.moveNode = new Element("oo-currency", {
			"MWFType": "OOCurrency",
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
	},
	_resetModuleDomNode: function(){
        if (this.json.preprocessing){
            this.node.empty();
			if (this.json.innerHTML) this.node.set("html", this.json.innerHTML);
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

	_preprocessingModuleData: function(){
		this.node.clearStyles();
		if (this.json.styles){
			this.json.recoveryStyles = Object.clone(this.json.styles);
			this.oocurrency = this.node;
			if (this.json.recoveryStyles) Object.each(this.json.recoveryStyles, function(value, key){
				if ((value.indexOf("x_processplatform_assemble_surface")==-1 && value.indexOf("x_portal_assemble_surface")==-1)){
					this.oocurrency.setStyle(key, value);
					delete this.json.styles[key];
				}
			}.bind(this));
		}
		this.json.preprocessing = "y";
	}
});
