MWF.xDesktop.requireApp("process.Xform", "$Module", null, false);
/** @class Div 容器组件。
 * @example
 * //可以在脚本中获取该组件
 * //方法1：
 * var div = this.form.get("name"); //获取组件
 * //方法2
 * var div = this.target; //在组件事件脚本中获取
 * @extends MWF.xApplication.process.Xform.$Module
 * @o2category FormComponents
 * @o2range {Process|CMS|Portal}
 * @hideconstructor
 */
MWF.xApplication.process.Xform.Div = MWF.APPDiv =  new Class({
    Extends: MWF.APP$Module
});