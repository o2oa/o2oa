MWF.xDesktop.requireApp("process.Xform", "Number", null, false);
/** @class Currency 货币输入组件。
 * @o2cn 货币输入组件
 * @example
 * //可以在脚本中获取该组件
 * //方法1：
 * var field = this.form.get("name"); //获取组件
 * //方法2
 * var field = this.target; //在组件事件脚本中获取
 * @extends MWF.xApplication.process.Xform.Number
 * @o2category FormComponents
 * @o2range {Process|CMS}
 * @hideconstructor
 */
MWF.xApplication.process.Xform.Currency = MWF.APPCurrency =  new Class({
    Implements: [Events],
    Extends: MWF.APPNumber,
    iconStyle: "currencyIcon",
    _loadMergeAmountReadNode: function(){
        var data = this.getBusinessDataById();
        var total = new Decimal(0);
        for( var key in data ){
            total = total.plus(new Decimal(data[key] || 0));
        }
        this.node.set("text", this.formatNumber(total.toString()));
        this.loadSymboleRead();
    },
    _loadMergeAverageReadNode: function(){
        var data = this.getBusinessDataById();
        var total = new Decimal(0);
        for( var key in data ){
            total = total.plus(new Decimal(data[key] || 0));
        }
        var average = total.div(  new Decimal(Object.keys(data).length) );
        this.node.set("text", this.formatNumber(average.toString()));
        this.loadSymboleRead();
    },
    _resetNodeEdit: function(){
        var input = new Element("input", {
            "styles": {
                "background": "transparent",
                "width": "100%",
                "border": "0px"
            }
        });
        input.setStyles( this.json.recoveryInputStyles || {} );

        var node = new Element("div", {"styles": {
                "overflow": "hidden",
                "position": "relative",
                "margin-right": "20px",
                "padding-right": "4px"
            }}).inject(this.node, "after");
        node.setStyles( this.json.recoveryStyles || {} );
        input.inject(node);

        if( this.json.currencySymbol ){
            var symbole = new Element("span.MWFCurrencySymbol", {
                text: this.json.currencySymbol
            });

            symbole.inject( node.offsetParent !== null ? node : this.form.node );

            symbole.setStyles( this.json.symbolStyles || {} );

            var paddingLeft = symbole.getSize().x + 5;
            var inputPadding = input.getStyle("padding-right");
            var width = paddingLeft;
            if( this.isNumber( inputPadding ) ){
                width = width + parseFloat(inputPadding);
            }
            input.setStyles({
                "padding-left": paddingLeft + "px",
                "width": "calc( 100% - " + width +"px )"
            });

            if( node.offsetParent === null )symbole.inject( node );

            symbole.setStyles({
                "top": "0px",
                "left": "0px",
                "position": "absolute"
            });
        }

        this.node.destroy();
        this.node = node;
    },

    _loadNodeEdit: function(){
        //if (!this.json.preprocessing) this._resetNodeEdit();
        this._resetNodeEdit();
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
    __setData: function(data, fireChange){
        var old = this.getInputData();
        this._setBusinessData(data);
        if (this.node.getFirst()){
            this.node.getFirst().set("value", this.formatNumber(data));
            this.checkDescription();
            this.validationMode();
        }else{
            this.node.set("text", this.formatNumber(data));
            this.loadSymboleRead();
        }
        if (fireChange && old!==data) this.fireEvent("change");
        this.moduleValueAG = null;
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
        if (this.isReadonly()) {
            this.node.set("text", value || val);
            this.loadSymboleRead()
        }
        this.moduleValueAG = null;
        this.fieldModuleLoaded = true;
        return value;
    },
    loadSymboleRead: function () {
        var symbole = new Element("span.MWFCurrencySymbol", {
            text: this.json.currencySymbol,
            styles: this.json.symbolStyles || {}
        }).inject(this.node, "top");
        var paddingRight = symbole.getStyle("padding-right");
        if( typeOf(paddingRight) === "string" && parseInt(paddingRight) === 0 ){
            symbole.setStyle("padding-right", "5px");
        }
    }
});
