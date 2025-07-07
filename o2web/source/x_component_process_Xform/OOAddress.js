MWF.xDesktop.requireApp('process.Xform', 'OOSelect', null, false);
MWF.xApplication.process.Xform.OOAddress = MWF.APPOOAddress = new Class({
    Implements: [Events],
    Extends: MWF.APPOOSelect,
    iconStyle: 'selectIcon',
    options: {
        moduleEvents: ['load', 'queryLoad', 'postLoad'],
    },

    _loadNode: function () {
        if (!this.isReadable && !!this.isHideUnreadable) {
            this.node.setStyle('display', 'none');
        } else {
            this._loadNodeEdit();
        }
    },
    _loadNodeEdit: function () {
        this._resetNodeEdit();
        this.node.setAttribute('value', undefined);
        this.node.removeAttribute('placeholder');

        this.node.setAttribute('option-width', 'auto');

        if (o2.isMediaMobile() && !this.json.inDatatable) {
            this.node.setAttribute('skin-mode', 'mobile');
        } else {
            this.node.removeAttribute('skin-mode');
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

        if (this.json.inDatatable) {
            this.node.setAttribute('view-style', '');
        }

        this.node.setAttribute('readonly', false);
        this.node.setAttribute('readmode', false);
        this.node.setAttribute('disabled', false);
        if (!this.isReadonly() && this.isEditable) {
            if (this.json.showMode === 'readonlyMode') {
                this.node.setAttribute('readonly', true);
            } else if (this.json.showMode === 'disabled') {
                this.node.setAttribute('disabled', true);
            } else if (this.json.showMode === 'read') {
                this.node.setAttribute('readmode', true);
                if (this.json.readModeEvents !== 'yes') {
                    this.node.setStyle('pointer-events', 'none');
                }
            } else {
            }
        } else {
            this.node.setAttribute('readmode', true);
            if (this.json.readModeEvents !== 'yes') {
                this.node.setStyle('pointer-events', 'none');
            }
        }

        if (this.json.required) {
            this.node.setAttribute('required', true);
            // if (!this.json.validationConfig) this.json.validationConfig = [];
            // var label = this.json.label ? `“${this.json.label.replace(/　/g, '')}”` :  MWF.xApplication.process.Xform.LP.requiredHintField;
            // this.json.validationConfig.push({
            // 	status : "all",
            // 	decision : "",
            // 	valueType : "length",
            // 	operateor : "isnull",
            // 	value : "",
            // 	prompt : MWF.xApplication.process.Xform.LP.requiredHint.replace('{label}', label),
            // });
        } else {
            this.node.removeAttribute('required');
        }

        // if (this.json.allowInput) {
        //     this.node.setAttribute('allow-input', 'true');
        // }else{
        this.node.removeAttribute('allow-input');
        // }

        this.node.addEvent(
            'change',
            function (ev) {
                var v = this.getInputData('change');
                this._setBusinessData(v);
                this.validationMode();
                this.validation();
                this.fireEvent('change', [this._getSelectedOption()]);
            }.bind(this),
        );

        var inputNode = this.node;
        // if (inputNode) inputNode.addEvent('input', function (e) {
        // 	var v = e.target.get('value');
        // 	this._setBusinessData(v);
        // }.bind(this));

        // this.node.addEvent('blur', function () {
        // 	this.validation();
        // }.bind(this));
        // this.node.addEvent('keyup', function () {
        // 	this.validationMode();
        // }.bind(this));

        this.node.addEventListener('validity', (e) => {
            if (this.validationText) {
                e.target.setCustomValidity(this.validationText);
            }
        });
        this.node.addEventListener('invalid', (e) => {
            var label = this.json.label ? `“${this.json.label.replace(/　/g, '')}”` : MWF.xApplication.process.Xform.LP.requiredHintField;
            const o = {
                valueMissing: MWF.xApplication.process.Xform.LP.requiredHint.replace('{label}', label),
            };
            //通过 e.detail 获取 验证有效性状态对象：ValidityState
            for (const k in o) {
                if (e.detail[k]) {
                    if (o[k]) {
                        e.target.setCustomValidity(o[k]);
                        break;
                    }
                }
            }
        });

        this.setOptions();
    },

    _createOptionItem: function (o, node) {
        var option = new Element('oo-option');
		option.setAttribute('value', o.name);
        option.setAttribute('text', o.name);
		option.setAttribute('data-address-level', o.level);
        option.inject(node);
        return option;
    },
    _createSubLoadingOption: function (node) {
        const option = new Element('oo-option');
        option.setAttribute('icon', 'loading');
        node.appendChild(option);

		debugger;

        const loadSubOptionFun = async () => {
            const items = await this._getAddressData(node);
            node.empty();
            this._setOptionItems(items, node);
            node.removeEventListener('show', loadSubOptionFun);
        };
        node.addEventListener('show', loadSubOptionFun);
    },
	_getAddressData: function(node){
		return new Promise((r)=>{
			const v = node.value;
			switch (node.dataset.addressLevel){
				case 'province':
					o2.Actions.get("x_general_assemble_control").listCity(v, function(json){
						r(json.data);
					});
					break;
				case 'city':
					o2.Actions.get("x_general_assemble_control").listDistrict(v[0], v[1], function(json){
						r(json.data);
					});
					break;
				case 'district':
					r([]);
					break;
				default:
					o2.Actions.get("x_general_assemble_control").listProvince(function(json){
						r(json.data);
					});
			}
		
		});
	},
    _setOptionItems: function (items, node) {
        items.each(
            function (o) {
                var option = this._createOptionItem(o, node);
				
                if (o.level !== this.json.selectRange && o.level!=='district') {
					this._createSubLoadingOption(option);
                }
            }.bind(this),
        );
    },

    setOptions: function () {
        o2.Actions.get('x_general_assemble_control').listProvince(
            function (json) {
                this._setOptionItems(json.data, this.node);
            }.bind(this),
        );
    },

    addOption: function (text, value) {},

    __setValue: function (value) {
        this._setBusinessData(value);
        this.node.value = value;
        this.fieldModuleLoaded = true;
        this.moduleValueAG = null;
    },

    _getSelectedOption: function () {
        var ops = this.node.getElements('oo-option');
        for (var i = 0; i < ops.length; i++) {
            if (ops[i].selected) return ops[i];
        }
        return null;
    },
    _getInputTextData: function () {
        return {value: this.node.value, text: this.node.text};
    },
    getText: function () {
        return this.node.text;
    },
    getInputData: function () {
        return this.node.value;
    },
    resetData: function () {
        this.setData(this.getValue());
    },

    setData: function (data, fireChange) {
        return this._setValue(data, '__setData', fireChange);
    },

    __setData: function (data, fireChange) {
        var old = this.getInputData();
        this.moduleValueAG = null;
        this._setBusinessData(data);
        this.node.set('value', data || '');
        this.fieldModuleLoaded = true;
        if (fireChange && old !== data) this.fireEvent('change');
        return value;
    },

    notValidationMode: function (text) {
        this.validationText = text;
        this.node.checkValidity();
    },
    validationMode: function () {
        this.validationText = '';
        this.node.unInvalidStyle();
    },
});
