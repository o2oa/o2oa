MWF.xDesktop.requireApp("process.Xform", "$Input", null, false);
MWF.require("MWF.widget.UUID", null, false);
/** @class Calendar 多选按钮组件。
 * @example
 * //可以在脚本中获取该组件
 * //方法1：
 * var field = this.form.get("fieldId"); //获取组件对象
 * //方法2
 * var field = this.target; //在组件本身的脚本中获取，比如事件脚本、默认值脚本、校验脚本等等
 * @extends MWF.xApplication.process.Xform.$Input
 * @o2category FormComponents
 * @o2range {Process|CMS|Portal}
 * @hideconstructor
 */
MWF.xApplication.process.Xform.Checkbox = MWF.APPCheckbox =  new Class(
    /** @lends MWF.xApplication.process.Xform.Checkbox# */
    {
	Implements: [Events],
	Extends: MWF.APP$Input,

    loadDescription: function(){},
    _loadNode: function(){
        if (this.readonly || this.json.isReadonly ){
            this._loadNodeRead();
        }else{
            this._loadNodeEdit();
        }
    },
    _loadNodeRead: function(){
        this.node.empty();
        this.node.set({
            "nodeId": this.json.id,
            "MWFType": this.json.type
        });
        var radioValues = this.getOptions();
        var value = this.getValue();
        if (value){
            var texts = [];
            radioValues.each(function(item){
                var tmps = item.split("|");
                var t = tmps[0];
                var v = tmps[1] || t;

                if (value.indexOf(v)!=-1){
                    texts.push(t);
                }
            });
            this.node.set("text", texts.join(", "));
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
    /**
     * @summary 重新计算下拉选项，该功能通常用在下拉选项为动态计算的情况.
     * @example
     * this.form.get('fieldId').resetOption();
     */
    resetOption: function(){
        this.node.empty();
        this.setOptions();
    },
        /**
         * @summary 获取选择项。
         * @return {Array} 返回选择项数组，如果使用选择项脚本，根据脚本返回决定，如：<pre><code class='language-js'>[
         *  "女|female",
         *  "男|male"
         * ]</code></pre>
         * @example
         * this.form.get('fieldId').getOptions();
         */
	getOptions: function(){
		if (this.json.itemType == "values"){
			return this.json.itemValues;
		}else{
			return this.form.Macro.exec(((this.json.itemScript) ? this.json.itemScript.code : ""), this);
		}
		//return [];
	},

    /**
     * @summary 获取整理后的选择项。
     * @return {Object} 返回整理后的选择项，如：
     * <pre><code class='language-js'>{"valueList": ["","female","male"], "textList": ["","女","男"]}
     * </code></pre>
     * @example
     * var optionData = this.form.get('fieldId').getOptionsObj();
     */
    getOptionsObj : function(){
        var textList = [];
        var valueList = [];
        var optionItems = this.getOptions();
        if (!optionItems) optionItems = [];
        if (o2.typeOf(optionItems)==="array"){
            optionItems.each(function(item){
                var tmps = item.split("|");
                textList.push( tmps[0] );
                valueList.push( tmps[1] || tmps[0] );
            }.bind(this));
        }
        return { textList : textList, valueList : valueList };
    },

    setOptions: function(){
        var optionItems = this.getOptions();
        this._setOptions(optionItems);
    },

    _setOptions: function(optionItems){
        var p = o2.promiseAll(optionItems).then(function(radioValues){
            this.moduleSelectAG = null;
            if (!radioValues) radioValues = [];
            if (o2.typeOf(radioValues)==="array"){
                var flag = (new MWF.widget.UUID).toString();
                radioValues.each(function(item){
                    var tmps = item.split("|");
                    var text = tmps[0];
                    var value = tmps[1] || text;

                    var radio = new Element("input", {
                        "type": "checkbox",
                        "name": ((this.json.properties) ? this.json.properties.name : null) || flag+this.json.id,
                        "value": value,
                        "showText": text,
                        "styles": this.json.buttonStyles
                    }).inject(this.node);
                    //radio.appendText(text, "after");

                    var textNode = new Element( "span", {
                        "text" : text,
                        "styles" : { "cursor" : "default" }
                    }).inject(this.node);
                    textNode.addEvent("click", function( ev ){
                        if( this.radio.get("disabled") === true || this.radio.get("disabled") === "true" )return;
                        this.radio.checked = ! this.radio.checked;
                        this.radio.fireEvent("change");
                        this.radio.fireEvent("click");
                    }.bind( {radio : radio} ) );

                    radio.addEvent("click", function(){
                        this.validationMode();
                        if (this.validation()) {
                            this._setBusinessData(this.getInputData("change") || []);
                            this.fireEvent("change");
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
        this._setBusinessData(value);
        var radios = this.node.getElements("input");
        for (var i=0; i<radios.length; i++){
            var radio = radios[i];
            radio.checked = value.indexOf(radio.value) != -1;
        }
    },
    /**
     * @summary 获取选中的值和文本.
     * @example
     * var array = this.form.get('fieldId').getTextData();
     * @return {Object} 返回选中项值和文本，格式为 { 'value' : value, 'text' : text }.
     */
	getTextData: function(){
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
        if (this.readonly || this.json.isReadonly ){
            return this._getBusinessData();
        }else{
            var inputs = this.node.getElements("input");
            var value = [];
            if (inputs.length){
                inputs.each(function(input){
                    if (input.checked){
                        var v = input.get("value");
                        if (v) value.push(v || "");
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
     *  @example
     *  this.form.get("fieldId").setData("test"); //赋文本值
     *  @example
     *  //使用Promise
     *  var field = this.form.get("fieldId");
     *  var dict = new this.Dict("test"); //test为数据字典名称
     *  var promise = dict.get("tools", true); //异步使用数据字典的get方法时返回Promise，参数true表示异步
     *  field.setData( promise );
     */
    setData: function(data){
	    return this._setValue(data, "__setData");
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

    __setData: function(data){
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
        this.fireEvent("setData");
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

            if (!this.node.isIntoView()) this.node.scrollIntoView();
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
    }
	
}); 
