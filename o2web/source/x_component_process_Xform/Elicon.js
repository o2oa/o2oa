MWF.xDesktop.requireApp("process.Xform", "$ElModule", null, false);
/** @class Elicon 基于Element UI的图标组件。
 * @o2cn 图标组件
 * @example
 * //可以在脚本中获取该组件
 * //方法1：
 * var icon = this.form.get("name"); //获取组件
 * //方法2
 * var icon = this.target; //在组件事件脚本中获取
 * @extends MWF.xApplication.process.Xform.$Module
 * @o2category FormComponents
 * @o2range {Process|CMS|Portal}
 * @hideconstructor
 * @see {@link https://element.eleme.cn/#/zh-CN/component/icon|Element UI Icon 图标}
 */
MWF.xApplication.process.Xform.Elicon = MWF.APPElicon =  new Class(
    /** @lends MWF.xApplication.process.Xform.Elicon# */
    {
    Implements: [Events],
    Extends: MWF.APP$ElModule,
    _appendVueData: function(){
        if (!this.json.icon) this.json.icon = "el-icon-platform-eleme";
        if (!this.json.iconSize) this.json.iconSize = "16";
        if (!this.json.iconColor) this.json.iconColor = "";
        if (!this.json.icon) this.json.icon = "";
        if (!this.json.elStyles) this.json.elStyles = {};
    },
    _createElementHtml: function(){
        var html = "<i";
        html += " :class=\"icon\"";

        if (this.json.elProperties){
            Object.keys(this.json.elProperties).forEach(function(k){
                if (this.json.elProperties[k]) html += " "+k+"=\""+this.json.elProperties[k]+"\"";
            }, this);
        }

        html += " :style=\"[elStyles, {fontSize: iconSize+'px', color: iconColor}]\"";


        html += "></i>";
        return html;
    }
    // _loadVue: function(callback){
    //     if (!window.Vue){
    //         var vue = (o2.session.isDebugger) ? "vue_develop" : "vue";
    //         o2.loadAll({"css": "../o2_lib/vue/element/index.css", "js": [vue, "elementui"]}, { "sequence": true }, callback);
    //     }else{
    //         if (callback) callback();
    //     }
    // },
    // _queryLoaded: function(){
    //     this._loadVue();
    // },

    // load: function(){
    //     this._loadModuleEvents();
    //     if (this.fireEvent("queryLoad")){
    //         this._queryLoaded();
    //         this._loadUserInterface();
    //     }
    // },
    //
    // _loadUserInterface: function(){
    //     this.node.appendHTML(this._createElementHtml(), "before");
    //     var icon = this.node.getPrevious();
    //
    //     this.node.destroy();
    //     this.node = icon;
    //     this.node.set({
    //         "id": this.json.id,
    //         "MWFType": this.json.type
    //     });
    //     this._createVueApp();
    // },
    // _createVueApp: function(){
    //     if (!this.vm) this._loadVue(this._mountVueApp.bind(this));
    // },
    //
    // _loadVue: function(callback){
    //     if (!window.Vue){
    //         o2.loadAll({"css": "../o2_lib/vue/element/index.css", "js": ["vue", "elementui"]}, { "sequence": true }, callback);
    //     }else{
    //         if (callback) callback();
    //     }
    // },
    // _mountVueApp: function(){
    //     if (!this.vueApp) this.vueApp = this._createVueExtend();
    //
    //     /**
    //      * @summary Vue对象实例
    //      * @see https://vuejs.org/
    //      * @member {VueInstance}
    //      */
    //     this.vm = new Vue(this.vueApp).$mount(this.node);
    // },
    //
    // _createVueExtend: function(){
    //     var _self = this;
    //     return {
    //         data: this._createVueData(),
    //         mounted: function(){
    //             _self._afterMounted(this.$el);
    //         }
    //     };
    // },
    // _createVueData: function(){
    //     return this.json;
    // },
    // _afterMounted: function(el){
    //     this.node = el;
    //     this.node.set({
    //         "id": this.json.id,
    //         "MWFType": this.json.type
    //     });
    //     this._loadDomEvents();
    //     this._afterLoaded();
    //     this.fireEvent("postLoad");
    //     this.fireEvent("load");
    // },
    //     _createElementHtml: function(){
    //         var html = "<i";
    //         html += " :class=\"icon\"";
    //
    //         if (this.json.elProperties){
    //             Object.keys(this.json.elProperties).forEach(function(k){
    //                 if (this.json.elProperties[k]) html += " "+k+"=\""+this.json.elProperties[k]+"\"";
    //             }, this);
    //         }
    //
    //         // var styles = {};
    //         // if (this.json.iconSize) styles["font-size"] = this.json.iconSize+"px";
    //         // if (this.json.iconColor) styles["color"] = this.json.iconColor;
    //         // styles = Object.merge(styles, this.json.elStyles);
    //         //
    //         // if (styles){
    //         //     var style = "";
    //         //     Object.keys(styles).forEach(function(k){
    //         //         if (styles[k]) style += k+":"+styles[k]+";";
    //         //     }, this);
    //         //     html += " style=\""+style+"\"";
    //         // }
    //
    //         html += " :style=\"[elStyles, {fontSize: iconSize+'px', color: iconColor}]\"";
    //
    //
    //         html += "></i>";
    //         return html;
    //     }

}); 
