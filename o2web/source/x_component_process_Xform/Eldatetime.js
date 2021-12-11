o2.xDesktop.requireApp("process.Xform", "$Elinput", null, false);
/** @class Eldatetime 基于Element UI的输入框组件。
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
MWF.xApplication.process.Xform.Eldatetime = MWF.APPEldatetime =  new Class(
    /** @lends o2.xApplication.process.Xform.Eldatetime# */
    {
    Implements: [Events],
    Extends: MWF.APP$Elinput,
    options: {
        "moduleEvents": ["load", "queryLoad", "postLoad"],
        "elEvents": ["focus", "blur", "change", "input", "clear"]
    },
    _queryLoaded: function(){
        debugger;
        var data = this._getBusinessData();
        if( ["datetimerange"].contains(this.json.selectType) ) {
            if (typeOf(data) === "string") this._setBusinessData([data, ""]);
        }else{
            if( typeOf(data) === "array" )this._setBusinessData(data[0] || "");
        }
    },
    _appendVueData: function(){
        if (!this.json.isReadonly) this.json.isReadonly = false;
        if (!this.json.disabled) this.json.disabled = false;
        if (!this.json.clearable) this.json.clearable = false;
        if (!this.json.disabled) this.json.disabled = false;
        if (!this.json.editable) this.json.editable = false;
        if (!this.json.size) this.json.size = "";
        if (!this.json.prefixIcon) this.json.prefixIcon = "";
        if (!this.json.description) this.json.description = "";
        if (!this.json.arrowControl) this.json.arrowControl = false;
        this.json.pickerOptions = {
            firstDayOfWeek: this.json.firstDayOfWeek.toInt()
        }
        if (this.json.disabledDate && this.json.disabledDate.code){
            this.json.pickerOptions.disabledDate = function(date){
                return this.form.Macro.fire(this.json.disabledDate.code, this, date);
            }.bind(this)
        }
        // if(this.json.selectableRange && this.json.selectableRange.code){
        //     this.json.pickerOptions.selectableRange = this.form.Macro.fire(this.json.selectableRange.code, this);
        // }
    },
    _createElementHtml: function() {
        var html = "<el-date-picker";
        html += " v-model=\""+this.json.$id+"\"";
        html += " :type=\"selectType\"";
        html += " :readonly=\"isReadonly\"";
        html += " :disabled=\"disabled\"";
        html += " :editable=\"editable\"";
        html += " :clearable=\"clearable\"";
        html += " :size=\"size\"";
        html += " :prefix-icon=\"prefixIcon\"";
        html += " :range-separator=\"rangeSeparator\"";
        html += " :start-placeholder=\"startPlaceholder\"";
        html += " :end-placeholder=\"endPlaceholder\"";
        html += " :value-format=\"format\"";
        html += " :format=\"format\"";
        html += " :picker-options=\"pickerOptions\"";
        html += " :arrow-control=\"arrowControl\"";
        // html += " :picker-options=\"{" +
            // ":firstDayOfWeek=firstDayOfWeek," +
            // ":disabledDate=\"disabledDateFun\""+
            // "}\"";

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

        html += "</el-date-picker>";
        return html;
    }
}); 
