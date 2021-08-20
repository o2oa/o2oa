o2.xDesktop.requireApp("process.Xform", "$Module", null, false);
/** @class Elinput 基于Element UI的输入框组件。
 * @example
 * //可以在脚本中获取该组件
 * //方法1：
 * var input = this.form.get("name"); //获取组件
 * //方法2
 * var input = this.target; //在组件事件脚本中获取
 * @extends MWF.xApplication.process.Xform.$Module
 * @o2category FormComponents
 * @o2range {Process|CMS|Portal}
 * @hideconstructor
 */
o2.xApplication.process.Xform.Elinput = MWF.APPElinput =  new Class(
    /** @lends o2.xApplication.process.Xform.Elinput# */
    {
    Implements: [Events],
    Extends: MWF.APP$ElModule,
    /**
     * @summary 组件的配置信息，同时也是Vue组件的data。
     * @member MWF.xApplication.process.Xform.Elinput#json {JsonObject}
     * @example
     *  //可以在脚本中获取此对象，下面两行代码是等价的，它们获取的是同一个对象
     * var json = this.form.get("elinput").json;       //获取组件的json对象
     * var json = this.form.get("elinput").vm.$data;   //获取Vue组件的data数据，
     *
     * //通过json对象操作Element组件
     * json.size = "mini";      //改变输入框大小
     * json.disabled = true;     //设置输入框为禁用
     */
    _appendVueData: function(){
        if (!this.json.size) this.json.size = "";
        if (!this.json.bttype) this.json.bttype = "";
        if (!this.json.plain) this.json.plain = false;
        if (!this.json.round) this.json.round = false;
        if (!this.json.circle) this.json.circle = false;
        if (!this.json.disabled) this.json.disabled = false;
        if (!this.json.loading) this.json.loading = false;
    },
    _createElementHtml: function(){
        debugger;
        var html = "<el-button";
        html += " :size=\"size\"";
        html += " :type=\"bttype\"";
        html += " :plain=\"plain\"";
        html += " :round=\"round\"";
        html += " :circle=\"circle\"";
        html += " :disabled=\"disabled\"";
        html += " :loading=\"loading\"";

        if (this.json.autofocus==="yes") html += " autofocus";

        if (this.json.elProperties){
            Object.keys(this.json.elProperties).forEach(function(k){
                if (this.json.elProperties[k]) html += " "+k+"=\""+this.json.elProperties[k]+"\"";
            }, this);
        }

        if (this.json.elStyles){
            var style = "";
            Object.keys(this.json.elStyles).forEach(function(k){
                if (this.json.elStyles[k]) style += k+":"+this.json.elStyles[k]+";";
            }, this);
            html += " style=\""+style+"\"";
        }

        html += ">"+((this.json.circle!=="yes" && this.json.isText!=="no") ? (this.json.name || this.json.id) : "")+"</el-button>";
        return html;
    }
}); 
