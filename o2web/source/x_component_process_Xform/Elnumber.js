o2.xDesktop.requireApp("process.Xform", "$Elinput", null, false);
/** @class Elinput 基于Element UI的数字输入框组件。
 * @o2cn 数字输入框
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
 * @see {@link https://element.eleme.cn/#/zh-CN/component/input-number|Element UI InputNumber 计数器}
 */
MWF.xApplication.process.Xform.Elnumber = MWF.APPElnumber =  new Class(
    /** @lends o2.xApplication.process.Xform.Elnumber# */
    {
    Implements: [Events],
    Extends: MWF.APP$Elinput,
    options: {
        "moduleEvents": ["load", "queryLoad", "postLoad"],
        /**
         * 当 input 获得焦点时触发。this.event[0]指向Event
         * @event MWF.xApplication.process.Xform.Elnumber#focus
         * @see {@link https://element.eleme.cn/#/zh-CN/component/input-number|计数器的Events章节}
         */
        /**
         * 当 input 失去焦点时触发。this.event[0]指向Event
         * @event MWF.xApplication.process.Xform.Elnumber#blur
         * @see {@link https://element.eleme.cn/#/zh-CN/component/input-number|计数器的Events章节}
         */
        /**
         * 绑定值被改变时触发。this.event[0]为组件的currentValue，this.event[1]为组件的oldValue
         * @event MWF.xApplication.process.Xform.Elnumber#change
         * @see {@link https://element.eleme.cn/#/zh-CN/component/input-number|计数器的Events章节}
         */
        "elEvents": ["focus", "blur", "change"]
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
    _loadUserInterface: function(){
        if ( this.isSectionMergeRead() ) { //区段合并显示
            this.node.empty();
            this.node.set({
                "nodeId": this.json.id,
                "MWFType": this.json.type
            });
            switch (this.json.mergeTypeRead) {
                case "amount":
                    this._loadMergeAmountReadNode();
                    break;
                case "average":
                    this._loadMergeAverageReadNode();
                    break;
                default:
                    this._loadMergeReadNode();
                    break;
            }
        }else{
            if( this.isSectionMergeEdit() ){
                switch (this.json.mergeTypeEdit) {
                    case "amount":
                        this._loadMergeAmountEidtNode();
                        break;
                    case "average":
                        this._loadMergeAverageEditNode();
                        break;
                }
            }else{
                this._loadNode();
            }
            if (this.json.compute === "show"){
                this._setValue(this._computeValue());
            }else{
                this._loadValue();
            }
        }
    },
    _loadMergeAmountReadNode: function(){
        var data = this.getBusinessDataById();
        var total = new Decimal(0);
        for( var key in data ){
            total = total.plus(new Decimal(data[key] || 0));
        }
        this.node.set("text", total.toString());
    },
    _loadMergeAverageReadNode: function(){
        var data = this.getBusinessDataById();
        var total = new Decimal(0);
        for( var key in data ){
            total = total.plus(new Decimal(data[key] || 0));
        }
        var average = total.div(  new Decimal(Object.keys(data).length) );
        this.node.set("text", average.toString());
    },
    _loadMergeAmountEidtNode: function(){
        var data = this.getBusinessDataById();
        var total = new Decimal(0);
        for( var key in data ){
            total = total.plus(new Decimal(data[key] || 0));
        }
        this._setBusinessData( total.toNumber() );
        this._loadNode();
    },
    _loadMergeAverageEditNode: function(){
        var data = this.getBusinessDataById();
        var total = new Decimal(0);
        for( var key in data ){
            total = total.plus(new Decimal(data[key] || 0));
        }
        var average = total.div(  new Decimal(Object.keys(data).length) );
        this._setBusinessData( average.toNumber() );
        this._loadNode();
    },
    _appendVueData: function(){
        this.form.Macro.environment.data.check(this.json.id);
        this.json[this.json.id] = this._getBusinessData();

        // if (!this.json.max || o2.typeOf(this.json.max)!=="number") this.json.max = "Infinity";
        // if (!this.json.min || o2.typeOf(this.json.max)!=="number") this.json.min = "-Infinity";
        if (!this.json.step || o2.typeOf(this.json.step)!=="number") this.json.step = 1;
        if (!this.json.stepStrictly) this.json.stepStrictly = false;
        if (!this.json.disabled) this.json.disabled = false;
        if (!this.json.precision) this.json.precision = 0;
        if (!this.json.size) this.json.size = "";
        if (!this.json.controlsPosition) this.json.controlsPosition = "";
        if (!this.json.readonly) this.json.readonly = false;
        if (!this.json.description) this.json.description = "";
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
        var html = "<el-input-number";
        html += " v-model=\""+this.json.$id+"\"";
        if (o2.typeOf(this.json.max)==="number") html += " :max=\"max\"";
        if (o2.typeOf(this.json.min)==="number") html += " :min=\"min\"";
        html += " :step=\"step\"";
        html += " :step-strictly=\"stepStrictly\"";
        html += " :disabled=\"disabled\"";
        html += " :size=\"size\"";
        html += " :precision=\"precision\"";
        html += " :controls-position=\"controlsPosition\"";
        html += " :readonly=\"readonly\"";
        html += " :placeholder=\"description\"";

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

        // if (this.json.vueSlot) html += this.json.vueSlot;

        html += "</el-input-number>";
        return html;
    }
}); 
