MWF.xDesktop.requireApp('process.Xform', 'Radio', null, false);
MWF.xApplication.process.Xform.OORadioGroup = MWF.APPOORadioGroup = new Class({
    Implements: [Events],
    Extends: MWF.APPRadio,
    options: {
        "moduleEvents": ["load", "queryLoad", "postLoad"],
        "tag": "oo-radio-group",
        "itemTag": "oo-radio"
    },

    _loadNode: function () {
        // if (this.isReadonly() || this.json.showMode==="read"){
        //     this._loadNodeRead();
        // }else{
            if (!this.isReadable && !!this.isHideUnreadable){
                this.node?.addClass('hide');
            }else{
                this._loadNodeEdit();
            }
        // }
    },
    getValue: function(){
        if (!this.isReadable) return '';
        if (this.moduleValueAG) return this.moduleValueAG;
        var value = this._getBusinessData();
        if (!value) value = this._computeValue();
        return value || "";
    },
    _loadNodeEdit: function () {
        var node = new Element(this.options.tag, {
            'id': this.json.id,
            'MWFType': this.json.type,
            // "label-style": "width:6.2vw; min-width:5em; max-width:9em"
        }).inject(this.node, 'before');
        this.node.destroy();
        this.node = node;
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
        if (!this.json.countPerline || this.json.countPerline==="0") {
            this.node.removeAttribute('col');
        }else{
            this.node.setAttribute('col', this.json.countPerline);
        }

        if (this.json.inDatatable){
            this.node.setAttribute('view-style', '');
        }

        if (!this.isReadonly() && this.isEditable){
            if (this.json.showMode === 'disabled') {
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

        this.node.addEvent('change', function () {
            var v = this.getInputData('change');
            this.validationMode();
            if (this.node.value) this.validation();
            this._setBusinessData(v);
            this.fireEvent('change');
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
    _setOptions: function(optionItems) {
        var p = o2.promiseAll(optionItems).then(function (radioValues) {
            this.moduleSelectAG = null;

            if (!radioValues) radioValues = [];
            if (o2.typeOf(radioValues) === "array") {
                var flag = (new MWF.widget.UUID).toString();
                radioValues.each(function (item, i) {
                    var tmps = item.split("|");
                    var text = tmps[0];
                    var value = tmps[1] || text;
                    var value = tmps[1] || text;

                    var radio = new Element(this.options.itemTag, {
                        "type": "radio",
                        "name": (this.json.properties && this.json.properties.name) ? this.json.properties.name : flag + this.json.id,
                        "value": value,
                        // "text": text,
                        "styles": this.json.buttonStyles
                    }).inject(this.node);
                    radio.setAttribute('text', text);



                }.bind(this));
            }
        }.bind(this), function () {
            this.moduleSelectAG = null;
        }.bind(this));
        this.moduleSelectAG = p;
        if (p) p.then(function () {
            this.moduleSelectAG = null;
        }.bind(this), function () {
            this.moduleSelectAG = null;
        }.bind(this));
    },
    __setData: function(data, fireChange){
        var old = this.getInputData();
        this._setBusinessData(data);
        this.node.value = data;
        if (fireChange && old!==data) this.fireEvent("change");
        this.moduleValueAG = null;
    },
    __setValue: function(value){
        this.moduleValueAG = null;
        this._setBusinessData(value);
        this.node.value = value;

        this.fieldModuleLoaded = true;
    },
    getTextData: function () {
        return {"value": this.node.value , "text": this.node.text};
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
    getSelectedInput: function(){
        var inputs = this.node.getElements(this.options.itemTag);
        for (var i of inputs){
            if (i.checked) return i;
        }
        return null;
    },
    __setData: function(data, fireChange){
        this.moduleValueAG = null;
        this._setBusinessData(data);
        this.node.value = data;
        this.validationMode();
        this.fieldModuleLoaded = true;
        this.fireEvent("setData");
    },


    //
    // createModelNode: function () {
    //     this.modelNode = new Element('div', {'styles': this.form.css.modelNode}).inject(this.node, 'after');
    //     new Element('div', {
    //         'styles': this.form.css.modelNodeTitle,
    //         'text': MWF.xApplication.process.Xform.LP.ANNInput
    //     }).inject(this.modelNode);
    //     new Element('div', {
    //         'styles': this.form.css.modelNodeContent,
    //         'text': MWF.xApplication.process.Xform.LP.ANNInput
    //     }).inject(this.modelNode);
    // },
    // __setValue: function (value) {
    //     this.moduleValueAG = null;
    //     this._setBusinessData(value);
    //     this.node.set('value', value || '');
    //     this.fieldModuleLoaded = true;
    //     return value;
    // },

    notValidationMode: function (text) {
        this.validationText = text;
        this.node.checkValidity();
    },
    validationMode: function () {
        this.validationText = '';
        this.node.unInvalidStyle();
    }
});
