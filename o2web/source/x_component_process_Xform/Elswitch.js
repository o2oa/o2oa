o2.xDesktop.requireApp("process.Xform", "$Elinput", null, false);
/** @class Elinput 基于Element UI的开关组件。
 * @o2cn 开关组件
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
 * @see {@link https://element.eleme.cn/#/zh-CN/component/switch|Element UI Switch 开关}
 */
MWF.xApplication.process.Xform.Elswitch = MWF.APPElswitch =  new Class(
    /** @lends o2.xApplication.process.Xform.Elswitch# */
    {
    Implements: [Events],
    Extends: MWF.APP$Elinput,
    options: {
        "moduleEvents": ["load", "queryLoad", "postLoad"],
        /**
         * switch 状态发生变化时的回调函数。this.event[0]为新状态的值
         * @event MWF.xApplication.process.Xform.Elswitch#change
         * @see {@link https://element.eleme.cn/#/zh-CN/component/switch|开关组件的的 Events章节}
         */
        "elEvents": ["change"]
    },
    _loadMergeReadContentNode: function( contentNode, data ){
        this._loadActiveJson();
        if (data.data==="" || data.data){
            contentNode.set("text", (this.json.activeText || "true"));
        }else{
            contentNode.set("text", (this.json.inactiveText || "false"));
        }
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
    //     debugger;
    //     if (this.isReadonly()) this.json.disabled = true;
    //     this._loadNodeEdit();
    // },
    _appendVueData: function(){
        this.form.Macro.environment.data.check(this.json.id);
        this.json[this.json.id] = this._getBusinessData();

        if (!this.json.width || !this.json.width.toFloat()) this.json.width = 40;
        if (!this.json.activeText) this.json.activeText = "";
        if (!this.json.inactiveText) this.json.inactiveText = "";
        if (!this.json.activeColor) this.json.activeColor = "#409EFF";
        if (!this.json.inactiveColor) this.json.inactiveColor = "#C0CCDA";

        if (!this.json.activeIconClass) this.json.activeIconClass = "";
        if (!this.json.inactiveIconClass) this.json.inactiveIconClass = "";

        if (!this.json.readonly) this.json.readonly = false;
        if (!this.json.description) this.json.description = "";
        if (!this.json.disabled) this.json.disabled = false;

        this._loadActiveJson();
    },
    _loadActiveJson: function(){
        if (!this.json.valueType) this.json.activeValueType = "boolean";
        switch(this.json.valueType){
            case "boolean":
                this.json.activeValue = true;
                this.json.inactiveValue = false;
                break;
            case "string":
                if (!this.json.activeValue) this.json.activeValue = "1";
                if (!this.json.inactiveValue) this.json.inactiveValue = "0";
                break;
            case "number":
                if (!this.json.activeValue) this.json.activeValue = 1;
                if (!this.json.inactiveValue) this.json.inactiveValue = 0;
                this.json.activeValue = this.json.activeValue.toFloat();
                this.json.inactiveValue = this.json.inactiveValue.toFloat();
                break;
            default:
                this.json.activeValue = true;
                this.json.inactiveValue = false;
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
        var html = "<el-switch";
        html += " v-model=\""+this.json.$id+"\"";
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
        html += "</el-switch>";
        return html;
    },
    __setReadonly: function(data){
        if (this.isReadonly()){
            if (data==="" || data){
                this.node.set("text", (this.json.activeText || "true"));
            }else{
                this.node.set("text", (this.json.inactiveText || "false"));
            }

            if( this.json.elProperties ){
                this.node.set(this.json.elProperties );
            }
            if (this.json.elStyles){
                this.node.setStyles( this._parseStyles(this.json.elStyles) );
            }

            this.fireEvent("load");
            this.isLoaded = true;
        }
    },

        getExcelData: function(){
            var data = this.json[this.json.$id];
            if (data==="" || data){
                return this.json.activeText || "true";
            }else{
                return this.json.inactiveText || "false";
            }
        },
        setExcelData: function(d){
            var data = true;
            this.excelData = d;
            if ( (d || "").toString === ( this.json.inactiveText || "false" ) ){
                data = false;
            }
            this.setData(data, true);
        }
}); 
