MWF.xDesktop.requireApp("process.Xform", "$Selector", null, false);
/** @class Combox 组合框组件。
 * @o2cn 组合框
 * @example
 * //可以在脚本中获取该组件
 * //方法1：
 * var field = this.form.get("fieldId"); //获取组件对象
 * //方法2
 * var field = this.target; //在组件本身的脚本中获取，比如事件脚本、默认值脚本、校验脚本等等
 * @extends MWF.xApplication.process.Xform.$Selector
 * @o2category FormComponents
 * @o2range {Process|CMS}
 * @hideconstructor
 */
MWF.xApplication.process.Xform.Combox = MWF.APPCombox =  new Class(
    /** @lends MWF.xApplication.process.Xform.Combox# */
{
	Implements: [Events],
	Extends: MWF.APP$Selector,
	iconStyle: "selectIcon",
    options: {
        /**
         * 手工输入完成后触发。
         * @event MWF.xApplication.process.Xform.Combox#commitInput
         * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
         */
        /**
         * 值改变时触发。
         * @event MWF.xApplication.process.Xform.Combox#change
         * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
         */
        "moduleEvents": ["load", "queryLoad", "postLoad", "commitInput", "change"]
    },

    initialize: function(node, json, form, options){
        this.node = $(node);
        this.node.store("module", this);
        this.json = json;
        this.form = form;
        this.field = true;
        this.fieldModuleLoaded = false;
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
            // if (this.json.compute === "show"){
            //     this._setValue(this._computeValue());
            // }else{
            //     this._loadValue();
            // }
        }
    },
    loadVal: function(){
        if (this.json.compute === "show"){
            this._setValue(this._computeValue());
        }else{
            this._loadValue();
        }
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
    _loadNodeRead: function(){
        this.node.empty();
        this.node.set({
            "nodeId": this.json.id,
            "MWFType": this.json.type
        });
        this.loadVal();
        //new Element("select").inject(this.node);
    },
    _loadMergeReadContentNode: function( contentNode, data ){
	    this.mergeRead = true;
        contentNode.setStyles({ "overflow": "hidden"});
		var textList = this.getTextListByValue( data.data );

        if( typeOf(textList.then) === "function" ){
            Promise.resolve(textList).then(function (tList) {
                this.__setValueRead( data.data, tList, contentNode );
            }.bind(this))
        }else{
            this.__setValueRead( data.data, textList, contentNode );
        }
        // textList.each(function(text, i){
        //     if (i<data.data.length-1) text += this.json.splitShow;
        //     new Element("div", {"styles": {
        //             "float": "left",
        //             "margin-right": "5px"
        //         },"text": text}).inject( contentNode ); //.inject(this.node.getFirst() || this.node);
        // }.bind(this));
    },
    _loadNodeEdit: function(){
        var options = this.getOptions();
        if( typeOf(options.then) === "function" ){
            Promise.resolve(options).then(function (opt) {
                this.__loadNodeEdit(opt);
                this.loadVal();
                this._loadStyles();
            }.bind(this))
        }else{
            this.__loadNodeEdit(options);
            this.loadVal();
        }
    },
    __loadNodeEdit: function(opt){
        this.node.empty();
        var select;
        MWF.require("MWF.widget.Combox", function(){
            select = this.combox = new MWF.widget.Combox({
                "style": this.form.json.comboxStyle ? this.form.json.comboxStyle.style : "default",
                "positionX": this.form.json.addressStyle ? this.form.json.addressStyle.positionX : "left",
                "onlySelect": this.json.onlySelect==="y",
                "count": this.json.count.toInt() || 0,
                "splitStr": this.json.splitStr || ",\\s*|;\\s*|，\\s*|；\\s*",
                "splitShow": this.json.splitShow || ", ",
                "focusList": this.json.showOptions === "focus",
                "list": opt,
                "onCommitInput": function(item){
                    this.fireEvent("commitInput");
                }.bind(this),
                "onChange": function(){
                    this.fireEvent("change");
                }.bind(this),
                "optionsMethod": this._searchOptions()
            });
        }.bind(this), false);


        // var select = new Element("select");
        // select.set(this.json.properties);
        select.inject(this.node);
        //this.node.destroy();
        // this.areaNode = this.node;
        // this.node = select;
        this.node.set({
            "id": this.json.id,
            "MWFType": this.json.type
        });

        this.combox.addEvent("change", function(){
            this.validationMode();
            if (this.validation()){
                var v = this.getInputData("change");
                this._setBusinessData(v);
            }
        }.bind(this));

    },
    _loadStyles: function(){
        if (this.json.styles) this.node.setStyles(this.json.styles);
        if (this.json.inputStyles && !this.mergeRead) if (this.node.getFirst()) this.node.getFirst().setStyles(this.json.inputStyles);
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
    _searchOptions: function(){
        if (this.json.itemType === "dynamic"){
			return function(value, callback){
				var event = {
					"value": value,
					"callback": callback
				};
                this.form.Macro.fire(((this.json.itemDynamic) ? this.json.itemDynamic.code : ""), this, event);
			}.bind(this);
		}else{
        	return null;
		}
	},
    /**
     * @summary 获取选择项。
     * @return {Array | Promise} 返回选择项数组或Promise，如：<pre><code class='language-js'>[
     *  "女|female",
     *  "男|male"
     * ]</code></pre>
     * @example
     * this.form.get('fieldId').getOptions();
     * @example
     * //异步
     * var opt = this.form.get('fieldId').getOptions();
     * Promise.resolve(opt).then(function(options){
     *     //options为选择项数组
     * })
     */
    getOptions: function(async, refresh){
        if( this.optionsCache && !refresh )return this.optionsCache;
        this.optionsCache = null;
        var opt = this._getOptions(async, refresh);
        if( (opt && typeOf(opt.then) === "function") ){
            var p = Promise.resolve( opt ).then(function(option){
                this.moduleSelectAG = null;
                this.optionsCache = this.parseOptions(option || []);
                return this.optionsCache;
            }.bind(this));
            this.moduleSelectAG = p;
            return p;
        }else{
            this.optionsCache = this.parseOptions(opt || []);
            return this.optionsCache;
        }
    },
    parseOptions: function(list){
    	// var list = [];
        // if (this.json.itemType === "values"){
        //     list = this.json.itemValues;
        // }else if (this.json.itemType === "script"){
        //     list = this.form.Macro.exec(((this.json.itemScript) ? this.json.itemScript.code : ""), this);
        // }

        if (list.length){
            var options = [];
            list.each(function(v){
                if (typeOf(v)==="object"){
                    options.push(v);
                }else{
                	v = v.toString();
                	var arr = v.split("|");
                	var o = { "text": "", "keyword": "", "value": "" };
                	switch (arr.length){
                        case 0: break;
						case 1:
                            o.text = arr[0];
                            o.keyword = arr[0];
                            o.value = arr[0];
							break;
						case 2:
                            o.text = arr[0];
                            o.keyword = arr[0];
                            o.value = arr[1];
							break;
						case 3:
                            o.text = arr[0];
                            o.keyword = arr[1];
                            o.value = arr[2];
							break;
						default:
                            o.text = arr[0];
                            o.keyword = arr[1];
                            o.value = arr[2];
					}
                    options.push(o);
				}
			}.bind(this));
            return options;
		}
        return [];
    },
    /**
     * 当表单上没有对应组件的时候，可以使用this.data[fieldId] = data赋值。
     * @summary 为组件赋值。
     * @param value{String} .
     * @example
     *  this.form.get("fieldId").setData("test"); //赋文本值
     * @example
     *  //如果无法确定表单上是否有组件，需要判断
     *  if( this.form.get('fieldId') ){ //判断表单是否有无对应组件
     *      this.form.get('fieldId').setData( data );
     *  }else{
     *      this.data['fieldId'] = data;
     *  }
     */
    setData: function(value){
        this._setBusinessData(value);
        this._setValue(value);
	},
    _setValue: function(value){
        if (!value) value = [];
        if (value.length==1 && (!value[0])) value = [];
        if (typeOf(value) !=="array") value = [value];

		if (this.combox){
            var textData = this.getTextData( value );
            if( typeOf(textData.then) === "function" ){
                Promise.resolve(textData).then(function (tData) {
                    this.combox.clear();
                    this.__setValueEdit( tData );
                }.bind(this))
            }else{
                this.combox.clear();
                this.__setValueEdit( textData )
            }
		}else{
		    var textList = this.getTextListByValue( value );
            if( typeOf(textList.then) === "function" ){
                Promise.resolve(textList).then(function (tList) {
                    this.__setValueRead( value, tList );
                }.bind(this))
            }else{
                this.__setValueRead( value, textList );
            }
		}
    },
    __setValueEdit: function(textData){
        var comboxValues = [];
        textData.value.each(function(v, i){
            comboxValues.push({
                "text": textData.text[i] || v,
                "value": v
            });
        }.bind(this));
        this.combox.addNewValues(comboxValues);
        this.fieldModuleLoaded = true;
    },
    __setValueRead: function(value, textList, contentNode){
        if(!contentNode)contentNode = new Element("div", {
            "styles": { "overflow": "hidden"}
        }).inject(this.node);
        textList.each(function(text, i){
            if (i<value.length-1) text += this.json.splitShow;
            new Element("div", {"styles": {
                    "float": "left",
                    "margin-right": "5px"
                },"text": text}).inject( contentNode ); //.inject(this.node.getFirst() || this.node);
        }.bind(this));
        this.fieldModuleLoaded = true;
    },
    /**
     * @summary 重新计算下拉选项.
     * @param {Function} callback 回调方法
     * @example
     * this.form.get('fieldId').resetOption();
     * @example
     * this.form.get('fieldId').resetOption(function(){
     *   //设置完成后的回调
     * });
     */
    resetOption: function( callback ){
        if (this.combox){
            var list = this.getOptions(true, true);
            if( typeOf(list.then) === "function" ){
                Promise.resolve(list).then(function (array) {
                    this.combox.setOptions({"list": array});
                    if(callback)callback();
                })
            }else{
                this.combox.setOptions({"list": list});
                if(callback)callback();
            }
        }
    },
    /**
     * @summary 添加下拉选项.
     * @param text  {String} 下拉选项文本
     * @param value {String} 下拉选项值
     * @example
     * this.form.get('fieldId').addOption("秘密","level1");
     */
	addOption: function(text, value){
        if (this.combox){
            var list = this.combox.options.list || [];
            list.push({
				"text": text,
                "value": value
			});
            this.combox.setOptions({"list": list});
        }
	},
    isEmpty : function(){
        var data = this.getData();
        if( typeOf(data) === "array" ){
            return data.length === 0;
        }else{
            return !data;
        }
    },
    getInputData: function(){
        if (this.combox) return this.combox.getData();
        return this._getBusinessData();
    },
    _beforeReloaded: function(){
	    if( this.combox )this.combox.clear();
        this.combox = null;
    },
    /**
     * @summary 获取选中项的text。
     * @return {Array|Promise} 返回选中项的text
     * @example
     * var text = this.form.get('fieldId').getText(); //获取选中项的文本
     * @example
     * //如果选项是异步的，比如数据字典、视图、查询视图
     * var p = this.form.get('fieldId').getText(); //获取选中项Promise
     * Promise.resolve(p).then(function(text){
     *     //text 为选选中项的文本
     * })
     */
    getText: function(){
        var d = this.getTextData();
        if( typeOf(d.then) === "function" ){
            return d.then(function( d1 ){
                return d1.text;
            });
        }else{
            return d.text;
        }
    },
    /**
     * @summary 获取选中的值和文本.
     * @example
     * var array = this.form.get('fieldId').getTextData();
     * @example
     * //异步
     * var array = this.form.get('fieldId').getTextData();
     * Promise.resolve(array).then(function(arr){
     *     //arr为选中项值和文本
     * })
     * @return {Object|Promise} 返回选中项值和文本，格式为 { 'value' : [value], 'text' : [text] }.
     */
    getTextData: function( value ){
	    var v = value || this.getData();
	    var textList = this.getTextListByValue( v );
        if( typeOf(textList.then) === "function" ){
            return Promise.resolve(textList).then(function (tList) {
                return {"value": v, "text": tList.length ? tList : v};
            }.bind(this));
        }else{
            return {"value": v, "text": textList.length ? textList : v};
        }
        //return this.node.get("text");
    },
    getTextListByValue: function( v ){
        var options, textList=[];
        if( this.json.itemType === "dynamic" ){
            var fun = this._searchOptions();
            if( fun ){
                v.each(function ( i ) {
                    var text;
                    if (typeOf(i)==="object"){
                        text = i.text || i.title || i.subject  || i.name;
                    }else{
                        i = i.toString();
                        fun(i, function( options ){
                            var matchList = options.filter(function (o) {
                                if( o.value === i )return true;
                            });
                            text = (matchList[0] && matchList[0].text) ? matchList[0].text : i;
                            textList.push( text );
                        }.bind(this));
                    }
                }.bind(this));
                return textList;
            }
        }else{
            options = this.getOptions();
            if( typeOf(options.then) === "function" ){
                return Promise.resolve(options).then(function (opt) {
                    return this._getTextListByValue(v, opt);
                }.bind(this));
            }else{
                return this._getTextListByValue(v, options);
            }
        }
        v.each(function ( i ) {
            var text;
            if (typeOf(i)==="object"){
                text = i.text || i.title || i.subject  || i.name;
            }else{
                text = i.toString();
            }
            textList.push( text );
        }.bind(this));
        return textList;
    },
    _getTextListByValue: function(v, options){
        var textList = [];
        v.each(function ( i ) {
            var text;
            if (typeOf(i)==="object"){
                text = i.text || i.title || i.subject  || i.name;
            }else{
                i = i.toString();
                var matchList = options.filter(function (o) {
                    if( o.value === i )return true;
                });
                text = (matchList[0] && matchList[0].text) ? matchList[0].text : i;
            }
            textList.push( text );
        }.bind(this));
        return textList;
    },

    getValueListByText: function( text ){
        var options, valueList=[];
        if( this.json.itemType === "dynamic" ){
        }else{
            options = this.getOptions();
            if( typeOf(options.then) === "function" ){
                return Promise.resolve(options).then(function (opt) {
                    return this._getValueListByText(text, opt);
                }.bind(this));
            }else{
                return this._getValueListByText(text, options);
            }
        }
        text.each(function ( i ) {
            var value;
            if (typeOf(i)==="object"){
                value = i.name || i.text || i.title || i.subject ;
            }else{
                value = i.toString();
            }
            valueList.push( value );
        }.bind(this));
        return valueList;
    },
    _getValueListByText: function(text, options){
        var valueList = [];
        text.each(function ( i ) {
            var value;
            if (typeOf(i)==="object"){
                value = i.name || i.text || i.title || i.subject;
            }else{
                i = i.toString();
                var matchList = options.filter(function (o) {
                    if( o.text === i )return true;
                });
                value = (matchList[0] && matchList[0].text) ? matchList[0].value : i;
            }
            valueList.push( value );
        }.bind(this));
        return valueList;
    },

    resetData: function(){
        //this._setBusinessData(this.getValue());
        this.setData(this.getValue());
    },

    getExcelData: function( type ){
        var value = this.getData();
        if( type === "value" )return o2.typeOf(value) === "array" ? value.join(", ") : value;

        var textList = this.getTextListByValue( value );
		return Promise.resolve(textList).then(function (tList) {
			return tList.join(", ");
		});
    },
    setExcelData: function(data, type){
        var arr = this.stringToArray(data);
        this.excelData = arr;
        if( type === "value" ){
            this.setData(arr, true);
        }else{
            var values = this.getValueListByText( arr );
            this.moduleExcelAG = Promise.resolve(values).then(function (vs) {
                this.setData(vs, true);
                this.moduleExcelAG = null;
            }.bind(this));
        }
    }
}); 
