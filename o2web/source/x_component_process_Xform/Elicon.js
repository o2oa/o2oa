MWF.xDesktop.requireApp("process.Xform", "$ElModule", null, false);
/** @class Elicon 基于Element UI的图标组件。
 * @o2cn 图标组件
 * @example
 * //可以在脚本中获取该组件
 * //方法1：
 * var icon = this.form.get("name"); //获取组件
 * //方法2
 * var icon = this.target; //在组件事件脚本中获取
 * @extends MWF.xApplication.process.Xform.$Module
 * @o2category FormComponents
 * @o2range {Process|CMS|Portal}
 * @hideconstructor
 * @see {@link https://element.eleme.cn/#/zh-CN/component/icon|Element UI Icon 图标}
 */
MWF.xApplication.process.Xform.Elicon = MWF.APPElicon =  new Class(
    /** @lends MWF.xApplication.process.Xform.Elicon# */
    {
    Implements: [Events],
    Extends: MWF.APP$ElModule,
    _appendVueData: function(){
        if (!this.json.icon) this.json.icon = "el-icon-platform-eleme";
        if (!this.json.iconSize) this.json.iconSize = "16";
        if (!this.json.iconColor) this.json.iconColor = "";
        if (!this.json.icon) this.json.icon = "";
        if (!this.json.elStyles) this.json.elStyles = {};
    },
    _createElementHtml: function(){
        if (!this.isReadable){
            this.node?.addClass('hide');
            return '';
        } 

        var html = "<i";
        html += " :class=\"icon\"";

        if (this.json.elProperties){
            Object.keys(this.json.elProperties).forEach(function(k){
                if (this.json.elProperties[k]) html += " "+k+"=\""+this.json.elProperties[k]+"\"";
            }, this);
        }

        html += " :style=\"[elStyles, {fontSize: iconSize+'px', color: iconColor}]\"";


        html += "></i>";
        return html;
    }

}); 
