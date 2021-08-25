o2.xDesktop.requireApp("process.Xform", "$Elinput", null, false);
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
MWF.xApplication.process.Xform.Elinput = MWF.APPElinput =  new Class(
    /** @lends o2.xApplication.process.Xform.Elinput# */
    {
    Implements: [Events],
    Extends: MWF.APP$Elinput,
    options: {
        "moduleEvents": ["load", "queryLoad", "postLoad"],
        "elEvents": ["focus", "blur", "change", "input", "clear"]
    },
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
        this.form.Macro.environment.data.check(this.json.id);
        //if (!this.json[this.json.id]){
            this.json[this.json.id] = this._getBusinessData();
        //}

        if (!this.json.maxlength) this.json.maxlength = "";
        if (!this.json.minlength) this.json.minlength = "";
        if (!this.json.showWordLimit) this.json.showWordLimit = false;
        if (!this.json.showPassword) this.json.showPassword = false;
        if (!this.json.disabled) this.json.disabled = false;
        if (!this.json.clearable) this.json.clearable = false;
        if (!this.json.size) this.json.size = "";
        if (!this.json.prefixIcon) this.json.prefixIcon = "";
        if (!this.json.suffixIcon) this.json.suffixIcon = "";
        if (!this.json.rows) this.json.rows = "2";
        if (!this.json.autosize) this.json.autosize = false;
        if (!this.json.readonly) this.json.readonly = false;
        if (!this.json.resize) this.json.resize = "none";
        if (!this.json.description) this.json.description = "";
    },
    appendVueExtend: function(app){
        if (!app.methods) app.methods = {};
        app.methods.$loadElEvent = function(ev){
            this.validationMode();
            if (ev==="change") this._setBusinessData(this.getInputData());
            if (this.json.events[ev] && this.json.events[ev].code){
                this.form.Macro.fire(this.json.events[ev].code, this, event);
            }
        }.bind(this);
    },
    _createElementHtml: function(){
        var html = "<el-input";
        html += " v-model=\""+this.json.id+"\"";
        html += " :maxlength=\"maxlength\"";
        html += " :minlength=\"minlength\"";
        html += " :show-word-limit=\"showWordLimit\"";
        html += " :show-password=\"showPassword\"";
        html += " :disabled=\"disabled\"";
        html += " :size=\"size\"";
        html += " :prefix-icon=\"prefixIcon\"";
        html += " :suffix-icon=\"suffixIcon\"";
        html += " :rows=\"rows\"";
        html += " :autosize=\"autosize\"";
        html += " :readonly=\"readonly\"";
        html += " :resize=\"resize\"";
        html += " :clearable=\"clearable\"";
        html += " :type=\"inputType\"";
        html += " :placeholder=\"description\"";

        this.options.elEvents.forEach(function(k){
            html += " @"+k+"=\"$loadElEvent('"+k+"')\"";
        });

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

        html += ">";

        if (this.json.vueSlot) html += this.json.vueSlot;

        html += "</el-input>";
        return html;
    }
}); 
