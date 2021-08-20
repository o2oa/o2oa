o2.xDesktop.requireApp("process.Xform", "$Module", null, false);
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
o2.xApplication.process.Xform.Elinput = MWF.APPElinput =  new Class(
    /** @lends o2.xApplication.process.Xform.Elinput# */
    {
    Implements: [Events],
    Extends: MWF.APP$Module,
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
    load: function(){
        this._loadModuleEvents();
        if (this.fireEvent("queryLoad")){
            this._queryLoaded();
            this._loadUserInterface();
        }
    },

    _loadUserInterface: function(){
        this.node.appendHTML(this._createElementHtml(), "before");
        var input = this.node.getPrevious();

        this.node.destroy();
        this.node = input;
        this.node.set({
            "id": this.json.id,
            "MWFType": this.json.type
        });
        this.node.addClass("o2_vue");
        this._createVueApp();
    },
    _createVueApp: function(){
        if (!this.vm) this._loadVue(this._mountVueApp.bind(this));
    },

    _loadVue: function(callback){
        if (!window.Vue){
            o2.loadAll({"css": "../o2_lib/vue/element/index.css", "js": ["vue", "elementui"]}, { "sequence": true }, callback);
        }else{
            if (callback) callback();
        }
    },
    _mountVueApp: function(){
        if (!this.vueApp) this.vueApp = this._createVueExtend();

        /**
         * @summary Vue对象实例
         * @see https://vuejs.org/
         * @member {VueInstance}
         */
        this.vm = new Vue(this.vueApp).$mount(this.node);
    },

    _createVueExtend: function(){
        var _self = this;
        return {
            data: this._createVueData(),
            mounted: function(){
                _self._afterMounted(this.$el);
            }
        };
    },
    _createVueData: function(){
        if (this.json.vueData && this.json.vueData.code){
            var d = this.form.Macro.exec(this.json.vueData.code, this);
            this.json = Object.merge(d, this.json);
        }
        if (!this.json.size) this.json.size = "";
        if (!this.json.bttype) this.json.bttype = "";
        if (!this.json.plain) this.json.plain = false;
        if (!this.json.round) this.json.round = false;
        if (!this.json.circle) this.json.circle = false;
        if (!this.json.disabled) this.json.disabled = false;
        if (!this.json.loading) this.json.loading = false;

        return this.json;
    },
    _afterMounted: function(el){
        this.node = el;
        this.node.set({
            "id": this.json.id,
            "MWFType": this.json.type
        });
        this._loadDomEvents();
        this._afterLoaded();
        this.fireEvent("postLoad");
        this.fireEvent("load");
    },
    _createElementHtml: function(){
        debugger;
        var html = "<el-button";
        html += " :size=\"size\"";
        html += " :type=\"bttype\"";
        html += " :plain=\"plain\"";
        html += " :round=\"round\"";
        html += " :circle=\"circle\"";
        html += " :disabled=\"disabled\"";
        html += " :loading=\"loading\"";

        if (this.json.autofocus==="yes") html += " autofocus";

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

        html += ">"+((this.json.circle!=="yes" && this.json.isText!=="no") ? (this.json.name || this.json.id) : "")+"</el-button>";
        return html;
    }
}); 
