MWF.xDesktop.requireApp("process.Xform", "$ElModule", null, false);
/** @class Elbutton 基于Element UI的按钮组件。
 * @o2cn 按钮组件
 * @example
 * //可以在脚本中获取该组件
 * //方法1：
 * var button = this.form.get("name"); //获取组件
 * //方法2
 * var button = this.target; //在组件事件脚本中获取
 * @extends MWF.xApplication.process.Xform.$Module
 * @o2category FormComponents
 * @o2range {Process|CMS|Portal}
 * @hideconstructor
 * @see {@link https://element.eleme.cn/#/zh-CN/component/button|Element UI Button 按钮}
 */
MWF.xApplication.process.Xform.Elbutton = MWF.APPElbutton =  new Class(
    /** @lends MWF.xApplication.process.Xform.Elbutton# */
    {
    Implements: [Events],
    Extends: MWF.APP$ElModule,
    /**
     * @summary 组件的配置信息，同时也是Vue组件的data。
     * @member MWF.xApplication.process.Xform.Elbutton#json {JsonObject}
     * @example
     *  //可以在脚本中获取此对象，下面两行代码是等价的，它们获取的是同一个对象
     * var json = this.form.get("elbutton").json;       //获取组件的json对象
     * var json = this.form.get("elbutton").vm.$data;   //获取Vue组件的data数据，
     *
     * //通过json对象操作Element组件
     * json.bttype = "success"; //将按钮样式改为success
     * json.loading = true;     //将按钮显示为加载中状态
     * json.disabled = true;    //将按钮设置为禁用
     */

    _appendVueData: function(){
        if (!this.json.size) this.json.size = "";
        if (!this.json.bttype) this.json.bttype = "";
        if (!this.json.plain) this.json.plain = false;
        if (!this.json.round) this.json.round = false;
        if (!this.json.circle) this.json.circle = false;
        if (!this.json.disabled) this.json.disabled = false;
        if (!this.json.loading) this.json.loading = false;
        if (!this.json.icon) this.json.icon = false;
    },
    _createElementHtml: function(){
        var html = "<el-button";
        html += " :size=\"size\"";
        html += " :type=\"bttype\"";
        html += " :plain=\"plain\"";
        html += " :round=\"round\"";
        html += " :circle=\"circle\"";
        html += " :disabled=\"disabled\"";
        html += " :loading=\"loading\"";
        if( this.json.iconPosition !== "right" )html += " :icon=\"icon\"";

        if (this.json.autofocus==="yes") html += " autofocus";

        if (this.json.elProperties){
            Object.keys(this.json.elProperties).forEach(function(k){
                if (this.json.elProperties[k]) html += " "+k+"=\""+this.json.elProperties[k]+"\"";
            }, this);
        }

        if (this.json.elStyles) html += " :style=\"elStyles\"";
        // if (this.json.elStyles){
        //     var style = "";
        //     Object.keys(this.json.elStyles).forEach(function(k){
        //         if (this.json.elStyles[k]) style += k+":"+this.json.elStyles[k]+";";
        //     }, this);
        //     html += " style=\""+style+"\"";
        // }

        html += ">"+((this.json.circle!=="yes" && (this.json.isText!=="no" && this.json.isText)) ? (this.json.name || this.json.id) : "");
        if( this.json.iconPosition === "right" )html += "<i class=\""+ this.json.icon +" el-icon--right\"></i>";
        html += "</el-button>";
        return html;
    }
}); 
