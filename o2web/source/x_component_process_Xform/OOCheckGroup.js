MWF.xDesktop.requireApp('process.Xform', 'OORadioGroup', null, false);
MWF.xApplication.process.Xform.OOCheckGroup = MWF.APPOOCheckGroup = new Class({
    Implements: [Events],
    Extends: MWF.APPOORadioGroup,
    options: {
        "moduleEvents": ["load", "queryLoad", "postLoad"],
        "tag": "oo-checkbox-group",
        "itemTag": "oo-checkbox"
    },

    // _loadNode: function () {
    //     // if (this.isReadonly() || this.json.showMode==="read"){
    //     //     this._loadNodeRead();
    //     // }else{
    //     this._loadNodeEdit();
    //     // }
    // },
    //
    //
    _loadNodeEdit: function () {
        var node = new Element(this.options.tag, {
            'id': this.json.id,
            'MWFType': this.json.type,
            // "label-style": "width:6.2vw; min-width:4.3em; max-width:9em"
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
        if (this.json.canSelectCount){
            this.node.setAttribute('count', this.json.canSelectCount);
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
            // if (this.node.value.length) this.validation();
            this._setBusinessData(v);
            this.fireEvent('change');

        }.bind(this));

        this.node.addEventListener('validity', (e) => {
            if (this.validationText) {
                e.target.setCustomValidity(this.validationText);
            }
        });

        this.setOptions();
    },
    _setValue: function(value, m, fireChange){
        var mothed = m || "__setValue";
        if (!!value){
            var p = o2.promiseAll(value).then(function(v){
                //if (o2.typeOf(v)=="array") v = v[0];
                if (this.moduleSelectAG){
                    this.moduleValueAG = this.moduleSelectAG;
                    this.moduleSelectAG.then(function(){
                        this[mothed](v, fireChange);
                        return v;
                    }.bind(this), function(){});
                }else{
                    this[mothed](v, fireChange)
                }
                return v;
            }.bind(this), function(){});
            this.moduleValueAG = p;
            if (this.moduleValueAG) this.moduleValueAG.then(function(){
                this.moduleValueAG = null;
            }.bind(this), function(){
                this.moduleValueAG = null;
            }.bind(this));
        }else{
            this[mothed](value, fireChange);
        }
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

    getSelectedInput: function(){
        var inputs = this.node.getElements(this.options.itemTag);
        var items = [];
        for (var i of inputs){
            if (i.checked) items.push(i);
        }
        return items;
    }
    // __setData: function(data, fireChange){
    //     this.moduleValueAG = null;
    //     this._setBusinessData(data);
    //     this.node.value = data;
    //     this.validationMode();
    //     this.fieldModuleLoaded = true;
    //     this.fireEvent("setData");
    // },
    //
    // notValidationMode: function (text) {
    //     this.validationText = text;
    //     this.node.checkValidity();
    // },
    // validationMode: function () {
    //     this.validationText = '';
    // }
});
