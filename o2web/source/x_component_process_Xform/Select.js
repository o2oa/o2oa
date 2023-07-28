MWF.xDesktop.requireApp("process.Xform", "$Selector", null, false);
/** @class Select 下拉选择组件。
 * 在8.1之后，支持从数据字典、视图和查询获取可选项。获取过程为异步。
 * @o2cn 下拉选择
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
MWF.xApplication.process.Xform.Select = MWF.APPSelect =  new Class(
	/** @lends MWF.xApplication.process.Xform.Select# */
	{
	Implements: [Events],
	Extends: MWF.APP$Selector,
	iconStyle: "selectIcon",

		/**
		 * 值改变时触发。可以通过this.event获取修改后的选择项（Dom对象）。
		 * @event MWF.xApplication.process.Xform.Select#change
		 * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
		 */

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
		this.fieldModuleLoaded = false;
		this.nodeHtml = this.node.get("html");
    },
	/**
	 * @summary 重新加载组件。会执行postLoad事件。
	 * @example
	 * this.form.get("fieldId").reload(); //重新加载事件
	 */
	reload: function(){
		if (this.areaNode){
			this.node = this.areaNode;
			this.areaNode.empty();
			this.areaNode = null;
		}
		this._beforeReloaded();
		this._loadUserInterface();
		this._loadStyles();
		this._afterLoaded();
		this._afterReloaded();
		this.fireEvent("postLoad");
	},
    _loadNode: function(){
        if (this.isReadonly()){
            this._loadNodeRead();
        }else{
            this._loadNodeEdit();
        }
    },
	_loadMergeReadContentNode: function( contentNode, data ){
		this._showValue(contentNode, data.data);
	},
    _loadNodeRead: function(){
        this.node.empty();
		this.node.set({
			"nodeId": this.json.id,
			"MWFType": this.json.type
		});
        var value = this.getValue();
        this._showValue( this.node, value );
    },
	__showValue: function(node, value, optionItems){
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
            node.set("text", texts.join(", "));
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
		if( !select && this.nodeHtml ){
			this.node.set("html", this.nodeHtml);
			select = this.node.getFirst();
		}

		this.areaNode = this.node;
		this.areaNode.set({
			"id": this.json.id,
			"MWFType": this.json.type
		});
		this.node = select;

		this.node.set({
			"styles": {
				"margin-right": "12px"
			}
		});
		// this.node.set({
		// 	"id": this.json.id,
		// 	"MWFType": this.json.type,
		// 	"styles": {
		// 		"margin-right": "12px"
		// 	}
		// });
		
		this.setOptions();
        this.node.addEvent("change", function( ev ){
			var v = this.getInputData("change");
			this._setBusinessData(v);
            this.validationMode();
            if (this.validation()) {
				//this._setEnvironmentData(v);
				this.fireEvent("change", [this._getSelectedOption()]);
			}
        }.bind(this));

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
		if (!this.isReadonly()) {
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
		this.fieldModuleLoaded = true;
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

	_getSelectedOption: function(){
		var ops = this.node.getElements("option");
		for( var i=0; i<ops.length; i++ ){
			if( ops[i].selected )return ops[i];
		}
		return null;
	},
	_getInputTextData: function(){
	    var value = [], text = [];
        var ops = this.node.getElements("option");
        ops.each(function(op){
            if (op.selected){
                var v = op.get("value");
                var t = op.get("text");
                value.push(v || "");
                text.push(t || v || "");
            }
        });
        if (!value.length) value = [""];
        if (!text.length) text = [""];
        return {"value": value, "text": text};
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
		if( this.isReadonly()){
			return this._getBusinessData();
		}else{
			var ops = this.node.getElements("option");
			var value = [];
			ops.each(function(op){
				if (op.selected){
					var v = op.get("value");
					value.push(v || "");
				}
			});
			if (!value.length) return null;
			return (value.length==1) ? value[0] : value;
		}
	},
    resetData: function(){
        this.setData(this.getValue());
    },

	setData: function(data, fireChange){
		return this._setValue(data, "__setData", fireChange);
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

	__setData: function(data, fireChange){
		var old = this.getInputData();
        this._setBusinessData(data);
        var selectedOption = null;
		if (this.isReadonly()){
			var d = typeOf(data) === "array" ? data : [data];
			var ops = this.getOptionsObj();
			var result = [];
			if( typeOf(ops.then) === "function" ){
                this.moduleSelectAG = Promise.resolve(ops).then(function(){
                    d.each( function (v) {
                        var idx = ops.valueList.indexOf( v );
                        result.push( idx > -1 ? ops.textList[idx] : v);
                    })
                    this.node.set("text", result.join(","));
                }.bind(this))
			}else{
			    d.each( function (v) {
                    var idx = ops.valueList.indexOf( v );
                    result.push( idx > -1 ? ops.textList[idx] : v);
                })
                this.node.set("text", result.join(","));
			}
		}else{
			var ops = this.node.getElements("option");
			ops.each(function(op){
				if (typeOf(data)==="array"){
					if (data.indexOf(op.get("value"))!=-1){
						op.set("selected", true);
						selectedOption = op;
					}else{
						op.set("selected", false);
					}
				}else{
					if (data == op.get("value")){
						op.set("selected", true);
						selectedOption = op;
					}else{
						op.set("selected", false);
					}
				}
			});
			this.validationMode();
		}
		this.fieldModuleLoaded = true;
		this.fireEvent("setData", [data]);
		if (fireChange && old!==data) this.fireEvent("change", [selectedOption]);
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
