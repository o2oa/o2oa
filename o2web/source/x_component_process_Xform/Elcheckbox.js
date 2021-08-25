MWF.xDesktop.requireApp("process.Xform", "Elradio", null, false);
/** @class Elradio 基于Element UI的按钮组件。
 * @example
 * //可以在脚本中获取该组件
 * //方法1：
 * var radio = this.form.get("name"); //获取组件
 * //方法2
 * var radio = this.target; //在组件事件脚本中获取
 * @extends MWF.xApplication.process.Xform.$Module
 * @o2category FormComponents
 * @o2range {Process|CMS|Portal}
 * @hideconstructor
 */
MWF.xApplication.process.Xform.Elcheckbox = MWF.APPElcheckbox =  new Class(
    /** @lends MWF.xApplication.process.Xform.Elradio# */
    {
    Implements: [Events],
    Extends: MWF.APPElradio,
    options: {
        /**
         * 组件加载前触发。
         * @event MWF.xApplication.process.Xform.$Module#queryLoad
         * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
         */
        /**
         * 组件加载时触发.
         * @event MWF.xApplication.process.Xform.$Module#load
         * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
         */
        /**
         * 组件加载后触发.
         * @event MWF.xApplication.process.Xform.$Module#postLoad
         * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
         */
        /**
         * 值发生改变时触发.
         * @event MWF.xApplication.process.Xform.$Module#change
         * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
         */
        "moduleEvents": ["load", "queryLoad", "postLoad", "change"]
    },
    /**
     * @summary 组件的配置信息，同时也是Vue组件的data。
     * @member MWF.xApplication.process.Xform.Elradio#json {JsonObject}
     * @example
     *  //可以在脚本中获取此对象，下面两行代码是等价的，它们获取的是同一个对象
     * var json = this.form.get("elcheckbox").json;       //获取组件的json对象
     * var json = this.form.get("elcheckbox").vm.$data;   //获取Vue组件的data数据，
     *
     * //通过json对象操作Element组件
     * json.border = true;      //带边框的多选按钮
     * json.size = "small";     //设置多选按钮大小，仅border为true时有效
     * json.textColor = "#ff0000";  //设置按钮样式时，选中状态的文本颜色
     * json.fillColor = "#ff0000";  //设置按钮样式时，选中状态的背景颜色
     */
    load: function(){
        this._loadModuleEvents();
        if (this.fireEvent("queryLoad")){
            this._queryLoaded();
            this._loadUserInterface();
        }
    },
    _loadNode: function(){
        this.node.empty();
        if (this.readonly || this.json.isReadonly ){
            this._loadNodeRead();
        }else{
            this._loadNodeEdit();
        }
    },
    _resetNodeEdit: function(){
        var div = new Element("div");
        div.inject(this.node, "after");
        this.node.destroy();
        this.node = div;
    },
    _loadNodeEdit: function(){
        if (!this.json.preprocessing) this._resetNodeEdit();
        this.setOptions();
    },

    setOptions: function(){
        var optionItems = this.getOptions();
        this._setOptions(optionItems);
    },

    _setOptions: function(optionItems){
        var p = o2.promiseAll(optionItems).then(function(radioValues){
            //this.moduleSelectAG = null;
            return this._loadElradio(radioValues);
        }.bind(this), function(){
            this.moduleSelectAG = null;
        }.bind(this));
        this.moduleSelectAG = Promise.resolve(p);
    },

    _loadElradio: function(radioValues){
        return new Promise(function(resolve){
            if (radioValues && o2.typeOf(radioValues)==="array"){
                this.node.appendHTML(this._createElementHtml(radioValues), "before");
                var button = this.node.getPrevious();

                this.node.destroy();
                this.node = button;
                this.node.set({
                    "id": this.json.id,
                    "MWFType": this.json.type
                });
                this._createVueApp(resolve);
            }
        }.bind(this));
    },

    _createElementHtml: function(radioValues){
        debugger;
        var html = "<el-checkbox-group class='o2_vue' style='box-sizing: border-box!important'";
        html += " v-model=\""+this.json.id+"\"";
        html += " :text-color=\"textColor\"";
        html += " :fill=\"fillColor\"";
        html += " :size=\"size\"";
        html += " @change=\"change\"";


        if (this.json.elGroupProperties){
            Object.keys(this.json.elGroupProperties).forEach(function(k){
                if (this.json.elGroupProperties[k]) html += " "+k+"=\""+this.json.elGroupProperties[k]+"\"";
            }, this);
        }
        if (this.json.elGroupStyles){
            var style = "";
            Object.keys(this.json.elGroupStyles).forEach(function(k){
                if (this.json.elGroupStyles[k]) style += k+":"+this.json.elGroupStyles[k]+";";
            }, this);
            html += " style=\""+style+"\"";
        }
        html += " >";

        radioValues.each(function(item){
            var tmps = item.split("|");
            var text = tmps[0];
            var value = tmps[1] || text;

            html += (this.json.buttonRadio) ? "<el-checkbox-button" : "<el-checkbox";
            html += " label=\""+value+"\"";
            html += " :border=\"border\"";

            if (this.json.elProperties){
                Object.keys(this.json.elProperties).forEach(function(k){
                    if (this.json.elProperties[k]) html += " "+k+"=\""+this.json.elProperties[k]+"\"";
                }, this);
            }

            var radiostyle = "box-sizing: border-box!important;";
            if (this.json.elStyles){
                Object.keys(this.json.elStyles).forEach(function(k){
                    if (this.json.elStyles[k]) radiostyle += k+":"+this.json.elStyles[k]+";";
                }, this);
            }
            html += " style=\""+radiostyle+"\"";

            html += " >"+text;
            html += (this.json.buttonRadio) ? "</el-checkbox-button>" : "</el-checkbox>";
        }.bind(this));
        html += "</el-checkbox-group>";
        return html;
    },

        _setValue: function(value, m){
            var mothed = m || "__setValue";
            if (!!value){
                var p = o2.promiseAll(value).then(function(v){
                    //if (o2.typeOf(v)=="array") v = v[0];
                    if (this.moduleSelectAG){
                        this.moduleValueAG = this.moduleSelectAG;
                        this.moduleSelectAG.then(function(){
                            this[mothed](v);
                            return v;
                        }.bind(this), function(){});
                    }else{
                        this[mothed](v)
                    }
                    return v;
                }.bind(this), function(){});
                this.moduleValueAG = p;
                if (this.moduleValueAG) this.moduleValueAG.then(function(){
                    this.moduleValueAG = null;
                }.bind(this), function(){
                    this.moduleValueAG = null;
                }.bind(this));
            }else{
                this[mothed](value);
            }
        },
    __setValue: function(value){
        this._setBusinessData(value);
        this.json[this.json.id] = (value) ? value : [];
    },
    __setData: function(data){
        this._setBusinessData(data);
        this.json[this.json.id] = data;
        this.validationMode();
        this.fireEvent("setData");
    },


    _createVueApp: function(callback){
        if (!this.vm){
            this._loadVue(function(){
                this._mountVueApp(callback);
            }.bind(this));
        }else{
            if (callback) callback();
        }
    },

    _loadVue: function(callback){
        if (!window.Vue){
            o2.loadAll({"css": "../o2_lib/vue/element/index.css", "js": ["vue", "elementui"]}, { "sequence": true }, callback);
        }else{
            if (callback) callback();
        }
    },
    _mountVueApp: function(callback){
        if (!this.vueApp) this.vueApp = this._createVueExtend(callback);

        /**
         * @summary Vue对象实例
         * @see https://vuejs.org/
         * @member {VueInstance}
         */
        this.vm = new Vue(this.vueApp);
        this.vm.$mount(this.node);
    },

    _createVueExtend: function(callback){
        var _self = this;
        return {
            data: this._createVueData(),
            mounted: function(){
                _self._afterMounted(this.$el);
                if (callback) callback();
            },
            methods: {
                change: function(v){
                    _self.validationMode();
                    if (_self.validation()) {
                        _self._setBusinessData(v || []);
                        _self.fireEvent("change");
                    }
                }
            }
        };
    },
    _createVueData: function(){
        this.form.Macro.environment.data.check(this.json.id);
        this.json[this.json.id] = this._getBusinessData();
        if (!this.json[this.json.id] || !this.json[this.json.id].length) this.json[this.json.id] = [];

        if (this.json.vueData && this.json.vueData.code){
            var d = this.form.Macro.exec(this.json.vueData.code, this);
            this.json = Object.merge(d, this.json);
        }

        if (!this.json.textColor) this.json.textColor = "";
        if (!this.json.fillColor) this.json.fillColor = "";
        if (!this.json.size) this.json.size = "";

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

    getInputData: function(){
        return this.json[this.json.id];
    },

}); 
