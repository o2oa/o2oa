o2.xDesktop.requireApp("process.Xform", "$Elinput", null, false);
/** @class Eltime 基于Element UI的输入框组件。
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
MWF.xApplication.process.Xform.Eltime = MWF.APPEltime =  new Class(
    /** @lends o2.xApplication.process.Xform.Eltime# */
    {
    Implements: [Events],
    Extends: MWF.APP$Eltime,
    options: {
        "moduleEvents": ["load", "queryLoad", "postLoad"],
        "elEvents": ["focus", "blur", "change", "input", "clear"]
    },
    _appendVueData: function(){
        if (!this.json.maxlength) this.json.maxlength = "";
        if (!this.json.minlength) this.json.minlength = "";
        if (!this.json.showWordLimit) this.json.showWordLimit = false;
        if (!this.json.showPassword) this.json.showPassword = false;
        if (!this.json.disabled) this.json.disabled = false;
        if (!this.json.clearable) this.json.clearable = false;
        if (!this.json.size) this.json.size = "";
        if (!this.json.prefixIcon) this.json.prefixIcon = "";
        if (!this.json.suffixIcon) this.json.suffixIcon = "";
        if (!this.json.rows) this.json.rows = 2;
        if (!this.json.autosize) this.json.autosize = false;
        if (!this.json.readonly) this.json.readonly = false;
        if (!this.json.resize) this.json.resize = "none";
        if (!this.json.description) this.json.description = "";
    },
    _createElementHtml: function() {
        debugger;
        if (this.json.timeSelectType === "select"){
            if (this.json.isRange) {
                return this.createSelectRangeElementHtml();
            } else {
                return this.createSelectElementHtml();
            }
        }else{
            if (this.json.isRange) {
                return this.createPickerRangeElementHtml();
            } else {
                return this.createPickerElementHtml();
            }
        }
    },
    getCommonHtml: function(){
        var html = "";
        html += " v-model=\""+this.json.$id+"\"";
        html += " :disabled=\"disabled\"";
        html += " :size=\"size\"";
        html += " :prefix-icon=\"prefixIcon\"";
        html += " :suffix-icon=\"suffixIcon\"";
        html += " :readonly=\"isReadonly\"";
        html += " :clearable=\"clearable\"";
        html += " :placeholder=\"description\"";

        this.options.elEvents.forEach(function(k){
            html += " @"+k+"=\"$loadElEvent_"+k.camelCase()+"\"";
        });

        if (this.json.elProperties){
            Object.keys(this.json.elProperties).forEach(function(k){
                if (this.json.elProperties[k]) html += " "+k+"=\""+this.json.elProperties[k]+"\"";
            }, this);
        }

        if (this.json.elStyles) html += " :style=\"elStyles\"";

        html += ">";

        if (this.json.vueSlot) html += this.json.vueSlot;

        return html;
    },
    createSelectElementHtml: function(){
        var html = "<el-time-select";
        html += " :placeholder=\"description\"";
        html += this.getCommonHtml();
        html += "</el-time-select>";
        return html;
    },
    createSelectRangeElementHtml: function(){
        var html = "<el-time-select";
        html += " :placeholder=\"startPlaceholder\"";
        html += this.getCommonHtml();
        html += "</el-time-select>";

        html += "<span style='padding: 0px 5px;'>"+this.json.rangeSeparator+"</span>";

        html += "<el-time-select";
        html += " :placeholder=\"endPlaceholder\"";
        html += this.getCommonHtml();
        html += "</el-time-select>";
        return html;
    },
    createPickerElementHtml: function(){
        var html = "<el-time-picker";
        html += " placeholder="+this.json.id;
        html += this.getCommonHtml();
        html += "</el-time-picker>";
        return html;
    },
    createPickerRangeElementHtml: function(){
        var html = "<el-time-picker";
        html += " is-range";
        html += " :range-separator=\"rangeSeparator\"";
        html += " :start-placeholder=\"startPlaceholder\"";
        html += " :end-placeholder=\"endPlaceholder\"";
        html += this.getCommonHtml();
        html += "</el-time-picker>";
        return html;
    },
}); 
