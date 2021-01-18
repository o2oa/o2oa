MWF.xDesktop.requireApp("process.Xform", "$Module", null, false);
/** @class SourceText 数据文本组件。
 * @example
 * //可以在脚本中获取该组件
 * //方法1：
 * var sourceText = this.form.get("fieldId"); //获取组件
 * //方法2
 * var sourceText = this.target; //在组件本身的脚本中获取
 * @extends MWF.xApplication.process.Xform.$Module
 * @o2category FormComponents
 * @o2range {Portal}
 * @hideconstructor
 */
MWF.xApplication.process.Xform.SourceText = MWF.APPSourceText =  new Class({
    Extends: MWF.APP$Module,

	_loadUserInterface: function(){
        this._loadJsonData();
	},
    _getSource: function(){
        var parent = this.node.getParent();
        while(parent && (parent.get("MWFtype")!="source" && parent.get("MWFtype")!="subSource" && parent.get("MWFtype")!="subSourceItem")) parent = parent.getParent();
        return (parent) ? parent.retrieve("module") : null;
    },
    _loadJsonData: function(){
        this.node.set("text", "");
        this.source = this._getSource();
        if (this.source){
            if (this.source.data){
                COMMON.AjaxModule.load("JSONTemplate", function(){

                    this.template = new Template();
                    this.text = this.template.substitute("{"+this.json.jsonPath+"}", this.source.data);

                    if (this.json.jsonText){
                        if (this.json.jsonText.code){
                            this.text = this.form.Macro.exec(this.json.jsonText.code, this);
                            if( typeOf(this.text) === "string" )this.node.set("text", this.text);
                        }else{
                            this.node.set("text", this.text);
                        }
                    }else{
                        this.node.set("text", this.text);
                    }

                }.bind(this));
            }
        }
    }
}); 