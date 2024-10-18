o2.xDesktop.requireApp("process.Xform", "$Elinput", null, false);
/** @class Elinput 基于Element UI的输入框组件。
 * @o2cn 输入框
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
 * @see {@link https://element.eleme.cn/#/zh-CN/component/input|Element UI Input 输入框}
 */
MWF.xApplication.process.Xform.Elinput = MWF.APPElinput =  new Class(
    /** @lends o2.xApplication.process.Xform.Elinput# */
    {
    Implements: [Events],
    Extends: MWF.APP$Elinput,
    options: {
        "moduleEvents": ["load", "queryLoad", "postLoad"],
        /**
         * 当 input 获得焦点时触发。this.event[0]指向Event
         * @event MWF.xApplication.process.Xform.Elinput#focus
         * @see {@link https://element.eleme.cn/#/zh-CN/component/input|输入组件的Input Events章节}
         */
        /**
         * 当 input 失去焦点时触发。this.event[0]指向Event
         * @event MWF.xApplication.process.Xform.Elinput#blur
         * @see {@link https://element.eleme.cn/#/zh-CN/component/input|输入组件的Input Events章节}
         */
        /**
         * 仅在输入框失去焦点或用户按下回车时触发。this.event[0]为组件的value
         * @event MWF.xApplication.process.Xform.Elinput#change
         * @see {@link https://element.eleme.cn/#/zh-CN/component/input|输入组件的Input Events章节}
         */
        /**
         * 在 Input 值改变时触发。this.event[0]为组件的value
         * @event MWF.xApplication.process.Xform.Elinput#input
         * @see {@link https://element.eleme.cn/#/zh-CN/component/input|输入组件的Input Events章节}
         */
        /**
         * 在点击由 clearable 属性生成的清空按钮时触发
         * @event MWF.xApplication.process.Xform.Elinput#clear
         * @see {@link https://element.eleme.cn/#/zh-CN/component/input|输入组件的Input Events章节}
         */
        "elEvents": ["focus", "blur", "change", "input", "clear"]
    },
    _appendVueData: function(){
        if (!this.json.maxlength) this.json.maxlength = "";
        if (!this.json.minlength) this.json.minlength = "";
        if (!this.json.max) this.json.max = "";
        if (!this.json.min) this.json.min = "";
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
        if (this.json.minRows && this.json.maxRows){
            this.json.autosize = {minRows: this.json.minRows.toInt(), maxRows: this.json.maxRows.toInt()}
        }
    },
    // appendVueExtend: function(app){
    //     if (!app.methods) app.methods = {};
    //     app.methods.$loadElEvent = function(ev){
    //         this.validationMode();
    //         if (ev==="change") this._setBusinessData(this.getInputData());
    //         if (this.json.events && this.json.events[ev] && this.json.events[ev].code){
    //             this.form.Macro.fire(this.json.events[ev].code, this, event);
    //         }
    //     }.bind(this);
    // },
    _createElementHtml: function(){
        //var numberStr = (this.json.inputType === "number" && this.json.resultType === "number" ) ? ".number" : "";
        var html = "<el-input";
        html += " v-model"+"=\""+this.json.$id+"\"";
        if( this.json.inputType === 'number' ){
            html += " :max=\"max\"";
            html += " :min=\"min\"";
        }else{
            html += " :maxlength=\"maxlength\"";
            html += " :minlength=\"minlength\"";
        }
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


        // this.options.elEvents.forEach(function(k){
        //     html += " @"+k+"=\"$loadElEvent('"+k+"')\"";
        // });
        this.options.elEvents.forEach(function(k){
            html += " @"+k+"=\"$loadElEvent_"+k.camelCase()+"\"";
        });

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

        html += ">";

        if (this.json.vueSlot) html += this.json.vueSlot;

        html += "</el-input>";
        return html;
    },
        _createEventFunction: function(methods, k){
            methods["$loadElEvent_"+k.camelCase()] = function(){
                var flag = true;
                if (k==="change"){
                    if(this.json.inputType === "number"  ){
                        if( this.json.resultType === "number" ){
                            if( parseFloat(arguments[0]).toString() !== "NaN" ){
                                this.json[this.json.$id] = parseFloat(arguments[0]);
                            }
                        }
                        var value = this.getMin( this.getMax( this.json[this.json.$id] ) );
                        if( value !== this.json[this.json.$id] ){
                            this.json[this.json.$id] = value;
                        }
                    }
                    this.validationMode();
                    this._setBusinessData(this.getInputData());
                    if( !this.validation() )flag = false;
                }
                if (this.json.events && this.json.events[k] && this.json.events[k].code){
                    this.form.Macro.fire(this.json.events[k].code, this, arguments);
                }
                if( flag )this.fireEvent(k, arguments);
            }.bind(this);
        },
        getMax: function( value ){
            if( isNaN(value) )return value;
            if( typeOf( value ) === "string" )value = parseFloat(value);
            if( typeOf(this.json.max)!=='null' && !isNaN( this.json.max )){
                var max = this.json.max;
                if( typeOf( max ) === "string" )max = parseFloat(max);
                return isNaN(max) ? value : Math.min( max, value );
            }else{
                return value;
            }
        },
        getMin: function( value ){
            if( isNaN(value) )return value;
            if( typeOf( value ) === "string" )value = parseFloat(value);
            if( typeOf(this.json.min)!=='null' && !isNaN( this.json.min )){
                var min = this.json.min;
                if( typeOf( min ) === "string" )min = parseFloat(min);
                return isNaN(min) ? value : Math.max( min, value );
            }else{
                return value;
            }
        },
    getValue: function(){
        if (this.moduleValueAG) return this.moduleValueAG;
        var value = this._getBusinessData();
        if( this.json.inputType === "number" ){
            if (value || value===0 ){
                return this.json.resultType === "string" ? value.toString() : value;
            }else{
                value = this._computeValue();
                value = (o2.typeOf(value)!=="null") ? value : "";
                return this.json.resultType === "string" ? value.toString() : value;
            }
        }else{
            if (value || value===0 || value === false){
                return value;
            }else{
                value = this._computeValue();
                return (o2.typeOf(value)!=="null") ? value : "";
            }
        }
    }
}); 
