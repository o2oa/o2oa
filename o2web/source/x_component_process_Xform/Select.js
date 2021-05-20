MWF.xDesktop.requireApp("process.Xform", "$Input", null, false);
/** @class Select 下拉选择组件。
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
 * field.resetData();  //重置字段的值为默认值或置空
 * @extends MWF.xApplication.process.Xform.$Input
 * @o2category FormComponents
 * @o2range {Process|CMS|Portal}
 * @hideconstructor
 */
MWF.xApplication.process.Xform.Select = MWF.APPSelect =  new Class(
	/** @lends MWF.xApplication.process.Xform.Select# */
	{
	Implements: [Events],
	Extends: MWF.APP$Input,
	iconStyle: "selectIcon",

	/**
	 * @ignore
	 * @member {Element} descriptionNode
	 * @memberOf MWF.xApplication.process.Xform.Select#
	 */
    initialize: function(node, json, form, options){
        this.node = $(node);
        this.node.store("module", this);
        this.json = json;
        this.form = form;
        this.field = true;
    },
    _loadNode: function(){
        if (this.readonly|| this.json.isReadonly){
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
        var optionItems = this.getOptions();
        var value = this.getValue();
        if (value){
            if (typeOf(value)!=="array") value = [value];
            var texts = [];
            optionItems.each(function(item){
                var tmps = item.split("|");
                var t = tmps[0];
                var v = tmps[1] || t;

                if (v){

                    if (value.indexOf(v)!=-1){
                        texts.push(t);
                    }
                }

            });
            this.node.set("text", texts.join(", "));
        }
    },
	_loadDomEvents: function(){
		Object.each(this.json.events, function(e, key){
			if (e.code){
				if (this.options.moduleEvents.indexOf(key)===-1){
					this.node.addEvent(key, function(event){
						return this.form.Macro.fire(e.code, this, event);
					}.bind(this));
				}
			}
		}.bind(this));
	},
    _loadEvents: function(){
        Object.each(this.json.events, function(e, key){
            if (e.code){
                if (this.options.moduleEvents.indexOf(key)!=-1){
                    this.addEvent(key, function(event){
                        return this.form.Macro.fire(e.code, this, event);
                    }.bind(this));
                }else{
                    this.node.addEvent(key, function(event){
                        return this.form.Macro.fire(e.code, this, event);
                    }.bind(this));
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
			this.node.addEvent(key, function(event){
				return (fun) ? fun(this, event) : null;
			}.bind(this));
		}
	},
    _loadStyles: function(){
    	if (this.areaNode){
            if (this.json.styles) if (this.areaNode) this.areaNode.setStyles(this.json.styles);
            if (this.json.inputStyles) this.node.setStyles(this.json.inputStyles);
		}else{
            if (this.json.styles) this.node.setStyles(this.json.styles);
		}
    },
	_resetNodeEdit: function(){
		this.node.empty();
		var select = new Element("select");
		select.set(this.json.properties);
		select.inject(this.node);
	},
    _loadNodeEdit: function(){
		if (!this.json.preprocessing) this._resetNodeEdit();

		var select = this.node.getFirst();
		this.areaNode = this.node;
		this.node = select;

		this.node.set({
			"id": this.json.id,
			"MWFType": this.json.type,
			"styles": {
				"margin-right": "12px"
			}
		});
		
		this.setOptions();
        this.node.addEvent("change", function(){
			var v = this.getInputData("change");
			this._setBusinessData(v);
            this.validationMode();
            if (this.validation()) {
				this._setBusinessData(v);
				this.fireEvent("change");
			}
        }.bind(this));

	},
	/**
	 * @summary 刷新选择项，如果选择项是脚本，重新计算。
	 * @example
	 * this.form.get('fieldId').resetOption();
	 */
    resetOption: function(){
        this.node.empty();
        this.setOptions();
		this.fireEvent("resetOption")
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
		return [];
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
		var p = o2.promiseAll(optionItems).then(function(options){
			this.moduleSelectAG = null;
			if (!options) options = [];
			if (o2.typeOf(options)==="array"){
				options.each(function(item){
					var tmps = item.split("|");
					var text = tmps[0];
					var value = tmps[1] || text;

					var option = new Element("option", {
						"value": value,
						"text": text
					}).inject(this.node);
				}.bind(this));
				this.fireEvent("setOptions", [options])
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

		// this.moduleSelectAG = o2.AG.all(optionItems).then(function(options){
		// 	this.moduleSelectAG = null;
		// 	if (!options) options = [];
		// 	if (o2.typeOf(options)==="array"){
		// 		options.each(function(item){
		// 			var tmps = item.split("|");
		// 			var text = tmps[0];
		// 			var value = tmps[1] || text;
		//
		// 			var option = new Element("option", {
		// 				"value": value,
		// 				"text": text
		// 			}).inject(this.node);
		// 		}.bind(this));
		// 		this.fireEvent("setOptions", [options])
		// 	}
		// }.bind(this));
		// if (this.moduleSelectAG) this.moduleSelectAG.then(function(){
		// 	this.moduleSelectAG = null;
		// }.bind(this));
	},
	// __setOptions: function(){
	// 	var optionItems = this.getOptions();
    //     if (!optionItems) optionItems = [];
    //     if (o2.typeOf(optionItems)==="array"){
	// 		optionItems.each(function(item){
	// 			var tmps = item.split("|");
	// 			var text = tmps[0];
	// 			var value = tmps[1] || text;
	//
	// 			var option = new Element("option", {
	// 				"value": value,
	// 				"text": text
	// 			}).inject(this.node);
	// 		}.bind(this));
	// 		this.fireEvent("setOptions", [optionItems])
	// 	}
	// },
	addOption: function(text, value){
        var option = new Element("option", {
            "value": value || text,
            "text": text
        }).inject(this.node);
		this.fireEvent("addOption", [text, value])
	},

	_setValue: function(value, m){
		var mothed = m || "__setValue";
		if (!!value){
			var p = o2.promiseAll(value).then(function(v){
				if (o2.typeOf(v)=="array") v = v[0];
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
		// 	if (o2.typeOf(v)=="array") v = v[0];
		// 	if (this.moduleSelectAG){
		// 		this.moduleValueAG = this.moduleSelectAG;
		// 		this.moduleSelectAG.then(function(){
		// 			this.__setValue(v);
		// 		}.bind(this));
		// 	}else{
		// 		this.__setValue(v)
		// 	}
		// 	return v;
		// }.bind(this));

		// if (value && value.isAG){
		// 	this.moduleValueAG = o2.AG.all(value),then(function(v){
		// 		this._setValue(v);
		// 	}.bind(this));
		// 	// this.moduleValueAG = value;
		// 	// value.addResolve(function(v){
		// 	// 	this._setValue(v);
		// 	// }.bind(this));
		// }else{
		//
		// }
	},
	__setValue: function(value){
		if (!this.readonly && !this.json.isReadonly ) {
			this._setBusinessData(value);

			var ops = this.node.getElements("option");
			for (var i=0; i<ops.length; i++){
				var option = ops[i];
				if (option.value==value){
					option.selected = true;
					//	break;
				}else{
					option.selected = false;
				}
			}
		}
		this.moduleValueAG = null;
	},

	// _setValue: function(value){
	// 	if (!this.readonly && !this.json.isReadonly ) {
    //         this._setBusinessData(value);
    //         for (var i=0; i<this.node.options.length; i++){
    //             var option = this.node.options[i];
    //             if (option.value==value){
    //                 option.selected = true;
    //                 //	break;
    //             }else{
    //                 option.selected = false;
    //             }
    //         }
    //     }
	// 	//this.node.set("value", value);
	// },

	/**
	 * @summary 获取选中项的value和text。
	 * @return {Object} 返回选中项的value和text，如：
	 * <pre><code class='language-js'>{"value": ["male"], "text": ["男"]}
	 * {"value": [""], "text": [""]}
	 * </code></pre>
	 * @example
	 * var data = this.form.get('fieldId').getTextData();
	 * var text = data.text[0] //获取选中项的文本
	 */
	getTextData: function(){
		var value = [];
		var text = [];
		if (this.readonly|| this.json.isReadonly){
			var ops = this.getOptionsObj();
			var data = this._getBusinessData();
			var d = typeOf(data) === "array" ? data : [data];
			d.each( function (v) {
				var idx = ops.valueList.indexOf( v );
				value.push( v || "" );
				text.push( idx > -1 ? ops.textList[idx] : (v || "") );
			});
		}else{
			var ops = this.node.getElements("option");
			ops.each(function(op){
				if (op.selected){
					var v = op.get("value");
					var t = op.get("text");
					value.push(v || "");
					text.push(t || v || "");
				}
			});
		}
		if (!value.length) value = [""];
		if (!text.length) text = [""];
		return {"value": value, "text": text};
	},
    getInputData: function(){
		if( this.readonly || this.json.isReadonly ){
			return this._getBusinessData();
		}else{
			var ops = this.node.getElements("option");
			var value = [];
			ops.each(function(op){
				if (op.selected){
					var v = op.get("value");
					if (v) value.push(v);
				}
			});
			if (!value.length) return null;
			return (value.length==1) ? value[0] : value;
		}
	},
    resetData: function(){

        this.setData(this.getValue());
    },

	setData: function(data){
		return this._setValue(data, "__setData");
		// if (data && data.isAG){
		// 	this.moduleValueAG = o2.AG.all(data).then(function(v){
		// 		if (o2.typeOf(v)=="array") v = v[0];
		// 		this.__setData(v);
		// 	}.bind(this));
		// }else{
		// 	this.__setData(data);
		// }
		// if (data && data.isAG){
		// 	this.moduleValueAG = data;
		// 	data.addResolve(function(v){
		// 		this.setData(v);
		// 	}.bind(this));
		// }else{
		// 	this.__setData(data);
		// 	this.moduleValueAG = null;
		// }
	},

	__setData: function(data){
        this._setBusinessData(data);
		if (this.readonly|| this.json.isReadonly){
			var d = typeOf(data) === "array" ? data : [data];
			var ops = this.getOptionsObj();
			var result = [];
			d.each( function (v) {
				var idx = ops.valueList.indexOf( v );
				result.push( idx > -1 ? ops.textList[idx] : v);
			})
			this.node.set("text", result.join(","));
		}else{
			var ops = this.node.getElements("option");
			ops.each(function(op){
				if (typeOf(data)=="array"){
					if (data.indexOf(op.get("value"))!=-1){
						op.set("selected", true);
					}else{
						op.set("selected", false);
					}
				}else{
					if (data == op.get("value")){
						op.set("selected", true);
					}else{
						op.set("selected", false);
					}
				}
			});
			this.validationMode();
		}
		this.fireEvent("setData", [data]);
	}
	
}); 
