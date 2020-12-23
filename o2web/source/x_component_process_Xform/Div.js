MWF.xDesktop.requireApp("process.Xform", "$Module", null, false);
/** @class Div 容器组件。
 * @example
 * //可以在脚本中获取该组件
 * //方法1：
 * var attachment = this.form.get("name"); //获取组件
 * //方法2
 * var attachment = this.target; //在组件事件脚本中获取
 * @extends MWF.xApplication.process.Xform.$Module
 * @category FormComponents
 * @hideconstructor
 */
MWF.xApplication.process.Xform.Div = MWF.APPDiv =  new Class({
    Extends: MWF.APP$Module
});