o2.xDesktop.requireApp("process.Xform", "$Elswitch", null, false);
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
MWF.xApplication.process.Xform.Elswitch = MWF.APPElswitch =  new Class(
    /** @lends o2.xApplication.process.Xform.Elnumber# */
    {
    Implements: [Events],
    Extends: MWF.APP$Elinput,
    options: {
        "moduleEvents": ["load", "queryLoad", "postLoad"],
        "elEvents": ["change"]
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
        this.json[this.json.id] = this._getBusinessData();

        if (!this.json.width || o2.typeOf(this.json.width)!=="number") this.json.width = 40;
        if (!this.json.activeText) this.json.activeText = "";
        if (!this.json.inactiveText) this.json.inactiveText = "";
        if (!this.json.activeColor) this.json.activeColor = "#409EFF";
        if (!this.json.inactiveColor) this.json.inactiveColor = "#C0CCDA";

        if (!this.json.activeIconClass) this.json.activeIconClass = "";
        if (!this.json.inactiveIconClass) this.json.inactiveIconClass = "";

        if (!this.json.readonly) this.json.readonly = false;
        if (!this.json.description) this.json.description = "";
        if (!this.json.disabled) this.json.disabled = false;

        if (!this.json.activeValueType) this.json.activeValueType = "boolean-true";
        switch(this.json.activeValueType){
            case "boolean-false":
                this.json.activeValue = false;
                break;
            case "boolean-true":
                this.json.activeValue = true;
                break;
            default:
                if (!this.json.activeValue) this.json.activeValue = true;
        }

        if (!this.json.inactiveValueType) this.json.inactiveValueType = "boolean-false";
        switch(this.json.inactiveValueType){
            case "boolean-false":
                this.json.inactiveValue = false;
                break;
            case "boolean-true":
                this.json.inactiveValue = true;
                break;
            default:
                if (!this.json.activeValue) this.json.inactiveValue = false;
        }


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
        var html = "<el-switch";
        html += " v-model=\""+this.json.id+"\"";
        html += " :width=\"width\"";
        html += " :active-text=\"activeText\"";
        html += " :inactive-text=\"inactiveText\"";
        html += " :active-color=\"activeColor\"";
        html += " :inactive-color=\"inactiveColor\"";

        html += " :disabled=\"disabled\"";
        html += " :active-icon-class=\"activeIconClass\"";
        html += " :inactive-icon-class=\"inactiveIconClass\"";

        html += " :active-value=\"activeValue\"";
        html += " :inactive-value=\"inactiveValue\"";

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
        html += "</el-switch>";
        return html;
    }
}); 
