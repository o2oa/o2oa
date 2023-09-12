MWF.xDesktop.requireApp("process.Xform", "Number", null, false);
/** @class Currency 货币输入组件。
 * @o2cn 货币输入组件
 * @example
 * //可以在脚本中获取该组件
 * //方法1：
 * var field = this.form.get("name"); //获取组件
 * //方法2
 * var field = this.target; //在组件事件脚本中获取
 * @extends MWF.xApplication.process.Xform.Currency
 * @o2category FormComponents
 * @o2range {Process|CMS}
 * @hideconstructor
 */
MWF.xApplication.process.Xform.Currency = MWF.APPCurrency =  new Class({
    Implements: [Events],
    Extends: MWF.APPNumber,
    iconStyle: "numberIcon",
    unformatNumber: function(str){
        return str.replace(/,/g, "");
    },
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
    }
});
