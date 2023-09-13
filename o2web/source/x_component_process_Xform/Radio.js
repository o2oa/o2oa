MWF.xDesktop.requireApp("process.Xform", "$Selector", null, false);
MWF.require("MWF.widget.UUID", null, false);
/** @class Radio 单选按钮。
 * @o2cn 单选按钮
 * @example
 * //可以在脚本中获取该组件
 * //方法1：
 * var field = this.form.get("fieldId"); //获取组件对象
 * //方法2
 * var field = this.target; //在组件本身的脚本中获取，比如事件脚本、默认值脚本、校验脚本等等
 *
 * var data = field.getData(); //获取值
 * field.setData("字符串值"); //设置值
 * field.hide(); //隐藏字段
 * var id = field.json.id; //获取字段标识
 * var flag = field.isEmpty(); //字段是否为空
 * @extends MWF.xApplication.process.Xform.$Selector
 * @o2category FormComponents
 * @o2range {Process|CMS|Portal}
 * @hideconstructor
 */
MWF.xApplication.process.Xform.Radio = MWF.APPRadio =  new Class(
    /** @lends MWF.xApplication.process.Xform.Radio# */
    {
	Implements: [Events],
	Extends: MWF.APP$Selector,
        /**
         * 组件加载后触发。如果选项加载为异步，则异步处理完成后触发此事件
         * @event MWF.xApplication.process.Xform.Radio#load
         * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
         */


        /**
         * 值改变时触发。可以通过this.event获取修改后的选择项（Dom对象）。
         * @event MWF.xApplication.process.Xform.Radio#change
         * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
         */

    /**
     * @ignore
     * @member {Element} descriptionNode
     * @memberOf MWF.xApplication.process.Xform.Radio#
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
    _loadNodeRead: function(){
        this.node.empty();
        this.node.set({
            "nodeId": this.json.id,
            "MWFType": this.json.type
        });
        var value = this.getValue();
        this._showValue(this.node, value);
    },
    __showValue: function(node, value, optionItems){
        if (value){
            var texts = "";
            for (var i=0; i<optionItems.length; i++){
                var item = optionItems[i];
                var tmps = item.split("|");
                var t = tmps[0];
                var v = tmps[1] || t;

                if (value == v){
                    texts = t;
                    break;
                }
            }
            node.set("text", texts);
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
			"MWFType": this.json.type
		});
		if( this.json.recoveryStyles && this.json.recoveryStyles.display ){
            this.node.setStyle("display", this.json.recoveryStyles.display);
		}else if( this.json.styles && this.json.styles.display ){
            this.node.setStyle("display", this.json.styles.display);
        }else{
            this.node.setStyle("display", "inline");
        }
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
        return parseInt(d).toString() !== "NaN"
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
                        if( countPerLine === 0 && i===0 ){
                            node = new Element("div").inject(this.node);
                        }else if( i % countPerLine === 0){
                            node = new Element("div").inject(this.node);
                        }
                    }

                    var radio = new Element("input", {
                        "type": "radio",
                        "name": (this.json.properties && this.json.properties.name) ? this.json.properties.name : flag+this.json.id,
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
                        this.radio.checked = true;
                        this.radio.fireEvent("change", [this.radio]);
                        this.radio.fireEvent("click");
                    }.bind( {radio : radio} ) );

                    radio.addEvent("click", function(){
                        this.validationMode();
                        if (this.validation()) {
                            this._setBusinessData(this.getInputData("change"));
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
        }.bind(this), function(){
            this.moduleSelectAG = null;
        }.bind(this));
        this.moduleSelectAG = p;
        if (p) p.then(function(){
            this.moduleSelectAG = null;
        }.bind(this), function(){
            this.moduleSelectAG = null;
        }.bind(this));

        // this.moduleSelectAG = o2.AG.all(optionItems).then(function(radioValues){
        //     this.moduleSelectAG = null;
        //
        //     if (!radioValues) radioValues = [];
        //     if (o2.typeOf(radioValues)==="array"){
        //         var flag = (new MWF.widget.UUID).toString();
        //         radioValues.each(function(item){
        //             var tmps = item.split("|");
        //             var text = tmps[0];
        //             var value = tmps[1] || text;
        //
        //             var radio = new Element("input", {
        //                 "type": "radio",
        //                 "name": (this.json.properties && this.json.properties.name) ? this.json.properties.name : flag+this.json.id,
        //                 "value": value,
        //                 "showText": text,
        //                 "styles": this.json.buttonStyles
        //             }).inject(this.node);
        //             //radio.appendText(text, "after");
        //
        //             var textNode = new Element( "span", {
        //                 "text" : text,
        //                 "styles" : { "cursor" : "default" }
        //             }).inject(this.node);
        //             textNode.addEvent("click", function( ev ){
        //                 if( this.radio.get("disabled") === true || this.radio.get("disabled") === "true" )return;
        //                 this.radio.checked = true;
        //                 this.radio.fireEvent("change");
        //                 this.radio.fireEvent("click");
        //             }.bind( {radio : radio} ) );
        //
        //             radio.addEvent("click", function(){
        //                 this.validationMode();
        //                 if (this.validation()) this._setBusinessData(this.getInputData("change"));
        //             }.bind(this));
        //
        //             Object.each(this.json.events, function(e, key){
        //                 if (e.code){
        //                     if (this.options.moduleEvents.indexOf(key)!=-1){
        //                     }else{
        //                         radio.addEvent(key, function(event){
        //                             return this.form.Macro.fire(e.code, this, event);
        //                         }.bind(this));
        //                     }
        //                 }
        //             }.bind(this));
        //         }.bind(this));
        //     }
        // }.bind(this))
        // if (this.moduleSelectAG) this.moduleSelectAG.then(function(){
        //     this.moduleSelectAG = null;
        // }.bind(this));
	},

    _setValue: function(value, m, fireChange){
        var mothed = m || "__setValue";
	    if (!!value){
            var p = o2.promiseAll(value).then(function(v){
                if (o2.typeOf(v)=="array") v = v[0];
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
        //     if (o2.typeOf(v)=="array") v = v[0];
        //     if (this.moduleSelectAG){
        //         this.moduleValueAG = this.moduleSelectAG;
        //         this.moduleSelectAG.then(function(){
        //             this.__setValue(v);
        //         }.bind(this));
        //     }else{
        //         this.__setValue(v)
        //     }
        //     return v;
        // }.bind(this));
        //
        // if (this.moduleValueAG) this.moduleValueAG.then(function(){
        //     this.moduleValueAG = null;
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
                if (radio.value==value){
                    radio.checked = true;
                    break;
                }
            }
        }
        this.fieldModuleLoaded = true;
	},

	_getInputTextData: function(){
		var inputs = this.node.getElements("input");
		var value = "";
		var text = "";
		if (inputs.length){
			for (var i=0; i<inputs.length; i++){
				var input = inputs[i];
				if (input.checked){
					value = input.get("value");
					text = input.get("showText");
					break;
				}
			}
		}
		return {"value": [value || ""] , "text": [text || value || ""]};
	},
    /**
     * @summary 获取选中项的text。
     * @return {String} 返回选中项的text
     * @example
     * var text = this.form.get('fieldId').getText(); //获取选中项的文本
     */
    getText: function(){
		var d = this.getTextData();
		if( typeOf(d.then) === "function" ){
			return d.then(function( d1 ){
				var texts = d1.text;
				return (texts && texts.length) ? texts[0] : "";
			})
		}else{
			var texts = d.text;
			return (texts && texts.length) ? texts[0] : "";
		}
	},
    getInputData: function(){
        if (this.isReadonly()){
            return this._getBusinessData();
        }else{
            var inputs = this.node.getElements("input");
            var value = "";
            if (inputs.length){
                for (var i=0; i<inputs.length; i++){
                    var input = inputs[i];
                    if (input.checked){
                        value = input.get("value");
                        break;
                    }
                }
            }
            return value;
        }
	},
    resetData: function(){
        this.setData(this.getValue());
    },

    /**
     * @summary 获取选中的Dom对象。
     * @return {Element} 返回选中的Dom对象
     * @example
     * var input = this.form.get('fieldId').getSelectedInput();
     */
    getSelectedInput: function(){
        var inputs = this.node.getElements("input");
        if (inputs.length){
            for (var i=0; i<inputs.length; i++){
                if (inputs[i].checked) return inputs[i];
            }
        }
        return null;
    },

    setData: function(data, fireChange){
        return this._setValue(data, "__setData", fireChange);
        // if (data && data.isAG){
        //     this.moduleValueAG = o2.AG.all(data).then(function(v){
        //         if (o2.typeOf(v)=="array") v = v[0];
        //         this.__setData(v);
        //     }.bind(this));
        // }else{
        //     this.__setData(data);
        // }

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
			for (var i=0; i<inputs.length; i++){
				if (data==inputs[i].get("value")){
					inputs[i].set("checked", true);
				}else{
					inputs[i].set("checked", false);
				}
			}
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

    validationMode: function(routeName, opinion){
        // if (!this.validationConfig(routeName, opinion))  return false;

        if (this.isNotValidationMode){
            this.isNotValidationMode = false;
            this.node.setStyles(this.node.retrieve("background"));
            if (this.errNode){
                this.errNode.destroy();
                this.errNode = null;
            }
        }
    },

    getExcelData: function( type ){
		var value = this.getData();
		if( type === "value" )return value;

		var options = this.getOptionsObj();
		return Promise.resolve(options).then(function (opts) {
			var idx = opts.valueList.indexOf( value );
			var text = idx > -1 ? opts.textList[ idx ] : "";
			return text;
		});
	},
    setExcelData: function(d, type){
		var value = d.replace(/&#10;/g,""); //换行符&#10;
		this.excelData = value;
		if( type === "value" ){
			this.setData(value, true);
		}else{
            var options = this.getOptionsObj();
            this.moduleExcelAG = Promise.resolve(options).then(function (opts) {
                var idx = opts.textList.indexOf( value );
                value = idx > -1 ? opts.valueList[ idx ] : "";
                this.setData(value, true);
                this.moduleExcelAG = null;
            }.bind(this));
	    }
	}
	
}); 
