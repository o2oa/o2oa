o2.xDesktop.requireApp("process.Xform", "$Elinput", null, false);
/** @class Eltime 基于Element UI的时间选择器。
 * @o2cn 时间选择
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
 * @see {@link https://element.eleme.cn/#/zh-CN/component/time-picker|Element UI TimePicker 时间选择器}
 */
MWF.xApplication.process.Xform.Eltime = MWF.APPEltime =  new Class(
    /** @lends MWF.xApplication.process.Xform.Eltime# */
    {
    Implements: [Events],
    Extends: MWF.APP$Elinput,
    options: {
        "moduleEvents": ["load", "queryLoad", "postLoad"],
        /**
         * 当 input 失去焦点时触发。this.event[0]指向组件实例
         * @event MWF.xApplication.process.Xform.Eltime#blur
         * @see {@link https://element.eleme.cn/#/zh-CN/component/time-picker|时间选择组件的Events章节}
         */
        /**
         * 当 input 获得焦点时触发。this.event[0]指向组件实例
         * @event MWF.xApplication.process.Xform.Eltime#focus
         * @see {@link https://element.eleme.cn/#/zh-CN/component/time-picker|时间选择组件的Events章节}
         */
        /**
         * 用户确认选定的值时触发。this.event[0]为组件绑定值
         * @event MWF.xApplication.process.Xform.Eltime#change
         * @see {@link https://element.eleme.cn/#/zh-CN/component/time-picker|时间选择组件的Events章节}
         */
        "elEvents": ["focus", "blur", "change"]
    },
    _queryLoaded: function(){
        this._loadReadEditAbeld();
        var data = this._getBusinessData();
        if( ["picker"].contains(this.json.timeSelectType) && this.json.isRange) {
            if (typeOf(data) === "string") this._setBusinessData([data, ""]);
        }else{
            if( typeOf(data) === "array" )this._setBusinessData(data[0] || "");
        }
    },
    _appendVueData: function(){
        if (!this.json.readonly) this.json.readonly = false;
        if (!this.json.disabled) this.json.disabled = false;
        if (!this.json.clearable) this.json.clearable = false;
        if (!this.json.disabled) this.json.disabled = false;
        if (!this.json.editable) this.json.editable = false;
        if (!this.json.size) this.json.size = "";
        if (!this.json.prefixIcon) this.json.prefixIcon = "";
        if (!this.json.suffixIcon) this.json.suffixIcon = "";
        if (!this.json.description) this.json.description = "";
        if (!this.json.arrowControl) this.json.arrowControl = false;
        if (this.json.timeSelectType === "select"){
            this.json.pickerOptions = {
                "start": this.json.start,
                "step": this.json.step,
                "end": this.json.end
            };
        }else{
            this.json.pickerOptions = {
                "format": this.json.format
            };
            if (!this.json.isRange && this.json.selectableRange && this.json.selectableRange.code){
                this.json.pickerOptions.selectableRange = this.form.Macro.fire(this.json.selectableRange.code, this);
            }
        }
    },
    _createElementHtml: function() {
        if (this.json.timeSelectType === "select"){
            return this.createSelectElementHtml();
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
        html += " :readonly=\"isReadonly\"";
        html += " :disabled=\"disabled\"";
		html += " :editable=\"editable\"";
		html += " :clearable=\"clearable\"";
        html += " :size=\"size\"";
        html += " :prefix-icon=\"prefixIcon\"";

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
        html += " :picker-options=\"pickerOptions\"";
        html += this.getCommonHtml();
        html += "</el-time-select>";
        return html;
    },
    // createSelectRangeElementHtml: function(){
    //     var html = "<el-time-select";
    //     html += " :placeholder=\"startPlaceholder\"";
    //     html += this.getSelectOpt();
    //     html += this.getCommonHtml();
    //     html += "</el-time-select>";
    //
    //     html += "<span>"+this.json.rangeSeparator+"</span>";
    //
    //     html += "<el-time-select";
    //     html += " :placeholder=\"endPlaceholder\"";
    //     html += this.getSelectOpt();
    //     html += this.getCommonHtml();
    //     html += "</el-time-select>";
    //     return html;
    // },

    createPickerElementHtml: function(){
        var html = "<el-time-picker";
        html += " :placeholder=\"description\"";
        html += " :arrow-control=\"arrowControl\"";
        html += " :value-format=\"format\"";
        html += " :picker-options=\"pickerOptions\"";
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
        html += " :arrow-control=\"arrowControl\"";
        html += " :value-format=\"format\"";
        html += " :picker-options=\"pickerOptions\"";
        html += this.getCommonHtml();
        html += "</el-time-picker>";
        return html;
    },

        getExcelData: function(){
            var value = this.getData();
            return o2.typeOf(value) === "array" ? value.join(", ") : value;
        },
        setExcelData: function(data){
            var arr = this.stringToArray(data);
            this.excelData = arr;
            var value = arr.length === 0  ? arr[0] : arr;
            this.setData(value, true);
        }
});
