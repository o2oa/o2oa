o2.xDesktop.requireApp("process.Xform", "$Elinput", null, false);
MWF.xDesktop.requireApp("process.Xform", "$Selector", null, false);

if( !o2.APP$ElSelector ){
    o2.xApplication.process.Xform.$ElSelector = o2.APP$ElSelector = new Class({
        Implements: [Events],
        Extends: MWF.APP$Elinput
    });
    Object.assign(o2.APP$ElSelector.prototype, o2.APP$Selector.prototype);
}

/** @class Elselect 基于Element UI的选择框组de件。
 * @o2cn 选择框
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
 * @see {@link https://element.eleme.cn/#/zh-CN/component/select|Element UI Select 选择器}
 */
MWF.xApplication.process.Xform.Elselect = MWF.APPElselect =  new Class(
    /** @lends o2.xApplication.process.Xform.Elselect# */
    {
    Implements: [Events],
    Extends: MWF.APP$ElSelector,
    options: {
        /**
         * 组件加载后触发。如果选项加载为异步，则异步处理完成后触发此事件
         * @event MWF.xApplication.process.Xform.Elselect#load
         */
        "moduleEvents": ["load", "queryLoad", "postLoad"],
        /**
         * 当 input 获得焦点时触发。this.event[0]指向Event
         * @event MWF.xApplication.process.Xform.Elselect#focus
         * @see {@link https://element.eleme.cn/#/zh-CN/component/select|选择器的Select Events章节}
         */
        /**
         * 当 input 失去焦点时触发。this.event[0]指向Event
         * @event MWF.xApplication.process.Xform.Elselect#blur
         * @see {@link https://element.eleme.cn/#/zh-CN/component/select|选择器的Select Events章节}
         */
        /**
         * 选中值发生变化时触发。this.event[0]为组件目前的选中值
         * @event MWF.xApplication.process.Xform.Elselect#change
         * @see {@link https://element.eleme.cn/#/zh-CN/component/select|选择器的Select Events章节}
         */
        /**
         * 下拉框出现/隐藏时触发。this.event[0]的值出现则为 true，隐藏则为 false
         * @event MWF.xApplication.process.Xform.Elselect#visible-change
         * @see {@link https://element.eleme.cn/#/zh-CN/component/select|选择器的Select Events章节}
         */
        /**
         * 多选模式下移除tag时触发。this.event[0]为移除的tag值
         * @event MWF.xApplication.process.Xform.Elselect#remove-tag
         * @see {@link https://element.eleme.cn/#/zh-CN/component/select|选择器的Select Events章节}
         */
        /**
         * 可清空的单选模式下用户点击清空按钮时触发。
         * @event MWF.xApplication.process.Xform.Elselect#clear
         * @see {@link https://element.eleme.cn/#/zh-CN/component/select|选择器的Select Events章节}
         */
        "elEvents": ["focus", "blur", "change", "visible-change", "remove-tag", "clear"]
    },
    // _loadNode: function(){
    //     if (this.isReadonly()) this.json.disabled = true;
    //     this._loadNodeEdit();
    // },
    _loadMergeReadContentNode: function( contentNode, data ){
        this._loadOptions();
        Promise.resolve( this.json.options || this.moduleSelectAG ).then(function(options){
            var values = (o2.typeOf(data.data) !== "array") ? [data.data] : data.data;
            var text = this.__getOptionsText(options, values);
            contentNode.set("text", text.join(","));
        }.bind(this));
    },
    resetOption: function(){
        this.reload();
    },
    _appendVueData: function(){
        // this.form.Macro.environment.data.check(this.json.id);
        // this.json[this.json.id] = this._getBusinessData();

        if (!this.json.clearable) this.json.clearable = false;
        if (!this.json.size) this.json.size = "";
        if (!this.json.multiple) this.json.multiple = false;
        if (!this.json.collapseTags) this.json.collapseTags = false;
        if (!this.json.filterable) this.json.filterable = false;
        if (!this.json.allowCreate) this.json.allowCreate = false;
        if (!this.json.remote) this.json.remote = false;
        if (!this.json.popperClass) this.json.popperClass = "";
        if (!this.json.multipleLimit || !this.json.multipleLimit.toInt()) this.json.multipleLimit = 0;
        if (!this.json.noMatchText) this.json.noMatchText = "";
        if (!this.json.noDataText) this.json.noDataText = "";
        if (!this.json.loadingText) this.json.loadingText = "";
        if (!this.json.description) this.json.description = "";
        if (!this.json.disabled) this.json.disabled = false;
        if (!this.json.loading) this.json.loading = false;
        if (!this.json.options) this.json.options = [];

        this._loadOptions();

        if (this.json.multiple===true) if (!this.json[this.json.$id] || !this.json[this.json.$id].length) this.json[this.json.$id] = [];
    },
    appendVueMethods: function(methods){
        if (this.json.filterMethod && this.json.filterMethod.code){
            var fn = this.form.Macro.exec(this.json.filterMethod.code, this);
            methods.$filterMethod = function(){
                fn.apply(this, arguments);
            }.bind(this)
        }
        if (this.json.remoteMethod && this.json.remoteMethod.code){
            var fn = this.form.Macro.exec(this.json.remoteMethod.code, this);
            methods.$remoteMethod = function(){
                fn.apply(this, arguments);
            }.bind(this)
        }
    },

    _setOptionsWithCode: function(code){
        var v = this.form.Macro.exec(code, this);
        if (v.then){
            this.moduleSelectAG = v.then(function(o){
                if (o2.typeOf(o)==="array"){
                    this.json.options = o.map(function(item){
                        if (o2.typeOf(item)!=="object"){
                            var value = item.toString();
                            return {"value": value, "label": value};
                        }
                        return item;
                    });
                    this.json.$options = Array.clone(this.json.options);
                    return this.json.options;
                }
            }.bind(this));
        }else if (o2.typeOf(v)==="array"){
            this.json.options = v.map(function(item){
                if (o2.typeOf(item)!=="object"){
                    var value = item.toString();
                    return {"value": value, "label": value};
                }
                return item;
            });
            this.json.$options = Array.clone(this.json.options);
        }
    },
    _setOptionsWithArray: function( optionItems ){
        if (o2.typeOf(optionItems)==="array"){
            this.json.options = optionItems.map(function(item){
                if (item && o2.typeOf(item)!=="object"){
                    var value = item.toString();
                    var arr = value.split("|");
                    if (arr.length>1){
                        return {"label": arr[0], "value": arr[1]};
                    }else{
                        return {"label": arr[0], "value": arr[0]};
                    }
                }
                return item;
            });
            this.json.$options = Array.clone(this.json.options);
        }
    },
    _loadOptions: function(){
        if (this.json.itemsGroup===true){
            if (this.json.itemGroupScript && this.json.itemGroupScript.code)  this._setOptionsWithCode(this.json.itemGroupScript.code);
        }else{
            // if (this.json.itemType=="script"){
            //     if (this.json.itemScript && this.json.itemScript.code) this._setOptionsWithCode(this.json.itemScript.code);
            // }else{
            //     if (this.json.itemValues && (o2.typeOf(this.json.itemValues)==="array")){
            //         this.json.options = this.json.itemValues.map(function(item){
            //             if (item){
            //                 var arr = item.split("|");
            //                 if (arr.length>1){
            //                     return {"label": arr[0], "value": arr[1]};
            //                 }else{
            //                     return {"label": arr[0], "value": arr[0]};
            //                 }
            //             }
            //         });
            //     }
            // }
            var optionItems = this.getOptions();
            if( optionItems && typeOf(optionItems.then) === "function" ){
                this.moduleSelectAG = Promise.resolve(optionItems).then(function (optItems) {
                    this._setOptionsWithArray( optItems );
                    this.moduleSelectAG = false;
                    return this.json.options;
                }.bind(this));
            }else{
                this._setOptionsWithArray( optionItems );
            }

        }
    },
    _createElementHtml: function(){
        var html = "<el-select";
        html += " v-model=\""+this.json.$id+"\"";
        html += " :clearable=\"clearable\"";
        html += " :size=\"size\"";
        html += " :filterable=\"filterable\"";
        html += " :disabled=\"disabled\"";
        html += " :placeholder=\"description\"";
        html += " :multiple=\"multiple\"";
        html += " :collapse-tags=\"collapseTags\"";
        html += " :allow-create=\"allowCreate\"";
        html += " :remote=\"remote\"";
        html += " :popper-class=\"popperClass\"";
        html += " :multiple-limit=\"multipleLimit\"";
        html += " :no-match-text=\"noMatchText\"";
        html += " :no-data-text=\"noDataText\"";
        html += " :loading-text=\"loadingText\"";
        html += " :loading=\"loading\"";

        if (this.json.filterMethod && this.json.filterMethod.code){
            html += " :filter-method=\"$filterMethod\"";
        }
        if (this.json.remoteMethod && this.json.remoteMethod.code){
            html += " :remote-method=\"$remoteMethod\"";
        }

        this.options.elEvents.forEach(function(k){
            html += " @"+k+"=\"$loadElEvent_"+k.camelCase()+"\"";
        });

        if (this.json.elProperties){
            Object.keys(this.json.elProperties).forEach(function(k){
                if (this.json.elProperties[k]) html += " "+k+"=\""+this.json.elProperties[k]+"\"";
            }, this);
        }

        if (this.json.elStyles) html += " :style=\"elStyles\"";
        html += ">";

        var itemFor = "item in options";
        if (this.json.itemsGroup===true){
            html += "<el-option-group v-for=\"group in options\" :key=\"group.label\" :label=\"group.label\" :disabled=\"group.disabled\"";
            if (this.json.elGroupProperties){
                Object.keys(this.json.elGroupProperties).forEach(function(k){
                    if (this.json.elGroupProperties[k]) html += " "+k+"=\""+this.json.elGroupProperties[k]+"\"";
                }, this);
            }
            if (this.json.elGroupStyles) html += " :style=\"elGroupStyles\"";
            html += ">";

            itemFor = "item in group.options";
        }
        html += "<el-option v-for=\""+itemFor+"\" :key=\"item.value\" :label=\"item.label\" :value=\"item.value\" :disabled=\"item.disabled\"";
        if (this.json.elOptionProperties){
            Object.keys(this.json.elOptionProperties).forEach(function(k){
                if (this.json.elOptionProperties[k]) html += " "+k+"=\""+this.json.elOptionProperties[k]+"\"";
            }, this);
        }
        if (this.json.elOptionStyles) html += " :style=\"elOptionStyles\"";
        html += ">";
        if (this.json.customTemplete) html += this.json.customTemplete;
        html += "</el-option>";

        if (this.json.itemsGroup===true){
            html += "</el-option-group>"
        }

        if (this.json.vueSlot) html += this.json.vueSlot;

        html += "</el-select>";
        return html;
    },
    // _afterLoaded: function(){
    //     if (this.isReadonly()){
    //         this.node.hide();
    //         window.setTimeout(function(){
    //             var text = "";
    //             var nodes = this.node.getElements(".el-select__tags-text");
    //             if (nodes && nodes.length){
    //                 nodes.forEach(function(n){
    //                     text += ((text) ? ", " : "")+n.get("text");
    //                 });
    //             }
    //             var node = new Element("div").inject(this.node, "before");
    //             this.node.destroy();
    //             this.node = node;
    //             this.node.set({
    //                 "nodeId": this.json.id,
    //                 "MWFType": this.json.type
    //             });
    //             this._loadDomEvents();
    //             //this.node.removeEvents("click");
    //             //this.node.empty();
    //             this.node.set("text", text);
    //             //this.node.show();
    //         }.bind(this), 20);
    //
    //     }
    // },
        _createEventFunction: function(methods, k){
            methods["$loadElEvent_"+k.camelCase()] = function(){
                var flag = true;
                if (k==="change"){
                    if (this.validationMode){
                        this.validationMode();
                        this._setBusinessData(this.getInputData());
                        if( !this.validation() ) flag = false;
                    }
                }else if( this.json.filterable && ['visible-change','focus'].contains(k) ){
                    var input = this.node.getElement('.el-input__inner');
                    if( input ){
                        window.setTimeout(function(){
                            input.removeAttribute('readonly');
                        }, 200);
                    }
                }
                if (this.json.events && this.json.events[k] && this.json.events[k].code){
                    this.form.Macro.fire(this.json.events[k].code, this, arguments);
                }
                if( flag )this.fireEvent(k, arguments);
            }.bind(this);
        },
        _afterLoaded: function (){
            if( this.json.filterable ){
                var input = this.node.getElement('.el-input__inner');
                if( input ){
                    window.setTimeout(function (){
                        input.removeAttribute('readonly');
                    }, 200);
                }
            }
        },
    __setReadonly: function(data){
        if (this.isReadonly()){
            this._loadOptions();
            Promise.resolve(this.json.options || this.moduleSelectAG).then(function(options){
                var values = (o2.typeOf(data) !== "array") ? [data] : data;
                var text = this.__getOptionsText(options, values);
                this.node.set("text", text.join(","));

                this.fireEvent("load");
                this.isLoaded = true;
            }.bind(this));

            if( this.json.elProperties ){
                this.node.set(this.json.elProperties );
            }
            if (this.json.elStyles){
                this.node.setStyles( this._parseStyles(this.json.elStyles) );
            }
            this.fireEvent("postLoad");
        }
    },
    __getOptionsText: function(options, values){
        var text = [];
        options.forEach(function(op){
            if (op.value){
                if (values.indexOf(op.value)!=-1) text.push(op.label || op.value);
            }else if (op.options && op.options.length){
                text = text.concat(this.__getOptionsText(op.options, values));
            }
        }.bind(this));
        return text;
    },

        getDataByText: function(text){
            var opt = this.json.options;
            if( !opt )return "";
            if( o2.typeOf(opt.then)==="function" ){
                return Promise.resolve(opt).then(function(options){
                    return this._getDataByText(options, text);
                }.bind(this));
            }else{
                return this._getDataByText(opt, text);
            }
        },
        _getDataByText: function(options, texts){
            var value = [];
            options.forEach(function(op){
                if (op.value){
                    if (texts.indexOf(op.label || op.value)!=-1) value.push(op.value);
                }else if (op.options && op.options.length){
                    value = value.concat(this._getDataByText(op.options, texts));
                }
            }.bind(this));
            return value;
        },

        getExcelData: function(){
            var data = this.json[this.json.$id];
            if( !data )return "";
            if( !this.json.options )this._loadOptions();
            var text, opt = this.json.options;
            if( !opt )return "";
            if( o2.typeOf(opt.then)==="function" ){
                return Promise.resolve(opt).then(function(options){
                    text = this.__getOptionsText(options, data);
                    return typeOf(text) === "array" ? text.join(", ") : (text || "");
                }.bind(this));
            }else{
                text = this.__getOptionsText(opt, data);
                return typeOf(text) === "array" ? text.join(", ") : (text || "");
            }
        },
        setExcelData: function(d){
            this._loadOptions();
            this.moduleExcelAG = Promise.resolve( this.json.options || this.moduleSelectAG ).then(function(options){
                var arr = this.stringToArray(d);
                this.excelData = arr;
                arr = arr.map(function (a) {
                    return a.contains("/") ? a.split("/") : a;
                });
                var data = this.getDataByText( arr );
                this.setData( this.json.multiple ? data : data[0], true);
                this.moduleExcelAG = null;
            }.bind(this))
        }
}); 
