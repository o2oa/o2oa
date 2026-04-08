MWF.xDesktop.requireApp("process.Xform", "OOSelect", null, false);
MWF.xApplication.process.Xform.OOCascade = MWF.APPOOCascade =  new Class({
	Implements: [Events],
	Extends: MWF.APPOOSelect,
	iconStyle: "selectIcon",
	options: {
		"moduleEvents": ["load", "queryLoad", "postLoad"]
	},

    _loadNodeEdit: function(){
		this._resetNodeEdit();
		this.node.removeAttribute("value");
		this.node.removeAttribute("placeholder");

		if (o2.isMediaMobile() && !this.json.inDatatable){
			this.node.setAttribute("skin-mode", 'mobile');
		}else{
            this.node.removeAttribute("skin-mode");
        }

		if (this.json.properties) {
			this.node.set(this.json.properties);
		}
		if (this.json.styles) {
			this.node.setStyles(this.json.styles);
		}
		if (this.json.label) {
			this.node.setAttribute('label', this.json.label);
		}

		if (this.json.inDatatable){
            this.node.setAttribute('view-style', '');
        }

		this.node.setAttribute('readonly', false);
		this.node.setAttribute('readmode', false);
		this.node.setAttribute('disabled', false);
		if (!this.isReadonly() && this.isEditable){
			if (this.json.showMode === 'readonlyMode') {
				this.node.setAttribute('readonly', true);
			} else if (this.json.showMode === 'disabled') {
				this.node.setAttribute('disabled', true);
			} else if (this.json.showMode === 'read') {
				this.node.setAttribute('readmode', true);
				// if (this.json.readModeEvents!=='yes'){
				// 	this.node.setStyle('pointer-events', 'none');
				// }
			} else {
			}
		}else{
			this.node.setAttribute('readmode', true);
			// if (this.json.readModeEvents!=='yes'){
			// 	this.node.setStyle('pointer-events', 'none');
			// }
		}

		if (this.json.required){
			this.node.setAttribute("required", true);
			if (!this.json.validationConfig) this.json.validationConfig = [];
			var label = this.json.label ? `“${this.json.label.replace(/　/g, '')}”` :  MWF.xApplication.process.Xform.LP.requiredHintField;
			this.json.validationConfig.push({
				status : "all",
				decision : "",
				valueType : "value",
				operateor : "isnull",
				value : "",
				prompt : MWF.xApplication.process.Xform.LP.requiredHint.replace('{label}', label),
			});
		}else{
			this.node.removeAttribute("required");
		}

		// if (this.json.allowInput) {
        //     this.node.setAttribute('allow-input', 'true');
        // }else{
		// 	this.node.removeAttribute('allow-input');
		// }

		if (this.json.maxTags) {
			this.node.setAttribute('max-tags', this.json.maxTags);
		}else{
			this.node.removeAttribute('max-tags');
		}

		if (this.json.multiple) {
			this.node.setAttribute('multiple', 'true');
		}else{
			this.node.removeAttribute('multiple');
		}

		if (this.json.showAllLevels) {
			this.node.setAttribute('show-all-levels', 'true');
		}else{
			this.node.setAttribute('show-all-levels', 'false');
		}

		if (this.json.checkStrictly) {
			this.node.setAttribute('check-strictly', 'true');
		}else{
			this.node.removeAttribute('check-strictly');
		}

		if (this.json.innerHTML){
			this.node.set("html", this.json.innerHTML);
		}

        this.node.addEvent("change", function( ev ){
			var v = this.getInputData("change");
			this._setBusinessData(v);
            this.validationMode();
			this.validation();
			this.fireEvent("change", [this._getSelectedOption()]);
        }.bind(this));

		var inputNode = this.node;
		if (inputNode) inputNode.addEvent('input', function (e) {
			var v = e.target.get('value');
			this._setBusinessData(v);
		}.bind(this));

		this.node.addEvent('blur', function () {
			this.validation();
		}.bind(this));
		this.node.addEvent('keyup', function () {
			this.validationMode();
		}.bind(this));

		this.node.addEventListener('validity', (e) => {
			if (this.validationText) {
				e.target.setCustomValidity(this.validationText);
			}
		});
		this.node.addEventListener('invalid', (e)=>{
            if (this.node._props.validity){
                e.target.setCustomValidity(this.node._props.validity);
            }else{
                var label = this.json.label ? `“${this.json.label.replace(/　/g, '')}”` :  MWF.xApplication.process.Xform.LP.requiredHintField;
                const o = {
                    valueMissing: MWF.xApplication.process.Xform.LP.requiredHint.replace('{label}', label),
                }
                //通过 e.detail 获取 验证有效性状态对象：ValidityState
                for (const k in o){
                    if (e.detail[k]){
                        if (o[k]){
                            break;
                        }
                    }
                }
            }
        });

		this.setOptions();
	},

	setOptions: function () {
		var optionItems = this.getOptions();
		this._setOptions(optionItems);
	},
	_getOptions: function (async, refresh) {
		switch (this.json.itemType) {
			case "script":
				return this.form.Macro.exec(((this.json.itemScript) ? this.json.itemScript.code : ""), this);
			case "dict":
				return this.getOptionsWithDict(async, refresh);
		}
	},
	_getLazyLoadFunction: function (){
		if( this.lazyLoadFunction ){
			return this.lazyLoadFunction;
		}
		this.lazyLoadFunction = this.form.Macro.exec(((this.json.lazyLoadScript) ? this.json.lazyLoadScript.code : ""), this);
		return this.lazyLoadFunction;
	},
	_setOptions: function(optionItems){
		var p = o2.promiseAll(optionItems).then(function(options){
			this.moduleSelectAG = null;
			if( options && options.length ){
				this._parseChilren(options);
				this.node.setOption( options );
			}else{
				const fun = this._getLazyLoadFunction();
				if( typeof fun === 'function' ){
					this.node.setOption( fun );
				}
			}
		}.bind(this), function(){
			this.moduleSelectAG = null;
		}.bind(this));
		this.moduleSelectAG = p;
		if (p) p.then(function(){
			this.moduleSelectAG = null;
		}.bind(this), function(){
			this.moduleSelectAG = null;
		}.bind(this));
	},
	_parseChilren: function(optionItmes){
		optionItmes.forEach((opt)=>{
			if(opt.children) {
				if( Array.isArray(opt.children) ){
					this._parseChilren(opt.children);
				}else{
					opt.children = this._getLazyLoadFunction();
				}
			}
		});
	},

	__setValue: function(value){
		debugger;
		var text = this.getText();
		this.node.text = Array.isArray(text) ? JSON.stringify(text) : text;

		this._setBusinessData(value);
		this.node.value = Array.isArray(value) ? JSON.stringify(value) : value;
		this.fieldModuleLoaded = true;
		this.moduleValueAG = null;
	},

	getText: function(){
		if( this.fieldModuleLoaded ){
			return this.node.text || '';
		}else{
			return this.getBusinessDataById(null, `${this.json.id}$text`) || '';
		}
	},
    getInputData: function(){
		return this.node.value || '';
	}
});
