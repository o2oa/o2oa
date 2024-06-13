o2.xDesktop.requireApp("process.Xform", "$Elinput", null, false);
MWF.xDesktop.requireApp("process.Xform", "$Selector", null, false);

if( !o2.APP$ElSelector ){
    o2.xApplication.process.Xform.$ElSelector = o2.APP$ElSelector = new Class({
        Implements: [Events],
        Extends: MWF.APP$Elinput
    });
    Object.assign(o2.APP$ElSelector.prototype, o2.APP$Selector.prototype);
}

/** @class Elautocomplete 基于Element UI的自动完成输入框组件。
 * @o2cn 自动完成输入框
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
MWF.xApplication.process.Xform.Elautocomplete = MWF.APPElautocomplete =  new Class(
    /** @lends o2.xApplication.process.Xform.Elautocomplete# */
    {
    Implements: [Events],
    Extends: MWF.APP$ElSelector,
    options: {
        "moduleEvents": ["load", "queryLoad", "postLoad"],
        /**
         * 当 input失去焦点且值有修改是触发，或点击建议面板的选项后且值有修改时触发。this.event[0]为组件值
         * @event MWF.xApplication.process.Xform.Elautocomplete#change
         * @see {@link https://element.eleme.io/#/zh-CN/component/input#dai-shu-ru-jian-yi|input组件的带输入建议章节}
         */
        /**
         * 点击建议面板的选项后时触发。this.event[0]为选中的选项
         * @event MWF.xApplication.process.Xform.Elautocomplete#select
         * @see {@link https://element.eleme.io/#/zh-CN/component/input#input-methods|input组件的Input Method章节}
         */
        "elEvents": ["select", "change"]
    },
    /**
     * @summary 组件的配置信息，同时也是Vue组件的data。
     * @member MWF.xApplication.process.Xform.Elautocomplete#json {JsonObject}
     * @example
     *  //可以在脚本中获取此对象，下面两行代码是等价的，它们获取的是同一个对象
     * var json = this.form.get("elinput").json;       //获取组件的json对象
     * var json = this.form.get("elinput").vm.$data;   //获取Vue组件的data数据，
     *
     * //通过json对象操作Element组件
     * json.size = "mini";      //改变输入框大小
     * json.disabled = true;     //设置输入框为禁用
     */
    _appendVueData: function(){
        this.form.Macro.environment.data.check(this.json.id);
        this.json[this.json.id] = this._getBusinessData();

        if (!this.json.placement) this.json.placement = "bottom-start";
        if (!this.json.popperClass) this.json.popperClass = "";
        if (!this.json.triggerOnFocus && this.json.triggerOnFocus!==false) this.json.triggerOnFocus = true;
        if (!this.json.prefixIcon) this.json.prefixIcon = "";
        if (!this.json.suffixIcon) this.json.suffixIcon = "";
        if (!this.json.description) this.json.description = "";
    },
    resetOption: function(){
        this.reload();
    },
    _createEventFunction: function(methods, k){
        methods["$loadElEvent_"+k.camelCase()] = function(){
            var flag = true;
            if (k==="change") {
                this.validationMode();
                this._setBusinessData(this.getInputData());
                if( !this.validation() )flag = false;
            }
            if (this.json.events && this.json.events[k] && this.json.events[k].code){
                this.form.Macro.fire(this.json.events[k].code, this, arguments);
            }
            if(k==="select"){
                this.validationMode();
                var arr = [];
                var d = this._getBusinessData();
                if( arguments[0] && arguments[0].value )arr.push(arguments[0].value);
                if( (d||"") !== (arr[0] || "")){
                    if (this.json.events && this.json.events["change"] && this.json.events["change"].code){
                        this.form.Macro.fire(this.json.events["change"].code, this, arr);
                    }
                }
                this._setBusinessData(arr[0]);
                this.validation();
            }
            if( flag )this.fireEvent(k, arguments);
        }.bind(this);
    },
    appendVueExtend: function(app){
        if (!app.methods) app.methods = {};
        // app.methods.$loadElEvent = function(ev){
        //     this.validationMode();
        //     if (this.json.events && this.json.events[ev] && this.json.events[ev].code){
        //         this.form.Macro.fire(this.json.events[ev].code, this, event);
        //     }
        // }.bind(this);
        if (!this.json.itemType || this.json.itemType==='values'){
            app.methods.$fetchSuggestions = function(qs, cb){
                if (this.json.itemValues){
                    var items = this.json.itemValues.filter(function(v){
                        return !qs || v.indexOf(qs)!=-1;
                    }).map(function(v){
                        return {"value": v};
                    });
                    cb(items);
                    //return items;
                }
                return [];
            }.bind(this);
        }else if(this.json.itemType==='script'){
            if (this.json.itemScript && this.json.itemScript.code){
                var fetchSuggestions = this.form.Macro.exec(this.json.itemScript.code, this);
                if (o2.typeOf(fetchSuggestions)==="function"){
                    app.methods.$fetchSuggestions = function(){
                        fetchSuggestions.apply(this, arguments);
                    }.bind(this);
                }
            }
        }else{
            var options;
            Promise.resolve(this.getOptions()).then(function (opt) {
                options = opt;
            });
            app.methods.$fetchSuggestions = function(qs, cb){
                if (options){
                    var items = options.filter(function(v){
                        return !qs || v.indexOf(qs)!=-1;
                    }).map(function(v){
                        return {"value": v};
                    });
                    cb(items);
                    //return items;
                }
                return [];
            }.bind(this);
        }
        // app.methods.$fetchSuggestions = function(qs, cb){
        //     if (this.json.itemType!=='script'){
        //         if (this.json.itemValues){
        //             var items = this.json.itemValues.filter(function(v){
        //                 return !qs || v.indexOf(qs)!=-1;
        //             }).map(function(v){
        //                 return {"value": v};
        //             });
        //             cb(items);
        //             return;
        //         }
        //         cb();
        //     }else{
        //         this.form.Macro.environment.queryString = qs;
        //         var list = this.form.Macro.exec(this.json.itemScript.code, this);
        //         Promise.resolve(list).then(function(items){
        //             cb(items);
        //             delete this.form.Macro.environment.queryString;
        //         }).catch(function(){
        //             cb();
        //             delete this.form.Macro.environment.queryString;
        //         });
        //     }
        // }.bind(this);
    },
    _createElementHtml: function(){
        var html = "<el-autocomplete";
        html += " v-model=\""+this.json.$id+"\"";
        html += " :placement=\"placement\"";
        html += " :popper-class=\"popperClass\"";
        html += " :trigger-on-focus=\"triggerOnFocus\"";
        html += " :prefix-icon=\"prefixIcon\"";
        html += " :suffix-icon=\"suffixIcon\"";
        html += " :placeholder=\"description\"";
        html += " :fetch-suggestions=\"$fetchSuggestions\"";
        html += " :hide-loading=\"false\"";
        html += " :popper-append-to-body=\"false\"";

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

        if (this.json.vueSlot) html += this.json.vueSlot;

        html += "</el-autocomplete>";
        return html;
    }
}); 
