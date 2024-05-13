MWF.xDesktop.requireApp("process.Xform", "Textfield", null, false);
/** @class Number 数字输入组件。
 * @o2cn 数字输入组件
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
MWF.xApplication.process.Xform.Number = MWF.APPNumber =  new Class(
    /** @lends MWF.xApplication.process.Xform.Number# */
{
    Implements: [Events],
    Extends: MWF.APPTextfield,
    iconStyle: "numberIcon",
    _loadUserInterface: function(){
        if ( this.isSectionMergeRead() ) { //区段合并显示
            this.node.empty();
            this.node.set({
                "nodeId": this.json.id,
                "MWFType": this.json.type
            });
            switch (this.json.mergeTypeRead) {
                case "amount":
                    this._loadMergeAmountReadNode();
                    break;
                case "average":
                    this._loadMergeAverageReadNode();
                    break;
                default:
                    this._loadMergeReadNode();
                    break;
            }
        }else{
            if( this.isSectionMergeEdit() ){
                switch (this.json.mergeTypeEdit) {
                    case "amount":
                        this._loadMergeAmountEidtNode();
                        break;
                    case "average":
                        this._loadMergeAverageEditNode();
                }
            }else{
                this._loadNode();
            }
            if (this.json.compute === "show"){
                this._setValue(this._computeValue());
            }else{
                this._loadValue();
            }
        }
    },
    _loadMergeAmountReadNode: function(){
        var data = this.getBusinessDataById();
        var total = new Decimal(0);
        for( var key in data ){
            total = total.plus(new Decimal(data[key] || 0));
        }
        this.node.set("text", this.formatNumber(total.toString()));
    },
    _loadMergeAverageReadNode: function(){
        var data = this.getBusinessDataById();
        var total = new Decimal(0);
        for( var key in data ){
            total = total.plus(new Decimal(data[key] || 0));
        }
        var average = total.div(  new Decimal(Object.keys(data).length) );
        this.node.set("text", this.formatNumber(average.toString()));
    },
    _loadMergeAmountEidtNode: function(){
        var data = this.getBusinessDataById();
        var total = new Decimal(0);
        for( var key in data ){
            total = total.plus(new Decimal(data[key] || 0));
        }
        this._setBusinessData( total.toNumber() );
        this._loadNode();
    },
    _loadMergeAverageEditNode: function(){
        var data = this.getBusinessDataById();
        var total = new Decimal(0);
        for( var key in data ){
            total = total.plus(new Decimal(data[key] || 0));
        }
        var average = total.div(  new Decimal(Object.keys(data).length) );
        this._setBusinessData( average.toNumber() );
        this._loadNode();
    },
    isEmpty : function(){
        return !this.getData();
    },
    /**
     * @summary 传入值获取适配最大值和最小值的数值.
     * @example
     * //如果最大值设置为500，最小值为100
     *
     * var value = this.form.get('field').getRangeValue( 300 );
     * //value为300
     *
     * var value = this.form.get('field').getRangeValue( 10000 );
     * //value为500
     *
     * var value = this.form.get('field').getRangeValue( 1 );
     * //value为100
     * @param value {String|Nummber}
     * @return {Number} 获取适配最大值和最小值的数值.
     */
    getRangeValue: function( value ){
        var v = this.getMax( value );
        return this.getMin( v );
    },
    getMax: function( value ){
        if( isNaN(value) )return value;
        if( typeOf( value ) === "string" )value = parseFloat(value);
        if( !isNaN( this.json.max )){
            var max = this.json.max;
            if( typeOf( max ) === "string" )max = parseFloat(max);
            return Math.min( max, value );
        }else{
            return value;
        }
    },
    getMin: function( value ){
        if( isNaN(value) )return value;
        if( typeOf( value ) === "string" )value = parseFloat(value);
        if( !isNaN( this.json.min )){
            var min = this.json.min;
            if( typeOf( min ) === "string" )min = parseFloat(min);
            return Math.max( min, value );
        }else{
            return value;
        }
    },
    getInputData: function( flag ){
        var input = this.node.getElement("input");
        if (input){
            var v = input.get("value");
            v = this.unformatNumber( v );
            var n = v.toFloat();
            n = this.getMax( n );
            n = this.getMin( n );
            return (isNaN(n)) ? (this.json.emptyValue === "string" ? "" : 0) : n;
        }else{
            return this._getBusinessData();
        }
    },


    /**
     * @summary 传入文本获取清除逗号的数值
     * @example
     * var value = this.form.get('field').unformatNumber( "30,000" );
     * //value为30000
     * @param str{String} 文本
     * @return {Number} 获取清除逗号的数值.
     */
    unformatNumber: function(str){
        return str.replace(/,/g, "");
    },
    /**
     * @summary 传入文本或数值根据配置的小数位数和分隔符返回字符串
     * @example
     * //假设组件的设置为千分位分隔，保留小数两位
     *
     * var value = this.form.get('field').formatNumber( "30000.123" );
     * //value为字符串"30,000.12"
     *
     * var value = this.form.get('field').formatNumber( 30000.123 );
     * //value为字符串"30,000.12"
     *
     * @param str{String|Number}
     * @return {Number} 根据配置的小数位数和分隔符返回字符串
     */
    formatNumber: function(str){
        var v = (str || "0").toFloat();
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
                    var decimalStr = (str).substr(pos_decimal+1, (str).length);
                    while (decimalStr.length < decimals){
                        str += '0';
                        decimalStr += 0;
                    }
                }
            }
            if( this.json.digitsToSeparate && parseInt(this.json.digitsToSeparate) > 1 ){
                if( typeOf( str ) === "number" )str = str.toString();
                var digits = parseInt(this.json.digitsToSeparate);
                var reg = new RegExp( "(\\d{"+digits+"}\\B)" ,"g");
                var arr = str.split(".");
                var i = arr[0].split("").reverse().join("")
                    .replace(reg, "$1,")
                    .split("").reverse().join("");
                str = arr.length > 1 ? ( i + "." + arr[1] ) : i ;
            }
        }
        return str;
    },

    validationFormat: function(){
        if( !this.node.getElement("input") )return true;
        var n = this.getInputData();
        if (isNaN(n)) {
            if( n === "" && this.json.emptyValue === "string" ){
                return true;
            }else{
                this.notValidationMode(MWF.xApplication.process.Xform.LP.notValidation_number);
                return false;
            }
        }
        return true;
    },

    // validationFormat: function(){
    //     if( !this.node.getElement("input") )return true;
    //     var n = this.node.getElement("input").get("value");
    //     n = this.unformatNumber(n);
    //     if (isNaN(n)) {
    //         if( n === "" && this.json.emptyValue === "string" ){
    //             return true;
    //         }else{
    //             this.notValidationMode(MWF.xApplication.process.Xform.LP.notValidation_number);
    //             return false;
    //         }
    //     }else{
    //         this.node.getFirst().set("value", this.formatNumber(n));
    //     }
    //     return true;
    // },
    validationConfigItem: function(routeName, data){
        var flag = (data.status=="all") ? true: (routeName == data.decision);
        if (flag){
            var n = this.getInputData();
            var strN = n.toString();

            if( n === "" && this.json.emptyValue === "string" )n = 0;

            var v = (data.valueType=="value") ? n : strN.length;
            var strV = (data.valueType=="value") ? strN : strN.length;

            switch (data.operateor){
                case "isnull":
                    if (!strV && strV.toString()!=='0'){
                        this.notValidationMode(data.prompt);
                        return false;
                    }
                    break;
                case "notnull":
                    if (strV){
                        this.notValidationMode(data.prompt);
                        return false;
                    }
                    break;
                case "gt":
                    if (v>parseFloat(data.value)){
                        this.notValidationMode(data.prompt);
                        return false;
                    }
                    break;
                case "lt":
                    if (v<parseFloat(data.value)){
                        this.notValidationMode(data.prompt);
                        return false;
                    }
                    break;
                case "equal":
                    if (v==parseFloat(data.value)){
                        this.notValidationMode(data.prompt);
                        return false;
                    }
                    break;
                case "neq":
                    if (v!=parseFloat(data.value)){
                        this.notValidationMode(data.prompt);
                        return false;
                    }
                    break;
                case "contain":
                    if (strV.toString().indexOf(data.value)!=-1){
                        this.notValidationMode(data.prompt);
                        return false;
                    }
                    break;
                case "notcontain":
                    if (strV.toString().indexOf(data.value)==-1){
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
        debugger;

        if (!this.json.preprocessing) this._resetNodeEdit();
        var input = this.node.getFirst();
        if( !input && this.nodeHtml ){
            this.node.set("html", this.nodeHtml);
            input = this.node.getFirst();
        }
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
                var value = this.getInputData("change");
                this._setBusinessData(value);
                this.node.getFirst().set("value", this.formatNumber( value.toString() ));
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
        if( this.json.defaultValue && this.json.defaultValue.code){
            return this.form.Macro.exec(this.json.defaultValue.code, this)
        }else{
            if(value){
                return value;
            }else{
                return this.json.emptyValue === "string" ? "" : "0";
            }
        }
    },

    __setData: function(data, fireChange){
        var old = this.getInputData();
        this._setBusinessData(data);
        if (this.node.getFirst()){
            this.node.getFirst().set("value", this.formatNumber(data));
            this.checkDescription();
            this.validationMode();
        }else{
            this.node.set("text", this.formatNumber(data));
        }
        if (fireChange && old!==data) this.fireEvent("change");
        this.moduleValueAG = null;
    },

    resetData: function(){
        var value = this.moduleValueAG || this._getBusinessData();
        if (!value) value = this._computeValue();
        this.setData(value || "");
    },

    getValue: function(){
        if (this.moduleValueAG) return this.moduleValueAG;
        var value = this._getBusinessData();
        if( this.json.emptyValue === "string" ){
            if( value === "" || typeOf(value)==="null" )value = this._computeValue();
        }else{
            if( value === "" || value === 0 || typeOf(value)==="null" )value = this._computeValue();
        }
        if( ( value === "" || typeOf(value)==="null" ) && this.json.emptyValue === "string"){
            return "";
        }else{
            value = this.formatNumber(value);
            return value || "0";
        }
    },
    __setValue: function(value){
        var v = typeOf( value ) === "string" ? this.unformatNumber( value ) : value;
        v = this.isNumber( v ) ? parseFloat( v ) : v;
        this._setBusinessData(v);
        var val = value;
        if( this.json.emptyValue === "string" ){
            if( typeOf(v)==="null" )val = "";
            if( v === 0 )val = "0";
        }else{
            if( v === 0 || v === "" || typeOf(v)==="null" )val = "0";
        }
        if (this.node.getFirst()) this.node.getFirst().set("value", value || val);
        if (this.isReadonly()) this.node.set("text", value || val);
        this.moduleValueAG = null;
        this.fieldModuleLoaded = true;
        return value;
    },
    isNumber : function( d ){
        return parseFloat(d).toString() !== "NaN";
    },


    validationConfigItemExcel: function(data){
        if (data.status=="all"){
            var n = this.getInputData();
            var strN = n.toString();

            if( n === "" && this.json.emptyValue === "string" )n = 0;

            var v = (data.valueType=="value") ? n : strN.length;
            var strV = (data.valueType=="value") ? strN : strN.length;

            switch (data.operateor){
                case "isnull":
                    if (!strV && strV.toString()!=='0')return data.prompt;
                    break;
                case "notnull":
                    if (strV)return data.prompt;
                    break;
                case "gt":
                    if (v>data.value)return data.prompt;
                    break;
                case "lt":
                    if (v<data.value)return data.prompt;
                    break;
                case "equal":
                    if (v==data.value)return data.prompt;
                    break;
                case "neq":
                    if (v!=data.value)return data.prompt;
                    break;
                case "contain":
                    if (strV.toString().indexOf(data.value)!=-1)return data.prompt;
                    break;
                case "notcontain":
                    if (strV.toString().indexOf(data.value)==-1)return data.prompt;
                    break;
            }
        }
        return true;
    }
});
