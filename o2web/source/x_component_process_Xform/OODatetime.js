MWF.xDesktop.requireApp('process.Xform', '$Input', null, false);
MWF.xApplication.process.Xform.OODatetime = MWF.APPOODatetime = new Class({
    Implements: [Events],
    Extends: MWF.APP$Input,
    iconStyle: 'textFieldIcon',
    options: {
        moduleEvents: ['load', 'queryLoad', 'postLoad'],
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

        if (!this.isReadonly() && this.isEditable) {
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

        this.setRange();

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
    setRange: function(){
        var range, j = this.json;
        switch ( j.rangeType ) {
            case "dateTime":
                var datetimeRangeScript = j.dateTimeRangeScript && j.dateTimeRangeScript.code;
                if (datetimeRangeScript) {
                    range = this.form.Macro.fire(datetimeRangeScript, this);
                    o2.typeOf(range) === "array" && this.node.setDatetimeRange(range);
                }
                break;
            case "dateAndTime":
                var dateRangeScript = j.dateRangeScript && j.dateRangeScript.code;
                if (dateRangeScript) {
                    range = this.form.Macro.fire(dateRangeScript, this);
                    o2.typeOf(range) === "array" && this.node.setDateRange(range);
                }
                var timeRangeScript = j.timeRangeScript && j.timeRangeScript.code;
                if (timeRangeScript) {
                    range = this.form.Macro.fire(timeRangeScript, this);
                    o2.typeOf(range) === "array" && this.node.setTimeRange(range);
                }
                break;
            case "other":
                var enableYear = j.enableYear && j.enableYear.code;
                if (!!enableYear) {
                    this.node.setCustomRangeMethod('year', function (year) {
                        return this.form.Macro.fire(enableYear, this, {year: year});
                    }.bind(this));
                }
                var enableMonth = j.enableMonth && j.enableMonth.code;
                if (!!enableMonth) {
                    this.node.setCustomRangeMethod('month', function (month) {
                        return this.form.Macro.fire(enableMonth, this, {month: month});
                    }.bind(this));
                }
                var enableDate = j.enableDate && j.enableDate.code;
                if (!!enableDate) {
                    this.node.setCustomRangeMethod('date', function (date) {
                        return this.form.Macro.fire(enableDate, this, {date: date});
                    }.bind(this));
                }
                var enableHour = j.enableHour && j.enableHour.code;
                if (enableHour) {
                    this.node.setCustomRangeMethod('hour', function (date, hour) {
                        return this.form.Macro.fire(enableHour, this, {date: date, hour: hour});
                    }.bind(this));
                }
                var enableMinute = j.enableMinute && j.enableMinute.code;
                if (enableMinute) {
                    this.node.setCustomRangeMethod('minute', function (date, hour, minute) {
                        return this.form.Macro.fire(enableMinute, this, {date: date, hour: hour, minute: minute});
                    }.bind(this));
                }
                var enableSecond = j.enableSecond && j.enableSecond.code;
                if (enableSecond) {
                    this.node.setCustomRangeMethod('second', function (date, hour, minute, second) {
                        return this.form.Macro.fire(enableSecond, this, {date: date, hour: hour, minute: minute, second:second});
                    }.bind(this));
                }
                break;
        }
    }
});
