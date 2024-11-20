o2.xDesktop.requireApp("process.Xform", "$Elinput", null, false);
/** @class Elinput 基于Element UI的评分组件。
 * @o2cn 评分组件
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
 * @see {@link https://element.eleme.cn/#/zh-CN/component/rate|Element UI Rate 评分}
 */
MWF.xApplication.process.Xform.Elrate = MWF.APPElrate =  new Class(
    /** @lends o2.xApplication.process.Xform.Elrate# */
    {
    Implements: [Events],
    Extends: MWF.APP$Elinput,
    options: {
        "moduleEvents": ["load", "queryLoad", "postLoad"],
        /**
         * 分值改变时触发	。this.event[0]为改变后的分值
         * @event MWF.xApplication.process.Xform.Elrate#change
         * @see {@link https://element.eleme.cn/#/zh-CN/component/rate|评分组件的Events章节}
         */
        "elEvents": ["change"]
    },
    // _loadNode: function(){
    //     if (this.isReadonly()) this.json.disabled = true;
    //     this._loadNodeEdit();
    // },
    _appendVueData: function(){
        if (!this.json.max) this.json.max = "";
        if (!this.json.isReadonly && !this.form.json.isReadonly) this.json.isReadonly = false;
        if (!this.json.max) this.json.max = 5;
        if (!this.json.allowHalf) this.json.allowHalf = false;
        if (!this.json.lowThreshold) this.json.lowThreshold = 2;
        if (!this.json.highThreshold) this.json.highThreshold = 4;
        this.json.colors = [
            this.json.lowColor, this.json.mediumColor, this.json.highColor
        ];
        if( this.json.showAfter === "text" ){
            this.json.showText = true;
            this.json.showScore = false;
        }else if( this.json.showAfter === "score" ){
            this.json.showText = false;
            this.json.showScore = true;
        }else{
            this.json.showText = false;
            this.json.showScore = false;
        }
        if( o2.typeOf(this.json.texts) === "string"){
            this.json.texts = this.json.texts.split(",");
        }
        if(!this.json.textColor)this.json.textColor = "";

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
        var html = "<el-rate";
        html += " v-model=\""+this.json.$id+"\"";
        html += " :readonly=\"isReadonly\"";
        html += " :disabled=\"disabled\"";
        html += " :max=\"max\"";
        html += " :allow-half=\"allowHalf\"";
        html += " :low-threshold=\"lowThreshold\"";
        html += " :high-threshold=\"highThreshold\"";
        html += " :colors=\"colors\"";
        html += " :void-color=\"voidColor\"";
        html += " :disabled-void-color=\"disabledVoidColor\"";
        html += " :show-text=\"showText\"";
        html += " :text-color=\"textColor\"";
        html += " :texts=\"texts\"";
        html += " :show-score=\"showScore\"";


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

        html += "</el-rate>";
        return html;
    }
}); 
