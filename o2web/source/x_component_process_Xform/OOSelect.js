MWF.xDesktop.requireApp("process.Xform", "$Selector", null, false);
MWF.xApplication.process.Xform.OOSelect = MWF.APPOOSelect =  new Class({
	Implements: [Events],
	Extends: MWF.APP$Selector,
	iconStyle: "selectIcon",
	options: {
		"moduleEvents": ["load", "queryLoad", "postLoad"]
	},

    initialize: function(node, json, form, options){
        this.node = $(node);
        this.node.store("module", this);
        this.json = json;
        this.form = form;
        this.field = true;
		this.fieldModuleLoaded = false;
		this.nodeHtml = this.node.get("html");
    },

	reload: function(){
		if (this.areaNode){
			this.node = this.areaNode;
			this.areaNode.empty();
			this.areaNode = null;
		}
		this.node.empty();
		this._beforeReloaded();
		this._loadReadEditAbeld();
		this._loadUserInterface();
		this._loadStyles();
		this._afterLoaded();
		this._afterReloaded();
		this.fireEvent("postLoad");
	},

	loadDescription: function () {
		this.node.setAttribute('placeholder', this.json.description || '');
	},

    _loadNode: function(){
		if (!this.isReadable && !!this.isHideUnreadable){
            this.node?.addClass('hide');
        }else{
            this._loadNodeEdit();
        }

		// this._loadNodeEdit();
    },
	_loadMergeReadContentNode: function( contentNode, data ){
		// this._showValue(contentNode, data.data);
	},

	_loadDomEvents: function(){
		Object.each(this.json.events, function(e, key){
			if (e.code){
				if (this.options.moduleEvents.indexOf(key)===-1){
					this.node.addEvent(key, function(event){
						return this.form.Macro.fire(e.code, this, event);
					}.bind(this));
				}
			}
		}.bind(this));
	},

	__showValue: function(node, value, optionItems){
        // if (value){
        //     if (typeOf(value)!=="array") value = [value];
        //     var texts = [];
        //     optionItems.each(function(item){
        //         var tmps = item.split("|");
        //         var t = tmps[0];
        //         var v = tmps[1] || t;
		//
        //         if (v){
		//
        //             if (value.indexOf(v)!=-1){
        //                 texts.push(t);
        //             }
        //         }
		//
        //     });
        //     node.set("text", texts.join(", "));
        // }
	},

    _loadStyles: function(){
		if (this.json.styles) this.node.setStyles(this.json.styles);
    },
	_resetNodeEdit: function(){
		// var node = new Element('oo-select', {
		// 	'id': this.json.id,
		// 	'MWFType': this.json.type
		// }).inject(this.node, 'before');
		// this.node.destroy();
		// this.node = node;

		this.node.set({
			'id': this.json.id,
			'MWFType': this.json.type,
			// "label-style": "width:6.2vw; min-width:5em; max-width:9em"
		});

	},
	addModuleEvent: function(key, fun){
        if (this.options.moduleEvents.indexOf(key)!==-1){
            this.addEvent(key, function(event){
                return (fun) ? fun(this, event) : null;
            }.bind(this));
        }else{
			this.node.addEvent(key, function(event){
                return (fun) ? fun(this, event) : null;
            }.bind(this));
        }
    },
    _loadNodeEdit: function(){
		this._resetNodeEdit();
		this.node.setAttribute('value', undefined);
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
				if (this.json.readModeEvents!=='yes'){
					this.node.setStyle('pointer-events', 'none');
				}
			} else {
			}
		}else{
			this.node.setAttribute('readmode', true);
			if (this.json.readModeEvents!=='yes'){
				this.node.setStyle('pointer-events', 'none');
			}
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

		if (this.json.allowInput) {
            this.node.setAttribute('allow-input', 'true');
        }else{
			this.node.removeAttribute('allow-input');
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

	_setOptions: function(optionItems){
		var p = o2.promiseAll(optionItems).then(function(options){
			this.moduleSelectAG = null;
			if (!options) options = [];
			if (o2.typeOf(options)==="array"){
				options.each(function(item){
					var tmps = item.split("|");
					var text = tmps[0];
					var value = tmps[1] || text;

					var option = new Element("oo-option", {
						"value": value
					});
					option.setAttribute('value', value);
					option.setAttribute('text', text);
					option.inject(this.node);


				}.bind(this));
				this.fireEvent("setOptions", [options])
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
	addOption: function(text, value){
        var option = new Element("oo-option", {
            "value": value || text,
            "text": text
        }).inject(this.node);
		option.setAttribute('value', value || text);
		this.fireEvent("addOption", [text, value])
	},

	// _setValue: function(value, m, fireChange){
	// 	var mothed = m || "__setValue";
	// 	if (!!value){
	// 		var p = o2.promiseAll(value).then(function(v){
	// 			if (o2.typeOf(v)=="array") v = v[0];
	// 			if (this.moduleSelectAG){
	// 				this.moduleValueAG = this.moduleSelectAG;
	// 				this.moduleSelectAG.then(function(){
	// 					this[mothed](v, fireChange);
	// 					return v;
	// 				}.bind(this), function(){});
	// 			}else{
	// 				this[mothed](v, fireChange)
	// 			}
	// 			return v;
	// 		}.bind(this), function(){});
	//
	// 		this.moduleValueAG = p;
	// 		if (this.moduleValueAG) this.moduleValueAG.then(function(){
	// 			this.moduleValueAG = null;
	// 		}.bind(this), function(){
	// 			this.moduleValueAG = null;
	// 		}.bind(this));
	// 	}else{
	// 		this[mothed](value, fireChange);
	// 	}
	// },

	__setValue: function(value){
		this._setBusinessData(value);
		this.node.value = value;
		this.fieldModuleLoaded = true;
		this.moduleValueAG = null;
	},

	_getSelectedOption: function(){
		var ops = this.node.getElements("oo-option");
		for( var i=0; i<ops.length; i++ ){
			if( ops[i].selected )return ops[i];
		}
		return null;
	},
	_getInputTextData: function(){
		return {"value": this.node.value , "text": this.node.text};
	},
	getText: function(){
		return this.node.text;
	},
    getInputData: function(){
		return this.node.value;
	},
    resetData: function(){
        this.setData(this.getValue());
    },

	setData: function(data, fireChange){
		return this._setValue(data, "__setData", fireChange);
	},

	__setData: function(data, fireChange){
		var old = this.getInputData();
		this.moduleValueAG = null;
		this._setBusinessData(data);
		this.node.set('value', data || '');
		this.fieldModuleLoaded = true;
		if (fireChange && old!==data) this.fireEvent("change");
		return value;
	},

	notValidationMode: function (text) {
		this.validationText = text;
		this.node.checkValidity();
	},
	validationMode: function () {
		this.validationText = '';
		this.node.unInvalidStyle();
	}
});

function mergeUniqueArrays(arr1, arr2) {
	return Array.from(new Set([...arr1, ...arr2]));
}