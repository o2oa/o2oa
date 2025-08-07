o2.xDesktop.requireApp("process.Xform", "$Elinput", null, false);
/** @class Elinput 基于Element UI的滑块组件。
 * @o2cn 滑块组件
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
 * @see {@link https://element.eleme.cn/#/zh-CN/component/slider|Element UI Slider 滑块}
 */
MWF.xApplication.process.Xform.Elslider = MWF.APPElslider =  new Class(
    /** @lends MWF.xApplication.process.Xform.Elslider# */
    {
    Implements: [Events],
    Extends: MWF.APP$Elinput,
    options: {
        "moduleEvents": ["load", "queryLoad", "postLoad"],
        /**
         * 值改变时触发（使用鼠标拖曳时，只在松开鼠标后触发）。this.event[0]为改变后的值
         * @event MWF.xApplication.process.Xform.Elslider#change
         * @see {@link https://element.eleme.cn/#/zh-CN/component/slider|Slider 滑块的 Events章节}
         */
        /**
         * 数据改变时触发（使用鼠标拖曳时，活动过程实时触发）。this.event[0]为改变后的值
         * @event MWF.xApplication.process.Xform.Elslider#input
         * @see {@link https://element.eleme.cn/#/zh-CN/component/slider|Slider 滑块的 Events章节}
         */
        "elEvents": ["change", "input"]
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
     * json.disabled = true;     //设置输入框为禁用
     */
    // _loadNode: function(){
    //     if (this.isReadonly()) this.json.disabled = true;
    //     this._loadNodeEdit();
    // },
    isReadonly : function(){
        return !!(this.readonly || this.json.isReadonly || this.form.json.isReadonly || this.json.showMode==="read" || this.isSectionMergeRead());
    },
    _loadNode: function(){
        if (!this.isReadable && !!this.isHideUnreadable){
            this.node.setStyle('display', 'none');
        }else{
             if (this.isReadonly()){
                this._loadNodeRead();
            }else{
                if (!this.isReadable) this.json.disabled = true;
                this._loadNodeEdit();
            }
        }
    },

    _loadMergeReadContentNode: function( contentNode, data ){
        contentNode.set("text", data.data);
    },
    _appendVueData: function(){
        this.form.Macro.environment.data.check(this.json.id);
        this.json[this.json.id] = this._getBusinessData();

        if (!this.json.max || !this.json.max.toFloat()) this.json.max = 100;
        if (!this.json.min || !this.json.min.toFloat()) this.json.min = 0;
        this.json.min = this.json.min.toFloat();
        this.json.max = this.json.max.toFloat();

        if (!this.json.step || !this.json.step.toFloat()) this.json.step = 1;
        this.json.step = this.json.step.toFloat();

        if (this.json.showTooltip!==false) this.json.showTooltip = true;

        if (this.json.vertical && !this.json.height) this.json.height = "100px";
        if (!this.json.height) this.json.height = "";
        if (!this.json.inputSize) this.json.inputSize = "";
        if (!this.json.tooltipClass) this.json.tooltipClass = "";

        if (!this.json.disabled) this.json.disabled = false;

        if (this.json.marksScript && this.json.marksScript.code){
            this.json.marks = this.form.Macro.exec(this.json.marksScript.code, this);
        }else{
            this.json.marks = {};
        }
    },
    appendVueExtend: function(app){
        if (!app.methods) app.methods = {};
        // app.methods.$loadElEvent = function(ev){
        //     this.validationMode();
        //     if (ev==="change") this._setBusinessData(this.getInputData());
        //     if (this.json.events && this.json.events[ev] && this.json.events[ev].code){
        //         this.form.Macro.fire(this.json.events[ev].code, this, event);
        //     }
        // }.bind(this);

        if (this.json.formatTooltip && this.json.formatTooltip.code){
            var fun = this.form.Macro.exec(this.json.formatTooltip.code, this);
            if (o2.typeOf(fun)==="function"){
                app.methods.$formatTooltip = function(){
                    fun.apply(this, arguments);
                }.bind(this);
            }else{
                app.methods.$formatTooltip = function(){};
            }
        }
    },
    _createElementHtml: function(){
        var html = "<el-slider";
        html += " v-model=\""+this.json.$id+"\"";
        html += " :max=\"max\"";
        html += " :min=\"min\"";
        html += " :step=\"step\"";
        html += " :show-stops=\"showStops\"";
        html += " :range=\"range\"";
        html += " :vertical=\"vertical\"";
        html += " :height=\"height\"";
        html += " :show-input=\"showInput\"";
        html += " :show-input-controls=\"showInputControls\"";
        html += " :input-size=\"inputSize\"";
        html += " :show-tooltip=\"showTooltip\"";
        html += " :tooltip-class=\"tooltipClass\"";
        html += " :disabled=\"disabled\"";
        html += " :marks=\"marks\"";

        if (this.json.formatTooltip && this.json.formatTooltip.code){
            html += " :format-tooltip=\"$formatTooltip\"";
        }

        this.options.elEvents.forEach(function(k){
            html += " @"+k+"=\"$loadElEvent_"+k.camelCase()+"\"";
        });
        // this.options.elEvents.forEach(function(k){
        //     html += " @"+k+"=\"$loadElEvent('"+k+"')\"";
        // });

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
        html += "</el-slider>";
        return html;
    }
    // __setReadonly: function(data){
    //
    // }
});
