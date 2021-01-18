MWF.xDesktop.requireApp("cms.Xform", "Org", null, false);
/** @class Reader 读者组件。
 * @example
 * //可以在脚本中获取该组件
 * //方法1：
 * var field = this.form.get("fieldId"); //获取组件对象
 * //方法2
 * var field = this.target; //在组件本身的脚本中获取，比如事件脚本、默认值脚本、校验脚本等等
 *
 * var data = field.getData(); //获取值
 * field.setData("字符串值"); //设置值
 * field.hide(); //隐藏字段
 * var id = field.json.id; //获取字段标识
 * var flag = field.isEmpty(); //字段是否为空
 * field.resetData();  //重置字段的值为默认值或置空
 * @extends MWF.xApplication.process.Xform.Org
 * @o2category FormComponents
 * @o2range {CMS}
 * @hideconstructor
 */
MWF.xApplication.cms.Xform.Reader = MWF.CMSReader =  new Class({
	Extends: MWF.CMSOrg,
	iconStyle: "readerIcon"
});