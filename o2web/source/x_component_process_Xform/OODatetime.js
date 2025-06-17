MWF.xDesktop.requireApp('process.Xform', '$Input', null, false);
MWF.xApplication.process.Xform.OODatetime = MWF.APPOODatetime = new Class({
    Implements: [Events],
    Extends: MWF.APP$Input,
    iconStyle: 'textFieldIcon',
    options: {
        moduleEvents: ['load', 'queryLoad', 'postLoad'],
    },
    _loadNode: function () {
        this._loadNodeEdit();
    },
    loadDescription: function () {
        this.node.setAttribute('placeholder', this.json.description || '');
    },
    _loadDomEvents: function () {
        Object.each(
            this.json.events,
            function (e, key) {
                if (e.code) {
                    if (this.options.moduleEvents.indexOf(key) === -1) {
                        this.node.addEvent(
                            key,
                            function (event) {
                                return this.form.Macro.fire(e.code, this, event);
                            }.bind(this),
                        );
                    }
                }
            }.bind(this),
        );
    },
    _loadNodeEdit: function () {
        this.node.set({
            id: this.json.id,
            MWFType: this.json.type,
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

        if (this.json.showIcon !== 'no' && !this.form.json.hideModuleIcon) {
            this.node.setAttribute('right-icon', 'calendar');
        } else if (this.form.json.nodeStyleWithhideModuleIcon) {
            this.node.setAttribute('right-icon', '');
        }

        if (this.json.inDatatable){
            this.node.setAttribute('view-style', '');
        }

        this.node.setAttribute('readonly', true);
        this.node.setAttribute('readmode', false);
        this.node.setAttribute('disabled', false);
        this.node.setAttribute('read', false);

        if (!this.isReadonly()) {
            if (this.json.showMode === 'readonlyMode') {
                this.node.setAttribute('readonly', true);
                this.node.setAttribute('read', true);
            } else if (this.json.showMode === 'disabled') {
                this.node.setAttribute('disabled', true);
                this.node.setAttribute('read', true);
            } else if (this.json.showMode === 'read') {
                this.node.setAttribute('readmode', true);
                this.node.setAttribute('read', true);
                if (this.json.readModeEvents !== 'yes') {
                    this.node.setStyle('pointer-events', 'none');
                }
            } else {
                this.node.setAttribute('readonly', true);
            }
        } else {
            this.node.setAttribute('readmode', true);
            if (this.json.readModeEvents !== 'yes') {
                this.node.setStyle('pointer-events', 'none');
            }
        }
        if (this.json.required) {
            this.node.setAttribute('required', true);
            if (!this.json.validationConfig) this.json.validationConfig = [];
            var label = this.json.label ? `“${this.json.label.replace(/　/g, '')}”` : MWF.xApplication.process.Xform.LP.requiredHintField;
            this.json.validationConfig.push({
                status: 'all',
                decision: '',
                valueType: 'value',
                operateor: 'isnull',
                value: '',
                prompt: MWF.xApplication.process.Xform.LP.requiredHint.replace('{label}', label),
            });
        } else {
            this.node.removeAttribute('required');
        }

        // this.node.setAttribute('year-only', false);
        // this.node.setAttribute('month-only', false);
        // this.node.setAttribute('date-only', false);
        // this.node.setAttribute('week-only', false);
        // this.node.setAttribute('time-only', false);

        if (this.json.defaultView){
            this.node.setAttribute('view', this.json.defaultView);
        }

        if (this.json.dataType) {
            switch (this.json.dataType) {
                case 'date-only':
                    this.node.setAttribute('mode', 'date');
                    break;
                case 'month-only':
                    this.node.setAttribute('mode', 'month');
                    break;
                case 'year-only':
                    this.node.setAttribute('mode', 'year');
                    break;
                case 'time-only':
                    this.node.setAttribute('mode', 'time');
                    break;
                case 'week-only':
                    this.node.setAttribute('mode', 'week');
                    break;
                default:
            }
            // if (this.json.dataType !== 'dateTime') {
            //     this.node.setAttribute(this.json.dataType, true);
            // }
        }
        if (this.json.secondEnable === 'yes') {
            this.node.setAttribute('second-enable', true);
        } else {
            this.node.setAttribute('second-enable', false);
        }

        this.node.setAttribute('week-begin', this.json.weekBegin || 1);

        if (this.json.format) this.node.setAttribute('format', this.json.format);

        this.node.addEvent(
            'change',
            function () {
                var v = this.getInputData('change');
                this.validationMode();
                this.validation();
                this._setBusinessData(v);
                this.fireEvent('change');
            }.bind(this),
        );

        this.node.addEventListener('validity', (e) => {
            if (this.validationText) {
                e.target.setCustomValidity(this.validationText);
            }
        });
    },
    createModelNode: function () {
        this.modelNode = new Element('div', {styles: this.form.css.modelNode}).inject(this.node, 'after');
        new Element('div', {
            styles: this.form.css.modelNodeTitle,
            text: MWF.xApplication.process.Xform.LP.ANNInput,
        }).inject(this.modelNode);
        new Element('div', {
            styles: this.form.css.modelNodeContent,
            text: MWF.xApplication.process.Xform.LP.ANNInput,
        }).inject(this.modelNode);
    },
    __setData: function (data, fireChange) {
        var old = this.getInputData();
        this._setBusinessData(data);
        this.node.value = data;
        if (fireChange && old !== data) this.fireEvent('change');
        this.moduleValueAG = null;
    },
    __setValue: function (value) {
        this.moduleValueAG = null;
        this._setBusinessData(value);
        this.node.set('value', value || '');
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
        this.node.unInvalidStyle();
    },
});
