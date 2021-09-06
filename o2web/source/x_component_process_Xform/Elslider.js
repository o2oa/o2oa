o2.xDesktop.requireApp("process.Xform", "$Elinput", null, false);
/** @class Elinput 基于Element UI的数字输入框组件。
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
MWF.xApplication.process.Xform.Elslider = MWF.APPElslider =  new Class(
    /** @lends o2.xApplication.process.Xform.Elnumber# */
    {
    Implements: [Events],
    Extends: MWF.APP$Elinput,
    options: {
        "moduleEvents": ["load", "queryLoad", "postLoad"],
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
    _loadNode: function(){
        if (this.isReadonly()) this.json.disabled = true;
        this._loadNodeEdit();
    },
    _appendVueData: function(){
        this.form.Macro.environment.data.check(this.json.id);
        this.json[this.json.id] = this._getBusinessData();

        if (!this.json.max || !this.json.max.toFloat()) this.json.max = 100;
        if (!this.json.min || !this.json.min.toFloat()) this.json.min = 0;
        if (!this.json.step || !this.json.step.toFloat()) this.json.step = 1;
        if (this.json.showTooltip!==false) this.json.showTooltip = true;

        if (this.json.vertical && !this.json.height) this.json.height = "100px";
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
        app.methods.$loadElEvent = function(ev){
            this.validationMode();
            if (ev==="change") this._setBusinessData(this.getInputData());
            if (this.json.events && this.json.events[ev] && this.json.events[ev].code){
                this.form.Macro.fire(this.json.events[ev].code, this, event);
            }
        }.bind(this);

        if (this.json.formatTooltip && this.json.formatTooltip.code){
            var fun = this.form.Macro.exec(this.json.formatTooltip.code, this);
            if (o2.typeOf(fun)==="function"){
                app.methods.$formatTooltip = fun;
            }else{
                app.methods.$formatTooltip = function(){};
            }
        }
    },
    _createElementHtml: function(){
        var html = "<el-slider";
        html += " v-model=\""+this.json.id+"\"";
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
        html += "</el-slider>";
        return html;
    },
    __setValue: function(value){
        this.moduleValueAG = null;
        this._setBusinessData(value);
        this.json[this.json.id] = value;
        this.fieldModuleLoaded = true;
        return value;
    },
    __setData: function(data){
        var old = this.getInputData();
        this._setBusinessData(data);
        this.json[this.json.id] = data;
        if (old!==data) this.fireEvent("change");
        this.moduleValueAG = null;
        this.validationMode();
    },
}); 
