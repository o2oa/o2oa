MWF.xDesktop.requireApp("process.Xform", "Textfield", null, false);
/** @class Number 数字输入组件。
 * @example
 * //可以在脚本中获取该组件
 * //方法1：
 * var field = this.form.get("name"); //获取组件
 * //方法2
 * var field = this.target; //在组件事件脚本中获取
 * @extends MWF.xApplication.process.Xform.Textfield
 * @o2category FormComponents
 * @o2range {Process|CMS}
 * @hideconstructor
 */
MWF.xApplication.process.Xform.Number = MWF.APPNumber =  new Class({
    Implements: [Events],
    Extends: MWF.APPTextfield,
    iconStyle: "numberIcon",
    isEmpty : function(){
        return !this.getData();
    },
    getInputData: function(){
        if (this.node.getFirst()){
            var v = this.node.getElement("input").get("value");
            var n = v.toFloat();
            return (isNaN(n)) ? 0 : n;
        }else{
            return this._getBusinessData();
        }
        return v;
    },
    // getInputData: function(){
    //     var n = this.node.getElement("input").get("value").toFloat();
    //     if ((isNaN(n))) {this.setData('0')};
    //     return (isNaN(n)) ? 0 : n;
    // },

    formatNumber: function(str){
        var v = str.toFloat();
        if (v){
            if (this.json.decimals && (this.json.decimals!="*")){

                var decimals = this.json.decimals.toInt();

                var p = Math.pow(10,decimals);
                var f_x = Math.round(v*p)/p;
                str = f_x.toString();

                if (decimals>0){
                    var pos_decimal = str.indexOf('.');
                    if (pos_decimal < 0){
                        pos_decimal = str.length;
                        str += '.';
                    }
                    decimalStr = (str).substr(pos_decimal+1, (str).length);
                    while (decimalStr.length < decimals){
                        str += '0';
                        decimalStr += 0;
                    }
                }
            }
        }
        return str;
    },

    validationFormat: function(){
debugger;
        if( !this.node.getElement("input") )return true;
        var n = this.node.getElement("input").get("value");
        if (isNaN(n)) {
            this.notValidationMode(MWF.xApplication.process.Xform.LP.notValidation_number);
            return false;
        }

        this.node.getFirst().set("value", this.formatNumber(n));

        // var v = n.toFloat();
        // if (v){
        //     if (this.json.decimals && (this.json.decimals!="*")){
        //
        //         var decimals = this.json.decimals.toInt();
        //
        //         var p = Math.pow(10,decimals);
        //         var f_x = Math.round(v*p)/p;
        //         var s_x = f_x.toString();
        //
        //         if (decimals>0){
        //             var pos_decimal = s_x.indexOf('.');
        //             if (pos_decimal < 0){
        //                 pos_decimal = s_x.length;
        //                 s_x += '.';
        //             }
        //             decimalStr = (s_x).substr(pos_decimal+1, (s_x).length);
        //             while (decimalStr.length < decimals){
        //                 s_x += '0';
        //                 decimalStr += 0;
        //             }
        //         }
        //
        //         this.node.getFirst().set("value", s_x);
        //     }
        // }
        return true;
    },
    validationConfigItem: function(routeName, data){

        var flag = (data.status=="all") ? true: (routeName == data.decision);
        if (flag){
            var n = this.getInputData();
            var v = (data.valueType=="value") ? n : n.length;
            switch (data.operateor){
                case "isnull":
                    if (!v && v.toString()!=='0'){
                        this.notValidationMode(data.prompt);
                        return false;
                    }
                    break;
                case "notnull":
                    if (v){
                        this.notValidationMode(data.prompt);
                        return false;
                    }
                    break;
                case "gt":
                    if (v>data.value){
                        this.notValidationMode(data.prompt);
                        return false;
                    }
                    break;
                case "lt":
                    if (v<data.value){
                        this.notValidationMode(data.prompt);
                        return false;
                    }
                    break;
                case "equal":
                    if (v==data.value){
                        this.notValidationMode(data.prompt);
                        return false;
                    }
                    break;
                case "neq":
                    if (v!=data.value){
                        this.notValidationMode(data.prompt);
                        return false;
                    }
                    break;
                case "contain":
                    if (v.indexOf(data.value)!=-1){
                        this.notValidationMode(data.prompt);
                        return false;
                    }
                    break;
                case "notcontain":
                    if (v.indexOf(data.value)==-1){
                        this.notValidationMode(data.prompt);
                        return false;
                    }
                    break;
            }
        }
        return true;
    },
    validationConfig: function(routeName, opinion){
        if (this.json.validationConfig){
            if (this.json.validationConfig.length){
                for (var i=0; i<this.json.validationConfig.length; i++) {
                    var data = this.json.validationConfig[i];
                    if (!this.validationConfigItem(routeName, data)) return false;
                }
            }
            return true;
        }
        return true;
    },

    validation: function(routeName, opinion){
        if (!this.readonly && !this.json.isReadonly){
            if (!this.validationFormat()) return false;
            if (!this.validationConfig(routeName, opinion)) return false;

            if (!this.json.validation) return true;
            if (!this.json.validation.code) return true;
            this.currentRouteName = routeName;
            var flag = this.form.Macro.exec(this.json.validation.code, this);
            this.currentRouteName = "";
            if (!flag) flag = MWF.xApplication.process.Xform.LP.notValidation;
            if (flag.toString() != "true") {
                this.notValidationMode(flag);
                return false;
            }
        }
        return true;
    },

    _resetNodeEdit: function(){
        var input = new Element("input", {
            "styles": {
                "background": "transparent",
                "width": "100%",
                "border": "0px"
            }
        });

        var node = new Element("div", {"styles": {
                "overflow": "hidden",
                "position": "relative",
                "margin-right": "20px",
                "padding-right": "4px"
            }}).inject(this.node, "after");
        input.inject(node);

        this.node.destroy();
        this.node = node;
    },

    _loadNodeEdit: function(){
        if (!this.json.preprocessing) this._resetNodeEdit();
        var input = this.node.getFirst();
        input.set(this.json.properties);

        this.node.set({
            "id": this.json.id,
            "MWFType": this.json.type,
            "events": {
                "click": this.clickSelect.bind(this)
            }
        });
        if (this.json.showIcon!='no' && !this.form.json.hideModuleIcon) {
            this.iconNode = new Element("div", {
                "styles": this.form.css[this.iconStyle]
            }).inject(this.node, "before");
        }else if( this.form.json.nodeStyleWithhideModuleIcon ){
            this.node.setStyles(this.form.json.nodeStyleWithhideModuleIcon)
        }

        this.node.getFirst().addEvent("change", function(){
            this.validationMode();
            if (this.validation()) {
                this._setBusinessData(this.getInputData("change"));
                this.fireEvent("change");
            }
        }.bind(this));

        this.node.getFirst().addEvent("blur", function(){
            this.validation();
        }.bind(this));
        this.node.getFirst().addEvent("keyup", function(){
            this.validationMode();
        }.bind(this));
    },
    _computeValue: function(value){
        return (this.json.defaultValue && this.json.defaultValue.code) ? this.form.Macro.exec(this.json.defaultValue.code, this): (value || "0");
    },
    getValue: function(){
        if (this.moduleValueAG) return this.moduleValueAG;
        var value = this._getBusinessData();
        if (!value) value = this._computeValue();
        value = this.formatNumber(value);
        return value || "0";
    },
    __setValue: function(value){
        this._setBusinessData(value);
        if (this.node.getFirst()) this.node.getFirst().set("value", value || "0");
        if (this.readonly || this.json.isReadonly) this.node.set("text", value);
        this.moduleValueAG = null;
        return value;
    }
});
