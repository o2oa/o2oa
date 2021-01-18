MWF.xDesktop.requireApp("process.Xform", "$Module", null, false);
/** @class Label 文本组件。
 * @example
 * //可以在脚本中获取该组件
 * //方法1：
 * var label = this.form.get("name"); //获取组件
 * //方法2
 * var label = this.target; //在组件事件脚本中获取
 * @extends MWF.xApplication.process.Xform.$Module
 * @o2category FormComponents
 * @o2range {Process|CMS|Portal}
 * @hideconstructor
 */
MWF.xApplication.process.Xform.Label = MWF.APPLabel =  new Class(
    /** @lends MWF.xApplication.process.Xform.Label# */
    {
	Implements: [Events],
	Extends: MWF.APP$Module,
	
	_loadUserInterface: function(){
		if (this.json.valueType == "text"){
			this.node.set("text", this.json.text || "");
		}
		if (this.json.valueType == "script"){
			var code = (this.json.script) ? this.json.script.code : "";
			if (code){
			    var value = this.form.Macro.exec(code, this);
			    this._setNodeText(value);
				//this.node.set("text", this.form.Macro.exec(code, this) || "");
			} 
		}
		if (this.json.prefixIcon || this.json.suffixIcon){
            var text = this.node.get("text");
            this.node.empty();

            var tNode = new Element("div", {"styles": {
            	"margin-left": (this.json.prefixIcon) ? "20px" : "0px",
                "margin-right": (this.json.suffixIcon) ? "20px" : "0px",
                "height": "100%"
			}, "text": text}).inject(this.node);

            if (this.json.prefixIcon){
                var node = new Element("div", {"styles": {
                    "float": "left",
                    "width": "20px",
                    "height": ""+this.node.getSize().y+"px",
                    "background": "url("+this.json.prefixIcon+") center center no-repeat"
                }}).inject(tNode, "before");
            }
            if (this.json.suffixIcon){
                var node = new Element("div", {"styles": {
                    "float": "right",
                    "width": "20px",
                    "height": ""+this.node.getSize().y+"px",
                    "background": "url("+this.json.suffixIcon+") center center no-repeat"
                }}).inject(tNode, "before");
            }
		}
	},
    _setNodeText: function(value){
        if (value && value.isAG){
            value.addResolve(function(v){
                this._setNodeText(v);
            }.bind(this));
        }else{
            o2.promiseAll(value).then(function(v){
                this.node.set("text", v || "");
            }.bind(this), function(){});
            //this.node.set("text", value || "");
        }
    },
    /**当参数为Promise的时候，请参考文档: {@link  https://www.yuque.com/o2oa/ixsnyt/ws07m0|使用Promise处理表单异步}<br/>
     * @summary 为组件设置文本，该文本不会被保存到后台。
     * @param text{String|Promise} .
     * @example
     *  this.form.get("fieldId").setText("test"); //赋文本值
     * @example
     *  //使用Promise
     *  var field = this.form.get("fieldId");
     *  var dict = new this.Dict("test"); //test为数据字典名称
     *  var promise = dict.get("tools", true); //异步使用数据字典的get方法时返回Promise，参数true表示异步
     *  field.setText( promise );
     */
    setText: function(text){
	    if (!!text){
            o2.promiseAll(text).then(function(v){
                this.node.set("text", v || "");
            }.bind(this), function(){});
        }else{
            this.node.set("text", v || "");
        }
        //this.node.set("text", text);
    }
});
