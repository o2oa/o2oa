MWF.xDesktop.requireApp('process.Xform', 'Org', null, false);
MWF.xApplication.process.Xform.OOOrg = MWF.APPOOOrg = new Class({
    Implements: [Events],
    Extends: MWF.APPOrg,
    iconStyle: 'textFieldIcon',
    options: {
        "moduleEvents": ["load", "queryLoad", "postLoad", "change", "select", "removeItem"]
    },
    _loadNode: function () {
        this._getOrgOptions();
        this._loadNodeEdit();
    },
    loadDescription: function () {
        this.node.setAttribute('placeholder', this.json.description || '');
    },
    _loadDomEvents: function(){
        if (!((this.json.showMode === 'read' || this.isReadonly()) && this.json.readModeEvents!=='yes')) {
            Object.each(this.json.events, function(e, key){
                if (e.code){
                    if (this.options.moduleEvents.indexOf(key)===-1){
                        this.node.addEvent(key, function(event){
                            return this.form.Macro.fire(e.code, this, event);
                        }.bind(this));
                    }
                }
            }.bind(this));
        }
    },
    _loadNodeEdit: function () {
        this.node.set({
            'id': this.json.id,
            'MWFType': this.json.type,
            'validity-blur': 'true',
            // "label-style": "width:6.2vw; min-width:5em; max-width:9em"
        });

        if (this.json.label) {
            this.node.setAttribute('label', this.json.label);
        }

        if (this.json.showIcon !== 'no' && !this.form.json.hideModuleIcon) {
            this.node.setAttribute('right-icon', 'person');
        } else if (this.form.json.nodeStyleWithhideModuleIcon) {
            this.node.setAttribute('right-icon', '');
        }

        if (this.json.properties) {
            this.node.set(this.json.properties);
        }
        if (this.json.styles) {
            this.node.setStyles(this.json.styles);
        }

        this.node.setAttribute('readonly', false);
        this.node.setAttribute('readmode', false);
        this.node.setAttribute('disabled', false);

        if (!this.isReadonly()){
            if (this.json.showMode === 'readonlyMode') {
                this.node.setAttribute('readonly', true);
            } else if (this.json.showMode === 'disabled') {
                this.node.setAttribute('disabled', true);
            } else if (this.json.showMode === 'read') {
                this.node.setAttribute('readmode', true);
                // if (this.json.readModeEvents!=='yes'){
                //     this.node.setStyle('pointer-events', 'none');
                // }
            } else {
            }
        }else{
            this.node.setAttribute('readmode', true);
            // if (this.json.readModeEvents!=='yes'){
            //     this.node.setStyle('pointer-events', 'none');
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

        this.node.addEvent('change', function () {
            debugger;
            var v = this.getInputData('change');
            this.validationMode();
            this.validation();
            this._setBusinessData(v);
            this.fireEvent('change');
        }.bind(this));

        this.node.addEventListener('validity', (e) => {
            if (this.validationText) {
                e.target.setCustomValidity(this.validationText);
            }
        });

        this.node.setAttribute('sel-config', 'o29');
        this.node.select = ()=>{
            this.node.unInvalidStyle();
            this.clickSelect();
        }
    },
    createModelNode: function () {
    },


    clickSelect: function( ev ){
        if (this.isReadonly())return;
        if( layout.mobile ){
            setTimeout( function(){ //如果有输入法界面，这个时候页面的计算不对，所以等100毫秒
                var options = this.getOptions();
                if(options){
                    if( !options.title && this.json.label ){
                        var select = MWF.xApplication.process.Xform.LP.select;
                        options.title = this.json.label.contains(select) ? this.json.label : (select+this.json.label);
                    }
                    if( this.selector && this.selector.loading ) {
                    }else if( this.selector && this.selector.selector && this.selector.selector.active ){
                    }else{
                        /**
                         * @summary 人员选择框package的对象
                         * @member {o2.O2Selector}
                         * @example
                         *  //可以在脚本中获取该组件
                         * var selector = this.form.get("fieldId").selector.selector; //获取人员选择框对象
                         * var options = selector.options; //获取人员选择框的选项
                         */
                        //options.style = 'v10';
                        Object.assign(options, {
                            style: "v10_mobile",
                            tabStyle: "v10_mobile",
                            useBreadcrumbs: true,
                            contentUrl: "../x_component_Selector/$Selector/v10_mobile/selector.html",
                            categoryUrl: "../x_component_Selector/$Selector/v10_mobile/category.html",
                            categoryItemUrl: "../x_component_Selector/$Selector/v10_mobile/category_item.html",
                            itemUrl: "../x_component_Selector/$Selector/v10_mobile/item.html",
                            useO2Load: true,
                            injectToBody: true
                        });
                        this.selector = new MWF.O2Selector(this.form.app.content, options);
                    }
                }
            }.bind(this), 100 );
        }else{
            var options = this.getOptions();
            if(options){
                if( !options.title && this.json.label ){
                    var select = MWF.xApplication.process.Xform.LP.select;
                    options.title = this.json.label.contains(select) ? this.json.label : (select+this.json.label);
                }
                if( this.selector && this.selector.loading ) {
                }else if( this.selector && this.selector.selector && this.selector.selector.active ){
                }else {
                    options.style = 'v10';
                    options.tabStyle = 'v10';
                    this.selector = new MWF.O2Selector(this.form.app.content, options);
                }
            }
        }
    },

    selectOnComplete: function(items){
        var array = [];
        items.each(function(item){
            array.push(item.data);
        }.bind(this));

        var simple = this.json.storeRange === "simple";

        this.checkEmpower( array, function( data ){
            var values = [];

            data.each(function(d){
                values.push(MWF.org.parseOrgData(d, true, simple));
            }.bind(this));

            if (this.json.isInput){
                this.addData(values);
            }else{
                this.setData(values, true);
            }

            //this._setBusinessData(values);
            this.validationMode();
            this.validation();

            var p = this.getValue();
            if (p.then){
                p.then(function(){
                    if (this.node._props.validityBlur){
                        this.node.checkValidity();
                    }
                    this.node.dispatchEvent(new MouseEvent('change'));
                    this.fireEvent("select");
                }.bind(this), function(){});
            }else{
                if (this.node._props.validityBlur){
                    this.node.checkValidity();
                }
                this.node.dispatchEvent(new MouseEvent('change'));
                this.fireEvent("select");
            }

        }.bind(this))
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
        this.node.value = value.map(v => (v.distinguishedName || v)).join(', ');
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

    _setValue: function(value){
        var flag = false;
        if (typeOf(value)!=="array") value = (!!value) ? [value] : [];

        this.__setValue(value);
        return value;
    },
    isReadonly : function(){
        var readonly = !!(this.readonly || this.form.json.isReadonly || this.json.showMode==="read");
        if( readonly )return readonly;
        if( this.json.isReadonly === "script" ){
            if( this.json.readonlyScript && this.json.readonlyScript.code ){
                readonly = this.form.Macro.exec(this.json.readonlyScript.code, this);
            }
        }else{
            readonly = !!this.json.isReadonly
        }
        return readonly || !!this.isSectionMergeRead();
    },
    // addModuleEvent: function(key, fun){
    //     if (this.options.moduleEvents.indexOf(key)!==-1){
    //         this.addEvent(key, function(event){
    //             return (fun) ? fun(this, event) : null;
    //         }.bind(this));
    //     }else{
    //         this.node.addEvent(key, function(event){
    //             return (fun) ? fun(this, event) : null;
    //         }.bind(this));
    //     }
    // },
});
