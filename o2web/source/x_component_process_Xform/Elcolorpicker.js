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
MWF.xApplication.process.Xform.Elcolorpicker = MWF.APPElcolorpicker =  new Class(
    /** @lends o2.xApplication.process.Xform.Elcolorpicker# */
    {
    Implements: [Events],
    Extends: MWF.APP$Elinput,
    options: {
        "moduleEvents": ["load", "queryLoad", "postLoad"],
        "elEvents": ["change","active-change"]
    },
    _appendVueData: function(){
        if (!this.json.isReadonly) this.json.isReadonly = false;
        if (!this.json.disabled) this.json.disabled = false;
        if (!this.json.showAlpha) this.json.showAlpha = false;
        if (!this.json.colorFormat) {
            if( this.json.showAlpha ){
                this.json.colorFormat = "hex";
            }else{
                this.json.colorFormat = "rgb";
            }
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
        var html = "<el-color-picker";
        html += " v-model=\""+this.json.$id+"\"";
        html += " :readonly=\"isReadonly\"";
        html += " :disabled=\"disabled\"";
        html += " :show-alpha=\"showAlpha\"";
        html += " :color-format=\"colorFormat\"";

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

        html += "</el-color-picker>";
        return html;
    }
}); 
