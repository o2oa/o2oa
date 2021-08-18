o2.xDesktop.requireApp("process.Xform", "$Module", null, false);
/** @class Elcommon 基于Element UI的通用组件。
 * @example
 * //可以在脚本中获取该组件
 * //方法1：
 * var button = this.form.get("name"); //获取组件
 * //方法2
 * var button = this.target; //在组件事件脚本中获取
 * @extends o2.xApplication.process.Xform.$Module
 * @o2category FormComponents
 * @o2range {Process|CMS|Portal}
 * @hideconstructor
 */
o2.xApplication.process.Xform.Elcommon = o2.APPElcommon =  new Class(
    /** @lends o2.xApplication.process.Xform.Elbutton# */
    {
    Implements: [Events],
    Extends: o2.APP$Module,
    /**
     * @summary 组件的配置信息，同时也是Vue组件的data。
     * @member o2.xApplication.process.Xform.Elbutton#json {JsonObject}
     * @example
     *  //可以在脚本中获取此对象，下面两行代码是等价的，它们获取的是同一个对象
     * var json = this.form.get("elcommon").json;       //获取组件的json对象
     * var json = this.form.get("elcommon").vm.$data;   //获取Vue组件的data数据，
     *
     * //通过json对象操作Element组件
     * json.bttype = "success"; //将按钮样式改为success
     * json.loading = true;     //将按钮显示为加载中状态
     * json.disabled = true;    //将按钮设置为禁用
     */
    load: function(){
        this._loadModuleEvents();
        if (this.fireEvent("queryLoad")){
            this._queryLoaded();
            this._loadUserInterface();
        }
    },

    _loadUserInterface: function(){
        this.node.appendHTML(this._createElementHtml());
        this.node.set({
            "id": this.json.id,
            "MWFType": this.json.type
        });
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
        var html = this.json.vueTemplate || "";
        return html;
    }
}); 
