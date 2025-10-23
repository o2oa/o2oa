o2.xDesktop.requireApp("process.Xform", "$Elinput", null, false);
/** @class Eldate 基于Element UI的日期选择组件。
 * @o2cn 日期选择
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
 * @see {@link https://element.eleme.cn/#/zh-CN/component/date-picker|Element UI DatePicker 日期选择器}
 */
MWF.xApplication.process.Xform.Eldate = MWF.APPEldate =  new Class(
    /** @lends MWF.xApplication.process.Xform.Eldate# */
    {
    Implements: [Events],
    Extends: MWF.APP$Elinput,
    options: {
        "moduleEvents": ["load", "queryLoad", "postLoad"],
        /**
         * 当 input 失去焦点时触发。this.event[0]指向组件实例
         * @event MWF.xApplication.process.Xform.Eldate#blur
         * @see {@link https://element.eleme.cn/#/zh-CN/component/date-picker|日期选择组件的Events章节}
         */
        /**
         * 当 input 获得焦点时触发。this.event[0]指向组件实例
         * @event MWF.xApplication.process.Xform.Eldate#focus
         * @see {@link https://element.eleme.cn/#/zh-CN/component/date-picker|日期选择组件的Events章节}
         */
        /**
         * 用户确认选定的值时触发。this.event[0]为组件绑定值；格式与绑定值一致，可受 value-format 控制
         * @event MWF.xApplication.process.Xform.Eldate#change
         * @see {@link https://element.eleme.cn/#/zh-CN/component/date-picker|日期选择组件的Events章节}
         */
        "elEvents": ["focus", "blur", "change"]
    },
    _loadMergeReadContentNode: function( contentNode, data ){
        var d = o2.typeOf( data.data ) === "array" ? data.data : [data.data];
        contentNode.set("text", d.join( this.json.rangeSeparator ? " "+this.json.rangeSeparator+" " : " 至 " ) );
    },
    _queryLoaded: function(){
        this._loadReadEditAbeld();
        var data = this._getBusinessData();
        if (data){
            if( ["monthrange","daterange"].contains(this.json.selectType) ) {
                if (typeOf(data) === "string") this._setBusinessData([data, ""]);
            }else if( ["dates"].contains(this.json.selectType) ){
                if (typeOf(data) === "string") this._setBusinessData([data]);
            }else{
                if( typeOf(data) === "array" )this._setBusinessData(data[0] || "");
            }
        }
    },
    __setReadonly: function(data){
        if (this.isReadonly()){
            var format = this.json.format || this.json.valueFormat;
            if( o2.typeOf(data) === "array" ){
                if( ["monthrange","daterange"].contains(this.json.selectType) ) {
                    var ds = data.map(function (d){
                        return this.isValidDate(d) ? this.formatDate(new Date(d), format) : d;
                    }.bind(this));
                    this.node.set("text", this.json.rangeSeparator ? ds.join(this.json.rangeSeparator) : ds);
                }else{
                    this.node.set("text", this.isValidDate(data) ? this.formatDate(new Date(data), format) : data );
                }
            }else{
                this.node.set("text", this.isValidDate(data) ? this.formatDate(new Date(data), format) : data );
            }
            if( this.json.elProperties ){
                this.node.set(this.json.elProperties );
            }
            if (this.json.elStyles){
                this.node.setStyles( this._parseStyles(this.json.elStyles) );
            }

            if( !this.eventLoaded ){
                this._loadDomEvents();
                this.eventLoaded = true;
            }

            this.fireEvent("postLoad");
            this.fireEvent("load");
            this.isLoaded = true;
        }
    },
    _appendVueData: function(){
        if (!this.json.isReadonly && !this.form.json.isReadonly) this.json.isReadonly = false;
        if (!this.json.disabled) this.json.disabled = false;
        if (!this.json.clearable) this.json.clearable = false;
        if (!this.json.disabled) this.json.disabled = false;
        if (!this.json.editable) this.json.editable = false;
        if (!this.json.size) this.json.size = "";
        if (!this.json.valueFormat) this.json.valueFormat = this.json.format || "";
        if (!this.json.prefixIcon) this.json.prefixIcon = "";
        if (!this.json.description) this.json.description = "";
        if (!this.json.popperClass) this.json.popperClass = "";
        this.json.pickerOptions = {
            firstDayOfWeek: this.json.firstDayOfWeek.toInt()
        }
        if (this.json.disabledDate && this.json.disabledDate.code){
            this.json.pickerOptions.disabledDate = function(date){
                return this.form.Macro.fire(this.json.disabledDate.code, this, date);
            }.bind(this)
        }

        this._setPopperClass();
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
        html += " :value-format=\"valueFormat\"";
        html += " :format=\"format\"";
        html += " :picker-options=\"pickerOptions\"";
        html += " :popper-class=\"popperClass\"";
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
    },
    getInputData: function(){
        var data = this.json[this.json.$id];
        if( data === null ){
            if( ["monthrange","daterange"].contains(this.json.selectType) ) {
                return [];
            }else if( ["dates"].contains(this.json.selectType) ){
                return [];
            }else{
                return "";
            }
        }
        return this.json[this.json.$id];
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
        },
        isValidDate: function(dateString) {
            if( !dateString ){
                return false;
            }
            var date = new Date(dateString);
            return !isNaN(date.getTime());
        },
        formatDate: function (date, format) {
            var o = {
                'M+': date.getMonth() + 1, // 月份
                'd+': date.getDate(), // 日
                'h+': date.getHours() % 12 === 0 ? 12 : date.getHours() % 12, // 小时
                'H+': date.getHours(), // 小时
                'm+': date.getMinutes(), // 分
                's+': date.getSeconds(), // 秒
                'q+': Math.floor((date.getMonth() + 3) / 3), // 季度
                d: date.getDay(),
                S: date.getMilliseconds(), // 毫秒
                a: date.getHours() < 12 ? 'am' : 'pm', // 上午/下午
                A: date.getHours() < 12 ? 'AM' : 'PM' // AM/PM
            };
            if (/(y+)/.test(format)) {
                format = format.replace(RegExp.$1, (date.getFullYear() + '').substr(4 - RegExp.$1.length));
            }
            if (/(W+)/.test(format)) {
                o['W+'] = this.weekNumberOfDate(date);
            }
            for (let k in o) {
                if (new RegExp('(' + k + ')').test(format)) {
                    format = format.replace(
                        RegExp.$1,
                        RegExp.$1.length === 1 ? o[k] : ('00' + o[k]).substr(('' + o[k]).length)
                    );
                }
            }
            return format;
        },
        weekNumberOfDate: function ( date ){
            var target  = new Date(date.getTime());
            var dayNumber   =  ( 7 + date.getDay() - parseInt( (this.json.firstDayOfWeek || 0) ) ) % 7;
            target.setDate(target.getDate() - dayNumber + 3);
            var firstThursday = target.valueOf();
            target.setMonth(0, 1);
            if (target.getDay() !== 4) {
                target.setMonth(0, 1 + ((4 - target.getDay()) + 7) % 7);
            }
            return 1 + Math.ceil((firstThursday - target) / 608400000); // 604800000 = 7 * 24 * 3600 * 1000
        }
});
