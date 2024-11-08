MWF.xDesktop.requireApp('process.Xform', '$Input', null, false);
MWF.xApplication.process.Xform.OOInput = MWF.APPOOInput = new Class({
    Implements: [Events],
    Extends: MWF.APP$Input,
    iconStyle: 'textFieldIcon',
    options: {
        "moduleEvents": ["load", "queryLoad", "postLoad"]
    },
    _loadNode: function () {
        // if (this.isReadonly() || this.json.showMode==="read"){
        //     this._loadNodeRead();
        // }else{
        this._loadNodeEdit();
        // }
    },
    loadDescription: function () {
        this.node.setAttribute('placeholder', this.json.description || '');
    },
    _loadDomEvents: function(){
        Object.each(this.json.events, function(e, key){
            if (e.code){
                if (this.options.moduleEvents.indexOf(key)===-1){
                    var target;
                    switch (key){
                        case "change":
                            target = this.node;
                            break;
                        case 'blur': case 'focus':
                            target = (this.node._elements ? this.node._elements.input : null) || this.node;
                            break;
                        default:
                            target = (this.node._elements ? this.node._elements.box : null) || this.node;
                    }
                    target.addEvent(key, function(event){
                        return this.form.Macro.fire(e.code, this, event);
                    }.bind(this));
                }
            }
        }.bind(this));
    },
    _loadNodeEdit: function () {
        // var node = new Element('oo-input', {
        //     'id': this.json.id,
        //     'MWFType': this.json.type,
        //     'events': {
        //         'click': this.clickSelect.bind(this)
        //     }
        // }).inject(this.node, 'before');
        // this.node.destroy();
        // this.node = node;
        this.node.set({
            'id': this.json.id,
            'MWFType': this.json.type,
            'validity-blur': 'true'
        })

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
            this.node.setAttribute('right-icon', 'edit');
        } else if (this.form.json.nodeStyleWithhideModuleIcon) {
            this.node.setAttribute('right-icon', '');
        }

        this.node.setAttribute('readonly', false);
        this.node.setAttribute('readmode', false);
        this.node.setAttribute('disabled', false);

        if (this.json.showMode === 'readonlyMode') {
            this.node.setAttribute('readonly', true);
        } else if (this.json.showMode === 'disabled') {
            this.node.setAttribute('disabled', true);
        } else if (this.json.showMode === 'read') {
            this.node.setAttribute('readmode', true);
        } else {
        }

        if (this.json.dataType){
            this.node.setAttribute("type", this.json.dataType);
        }

        this.node.addEvent('change', function () {
            var v = this.getInputData('change');
            this.validationMode();
            this.validation()
            this._setBusinessData(v);
            this.fireEvent('change');
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
    },
    createModelNode: function () {
        this.modelNode = new Element('div', {'styles': this.form.css.modelNode}).inject(this.node, 'after');
        new Element('div', {
            'styles': this.form.css.modelNodeTitle,
            'text': MWF.xApplication.process.Xform.LP.ANNInput
        }).inject(this.modelNode);
        new Element('div', {
            'styles': this.form.css.modelNodeContent,
            'text': MWF.xApplication.process.Xform.LP.ANNInput
        }).inject(this.modelNode);
    },
    __setData: function(data, fireChange){
        var old = this.getInputData();
        this._setBusinessData(data);
        this.node.value = data;
        if (fireChange && old!==data) this.fireEvent("change");
        this.moduleValueAG = null;
    },
    __setValue: function (value) {
        this.moduleValueAG = null;
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
    },
    validationMode: function () {
        this.validationText = '';
    }
});
