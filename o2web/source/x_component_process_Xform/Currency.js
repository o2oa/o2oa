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
    iconStyle: "currencyIcon",

    _resetNodeEdit: function(){
        var input = new Element("input", {
            "styles": {
                "background": "transparent",
                "width": "100%",
                "border": "0px",
                "padding-left": "20px"
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
            var symbole = new Element("div.MWFCurrencySymbol", {
                styles:{
                    "position": "absolute",
                    "width": "20px",
                    "height": "20px",
                    "top": "0px",
                    "left": "0px"
                },
                text: this.json.currencySymbol
            }).inject( node );
            symbole.setStyles( this.json.recoveryStyles || {} );
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
    }
});
