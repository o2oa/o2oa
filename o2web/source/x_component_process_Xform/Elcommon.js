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
    /** @lends o2.xApplication.process.Xform.Elcommon# */
    {
    Implements: [Events],
    Extends: o2.APP$ElModule,
    /**
     * @summary 组件的配置信息，同时也是Vue组件的data。
     * @member o2.xApplication.process.Xform.Elcommon#json {JsonObject}
     * @example
     *  //可以在脚本中获取此对象，下面两行代码是等价的，它们获取的是同一个对象
     * var json = this.form.get("elcommon").json;       //获取组件的json对象
     * var json = this.form.get("elcommon").vm.$data;   //获取Vue组件的data数据，
     *
     */
    // load: function(){
    //     this._loadModuleEvents();
    //     if (this.fireEvent("queryLoad")){
    //         this._queryLoaded();
    //         this._loadUserInterface();
    //     }
    // },
    //
    _loadUserInterface: function(){
        this.node.set("html", this._createElementHtml());
        this.node.set({
            "id": this.json.id,
            "MWFType": this.json.type
        });
        this.node.addClass("o2_vue");
        this._createVueApp();
    },
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

    _createVueExtend: function(){
        if (this.tmpVueData){
            Object.keys(this.tmpVueData).each(function(k){
                this.form.Macro.environment.data.check(k);
            }.bind(this));
        }
        var app = {};
        if (this.json.vueApp && this.json.vueApp.code) app = this.form.Macro.exec(this.json.vueApp.code, this);
        if (app.data){
            var ty = o2.typeOf(app.data);
            switch (ty){
                case "object":
                    Object.keys(app.data).each(function(k){
                        this.form.Macro.environment.data.add(k, app.data[k]);
                    }.bind(this));
                    app.data = this.form.Macro.environment.data;
                    // app.data = this.json;
                    // app.data = Object.merge(this.json, this.form.Macro.environment.data);
                    break;
                case "function":
                    var dataFun = app.data;
                    var _slef = this;
                    app.data = function(){
                        var d = dataFun();

                        //_self.form.Macro.environment.data.add(_self.json.id, d);

                        Object.keys(d).each(function(k){
                            _self.form.Macro.environment.data.add(k, d[k]);
                        });
                        //var data = Object.merge(_slef.json);
                        return _self.form.Macro.environment.data;
                    };
                    break;
            }
        }else{
            app.data = this.form.Macro.environment.data;
        }

        var _self = this;
        var mountedFun = app.mounted;
        app.mounted = function(){
            _self._afterMounted(this.$el);
            if (mountedFun && o2.typeOf(mountedFun)=="function") mountedFun.apply(this);
        };
        return app;
    },

    // _afterMounted: function(el){
    //     this.node = el;
    //     this.node.set({
    //         "id": this.json.id,
    //         "MWFType": this.json.type
    //     });
    //     this._loadVueCss();
    //     this._loadDomEvents();
    //     this._afterLoaded();
    //     this.fireEvent("postLoad");
    //     this.fireEvent("load");
    // },
    // _loadVueCss: function(){
    //     if (this.json.vueCss && this.json.vueCss.code){
    //         this.styleNode = this.node.loadCssText(this.json.vueCss.code, {"notInject": true});
    //         this.styleNode.inject(this.node, "before");
    //     }
    // },
    _filterHtml: function(html){
        var reg = /(?:@|\:)\S*(?:\=)\S*(?:\"|\')/g;
        var v = html.replace(reg, "");

        var tmp = new Element("div", {"html": v});
        var nodes = tmp.querySelectorAll("*[v-model]");
        this.tmpVueData = {};
        nodes.forEach(function(node){
            this.tmpVueData[node.get("v-model")] = "";
        }.bind(this));


        return v;
    },
    _createElementHtml: function(){
        var html = this.json.vueTemplate || "";
        return this._filterHtml(html);
    }
}); 
