MWF.xDesktop.requireApp("process.Xform", "$Module", null, false);
MWF.xDesktop.requireApp("cms.Xform", "widget.Log", null, false);
/** @class CMSLog 文档查看日志组件。
 * @alias CMSLog
 * @example
 * //可以在脚本中获取该组件
 * //方法1：
 * var log = this.form.get("name"); //获取组件
 * //方法2
 * var log = this.target; //在组件事件脚本中获取
 * @extends MWF.xApplication.process.Xform.$Module
 * @o2category FormComponents
 * @o2range {CMS}
 * @hideconstructor
 */
MWF.xApplication.cms.Xform.Log = MWF.CMSLog =  new Class(
    /** @lends CMSLog# */
{
	Extends: MWF.APP$Module,
	_loadUserInterface: function(){
		this.node.empty();
        this.node.setStyle("-webkit-user-select", "text");
        /**
         * @summary log组件使用this.log实现功能
         * @member {MWF.xApplication.cms.Xform.widget.Log}
         * @example
         *  //可以在脚本中获取该组件
         * var field = this.form.get("fieldId"); //获取组件对象
         * var items = field.log.items; //获取日志的行对象
         */
        this.log = new MWF.xApplication.cms.Xform.widget.Log( this.form.app, this.node, {
            "documentId" : this.form.businessData.document.id,
            "mode" : this.json.mode,
            "textStyle" : this.json.textStyle
        });
        this.log.load();
	}
}); 