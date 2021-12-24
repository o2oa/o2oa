o2.xDesktop.requireApp("process.Xform", "$Elinput", null, false);
/** @class Elinput 基于Element UI的颜色选择组件。
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
 * @see {@link https://element.eleme.cn/#/zh-CN/component/color-picker|Element UI ColorPicker 颜色选择器}
 */
MWF.xApplication.process.Xform.Elcolorpicker = MWF.APPElcolorpicker =  new Class(
    /** @lends o2.xApplication.process.Xform.Elcolorpicker# */
    {
    Implements: [Events],
    Extends: MWF.APP$Elinput,
    options: {
        "moduleEvents": ["load", "queryLoad", "postLoad"],

        /**
         * 	当绑定值变化时触发。this.event[0]为当前值
         * @event MWF.xApplication.process.Xform.Elcolorpicker#change
         * @see {@link https://element.eleme.cn/#/zh-CN/component/color-picker|颜色选择组件的Events章节}
         */

        /**
         * 面板中当前显示的颜色发生改变时触发。this.event[0]当前显示的颜色值
         * @event MWF.xApplication.process.Xform.Elcolorpicker#active-change
         * @see {@link https://element.eleme.cn/#/zh-CN/component/color-picker|颜色选择组件的Events章节}
         */
        "elEvents": ["change","active-change"]
    },
    _loadNode: function(){
        if (this.isReadonly()) this.json.disabled = true;
        this._loadNodeEdit();
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
