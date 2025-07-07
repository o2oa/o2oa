MWF.xDesktop.requireApp('process.Xform', '$Input', null, false);
MWF.xApplication.process.Xform.OOCurrency = MWF.APPOOCurrency = new Class({
    Implements: [Events],
    Extends: MWF.APP$Input,
    iconStyle: 'textFieldIcon',
    options: {
        "moduleEvents": ["load", "queryLoad", "postLoad"]
    },
    _loadNode: function () {
        if (!this.isReadable && !!this.isHideUnreadable){
            this.node.setStyle('display', 'none');
        }else{
            this._loadNodeEdit();
        }
    },

    loadDescription: function () {
        this.node.setAttribute('placeholder', this.json.description || '');
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
    _loadNodeEdit: function () {
        this.node.set({
            'id': this.json.id,
            'MWFType': this.json.type,
            'validity-blur': 'true',
            // "label-style": "width:6.2vw; min-width:5em; max-width:9em"
        });

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

        if (this.json.showIcon != 'no' && !this.form.json.hideModuleIcon) {
            this.node.setAttribute('right-icon', (this.json.properties && this.json.properties["right-icon"]) || 'currency');
        } else if (this.form.json.nodeStyleWithhideModuleIcon) {
            this.node.setAttribute('right-icon', '');
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


        if (this.json.innerHTML){
			this.node.set("html", this.json.innerHTML);
		}

        this.checkCurrencyAttribute();

        this.node.addEvent('change', function () {
            var v = this.getInputData('change');
            this.validationMode();
            this.validation()
            this._setBusinessData(v);
            this.fireEvent('change');
        }.bind(this));

        // var inputNode = this.node;
        // if (inputNode) inputNode.addEvent('input', function (e) {
        //     var v = e.target.get('value');
        //     this._setBusinessData(v);
        // }.bind(this));

        this.node.addEvent('blur', function () {
            this.validationMode();
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
    },
    checkCurrencyAttribute: function (){
        var checkAttribute = function (name) {
            if( typeof this.json[name] === 'undefined' ){
                console.log('removeAttribute', name);
                removeAttribute(name);
            }else{
                if( this.node.getAttribute(name) !== this.json[name].toString() ){
                    console.log('setAttribute', name, this.json[name], this.node.getAttribute(name));
                    this.node.setAttribute(name, this.json[name]);
                }
            }
        }.bind(this);
        var removeAttribute = function (name) {
            this.node.hasAttribute(name) && this.node.removeAttribute(name);
        }.bind(this);

        if( this.json.preset === 'currency' ){
            removeAttribute("prefix");
            removeAttribute("suffix");
            removeAttribute('thousands');
            removeAttribute('decimal');
            checkAttribute('currency');
            checkAttribute('prefixuse');
        }else{
            if( !this.json.decimal ){
                this.json.decimal = '.';
            }
            removeAttribute('currency');
            removeAttribute('prefixuse');
            checkAttribute("prefix");
            checkAttribute("suffix");
            checkAttribute('thousands');
            checkAttribute('decimal');
        }

        ['precision','allowblank','disablenegative', 'round'].forEach(function (name){
            checkAttribute(name);
        }.bind(this));

        ['maximum','minimum'].forEach(function (name){
            this.json[name] === '' ? removeAttribute(name) : checkAttribute(name);
        }.bind(this));
    },
    createModelNode: function () {
    },
    __setData: function(data, fireChange){
        var old = this.getInputData();
        this.validationMode();
        this._setBusinessData(data);
        this.node.value = data;
        if (fireChange && old!==data) this.fireEvent("change");
        this.moduleValueAG = null;
    },
    __setValue: function (value) {
        this.moduleValueAG = null;
        this.validationMode();
        this._setBusinessData(value);
        // this.node.set('value', value || '');
        this.node.value = value;
        this.fieldModuleLoaded = true;
        return value;
    },

    getInputData: function () {
        return this.node.value;
    },

    notValidationMode: function (text) {
        this.validationText = text;
        this.node.checkValidity();

        if ( this.node && !this.node.isIntoView()) this.node.scrollIntoView({ behavior: "smooth", block: "center" });
    },
    validationMode: function () {
        this.validationText = '';
        this.node.unInvalidStyle();
    }
});
