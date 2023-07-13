MWF.xDesktop.requireApp("process.Xform", "$Module", null, false);
/** @class $Input 组件类，此类为所有输入组件的父类
 * @hideconstructor
 * @o2category FormComponents
* @extends MWF.xApplication.process.Xform.$Module
 * @abstract
 */
MWF.xApplication.process.Xform.$Input = MWF.APP$Input =  new Class(
    /** @lends MWF.xApplication.process.Xform.$Input# */
    {
	Implements: [Events],
	Extends: MWF.APP$Module,
	iconStyle: "personfieldIcon",
    options: {
        "moduleEvents": ["change", "load", "queryLoad", "postLoad"]
    },
    initialize: function(node, json, form, options){
        this.node = $(node);
        this.node.store("module", this);
        this.json = json;
        this.form = form;
        this.field = true;
        this.fieldModuleLoaded = false;
        this.nodeHtml = this.node.get("html");
    },
	_loadUserInterface: function(){
        if ( this.isSectionMergeRead() ) { //区段合并显示
            this._loadMergeReadNode();
        }else{
            if( this.isSectionMergeEdit() ){
                this._loadMergeEditNode();
            }else{
                this._loadNode();
            }
            if (this.json.compute === "show"){
                this._setValue(this._computeValue());
            }else{
                this._loadValue();
            }
        }
	},
    _loadDomEvents: function(){
        Object.each(this.json.events, function(e, key){
            if (e.code){
                if (this.options.moduleEvents.indexOf(key)===-1){
                    (this.node.getFirst() || this.node).addEvent(key, function(event){
                        return this.form.Macro.fire(e.code, this, event);
                    }.bind(this));
                }
            }
        }.bind(this));
    },
    _loadEvents: function(){
        Object.each(this.json.events, function(e, key){
            if (e.code){
                if (this.options.moduleEvents.indexOf(key)!==-1){
                    this.addEvent(key, function(event){
                        return this.form.Macro.fire(e.code, this, event);
                    }.bind(this));
                }else{
                    (this.node.getFirst() || this.node).addEvent(key, function(event){
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
            (this.node.getFirst() || this.node).addEvent(key, function(event){
                return (fun) ? fun(this, event) : null;
            }.bind(this));
        }
    },
    /**
     * @summary 重新加载组件。会执行postLoad事件。
     * @example
     * this.form.get("fieldId").reload(); //重新加载事件
     */
    reload: function(){
        if( this.iconNode ){
            this.iconNode.destroy();
            this.iconNode = null;
        }
        if( this.descriptionNode ){
            this.descriptionNode.destroy();
            this.descriptionNode = null;
        }
        this.node.empty();
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
    _loadNodeRead: function(){
        this.node.empty();
        this.node.set({
            "nodeId": this.json.id,
            "MWFType": this.json.type
        });
    },
    loadDescription: function(){
        if (this.isReadonly())return;
        var v = this._getBusinessData();
        if (!v){
            if (this.json.description){
                var size, w;
                if( this.node.offsetParent === null ){ //隐藏
                    size = { y: 26 }
                }else{
                    size = this.node.getFirst().getSize();
                    w = size.x-3;
                    if( this.hasIcon() ){
                        if (COMMON.Browser.safari) w = w-20;
                    }
                }

                /**
                 * @summary 描述信息节点，允许用户手工输入的组件才有此节点，只读情况下无此节点.
                 * @member {Element}
                 */
                this.descriptionNode = new Element("div", {"styles": this.form.css.descriptionNode, "text": this.json.description}).inject(this.node);
                this.descriptionNode.setStyles({
                    "height": ""+size.y+"px",
                    "line-height": ""+size.y+"px"
                });
                if( w )this.descriptionNode.setStyles({
                    "width": ""+w+"px"
                });
                this.setDescriptionEvent();
            }
        }
    },
    setDescriptionEvent: function(){
        if (this.descriptionNode){
            this.descriptionNode.addEvents({
                "mousedown": function(){
                    this.descriptionNode.setStyle("display", "none");
                    this.clickSelect();
                }.bind(this)
            });
            this.node.getFirst().addEvents({
                "focus": function(){
                    if (this.descriptionNode) this.descriptionNode.setStyle("display", "none");
                }.bind(this),
                "blur": function(){
                    if (!this.node.getFirst().get("value")) if (this.descriptionNode)  this.descriptionNode.setStyle("display", "block");
                }.bind(this)
            });
        }
    },
    checkDescription: function(){
        if (!this.node.getFirst().get("value")){
            if (this.descriptionNode)  this.descriptionNode.setStyle("display", "block");
        }else{
            if (this.descriptionNode) this.descriptionNode.setStyle("display", "none");
        }
    },
    _resetNodeEdit: function(){
        var input = new Element("input", {
            "styles": {
                "background": "transparent",
                "width": "100%",
                "border": "0px"
            },
            "readonly": true
        });

        var node = new Element("div", {"styles": {
                "overflow": (this.json.styles && this.json.styles.overflow) ? this.json.styles.overflow : "hidden",
                "position": "relative",
                "margin-right": this.hasIcon() ? "20px" : "0px",
                "padding-right": "4px"
            }}).inject(this.node, "after");
        input.inject(node);

        this.node.destroy();
        this.node = node;
    },
    hasIcon: function(){
	    return this.json.showIcon!='no' && ( !this.form.json || !this.form.json.hideModuleIcon );
    },
    _loadNodeEdit: function(){
	    if (!this.json.preprocessing) this._resetNodeEdit();
	    var input = this.node.getFirst();
        if( !input && this.nodeHtml ){
            this.node.set("html", this.nodeHtml);
            input = this.node.getFirst();
        }
        input.set(this.json.properties);
		this.node.set({
			"id": this.json.id,
			"MWFType": this.json.type,
			"readonly": true,
			"events": {
				"click": this.clickSelect.bind(this)
			}
		});
        if ( this.hasIcon() ){
            this.iconNode = new Element("div", {
                "styles": this.form.css[this.iconStyle],
                "events": {
                    "click": this.clickSelect.bind(this)
                }
            }).inject(this.node, "before");
        }else if( this.form.json.nodeStyleWithhideModuleIcon ){
            this.node.setStyles(this.form.json.nodeStyleWithhideModuleIcon)
        }

        this.node.getFirst().addEvent("change", function(){
            this.validationMode();
            if (this.validation()) {
                this._setBusinessData(this.getInputData("change"));
                this.fireEvent("change");
            }
        }.bind(this));
        //
        // var inputNode = this.node.getFirst();
        // if (inputNode) inputNode.addEvent("input", function(e){
        //     var v=e.target.get("value");
        //     this._setEnvironmentData(v);
        // }.bind(this));
	},
    _loadStyles: function(){
        if (this.json.styles) this.node.setStyles(this.json.styles);
        if (this.json.inputStyles) if (this.node.getFirst()) this.node.getFirst().setStyles(this.json.inputStyles);
        if (this.iconNode && this.iconNode.offsetParent !== null){
            var size = this.node.getSize();
            //if (!size.y){
            //    var y1 = this.node.getStyle("height");
            //    var y2 = this.node.getFirst().getStyle("height");
            //    alert(y1+"," +y2);
            //    var y = ((y1!="auto" && y1>y2) || y2=="auto") ? y1 : y2;
            //    size.y = (y=="auto") ? "auto" : y.toInt();
            //    //alert(size.y)
            //}
            this.iconNode.setStyle("height", ""+size.y+"px");
            //alert(this.iconNode.getStyle("height"))
        }
    },
    _computeValue: function(value){
        return (this.json.defaultValue && this.json.defaultValue.code) ? this.form.Macro.exec(this.json.defaultValue.code, this): (value || "");
    },
	getValue: function(){
        if (this.moduleValueAG) return this.moduleValueAG;
        var value = this._getBusinessData();
        if (!value) value = this._computeValue();
		return value || "";
	},
    _setValue: function(value){
	    // if (value && value.isAG){
	    //     var ag = o2.AG.all(value).then(function(v){
	    //         if (o2.typeOf(v)=="array") v = v[0];
        //         this.__setValue(v);
        //     }.bind(this));
        //     this.moduleValueAG = ag;
	    //     ag.then(function(){
        //         this.moduleValueAG = null;
        //     }.bind(this));
        // }else {
        if (!!value && o2.typeOf(value.then)=="function"){
            var p = Promise.resolve(value).then(function(v){
                this.__setValue(v);
            }.bind(this), function(){});
            this.moduleValueAG = p;
        }else{
            this.moduleValueAG = null;
            this.__setValue(value);
        }

            //this.__setValue(value);
        // }

    },
    __setValue: function(value){
        this.moduleValueAG = null;
        this._setBusinessData(value);
        if (this.node.getFirst()) this.node.getFirst().set("value", value || "");
        if (this.isReadonly()) this.node.set("text", value);
        this.moduleValueAG = null;
        this.fieldModuleLoaded = true;
        return value;
    },

	_loadValue: function(){
        this._setValue(this.getValue());
	},
	clickSelect: function(){
	},
	_afterLoaded: function(){
//		if (this.iconNode){
////			var p = this.node.getPosition();
////			var s = this.node.getSize();
////			var is = this.iconNode.getSize();
////
////			var y = p.y;
////			var x = p.x+s.x-is.x;
//			this.iconNode.setStyles({
//				"top": "5px",
//				"left": "-18px"
//			});
//		}
        if (!this.isReadonly()){
            this.loadDescription();
        }
	},
    _beforeReloaded: function(){},
    _afterReloaded: function(){},
    /**
     * @summary 判断组件是否只读.
     * @example
     * var readonly = this.form.get('subject').isReadonly();
     * @return {Boolean} 是否只读.
     */
	isReadonly : function(){
        return !!(this.readonly || this.json.isReadonly || this.form.json.isReadonly || this.isSectionMergeRead());
    },
	getTextData: function(){
		//var value = this.node.get("value");
		//var text = this.node.get("text");
        var value = (this.node.getFirst()) ? this.node.getFirst().get("value") : this.node.get("text");
        var text = (this.node.getFirst()) ? this.node.getFirst().get("text") : this.node.get("text");
		return {"value": [value || ""] , "text": [text || value || ""]};
	},
    /**
     * @summary 判断组件值是否为空.
     * @example
     * if( this.form.get('subject').isEmpty() ){
     *     this.form.notice('标题不能为空', 'warn');
     * }
     * @return {Boolean} 值是否为空.
     */
    isEmpty : function(){
	    var data = this.getData();
	    return !data || !data.trim();
    },
    /**
     * 在脚本中使用 this.data[fieldId] 也可以获取组件值。
     * 区别如下：<br/>
     * 1、当使用Promise的时候<br/>
     * 使用异步函数生成器（Promise）为组件赋值的时候，用getData方法立即获取数据，可能返回修改前的值，当Promise执行完成以后，会返回修改后的值。<br/>
     * this.data[fieldId] 立即获取数据，可能获取到异步函数生成器，当Promise执行完成以后，会返回修改后的值。<br/>
     * {@link https://www.yuque.com/o2oa/ixsnyt/ws07m0#EggIl|具体差异请查看链接}<br/>
     * 2、当表单上没有对应组件的时候，可以使用this.data[fieldId]获取值，但是this.form.get('fieldId')无法获取到组件。
     * @summary 获取组件值。
     * @example
     * var data = this.form.get('fieldId').getData(); //没有使用promise的情况、
     * @example
     *  //如果无法确定表单上是否有组件，需要判断
     *  var data;
     *  if( this.form.get('fieldId') ){ //判断表单是否有无对应组件
     *      data = this.form.get('fieldId').getData();
     *  }else{
     *      data = this.data['fieldId']; //直接从数据中获取字段值
     *  }
     * @example
     *  //使用Promise的情况
     *  var field = this.form.get("fieldId");
     *  var dict = new this.Dict("test"); //test为数据字典名称
     *  var promise = dict.get("tools", true); //异步使用数据字典的get方法时返回Promise，参数true表示异步
     *  promise.then( function(){
     *      var data = field.getData(); //此时由于异步请求已经执行完毕，getData方法获取到了数据字典的值
     *  })
     *  field.setData( promise );
     * @return 组件的数据.
     */
	getData: function(when){
        if (this.json.compute == "save") this._setValue(this._computeValue());
        return this.getInputData();
	},
    getInputData: function(){
        if (this.node.getFirst()){
            return this.node.getFirst().get("value");
        }else{
            return this._getBusinessData();
        }
    },
    // /**
    //  * @summary 重置组件的值为默认值或置空。
    //  *  @example
    //  * this.form.get('subject').resetData();
    //  */
    resetData: function(){
        this.setData(this.getValue());
    },
    /**当参数为Promise的时候，请参考文档: {@link  https://www.yuque.com/o2oa/ixsnyt/ws07m0|使用Promise处理表单异步}<br/>
     * 当表单上没有对应组件的时候，可以使用this.data.add(fieldId, data, true) 赋值。
     * @summary 为组件赋值。
     * @param data{String|Promise} .
     * @param fireChange{boolean} 可选，是否触发change事件，默认false.
     * @example
     *  this.form.get("fieldId").setData("test"); //赋文本值
     * @example
     *  //如果无法确定表单上是否有组件，需要判断
     *  if( this.form.get('fieldId') ){ //判断表单是否有无对应组件
     *      this.form.get('fieldId').setData( data );
     *  }else{
     *      this.data.add(fieldId, data, true);
     *  }
     * @example
     *  //使用Promise
     *  var field = this.form.get("fieldId");
     *  var dict = new this.Dict("test"); //test为数据字典名称
     *  var promise = dict.get("tools", true); //异步使用数据字典的get方法时返回Promise，参数true表示异步
     *  field.setData( promise );
     */
	setData: function(data, fireChange){
        // if (data && data.isAG){
        //     var ag = o2.AG.all(data).then(function(v){
        //         if (o2.typeOf(v)=="array") v = v[0];
        //         this.__setData(v);
        //     }.bind(this));
        //     this.moduleValueAG = ag;
        //     ag.then(function(){
        //         this.moduleValueAG = null;
        //     }.bind(this));
        // }else{
        if (!!data && o2.typeOf(data.then)=="function"){
            var p = o2.promiseAll(data).then(function(v){
                this.__setData(v, fireChange);
                // if (this.node.getFirst() && !this.readonly && !this.json.isReadonly) {
                //     this.checkDescription();
                //     this.validationMode();
                // }
            }.bind(this), function(){});
            this.moduleValueAG = p;
            p.then(function(){
                this.moduleValueAG = null;
            }.bind(this), function(){
                this.moduleValueAG = null;
            }.bind(this));
        }else{
            this.moduleValueAG = null;
            this.__setData(data, fireChange);
            // if (this.node.getFirst() && !this.readonly && !this.json.isReadonly) {
            //     this.checkDescription();
            //     this.validationMode();
            // }
        }
            //this.__setData(data);
        //}
	},
    __setData: function(data, fireChange){
	    var old = this.getInputData();
        this._setBusinessData(data);
        if (this.node.getFirst()){
            this.node.getFirst().set("value", data);
            this.checkDescription();
            this.validationMode();
        }else{
            this.node.set("text", data);
        }
        if (fireChange && old!==data) this.fireEvent("change");
        this.moduleValueAG = null;
    },


    createErrorNode: function(text){

        //var size = this.node.getFirst().getSize();
        //var w = size.x-3;
        //if (COMMON.Browser.safari) w = w-20;
        //node.setStyles({
        //    "width": ""+w+"px",
        //    "height": ""+size.y+"px",
        //    "line-height": ""+size.y+"px",
        //    "position": "absolute",
        //    "top": "0px"
        //});
        var node;
        if( this.form.json.errorStyle ){
            if( this.form.json.errorStyle.type === "notice" ){
                if( !this.form.errorNoticing ){ //如果是弹出
                    this.form.errorNoticing = true;
                    this.form.notice(text, "error", this.node, null, null, {
                        onClose : function () {
                            this.form.errorNoticing = false;
                        }.bind(this)
                    });
                }
            }else{
                node = new Element("div",{
                    "styles" : this.form.json.errorStyle.node,
                    "text": text
                });
                if( this.form.json.errorStyle.close ){
                    var closeNode = new Element("div",{
                        "styles" : this.form.json.errorStyle.close ,
                        "events": {
                            "click" : function(){
                                //this.destroy();
                                this.validationMode();
                            }.bind(this)
                        }
                    }).inject(node);
                }
            }
        }else{
            node = new Element("div");
            var iconNode = new Element("div", {
                "styles": {
                    "width": "20px",
                    "height": "20px",
                    "float": "left",
                    "background": "url("+"../x_component_process_Xform/$Form/default/icon/error.png) center center no-repeat"
                }
            }).inject(node);
            var textNode = new Element("div", {
                "styles": {
                    "height": "20px",
                    "line-height": "20px",
                    "margin-left": "20px",
                    "color": "red",
                    "word-break": "keep-all"
                },
                "text": text
            }).inject(node);
        }
        return node;
    },
    notValidationMode: function(text){
        if (!this.isNotValidationMode){
            this.isNotValidationMode = true;
            this.node.store("borderStyle", this.node.getStyles("border-left", "border-right", "border-top", "border-bottom"));
            this.node.setStyle("border-color", "red");

            this.errNode = this.createErrorNode(text);
            //if (this.iconNode){
            //    this.errNode.inject(this.iconNode, "after");
            //}else{
                this.errNode.inject(this.node, "after");
            //}
            this.showNotValidationMode(this.node);

            var parentNode = this.errNode;
            while( parentNode && parentNode.offsetParent === null ){
                parentNode = parentNode.getParent();
            }

            if ( parentNode && !parentNode.isIntoView()) parentNode.scrollIntoView(false);
        }
    },
    showNotValidationMode: function(node){
        var p = node.getParent("div");
        if (p){
            var mwftype = p.get("MWFtype") || p.get("mwftype");
            if (mwftype == "tab$Content"){
                if (p.getParent("div").getStyle("display")=="none"){
                    var contentAreaNode = p.getParent("div").getParent("div");
                    var tabAreaNode = contentAreaNode.getPrevious("div");
                    var idx = contentAreaNode.getChildren().indexOf(p.getParent("div"));
                    var tabNode = tabAreaNode.getLast().getFirst().getChildren()[idx];
                    tabNode.click();
                    p = tabAreaNode.getParent("div");
                }
            }
            this.showNotValidationMode(p);
        }
    },
    validationMode: function(){
        if (this.isNotValidationMode){
            this.isNotValidationMode = false;
            this.node.setStyles(this.node.retrieve("borderStyle"));
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
    validationConfig: function(routeName, opinion){
        if (this.json.validationConfig){
            if (this.json.validationConfig.length){
                for (var i=0; i<this.json.validationConfig.length; i++) {
                    var data = this.json.validationConfig[i];
                    if (!this.validationConfigItem(routeName, data)) return false;
                }
            }
            return true;
        }
        return true;
    },

    getExcelData: function(){
        return this.getData();
    },
    setExcelData: function(data){
	    if( typeOf(data) === "string" )data = data.replace(/&#10;/g,""); //excel字段换行是 &#10
        this.excelData = data;
        this.setData(data, true);
    },
    stringToArray: function(string){ //excel字段换行是 &#10;，换行和逗号作为数组分隔符
        return string.replace(/&#10;/g,",").split(/\s*,\s*/g ).filter(function(s){
            return !!s;
        });
    },
    validationExcel: function () {
        if (!this.isReadonly()){
            var errorList = this.validationConfigExcel();
            if (errorList.length) return errorList;

            if (!this.json.validation) return [];
            if (!this.json.validation.code) return [];

            var flag = this.form.Macro.exec(this.json.validation.code, this);

            if (!flag) flag = MWF.xApplication.process.Xform.LP.notValidation;
            if (flag.toString() !== "true") {
                return [flag];
            }
        }
        return [];
    },
    validationConfigExcel: function () {
	    var errorList = [];
        if (this.json.validationConfig){
            if (this.json.validationConfig.length){
                for (var i=0; i<this.json.validationConfig.length; i++) {
                    var flag = this.validationConfigItemExcel(this.json.validationConfig[i]);
                    if ( flag !== true ){
                        errorList.push( flag );
                    }
                }
            }
        }
        return errorList;
    },
    validationConfigItemExcel: function(data){
        if ( data.status==="all"){
            var n = this._getBusinessData();
            var ed = typeOf( this.excelData ) === "null" ? "" : this.excelData;
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
                    if (!v){
                        return !ev ? data.prompt : "不在选项中";
                    }
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
                    if (v===data.value)return data.prompt;
                    break;
                case "neq":
                    if (v!==data.value)return data.prompt;
                    break;
                case "contain":
                    if (v.indexOf(data.value)!==-1) return data.prompt;
                    break;
                case "notcontain":
                    if (v.indexOf(data.value)===-1)return data.prompt;
                    break;
            }
        }
        return true;
    }
}); 
