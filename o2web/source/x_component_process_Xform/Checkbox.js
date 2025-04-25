MWF.xDesktop.requireApp("process.Xform", "$Selector", null, false);
MWF.require("MWF.widget.UUID", null, false);
/** @class Calendar 多选按钮组件。
 * @o2cn 多选按钮
 * @example
 * //可以在脚本中获取该组件
 * //方法1：
 * var field = this.form.get("fieldId"); //获取组件对象
 * //方法2
 * var field = this.target; //在组件本身的脚本中获取，比如事件脚本、默认值脚本、校验脚本等等
 * @extends MWF.xApplication.process.Xform.$Selector
 * @o2category FormComponents
 * @o2range {Process|CMS|Portal}
 * @hideconstructor
 */
MWF.xApplication.process.Xform.Checkbox = MWF.APPCheckbox =  new Class(
    /** @lends MWF.xApplication.process.Xform.Checkbox# */
    {
        Implements: [Events],
        Extends: MWF.APP$Selector,
        /**
         * 组件加载后触发。如果选项加载为异步，则异步处理完成后触发此事件
         * @event MWF.xApplication.process.Xform.Checkbox#load
         * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
         */
        /**
         * 值改变时触发。可以通过this.event获取修改后的选择项（Dom对象）。
         * @event MWF.xApplication.process.Xform.Checkbox#change
         * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
         */
        loadDescription: function(){},
        _loadNode: function(){
            if (this.isReadonly()){
                this._loadNodeRead();
            }else{
                this._loadNodeEdit();
            }
        },
        _loadMergeReadContentNode: function( contentNode, data ){
            this._showValue(contentNode, data.data)
        },
        _loadMergeEditNodeByDefault: function(){
            var data = this.getSortedSectionData();
            var businessData = [];
            data.each(function(d){
                businessData = businessData.concat( d.data || [] );
            });
            this._setBusinessData( businessData );
            this._loadNode();
        },
        _loadNodeRead: function(){
            this.node.empty();
            this.node.set({
                "nodeId": this.json.id,
                "MWFType": this.json.type
            });
            var value = this.getValue();
            this._showValue(this.node, value)
        },
        __showValue: function(node, value, optionItems){
            if( !value )return;
            var texts = [];
            optionItems.each(function(item){
                var tmps = item.split("|");
                var t = tmps[0];
                var v = tmps[1] || t;

                if (value.indexOf(v)!=-1){
                    texts.push(t);
                }
            });
            if( !this.isNumber(this.json.countPerline) ) {
                if( this.json.newline ){
                    texts.each(function(t){
                        new Element("div", { "text": t }).inject(node)
                    }.bind(this))
                }else{
                    node.set("text", texts.join(", "));
                }
            }else{
                var div;
                var countPerLine = this.json.countPerline.toInt();
                if( countPerLine === 0 ){
                    div = new Element("div", {"style":"display:inline-block;"}).inject( node );
                    div.set("text", texts.join(", "));
                }else{
                    var textsPerLine = [];
                    texts.each(function(t, i){
                        if( i % countPerLine === 0){ //如果需要换行了
                            if( div && textsPerLine.length )div.set("text", textsPerLine.join(",") +",");
                            textsPerLine = [];
                            div = new Element("div").inject( node );
                        }
                        textsPerLine.push( t );
                    }.bind(this));
                    if( div && textsPerLine.length )div.set("text", textsPerLine.join(","));
                }
            }
        },
        _resetNodeEdit: function(){
            var div = new Element("div");
            div.set(this.json.properties);
            div.inject(this.node, "after");

            this.node.destroy();
            this.node = div;
        },
        _loadNodeEdit: function(){
            //this.container = new Element("select");
            if (!this.json.preprocessing) this._resetNodeEdit();
            this.node.set({
                "id": this.json.id,
                "MWFType": this.json.type,
                "styles": {
                    "display": "inline"
                }
            });
            if( this.json.newline )this.node.setStyle("display", "block");
            this.setOptions();
        },
        _loadDomEvents: function(){
        },
        _loadEvents: function(){
            Object.each(this.json.events, function(e, key){
                if (e.code){
                    if (this.options.moduleEvents.indexOf(key)!=-1){
                        this.addEvent(key, function(event){
                            return this.form.Macro.fire(e.code, this, event);
                        }.bind(this));
                    }else{
                        //this.node.addEvent(key, function(event){
                        //    return this.form.Macro.fire(e.code, this, event);
                        //}.bind(this));
                    }
                }
            }.bind(this));
        },
        addModuleEvent: function(key, fun){
            if (this.options.moduleEvents.indexOf(key)!==-1){
                this.addEvent(key, function(event){
                    return (fun) ? fun(this, event) : null;
                }.bind(this));
            }else{
                var inputs = this.node.getElements("input");
                inputs.each(function(input){
                    input.addEvent(key, function(event){
                        return (fun) ? fun(this, event) : null;
                    }.bind(this));
                }.bind(this));
            }
        },

        isNumber : function( d ){
            return parseInt(d).toString() !== "NaN";
        },
        _setOptions: function(optionItems){
            var p = o2.promiseAll(optionItems).then(function(radioValues){
                this.moduleSelectAG = null;
                if (!radioValues) radioValues = [];
                var node;
                if (o2.typeOf(radioValues)==="array"){
                    var flag = (new MWF.widget.UUID).toString();
                    radioValues.each(function(item, i){
                        var tmps = item.split("|");
                        var text = tmps[0];
                        var value = tmps[1] || text;

                        if( !this.isNumber(this.json.countPerline) ) {
                            if( this.json.newline ){
                                node = new Element("div").inject(this.node);
                            }else{
                                node = this.node;
                            }
                        }else{
                            var countPerLine = this.json.countPerline.toInt();
                            if( countPerLine === 0 ){
                                if(i===0)node = new Element("div", {"style":"display:inline-block;"}).inject(this.node);
                            }else if( i % countPerLine === 0){
                                node = new Element("div").inject(this.node);
                            }
                        }

                        var radio = new Element("input", {
                            "type": "checkbox",
                            "name": ((this.json.properties) ? this.json.properties.name : null) || flag+this.json.id,
                            "value": value,
                            "showText": text,
                            "styles": this.json.buttonStyles
                        }).inject(node);
                        //radio.appendText(text, "after");

                        var textNode = new Element( "span", {
                            "text" : text,
                            "styles" : { "cursor" : "default" }
                        }).inject(node);
                        textNode.addEvent("click", function( ev ){
                            if( this.radio.get("disabled") === true || this.radio.get("disabled") === "true" )return;
                            this.radio.checked = ! this.radio.checked;
                            this.radio.fireEvent("change", [this.radio]);
                            this.radio.fireEvent("click");
                        }.bind( {radio : radio} ) );

                        radio.addEvent("click", function(){
                            this.validationMode();
                            if (this.validation()) {
                                var v = this.getInputData("change");
                                this._setBusinessData(v || []);
                                //this._setEnvironmentData(v || []);
                                //this._setBusinessData(this.getInputData("change") || []);
                                this.fireEvent("change", [radio]);
                            }
                        }.bind(this));

                        Object.each(this.json.events, function(e, key){
                            if (e.code){
                                if (this.options.moduleEvents.indexOf(key)!=-1){
                                }else{
                                    radio.addEvent(key, function(event){
                                        return this.form.Macro.fire(e.code, this, event);
                                    }.bind(this));
                                }
                            }
                        }.bind(this));

                    }.bind(this));
                }
            }.bind(this), function(){});
            this.moduleSelectAG = p;
            if (p) p.then(function(){
                this.moduleSelectAG = null;
            }.bind(this), function(){
                this.moduleSelectAG = null;
            }.bind(this));
        },

        _setValue: function(value, m, fireChange){
            var mothed = m || "__setValue";
            if (!!value){
                var p = o2.promiseAll(value).then(function(v){
                    //if (o2.typeOf(v)=="array") v = v[0];
                    if (this.moduleSelectAG){
                        this.moduleValueAG = this.moduleSelectAG;
                        this.moduleSelectAG.then(function(){
                            this[mothed](v, fireChange);
                            return v;
                        }.bind(this), function(){});
                    }else{
                        this[mothed](v, fireChange)
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
                this[mothed](value, fireChange);
            }


            // this.moduleValueAG = o2.AG.all(value).then(function(v){
            //     if (this.moduleSelectAG){
            //         this.moduleValueAG = this.moduleSelectAG;
            //         this.moduleSelectAG.then(function(){
            //             this.moduleValueAG = null;
            //             this.__setValue(v);
            //         }.bind(this));
            //     }else{
            //         this.moduleValueAG = null;
            //         this.__setValue(v);
            //     }
            //     return v;
            // }.bind(this));
            //
            // if (this.moduleValueAG) this.moduleValueAG.then(function(){
            //     this.moduleValueAG = "";
            // }.bind(this));
        },

        __setValue: function(value){
            this.moduleValueAG = null;
            this._setBusinessData(value);
            if (this.isReadonly()){
                this._loadNodeRead();
            }else{
                var radios = this.node.getElements("input");
                for (var i=0; i<radios.length; i++){
                    var radio = radios[i];
                    radio.checked = value.indexOf(radio.value) != -1;
                }
            }
            this.fieldModuleLoaded = true;
        },

        _getInputTextData: function(){
            var inputs = this.node.getElements("input");
            var value = [];
            var text = [];
            if (inputs.length){
                inputs.each(function(input){
                    if (input.checked){
                        var v = input.get("value");
                        var t = input.get("showText");
                        value.push(v || "");
                        text.push(t || v || "");
                    }
                });
            }
            if (!value.length) value = [""];
            if (!text.length) text = [""];
            return {"value": value, "text": text};
        },
        /**
         * @summary 获取选中项的text，如果未选中返回空数组。
         * @return {Array} 返回选中项的text数组
         * @example
         * var array = this.form.get('fieldId').getText(); //获取选中项的数组
         */
        getText: function(){
            var texts = this.getTextData().text;
            if( texts && texts.length === 1 ){
                return texts[0] ? texts : [];
            }else{
                return (texts && texts.length) ? texts : [];
            }
        },
        //getData: function(){
        //var inputs = this.node.getElements("input");
        //var value = [];
        //if (inputs.length){
        //	inputs.each(function(input){
        //		if (input.checked){
        //			var v = input.get("value");
        //			if (v) value.push(v || "");
        //		}
        //	});
        //}
        //return (value.length==1) ? value[0] : value;
        //},
        isEmpty: function(){
            var data = this.getData();
            if( typeOf(data) !== "array" )return true;
            if( data.length === 0 )return true;
            return false;
        },
        getInputData: function(){
            if (this.isReadonly()){
                return this._getBusinessData();
            }else{
                var inputs = this.node.getElements("input");
                var value = [];
                if (inputs.length){
                    inputs.each(function(input){
                        if (input.checked){
                            var v = input.get("value");
                            value.push(v || "");
                        }
                    });
                }
                return (value.length) ? value : [];
            }
        },
        resetData: function(){
            this.setData(this.getValue());
        },
        /**当参数为Promise的时候，请查看文档: {@link  https://www.yuque.com/o2oa/ixsnyt/ws07m0|使用Promise处理表单异步}
         * @summary 为字段赋值，并且使值对应的选项选中。
         *  @param data{String|Promise} .
         *  @param fireChange{boolean} 可选，是否触发change事件，默认false.
         *  @example
         *  this.form.get("fieldId").setData("test"); //赋文本值
         *  @example
         *  //使用Promise
         *  var field = this.form.get("fieldId");
         *  var dict = new this.Dict("test"); //test为数据字典名称
         *  var promise = dict.get("tools", true); //异步使用数据字典的get方法时返回Promise，参数true表示异步
         *  field.setData( promise );
         */
        setData: function(data, fireChange){
            return this._setValue(data, "__setData", fireChange);
            // if (data && data.isAG){
            //     this.moduleValueAG = data;
            //     data.addResolve(function(v){
            //         this.setData(v);
            //     }.bind(this));
            // }else{
            //     this.__setData(data);
            //     this.moduleValueAG = null;
            // }
        },

        __setData: function(data, fireChange){
            this.moduleValueAG = null;
            var old = this.getInputData();
            this._setBusinessData(data);

            var inputs = this.node.getElements("input");
            if (inputs.length){
                inputs.each(function(input){
                    if (typeOf(data)=="array"){
                        if (data.indexOf(input.get("value"))!=-1){
                            input.set("checked", true);
                        }else{
                            input.set("checked", false);
                        }
                    }else{
                        if (data == input.get("value")){
                            input.set("checked", true);
                        }else{
                            input.set("checked", false);
                        }
                    }
                });
                this.validationMode();
            }
            this.fieldModuleLoaded = true;
            this.fireEvent("setData");
            if (fireChange && old!==data) this.fireEvent("change");
        },

        notValidationMode: function(text){
            if (!this.isNotValidationMode){
                this.isNotValidationMode = true;
                this.node.store("background", this.node.getStyles("background"));
                this.node.setStyle("background", "#ffdcdc");

                this.errNode = this.createErrorNode(text);
                if (this.iconNode){
                    this.errNode.inject(this.iconNode, "after");
                }else{
                    this.errNode.inject(this.node, "after");
                }
                this.showNotValidationMode(this.node);

                if (!this.errNode.isIntoView()) this.errNode.scrollIntoView(false);
            }
        },
        validationMode: function(){
            if (this.isNotValidationMode){
                this.isNotValidationMode = false;
                this.node.setStyles(this.node.retrieve("background"));
                if (this.errNode){
                    this.errNode.destroy();
                    this.errNode = null;
                }
            }
        },
        validationConfigItem: function(routeName, data){
            var flag = (data.status==="all") ? true: (routeName === data.decision);
            if (flag){
                var n = this.getInputData();
                if( typeOf(n)==="array" && n.length === 0 )n = "";
                var v = (data.valueType==="value") ? n : n.length;
                switch (data.operateor){
                    case "isnull":
                        if (!v){
                            this.notValidationMode(data.prompt);
                            return false;
                        }
                        break;
                    case "notnull":
                        if (v){
                            this.notValidationMode(data.prompt);
                            return false;
                        }
                        break;
                    case "gt":
                        if (v>data.value){
                            this.notValidationMode(data.prompt);
                            return false;
                        }
                        break;
                    case "lt":
                        if (v<data.value){
                            this.notValidationMode(data.prompt);
                            return false;
                        }
                        break;
                    case "equal":
                        if (v==data.value){
                            this.notValidationMode(data.prompt);
                            return false;
                        }
                        break;
                    case "neq":
                        if (v!=data.value){
                            this.notValidationMode(data.prompt);
                            return false;
                        }
                        break;
                    case "contain":
                        if (v.indexOf(data.value)!=-1){
                            this.notValidationMode(data.prompt);
                            return false;
                        }
                        break;
                    case "notcontain":
                        if (v.indexOf(data.value)==-1){
                            this.notValidationMode(data.prompt);
                            return false;
                        }
                        break;
                }
            }
            return true;
        },


        getExcelData: function( type ){
            var value = this.getData();
		    if( type === "value" )return value;

            var options = this.getOptionsObj();
            return Promise.resolve(options).then(function (opts) {
                value = o2.typeOf(value) === "array" ? value : [value];
                var arr = [];
                value.each( function( a, i ){
                    var idx = options.valueList.indexOf( a );
                    arr.push( idx > -1 ? options.textList[ idx ] : "") ;
                });
                return arr.join(", ");
            });
        },
        setExcelData: function(d, type){
            var arr = this.stringToArray(d);
            this.excelData = arr;
            if( type === "value" ){
                this.setData(arr, true);
            }else{
                var options = this.getOptionsObj();
                this.moduleExcelAG = Promise.resolve(options).then(function (opts) {
                    arr.each( function( a, i ){
                        var idx = opts.textList.indexOf( a );
                        arr[ i ] = idx > -1 ? opts.valueList[ idx ] : null;
                    });
                    arr.clean();
                    var value = arr.length === 1  ? arr[0] : arr;
                    this.setData(value, true);
                    this.moduleExcelAG = null;
                }.bind(this));
            }
        },
        validationConfigItemExcel: function(data){
            if (data.status==="all"){
                var n = this.getInputData();
                if( typeOf(n)==="array" && n.length === 0 )n = "";

                var ed = typeOf( this.excelData ) === "null" ? [] : this.excelData;
                if( typeOf(ed)==="array" && ed.length === 0 )ed = "";

                var v, ev;
                if(data.valueType==="value"){
                    v = n;
                    ev = ed;
                }else{
                    v = n.length;
                    ev = ed.length || 0;
                }
                switch (data.operateor){
                    case "isnull":
                        if (!v)return !ev ? data.prompt : "不在选项中";
                        break;
                    case "notnull":
                        if (v)return data.prompt;
                        break;
                    case "gt":
                        if (v>data.value)return data.prompt;
                        break;
                    case "lt":
                        if (v<data.value)return data.prompt;
                        break;
                    case "equal":
                        if (v==data.value)return data.prompt;
                        break;
                    case "neq":
                        if (v!=data.value)return data.prompt;
                        break;
                    case "contain":
                        if (v.indexOf(data.value)!=-1)return data.prompt;
                        break;
                    case "notcontain":
                        if (v.indexOf(data.value)==-1)return data.prompt;
                        break;
                }
            }
            return true;
        }

    });
