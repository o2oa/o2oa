o2.xDesktop.requireApp("process.Xform", "$Module", null, false);
o2.xDesktop.requireApp("process.Xform", "$ElModule", null, false);
/** @class Elcommon 基于Element UI的通用组件。
 * @o2cn 通用组件
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
    // initialize: function(node, json, form, options){
    //     this.node = $(node);
    //     this.node.store("module", this);
    //     this.json = json;
    //     this.form = form;
    //     this.field = true;
    //     this.parentLine = null;
    // },
    _checkVueHtml: function(){
        var nodes = this.node.querySelectorAll("*[v-model]");
        this.tmpVueData = {};
        var arrs = ["el-checkbox-group"];
        nodes.forEach(function(node){
            var model = node.get("v-model");
            if (model) this.tmpVueData[model] = (arrs.indexOf(node.tagName.toString().toLowerCase())===-1) ? "" : [];
        }.bind(this));
    },
    _loadUserInterface: function(){
        this.node.set("html", this._createElementHtml());
        //this._checkVueHtml();
        this._checkVmodel();

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
        // if (this.tmpVueData){
        //     Object.keys(this.tmpVueData).each(function(k){
        //         this.form.Macro.environment.data.check(k, this.tmpVueData[k]);
        //     }.bind(this));
        // }
        if (this.vModels && this.vModels.length){
            this.vModels.forEach(function(m){
                if (!this.json.hasOwnProperty(m)) this.json[m] = "";
            }.bind(this));
        }

        var app = {};
        if (this.json.vueApp && this.json.vueApp.code) app = this.form.Macro.exec(this.json.vueApp.code, this);
        if (!app) app = {};
        if (app.data){
            var ty = o2.typeOf(app.data);
            switch (ty){
                case "object":
                    Object.keys(app.data).each(function(k){
                        this.json[k] = app.data[k];
                        //this.form.Macro.environment.data.add(k, app.data[k]);
                    }.bind(this));
                    app.data = this.json;
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
                            _self.json[k] = d[k];
                            //_self.form.Macro.environment.data.add(k, d[k]);
                        });
                        //var data = Object.merge(_slef.json);
                        //return _self.form.Macro.environment.data;
                        return _self.json;
                    };
                    break;
            }
        }else{
            //app.data = this.form.Macro.environment.data;
            app.data = this.json;
        }

        var _self = this;
        var mountedFun = app.mounted;
        app.mounted = function(){
            _self._afterMounted(this.$el);
            if (mountedFun && o2.typeOf(mountedFun)=="function") return mountedFun.apply(this);
        };

        this.appendVueWatch(app);

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
    // _filterHtml: function(html){
    //     var tmp = new Element("div", {"html": html});
    //     var nodes = tmp.querySelectorAll("*[v-model]");
    //     this.tmpVueData = {};
    //     nodes.forEach(function(node){
    //         this.tmpVueData[node.get("v-model")] = "";
    //     }.bind(this));
    //     return html;
    // },
    _checkVmodel: function(text){
        // if (text){
        //     this.vModels = [];
        //     var reg = /(?:v-model)(?:.lazy|.number|.trim)?(?:\s*=\s*)(?:["'])?([^"']*)/g;
        //     var arr;
        //     while ((arr = reg.exec(text)) !== null) {
        //         if (arr.length>1 && arr[1]){
        //             var modelId = this.json.id.substring(0, this.json.id.lastIndexOf(".."));
        //             modelId = (modelId) ? modelId+".."+arr[1] : arr[1];
        //             this.json[arr[1]] = this._getBusinessData(modelId);
        //             this.vModels.push(arr[1]);
        //         }
        //     }
        // }
        var nodes = this.node.querySelectorAll("*[v-model]");
        this.vModels = [];
        var arrs = ["el-checkbox-group"];
        nodes.forEach(function(node){
            var model = node.get("v-model");
            var tag = node.tagName.toString().toLowerCase();
            if (model){
                var modelId = this.json.id.substring(0, this.json.id.lastIndexOf(".."));
                modelId = (modelId) ? modelId+".."+model : model;
                this.json[model] = this._getBusinessData(modelId);
                if (!this.json[model]){
                    this.json[model] = (arrs.indexOf(tag)===-1) ? "" : []
                }
                this.vModels.push(model);
            }
        }.bind(this));
    },
    _createElementHtml: function(){
        var html = this.json.vueTemplate || "";
        // if (html) this._checkVmodel(html);
        // return this._filterHtml(html);
        return html;
    },
    resetData: function(){
        if (this.vModels && this.vModels.length){
            this.vModels.forEach(function(m){
                var modelId = this.json.id.substring(0, this.json.id.lastIndexOf(".."));
                modelId = (modelId) ? modelId+".."+m : m;
                this.json[m] = this._getBusinessData(modelId);
            }.bind(this));
        }
    },
}); 
