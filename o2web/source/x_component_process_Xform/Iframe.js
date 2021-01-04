MWF.xDesktop.requireApp("process.Xform", "$Module", null, false);
/** @class Iframe HTML iframe。
 * @example
 * //可以在脚本中获取该组件
 * //方法1：
 * var iframe = this.form.get("name"); //获取组件
 * //方法2
 * var iframe = this.target; //在组件事件脚本中获取
 * @extends MWF.xApplication.process.Xform.$Module
 * @o2category FormComponents
 * @o2range {Process|CMS|Portal}
 * @hideconstructor
 */
MWF.xApplication.process.Xform.Iframe = MWF.APPIframe =  new Class({
	Extends: MWF.APP$Module,

	_loadUserInterface: function(){
		this.node.empty();

        var src = this.json.src;
        if (this.json.valueType=="script"){
            src = this.form.Macro.exec(((this.json.script) ? this.json.script.code : ""), this);
        }

		this.iframe = new Element("iframe", {
			"src": src
		}).inject(this.node, "after");
		
		this.node.destroy();
		this.node = this.iframe.setStyles({
			"width": "100%",
			"border": "0"
		});
	}
}); 